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
 * DispatchServ.java
 *
 * Created on February 3, 2005, 11:24 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import java.util.*;
import org.apache.log4j.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.updates.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable.EarningsInfo;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.PeriodDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SessionDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author  Raj Advani, Walter M. Yuan
 */
public class DispatchServ implements Dispatcher {
    
    /** Creates a new instance of DispatchServ */
    public DispatchServ(Properties props, String receiverPath) {
        this.receiverPath = receiverPath;
        
        controlServ = new ControlServ(props);
        updateServ = new HTTPUpdateServ();
        
        continuousMarketEngine =  new ContinuousMarketEngine(controlServ.getDBWriter());
        callMarketEngine = new CallMarketEngine(controlServ.getDBWriter());
        activeEngines = new Hashtable<Integer, TradeEngine>();
        
        Hashtable sessionMonitors = new Hashtable();
        monitorServ = new MonitorServ(sessionMonitors, props);
        activateMonitorTunnel(monitorServ.getMonitorProtocol(), monitorServ.getMonitorPort());
        
        appender = new guiAppender(monitorServ);
        Category root = Category.getRoot();
        root.addAppender(appender);
        
        jTimer = new JMTimer(this);
        
        cmon = new ConnectionMonitor(controlServ, updateServ, monitorServ);
        cmon.start();
    }
    
    public void destroy(){
        log.debug("DispatchServ.destroy() called...");
        // tell the connection monitor we're ending, close its threads'
        cmon.stopMonitor();
        // tell monitorServ to end
        monitorServ.done();
        // end the registration thread in this class
        stopRegThread=true;
        // tell the timers to cancel themselves
        jTimer.cancel();
        
        //close the two sockets in this class
        try{
            servSock.close();
            monitorSocket.close();            
        } catch( IOException ioe ){
            log.warn("IOException in DispatchServ.destroy()");
            System.out.println("IOException in DispatchServ.destroy():");
            ioe.printStackTrace();
        }
    }
    
    /** Called when the server receives a SERVER_INIT_REQUEST from an experimenter. Dispatches this
     *  message to the ControlServ, which will create a new session and return the ID of the session
     *  created. Return a SERVER_INIT_RESPONSE, which includes the session ID number */
    public Response processInitRequest(Request req) {
        try {
            log.info("Dispatcher has received a SERVER_INIT_REQUEST -- initializing session...");
            
            int numClients = req.getIntInfo("numClients");
            String sessionName = req.getStringInfo("name");
            int updateTime = getUpdateTime(req);
            
            SessionDef session = (SessionDef) req.getInfo("session");
            
            int sessionId = controlServ.startNewSession(sessionName, numClients, updateTime, session);
            monitorServ.registerSession(sessionId);
            
            Response res = new Response(Response.SERVER_INIT_RESPONSE);
            res.addIntInfo("sessionId", sessionId);
            
            log.info("Processed all session initialization parameters for session " + sessionId + ". Waiting for admin connection...");
            
            return res;
            
        }catch(Exception e) {
            log.error("Error processing the session initialization request", e);
        }
        return new Response(Response.INVALID_SESSION_RESPONSE);
    }
    
    /** Return the update time that will be used in a new session. Extract this information
     *  from the given SERVER_INIT_REQUEST */
    private int getUpdateTime(Request req) {
        if (req.getType() != Request.SERVER_INIT_REQUEST) {
            log.warn("Attempted to read update protocol information from an invalid request");
            return OPERATION_FAILED;
        }
        
        int updateTime = 0;
        int updateProtocol = req.getIntInfo("updateProtocol");
        if (updateProtocol == JMConstants.HTTP_UPDATE_PROTOCOL) {
            log.info("Session will be using HTTP communication protocol");
            updateTime = req.getIntInfo("updateTime");
        }
        if (updateProtocol == JMConstants.RMI_UPDATE_PROTOCOL) {
            log.info("Session will be using RMI communication protocol");
        }
        
        return updateTime;
    }
    
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
    public void registerMonitorTransmitter(MonitorTransmitter mt, int sessionId) {
        if (monitorServ.registerExpMonitor(sessionId, mt)) {
            boolean firstMonitor = controlServ.registerExpMonitor(sessionId, mt);
            
            if (!firstMonitor) {
                Vector securities = null;
                NumOffersUpdate[] offerUpdates = null;
                MetricsUpdate[] metricsUpdates = null;
                
                TradeEngine tradeServ = activeEngines.get(sessionId);
                if (tradeServ != null) {
                    securities = tradeServ.getPriceChartView(sessionId);
                    offerUpdates = tradeServ.getNumOffers(sessionId);
                    metricsUpdates = tradeServ.getMetrics(sessionId);
                }
                
                String[] names = controlServ.getSubjectNames(sessionId);
                boolean enableStart = !controlServ.isSessionRunning(sessionId);
                
                
                monitorServ.updateState(sessionId, mt, securities, names, enableStart, offerUpdates, metricsUpdates);
                log.info("An alternative (non-primary) ExpMonitor has been connected and brought up to state for session " + sessionId);
            }
        }
    }
    
