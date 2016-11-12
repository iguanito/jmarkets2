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
 * MarketDef.java
 *
 * Created on March 18, 2004, 6:48 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def;

import java.util.Hashtable;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;

/**
 *
 * Contains all the information about the market being played
 *
 * @author  Raj Advani
 * @version $Id: MarketDef.java 224 2005-04-11 17:36:59Z raj $
 */
public class MarketDef implements java.io.Serializable {
    
    /**
     * Creates a new instance of MarketDef
     */
    public MarketDef(int numMarkets, String[] marketTitles, float[] minPrices, float[] maxPrices, int[] marketTime, int numDivisions, boolean useGui, boolean useEffCashValidation) {
        this.numMarkets = numMarkets;
        this.marketTitles = marketTitles;
        this.minPrices = minPrices;
        this.maxPrices = maxPrices;
        this.marketTime = marketTime;
        this.numDivisions = numDivisions;
        this.useGui = useGui;
        this.useEffCashValidation = useEffCashValidation;
        generatePrices();
    }
    
    public MarketDef(int numMarkets, String[] marketTitles, float[] minPrices, float[] maxPrices, int[] marketTime, int numDivisions) {
        this(numMarkets, marketTitles, minPrices, maxPrices, marketTime, numDivisions, false, false);
        /*this.numMarkets = numMarkets;
        this.marketTitles = marketTitles;
        this.minPrices = minPrices;
        this.maxPrices = maxPrices;
        this.marketTime = marketTime;
        this.numDivisions = numDivisions;
         */
        generatePrices();
    }
    
    
    public MarketDef(Request req) {
        this.numMarkets = req.getIntInfo("numMarkets");
        this.marketTitles = (String[]) req.getInfo("marketTitles");
        this.minPrices = (float[]) req.getInfo("minPrices");
        this.maxPrices = (float[]) req.getInfo("maxPrices");
        this.numDivisions = req.getIntInfo("numDivisions");
        generatePrices();
    }
    
    /** Generate the price levels for each market given the minPrices,
     *  maxPrices and numDivisions. Round these prices to two decimal places */
    public void generatePrices() {
        prices = new float[numMarkets][numDivisions];
        
        for (int i=0; i<numMarkets; i++) {
            float maxPrice = maxPrices[i];
            float minPrice = minPrices[i];
            
            float increment = (maxPrice - minPrice) / (numDivisions-1);
            float price = minPrice;
            
            for (int j=0; j<numDivisions; j++) {
                float roundedPrice = (float) Math.round(price * 100) / 100;
                prices[i][j] = roundedPrice;
                price += increment;
            }
        }
        
        priceIds = new Hashtable[numMarkets];
        for (int i=0; i<numMarkets; i++)
            priceIds[i] = new Hashtable();
        
        periodSecurityIds = new int[numMarkets];
        securityIds = new int[numMarkets];
    }
    
    public float getIncrement(int market) {
        return (maxPrices[market] - minPrices[market]) / (numDivisions-1);
    }
    
    public float[][] getPrices() {
        return prices;
    }
    
    public void setNumMarkets(int numMarkets) {
        this.numMarkets = numMarkets;
    }
    
    public int getNumMarkets() {
        return numMarkets;
    }
    
    public void setMarketTitles(String[] marketTitles) {
        this.marketTitles = marketTitles;
    }
    
    public String[] getMarketTitles() {
        return marketTitles;
    }
    
    public void setMinPrices(float[] minPrices) {
        this.minPrices = minPrices;
    }
    
    public float[] getMinPrices() {
        return minPrices;
    }
    
    public void setMaxPrices(float[] maxPrices) {
        this.maxPrices = maxPrices;
    }
    
    public float[] getMaxPrices() {
        return maxPrices;
    }
    
    public void setNumDivisions(int numDivisions) {
        this.numDivisions = numDivisions;
    }
    
    public int getNumDivisions() {
        return numDivisions;
    }
    
    public void setPriceId_db(int market, float price, int id) {
        priceIds[market].put(new Float(price), new Integer(id));
        priceIds[market].put(new Integer(id), new Float(price));
    }
    
    /** Get the price given the market and normal price id */
    public float getPriceWithNormalId(int market, int id) {
        return prices[market][id];
    }
    
    /** Get the price database id given the market and price */
    public int getPriceId_db(int market, float price) {
        Integer pid = (Integer) priceIds[market].get(new Float(price));
        return pid.intValue();
    }
    
    /** Get the price given the market and database price id */
    public float getPrice(int market, int id_db) {
        Float price = (Float) priceIds[market].get(new Integer(id_db));
        return price.floatValue();
    }
    
    /** Get the price id given the database price id */
    public int getPriceId(int market, int id_db) {
        float price = getPrice(market, id_db);
        
        float[] priceLevels = prices[market];
        for (int i=0; i<priceLevels.length; i++) {
            if (priceLevels[i] == price)
                return i;
        }
        return -1;
    }
    
    public void setPeriodSecurityId(int market, int id) {
        periodSecurityIds[market] = id;
    }
    
    public int getPeriodSecurityId(int market) {
        return periodSecurityIds[market];
    }
    
    public void setSecurityId(int market, int id) {
        securityIds[market] = id;
    }
    
    public int getSecurityId(int market) {
        return securityIds[market];
    }
    
    /** Get the market id corresponding to the given security id. Return -1 if the
     *  given security id does not exist */
    public int getMarketId(int securityId) {
        for (int i=0; i<securityIds.length; i++) {
            if (securityIds[i] == securityId)
                return i;
        }
        return -1;
    }
    
    public int[] getMarketTime() {
        return marketTime;
    }
    
    public void setMarketTime(int[] marketTime) {
        this.marketTime = marketTime;
    }
    
    public int getMarketTime(int market) {
        return marketTime[market];
    }
    
    public void setMarketTime(int market, int time) {
        marketTime[market] = time;
    }
    
    public void setUseGui(boolean useGui){
        this.useGui = useGui;
    }
    
    public boolean getUseGui(){
        return useGui;
    }
    
    public void setUseEffCashValidation(boolean b){
        this.useEffCashValidation = b;
    }
    
    public boolean getUseEffCashValidation(){
        return useEffCashValidation;
    }
    
    private int numMarkets;
    private String[] marketTitles;
    private float[] minPrices;
    private float[] maxPrices;
    private int numDivisions;
    private float[][] prices;
    private int[] marketTime;
    private boolean useGui;
    private boolean useEffCashValidation;
    
    /** Maps each price level to its database id and reverse */
    private Hashtable[] priceIds;
    
    /** Maps each market number to its database id in the securities table */
    private int[] securityIds;
    
    /** Maps each market number to its database id in the period_securities table */
    private int[] periodSecurityIds;
}
