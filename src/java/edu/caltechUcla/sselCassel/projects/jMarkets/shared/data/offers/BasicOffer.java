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

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.GroupDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.BankruptcyFunction;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;

/**
 * This is a normal bid/ask offer. It contains methods to validate itself given
 * an offerbook and the group definitions.
 *
 * @author  Raj Advani
 */
public class BasicOffer extends AbstractOffer {
    
    /**
     * Creates a new instance of BasicOffer
     */
    public BasicOffer() {
    }
    
    public void finishedProcessing() {
        completed = true;
    }
    
    /** Validates this offer against the role constraints. If a constraint is violated,
     *  then set the invalid message and return false */
    public boolean validateRoleConstraints(Trader trader) {
        if (action == JMConstants.BUY_ACTION && !trader.canBid(marketId)) {
            setInvalidMessage("You are not allowed to make buy orders on this security for this period");
            return false;
        }
        if (action == JMConstants.SELL_ACTION && !trader.canAsk(marketId)) {
            setInvalidMessage("You are not allowed to make sell orders on this security for this period");
            return false;
        }
        
        return true;
    }
    
    /** Validate this offer against the 'trade with self' constraints. That is, ensure
     *  that this offer will not result in a trade with oneself */
    public boolean validateSelfTradeConstraint(OfferBook offerBook) {
        if (action == JMConstants.BUY_ACTION) {
            int[][] marketOrders = offerBook.getMarketBook(marketId);
            
            for (int i=0; i<marketOrders.length; i++) {
                float price = offerBook.getPrice(marketId, i);
                if ((marketOrders[i][subjectId] < 0) && (price <= getPrice() || i == getPriceId())) {
                    setInvalidMessage("<html><center>You cannot make this buy order.<br>You must first cancel your sell orders below and at this price</center></html>");
                    return false;
                }
            }
        }
        
        if (action == JMConstants.SELL_ACTION) {
            int[][] marketOrders = offerBook.getMarketBook(getMarketId());
            
            for (int i=0; i<marketOrders.length; i++) {
                float price = offerBook.getPrice(marketId, i);
                if ((marketOrders[i][subjectId] > 0) && (price >= getPrice() || i == getPriceId())) {
                    setInvalidMessage("<html><center>You cannot make this sell order.<br>You must first cancel your buy orders above and at this price</center></html>");
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /** Validates this offer against the given offer book, given the group definition. If the
     *  offer is invalid, return false and set the invalid message string. If the offer is valid
     *  return true. */
    public boolean validate(Trader trader, OfferBook offerBook) {
        if (!validateRoleConstraints(trader))
            return false;
        
        if (!validateSelfTradeConstraint(offerBook))
            return false;
        
        if (!validateCashConstraint(trader, offerBook))
            return false;
        
        if (!validateShortSaleConstraint(trader, offerBook))
            return false;
        
        if (!validateBankruptcyConstraint(trader, offerBook)) 
            return false;
        
        return true;
    }
    
    /** Returns the cash obligations entailed by this offer. That is, how much cash
     *  is promised (held up by) this order */
    public float getCashObligations(OfferBook offerBook) {
        if (action == JMConstants.BUY_ACTION)
            return price * units;
        
        return 0f;
    }
    
    /** Returns the security obligations entailed by this offer. That is, how much
     *  of this security is promised by the order */
    public int getSecurityObligations(OfferBook offerBook) {
        if (action == JMConstants.SELL_ACTION)
            return units;
        
        return 0;
    }
    
    /** Returns the change in cash of this trader if this offer is executed */
    public float getExecCashChange(OfferBook offerBook) {
        if (action == JMConstants.BUY_ACTION)
            return price * units * -1f;
        
        else
            return price * units;
    }
    
    /** Returns the change in security holdings of this trader if this offer
     *  is executed */
    public int getExecSecurityChange(OfferBook offerBook) {
        if (action == JMConstants.BUY_ACTION)
            return units;
        
        else
            return units * -1;
    }
}
