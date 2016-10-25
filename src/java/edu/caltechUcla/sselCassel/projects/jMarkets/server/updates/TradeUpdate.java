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
 * TradeUpdate.java
 *
 * Created on February 6, 2005, 10:30 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.updates;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.*;

/**
 *
 * @author  Raj Advani
 */
public class TradeUpdate {
    
    public TradeUpdate(int client, int type) {
        this.client = client;
        this.type = type;
    }
    
    /**
     * Getter for property priceBook.
     * @return Value of property priceBook.
     */
    public int[] getPriceBook() {
        return this.priceBook;
    }
    
    /**
     * Setter for property priceBook.
     * @param priceBook New value of property priceBook.
     */
    public void setPriceBook(int[] priceBook) {
        this.priceBook = priceBook;
    }
    
    /**
     * Getter for property marketId.
     * @return Value of property marketId.
     */
    public int getMarketId() {
        return marketId;
    }
    
    /**
     * Setter for property marketId.
     * @param marketId New value of property marketId.
     */
    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }
    
    /**
     * Getter for property key.
     * @return Value of property key.
     */
    public long getKey() {
        return key;
    }
    
    /**
     * Setter for property key.
     * @param key New value of property key.
     */
    public void setKey(long key) {
        this.key = key;
    }
    
    /**
     * Getter for property priceId.
     * @return Value of property priceId.
     */
    public int getExecutedPriceId() {
        return executedPriceId;
    }
    
    /**
     * Setter for property priceId.
     * @param priceId New value of property priceId.
     */
    public void setExecutedPriceId(int priceId) {
        this.executedPriceId = priceId;
    }
    
    public int getStandingPriceId() {
        return standingPriceId;
    }

    public void setStandingPriceId(int standingPriceId) {
        this.standingPriceId = standingPriceId;
    }
    
    /**
     * Getter for property cashHoldings.
     * @return Value of property cashHoldings.
     */
    public float getCashHoldings() {
        return cashHoldings;
    }
    
    /**
     * Setter for property cashHoldings.
     * @param cashHoldings New value of property cashHoldings.
     */
    public void setCashHoldings(float cashHoldings) {
        this.cashHoldings = cashHoldings;
    }
    
    /**
     * Getter for property marketTime.
     * @return Value of property marketTime.
     */
    public int[] getMarketTime() {
        return marketTime;
    }
    
    /**
     * Setter for property marketTime.
     * @param marketTime New value of property marketTime.
     */
    public void setMarketTime(int[] marketTime) {
        this.marketTime = marketTime;
    }
    
    /**
     * Getter for property client.
     * @return Value of property client.
     */
    public int getClient() {
        return client;
    }
    
    /**
     * Setter for property client.
     * @param client New value of property client.
     */
    public void setClient(int client) {
        this.client = client;
    }
    
    /**
     * Getter for property periodTime.
     * @return Value of property periodTime.
     */
    public int getPeriodTime() {
        return periodTime;
    }
    
    /**
     * Setter for property periodTime.
     * @param periodTime New value of property periodTime.
     */
    public void setPeriodTime(int periodTime) {
        this.periodTime = periodTime;
    }
    
    /**
     * Getter for property securityHoldings.
     * @return Value of property securityHoldings.
     */
    public int getSecurityHoldings() {
        return securityHoldings;
    }
    
    /**
     * Setter for property securityHoldings.
     * @param securityHoldings New value of property securityHoldings.
     */
    public void setSecurityHoldings(int securityHoldings) {
        this.securityHoldings = securityHoldings;
    }
    
    /**
     * Getter for property type.
     * @return Value of property type.
     */
    public int getType() {
        return type;
    }
    
    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    public void setType(int type) {
        this.type = type;
    }
    
    /**
     * Getter for property time.
     * @return Value of property time.
     */
    public long getTime() {
        return time;
    }
    
    /**
     * Setter for property time.
     * @param time New value of property time.
     */
    public void setTime(long time) {
        this.time = time;
    }
    
    /**
     * Getter for property code.
     * @return Value of property code.
     */
    public java.lang.String getCode() {
        return code;
    }
    
    /**
     * Setter for property code.
     * @param code New value of property code.
     */
    public void setCode(java.lang.String code) {
        this.code = code;
    }
    
    /**
     * Getter for property action.
     * @return Value of property action.
     */
    public int getAction() {
        return action;
    }
    
    /**
     * Setter for property action.
     * @param action New value of property action.
     */
    public void setAction(int action) {
        this.action = action;
    }
    
    /**
     * Getter for property openingTime.
     * @return Value of property openingTime.
     */
    public int getOpeningTime() {
        return openingTime;
    }
    
    /**
     * Setter for property openingTime.
     * @param openingTime New value of property openingTime.
     */
    public void setOpeningTime(int openingTime) {
        this.openingTime = openingTime;
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
     * Getter for property errorMsg.
     * @return Value of property errorMsg.
     */
    public java.lang.String getErrorMsg() {
        return errorMsg;
    }
    
    /**
     * Setter for property errorMsg.
     * @param errorMsg New value of property errorMsg.
     */
    public void setErrorMsg(java.lang.String errorMsg) {
        this.errorMsg = errorMsg;
    }
    
    public int[] getNumPurchases() {
        return numPurchases;
    }

    public void setNumPurchases(int[] numPurchases) {
        this.numPurchases = numPurchases;
    }

    public int[] getNumSales() {
        return numSales;
    }

    public void setNumSales(int[] numSales) {
        this.numSales = numSales;
    }
    
    public void setNumUnitsInTrade(int numUnitsInTrade){
        this.numUnitsInTrade = numUnitsInTrade;
    }
    
    public int getNumUnitsInTrade(){
        return numUnitsInTrade;
    }
    
    public void setTxnActionType(String actionType){
        this.actionType = actionType;
    }
    
    public String getTxnActionType(){
        return actionType;
    }
    
    /** The client who is to receive the update */
    private int client;
    
    /** The type of update */
    private int type;
    
    /** These fields are used for all valid updates */
    private int action;
    private int marketId;
    private int executedPriceId;
    private int standingPriceId; 
    private long time;
    private long key;
    private String code;
    private int[] priceBook;
    private int periodNum;
    
    /** These fields are used for transaction updates */
    private float cashHoldings;
    private int securityHoldings;
    private int numUnitsInTrade;
    private String actionType;
    private int[] numPurchases;
    private int[] numSales;
    
    /** These fields are used for invalid updates */
    private String errorMsg;
    
    /** These fields are filled by the dispatcher */
    private int periodTime;
    private int openingTime;
    private int[] marketTime;
   
    public static int INVALID_OFFER_UPDATE = 0;
    public static int NON_TRANSACTION_UPDATE = 1;
    public static int TRANSACTION_UPDATE = 2;

    
}
