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
  * Created on December 7, 2001, 2:52 AM
  */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.interfaces;


import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.orderstable.OrdersInfo;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.orderstable.OrdersRow;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Displays the current orders held by the client. Clicking on an order will
 * center the associated price level around that price. Orders may also be
 * canceled here through the use of the cancel button
 * @author  Raj Advani
 */
public class OrdersPanel extends javax.swing.JPanel {
    
    /** Creates a dummy, empty ClientHistoryPanel. This is the constructor called by
     *  the ClientGUI. It REQUIRES additional initialization by using the setter
     *  methods below
     *  @param name the title of the History Panel
     */
    public OrdersPanel() {
        this(false, true); 
    }
    
    public OrdersPanel(boolean cacheHistory) {
        this(cacheHistory, true); 
    }
    
    public OrdersPanel(boolean cacheHistory, boolean closebook) {
        this.cacheHistory = cacheHistory;
        this.closebook = closebook; 
        
        this.pastOrderPanels = new ArrayList<JPanel>(); 
        this.pastOrdersInfo = new ArrayList<OrdersInfo>(); 
       
        init();
        this.curPeriod =0;
    }

    public void addMarkets(Market [] markets){

        JPanel curOrderPanel = new JPanel();
        if(this.cacheHistory){
            //reveal all orders at the end
            if(closebook){
                for(int i=0; i<this.pastOrderPanels.size(); i++)
                    createOrdersPanel(this.pastOrderPanels.get(i), this.pastOrdersInfo.get(i), false, null);
            }
            
            CardPanel.addTab("Period " + (curPeriod +1), curOrderPanel); 
            CardPanel.setSelectedComponent(curOrderPanel); 
        }else{
            CardPanel.removeAll(); 
            CardPanel.addTab("Period " + (curPeriod +1), curOrderPanel); 
            
            this.pastOrderPanels.clear(); 
            this.pastOrdersInfo.clear(); 
        }
        
        this.curMarkets = markets;
        OrdersInfo curOrdersInfo = new OrdersInfo();
        createOrdersPanel(curOrderPanel, curOrdersInfo, closebook, null);
        
        pastOrderPanels.add(curOrderPanel);
        this.pastOrdersInfo.add(curOrdersInfo); 
        this.curPeriod++; 
    }
    
    /** This intializes the title and adds the match Card Panel */
    private void init() {
        TitleLabel = new JLabel();
        LabelPanel = new JPanel();
        
        darkColor = new Color(204, 204, 204);
        lightColor = LabelPanel.getBackground();
        
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        
        TitleLabel.setText("Orderbook");
        TitleLabel.setForeground(java.awt.Color.black);
        TitleLabel.setFont(new java.awt.Font("Book Antiqua", 0, 15));
        LabelPanel.add(TitleLabel);
        LabelPanel.setPreferredSize(new Dimension(450,35));
        LabelPanel.setMaximumSize(new Dimension(20000,35));
        add(LabelPanel);
        
        this.CardPanel = new JTabbedPane();;
        add(CardPanel);
    }
    
    public void createOrdersPanel(JPanel curOrderPanel, OrdersInfo orderTree, boolean closebook, String triggerSecurity) {
        curOrderPanel.removeAll(); 
        curOrderPanel.setLayout(new BoxLayout(curOrderPanel, BoxLayout.Y_AXIS));
        
        JTabbedPane marketTabs = new JTabbedPane();
        curOrderPanel.add(marketTabs);
        
        Iterator names = orderTree.getSecurityNames();
        int i=0;
        int defaultTab = -1;
        while(names.hasNext()){
            String security = (String)names.next(); 
            JScrollPane scroller = new JScrollPane();
            
            scroller.setPreferredSize(new Dimension(300,200));
            scroller.setMinimumSize(new Dimension(300,200));
            
            JLabel[] columnHeaders = null;
            
            //Add and crop the header panel
            JPanel headPanel = new JPanel();
            scroller.setColumnHeaderView(headPanel);
            initLayout(headPanel);
            
            columnHeaders = buildHeaders(headPanel, false, null);
            buildHistory(orderTree, security, headPanel, false);
            
            JViewport headerView = scroller.getColumnHeader();
            headerView.setPreferredSize(new Dimension(300, getLargestHeight(columnHeaders)));
            
            //Add and crop the info panel
            JPanel infoPanel = new JPanel();
            scroller.setViewportView(infoPanel);
            initLayout(infoPanel);
            
            buildHeaders(infoPanel, true, columnHeaders);
            buildHistory(orderTree, security, infoPanel, closebook);
            
            //Add the bottom filler panel
            JPanel filler = new JPanel();
            gridbag.setConstraints(filler, fillerConstraints);
            infoPanel.add(filler);
            
            scroller.getVerticalScrollBar().setMaximum(infoPanel.getPreferredSize().height);
            scroller.getVerticalScrollBar().setValue(infoPanel.getPreferredSize().height);
            
            marketTabs.addTab(security, scroller);
            if(triggerSecurity != null && triggerSecurity.equalsIgnoreCase(security))
                defaultTab =i;

            i++;
        }
        if(defaultTab>=0)
            marketTabs.setSelectedIndex(defaultTab);
        
    }
    
