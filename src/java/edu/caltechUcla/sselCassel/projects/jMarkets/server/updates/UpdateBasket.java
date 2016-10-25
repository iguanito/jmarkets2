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
 * UpdateBasket.java
 *
 * Created on February 6, 2005, 10:31 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.updates;

import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.*;

/**
 *  Contains a Vector full of trade updates and an int containing the number
 *  of iterations processed in a transaction
 *
 * @author  Raj Advani
 */

public class UpdateBasket {
    
    public UpdateBasket(String code) {
        tradeUpdates = new Vector();
        offerBacklogUpdates = new Vector();
        numOffersUpdates = new Vector();
        priceChartUpdates = new Vector();
        metricsUpdates = new Vector();
        
        this.code = code;
    }
    
    public void addTradeUpdate(TradeUpdate update) {
        tradeUpdates.add(update);
    }
    
    public void addOfferBacklogUpdate(OfferBacklogUpdate update) {
        offerBacklogUpdates.add(update);
    }
    
    public void addPriceChartUpdate(PriceChartUpdate update) {
        priceChartUpdates.add(update);
    }
    
    public void addNumOffersUpdate(NumOffersUpdate update) {
        numOffersUpdates.add(update);
    }
    
    public void addMetricsUpdate(MetricsUpdate update) {
        metricsUpdates.add(update);
    }
    
    /**
     * Getter for property offerBacklogUpdates.
     * @return Value of property offerBacklogUpdates.
     */
    public Vector getOfferBacklogUpdates() {
        return offerBacklogUpdates;
    }
    
    /**
     * Setter for property offerBacklogUpdates.
     * @param offerBacklogUpdates New value of property offerBacklogUpdates.
     */
    public void setOfferBacklogUpdates(Vector offerBacklogUpdates) {
        this.offerBacklogUpdates = offerBacklogUpdates;
    }
    
    /**
     * Getter for property numOffersUpdates.
     * @return Value of property numOffersUpdates.
     */
    public Vector getNumOffersUpdates() {
        return numOffersUpdates;
    }
    
    /**
     * Setter for property numOffersUpdates.
     * @param numOffersUpdates New value of property numOffersUpdates.
     */
    public void setNumOffersUpdates(Vector numOffersUpdates) {
        this.numOffersUpdates = numOffersUpdates;
    }
    
    /**
     * Getter for property metricsUpdates.
     * @return Value of property metricsUpdates.
     */
    public Vector getMetricsUpdates() {
        return metricsUpdates;
    }
    
    /**
     * Setter for property metricsUpdates.
     * @param metricsUpdates New value of property metricsUpdates.
     */
    public void setMetricsUpdates(Vector metricsUpdates) {
        this.metricsUpdates = metricsUpdates;
    }
    
    /**
     * Getter for property priceChartUpdates.
     * @return Value of property priceChartUpdates.
     */
    public Vector getPriceChartUpdates() {
        return priceChartUpdates;
    }
    
    /**
     * Setter for property priceChartUpdates.
     * @param priceChartUpdates New value of property priceChartUpdates.
     */
    public void setPriceChartUpdates(Vector priceChartUpdates) {
        this.priceChartUpdates = priceChartUpdates;
    }
    
    /**
     * Getter for property tradeUpdates.
     * @return Value of property tradeUpdates.
     */
    public Vector getTradeUpdates() {
        return tradeUpdates;
    }
    
    /**
     * Setter for property tradeUpdates.
     * @param tradeUpdates New value of property tradeUpdates.
     */
    public void setTradeUpdates(Vector tradeUpdates) {
        this.tradeUpdates = tradeUpdates;
    }
    
    /** A string that identifies the transaction associated with this basket */
    private String code;
    
    /** List of the  trade updates that are to be sent to the clients. They are dumped in the
     *  event of a rollback */
    private Vector tradeUpdates;
    
    /** A queue of metrics updates to be sent to the admin GUI. The main transaction threads
     *  store metrics updates here, instead of calling the admin GUI itself, in order to
     *  speed up the transaction. The metrics update thread dumps these onto the admin GUI */
    private Vector metricsUpdates;
    
    /**  Similar in function to metricsUpdates, this contains price chart updates */
    private Vector priceChartUpdates;
    
    /** Similar in function to metricsUpdates, contains numOffers updates */
    private Vector numOffersUpdates;
    
    /** Similar in function to metricsUpdates, contains offerBacklog updates */
    private Vector offerBacklogUpdates;
}
