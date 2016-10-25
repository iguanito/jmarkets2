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
import java.util.*;
import java.sql.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;

/**
 *
 * @author  Walter M. Yuan
 */
public class OrderHistoryTable extends OutputTable {
    
    public static final String[] headers = {"Period", "Entry Time", "Subject", "Order Id", "Security", "Order Type", "Price", "Order Size", "Entry Type", "Order Status"};
    
    /** Creates a new instance of TransactionHistoryTable */
    public OrderHistoryTable(DBWriter dbw, int sessionId) {
        super(dbw, sessionId);
    }
    
    public String getTitle() {
        return "Order History";
    }
    
    // Time Buyer Seller Price Units
    public String[] getHeaders() {
        return headers;
    }
    
    public String [][] getData(){
        List <OrderRecord> allOrders = getMarketOrderData(); 
        allOrders.addAll(getLimitOrderData()); 
        allOrders.addAll(getCancelOrderData()); 
        Collections.sort(allOrders, OrderRecord.COMPARATOR); 
        
        String [][] dataMatrix = null; 
        if(allOrders != null && allOrders.size()>0){
            dataMatrix = new String [allOrders.size()][this.getHeaders().length]; 
            int i=0; 
            for(OrderRecord rec : allOrders){
                dataMatrix[i] = new String[this.getHeaders().length]; 
                dataMatrix[i][0] = String.valueOf(rec.getPeriodId());
                dataMatrix[i][1] = String.valueOf(rec.getEntryTime());
                dataMatrix[i][2] = String.valueOf(rec.getSubjectId());
                dataMatrix[i][3] = String.valueOf(rec.getOrderId());
                dataMatrix[i][4] = rec.getSecurity();
                dataMatrix[i][5] = rec.getOrderType();
                dataMatrix[i][6] = String.valueOf(rec.getPrice());
                dataMatrix[i][7] = String.valueOf(rec.getOrderSize());
                dataMatrix[i][8] = rec.getEntryType();
                dataMatrix[i][9] = rec.getOrderStatus();
                i++; 
            }
        }
        return dataMatrix; 
    }
    
    private List <OrderRecord> getMarketOrderData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet results = null;

