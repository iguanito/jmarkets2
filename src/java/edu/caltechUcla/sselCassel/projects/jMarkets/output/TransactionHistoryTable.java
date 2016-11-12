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
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author  Raj Advani, Walter M. Yuan
 */
public class TransactionHistoryTable extends OutputTable {
    
    public static final String[] headers = {"Period", "Txn ID", "Time", "Subject", "Offer Id", "Entry Type", "Offer Type", "Security", "Avg Txn Price", "Units", "Market Type"};
    
    /** Creates a new instance of TransactionHistoryTable */
    public TransactionHistoryTable(DBWriter dbw, int sessionId) {
        super(dbw, sessionId);
    }
    
    public String getTitle() {
        return "Transaction History";
    }
    
    // Time Buyer Seller Price Units
    public String[] getHeaders() {
        return headers;
    }
    
    public String[][] getData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet results = null;
        try {
            conn = dbw.getConnection();
            stmt = conn.createStatement();
            
            String query = "select p.period_id, tb.id, tb.time_entry, ob.subject_id, ob.id, ob.entry_type, ob.offer_type, s.security_name, tb.price, ts.units_contributed, p.market_type " + 
            "from transaction_book tb, transaction_sides ts, periods p, securities s, offer_book ob, security_pricelevels sp, period_securities ps where " +
            "p.session_id=" + sessionId + " and ps.session_id=" + sessionId + " and p.period_id=ps.period_id and tb.id=ts.transaction_id and ts.offer_id=ob.id and ob.pricelevel_id=sp.id and " +
            "sp.period_security_id=ps.id and ps.security_id=s.id order by p.period_id, tb.time_entry, tb.id";
            
            results = stmt.executeQuery(query);
            
            ArrayList data = new ArrayList();
            
            while (results.next()) {
                String[] row = new String[this.headers.length];
                row[0] = String.valueOf(results.getInt("p.period_id"));
                row[1] = String.valueOf(results.getInt("tb.id"));
                row[2] = String.valueOf(results.getInt("tb.time_entry"));
                row[3] = String.valueOf(results.getInt("ob.subject_id"));
                row[4] = String.valueOf(results.getInt("ob.id"));
                row[5] = results.getString("ob.entry_type"); 
                row[6] = results.getInt("ob.offer_type") == JMConstants.BUY_ACTION ?  JMConstants.BUY_ORDER : JMConstants.SELL_ORDER; 
                
                row[7] =  results.getString("s.security_name");
                row[8] =  String.valueOf(results.getFloat("tb.price"));
                row[9] = String.valueOf(results.getInt("ts.units_contributed"));
                row[10] = results.getString("p.market_type");
                
                data.add(row);
            }
            
            String[][] rows = new String[data.size()][headers.length];
            for (int i=0; i<data.size(); i++)
                rows[i] = (String[]) data.get(i);
            
            
            return rows;
        }catch(Exception e) {
            log.error("Failed to collect data for transaction history table", e);
            e.printStackTrace();
        }finally {
            dbw.closeConnection(conn, results, stmt);
        }
        return null;
    }
    
}