    /** From an array of column labels return the largest preferred height found */
    private int getLargestHeight(JLabel[] columnLabels) {
        int largestHeight = 0;
        for (int i=0; i<columnLabels.length; i++) {
            int height = columnLabels[i].getPreferredSize().height;
            if (height > largestHeight)
                largestHeight = height;
        }
        return largestHeight;
    }
    
    /** Initialize the gridbag layout for the given panel */
    private void initLayout(JPanel panel) {
        gridbag = new GridBagLayout();
        panel.setLayout(gridbag);
        
        normalConstraints = new GridBagConstraints();
        endRowConstraints = new GridBagConstraints();
        fillerConstraints = new GridBagConstraints();
        
        normalConstraints.fill = GridBagConstraints.BOTH;
        normalConstraints.weightx = 1;
        normalConstraints.ipadx = 10;
        
        fillerConstraints.fill = GridBagConstraints.BOTH;
        fillerConstraints.weighty = 1;
        
        endRowConstraints.fill = GridBagConstraints.BOTH;
        endRowConstraints.gridwidth = GridBagConstraints.REMAINDER;
        endRowConstraints.weightx = 1;
        endRowConstraints.ipadx = 10;
    }
    
    private JLabel[] buildHeaders(JPanel panel, boolean blankOut, JLabel[] clabels) {
        String[] headers = {"Price", "Offer", "Units"};
        
        JLabel[] columnLabels = new JLabel[headers.length];
        
        for (int i=0; i<headers.length; i++) {
            JLabel columnLabel = new JLabel();
            
            String text = headers[i];
            Color foreground = Color.blue;
            
            if (blankOut) {
                foreground = columnLabel.getBackground();
                columnLabel.setMaximumSize(new Dimension(32000, 2));
                if (clabels != null)
                    columnLabel.setPreferredSize(new Dimension(clabels[i].getPreferredSize().width, 2));
            }
            
            columnLabel.setText(text);
            columnLabel.setForeground(foreground);
            columnLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            if (i == (headers.length - 1))
                gridbag.setConstraints(columnLabel, endRowConstraints);
            else
                gridbag.setConstraints(columnLabel, normalConstraints);
            
            panel.add(columnLabel);
            columnLabels[i] = columnLabel;
        }
        return columnLabels;
    }
    
