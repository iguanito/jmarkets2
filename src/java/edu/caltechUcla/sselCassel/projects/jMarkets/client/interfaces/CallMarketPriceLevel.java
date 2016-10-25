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

/**
 * CallMarketPriceLevel.java
 *
 * Created on October 26, 2005, 4:01 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.interfaces;

import edu.caltechUcla.sselCassel.projects.jMarkets.client.control.Client;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.OfferBook;
import java.awt.Color;
import javax.swing.SwingUtilities;

/**
 *
 * @author Raj Advani
 */
public class CallMarketPriceLevel extends PriceLevel {
    
    /** Creates a new instance of ContinuousMarketPriceLevel */
    public CallMarketPriceLevel(int id, float price, Market market, Client client) {
        super(id, price, market, client);
    }
    
    /** This method is called when the server informs the client of a transaction
     *  that occured on this PriceLevel. Updates the UI to reflect the transaction. */
    public void inputTransaction(int action, boolean isMyAction, int[] units, OfferBook offerBook) {
        if (!enabled)
            return;
        
        Color settleColor = updateUnits(units, offerBook);
        
        if(!this.client.getTrader().isClosebook()){
            if (action == JMConstants.BUY_ACTION) {
                pricePanel.setBlinkingColor(buyBlinkColor);
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setSettlingColor(settleColor);
                timer.start();
            }
            if (action == JMConstants.SELL_ACTION) {
                pricePanel.setBlinkingColor(sellBlinkColor);
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setSettlingColor(settleColor);
                timer.start();
            }
            if (action == JMConstants.EXECUTE_ACTION || action == JMConstants.VISUAL_EXECUTE_ACTION) {
                pricePanel.setBlinkingColor(executeColor);
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setSettlingColor(settleColor);
                timer.start();
            }
            if (action == JMConstants.CANCEL_ACTION) {
                pricePanel.setBlinkingColor(cancelBlinkColor);
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setSettlingColor(settleColor);
                timer.start();
            }
        }else{
            if(isMyAction){
                if(units[2]>0 && action == JMConstants.BUY_ACTION)
                    pricePanel.setBlinkingColor(buyBlinkColor);
                if(units[3]>0 && action == JMConstants.SELL_ACTION)
                    pricePanel.setBlinkingColor(sellBlinkColor);
                if(action == JMConstants.CANCEL_ACTION)
                    pricePanel.setBlinkingColor(cancelBlinkColor);
                
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setSettlingColor(settleColor);
                timer.start();
                
            }
        }
        recolorPriceBook(offerBook);
    }
    
    /** Updates the panel with the total amount of units being bid for or asked, and
     *  the amount that this client has on offer. The units array is of the following
     *  form:
     *
     *  0) The total bids on the pricelevel
     *  1) The total asks on the pricelevel
     *  2) The number of bids by player subjectId on the pricelevel
     *  3) The number of asks by player subjectId on the pricelevel
     *
     *  Returns the settling color for the flicker.
     */
    public Color updateUnits(int[] units, OfferBook offerBook) {
       
        totalAsks = units[1];
        totalBids = units[0];
        int subjId =-1; 
        if(this.client.getTrader().isClosebook()){
            totalBids = units[2]; 
            totalAsks = units[3]; 
            subjId = this.client.getId(); 
        }
        
        buyLabel.setText("Buy (" + totalBids +")");
        sellLabel.setText("Sell (" + totalAsks + ")");
        
        
        if (units[2] > 0)
            yourOffers = units[2];
        
        else if (units[3] > 0)
            yourOffers = units[3];
        
        else
            yourOffers = 0;
        
        if (!ordersLabel.getText().equals(""))
            ordersLabel.setText("Your offers: " + yourOffers);
        
        if (totalBids > 0 || totalAsks > 0)
            return getCallMarketColor(id, market.getId(), offerBook, subjId);
      
        return neutralColor;
    }
    
    /** Return the color this price level should assume on a call market. This is based
     *  on the number of bids/asks above/below this price level */
    private Color getCallMarketColor(int priceId, int marketId, OfferBook offerBook, int subjId) {
        int numAsks = offerBook.getLessThanOrEqualToAskOrders(marketId, priceId, subjId);
        int numBids = offerBook.getGreaterThanOrEqualToBidOrders(marketId, priceId, subjId);
        
        //System.out.println("Market: " + marketId + ", Price: " + priceId + ": Number of <= asks: " + numAsks + ", and >= bids: " + numBids);
        
        if (numBids >= numAsks)
            return buyBlinkColor;
        else
            return sellBlinkColor;
    }
    
    /** Goes through each price in the market and updates its color based on the new offerbook given. Skips
     *  this price, which should be handled elsewhere */
    private void recolorPriceBook(OfferBook offerBook) {
        int marketId = market.getId();
        
        PriceLevel[] priceLevels = market.getPriceLevels();
        for (int i=0; i<priceLevels.length; i++) {
            CallMarketPriceLevel callPrice = (CallMarketPriceLevel) priceLevels[i];
            int priceId = callPrice.getId();
            
            if (priceId == id)
                continue;
            
            int subjId = -1; 
            int totalAsks = offerBook.getTotalAsks(marketId, priceId);
            int totalBids = offerBook.getTotalBids(marketId, priceId);
            if(this.client.getTrader().isClosebook()){
                subjId = this.client.getId(); 
                totalAsks = offerBook.getMyTotalAsks(marketId, priceId, subjId);
                totalBids = offerBook.getMyTotalBids(marketId, priceId, subjId);
            }
            
            Color newColor = this.neutralColor; 
            if (totalAsks != 0 || totalBids != 0)
               newColor = getCallMarketColor(priceId, marketId, offerBook, subjId);
            
            callPrice.setBackgroundColor(newColor);
        }
    }
    
    public void disable() {
        Runnable doUpdate = new Runnable() {
            public void run() {
                removeInfo(); 
                pricePanel.setEnabled(false);
                priceLabel.setEnabled(false);
                enabled = false;
                
                //setBackgroundColor(new Color(190, 190, 190));
                
                if (timer != null)
                    timer.removeActionListener(pricePanel);
                timer = null;
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
}
