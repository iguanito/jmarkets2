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
 * Market.java
 *
 * Created on February 10, 2004, 6:59 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.interfaces;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.control.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;

/**
 *
 * @author  Raj Advani, Walter Yuan
 */
public class Market {
    
    /** Creates a new instance of Market */
    public Market(int id, String title, int initialHoldings, float[] prices, Client client) {
        this.id = id;
        this.title = title;
        this.prices = prices;
        this.holdings = initialHoldings;
        this.client = client;
    }
    
    public void initializeMarket(ClientGUI gui) {
        this.gui = gui;
        
        String marketEngine = client.getMarketEngine();
        priceLevels = new PriceLevel[prices.length];
        for (int i=0; i<prices.length; i++) {
            if (marketEngine.equalsIgnoreCase(JMConstants.CALL_MARKET_ENGINE)) {
                priceLevels[i] = new CallMarketPriceLevel(i, prices[i], this, client);
            }
            else {
                priceLevels[i] = new ContinuousMarketPriceLevel(i, prices[i], this, client);
            }
        }
        
        marketColumn = new JPanel();
        marketColumn.setLayout(new BoxLayout(marketColumn, BoxLayout.Y_AXIS));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new GridLayout(3, 1, 10, 10));
        titlePanel.setMinimumSize(new Dimension(50, (int) relativeSize(.5f, .6f).getHeight()));
        titlePanel.setMaximumSize(new Dimension(1000, (int) relativeSize(.5f, .6f).getHeight()));
        
        titleLabel = new JLabel("<html><font color=#003366>" + title + "</font></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        titleLabel.setForeground(Color.blue);
        titlePanel.add(titleLabel);
        
        holdingsLabel = new JLabel("<html><font color=#000000>Holdings: </font><font color=#993333><b>" + holdings + "</b></html>");
        holdingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        holdingsLabel.setFont(new Font("Verdana", 0, 14));
        final Color defaultHoldingsColor = holdingsLabel.getForeground();
        
        MouseAdapter holdingsMouse = new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                
            }
            
            public void mouseClicked(MouseEvent evt) {
                if (client == null || client.getTrader() == null)
                    return;
                
                int numPurchases = client.getTrader().getTotalPurchases(id);
                int numSales = client.getTrader().getTotalSales(id);
                
                if (popup == null || !popup.isVisible()) {
                    popup = new JPopupMenu();
                     
                    JLabel titleLabel = new JLabel(title);
                    JLabel purchases = new JLabel("Purchases: " + numPurchases);
                    JLabel sales = new JLabel("Sales: " + numSales);
                    
                    popup.add(titleLabel);
                    popup.addSeparator();
                    popup.add(purchases);
                    popup.add(sales);
        
                    popup.show(evt.getComponent(), evt.getX(), evt.getY());
                } 
            }
            
            public void mouseEntered(MouseEvent evt) {
                holdingsLabel.setForeground(Color.blue);
            }
            