    /** Whenever a client or admin monitor logs in, they send a session query request to
     *  find out what sessions are currently active. This returns an array of SessionIdentifier
     *  objects so that they can display this information and choose what session to
     *  join. This also sends the monitor the communication protocol and port that it will use
     *  from this point onward to register with and communicate with the server */
    public Response processSessionQueryRequest(Request req) {
        log.debug("DispatchServ has received a SESSION_QUERY_REQUEST -- returning active session list");
        
        SessionIdentifier[] identifiers = controlServ.getSessionIdentifiers();
        
        int protocol = monitorServ.getMonitorProtocol();
        String protocolStr = "rmi";
        if (protocol == Monitor.TCP_MONITOR_PROTOCOL)
            protocolStr = "tcp";
        
        int port = monitorServ.getMonitorPort();
        
        Response res = new Response(Response.SESSION_QUERY_RESPONSE);
        res.addInfo("identifiers", identifiers);
        res.addStringInfo("protocol", protocolStr);
        res.addIntInfo("port", port);
        
        return res;
    }
    
    /** Called when the server receives a CLIENT_AUTH_REQUEST from a client. Initializes new clients
     *  by sending them their client ID numbers. Re-authenticates disconnected clients
     *  by sending all previous state information. The dispatcher collects authentication information
     *  from the client request, sends it to the ControlServ for processing, then sends out the
     *  results of the authentication process back to the clients. This method also handles
     *  re-authentication, for disconnected clients who try to reconnect to currently running
     *  sessions. In order to process re-authentication, the dispatcher collects information on
     *  client connection status from the UpdateServ and passes this to the ControlServ */
    public synchronized Response processAuthRequest(Request req) {
        try {
            log.debug("DispatchServ has received a CLIENT_AUTH_REQUEST -- processing authentication");
            
            String name = req.getStringInfo("name");
            int dbId = req.getIntInfo("dbId");
            int sessionId = req.getIntInfo("sessionId");
            
            Hashtable disconnectStatus = updateServ.getDisconnectStatus();
            AuthUpdate authPacket = controlServ.authClient(sessionId, name, dbId, disconnectStatus);
            
            if (authPacket.getStatus() == AuthUpdate.AUTH_FAILED) {
                Response res = new Response(Response.AUTH_REJECT_RESPONSE);
                res.addStringInfo("message", authPacket.getErrorMsg());
                res.addBooleanInfo("retry", authPacket.isRetry());
                
                return res;
            }
            
            int id = authPacket.getId();
            int updateTime = authPacket.getUpdateTime();
            
            Response res = new Response(Response.AUTH_CONFIRM_RESPONSE);
            res.addIntInfo("updateProtocol", JMConstants.HTTP_UPDATE_PROTOCOL);
            res.addIntInfo("updateTime", updateTime);
            res.addStringInfo("tradeServlet", receiverPath);
            res.addIntInfo("id", id);
            res.addIntInfo("sessionId", sessionId);
            
            if (authPacket.getStatus() == AuthUpdate.AUTH_SUCCESS) {
                int numConnected = authPacket.getNumConnected();
                boolean allConnected = authPacket.isAllConnected();
                
                monitorServ.updateAuthStatus(sessionId, id, name, numConnected, allConnected);
                updateServ.addClient(dbId);
            }
            
            if (authPacket.getStatus() == AuthUpdate.RE_AUTH_SUCCESS) {
                if (authPacket.isReplacement()) {
                    Response reauthKill = new Response(Response.REAUTH_KILL_RESPONSE);
                    updateServ.update(dbId, reauthKill);
                }
                
                PeriodDef period = authPacket.getPeriodInfo();
                TradeEngine tradeServ = activeEngines.get(sessionId);
                
                tradeServ.resetClientKey(sessionId, id);
                
                Response reauth = new Response(Response.REAUTH_RESPONSE);
                reauth.addInfo("offerbook", tradeServ.generateClientBook(sessionId));
                reauth.addInfo("chart", tradeServ.getPriceChartView(sessionId));
                reauth.addInfo("marketInfo", period.getMarketInfo());
                reauth.addStringInfo("marketEngine", period.getMarketEngine());
                reauth.addInfo("trader", tradeServ.getTraders(sessionId)[id]);
                reauth.addIntInfo("openDelay", controlServ.getOpeningTime(sessionId));
                reauth.addIntInfo("periodLength", controlServ.getPeriodTime(sessionId));
                reauth.addInfo("marketLength", controlServ.getMarketTime(sessionId));
                reauth.addIntInfo("periodNum", controlServ.getPeriodNum(sessionId));
                reauth.addInfo("earningsInfo", controlServ.getEarningsHistory(sessionId)[id]);
                reauth.addInfo("marketClosed", controlServ.getMarketStatus(sessionId));
                reauth.addBooleanInfo("periodClosed", controlServ.isPeriodClosed(sessionId));
                
                updateServ.update(dbId, reauth);
            }
            
            return res;
        }catch(Exception e) {
            log.error("Failed to process a CLIENT_AUTH_REQUEST to connect a client to server", e);
        }
        Response res = new Response(Response.AUTH_REJECT_RESPONSE);
        res.addStringInfo("message", "Authentication failed for unknown reason. Please try again.");
        return res;
    }
    
