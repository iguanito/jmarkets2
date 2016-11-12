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
 * Client.java
 *
 * Created on July 20, 2004, 1:19 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.control;

import java.net.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.interfaces.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable.EarningsInfo;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.MarketDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AutoCancelOffer;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.BasicOffer;

/**
 *
 * @author  Raj Advani
 */
public class Client {
    
    public Client() {
        
    }
    
    /** Creates a new instance of Client. Load the client info from the
     *  applet then retrieve a list of sessions from the server. Wait until
     *  the client selects a session before proceeding */
    public void init(ClientGUI ui, ClientInfo cinfo, URL codebase, boolean fastMode) {
        this.ui = ui;
        this.cinfo = cinfo;
        this.periodClosed = true;
        
        if (cinfo.isTestMode())
            tclient = new TestClient(this);
        
        initCommunicator(codebase);
        
        try {
            SessionIdentifier[] identifiers = com.getSessionList();
            ui.chooseSession(fastMode, identifiers);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Join the given session by sending an authentication request */
    public void joinSession(int session) {
        try {
            System.out.println("Joining session " + session);
            com.connect(session, cinfo.getName(), cinfo.getDbId());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Initialize the Communicator for this client. The Communicator handles all server-client request/response
     *  handling */
    private void initCommunicator(URL codebase) {
        com = new Communicator(codebase, this);
    }
    
    /** Starts the period after the given delay. The period will be of the given length and will use
     *  the information given */
    public void startPeriod(int periodLength, Trader trader, MarketDef minfo, EarningsInfo einfo, OfferBook offerBook, String marketEngine) {
        periodClosed = true;
        key = 0;
        
        this.offerBook = offerBook;
        this.marketDef = minfo;
        this.trader = trader;
        this.marketEngine = marketEngine;
        
        float initialCash = trader.getCash();
        int[] initialHoldings = trader.getHoldings();
        
        marketClosed = new boolean[minfo.getNumMarkets()];
        
        ui.startPeriod(periodLength, minfo, initialCash, initialHoldings, einfo);
        ui.bringTransactionsPanelToFront();
        initTimers(periodLength, minfo.getMarketTime());
        
        if (cinfo.isTestMode()) {
            tclient.setMarketInfo(minfo);
            tclient.startTestMode();
        }
    }
    
    /** Starts the period with the given offer book and information. This is used when a client is
     *  re-connecting to an already active period */
    public void restartPeriod(int periodLength, int[] marketLength, boolean[] marketClosed, Trader trader, MarketDef marketDef, OfferBook offerBook, Vector chart, EarningsInfo einfo, boolean periodClosed, String marketEngine) {
        key = 0;
        this.offerBook = offerBook;
        
        this.trader = trader;
        this.offerBook = offerBook;
        this.marketDef = marketDef;
        this.marketClosed = marketClosed;
        this.marketEngine = marketEngine;
        
        float cash = trader.getCash();
        int[] holdings = trader.getHoldings();
        
        ui.startPeriod(periodLength, marketDef, cash, holdings, einfo);
        ui.updateBook(cinfo.getId(), offerBook);
        ui.insertPriceChart(chart);
        
        initTimers(periodLength, marketLength);
        startProxyTimer();
        
        for (int i=0; i<marketClosed.length; i++) {
            if (marketClosed[i])
                closeMarket(i);
        }
        
        this.periodClosed = periodClosed;
        
        if (periodClosed) {
            timer.cancel();
            ui.deactivatePeriod();
            
            if (cinfo.isTestMode())
                tclient.stopTestMode();
        }
        
        else if (cinfo.isTestMode()) {
            tclient.setMarketInfo(marketDef);
            tclient.startTestMode();
        }
        
        setWaitingDialogInactive();
    }
    
    /**
     * Called when the client receives an earnings info update. Updates the ClientGUI
     */
    public void updateEarnings(EarningsInfo einfo) {
        ui.insertEarningsPanel(einfo);
    }
    
    /** Initializes and starts all of the period and market timers. See the JMTimer class
     *  for more detail on the timing system. The timer here
     *  is really a fake -- it just keeps the time between server time synchronization
     *  updates, which occurs in the updateTime method */
    private void initTimers(int periodLength, int[] marketLength) {
        periodTimeRemaining = periodLength;
        marketTimeRemaining = marketLength;
        
        ui.setTimeLabel(periodLength);
        
        for (int i=0; i<marketLength.length; i++) {
            ui.setTimeLabel(i, marketLength[i]);
            if (marketLength[i] <= 0) {
                ui.disableMarket(i);
                ui.setClosedLabel(i);
            }else{
                ui.centerMarket(i);
            }
        }
    }
    
    /** Start the proxy timer that keeps track of tiem between server synchronization
     *  updates */
    private void startProxyTimer() {
        timer = new java.util.Timer();
        
        TimerTask task = new TimerTask() {
            public void run() {
                if (periodTimeRemaining > 0)
                    periodTimeRemaining--;
                else
                    timer.cancel();
                
                for (int i=0; i<marketTimeRemaining.length; i++) {
                    if (marketTimeRemaining[i] > 0)
                        marketTimeRemaining[i]--;
                    else if( !marketClosed[i] )
                        closeMarket(i);
                }
                
                ui.setTimeLabel(periodTimeRemaining);
                
                for (int i=0; i<marketTimeRemaining.length; i++) {
                    if ((marketTimeRemaining[i] >= 0) && !marketClosed[i]) {
                        ui.setTimeLabel(i, marketTimeRemaining[i]);
                    }
                }
            }
        };
        
        timer.schedule(task, 1000, 1000);
    }
    
    /** Update the period and market time to the given numbers */
    public void updateTime(int periodTime, int[] marketTime, int openingTime) {
        //periodTimeRemaining = periodTime;
        //marketTimeRemaining = marketTime;
        if (openingTime > 1 || openingTime == 0) {
            setWaitingDialogActive("Markets Will Open In:", openingTime + " seconds");
            return;
        }
        if (openingTime == 1) {
            timerStarted = false;
            setWaitingDialogActive("Markets Will Open In:", "1 second");
            return;
        }
        if (openingTime == -1 && !timerStarted) {
            setWaitingDialogInactive();
            timerStarted = true;
            startProxyTimer();
            periodClosed = false;
        }
        
        if (periodTime == 0) {
            timer.cancel();
        }
    }
    
    /** Called when the Server period timer hits zero. The client responds by sending an
     *  end period notification */
    public void closePeriod() {
        periodClosed = true;
        timer.cancel();
        ui.deactivatePeriod();
        
        if (cinfo.isTestMode())
            tclient.stopTestMode();
    }
    
    /** Called when any of the server market timers hits zero */
    public void closeMarket(int market) {
        marketClosed[market] = true;
        ui.disableMarket(market);
        ui.setClosedLabel(market);
        ui.bringEarningsPanelToFront();
    }
    
    /** Update this client's key. The key is used to ensure that the client is in sync with
     *  the server. If the client's key does not match that of the server, then all transactions
     *  output by the client until the keys match will be rejected */
    public void updateKey(long key, String code) {
        this.key = key;
        //System.out.println(getId() + ": key updated to " + key + " due to offer " + code);
    }
    
    /** Outputs a transaction to the communicator. First validates that transaction. Returns true
     *  if the transaction was valid, false otherwise. If a transaction is invalid, try to
     *  construct a composite offer that will cancel inferior offers in order to make the current
     *  offer valid. If this fails as well, then tell the client his/her offer is invalid */
    public boolean outputTransaction(AbstractOffer offer) {
        if (periodClosed) {
            displayError("Market Closed", "Please wait until the market opens");
            return false;
        }
        
        offer.setSubjectId(cinfo.getId());
        offer.setUseEffCashValidation(marketDef.getUseEffCashValidation());
        
        if (offer.validate(trader, offerBook)) {
            com.sendTransactionInfo(offer, sessionId, key);
            System.out.println(getId() + ": sent basic order to TradeServ");
            return true;
        }
        
        else if (offer.getAction() == JMConstants.BUY_ACTION || offer.getAction() == JMConstants.SELL_ACTION) {
            AutoCancelOffer compositeOffer = new AutoCancelOffer((BasicOffer) offer);
            compositeOffer.setUseEffCashValidation(marketDef.getUseEffCashValidation());
            
            if (compositeOffer.generateCancelOffer(trader, offerBook)) {
                com.sendTransactionInfo(compositeOffer, sessionId, key);
                System.out.println(getId() + ": sent composite offer to TradeServ");
                return true;
            }
        }
        
        displayError("Invalid Offer", offer.getInvalidMessage());
        return false;
    }
    
    /** Inputs a transaction to the client from the communicator. Update the GUI display */
    public void inputTransaction(int action, int marketId, int priceId, int[] priceBook, long time) {
        int myAction = offerBook.insertPriceBook(marketId, priceId, priceBook, getId());
        int[] units = createUnitsArray(marketId, priceId, getId());
        
        float price = marketDef.getPrices()[marketId][priceId];
        String name = marketDef.getMarketTitles()[marketId];
        
        boolean isMyAction = myAction > 0; 
        ui.getMarkets()[marketId].getPriceLevels()[priceId].inputTransaction(action, isMyAction, units, offerBook);
        ui.updateOrdersPanel(name, price, units);
        
        if (action == JMConstants.EXECUTE_ACTION && !trader.isShowSuggestedClearingPrice())
            ui.updatePriceChart(name, (time / 1000), price);
            
        if(trader.isShowSuggestedClearingPrice()){
            float clearingPrice = this.offerBook.getMarketClearingPrice(marketId); 
            if(clearingPrice>0)
                ui.updatePriceChart(name, (time/1000), clearingPrice); 
        }
    }
    
    public void updateTransactionsPanel(int marketId, int txnPriceId, int stdPriceId, long time, int unitsTraded, String action, boolean owned){
        if(owned || this.getMarketEngine().equalsIgnoreCase(JMConstants.CALL_MARKET_ENGINE)){
            float txnPrice = marketDef.getPrices()[marketId][txnPriceId];
            float stdPrice = marketDef.getPrices()[marketId][stdPriceId];
            String name = marketDef.getMarketTitles()[marketId];
            ui.updateTransactions(action, name, txnPrice, stdPrice, unitsTraded, time, owned);
        }
    }
    
    /** Return in an int array, in this order:
     *
     *  0) The total bids on the pricelevel
     *  1) The total asks on the pricelevel
     *  2) The number of bids by player subjectId on the pricelevel
     *  3) The number of asks by player subjectId on the pricelevel
     *
     *  Obviously if 0 is positive then 1 is zero; if 2 is positive then 3 is zero, and vice-versa
     *  This is used for updating the offers on the client-side (the unit amounts)
     */
    private int[] createUnitsArray(int marketId, int priceId, int subjectId) {
        int[] units = new int[4];
        units[0] = offerBook.getTotalBids(marketId, priceId);
        units[1] = offerBook.getTotalAsks(marketId, priceId);
        units[2] = offerBook.getBids(marketId, priceId, subjectId);
        units[3] = offerBook.getAsks(marketId, priceId, subjectId);
        
        return units;
    }
    
    /** Update the client's holdings of cash and the given security */
    public void updateHoldings(int marketId, float cashHoldings, int secHoldings, int[] numSales, int[] numPurchases) {
        trader.setCash(cashHoldings);
        trader.setHoldings(marketId, secHoldings);
        trader.setNumSales(marketId, numSales);
        trader.setNumPurchases(marketId, numPurchases);
        
        ui.setCashLabel(cashHoldings);
        ui.setHoldingsLabel(marketId, secHoldings);
    }
    
    /** Called by the Communicator when the server has received all end period notifications and
     *  informed the client of his/her payoff. If endExperiment is true, then exit the client */
    public void processEndPeriod(float payoff, String mask, boolean endExperiment) {
        if (mask == null)
            setWaitingDialogActive("Period Complete", "Your Cumulative Payoff: " + payoff, 7000);
        else
            setWaitingDialogActive("Period Complete", mask, 7000);
        
        if (!endExperiment)
            setWaitingDialogActive("Please Wait", "Waiting for next period");
        
        else {
            setWaitingDialogActive("Thank You", "The experiment has concluded", 5000);
            setWaitingDialogInactive();
            System.exit(0);
        }
        cumulativePayoff+=payoff;
        
    }
    
    public void setName(String name) {
        cinfo.setName(name);
    }
    
    public String getName() {
        return cinfo.getName();
    }
    
    public void setId(int id) {
        cinfo.setId(id);
    }
    
    public int getId() {
        return cinfo.getId();
    }
    
    public int getDbId() {
        return cinfo.getDbId();
    }
    
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
    
    public int getSessionId() {
        return sessionId;
    }
    
    /** These three methods control the waiting dialog used by this client */
    public void setWaitingDialogActive(String topMessage, String bottomMessage, long time) {
        ui.setWaitingDialogActive(topMessage, bottomMessage, time);
    }
    
    public void setWaitingDialogActive(String topMessage, String bottomMessage) {
        ui.setWaitingDialogActive(topMessage, bottomMessage);
    }
    
    public void setWaitingDialogInactive() {
        ui.setWaitingDialogInactive();
    }
    
    public boolean isWaitingDialogNull() {
        return ui.isWaitingDialogNull();
    }
    
    /** Display the given error on the client gui if we are not in test mode */
    public void displayError(String title, String message) {
        if (cinfo.isTestMode()) {
            System.out.println(getId() + ": test client has made invalid move: " + message);
            return;
        }
        
        ui.displayError(title, message);
    }
    
    /** Display the given error that resulted from an offer with code 'code' */
    public void displayError(String title, String message, String code) {
        if (cinfo.isTestMode()) {
            System.out.println(getId() + ": Offer " + code + " was invalid ( " + message + ")");
            return;
        }
        
        displayError(title, message);
    }
    
    /** Get the ID of the price level that is in the center of the market. The center is
     *  described as the price level halfway between the highest bid and the lowest ask */
    public int getMarketCenter(int marketId) {
        if (cinfo.isTestMode())
            tclient.stopTestMode(); //FIX ME
        return offerBook.getMarketCenter(marketId);
    }
    
    public int getMarketBestBuy(int marketId) {
        if (cinfo.isTestMode())
            tclient.stopTestMode(); //FIX ME
        return offerBook.getHighestBid(marketId);
    }
    
    public int getMarketBestSell(int marketId) {
        if (cinfo.isTestMode())
            tclient.stopTestMode(); //FIX ME
        return offerBook.getLowestAsk(marketId);
    }
    
    public int getPeriodNum() {
        return periodNum;
    }
    
    public void setPeriodNum(int periodNum) {
        this.periodNum = periodNum;
    }
    
    public Trader getTrader() {
        return trader;
    }
    
    public boolean isPeriodClosed() {
        return periodClosed;
    }
    
    public String getMarketEngine() {
        return marketEngine;
    }
    
    public boolean allowTransactions() {
        if( cumulativePayoff >= 0 ){
            return true;
        } else {
            return false;
        }
    }
    
    /** The main network communicator */
    private Communicator com;
    
    /** The time remaining in the period */
    private int periodTimeRemaining;
    
    /** The time remaining in each market */
    private int[] marketTimeRemaining;
    
    /** True if the given market is closed, false otherwise */
    private boolean[] marketClosed;
    
    /** The period number */
    private int periodNum;
    
    /** The proxy timer */
    private java.util.Timer timer;

    /** Boolean to make sure proxy timer is only started once */
    private boolean timerStarted;
    
    /** The Client GUI */
    private ClientGUI ui;
    
    /** The biographical information of the client (name, etc) */
    private ClientInfo cinfo;
    
    /** The session ID, used to communicate with the server after authentiation */
    private int sessionId;
    
    /** The random move generator if we are in testing mode */
    private TestClient tclient;
    
    /** The key that this client has received from the server. Used by the server to
     *  determine if the client's validation is in sync with the server */
    private long key;
    
    /** The market definition object, which contains the properties of all the markets
     *  in the session */
    private MarketDef marketDef;
    
    /** The Trader object for this client, which contains most of the market/holdings information */
    private Trader trader;
    
    /** The OfferBook object contains current information on the holdings of each client
     *  and the market orders currently on the table */
    private OfferBook offerBook;
    
    /** False when the markets for the period are open */
    private boolean periodClosed;
    
    /** The market engine we are using to process trades (determines the type of pricelevel to use) */
    private String marketEngine;
    
    private float cumulativePayoff = 0.f;
}
