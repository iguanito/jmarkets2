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
import java.util.List;

/**
 * A composite offer is an offer that contains a list of other offers. Offers can be
 * added onto this composite offer, and the server will process them together as one
 * transaction. This composite pattern works by overriding all the methods in the AbstractOffer
 * such that calls to those methods are routed to the 'active' offer in this composite.
 * The 'active' offer is the offer that is currently being processed by the server.
 *
 * @author  Raj Advani
 */
public class CompositeOffer extends AbstractOffer {
    
    /**
     * Creates a new instance of CompositeOffer
     */
    public CompositeOffer() {
    }
    
    public void setNumOffers(int numOffers) {
        offers = new AbstractOffer[numOffers];
    }
    
    public int getNumOffers() {
        return offers.length;
    }
    
    /** In a composite offer, when the server indicates that we are finished processing,
     *  increment the index so that the next offer will be processed. Only set to completed
     *  when the last offer has been fully processed */
    public void finishedProcessing() {
        if (activeOffer == (offers.length - 1))
            completed = true;
        else
            activeOffer++;
    }
    
    public AbstractOffer[] getOffers() {
        return offers;
    }
    
    public void setOffers(AbstractOffer[] offers) {
        this.offers = offers;
    }
    
    public void setOffer(int index, AbstractOffer offer) {
        offers[index] = offer;
    }
    
    public AbstractOffer getOffer(int index) {
        return offers[index];
    }
    
    public int getAction() {
        return offers[activeOffer].getAction();
    }
    
    public void setAction(int action) {
        offers[activeOffer].setAction(action);
    }
    
    public int getUnits() {
        return offers[activeOffer].getUnits();
    }
    
    public void setUnits(int units) {
        offers[activeOffer].setUnits(units);
    }
    
    public float getPrice() {
        return offers[activeOffer].getPrice();
    }
    
    public void setPrice(float price) {
        offers[activeOffer].setPrice(price);
    }
    
    public int getMarketId() {
        return offers[activeOffer].getMarketId();
    }
    
    public void setMarketId(int marketId) {
        offers[activeOffer].setMarketId(marketId);
    }
    
    public int getPriceId() {
        return offers[activeOffer].getPriceId();
    }
    
    public void setPriceId(int priceId) {
        offers[activeOffer].setPriceId(priceId);
    }
    
    public int getSubjectId() {
        return offers[activeOffer].getSubjectId();
    }
    
    public void setSubjectId(int subjectId) {
        for (int i=0; i<offers.length; i++)
            offers[i].setSubjectId(subjectId);
    }
    
    public int getSubjectId_db() {
        return offers[activeOffer].getSubjectId_db();
    }
    
    public void setSubjectId_db(int subjectId_db) {
        for (int i=0; i<offers.length; i++)
            offers[i].setSubjectId_db(subjectId_db);
    }
    
    public long getTime() {
        return offers[activeOffer].getTime();
    }
    
    public void setTime(long time) {
        for (int i=0; i<offers.length; i++)
            offers[i].setTime(time);
    }
    
    public int getId_db() {
        return offers[activeOffer].getId_db();
    }
    
    public void setId_db(int id_db) {
        offers[activeOffer].setId_db(id_db);
    }
    
    public String getInvalidMessage() {
        return offers[activeOffer].getInvalidMessage();
    }
    
    public void setInvalidMessage(String invalidMessage) {
        offers[activeOffer].setInvalidMessage(invalidMessage);
    }
    
    /** Validates this offer against the given offer book, given the group definition. If the
     *  offer is invalid, return false and set the invalid message string. If the offer is valid
     *  return true. */
    public boolean validate(Trader trader, OfferBook offerBook) {
        return offers[activeOffer].validate(trader, offerBook);
    }
    
    /** Returns the cash obligations entailed by this offer. That is, how much cash
     *  is promised (held up by) this order */
    public float getCashObligations(OfferBook offerBook) {
        float obligations = 0f;
        
        for (int i=0; i<offers.length; i++)
            obligations += offers[i].getCashObligations(offerBook);
        
        return obligations;
    }
    
    /** Returns the security obligations entailed by this offer. That is, how much
     *  of this security is promised by the order */
    public int getSecurityObligations(OfferBook offerBook) {
        int obligations = 0;
        
        for (int i=0; i<offers.length; i++)
            obligations += offers[i].getSecurityObligations(offerBook);
        
        return obligations;
    }
    
    /** Returns the change in cash of this trader if this offer is executed */
    public float getExecCashChange(OfferBook offerBook) {
        int obligations = 0;
        
        for (int i=0; i<offers.length; i++)
            obligations += offers[i].getExecCashChange(offerBook);
        
        return obligations;
    }
    
    /** Returns the change in security holdings of this trader if this offer
     *  is executed */
    public int getExecSecurityChange(OfferBook offerBook) {
        int obligations = 0;
        
        for (int i=0; i<offers.length; i++)
            obligations += offers[i].getExecSecurityChange(offerBook);
        
        return obligations;
    }
    
    /** Prepend the given List of offers to this composite offer */
    public void prependOffers(List preOffers) {
        AbstractOffer[] newOffers = new AbstractOffer[preOffers.size() + offers.length];
        
        for (int i=0; i<preOffers.size(); i++)
            newOffers[i] = (AbstractOffer) preOffers.get(i);
        
        for (int i=preOffers.size(); i < (preOffers.size() + offers.length); i++)
            newOffers[i] = offers[i - preOffers.size()];
        
        this.offers = newOffers;
    }
    
    /** The index of the currently active offer in this composite */
    protected int activeOffer;
    
    /** The array of offers that composes this composite offer */
    protected AbstractOffer[] offers;
}
