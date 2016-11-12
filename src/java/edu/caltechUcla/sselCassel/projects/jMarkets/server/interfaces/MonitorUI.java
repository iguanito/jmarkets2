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
 * MonitorUI.java
 *
 * Created on September 30, 2004, 6:14 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.interfaces;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.caltechUcla.sselCassel.projects.jMarkets.server.control.MonitorControl;
import java.util.Vector;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.interfaces.*;

/**
 *
 * @author  Raj Advani, Walter Yuan
 */
public class MonitorUI extends JFrame {
    
    public MonitorUI() {
    }
    
    /** Initialization the GUI interface */
    public void init(MonitorControl control) {
        this.control = control;
        setTitle("JMarkets Admin Interface");
        
        logMonitor = new LoggingMonitor();
        logMonitor.setPreferredSize(relativeSize(0.6f, 0.4f));
        
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        
        constructClientsPanel(10);
        constructButtonPanel();
        constructChartPanel();
        constructInfoPanel();
        constructTitlePanel();
        constructMetricsPanel();
        
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        
        getContentPane().add(titlePanel);
        getContentPane().add(infoPanel);
        
        topPanel.add(chartPanel);
        topPanel.setPreferredSize(relativeSize(0.85f, 0.42f));
        
        getContentPane().add(topPanel);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(logMonitor);
        
        JPanel bottomRightPanel = new JPanel();
        bottomRightPanel.setLayout(new BoxLayout(bottomRightPanel, BoxLayout.Y_AXIS));
        bottomRightPanel.add(metricsPanel);
        bottomRightPanel.add(buttonPanel);
        
        bottomPanel.add(bottomRightPanel);
        bottomPanel.setPreferredSize(relativeSize(0.85f, 0.4f));
        
        getContentPane().add(bottomPanel);
        
        pack();
        centerOnScreen();
        setVisible(true);
    }
    
    
    
    /** Send the ServletReceiver a TERMINATE_SESSION_REQUEST. Return true if the operation
     *  was successful */
    public void stopExperiment() {
        control.stopExperiment();
    }
    
    /** Send the ServletReceiver a START_GAME_REQUEST. Return true if the operation was
     *  successful */
    public void startExperiment() {
        Runnable starter = new Runnable() {
            public void run() {
                manualAdvance = control.isManualControl();
                setStartPeriodButtonEnabled(manualAdvance);
                setSwitchStatus();
                
                control.startExperiment();
            }
        };
        
        Thread stThread = new Thread(starter);
        stThread.start();
    }
    
    public void stopPeriod() {
        control.stopPeriod();
    }
    
    public void startPeriod() {
        control.startPeriod();
    }
    
