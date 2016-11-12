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
 * This is a cancellation offer. It differs from basic offers only in its validation routines
 *
 * @author  Raj Advani
 */
public class CancelOffer extends AbstractOffer {
    
    /**
     * Creates a new instance of BasicOffer
     */
    public CancelOffer() {
        action = JMConstants.CANCEL_ACTION;
    }
    
    public void finishedProcessing() {
        completed = true;
    }
    
    /** Return the action that this cancel offer is canceling. That is, if we are canceling
     *  a bid order, return a BUY_ACTION; if we are canceling a sell order, then return a SELL_ACTION */
    public int getCancelAction(OfferBook offerBook) {
        int numBids = offerBook.getBids(getMarketId(), getPriceId(), getSubjectId());
        int numAsks = offerBook.getAsks(getMarketId(), getPriceId(), getSubjectId());
        
        if (numBids > 0)
            return JMConstants.BUY_ACTION;
        else if (numAsks > 0)
            return JMConstants.SELL_ACTION;
        
        return -1;
    }
    
    /** Validate this offer against the cancel constraint (only applies if this is a cancel
     *  offer). Simply ensures that this client has the given amount of units to cancel */
    private boolean validateCancelConstraint(OfferBook offerBook) {
        if (action == JMConstants.CANCEL_ACTION) {
            int numOrders = offerBook.getOrders(getMarketId(), getPriceId(), subjectId);
            if (numOrders < 0)
                numOrders = -1 * numOrders;
            
            if (getUnits() > numOrders) {
                setInvalidMessage("You do not have that amount of orders to cancel.");
                return false;
            }
        }
        
        return true;
    }
    
    /** Validates this offer against the given offer book, given the group definition. If the
     *  offer is invalid, return false and set the invalid message string. If the offer is valid
     *  return true. */
    public boolean validate(Trader trader, OfferBook offerBook) {
        if (!validateCancelConstraint(offerBook))
            return false;
        
        if (!validateBankruptcyConstraint(trader, offerBook))
            return false;
        
        return true;
    }
    
    /** Returns the cash obligations entailed by this offer. That is, how much cash
     *  is promised (held up by) this order */
    public float getCashObligations(OfferBook offerBook) {
        int action = getCancelAction(offerBook);
        
        if (action == JMConstants.BUY_ACTION)
            return price * units * -1f;
        
        return 0f;
    }
    
    /** Returns the security obligations entailed by this offer. That is, how much
     *  of this security is promised by the order */
    public int getSecurityObligations(OfferBook offerBook) {
        int action = getCancelAction(offerBook);
       
        if (action == JMConstants.SELL_ACTION)
            return units * -1;
        
        return 0;
    }
    
    /** Returns the change in cash of this trader if this offer is executed */
    public float getExecCashChange(OfferBook offerBook) {
        int action = getCancelAction(offerBook);
        
        if (action == JMConstants.BUY_ACTION)
            return price * units;
        
        else
            return price * units * -1;
    }
    
    /** Returns the change in security holdings of this trader if this offer
     *  is executed */
    public int getExecSecurityChange(OfferBook offerBook) {
        int action = getCancelAction(offerBook);
        
        if (action == JMConstants.BUY_ACTION)
            return units * -1;
        
        else
            return units;
    }
}
