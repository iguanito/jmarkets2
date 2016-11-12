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
 * NewPeriodUpdate.java
 *
 * Created on February 4, 2005, 10:42 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.updates;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.PeriodDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable.EarningsInfo;

/**
 *
 * @author  Raj Advani
 */
public class NewPeriodUpdate {
    
    /** Creates a new instance of NewPeriodUpdate */
    public NewPeriodUpdate() {
    }
    
    /** Getter for property earningsHistory.
     * @return Value of property earningsHistory.
     *
     */
    public EarningsInfo[] getEarningsHistory() {
        return this.earningsHistory;
    }
    
    /** Setter for property earningsHistory.
     * @param earningsHistory New value of property earningsHistory.
     *
     */
    public void setEarningsHistory(EarningsInfo[] earningsHistory) {
        this.earningsHistory = earningsHistory;
    }
    
    /** Getter for property periodInfo.
     * @return Value of property periodInfo.
     *
     */
    public PeriodDef getPeriodInfo() {
        return periodInfo;
    }
    
    /** Setter for property periodInfo.
     * @param periodInfo New value of property periodInfo.
     *
     */
    public void setPeriodInfo(PeriodDef periodInfo) {
        this.periodInfo = periodInfo;
    }
    
    /** Getter for property periodNum.
     * @return Value of property periodNum.
     *
     */
    public int getPeriodNum() {
        return periodNum;
    }
    
    /** Setter for property periodNum.
     * @param periodNum New value of property periodNum.
     *
     */
    public void setPeriodNum(int periodNum) {
        this.periodNum = periodNum;
    }
    
    /** Getter for property recipients.
     * @return Value of property recipients.
     *
     */
    public int[] getRecipients() {
        return this.recipients;
    }
    
    /** Setter for property recipients.
     * @param recipients New value of property recipients.
     *
     */
    public void setRecipients(int[] recipients) {
        this.recipients = recipients;
    }
    
    /**
     * Getter for property initialCash.
     * @return Value of property initialCash.
     */
    public float[] getInitialCash() {
        return this.initialCash;
    }
    
    /**
     * Setter for property initialCash.
     * @param initialCash New value of property initialCash.
     */
    public void setInitialCash(float[] initialCash) {
        this.initialCash = initialCash;
    }
    
    /**
     * Getter for property initialHoldings.
     * @return Value of property initialHoldings.
     */
    public int[][] getInitialHoldings() {
        return this.initialHoldings;
    }
    
    /**
     * Setter for property initialHoldings.
     * @param initialHoldings New value of property initialHoldings.
     */
    public void setInitialHoldings(int[][] initialHoldings) {
        this.initialHoldings = initialHoldings;
    }
    
    /**
     * Getter for property timeoutLength.
     * @return Value of property timeoutLength.
     */
    public int getTimeoutLength() {
        return timeoutLength;
    }
    
    /**
     * Setter for property timeoutLength.
     * @param timeoutLength New value of property timeoutLength.
     */
    public void setTimeoutLength(int timeoutLength) {
        this.timeoutLength = timeoutLength;
    }
    
    public String getMarketEngine() {
        return marketEngine;
    }
    
    public void setMarketEngine(String marketEngine) {
        this.marketEngine = marketEngine;
    }
    
    private String marketEngine;
    private int[] recipients;
    private int periodNum;
    private EarningsInfo[] earningsHistory;
    private PeriodDef periodInfo;
    private float[] initialCash;
    private int[][] initialHoldings;
    private int timeoutLength;
}