    /** Start the game in response to the administrator pressing the start button on the ExpMonitor */
    public Response processStartRequest(Request req) {
        try {
            int sessionId = req.getIntInfo("sessionId");
            boolean sessionStarted = controlServ.startSession(sessionId);
            
            if (sessionStarted) {
                monitorServ.setSessionRunning(sessionId, true);
                monitorServ.updateExpStatus(sessionId, "Starting Session");
                nextPeriod(sessionId);
            } else {
                monitorServ.setSessionRunning(sessionId, false);
            }
            
        }catch(Exception e) {
            log.error("Failed to process a START_GAME_REQUEST and move to the next period", e);
        }
        return new Response(Response.GENERAL_ACK_RESPONSE);
    }
    
    public Response processStartPeriodRequest(Request req) {
        try {
            int sessionId = req.getIntInfo("sessionId");
            
            nextPeriod(sessionId);
            
        }catch(Exception e) {
            log.error("Failed to process a START_GAME_REQUEST and move to the next period", e);
        }
        return new Response(Response.GENERAL_ACK_RESPONSE);
    }
    
    public Response processStopPeriodRequest(Request req) {
        try {
            int sessionId = req.getIntInfo("sessionId");
        }catch(Exception e) {
            log.error("Failed to process a STOP_GAME_REQUEST and move to the next period", e);
        }
        return new Response(Response.GENERAL_ACK_RESPONSE);
    }
    
    /** Stop the game in response to the administrator pressing the stop button on the ExpMonitor */
    public Response processStopRequest(Request req) {
        try {
            int sessionId = req.getIntInfo("sessionId");
        }catch(Exception e) {
            log.error("Failed to process a STOP_GAME_REQUEST and move to the next period", e);
        }
        return new Response(Response.GENERAL_ACK_RESPONSE);
    }
    
    public Response processIsManualControlRequest(Request req) {
        boolean manual = false;
        try {
            int sessionId = req.getIntInfo("sessionId");
            //log.error("processing an IS_MANUAL_CONTROL_REQUEST for sessionId="+sessionId);
            manual = controlServ.getManualControl(sessionId);
            //log.error("processing an IS_MANUAL_CONTROL_REQUEST: manual=="+manual);
            
        }catch(Exception e) {
            log.error("Failed to process a IS_MANUAL_CONTROL_REQUEST - returning FALSE", e);
        }
        Response res = new Response(Response.IS_MANUAL_CONTROL_RESPONSE);
        res.addBooleanInfo("manualControl", manual);
        return res;
    }
    
