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
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.control.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.OfferBook;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.BasicOffer;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.CancelOffer;

/**
 *
 * @author  Raj Advani, Walter Yuan
 */
public abstract class PriceLevel {
    
    /** Creates a new instance of PriceLevel */
    public PriceLevel(int id, float price, Market market, Client client) {
        try {
            this.id = id;
            this.price = price;
            this.market = market;
            this.enabled = true;
            this.client = client;
            
            constructPricePanel();
            PriceLevel.neutralColor = pricePanel.getBackground();
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Construct the PricePanel GUI */
    private void constructPricePanel() {
        pricePanel = new PricePanel();
        pricePanel.setLayout(new GridLayout(3, 1));
        pricePanel.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        pricePanel.setPreferredSize(new Dimension(Market.MARKET_WIDTH - 20, Market.MARKET_WIDTH - 20));
        pricePanel.setMinimumSize(new Dimension(Market.MARKET_WIDTH - 20, Market.MARKET_WIDTH - 20));
        
        pricePanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                
            }
            public void mouseExited(MouseEvent evt) {
                removeInfo();
            }
            public void mouseEntered(MouseEvent evt) {
                removeInfo();
                addInfo();
            }
        });
        
        constructTopPanel();
        constructCenterPanel();
        constructBottomPanel();
        
        pricePanel.add(topPanel);
        pricePanel.add(centerPanel);
        pricePanel.add(bottomPanel);
        
        centerPanel.setOpaque(false);
        buyPanel.setOpaque(false);
        sellPanel.setOpaque(false);
        bottomPanel.setOpaque(false);
        topPanel.setOpaque(false);
        
        timer = new javax.swing.Timer(30, pricePanel);
        