            public void mouseExited(MouseEvent evt) {
                holdingsLabel.setForeground(defaultHoldingsColor);
            }
        };
        
        holdingsLabel.addMouseListener(holdingsMouse);
        titlePanel.add(holdingsLabel);
        
        JPanel averagePanel = new JPanel();
        bestBuyButton = new JButton("Best Buy");
        bestBuyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                int centerPrice = client.getMarketBestBuy(id);
                if (centerPrice == -1)
                    return;
                
                int centerHeight = priceLevels[centerPrice].getCenterHeight();
                int targetHeight = Math.max(0, centerHeight - priceScroller.getHeight() / 2);
                
                priceScroller.getVerticalScrollBar().setValue(targetHeight);
            }
        });
        averagePanel.add(bestBuyButton);
        
        bestSellButton = new JButton("Best Sell");
        bestSellButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                int centerPrice = client.getMarketBestSell(id);
                if (centerPrice == -1)
                    return;
                
                int centerHeight = priceLevels[centerPrice].getCenterHeight();
                int targetHeight = Math.max(0, centerHeight - priceScroller.getHeight() / 2);
                
                priceScroller.getVerticalScrollBar().setValue(targetHeight);
            }
        });
        averagePanel.add(bestSellButton);
        titlePanel.add(averagePanel);
        
        marketColumn.add(titlePanel);
        
        priceColumn = new PriceColumn();
        priceScroller = new JScrollPane(priceColumn, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        priceScroller.setPreferredSize(new Dimension(MARKET_WIDTH, (int) relativeSize(.5f, .55f).getHeight()));
        priceScroller.setMaximumSize(new Dimension(1000, (int) relativeSize(.5f, .575f).getHeight()));
        
        priceColumn.setLayout(new GridLayout(0, 1));
        for (int i=(prices.length-1); i>=0; i--) {
            priceColumn.add(priceLevels[i].getPricePanel());
        }
        
        marketColumn.add(priceScroller);
        
        JPanel timePanel = new JPanel();
        timeLabel = new JLabel("Initializing");
        timeLabel.setFont(new Font("Georgia", Font.BOLD, 12));
        timeLabel.setForeground(Color.black);
        timePanel.add(timeLabel);
        
        timePanel.setMinimumSize(new Dimension(50, (int) relativeSize(.5f, .5f).getHeight()));
        timePanel.setMaximumSize(new Dimension(1000, (int) relativeSize(.5f, .5f).getHeight()));
        
        marketColumn.add(timePanel);

        this.centerOnPrice((this.prices[0] + this.prices[this.prices.length-1])/2); 
    }
    
    /** Update the time label with the given amount of time */
    public void setTimeLabel(int time) {
        final int t = time;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                timeLabel.setText("<html>Time Left: <font color=#993333>" + t + "          </font></html>");
            }
        };
        
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Disable this market, preventing any further updates and clicks */
    public void disable() {
        Runnable doUpdate = new Runnable() {
            public void run() {
                marketColumn.setEnabled(false);
                titleLabel.setText("<html><font color=#888888>" + title + "</font></html>");
                bestBuyButton.setEnabled(false);
                bestSellButton.setEnabled(false);
                holdingsLabel.setText("<html><font color=#888888>Holdings: <b>" + holdings + "</b></font></html>");
            }
        };
        SwingUtilities.invokeLater(doUpdate);
        
        for (int i=0; i<priceLevels.length; i++)
            priceLevels[i].disable();
    }
    
    public void setClosedLabel() {
        Runnable doUpdate = new Runnable() {
            public void run() {
                timeLabel.setText("Closed");
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    protected Dimension relativeSize(float widthMultiple, float heightMultiple) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screen.width * widthMultiple);
        int height = (int) (screen.height * heightMultiple);
        Dimension dim = new Dimension(width, height);
        return dim;
    }
    
    /** Set the the player's holdings of this security to the given amount */
    public void setHoldings(int holdings) {
        this.holdings = holdings;
        holdingsLabel.setText("<html><font color=#000000>Holdings: </font><font color=#993333><b>" + holdings + "</b></html>");
    }
    
    /** Center this market scroller onto the given price */
    public void centerOnPrice(float price) {
        final float p = price;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                int priceId = getPriceId(p);
                if (priceId == -1)
                    return;
                
                int centerHeight = priceLevels[priceId].getCenterHeight();
                int targetHeight = Math.max(0, centerHeight - priceScroller.getHeight() / 2);
                
                priceScroller.getVerticalScrollBar().setValue(targetHeight);
                
                priceLevels[priceId].flickerBorder(2, 1000);
            }
        };
        
        SwingUtilities.invokeLater(doUpdate);
    }

    float getCenterPrice() {
        return this.prices[(this.prices.length-1)/2];
    }
    
    /** Get the price level id of the given price. Return -1 if the price doesn't exist */
    private int getPriceId(float price) {
        for (int i=0; i<prices.length; i++) {
            if (prices[i] == price)
                return i;
        }
        return -1;
    }
    
    /** Return the column GUI representation of this market */
    public JPanel getMarketColumn() {
        return marketColumn;
    }
    
    /** Return the column that contains all the PricePanels */
    public JPanel getPriceColumn() {
        return priceColumn;
    }
    
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
    
    public java.lang.String getTitle() {
        return title;
    }
    
    public Client getClient(){
        return client;
    }
    
    public int getHoldings(){
        return holdings;
    }
    
    public void setTitle(java.lang.String title) {
        this.title = title;
    }
    
    /** The ID number of the market */
    private int id;
    
    /** The title of the market */
    private String title;
    
    /** The amount of this security owned by the client */
    private int holdings;
    
    /** Scroll Pane containing the PriceLevel panels, part of marketColumn */
    private JScrollPane priceScroller;
    
    /** Array containing the price levels as float values */
    private float[] prices;
    
    /** The ClientGUI containing this market */
    private ClientGUI gui;
    
    /** Array of PriceLevel objects in ascending order (lowest price first) */
    private PriceLevel[] priceLevels;
    
    /** JPanel that contains all the PriceLevel panels (the column) */
    private JPanel priceColumn;
    
    /** The label that displays the holdings of this security */
    private JLabel holdingsLabel;
    
    /** Holds the title of the market */
    private JLabel titleLabel;
    
    /** The main column panel */
    private JPanel marketColumn;
    
    /** The main client controller */
    private Client client;
    
    /** JLabel containing the time remaining for this security */
    private JLabel timeLabel;
    
    /** When this button is pressed the gui is signalled to center the scrollbar of
     *  this market onto the average price (halfway between bids and asks) */
    private JButton bestSellButton;
    private JButton bestBuyButton;
    
    /** The JPopupMenu that displays the number of sales and purchases of the clicked-on
     *  security */
    private JPopupMenu popup;
    
    /** A JPanel that allows for faster scrolling speed */
    class PriceColumn extends JPanel implements Scrollable {
        
        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension(MARKET_WIDTH, (int) relativeSize(.5f, .55f).getHeight());
        }
        
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 30;
        }
        
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
        
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }
        
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 30;
        }
        
    }
    
    public static int MARKET_WIDTH = 140;
}