    protected Dimension relativeSize(float widthMultiple, float heightMultiple) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screen.width * widthMultiple);
        int height = (int) (screen.height * heightMultiple);
        Dimension dim = new Dimension(width, height);
        return dim;
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
    
    /** Construct a panel that displays a list of clients connected and client slots remaining to be filled.
     *  Add this panel to the main frame */
    public void constructClientsPanel(int numClients) {
        cnames = new Vector();
        cstatus = new Vector();
        ctrans = new Vector();
        
        int numRows = numClients;
        if (numRows < 7)
            numRows = 7;
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(numRows + 1, 3));
        
        clientScroller = new JScrollPane(listPanel);
        clientScroller.setPreferredSize(relativeSize(0.4f, 0.35f));
        clientScroller.setBorder(BorderFactory.createTitledBorder("Client Monitor"));
        
        JPanel nameTitlePanel = new JPanel();
        JLabel nameTitleLabel = new JLabel();
        nameTitleLabel.setText("Client");
        nameTitleLabel.setFont(new Font("Arial Black", 0, 16));
        nameTitleLabel.setForeground(Color.black);
        nameTitlePanel.add(nameTitleLabel);
        
        listPanel.add(nameTitlePanel);
        
        JPanel statusTitlePanel = new JPanel();
        JLabel statusTitleLabel = new JLabel();
        statusTitleLabel.setText("Status");
        statusTitleLabel.setFont(new Font("Arial Black", 0, 16));
        statusTitleLabel.setForeground(Color.black);
        statusTitlePanel.add(statusTitleLabel);
        
        listPanel.add(statusTitlePanel);
        
        JPanel numTransPanel = new JPanel();
        JLabel numTransLabel = new JLabel();
        numTransLabel.setText("Num Offers");
        numTransLabel.setFont(new Font("Arial Black", 0, 16));
        numTransLabel.setForeground(Color.black);
        numTransPanel.add(numTransLabel);
        
        listPanel.add(numTransPanel);
        
        for (int i=0; i<numRows; i++) {
            JPanel namePanel = new JPanel();
            JLabel nameLabel = new JLabel("<html><font color=#003366>Client " + i + "</font></html>");
            nameLabel.setFont(new Font("Verdana", Font.BOLD, 16));
            if (i < numClients) {
                namePanel.add(nameLabel);
                cnames.add(nameLabel);
            }
            
            else
                namePanel.add(new JLabel());
            
            listPanel.add(namePanel);
            
            JPanel statusPanel = new JPanel();
            JLabel statusLabel = new JLabel("Disconnected");
            statusLabel.setFont(new Font("Verdana", Font.BOLD, 16));
            statusLabel.setForeground(new Color(90, 25, 25));
            if (i < numClients) {
                statusPanel.add(statusLabel);
                cstatus.add(statusLabel);
            }
            
            else
                statusPanel.add(new JLabel());
            
            listPanel.add(statusPanel);
            
            JPanel transPanel = new JPanel();
            JLabel transLabel = new JLabel("N/A");
            transLabel.setFont(new Font("Verdana", Font.BOLD, 16));
            transLabel.setForeground(Color.black);
            if (i < numClients) {
                transPanel.add(transLabel);
                ctrans.add(transLabel);
            } else
                transPanel.add(new JLabel());
            
            listPanel.add(transPanel);
        }
        
        if (topPanel.getComponentCount() != 0)
            topPanel.remove(0);
        
        topPanel.add(clientScroller, 0);
        topPanel.setPreferredSize(relativeSize(0.85f, 0.42f));
        
        pack();
        centerOnScreen();
    }
    
    /** Set the given client slot to conencted with the given client name */
    public void connectClient(int client, String name) {
        final String n = name;
        final int c = client;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                JLabel nameLabel = (JLabel) cnames.get(c);
                nameLabel.setText(n);
                nameLabel.setForeground(Color.blue);
                
                JLabel statusLabel = (JLabel) cstatus.get(c);
                statusLabel.setText("Connected");
                statusLabel.setForeground(new Color(25, 90, 25));
                
                pack();
                repaint();
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Set the given slot to connected or disconnected (used for re-authentication, disconnect
     *  detection, not for authenticating new clients */
    public void setConnected(int client, boolean connected) {
        final int c = client;
        final boolean conn = connected;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                JLabel statusLabel = (JLabel) cstatus.get(c);
                if (conn) {
                    statusLabel.setText("Connected");
                    statusLabel.setForeground(new Color(25, 90, 25));
                } else {
                    statusLabel.setText("Disconnected");
                    statusLabel.setForeground(new Color(90, 25, 25));
                }
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Construct a panel that shows game metrics */
    private void constructMetricsPanel() {
        int FONT_SIZE = 12;
        
        metricsPanel = new JPanel();
        metricsPanel.setLayout(new GridLayout(6, 3));
        metricsPanel.setBorder(BorderFactory.createTitledBorder("Metrics"));
        
        JLabel nameHeader = new JLabel("Type");
        nameHeader.setFont(new Font("Times New Roman", Font.BOLD, FONT_SIZE));
        nameHeader.setHorizontalAlignment(SwingConstants.CENTER);
        nameHeader.setForeground(Color.black);
        
        metricsPanel.add(nameHeader);
        
        JLabel numHeader = new JLabel("Number");
        numHeader.setFont(new Font("Times New Roman", Font.BOLD, FONT_SIZE));
        numHeader.setHorizontalAlignment(SwingConstants.CENTER);
        numHeader.setForeground(Color.black);
        
        metricsPanel.add(numHeader);
        
        JLabel timeHeader = new JLabel("Avg Time");
        timeHeader.setFont(new Font("Times New Roman", Font.BOLD, FONT_SIZE));
        timeHeader.setHorizontalAlignment(SwingConstants.CENTER);
        timeHeader.setForeground(Color.black);
        
        metricsPanel.add(timeHeader);
        
        metricsNumLabels = new JLabel[5];
        metricsTimeLabels = new JLabel[5];
        
        for (int i=0; i<5; i++) {
            JLabel name = new JLabel("Market Orders");
            if (i == 1)
                name.setText("Two Order Trades");
            if (i == 2)
                name.setText("Three Order Trades");
            if (i == 3)
                name.setText("Four Order Trades");
            if (i == 4)
                name.setText("Five Order Trades");
            
            name.setFont(new Font("Times New Roman", Font.PLAIN, FONT_SIZE));
            name.setHorizontalAlignment(SwingConstants.CENTER);
            name.setForeground(Color.black);
            
            metricsPanel.add(name);
            
            metricsNumLabels[i] = new JLabel("0");
            metricsNumLabels[i].setFont(new Font("Times New Roman", Font.PLAIN, FONT_SIZE));
            metricsNumLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            metricsNumLabels[i].setForeground(Color.black);
            
            metricsPanel.add(metricsNumLabels[i]);
            
            metricsTimeLabels[i] = new JLabel("N/A");
            metricsTimeLabels[i].setFont(new Font("Times New Roman", Font.PLAIN, FONT_SIZE));
            metricsTimeLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            metricsTimeLabels[i].setForeground(Color.black);
            
            metricsPanel.add(metricsTimeLabels[i]);
        }
        
        metricsPanel.setPreferredSize(relativeSize(0.25f, 0.25f));
    }
    
    /** Construct a panel that contains the start and stop experiment buttons */
    private void constructButtonPanel() {
        //boolean manualAdvance = true;
        
        buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Admin Actions"));
        buttonPanel.setLayout(new GridLayout(2, 2, 20, 15));
        
        JPanel startExpPanel = new JPanel();
        startExpButton = new JButton("Start Experiment");
        startExpButton.setEnabled(false);
        startExpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (allConnected || JOptionPane.showConfirmDialog(null, "<html>Start the game even though all clients are not yet connected?</html>", "Premature Start", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                    startExperiment();
            }
        });
        startExpPanel.add(startExpButton);
        
        JPanel startPeriodPanel = new JPanel();
        startPeriodButton = new JButton("Start Period");
        startPeriodButton.setEnabled(false);
        startPeriodPanel.add(startPeriodButton);
        startPeriodButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (JOptionPane.showConfirmDialog(null, "<html>Start this period?</html>", "Confirm Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                    startPeriod();
            }
        });
        
        JPanel switchButtonPanel = new JPanel();
        switchButton = new JButton(autoSwitchString);
        switchButton.setEnabled(true);
        switchButtonPanel.add(switchButton);
        switchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                manualAdvance = !manualAdvance;
                setSwitchStatus();
            }
        });
        
        JPanel stopExpPanel = new JPanel();
        stopExpButton = new JButton("Stop Experiment");
        stopExpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (JOptionPane.showConfirmDialog(null, "<html>Terminate the current experiment?<br>(This will exit this interface)</html>", "Confirm Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                    stopExperiment();
            }
        });
        stopExpButton.setEnabled(false);
        stopExpPanel.add(stopExpButton);
        
        buttonPanel.add(startExpPanel);
        buttonPanel.add(startPeriodPanel);
        buttonPanel.add(stopExpPanel);
        buttonPanel.add(switchButtonPanel);
        
        buttonPanel.setPreferredSize(relativeSize(0.25f, 0.15f));
    }
    
    /** Inserts the given PriceChart into the display */
    public void insertPriceChart(Vector priceChart) {
        try {
            chart = new PriceChart();
            chart.setSecurities(priceChart);
            java.awt.EventQueue.invokeLater(new Runnable() {

                public void run() {
                    chartPanel.removeAll();
                    chartPanel.add(displayChartButton);
                    displayChartButton.setEnabled(true);
                    chartPanel.repaint();
                }
            });
            /*
            Runnable doUpdate = new Runnable() {
                public void run() {
                    chartPanel.removeAll();
                    chartPanel.add(displayChartButton);
                    displayChartButton.setEnabled(true);
                }
            };
            SwingUtilities.invokeAndWait(doUpdate);
             * */
        }catch(Exception e) {
            e.printStackTrace();
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
    
    /** Update the metrics table with the given information */
    public void updateMetrics(int iterations, int num, float time) {
        final int n = num;
        final float t = time / 1000;
        final int i= iterations;
        
        java.text.NumberFormat formatter = new java.text.DecimalFormat("#######.###");
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                metricsNumLabels[i].setText("" + n);
                metricsTimeLabels[i].setText("" + t + " sec");
                
                if (t > 1)
                    metricsTimeLabels[i].setForeground(Color.red);
                else
                    metricsTimeLabels[i].setForeground(Color.black);
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Update the number of offers made by the given client */
    public void updateNumOffers(int client, int offers) {
        final JLabel transLabel = (JLabel) ctrans.get(client);
        final int o = offers;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                transLabel.setText("" + o);
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Update the time label with the given amount of time */
    public void setTimeLeft(int time) {
        final int t = time;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                timeLabel.setText("<html>Period Time Remaining: <font color=#993333>" + t + "</font></html>");
            }
        };
        
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Update the backlog label with the number of offers currently waiting to be processed on the server */
    public void setOfferBacklog(int offers, boolean rejecting) {
        final int o = offers;
        final boolean r = rejecting;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                if (!r)
                    backlogLabel.setText("<html>Offer Backlog: " + o + "</html>");
                else
                    backlogLabel.setText("<html>Offer Backlog: <font color=#993333>" + o + "</font></html>");
            }
        };
        
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Construct a panel that contains the server-side price chart */
    private void constructChartPanel() {
        chartPanel = new JPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder("Price Chart"));
        chartPanel.setPreferredSize(relativeSize(0.4f, 0.35f));
        
        displayChartButton = new JButton("Click to Display Price Chart");
        displayChartButton.setEnabled(false);
        displayChartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel cPanel = chart.getChartPanel();
                chartPanel.removeAll();
                chartPanel.add(cPanel);
                cPanel.setPreferredSize(relativeSize(0.4f, 0.35f));
                
                pack();
                repaint();
            }
        });
        
        chartPanel.add(displayChartButton);
    }
    
    /** Construct a panel that contains messages indicating the current status of the server */
    private void constructInfoPanel() {
        try {
            Runnable doUpdate = new Runnable() {
                public void run() {
                    infoPanel = new JPanel();
                    infoPanel.setPreferredSize(relativeSize(0.8f, 0.04f));
                    
                    infoLabel = new JLabel("Waiting for Clients");
                    infoLabel.setForeground(new Color(0, 51, 102));
                    infoLabel.setFont(new Font("Garamond", Font.BOLD, 17));
                    
                    infoPanel.add(infoLabel);
                }
            };
            if (SwingUtilities.isEventDispatchThread())
                doUpdate.run();
            else
                SwingUtilities.invokeAndWait(doUpdate);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Reform the info panel when the game starts so that we can see the order backlog and the
     *  time remaining */
    public void constructInfoPeriodPanel() {
        Runnable doUpdate = new Runnable() {
            public void run() {
                infoPanel.removeAll();
                infoPanel.setLayout(new GridLayout(1, 2));
                
                JPanel timePanel = new JPanel();
                timeLabel = new JLabel();
                timeLabel.setForeground(new Color(0, 51, 102));
                timeLabel.setText("<html>Period Time Remaining: <font color=#993333>N/A</font></html>");
                timePanel.add(timeLabel);
                
                
                infoPanel.add(timePanel);
                
                JPanel backlogPanel = new JPanel();
                backlogLabel = new JLabel();
                backlogLabel.setForeground(new Color(0, 51, 102));
                backlogLabel.setText("<html>Offer Backlog: <font color=#993333>0</font></html>");
                backlogPanel.add(backlogLabel);
                
                infoPanel.add(backlogPanel);
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Updates the info label with the given string */
    public void updateExpStatus(String str) {
        final String status = str;
        Runnable doUpdate = new Runnable() {
            public void run() {
                infoLabel.setText(status);
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    private void setSwitchStatus(){
        final boolean manual = manualAdvance;
        Runnable doUpdate = new Runnable() {
            public void run() {
                if( manual ){
                    switchButton.setText(manualSwitchString);
                    startPeriodButton.setEnabled(true);
                    control.setManualControl(true);
                } else {
                    switchButton.setText(autoSwitchString);
                    startPeriodButton.setEnabled(false);
                    control.setManualControl(false);
                }
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    /** Construct a panel that contains the heading and title */
    private void constructTitlePanel() {
        titlePanel = new JPanel();
        titlePanel.setPreferredSize(relativeSize(0.8f, 0.03f));
        
        JLabel titleLabel = new JLabel("JMarkets Server Interface");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(102, 51, 0));
        
        titlePanel.add(titleLabel);
    }
    
    public void addLogMessage(String logMsg, int p, String classInfo) {
        logMonitor.addLogMessage(logMsg, p, classInfo);
    }
    
    public boolean isDisplayLocationInfo() {
        return logMonitor.isDisplayLocationInfo();
    }
    
    public void setAllConnected(boolean allConnected) {
        this.allConnected = allConnected;
    }
    
    public void setStartExpButtonEnabled(boolean enabled) {
        final boolean en = enabled;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                startExpButton.setEnabled(en);
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    public void setStopExpButtonEnabled(boolean enabled) {
        final boolean en = enabled;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                stopExpButton.setEnabled(en);
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    public void setStartPeriodButtonEnabled(boolean enabled) {
        final boolean en = enabled;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                startPeriodButton.setEnabled(en);
            }
        };
        SwingUtilities.invokeLater(doUpdate);
    }
    
    
    public static void main(String [] args){
        MonitorUI ui = new MonitorUI();
        ui.init(null);
    }
    
    private LoggingMonitor logMonitor;
    private JPanel topPanel;
    private JPanel buttonPanel, chartPanel, infoPanel, titlePanel, metricsPanel;
    private JScrollPane clientScroller;
    private JFrame mainFrame;
    
    private JLabel infoLabel, timeLabel, backlogLabel;
    private JButton switchButton, startPeriodButton, stopExpButton, startExpButton;
    private boolean manualAdvance;
    
    private JLabel[] metricsNumLabels;
    private JLabel[] metricsTimeLabels;
    
    /** Contains all the client name and status Vectors so these can be updated */
    private Vector cnames, cstatus, ctrans;
    
    private PriceChart chart;
    private JButton displayChartButton;
    
    private static final String manualSwitchString = "Manual Adv.";
    private static final String autoSwitchString = "Automatic Adv.";
    
    /** True if all maxClients are connected */
    private boolean allConnected;
    
    /** The monitor controller */
    private MonitorControl control;
}
