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
 * TradeEngine.java
 *
 * Created on February 9, 2005, 1:59 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.updates.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.PeriodDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;

/**
 *
 * @author  Raj Advani
 */
public interface TradeEngine {
    
    /** Initialize the TradeServ to handle the given period. The TradeServ creates a MarketState
     *  object to hold the cached market data of the period. Returns the Trader objects created
     *  for this period */
    Trader[] initPeriod(int sessionId, int periodNum, PeriodDef periodInfo, float[] initialCash, int[][] initialHoldings);
    
    /** Get the Trader objects associated with the given session */
    Trader[] getTraders(int sessionId);
    
    /** Terminate the given session */
    boolean terminateSession(int sessionId);
    
    /** Process the closure of the period. Return an UpdateBasket with TradeUpdates, if this 
     *  implementation processes trades upon period closure */
    UpdateBasket processClosePeriod(int sessionId);
    
    /** Set the current time as the period start time for the given session and period */
    boolean stampPeriodStartTime(int sessionId);
    
    /** Given a transaction request, return the information generated in an UpdateBasket
     *  object. An UpdateBasket consists of a series of data structures that are parsed
     *  by the dispatcher and routed to the appropriate controllers. For example, there are
     *  TradeUpdates, which tell the dispatcher what happened with the OfferBook, there
     *  are PriceChartUpdates, which tell the dispatcher what happened to the PriceChart,
     *  and so on */
    UpdateBasket processOffer(int sessionId, AbstractOffer newOffer, long receivedKey);
    
    /** Reset the given client's key to zero */
    public void resetClientKey(int sessionId, int subjectId);
    
    /** Generate the client-side offer-book for the given client using the server-side offer-book */
    OfferBook generateClientBook(int sessionId);
    
    /** Generate the client-side holdings array for the given client */
    int[] generateClientHoldings(int sessionId, int subjectId);
    
    /** Generate the cash holdings of the given client */
    float generateClientCash(int sessionId, int subjectId);
    
    /** Generate the securities view of the price chart for the given session */
    Vector getPriceChartView(int sessionId);
    
    /** Get the metrics information for the given session's current period */
    public MetricsUpdate[] getMetrics(int sessionId);
    
    /** Get the num offers metrics information for hte given session's current period */
    public NumOffersUpdate[] getNumOffers(int sessionId);
}
