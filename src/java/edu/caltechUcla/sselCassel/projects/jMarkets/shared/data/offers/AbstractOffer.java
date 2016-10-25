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
 * BasicOffer.java
 *
 * Created on March 28, 2004, 7:15 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.BankruptcyFunction;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.functions.*;

/**
     * Convenience class that contains a number of fields that represent an offer
 * made by a client
 *
 * @author  Raj Advani
 */
public abstract class AbstractOffer implements java.io.Serializable {
    
    public int getAction() {
        return action;
    }
    
    public void setAction(int action) {
        this.action = action;
    }
    
    public int getUnits() {
        return units;
    }
    
    public void setUnits(int units) {
        this.units = units;
    }
    
    public float getPrice() {
        return price;
    }
    
    public void setPrice(float price) {
        this.price = price;
    }
    
    public int getMarketId() {
        return marketId;
    }
    
    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }
    
    public int getPriceId() {
        return priceId;
    }
    
    public void setPriceId(int priceId) {
        this.priceId = priceId;
    }
    
    public int getSubjectId() {
        return subjectId;
    }
    
    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }
    
    public int getSubjectId_db() {
        return subjectId_db;
    }
    
    public void setSubjectId_db(int subjectId_db) {
        this.subjectId_db = subjectId_db;
    }
    
    public long getTime() {
        return time;
    }
    
    public void setTime(long time) {
        this.time = time;
    }
    
    public int getId_db() {
        return id_db;
    }
    
    public void setId_db(int id_db) {
        this.id_db = id_db;
    }
    
    public String getInvalidMessage() {
        return invalidMessage;
    }
    
    public void setInvalidMessage(String invalidMessage) {
        this.invalidMessage = invalidMessage;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setUseEffCashValidation(boolean b){
        this.useEffCashValidation = b;
    }
    
    public boolean getUseEffCashValidation(){
        return useEffCashValidation;
    }
    
    
    /** Returns the cash obligations entailed by this offer. That is, how much cash
     *  is promised (held up by) this order. A cancel order will decrease obligations
     *  and a buy order will increase obligations. Sell orders do nothing to cash
     *  obligations. These obligations are what will be checked against the cash
     *  constraint */
    public abstract float getCashObligations(OfferBook offerBook);
    
    /** Returns the security obligations entailed by this offer. That is, how much
     *  of this security is promised by the order. A cancel order will decrease security
     *  obligations and an ask order will increase obligations. Bid orders do nothing
     *  to security obligations. These obligations are what will be checked against
     *  the short-sale constraint */
    public abstract int getSecurityObligations(OfferBook offerBook);
    
    /** Returns the change in cash of this trader if this offer is executed */
    public abstract float getExecCashChange(OfferBook offerBook);
    
    /** Returns the change in security holdings of this trader if this offer
     *  is executed */
    public abstract int getExecSecurityChange(OfferBook offerBook);
    
    /** Called when the server has finished processing the offer */
    public abstract void finishedProcessing();
    
    /** Validates this offer against the given offer book using the given trader. If the
     *  offer is invalid, return false and set the invalid message string. If the offer is valid
     *  return true. */
    public abstract boolean validate(Trader trader, OfferBook offerBook);
    
    /** Validate this offer against the short-sale constraint. Checks to make sure the
     *  security obligatoins of this order will not be greater than the short-sale
     *  constraint allows. This is one form of validation that may be used by the
     *  extending classes */
    protected boolean validateShortSaleConstraint(Trader trader, OfferBook offerBook) {
        int orders = offerBook.getMarketAsks(getMarketId(), getSubjectId());
        int constraint = trader.getShortSaleConstraint(getMarketId());
        int contract = getSecurityObligations(offerBook);
        int holdings = trader.getHoldings(getMarketId());
        
        //System.out.println("Validating short sale constraint with current orders: " + orders + ", constraint: " + constraint + ", contract: " + contract + ", and holdings: " + holdings);
        
        if ((orders + contract) > (constraint + holdings)) {
            setInvalidMessage("The proposed order violates the shortsale constraint.");
            return false;
        }
        
        return true;
    }
    
    protected boolean validateEffectiveCashConstraint(Trader trader, OfferBook offerBook) {
        ClientPayoffFunction cpf = trader.getClientPayoffFunction();
        float[][] pp = cpf.getPotentialPayoffs(trader.getPeriodNum());
        int[] holdings = trader.getHoldings();
        int numStates = cpf.getNumStates();
        int numSecurities = cpf.getNumSecurities();
        
        for( int state=0; state<numStates; state++ ) {
            float effCash = 0;
            for( int sec=0; sec<numSecurities; sec++ ) {
                effCash += holdings[sec]*pp[state][sec];
                if( sec==marketId ){
                    effCash += units*pp[state][sec];
                }
            }
            effCash += trader.getCash();
            //System.out.println("EffCash (state="+state+", units="+units+", price="+price+")="+effCash);
            //System.out.println("price*units="+(price*units));
            if (price*units > effCash ){
                setInvalidMessage("You do not have enough cash for the proposed buy order.");
                return false;
            }
        }
        
        return true;
    }
    
    /** Validate this offer against the cash constraint. This is one form of validation
     *  that may be used by the extending classes */
    protected boolean validateCashConstraint(Trader trader, OfferBook offerBook) {
        if( useEffCashValidation ){
            //System.out.println("useEffCashValidation is TRUE: ");
            return validateEffectiveCashConstraint(trader, offerBook);
        } else {
            float contract = getCashObligations(offerBook);
            float currentObligations = offerBook.getCashObligations(getSubjectId());
            float cash = trader.getCash();
            
            System.out.println("Validating cash constraint with current obligations: " + currentObligations + ", contract: " + contract + ", and current cash: " + cash);
            
            if ((contract + currentObligations) > cash) {
                setInvalidMessage("You do not have enough cash for the proposed buy order.");
                return false;
            }
            
            return true;
        }
    }
    
    /** Validate this offer against the bankruptcy constraint */
    protected boolean validateBankruptcyConstraint(Trader trader, OfferBook offerBook) {
        BankruptcyFunction func = trader.getBankruptcyFunction();
        float cutoff = trader.getBankruptcyCutoff();
        
        return func.validateOffer(this, trader, offerBook, cutoff);
    }
    
    /** The action associated with this offer: buy, sell, or cancel */
    protected int action;
    
    /** The number of units requested in this offer */
    protected int units;
    
    /** The price level as a float value for the offer */
    protected float price;
    
    /** The market associated with the offer */
    protected int marketId;
    
    /** The price level integer id associated with the offer */
    protected int priceId;
    
    /** The subject's id within the session */
    protected int subjectId;
    
    /** The subject's database id */
    protected int subjectId_db;
    
    /** The time the offer was made, in game milliseconds */
    protected long time;
    
    /** The ID of this offer in the offer book */
    protected int id_db;
    
    /** If the offer is invalid, this String contains the reason why */
    protected String invalidMessage;
    
    /** True if this offer has been processed / transacted by the server */
    protected boolean completed;
    
    /** whether to use the relaxed "Effective" cash constraint validation */
    protected boolean useEffCashValidation;
}