        try {
            conn = dbw.getConnection();
            stmt = conn.createStatement();
            
            String query ="select ps.period_id, ob.time_entry, ob.subject_id, ob.id, s.security_name, ob.offer_type, sp.price_level, ob.offer_units, ob.entry_type, ob.offer_status " 
                          + "from securities s, offer_book ob, security_pricelevels sp, period_securities ps where ps.session_id=" + sessionId+ " and ps.security_id=s.id and ps.id=sp.period_security_id"
                          + " and sp.id=ob.pricelevel_id and ob.entry_type='" + JMConstants.ORDER_META_MARKET + "' order by ps.period_id, ob.time_entry "; 
                                  
            results = stmt.executeQuery(query);
            
            List <OrderRecord> data = new ArrayList <OrderRecord> ();
            List <Integer> check = new ArrayList<Integer>(); 
            while (results.next()) {
                OrderRecord rec = new OrderRecord(); 
                rec.setPeriodId(results.getInt("ps.period_id"));
                rec.setEntryTime(results.getLong("ob.time_entry"));
                rec.setSubjectId(results.getInt("ob.subject_id"));
                rec.setOrderId(results.getInt("ob.id"));
                rec.setSecurity(results.getString("s.security_name"));
                rec.setEntryType(JMConstants.ORDER_META_MARKET);
                rec.setOrderType(results.getInt("ob.offer_type") == JMConstants.BUY_ACTION ? JMConstants.BUY_ORDER : JMConstants.SELL_ORDER);
                rec.setPrice(results.getFloat("sp.price_level"));
                rec.setOrderSize(results.getInt("ob.offer_units"));
                rec.setOrderStatus(results.getString("ob.offer_status"));
                
                data.add(rec);
            }
            
            return data;
        }catch(Exception e) {
            log.error("Failed to collect data for market orders ", e);
            e.printStackTrace();
        }finally {
            dbw.closeConnection(conn, results, stmt);
        }
        return null;
    }
   
    private List <OrderRecord> getLimitOrderData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet results = null;

        try {
            conn = dbw.getConnection();
            stmt = conn.createStatement();
            
            String query = "select ps.period_id, ob.time_entry, ob.subject_id, s.security_name, ob.offer_type, sp.price_level, ob.offer_units, sum(tt.units_changed), ob.id, ob.offer_status" +
                    " from period_securities ps, securities s, security_pricelevels sp, offer_book ob left join ticker_tape tt on ob.id=tt.offer_book_id" +
                    " where ps.session_id=" + sessionId + " and ob.pricelevel_id=sp.id and sp.period_security_id=ps.id and "+
                    "ps.security_id=s.id and ob.entry_type='" + JMConstants.ORDER_META_LIMIT + "' group by ob.id";
            
            results = stmt.executeQuery(query);
            
            List <OrderRecord> data = new ArrayList <OrderRecord> ();
            List <Integer> check = new ArrayList<Integer>(); 
            while (results.next()) {
                OrderRecord rec = new OrderRecord(); 
                rec.setPeriodId(results.getInt("ps.period_id"));
                rec.setEntryTime(results.getLong("ob.time_entry"));
                rec.setSubjectId(results.getInt("ob.subject_id"));
                rec.setSecurity(results.getString("s.security_name"));
                rec.setEntryType(JMConstants.ORDER_META_LIMIT);
                rec.setOrderType(results.getInt("ob.offer_type") == JMConstants.BUY_ACTION ? JMConstants.BUY_ORDER : JMConstants.SELL_ORDER);
                rec.setPrice(results.getFloat("sp.price_level"));
                rec.setOrderSize(results.getInt(7) + results.getInt(8));
                rec.setOrderId(results.getInt("ob.id"));
                rec.setOrderStatus(results.getString("ob.offer_status"));
                
                if (!check.contains(rec.getOrderId())) {
                    data.add(rec);
                    check.add(rec.getOrderId());
                }
            }
           
            return data;
        }catch(Exception e) {
            log.error("Failed to collect data for limit orders ", e);
            e.printStackTrace();
        }finally {
            dbw.closeConnection(conn, results, stmt);
        }
        return null;
    }
    
    private List <OrderRecord> getCancelOrderData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet results = null;

        try {
            conn = dbw.getConnection();
            stmt = conn.createStatement();
            
            String query = "select ps.period_id, tt.time_stamp, ob.subject_id, s.security_name, ob.offer_type, sp.price_level, tt.units_changed, ob.id, ob.offer_status" +
                    " from period_securities ps, securities s, offer_book ob, security_pricelevels sp, ticker_tape tt" +
                    " where ps.session_id=" + sessionId + " and ps.id=sp.period_security_id and tt.offer_book_id=ob.id and ob.pricelevel_id=sp.id and " +
                    "ps.security_id=s.id and ob.offer_status='" + JMConstants.ORDER_CANCELLED + "' order by ps.period_id, tt.time_stamp";
            
            results = stmt.executeQuery(query);
            
            List <OrderRecord> data = new ArrayList <OrderRecord> ();
            List <Integer> check = new ArrayList<Integer>(); 
            while (results.next()) {
                OrderRecord rec = new OrderRecord(); 
                rec.setPeriodId(results.getInt("ps.period_id"));
                rec.setEntryTime(results.getLong("tt.time_stamp"));
                rec.setSubjectId(results.getInt("ob.subject_id"));
                rec.setSecurity(results.getString("s.security_name"));
                rec.setEntryType(JMConstants.ORDER_META_LIMIT);
                rec.setOrderType(results.getInt("ob.offer_type") == JMConstants.BUY_ACTION ? JMConstants.BUY_ORDER : JMConstants.SELL_ORDER);
                rec.setPrice(results.getFloat("sp.price_level"));
                rec.setOrderSize(results.getInt("tt.units_changed"));
                rec.setOrderId(results.getInt("ob.id"));
                rec.setOrderStatus(results.getString("ob.offer_status"));
                
                data.add(rec);
                check.add(rec.getOrderId());
            }
            
            return data;
        }catch(Exception e) {
            log.error("Failed to collect data for cancel orders ", e);
            e.printStackTrace();
        }finally {
            dbw.closeConnection(conn, results, stmt);
        }
        return null;
    }
    
}

