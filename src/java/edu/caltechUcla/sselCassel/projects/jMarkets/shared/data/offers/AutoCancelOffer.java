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
 * AutoCancelOffer.java
 *
 * Created on June 29, 2005, 2:29 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.OfferBook;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.BankruptcyFunction;
import java.util.Vector;

/**
 * This is a form of composite offer that automatically cancels inferior orders in order
 * to make some BasicOffer valid. For example, it automatically cancels inferior asks when
 * a new ask is made in order to not violate the short-sale constraint.
 *
 * @author Raj
 */
public class AutoCancelOffer extends CompositeOffer {
    
    /** Creates a new instance of AutoCancelOffer, with the given BasicOffer at the root.
     *  The function generateCancelOffer must be called in order to make this offer attempt
     *  to construct a valid composite (pre-pended cancels) offer */
    public AutoCancelOffer(BasicOffer rootOffer) {
        this.rootOffer = rootOffer;
        
        setNumOffers(1);
        setOffer(0, rootOffer);
    }
    
    /** Return true if this offer can be modified (pre-pended with cancels) in such a way that
     *  the basic offer at the root becomes valid. Note that role constraints and self-trade
     *  constraints cannot be fixed by any cancellation. This function operates recursively in 
     *  order to handle multiple constraint failures. True will only be returned when ALL constraints
     *  are validated */
    public boolean generateCancelOffer(Trader trader, OfferBook offerBook) {
        //System.out.println("Generating cancel offer in recursive loop with " + getNumOffers() + " offers");
        if (getNumOffers() > 50) {
            System.out.println("Canceling generation of auto-cancel offer due to system error");
            return false;
        }
        
        if (!rootOffer.validateRoleConstraints(trader) || !rootOffer.validateSelfTradeConstraint(offerBook)) {
            System.out.println("Role or self-trade constraint violated -- not attempting to generate an auto-cancel offer");
            return false;
        }
        
        boolean passedShortSale = validateShortSaleConstraint(trader, offerBook);
        boolean passedBankruptcy = validateBankruptcyConstraint(trader, offerBook);
        boolean passedCash = validateCashConstraint(trader, offerBook);
        
        System.out.println("Auto-cancellation constraint results: short sale-" + passedShortSale + ", bankruptcy-" + passedBankruptcy + ", cash-" + passedCash);
        
        if (!passedShortSale) {
            if (generateShortSaleCancel(trader, offerBook))
                return generateCancelOffer(trader, offerBook);
            else
                return false;
        }
        
        if (!passedBankruptcy) {
            if (generateBankruptcyCancel(trader, offerBook))
                return generateCancelOffer(trader, offerBook);
            else
                return false;
        }
        
        if (!passedCash) {
            if (generateCashCancel(trader, offerBook))
                return generateCancelOffer(trader, offerBook);
            else
                return false;
        }
        
        return true;
    }
    
    /** When we make a new bid or ask that violates the bankruptcy constraint, just check
     *  to see if that bid/ask will work after canceling a number of inferior orders. Do this
     *  by repeatedly checking the bankruptcy function with the holdings after making the
     *  cancellation, until we find the minimal cancellation that will work. The process is as
     *  follows:
     *
     *  1) Get the list of inferior offers from the offer book
     *  2) Iterate through the list. At each offer, change the executed holdings and cash data
     *     to reflect what would happen if that offer was canceled
     *  3) Add the potentially to-be-canceled offer to a vector
     *  4) Check to see if the bankruptcy constraint is satisfied if the offer is canceled
     *  5) If it is, then break out of the loop and add the cancel orders thus far stored
     *     in the vector to this composite offer
     *  6) If it isn't, then move on to the next inferior offer
     */
    private boolean generateBankruptcyCancel(Trader trader, OfferBook offerBook) {
        int action = getMainAction();
        int marketId = getMarketId();
        int subjectId = getSubjectId();
        int priceId = getMainPriceId();
        float price = getMainPrice();
        
        BankruptcyFunction bfunc = trader.getBankruptcyFunction();
        float bcut = trader.getBankruptcyCutoff();
        
        int[] inferiorOffers = null;
        if (action == JMConstants.BUY_ACTION)
            inferiorOffers = offerBook.getInferiorBids(marketId, priceId, subjectId);
        else
            inferiorOffers = offerBook.getInferiorAsks(marketId, priceId, subjectId);
        
        if (inferiorOffers == null)
            return false;
        
        boolean validated = false;
        Vector cancelOffers = new Vector();
        
        for (int i=0; i<inferiorOffers.length; i++) {
            int cancelPriceId = inferiorOffers[i];
            float cancelPrice = offerBook.getPrice(marketId, cancelPriceId);
            
            CancelOffer cancelOffer = new CancelOffer();
            cancelOffer.setUseEffCashValidation(useEffCashValidation);
            cancelOffer.setMarketId(marketId);
            cancelOffer.setSubjectId(subjectId);
            cancelOffer.setPriceId(cancelPriceId);
            cancelOffer.setPrice(cancelPrice);
            cancelOffer.setUnits(1);
            cancelOffers.add(cancelOffer);
            
            AutoCancelOffer validateTest = new AutoCancelOffer(rootOffer);
            validateTest.setUseEffCashValidation(this.useEffCashValidation);
            validateTest.setOffers(getOffers());
            validateTest.prependOffers(cancelOffers);
            
            if (bfunc.validateOffer(validateTest, trader, offerBook, bcut)) {
                validated = true;
                break;
            }
        }
        
        if (validated) {
            prependOffers(cancelOffers);
            return true;
        }
        
        return false;
    }
    
