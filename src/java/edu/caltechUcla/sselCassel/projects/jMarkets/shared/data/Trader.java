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
 * OfferBook.java
 *
 * Created on October 29, 2004, 7:25 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.BankruptcyFunction;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.functions.*;

/**
 * This shared data object encapsulates the data about each individual trader. One Trader
 * object exists for each client in a session. The Trader object has a lifespan of only
 * one period. This object contains all market-related information that is specific to
 * that client for that period, such as cash holdings, security holdings, and the number
 * of purchases and sales of each security. Additionally, by passing in the OfferBook to
 * some methods here, additional information about the trader can be uncovered, such as
 * the executed holdings, executed cash, etc.
 *
 * @author  Raj Advani
 */
public class Trader implements java.io.Serializable {
    
    /** Creates an OfferBook with the given number of markets, divisions and clients */
    public Trader(int subjectId, int numClients, int numMarkets) {
        this.subjectId = subjectId;
        this.cash = 0;
        this.holdings = new int[numMarkets];
        this.numSales = new int[numMarkets][numClients];
        this.numPurchases = new int[numMarkets][numClients];
    }
    
    /** Return the amount of cash tied up in bid orders (the cash obligations) for the
     *  given client */
    public float getCashObligations(OfferBook offerBook) {
        return offerBook.getCashObligations(subjectId);
    }
    
    /** Return the security holdings of the given client if all orders were executed */
    public int[] getExecHoldings(OfferBook offerBook) {
        int[] execHoldings = new int[holdings.length];
        
        for (int i=0; i<execHoldings.length; i++) {
            execHoldings[i] = holdings[i];
            execHoldings[i] += offerBook.getMarketBids(i, subjectId);
            execHoldings[i] -= offerBook.getMarketAsks(i, subjectId);
        }
        
        return execHoldings;
    }
    
    /** Return the cash holdings of the given client if all orders were executed */
    public float getExecCash(OfferBook offerBook) {
        return cash + offerBook.getExecCash(subjectId);
    }
    
    public float getCash() {
        return cash;
    }
    
    public void setCash(float c) {
        this.cash = c;
    }
    
    public int getHoldings(int marketId) {
        return holdings[marketId];
    }
    
    public int[] getHoldings() {
        return holdings;
    }
    
    public void setHoldings(int marketId, int h) {
        holdings[marketId] = h;
    }
    
    public void setHoldings(int[] holdings) {
        this.holdings = holdings;
    }
    
    public void incNumPurchases(int marketId, int fromSubject, int units) {
        numPurchases[marketId][fromSubject] += units;
    }
    
    public void incNumSales(int marketId, int toSubject, int units) {
        numSales[marketId][toSubject] += units;
    }
    
    public void decNumPurchases(int marketId, int fromSubject, int units) {
        numPurchases[marketId][fromSubject] -= units;
    }
    
    public void decNumSales(int marketId, int toSubject, int units) {
        numSales[marketId][toSubject] -= units;
    }
    
    /** Get the total number of purchases by this trader on this security (includes
     *  purchases from all subjects) */
    public int getTotalPurchases(int marketId) {
        int totalPurchases = 0;
        for (int i=0; i<numPurchases[marketId].length; i++)
            totalPurchases += numPurchases[marketId][i];
        
        return totalPurchases;
    }
    
    /** Get the array of total purchases made on each market. This array is indexed
     *  by the market */
    public int[] getTotalPurchases() {
        int[] totalPurchases = new int[numPurchases.length];
        for (int i=0; i<totalPurchases.length; i++)
            totalPurchases[i] = getTotalPurchases(i);
        
        return totalPurchases;
    }
    
    /** Get the array of purchases made by this trader, indexed first by security then
     *  by the subject purchased from */
    public int[][] getNumPurchases() {
        return numPurchases;
    }
    
    /** Get the array of purchases made of the given security. The array is indexed
     *  by subject purchased from */
    public int[] getNumPurchases(int marketId) {
        return numPurchases[marketId];
    }
    
    public void setNumPurchases(int marketId, int fromSubject, int numPurchases) {
        this.numPurchases[marketId][fromSubject] = numPurchases;
    }
    
    public void setNumPurchases(int marketId, int[] np) {
        numPurchases[marketId] = np;
    }
    
    public void setNumPurchases(int[][] numPurchases) {
        this.numPurchases = numPurchases;
    }
    
    /** Get the total number of sales by this trader on this security (includes sales
     *  to ALL subjects) */
    public int getTotalSales(int marketId) {
        int totalSales = 0;
        for (int i=0; i<numSales[marketId].length; i++)
            totalSales += numSales[marketId][i];
        
        return totalSales;
    }
    
    /** Get the array of total sales made on each market. This array is indexed
     *  by market number */
    public int[] getTotalSales() {
        int[] totalSales = new int[numSales.length];
        for (int i=0; i<totalSales.length; i++)
            totalSales[i] = getTotalSales(i);
        
        return totalSales;
    }
    
    /** Get the array of sales made by this trader, indexed first by security then by
     *  the subject sold to */
    public int[][] getNumSales() {
        return numSales;
    }
    