    public Response processSetManualControlRequest(Request req) {
        try {
            boolean manual = req.getBooleanInfo("mc");
            int sessionId = req.getIntInfo("sessionId");
            controlServ.setManualControl(sessionId, manual);
        }catch(Exception e) {
            log.error("Failed to process a STOP_GAME_REQUEST and move to the next period", e);
        }
        return new Response(Response.GENERAL_ACK_RESPONSE);
    }
    
    /** Move to the next period of the given session. Collect the information for the new period
     *  from the ControlServ, which also writes that information into the database. Send the
     *  collected information to the clients via the updateServ. Finally, start a JTimer to track
     *  time events in the new period */
    private void nextPeriod(int sessionId) {
        NewPeriodUpdate npu = controlServ.nextPeriod(sessionId);
        
        int[] recipients = npu.getRecipients();
        int periodNum = npu.getPeriodNum();
        PeriodDef pinfo = npu.getPeriodInfo();
        EarningsInfo[] earningsHistory = npu.getEarningsHistory();
        float[] initialCash = npu.getInitialCash();
        int[][] initialHoldings = npu.getInitialHoldings();
        int timeoutLength = npu.getTimeoutLength();
        String engine = npu.getMarketEngine();
        
        TradeEngine tradeServ = continuousMarketEngine;
        if (engine.equalsIgnoreCase(JMConstants.CALL_MARKET_ENGINE)) {
            tradeServ = callMarketEngine;
        }
        activeEngines.put(sessionId, tradeServ);
        
        Trader[] traders = tradeServ.initPeriod(sessionId, periodNum, pinfo, initialCash, initialHoldings);
        Vector securities = tradeServ.getPriceChartView(sessionId);
        OfferBook offerBook = tradeServ.generateClientBook(sessionId);
        
        updateServ.sendNewPeriodUpdate(recipients, periodNum, pinfo, earningsHistory, traders, offerBook);
        monitorServ.updateExpStatus(sessionId, "Initializing Period " + periodNum);
        monitorServ.updatePeriodStatus(sessionId, securities);
        controlServ.setTraders(sessionId, periodNum, traders);
        
        int periodLength = pinfo.getPeriodLength();
        int openDelay = pinfo.getOpenDelay();
        int[] marketLength = pinfo.getMarketInfo().getMarketTime();
        
        jTimer.schedulePeriodTimer(sessionId, periodNum, timeoutLength, periodLength, openDelay, marketLength);
    }
    
    /** JMTimer calls this method every second whenever a session's timer display should be updated. Note
     *  this is NOT called when the JMTimer wants to update (re-sync) client timers -- for that, the method
     *  processTimeEvent is called. This method ONLY updates timers on the monitors */
    public boolean setTimeLeft(int sessionId, int openDelay, int periodLength, int[] marketLength) {
        monitorServ.updateTimeLeft(sessionId, periodLength);
        return controlServ.updateTimers(sessionId, openDelay, periodLength, marketLength);
    }
    
    /** JMTimer calls this method every time it detects the need for a time synchronization between
     *  the server and clients. It passes in the three timers for the period. The dispatcher routes
     *  this request to the UpdateServ to inform the clients, and the ControlServ to update the session
     *  data structures. Returns the response sent out by the UpdateServ */
    public Response processTimeEvent(int sessionId, int periodNum, int openDelay, int periodLength, int[] marketLength) {
        int[] recipients = controlServ.getSessionClients(sessionId);
        controlServ.updateTimers(sessionId, openDelay, periodLength, marketLength);
        
        if (recipients != null)
            return updateServ.sendTimeUpdate(recipients, periodNum, openDelay, periodLength, marketLength);
        else
            return null;
    }
    
    /** JMTimer calls this method every time a market closes in the given session. The dispatcher
     *  routes this request to the UpdateServ to inform the clients, and the ControlServ to update
     *  the session data structures. Returns the response sent out by the UpdateServ */
    public Response processMarketClosure(int sessionId, int periodNum, int market) {
        int[] recipients = controlServ.getSessionClients(sessionId);
        controlServ.closeMarket(sessionId, market);
        
        if (recipients != null)
            return updateServ.sendMarketCloseUpdate(recipients, periodNum, market);
        else
            return null;
    }
    
