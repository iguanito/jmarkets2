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
import java.util.Vector;

/**
 * This is the non-database version of the offerbook. It contains the number of active
 * offers for every market and price level, for each subject.
 *
 * The main data structure is the int[][][] indexed by market ID, price ID, then
 * subject ID. The methods of this class manipulate and retrieve information from
 * this data structure.
 *
 * @author  Raj Advani
 */
public class OfferBook implements java.io.Serializable {
    
    /** Creates an OfferBook with the given number of markets, divisions and clients */
    public OfferBook(int numMarkets, int numDivisions, int numClients) {
        offerBook = new int[numMarkets][numDivisions][numClients];
    }
    
    /** Add the given number of BUY units to the offer book. Return an UpdateInvert object
     *  so that this transaction can be inverted if rollback occurs */
    public UpdateInvert insertBuy(int marketId, int priceId, int subjectId, int units) {
        offerBook[marketId][priceId][subjectId] += units;
        
        return new UpdateInvert(UpdateInvert.ADD_INVERT, marketId, priceId, subjectId, units);
    }
    
    /** Add the given number of SELL units to the offer book. Return an UpdateInvert object
     *  so that this transaction can be inverted if rollback occurs */
    public UpdateInvert insertSell(int marketId, int priceId, int subjectId, int units) {
        offerBook[marketId][priceId][subjectId] -= units;
        
        return new UpdateInvert(UpdateInvert.SUBTRACT_INVERT, marketId, priceId, subjectId, units);
    }
    
    /** Decrement the given number of units from the offer book. Return an UpdateInvert object
     *  so that this transaction can be inverted if rollback occurs */
    public UpdateInvert insertCancel(int marketId, int priceId, int subjectId, int units) {
        if (offerBook[marketId][priceId][subjectId] > 0) {
            offerBook[marketId][priceId][subjectId] -= units;
            return new UpdateInvert(UpdateInvert.SUBTRACT_INVERT, marketId, priceId, subjectId, units);
        }
        if (offerBook[marketId][priceId][subjectId] < 0) {
            offerBook[marketId][priceId][subjectId] += units;
            return new UpdateInvert(UpdateInvert.ADD_INVERT, marketId, priceId, subjectId, units);
        }
        return null;
    }
    
    /** Process the given UpdateInvert */
    public void invertUpdate(UpdateInvert invert) {
        int market = invert.market;
        int price = invert.price;
        int subject = invert.subject;
        int type = invert.type;
        int units = invert.units;
        
        if (type == UpdateInvert.ADD_INVERT)
            offerBook[market][price][subject] -= units;
        else if (type == UpdateInvert.SUBTRACT_INVERT)
            offerBook[market][price][subject] += units;
    }
    
    public String getState(int marketId, int priceId, int subjectId) {
        return "Market: " + marketId + ", Price: " + priceId + " , Subject: " + subjectId + " is now " + offerBook[marketId][priceId][subjectId];
    }
    
    /** Insert an array indexed by subject ID showing the bids/asks on the given market at the given price */
    public int insertPriceBook(int marketId, int priceId, int[] priceBook, int subjectId) {
        int action = JMConstants.NO_ACTION; 
        if(offerBook[marketId][priceId][subjectId] != priceBook[subjectId]) 
            action= JMConstants.EXECUTE_ACTION; 
        
        offerBook[marketId][priceId] = priceBook;
        return action; 
    }
    
    /** Return the total number of asks on the given price level for the given market */
    public int getTotalAsks(int marketId, int priceId) {
        int total = 0;
        
        for (int i=0; i<offerBook[marketId][priceId].length; i++)
            total += getAsks(marketId, priceId, i);
        
        return total;
    }
    
    public int getMyTotalAsks(int marketId, int priceId, int subjId) {
        return getAsks(marketId, priceId, subjId);
    }
    
    /** Return the total number of bids on the given price level for the given market */
    public int getTotalBids(int marketId, int priceId) {
        int total = 0;
        
        for (int i=0; i<offerBook[marketId][priceId].length; i++)
            total += getBids(marketId, priceId, i);
        
        return total;
    }
    
    public int getMyTotalBids(int marketId, int priceId, int subjId) {
        return getBids(marketId, priceId, subjId);
    }
    
