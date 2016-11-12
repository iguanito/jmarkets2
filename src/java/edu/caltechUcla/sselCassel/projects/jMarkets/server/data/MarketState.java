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
 * MarketState.java
 *
 * Created on February 6, 2005, 10:05 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.data;

import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.interfaces.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SubjectDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.GroupDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.MarketDef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  Raj Advani
 */
public class MarketState {
    
    /** Creates a new instance of MarketState */
    public MarketState(int sessionId, int periodNum, MarketDef marketInfo, SubjectDef subjectInfo, GroupDef groupInfo, float[] initialCash, int[][] initialHoldings) {
        this.sessionId = sessionId;
        this.periodNum = periodNum;
        
        numTrans = new int[MAX_RECORDED_ITERATIONS];
        transTime = new long[MAX_RECORDED_ITERATIONS];
        
        this.marketInfo = marketInfo;
        this.subjectInfo = subjectInfo;
        
        numTrans = new int[MAX_RECORDED_ITERATIONS];
        transTime = new long[MAX_RECORDED_ITERATIONS];
        
        log.info("Received new period parameters, creating markets...");
        
        int numClients = subjectInfo.getNumSubjects();
        
        numOffers = new int[numClients];
        keys = new long[numClients];
        keyCodes = new String[numClients];
        
        offerBook = new OfferBook(marketInfo.getNumMarkets(), marketInfo.getNumDivisions(), numClients);
        offerBook.setPrices(marketInfo.getPrices());
        
        setTraders(new Trader[numClients]);
        for (int i=0; i<getTraders().length; i++) {
            int group = subjectInfo.getGroup(i);
            
            traders[i] = new Trader(i, numClients, marketInfo.getNumMarkets());
            traders[i].setPeriodNum(periodNum);
            traders[i].setBankruptcyFunction(groupInfo.getBankruptcyFunction(group));
            traders[i].setClientPayoffFunction(groupInfo.getClientPayoffFunction(group));
            traders[i].setBankruptcyCutoff(groupInfo.getBankruptcyCutoff(group));
            traders[i].setCash(initialCash[i]);
            traders[i].setHoldings(initialHoldings[i].clone());
            traders[i].setInitialCash(initialCash[i]);
            traders[i].setInitialHoldings(initialHoldings[i].clone());
            traders[i].setShortSaleConstraints(groupInfo.getSecurityShortConstraints(group));
            traders[i].setSecurityPrivileges(groupInfo.getSecurityPrivileges(group));
            traders[i].setAnnouncement(subjectInfo.getAnnouncement(i));
        }
        
        marketLocks = new boolean[marketInfo.getNumMarkets()];
        threadQueue = new Vector[marketInfo.getNumMarkets()];
        
        for (int i=0; i<threadQueue.length; i++)
            threadQueue[i] = new Vector();
        
        log.info("Market State object has finished data initialization for period " + periodNum + " in session " + sessionId);
        
        chart = new PriceChart();
        for (int i=0; i<marketInfo.getNumMarkets(); i++)
            chart.addSecurity(marketInfo.getMarketTitles()[i]);
        
        log.info("Market State for period " + periodNum + " in session " + sessionId + " has created a price chart");
        
        periodStartTime = new java.util.Date().getTime();
    }
    
    /** Return the database ID of the given client */
    public int getDatabaseId(int id) {
        return subjectInfo.getDatabaseId(id);
    }
    
    /** Return a vector of securities that can be used to generate a price chart view of
     *  this market state object */
    public Vector getChartSecurities() {
        return chart.getSecurities();
    }
    
    /** Increment the number of offers made by the given client. Return the new number of offers */
    public int incNumOffers(int client) {
        numOffers[client]++;
        return numOffers[client];
    }
    
    /** Set the key of the given client to the given number */
    public void setKey(int client, long key) {
        keys[client] = key;
    }
    
    /** Set the key code of the given client to the given string */
    public void setKeyCode(int client, String keyCode) {
        keyCodes[client] = keyCode;
    }
    
    /** Get the number of clients */
    public int getNumClients() {
        return subjectInfo.getNumSubjects();
    }
    
    /**
     * Getter for property chart.
     * @return Value of property chart.
     */
    public PriceChart getChart() {
        return chart;
    }
    
    /**
     * Setter for property chart.
     * @param chart New value of property chart.
     */
    public void setChart(PriceChart chart) {
        this.chart = chart;
    }
    
    /**
     * Getter for property keyCodes.
     * @return Value of property keyCodes.
     */
    public java.lang.String[] getKeyCodes() {
        return this.keyCodes;
    }
    
    /**
     * Setter for property keyCodes.
     * @param keyCodes New value of property keyCodes.
     */
    public void setKeyCodes(java.lang.String[] keyCodes) {
        this.keyCodes = keyCodes;
    }
    
    /**
     * Getter for property keys.
     * @return Value of property keys.
     */
    public long[] getKeys() {
        return this.keys;
    }
    
    /**
     * Setter for property keys.
     * @param keys New value of property keys.
     */
    public void setKeys(long[] keys) {
        this.keys = keys;
    }
    
    /**
     * Getter for property marketInfo.
     * @return Value of property marketInfo.
     */
    public MarketDef getMarketInfo() {
        return marketInfo;
    }
    
    /**
     * Setter for property marketInfo.
     * @param marketInfo New value of property marketInfo.
     */
    public void setMarketInfo(MarketDef marketInfo) {
        this.marketInfo = marketInfo;
    }
    
    /**
     * Getter for property marketLocks.
     * @return Value of property marketLocks.
     */
    public boolean[] getMarketLocks() {
        return this.marketLocks;
    }
    