    /**
     * JMTimer calls this method every time a period opens. This occurs when that period's market
     *  opening delay has finished. The dispatcher routes this request to the ContinuousMarketEngine
     */
    public boolean processPeriodOpening(int sessionId, int periodNum) {
        TradeEngine tradeServ = activeEngines.get(sessionId);
        
        return tradeServ.stampPeriodStartTime(sessionId);
    }
    
    /** JMTimer calls this method every time a period closes in the given session. The dispatcher
     *  routes this request to the UpdateServ to inform the clients, and the ControlServ to update
     *  the session data structures. Returns the response sent out by the UpdateServ. Before all this
     *  notify the Trade Engine that the period has closed, and process any updates that it
     *  generates (this happens predominantly in call markets, where all offers are processed upon
     *  period closure */
    public Response processPeriodClosure(int sessionId, int periodNum) {
        TradeEngine tradeServ = activeEngines.get(sessionId);
        
        UpdateBasket basket = tradeServ.processClosePeriod(sessionId);
        if (basket != null) {
            int periodTime = 0;
            int[] marketTime = new int[controlServ.getMarketTime(sessionId).length];
            int openingTime = controlServ.getOpeningTime(sessionId);
            
            Vector tradeUpdates = basket.getTradeUpdates();
            for (int i=0; i<tradeUpdates.size(); i++) {
                TradeUpdate tupdate = (TradeUpdate) tradeUpdates.get(i);
                tupdate.setPeriodTime(periodTime);
                tupdate.setMarketTime(marketTime);
                tupdate.setOpeningTime(openingTime);
            }
            
            updateServ.sendTradeUpdates(periodNum, tradeUpdates);
            monitorServ.processTransactionUpdates(basket);
        }
        
        int[] recipients = controlServ.getSessionClients(sessionId);
        boolean lastPeriod = controlServ.closePeriod(sessionId);
        
        
        
        if (recipients == null)
            return null;
        
        PayoffUpdate pupdate = controlServ.calculatePayoffs(sessionId);
        float[] payoffs = pupdate.getPayoffs();
        String[] masks = pupdate.getMasks();
        
        EarningsInfo[] einfo = controlServ.getEarningsHistory(sessionId);
        
        if (lastPeriod) {
            updateServ.sendEndPeriodUpdate(recipients, periodNum, payoffs, masks, einfo, true);
            log.info("Subjects have completed all periods. Ending session in 30 seconds...");
            terminateSession(sessionId, 30000);
        } else {
            log.info("Moving to the next period (period " + (periodNum + 1) + ")");
            updateServ.sendEndPeriodUpdate(recipients, periodNum, payoffs, masks, einfo, false);
            if( !controlServ.getManualControl(sessionId) ){
                nextPeriod(sessionId);
            }
        }
        return null;
    }
    
    /** Called whenever a client attempts to make a trade */
    public Response processTransactionRequest(Request req) {
        if (!isSessionValid(req))
            return new Response(Response.INVALID_SESSION_RESPONSE);
        
        int sessionId = req.getIntInfo("sessionId");
        AbstractOffer newOffer = (AbstractOffer) req.getInfo("offer");
        long key = req.getLongInfo("key");
        int periodNum = controlServ.getPeriodNum(sessionId);
        TradeEngine tradeServ = activeEngines.get(sessionId);
        
        UpdateBasket basket = tradeServ.processOffer(sessionId, newOffer, key);
        
        int periodTime = controlServ.getPeriodTime(sessionId);
        int[] marketTime = controlServ.getMarketTime(sessionId);
        int openingTime = controlServ.getOpeningTime(sessionId);
        
        Vector tradeUpdates = basket.getTradeUpdates();
        for (int i=0; i<tradeUpdates.size(); i++) {
            TradeUpdate tupdate = (TradeUpdate) tradeUpdates.get(i);
            tupdate.setPeriodTime(periodTime);
            tupdate.setMarketTime(marketTime);
            tupdate.setOpeningTime(openingTime);
        }
        
        updateServ.sendTradeUpdates(periodNum, tradeUpdates);
        monitorServ.processTransactionUpdates(basket);
        
        return new Response(Response.GENERAL_ACK_RESPONSE);
    }
    
    /** Respond to a TERMINATE_SESSION_REQUEST by ending the current session and sending back
     *  a general acknowledgment */
    public Response processTerminateRequest(Request req) {
        int sessionId = req.getIntInfo("sessionId");
        log.info("Server has received a TERMINATE_SESSION_REQUEST for session " + sessionId + " -- ending session");
        terminateSession(sessionId, 0);
        
        return new Response(Response.GENERAL_ACK_RESPONSE);
    }
    
