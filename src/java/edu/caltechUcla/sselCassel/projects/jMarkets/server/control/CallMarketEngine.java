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
 * ContinuousMarketEngine.java
 *
 * Created on March 21, 2004, 4:37 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import java.util.*;
import java.sql.*;
import java.text.NumberFormat;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.interfaces.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.updates.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SubjectDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.GroupDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.PeriodDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.MarketDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author  Raj Advani
 */
public class CallMarketEngine implements TradeEngine {
    
    private static Log log = LogFactory.getLog(CallMarketEngine.class);
    
    /**
     * Creates a new instance of ContinuousMarketEngine
     */
    public CallMarketEngine(DBWriter dbw) {
        log.info("The CallMarketEngine has been instantiated");
        
        this.dbw = dbw;
        formatter = NumberFormat.getCurrencyInstance();
        rand = new Random();
        marketStates = new Hashtable();
        
        log.info("CallMarketEngine has finished initialization procedures");
    }
    
    /**
     * Initialize the ContinuousMarketEngine to handle the given period. The ContinuousMarketEngine creates a MarketState
     *  object to hold the cached market data of the period. Return the array of Trader objects created
     *  for this period
     */
    public Trader[] initPeriod(int sessionId, int periodNum, PeriodDef periodInfo, float[] initialCash, int[][] initialHoldings) {
        try {
            log.info("CallMarketEngine is initializing a new MarketState object for period " + periodNum + " of session " + sessionId);
            MarketDef marketInfo = periodInfo.getMarketInfo();
            SubjectDef subjectInfo = periodInfo.getSubjectInfo();
            GroupDef groupInfo = periodInfo.getGroupInfo();
            
            MarketState marketState = new MarketState(sessionId, periodNum, marketInfo, subjectInfo, groupInfo, initialCash, initialHoldings);
            marketStates.put(new Integer(sessionId), marketState);
            
            return marketState.getTraders();
        } catch(Exception e) {
            log.error("CallMarketEngine has failed to initialize", e);
        }
        return null;
    }
    
    public Trader[] getTraders(int sessionId) {
        MarketState marketState = (MarketState) marketStates.get(new Integer(sessionId));
        if (marketState == null) {
            log.warn("Cannot retrieve traders for session " + sessionId + " -- that session is not active!");
            return null;
        }
        
        return marketState.getTraders();
    }
    
    public MetricsUpdate[] getMetrics(int sessionId) {
        MarketState marketState = (MarketState) marketStates.get(new Integer(sessionId));
        if (marketState == null) {
            log.warn("Cannot retrieve traders for session " + sessionId + " -- that session is not active!");
            return null;
        }
        
        long[] transTime = marketState.getTransTime();
        int[] numTrans = marketState.getNumTrans();
        
        MetricsUpdate[] updates = new MetricsUpdate[transTime.length];
        for (int i=0; i<updates.length; i++) {
            updates[i] = new MetricsUpdate(sessionId, i, numTrans[i], transTime[i]);
        }
        return updates;
    }
    
    public NumOffersUpdate[] getNumOffers(int sessionId) {
        MarketState marketState = (MarketState) marketStates.get(new Integer(sessionId));
        if (marketState == null) {
            log.warn("Cannot retrieve traders for session " + sessionId + " -- that session is not active!");
            return null;
        }
        
        int[] numOffers = marketState.getNumOffers();
        
        NumOffersUpdate[] updates = new NumOffersUpdate[numOffers.length];
        for (int i=0; i<updates.length; i++) {
            updates[i] = new NumOffersUpdate(sessionId, i, numOffers[i]);
        }
        return updates;
    }
    
    /** Set the current time as the period start time for the given session and period */
    public boolean stampPeriodStartTime(int sessionId) {
        MarketState marketState = (MarketState) marketStates.get(new Integer(sessionId));
        if (marketState == null) {
            log.warn("Cannot set period start time for session " + sessionId + " -- that session is not active!");
            return false;
        }
        
        marketState.setPeriodStartTime(new java.util.Date().getTime());
        return true;
    }
    
    /** Terminate the given session */
    public boolean terminateSession(int sessionId) {
        marketStates.remove(new Integer(sessionId));
        return true;
    }
    
    /**
     *  Calculate the price p in the given market that minimizes the difference between the number of buy
     *  orders >= p and the number of asks <= p. Return -1 if there is no execute price found (should only
     *  occur if there were no bids/asks on that market. If multiple minima are found, pick one at random.
     */
    private int getExecutePrice(int marketId, OfferBook offerBook) {
        int numPrices = offerBook.getNumPrices();
        int difference = 10000;
        Vector executePriceIds = new Vector();
        
        for (int p=0; p<numPrices; p++) {
            int numBids = offerBook.getGreaterThanOrEqualToBidOrders(marketId, p, -1);
            int numAsks = offerBook.getLessThanOrEqualToAskOrders(marketId, p, -1);
            
            int diff = Math.abs(numBids - numAsks);
            if (diff < difference && numBids != 0 && numAsks != 0) {
                difference = diff;
                executePriceIds.removeAllElements();
                executePriceIds.add(new Integer(p));
            } else if (diff == difference && numBids != 0 && numAsks != 0) {
                executePriceIds.add(new Integer(p));
            }
        }
        
        if (executePriceIds.size() > 0) {
            int mid = executePriceIds.size()/2; 
            log.info("Found the following execute price minima on market " + marketId + ": " + executePriceIds.toString() + " -- selecting one at random");
            
            int executePriceId = ((Integer) executePriceIds.get(mid)).intValue();
            float executePrice = offerBook.getPrice(marketId, executePriceId);
            
            log.info("Execute price for market " + marketId + " is: ID=" + executePriceId + ", Value=" + executePrice);
            return executePriceId;
        } else {
            log.info("No possible transactions on market " + marketId + " -- no execute price found");
        }
        
        return -1;
    }
    
