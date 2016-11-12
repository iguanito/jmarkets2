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
 * DBWriter.java
 *
 * Created on March 27, 2004, 9:03 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.data;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SubjectDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.GroupDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.PeriodDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SessionDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.MarketDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.BasicOffer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.LabelValueBean;

/**
 * This class contains method that write common queries to the database. It uses
 * the DBConnector class
 *
 * @author  Raj Advani, Walter M. Yuan
 */
public class DBWriter {
    
    /** Creates a new instance of DBWriter */
    public DBWriter(DBConnector dbc) {
        if (dbc == null)
            log.error("Cannot instantiate a DBWriter when the server does not have an active DBConnector");
        this.dbc = dbc;
    }
    
    /** Get a new connection from the pool */
    public Connection getConnection() {
        try {
            return dbc.getConnection();
        }catch(SQLException e) {
            log.error("Failed to get a connection to the database", e);
        }
        return null;
    }

    public void closeConnection(Connection conn){
        dbc.closeQuery(conn);
    }

    public void closeConnection(Connection conn, ResultSet r, Statement s){
        Object [] obj = {r, s};
        dbc.closeQuery(obj, conn);
    }


    
    /** Start a database transaction */
    public void startTransaction(Connection conn) {
        dbc.startTransaction(conn);
    }
    
    /** Commit the current database transaction */
    public void commit(Connection conn) throws TransactionInterruptedException {
        try {
            dbc.commit(conn);
        }catch(SQLException e) {
            if (e.getSQLState().equals("40001")) {
                log.warn("Database deadlock detected -- alerting TradeServ");
                TransactionInterruptedException te = new TransactionInterruptedException();
                te.setDeadlock(true);
                throw te;
            } else {
                log.error("Failed to commit the last transaction to the database", e);
                throw new TransactionInterruptedException();
            }
        }
    }
    
    /** Rollback all changes made in the current transaction */
    public void rollback(Connection conn) {
        dbc.rollback(conn);
    }
    
    /** Write the contents of a session object for when a new session is inaugurated. Return
     *  the ID of the session */
    public int writeSession(String sessionName, int numTraders, SessionDef session) {
        Connection conn = null; 
        Object[] results = null; 
        try {
            conn = dbc.getConnection();
            
            int id = 0;
            int numPeriods = session.getNumPeriods();
            int timeout = session.getTimeoutLength();
            
            String [] colNames = {"experimenter_id", "name", "num_periods", "num_traders", "open_delay", "session_status", "def"}; 
            
            String[] values = {String.valueOf(session.getExperimenterId()), sessionName, String.valueOf(numPeriods), String.valueOf(numTraders), String.valueOf(timeout), 
                                String.valueOf(JMConstants.ACTION_STR[JMConstants.ACTION_WAIT]), session.getDef()};
            results = dbc.insert("sessions", colNames, values, conn);
            
            ResultSet rs = (ResultSet) results[0];
            rs.next();
            id = rs.getInt(1);
            
            log.info("Database has generated session id: " + id);
            
            return id;
        }catch(Exception e) {
            log.error("Cannot create a session id in the database: " + e);
        }finally{
            dbc.closeQuery(results, conn);
        }
        return -1;
    }
    
    /** Write the contents of a period object for when a new period is inaugurated */
    public void writePeriod(int sessionId, int periodId, PeriodDef period){
        Connection conn = null; 
        Object[] results = null;
        try {
            
            conn = dbc.getConnection();
            
            int timeLength = period.getPeriodLength();
            int openDelay = period.getOpenDelay();
            
            String [] colNames = {"session_id", "period_id", "duration", "open_delay", "market_type"}; 
                
            String[] values = {String.valueOf(sessionId), String.valueOf(periodId), String.valueOf(timeLength), String.valueOf(openDelay), period.getMarketEngine()};
            results = dbc.insert("periods", colNames, values, conn);
        } catch (SQLException ex) {
            log.error("Failed to write period data. ", ex); 
        }finally{
            dbc.closeQuery(results, conn);
        }
    }
    
    /**
     * Record a session event [session: start | stop]
     */
    public void writeSessionEvent(int sessionId, int actionType){
        Connection conn = null; 
        Object[] results = null; 
        try{
            conn = dbc.getConnection();
            String [] names = null; 
            String [] values = null; 
            String [] matchValues = new String[1]; 
            matchValues [0] = String.valueOf(sessionId); 
            String [] matchNames = new String [1]; 
            matchNames[0] = "id"; 
            
            switch(actionType){
                case JMConstants.ACTION_START:
                    values = new String[2]; 
                    names = new String [2]; 
                    values [0] = new Timestamp(new Date().getTime()).toString(); 
                    values [1] = JMConstants.ACTION_STR[JMConstants.ACTION_START]; 
                    names [0] = "start_time"; 
                    names [1] = "session_status"; 
                    break; 
                case JMConstants.ACTION_FINISH: 
                case JMConstants.ACTION_ABORT:
                    values = new String[2]; 
                    names = new String [2]; 
                    values [0] = new Timestamp(new Date().getTime()).toString(); 
                    values [1] = JMConstants.ACTION_STR[actionType]; 
                    names [0] = "end_time"; 
                    names [1] = "session_status"; 
                    break;
                default:
                    throw new Exception("Failed to update session status!"); 
            }
           
            results = dbc.update("sessions", names, values, matchNames, matchValues, conn); 
        }catch(Exception e) {
            log.error("Failed to write session event to database", e);
        }finally{
            dbc.closeQuery(results, conn);
        }
    }
    
    /**
     * Record a session event [session: start | stop]
     */
    public void writePeriodEvent(int sessionId, int periodId, int actionType){
        Connection conn = null; 
        Object[] results = null; 
        try{
            conn = dbc.getConnection();
            String [] names = null; 
            String [] values = null; 
            String [] matchValues = new String[2]; 
            matchValues [0] = String.valueOf(sessionId); 
            matchValues [1] = String.valueOf(periodId); 
            String [] matchNames = new String [2]; 
            matchNames[0] = "session_id";
            matchNames[1] = "period_id";
            
            switch(actionType){
                case JMConstants.ACTION_START:
                    values = new String[1]; 
                    names = new String [1]; 
                    values [0] = new Timestamp(new Date().getTime()).toString(); 
                    names [0] = "start_time"; 
                    break; 
                case JMConstants.ACTION_FINISH: 
                    values = new String[1]; 
                    names = new String [1]; 
                    values [0] = new Timestamp(new Date().getTime()).toString(); 
                    names [0] = "end_time"; 
                    break;
                default:
                    throw new Exception("Failed to update period status!"); 
            }
           
            results = dbc.update("periods", names, values, matchNames, matchValues, conn); 
        }catch(Exception e) {
            log.error("Failed to write period event to database", e);
        }finally{
            dbc.closeQuery(results, conn);
        }
    }
    
    /**
     * Write the group names into the market_groups table, and retrieve their IDs to create
     *  mappings from group ID to group database ID. Insert these mappings into the GroupDef
     *  object
     */
    public void writeGroups(GroupDef ginfo) {
        try {
            for (int i=0; i<ginfo.getNumGroups(); i++) {
                String group = ginfo.getGroupTitle(i);
                int groupId_db = addGroup(group);
                
                ginfo.setGroupId_db(i, groupId_db);
            }
        }catch(Exception e) {
            log.error("Failed to write the group information into the market_groups table", e);
        }
    }
    
    
    
