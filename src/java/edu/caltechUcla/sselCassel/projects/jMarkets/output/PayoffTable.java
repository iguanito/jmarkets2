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
 * TransactionHistoryTable.java
 *
 * Created on January 30, 2005, 5:57 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.output;

import edu.caltechUcla.sselCassel.projects.jMarkets.server.data.DBWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


/**
 *
 * @author  Walter M. Yuan, Raj Advani
 */
public class PayoffTable extends OutputTable {
    
    /** Creates a new instance of TransactionHistoryTable */
    public PayoffTable(DBWriter dbw, int sessionId) {
        super(dbw, sessionId);
    }
    
    public String getTitle() {
        return "Payoff Summary";
    }
    
    // Time Buyer Seller Price Units
    public String[] getHeaders() {
        String[] headers = {"Subject Id", "Last Name", "First Name", "Email", "Payoff"};
        return headers;
    }
    
    public String[][] getData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet results = null;
        try {
            conn = dbw.getConnection();
            stmt = conn.createStatement();
            
            String query = "select s.id, s.lname, s.fname, s.email, sum(p.payoff) from jm_user s, " +
                    "subject_payoffs p where s.id=p.subject_id and p.session_id=" + sessionId + " group by " +
                    "p.session_id, subject_id";
            
            results = stmt.executeQuery(query);
            
            ArrayList data = new ArrayList();
            
            while (results.next()) {
                String[] row = new String[5];
                
                row[0] = String.valueOf(results.getLong(1));
                row[1] = results.getString(2); 
                row[2] = results.getString(3); 
                row[3] = results.getString(4); 
                row[4] = String.valueOf(results.getFloat(5)); 
               
                
                data.add(row);
            }
            
            String[][] rows = new String[data.size()][5];
            for (int i=0; i<data.size(); i++)
                rows[i] = (String[]) data.get(i);
            
            return rows;
        }catch(Exception e) {
            log.error("Failed to collect data for the payoff table", e);
            e.printStackTrace();
        }finally {
            dbw.closeConnection(conn, results, stmt);
        }
        return null;
    }
   
}