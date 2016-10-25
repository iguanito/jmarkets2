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
 * Controller.java
 *
 * Created on February 9, 2005, 1:59 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import edu.caltechUcla.sselCassel.projects.jMarkets.server.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.updates.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable.EarningsInfo;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SessionDef;

/**
 *
 * @author  Raj Advani, Walter M. Yuan
 */
public interface Controller {
    
    /** Get the Database writer used by the ControlServ. This is needed so that the
     *  dispatcher can pass this writer to the TradeServ */
    DBWriter getDBWriter();
    
    /** Start a new session. Create a new SessionState object and write the session to the database,
     *  returning the session ID. Store the session in the sessions hashtable so it can be accessed
     *  by its ID when needed */
    int startNewSession(String name, int numClients, int updateTime, SessionDef sessionInfo);
    
    /** Get the array of database Id numbers associated with the given session. These numbers
     *  are invalid unless the game is running. Therefore return null if the session is still
     *  initializing (first period has not yet begun) */
    int[] getSessionClients(int sessionId);
    
    /** Return an array of SessionIdentifier objects that contain the ID numbers, names, and status
     *  of all active sessions. Synchronized so that new sessions are not created while
     *  retrieving identifiers */
    SessionIdentifier[] getSessionIdentifiers();
    
    /** Return an array of active session ID numbers */
    int[] getSessionIds();
    
    /** Register the given ExpMonitor (MonitorTransmitter interface) with the given session ID.
     *  Return true if this is the first ExpMonitor link established for this session. Otherwise
     *  return false. The state will be set to ACCEPTING_CLIENTS_STATE once at least one ExpMonitor
     *  has been registered. For ExpMonitors that register after the initial one, the dispatcher will
     *  instruct the MonitorServ to send them the price chart, client connection status, and button status
     *  information */
    boolean registerExpMonitor(int sessionId, MonitorTransmitter ui);
    
    /** Authenticate the given client into the given session. First check to see if the session
     *  is in a state where it can accept new clients. If it is, add the client to the session
     *  and return an AuthUpdate to the DispatchServ so that it can send out a confirmation to
     *  the client. Otherwise return an AuthUpdate with an error message.  The disconnected Hashtable
     *  is sent by the DispatchServ and tell us what clients are currently disconnected from the
     *  the server. This is needed so that we do not reauthenticate a client who is already connected */
    AuthUpdate authClient(int sessionId, String name, int dbId, Hashtable disconnected);
    
    /** Start the session that corresponds to the given session ID */
    boolean startSession(int sessionId);
    
    /** Checks to see if the session ID in the given request is valid. Returns
     *  true if the ID is valid. Returns false otherwise */
    boolean isSessionValid(int sessionId);
    
    /** Return true if the given session is currently running -- that is, if it not only valid but
     *  has also started */
    boolean isSessionRunning(int sessionId);
    
    /** Updates the times of the given session. Called from the dispatcher when it
     *  receives an update from the JMTimer */
    boolean updateTimers(int sessionId, int openDelay, int periodLength, int[] marketLength);
    
    /** Updates the closure status of the given market in the given session. Called from the
     *  dispatcher when it receives a market closure update from the JMTimer */
    boolean closeMarket(int sessionId, int market);
    
    /** Updates the closure status of the current period in the given session. Called from the
     *  dispatcher when it receieves a period closure update from the JMTimer. Return true
     *  if this was the last period */
    boolean closePeriod(int sessionId);
    
    /** Returns the current period number of the given session */
    int getPeriodNum(int sessionId);
    
    /** Returns the amount of period time remaining in the given session */
    int getPeriodTime(int sessionId);
    
    /** Returns the amount of market opening time remaining in the given session */
    int getOpeningTime(int sessionId);
    
    /** Returns the amount of market time remaining in the given session for each market */
    int[] getMarketTime(int sessionId);
    
    boolean getManualControl(int sessionId);
    void setManualControl(int sessionId, boolean manual);
    
    /** Returns an array of the names of the clients participating in the given session. These
     *  are indexed by the system id number (not the database id number) */
    String[] getSubjectNames(int sessionId);
    
    /** Returns the EarningsInfo array for the given session */
    EarningsInfo[] getEarningsHistory(int sessionId);
    
    /** Returns the market closure status for the given session */
    boolean[] getMarketStatus(int sessionId);
    
    /** Returns the period closure status for the given session */
    boolean isPeriodClosed(int sessionId);
    
    /** Returns true if the current period for the given session is the last period of that session */
    boolean isLastPeriod(int sessionId);
    
    /** Calculate the payoffs for the current period in the given session. Return a PayoffUpdate 
     *  object that contains an array of the payoffs calculated and a payoff mask string, which 
     *  can be sent to the subjects to mask their payoff from them */
    public PayoffUpdate calculatePayoffs(int sessionId);
    
    /** Move the given session to the next period. Return a NewPeriodUpdate object, which encapsulates
     *  all the information about the period that will be needed by the clients */
    NewPeriodUpdate nextPeriod(int sessionId);
    
    /** End the given session. Remove the experiment monitor and the session state object */
    boolean terminateSession(int sessionId);
    
    /** Set the Trader objects for the given period. These objects hold all the transaction information
     *  of the clients */
    void setTraders(int sessionId, int period, Trader[] traders);
}