    private int buildHistory(OrdersInfo info, String security, JPanel panel, boolean own) {
        OrdersRow[] rows = info.getOrdersInPriceOrder(security, own);
        
        int numRows = rows.length;
        int numCols = 3;
        final JPanel[][] panelRows = new JPanel[numRows][numCols];
        
        final Color defaultBorder = new Color(210, 210, 210);
        
        for (int i=0; i<numRows; i++) {
            OrdersRow row = rows[i];
            final int rowNum = i;
            Color backColor;
            
            if (row.own)
                backColor = darkColor;
            else
                backColor = lightColor;
            
            NumberFormat formatter = new DecimalFormat("##.##");
            
            String[] values = new String[numCols];
            
            //values[0] = row.security;
            values[0] = formatter.format(row.price);
            values[1] = row.action;
            values[2] = "" + row.units;
            //values[4] = "CANCEL";
            
            for (int k=0; k<values.length; k++) {
                JPanel columnPanel = new JPanel();
                JLabel columnLabel = new JLabel();
                
                String data = values[k];
                if (data == null)
                    data = "";
                
                columnLabel.setText(data);
                
                //if (k != 4)
                //    columnLabel.setForeground(Color.black);
                //else
                columnLabel.setForeground(Color.black);
                
                columnLabel.setHorizontalAlignment(SwingConstants.CENTER);
                
                columnPanel.add(columnLabel);
                columnPanel.setBackground(backColor);
                
                if (i == numRows - 1) {
                    if (k == 0)
                        columnPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, defaultBorder));
                    else if  (k == panelRows[rowNum].length-1)
                        columnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, defaultBorder));
                    else
                        columnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, defaultBorder));
                } else {
                    if (k == 0)
                        columnPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, defaultBorder));
                    else if  (k == panelRows[rowNum].length-1)
                        columnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, defaultBorder));
                    else
                        columnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, defaultBorder));
                }
                
                final float price = row.price;
                final String secName = security;
                
                MouseListener listener = new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        int marketId = getMarketId(secName);
                        if (marketId != -1)
                            curMarkets[marketId].centerOnPrice(price);
                    }
                    
                    public void mouseEntered(MouseEvent evt) {
                        for (int j=0; j<panelRows[rowNum].length; j++) {
                            JPanel p = panelRows[rowNum][j];
                            if (j == 0)
                                p.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.blue));
                            else if  (j == panelRows[rowNum].length-1)
                                p.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.blue));
                            else
                                p.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.blue));
                        }
                    }
                    
                    public void mouseExited(MouseEvent evt) {
                        for (int j=0; j<panelRows[rowNum].length; j++) {
                            JPanel p = panelRows[rowNum][j];
                            
                            if (rowNum == panelRows.length - 1) {
                                if (j == 0)
                                    p.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, defaultBorder));
                                else if  (j == panelRows[rowNum].length-1)
                                    p.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, defaultBorder));
                                else
                                    p.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, defaultBorder));
                            } else {
                                if (j == 0)
                                    p.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, defaultBorder));
                                else if  (j == panelRows[rowNum].length-1)
                                    p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, defaultBorder));
                                else
                                    p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, defaultBorder));
                            }
                            
                        }
                    }
                };
                
                columnPanel.addMouseListener(listener);
                columnLabel.addMouseListener(listener);
                
                if (k == (values.length - 1))
                    gridbag.setConstraints(columnPanel, endRowConstraints);
                else
                    gridbag.setConstraints(columnPanel, normalConstraints);
                
                panel.add(columnPanel);
                panelRows[i][k] = columnPanel;
            }
        }
        
        return numRows;
    }
    
    /** Return the ID number of the market with the given name */
    public int getMarketId(String name) {
        for (int i=0; i<curMarkets.length; i++) {
            if (curMarkets[i].getTitle().equals(name))
                return i;
        }
        return -1;
    }
    
    public void updateOrders(final String security, float price, int[] units) {
        //final int verticalPos = scroller.getVerticalScrollBar().getValue();
        this.pastOrdersInfo.get(this.pastOrdersInfo.size()-1).updateOrder(security, price, units);
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                createOrdersPanel(pastOrderPanels.get(pastOrderPanels.size()-1), pastOrdersInfo.get(pastOrdersInfo.size()-1), closebook, security);
                //scroller.getVerticalScrollBar().setValue(verticalPos);
            }
        };
        try{
            SwingUtilities.invokeAndWait(doUpdate);
        }catch(Exception e){
            e.printStackTrace(); 
        }
    }
    
    public static void main(String args[]) {
        OrdersPanel panel = new OrdersPanel(true, true);
        panel.addMarkets(null); 
        
        int[] units = {0, 2, 0, 2};
        int[] units2 = {3, 0, 1, 0};
        
        panel.updateOrders("Security 255", 1.00f, units);
        panel.updateOrders("Security 2", 2.00f, units2);
        
        //add the second period
        panel.addMarkets(null); 

        int [] units3 = {0, 4, 0, 4};
        int [] units4 = {6, 0, 6, 0};
        
        panel.updateOrders("Security 512", 1.00f, units3);
        panel.updateOrders("Security 4", 2.00f, units4);
        
        panel.addMarkets(null); 
        
        int [] units5 = {0, 8, 0, 8};
        int [] units6 = {12, 0, 12, 0};
        
        panel.updateOrders("Security 1024", 2.00f, units5);
        panel.updateOrders("Security 8", 4.00f, units6);
       
        JFrame holder = new JFrame();
        holder.getContentPane().add(panel);
        holder.pack();
        holder.setVisible(true);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel LabelPanel;
    private javax.swing.JLabel TitleLabel;
    private javax.swing.JTabbedPane CardPanel;
    // End of variables declaration//GEN-END:variables
    
    /**
     *Whether to cache orderbook history
     */
    private boolean cacheHistory; 
    
    /**
     * Whether this is a closebook trading session
     */
    private boolean closebook; 
    
    /** 
     * Period counter 
     */
    private int curPeriod;
    
    /** 
     * Past order panels 
     */
    private List <JPanel> pastOrderPanels;
    private List <OrdersInfo> pastOrdersInfo; 
    
    /**
     * Need to keep track of this to remove mouse listeners from past market panels
     */
    //private List <JPanel[][]> pastDataRows; 
    
    /** This object contains all the information on the current orders, formatted for
     *  this panel */
    //private OrdersInfo curOrdersInfo;
    
    /** 
     * Array containing all the Market objects 
     */
    private Market[] curMarkets;
    
    /** The collapsed match panel (akin to the CardPanel but for collapsed match form) */
    //private JPanel curOrderPanel;
    
    /** An array of JPanels that are indexed by row. Each set corresponds to one row
     *  in the table. They are stored here so that the action listeners can act on them */
    //private JPanel[][] panelRows;
    

    
    /** Various layout information */
    private GridBagConstraints normalConstraints, endRowConstraints, fillerConstraints;
    private GridBagLayout gridbag;
    
    private Color darkColor;
    private Color lightColor;
}