    /** Get the array of sales made of the given security. The array is indexed by
     *  subject sold to */
    public int[] getNumSales(int marketId) {
        return numSales[marketId];
    }
    
    public void setNumSales(int marketId, int toSubject, int numSales) {
        this.numSales[marketId][toSubject] = numSales;
    }
    
    public void setNumSales(int marketId, int[] ns) {
        numSales[marketId] = ns;
    }
    
    public void setNumSales(int[][] numSales) {
        this.numSales = numSales;
    }
    
    /** Return true if this trader is allowed to bid on the given security */
    public boolean canBid(int security) {
        int role = getSecurityPrivileges()[security];
        if (role == JMConstants.BUYER_ROLE || role == JMConstants.BOTH_ROLE)
            return true;
        else
            return false;
    }
    
    /** Return true if this trader is allowed to ask on the given security */
    public boolean canAsk(int security) {
        int role = getSecurityPrivileges()[security];
        if (role == JMConstants.SELLER_ROLE || role == JMConstants.BOTH_ROLE)
            return true;
        else
            return false;
    }
    
    public int[] getSecurityPrivileges() {
        return securityPrivileges;
    }
    
    public void setSecurityPrivileges(int[] securityPrivileges) {
        this.securityPrivileges = securityPrivileges;
    }
    
    public int getShortSaleConstraint(int marketId) {
        return shortSaleConstraints[marketId];
    }
    
    public int[] getShortSaleConstraints() {
        return shortSaleConstraints;
    }
    
    public void setShortSaleConstraints(int[] shortSaleConstraints) {
        this.shortSaleConstraints = shortSaleConstraints;
    }
    
    public BankruptcyFunction getBankruptcyFunction() {
        return bankruptcyFunction;
    }
    
    public void setBankruptcyFunction(BankruptcyFunction bankruptcyFunction) {
        this.bankruptcyFunction = bankruptcyFunction;
    }
    
    public float getBankruptcyCutoff() {
        return bankruptcyCutoff;
    }
    
    public void setBankruptcyCutoff(float bankruptcyCutoff) {
        this.bankruptcyCutoff = bankruptcyCutoff;
    }
    
    public ClientPayoffFunction getClientPayoffFunction() {
        return clientPayoffFunction;
    }
    
    public void setClientPayoffFunction(ClientPayoffFunction cpf) {
        this.clientPayoffFunction = cpf;
    }
    
    
    public int[] getInitialHoldings() {
        return initialHoldings;
    }
    
    public void setInitialHoldings(int[] initialHoldings) {
        this.initialHoldings = initialHoldings;
    }
    
    public float getInitialCash() {
        return initialCash;
    }
    
    public void setInitialCash(float initialCash) {
        this.initialCash = initialCash;
    }

    public int getPeriodNum(){
        return periodNum;
    }
    
    public void setPeriodNum(int periodNum){
        this.periodNum = periodNum;
    }
    
    public boolean isCacheOrders() {
        return cacheOrders;
    }

    public void setCacheOrders(boolean cacheOrders) {
        this.cacheOrders = cacheOrders;
    }

    public boolean isCacheTransactions() {
        return cacheTransactions;
    }

    public void setCacheTransactions(boolean cacheTransactions) {
        this.cacheTransactions = cacheTransactions;
    }
    
    public boolean isClosebook() {
        return closebook;
    }

    public void setClosebook(boolean closebook) {
        this.closebook = closebook;
    }

    public boolean isShowSuggestedClearingPrice() {
        return showSuggestedClearingPrice;
    }

    public void setShowSuggestedClearingPrice(boolean showSuggestedClearingPrice) {
        this.showSuggestedClearingPrice = showSuggestedClearingPrice;
    }
    
    
    /** The subject id of this client */
    private int subjectId;
    
    private int periodNum;
    
    /** The security holdings of this client, indexed by subject then market ID */
    private int[] holdings;
    
    /** The cash holdings of this client */
    private float cash;
    
    /** The security holdings this client had at the start of this period */
    private int[] initialHoldings;
    
    /** The cash this client has at the start of the period */
    private float initialCash;
    
    /** The number of purchases made by this client during this period on security
     *  i from subject j is numPurchases[i][j]. Added up all the entries will give the
     *  total number of purchases made by this client */
    private int[][] numPurchases;
    
    /** The number of sells, see comment for numPurchases */
    private int[][] numSales;
    
    /** The security privelege of this trader on each security */
    private int[] securityPrivileges;
    
    /** The short sale constraints of this trader on each security */
    private int[] shortSaleConstraints;
    
    /** The bankruptcy function used by this trader */
    private BankruptcyFunction bankruptcyFunction;
    
    /** The bankruptcy cutoff for this trader */
    private float bankruptcyCutoff;
    
    private ClientPayoffFunction clientPayoffFunction;
    
    /** Cache standing orders from previous periods */
    private boolean cacheOrders; 
    
    /** Cache transactions from previous periods */
    private boolean cacheTransactions; 
    
    private boolean closebook; 
    
    private boolean showSuggestedClearingPrice; 
    
    private String announcement;

    /**
     * @return the announcement
     */
    public String getAnnouncement() {
        return announcement;
    }

    /**
     * @param announcement the announcement to set
     */
    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

}
