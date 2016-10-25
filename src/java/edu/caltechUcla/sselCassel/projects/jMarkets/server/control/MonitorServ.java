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
 * MonitorServ.java
 *
 * Created on February 3, 2005, 4:03 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.updates.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.network.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  Raj Advani
 */
public class MonitorServ implements Monitor {
    
    /** Creates a new instance of MonitorServ */
    public MonitorServ(Hashtable sessionMonitors, Properties props) {
        this.sessionMonitors = sessionMonitors;
        readProperties(props);
        
        log.info("MonitorServ starting auxiliary update threads...");
        startMetricsUpdateThread();
        startPriceChartUpdateThread();
        startNumOffersUpdateThread();
        startOfferBacklogThread();
    }
    
    public void done(){
        log.error("MonitorServ.done() called...");
        metricsUpdateDone = true;
        priceChartDone = true;
        numOffersDone = true;
        offerBacklogDone = true;
    }
    
    /** Read the properties required for the MonitorServ. These are the monitorPort and
     *  the monitorProtocol, which are used to determine the style of network communication
     *  with the monitors */
    private void readProperties(Properties props) {
        String monitorProtocolStr = props.getProperty("monitorProtocol", "tcp");
        if (monitorProtocolStr.equalsIgnoreCase("rmi")) {
            monitorProtocol = RMI_MONITOR_PROTOCOL;
            log.info("Using the RMI protocol for server-monitor communication");
        }
        else {
            monitorProtocol = TCP_MONITOR_PROTOCOL;
            log.info("Using the TCP protocol for server-monitor communication");
        }
        
        String monitorPortStr = props.getProperty("monitorPort", "5000");
        try {
            monitorPort = Integer.parseInt(monitorPortStr);
            log.info("Using port " + monitorPort + " for server-monitor communication");
        }catch(Exception e) {
            log.warn("Invalid monitor port property read: " + monitorPortStr + "; using default port 5000");
            monitorPort = 5000;
        }
    }
    
    /** Add a given session to the monitor serv. Once this is done the monitor serv is ready to
     *  accept MonitorTransmitters for that session */
    public void registerSession(int sessionId) {
        sessionMonitors.put(new Integer(sessionId), new Vector());
    }
    
    /** Terminate the given session in the monitor interface by removing all monitors associated
     *  with the given session. Also disable the stop button in all the montiors of that session */
    public void terminateSession(int sessionId) {
        Vector monitors = getMonitors(sessionId);
        if (monitors == null) {
            log.warn("Cannot update MonitorTransmitters with session terminated status for session " + sessionId + " -- that session does not exist!");
            return;
        }
        
        for (int i=0; i<monitors.size(); i++) {
            MonitorTransmitter ui = (MonitorTransmitter) monitors.get(i);
            try {
                ui.setStopExpButtonEnabled(false);
            }catch(MonitorDisconnectedException e) {
                log.error("Failed to establish connection with MonitorTransmitter -- disconnecting from failed monitor");
                disconnectMonitor(sessionId, ui);
            }
        }
        
        sessionMonitors.remove(new Integer(sessionId));
    }
    
    /** Register the given MonitorTransmitter interface with the given session ID. Return true if the operation
     *  is successful. Each MonitorTransmitter communicates with a unique monitor */
    public boolean registerExpMonitor(int sessionId, MonitorTransmitter ui) {
        Vector monitors = (Vector) sessionMonitors.get(new Integer(sessionId));
        if (monitors == null) {
            log.warn("Cannot register a monitor with session " + sessionId + " -- that session has not been initialized");
            return false;
        }
        
        monitors.add(ui);
        
        log.info("Experiment monitor has been registered for session " + sessionId);
        return true;
    }
    
