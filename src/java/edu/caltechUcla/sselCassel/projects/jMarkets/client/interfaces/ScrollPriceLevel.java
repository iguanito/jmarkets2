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
 * PriceLevel.java
 *
 * Created on February 10, 2004, 8:25 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.interfaces;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.control.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.BasicOffer;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.CancelOffer;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.OfferBook;

/**
 *
 * @author  Raj Advani
 */
public class ScrollPriceLevel extends PriceLevel {
    
    /** Creates a new instance of PriceLevel */
    public ScrollPriceLevel(int id, float price, ScrollMarket market, Client client) {
        super(id, price, market, client);
        try {
            this.id = id;
            this.price = price;
            this.market = market;            
            this.enabled = true;
            this.client = client;
            //this.bidLabel = bidLabel;
            constructPricePanel();
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setUpdateLabel(SliderLabel bidLabel){
        this.bidLabel = bidLabel;
    }
    
    /*public void setUpdateLabel(JLabel bidLabel){
        this.bidLabel = bidLabel;
    }*/
    
    /** Construct the PricePanel GUI */
    private void constructPricePanel() {
        timer = new javax.swing.Timer(30, pricePanel);
    }
    
    
    public void buyPanelMousePressed(MouseEvent evt) {
                /*
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setBlinkingColor(buyBlinkColor);
                pricePanel.setSettlingColor(buyBlinkColor);
                timer.start();
                 */
        
        outputTransaction(JMConstants.BUY_ACTION, 1);
    }
    
    public void buyPanelMousePressed(MouseEvent evt, int numItems){
        outputTransaction(JMConstants.BUY_ACTION, numItems);
    }
    
    public void sellPanelMousePressed(MouseEvent evt) {
                /*
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setBlinkingColor(sellBlinkColor);
                pricePanel.setSettlingColor(sellBlinkColor);
                timer.start();
                 */
        
        outputTransaction(JMConstants.SELL_ACTION, 1);
    }
    
    public void sellPanelMousePressed(MouseEvent evt, int numItems){
        outputTransaction(JMConstants.SELL_ACTION, numItems);
    }
    
    public void cancelLabelMousePressed(MouseEvent evt) {
                /*
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setBlinkingColor(cancelBlinkColor);
                pricePanel.setSettlingColor(neutralColor);
                timer.start();
                 */
        
        outputTransaction(JMConstants.CANCEL_ACTION, 1);
    }
    
    
    public void cancelLabelMousePressed(MouseEvent evt, int numItems) {
                /*
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setBlinkingColor(cancelBlinkColor);
                pricePanel.setSettlingColor(neutralColor);
                timer.start();
                 */
        
        outputTransaction(JMConstants.CANCEL_ACTION, numItems);
    }
    
    
    /** Construct the center panel of the price panel, which contains the price of the
     *  security */
    /*private void constructCenterPanel() {
        final NumberFormat formatter = NumberFormat.getCurrencyInstance();
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1,0));
     
        centerPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (enabled)
                    showOfferDialog();
            }
            public void mouseExited(MouseEvent evt) {
                removeInfo();
            }
            public void mouseEntered(MouseEvent evt) {
                removeInfo();
                addInfo();
            }
        });
     
        priceLabel = new JLabel(formatter.format(price));
        priceLabel.setFont(new Font("Georgia", 0, 14));
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        priceLabel.setVerticalAlignment(SwingConstants.CENTER);
        centerPanel.add(priceLabel);
    }*/
    
    
    /** Returns the price panel */
    public JPanel getPricePanel() {
        return pricePanel;
    }
    
    /** Given a JSlider, update the color of the labels to the given color */
    private void updateSliderColor(JSlider slider, Color color) {
        Dictionary labels = slider.getLabelTable();
        Enumeration keys = labels.keys();
        while (keys.hasMoreElements()) {
            Integer key = (Integer) keys.nextElement();
            JLabel lbl = (JLabel) labels.get(key);
            lbl.setForeground(color);
            labels.put(key, lbl);
        }
    }
    
    /** Tell the server that a bid/sell has been made at this price level for the
     *  given amount of units */
    private void outputTransaction(int action, int units) {
        if (!enabled)
            return;
        
        AbstractOffer offer = null;
        if (action == JMConstants.BUY_ACTION || action == JMConstants.SELL_ACTION)
            offer = new BasicOffer();
        else
            offer = new CancelOffer();
        
        offer.setAction(action);
        offer.setUnits(units);
        offer.setMarketId(market.getId());
        offer.setPriceId(id);
        offer.setPrice(price);
        
        client.outputTransaction(offer);
    }
    
    /** This method is called when the server informs the client of a transaction
     *  that occured on this PriceLevel. Updates the UI to reflect the transaction.
     *  If this update was a simple BUY or SELL conducted by THIS player then nothing
     *  need be updated because the 'flickering' already occurred here when the offer
     *  was made. Transaction updates (EXECUTE) always need to be displayed */
    public void inputTransaction(int action, boolean isMyAction, int[] units, OfferBook offerBook) {
        if (!enabled)
            return;
        
        int totalBids = units[0];
        int totalAsks = units[1];
        int yourBids = units[2];
        if( yourBids<0 ) 
            yourBids=0;
        int yourOffers = units[3];
        if( yourOffers<0 )
            yourOffers=0;
                        
        Color settleColor = updateUnits(units, offerBook);
        
        
        int transUnits = 0;
        if (action == JMConstants.BUY_ACTION) {
            //     public void updateTransactions(int action, String security, float price, int units, long time) {
            
            
            //pricePanel.setNeutralColor(neutralColor);
            //pricePanel.setBlinkingColor(buyBlinkColor);
            //pricePanel.setSettlingColor(settleColor);
            timer.start();
        }
        
        else if (action == JMConstants.SELL_ACTION) {
            
            //pricePanel.setNeutralColor(neutralColor);
            //pricePanel.setBlinkingColor(sellBlinkColor);
            //pricePanel.setSettlingColor(settleColor);
            timer.start();
        }
        
        else if (action == JMConstants.EXECUTE_ACTION || action == JMConstants.VISUAL_EXECUTE_ACTION) {
            //pricePanel.setNeutralColor(neutralColor);
            //pricePanel.setBlinkingColor(executeColor);
            //pricePanel.setSettlingColor(settleColor);
            
            timer.start();
        }
        
        else if (action == JMConstants.CANCEL_ACTION) {
            
            //pricePanel.setNeutralColor(neutralColor);
            //pricePanel.setBlinkingColor(cancelBlinkColor);
            //pricePanel.setSettlingColor(settleColor);
            timer.start();
        }
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
     *  Returns the settling color for the flicker. (buy color if buy units are still
     *  positive, sell color if sell colors positive, neutral if units are zero)
     *
     */
    public Color updateUnits(int[] units, OfferBook offerBook) {
        //buyLabel.setText("Buy (" + units[0] +")");
        //sellLabel.setText("Sell (" + units[1] + ")");
        
        //int oldTotalBids = totalBids;
        //int oldTotalAsks = totalAsks;
        //int oldYourOffers = yourOffers;
        totalBids = units[0];
        totalAsks = units[1];
        if(this.client.getTrader().isClosebook()){
            totalBids = units[2]; 
            totalAsks = units[3]; 
        }
        //bidLabel.setText("<html><font color=\"orange\">"+totalBids+"</font>|<font color=\"blue\">"+totalAsks+"</font></html>");;
        
        bidLabel.setNumBids(totalBids);
        bidLabel.setNumOffers(totalAsks);
                
        
        /*if( totalBids > 0 ){
            if( totalBids > 1 ){
                bidLabel.setText("<html><font color=#CC9900><b>^</b></font></html>");
            } else {
                bidLabel.setText("<html><font color=#CC9900>^</font></html>");
            }
        } else if( totalAsks > 0 ){
            if( totalAsks > 1 ){
                bidLabel.setText("<html><font color=\"blue\"><b>^</b></font></html>");
            } else {
                bidLabel.setText("<html><font color=\"blue\">^</font></html>");
            }
        } else {
            bidLabel.setText("");
        }*/
        
        /*if( totalBids > 0 ){
            if( totalBids == 1 ){
                bidLabel.removeAll();
                bidLabel.add(new JLabel("<html><font color=#CC9900>^</font></html>"));
                bidLabel.validate();
            } else {
                bidLabel.removeAll();
                bidLabel.add(new JLabel("<html><font color=#CC9900>^</font></html>"));
                bidLabel.validate();
            }
        } else if( totalAsks > 0 ){
            if( totalAsks == 1 ){
                bidLabel.removeAll();
                bidLabel.add(new JLabel("<html><font color=\"blue\">^</font></html>"));
                bidLabel.validate();
            } else {
                bidLabel.removeAll();
                bidLabel.add(new JLabel("<html><font color=\"blue\">^</font></html>"));
                bidLabel.validate();
            }
        }*/
        
        
        bidLabel.repaint();
        market.setBidLabel(String.valueOf(totalBids));
        market.setOfferLabel(String.valueOf(totalAsks));
        if (units[2] > 0)
            yourOffers = units[2];
        
        else if (units[3] > 0)
            yourOffers = units[3];
        
        else
            yourOffers = 0;
        
        //if (!ordersLabel.getText().equals(""))
        //    ordersLabel.setText("Your offers: " + yourOffers);
        
        if (totalBids > 0)
            return buyBlinkColor;
        if (totalAsks > 0)
            return sellBlinkColor;
        
        /*System.out.print("calculating bests()...");
        if( market.calculateBests() ){
            System.out.print("new bests found: bestBid=");
            System.out.print(""+market.getBestBid());
            System.out.print(", bestOffer=");
            System.out.println(""+market.getBestOffer());
        } else {
            System.out.println("no new bests found...");
        }*/
        
        //market.setBestBidQuantity(""+priceLevelsmarket.getBestBid())
        
        
        return neutralColor;
    }
    
    /** Set the panels labels appropriately, without flickering, to the units specified
     *  in the array */
    public void setUnits(int[] units, OfferBook offerBook) {
        Color color = updateUnits(units, offerBook);
        //setBackgroundColor(color);
    }
    
    /** Disable this PriceLevel, preventing all further updates and disallowing
     *  any further clicks */
    public void disable() {
        Runnable doUpdate = new Runnable() {
            public void run() {
                //removeInfo();
                //pricePanel.setEnabled(false);
                //priceLabel.setEnabled(false);
                enabled = false;
                
                //setBackgroundColor(new Color(190, 190, 190));
                //pricePanel.setNeutralColor(neutralColor);
                //pricePanel.setBlinkingColor(cancelBlinkColor);
                //pricePanel.setSettlingColor(new Color(190, 190, 190)); //190
                //timer.start();
                if (timer != null)
                    timer.removeActionListener(pricePanel);
                timer = null;
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    public float getPrice(){
        return price;
    }
    
    public int getYourOffers(){
        return yourOffers;
    }
    
    /** Get the center coordinates of this PricePanel, in the coordinate space of the
     *  Market column container */
    public int getCenterHeight() {
        return 0;
    }
    
    /** Called when the price level is destroyed by the garbage collector */
    protected void finalize() {
        System.out.println("Price level is being garbage collected");
    }
    
    /** This inner class is essentially a JPanel with an ActionListener that allows
     *  for blinking animations when the panel is clicked */
    class PricePanel extends JPanel implements java.awt.event.ActionListener {
        
        public PricePanel() {
            super();
            blinksSoFar = 0;
            onNeutralColor = true;
        }
        
        public void actionPerformed(ActionEvent e) {
            if (blinksSoFar < NUM_BLINKS) {
                if (onNeutralColor) {
                    //setBackgroundColor(blinkingColor);
                } else {
                    //(neutralColor);
                }
                blinksSoFar++;
                onNeutralColor = !onNeutralColor;
            } else {
                timer.stop();
                blinksSoFar = 0;
                onNeutralColor = true;
                //setBackgroundColor(settlingColor);
            }
        }
        
        private void setBlinkingColor(Color color) {
            this.blinkingColor = color;
        }
        
        private void setNeutralColor(Color color) {
            this.neutralColor = color;
        }
        
        private void setSettlingColor(Color color) {
            this.settlingColor = color;
        }
        
        private int blinksSoFar;
        private boolean onNeutralColor;
        private Color blinkingColor, neutralColor, settlingColor;
    }
    
    public int getNumOffers(){
        return totalAsks;
    }
    
    public int getNumBids(){
        return totalBids;
    }
    
    /** The ID number of this price level (the ID is unique only for this market) */
    private int id;
    
    /** True if this PriceLevel is enabled, false if it has been disabled, preventing
     *  any further mouse clicks and disallowing all further updates */
    private boolean enabled;
    
    /** The price associated with this PriceLevel */
    private float price;
    
    /** The number of offers made by this client on this price level */
    private int yourOffers;
    
    /** The JPanel associated with this price level */
    private PricePanel pricePanel;
    
    /** The JLabel that displays the price */
    private JLabel priceLabel;
    
    /** The market that this price level belongs to */
    private ScrollMarket market;
    
    /** The main client controller */
    private Client client;
    
    /** The buy button */
    private JButton buyButton;
    
    /** The sell button */
    private JButton sellButton;
    
    /** The labels displaying the quantity of bids and offers for this price level */
    //private JLabel bidLabel;
    //private JPanel bidLabel;
    private SliderLabel bidLabel;
    /** The total number of bids and asks currently made at this price level */
    private int totalBids, totalAsks;
    
    /** The showOfferDialog uses this boolean to keep track of whether the player is buying, selling
     *  or canceling */
    private int transactionMode;
    
    
    /** This timer controls the blinking animation */
    private javax.swing.Timer timer;
    
    
    //private static Color buyColor = new Color(51, 102, 0);
    private static Color buyColor = new Color(0, 51, 102);
    private static Color sellColor = new Color(153, 51, 51);
    private static Color ordersColor = Color.black;
    private static Color cancelColor = Color.black;
    
    private static Color neutralColor = new Color(204,204,204);
    private static Color buyBlinkColor = new Color(206, 234, 249);
    private static Color sellBlinkColor = new Color(251, 206, 207);
    private static Color cancelBlinkColor = new Color(190, 180, 181);
    private static Color executeColor = new Color(255, 249, 184);
    
    private static int BUY_MODE = 0;
    private static int SELL_MODE = 1;
    private static int CANCEL_MODE = 2;
    
    private static int NUM_BLINKS = 15;
}
