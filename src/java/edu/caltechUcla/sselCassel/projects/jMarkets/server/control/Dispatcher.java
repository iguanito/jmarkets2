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
 * Dispatcher.java
 *
 * Created on February 9, 2005, 1:59 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import edu.caltechUcla.sselCassel.projects.jMarkets.server.interfaces.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.updates.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;

/**
 *
 * @author  Raj Advani
 */
public interface Dispatcher {
    
    /** Called when the server receives a SERVER_INIT_REQUEST from an experimenter. Dispatches this
     *  message to the ControlServ, which will create a new session and return the ID of the session
     *  created. Return a SERVER_INIT_RESPONSE, which includes the session ID number */
    Response processInitRequest(Request req);
    
    /** This function is either called by the RMI GUI Tunnel registration interface or by the
     *  TCP registration thread (see activateMonitorTunnel comments). This function registers a new 
     *  monitor with the system by way of its transmitter (each admin monitor is has a receiver
     *  on the admin 'client' end and a transmitter on this server end). The transmitters are used
     *  by the MonitorServ to communicate with the actual Monitors. 
     * 
     *  The monitor transmitter is added to the session it belongs to. One ExpMonitor is required
     *  to be linked to the session before clients can start connecting. Once the session is started
     *  by pressing the Start button on this initial monitor, it is safe to shut down the monitor.
     *  At any time new monitors can be created to view a session -- these monitors are brought
     *  up to speed with the latest client connection status, button status, metrics, and price chart
     *  of the given session */
    void registerMonitorTransmitter(MonitorTransmitter ui, int sessionId);
    
    /** Whenever a client or admin monitor logs in, they send a session query request to
     *  find out what sessions are currently active. This returns an array of SessionIdentifier
     *  objects so that they can display this information and choose what session to
     *  join */
    Response processSessionQueryRequest(Request req);
    
    /** Called when the server receives a CLIENT_AUTH_REQUEST from a client. Initializes new clients
     *  by sending them their client ID numbers. Re-authenticates disconnected clients
     *  by sending all previous state information. The dispatcher collects authentication information
     *  from the client request, sends it to the ControlServ for processing, then sends out the
     *  results of the authentication process back to the clients. This method also handles
     *  re-authentication, for disconnected clients who try to reconnect to currently running
     *  sessions. In order to process re-authentication, the dispatcher collects information on
     *  client connection status from the UpdateServ and passes this to the ControlServ */
    Response processAuthRequest(Request req);
    
    /** Start the game in response to the administrator pressing the start button on the ExpMonitor */
    Response processStartRequest(Request req);
    
    /** Start the game in response to the administrator pressing the start period button on the ExpMonitor */
    Response processStartPeriodRequest(Request req);
    /** Stop the game in response to the administrator pressing the stop period button on the ExpMonitor */
    Response processStopPeriodRequest(Request req);
    /** find out if the session has manual control over period advancement */
    Response processIsManualControlRequest(Request req);
    /** change whether the session has manual control over round advancement */
    Response processSetManualControlRequest(Request req);
    
    /** JMTimer calls this method every second whenever a session's timer display should be updated. Note
     *  this is NOT called when the JMTimer wants to update (re-sync) client timers -- for that, the method
     *  processTimeEvent is called. This method ONLY updates timers on the monitors */
    boolean setTimeLeft(int sessionId, int openDelay, int periodLength, int[] marketLength);
    
    /** JMTimer calls this method every time it detects the need for a time synchronization between
     *  the server and clients. It passes in the three timers for the period. The dispatcher routes
     *  this request to the UpdateServ to inform the clients, and the ControlServ to update the session
     *  data structures. Returns the response sent out by the UpdateServ */
    Response processTimeEvent(int sessionId, int periodNum, int openDelay, int periodLength, int[] marketLength);
    
    /** JMTimer calls this method every time a market closes in the given session. The dispatcher
     *  routes this request to the UpdateServ to inform the clients, and the ControlServ to update
     *  the session data structures. Returns the response sent out by the UpdateServ */
    Response processMarketClosure(int sessionId, int periodNum, int market);
    
    /** JMTimer calls this method every time a period closes in the given session. The dispatcher
     *  routes this request to the UpdateServ to inform the clients, and the ControlServ to update
     *  the session data structures. Returns the response sent out by the UpdateServ */
    Response processPeriodClosure(int sessionId, int periodNum);
    
    /** JMTimer calls this method every time a period opens. This occurs when that period's market
     *  opening delay has finished. The dispatcher routes this request to the TradeServ */
    boolean processPeriodOpening(int sessionId, int periodNum);
    
    /** Called whenever a client attempts to make a trade */
    Response processTransactionRequest(Request req);
    
    /** Respond to a TERMINATE_SESSION_REQUEST by ending the current session and sending back
     *  a general acknowledgment */
    Response processTerminateRequest(Request req);
    
    /** Return an update in response to a client's request */
    Response processUpdateRequest(Request req);
    
    /** Called when a request of invalid type is received */
    Response processUnknownRequest(Request req);
    
    /** releases all resourses held by the dispatcher */
    void destroy();
}