    /** The Call Market Engine crosses all offers upon period closing. We cross orders strictly by
     *  time preference after determining the execution price. */
    public UpdateBasket processClosePeriod(int sessionId) {
        Connection conn = null;
        
        try {
            conn = getConnection();
            MarketState marketState = (MarketState) marketStates.get(new Integer(sessionId));
            if (marketState == null) {
                log.warn("Cannot close session " + sessionId + " -- that session does not exist!");
                return null;
            }
            
            Trader[] traders = marketState.getTraders();
            OfferBook offerBook = marketState.getOfferBook();
            
            UpdateBasket basket = new UpdateBasket("Call Order End Period Processing");
            
            for (int m=0; m<offerBook.getNumMarkets(); m++) {
                int executePriceId = getExecutePrice(m, offerBook);
                if (executePriceId == -1)
                    continue;
                
                crossCallMarketOrders(m, executePriceId, offerBook, marketState, conn, basket);
            }
            
            return basket;
        }catch(Exception e) {
            log.error("Failed to close call market", e);
        }finally {
            dbw.closeConnection(conn);
        }
        
        return null;
    }
    
    /** Given a transaction request perform the following:
     *
     *  1) Ensure that the bid/ask is valid
     *  2) Check if the bid/ask results in a transaction
     *  3) If it does, add the transaction to the database
     *  4) Update the offer book
     *
     *  Creates an UpdateBasket object that tracks all the changes made in the course
     *  of this transaction so that it can be successfully rolled back in case something
     *  goes wrong. If the rollback is due to a SQL deadlock, then retry the transaction.
     *
     *  There are also two types of controls here for data integrity:
     *
     *  1) Key Validation ensures that the offer received was verified by a client who
     *     had up-to-date cash and security holding information. Each time the server updates
     *     the holdings of a client, that client's key is changed and the new key is sent
     *     to the client. If a client's key is out of date, that means the client holding
     *     information may be out of date. Orders received with out of date keys are rejected
     *  2) Market locking ensures that any market is only accessed by one transaction at a
     *     time. Orders that are waiting to access the market are kept in a backlog. When
     *     the backlog reaches a critical level, all orders are rejected until the server
     *     "catches up" sufficiently
     *
     *
     *
     */
    public UpdateBasket processOffer(int sessionId, AbstractOffer newOffer, long receivedKey) {
        int subjectId = newOffer.getSubjectId();
        int marketId = newOffer.getMarketId();
        int priceId = newOffer.getPriceId();
        int units = newOffer.getUnits();
        
        MarketState marketState = (MarketState) marketStates.get(new Integer(sessionId));
        if (marketState == null) {
            log.warn("Cannot process trade for session " + sessionId + " -- that session is not active!");
            return null;
        }
        
        int numOffers = incNumOffers(marketState, newOffer.getSubjectId());
        newOffer.setTime(getTimeElapsed(marketState));
        newOffer.setSubjectId_db(marketState.getDatabaseId(subjectId));
        
        String offerCode = "(S: " + subjectId + ", M: " + marketId + ", P: " + priceId + ", U: " + units + ", N: " + numOffers + ", R: " + rand.nextInt(4000) + ")";
        log.debug(offerCode +": Received new offer");
        
        UpdateBasket basket = new UpdateBasket(offerCode);
        basket.addNumOffersUpdate(new NumOffersUpdate(sessionId, subjectId, numOffers));
        
        boolean valid = validateOffer(marketState, newOffer, receivedKey, offerCode);
        
        if (valid) {
            long newKey = (new java.util.Date()).getTime();
            
            marketState.setKey(subjectId, newKey);
            marketState.setKeyCode(subjectId, offerCode);
            
            lockMarket(marketState, marketId, offerCode);
            
            while (!newOffer.isCompleted()) {
                Connection conn = null;
                TransactionState transState = null;
                try {
                    conn = getConnection();

                    transState = new TransactionState(newOffer, offerCode, conn);

                    startTransaction(marketState, transState);
                    log.debug(offerCode +": Transaction started");
                    
                    if (newOffer.getAction() == JMConstants.CANCEL_ACTION)
                        basket = processCancel(marketState, transState, basket);
                    else {
                        basket = submitCallOrder(marketState, transState, basket);
                    }
                    
                    commit(marketState, transState, basket);
                    log.debug(offerCode + ": Transaction committed");
                    
                    newOffer.finishedProcessing();
                    
                } catch(TransactionInterruptedException e) {
                    log.warn("A transaction has been interrupted -- rolling back database and TradeServ");
                    basket = rollback(marketState, transState, basket);
                    newOffer.finishedProcessing();
                    
                    if (e.isDeadlock()) {
                        log.warn("Retrying a deadlocked transaction");
                        return processOffer(sessionId, newOffer, receivedKey);
                    }
                    
                    break;
                }finally{
                    dbw.closeConnection(conn);
                }
            }
            
            unlockMarket(marketState, marketId);
        }
        
        else {
            TradeUpdate invalid = new TradeUpdate(newOffer.getSubjectId_db(), TradeUpdate.INVALID_OFFER_UPDATE);
            invalid.setErrorMsg(newOffer.getInvalidMessage());
            basket.addTradeUpdate(invalid);
        }
        
        return basket;
    }
    
    /** Log a detailed description of a newly received offer */
    private void logOffer(MarketState marketState, AbstractOffer newOffer) {
        String good = marketState.getMarketInfo().getMarketTitles()[newOffer.getMarketId()];
        newOffer.setPrice(marketState.getMarketInfo().getPrices()[newOffer.getMarketId()][newOffer.getPriceId()]);
        
        if (newOffer.getAction() == JMConstants.BUY_ACTION)
            log.debug("Received bid offer at period time " + newOffer.getTime() + " by client ID: " + newOffer.getSubjectId() + " for " + newOffer.getUnits() + " units of good '" + good + "' at price: " + formatter.format(newOffer.getPrice()));
        if (newOffer.getAction() == JMConstants.SELL_ACTION)
            log.debug("Received ask offer at period time " + newOffer.getTime() + " by client ID: " + newOffer.getSubjectId() + " for " + newOffer.getUnits() + " units of good '" + good + "' at price: " + formatter.format(newOffer.getPrice()));
        if (newOffer.getAction() == JMConstants.CANCEL_ACTION)
            log.debug("Received cancel action at period time " + newOffer.getTime() + " buy client ID: " + newOffer.getSubjectId() + " for " + newOffer.getUnits() + " units of good '" + good + "' at price: " + formatter.format(newOffer.getPrice()));
    }
    