    /** Disconnect the given monitor from the given session. This is called whenever a MonitorDisconnectedException
     *  is thrown, indicating the the server has lost connection with the given monitor */
    private boolean disconnectMonitor(int sessionId, MonitorTransmitter ui) {
        Vector monitors = (Vector) sessionMonitors.get(new Integer(sessionId));
        if (monitors == null) {
            log.warn("Cannot disconnect a monitor in session " + sessionId + " -- that session has not been initialized");
            return false;
        }
        
        try {
            ui.close();
        }catch(MonitorDisconnectedException e) {
            
        }
        
        return monitors.remove(ui);
    }
    
    /** Sets the state of the given ExpMonitor to reflect the given session. The dispatcher calls
     *  this function for all ExpMonitors that begin monitoring a session after the first one. These
     *  monitors need to have the latest price chart, the latest connection status, and the latest
     *  button status (start button). Also update metrics information */
    public void updateState(int sessionId, MonitorTransmitter ui, Vector chartSecurities, String[] names, boolean enableStart, NumOffersUpdate[] offerUpdates, MetricsUpdate[] metricsUpdates) {
        try {
            if (enableStart)
                ui.setStartExpButtonEnabled(true);
            else {
                ui.setStartExpButtonEnabled(false);
                ui.setStopExpButtonEnabled(true);
            }
            
            if (chartSecurities != null) {
                ui.insertPriceChart(chartSecurities);
                ui.constructInfoPeriodPanel();
            }
            
            if (names != null) {
                for (int i=0; i<names.length; i++) {
                    if (names[i] != null) 
                        ui.connectClient(i, names[i]);
                    else {
                        ui.connectClient(i, "<html><font color=#003366>Client " + i + "</font></html>");
                        ui.setConnected(i, false);
                    }
                }
            }
            
            if (offerUpdates != null) {
                for (int i=0; i<offerUpdates.length; i++) {
                    ui.updateNumOffers(offerUpdates[i].client, offerUpdates[i].numOffers);
                }
            }
            
            if (metricsUpdates != null) {
                for (int i=0; i<metricsUpdates.length; i++) {
                    ui.updateMetrics(metricsUpdates[i].iterations, metricsUpdates[i].numTrans, metricsUpdates[i].average);
                }
            }
            
        }catch(MonitorDisconnectedException e) {
            log.error("Failed to establish connection with MonitorTransmitter -- disconnecting from failed monitor");
            disconnectMonitor(sessionId, ui);
        }
    }
    
    /** Update the server monitors with a new client connection. This is called whenever a new
     *  client is authenticated */
    public void updateAuthStatus(int sessionId, int id, String name, int numConnected, boolean allConnected) {
        Vector monitors = getMonitors(sessionId);
        if (monitors == null) {
            log.warn("Cannot update MonitorTransmitters with new client connection status for session " + sessionId + " -- that session does not exist!");
            return;
        }
        
        for (int i=0; i<monitors.size(); i++) {
            MonitorTransmitter ui = (MonitorTransmitter) monitors.get(i);
            try {
                ui.connectClient(id, name);
                
                if (numConnected == 1)
                    ui.setStartExpButtonEnabled(true);
                if (allConnected)
                    ui.setAllConnected(true);
            }catch(MonitorDisconnectedException e) {
                log.error("Failed to establish connection with MonitorTransmitter -- disconnecting from failed monitor");
                disconnectMonitor(sessionId, ui);
            }
        }
        
    }
    
    /** Update the status string on each of the server monitors associated with the given session ID to the
     *  given string */
    public void updateExpStatus(int sessionId, String msg) {
        Vector monitors = getMonitors(sessionId);
        if (monitors == null) {
            log.warn("Cannot update MonitorTransmitters with new experiment status for session " + sessionId + " -- that session does not exist!");
            return;
        }
        
        for (int i=0; i<monitors.size(); i++) {
            MonitorTransmitter ui = (MonitorTransmitter) monitors.get(i);
            try {
                ui.updateExpStatus(msg);
            }catch(MonitorDisconnectedException e) {
                log.error("Failed to establish connection with MonitorTransmitter -- disconnecting from failed monitor");
                disconnectMonitor(sessionId, ui);
            }
        }
    }
    