    /** Terminate the given session. Delete the clients involved in the session from the roster
     *  of the updateServ, shut down all remaining JTimers associated with the session, and
     *  tell the controlServ to drop all information regarding the session. Return true if
     *  termination succeeds. Wait the given number of seconds before terminating. This is needed
     *  because if we terminate immediately after a session ends, the clients may not have time
     *  to get the END_PERIOD_UPDATES */
    private synchronized boolean terminateSession(int sessionId, int waitTime) {
        try {
            Timer timer = new Timer();
            final int sid = sessionId;
            final TradeEngine tradeServ = activeEngines.get(sid);
            
            TimerTask task = new TimerTask() {
                public void run() {
                    int[] clients = controlServ.getSessionClients(sid);
                    jTimer.terminateSession(sid);
                    updateServ.deleteClients(clients);
                    controlServ.terminateSession(sid);
                    monitorServ.terminateSession(sid);
                    tradeServ.terminateSession(sid);
                    
                    log.info("Session " + sid + " fully terminated, all remaining clients will be deactivated");
                }
            };
            
            timer.schedule(task, waitTime);
            
            return true;
        }catch(Exception e) {
            log.error("Failed to terminate session " + sessionId, e);
        }
        return false;
    }
    
    /** Checks to see if the session ID in the given request is valid. Returns true if the
     *  true if the ID is valid. Returns false otherwise. Usually, this will prompt the
     *  the DispatchServ to send a SESSION_INVALID_RESPONSE to the clieint. A
     *  SESSION_INVALID_RESPONSE, when read by a client in response to an update request,
     *  tells that client to stop sending update requests. This is important because antiquated,
     *  non-disconnected clients can slow down a server with these requests */
    private boolean isSessionValid(Request req) {
        int sessionId = req.getIntInfo("sessionId");
        return controlServ.isSessionValid(sessionId);
    }
    
    /** Return an update in response to a client's request */
    public Response processUpdateRequest(Request req) {
        if (!isSessionValid(req))
            return new Response(Response.INVALID_SESSION_RESPONSE);
        
        return updateServ.getUpdate(req);
    }
    
    /** Called when a request of invalid type is received */
    public Response processUnknownRequest(Request req) {
        log.warn("Received an unknown request. Responding with general acknowledgement");
        return new Response(Response.GENERAL_ACK_RESPONSE);
    }
    
    /** This readies the DispatchServ to receive registration connections from admin monitor GUIs.
     *  The action of this method depends on the protocol being used by the monitors. See the specific
     *  protocol methods for more details on this. Regardless of protocol, what happens is the following:
     *  a persistant interface is set up to listen for monitor receivers to connect. When monitor receivers
     *  attempt to connect to the server, a monitor transmitter is created for them. This transmitter is
     *  then registered with the MonitorServ so that the MonitorServ can communicate directly with the
     *  monitors. The control flow becomes:
     *
     *  MonitorServ --(one to many)--> Transmitters --(one to one) --> Receivers --(one to one)--> Monitors
     */
    private int activateMonitorTunnel(int protocol, int port) {
        if (protocol == Monitor.RMI_MONITOR_PROTOCOL) {
            return activateRMIMonitorTunnel(port);
        }
        
        else if (protocol == Monitor.TCP_MONITOR_PROTOCOL) {
            return activateTCPMonitorTunnel(port);
        }
        
        else return OPERATION_FAILED;
    }
    
    /** RMI: This method turns on an RMI server that waits for an RMIMonitor interfaces (specifically,
     *  RMIMonitorReceivers) to register themselves with the MonitorServ. Each receiver that registers
     *  is assigned a new RMIMonitorTransmitter which is used by the MonitorServ to communicate to the
     *  receiver, which in turn manipulates the monitor on the admin side.
     */
    private int activateRMIMonitorTunnel(int port) {
        try {
            tunnel = new GUITunnel(this);
            log.info("DispatchServ has created a GUITunnel object and linked it to the Dispatcher");
            
            UnicastRemoteObject.exportObject((GUILink) tunnel);
            log.info("DispatchServ has exported the GUITunnel in the form of a GUILink object");
            
            registry = new SimpleObjectRegistry(port, port + 20);
            
            registry.rebind("link", (GUILink) tunnel);
            log.info("DispatchServ has successfully bound the Monitor Registrar onto port " + registry.port());
            
            return registry.port();
        } catch (java.rmi.UnknownHostException uhe) {
            log.error("The host computer name you have specified for RMI monitor communication is invalid", uhe);
            return OPERATION_FAILED;
        } catch (Exception re) {
            log.error("Error exporting GUITunnel for use in RMI monitor communication", re);
            return OPERATION_FAILED;
        }
    }
    