    /** Given a valid cancel order, go through each offer that is subject to cancellation by this order.
     *  Cancel all offers by this subject at this price level until the cancel order is exhausted. If an
     *  order must be cut in half (i.e. half its units are canceled) then just update the order with the
     *  new amount of units */
    private UpdateBasket processCancel(MarketState marketState, TransactionState transState, UpdateBasket basket) throws TransactionInterruptedException {
        Connection conn = null; 
        Object [] results = null;
        try {
            MarketDef marketInfo = marketState.getMarketInfo();
            SubjectDef subjectInfo = marketState.getSubjectInfo();
            AbstractOffer offer = transState.getOffer();
            conn = transState.getConn();
            
            String good = marketInfo.getMarketTitles()[offer.getMarketId()];
            results = dbw.getOffersForCancel(offer.getSubjectId_db(), offer.getMarketId(), offer.getPriceId(), marketInfo, conn);
            
            //Go through each of the cancelable offers
            while (true) {
                AbstractOffer standingOffer = dbw.getNextOffer(offer.getMarketId(), results, subjectInfo, marketInfo, conn);
                if (standingOffer == null)
                    break;
                
                int unitsCanceled = executeCancel(marketState, transState, basket, standingOffer);
                offer.setUnits(offer.getUnits() - unitsCanceled);
                
                if (offer.getUnits() <= 0)
                    break;
            }
            
            transState.setIterations(-1);
            return basket;
            
        } catch(Exception e) {
            log.error("Failed to check for cancellations upon receipt of cancel order", e);
            throw new TransactionInterruptedException();
        }finally{
             dbw.closeOffers(results);
        }
    }
    
    /** Add the given order to the offer book. Then go through the other orders on the market to figure
     *  out if the order should be color-coded as a buy or sell. Update the client accordingly */
    private UpdateBasket submitCallOrder(MarketState marketState, TransactionState transState, UpdateBasket basket) throws TransactionInterruptedException {
        try {
            writeOffer(marketState, transState, basket);
            return basket;
        }catch(Exception e) {
            log.error("Failed to submit a call market order", e);
            throw new TransactionInterruptedException();
        }
    }
    
    /** Given bid/ask information, check the offer book to see if any transaction can be made */
    private UpdateBasket checkForTransaction(MarketState marketState, TransactionState transState, UpdateBasket basket, int executePriceId, float executePrice) throws TransactionInterruptedException {
        try {
            int sessionId = marketState.getSessionId();
            int periodNum = marketState.getPeriodNum();
            MarketDef marketInfo = marketState.getMarketInfo();
            AbstractOffer newOffer = transState.getOffer();
            Connection conn = transState.getConn();
            Object[] results = null;
            
            if (newOffer.getAction() == JMConstants.BUY_ACTION)
                results = dbw.getAskOffersOrderedByTime(sessionId, periodNum, newOffer.getMarketId(), executePriceId, executePrice, marketInfo, conn);
            if (newOffer.getAction() == JMConstants.SELL_ACTION)
                results = dbw.getBidOffersOrderedByTime(sessionId, periodNum, newOffer.getMarketId(), executePriceId, executePrice, marketInfo, conn);
            
            return processTransactions(marketState, transState, results, basket, executePriceId, executePrice);
        } catch(Exception e) {
            log.error("Failed to check for transactions upon receipt of new order", e);
            throw new TransactionInterruptedException();
        }
    }
    
    /** Given the execution price transact the standing orders in the given market at that
     *  price. Execute the oldest orders first. */
    private UpdateBasket crossCallMarketOrders(int marketId, int executePriceId, OfferBook offerBook, MarketState marketState, Connection conn, UpdateBasket basket) throws TransactionInterruptedException {
        int sessionId = marketState.getSessionId();
        int periodNum = marketState.getPeriodNum();
        float executePrice = offerBook.getPrice(marketId, executePriceId);
        MarketDef marketDef = marketState.getMarketInfo();
        SubjectDef subjectDef = marketState.getSubjectInfo();
        
        int numBids = offerBook.getGreaterThanOrEqualToBidOrders(marketId, executePriceId, -1);
        int numAsks = offerBook.getLessThanOrEqualToAskOrders(marketId, executePriceId, -1);
        
        Object[] largeSet = null;
        
        if (numBids >= numAsks) {
            largeSet = dbw.getBidOffersOrderedByTime(sessionId, periodNum, marketId, executePriceId, executePrice, marketDef, conn);
        } else {
            largeSet = dbw.getAskOffersOrderedByTime(sessionId, periodNum, marketId, executePriceId, executePrice, marketDef, conn);
        }
        
        while (true) {
            AbstractOffer offer = dbw.getNextOffer(marketId, largeSet, subjectDef, marketDef, conn);
            if (offer == null)
                break;
            
            String offerCode = "(S: " + offer.getSubjectId() + ", M: " + marketId + ", P: " + offer.getPriceId() + ", U: " + offer.getUnits()+ ")";
            
            TransactionState transState = new TransactionState(offer, offerCode, conn);
            basket = checkForTransaction(marketState, transState, basket, executePriceId, executePrice);
        }
        
        return basket;
    }
    