    /** Update the ExpMonitors to set the securities and construct the info panel. This is called
     *  as each period is initialized */
    public void updatePeriodStatus(int sessionId, Vector securities) {
        Vector monitors = getMonitors(sessionId);
        if (monitors == null) {
            log.warn("Cannot update ExpMonitor with new period status for session " + sessionId + " -- that session does not exist!");
            return;
        }
        
        for (int i=0; i<monitors.size(); i++) {
            MonitorTransmitter ui = (MonitorTransmitter) monitors.get(i);
            try {
                ui.insertPriceChart(securities);
                ui.constructInfoPeriodPanel();
            }catch(MonitorDisconnectedException e) {
                log.error("Failed to establish connection with MonitorTransmitter -- disconnecting from failed monitor");
                disconnectMonitor(sessionId, ui);
            }
        }
        
    }
    
    /** Update the time left on the server monitors of the given session with the given amount */
    public void updateTimeLeft(int sessionId, int timeLeft) {
        Vector monitors = getMonitors(sessionId);
        if (monitors == null) {
            log.warn("Cannot update MonitorTransmitters with new time left status for session " + sessionId + " -- that session does not exist!");
            return;
        }
        
        for (int i=0; i<monitors.size(); i++) {
            MonitorTransmitter ui = (MonitorTransmitter) monitors.get(i);
            try {
                ui.setTimeLeft(timeLeft);
            }catch(MonitorDisconnectedException e) {
                log.error("Failed to establish connection with MonitorTransmitter -- disconnecting from failed monitor");
                disconnectMonitor(sessionId, ui);
            }
        }
    }
    
    /** Update the connection status of the given client in the given session. This takes client ID
     *  number, not database ID number */
    public void updateConnectionStatus(int sessionId, int subjectId, boolean connected) {
        Vector monitors = getMonitors(sessionId);
        if (monitors == null) {
            log.warn("Cannot update MonitorTransmitters with new connection status for session " + sessionId + " -- that session does not exist!");
            return;
        }
        
        for (int i=0; i<monitors.size(); i++) {
            MonitorTransmitter ui = (MonitorTransmitter) monitors.get(i);
            try {
                ui.setConnected(subjectId, connected);
            }catch(MonitorDisconnectedException e) {
                log.error("Failed to establish connection with MonitorTransmitter -- disconnecting from failed monitor");
                disconnectMonitor(sessionId, ui);
            }
        }
    }
    
    /** Set the start button, end button, and status message to the session running or not running state for 
     *  each monitor associated with the given session id */
    public void setSessionRunning(int sessionId, boolean running) {
        Vector monitors = getMonitors(sessionId);
        if (monitors == null) {
            log.warn("Cannot update MonitorTransmitters with start button status for session " + sessionId + " -- that session does not exist!");
            return;
        }
        
        for (int i=0; i<monitors.size(); i++) {
            MonitorTransmitter ui = (MonitorTransmitter) monitors.get(i);
            try {
                ui.setStartExpButtonEnabled(!running);
                ui.setStopExpButtonEnabled(running);
            }catch(MonitorDisconnectedException e) {
                log.error("Failed to establish connection with MonitorTransmitter -- disconnecting from failed monitor");
                disconnectMonitor(sessionId, ui);
            }
        }
    }
    
    /** Process a basket of metrics, price chart, numoffers, and backlog updates for the monitors.
     *  This operates simply by adding the offers contained in the UpdateBasket to the update queues
     *  contained here, which are continually dequeued through the updater threads */
    public void processTransactionUpdates(UpdateBasket basket) {
        priceChartUpdates.addAll(basket.getPriceChartUpdates());
        metricsUpdates.addAll(basket.getMetricsUpdates());
        numOffersUpdates.addAll(basket.getNumOffersUpdates());
        offerBacklogUpdates.addAll(basket.getOfferBacklogUpdates());
    }
    
