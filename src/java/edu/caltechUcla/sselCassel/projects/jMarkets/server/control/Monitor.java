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
 * Monitor.java
 *
 * Created on February 9, 2005, 1:59 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.updates.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.network.*;

/**
 *
 * @author  Raj Advani
 */
public interface Monitor {
    
    /** Add a given session to the monitor serv. Once this is done the monitor serv is ready to
     *  accept ExpMonitors for that session */
    void registerSession(int sessionId);
    
    /** Terminate the given session in the monitor interface by removing all monitors associated
     *  with the given session */
    void terminateSession(int sessionId);
    
    /** Register the given MonitorTransmitter interface with the given session ID. Return true if the operation
     *  is successful. Each MonitorTransmitter communicates with a unique monitor */
    boolean registerExpMonitor(int sessionId, MonitorTransmitter ui);
    
    /** Sets the state of the given ExpMonitor to reflect the given session. The dispatcher calls
     *  this function for all ExpMonitors that begin monitoring a session after the first one. These
     *  monitors need to have the latest price chart, the latest connection status, and the latest
     *  button status (start button disabled). The metrics information will be automatically updated
     *  with the latest the next time metrics information is received for the session, and the
     *  logger will start fresh */
    void updateState(int sessionId, MonitorTransmitter ui, Vector chartSecurities, String[] names, boolean enableStart, NumOffersUpdate[] offerUpdates, MetricsUpdate[] metricsUpdates);
    
    /** Update the server monitors with a new client connection. This is called whenever a new
     *  client is authenticated */
    void updateAuthStatus(int sessionId, int id, String name, int numConnected, boolean allConnected);
    
    /** Update the status string on each of the server monitors associated with the given session ID to the
     *  given string */
    void updateExpStatus(int sessionId, String msg);
    
    /** Update the ExpMonitors to set the securities and construct the info panel. This is called
     *  as each period is initialized */
    void updatePeriodStatus(int sessionId, Vector securities);
    
    /** Update the time left on the server monitors of the given session with the given amount */
    void updateTimeLeft(int sessionId, int timeLeft);
    
    /** Update the connection status of the given client in the given session. This takes client ID
     *  number, not database ID number */
    void updateConnectionStatus(int sessionId, int subjectId, boolean connected);
    
    /** Set the start button, end button, and status message to the session running or not running state for 
     *  each monitor associated with the given session id */
    void setSessionRunning(int sessionId, boolean running);
    
    /** Process a basket of metrics, price chart, numoffers, and backlog updates for the monitors.
     *  This operates simply by adding the offers contained in the UpdateBasket to the update queues
     *  contained here, which are continually dequeued through the updater threads */
    void processTransactionUpdates(UpdateBasket basket);
    
    /** Log the given message onto the ExpMonitors of all sessions */
    public void logMessage(String logStr, int level, String className);
    
    /** Get the protocol being used to communicate with monitors */
    public int getMonitorProtocol();
    
    /** Set the protocol */
    public void setMonitorProtocol(int monitorProtocol);
    
    /** Get the port being used for monitor communication */
    public int getMonitorPort();
    
    /** Set the port */
    public void setMonitorPort(int monitorPort);
    
    /** clean up anything that neads cleaning up */
    public void done();
    
    public static int TCP_MONITOR_PROTOCOL = 0;
    public static int RMI_MONITOR_PROTOCOL = 1;
}