    /** Iterate through the list of possible transactions contained in the ResultSet and
     *  process each one of them. Return an UpdateBasket object containing the updates that
     *  must be sent to the client upon commit. This object also contains the number of iterations
     *  processed in the trade (that is, the number of interactions between offers) */
    private UpdateBasket processTransactions(MarketState marketState, TransactionState transState, Object[] results, UpdateBasket basket, int executePriceId, float executePrice) throws TransactionInterruptedException {
        Connection conn = null;
        try {
            OfferBook offerBook = marketState.getOfferBook();
            MarketDef marketInfo = marketState.getMarketInfo();
            SubjectDef subjectInfo = marketState.getSubjectInfo();
            
            AbstractOffer newOffer = transState.getOffer();
            conn = transState.getConn();
            
            int iterations = 0;
            
            int unitsTransacted = 0;
            int transId = -1;
            int marketOrderId = -1;
            
            //Go through each of standing offers with which a transaction can be made with this new offer,
            //in order of time entry
            while (true) {
                AbstractOffer standingOffer = dbw.getNextOffer(newOffer.getMarketId(), results, subjectInfo, marketInfo, conn);
                if (standingOffer == null)
                    break;
                else if (transId == -1)
                    transId = openTransaction(newOffer.getTime(), conn);
                
                if(iterations == 0)
                    marketOrderId = writeMarketOrder(marketState, newOffer, conn);
                
                int unitsTraded = executeTrade(marketState, transState, basket, standingOffer, transId, executePriceId, executePrice);
                
                newOffer.setUnits(newOffer.getUnits() - unitsTraded);
                unitsTransacted += unitsTraded;
                
                iterations++;
                if (newOffer.getUnits() <= 0)
                    break;
            }
            
            //If a transaction was made with the new offer, add it to the market_orders table and close it
            if (transId != -1) {
                executeTrade(transId, marketOrderId, unitsTransacted, newOffer.getAction(), transState);
                closeTransaction(transId, unitsTransacted, executePrice, conn);
            }
            
            
            //If there are units remaining in the new offer then return from here so that the next new offer (from the large set, asks or bids) can
            //be put through this same checkForTransaction/processTransaction process. However, if there are units remaining in this offer that means
            //all the orders on the small list are exhausted, so all succeeding new offers will cross with nothing. So if newOffer units > 0 then
            //the entire call market transaction process is finished
            //if (newOffer.getUnits() > 0)
            //    writeOffer(marketState, transState, basket);
            
            //TradeUpdate tupdate = createSinglePostUpdate(marketState, newOffer.getSubjectId(), JMConstants.VISUAL_EXECUTE_ACTION, newOffer, newOffer.getTime(), offerBook.getPriceBook(newOffer.getMarketId(), newOffer.getPriceId()), transState.getCode());
            //basket.addTradeUpdate(tupdate);
            
            transState.setIterations(iterations);
            
            return basket;
            
        } catch(Exception e) {
            log.error("Failed to check for transactions upon receipt of new order", e);
            throw new TransactionInterruptedException();
        }finally{
             dbw.closeOffers(results);
        }
    }
    
    /** Write the given market order into the database */
    private int writeMarketOrder(MarketState marketState, AbstractOffer offer, Connection conn) throws TransactionInterruptedException {
        return dbw.writeOffer(offer.getSubjectId_db(), offer.getMarketId(), offer.getPriceId(), offer.getAction(), offer.getUnits(), JMConstants.ORDER_META_MARKET, offer.getTime(), marketState.getMarketInfo(), conn);
    }
    
    
    /** Write the given offer into the database, the server offerbook, and the client offerbooks. Return
     *  the number of units posted to the offer book */
    private int writeOffer(MarketState marketState, TransactionState transState, UpdateBasket basket) throws TransactionInterruptedException {
        OfferBook offerBook = marketState.getOfferBook();
        AbstractOffer offer = transState.getOffer();
        Connection conn = transState.getConn();
        UpdateInvert uinvert = null;
        
        if (offer.getAction() == JMConstants.BUY_ACTION)
            uinvert = offerBook.insertBuy(offer.getMarketId(), offer.getPriceId(), offer.getSubjectId(), offer.getUnits());
        else if (offer.getAction() == JMConstants.SELL_ACTION)
            uinvert = offerBook.insertSell(offer.getMarketId(), offer.getPriceId(), offer.getSubjectId(), offer.getUnits());
        
        log.debug(transState.getCode() + ": Server-side offer book updated: " + offerBook.getState(offer.getMarketId(), offer.getPriceId(), offer.getSubjectId()));
        transState.addUpdateInvert(uinvert);
        
        dbw.writeOffer(offer.getSubjectId_db(), offer.getMarketId(), offer.getPriceId(), offer.getAction(), offer.getUnits(), JMConstants.ORDER_META_LIMIT, offer.getTime(), marketState.getMarketInfo(), conn);
        
        TradeUpdate[] tupdates = createPostUpdates(marketState, offer.getAction(), offer, offer.getTime(), transState.getCode());
        for (int i=0; i<tupdates.length; i++)
            basket.addTradeUpdate(tupdates[i]);
        
        log.debug(transState.getCode() + ": Remaining non-traded " + offer.getUnits() + " unit(s) posted to offer book");
        return offer.getUnits();
    }
    
    /** Given a cancelOffer (which contains just the number of units to cancel and the time received) and a
     *  standing offer, cancel the designated number of units from the standing offer. If the standing offer
     *  still has units remaining, then just update in the database offerbook instead of setting it to
     *  canceled status. Finally, update the server offerbook and the client offerbooks */
    private int executeCancel(MarketState marketState, TransactionState transState, UpdateBasket basket, AbstractOffer standingOffer) throws TransactionInterruptedException {
        OfferBook offerBook = marketState.getOfferBook();
        AbstractOffer cancelOffer = transState.getOffer();
        Connection conn = transState.getConn();
        
        int unitsCanceled = Math.min(cancelOffer.getUnits(), standingOffer.getUnits());
        boolean canceled = unitsCanceled == standingOffer.getUnits();
        
        UpdateInvert uinvert = offerBook.insertCancel(standingOffer.getMarketId(), standingOffer.getPriceId(), standingOffer.getSubjectId(), unitsCanceled);
        log.debug(transState.getCode() + ": Server-side offer book updated: " + offerBook.getState(standingOffer.getMarketId(), standingOffer.getPriceId(), standingOffer.getSubjectId()));
        transState.addUpdateInvert(uinvert);
        
        if (canceled)
            dbw.cancelOffer(standingOffer.getId_db(), cancelOffer.getTime(), unitsCanceled, conn);
        else
            dbw.updateOffer(standingOffer.getId_db(), standingOffer.getUnits() - unitsCanceled, cancelOffer.getTime(), unitsCanceled, JMConstants.ORDER_CANCELLED, conn);
        
        TradeUpdate[] tupdates = createPostUpdates(marketState, JMConstants.CANCEL_ACTION, standingOffer, cancelOffer.getTime(), transState.getCode());
        for (int i=0; i<tupdates.length; i++)
            basket.addTradeUpdate(tupdates[i]);
        
        log.debug(transState.getCode() + ": Completed cancellation of " + unitsCanceled + " units at price " + formatter.format(standingOffer.getPrice()) + " for player " + standingOffer.getSubjectId());
        return unitsCanceled;
    }
    
