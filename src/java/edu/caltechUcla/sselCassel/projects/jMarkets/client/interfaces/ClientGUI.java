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
 * ClientGUI.java
 *
 * Created on February 10, 2004, 5:37 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.interfaces;

import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.text.NumberFormat;
import java.net.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.control.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.interfaces.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable.EarningsInfo;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.MarketDef;

/**
 *
 * @author  Raj Advani
 * @version $Id: ClientGUI.java 353 2005-10-26 21:37:02Z raj $
 */
public class ClientGUI extends JFrame {
    
    // This is a hack to avoid an ugly error message in 1.1.
    public ClientGUI(URL host, ClientInfo cinfo, boolean fast) {
        waitingDialog = new WaitingDialog(this, false, cinfo.getName(), false);
        
        setWaitingDialogActive("Please Wait", "Connecting to Server", 3000);
        
        client = new Client();
        client.init(this, cinfo, host, fast);
        
        centerOnScreen();
        setTitle("JMarkets Client " + cinfo.getName());
        setVisible(true);
    }

    public void centerMarket(int idx) {
        markets[idx].centerOnPrice(markets[idx].getCenterPrice());
    }
    
    /** Center the mainFrame on the screen */
    public void centerOnScreen() {
        Runnable doUpdate = new Runnable() {
            public void run() {
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                Dimension window = getSize();
                int iCenterX = screen.width / 2;
                int iCenterY = screen.height / 2;
                setLocation(iCenterX - (window.width / 2), iCenterY - (window.height / 2));
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    public void destroy() {
        
    }
    
    /** Display a dialog that allows the client to select a session to join. Override
     *  the joinSession method in the dialog so that we can route the selection to the
     *  client control. If this client is set-up for fast mode, then just join the first
     *  session without asking */
    public void chooseSession(boolean fastMode, SessionIdentifier[] identifiers) {
        if (fastMode) {
            int sid = identifiers[0].getSessionId();
            waitingDialog.setActive("Please Wait", "Authenticating Session " + sid, 2000);
            client.joinSession(sid);
            
            return;
        }
        
        sessionSelector = new SessionSelectorDialog(this, identifiers) {
            public void joinSession(int sessionId) {
                sessionSelector.setVisible(false);
                sessionSelector.dispose();
                
                final int sid = sessionId;
                Runnable doUpdate = new Runnable() {
                    public void run() {
                        waitingDialog.setActive("Please Wait", "Authenticating Session " + sid, 2000);
                        client.joinSession(sid);
                    }
                };
                Thread updateThr = new Thread(doUpdate);
                updateThr.start();
            }
        };
        waitingDialog.setInactive();
        sessionSelector.setVisible(true);
    }
    
    /** Display the given error message in a modal dialog */
    public void displayError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /** Construct the title panel */
    public JPanel constructTitle(String name, int id) {
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel(name + " (Subject ID: " + id + ")");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        
        return titlePanel;
    }
    
    /** Start a new period with the current marketInfo, subjectInfo and roleInfo. Called when the
     *  Communicator receives a NEW_PERIOD_RESPONSE */
    public void startPeriod(int periodLength, MarketDef minfo, float cash, int[] holdings, EarningsInfo einfo) {
        try {
            final MarketDef marketInfo = minfo;
            final float initialCash = cash;
            final int[] initialHoldings = holdings;
            final int periodTime = periodLength;
            final EarningsInfo info = einfo;
            
            Runnable doUpdate = new Runnable() {
                public void run() {
                    int numMarkets = marketInfo.getNumMarkets();
                    String[] marketTitles = marketInfo.getMarketTitles();
                    float[][] prices = marketInfo.getPrices();
                    int numDivisions = marketInfo.getNumDivisions();
                    useSliderGui = marketInfo.getUseGui();
                    
                    getContentPane().removeAll();
                    mainTabbedPane = new JTabbedPane();
                    mainTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {

                        public void stateChanged(javax.swing.event.ChangeEvent evt) {
                            mainTabbedPaneStateChanged(evt);
                        }
                    });
                    getContentPane().add(mainTabbedPane);

                    tradingPanel = new JPanel();
                    tradingPanel.setLayout(new BoxLayout(tradingPanel, BoxLayout.Y_AXIS));
                    
                    JPanel titlePanel = constructTitle(client.getName(), client.getId());
                    titlePanel.setMaximumSize(new Dimension(32000, 40));
                    tradingPanel.add(titlePanel);
                    
                    JPanel infoPanel = constructInfo(initialCash, periodTime);
                    infoPanel.setMaximumSize(new Dimension(32000, 60));
                    tradingPanel.add(infoPanel);
                    
                    JPanel bottomPanel = new JPanel();
                    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
                    
                    JPanel marketPanel = generateMarkets(numMarkets, marketTitles, initialHoldings, prices, useSliderGui);
                    
                    JScrollPane scroller = new JScrollPane(marketPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    if( useSliderGui ){
                        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                    }
                    scroller.setPreferredSize(relativeSize(.7f, .75f));
                    bottomPanel.add(scroller);
                    
                    chart = new PriceChart();
                    for (int i=0; i<marketInfo.getNumMarkets(); i++)
                        chart.addSecurity(marketInfo.getMarketTitles()[i]);
                    
                    historyPanel = new JPanel();
                    historyPanel.setBorder(BorderFactory.createTitledBorder("History"));
                    historyPanel.setLayout(new GridLayout(3, 1));
                    
                    JPanel chartPanel = chart.getChartPanel();
                    chartPanel.setPreferredSize(new Dimension(300, 300));
                    chartPanel.setMaximumSize(new Dimension(300, 300));
                    
                    historyPanel.add(chartPanel);
                    
                    //ordersPanel = new OrdersPanel(markets);
                    if(client.getTrader().isCacheOrders()){
                        if(ordersPanel == null)
                            ordersPanel = new OrdersPanel(true, client.getTrader().isClosebook());
                    }else{
                        ordersPanel = new OrdersPanel(false, client.getTrader().isClosebook());
                    }
                    
                    ordersPanel.addMarkets(markets); 
                    
                    historyPanel.add(ordersPanel);
                    
                    tabbedPane = new JTabbedPane();
                    
                    earningsPanel = new EarningsPanel(info);
                    //historyPanel.add(earningsPanel);
                    tabbedPane.addTab("Earnings", earningsPanel);
                    
                    if(client.getTrader().isCacheTransactions()){
                        if(transactionsPanel == null)
                            transactionsPanel = new TransactionsPanel(true);
                    }else{
                        transactionsPanel = new TransactionsPanel(false);
                    }
                    transactionsPanel.addMarkets(markets);
                    //historyPanel.add(transactionsPanel);
                    tabbedPane.addTab("Transactions", transactionsPanel);
                    
                    tabbedPane.setSelectedComponent(transactionsPanel);
                    historyPanel.add(tabbedPane);
                    
                    historyPanel.setPreferredSize(relativeSize(.25f, .75f));
                    bottomPanel.add(historyPanel);
                    tradingPanel.add(bottomPanel);

                    mainTabbedPane.addTab("Market Panel", tradingPanel);
                    String ann = client.getTrader().getAnnouncement();
                    if(ann != null && ann.trim().length()>0){
                        msgPanel = new MessagePanel();
                        mainTabbedPane.addTab("Message Board", msgPanel);
                        msgPanel.appendMsg(ann);
                        mainTabbedPane.setBackgroundAt(1, Color.CYAN);
                    }
                    
                    pack();
                    centerOnScreen();
                }
            };
            SwingUtilities.invokeAndWait(doUpdate);
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void bringEarningsPanelToFront(){
        tabbedPane.setSelectedComponent(earningsPanel);
    }
    public void bringTransactionsPanelToFront(){
        tabbedPane.setSelectedComponent(transactionsPanel);
    }
    
    protected Dimension relativeSize(float widthMultiple, float heightMultiple) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screen.width * widthMultiple);
        int height = (int) (screen.height * heightMultiple);
        Dimension dim = new Dimension(width, height);
        return dim;
    }
    
    /** Given an offer book in 3D array representation, update the visual display to match it. This is
     *  used during re-authentication */
    public void updateBook(int id, OfferBook offerBook) {
        int[][][] offerbook = offerBook.getOfferBook();
        
        for (int m=0; m<offerbook.length; m++) {
            PriceLevel[] priceLevels = markets[m].getPriceLevels();
            
            for (int p=0; p<offerbook[m].length; p++) {
                int[] units = new int[4];
                
                int bids = 0;
                int asks = 0;
                int yourBids = 0;
                int yourAsks = 0;
                
                for (int s=0; s<offerbook[m][p].length; s++) {
                    if (offerbook[m][p][s] == 0)
                        continue;
                    else if (offerbook[m][p][s] > 0) {
                        bids += offerbook[m][p][s];
                        if (s == id)
                            yourBids += offerbook[m][p][s];
                    } else {
                        asks -= offerbook[m][p][s];
                        if (s == id)
                            yourAsks -= offerbook[m][p][s];
                    }
                }
                
                units[0] = bids;
                units[1] = asks;
                units[2] = yourBids;
                units[3] = yourAsks;
                
                priceLevels[p].setUnits(units, offerBook);
                ordersPanel.updateOrders(markets[m].getTitle(), markets[m].getPrices()[p], units);
                
            }
        }
    }
    
    
    
    /** Called upon re-authentication, this method inserts the given PriceChart into the ClientGUI
     *  display */
    public void insertPriceChart(Vector priceChart) {
        chart = new PriceChart();
        chart.setSecurities(priceChart);
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                JPanel chartPanel = chart.getChartPanel();
                chartPanel.setPreferredSize(new Dimension(300, 300));
                chartPanel.setMaximumSize(new Dimension(300, 300));
                
                JPanel earningsPanel = (JPanel) historyPanel.getComponent(2);
                
                historyPanel.removeAll();
                historyPanel.add(chartPanel);
                
                if (ordersPanel != null)
                    historyPanel.add(ordersPanel);
                else
                    historyPanel.add(new JPanel());
                
                if (earningsPanel != null)
                    historyPanel.add(earningsPanel);
                else
                    historyPanel.add(new JPanel());
                
                historyPanel.setPreferredSize(relativeSize(.25f, .75f));
                
                pack();
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Called when an earnings info response is received. Replaces the earnings panel with
     *  a new one */
    public void insertEarningsPanel(EarningsInfo einfo) {
        final EarningsInfo info = einfo;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                earningsPanel = new EarningsPanel(info);
                /*JPanel chartPanel = (JPanel) historyPanel.getComponent(0);
                
                historyPanel.removeAll();
                
                if (chartPanel != null)
                    historyPanel.add(chartPanel);
                else
                    historyPanel.add(new JPanel());
                
                if (ordersPanel != null)
                    historyPanel.add(ordersPanel);
                else
                    historyPanel.add(new JPanel());
                
                historyPanel.add(earningsPanel);
                historyPanel.setPreferredSize(relativeSize(.25f, .75f));
                */
                tabbedPane.setComponentAt(0, earningsPanel); 
                pack();
            }
        };
        
        try {
            SwingUtilities.invokeAndWait(doUpdate);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    
    /** Update the price chart with the given price point for the given security */
    public void updatePriceChart(String security, float time, float price) {
        final String s = security;
        final float t = time;
        final float p = price;

        Runnable doUpdate = new Runnable() {
            public void run() {
                if (chart != null)
                    chart.addPoint(s, t, p);
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Update the time label with the given amount of time */
    public void setTimeLabel(int time) {
        final int t = time;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                timeLabel.setText("<html>Time Remaining: <font color=#993333>" + t + "          </font></html>");
            }
        };
        
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Update the time label of the given market with the given amount of time */
    public void setTimeLabel(int market, int time) {
        markets[market].setTimeLabel(time);
    }
    
    /** Update the cash holdings label with the given holdings */
    public void setCashLabel(float cash) {
        final NumberFormat formatter = NumberFormat.getCurrencyInstance();
        final float c = cash;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                cashLabel.setText("<html>Total Cash Holdings: <font color=#336600>" + formatter.format(c) + "          </font></html>");
            }
        };
        
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Set security holdings label of the given market to the given amount of holdings */
    public void setHoldingsLabel(int marketId, int holdings) {
        final int h = holdings;
        final int mid = marketId;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                markets[mid].setHoldings(h);
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Deactives the current period by disabling the markets and displaying a wait message */
    public void deactivatePeriod() {
        disableAllMarkets();
        this.setTimeLabel(0);
        waitingDialog.setActive("Please Wait", "The period has come to an end", 2000);
    }
    
    /** Disable every market */
    public void disableAllMarkets() {
        for (int i=0; i<numMarkets; i++)
            disableMarket(i);
    }
    
    /** Disable the given market -- Disallow any further trades on the market and turn off all
     *  updates to it */
    public void disableMarket(int marketNum) {
        Market market = markets[marketNum];
        market.disable();
    }
    
    /** Sets the market's time label to 'Closed' */
    public void setClosedLabel(int marketNum) {
        markets[marketNum].setClosedLabel();
    }
    
    /** Construct the information panel that informs the client of his/her total cash holdings and
     *  time remaining in the period */
    private JPanel constructInfo(float cash, int time) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 2));
        
        infoPanel.add(new JPanel());
        
        timeLabel = new JLabel("<html>Time Remaining: <font color=#993333>" + time + "          </font></html>");
        timeLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        timeLabel.setForeground(Color.black);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        infoPanel.add(timeLabel);
        
        infoPanel.add(new JPanel());
        
        cashLabel = new JLabel("<html>Total Cash Holdings: <font color=#336600>" + formatter.format(cash) + "          </font></html>");
        cashLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        cashLabel.setForeground(Color.black);
        cashLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        infoPanel.add(cashLabel);
        
        return infoPanel;
    }

    private void mainTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {
        // TODO add your handling code here:
        int index = mainTabbedPane.getSelectedIndex();
        if (index >= 0){
            mainTabbedPane.setBackgroundAt(index, mainTabbedPane.getBackground());
        }
    }
    
    /** Load in the markets */
    private JPanel generateMarkets(int numMarkets, String[] marketTitles, int[] initialHoldings, float[][] prices) {
        return generateMarkets(numMarkets, marketTitles, initialHoldings, prices, false/*, false*/);
    }
    
    private JPanel generateMarkets(int numMarkets, String[] marketTitles, int[] initialHoldings, float[][] prices, boolean useSliderGui) {
        this.numMarkets = numMarkets;
        markets = new Market[numMarkets];
        this.useSliderGui = useSliderGui;
        
        
        JPanel marketPanel = new JPanel();
        marketPanel.setBorder(BorderFactory.createTitledBorder("Active Markets"));
        if( useSliderGui ){
            marketPanel.setLayout(new GridLayout(numMarkets, 0));
        } else {
            marketPanel.setLayout(new GridLayout(0, numMarkets));
        }
        
        for (int i=0; i<markets.length; i++) {
            Market market;
            if( !useSliderGui ){
                market = new Market(i, marketTitles[i], initialHoldings[i], prices[i], client);
            } else {
                market = new ScrollMarket(i, marketTitles[i], initialHoldings[i], prices[i], client);
            }
            market.initializeMarket(this);
            
            JPanel column = market.getMarketColumn();
            marketPanel.add(column);
            markets[i] = market;
        }
        
        return marketPanel;
    }
    
    public void setWaitingDialogActive(String topMessage, String bottomMessage, long time) {
        waitingDialog.setActive(topMessage, bottomMessage, time);
    }
    
    public void setWaitingDialogActive(String topMessage, String bottomMessage) {
        waitingDialog.setActive(topMessage, bottomMessage);
    }
    
    public void setWaitingDialogInactive() {
        waitingDialog.setInactive();
    }
    
    public boolean isWaitingDialogNull() {
        if (waitingDialog != null)
            return false;
        return true;
    }
    
    public Market[] getMarkets() {
        return markets;
    }
    
    /** Update the orders panel with the given order information */
    public void updateOrdersPanel(String security, float price, int[] units) {
        if (ordersPanel == null)
            return;
        
        ordersPanel.updateOrders(security, price, units);
    }
    
    public void updateTransactions(String action, String security, float txnPrice, float stdPrice, int unitsTraded, long time, boolean owned) {
        transactionsPanel.updateTransactions(action, security, txnPrice, stdPrice, unitsTraded, time, owned);
    }
    
    /** The time remaining in the current period for each market/security, displayed here */
    private int[] marketTimeRemaining;
    
    /** This is the modal dialog screen used for all "Please Wait" messages */
    private WaitingDialog waitingDialog;
    
    /** The main client controller */
    public Client client;
    
    private SessionSelectorDialog sessionSelector;
    private JPanel historyPanel;
    
    /** This panel tracks the orders made by this client on the sidebar */
    private OrdersPanel ordersPanel;
    private TransactionsPanel transactionsPanel;
    private JPanel earningsPanel;
    private JTabbedPane tabbedPane;

    private JPanel tradingPanel;
    private MessagePanel msgPanel;
    private JTabbedPane mainTabbedPane;
    
    private JLabel timeLabel, cashLabel;
    private int numMarkets;
    private PriceChart chart;
    private Market[] markets;
    private boolean useSliderGui;
    private boolean allowTransactions=true;
}