    /** Start a thread that continually checks for new metrics updates and sends them to
     *  the server admin screen. This thread is needed so that calls to the UI, which are
     *  slow remote calls, do not prolong transaction timing so that transactions can
     *  release market locks ASAP. The dispatcher can add updates to the metricsUpdates
     *  queue */    
    
    private void startMetricsUpdateThread() {
        metricsUpdates = new Vector();
        
        Runnable updater = new Runnable() {
            public void run() {
                try {
                    while (!metricsUpdateDone) {
                        MetricsUpdate update = getNextUpdate();
                        if (update.sessionId == -1) {
                            log.debug("Shutting down auxiliary metrics update thread");
                            break;
                        }
                        Vector monitors = getMonitors(update.sessionId);
                        if (monitors == null) {
                            log.warn("Cannot update MonitorTransmitters with metrics information for session " + update.sessionId + " -- that session does not exist!");
                            continue;
                        }
                        
                        for (int i=0; i<monitors.size(); i++) {
                            MonitorTransmitter ui = (MonitorTransmitter) monitors.get(i);
                            try {
                                ui.updateMetrics(update.iterations, update.numTrans, update.average);
                            }catch(MonitorDisconnectedException e) {
                                log.error("Failed to establish connection with MonitorTransmitter -- disconnecting from failed monitor");
                                disconnectMonitor(update.sessionId, ui);
                            }
                        }
                        
                    }
                }catch(Exception e) {
                    log.error("MonitorServ failed to update admin screen with metrics information", e);
                }
            }
            
            private synchronized MetricsUpdate getNextUpdate() throws InterruptedException {
                try {
                     if( metricsUpdateDone ){
                        log.info("metricsUpdate ending... returning from getNextUpdate()...");
                        return new MetricsUpdate(0,0,0,0.f);
                    }
                    if (!metricsUpdates.isEmpty() && metricsUpdates.size() > 0)
                        return (MetricsUpdate) metricsUpdates.remove(0);
                    else
                        wait(1000);
                    return getNextUpdate();
                }catch(Exception e) {
                    log.debug("Metrics updates lost synchronization -- resynchronizing");
                    return getNextUpdate();
                }
            }
        };
        
        Thread updateThr = new Thread(updater);
        updateThr.setDaemon(true);
        updateThr.start();
    }
    
    /** Works like MetricsUpdates, except for Price Chart updates */
    private void startPriceChartUpdateThread() {
        priceChartUpdates = new Vector();
        
        Runnable updater = new Runnable() {
            public void run() {
                try {
                    while (!priceChartDone) {
                        PriceChartUpdate pupdate = getNextUpdate();
                        if (pupdate.sessionId == -1) {
                            log.debug("Shutting down auxiliary price chart update thread");
                            break;
                        }
                        Vector monitors = getMonitors(pupdate.sessionId);
                        if (monitors == null) {
                            log.warn("Cannot update MonitorTransmitters with price chart information for session " + pupdate.sessionId + " -- that session does not exist!");
                            continue;
                        }
                        
                        for (int i=0; i<monitors.size(); i++) {
                            MonitorTransmitter ui = (MonitorTransmitter) monitors.get(i);
                            try {
                                ui.updatePriceChart(pupdate.security, pupdate.time, pupdate.price);
                            }catch(MonitorDisconnectedException e) {
                                log.error("Failed to establish connection with MonitorTransmitter -- disconnecting from failed monitor");
                                disconnectMonitor(pupdate.sessionId, ui);
                            }
                        }
                    }
                }catch(Exception e) {
                    log.error("MonitorServ failed to update admin screen with price chart information", e);
                }
            }
            
            private synchronized PriceChartUpdate getNextUpdate() throws InterruptedException {
                try {
                     if( priceChartDone ){
                        log.info("priceChartUpdate ending... returning from getNextUpdate()...");
                        return new PriceChartUpdate(0,"0",0.f,0.f);
                    }
                    if (!priceChartUpdates.isEmpty() && priceChartUpdates.size() > 0)
                        return (PriceChartUpdate) priceChartUpdates.remove(0);
                    else
                        wait(1000);
                    return getNextUpdate();
                }catch(Exception e) {
                    log.debug("Price chart updates lost synchronization -- resynchronizing");
                    return getNextUpdate();
                }
            }
        };
        
        Thread updateThr = new Thread(updater);
        updateThr.setDaemon(true);
        updateThr.start();
    }
    