    private void executeTrade(int transId, int marketOrderId, int units, int orderType, TransactionState transState) throws TransactionInterruptedException {
        Connection conn = transState.getConn();
        dbw.writeTrade(transId, marketOrderId, units, orderType, conn);
    }
    
    /** Given two offers that are eligible for a trade, and the transaction id that they belong to, execute the trade.
     *  First calculate whether the standing offer will have units remaining after being transacted with the
     *  new offer. If it will, then update it in the database offerbook instead of setting it to executed status. Then
     *  construct a trade object -- which encapsulates the pre- and post- cash and security holdings for each client,
     *  and update the database with the transaction information. Also update the server offerbook, send trade updates
     *  to update the client offerbooks, and update the price chart */
    private int executeTrade(MarketState marketState, TransactionState transState, UpdateBasket basket, AbstractOffer standingOffer, int transId, int executePriceId, float executePrice) throws TransactionInterruptedException {
        OfferBook offerBook = marketState.getOfferBook();
        MarketDef marketInfo = marketState.getMarketInfo();
        Trader[] traders = marketState.getTraders();
        
        int sessionId = marketState.getSessionId();
        int periodNum = marketState.getPeriodNum();
        
        AbstractOffer newOffer = transState.getOffer();
        Connection conn = transState.getConn();
        
        int unitsReduced = Math.min(standingOffer.getUnits(), newOffer.getUnits());
        boolean executed = unitsReduced == standingOffer.getUnits();
        
        UpdateInvert uinvert = offerBook.insertCancel(standingOffer.getMarketId(), standingOffer.getPriceId(), standingOffer.getSubjectId(), unitsReduced);
        log.debug(transState.getCode() + ": Server-side offer book updated: " + offerBook.getState(standingOffer.getMarketId(), standingOffer.getPriceId(), standingOffer.getSubjectId()));
        transState.addUpdateInvert(uinvert);
        
        if (executed)
            dbw.executeOffer(standingOffer.getId_db(), newOffer.getTime(), unitsReduced, conn);
        else
            dbw.updateOffer(standingOffer.getId_db(), standingOffer.getUnits() - unitsReduced, newOffer.getTime(), unitsReduced, JMConstants.ORDER_TRANSACTED, conn);
        
        Trade trade = new Trade(standingOffer, newOffer, unitsReduced, transId);
        trade.setPreAskCash(traders[trade.getAskParty()].getCash());
        trade.setPreBidCash(traders[trade.getBidParty()].getCash());
        trade.setPreAskSec(traders[trade.getAskParty()].getHoldings(standingOffer.getMarketId()));
        trade.setPreBidSec(traders[trade.getBidParty()].getHoldings(standingOffer.getMarketId()));
        
        log.debug(transState.getCode() + ": Starting trade execution with (Bidder: " + trade.getBidParty() + ", Units: " + trade.getPreBidSec() + ") and (Seller: " + trade.getAskParty() + ", Units: " + trade.getPreAskSec() +")");
        
        trade = dbw.writeTrade(sessionId, periodNum, trade, marketInfo, executePriceId, executePrice, conn);
        
        log.debug(transState.getCode() + ": Finished trade execution with (Bidder: " + trade.getBidParty() + ", Units: " + trade.getPostBidSec() + ") and (Seller: " + trade.getAskParty() + ", Units: " + trade.getPostAskSec() +")");
        
        updateTraderData(traders, trade);
        TradeUpdate[] tupdates = createTradeUpdates(marketState, executePriceId, trade, transState.getCode());
        for (int i=0; i<tupdates.length; i++)
            basket.addTradeUpdate(tupdates[i]);
        
        String good = marketInfo.getMarketTitles()[newOffer.getMarketId()];
        PriceChartUpdate pupdate = updatePriceChart(marketState, good, newOffer.getTime(), standingOffer.getPrice());
        basket.addPriceChartUpdate(pupdate);
        
        log.debug(transState.getCode() + ": Completed transaction of " + unitsReduced + " units at price " + formatter.format(executePrice) + " between players " + newOffer.getSubjectId() + " and " + standingOffer.getSubjectId());
        return unitsReduced;
    }
    
    /** Validate the offer by checking if the key sent by the client is up to date */
    private boolean validateOffer(MarketState marketState, AbstractOffer offer, long key, String code) {
        long[] keys = marketState.getKeys();
        String[] keyCodes = marketState.getKeyCodes();
        
        if (backlogRejection) {
            offer.setInvalidMessage("Order canceled because server is busy -- please wait a few seconds");
            log.debug("TradeServ rejected offer, code " + code + ", because of excessive offer backlog");
            
            return false;
        }
        
        else if (keys[offer.getSubjectId()] != key) {
            offer.setInvalidMessage("Order canceled because you are currently involved in a transaction");
            log.debug("TradeServ rejected offer, code " + code + ", because of antiquated key: last updated key-code: " + keyCodes[offer.getSubjectId()] +", client key: " + key + ", server key: " + keys[offer.getSubjectId()]);
            
            return false;
        }
        
        return true;
    }
    
    /** Get a transaction ID for the given offer. This is called when we know that a new offer has intersected
     *  with a standing offer such that at lesat one transaction will occur. All exchanges that occur with this
     *  new offer will be assigned this same transaction ID */
    private int openTransaction(long time, Connection conn) throws TransactionInterruptedException {
        return dbw.openTransaction(time, conn);
    }
    
    /** Close the given transaction, filling in the number of units in total transacted */
    private void closeTransaction(int transId, int units, float txnPrice, Connection conn) throws TransactionInterruptedException {
        dbw.closeTransaction(transId, units, txnPrice, conn);
    }
    
    /** Update the traders with the given transaction information */
    private void updateTraderData(Trader[] traders, Trade trade) {
        traders[trade.getBidParty()].setHoldings(trade.getMarketId(), trade.getPostBidSec());
        traders[trade.getBidParty()].setCash(trade.getPostBidCash());
        traders[trade.getBidParty()].incNumPurchases(trade.getMarketId(), trade.getAskParty(), trade.getUnitsTraded());
        
        traders[trade.getAskParty()].setHoldings(trade.getMarketId(), trade.getPostAskSec());
        traders[trade.getAskParty()].setCash(trade.getPostAskCash());
        traders[trade.getAskParty()].incNumSales(trade.getMarketId(), trade.getBidParty(), trade.getUnitsTraded());
    }
    
