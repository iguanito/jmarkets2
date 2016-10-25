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
 * StockBondBankruptcyFunction.java
 *
 * Created on Oct 19, 2004, 11:21 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.OfferBook;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;

/**
 *
 * @author  Walter M. Yuan
 * @version $Id: GenericBankruptcyFunction.java 239 2005-06-29 18:35:52Z raj $
 */
public class GenericLtdBankruptcyFunction implements BankruptcyFunction {
    
    /** Creates a new instance of StockBondBankruptcyFunction */
    public GenericLtdBankruptcyFunction() {
    }
    
    public boolean validateOffer(AbstractOffer offer, Trader trader, OfferBook offerBook, float bankruptcyCutoff) {
        int s = offer.getSubjectId();
        int m1 = offer.getMarketId();
        
        int[] qualifiedSecurityHoldings = trader.getHoldings().clone();
        float qualifiedCashHoldings = trader.getCash();
        
        qualifiedSecurityHoldings[offer.getMarketId()] += offer.getExecSecurityChange(offerBook);
        qualifiedCashHoldings += offer.getExecCashChange(offerBook);
        
        //System.out.println("Market " + m1 + " post new-order holdings: " + qualifiedSecurityHoldings[m1]);
        //System.out.println("Post new-order cash holdings: " + qualifiedCashHoldings);
        
        //Check the bidding side for qualified standing orders
        for (int m=0; m<qualifiedSecurityHoldings.length; m++) {
            int highBidPriceLevel = offerBook.getHighestBid(m);
            if (highBidPriceLevel == -1)
                continue;
            
            float highBidPrice = offerBook.getPrice(m, highBidPriceLevel);
            float qualifyLevel = 0.75f * highBidPrice;
            
            for (int p=0; p<offerBook.getNumPrices(); p++) {
                float price = offerBook.getPrice(m, p);
                boolean qualified = price > qualifyLevel;
                
                int numBids = offerBook.getBids(m, p, s);
                if (qualified && numBids > 0) {
                    qualifiedSecurityHoldings[m] += numBids;
                    qualifiedCashHoldings -= (numBids * price);
                }
            }
        }
        
        //Check the ask side for qualified standing orders
        for (int m=0; m<qualifiedSecurityHoldings.length; m++) {
            int lowAskPriceLevel = offerBook.getLowestAsk(m);
            if (lowAskPriceLevel == -1)
                continue;
            
            float lowAskPrice = offerBook.getPrice(m, lowAskPriceLevel);
            float qualifyLevel = 1.25f * lowAskPrice;
            
            for (int p=0; p<offerBook.getNumPrices(); p++) {
                float price = offerBook.getPrice(m, p);
                boolean qualified = price < qualifyLevel;
                
                int numAsks = offerBook.getAsks(m, p, s);
                if (qualified && numAsks > 0) {
                    qualifiedSecurityHoldings[m] -= numAsks;
                    qualifiedCashHoldings += (numAsks * price);
                }
            }
        }
        
        //System.out.println("Market " + m1 + " post standing order holdings: " + qualifiedSecurityHoldings[m1]);
        //System.out.println("Post standing order cash holdings: " + qualifiedCashHoldings);
        
        boolean[] stateChecks = new boolean[numOfStates];
        
        for(int j=0; j<stateChecks.length; j++) {
            float payoff = 0;
            
            for(int i=0; i<qualifiedSecurityHoldings.length; i++)
                payoff += qualifiedSecurityHoldings[i] * securityPayoffs[i][j];
            
            payoff += qualifiedCashHoldings * securityPayoffs[securityPayoffs.length-1][j];
            
            //System.out.println("Total payoff in state " + j +": " + payoff);
            stateChecks[j] = payoff >= bankruptcyCutoff;
        }
        
        for(int i=0; i<stateChecks.length; i++){
            if(!stateChecks[i])   {
                offer.setInvalidMessage("The proposed order violates the bankruptcy constraint");
                return false;
            }
        }
        
        return true;
    }
    
    /** Given an offer with a cancel action, return the action that the cancel is canceling. That is,
     *  if we are canceling a bid order, return a BUY_ACTION; if we are canceling a sell order,
     *  then return a SELL_ACTION */
    private int getCancelAction(AbstractOffer offer, OfferBook offerBook) {
        int action = offer.getAction();
        if (action == JMConstants.CANCEL_ACTION) {
            int numBids = offerBook.getBids(offer.getMarketId(), offer.getPriceId(), offer.getSubjectId());
            int numAsks = offerBook.getBids(offer.getMarketId(), offer.getPriceId(), offer.getSubjectId());
            
            if (numBids > 0)
                return JMConstants.BUY_ACTION;
            else if (numAsks > 0)
                return JMConstants.SELL_ACTION;
        }
        
        return -1;
    }
    
    public String[] getFields() {
        return null;
    }
    
    public String[] getFields(int numOfSecurities, int numOfStates) {
        this.numOfSecurities = numOfSecurities;
        this.numOfStates = numOfStates;
        
        fields = new String[numOfSecurities + 1];
        for (int i=0; i<numOfSecurities; i++)
            fields[i] = "Security " + i;
        
        fields[numOfSecurities] = "Cash";
        return fields;
    }
    
    public void setField(String field, int state, String value) {
        if(field == null || value == null)
            return;
        
        if (securityPayoffs == null)
            securityPayoffs = new float[numOfSecurities + 1][numOfStates];
        
        if(fields != null){
            for(int i=0; i<fields.length; i++){
                if(field.equals(fields[i])){
                    try{
                        securityPayoffs[i][state] = Float.parseFloat(value);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public String getName() {
        return Fn_Name;
    }
    
    private float[][] securityPayoffs;
    private String[] fields;
    
    private int numOfSecurities;
    private int numOfStates;
    
    public static final String Security_Payoff = "Period_Security_Payoff";
    
    public static final String Fn_Name = "Generic_Market";
}
