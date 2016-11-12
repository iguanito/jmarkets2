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
 * Updater.java
 *
 * Created on February 9, 2005, 1:59 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable.EarningsInfo;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.PeriodDef;

/**
 *
 * @author  Raj Advani
 */
public interface Updater {
    
    /** Initializes the UpdateServ for the new session. Called when the UpdateServ
     *  is instantiated */
    void initServ();
    
    /** Add the given array of clients. Return true if the operation is successful */
    boolean addClients(int[] dbIds);
    
    /** Add the given array of clients. Return true if the operation is successful */
    boolean deleteClients(int[] dbIds);
    
    /** Add a client, with the given database subject ID, to the current UpdateServ. This
     *  is called at the beginning of a session. Return true if the operation is successful */
    boolean addClient(int dbId);
    
    /** Delete a client form the UpdateServ queues. This is called when a client drops out of
     *  a session or when a session is completed */
    boolean deleteClient(int dbId);
    
    /** Adds the given update response to the given client's queue */
    void update(int dbId, Response res);
    
    /** Add the given update to the queue of the given clients */
    void update(int[] recipients, Response update);
    
    /** Send a NEW_PERIOD_UPDATE to each of the clients in the recipients array. Form the update from the given market, subject,
     *  and earnings information. Return the array of Response objects that are to be sent to the clients. */
    Response[] sendNewPeriodUpdate(int[] recipients, int periodNum, PeriodDef period, EarningsInfo[] earningsHistory, Trader[] traders, OfferBook offerBook);
    
    /** Send a time update to the given clients. These updates re-sync the client clocks with the
     *  server clocks. The JMTimer class determines when it is appropriate to send these re-syncs.
     *  Re-syncs also occur whenever any client sends an offer */
    Response sendTimeUpdate(int[] recipients, int periodNum, int openDelay, int periodLength, int[] marketLength);
    
    /** Send an update to the clients indicating that the given market has closed */
    Response sendMarketCloseUpdate(int[] recipients, int periodNum, int market);
    
    /** Send an END_PERIOD_UPDATE to each of the clients that tells them their payoffs earned.
     *  Also tell them whether to await a new period or terminate the experiment */
    Response[] sendEndPeriodUpdate(int[] recipients, int periodNum, float[] payoffs, String[] masks, EarningsInfo[] earningsHistory, boolean endExperiment);
    
    /** Process a Vector of TradeUpdate objects. Send out both the invalid updates and
     *  the valid offer book updates. Return an array of Responses, one corresponding
     *  to each tradeUpdate object */
    Response[] sendTradeUpdates(int periodNum, Vector tradeUpdates);
    
    /** Given an UPDATE_REQUEST from a client, return a response containing the update to send the
     *  client */
    Response getUpdate(Request req);
    
    /** Returns the hashtable that maps subject ID to their disconnected status. This is
     *  requested by the dispatcher upon any re-authentication attempt */
    Hashtable getDisconnectStatus();
}
