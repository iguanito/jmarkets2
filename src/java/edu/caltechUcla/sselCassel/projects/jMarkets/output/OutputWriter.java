/*
 * Copyright (C) 2005-2006, <a href="http://www.ssel.caltech.edu">SSEL</a>
 * <a href="http://www.cassel.ucla.edu">CASSEL</a>, Caltech/UCLA
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 *
 * Project Authors: Raj Advani, Walter M. Yuan, and Peter Bossaerts
 * Email: jmarkets@ssel.caltech.edu
 */

/*
 * OutputWriter.java
 *
 * Created on January 30, 2005, 5:22 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.output;

import edu.caltechUcla.sselCassel.projects.jMarkets.server.control.ControlServ;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.data.DBWriter;
import java.sql.*;
import java.io.*;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  Raj Advani
 */
public class OutputWriter {
    
    /** Creates a new instance of OutputWriter */
    public OutputWriter() {
        this.dbw = ControlServ.dbw;
    }
    
    /** Return true if the session with the given ID exists */
    private boolean sessionExists(int sessionId) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet results = null;
        try {
            conn = dbw.getConnection();
            stmt = conn.createStatement();
            String query = "select * from sessions where id=" + sessionId;
            
            results = stmt.executeQuery(query);
            boolean exists = false;
            
            if (results.next())
                exists = true;
            
            return exists;
        }catch(SQLException e) {
            log.warn("Error while checking existence of session " + sessionId);
            return false;
        }finally {
            dbw.closeConnection(conn, results, stmt);
        }
    }
    
    /** Output the data for the given session to the given path, if the session exists */
    public boolean outputSession(int sessionId, String path) {
        try {
            if (!sessionExists(sessionId)) {
                log.warn("Session " + sessionId + " does not exist -- cannot write output file");
                return false;
            }
            
            File file = new File(path);
            file.createNewFile();
            
            FileOutputStream outStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(outStream);
            
            OutputTable txnTable = new TransactionHistoryTable(dbw, sessionId);
            OutputFormatter formatter = new CSVFormatter();
            
            String output = formatter.formatTable(txnTable);
            
            writer.write(output);
            
            OutputTable orderTable = new OrderHistoryTable(dbw, sessionId);
            formatter = new CSVFormatter();
            
            output = formatter.formatTable(orderTable);
            
            writer.write(output);

            OutputTable subjectTable = new SubjectTable(dbw, sessionId);
            formatter = new CSVFormatter();
            output = formatter.formatTable(subjectTable);
            writer.write(output);
            
            OutputTable payoffTable = new PayoffTable(dbw, sessionId);
            output = formatter.formatTable(payoffTable);
            writer.write(output);
            
            writer.close();
            outStream.close();
            
            return true;
        }catch(Exception e) {
            log.error("Error attempting to write output file for session " + sessionId, e);
        }
        return false;
    }
    
    public static void main(String[] args) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUsername("jmarkets");
        ds.setPassword("banana");
        
        String dbHost = "david.ssel.caltech.edu";
        String dbPort = "3306";
        String dbName = "jmarkets";
        
        ds.setUrl("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName);
        //ds.setUrl("jdbc:mysql://localhost:3306/jmarkets");
        ds.setMaxActive(10);
        
        OutputWriter writer = new OutputWriter();
        writer.outputSession(145, "c://output.csv");
    }
    
    
    protected DBWriter dbw;
    
    private static Log log = LogFactory.getLog(OutputWriter.class);
}