    /** TCP: This method opens up a ServerSocket at the given port then starts a thread that waits
     *  for TCPMonitorReceivers to attempt to connect. Each connecting TCPMonitorReceiver creates an
     *  individual Socket connection, which is then attached to a TCPMonitorTransmitter. The MonitorServ
     *  uses the transmitter to communicate with the receiver, which in turn manipulates the monitor.
     */
    private int activateTCPMonitorTunnel(int port) {
        try {
            log.info("DispatchServ is creating a TCP ServerSocket on port " + port + " for use in server-monitor communication");
            servSock = new ServerSocket(port);
            
            Runnable registrar = new Runnable() {
                
                public void run() {
                    
                    while (!stopRegThread) {
                        try {
                            monitorSocket = servSock.accept();
                            
                            log.debug("Communication socket accepted from new monitor, opening input/output streams...");
                            
                            ObjectInputStream inputStream = new ObjectInputStream(monitorSocket.getInputStream());
                            ObjectOutputStream outputStream = new ObjectOutputStream(monitorSocket.getOutputStream());
                            
                            outputStream.flush();
                            
                            log.debug("Input stream opened, reading session ID from connecting monitor");
                            int sessionId = inputStream.readInt();
                            log.debug("Session ID " + sessionId + " read from new monitor socket, pairing TCP receiver with a transmitter");
                            
                            MonitorTransmitter transmitter = new TCPMonitorTransmitter(monitorSocket, inputStream, outputStream);
                            registerMonitorTransmitter(transmitter, sessionId);
                        }catch(Exception e) {
                            log.warn("Error while accepting a monitor socket connection -- returning to 'waiting for accept' state", e);
                            System.out.println("Error while accepting a monitor socket connection -- returning to 'waiting for accept' state");
                            e.printStackTrace();
                            continue;
                        }
                    }
                    /*log.error("exited while loop");
                    try{
                        monitorSocket.close();
                    } catch( IOException ioe ){
                        log.warn("Error while trying to close monitorSocket");
                        ioe.printStackTrace();
                    }*/
                }
            };
            
            registrationThread = new Thread(registrar);
            registrationThread.setDaemon(true);
            registrationThread.start();
            
            return port;
        }catch(Exception re) {
            log.error("Error activating ServerSocket for use in TCP monitor communication", re);
            return OPERATION_FAILED;
        }
    }
    
    /** Controls the data and session state */
    private Controller controlServ;
    
    /** Controls all handling of updates to clients */
    private Updater updateServ;
    
    /** Maps session numbers to their currently active market engines */
    private Map<Integer, TradeEngine> activeEngines;
    
    /** Controls all continuos market transactions */
    private TradeEngine continuousMarketEngine;
    
    /** Controls all call market transactions */
    private TradeEngine callMarketEngine;
    
    /** Keeps track of and updates the experiment monitors for each session */
    private Monitor monitorServ;
    
    /** Handles all JMarkets timer events, the only class other than the Receiver that can
     *  call methods on the Dispatcher */
    private JMTimer jTimer;
    
    /** The appender used to log to the GUI */
    private guiAppender appender;
    
    /** The path used by clients to access the Servlet Receiver */
    private String receiverPath;
    
    /** Used to initialize links with the ExpMonitor interfaces */
    private GUITunnel tunnel;
    
    /** Registry used for exporting the GUITunnel objects via RMI */
    private SimpleObjectRegistry registry;
    
    private ConnectionMonitor cmon;
    
    private Thread registrationThread;
    private volatile boolean stopRegThread=false;
    private Socket monitorSocket;
    private ServerSocket servSock;
    
    private static int OPERATION_FAILED = -1;
    
    private static Log log = LogFactory.getLog(DispatchServ.class);
}