    /** Write the groups of the subjects for the given period into the subject_groups table */
    public void writeSubjectGroups(int sessionId, int periodId, SubjectDef si, GroupDef gi) {
        Connection conn = null; 
        Object[] results = null; 
        try {
            conn = dbc.getConnection();
            Map<Integer, Integer> groupMaps = new HashMap<Integer, Integer>();
            for (int i=0; i<si.getNumSubjects(); i++) {
                int subjectId_db = si.getDatabaseId(i);
                int groupId = si.getGroup(i);
                int subjNum = 0;
                if(groupMaps.containsKey(groupId)){
                    subjNum = groupMaps.get(groupId) + 1;
                }
                groupMaps.put(groupId, subjNum);

                int groupId_db = gi.getGroupId_db(groupId);
                
                String[] values = {"" + sessionId, "" + periodId, "" + subjectId_db, "" + groupId_db, "" + subjNum};
                results = dbc.insert("subject_groups", values, conn);
                dbc.closeQuery(results); 
            }
        }catch(Exception e) {
            log.error("Failed to write the subject group information into the database", e);
        }finally{
            dbc.closeQuery(conn);
        }
    }
    
    /** Write the given functions into their respective tables if they aren't already there. Then
     *  write them into the rules table */
    public void writeFunctions(int sessionId, int periodId, GroupDef ginfo) throws SQLException {
        Connection conn = null; 
        Object[] results = null; 
        try {
            conn = dbc.getConnection();
            for (int i=0; i<ginfo.getNumGroups(); i++) {
                int groupId_db = ginfo.getGroupId_db(i);
                PayoffFunction payoffFunction = ginfo.getPayoffFunction(i);
                BankruptcyFunction bankruptcyFunction = ginfo.getBankruptcyFunction(i);
                float bankruptcyCutoff = ginfo.getBankruptcyCutoff(i);
                
                String[] values = {"" + sessionId, "" + periodId, "" + groupId_db, "" + payoffFunction.getName(), "" + bankruptcyFunction.getName(), "" + bankruptcyCutoff, "" + 0};
                results = dbc.insert("rules", values, conn);
                dbc.closeQuery(results);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
            dbc.closeQuery(results, conn);
        }
    }
    
    /** Write the contents of the marketInfo object into the period_securities table. Also write
     *  the price levels for each market into the security_pricelevels table */
    public void writeSecurities(int sessionId, int periodId, MarketDef market) {
        Connection conn = null; 
        Object[] results = null;
        Object[] results2 = null;
        try {
            int numSecurities = market.getNumMarkets();
            conn = dbc.getConnection();
            for (int i=0; i<numSecurities; i++) {
                
                int id = 0; //unique identifier
                int securityId = addSecurity(market.getMarketTitles()[i]);
                float tickPrice = market.getIncrement(i);
                float minPrice = market.getMinPrices()[i];
                float maxPrice = market.getMaxPrices()[i];
                int timeLength = 0;   //get this working eventually
                int timeDelay = 0;  //get this working one day
                
                String[] values = {String.valueOf(id), String.valueOf(sessionId), String.valueOf(periodId), 
                        String.valueOf(securityId), String.valueOf(tickPrice), String.valueOf(minPrice), 
                        String.valueOf(maxPrice), String.valueOf(timeLength), String.valueOf(timeDelay)};
                        results = dbc.insert("period_securities", values, conn);
                        
                        ResultSet rs = (ResultSet) results[0];
                        rs.next();
                        id = rs.getInt(1);
                        dbc.closeQuery(results);
                        
                        float[] prices = market.getPrices()[i];
                        for (int j=0; j<prices.length; j++) {
                            
                            int priceId = 0;
                            int periodSecurityId = id;
                            float price = prices[j];
                            
                            String[] pvalues = {"" + priceId, "" + periodSecurityId, "" + price};
                            results2 = dbc.insert("security_pricelevels", pvalues, conn);
                            
                            ResultSet rs2 = (ResultSet) results2[0];
                            rs2.next();
                            priceId = rs2.getInt(1);
                            
                            market.setPriceId_db(i, price, priceId);
                            dbc.closeQuery(results2);
                        }
                        market.setPeriodSecurityId(i, id);
                        market.setSecurityId(i, securityId);
            }
        }catch(Exception e) {
            log.error("Error creating securities and prices tables: ", e);
        }finally{
            dbc.closeQuery(conn);
        }
    }
    
    /** Write the given offer to the offer book. Return the ID generated for the offer. Remember that
     *  offer_type=1=bid, offer_type=2=ask */
    public int writeOffer(int subjectId_db, int marketId, int priceId, int offerType, int units, String entryType, long time, MarketDef minfo, Connection conn) throws TransactionInterruptedException {
        Object[] results = null;
        try {
            int priceId_db = getPriceId_db(marketId, priceId, minfo);
            
            String[] values = {"0", String.valueOf(subjectId_db), String.valueOf(priceId_db), String.valueOf(offerType), 
                                String.valueOf(units), JMConstants.ORDER_STATUSES[JMConstants.ORDER_VALID], entryType, String.valueOf(time),  String.valueOf(time)};
            results = dbc.insert("offer_book", values, conn);
            
            ResultSet rs = (ResultSet) results[0];
            rs.next();
            return rs.getInt(1);

        }catch(Exception e) {
            log.error("Failed to write an offer into the SQL database", e);
            throw new TransactionInterruptedException();
        }finally{
            dbc.closeQuery(results);

        }
    }
    
    /**
     * First write into the period_cash_initials table the initial amount of cash that each
     *  group type will get. This information is contained in the GroupDef object. Then go through
     *  each client, find their group (by using the SubjectDef object) and write the appropriate
     *  amount of cash (for their group) into the subject_cash_holdings table. This amount is the
     *  intial amount plus the dividends from the previous period (given)
     */
    public void writeCashInitials(int sessionId, int periodId, SubjectDef si, GroupDef gi, float[] dividends){
        Connection conn = null;
        Object[] results = null;
        try {
            conn = dbc.getConnection();
            for (int i=0; i<gi.getNumGroups(); i++) {
                int groupId_db = gi.getGroupId_db(i);
                
                String[] values = {"" + sessionId, "" + periodId, "" + groupId_db, "" + gi.getCashInitial(i)};
                results = dbc.insert("period_cash_initials", values, conn);
                dbc.closeQuery(results);
            }
            
            for (int i=0; i<si.getNumSubjects(); i++) {
                int subjectId_db = si.getDatabaseId(i);
                int group = si.getGroup(i);
                float initial = gi.getCashInitial(group) + dividends[i];
                
                writeCashHoldings(sessionId, periodId, subjectId_db, initial, 0, conn);
            }
        } catch (SQLException ex) {
            log.error("Failed write cash initials. ", ex); 
        }finally{
            dbc.closeQuery(conn);
        }
    }
    
    /**
     * First write into the period_security_initials table the amount of each security that each
     *  group type will get. This information is contained in the GroupDef object. Then go through
     *  each client, find their group (by using the SubjectDef object) and write the appropriate
     *  amount of each security (for their group) into the subject_security_holdings table. This
     *  amount is the initial amount plus the carry over (via add surplus) from the previous period
     *  (given)
     */
    public void writeSecurityInitials(int sessionId, int periodId, MarketDef mi, SubjectDef si, GroupDef gi, int[][] surplus) throws SQLException {
        Connection conn = null;
        Object [] results = null;
        try {
            conn = dbc.getConnection();
            
            for (int group=0; group<gi.getNumGroups(); group++) {
                int groupId_db = gi.getGroupId_db(group);
                
                for (int m=0; m<mi.getNumMarkets(); m++) {
                    int initial = gi.getSecurityInitial(group, m);
                    int securityId_db = getPeriodSecurityId_db(m, mi);
                    
                    String[] values = {"" + securityId_db, "" + groupId_db, "" + initial};
                    results = dbc.insert("period_security_initials", values, conn);
                    dbc.closeQuery(results);
                }
                
            }
            
            for (int i=0; i<si.getNumSubjects(); i++) {
                int subjectId_db = si.getDatabaseId(i);
                int group = si.getGroup(i);
                
                for (int m=0; m<mi.getNumMarkets(); m++) {
                    int initial = gi.getSecurityInitial(group, m) + surplus[i][m];
                    writeSecurityHoldings(subjectId_db, m, initial, 0, mi, conn);
                }
            }
        } catch (SQLException ex) {
            log.error("Failed to write out security initials. ", ex); 
        }finally{
            dbc.closeQuery(conn);
        }
    }
    
    /** Write the security_priveleges table, which lists what role each group plays on each security. That is,
     *  the table indicates whether or not each group is able to buy or sell each security */
    public void writeSecurityPriveleges(int sessionId, int periodId, MarketDef minfo, GroupDef ginfo){
        Connection conn = null;
        Object[] results = null;
        try {
            conn = dbc.getConnection();
            for (int group=0; group<ginfo.getNumGroups(); group++) {
                int groupId_db = ginfo.getGroupId_db(group);
                
                for (int m=0; m<minfo.getNumMarkets(); m++) {
                    
                    int role = ginfo.getSecurityPrivilege(group, m);
                    int securityId_db = getPeriodSecurityId_db(m, minfo);
                    
                    String[] values = {String.valueOf(groupId_db), String.valueOf(securityId_db), JMConstants.MARKET_ROLES[role]};
                    results = dbc.insert("group_security_privileges", values, conn);
                    dbc.closeQuery(results);
                }
            }
        } catch (SQLException ex) {
            log.error("Failed to write out security privileges. ", ex);
        }finally{
            dbc.closeQuery(conn);
        }
    }
    
    /**
     * Write the short-sale constraints contained in the given GroupDef object into the database
     */
    public void writeSecurityRules(int sessionId, int periodId, MarketDef minfo, GroupDef ginfo){
        Connection conn = null;
        Object [] results = null;
        try {
            conn = dbc.getConnection(); 
            for (int group=0; group<ginfo.getNumGroups(); group++) {
                int groupId_db = ginfo.getGroupId_db(group);
                
                for (int m=0; m<minfo.getNumMarkets(); m++) {
                    int constraint = ginfo.getSecurityShortConstraint(group, m);
                    int securityId_db = getPeriodSecurityId_db(m, minfo);
                    
                    int addDividend = 0;
                    if (ginfo.getAddDividend(group, m))
                        addDividend = 1;
                    
                    int addSurplus = 0;
                    if (ginfo.getAddSurplus(group, m))
                        addSurplus = 1;
                    
                    String[] values = {"" + securityId_db, "" + groupId_db, "" + constraint, "" + addSurplus, "" + addDividend};
                    results = dbc.insert("security_rules", values, conn);
                    dbc.closeQuery(results);
                }
            }
        } catch (SQLException ex) {
            log.error("Failed to write out security rules. ", ex);
        }finally{
            dbc.closeQuery(conn);
        }
    }
    
    /** Get the cumulative payoffs of the given subject in the given session */
    public float getCumulativePayoff(int sessionId, int subjectId, SubjectDef subjectDef) {
        Connection conn = null;
        Object [] results = null;
        try {
            conn = dbc.getConnection();
            
            int subjectId_db = subjectDef.getDatabaseId(subjectId);
            String query = "select sum(payoff) from subject_payoffs where subject_id=" + subjectId_db + " and session_id=" + sessionId;
            results = dbc.executeQuery(query, conn);
            
            ResultSet rs = (ResultSet) results[0];
            rs.next();
            return rs.getFloat(1);
            
        }catch(SQLException e) {
            log.error("Failed to get the cumulative payoffs of subject " + subjectId, e);
        }finally{
            dbc.closeQuery(conn);
        }
        return 0f;
    }
    
    /** Write the given payoffs into the subject_payoffs table */
    public void writePayoffs(int sessionId, int periodId, float[] payoffs, SubjectDef si){
        Connection conn = null;
        Object[] results = null;
        try {
            conn = dbc.getConnection();
                
            for (int i=0; i<payoffs.length; i++) {
                int subjectId_db = si.getDatabaseId(i);
                String[] values = {"" + sessionId, "" + periodId, "" + subjectId_db, "" + payoffs[i]};
                results = dbc.insert("subject_payoffs", values, conn);
                dbc.closeQuery(results);
            }
        } catch (SQLException ex) {
            log.error("Failed to write payoffs. ", ex);
        }finally{
            dbc.closeQuery(conn);
        }
    }
    
    /** Return the amount of the given security that the subject currently has. Checks the
     *  subject_security_holdings table for the maximum id using a subquery */
    public int getSecurityHoldings(int subjectId_db, int marketId, MarketDef mi) {
        Connection conn = null;
        Object[] results =null;
        int holding = 0; 
        try {
            conn = dbc.getConnection();
            
            int securityId_db = getPeriodSecurityId_db(marketId, mi);
            
            StringBuffer query = new StringBuffer();
            query.append("select max(id) from subject_security_holdings where ");
            query.append("subject_id=").append(subjectId_db).append(" and ");
            query.append("period_security_id=").append(securityId_db);
            
            results = dbc.executeQuery(query.toString(), conn);
            ResultSet rs = (ResultSet) results[0];
            rs.next();
            int id = rs.getInt(1);
            dbc.closeQuery(results);

            query = new StringBuffer();
            query.append("select security_units from subject_security_holdings where ");
            query.append("id=").append(id);
            
            results = dbc.executeQuery(query.toString(), conn);
            rs = (ResultSet) results[0];
            rs.next();
            holding = rs.getInt("security_units");
            dbc.closeQuery(results);

        }catch(SQLException e) {
            log.error("Failed to retrieve security holding of subject " + subjectId_db + ": " + e, e);
        }finally{
            dbc.closeQuery(conn);
        }
        return holding;
    }
    
    /** Return the cash that the given subject currently has. Checks the subject_cash_holdings
     *  table for the maximum id using a subquery */
    public float getCashHoldings(int sessionId, int periodId, int subjectId_db) {
        Connection conn = null;
        Object[] results =null;
        float cash = 0; 
        try {
            conn = dbc.getConnection();
            
            StringBuffer query = new StringBuffer();
            query.append("select max(id) from subject_cash_holdings where ");
            query.append("subject_id=").append(subjectId_db).append(" and ");
            query.append("period_id=").append(periodId).append(" and ");
            query.append("session_id=").append(sessionId);
            
            results = dbc.executeQuery(query.toString(), conn);
            ResultSet rs = (ResultSet) results[0];
            rs.next();
            int id = rs.getInt(1);
            dbc.closeQuery(results);

            query = new StringBuffer();
            query.append("select cash_holding from subject_cash_holdings where ");
            query.append("id=").append(id);
            
            results = dbc.executeQuery(query.toString(), conn);
            rs = (ResultSet) results[0];
            rs.next();
            cash = rs.getFloat("cash_holding");
            
        }catch(SQLException e) {
            log.error("Failed to retrieve cash holding of subject " + subjectId_db + ": " + e, e);
        }finally{
            dbc.closeQuery(results, conn);
        }
        
        return cash;
    }
    
    /** Return the number of offers the given subject currently has on the given security at the
     *  given price level. Does not distinguish if the offers are buy or sell offers */
    public int getNumOffers(int subjectId_db, int marketId, int priceId, MarketDef mi) {
        Connection conn = null;
        Object[] results =null;
        try {
            conn = dbc.getConnection();
            
            int priceId_db = getPriceId_db(marketId, priceId, mi);
            
            StringBuffer query = new StringBuffer();
            query.append("select sum(offer_units) from offer_book where ");
            query.append("subject_id=").append(subjectId_db).append(" and ");
            query.append("pricelevel_id=").append(priceId_db).append(" and ");
            query.append("offer_status='").append(JMConstants.ORDER_STATUSES[JMConstants.ORDER_VALID]).append("'").append(" and ");
            query.append(query.append("entry_type='").append(JMConstants.ORDER_META_LIMIT).append("'")); 
            
            results = dbc.executeQuery(query.toString(), conn);
            ResultSet rs = (ResultSet) results[0];
            rs.next();
            return rs.getInt(1);

        }catch(SQLException e) {
            log.error("Failed to retrieve total number of offers of subject " + subjectId_db + " on market " + marketId + " price " + priceId + ": " + e);
        }finally{
            dbc.closeQuery(results, conn);
        }
        return 0;
    }
    
    /** Write the cash holdings of the given subject. Technically this is not an update but an
     *  insert since old rows are not changed. The ID is auto-incremented */
    public void writeCashHoldings(int sessionId, int periodId, int subjectId_db, float cash, long time, Connection conn){
        Object[] results = null;
        try {
            String[] values = {"" + 0, "" + sessionId, "" + periodId, "" + subjectId_db, "" + cash, "" + time};
            results = dbc.insert("subject_cash_holdings", values, conn);
        } catch (SQLException e) {
                log.error("Failed to write cash holding for subject " + subjectId_db + " in period " + periodId + " and session " + sessionId + ": " + e);

        } finally {
            dbc.closeQuery(results);
        }
    }
    
    /** Given the two offers involved in a transaction, writes the new cash holdings of each
     *  subject involved into the cash_holdings table. Uses writeCashHoldings(int,float,long)
     *  as a helper function. Returns an array with the bidders new cash holdings and the sellers
     *  new cash holdings (indexed such that bidder cash holdings has index 0) */
    public float[] writeCashHoldings(int sessionId, int periodId, BasicOffer newOffer, BasicOffer standingOffer, int units, long time, MarketDef minfo, Connection conn) throws SQLException {
        float price = getPrice(standingOffer.getMarketId(), standingOffer.getPriceId(), minfo);
        float difference = units * price;
        
        float cash1 = getCashHoldings(sessionId, periodId, newOffer.getSubjectId());
        if (newOffer.getAction() == JMConstants.BUY_ACTION)
            cash1 -= difference;
        else
            cash1 += difference;
        writeCashHoldings(sessionId, periodId, newOffer.getSubjectId(), cash1, time, conn);
        
        float cash2 = getCashHoldings(sessionId, periodId, standingOffer.getSubjectId());
        if (standingOffer.getAction() == JMConstants.BUY_ACTION)
            cash2 -= difference;
        else
            cash2 += difference;
        writeCashHoldings(sessionId, periodId, standingOffer.getSubjectId(), cash2, time, conn);
        
        float[] holdings = new float[2];
        if (newOffer.getAction() == JMConstants.BUY_ACTION) {
            holdings[0] = cash1;
            holdings[1] = cash2;
        } else {
            holdings[0] = cash2;
            holdings[1] = cash1;
        }
        return holdings;
    }
    
    /** Write the security holdings of the given subject. Technically this is not an update but an
     *  insert since old rows are not changed */
    public void writeSecurityHoldings(int subjectId_db, int marketId, int holdings, long time, MarketDef mi, Connection conn) throws SQLException {
        Object[] results = null;
        try {
            int securityId_db = getPeriodSecurityId_db(marketId, mi);

            String[] values = {"0", "" + securityId_db, "" + subjectId_db, "" + holdings, "" + time};
            results = dbc.insert("subject_security_holdings", values, conn);
        } catch (SQLException e) {
            log.error(e);
        } finally {
            dbc.closeQuery(results);
        }
    }
    
    /** Given the two offers involved in a transaction, writes the new security holdings of each
     *  subject involved into the security_holdings table. Uses writeSecurityHoldings(int,int,int,long)
     *  as a helper function. Returns an array with the bidder's new sec holdings and the seller's
     *  new sec holdings (indexed such that bidder sec holdings has index 0) */
    public int[] writeSecurityHoldings(BasicOffer newOffer, BasicOffer standingOffer, int units, long time, MarketDef mi, Connection conn) throws SQLException {
        int holdings1 = getSecurityHoldings(newOffer.getSubjectId(), newOffer.getMarketId(), mi);
        if (newOffer.getAction() == JMConstants.BUY_ACTION)
            holdings1 += units;
        else
            holdings1 -= units;
        writeSecurityHoldings(newOffer.getSubjectId(), newOffer.getMarketId(), holdings1, time, mi, conn);
        
        int holdings2 = getSecurityHoldings(standingOffer.getSubjectId(), standingOffer.getMarketId(), mi);
        if (standingOffer.getAction() == JMConstants.BUY_ACTION)
            holdings2 += units;
        else
            holdings2 -= units;
        writeSecurityHoldings(standingOffer.getSubjectId(), standingOffer.getMarketId(), holdings2, time, mi, conn);
        
        int[] secHoldings = new int[2];
        if (newOffer.getAction() == JMConstants.BUY_ACTION) {
            secHoldings[0] = holdings1;
            secHoldings[1] = holdings2;
        } else {
            secHoldings[0] = holdings2;
            secHoldings[1] = holdings1;
        }
        return secHoldings;
    }
    
    /** Open a transaction with the given timetamp in the transaction_book table and
     *  return the granted transaction ID */
    public int openTransaction(long time, Connection conn) throws TransactionInterruptedException {
        Object[] results = null;
        try {
            String update = "insert into transaction_book (units, price, time_entry) values(0, 0.0," + time + ")";
            results = dbc.executeUpdate(update, conn);
            ResultSet rs = (ResultSet) results[0];
            rs.next();
            return rs.getInt(1);

        }catch(Exception e) {
            log.error("Failed to open a transaction in the database", e);
            throw new TransactionInterruptedException();
        }finally{
            dbc.closeQuery(results);
        }
    }
    
    /** Close a transaction by filling in the number of units transacted. This is called after
     *  all transaction_sides have been recorded */
    public void closeTransaction(int transId, int units, float txnPrice, Connection conn) throws TransactionInterruptedException {
        Object[] results = null;
        try {
            String[] matchNames = {"id"};
            String[] matchValues = {"" + transId};
            String[] setNames = {"units", "price"};
            String[] setValues = {String.valueOf(units), String.valueOf(txnPrice)};
            results = dbc.update("transaction_book", setNames, setValues, matchNames, matchValues, conn);
            
        }catch(Exception e) {
            log.error("Failed to close a transaction in the database", e);
            throw new TransactionInterruptedException();
        }finally{
            dbc.closeQuery(results);
        }
    }
    
    
    /**
     * Write the given trade into the transaction_book and transaction_parties tables. The
     *  offer_book_id field in the transaction_book table is filled by the id in the offer_book of
     *  the STANDING order, since the NEW order (the order that resulted in the transaction) may not
     *  have an entry in the offer_book at all if it was filled entirely by standing orders (i.e. the
     *  new order may never become a limit order, hence it may never enter the offer_book). The id_db
     *  field must be filled in the standingOffer -- as it is with the BasicOffer objects returned by
     *  getNextOffer(). Also update the subjects' cash holdings and security holdings after the
     *  transaction. Update and return the Trade object with the post-trade information
     */
    public Trade writeTrade(int sessionId, int periodId, Trade trade, MarketDef mi, Connection conn) throws TransactionInterruptedException {
        Object[] results = null;
        try {
            int offerId_db = trade.getStandingOffer().getId_db();
            int transId = trade.getTransId();
            int units = trade.getUnitsTraded();
            long time = trade.getNewOffer().getTime();
            
            int offerType = 1;
            if (trade.getStandingOffer().getAction() == JMConstants.SELL_ACTION)
                offerType = 2;
            
            String[] values = {String.valueOf(transId), String.valueOf(offerId_db), String.valueOf(offerType), String.valueOf(units)};
            results = dbc.insert("transaction_sides", values, conn);
            
            float price = trade.getStandingOffer().getPrice();
            float cashChange = units * price;
            
            trade.setPostBidCash(trade.getPreBidCash() - cashChange);
            trade.setPostAskCash(trade.getPreAskCash() + cashChange);
            
            trade.setPostBidSec(trade.getPreBidSec() + units);
            trade.setPostAskSec(trade.getPreAskSec() - units);
            
            writeCashHoldings(sessionId, periodId, trade.getBidParty_db(), trade.getPostBidCash(), time, conn);
            writeCashHoldings(sessionId, periodId, trade.getAskParty_db(), trade.getPostAskCash(), time, conn);
            writeSecurityHoldings(trade.getBidParty_db(), trade.getMarketId(), trade.getPostBidSec(), time, mi, conn);
            writeSecurityHoldings(trade.getAskParty_db(), trade.getMarketId(), trade.getPostAskSec(), time, mi, conn);
            
            return trade;
            
        }catch(Exception e) {
            log.error("Failed to write a transaction into the database", e);
            throw new TransactionInterruptedException();
        }finally{
            dbc.closeQuery(results);
        }
    }
    
    
    public void writeTrade(int transId, int marketOrderId, int unitsTransacted, int offerType, Connection conn) throws TransactionInterruptedException {
        Object[] results = null;
        try {
            String[] values = {String.valueOf(transId), String.valueOf(marketOrderId), String.valueOf(offerType), String.valueOf(unitsTransacted)};
            results = dbc.insert("transaction_sides", values, conn);
        }catch(Exception e) {
            log.error("Failed to write a transaction into the database", e);
            throw new TransactionInterruptedException();
        }finally{
            dbc.closeQuery(results);
        }
    }
    
    /** Write the trade as above but have the order transacted at the given execute price */
    public Trade writeTrade(int sessionId, int periodId, Trade trade, MarketDef mi, int executePriceId, float executePrice, Connection conn) throws TransactionInterruptedException {
        Object[] results =null;
        try {
            int offerId_db = trade.getStandingOffer().getId_db();
            int transId = trade.getTransId();
            int units = trade.getUnitsTraded();
            long time = trade.getNewOffer().getTime();
            
            int offerType = 1;
            if (trade.getStandingOffer().getAction() == JMConstants.SELL_ACTION)
                offerType = 2;
            
            String[] values = {String.valueOf(transId), String.valueOf(offerId_db), String.valueOf(offerType), String.valueOf(units)};
            results = dbc.insert("transaction_sides", values, conn);
            
            
            float price = executePrice;
            float cashChange = units * price;
            
            trade.setPostBidCash(trade.getPreBidCash() - cashChange);
            trade.setPostAskCash(trade.getPreAskCash() + cashChange);
            
            trade.setPostBidSec(trade.getPreBidSec() + units);
            trade.setPostAskSec(trade.getPreAskSec() - units);
            
            writeCashHoldings(sessionId, periodId, trade.getBidParty_db(), trade.getPostBidCash(), time, conn);
            writeCashHoldings(sessionId, periodId, trade.getAskParty_db(), trade.getPostAskCash(), time, conn);
            writeSecurityHoldings(trade.getBidParty_db(), trade.getMarketId(), trade.getPostBidSec(), time, mi, conn);
            writeSecurityHoldings(trade.getAskParty_db(), trade.getMarketId(), trade.getPostAskSec(), time, mi, conn);
            
            return trade;
            
        }catch(Exception e) {
            log.error("Failed to write a transaction into the database", e);
            throw new TransactionInterruptedException();
        }finally{
            dbc.closeQuery(results);
        }
    }
    
    /** Returns ASK offers less than or equal to the given ASK price in the offer book.
     *
     *  Obeys the following ordering:
     *  First sort by price level -- return the lowest asks first
     *  Then sort by timestamp -- return the oldest asks first
     *
     *  The desired query is:
     *
     *  SELECT offer_book.*, security_pricelevels.price_level FROM offer_book, security_pricelevels,
     *  period_securities WHERE offer_book.pricelevel_id=security_pricelevels.id AND
     *  security_pricelevels.period_security_id=period_securities.id AND period_securities.session_id=sessionId
     *  AND period_securities.period_id=periodId AND period_securities.id=marketId
     *  AND (security_pricelevels.price_level>=askPrice OR offer_book.pricelevel_id=priceId_db)
     *  AND offer_book.offer_type=2 AND offer_book.offer_status=0 ORDER BY security_pricelevels.price_level, offer_book.time_entry;
     *
     */
    public Object[] getAskOffers(int sessionId, int periodId, int marketId, int priceId, float bidPrice, MarketDef mi, Connection conn) throws TransactionInterruptedException {
        try {
            int marketId_db = getPeriodSecurityId_db(marketId, mi);
            int priceId_db = getPriceId_db(marketId, priceId, mi);
            
            StringBuffer query = new StringBuffer();
            query.append("select offer_book.*, security_pricelevels.price_level from offer_book, security_pricelevels, period_securities ");
            query.append("where offer_book.pricelevel_id=security_pricelevels.id ");
            query.append("and security_pricelevels.period_security_id=period_securities.id ");
            query.append("and period_securities.session_id=").append(sessionId).append(" ");
            query.append("and period_securities.period_id=").append(periodId).append(" ");
            query.append("and period_securities.id=").append(marketId_db).append(" ");
            query.append("and (security_pricelevels.price_level<=").append(bidPrice).append(" ");
            query.append("or offer_book.pricelevel_id=").append(priceId_db).append(") ");
            query.append("and offer_book.offer_type=").append(JMConstants.SELL_ACTION);
            query.append(" and offer_book.offer_status='" + JMConstants.ORDER_STATUSES[JMConstants.ORDER_VALID] + "' ");
            query.append(" and offer_book.entry_type='" + JMConstants.ORDER_META_LIMIT + "' ");
            query.append("order by security_pricelevels.price_level, offer_book.time_entry");
            
            Object[] results = dbc.executeQuery(query.toString(), conn);
            
            return results;
        }catch(Exception e) {
            log.error("Error checking for the ask orders below the bid price " + bidPrice, e);
            throw new TransactionInterruptedException();
        }
    }
    
   /** 
    * Returns as getAskOffers but ordered by time.
    */
    public Object[] getAskOffersOrderedByTime(int sessionId, int periodId, int marketId, int priceId, float bidPrice, MarketDef mi, Connection conn) throws TransactionInterruptedException {
        try {
            int marketId_db = getPeriodSecurityId_db(marketId, mi);
            int priceId_db = getPriceId_db(marketId, priceId, mi);
            
            StringBuffer query = new StringBuffer();
            query.append("select offer_book.*, security_pricelevels.price_level from offer_book, security_pricelevels, period_securities ");
            query.append("where offer_book.pricelevel_id=security_pricelevels.id ");
            query.append("and security_pricelevels.period_security_id=period_securities.id ");
            query.append("and period_securities.session_id=").append(sessionId).append(" ");
            query.append("and period_securities.period_id=").append(periodId).append(" ");
            query.append("and period_securities.id=").append(marketId_db).append(" ");
            query.append("and (security_pricelevels.price_level<=").append(bidPrice).append(" ");
            query.append("or offer_book.pricelevel_id=").append(priceId_db).append(") ");
            query.append("and offer_book.offer_type=").append(JMConstants.SELL_ACTION);
            query.append(" and offer_book.offer_status='" + JMConstants.ORDER_STATUSES[JMConstants.ORDER_VALID] + "' ");
            query.append(" and offer_book.entry_type='" + JMConstants.ORDER_META_LIMIT + "' ");
            query.append("order by security_pricelevels.price_level DESC, offer_book.time_entry");
            
            Object[] results = dbc.executeQuery(query.toString(), conn);
            
            return results;
        }catch(Exception e) {
            log.error("Error checking for the ask orders below the bid price " + bidPrice, e);
            throw new TransactionInterruptedException();
        }
    }
    
    /** Returns BID offers greater than or equal to the given ASK price in the offer book.
     *
     *  Obeys the following ordering:
     *  First sort by price level -- return the highest bids first
     *  Then sort by timestamp -- return the oldest bids first
     *
     *  The desired query is:
     *
     *  SELECT offer_book.*, security_pricelevels.price_level FROM offer_book, security_pricelevels,
     *  period_securities WHERE offer_book.pricelevel_id=security_pricelevels.id AND
     *  security_pricelevels.period_security_id=period_securities.id AND period_securities.session_id=sessionId
     *  AND period_securities.period_id=periodId AND period_securities.id=marketId
     *  AND (security_pricelevels.price_level>=askPrice OR offer_book.pricelevel_id=priceId_db)
     *  AND offer_book.offer_type=1 AND offer_book.offer_status=0 ORDER BY security_pricelevels.price_level DESC, offer_book.time_entry;
     *
     */
    public Object[] getBidOffers(int sessionId, int periodId, int marketId, int priceId, float askPrice, MarketDef mi, Connection conn) throws TransactionInterruptedException {
        try {
            int marketId_db = getPeriodSecurityId_db(marketId, mi);
            int priceId_db = getPriceId_db(marketId, priceId, mi);
            
            StringBuffer query = new StringBuffer();
            query.append("select offer_book.*, security_pricelevels.price_level from offer_book, security_pricelevels, period_securities ");
            query.append("where offer_book.pricelevel_id=security_pricelevels.id ");
            query.append("and security_pricelevels.period_security_id=period_securities.id ");
            query.append("and period_securities.session_id=").append(sessionId).append(" ");
            query.append("and period_securities.period_id=").append(periodId).append(" ");
            query.append("and period_securities.id=").append(marketId_db).append(" ");
            query.append("and (security_pricelevels.price_level>=").append(askPrice).append(" ");
            query.append("or offer_book.pricelevel_id=").append(priceId_db).append(") ");
            query.append("and offer_book.offer_type=").append(JMConstants.BUY_ACTION);
            query.append(" and offer_book.offer_status='" + JMConstants.ORDER_STATUSES[JMConstants.ORDER_VALID] + "' ");
            query.append(" and offer_book.entry_type='" + JMConstants.ORDER_META_LIMIT + "' ");
            query.append("order by security_pricelevels.price_level DESC, offer_book.time_entry");
            
            Object[] results = dbc.executeQuery(query.toString(), conn);
            
            return results;
        }catch(Exception e) {
            log.error("Error checking for the bid orders above the ask price " + askPrice, e);
            throw new TransactionInterruptedException();
        }
    }
    
     /** 
      * Same as getBidOffers above but this one orders by time of entry.
      */
    public Object[] getBidOffersOrderedByTime(int sessionId, int periodId, int marketId, int priceId, float askPrice, MarketDef mi, Connection conn) throws TransactionInterruptedException {
        try {
            int marketId_db = getPeriodSecurityId_db(marketId, mi);
            int priceId_db = getPriceId_db(marketId, priceId, mi);
            
            StringBuffer query = new StringBuffer();
            query.append("select offer_book.*, security_pricelevels.price_level from offer_book, security_pricelevels, period_securities ");
            query.append("where offer_book.pricelevel_id=security_pricelevels.id ");
            query.append("and security_pricelevels.period_security_id=period_securities.id ");
            query.append("and period_securities.session_id=").append(sessionId).append(" ");
            query.append("and period_securities.period_id=").append(periodId).append(" ");
            query.append("and period_securities.id=").append(marketId_db).append(" ");
            query.append("and (security_pricelevels.price_level>=").append(askPrice).append(" ");
            query.append("or offer_book.pricelevel_id=").append(priceId_db).append(") ");
            query.append("and offer_book.offer_type=").append(JMConstants.BUY_ACTION);
            query.append(" and offer_book.offer_status='" + JMConstants.ORDER_STATUSES[JMConstants.ORDER_VALID] + "' ");
            query.append(" and offer_book.entry_type='" + JMConstants.ORDER_META_LIMIT + "' ");
            query.append("order by security_pricelevels.price_level DESC, offer_book.time_entry");
            
            Object[] results = dbc.executeQuery(query.toString(), conn);
            
            return results;
        }catch(Exception e) {
            log.error("Error checking for the bid orders above the ask price " + askPrice, e);
            throw new TransactionInterruptedException();
        }
    }
    
    /** Returns the offers made by the given subject on the given market and price level.
     *  These offers will be processed for cancellation by the TradeServ, which will retrieve
     *  them by calling the getNextOffer function here */
    public Object[] getOffersForCancel(int subjectId_db, int marketId, int priceId, MarketDef mi, Connection conn) throws TransactionInterruptedException {
        try {
            int priceId_db = getPriceId_db(marketId, priceId, mi);
            
            StringBuffer query = new StringBuffer();
            query.append("select * from offer_book where ");
            query.append("subject_id=").append(subjectId_db).append(" and ");
            query.append("pricelevel_id=").append(priceId_db).append(" and ");
            query.append("offer_status='" + JMConstants.ORDER_STATUSES[JMConstants.ORDER_VALID] + "'");
            query.append(" and offer_book.entry_type='" + JMConstants.ORDER_META_LIMIT + "' ");
            
            Object[] results = dbc.executeQuery(query.toString(), conn);
            
            return results;
        }catch(Exception e) {
            log.error("Error checking for orders valid for cancellation", e);
            throw new TransactionInterruptedException();
        }
    }
    
    /**
     * Given the Object[] object returned from getBidOffers or getAskOffers, return the next BasicOffer object
     *  that can form a transaction with the newOffer in the TradeServ. Return null if there are no more
     *  offers to be returned. Also returns cancel orders from the Object[] from getOffersForCancel function
     */
    public BasicOffer getNextOffer(int marketId, Object[] results, SubjectDef si, MarketDef mi, Connection conn) throws TransactionInterruptedException {
        try {
            ResultSet rs = (ResultSet) results[0];
            
            if (rs.next()) {
                BasicOffer standingOffer = new BasicOffer();
                
                standingOffer.setTime(rs.getInt("time_entry"));
                standingOffer.setAction(rs.getInt("offer_type"));
                standingOffer.setUnits(rs.getInt("offer_units"));
                standingOffer.setSubjectId_db(rs.getInt("subject_id"));
                standingOffer.setSubjectId(si.getId(standingOffer.getSubjectId_db()));
                standingOffer.setMarketId(marketId);
                standingOffer.setPriceId(getPriceId(marketId, rs.getInt("pricelevel_id"), mi));
                standingOffer.setPrice(getPrice(marketId, standingOffer.getPriceId(), mi));
                standingOffer.setId_db(rs.getInt("id"));
                
                return standingOffer;
            } else {
                return null;
            }
        }catch(SQLException e) {
            log.error("Failed to get the next transaction offer", e);
            throw new TransactionInterruptedException();
        }
    }
    
    /** Close the given result set of offers */
    public void closeOffers(Object[] results) {
        dbc.closeQuery(results);
    }
    
    /** Set the given order to 'Executed' status and specify the time at which this occured. Also update
     *  the ticker tape with the number of units changed */
    public void executeOffer(int offerId, long time, int unitsChanged, Connection conn) throws TransactionInterruptedException {
        Object[] results = null;
        try {
            String[] matchNames = {"id"};
            String[] matchValues = {"" + offerId};
            String[] setNames = {"offer_status", "offer_units", "time_changestatus"};
            String[] setValues = {JMConstants.ORDER_STATUSES[JMConstants.ORDER_TRANSACTED], String.valueOf(0), String.valueOf(time)};
            results = dbc.update("offer_book", setNames, setValues, matchNames, matchValues, conn);
            dbc.closeQuery(results);
            
            String[] values = {"0", String.valueOf(offerId), String.valueOf(unitsChanged), JMConstants.ORDER_STATUSES[JMConstants.ORDER_TRANSACTED], String.valueOf(time)};
            results = dbc.insert("ticker_tape", values, conn);
        }catch(Exception e) {
            log.error("Failed to execute the offer " + offerId + " in database", e);
            throw new TransactionInterruptedException();
        }finally{
            dbc.closeQuery(results);
        }
    }
    
    /** Set the given offer to the new number of units and specify when this occured. Update the ticker
     *  tape as well */
    public void updateOffer(int offerId, int units, long time, int unitsChanged, int orderChangeStatus, Connection conn) throws TransactionInterruptedException {
        Object[] results =null;
        try {
            String[] matchNames = {"id"};
            String[] matchValues = {String.valueOf(offerId)};
            String[] setNames = {"offer_units", "time_changestatus"};
            String[] setValues = {String.valueOf(units), String.valueOf(time)};
            results = dbc.update("offer_book", setNames, setValues, matchNames, matchValues, conn);
            dbc.closeQuery(results);

            String[] values = new String[5];
            values[0] = "0";
            values[1] = String.valueOf(offerId);
            values[2] = String.valueOf(unitsChanged);
            
            if(orderChangeStatus == JMConstants.ORDER_TRANSACTED)
                values[3]= JMConstants.ORDER_STATUSES[JMConstants.ORDER_TRANSACTED];
            else
                values [3] = JMConstants.ORDER_STATUSES[JMConstants.ORDER_CANCELLED];
            
            values[4] = String.valueOf(time);
            
            results = dbc.insert("ticker_tape", values, conn);
            
        }catch(Exception e) {
            log.error("Failed to update offer " + offerId + " in database", e);
            throw new TransactionInterruptedException();
        }finally{
            dbc.closeQuery(results);
        }
    }
    
    /** Cancel the given offer. It must have its id_db field filled out (as all offers called
     *  from getNextOffer do). Update the ticker tape as well */
    public void cancelOffer(int offerId, long time, int unitsChanged, Connection conn) throws TransactionInterruptedException {
        Object[] results = null;
        try {
            String[] matchNames = {"id"};
            String[] matchValues = {"" + offerId};
            String[] setNames = {"offer_status", "offer_units", "time_changestatus"};
            String[] setValues = {JMConstants.ORDER_STATUSES[JMConstants.ORDER_CANCELLED], String.valueOf(0), String.valueOf(time)};
            results = dbc.update("offer_book", setNames, setValues, matchNames, matchValues, conn);
            dbc.closeQuery(results);

            String[] values = {"0", String.valueOf(offerId), String.valueOf(unitsChanged), JMConstants.ORDER_STATUSES[JMConstants.ORDER_CANCELLED], String.valueOf(time)};
            results = dbc.insert("ticker_tape", values, conn);
            
        }catch(Exception e) {
            log.error("Failed to cancel the offer " + offerId + " in the database", e);
            throw new TransactionInterruptedException();
        }finally{
            dbc.closeQuery(results);
        }
    }
    
    public float getAvgTransactionPrice(int sessionId, int periodId, String securityName){
        float avgPrice = -1; 
        Connection conn = null;
        Object[] results = null;
        try {
            conn = dbc.getConnection(); 
            StringBuffer query = new StringBuffer();
            query.append("select avg(tb.price) from periods p, period_securities ps, securities s, security_pricelevels sl, transaction_book tb, transaction_sides ts, offer_book ob");
            query.append(" where p.session_id=").append(sessionId).append(" and p.period_id=").append(periodId).append(" and s.security_name='").append(securityName).append("' and ");
            query.append("ps.session_id=p.session_id and ps.period_id=p.period_id and security_id=s.id and sl.period_security_id=ps.id and sl.id=ob.pricelevel_id and ob.id=ts.offer_id and ts.transaction_id=tb.id group by s.security_name");
            
            results = dbc.executeQuery(query.toString(), conn);
            
            ResultSet rs = (ResultSet)results[0]; 
            if(rs.next())
                avgPrice = rs.getFloat(1); 
            
        }catch(Exception e) {
            log.error("Error querying average transaction price... ", e);
        }finally{
            dbc.closeQuery(results, conn);
        }
        
        return avgPrice; 
    }
    
    public String getSubjNameById(int clientId){
        String name = null; 
        Connection conn = null; 
        Object[] results = null;
        try {
            conn = dbc.getConnection(); 
            StringBuffer query = new StringBuffer("select * from jm_user where id=" + clientId);
           
            results = dbc.executeQuery(query.toString(), conn);
            
            ResultSet rs = (ResultSet)results[0]; 
            if(rs.next())
                name = rs.getString("fname") + " " + rs.getString("lname");
                
        }catch(Exception e) {
            log.error("Error querying subject name by id... ", e);
        }finally{
            dbc.closeQuery(results, conn);
        }
        
        return name; 
    }
    
    public int getClientIdByEmailAndPassword(String email, String passwd, int role){
        int id = -1; 
        Connection conn = null;
        Object[] results = null;
        try {
            conn = dbc.getConnection(); 
            StringBuffer query = null; 
            if(role == JMConstants.USER_ROLE)
                query= new StringBuffer("select id from jm_user where email='" + email + "' and passwd=PASSWORD('"+passwd+"') and role=" + JMConstants.USER_ROLE + ";");
            if(role > JMConstants.USER_ROLE)
                query = new StringBuffer("select id from jm_user where email='" + email + "' and passwd=PASSWORD('"+passwd+"') and role>" + JMConstants.USER_ROLE + ";");
           
            results = dbc.executeQuery(query.toString(), conn);
            
            ResultSet rs = (ResultSet)results[0]; 
            if(rs.next())
                id = rs.getInt("id");
            
        }catch(Exception e) {
            log.error("Failed to authenticate subject by email and passwd... ", e);
        }finally{
            dbc.closeQuery(results, conn);
        }
        
        return id; 
    }
    
    public int registerSubject(String query){
        int id = -1; 
        Connection conn = null;
        Object[] results = null;
        try {
            conn = dbc.getConnection(); 
            results = dbc.executeUpdate(query, conn);
            
            ResultSet rs = (ResultSet)results[0]; 
            if(rs.next())
                id = rs.getInt(1);

        }catch(Exception e) {
            log.error("Failed to register new subject... ", e);
        }finally{
            dbc.closeQuery(results, conn);
        }
        
        return id; 
    }
    
    public List <LabelValueBean> getExperimentsByExperimenterId(int experimenterId){
        List <LabelValueBean> sessions = new ArrayList<LabelValueBean>(); 
        Connection conn = null;
        Object[] results = null;
        try {
            conn = dbc.getConnection(); 
            StringBuffer query = new StringBuffer("select id, name, start_time from sessions where experimenter_id=" + experimenterId);
            results = dbc.executeQuery(query.toString(), conn);
            ResultSet rs = (ResultSet)results[0]; 
            while(rs.next()){
                LabelValueBean label = new LabelValueBean(); 
                String id = String.valueOf(rs.getInt("id")); 
                label.setLabel(id + ": " + rs.getString("name") + ", " + rs.getString("start_time")); 
                label.setValue(id); 
                sessions.add(label); 
            }

        }catch(Exception e) {
            log.error("Error querying experiment list by experimenter id... ", e);
        }finally{
            dbc.closeQuery(results, conn);
        }
        
        return sessions; 
    }
    
    /** Checks to see if the group exists in the market_groups table. If it does, return its ID number. If it
     *  does not, add it to the market_groups table and return its ID number */
    private int addGroup(String group) {
        Connection conn = null;
        Object[] results = null;
        int id = -1;
        try {
            conn = dbc.getConnection();
            
            String query = "select id from market_groups where group_name='" + group + "'";
            results = dbc.executeQuery(query, conn);
            
            //Check if the security is there -- if so, return its id
            if (results != null) {
                ResultSet rs = (ResultSet) results[0];
                if (!rs.wasNull()) {
                    rs.last();
                    int size = rs.getRow();
                    if (size > 0) {
                        id = rs.getInt("id");
                        dbc.closeQuery(results);
                        return id;
                    }
                }
            }
            dbc.closeQuery(results);

            //If the group was not found then add it and return the generated id
            String update = "insert into market_groups values(0, '" + group + "')";
            results = dbc.executeUpdate(update, conn);
            ResultSet rs = (ResultSet) results[0];
            rs.next();
            return rs.getInt(1);
            
        }catch(SQLException e) {
            log.error("Failed to add group " + group + " to market_groups table", e);
        }finally{
            dbc.closeQuery(results, conn);
        }
        return id; 
    }
    
    /** Checks to see if the security exists in the securities table. If it does, return its ID number. If it
     *  does not, add it to the securities table and return its ID number */
    private int addSecurity(String security) {
        Connection conn = null;
        Object[] results = null;
        int id = -1;
        try {
            conn = dbc.getConnection();
            
            String query = "select id from securities where security_name='" + security + "'";
            results = dbc.executeQuery(query, conn);
            
            
            //Check if the security is there -- if so, return its id
            if (results != null) {
                ResultSet rs = (ResultSet) results[0];
                if (!rs.wasNull()) {
                    rs.last();
                    int size = rs.getRow();
                    if (size > 0) {
                        return rs.getInt("id");
                    }
                }
            }
            //dbc.closeQuery(results);

            //If the security was not found then add it and return the generated id
            String update = "insert into securities values(0, '" + security + "')";
            results = dbc.executeUpdate(update, conn);
            ResultSet rs = (ResultSet) results[0];
            rs.next();
            id = rs.getInt(1);
    
        }catch(SQLException e) {
            log.error("Failed to add security " + security + " to securities table", e);
            return -1;
        }finally{
            dbc.closeQuery(results, conn);
        }
        return id;
    }
    
    /** Given a market id and price id return the database price id */
    private int getPriceId_db(int marketId, int priceId, MarketDef marketInfo) {
        float price = marketInfo.getPrices()[marketId][priceId];
        return getPriceId_db(marketId, price, marketInfo);
    }
    
    /** Given a market id and a price level return the database price id */
    private int getPriceId_db(int marketId, float price, MarketDef marketInfo) {
        return marketInfo.getPriceId_db(marketId, price);
    }
    
    /** Given a market and price id return the associated price */
    private float getPrice(int marketId, int priceId, MarketDef marketInfo) {
        return marketInfo.getPrices()[marketId][priceId];
    }
    
    /** Given a database price id return the price id */
    private int getPriceId(int marketId, int priceId_db, MarketDef marketInfo) {
        return marketInfo.getPriceId(marketId, priceId_db);
    }
    
    /** Given a market id return the period security database id */
    private int getPeriodSecurityId_db(int marketId, MarketDef marketInfo) {
        return marketInfo.getPeriodSecurityId(marketId);
    }
    
    /** Given a market id return the security database id */
    private int getSecurityId_db(int marketId, MarketDef marketInfo) {
        return marketInfo.getSecurityId(marketId);
    }
    
    public static Log log = LogFactory.getLog(DBWriter.class);
    private DBConnector dbc;
}
