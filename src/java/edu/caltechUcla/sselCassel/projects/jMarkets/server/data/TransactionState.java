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
 * TransactionState.java
 *
 * Created on February 7, 2005, 1:24 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.data;

import java.util.*;
import java.sql.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;

/**
 * This object holds the state of a transaction as it is being processed. It also
 * holds update inverts, so in the case of a transaction roll-back it can invert all
 * the changes made to the in-memory offer book structures
 *
 * @author  Raj Advani
 */
public class TransactionState {
    
    /** Creates a new instance of TransactionState */
    public TransactionState(AbstractOffer offer, String code, Connection conn) {
        this.offer = offer;
        this.code = code;
        this.conn = conn;
        
        updateInverts = new Vector();
    }
    
    public int getIterations() {
        return iterations;
    }
    
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
    
    public void stampStartTime() {
        java.util.Date date = new java.util.Date();
        startTime = date.getTime();
    }
    
    public void stampEndTime() {
        java.util.Date date = new java.util.Date();
        endTime = date.getTime();
    }
    
    public long getTimeElapsed() {
        return endTime - startTime;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public void stampHoldings(float[] cashHoldings, int[] secHoldings) {
        this.cashHoldings = cashHoldings;
        this.secHoldings = secHoldings;
    }
    
    public float[] getPriorCashHoldings() {
        return cashHoldings;
    }
    
    public int[] getPriorSecHoldings() {
        return secHoldings;
    }
    
    public void addUpdateInvert(UpdateInvert ui) {
        updateInverts.add(ui);
    }
    
    public Vector getUpdateInverts() {
        return updateInverts;
    }
    
    /**
     * Getter for property marketId.
     * @return Value of property marketId.
     */
    public int getMarketId() {
        return offer.getMarketId();
    }
    
    /**
     * Getter for property conn.
     * @return Value of property conn.
     */
    public java.sql.Connection getConn() {
        return conn;
    }
    
    /**
     * Setter for property conn.
     * @param conn New value of property conn.
     */
    public void setConn(java.sql.Connection conn) {
        this.conn = conn;
    }
    
    /**
     * Getter for property offer.
     * @return Value of property offer.
     */
    public AbstractOffer getOffer() {
        return offer;
    }
    
    /**
     * Setter for property offer.
     * @param offer New value of property offer.
     */
    public void setOffer(AbstractOffer offer) {
        this.offer = offer;
    }
    
    public int[][] getPriorPurchases() {
        return numPurchases;
    }
    
    public void setPriorPurchases(int[][] numPurchases) {
        this.numPurchases = numPurchases;
    }
    
    public int[][] getPriorSales() {
        return numSales;
    }
    
    public void setPriorSales(int[][] numSales) {
        this.numSales = numSales;
    }
    
    /** A string that identifies the transaction associated with this basket */
    private String code;
    
    /** The database connection used to process this transaction */
    private Connection conn;
    
    /** The offer that started the transaction (the new offer) */
    private AbstractOffer offer;
    
    /** The start and end time of the transaction associated with this basket */
    private long startTime, endTime;
    
    /** The number of iterations the transaciton associated with this basket underwent
     *  (iteration = trade) */
    private int iterations;
    
    /** Update inverts store changes to the server-side offerbook that can be
     *  rolled back */
    private Vector updateInverts;
    
    /** The cash and security holdings of each subject (for only the security involved
     *  in the trade) are stored here so that in the event of a rollback these values
     *  can be recalled */
    private int[] secHoldings;
    private float[] cashHoldings;
    
    /** The purchases and sales information for the security involved are stored here
     *  in case of rollback. This array is indexed by subject and subject */
    private int[][] numPurchases;
    private int[][] numSales;
}
