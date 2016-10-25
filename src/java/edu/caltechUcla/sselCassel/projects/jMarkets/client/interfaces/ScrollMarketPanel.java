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
 * ScrollMarketPanel.java
 *
 * Created on October 19, 2005, 10:23 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.interfaces;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.control.*;
import java.util.Hashtable;

/**
 *
 * @author  Chris Crabbe
 */
public class ScrollMarketPanel extends javax.swing.JPanel {
    
    
    /** Creates new form ScrollMarketPanel */
    public ScrollMarketPanel() {
        initComponents();
    }
    
    
    public ScrollMarketPanel(int id, String title, int initialHoldings, ScrollPriceLevel[] priceLevels, boolean allowTransactions){
        this.priceLevels = priceLevels;
        this.id = id;
        this.title = title;
        this.holdings = initialHoldings;
        this.allowTransactions = allowTransactions;
        
        
        // set up the hashtable with prices as keys and slider position values
        sliderValues = new Hashtable();
        for( int i=0; i<priceLevels.length; i++ ){
            sliderValues.put(new Float(priceLevels[i].getPrice()), new Integer(i));
        }
        Object[] keys = sliderValues.keySet().toArray();
        /*for( int i=0; i<keys.length; i++){
            System.out.println("sliderValues["+keys[i]+"=="+sliderValues.get(keys[i]));
        }*/
        
        bestBid=priceLevels.length/2;
        bestOffer=priceLevels.length/2;
        
        prices = new float[priceLevels.length];
        Hashtable labelTable = new Hashtable();
        for( int i=0; i<priceLevels.length; i++ ){
            prices[i] = priceLevels[i].getPrice();
        }
        
        initComponents();
        setBorder(javax.swing.BorderFactory.createTitledBorder(title));
        
        bidBookPanel.setPreferredSize(new Dimension(134,115));
        bidBookPanel.setMinimumSize(new Dimension(134,115));
        
        //Add and crop the info panel
        bidBook = new JPanel();
        bidBookPanel.setViewportView(bidBook);
        bidBook.setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.fill=GridBagConstraints.HORIZONTAL;
        //c.insets = new Insets(0,5,0,5);
        c.ipadx=10;
        JLabel priceLabel = new JLabel("<html><font color=#CC9900>Price</font></html>");
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bidBook.add(priceLabel,c);
        c.gridx=1;
        JLabel qLabel = new JLabel("<html><font color=#CC9900>Quantity</font></html>");
        qLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bidBook.add(qLabel,c);
        bidBookPrices = new JLabel[priceLevels.length];
        bidBookQuantities = new JLabel[priceLevels.length];
        //bidBookPanels = new JPanel[priceLevels.length];
        for( int i=0; i<priceLevels.length; i++){
            final int priceLvl = i;
            final int numLevels = priceLevels.length-1;
            bidBookPrices[i] = new JLabel("-");
            bidBookQuantities[i] = new JLabel("-");
            bidBookPrices[i].setOpaque(true);
            bidBookQuantities[i].setOpaque(true);
            bidBookPrices[i].setHorizontalAlignment(SwingConstants.CENTER);
            bidBookQuantities[i].setHorizontalAlignment(SwingConstants.CENTER);
            bidBookPrices[i].setBorder(BorderFactory.createMatteBorder(1,1,1,0, bidBook.getBackground()));
            bidBookQuantities[i].setBorder(BorderFactory.createMatteBorder(1,0,1,1, bidBook.getBackground()));
            bidBookPrices[i].addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent evt) {
                    System.out.print("mouseClicked()...");
                    int marketId = priceLvl;
                    //System.out.println("id="+marketId);
                    if (marketId != -1)
                        slider.setValue(numLevels-priceLvl);
                }
                
                public void mouseEntered(MouseEvent evt) {
                    int marketId = priceLvl;
                    //System.out.println("mouseEntered()... id="+marketId);
                    bidBookPrices[marketId].setBorder(BorderFactory.createMatteBorder(1,1,1,0,new java.awt.Color(204,153,0)));
                    bidBookQuantities[marketId].setBorder(BorderFactory.createMatteBorder(1,0,1,1,new java.awt.Color(204,153,0)));
                }
                
                public void mouseExited(MouseEvent evt) {
                    int marketId = priceLvl;
                    //System.out.println("mouseExited()... id="+marketId);
                    bidBookPrices[marketId].setBorder(BorderFactory.createMatteBorder(1,1,1,0, bidBookPrices[marketId].getBackground()));
                    bidBookQuantities[marketId].setBorder(BorderFactory.createMatteBorder(1,0,1,1, bidBookQuantities[marketId].getBackground()));
                }
            });
            bidBookQuantities[i].addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent evt) {
                    System.out.print("mouseClicked()...");
                    int marketId = priceLvl;
                    //System.out.println("id="+marketId);
                    if (marketId != -1)
                        slider.setValue(numLevels-priceLvl);
                }
                
                public void mouseEntered(MouseEvent evt) {
                    int marketId = priceLvl;
                    //System.out.println("mouseEntered()... id="+marketId);
                    bidBookPrices[marketId].setBorder(BorderFactory.createMatteBorder(1,1,1,0,new java.awt.Color(204,153,0)));
                    bidBookQuantities[marketId].setBorder(BorderFactory.createMatteBorder(1,0,1,1,new java.awt.Color(204,153,0)));
                }
                
                public void mouseExited(MouseEvent evt) {
                    int marketId = priceLvl;
                    //System.out.println("mouseExited()... id="+marketId);
                    bidBookPrices[marketId].setBorder(BorderFactory.createMatteBorder(1,1,1,0, bidBookPrices[marketId].getBackground()));
                    bidBookQuantities[marketId].setBorder(BorderFactory.createMatteBorder(1,0,1,1, bidBookQuantities[marketId].getBackground()));
                }
            });
            
            //System.out.println("added mouseListener");
            c.gridy=i+1;
            c.gridx=0;
            c.fill=GridBagConstraints.HORIZONTAL;
            c.anchor=GridBagConstraints.CENTER;
            c.insets=new Insets(0,0,0,0);
            //c.ipadx=10;
            bidBook.add(bidBookPrices[i], c);
            c.gridx=1;
            bidBook.add(bidBookQuantities[i], c);
        }
        
        bidFiller = new JPanel();
        fillerConstraints = new GridBagConstraints();
        fillerConstraints.gridx=0;
        fillerConstraints.gridy=priceLevels.length+1;
        fillerConstraints.weighty=1;
        bidBook.add(bidFiller, fillerConstraints);
        
        bidBookPanel.getVerticalScrollBar().setMaximum(bidBook.getPreferredSize().height);
        bidBookPanel.getVerticalScrollBar().setValue(bidBook.getPreferredSize().height);
        
        
        offerBookPanel.setPreferredSize(new Dimension(134,115));
        offerBookPanel.setMinimumSize(new Dimension(134,115));
        
        offerBook = new JPanel();
        offerBookPanel.setViewportView(offerBook);
        offerBook.setLayout(new GridBagLayout());
        
        c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.fill=GridBagConstraints.HORIZONTAL;
        //c.insets = new Insets(0,5,0,5);
        c.ipadx=10;
        JLabel priceLabelO = new JLabel("<html><font color=\"blue\">Price</font></html>");
        priceLabelO.setHorizontalAlignment(SwingConstants.CENTER);
        offerBook.add(priceLabelO,c);
        c.gridx=1;
        JLabel qLabelO = new JLabel("<html><font color=\"blue\">Quantity</font></html>");
        qLabelO.setHorizontalAlignment(SwingConstants.CENTER);
        offerBook.add(qLabelO,c);
        
        offerBookRows = new JPanel(new GridBagLayout());
        
        offerBookPrices = new JLabel[priceLevels.length];
        offerBookQuantities = new JLabel[priceLevels.length];
        
        for( int i=0; i<priceLevels.length; i++){
            final int priceLvl = i;
            final int numLevels = priceLevels.length;
            offerBookPrices[i] = new JLabel("-");
            offerBookQuantities[i] = new JLabel("-");
            offerBookPrices[i].setOpaque(true);
            offerBookQuantities[i].setOpaque(true);
            offerBookPrices[i].setHorizontalAlignment(SwingConstants.CENTER);
            offerBookQuantities[i].setHorizontalAlignment(SwingConstants.CENTER);
            
            offerBookPrices[i].addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent evt) {
                    System.out.print("mouseClicked()...");
                    int marketId = priceLvl;
                    //System.out.println("id="+marketId);
                    if (marketId != -1)
                        slider.setValue(priceLvl);
                }
                
                public void mouseEntered(MouseEvent evt) {
                    int marketId = priceLvl;
                    //System.out.println("mouseEntered()... id="+marketId);
                    offerBookPrices[marketId].setBorder(BorderFactory.createMatteBorder(1,1,1,0,new java.awt.Color(0,0,255)));
                    offerBookQuantities[marketId].setBorder(BorderFactory.createMatteBorder(1,0,1,1,new java.awt.Color(0,0,255)));
                }
                
                public void mouseExited(MouseEvent evt) {
                    int marketId = priceLvl;
                    //System.out.println("mouseExited()... id="+marketId);
                    offerBookPrices[marketId].setBorder(BorderFactory.createMatteBorder(1,1,1,0, offerBookPrices[marketId].getBackground()));
                    offerBookQuantities[marketId].setBorder(BorderFactory.createMatteBorder(1,0,1,1, offerBookQuantities[marketId].getBackground()));
                }
            });
            offerBookQuantities[i].addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent evt) {
                    System.out.print("mouseClicked()...");
                    int marketId = priceLvl;
                    //System.out.println("id="+marketId);
                    if (marketId != -1)
                        slider.setValue(priceLvl);
                }
                
                public void mouseEntered(MouseEvent evt) {
                    int marketId = priceLvl;
                    //System.out.println("mouseEntered()... id="+marketId);
                    offerBookPrices[marketId].setBorder(BorderFactory.createMatteBorder(1,1,1,0,new java.awt.Color(0,0,255)));
                    offerBookQuantities[marketId].setBorder(BorderFactory.createMatteBorder(1,0,1,1,new java.awt.Color(0,0,255)));
                }
                
                public void mouseExited(MouseEvent evt) {
                    int marketId = priceLvl;
                    //System.out.println("mouseExited()... id="+marketId);
                    offerBookPrices[marketId].setBorder(BorderFactory.createMatteBorder(1,1,1,0, offerBookPrices[marketId].getBackground()));
                    offerBookQuantities[marketId].setBorder(BorderFactory.createMatteBorder(1,0,1,1, offerBookQuantities[marketId].getBackground()));
                }
            });
            
            //System.out.println("added mouseListener");
            
            c.gridy=i+1;
            c.gridx=0;
            c.fill=GridBagConstraints.HORIZONTAL;
            c.anchor=GridBagConstraints.CENTER;
            c.insets=new Insets(0,0,0,0);
            //c.ipadx=10;
            offerBook.add(offerBookPrices[i], c);
            c.gridx=1;
            offerBook.add(offerBookQuantities[i], c);
        }
        
        c.gridy=1;
        c.gridwidth=2;
        c.gridx=0;
        offerBook.add(offerBookRows, c);
        
        offerFiller = new JPanel();
        fillerConstraints = new GridBagConstraints();
        fillerConstraints.gridx=0;
        fillerConstraints.gridy=priceLevels.length+1;
        fillerConstraints.weighty=1;
        offerBook.add(offerFiller, fillerConstraints);
        
        offerBookPanel.getVerticalScrollBar().setMaximum(offerBook.getPreferredSize().height);
        offerBookPanel.getVerticalScrollBar().setValue(offerBook.getPreferredSize().height);
        
        setHoldings(holdings);
        
        sliderBidLabels = new SliderLabel[priceLevels.length];
        sliderLabelPanels = new JPanel[priceLevels.length];
        for( int i=0; i<priceLevels.length; i++ ){
            //sliderBidLabels[i] = new JLabel("<html><font color=\"orange\">"+"0"+"</font>|<font color=\"blue\">"+"0"+"</font></html>");
            sliderLabelPanels[i] = new JPanel();
            //sliderLabelPanels[i].setOpaque(true);
            //sliderLabelPanels[i].add(new JLabel("!"));
            sliderBidLabels[i] = new SliderLabel("^");
            
            
            //sliderBidLabels[i].setOpaque(false);
            sliderBidLabels[i].setForeground(sliderBidLabels[i].getBackground());
            //sliderBidLabels[i].setText("^");
            //sliderLabelPanels[i].add(sliderBidLabels[i]);
            labelTable.put(new Integer(i), sliderBidLabels[i]);
            priceLevels[i].setUpdateLabel(sliderBidLabels[i]);
            //priceLevels[i].setUpdateLabel(sliderLabelPanels[i]);
        }
        //JPanel testPanel = new JPanel();
        //testPanel.add(new JLabel("! test !"));
        //labelTable.put(new Integer(0), testPanel);
        
        // initialize the JSlider to fit the prices given
        slider.setMinimum(0);
        slider.setMaximum(prices.length-1);
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);
        slider.setValue(0);
        
        // initialize the JSpinners to default to value 1
        Integer init = new Integer(1);
        Integer min = new Integer(0);
        Integer max = new Integer(65535);
        Integer step = new Integer(1);
        SpinnerNumberModel snmBuy = new SpinnerNumberModel(init, min, max, step);
        orderSizeSpinner.setModel(snmBuy);
        
        
        //bestOfferButton.setText("Best Offer");
        //bestBidButton.setText("Best Bid");
        
        minValue.setText(""+priceLevels[0].getPrice());
        maxValue.setText(""+priceLevels[priceLevels.length-1].getPrice());
        
        slider.validate();
        slider.repaint();
        validate();
        
        //System.out.println("ScrollMarketPanel constructed...");
        
        //this.validate();
        
    }
    
    public void updateSliderLabels(){
        
    }
    
    /** Disable this market, preventing any further updates and clicks */
    public void disableMarket() {
        Runnable doUpdate = new Runnable() {
            public void run() {
                setEnabled(false);
            }
        };
        SwingUtilities.invokeLater(doUpdate);
        
        for (int i=0; i<priceLevels.length; i++)
            priceLevels[i].disable();
    }
    
    /**
     *  calculate the best offer and best bid, and update the GUI with their quantities
     */
    public boolean calculateBests(){
        //System.out.println("calculateBests() called...");
        boolean oc = false;
        for( int i=0; i<priceLevels.length; i++ ){
            if( priceLevels[i].getNumOffers() > 0 ){
                if( i != bestOffer){
                    oc = true;
                }
                bestOffer = i;
                //System.out.println("bestOffer="+i);
                i = priceLevels.length+20;
            }
            if( i == priceLevels.length-1 && !oc ){
                bestOffer = priceLevels.length/2;
            }
        }
        
        boolean bc = false;
        for( int i=priceLevels.length-1; i>=0; i-- ){
            if( priceLevels[i].getNumBids() > 0 ){
                if( i!=bestBid ){
                    bc = true;
                }
                bestBid = i;
                //System.out.println("bestBid="+i);
                i =-1;
                //bestBidQuantityLabel.setText(""+priceLevels[bestBid].getNumBids());
                //bestBidQuantityLabel.repaint();
            }
            if( i==0 && !bc ) {
                bestBid = priceLevels.length/2;
            }
        }
        
        return bc||oc;
    }
    
    public void updateBooks(){
        //System.out.println("bigsize= "+this.getSize()+", offerBooksize="+offerBook.getSize());
        for( int i=0; i<priceLevels.length; i++ ){
            offerBookPrices[i].setText("");
            offerBookQuantities[i].setText("");
            bidBookQuantities[i].setText("");
            bidBookPrices[i].setText("");
        }
        
        int numOffers=0;
        offerBookRows.removeAll();
        GridBagConstraints c = new GridBagConstraints();

        for( int i=0; i<priceLevels.length; i++ ){
            if( priceLevels[i].getNumOffers() > 0 ){
                offerBookPrices[i].setText(""+priceLevels[i].getPrice());
                offerBookQuantities[i].setText(""+priceLevels[i].getNumOffers());
                //System.out.println("priceLevels["+i+"].getNumOffers()=="+priceLevels[i].getNumOffers()+", getYourOffers()=="+priceLevels[i].getYourOffers());
                if( priceLevels[i].getYourOffers() > 0 ){
                    offerBookPrices[i].setBackground(new Color(210,210,210));
                    offerBookQuantities[i].setBackground(new Color(210,210,210));
                } else {
                    //System.out.println("entering else block (regular background)");
                    offerBookPrices[i].setBackground(offerBook.getBackground());
                    offerBookQuantities[i].setBackground(offerBook.getBackground());
                }
                numOffers++;
            } else {
                offerBookPrices[i].setBorder(BorderFactory.createMatteBorder(0,0,0,0,Color.BLACK));
                offerBookPrices[i].setBorder(BorderFactory.createMatteBorder(0,0,0,0,Color.BLACK));
            }
        }
        fillerConstraints.gridheight=(priceLevels.length-numOffers);
        ((GridBagLayout)offerBook.getLayout()).setConstraints(offerFiller, fillerConstraints);
        
        int numBids = 0;
        for( int i=priceLevels.length-1; i>=0; i-- ){
            if( priceLevels[i].getNumBids()>0 ){
                //System.out.println("priceLevels["+i+"].getNumBids()=="+priceLevels[i].getNumBids()+", getYourOffers()=="+priceLevels[i].getYourOffers());
                bidBookPrices[priceLevels.length-1-i].setText(""+priceLevels[i].getPrice());
                bidBookQuantities[priceLevels.length-1-i].setText(""+priceLevels[i].getNumBids());
                if( priceLevels[i].getYourOffers() > 0 ){
                    //System.out.println("entering getYourOffers()>0 block");
                    bidBookPrices[priceLevels.length-1-i].setBackground(new Color(210,210,210));
                    bidBookQuantities[priceLevels.length-1-i].setBackground(new Color(210,210,210));
                } else {
                    bidBookPrices[priceLevels.length-1-i].setBackground(bidBook.getBackground());
                    bidBookQuantities[priceLevels.length-1-i].setBackground(bidBook.getBackground());
                    //System.out.println("entering else block (regular background)");
                }
                numBids++;
            } else {
                bidBookPrices[priceLevels.length-1-i].setBorder(BorderFactory.createMatteBorder(0,0,0,0, Color.BLACK));
                bidBookQuantities[priceLevels.length-1-i].setBorder(BorderFactory.createMatteBorder(0,0,0,0, Color.BLACK));
            }
        }
        fillerConstraints.gridheight=(priceLevels.length-numBids);
        ((GridBagLayout)bidBook.getLayout()).setConstraints(bidFiller, fillerConstraints);
        repaint();
        
    }
    
    protected Dimension relativeSize(float widthMultiple, float heightMultiple) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screen.width * widthMultiple);
        int height = (int) (screen.height * heightMultiple);
        Dimension dim = new Dimension(width, height);
        return dim;
    }
    
    public void snapToPrice(float price){
        int level = ((Integer)sliderValues.get(new Float(price))).intValue();
        slider.setValue(level);
    }
    
    /** Set the the player's holdings of this security to the given amount */
    public void setHoldings(int holdings) {
        this.holdings = holdings;
        holdingsLabel.setText("<html><font color=\"blue\">Holdings: " + holdings + "</b></html>");
        holdingsLabel.repaint();
    }
    
    /** Called upon garbage collection */
    protected void finalize() {
        System.out.println("Garbage collector cleaning up market");
    }
    
    public void updateOffers(){
        calculateBests();
        for( int i=0; i<sliderBidLabels.length; i++ ){
            sliderBidLabels[i].repaint();
            repaint();
        }
    }
    
    
    /** Return the column that contains all the PricePanels */
    //public JPanel getPriceColumn() {
    //    return priceColumn;
    //}
    
    protected ClientGUI getClientGUI() {
        return gui;
    }
    
    protected int getId() {
        return id;
    }
    
    public float[] getPrices() {
        return prices;
    }
    
    public PriceLevel[] getPriceLevels() {
        return priceLevels;
    }
    
    public JLabel getTimeLabel(){
        return timeLeft;
    }
    
    public void setOfferLabel(String s){
        numOffersLabel.setText(s);
        numOffersLabel.repaint();
    }
    
    public void setClosedLabel(String s){
        Runnable doUpdate = new Runnable() {
            public void run() {
                timeLeft.setText("Closed");
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    public void setBidLabel(String s){
        numBidsLabel.setText(s);
        numBidsLabel.repaint();
    }
    
    public int getBestBid(){
        return bestBid;
    }
    
    public int getBestOffer(){
        return bestOffer;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        slider = new javax.swing.JSlider();
        bestBidButton = new javax.swing.JButton();
        bestOfferButton = new javax.swing.JButton();
        bidButton = new javax.swing.JButton();
        offerButton = new javax.swing.JButton();
        sliderValue = new javax.swing.JLabel();
        holdingsLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        timeLeft = new javax.swing.JLabel();
        minValue = new javax.swing.JLabel();
        maxValue = new javax.swing.JLabel();
        bidBookPanel = new javax.swing.JScrollPane();
        offerBookPanel = new javax.swing.JScrollPane();
        numBidsLabel = new javax.swing.JLabel();
        numOffersLabel = new javax.swing.JLabel();
        orderSizeSpinner = new javax.swing.JSpinner();
        orderSizeLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        setMaximumSize(new java.awt.Dimension(697, 200));
        setMinimumSize(new java.awt.Dimension(697, 200));
        setPreferredSize(new java.awt.Dimension(697, 200));
        slider.setForeground(java.awt.Color.red);
        slider.setMaximum(6);
        slider.setMinimum(1);
        slider.setMinorTickSpacing(1);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.setValue(0);
        slider.setMaximumSize(new java.awt.Dimension(32767, 75));
        slider.setMinimumSize(new java.awt.Dimension(500, 75));
        slider.setPreferredSize(new java.awt.Dimension(500, 75));
        slider.setRequestFocusEnabled(false);
        slider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderStateChanged(evt);
            }
        });
        slider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                sliderMouseReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(slider, gridBagConstraints);

        bestBidButton.setText("Best Bid");
        bestBidButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bestBidButton.setMaximumSize(new java.awt.Dimension(95, 23));
        bestBidButton.setMinimumSize(new java.awt.Dimension(95, 23));
        bestBidButton.setPreferredSize(new java.awt.Dimension(95, 23));
        bestBidButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bestBidButtonMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        add(bestBidButton, gridBagConstraints);

        bestOfferButton.setText("Best Offer");
        bestOfferButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bestOfferButton.setMaximumSize(new java.awt.Dimension(95, 23));
        bestOfferButton.setMinimumSize(new java.awt.Dimension(95, 23));
        bestOfferButton.setPreferredSize(new java.awt.Dimension(95, 23));
        bestOfferButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bestOfferButtonMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(bestOfferButton, gridBagConstraints);

        bidButton.setText("Buy");
        bidButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bidButton.setMaximumSize(new java.awt.Dimension(60, 23));
        bidButton.setMinimumSize(new java.awt.Dimension(60, 23));
        bidButton.setPreferredSize(new java.awt.Dimension(60, 23));
        bidButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bidButtonMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.weightx = 1.0;
        add(bidButton, gridBagConstraints);

        offerButton.setText("Sell");
        offerButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        offerButton.setMaximumSize(new java.awt.Dimension(60, 23));
        offerButton.setMinimumSize(new java.awt.Dimension(60, 23));
        offerButton.setPreferredSize(new java.awt.Dimension(60, 23));
        offerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                offerButtonMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.weightx = 1.0;
        add(offerButton, gridBagConstraints);

        sliderValue.setBackground(java.awt.Color.white);
        sliderValue.setFont(new java.awt.Font("SansSerif", 1, 14));
        sliderValue.setForeground(java.awt.Color.red);
        sliderValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sliderValue.setText("Slider Value");
        sliderValue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        add(sliderValue, gridBagConstraints);

        holdingsLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        holdingsLabel.setText("Holdings:0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        add(holdingsLabel, gridBagConstraints);

        cancelButton.setText("Cancel Order");
        cancelButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelButtonMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        add(cancelButton, gridBagConstraints);

        timeLeft.setText("Time Left: xxx");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(timeLeft, gridBagConstraints);

        minValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        minValue.setText("1000.00");
        minValue.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        minValue.setMaximumSize(new java.awt.Dimension(44, 14));
        minValue.setMinimumSize(new java.awt.Dimension(44, 14));
        minValue.setPreferredSize(new java.awt.Dimension(44, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        add(minValue, gridBagConstraints);

        maxValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        maxValue.setText("1000.00");
        maxValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        maxValue.setMaximumSize(new java.awt.Dimension(44, 14));
        maxValue.setMinimumSize(new java.awt.Dimension(44, 14));
        maxValue.setPreferredSize(new java.awt.Dimension(44, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        add(maxValue, gridBagConstraints);

        bidBookPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 153, 0), 1, true), "Global Bids", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(204, 153, 0)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        add(bidBookPanel, gridBagConstraints);

        offerBookPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(java.awt.Color.blue, 1, true), "Global Offers", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 12), java.awt.Color.blue));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(offerBookPanel, gridBagConstraints);

        numBidsLabel.setFont(new java.awt.Font("SansSerif", 1, 14));
        numBidsLabel.setForeground(new java.awt.Color(204, 153, 0));
        numBidsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numBidsLabel.setText("#bids");
        numBidsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        numBidsLabel.setMaximumSize(new java.awt.Dimension(50, 19));
        numBidsLabel.setMinimumSize(new java.awt.Dimension(50, 19));
        numBidsLabel.setPreferredSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(numBidsLabel, gridBagConstraints);

        numOffersLabel.setFont(new java.awt.Font("SansSerif", 1, 14));
        numOffersLabel.setForeground(java.awt.Color.blue);
        numOffersLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numOffersLabel.setText("#offers");
        numOffersLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        numOffersLabel.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(numOffersLabel, gridBagConstraints);

        orderSizeSpinner.setAutoscrolls(true);
        orderSizeSpinner.setMaximumSize(new java.awt.Dimension(63, 18));
        orderSizeSpinner.setMinimumSize(new java.awt.Dimension(63, 18));
        orderSizeSpinner.setPreferredSize(new java.awt.Dimension(63, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        add(orderSizeSpinner, gridBagConstraints);

        orderSizeLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        orderSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        orderSizeLabel.setText("Size:");
        orderSizeLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        orderSizeLabel.setMaximumSize(new java.awt.Dimension(63, 18));
        orderSizeLabel.setMinimumSize(new java.awt.Dimension(63, 18));
        orderSizeLabel.setPreferredSize(new java.awt.Dimension(63, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(orderSizeLabel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    private void sliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderStateChanged
        int pLvl = slider.getValue();
        sliderValue.setText(""+prices[pLvl]);
        sliderValue.repaint();
        
        // update the buyButton and SellButton labels with current offer quantities
        //System.out.println( "numBids="+priceLevels[pLvl].getNumBids());
        numBidsLabel.setText(""+priceLevels[pLvl].getNumBids());
        numBidsLabel.repaint();
        
        //System.out.println( "numOffers="+priceLevels[pLvl].getNumOffers());
        numOffersLabel.setText(""+priceLevels[pLvl].getNumOffers());
        numOffersLabel.repaint();
    }//GEN-LAST:event_sliderStateChanged
    
    private void cancelButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButtonMouseClicked
        int pLvl = slider.getValue();
        
        
        // get the number of items to transact from the spinner
        int numItems = ((Integer)orderSizeSpinner.getValue()).intValue();
        priceLevels[pLvl].cancelLabelMousePressed(evt, numItems);
        orderSizeSpinner.setValue(new Integer(1));
        
        //for( int i=0; i<sliderBidLabels.length; i++ ){
        //    sliderBidLabels[i].repaint();
        //}
        sliderBidLabels[pLvl].repaint();
        //calculateBests();
        //bestOfferQuantityLabel.repaint();
        //bestBidQuantityLabel.repaint();
        numBidsLabel.setText(""+priceLevels[pLvl].getNumBids());
        numBidsLabel.repaint();
        numOffersLabel.setText(""+priceLevels[pLvl].getNumOffers());
        numOffersLabel.repaint();
    }//GEN-LAST:event_cancelButtonMouseClicked
    
    private void bidButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bidButtonMouseClicked
        if( allowTransactions ){
            int pLvl = slider.getValue();
        /*if( forceBestTrades ){
            boolean offersExist=false;
            for( int i=0; i<priceLevels.length; i++ ){
                if( priceLevels[i].getNumOffers()>0 ){
                    offersExist=true;
                }
            }
            if( offersExist && pLvl > bestOffer ){
                pLvl = bestOffer;
                slider.setValue(pLvl);
            }
        }*/
            
            // get the number of items to transact from the spinner
            int numItems = ((Integer)orderSizeSpinner.getValue()).intValue();
            priceLevels[pLvl].buyPanelMousePressed(evt, numItems);
            orderSizeSpinner.setValue(new Integer(1));
            
            sliderBidLabels[pLvl].repaint();
            numBidsLabel.setText(""+priceLevels[pLvl].getNumBids());
            numBidsLabel.repaint();
            repaint();
        } else {
            JOptionPane.showMessageDialog(null, "Because your cumulative payoff is less than zero, you aren't allowed to transact this round");
        }
    }//GEN-LAST:event_bidButtonMouseClicked
    
    private void offerButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_offerButtonMouseClicked
        if( allowTransactions ){
            int pLvl = slider.getValue();
            
        /*if( forceBestTrades ){
            boolean bidsExist=false;
            for( int i=0; i<priceLevels.length; i++ ){
                if( priceLevels[i].getNumBids()>0 ){
                    bidsExist=true;
                }
            }
            if( bidsExist && pLvl < bestBid){
                pLvl = bestBid;
                slider.setValue(pLvl);
            }
        }*/
            
            // get the number of items to transact from the spinner
            int numItems = ((Integer)orderSizeSpinner.getValue()).intValue();
            priceLevels[pLvl].sellPanelMousePressed(evt, numItems);
            orderSizeSpinner.setValue(new Integer(1));
            
            sliderBidLabels[pLvl].repaint();
            
            numOffersLabel.setText(""+priceLevels[pLvl].getNumOffers());
            numOffersLabel.repaint();
            
            repaint();
        } else {
            JOptionPane.showMessageDialog(null, "Because your cumulative payoff is less than zero, you aren't allowed to transact this round");
        }
    }//GEN-LAST:event_offerButtonMouseClicked
    
    private void bestOfferButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bestOfferButtonMouseClicked
        //calculateBests();
        if( bestOffer >= 0 && bestOffer <= (prices.length-1) ){
            slider.setValue(bestOffer);
        }
    }//GEN-LAST:event_bestOfferButtonMouseClicked
    
    private void bestBidButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bestBidButtonMouseClicked
        //calculateBests();
        if( bestBid >= 0 && bestBid <= (prices.length-1) ){
            slider.setValue(bestBid);
        }
    }//GEN-LAST:event_bestBidButtonMouseClicked
    
    private void sliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sliderMouseReleased
        
    }//GEN-LAST:event_sliderMouseReleased
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton bestBidButton;
    public javax.swing.JButton bestOfferButton;
    public javax.swing.JScrollPane bidBookPanel;
    public javax.swing.JButton bidButton;
    public javax.swing.JButton cancelButton;
    public javax.swing.JLabel holdingsLabel;
    public javax.swing.JLabel maxValue;
    public javax.swing.JLabel minValue;
    public javax.swing.JLabel numBidsLabel;
    public javax.swing.JLabel numOffersLabel;
    public javax.swing.JScrollPane offerBookPanel;
    public javax.swing.JButton offerButton;
    public javax.swing.JLabel orderSizeLabel;
    public javax.swing.JSpinner orderSizeSpinner;
    public javax.swing.JSlider slider;
    public javax.swing.JLabel sliderValue;
    public javax.swing.JLabel timeLeft;
    // End of variables declaration//GEN-END:variables
    
    public javax.swing.JPanel bidBook, offerBook, offerBookRows, bidFiller, offerFiller;
    public javax.swing.JLabel[] offerBookPrices, offerBookQuantities, bidBookPrices, bidBookQuantities;;
    
    /** The ID number of the market */
    private int id;
    private boolean /*forceBestTrades=true,*/ bestTradeExists=false;
    /** The title of the market */
    private String title;
    private GridBagConstraints fillerConstraints;
    private Hashtable sliderValues;
    /** The amount of this security owned by the client */
    private int holdings, bestOffer, bestBid;
    
    /** Array containing the price levels as float values */
    private ScrollPriceLevel[] priceLevels;
    
    private float[] prices;
    
    private JPanel[] sliderLabelPanels;
    private JLabel[] sliderOfferLabels;
    private SliderLabel[] sliderBidLabels;
    
    /** The ClientGUI containing this market */
    private ClientGUI gui;
    private boolean allowTransactions=true;
}
