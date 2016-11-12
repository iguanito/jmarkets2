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
 * UpdateServ.java
 *
 * Created on March 18, 2004, 4:54 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.updates.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable.EarningsInfo;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.PeriodDef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  Raj Advani
 *
 * The UpdateServ is extended by either an HTTP or RMI implementation. Each time an
 * action is made by one of the clients, the updateQueue is filled with the Response
 * objects that must be sent to the client. An HTTP implementation will send out these
 * responses each time it receives an update request from the client. An RMI implementation
 * will connect directly to the Communicator on the client-side and sent the responses
 * immediately when received in the updateQueue
 *
 */
public abstract class UpdateServ implements Updater {
    
    /** Creates a new instance of UpdateServ */
    public UpdateServ() {
        initServ();
    }
    
    /** Initializes the UpdateServ for the new session. Called when the UpdateServ
     *  is instantiated */
    public void initServ() {
        updateQueues = new Hashtable();
        disconnected = new Hashtable();
        
        log.debug("UpdateServ has been initialized");
    }
    
    /** Add the given array of clients. Return true if the operation is successful */
    public boolean addClients(int[] dbIds) {
        for (int i=0; i<dbIds.length; i++) {
            if (!addClient(dbIds[i]))
                return false;
        }
        return true;
    }
    
    /** Add the given array of clients. Return true if the operation is successful */
    public boolean deleteClients(int[] dbIds) {
        for (int i=0; i<dbIds.length; i++) {
            if (!deleteClient(dbIds[i]))
                return false;
        }
        return true;
    }
    
    /** Add a client, with the given database subject ID, to the current UpdateServ. This
     *  is called at the beginning of a session. Return true if the operation is successful */
    public boolean addClient(int dbId) {
        Integer clientInt = new Integer(dbId);
        
        if (updateQueues.containsKey(clientInt)) {
            log.warn("UpdateServ already contains client " + dbId + " -- a client may only be involved in one session at a time");
            return false;
        }
        
        updateQueues.put(clientInt, new Vector());
        disconnected.put(clientInt, Boolean.FALSE);
        return true;
    }
    
    /** Delete a client form the UpdateServ queues. This is called when a client drops out of
     *  a session or when a session is completed */
    public boolean deleteClient(int dbId) {
        Integer clientInt = new Integer(dbId);
        
        if (updateQueues.containsKey(clientInt)) {
            log.debug("Deleting client " + dbId + " from the UpdateServ");
            updateQueues.remove(clientInt);
            disconnected.remove(clientInt);
            return true;
        }
        
        log.warn("UpdateServ cannot delete client " + dbId + " -- client was not in any session!");
        return false;
    }
    
    /** Adds the given update response to the given client's queue */
    public void update(int dbId, Response res) {
        processUpdate(dbId, res);
    }
    
    /** Add the given update to the queue of the given clients */
    public void update(int[] recipients, Response update) {
        for (int i=0; i<recipients.length; i++) {
            update(recipients[i], update);
        }
    }
    
    /** Send a NEW_PERIOD_UPDATE to each of the clients in the recipients array. Form the update from the given market, subject,
     *  and earnings information. Return the array of Response objects that are to be sent to the clients. */
    public Response[] sendNewPeriodUpdate(int[] recipients, int periodNum, PeriodDef period, EarningsInfo[] earningsHistory, Trader[] traders, OfferBook offerBook) {
        Response[] updates = new Response[recipients.length];
        
        for (int i=0; i<recipients.length; i++) {
            updates[i] = new Response(Response.NEW_PERIOD_UPDATE);
            updates[i].addInfo("marketInfo", period.getMarketInfo());
            updates[i].addInfo("trader", traders[i]);
            updates[i].addInfo("earningsInfo", earningsHistory[i]);
            updates[i].addIntInfo("periodLength", period.getPeriodLength());
            updates[i].addIntInfo("periodNum", periodNum);
            updates[i].addInfo("offerBook", offerBook);
            updates[i].addStringInfo("marketEngine", period.getMarketEngine());
            
            update(recipients[i], updates[i]);
        }
        
        log.debug("UpdateServ has sent a NEW_PERIOD_UPDATE to the clients");
        
        return updates;
    }
    
    /** Send a time update to the given clients. These updates re-sync the client clocks with the
     *  server clocks. The JMTimer class determines when it is appropriate to send these re-syncs.
     *  Re-syncs also occur whenever any client sends an offer */
    public Response sendTimeUpdate(int[] recipients, int periodNum, int openDelay, int periodLength, int[] marketLength) {
        log.debug("Sending time update with period time " + periodLength  + " and opening time " + openDelay);
        
        Response timeUpdate = new Response(Response.TIME_UPDATE_RESPONSE);
        timeUpdate.addIntInfo("period_time", periodLength);
        timeUpdate.addIntInfo("opening_time", openDelay);
        timeUpdate.addInfo("market_time", marketLength);
        timeUpdate.addIntInfo("periodNum", periodNum);
        
        update(recipients, timeUpdate);
        return timeUpdate;
    }
    
