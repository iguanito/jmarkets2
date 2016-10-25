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

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.text.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable.EarningsInfo;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable.EarningsRow;

/**
 * Displays the history of clients actions. Columns are added through the ServerControl
 * class by using the addColumn functions. This class retrieves all its data from the
 * ClientData objects.
 * @author  Raj Advani
 */
public class EarningsPanel extends javax.swing.JPanel {
    
    /** Creates a dummy, empty ClientHistoryPanel. This is the constructor called by
     *  the ClientGUI. It REQUIRES additional initialization by using the setter
     *  methods below
     *  @param name the title of the History Panel
     */
    public EarningsPanel(EarningsInfo info) {
        init();
        createEarningsPanel(info);
    }
    
    /** This intializes the title and adds the match Card Panel */
    private void init() {
        TitleLabel = new JLabel();
        LabelPanel = new JPanel();
        
        darkColor = new Color(220, 220, 220);
        lightColor = LabelPanel.getBackground();
        
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        
        TitleLabel.setText("Earnings History");
        TitleLabel.setForeground(java.awt.Color.black);
        TitleLabel.setFont(new java.awt.Font("Book Antiqua", 0, 15));
        LabelPanel.add(TitleLabel);
        
        add(LabelPanel);
        
        FullPanel = new JPanel();
        add(FullPanel);
    }
    
    public void createEarningsPanel(EarningsInfo info) {
        LabelPanel.setPreferredSize(new Dimension(450,35));
        LabelPanel.setMaximumSize(new Dimension(20000,35));
        FullPanel.setLayout(new BoxLayout(FullPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scroller = new JScrollPane();
        
        scroller.setPreferredSize(new Dimension(300,200));
        scroller.setMinimumSize(new Dimension(300,200));
        
        JLabel[] columnHeaders = null;
        
        //Add and crop the header panel
        JPanel headPanel = new JPanel();
        scroller.setColumnHeaderView(headPanel);
        initLayout(headPanel);
        
        columnHeaders = buildHeaders(headPanel, false, null);
        buildHistory(info, headPanel);
        
        JViewport headerView = scroller.getColumnHeader();
        headerView.setPreferredSize(new Dimension(300, getLargestHeight(columnHeaders)));
        
        //Add and crop the info panel
        JPanel infoPanel = new JPanel();
        scroller.setViewportView(infoPanel);
        initLayout(infoPanel);
        
        buildHeaders(infoPanel, true, columnHeaders);
        buildHistory(info, infoPanel);
        
        //Add the bottom filler panel
        JPanel filler = new JPanel();
        gridbag.setConstraints(filler, fillerConstraints);
        infoPanel.add(filler);
        
        scroller.getVerticalScrollBar().setMaximum(infoPanel.getPreferredSize().height);
        scroller.getVerticalScrollBar().setValue(infoPanel.getPreferredSize().height);
        
        FullPanel.add(scroller);
        
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
        //String[] headers = {"Period", "Security", "Final Holdings", "Security Payoff", "Total Payoff", "Cumulative Payoff"};
        String[] headers = {"Period", "Security", "Final Holdings", "Cumulative Earnings"};
       
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
    
    private int buildHistory(EarningsInfo info, JPanel panel) {
        
        int numRows = info.numRows();
        int firstRow = 0;
        
        for (int i=firstRow; i<numRows; i++) {
            Color backColor;
            if (i % 2 == 0)
                backColor = darkColor;
            else
                backColor = lightColor;
            
            EarningsRow row = info.getRow(i);
            NumberFormat formatter = new DecimalFormat("##.##");
            
            String[] values = new String[4];
            
            if (row.getSecurity() != null) {
                values[0] = String.valueOf(row.getPeriod()+1);
                values[1] = row.getSecurity();
                values[2] = formatter.format(row.getHoldings()) + "  (+" + row.getNumPurchases() + ", -" + row.getNumSales() + ")";
                values[3] = "";
            }
            else {
                values[0] = "";
                values[1] = "";
                values[2] = "";
                values[3] = formatter.format(row.getCumPayoff());
            }
            
            for (int k=0; k<values.length; k++) {
                JPanel columnPanel = new JPanel();
                JLabel columnLabel = new JLabel();
                
                String data = values[k];
                if (data == null)
                    data = "";
                
                columnLabel.setText(data);
                columnLabel.setForeground(Color.black);
                columnLabel.setHorizontalAlignment(SwingConstants.CENTER);
                
                columnPanel.add(columnLabel);
                columnPanel.setBackground(backColor);
                
                if (k == (values.length - 1))
                    gridbag.setConstraints(columnPanel, endRowConstraints);
                else
                    gridbag.setConstraints(columnPanel, normalConstraints);
                
                panel.add(columnPanel);
            }
        }
        
        return numRows;
    }
    
    public static void main(String args[]) {
        EarningsInfo info = new EarningsInfo();
        
        EarningsRow r1 = new EarningsRow();
        r1.setPeriod(1);
        r1.setSecurity("Stock");
        r1.setHoldings(5);
        r1.setCumPayoff(55);
        info.addRow(r1);
        
        EarningsRow r2 = new EarningsRow();
        r2.setPeriod(1);
        r2.setSecurity("Stock");
        r2.setHoldings(5);
        r2.setCumPayoff(100);
        info.addRow(r2);
        
        EarningsPanel panel = new EarningsPanel(info);
        
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
    
    /** The collapsed match panel (akin to the CardPanel but for collapsed match form) */
    private JPanel FullPanel;
    
    /** The 'Practice Match' string */
    private String practiceMatchStr;
    
    /** The 'Normal Match' string */
    private String normalMatchStr;
    
    /** Various layout information */
    private GridBagConstraints normalConstraints, endRowConstraints, fillerConstraints;
    private GridBagLayout gridbag;
    
    private Color darkColor;
    private Color lightColor;
}