    /** Update the server-side copy of the price chart with the given transaction */
    private PriceChartUpdate updatePriceChart(MarketState marketState, String security, float time, float price) {
        try {
            int sessionId = marketState.getSessionId();
            PriceChart chart = marketState.getChart();
            chart.addPoint(security, time / 1000, price);
            
            PriceChartUpdate pupdate = new PriceChartUpdate(sessionId, security, time / 1000, price);
            return pupdate;
        } catch(Exception e) {
            log.error("Failed to update the price chart in the TradeServ", e);
        }
        return null;
    }
    
    /** Reset the given client's key to zero */
    public void resetClientKey(int sessionId, int subjectId) {
        MarketState marketState = (MarketState) marketStates.get(new Integer(sessionId));
        if (marketState == null) {
            log.error("Failed to reset client key for session " + sessionId + " -- that session does not exist!");
            return;
        }
        
        marketState.setKey(subjectId, 0);
    }
    
    /** Generate the client-side offer-book for the given client using the server-side offer-book */
    public OfferBook generateClientBook(int sessionId) {
        MarketState marketState = (MarketState) marketStates.get(new Integer(sessionId));
        if (marketState == null) {
            log.error("Failed to generate TradeServ information for session " + sessionId + " -- that session does not exist!");
            return null;
        }
        
        return marketState.getOfferBook();
    }
    
    /** Generate the client-side holdings array for the given client */
    public int[] generateClientHoldings(int sessionId, int subjectId) {
        MarketState marketState = (MarketState) marketStates.get(new Integer(sessionId));
        if (marketState == null) {
            log.error("Failed to generate TradeServ information for session " + sessionId + " -- that session does not exist!");
            return null;
        }
        
        return marketState.getTraders()[subjectId].getHoldings();
    }
    
    /** Generate the cash holdings of the given client */
    public float generateClientCash(int sessionId, int subjectId) {
        MarketState marketState = (MarketState) marketStates.get(new Integer(sessionId));
        if (marketState == null) {
            log.error("Failed to generate TradeServ information for session " + sessionId + " -- that session does not exist!");
            return 0;
        }
        
        return marketState.getTraders()[subjectId].getCash();
    }
    
    /** Generate the securities view of the price chart for the given session */
    public Vector getPriceChartView(int sessionId) {
        MarketState marketState = (MarketState) marketStates.get(new Integer(sessionId));
        if (marketState == null) {
            log.info("No market information generated for new admin monitor in session " + sessionId + " -- no market exists yet for the session");
            return null;
        }
        
        return marketState.getChartSecurities();
    }
    
    /** Get a database connection from the pool */
    private Connection getConnection() {
        return dbw.getConnection();
    }
    
    /**  Lock the market being used by this transaction, first waiting for it to become
     *  available. Implements a threading queue (FIFO) so that orders are processed in the
     *  order received. This is accomplished by adding each thread to the queue the FIRST
     *  time they attempt to get the monitor (the IF time). Each subsequent time (the WHILE
     *  attempts) we will check to see if they are first in the queue; if not, they wait
     *  again as the queue is updated */
    private synchronized void lockMarket(MarketState marketState, int marketId, String offerCode) {
        try {
            Thread offerThr = Thread.currentThread();
            boolean[] marketLocks = marketState.getMarketLocks();
            Vector[] threadQueue = marketState.getThreadQueue();
            
            if (marketLocks[marketId]) {
                threadQueue[marketId].add(offerThr);
                
                while (marketLocks[marketId] || threadQueue[marketId].get(0) != offerThr) {
                    if (marketLocks[marketId])
                        log.debug(offerCode + ": Market " + marketId + " locked -- waiting for release at position " + threadQueue[marketId].indexOf(offerThr) + " in queue...");
                    wait();
                }
                
                threadQueue[marketId].remove(offerThr);
            }
            
            marketLocks[marketId] = true;
            log.debug(offerCode + ": Locked market");
        } catch(Exception e) {
            log.error("Failed to lock market " + marketId, e);
        }
    }
    
    /** Unlock the hold on the given market so that other threads can access it */
    private synchronized void unlockMarket(MarketState marketState, int marketId) {
        boolean[] marketLocks = marketState.getMarketLocks();
        
        marketLocks[marketId] = false;
        notifyAll();
    }
    
    /** Start a database transaction. Starts storing the client-side updates in a
     *  UpdateBasket, for release upon commit. Stamp the current time so that
     *  when committed we can see how long the transaction took to complete. Also
     *  stamp the current holdings of each client so that these can be recalled if
     *  rollback occurs */
    private TransactionState startTransaction(MarketState marketState, TransactionState transState) {
        try {
            Trader[] traders = marketState.getTraders();
            
            Connection conn = transState.getConn();
            int marketId = transState.getMarketId();
            int numClients = marketState.getNumClients();
            
            dbw.startTransaction(conn);
            
            transState.stampStartTime();
            
            float[] priorCashHoldings = new float[numClients];
            for (int i=0; i<priorCashHoldings.length; i++)
                priorCashHoldings[i] = traders[i].getCash();
            
            int[] priorSecHoldings = new int[numClients];
            for (int i=0; i<priorSecHoldings.length; i++)
                priorSecHoldings[i] = traders[i].getHoldings(marketId);
            
            int[][] priorPurchases = new int[numClients][numClients];
            for (int i=0; i<priorPurchases.length; i++)
                priorPurchases[i] = traders[i].getNumPurchases(marketId);
            
            int[][] priorSales = new int[numClients][numClients];
            for (int i=0; i<priorSales.length; i++)
                priorSales[i] = traders[i].getNumSales(marketId);
            
            transState.stampHoldings(priorCashHoldings, priorSecHoldings);
            transState.setPriorPurchases(priorPurchases);
            transState.setPriorSales(priorSales);
            
            return transState;
        } catch(Exception e) {
            log.error("Failed to start transaction", e);
            return transState;
        }
    }
    