        borderControl = new BorderControl();
        borderTimer = new javax.swing.Timer(30, borderControl);
        
    }
    
    
    /** Construct the top panel of the price panel, which contains the buy and sell
     *  buttons */
    private void constructTopPanel() {
        buyPanel = new JPanel();
        buyPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                /*
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setBlinkingColor(buyBlinkColor);
                pricePanel.setSettlingColor(buyBlinkColor);
                timer.start();
                 */
                
                outputTransaction(JMConstants.BUY_ACTION, 1);
            }
            public void mouseExited(MouseEvent evt) {
                removeInfo();
                buyLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
            }
            public void mouseEntered(MouseEvent evt) {
                removeInfo();
                addInfo();
                buyLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            }
        });
        
        buyLabel = new JLabel("Buy (0)");
        buyLabel.setForeground(buyColor);
        buyLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        
        sellPanel = new JPanel();
        sellPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                /*
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setBlinkingColor(sellBlinkColor);
                pricePanel.setSettlingColor(sellBlinkColor);
                timer.start();
                 */
                
                outputTransaction(JMConstants.SELL_ACTION, 1);
            }
            public void mouseExited(MouseEvent evt) {
                removeInfo();
                sellLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
            }
            public void mouseEntered(MouseEvent evt) {
                removeInfo();
                addInfo();
                sellLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            }
        });
        
        sellLabel = new JLabel("Sell (0)");
        sellLabel.setForeground(sellColor);
        sellLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));
        topPanel.add(buyPanel);
        topPanel.add(sellPanel);
    }
    
    /** Construct the center panel of the price panel, which contains the price of the
     *  security */
    private void constructCenterPanel() {
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
    }
    
    /** Construct the bottom panel of the price panel, which contains the holdings info
     *  and cancel button */
    private void constructBottomPanel() {
        ordersLabel = new JLabel("");
        ordersLabel.setFont(new Font("Georgia", 0, 10));
        ordersLabel.setForeground(ordersColor);
        ordersLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        cancelLabel = new JLabel("");
        cancelLabel.setForeground(cancelColor);
        cancelLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        cancelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cancelLabel.setVerticalAlignment(SwingConstants.TOP);
        
        cancelLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                /*
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setBlinkingColor(cancelBlinkColor);
                pricePanel.setSettlingColor(neutralColor);
                timer.start();
                 */
                
                outputTransaction(JMConstants.CANCEL_ACTION, 1);
            }
            public void mouseExited(MouseEvent evt) {
                removeInfo();
                cancelLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
            }
            public void mouseEntered(MouseEvent evt) {
                removeInfo();
                addInfo();
                cancelLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            }
        });
        
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 1));
        bottomPanel.add(ordersLabel);
        bottomPanel.add(cancelLabel);
    }
    
    /** Remove all the mouseover information from the panel */
    protected void removeInfo() {
        buyPanel.removeAll();
        sellPanel.removeAll();
        ordersLabel.setText("");
        cancelLabel.setText("");
        priceLabel.setFont(new Font("Georgia", 0, 14));
        pricePanel.repaint();
        pricePanel.setBorder(BorderFactory.createLineBorder(Color.black, 3));
    }
    
    /** Add all the mouseover information to the panel if not disabled */
    private void addInfo() {
        if (!enabled)
            return;
        
        buyPanel.add(buyLabel);
        sellPanel.add(sellLabel);
        ordersLabel.setText("Your offers: " + yourOffers);
        cancelLabel.setText("Cancel");
        priceLabel.setFont(new Font("Georgia", 1, 14));
        pricePanel.repaint();
        pricePanel.setBorder(BorderFactory.createLineBorder(Color.black, 6));
    }
    
    /** Change the background color of the PricePanel */
    public void setBackgroundColor(Color color) {
        pricePanel.setBackground(color);
        centerPanel.setBackground(color);
        buyPanel.setBackground(color);
        sellPanel.setBackground(color);
        bottomPanel.setBackground(color);
        topPanel.setBackground(color);
    }
    
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
    
    /** Constructs and shows a dialog that allows the user to select, with sliders, a
     *  specific amount to buy or sell. This is useful for clients who want to make large
     *  offers without having to click 'Buy' or 'Sell' repeatedly */
    private void showOfferDialog() {
        final NumberFormat formatter = NumberFormat.getCurrencyInstance();
        
        //Trick to get a modal dialog to appear in an applet
        Frame f = market.getClientGUI();
        final JDialog showOfferDialog = new JDialog(f, true);
        transactionMode = BUY_MODE;
        
        showOfferDialog.setTitle("Price Level " + formatter.format(price));
        
        if (yourOffers > 0)
            showOfferDialog.getContentPane().setLayout(new GridLayout(4, 1));
        else
            showOfferDialog.getContentPane().setLayout(new GridLayout(3, 1));
        
        JPanel buyPanel = new JPanel();
        JPanel sellPanel = new JPanel();
        JPanel cancelPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        
        final JButton finishButton = new JButton("Make Order");
        final JButton cancelButton = new JButton("Cancel");
        final JSlider buySlider = new JSlider(0, 20);
        final JSlider sellSlider = new JSlider(0, 20);
        final JSlider cancelSlider = new JSlider(0, yourOffers);
        
        final JLabel buyLabel = new JLabel("Buy Amount");
        final JLabel sellLabel = new JLabel("Sell Amount");
        final JLabel cancelLabel = new JLabel("Cancel Amount");
        
        buyPanel.setLayout(new BoxLayout(buyPanel, BoxLayout.Y_AXIS));
        buyLabel.setForeground(new Color(0, 51, 102));
        buySlider.setMajorTickSpacing(5);
        buySlider.setMinorTickSpacing(1);
        buySlider.setValue(0);
        buySlider.setPaintTicks(true);
        buySlider.setPaintLabels(true);
        buySlider.setSnapToTicks(true);
        buySlider.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                updateSliderColor(buySlider, buyColor);
                updateSliderColor(sellSlider, Color.gray);
                updateSliderColor(cancelSlider, Color.gray);
                sellLabel.setForeground(Color.gray);
                buyLabel.setForeground(buyColor);
                cancelLabel.setForeground(Color.gray);
                finishButton.setForeground(buyColor);
                
                transactionMode = BUY_MODE;
            }
        });
        updateSliderColor(buySlider, buyColor);
        buyPanel.add(buyLabel);
        buyPanel.add(buySlider);
        finishButton.setForeground(buyColor);
        
        sellPanel.setLayout(new BoxLayout(sellPanel, BoxLayout.Y_AXIS));
        sellLabel.setForeground(Color.black);
        sellSlider.setMajorTickSpacing(5);
        sellSlider.setMinorTickSpacing(1);
        sellSlider.setValue(0);
        sellSlider.setPaintTicks(true);
        sellSlider.setPaintLabels(true);
        sellSlider.setSnapToTicks(true);
        sellSlider.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                updateSliderColor(sellSlider, sellColor);
                updateSliderColor(buySlider, Color.gray);
                updateSliderColor(cancelSlider, Color.gray);
                sellLabel.setForeground(sellColor);
                buyLabel.setForeground(Color.gray);
                cancelLabel.setForeground(Color.gray);
                finishButton.setForeground(sellColor);
                
                transactionMode = SELL_MODE;
            }
        });
        updateSliderColor(sellSlider, Color.gray);
        sellPanel.add(sellLabel);
        sellPanel.add(sellSlider);
        sellLabel.setForeground(Color.gray);
        
        cancelPanel.setLayout(new BoxLayout(cancelPanel, BoxLayout.Y_AXIS));
        cancelLabel.setForeground(Color.black);
        cancelSlider.setMajorTickSpacing(5);
        cancelSlider.setMinorTickSpacing(1);
        cancelSlider.setValue(0);
        cancelSlider.setPaintTicks(true);
        cancelSlider.setPaintLabels(true);
        cancelSlider.setSnapToTicks(true);
        cancelSlider.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                updateSliderColor(sellSlider, Color.gray);
                updateSliderColor(buySlider, Color.gray);
                updateSliderColor(cancelSlider, cancelColor);
                cancelLabel.setForeground(cancelColor);
                sellLabel.setForeground(Color.gray);
                buyLabel.setForeground(Color.gray);
                finishButton.setForeground(cancelColor);
                
                transactionMode = CANCEL_MODE;
            }
        });
        updateSliderColor(cancelSlider, Color.gray);
        cancelPanel.add(cancelLabel);
        cancelPanel.add(cancelSlider);
        cancelLabel.setForeground(Color.gray);
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showOfferDialog.setVisible(false);
                showOfferDialog.dispose();
                /*
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setBlinkingColor(cancelBlinkColor);
                pricePanel.setSettlingColor(neutralColor);
                timer.start();
                 */
            }
        });
        buttonPanel.add(cancelButton);
        
        finishButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Color color = buyBlinkColor;
                if (transactionMode == SELL_MODE)
                    color = sellBlinkColor;
                if (transactionMode == CANCEL_MODE)
                    color = cancelBlinkColor;
                
                showOfferDialog.setVisible(false);
                showOfferDialog.dispose();
                
                /*
                pricePanel.setNeutralColor(neutralColor);
                pricePanel.setBlinkingColor(color);
                pricePanel.setSettlingColor(color);
                timer.start();
                 */
                
                if (transactionMode == BUY_MODE)
                    outputTransaction(JMConstants.BUY_ACTION, buySlider.getValue());
                else if (transactionMode == SELL_MODE)
                    outputTransaction(JMConstants.SELL_ACTION, sellSlider.getValue());
                else
                    outputTransaction(JMConstants.CANCEL_ACTION, cancelSlider.getValue());
            }
        });
        buttonPanel.add(finishButton);
        
        showOfferDialog.getContentPane().add(buyPanel);
        showOfferDialog.getContentPane().add(sellPanel);
        if (yourOffers > 0)
            showOfferDialog.getContentPane().add(cancelPanel);
        showOfferDialog.getContentPane().add(buttonPanel);
        
        if (yourOffers > 0)
            showOfferDialog.setSize(new Dimension(300, 470));
        else
            showOfferDialog.setSize(new Dimension(300, 350));
        
        //Position the Dialog
        Point point = new Point(0, 0);
        SwingUtilities.convertPointToScreen(point, pricePanel);
        
        Dimension diagDim = showOfferDialog.getSize();
        Dimension panelDim = pricePanel.getSize();
        int xdif = (diagDim.width - panelDim.width) / 4;
        int ydif = (diagDim.height - panelDim.height) / 4;
        Point position = new Point(point.x - xdif, point.y - ydif);
        
        showOfferDialog.setLocation(position);
        showOfferDialog.pack();
        showOfferDialog.setVisible(true);
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
    
    /**
     * Input the given transaction into the pricelevel. This is called upon receiving
     * new orders, so perform all flickering, etc. animations. The units array is of
     * the following form:
     *
     *  0) The total bids on the pricelevel
     *  1) The total asks on the pricelevel
     *  2) The number of bids by player subjectId on the pricelevel
     *  3) The number of asks by player subjectId on the pricelevel
     */
    public abstract void inputTransaction(int action, boolean myAction, int[] units, OfferBook offerBook);
    
    /**
     * Update the pricelevel to the given number of units.
     */
    public abstract Color updateUnits(int[] units, OfferBook offerBook);
    
    /** Set the panels labels appropriately, without flickering, to the units specified
     *  in the array. Returns the color that this panel should then be set to. */
    public void setUnits(int[] units, OfferBook offerBook) {
        Color color = updateUnits(units, offerBook);
        setBackgroundColor(color);
    }
    
    /** Flicker the border of the price panel the given number of times with the given
     *  delay between flickers */
    public void flickerBorder(int num, long delay) {
        borderControl.n = num;
        borderControl.d = delay;
        borderTimer.setDelay((int) delay/2);
        borderTimer.setRepeats(true);
       
        borderTimer.start();
    }
    
    /** Disable this PriceLevel, preventing all further updates and disallowing
     *  any further clicks */
    public abstract void disable();
    
    /** Get the center coordinates of this PricePanel, in the coordinate space of the
     *  Market column container */
    public int getCenterHeight() {
        Dimension size = pricePanel.getSize();
        Point centerInPanel = new Point(size.width / 2, size.height / 2);
        Point centerInMarket = SwingUtilities.convertPoint(pricePanel, centerInPanel, market.getPriceColumn());
        
        return centerInMarket.y;
    }
    
    /** Test the display of the panel */
    public static void main(String[] args) {
        PriceLevel pl = new ContinuousMarketPriceLevel(0, 25f, null, null);
        
        int[] units = {0, 1, 0, 1};
        //pl.inputTransaction(JMConstants.BUY_ACTION, units);
        
        pl.flickerBorder(3, 1000);
        
        JFrame holder = new JFrame();
        holder.getContentPane().add(pl.getPricePanel());
        holder.pack();
        holder.setVisible(true);
    }
    
    class BorderControl implements java.awt.event.ActionListener {
        
        public long d;
        
        public long n;
        
        public void actionPerformed(ActionEvent e) {
            run();
        }
        
        public void run() {
            if (numCompleted % 2 == 0) {
                pricePanel.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
                pricePanel.repaint();
            } else {
                pricePanel.setBorder(BorderFactory.createLineBorder(Color.black, 3));
                pricePanel.repaint();
            }
            
            numCompleted++;
            if (numCompleted >= 2*n) {
                borderTimer.stop();
                numCompleted = 0;
            }
        }
        
        private synchronized void waitDelay(long time) {
            try {
                wait(time);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        private int numCompleted = 0;
    }
    
    /** This inner class is essentially a JPanel with an ActionListener that allows
     *  for blinking animations when the panel is clicked */
    class PricePanel extends JPanel implements java.awt.event.ActionListener {
        
        public PricePanel() {
            super();
            blinksSoFar = 0;
            onNeutralColor = true;
            
            setOpaque(false);
        }
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            
            if (totalAsks == 0 && totalBids == 0)
                return;
            
            if (blinksSoFar > 0)
                return;
            
            Dimension size = getSize();
            int fontSize = Math.min(size.width - 20, size.height - 20);
            
            String str = "" + yourOffers;
            Font font = new Font("Arial Black", 0, fontSize);
            g2d.setFont(font);
            
            if (totalAsks == 0)
                g2d.setPaint(buyOrdersColor);
            else
                g2d.setPaint(sellOrdersColor);
            
            double strWidth = g2d.getFontMetrics().getStringBounds(str, g2d).getWidth();
            double strHeight = g2d.getFontMetrics().getStringBounds(str, g2d).getHeight();  //note that strHeight is DOUBLE the height of the actual string
            
            //NOTE THAT THE OVERLAY IS TEMPORARILY DISABLED FIXME: ADD A SWITCH FOR THE OVERLAY
            //if (str != null)
            //    g2d.drawString(str, (float) (size.getWidth() / 2) - (float) (strWidth / 2), (float) size.getHeight() - (float) (size.getHeight() / 2) + (float) (strHeight / 4));// + (float) (strHeight / 3));
        }
        
        public void actionPerformed(ActionEvent e) {
            if (blinksSoFar < NUM_BLINKS) {
                if (onNeutralColor) {
                    setBackgroundColor(blinkingColor);
                } else {
                    setBackgroundColor(neutralColor);
                }
                blinksSoFar++;
                onNeutralColor = !onNeutralColor;
            } else {
                timer.stop();
                blinksSoFar = 0;
                onNeutralColor = true;
                setBackgroundColor(settlingColor);
            }
            repaint();
        }
        
        protected void setBlinkingColor(Color color) {
            this.blinkingColor = color;
        }
        
        protected void setNeutralColor(Color color) {
            this.neutralColor = color;
        }
        
        protected void setSettlingColor(Color color) {
            this.settlingColor = color;
        }
        
        private int blinksSoFar;
        private boolean onNeutralColor;
        private Color blinkingColor, neutralColor, settlingColor;
    }
    
    public int getId() {
        return id;
    }
    
    /** The ID number of this price level (the ID is unique only for this market) */
    protected int id;
    
    /** True if this PriceLevel is enabled, false if it has been disabled, preventing
     *  any further mouse clicks and disallowing all further updates */
    protected boolean enabled;
    
    /** The price associated with this PriceLevel */
    protected float price;
    
    /** The number of offers made by this client on this price level */
    protected int yourOffers;
    
    /** The JPanel associated with this price level */
    protected PricePanel pricePanel;
    
    /** The JLabel that displays the price */
    protected JLabel priceLabel;
    
    /** The market that this price level belongs to */
    protected Market market;
    
    /** The main client controller */
    protected Client client;
    
    /** The buy button */
    protected JButton buyButton;
    
    /** The sell button */
    protected JButton sellButton;
    
    /** The panels that contain the buy and sell 'buttons' */
    protected JPanel buyPanel, sellPanel;
    
    /** The pricePanel is subdivided into these */
    protected JPanel centerPanel, topPanel, bottomPanel;
    
    /** These are the information labels that must be updated when bids/asks are made */
    protected JLabel buyLabel, sellLabel, ordersLabel, cancelLabel;
    
    /** The total number of bids and asks currently made at this price level */
    protected int totalBids, totalAsks;
    
    /** The showOfferDialog uses this boolean to keep track of whether the player is buying, selling
     *  or canceling */
    protected int transactionMode;
    
    /** This timer controls the blinking animation */
    protected javax.swing.Timer timer;
    
    /** This timer controls the border highlight animation */
    protected javax.swing.Timer borderTimer;
    
    protected BorderControl borderControl;
    
    
    //protected static Color buyColor = new Color(51, 102, 0);
    protected static Color buyColor = new Color(0, 51, 102);
    protected static Color sellColor = new Color(153, 51, 51);
    protected static Color ordersColor = Color.black;
    protected static Color cancelColor = Color.black;
    
    protected static Color buyOrdersColor = new Color(186, 214, 229);//new Color(211, 239, 254);
    protected static Color sellOrdersColor = new Color(231, 186, 187);
    
    protected static Color neutralColor = new Color(204,204,204);
    protected static Color buyBlinkColor = new Color(206, 234, 249);
    protected static Color sellBlinkColor = new Color(251, 206, 207);
    protected static Color cancelBlinkColor = new Color(190, 180, 181);
    protected static Color executeColor = new Color(255, 249, 184);
    
    protected static int BUY_MODE = 0;
    protected static int SELL_MODE = 1;
    protected static int CANCEL_MODE = 2;
    
    protected static int NUM_BLINKS = 15;
}