    /** Send an update to the clients indicating that the given market has closed */
    public Response sendMarketCloseUpdate(int[] recipients, int periodNum, int market) {
        Response marketCloseUpdate = new Response(Response.CLOSE_MARKET_UPDATE);
        marketCloseUpdate.addIntInfo("marketNum", market);
        marketCloseUpdate.addIntInfo("periodNum", periodNum);
        
        update(recipients, marketCloseUpdate);
        return marketCloseUpdate;
    }
    
    /** Send an END_PERIOD_UPDATE to each of the clients that tells them their payoffs earned.
     *  Also tell them whether to await a new period or terminate the experiment */
    public Response[] sendEndPeriodUpdate(int[] recipients, int periodNum, float[] payoffs, String[] masks, EarningsInfo[] earningsHistory, boolean endExperiment) {
        Response[] updates = new Response[recipients.length];
        
        for (int i=0; i<recipients.length; i++) {
            updates[i] = new Response(Response.END_PERIOD_UPDATE);
            updates[i].addFloatInfo("payoff", payoffs[i]);
            updates[i].addBooleanInfo("endExperiment", endExperiment);
            updates[i].addIntInfo("periodNum", periodNum);
            updates[i].addInfo("earningsHistory", earningsHistory[i]);
            
            if (masks[i] != null)
                updates[i].addStringInfo("mask", masks[i]);
            
            update(recipients[i], updates[i]);
            log.debug("Server has sent an end of period update to client " + recipients[i]);
        }
        return updates;
    }
    
    /** Process a Vector of TradeUpdate objects. Send out both the invalid updates and
     *  the valid offer book updates. Return an array of Responses, one corresponding
     *  to each tradeUpdate object */
    public Response[] sendTradeUpdates(int periodNum, Vector tradeUpdates) {
        Response[] responses = new Response[tradeUpdates.size()];
        
        for (int i=0; i<tradeUpdates.size(); i++) {
            TradeUpdate tupdate = (TradeUpdate) tradeUpdates.get(i);
            int type = tupdate.getType();
            int client = tupdate.getClient();
            
            if (type == TradeUpdate.INVALID_OFFER_UPDATE) {
                String msg = tupdate.getErrorMsg();
                String code = tupdate.getCode();
                
                responses[i] = sendInvalidOfferUpdate(client, periodNum, msg, code);
            }
            else {
                responses[i] = sendOfferBookUpdate(client, tupdate);
            }
        }
        return responses;
    }
    
    /** Send an OFFER_INVALID_UPDATE to the given player. This is sent to players who send
     *  in invalid offers */
    private Response sendInvalidOfferUpdate(int dbId, int periodNum, String message, String code) {
        Response update = new Response(Response.OFFER_INVALID_UPDATE);
        update.addStringInfo("message", message);
        update.addStringInfo("code", code);
        update.addIntInfo("periodNum", periodNum);
        
        update(dbId, update);
        return update;
    }
    
    /** Send an OFFER_BOOK_UPDATE to the given player */
    private Response sendOfferBookUpdate(int dbId, TradeUpdate tupdate) {
        Response update = new Response(Response.OFFER_BOOK_UPDATE);
        
        update.addIntInfo("action", tupdate.getAction());
        update.addIntInfo("marketId", tupdate.getMarketId());
        update.addIntInfo("priceId", tupdate.getExecutedPriceId());
        update.addIntInfo("standingPriceId", tupdate.getStandingPriceId()); 
        update.addLongInfo("time", tupdate.getTime());
        update.addLongInfo("key", tupdate.getKey());
        update.addStringInfo("code", tupdate.getCode());
        update.addInfo("pricebook", tupdate.getPriceBook());
        update.addIntInfo("periodNum", tupdate.getPeriodNum());
        
        if (tupdate.getType() == TradeUpdate.TRANSACTION_UPDATE) {
            update.addBooleanInfo("transaction", true); 
            update.addInfo("numPurchases", tupdate.getNumPurchases());
            update.addInfo("numSales", tupdate.getNumSales());
            
        }else{
            update.addBooleanInfo("transaction", false);
        }
        update.addFloatInfo("cashHoldings", tupdate.getCashHoldings());
        update.addIntInfo("securityHoldings", tupdate.getSecurityHoldings());
        update.addStringInfo("actionType", tupdate.getTxnActionType());
        update.addInfo("unitsTraded", tupdate.getNumUnitsInTrade());
        
        update.addIntInfo("period_time", tupdate.getPeriodTime());
        update.addIntInfo("opening_time", tupdate.getOpeningTime());
        update.addInfo("market_time", tupdate.getMarketTime());
        
        update(dbId, update);
        return update; 
    }
    