    /** Commit the current database transaction. Sends the stored updates to the
     *  clients and record the time elapsed during the transaction. Release the lock
     *  on the market used by this transaction */
    private void commit(MarketState marketState, TransactionState transState, UpdateBasket basket) throws TransactionInterruptedException {
        String offerCode = transState.getCode();
        Connection conn = transState.getConn();
        
        log.debug(offerCode + ": Writing transaction to database...");
        
        dbw.commit(conn);
        
        log.debug(offerCode + ": Finished writing");
        
        transState.stampEndTime();
        
        MetricsUpdate mupdate = updateMetrics(marketState, transState);
        if (mupdate != null)
            basket.addMetricsUpdate(mupdate);
        
        OfferBacklogUpdate oupdate = updateBacklog();
        if (oupdate != null)
            basket.addOfferBacklogUpdate(oupdate);
    }
    
    /** Rollback all changes made in the current transaction. Dump the updates that
     *  were going to sent to the clients, rollback the server-side offerbook, rollback
     *  the database, and rollback the server-side holdings. Return the rolled back
     *  Update Basket */
    private UpdateBasket rollback(MarketState marketState, TransactionState transState, UpdateBasket basket) {
        OfferBook offerBook = marketState.getOfferBook();
        int subjectId_db = transState.getOffer().getSubjectId_db();
        int marketId = transState.getMarketId();
        Connection conn = transState.getConn();
        
        basket.getTradeUpdates().clear();
        basket.getMetricsUpdates().clear();
        basket.getNumOffersUpdates().clear();
        basket.getPriceChartUpdates().clear();
        basket.getTradeUpdates().clear();
        
        TradeUpdate invalid = new TradeUpdate(subjectId_db, TradeUpdate.INVALID_OFFER_UPDATE);
        invalid.setErrorMsg("Transaction failed for unknown reason -- please try again");
        basket.addTradeUpdate(invalid);
        
        Vector inverts = transState.getUpdateInverts();
        for (int i=0; i<inverts.size(); i++) {
            UpdateInvert invert = (UpdateInvert) inverts.get(i);
            offerBook.invertUpdate(invert);
            log.debug(transState.getCode() + ": Server-side offer book ROLLED BACK: " + offerBook.getState(invert.market, invert.price, invert.subject));
        }
        
        Trader[] traders = marketState.getTraders();
        float[] priorCashHoldings = transState.getPriorCashHoldings();
        int[] priorSecHoldings = transState.getPriorSecHoldings();
        int[][] priorPurchases = transState.getPriorPurchases();
        int[][] priorSales = transState.getPriorSales();
        
        for (int i=0; i<traders.length; i++) {
            traders[i].setCash(priorCashHoldings[i]);
            traders[i].setHoldings(marketId, priorSecHoldings[i]);
            traders[i].setNumPurchases(marketId, priorPurchases[i]);
            traders[i].setNumSales(marketId, priorSales[i]);
        }
        dbw.rollback(conn);
        
        return basket;
    }
    
    /** Given the number of iterations of a transaction, return a MetricsUpdate object
     *  with the new average transaction times */
    private MetricsUpdate updateMetrics(MarketState marketState, TransactionState transState) {
        int iterations = transState.getIterations();
        long time = transState.getTimeElapsed();
        int[] numTrans = marketState.getNumTrans();
        long[] transTime = marketState.getTransTime();
        
        if (iterations >= MAX_RECORDED_ITERATIONS || iterations < 0)
            return null;
        
        numTrans[iterations]++;
        transTime[iterations] += time;
        
        float average = transTime[iterations] / numTrans[iterations];
        log.debug("The average transaction time for " + iterations + " iterations is " + average);
        
        MetricsUpdate update = new MetricsUpdate(marketState.getSessionId(), iterations, numTrans[iterations], average);
        return update;
    }
    
    /** Increment the number of offers attempted by the given client, and add those to
     *  the numOffersUpdates array */
    private int incNumOffers(MarketState marketState, int client) {
        return marketState.incNumOffers(client);
    }
    
    /** Return an OfferBacklogUpdate that contains the new number of offers in the offer backlog.
     *  If the number of offers in the backlog exceeds BACKLOG_MAX_LEVEL, then reject all
     *  incoming offers until the number drops to BACKLOG_RESTART_LEVEL. This rejection is accomplished
     *  in the validate function */
    private OfferBacklogUpdate updateBacklog() {
        int backlog = 0;
        
        Enumeration activeMarkets = marketStates.elements();
        while (activeMarkets.hasMoreElements()) {
            MarketState marketState = (MarketState) activeMarkets.nextElement();
            Vector[] threadQueue = marketState.getThreadQueue();
            
            for (int i=0; i<threadQueue.length; i++)
                backlog += threadQueue[i].size();
        }
        
        if (backlog > JMConstants.BACKLOG_MAX_LEVEL) {
            if (!backlogRejection)
                log.info("Server backlog reached critical level " + backlog + "; rejecting all offers until backlog reaches " + JMConstants.BACKLOG_RESTART_LEVEL);
            backlogRejection = true;
        } else if (backlog <= JMConstants.BACKLOG_RESTART_LEVEL) {
            if (backlogRejection)
                log.info("Server has reached backlog level " + backlog + "; now accepting offers");
            backlogRejection = false;
        }
        
        OfferBacklogUpdate update = new OfferBacklogUpdate(backlog, backlogRejection);
        return update;
    }
    
    /** Gets the time that has elapsed since the last period started, in milliseconds */
    private long getTimeElapsed(MarketState marketState) {
        java.util.Date date = new java.util.Date();
        return date.getTime() - marketState.getPeriodStartTime();
    }
    
