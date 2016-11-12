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
 * LoggingMonitor.java
 *
 * Created on September 30, 2004, 6:26 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.interfaces;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import org.apache.log4j.*;

/**
 *
 * @author  Administrator
 */
public class LoggingMonitor extends JPanel {
    
    /** Creates a new instance of LoggingMonitor */
    public LoggingMonitor() {
        debugMessages = new StringBuffer();
        infoMessages = new StringBuffer();
        warnMessages = new StringBuffer();
        loggingLevel = Priority.INFO_INT;
        
        displayLocationInfo = false;
        displayClientLog = true;
        displayNetworkLog = true;
        
        LogInfoPanel = new javax.swing.JPanel();
        LogDisablePanel = new javax.swing.JPanel();
        LogResolutionPanel = new javax.swing.JPanel();
        LogClearPanel = new javax.swing.JPanel();
        DebugButton = new javax.swing.JButton();
        InfoButton = new javax.swing.JButton();
        WarnButton = new javax.swing.JButton();
        LogScroller = new javax.swing.JScrollPane();
        LogText = new javax.swing.JTextArea();
        DisableAutoScrolling = new javax.swing.JToggleButton();
        ClearLogsButton = new javax.swing.JButton();
        
        initComponents();
    }
    
    /** Initialize the swing components in the Logging Monitor */
    private void initComponents() {
        setBorder(BorderFactory.createTitledBorder("Logging Monitor"));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        
        LogInfoPanel.setLayout(new GridLayout(1, 3));
        
        DisableAutoScrolling.setText("Disable Autoscrolling");
        LogDisablePanel.add(DisableAutoScrolling);
        LogInfoPanel.add(LogDisablePanel);
        
        LogResolutionPanel.setLayout(new FlowLayout(FlowLayout.CENTER,1,5));
        
        DebugButton.setText("Debug");
        DebugButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DebugButtonAction(evt);
            }
        });
        LogResolutionPanel.add(DebugButton);
        
        //Initialize the buttons section
        InfoButton.setText("Info");
        InfoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                InfoButtonAction(evt);
            }
        });
        InfoButton.setForeground(Color.red);
        LogResolutionPanel.add(InfoButton);
        
        WarnButton.setText("Warn");
        WarnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                WarnButtonAction(evt);
            }
        });
        LogResolutionPanel.add(WarnButton);
        
        LogInfoPanel.add(LogResolutionPanel);
        
        ClearLogsButton.setText("Clear Logs");
        ClearLogsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ClearLogsButtonAction(evt);
            }
        });
        LogClearPanel.add(ClearLogsButton);
        LogInfoPanel.add(LogClearPanel);
        
        add(LogInfoPanel);
        
        LogScroller.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        LogScroller.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        LogScroller.setPreferredSize(new Dimension(1000,500));
        LogText.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        
        LogScroller.setViewportView(LogText);
        
        add(LogScroller);
    }
    
    /** Makes LogText display ALL debugging messages */
    private void DebugButtonAction(ActionEvent evt) {
        LogText = new JTextArea();
        LogText.append(debugMessages.toString());
        LogScroller.setViewportView(LogText);
        loggingLevel = Priority.DEBUG_INT;
        
        DebugButton.setForeground(Color.red);
        InfoButton.setForeground(Color.black);
        WarnButton.setForeground(Color.black);
    }
    
    /** Makes LogText display medium priority debug messages */
    private void InfoButtonAction(ActionEvent evt) {
        LogText = new JTextArea();
        LogText.append(infoMessages.toString());
        LogScroller.setViewportView(LogText);
        loggingLevel = Priority.INFO_INT;
        
        DebugButton.setForeground(Color.black);
        InfoButton.setForeground(Color.red);
        WarnButton.setForeground(Color.black);
        
    }
    
    /** Makes LogText only display critical debug messages */
    private void WarnButtonAction(ActionEvent evt) {
        LogText = new JTextArea();
        LogText.append(warnMessages.toString());
        LogScroller.setViewportView(LogText);
        loggingLevel = Priority.WARN_INT;
        
        DebugButton.setForeground(Color.black);
        InfoButton.setForeground(Color.black);
        WarnButton.setForeground(Color.red);
    }
    
    private void ClearLogsButtonAction(ActionEvent evt) {
        debugMessages = new StringBuffer();
        infoMessages = new StringBuffer();
        warnMessages = new StringBuffer();
        LogText = new JTextArea();
        LogScroller.setViewportView(LogText);
    }
    
    /** This method adds a Log message to the logging text pane, and autoscrolls
     *  if autoscrolling is active.  If we're filtering out network messages, then
     *  don't display the message if it's a network message.  The same goes for
     *  client messages.  Only filter messages if we're showing location info */
    public void addLogMessage(String logMsg, int p, String classInfo) {
        final String fullMessage = logMsg + System.getProperty("line.separator");
        final int pr = p;
        
        if (classInfo != null && classInfo.length() > 1 && displayLocationInfo) {
            StringTokenizer classTokenizer = new StringTokenizer(classInfo,".");
            boolean networkMessage = false;
            boolean clientMessage = false;
            while (classTokenizer.hasMoreTokens()) {
                String className = classTokenizer.nextToken();
                if (className.equals("network"))
                    networkMessage = true;
                if (className.equals("client"))
                    clientMessage = true;
            }
            
            if (networkMessage && !displayNetworkLog)
                return;
            
            if (clientMessage && !displayClientLog)
                return;
        }
        
        if (p == Priority.DEBUG_INT) {
            debugMessages.append(fullMessage);
        }
        if (p == Priority.INFO_INT) {
            debugMessages.append(fullMessage);
            infoMessages.append(fullMessage);
            
        }
        if (p > Priority.INFO_INT) {
            debugMessages.append(fullMessage);
            infoMessages.append(fullMessage);
            warnMessages.append(fullMessage);
        }
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                final int caretPosition = LogText.getCaretPosition();
                
                if (pr >= loggingLevel)
                    LogText.append(fullMessage);
                if (!DisableAutoScrolling.isSelected())
                    LogText.setCaretPosition(LogText.getText().length());
                else
                    LogText.setCaretPosition(caretPosition);
            }
        };
        
        SwingUtilities.invokeLater(doUpdate);
    }
    
    public boolean isDisplayLocationInfo() {
        return displayLocationInfo;
    }
    
    public void setDisplayLocationInfo(boolean displayLocationInfo) {
        this.displayLocationInfo = displayLocationInfo;
    }
    
    public boolean isDisplayNetworkLog() {
        return displayNetworkLog;
    }
    
    public void setDisplayNetworkLog(boolean displayNetworkLog) {
        this.displayNetworkLog = displayNetworkLog;
    }
    
    public boolean isDisplayClientLog() {
        return displayClientLog;
    }
    
    public void setDisplayClientLog(boolean displayClientLog) {
        this.displayClientLog = displayClientLog;
    }
    
    private javax.swing.JPanel LogInfoPanel;
    private javax.swing.JPanel LogDisablePanel;
    private javax.swing.JPanel LogResolutionPanel;
    private javax.swing.JPanel LogClearPanel;
    private javax.swing.JButton DebugButton;
    private javax.swing.JButton InfoButton;
    private javax.swing.JButton WarnButton;
    private javax.swing.JScrollPane LogScroller;
    private javax.swing.JTextArea LogText;
    private javax.swing.JToggleButton DisableAutoScrolling;
    private javax.swing.JButton ClearLogsButton;
    
    /* All the debug, info, and warn messages */
    private StringBuffer debugMessages;
    
    /* All the info and warn messages */
    private StringBuffer infoMessages;
    
    /* All the warn messages */
    private StringBuffer warnMessages;
    
    /* The current logging priority level (debug, info, or warn) */
    private int loggingLevel;
    
    /** If true the monitor will record information from the clients (only when clients and server are run off same JVM) */
    private boolean displayClientLog;
    
    /** If true the monitor will record information received from the network classes */
    private boolean displayNetworkLog;
    
    /** If true the monitor will record the class-location information in each message (10x slowdown) */
    private boolean displayLocationInfo;
}
