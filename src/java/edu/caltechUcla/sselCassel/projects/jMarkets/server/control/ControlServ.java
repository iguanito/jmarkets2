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
 * AuthServ.java
 *
 * Created on March 16, 2004, 7:40 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;


import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.data.DBConnector;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.data.DBWriter;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.data.SessionState;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.network.MonitorTransmitter;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.updates.AuthUpdate;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.updates.NewPeriodUpdate;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.updates.PayoffUpdate;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.SessionIdentifier;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.GroupDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.MarketDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.PeriodDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SessionDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SubjectDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable.EarningsInfo;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable.EarningsRow;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.PayoffFunction;

/**
 *
 * @author  Raj Advani, Walter M. Yuan
 */
public class ControlServ implements Controller {
    
    public ControlServ(Properties props) {
        DBConnector dbc = new DBConnector(props);
        dbw = new DBWriter(dbc);
        
        sessionStates = new Hashtable();
    }
    
    /** Get the Database writer used by the ControlServ. This is needed so that the
     *  dispatcher can pass this writer to the TradeServ */
    public DBWriter getDBWriter() {
        return dbw;
    }
    
    /** Start a new session. Create a new SessionState object and write the session to the database,
     *  returning the session ID. Store the session in the sessions hashtable so it can be accessed
     *  by its ID when needed */
    public synchronized int startNewSession(String name, int numClients, int updateTime, SessionDef sessionInfo) {
        SessionState sessionState = createSession(name, numClients, updateTime, sessionInfo);
        int sessionId = dbw.writeSession(name, numClients, sessionInfo);
        sessionStates.put(new Integer(sessionId), sessionState);
        return sessionId;
    }
    
    /** Get the array of database Id numbers associated with the given session. These numbers
     *  are invalid unless the game is running. Therefore return null if the session is still
     *  initializing (first period has not yet begun) */
    public int[] getSessionClients(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot retrieve recipient list for session " + sessionId + " -- that session does not exist!");
            return null;
        }
        
