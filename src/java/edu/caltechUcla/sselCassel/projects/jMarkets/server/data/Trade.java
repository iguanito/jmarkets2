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
 * Transaction.java
 *
 * Created on April 2, 2004, 9:19 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.data;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;

/**
 * This object represents a single trade (iteration) of a transaction. It is created
 * and the pre-values are filled in the TradeServ. The post values are filled by the
 * DBWriter upon writing the trade to the database
 *
 * @author  Raj Advani
 */
public class Trade {
    
    /** Creates a new instance of Trade */
    public Trade(AbstractOffer standingOffer, AbstractOffer newOffer, int unitsTraded, int transId) {
        if (standingOffer.getAction() == JMConstants.BUY_ACTION) {
            bidParty = standingOffer.getSubjectId();
            bidParty_db = standingOffer.getSubjectId_db();
            
            askParty = newOffer.getSubjectId();
            askParty_db = newOffer.getSubjectId_db();
        } else {
            bidParty = newOffer.getSubjectId();
            bidParty_db = newOffer.getSubjectId_db();
            
            askParty = standingOffer.getSubjectId();
            askParty_db = standingOffer.getSubjectId_db();
        }
        
        marketId = standingOffer.getMarketId();
        this.standingOffer = standingOffer;
        this.newOffer = newOffer;
        this.transId = transId;
        this.unitsTraded = unitsTraded;
    }
    
    public AbstractOffer getNewOffer() {
        return newOffer;
    }
    
    public void setNewOffer(AbstractOffer newOffer) {
        this.newOffer = newOffer;
    }
    
    public AbstractOffer getStandingOffer() {
        return standingOffer;
    }
    
    public void setStandingOffer(AbstractOffer standingOffer) {
        this.standingOffer = standingOffer;
    }
    
    public int getUnitsTraded() {
        return unitsTraded;
    }
    
    public void setUnitsTraded(int unitsTraded) {
        this.unitsTraded = unitsTraded;
    }
    
    public int getTransId() {
        return transId;
    }
    
    public void setTransId(int transId) {
        this.transId = transId;
    }
    
    public float getPreBidCash() {
        return preBidCash;
    }
    
    public void setPreBidCash(float preBidCash) {
        this.preBidCash = preBidCash;
    }
    
    public float getPreAskCash() {
        return preAskCash;
    }
    
    public void setPreAskCash(float preAskCash) {
        this.preAskCash = preAskCash;
    }
    
    public int getPreBidSec() {
        return preBidSec;
    }
    
    public void setPreBidSec(int preBidSec) {
        this.preBidSec = preBidSec;
    }
    
    public int getPreAskSec() {
        return preAskSec;
    }
    
    public void setPreAskSec(int preAskSec) {
        this.preAskSec = preAskSec;
    }
    
    public float getPostBidCash() {
        return postBidCash;
    }
    
    public void setPostBidCash(float postBidCash) {
        this.postBidCash = postBidCash;
    }
    
    public float getPostAskCash() {
        return postAskCash;
    }
    
    public void setPostAskCash(float postAskCash) {
        this.postAskCash = postAskCash;
    }
    
    public int getPostBidSec() {
        return postBidSec;
    }
    
    public void setPostBidSec(int postBidSec) {
        this.postBidSec = postBidSec;
    }
    
    public int getPostAskSec() {
        return postAskSec;
    }
    
    public void setPostAskSec(int postAskSec) {
        this.postAskSec = postAskSec;
    }
    
    public int getBidParty() {
        return bidParty;
    }
    
    public void setBidParty(int bidParty) {
        this.bidParty = bidParty;
    }
    
    public int getAskParty() {
        return askParty;
    }
    
    public void setAskParty(int askParty) {
        this.askParty = askParty;
    }
    
    public int getBidParty_db() {
        return bidParty_db;
    }
    
    public void setBidParty_db(int bidParty_db) {
        this.bidParty_db = bidParty_db;
    }
    
    public int getAskParty_db() {
        return askParty_db;
    }
    
    public void setAskParty_db(int askParty_db) {
        this.askParty_db = askParty_db;
    }
    
    public int getMarketId() {
        return marketId;
    }
    
    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }
    
    private AbstractOffer newOffer;
    
    private AbstractOffer standingOffer;
    
    /**
     * The number of units traded (i.e. reduced from the standing offer)
     */
    private int unitsTraded;
    
    /** The database ID for the transaction this is a part of */
    private int transId;
    
    private float preBidCash;
    
    private float preAskCash;
    
    private int preBidSec;
    
    private int preAskSec;
    
    private float postBidCash;
    
    private float postAskCash;
    
    private int postBidSec;
    
    private int postAskSec;
    
    private int bidParty;
    
    private int askParty;
    
    private int bidParty_db;
    
    private int askParty_db;
    
    /**
     * The market ID of this trade
     */
    private int marketId;
}