    /**
     * Setter for property marketLocks.
     * @param marketLocks New value of property marketLocks.
     */
    public void setMarketLocks(boolean[] marketLocks) {
        this.marketLocks = marketLocks;
    }
    
    /**
     * Getter for property numOffers.
     * @return Value of property numOffers.
     */
    public int[] getNumOffers() {
        return this.numOffers;
    }
    
    /**
     * Setter for property numOffers.
     * @param numOffers New value of property numOffers.
     */
    public void setNumOffers(int[] numOffers) {
        this.numOffers = numOffers;
    }
    
    /**
     * Getter for property numTrans.
     * @return Value of property numTrans.
     */
    public int[] getNumTrans() {
        return this.numTrans;
    }
    
    /**
     * Setter for property numTrans.
     * @param numTrans New value of property numTrans.
     */
    public void setNumTrans(int[] numTrans) {
        this.numTrans = numTrans;
    }
    
    /**
     * Getter for property offerBook.
     * @return Value of property offerBook.
     */
    public OfferBook getOfferBook() {
        return offerBook;
    }
    
    /**
     * Setter for property offerBook.
     * @param offerBook New value of property offerBook.
     */
    public void setOfferBook(OfferBook offerBook) {
        this.offerBook = offerBook;
    }
    
    /**
     * Getter for property periodNum.
     * @return Value of property periodNum.
     */
    public int getPeriodNum() {
        return periodNum;
    }
    
    /**
     * Setter for property periodNum.
     * @param periodNum New value of property periodNum.
     */
    public void setPeriodNum(int periodNum) {
        this.periodNum = periodNum;
    }
    
    /**
     * Getter for property sessionId.
     * @return Value of property sessionId.
     */
    public int getSessionId() {
        return sessionId;
    }
    
    /**
     * Setter for property sessionId.
     * @param sessionId New value of property sessionId.
     */
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
    
    /**
     * Getter for property subjectInfo.
     * @return Value of property subjectInfo.
     */
    public SubjectDef getSubjectInfo() {
        return subjectInfo;
    }
    
    /**
     * Setter for property subjectInfo.
     * @param subjectInfo New value of property subjectInfo.
     */
    public void setSubjectInfo(SubjectDef subjectInfo) {
        this.subjectInfo = subjectInfo;
    }
    
    /**
     * Getter for property threadQueue.
     * @return Value of property threadQueue.
     */
    public Vector[] getThreadQueue() {
        return this.threadQueue;
    }
    
    /**
     * Setter for property threadQueue.
     * @param threadQueue New value of property threadQueue.
     */
    public void setThreadQueue(Vector[] threadQueue) {
        this.threadQueue = threadQueue;
    }
    
    /**
     * Getter for property transTime.
     * @return Value of property transTime.
     */
    public long[] getTransTime() {
        return this.transTime;
    }
    
    /**
     * Setter for property transTime.
     * @param transTime New value of property transTime.
     */
    public void setTransTime(long[] transTime) {
        this.transTime = transTime;
    }
    
    /**
     * Getter for property periodStartTime.
     * @return Value of property periodStartTime.
     */
    public long getPeriodStartTime() {
        return periodStartTime;
    }
    
    /**
     * Setter for property periodStartTime.
     * @param periodStartTime New value of property periodStartTime.
     */
    public void setPeriodStartTime(long periodStartTime) {
        this.periodStartTime = periodStartTime;
    }
    
    public Trader[] getTraders() {
        return traders;
    }
    
    public void setTraders(Trader[] traders) {
        this.traders = traders;
    }
    
    /** The session ID that this MarketState object belongs to */
    private int sessionId;
    
    /** The period number that this MarketState object belongs to */
    private int periodNum;
    
    /** A Vector array containing queues of waiting threads for each market. This way orders
     *  can be processed in FIFO order */
    private Vector[] threadQueue;
    
    /** numTransactions[i] contains the number of transactions with i iterations that have taken
     *  place since the start of the period. An iteration is defined as an interaction between
     *  two two orders. Zero iterations is a market order */
    private int[] numTrans;
    
    /** The total amount of time spent on transactions with i iterations */
    private long[] transTime;
    
    /** The number of attempted offres made by each subject */
    private int[] numOffers;
    
    /** Information about the current market being played. This is to facilitate the transfer of the
     *  markets to the clients. The actual 'state' of the market is kept in the database */
    private MarketDef marketInfo;
    
    /** Information about the current subjects for this period */
    private SubjectDef subjectInfo;
    
    /** A server-side copy of the offer book */
    private OfferBook offerBook;
    
    /** A server-side copy of the price chart */
    private PriceChart chart;
    
    /** A thread manager array that locks each market, so there can only be one transaction occuring on
     *  any market at any time (hope to deprecate this in the future to increase performance) */
    private boolean[] marketLocks;
    
    /** Contains a key for each client. This key is updated each time a client's cash or security holdings
     *  is updated. When a client attempts to make a transaction, the client-side key is checked with this
     *  one. If they do not match, then the client does not have up-to-date cash/security holdings, so his
     *  order is rejected */
    private long[] keys;
    
    /** Contains the Trader objects for each client in this market */
    private Trader[] traders;
    
    /** Array contains the offer code of the last offer to update the server-side key for each client */
    private String[] keyCodes;
    
    /** The time the period represented by this market state object started */
    private long periodStartTime;
    
    /** The max number of iterations that will have metrics recorded */
    private static int MAX_RECORDED_ITERATIONS = 5;
    
    private static Log log = LogFactory.getLog(MarketState.class);
}