        int state = session.getState();
        if (state == SessionState.ACCEPTING_CLIENTS_STATE || state == SessionState.ADMIN_CONNECTING_STATE || state == SessionState.CLIENTS_FULL_STATE)
            return null;
        else
            return session.getAllDatabaseIds();
    }
    
    /** Create a new session for the given number of clients. Return the SessionState object created,
     *  which contains all the session information */
    private SessionState createSession(String name, int numClients, int updateTime, SessionDef sessionInfo) {
        SessionState sessionState = new SessionState(name, numClients, updateTime, sessionInfo);
        return sessionState;
    }
    
    /** Return an array of SessionIdentifier objects that contain the ID numbers, names, and status
     *  of all active sessions. Synchronized so that new sessions are not created while
     *  retrieving identifiers */
    public synchronized SessionIdentifier[] getSessionIdentifiers() {
        SessionIdentifier[] identifiers = new SessionIdentifier[sessionStates.size()];
        
        Enumeration sessions = sessionStates.keys();
        int index = 0;
        while (sessions.hasMoreElements()) {
            Integer sessionId = (Integer) sessions.nextElement();
            SessionState sessionState = (SessionState) sessionStates.get(sessionId);
            
            String name = sessionState.getName();
            int id = sessionId.intValue();
            int numClients = sessionState.getNumClients();
            int state = sessionState.getState();
            
            String status = "Experiment in progress";
            if (state == SessionState.ACCEPTING_CLIENTS_STATE)
                status = "Accepting new clients";
            if (state == SessionState.ADMIN_CONNECTING_STATE)
                status = "Waiting for admin";
            
            identifiers[index] = new SessionIdentifier(id, name, status, numClients);
            index++;
        }
        return identifiers;
    }
    
    /** Return an array of active session ID numbers */
    public int[] getSessionIds() {
        int[] ids = new int[sessionStates.size()];
        
        Enumeration sessions = sessionStates.keys();
        int index = 0;
        while (sessions.hasMoreElements()) {
            ids[index] = ((Integer) sessions.nextElement()).intValue();
            index++;
        }
        return ids;
    }
    
    /** Register the given ExpMonitor (MonitorTransmitter interface) with the given session ID.
     *  Return true if this is the first ExpMonitor link established for this session. Otherwise
     *  return false. The state will be set to ACCEPTING_CLIENTS_STATE once at least one ExpMonitor
     *  has been registered. For ExpMonitors that register after the initial one, the dispatcher will
     *  instruct the MonitorServ to send them the price chart, client connection status, and button status
     *  information */
    public boolean registerExpMonitor(int sessionId, MonitorTransmitter ui) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session.getState() == SessionState.ADMIN_CONNECTING_STATE) {
            session.setState(SessionState.ACCEPTING_CLIENTS_STATE);
            
            log.info("Session " + sessionId + " has established initial ExpMonitor link -- awaiting client connections");
            log.info("After the session begins it is safe to shut down this monitor");
            
            return true;
        }
        
        return false;
    }
    
    /** Authenticate the given client into the given session. First check to see if the session
     *  is in a state where it can accept new clients. If it is, add the client to the session
     *  and return an AuthUpdate to the DispatchServ so that it can send out a confirmation to
     *  the client. Otherwise return an AuthUpdate with an error message.  The disconnected Hashtable
     *  is sent by the DispatchServ and tell us what clients are currently disconnected from the
     *  the server. This is needed so that we do not reauthenticate a client who is already connected */
    public AuthUpdate authClient(int sessionId, String name, int dbId, Hashtable disconnected) {
        try {
            log.debug("ControlServ has received an authentication request for client " + dbId + " in session " + sessionId);
            
            SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
            if (session == null) {
                log.error("Cannot authenticate client for session " + sessionId + " -- that session is inactive!");
                return createAuthFailedPacket("You cannot connect to an inactive session", false);
            }
            
            int state = session.getState();
            
            if (state == SessionState.CLIENTS_FULL_STATE) {
                log.info("Rejected a client authentication attempt: no more client slots available, all connected");
                return createAuthFailedPacket("No more players are needed for the current game", false);
            }
            
            else if (state == SessionState.ADMIN_CONNECTING_STATE) {
                log.info("Rejecting a client authentication attempt: admin connection not yet established for current game");
                return createAuthFailedPacket("Game is not yet ready; please retry in a few seconds", true);
            }
            
            else if (state == SessionState.SHUTDOWN_STATE) {
                log.info("Rejected a client authentication attempt: there is no game currently running on this server");
                return createAuthFailedPacket("No game is currently running on this server", false);
            }
            
            else if (state == SessionState.GAME_RUNNING_STATE) {
                return reauthClient(sessionId, session, name, dbId, disconnected);
            }
            
            else if (state == SessionState.ACCEPTING_CLIENTS_STATE) {
                return authClient(sessionId, session, name, dbId);
            }
        }catch(Exception e) {
            log.error("Failed to process an authentication request in the ControlServ", e);
        }
        return createAuthFailedPacket("Authentication failed for unknown reason", false);
    }
    
    /** Authenticate the given client into the given session. Collect the authentication
     *  information into an AuthUpdate object, which will be returned to the dispatcher */
    private AuthUpdate authClient(int sessionId, SessionState session, String name, int dbId) {
        try {
            int id = session.getNumConnected();
            boolean connectSuccess = session.addClient(id, dbId, name);
            
            if (!connectSuccess) {
                log.info("Client " + name + " with database id " + dbId + " is attempting to connect multiple times -- rejecting");
                return createAuthFailedPacket("Authentication failed -- you may not connect multiple times", false);
            }
            
            AuthUpdate authPacket = new AuthUpdate(AuthUpdate.AUTH_SUCCESS);
            authPacket.setUpdateTime(session.getUpdateTime());
            authPacket.setId(id);
            authPacket.setName(name);
            
            log.info("Client " + name + " (ID: " + (id) + ") has been authenticated. Waiting for " + (session.getNumClients() - session.getNumConnected() - 1) + " more clients");
            
            session.setNumConnected(session.getNumConnected() + 1);
            
            boolean allConnected = false;
            if (session.getNumConnected() == session.getNumClients()) {
                allConnected = true;
                session.setState(SessionState.CLIENTS_FULL_STATE);
            }
            
            authPacket.setNumConnected(session.getNumConnected());
            authPacket.setAllConnected(allConnected);
            
            return authPacket;
            
        }catch(Exception e) {
            log.error("Failed to authenticate client to session " + sessionId, e);
        }
        return createAuthFailedPacket("Authentication failed for unknown reason", false);
    }
    
    /** Attemp to re-authenticate the given client. This is called whenever a client sends an
     *  authentication request into a session that is already running. Re-authentication will fail
     *  if the client is not a part of the session. In this case an AUTH_FAILED authPacket is sent
     *  back to the dispatcher. Otherwise return a RE_AUTH_SUCCESS packet to the dispatcher, which
     *  will contain all the state information needed by the re-authenticating client */
    private AuthUpdate reauthClient(int sessionId, SessionState session, String name, int dbId, Hashtable disconnected) {
        try {
            PeriodDef pinfo = session.getSession().getPeriod(session.getPeriodNum());
            SubjectDef sinfo = pinfo.getSubjectInfo();
            int id = sinfo.getId(dbId);
            if (id == -1) {
                return createAuthFailedPacket("Re-authentication failed: You are not enrolled in session " + sessionId, false);
            }
            
            Boolean discon = (Boolean) disconnected.get(new Integer(dbId));
            if (discon == null) {
                return createAuthFailedPacket("Re-authentication failed: You have no connectivity status in session " + sessionId, false);
            }
            
            disconnected.put(new Integer(dbId), Boolean.FALSE);
            
            AuthUpdate authPacket = new AuthUpdate(AuthUpdate.RE_AUTH_SUCCESS);
            authPacket.setUpdateTime(session.getUpdateTime());
            authPacket.setId(id);
            authPacket.setName(name);
            authPacket.setPeriodInfo(pinfo);
            
            if (!discon.booleanValue()) {
                authPacket.setReplacement(true);
            } else {
                authPacket.setReplacement(false);
            }
            
            log.info("Client " + name + " (ID: " + (id) + ") is  re-authenticating into session " + sessionId);
            
            return authPacket;
            
        }catch(Exception e) {
            log.error("Failed to re-authenticate client to session " + sessionId, e);
        }
        return createAuthFailedPacket("Re-authentication failed for unknown reason", false);
    }
    
    /** Create and return an AuthUpdate with the given error message */
    private AuthUpdate createAuthFailedPacket(String msg, boolean retry) {
        AuthUpdate failPacket = new AuthUpdate(AuthUpdate.AUTH_FAILED);
        failPacket.setErrorMsg(msg);
        failPacket.setRetry(retry);
        
        return failPacket;
    }
    
    /** Start the session that corresponds to the given session ID */
    public boolean startSession(int sessionId) {
        log.debug("ControlServ is starting session " + sessionId);
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot start session " + sessionId + " -- that session does not exist!");
            return false;
        }
        
        int state = session.getState();
        if (state == SessionState.GAME_RUNNING_STATE || state == SessionState.SHUTDOWN_STATE) {
            log.error("Cannot start session " + sessionId + " -- that session is either shutdown or already running");
            return false;
        }
        
        session = trimAndInitSession(session);
        
        log.info("All clients have connected or have been trimmed -- starting session...");
        
        session.setState(SessionState.GAME_RUNNING_STATE);
        session.setPeriodNum(-1);
        dbw.writeSessionEvent(sessionId, JMConstants.ACTION_START);
        
        return true;
    }
    
    /** Return true if the given session is currently running -- that is, if it not only valid but
     *  has also started */
    public boolean isSessionRunning(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot check running status of session " + sessionId + " -- that session does not exist!");
            return false;
        }
        
        if (session.getState() == SessionState.GAME_RUNNING_STATE)
            return true;
        else
            return false;
    }
    
    /** Trim away all clients who are not connected from the given session. Then create all the
     *  SessionState data objects that are dependant on the number of clients (i.e. fixed length
     *  arrays). Return the modified SessionState object */
    private SessionState trimAndInitSession(SessionState sessionState) {
        int numConnected = sessionState.getNumConnected();
        int numClients = sessionState.getNumClients();
        
        if (numConnected < numClients) {
            log.info("Trimming the " + (numClients - numConnected) + " clients who have not yet connected");
            sessionState.trimClients(numConnected);
        }
        
        numClients = sessionState.getNumClients();
        
        EarningsInfo[] earningsHistory = new EarningsInfo[numClients];
        for (int i=0; i<earningsHistory.length; i++)
            earningsHistory[i] = new EarningsInfo();
        sessionState.setEarningsHistory(earningsHistory);
        
        Trader[][] traders = new Trader[sessionState.getSession().getNumPeriods()][numClients];
        sessionState.setTraders(traders);
        
        return sessionState;
    }
    
    /** Checks to see if the session ID in the given request is valid. Returns
     *  true if the ID is valid. Returns false otherwise */
    public boolean isSessionValid(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session != null)
            return true;
        
        return false;
    }
    
    /** Updates the times of the given session. Called from the dispatcher when it
     *  receives an update from the JMTimer */
    public boolean updateTimers(int sessionId, int openDelay, int periodLength, int[] marketLength) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot update timers of session " + sessionId + " -- that session does not exist");
            return false;
        }
        
        session.setPeriodLength(periodLength);
        session.setOpenDelay(openDelay);
        session.setMarketLength(marketLength);
        
        return true;
    }
    
    /** Set the Trader objects for the given period. These objects hold all the transaction information
     *  of the clients */
    public void setTraders(int sessionId, int period, Trader[] traders) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot set traders of session " + sessionId + " -- that session does not exist");
            return;
        }
        
        for(Trader t : traders){
            t.setCacheOrders(session.getSession().isShowPastOrders()); 
            t.setCacheTransactions(session.getSession().isShowPastTransactions()); 
            t.setClosebook(session.getSession().getPeriod(period).isClosebook()); 
            t.setShowSuggestedClearingPrice(session.getSession().getPeriod(period).isShowSuggestedClearingPrice()); 
        }
        session.setTraders(period, traders);
    }
    
    /** Updates the closure status of the given market in the given session. Called from the
     *  dispatcher when it receives a market closure update from the JMTimer */
    public boolean closeMarket(int sessionId, int market) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot close market of session " + sessionId + " -- that session does not exist");
            return false;
        }
        
        session.setMarketClosed(market);
        
        return true;
    }
    
    /** Updates the closure status of the current period in the given session. Called from the
     *  dispatcher when it receieves a period closure update from the JMTimer. Return true
     *  if this was the last period */
    public boolean closePeriod(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot close period of session " + sessionId + " -- that session does not exist!");
            return false;
        }
        
        session.setPeriodClosed(true);
        dbw.writePeriodEvent(sessionId, session.getPeriodNum(), JMConstants.ACTION_FINISH);
        return isLastPeriod(sessionId);
    }
    
    /** Returns the current period number of the given session */
    public int getPeriodNum(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot get period number of session " + sessionId + " -- that session does not exist!");
            return -1;
        }
        
        int periodNum = session.getPeriodNum();
        return periodNum;
    }
    
    public boolean getManualControl(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if( session == null ){
            log.error("Cannot get manual control boolean in session "+sessionId+" -- that session does not exist!");
        }
        return  session.isManualControl();
    }
    
    public void setManualControl(int sessionId, boolean manual) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if( session == null ){
            log.error("Cannot set manual control boolean in session "+sessionId+" -- that session does not exist!");
        }
        session.setManualControl(manual);
    }
    
    /** Returns the amount of period time remaining in the given session */
    public int getPeriodTime(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot get period time remaining in session " + sessionId + " -- that session does not exist!");
            return -1;
        }
        
        return session.getPeriodLength();
    }
    
    /** Returns the amount of market opening time remaining in the given session */
    public int getOpeningTime(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot get opening time remaining in session " + sessionId + " -- that session does not exist!");
            return -1;
        }
        
        return session.getOpenDelay();
    }
    
    
    /** Returns the amount of market time remaining in the given session for each market */
    public int[] getMarketTime(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot get market time remaining in session " + sessionId + " -- that session does not exist!");
            return null;
        }
        
        return session.getMarketLength();
    }
    
    /** Returns an array of the names of the clients participating in the given session. These
     *  are indexed by the system id number (not the database id number) */
    public String[] getSubjectNames(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot get subject names for session " + sessionId + " -- that session does not exist!");
            return null;
        }
        
        int periodNum = session.getPeriodNum();
        SubjectDef sinfo = session.getSession().getPeriod(periodNum).getSubjectInfo();
        return sinfo.getNames();
    }
    
    /** Returns the EarningsInfo array for the given session */
    public EarningsInfo[] getEarningsHistory(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot get earnings history for session " + sessionId + " -- that session does not exist!");
            return null;
        }
        
        return session.getEarningsHistory();
    }
    
    /** Returns the market closure status for the given session */
    public boolean[] getMarketStatus(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot get market status for session " + sessionId + " -- that session does not exist!");
            return null;
        }
        
        return session.getMarketClosed();
    }
    
    /** Returns the period closure status for the given session */
    public boolean isPeriodClosed(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot get period status session " + sessionId + " -- that session does not exist!");
            return false;
        }
        
        return session.isPeriodClosed();
    }
    
    /** Returns true if the current period for the given session is the last period of that session */
    public boolean isLastPeriod(int sessionId) {
        SessionState session = (SessionState) sessionStates.get(new Integer(sessionId));
        if (session == null) {
            log.error("Cannot check last period of session " + sessionId + " -- that session does not exist!");
            return false;
        }
        
        int periodNum = session.getPeriodNum();
        int numPeriods = session.getSession().getNumPeriods();
        
        if (periodNum < (numPeriods - 1))
            return false;
        else
            return true;
    }
    
    /** Calculate the payoffs for the current period in the given session. Return a PayoffUpdate
     *  object that contains an array of the payoffs calculated and a payoff mask string, which
     *  can be sent to the subjects to mask their payoff from them */
    public PayoffUpdate calculatePayoffs(int sessionId) {
        SessionState sessionState = (SessionState) sessionStates.get(new Integer(sessionId));
        if (sessionState == null) {
            log.error("Cannot calculate payoffs for session " + sessionId + " -- that session does not exist!");
            return null;
        }
        
        int periodNum = sessionState.getPeriodNum();
        SessionDef session = sessionState.getSession();
        PeriodDef period = session.getPeriod(periodNum);
        GroupDef ginfo = period.getGroupInfo();
        SubjectDef sinfo = period.getSubjectInfo();
        MarketDef minfo = period.getMarketInfo();
        EarningsInfo[] earningsHistory = sessionState.getEarningsHistory();
        Trader[][] traders = sessionState.getTraders();
        
        int numSubjects = sinfo.getNumSubjects();
        float[] payoffs = new float[numSubjects];
        float[] dividends = new float[numSubjects];
        String[] payoffMasks = new String[numSubjects];
        int[][] holdings = new int[numSubjects][minfo.getNumMarkets()];
        float[] cash = new float[numSubjects];
        
        for (int i=0; i<numSubjects; i++) {
            int subjectId_db = sinfo.getDatabaseId(i);
            int group = sinfo.getGroup(i);
                        
            for (int m=0; m<minfo.getNumMarkets(); m++) {
                holdings[i][m] = dbw.getSecurityHoldings(subjectId_db, m, minfo);
            }
            cash[i] = dbw.getCashHoldings(sessionId, periodNum, subjectId_db);
            
            PayoffFunction payoffFunction = ginfo.getPayoffFunction(group);
            
            float payout = 0f;
            String payoffMask = null;
            try {
                payout = payoffFunction.getPayoff(i, periodNum, session, traders);
                payoffMask = payoffFunction.getPayoffMask();
            }catch(Exception e) {
                log.error("Failed to calculate the payoff of the clients in session " + sessionId + " -- assigning zero payoff (check payoff function for errors)", e);
                payout = 0f;
            }
            
            if (ginfo.getAddDividend(group, 0)) {//if (minfo.isAddDividend(0)) { //all or nothing case only for now, so look at first security
                payoffs[i] = 0;
                dividends[i] = payout;
            } else {
                payoffs[i] = payout;
                dividends[i] = 0;
            }
            payoffMasks[i] = payoffMask;
            
            log.info("Payoff for player " + i + " for period " + periodNum + " is " + payoffs[i]);
            log.info("Dividends for player " + i + " for period " + periodNum + " are " + dividends[i]);
        }
        sessionState.setDividends(dividends);
        
        try {
            dbw.writePayoffs(sessionId, periodNum, payoffs, sinfo);
        }catch(Exception e) {
            log.error("Failed to write payoffs to database for session " + sessionId + " period " + periodNum, e);
        }
        
        float[] cumulativePayoffs = new float[numSubjects];
        for (int i=0; i<numSubjects; i++) {
            cumulativePayoffs[i] = dbw.getCumulativePayoff(sessionId, i, sinfo);
            
            for (int m=0; m<holdings[i].length; m++) {
                EarningsRow erow = new EarningsRow();
                erow.setPeriod(periodNum);
                erow.setSecurity(minfo.getMarketTitles()[m]);
                erow.setHoldings(holdings[i][m]);
                erow.setNumPurchases(traders[periodNum][i].getTotalPurchases(m));
                erow.setNumSales(traders[periodNum][i].getTotalSales(m));
                
                earningsHistory[i].addRow(erow);
            }
            
            EarningsRow erow = new EarningsRow();
            erow.setPeriod(periodNum);
            erow.setSecurity("Cash");
            erow.setHoldings(cash[i]);
            
            earningsHistory[i].addRow(erow);
            
            if (payoffMasks[i] == null) {
                EarningsRow cpay = new EarningsRow();
                cpay.setSecurity(null);
                cpay.setCumPayoff(cumulativePayoffs[i]);
                
                earningsHistory[i].addRow(cpay);
            }
        }
        
        //return new PayoffUpdate(payoffs, payoffMasks);    //uncomment this to send period payoff
        return new PayoffUpdate(cumulativePayoffs, payoffMasks);
    }
    
    /** Returns the payoff amount that was carried over from the period PREVIOUS to the given
     *  period. Returns 0 if the given period is the first period. The return value of this function
     *  depends on the settings for addDividend, which determines whether payoffs are carried over
     *  to the next period or instead written to the payoffs table. It also depends on the addCash
     *  variable, which determines whether leftover cash from the previous round is carried over to
     *  the next period. Indexed by subject id */
    private float[] getCarryOverCash(int sessionId, int periodNum) {
        SessionState state = (SessionState) sessionStates.get(new Integer(sessionId));
        if (state == null) {
            log.error("Cannot generate carry over cash for session " + sessionId + " -- that session does not exist!");
            return null;
        }
        
        float[] carryOver = state.getDividends();
        
        if (carryOver == null || periodNum == 0) {
            carryOver = new float[state.getNumClients()];
        }
        
        else {
            PeriodDef[] periods = state.getSession().getPeriods();
            GroupDef lastGroupInfo = periods[periodNum - 1].getGroupInfo();
            SubjectDef lastSubjectInfo = periods[periodNum - 1].getSubjectInfo();
            
            for (int i=0; i<carryOver.length; i++) {
                int subjectId_db = state.getDatabaseId(i);
                int lastGroup = lastSubjectInfo.getGroup(i);
                
                if (lastGroupInfo.getAddCash(lastGroup))
                    carryOver[i] += dbw.getCashHoldings(sessionId, periodNum - 1, subjectId_db);
            }
        }
        
        return carryOver;
    }
    
    /** Returns the security holdings carried over from the period PREVIOUS to the given period.
     *  Returns an array of zeros if the given period is the first period. The return value of this
     *  function depends on the settings for addSurplus, which determines whether securities are
     *  carried over to the next period or simply whisked away. Finally, for every security in the current
     *  period that does not match a security in the previous period (by security id in the securities
     *  database) return 0 for the carry over of that security. Indexed by subject id then security id */
    private int[][] getCarryOverSecurities(SessionState state, int periodNum) {
        PeriodDef[] periods = state.getSession().getPeriods();
        int numClients = state.getNumClients();
        
        MarketDef currentMarket = periods[periodNum].getMarketInfo();
        int numMarkets = currentMarket.getNumMarkets();
        
        int[][] carryOver = new int[numClients][numMarkets];
        if (periodNum == 0)
            return carryOver;
        
        MarketDef lastMarket = periods[periodNum - 1].getMarketInfo();
        GroupDef lastGroupInfo = periods[periodNum - 1].getGroupInfo();
        SubjectDef lastSubjectInfo = periods[periodNum - 1].getSubjectInfo();
        
        for (int i=0; i<carryOver.length; i++) {
            int subjectId_db = state.getDatabaseId(i);
            int lastGroup = lastSubjectInfo.getGroup(i);
            
            for (int m=0; m<carryOver[i].length; m++) {
                int securityId_db = currentMarket.getSecurityId(m);
                int previousMarketId = lastMarket.getMarketId(securityId_db);
                
                boolean addSurplus = false;
                if (previousMarketId != -1)
                    addSurplus = lastGroupInfo.getAddSurplus(lastGroup, previousMarketId);
                
                int previousHoldings = 0;
                if (addSurplus)
                    previousHoldings = dbw.getSecurityHoldings(subjectId_db, previousMarketId, lastMarket);
                
                carryOver[i][m] = previousHoldings;
            }
        }
        
        return carryOver;
    }
    
    /** Move the given session to the next period. Return a NewPeriodUpdate object, which encapsulates
     *  all the information about the period that will be needed by the clients */
    public NewPeriodUpdate nextPeriod(int sessionId) {
        SessionState sessionState = (SessionState) sessionStates.get(new Integer(sessionId));
        if (sessionState == null) {
            log.error("Cannot move session " + sessionId + " to next period -- that session does not exist!");
            return null;
        }
        
        int curPeriodNum = sessionState.getPeriodNum();
        
        //move to next period
        int periodNum = curPeriodNum + 1; 
        sessionState.setPeriodClosed(false);
        sessionState.resetMarketClosed(periodNum);
        sessionState.setPeriodNum(periodNum);
        
        SessionDef session = sessionState.getSession();
        PeriodDef periodInfo = session.getPeriod(periodNum);
        SubjectDef subjectInfo = periodInfo.getSubjectInfo();
        GroupDef groupInfo = periodInfo.getGroupInfo();
        MarketDef marketInfo = periodInfo.getMarketInfo();
        if(curPeriodNum>=0 && sessionState.getSession().getPeriod(curPeriodNum).isApplyTrigger()){
            adjustMarketAnchorPrices(sessionId, curPeriodNum, marketInfo); 
        }
        
        EarningsInfo[] earningsHistory = sessionState.getEarningsHistory();
        int[][] surplus = null;
        float[] dividends = null;
        
        try {
            dbw.writePeriod(sessionId, periodNum, periodInfo);
            dbw.writeSecurities(sessionId, periodNum, marketInfo);
            dbw.writeGroups(groupInfo);
            dbw.writeSubjectGroups(sessionId, periodNum, subjectInfo, groupInfo);
            
            surplus = getCarryOverSecurities(sessionState, periodNum);
            dividends = getCarryOverCash(sessionId, periodNum);
            
            dbw.writeCashInitials(sessionId, periodNum, subjectInfo, groupInfo, dividends);
            dbw.writeSecurityInitials(sessionId, periodNum, marketInfo, subjectInfo, groupInfo, surplus);
            dbw.writeSecurityRules(sessionId, periodNum, marketInfo, groupInfo);
            dbw.writeSecurityPriveleges(sessionId, periodNum, marketInfo, groupInfo);
            dbw.writeFunctions(sessionId, periodNum, groupInfo);
        }catch(SQLException e) {
            log.error("Failed to access database while writing next period information", e);
            return null;
        }
        
        int[][] initialHoldings = new int[sessionState.getNumClients()][marketInfo.getNumMarkets()];
        float[] initialCash = new float[sessionState.getNumClients()];
        
        for (int i=0; i<initialHoldings.length; i++) {
            for (int m=0; m<initialHoldings[i].length; m++) {
                initialHoldings[i][m] = groupInfo.getSecurityInitial(subjectInfo.getGroup(i), m) + surplus[i][m];
            }
            initialCash[i] = groupInfo.getCashInitial(subjectInfo.getGroup(i)) + dividends[i];
        }
        
        NewPeriodUpdate npu = new NewPeriodUpdate();
        npu.setRecipients(getSessionClients(sessionId));
        npu.setPeriodNum(periodNum);
        npu.setPeriodInfo(periodInfo);
        npu.setEarningsHistory(earningsHistory);
        npu.setInitialCash(initialCash);
        npu.setInitialHoldings(initialHoldings);
        npu.setTimeoutLength(session.getTimeoutLength());
        npu.setMarketEngine(periodInfo.getMarketEngine());
        
        dbw.writePeriodEvent(sessionId, periodNum, JMConstants.ACTION_START);
        
        return npu;
    }
    
    /** End the given session. Remove the experiment monitor and the session state object */
    public boolean terminateSession(int sessionId) {
        try {
            sessionStates.remove(new Integer(sessionId));
            dbw.writeSessionEvent(sessionId, JMConstants.ACTION_FINISH);
            log.info("Controlserv has successfully terminated session " + sessionId);
            return true;
        }catch(Exception e) {
            log.error("Server failed to terminate the session " + sessionId, e);
        }
        return false;
    }

    private void adjustMarketAnchorPrices(int sessionId, int curPeriodNum, MarketDef marketInfo) {
        for(int i=0; i<marketInfo.getNumMarkets(); i++){
            String marketName = marketInfo.getMarketTitles()[i]; 
            float avg = dbw.getAvgTransactionPrice(sessionId, curPeriodNum, marketName); 
            
            float trigger = marketInfo.getMaxPrices()[i] - 0.2f * (marketInfo.getMaxPrices()[i] - marketInfo.getMinPrices()[i]); 
            
            if(log.isDebugEnabled()){
                log.debug("Market " + marketName + " trigger check, avg: " + avg + "; trigger: " + trigger); 
            }
            
            //do anchor price adjustment if avg > trigger price
            if(avg > trigger){
                float newHigh = marketInfo.getMaxPrices()[i] + 0.2f * (marketInfo.getMaxPrices()[i] - marketInfo.getMinPrices()[i]);
                float newLow = marketInfo.getMinPrices()[i] + 0.2f * (marketInfo.getMaxPrices()[i] - marketInfo.getMinPrices()[i]); 
                marketInfo.getMaxPrices()[i] = newHigh; 
                marketInfo.getMinPrices()[i] = newLow; 
                
                marketInfo.generatePrices(); 
            }
        }
    }
    
    /** Database access writer */
    public static DBWriter dbw;
    
    /** Hashtable, keyed by session ID, that contains the SessionState objects corresponding to the ID */
    private Hashtable sessionStates;
    
    private static Log log = LogFactory.getLog(ControlServ.class);
}