    /** Works like MetricsUpdates, except for num offers updates */
    private void startNumOffersUpdateThread() {
        numOffersUpdates = new Vector();
        
        Runnable updater = new Runnable() {
            public void run() {
                try {
                    while (!numOffersDone) {
                        NumOffersUpdate update = getNextUpdate();
                        if (update.sessionId == -1) {
                            log.debug("Shutting down auxiliary num offers update thread");
                            break;
                        }
                        
                        Vector monitors = getMonitors(update.sessionId);
                        if (monitors == null) {
                            log.warn("Cannot update MonitorTransmitters with num offers information for session " + update.sessionId + " -- that session does not exist!");
                            continue;
                        }
                        
                        for (int i=0; i<monitors.size(); i++) {
                            MonitorTransmitter ui = (MonitorTransmitter) monitors.get(i);
                            try {
                                ui.updateNumOffers(update.client, update.numOffers);
                            }catch(MonitorDisconnectedException e) {
                                log.error("Failed to establish connection with MonitorTransmitter -- disconnecting from failed monitor");
                                disconnectMonitor(update.sessionId, ui);
                            }
                        }
                    }
                }catch(Exception e) {
                    log.error("MonitorServ failed to update admin screen with num offers information", e);
                }
            }
            
            private synchronized NumOffersUpdate getNextUpdate() throws InterruptedException {
                try {
                     if( numOffersDone ){
                        log.info("numOffersUpdate ending... returning from getNextUpdate()...");
                        return new NumOffersUpdate(0,0,0);
                    }
                    if (!numOffersUpdates.isEmpty() && numOffersUpdates.size() > 0)
                        return (NumOffersUpdate) numOffersUpdates.remove(0);
                    else
                        wait(1000);
                    return getNextUpdate();
                }catch(Exception e) {
                    log.debug("Num offers updates lost synchronization -- resynchronizing");
                    return getNextUpdate();
                }
            }
        };
        
        Thread updateThr = new Thread(updater);
        updateThr.setDaemon(true);
        updateThr.start();
    }
    
    /** Works like MetricsUpdates, except for offer backlog updates. These updates tell the server administrator
     *  how many offers are waiting to be processed. When this number reaches some critical amount, the server
     *  should temporarily stop accepting offers */
    private void startOfferBacklogThread() {
        offerBacklogUpdates = new Vector();
        
        Runnable updater = new Runnable() {
            public void run() {
                try {
                    while (!offerBacklogDone) {
                        OfferBacklogUpdate update = getNextUpdate();
                        if (update.backlog == -1) {
                            log.debug("Shutting down auxiliary offer backlog update thread");
                            break;
                        }
                        
                        Enumeration sessionIds = sessionMonitors.keys();
                        while (sessionIds.hasMoreElements()) {
                            Integer key = (Integer) sessionIds.nextElement();
                            Vector monitors = (Vector) sessionMonitors.get(key);
                            
                            for (int i=0; i<monitors.size(); i++) {
                                MonitorTransmitter ui = (MonitorTransmitter) monitors.get(i);
                                try {
                                    ui.setOfferBacklog(update.backlog, update.rejecting);
                                }catch(MonitorDisconnectedException e) {
                                    log.error("Failed to establish connection with MonitorTransmitter -- disconnecting from failed monitor");
                                    disconnectMonitor(key.intValue(), ui);
                                }
                            }
                        }
                    }
                }catch(Exception e) {
                    log.error("MonitorServ failed to update admin screen with offer backlog information", e);
                }
            }
            
            private synchronized OfferBacklogUpdate getNextUpdate() throws InterruptedException {
                try {
                    if( offerBacklogDone ){
                        log.info("offerBacklogUpdate ending... returning from getNextUpdate()...");
                        return new OfferBacklogUpdate(0,false);
                    }
                    if (!offerBacklogUpdates.isEmpty() && offerBacklogUpdates.size() > 0)
                        return (OfferBacklogUpdate) offerBacklogUpdates.remove(0);
                    else
                        wait(1000);
                    return getNextUpdate();
                }catch(Exception e) {
                    log.debug("Offer backlog updates lost synchronization -- resynchronizing");
                    return getNextUpdate();
                }
            }
        };
        
        Thread updateThr = new Thread(updater);
        updateThr.setDaemon(true);
        updateThr.start();
    }
    
