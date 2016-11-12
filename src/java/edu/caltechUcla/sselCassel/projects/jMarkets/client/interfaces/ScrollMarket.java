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
 * ScrollMarket.java
 *
 * Based on Market.java, a different GUI for the Markets.
 *
 * Created on October 12, 2005, 11:00 AM
 *
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.interfaces;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.control.*;

/**
 *
 * @author  Christopher Crabbe
 */
public class ScrollMarket extends Market {
    
    /** Creates a new instance of ScrollMarket */
    public ScrollMarket(int id, String title, int initialHoldings, float[] prices, Client client) {
	super(id, title, initialHoldings, prices, client);
	this.prices = prices;        
    }
    
    public void disable(){
	marketPanel.disableMarket();
    }
    
    public void initializeMarket(ClientGUI gui) {
	this.gui = gui;
	float[] prices = super.getPrices();
	priceLevels = new ScrollPriceLevel[prices.length];
	for (int i=0; i<prices.length; i++){
	    priceLevels[i] = new ScrollPriceLevel(i, prices[i], this, super.getClient());
	}
	marketPanel = new ScrollMarketPanel(super.getId(), super.getTitle(), super.getHoldings(), priceLevels, super.getClient().allowTransactions());
	
	timeLabel = marketPanel.getTimeLabel();
	if( timeLabel == null ){
	    System.out.println("initializeMarket:  timeLabel == NULL");
	}
    }
    
    /** Update the time label with the given amount of time */
    public void setTimeLabel(int time) {
	final int t = time;
	
	Runnable doUpdate = new Runnable() {
	    public void run() {
		timeLabel.setText("<html>Time Left: <font color=#993333>" + t + "          </font></html>");
		marketPanel.updateOffers();
		marketPanel.updateBooks();
	    }
	};
	
	SwingUtilities.invokeLater(doUpdate);
    }
    
    /*public void inputTransaction(int action, int priceId, int[] units) {
	priceLevels[priceId].inputTransaction(action, units);
    }*/
    
    /** Set the the player's holdings of this security to the given amount */
    public void setHoldings(int holdings) {
	//super.holdings = holdings;
	marketPanel.setHoldings(holdings);
    }
    
    public JPanel getMarketColumn(){
	return marketPanel;
    }
    
    public PriceLevel[] getPriceLevels(){
        return priceLevels;
    }
    
    public void setBidLabel(String s){
	final String fs = s;
	//Runnable doUpdate = new Runnable() {
	//    public void run() {
	marketPanel.setBidLabel(fs);
	//marketPanel.updateOffers();
	//    }
	//};
    }
    
    public void setOfferLabel(String s){
	final String fs = s;
	//Runnable doUpdate = new Runnable() {
	//    public void run() {
	marketPanel.setOfferLabel(fs);
	//marketPanel.updateOffers();
	//  }
	//};
    }
    
    public boolean calculateBests(){
	return marketPanel.calculateBests();
    }
    
    /*public void updateBooks(){
	Runnable doUpdate = new Runnable() {
	    public void run() {
		marketPanel.updateBooks();
		//marketPanel.updateOffers();
	    }
	};
    }*/
    
    public int getBestBid(){
	return marketPanel.getBestBid();
    }
    
    public int getBestOffer(){
	return marketPanel.getBestOffer();
    }
    
    public void setClosedLabel(){
	marketPanel.setClosedLabel("Closed");
    }
    
    
    /** Center this market scroller onto the given price */
    public void centerOnPrice(float price) {
	final float p = price;
	//int index = -1;
	/*for( int i=0; i<prices.length; i++ ){
	    if( prices[i] == price ){
		index = i;
	    }
	}*/
	//if( index > 0 ){
	    //final int fi=index;
	    Runnable doUpdate = new Runnable() {
		public void run() {
		    marketPanel.snapToPrice(p);
		}
	    };
	    SwingUtilities.invokeLater(doUpdate);
	//}
    }
    
    /** Array of PriceLevel objects in ascending order (lowest price first) */
    private ScrollPriceLevel[] priceLevels;
    private float[] prices;
    private JLabel timeLabel;
    private ScrollMarketPanel marketPanel;    
    
    /** The ClientGUI containing this market */
    private ClientGUI gui;
    
    
    
}