    /** Dump all updates in the queue of the given client. Typically called after receiving a
     *  re-authentication request, because old updates, collected while the client was
     *  disconnected, are not needed to bring the client back up to speed */
    protected boolean dumpUpdates(int dbId) {
        Integer clientInt = new Integer(dbId);
        
        if (!updateQueues.containsKey(clientInt)) {
            log.warn("Cannot dump updates for client " + dbId + " -- this client is not in any session!");
            return false;
        }
        
        Vector queue = (Vector) updateQueues.get(clientInt);
        updateQueues.put(clientInt, new Vector());
        
        return true;
    }
    
    /** Called by implementation of this class to directly add an update to the queue of the
     *  given client. Returns -1 if the operation failed. Ohterwise returns the new size of
     *  the queue */
    protected int enqueueUpdate(int dbId, Response update) {
        Integer clientInt = new Integer(dbId);
        
        if (!updateQueues.containsKey(clientInt)) {
            log.warn("Cannot enqueue update for client " + dbId + " -- this client is not in any session!");
            return -1;
        }
        
        Vector queue = (Vector) updateQueues.get(clientInt);
        queue.add(update);
        
        return queue.size();
    }
    
    /** Retrieves the next update in the queue of the given client. Returns a NO_UPDATE_RESPONSE if there
     *  are no updates in the given client's queue. The Response that is returned is removed permanently
     *  from the queue */
    protected Response dequeueUpdate(int dbId) {
        Integer clientInt = new Integer(dbId);
        
        if (!updateQueues.containsKey(clientInt)) {
            log.warn("Cannot dequeue update for client " + dbId + " -- this client is not in any session!");
            return new Response(Response.INVALID_SESSION_RESPONSE);
        }
        
        Vector queue = (Vector) updateQueues.get(clientInt);
        
        if (queue.size() < 1)
            return new Response(Response.NO_UPDATE_RESPONSE);
        else {
            Response res = (Response) queue.remove(0);
            return (Response) res;
        }
    }
    
    /** Given an UPDATE_REQUEST from a client, return a response containing the update to send the
     *  client */
    public Response getUpdate(Request req) {
        try {
            if (req.getType() == Request.UPDATE_REQUEST)
                return processUpdateRequest(req);
            else if (req.getType() == Request.RETRY_UPDATE_REQUEST)
                return processRetryRequest(req);
            
        }catch(Exception e) {
            log.error("Failed to process an update request from client", e);
            return new Response(Response.GENERAL_ACK_RESPONSE);
        }
        return new Response(Response.GENERAL_ACK_RESPONSE);
    }
    
    /** Set the given client to connected status */
    protected boolean setConnected(int dbId) {
        try {
            Integer clientInt = new Integer(dbId);
            if (!disconnected.containsKey(clientInt)) {
                log.warn("Cannot update connection status of client " + dbId + " -- client is not connected to any session!");
                return false;
            }
            
            Boolean ds = (Boolean) disconnected.get(clientInt);
            
            if (ds.booleanValue()) {
                disconnected.put(clientInt, Boolean.FALSE);
            }
            
            return true;
        }catch(Exception e) {
            log.error("UpdateServ failed to update admin monitor with client " + dbId + " connection status");
        }
        return false;
    }
    
    /** Set the given client to disconnected status */
    protected boolean setDisconnected(int dbId) {
        try {
            Integer clientInt = new Integer(dbId);
            if (!disconnected.containsKey(clientInt)) {
                log.warn("Cannot update connection status of client " + dbId + " -- client is not connected to any session!");
                return false;
            }
            
            Boolean ds = (Boolean) disconnected.get(clientInt);
            
            if (!ds.booleanValue()) {
                disconnected.put(clientInt, Boolean.TRUE);
            }
            
            return true;
        }catch(Exception e) {
            log.error("UpdateServ failed to update admin monitor with client " + dbId + " connection status");
        }
        return false;
    }
    
    /** Returns the hashtable that maps subject ID to their disconnected status. This is 
     *  requested by the dispatcher upon any re-authentication attempt */
    public Hashtable getDisconnectStatus() {
        return disconnected;
    }
    
    /** This abstract function is called each time an UPDATE_REQUEST is received. HTTP implementation
     *  should fill out this method */
    protected abstract Response processUpdateRequest(Request req);
    
    /** This abstract function is called each a RETRY_UPDATE_REQUEST is received. HTTP implementation
     *  should fill out this method, which returns the update last sent to the client */
    protected abstract Response processRetryRequest(Request req);
    
    /** This abstract function is called each time a client's updateQueue is updated. RMI implementation
     *  should fill out this method */
    protected abstract void processUpdate(int dbId, Response res);
    
    /** Keyed by dbId number, contains Vectors that contain updates ready to be sent to clients. The
     *  UpdateServ is responsible for sending these updates to the clients */
    private Hashtable updateQueues;
    
    /** True if dbId i is disconnected. Set to false whenever dbId i receives an update (because then we
     *  know the client is connected */
    private Hashtable disconnected;
    
    public static Log log = LogFactory.getLog(UpdateServ.class);
}