    /** Return the number of asks made on the given security by the given client */
    public int getMarketAsks(int marketId, int subjectId) {
        int[][] security = offerBook[marketId];
        int numOrders = 0;
        
        for (int i=0; i<security.length; i++) {
            if (security[i][subjectId] < 0)
                numOrders += (-1 * security[i][subjectId]);
        }
        
        return numOrders;
    }
    
    /** Return the number of bids made on the given security by the given client */
    public int getMarketBids(int marketId, int subjectId) {
        int[][] security = offerBook[marketId];
        int numOrders = 0;
        
        for (int i=0; i<security.length; i++) {
            if (security[i][subjectId] > 0)
                numOrders += security[i][subjectId];
        }
        
        return numOrders;
    }
    
    /** Return the number of bids made by the given subject on this price level */
    public int getBids(int marketId, int priceId, int subjectId) {
        if (offerBook[marketId][priceId][subjectId] > 0)
            return offerBook[marketId][priceId][subjectId];
        else
            return 0;
    }
    
    /** Return the number of asks made by the given subject on this price level */
    public int getAsks(int marketId, int priceId, int subjectId) {
        if (offerBook[marketId][priceId][subjectId] < 0)
            return offerBook[marketId][priceId][subjectId] * -1;
        else
            return 0;
    }
    
    /** Return the server (non-db) price level id of the lowest ask for the given
     *  market. Return -1 if there are no asks */
    public int getLowestAsk(int marketId) {
        int[][] marketBook = getMarketBook(marketId);
        
        for (int p=0; p<marketBook.length; p++) {
            for (int s=0; s<marketBook[p].length; s++) {
                if (marketBook[p][s] < 0)
                    return p;
            }
        }
        
        return -1;
    }
    
    /** Return the server (non-db) price level id of the highest bid for the given
     *  market. Return -1 if there are no bids */
    public int getHighestBid(int marketId) {
        int[][] marketBook = getMarketBook(marketId);
        
        for (int p=marketBook.length-1; p>=0; p--) {
            for (int s=0; s<marketBook[p].length; s++) {
                if (marketBook[p][s] > 0)
                    return p;
            }
        }
        
        return -1;
    }
    
    /** Return the center of the given market. If there are no bids return the lowest ask.
     *  If there are no asks return the highest bid */
    public int getMarketCenter(int marketId) {
        int highBid = getHighestBid(marketId);
        int lowAsk = getLowestAsk(marketId);
        
        if (highBid == -1 && lowAsk == -1)
            return -1;
        else if (highBid == -1)
            return lowAsk;
        else if (lowAsk == -1)
            return highBid;
        else
            return lowAsk + (highBid - lowAsk) / 2;
    }
    
    public float getMarketClearingPrice(int marketId){
        int numPrices = this.getNumPrices();
        int difference = 10000;
        Vector executePriceIds = new Vector();
        
        for (int p=0; p<numPrices; p++) {
            int numBids = this.getGreaterThanOrEqualToBidOrders(marketId, p, -1);
            int numAsks = this.getLessThanOrEqualToAskOrders(marketId, p, -1);
            
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
            int executePriceId = ((Integer) executePriceIds.get(mid)).intValue();
            return this.getPrice(marketId, executePriceId);
        } 
        
        return -1;
    }
    
    /** Finds the orders this client has that are inferior to an order at the given
     *  price level on the given market. This will return the inferior units in array
     *  form, where index maps to price level id. The inferiority of each unit is a
     *  decreasing function of the index -- therefore, the most inferior unit will be
     *  at index 0. On the buy side, this means the unit with the lowest price level;
     *  on the sell side, this means the unit with the hihgest price level. Note that
     *  time priority is ignored */
    public int[] getInferiorBids(int marketId, int priceId, int subjectId) {
        int[][] marketBook = getMarketBook(marketId);
        Vector orders = new Vector();
        
        for (int p=0; p<priceId; p++) {
            int numBids = marketBook[p][subjectId];
            if (numBids < 0)
                numBids = 0;
            
            if (numBids > 0) {
                for (int i=0; i<numBids; i++)
                    orders.add(new Integer(p));
            }
        }
        
        int[] orderArray = new int[orders.size()];
        for (int i=0; i<orders.size(); i++) {
            orderArray[i] = ((Integer) orders.get(i)).intValue();
        }
        
        if (orderArray.length > 0)
            return orderArray;
        
        return null;
    }
    