    /** Log the given message onto the MonitorTransmitters of all sessions */
    public void logMessage(String logStr, int level, String className) {
        Enumeration sessionIds = sessionMonitors.keys();
        
        while (sessionIds.hasMoreElements()) {
            Integer key = (Integer) sessionIds.nextElement();
            Vector monitors = (Vector) sessionMonitors.get(key);
            
            for (int i=0; i<monitors.size(); i++) {
                MonitorTransmitter ui = (MonitorTransmitter) monitors.get(i);
                try {
                    ui.addLogMessage(logStr, level, className);
                }catch(MonitorDisconnectedException e) {
                    disconnectMonitor(key.intValue(), ui);
                    log.error("Failed to establish connection with MonitorTransmitter -- disconnecting from failed monitor");
                }
            }
        }
    }
    
    /** Return a Vector full of the monitors for the given session. Return null if the session
     *  does not exist */
    private Vector getMonitors(int sessionId) {
        Vector monitors = (Vector) sessionMonitors.get(new Integer(sessionId));
        return monitors;
    }
    
    /** Getter for property monitorProtocol.
     * @return Value of property monitorProtocol.
     *
     */
    public int getMonitorProtocol() {
        return monitorProtocol;
    }
    
    /** Setter for property monitorProtocol.
     * @param monitorProtocol New value of property monitorProtocol.
     *
     */
    public void setMonitorProtocol(int monitorProtocol) {
        this.monitorProtocol = monitorProtocol;
    }
    
    /** Getter for property monitorPort.
     * @return Value of property monitorPort.
     *
     */
    public int getMonitorPort() {
        return monitorPort;
    }
    
    /** Setter for property monitorPort.
     * @param monitorPort New value of property monitorPort.
     *
     */
    public void setMonitorPort(int monitorPort) {
        this.monitorPort = monitorPort;
    }
    
    /** Hashtable, keyed by session ID, that contains Vectors, each of which contain the MonitorTransmitter
     *  interfaces active for that session ID */
    private Hashtable sessionMonitors;
    
    /** A queue of metrics updates to be sent to the admin GUI. The main transaction threads
     *  store metrics updates here, instead of calling the admin GUI itself, in order to
     *  speed up the transaction. The metrics update thread dumps these onto the admin GUI */
    private Vector metricsUpdates;
    
    /**  Similar in function to metricsUpdates, this contains price chart updates */
    private Vector priceChartUpdates;
    
    /** Similar in function to metricsUpdates, contains numOffers updates */
    private Vector numOffersUpdates;
    
    /** Similar in function to metricsUpdates, contains offerBacklog updates */
    private Vector offerBacklogUpdates;
    
    /** The port used to communicate with the monitors. When in RMI protocol mode, the port range
     *  is this port + 20. When in TCP mode, this is the port on which the ServerSocket is created */
    private int monitorPort;
    
    /** The protocol used to communicate with the monitors. Either TCP or RMI */
    private int monitorProtocol;
    
    private volatile boolean metricsUpdateDone=false, priceChartDone=false,  numOffersDone=false, offerBacklogDone=false;
    
    private static Log log = LogFactory.getLog(MonitorServ.class);
}
