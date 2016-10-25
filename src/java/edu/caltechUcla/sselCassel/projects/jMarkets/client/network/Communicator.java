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
 * Communicator.java
 *
 * Created on March 17, 2004, 1:43 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.network;

import java.net.*;
import java.io.*; 
import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.control.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable.EarningsInfo;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SubjectDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.GroupDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.MarketDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;

/**
 *
 * @author  Raj Advani
 */
public class Communicator {
    
    /** Creates a new instance of Communicator */
    public Communicator(URL codebase, Client client) {
        this.codebase = codebase;
        this.client = client;
        
        try {
            this.servletReceiver = new URL(codebase, "servlet/ServletReceiver");
            
            System.out.println("Servlet Receiver URL: " + servletReceiver);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Get a list of active sessions from the server and send them to the Client
     *  controller */
    public SessionIdentifier[] getSessionList() {
        try {
            System.out.println("Getting session list...");
            
            Request req = new Request(Request.SESSION_QUERY_REQUEST);
            Response res = sendRequest(servletReceiver, req, req);
            
            if (res.getType() == Response.SESSION_QUERY_RESPONSE) {
                System.out.println("Retrieved session information");
                SessionIdentifier[] identifiers = (SessionIdentifier[]) res.getInfo("identifiers");
                return identifiers;
            }
            else {
                System.out.println("Error retrieving identifiers, returning null");
                return null;
            }
            
        }catch(Exception e) {
            System.out.println("Failed to retrieve session list form server");
            e.printStackTrace();
        }
        return null;
    }
    
    /** Connect this client to the server. This is done by connecting to the ServletReceiver
     *  servlet on the server that instnatiated the ClientGUI applet. That servlet should
     *  be waiting for clients to send CLIENT_AUTH_REQUESTS. The servlet will respond
     *  with a AUTH_CONFIRM_RESPONSE that will contain the following information:
     *
     *  1) The Update protocol to be used: RMI or HTTP and all necessary information to
     *     get the desired protocol up and running
     *  2) The URLs of the UpdateServ and the TradeServ
     *
     *  If a game is currently running then the servlet will respond with an
     *  AUTH_REJECT_RESPONSE that will contains a String message citing the reason the
     *  authentication request was rejected. This message will be displayed by the applet
     *  along with an invitation to attempt to authenticate again
     */
    public void connect(int sessionId, String name, int dbId) throws MalformedURLException, IOException, ClassNotFoundException {
        System.out.println("Connecting...");
        
        Request req = new Request(Request.CLIENT_AUTH_REQUEST);
        req.addStringInfo("name", name);
        req.addIntInfo("dbId", dbId);
        req.addIntInfo("sessionId", sessionId);
        
        System.out.println("Sending name " + name + " and dbId " + dbId + " to " + servletReceiver);
        
        Response res = sendRequest(servletReceiver, req, req);
        
        if (res.getType() == Response.AUTH_CONFIRM_RESPONSE) {
            client.setId(res.getIntInfo("id"));
            client.setSessionId(res.getIntInfo("sessionId"));
            
            if (!client.isWaitingDialogNull())
                client.setWaitingDialogActive("Connection Successful", "Please be Patient -- Waiting for Admin", 2000);
            
            updateProtocol = res.getIntInfo("updateProtocol");
            if (updateProtocol == JMConstants.HTTP_UPDATE_PROTOCOL)
                startHTTPUpdateProtocol(res);
            if (updateProtocol == JMConstants.RMI_UPDATE_PROTOCOL)
                startRMIUPdateProtocol(res);
            
            tradeServ = new URL(codebase, res.getStringInfo("tradeServlet"));
        }
        
        else if (res.getType() == Response.AUTH_REJECT_RESPONSE) {
            client.setWaitingDialogInactive();
            String message = res.getStringInfo("message");
            boolean retry = res.getBooleanInfo("retry");
            
            if (!retry)
                client.displayError("Connection Error", message);
            else {
                System.out.println("Authentication not ready, will retry: " + message);
                authRetryWait();
                connect(sessionId, name, dbId);
            }
        }
    }
    
    /** Wait for some period of time before returning. This is used to get the Communicator
     *  to wait before attempting to reconnect to the server */
    private synchronized void authRetryWait() {
        try {
            wait(1500);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Send an HTTP POST request to the specified servlet containing the given request. Opens
     *  an input/output non-cached connection to the servlet and readies it for sending
     *  binary data. Then sends the request and returns the response of the Servlet. The retryReq
     *  is the request that should be sent if the READ fails. If the write fails then the regular
     *  req is sent again. If retryReq is null, then if the read fails this function will simply
     *  return null */
    public Response sendRequest(URL servlet, Request req, Request retryReq) {
        try {
            URLConnection servletConnection = servlet.openConnection();
            servletConnection.setConnectTimeout(40000);
            servletConnection.setReadTimeout(60000);
            
            servletConnection.setDoInput(true);
            servletConnection.setDoOutput(true);
            servletConnection.setUseCaches(false);
            servletConnection.setDefaultUseCaches(false);
            servletConnection.setRequestProperty("Content-Type", "application/octet-stream");
            
            try {
                ObjectOutputStream outputToServlet = new ObjectOutputStream(servletConnection.getOutputStream());
                
                outputToServlet.writeObject(req);
                outputToServlet.flush();
                outputToServlet.close();
            }catch(SocketTimeoutException e) {
                System.out.println("Connect timeout -- retrying request");
                return sendRequest(servlet, req, retryReq);
            }
            
            try {
                ObjectInputStream inputFromServlet = new ObjectInputStream(servletConnection.getInputStream());
                Response res = (Response) inputFromServlet.readObject();
                inputFromServlet.close();
                return res;
            }catch(SocketTimeoutException e) {
                System.out.println("Read timeout -- sending retry request");
                if (retryReq != null)
                    return sendRequest(servlet, retryReq, retryReq);
            }
            
        }catch(SocketTimeoutException e) {
            System.out.println("Read or connect timeout -- retrying request");
            sendRequest(servlet, req, retryReq);
        }catch(BindException e) {
            System.out.println("Failed to bind to HTTP output port -- retrying");
            sendRequest(servlet, req, retryReq);
        }catch(IOException e) {
            e.printStackTrace();
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /** This method is called when a Response is received from the server. Handles the response. If
     *  the underlying implementation is HTTP then this method will be called by some thread that
     *  periodically asks the server for updates, which are then relayed here. If the underlying
     *  implementation is RMI (not firewall safe) then this method is called directly from the server
     *  whenever udpates are needed (RMI implementation heavily decreases the number of transactions
     *  needed as the HTTP implementation checks at least once a second for updates, even when there
     *  may not be any) */
    public void processUpdate(Response res) {
        try {
            int type = res.getType();
            
            if (type == Response.TIME_UPDATE_RESPONSE) {
                int periodTime = res.getIntInfo("period_time");
                int[] marketTime = (int[]) res.getInfo("market_time");
                int openingTime = res.getIntInfo("opening_time");
                int periodNum = res.getIntInfo("periodNum");
                
                if (periodNum != client.getPeriodNum()) {
                    System.out.println("Received server response from invalid period (" + periodNum + ") during period " + client.getPeriodNum());
                    return;
                }
                
                client.updateTime(periodTime, marketTime, openingTime);
            }
            
            if (type == Response.NEW_PERIOD_UPDATE) {
                MarketDef marketInfo = (MarketDef) res.getInfo("marketInfo");
                Trader trader = (Trader) res.getInfo("trader");
                EarningsInfo earningsInfo = (EarningsInfo) res.getInfo("earningsInfo");
                final int periodLength = res.getIntInfo("periodLength");
                int periodNum = res.getIntInfo("periodNum");
                OfferBook offerBook = (OfferBook) res.getInfo("offerBook");
                String marketEngine = res.getStringInfo("marketEngine");
                
                System.out.println("Received new period update");
                
                client.setWaitingDialogActive("Please Wait", "Initializing Period");
                client.setPeriodNum(periodNum);
                client.startPeriod(periodLength, trader, marketInfo, earningsInfo, offerBook, marketEngine); 
            }
            
            if (type == Response.REAUTH_RESPONSE) {
                OfferBook offerbook = (OfferBook) res.getInfo("offerbook");
                int[] marketLength = (int[]) res.getInfo("marketLength");
                MarketDef marketInfo = (MarketDef) res.getInfo("marketInfo");
                Trader trader = (Trader) res.getInfo("trader");
                EarningsInfo earningsInfo = (EarningsInfo) res.getInfo("earningsInfo");
                final int openDelay = res.getIntInfo("openDelay");
                final int periodLength = res.getIntInfo("periodLength");
                Vector chart = (Vector) res.getInfo("chart");
                boolean[] marketClosed = (boolean[]) res.getInfo("marketClosed");
                int periodNum = res.getIntInfo("periodNum");
                boolean periodClosed = res.getBooleanInfo("periodClosed");
                String marketEngine = res.getStringInfo("marketEngine");
                
                System.out.println("Processed re-auth response");
                
                client.setWaitingDialogActive("Please Wait", "Re-initializing Period", 2000);
                client.setPeriodNum(periodNum);
                client.restartPeriod(periodLength, marketLength, marketClosed, trader, marketInfo, offerbook, chart, earningsInfo, periodClosed, marketEngine); 
            }
            
            if (type == Response.OFFER_BOOK_UPDATE) {
                int action = res.getIntInfo("action");
                int[] priceBook = (int[]) res.getInfo("pricebook");
                int marketId = res.getIntInfo("marketId");
                int priceId = res.getIntInfo("priceId");
                int stdPriceId = res.getIntInfo("standingPriceId"); 
                long time = res.getLongInfo("time");
                long key = res.getLongInfo("key");
                String code = res.getStringInfo("code");
                int periodNum = res.getIntInfo("periodNum");
                
                if (periodNum != client.getPeriodNum()) {
                    System.out.println("Received server response from invalid period (" + periodNum + ") during period " + client.getPeriodNum());
                    return;
                }
                
                boolean owned = res.getBooleanInfo("transaction");
                float cashHoldings = res.getFloatInfo("cashHoldings");
                int unitsTraded = res.getIntInfo("unitsTraded");
                String actionName = res.getStringInfo("actionType");
                
                client.updateKey(key, code);
                if(!(client.getMarketEngine().equals(JMConstants.CALL_MARKET_ENGINE) && actionName!=null))
                    client.inputTransaction(action, marketId, stdPriceId, priceBook, time);
                
                if(owned || actionName != null)
                    client.updateTransactionsPanel(marketId, priceId, stdPriceId, time, unitsTraded, actionName, owned);
                
                if(owned){
                    int[] numSales = (int[]) res.getInfo("numSales");
                    int[] numPurchases = (int[]) res.getInfo("numPurchases");
                    int secHoldings = res.getIntInfo("securityHoldings");
                    client.updateHoldings(marketId, cashHoldings, secHoldings, numSales, numPurchases);
                }
                int periodTime = res.getIntInfo("period_time");
                int[] marketTime = (int[]) res.getInfo("market_time");
                int openingTime = res.getIntInfo("opening_time");
                
                //client.updateTime(periodTime, marketTime, openingTime);
            }
            
            if (type == Response.OFFER_INVALID_UPDATE) {
                String message = res.getStringInfo("message");
                String code = res.getStringInfo("code");
                int periodNum = res.getIntInfo("periodNum");
                
                if (periodNum != client.getPeriodNum()) {
                    System.out.println("Received server response from invalid period (" + periodNum + ") during period " + client.getPeriodNum());
                    return;
                }
                
                client.displayError("Invalid Offer", message, code);
            }
            
            if (type == Response.END_PERIOD_UPDATE) {
                boolean endExperiment = res.getBooleanInfo("endExperiment");
                float payoff = res.getFloatInfo("payoff");
                String mask = res.getStringInfo("mask");
                EarningsInfo einfo = (EarningsInfo) res.getInfo("earningsHistory");
                int periodNum = res.getIntInfo("periodNum");
                
                if (periodNum != client.getPeriodNum()) {
                    System.out.println("Received server response from invalid period (" + periodNum + ") during period " + client.getPeriodNum());
                    return;
                }
                
                System.out.println("Received end period update");
                
                client.closePeriod();
                client.updateEarnings(einfo);
                client.processEndPeriod(payoff, mask, endExperiment);
            }
            
            /*if (type == Response.CLOSE_MARKET_UPDATE) {
                int marketNum = res.getIntInfo("marketNum");
                int periodNum = res.getIntInfo("periodNum");
                
                if (periodNum != client.getPeriodNum()) {
                    System.out.println("Received server response from invalid period (" + periodNum + ") during period " + client.getPeriodNum());
                    return;
                }
                
                client.closeMarket(marketNum);
            }*/
            
            if (type == Response.REAUTH_KILL_RESPONSE) {
                client.closePeriod();
                client.setWaitingDialogActive("Client Disconnected", "Another computer logged in under your name", 5000);
                System.out.println("Client disconnected, replaced by newly authenticated client");
            }
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Starts the HTTPUpdater thread to continually receive updates from the server */
    private void startHTTPUpdateProtocol(Response res) {
        try {
            int updateTime = res.getIntInfo("updateTime");
            URL url = servletReceiver;
            
            httpUpdater = new HTTPUpdater(this, url, updateTime, client.getDbId(), client.getSessionId());
            Thread updateThread = new Thread(httpUpdater);
            
            updateThread.start();
        }catch(Exception e) {
        }
    }
    
    /** Starts the RMIUpdater to receive updates from the server via RMI */
    private void startRMIUPdateProtocol(Response res) {
        
    }
    
    /** Called when the client conducts a transaction. Send the transaction to the
     *  TradeServ. This method does not check to see if the transaction is valid; this
     *  should be checked by the client before calling this method. */
    public void sendTransactionInfo(AbstractOffer offer, int sessionId, long key) {
        Request req = new Request(Request.TRANSACTION_REQUEST);
        req.addInfo("offer", offer);
        req.addLongInfo("key", key);
        req.addIntInfo("sessionId", sessionId);
        
        String offerCode = "(" + offer.getSubjectId() + ", " + offer.getMarketId() + ", " + offer.getPriceId() + " ," + offer.getUnits() + ")";
        System.out.println(offer.getSubjectId() + ": sending offer code " + offerCode + " to TradeServ");
        sendRequest(tradeServ, req, null);
    }
    
    public HTTPUpdater getHTTPUpdater() {
        return httpUpdater;
    }
    
    /** URLS to the server */
    private URL codebase;
    private URL servletReceiver;
    private URL tradeServ;
    
    /** The HTTP updater */
    private HTTPUpdater httpUpdater;
    
    /** The main client controller */
    private Client client;
    
    /** The update protocol this client is using to receive updates from the server */
    public int updateProtocol;
}