    /** See the comment for getInferiorBids. This is the same but on the ask side */
    public int[] getInferiorAsks(int marketId, int priceId, int subjectId) {
        int[][] marketBook = getMarketBook(marketId);
        Vector orders = new Vector();
        
        int maxPriceId = prices[marketId].length - 1;
        
        for (int p=maxPriceId; p>priceId; p--) {
            int numBids = marketBook[p][subjectId];
            if (numBids > 0)
                numBids = 0;
            else
                numBids *= -1;
            
            if (numBids > 0) {
                for (int i=0; i<numBids; i++)
                    orders.add(new Integer(p));
            }
        }
        
        int[] orderArray = new int[orders.size()];
        for (int i=0; i<orders.size(); i++) {
            orderArray[i] = ((Integer) orders.get(i)).intValue();
        }
        
        if (orderArray.length > 0)
            return orderArray;
        
        return null;
    }
    
    /** Return the raw number of orders number made by the given subject on the given
     *  market and price level (raw means negative=sell, positive=buy) */
    public int getOrders(int marketId, int priceId, int subjectId) {
        return offerBook[marketId][priceId][subjectId];
    }
    
    /** Return the full offer book data structure */
    public int[][][] getOfferBook() {
        return offerBook;
    }
    
    /** Return the int[][] for the given market. This book is indexed by price id then
     *  subject id */
    public int[][] getMarketBook(int marketId) {
        return offerBook[marketId];
    }
    
    /** Return the int[] for the given market and price level */
    public int[] getPriceBook(int marketId, int priceId) {
        return offerBook[marketId][priceId];
    }
    
    /** Return the price that corresponds to the given market and price level id */
    public float getPrice(int marketId, int priceId) {
        return prices[marketId][priceId];
    }
    
    /** Set the prices array */
    public void setPrices(float[][] prices) {
        this.prices = prices;
    }
    
    /** Get the prices array */
    public float[][] getPrices() {
        return prices;
    }
    
    /** Return the number of price levels per market */
    public int getNumPrices() {
        return prices[0].length;
    }
    
    /** Return the number of markets */
    public int getNumMarkets() {
        return offerBook.length;
    }
    
    /** Return the amount of cash tied up in bid orders (the cash obligations) for the
     *  given client. Ignore the cash promise of ask orders */
    public float getCashObligations(int subjectId) {
        float obligations = 0.0f;
        int[][][] orders = offerBook;
        
        for (int m=0; m<orders.length; m++) {
            for (int i=0; i<orders[m].length; i++) {
                int numOrders = orders[m][i][subjectId];
                if (numOrders > 0)
                    obligations += (numOrders * getPrice(m, i));
            }
        }
        
        return obligations;
    }
    
    /** Return the amount of cash tied up in bid orders and released in ask orders
     *  for the given client in the given market */
    public float getExecCash(int marketId, int subjectId) {
        float execCash = 0f;
        
        for (int i=0; i<offerBook[marketId].length; i++) {
            int numOrders = offerBook[marketId][i][subjectId];
            execCash -= (numOrders * getPrice(marketId, i));
        }
        
        return execCash;
    }
    
    /** Return the amount of cash that the client will receive if all of that
     *  client's bids and asks are executed in all markets */
    public float getExecCash(int subjectId) {
        float execCash = 0f;
        
        for (int m=0; m<offerBook.length; m++)
            execCash += getExecCash(m, subjectId);
        
        return execCash;
    }
    
    /** Return the total number of buy orders for all prices greater than or equal
     *  to the given price on the given market */
    public int getGreaterThanOrEqualToBidOrders(int marketId, int priceId, int subjId) {
        int numBids = 0;
        
        for (int i=priceId; i<offerBook[marketId].length; i++) {
            if(subjId<0)
                numBids += getTotalBids(marketId, i);
            else
                numBids += this.getMyTotalBids(marketId, i, subjId);
        }
        
        return numBids;
    }
    
    /** Return the total number of sell orders for all prices less than or equal
     *  to the given price on the given market */
    public int getLessThanOrEqualToAskOrders(int marketId, int priceId, int subjId) {
        int numAsks = 0;
        
        for (int i=priceId; i >=0; i--) {
            if(subjId <0)
                numAsks += getTotalAsks(marketId, i);
            else
                numAsks += this.getMyTotalAsks(marketId, i, subjId); 
        }
        
        return numAsks;
    }
    
    /** The main data structure for the offer book indexed by marketID then priceID then subject ID */
    public int[][][] offerBook;
    
    /** The prices corresponding to each of the price levels (indexed by market id then price id) */
    private float[][] prices;
}