    /** Create OFFER_BOOK_UPDATES for every player in response to the given trade. The update contains
     *  a pricebook array that contains the updated data on bids and asks for the market and price level
     *  on which the given trade took place. The EXECUTE action tells the client to perform a gold flickering.
     *  For clients involved in the trade, updated cash and security holdings are sent */
    private TradeUpdate[] createTradeUpdates(MarketState marketState, int executePriceId, Trade trade, String code) {
        OfferBook offerBook = marketState.getOfferBook();
        Trader[] traders = marketState.getTraders();
        
        int bidParty = trade.getBidParty();
        int askParty = trade.getAskParty();
        
        int action = JMConstants.EXECUTE_ACTION;
        long time = trade.getNewOffer().getTime();
        int marketId = trade.getMarketId();
        int priceId = trade.getStandingOffer().getPriceId();
        int[] priceBook = offerBook.getPriceBook(marketId, executePriceId);
        
        int periodNum = marketState.getPeriodNum();
        int numClients = marketState.getNumClients();
        long[] keys = marketState.getKeys();
        
        TradeUpdate[] tupdates = new TradeUpdate[numClients*2];
        
        //process bid side first
        for (int i=0; i<numClients; i++) {
            int dbId = marketState.getDatabaseId(i);
            
            TradeUpdate tupdate = new TradeUpdate(dbId, TradeUpdate.NON_TRANSACTION_UPDATE);
            
            tupdate.setAction(action);
            tupdate.setMarketId(marketId);
            tupdate.setExecutedPriceId(executePriceId);
            if(bidParty == trade.getStandingOffer().getSubjectId()){
                tupdate.setStandingPriceId(priceId); 
                tupdate.setTime(trade.getStandingOffer().getTime());
            }else{
                tupdate.setStandingPriceId(trade.getNewOffer().getPriceId()); 
                tupdate.setTime(time);
            }
            tupdate.setKey(keys[i]);
            tupdate.setCode(code);
            tupdate.setPriceBook(priceBook);
            tupdate.setPeriodNum(periodNum);
            tupdate.setNumUnitsInTrade(trade.getUnitsTraded());
            tupdate.setTxnActionType("Buy");
            
            if (i == bidParty) {
                tupdate.setType(TradeUpdate.TRANSACTION_UPDATE);
                tupdate.setCashHoldings(trade.getPostBidCash());
                tupdate.setSecurityHoldings(trade.getPostBidSec());

                tupdate.setNumPurchases(traders[i].getNumPurchases()[marketId]);
                tupdate.setNumSales(traders[i].getNumSales()[marketId]);
            }
            tupdates[i] = tupdate;
        }
        
        for (int i=0; i<numClients; i++) {
            int dbId = marketState.getDatabaseId(i);
            
            TradeUpdate tupdate = new TradeUpdate(dbId, TradeUpdate.NON_TRANSACTION_UPDATE);
            
            tupdate.setAction(action);
            tupdate.setMarketId(marketId);
            tupdate.setExecutedPriceId(executePriceId);
            if(askParty == trade.getStandingOffer().getSubjectId()){
                tupdate.setStandingPriceId(priceId);
                tupdate.setTime(trade.getStandingOffer().getTime());
            }else{
                tupdate.setStandingPriceId(trade.getNewOffer().getPriceId());
                tupdate.setTime(time);
            }
            tupdate.setKey(keys[i]);
            tupdate.setCode(code);
            tupdate.setPriceBook(priceBook);
            tupdate.setPeriodNum(periodNum);
            tupdate.setNumUnitsInTrade(trade.getUnitsTraded());
            tupdate.setTxnActionType("Sell");
            
            if (i == askParty) {
                tupdate.setType(TradeUpdate.TRANSACTION_UPDATE);
                tupdate.setCashHoldings(trade.getPostAskCash());
                tupdate.setSecurityHoldings(trade.getPostAskSec());
                tupdate.setNumPurchases(traders[i].getNumPurchases()[marketId]);
                tupdate.setNumSales(traders[i].getNumSales()[marketId]);
            }
            
            tupdates[i+numClients] = tupdate;
        }
        
        return tupdates;
    }
    
    /** Create an OFFER_BOOK_UPDATE for every player. This will tell each player the total number
     *  of asks and bids on the given price level specified by the marketId and priceId. In other words,
     *  tell each client to update their offerbooks with the given offer. Store the update in a
     *  TradeUpdate object so that it will be sent to the clients upon commit */
    private TradeUpdate[] createPostUpdates(MarketState marketState, int action, AbstractOffer offer, long time, String code) {
        int numClients = marketState.getNumClients();
        OfferBook offerBook = marketState.getOfferBook();
        
        TradeUpdate[] tupdates = new TradeUpdate[numClients];
        int[] priceBook = offerBook.getPriceBook(offer.getMarketId(), offer.getPriceId());
        
        for (int i=0; i<numClients; i++) {
            tupdates[i] = createSinglePostUpdate(marketState, i, action, offer, time, priceBook, code);
        }
        
        return tupdates;
    }
    
    /** Create an update to send the given client telling her to post the given offer to her offerbook.
     *  See comments under createPostUpdate above */
    private TradeUpdate createSinglePostUpdate(MarketState marketState, int subjectId, int action, AbstractOffer offer, long time, int[] priceBook, String code) {
        int marketId = offer.getMarketId();
        int priceId = offer.getPriceId();
        long[] keys = marketState.getKeys();
        int periodNum = marketState.getPeriodNum();
        int subjectId_db = marketState.getDatabaseId(subjectId);
        
        TradeUpdate tupdate = new TradeUpdate(subjectId_db, TradeUpdate.NON_TRANSACTION_UPDATE);
        
        tupdate.setAction(action);
        tupdate.setMarketId(marketId);
        tupdate.setExecutedPriceId(priceId);
        tupdate.setStandingPriceId(priceId); 
        tupdate.setTime(time);
        tupdate.setKey(keys[subjectId]);
        tupdate.setCode(code);
        tupdate.setPriceBook(priceBook);
        tupdate.setPeriodNum(periodNum);
        
        return tupdate;
    }
    
    /** Database access writer */
    private DBWriter dbw;
    
    /** Contains the market state objects for each active session, keyed by session Id */
    private Hashtable marketStates;
    
    /** A random number generator */
    private Random rand;
    
    /** True if we are currently in offer rejection state because of an excessive backlog. This
     *  will not be set to false until the backlog drops to BACKLOG_RESTART_LEVEL */
    private boolean backlogRejection;
    
    /** A simple number formatter */
    private NumberFormat formatter;
    
    /** The max number of iterations that will have metrics recorded */
    private int MAX_RECORDED_ITERATIONS = 5;
    
    
    
}