    /** Check to see how many bid orders we must cancel to make the basic offer given pass the
     *  cash constraint. If there are enough inferior offers to make this happen, then create
     *  a composite offer with the appropriate cancellations and return true. Return false
     *  if no such composite offer can be created */
    private boolean generateCashCancel(Trader trader, OfferBook offerBook) {
        //System.out.println("Generating cash cancel offer");
        
        int marketId = getMarketId();
        int subjectId = getSubjectId();
        int priceId = getMainPriceId();
        float price = getMainPrice();
        int units = getUnits();
        
        float contract = getCashObligations(offerBook);
        float currentObligations = trader.getCashObligations(offerBook);
        float cash = trader.getCash();
        
        float needed = contract + currentObligations - cash;
        //System.out.println("Need " + needed + " cash for order to go through");
        
        if (needed <= 0f)
            return false;
        
        int[] inferiorOffers = offerBook.getInferiorBids(marketId, priceId, subjectId);
        
        if (inferiorOffers == null) {
            //System.out.println("Inferior orders null");
            return false;
        }
        
        Vector cancelOffers = new Vector();
        boolean validated = false;
        
        for (int i=0; i<inferiorOffers.length; i++) {
            int cancelPriceId = inferiorOffers[i];
            float cancelPrice = offerBook.getPrice(marketId, cancelPriceId);
            
            needed -= cancelPrice;
            
            CancelOffer cancelOffer = new CancelOffer();
            cancelOffer.setUseEffCashValidation(useEffCashValidation);
            cancelOffer.setMarketId(marketId);
            cancelOffer.setSubjectId(subjectId);
            cancelOffer.setPriceId(cancelPriceId);
            cancelOffer.setPrice(cancelPrice);
            cancelOffer.setUnits(1);
            cancelOffers.add(cancelOffer);
            System.out.println("Canceling offer with price " + cancelPrice + " to satisfy cash constraint");
            if (needed <= 0f) {
                validated = true;
                break;
            }
        }
        
        if (validated) {
            prependOffers(cancelOffers);
            return true;
        }
        
        return false;
    }
    
    /** Check to see how many units we must cancel to make given basic offer valid. If there are enough inferior
     *  units (inferior to the basic offer) then create a composite order with the cancellations required
     *  and return true. Return false if no composite offer could be created to make the short-sale valid */
    private boolean generateShortSaleCancel(Trader trader, OfferBook offerBook) {
        int marketId = getMarketId();
        int subjectId = getSubjectId();
        int priceId = getMainPriceId();
        float price = getMainPrice();
        
        int contract = getSecurityObligations(offerBook);
        int orders = offerBook.getMarketAsks(marketId, subjectId);
        int constraint = trader.getShortSaleConstraint(marketId);
        int holdings = trader.getHoldings(marketId);
        
        int numUnitsNeeded = (contract + orders) - (holdings + constraint);
        
        if (numUnitsNeeded <= 0)
            return false;
        
        int[] inferiorOrders = offerBook.getInferiorAsks(marketId, priceId, subjectId);
        
        if (inferiorOrders == null || inferiorOrders.length < numUnitsNeeded) {
            return false;
        }
        
        Vector cancelOffers = new Vector();
        
        int index = 0;
        while (numUnitsNeeded > 0) {
            int cancelPriceId = inferiorOrders[index];
            float cancelPrice = offerBook.getPrice(marketId, cancelPriceId);
            int cancelUnits = countOccurences(cancelPriceId, inferiorOrders);
            
            if (cancelUnits > numUnitsNeeded)
                cancelUnits = numUnitsNeeded;
            
            CancelOffer cancelOffer = new CancelOffer();
            cancelOffer.setUseEffCashValidation(useEffCashValidation);
            cancelOffer.setMarketId(marketId);
            cancelOffer.setSubjectId(subjectId);
            cancelOffer.setPriceId(cancelPriceId);
            cancelOffer.setPrice(cancelPrice);
            cancelOffer.setUnits(cancelUnits);
            
            cancelOffers.add(cancelOffer);
            System.out.println("Canceling offer with price " + cancelPrice + " to satisfy short-sale constraint");
            index += cancelUnits;
            numUnitsNeeded -= cancelUnits;
        }
        
        prependOffers(cancelOffers);
        return true;
    }
    
    /** Count the number of occurrences of the given number in the given array */
    private int countOccurences(int n, int[] array) {
        int count = 0;
        
        for (int i=0; i<array.length; i++) {
            if (array[i] == n)
                count++;
        }
        return count;
    }
    
    private int getMainAction() {
        return offers[offers.length - 1].getAction();
    }
    
    public float getMainPrice() {
        return offers[offers.length - 1].getPrice();
    }
    
    public int getMainPriceId() {
        return offers[offers.length - 1].getPriceId();
    }
    
    /** The buy or sell offer that this autocancel offer is attempting to make valid through
     *  pre-pended cancels */
    private BasicOffer rootOffer;
}
