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
 * SessionSelectorDialog.java
 *
 * Created on February 7, 2005, 9:47 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.interfaces;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;

/**
 *
 * @author  Raj Advani
 */
public class SessionSelectorDialog extends JDialog {
    
    /** Creates a new instance of SessionSelectorDialog */
    public SessionSelectorDialog(JFrame parent, SessionIdentifier[] identifiers) {
        super(parent, true);
        
        this.identifiers = identifiers;
        
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel();
        titleLabel.setText("JMarkets Session Selector");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 15));
        titlePanel.add(titleLabel);
        
        getContentPane().add(titlePanel);
        
        SelectPanel selectPanel = new SelectPanel(identifiers);
        
        JScrollPane selectScroller = new JScrollPane(selectPanel);
        selectScroller.setPreferredSize(new Dimension(600, 500));
        selectScroller.setMaximumSize(new Dimension(600, 500));
        
        getContentPane().add(selectScroller);
        
        pack();
        centerOnScreen();
    }
    
    public static void main(String[] args) {
        JFrame testFrame = new JFrame();
        SessionIdentifier[] identifiers = new SessionIdentifier[30];
        
        /*
        identifiers[0] = new SessionIdentifier(42, "Raj's Session", "Active");
        identifiers[1] = new SessionIdentifier(52, "Walter's Session", "Inactive");
        identifiers[2] = new SessionIdentifier(104, "Peter's Session", "Active");
        identifiers[3] = new SessionIdentifier(43, "John's Session", "Inactive");
        identifiers[4] = new SessionIdentifier(66, "Athena's Session", "Active");
        identifiers[5] = new SessionIdentifier(42, "Raj's Session", "Active");
        identifiers[6] = new SessionIdentifier(52, "Walter's Session", "Inactive");
        identifiers[7] = new SessionIdentifier(104, "Peter's Session", "Active");
        identifiers[8] = new SessionIdentifier(43, "John's Session", "Inactive");
        identifiers[9] = new SessionIdentifier(66, "Athena's Session", "Active");
        identifiers[10] = new SessionIdentifier(42, "Raj's Session", "Active");
        identifiers[11] = new SessionIdentifier(52, "Walter's Session", "Inactive");
        identifiers[12] = new SessionIdentifier(104, "Peter's Session", "Active");
        identifiers[13] = new SessionIdentifier(43, "John's Session", "Inactive");
        identifiers[14] = new SessionIdentifier(66, "Athena's Session", "Active");
        identifiers[15] = new SessionIdentifier(42, "Raj's Session", "Active");
        identifiers[16] = new SessionIdentifier(52, "Walter's Session", "Inactive");
        identifiers[17] = new SessionIdentifier(104, "Peter's Session", "Active");
        identifiers[18] = new SessionIdentifier(43, "John's Session", "Inactive");
        identifiers[19] = new SessionIdentifier(66, "Athena's Session", "Active");
        identifiers[20] = new SessionIdentifier(42, "Raj's Session", "Active");
        identifiers[21] = new SessionIdentifier(52, "Walter's Session", "Inactive");
        identifiers[22] = new SessionIdentifier(104, "Peter's Session", "Active");
        identifiers[23] = new SessionIdentifier(43, "John's Session", "Inactive");
        identifiers[24] = new SessionIdentifier(66, "Athena's Session", "Active");
        identifiers[25] = new SessionIdentifier(42, "Raj's Session", "Active");
        identifiers[26] = new SessionIdentifier(52, "Walter's Session", "Inactive");
        identifiers[27] = new SessionIdentifier(104, "Peter's Session", "Active");
        identifiers[28] = new SessionIdentifier(43, "John's Session", "Inactive");
        identifiers[29] = new SessionIdentifier(66, "Athena's Session", "Active");
        */
        SessionSelectorDialog diag = new SessionSelectorDialog(testFrame, identifiers);
        diag.setVisible(true);
    }
    
    public void processJoin(int session) {
        joinSession(identifiers[session].getSessionId());
        joinSession(identifiers[session].getSessionId(), identifiers[session].getNumClients());
    }
    
    /** The class that uses this dialog should override this method to do the proper
     *  thing when a session is chosen */
    public void joinSession(int sessionId) {
        
    }
    
    /** Alternatively this may be overriden if the using class wants to also receive
     *  the number of clients in the session */
    public void joinSession(int sessionId, int numClients) {
        
    }
     
    /* Center on the screen */
    public void centerOnScreen() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension window = this.getSize();
        int iCenterX = screen.width / 2;
        int iCenterY = screen.height / 2;
        setLocation(iCenterX - (window.width / 2),
        iCenterY - (window.height / 2));
    }
    
    private SessionIdentifier[] identifiers;
    
    /** This is a simple panel that displays a set of session identifier objects
     *  and lets the user select one of them. When the user drags the mouse over
     *  a session, that session is highlighted. When the user selects a panel, then
     *  the joinSession method is called in the main dialog */
    class SelectPanel extends JPanel {
        
        public SelectPanel(SessionIdentifier[] identifiers) {
            final int numSessions = identifiers.length;
            final SessionIdentifier[] ids = identifiers;
            
            int numDisplayed = 10;
            int fillers = numDisplayed - numSessions;
            
            int numRows = Math.max(numSessions + 1, numDisplayed + 1);
            int numCols = 3;
            
            setLayout(new GridLayout(numRows, numCols));
            
            JPanel idTitlePanel = new JPanel();
            JLabel idTitleLabel = new JLabel("ID Number");
            idTitleLabel.setFont(new Font("Verdana", Font.BOLD, 13));
            idTitleLabel.setForeground(Color.black);
            idTitlePanel.add(idTitleLabel);
            
            add(idTitlePanel);
            
            JPanel nameTitlePanel = new JPanel();
            JLabel nameTitleLabel = new JLabel("Name");
            nameTitleLabel.setFont(new Font("Verdana", Font.BOLD, 13));
            nameTitleLabel.setForeground(Color.black);
            nameTitlePanel.add(nameTitleLabel);
            
            add(nameTitlePanel);
            
            JPanel statusTitlePanel = new JPanel();
            JLabel statusTitleLabel = new JLabel("Status");
            statusTitleLabel.setFont(new Font("Verdana", Font.BOLD, 13));
            statusTitleLabel.setForeground(Color.black);
            statusTitlePanel.add(statusTitleLabel);
            
            add(statusTitlePanel);
            
            defaultColor = statusTitlePanel.getBackground();
            
            idPanels = new JPanel[identifiers.length];
            statusPanels = new JPanel[identifiers.length];
            namePanels = new JPanel[identifiers.length];
            
            for (int i=0; i<numRows - 1; i++) {
                if (i < identifiers.length) {
                    idPanels[i] = new JPanel();
                    JLabel idLabel = new JLabel(identifiers[i].getSessionId() + "");
                    idLabel.setFont(new Font("Verdana", Font.BOLD, 13));
                    //idLabel.setForeground(Color.black);
                    idPanels[i].add(idLabel);
                    
                    add(idPanels[i]);
                    
                    namePanels[i] = new JPanel();
                    JLabel nameLabel = new JLabel(identifiers[i].getSessionName());
                    nameLabel.setFont(new Font("Verdana", Font.BOLD, 13));
                    //nameLabel.setForeground(Color.black);
                    namePanels[i].add(nameLabel);
                    
                    add(namePanels[i]);
                    
                    statusPanels[i] = new JPanel();
                    JLabel statusLabel = new JLabel(identifiers[i].getStatus());
                    statusLabel.setFont(new Font("Verdana", Font.BOLD, 13));
                    //statusLabel.setForeground(Color.black);
                    statusPanels[i].add(statusLabel);
                    
                    add(statusPanels[i]);
                }
                else {
                    add(new JPanel());
                    add(new JPanel());
                    add(new JPanel());
                }
            }
            
            final JPanel sp = this;
            MouseMotionAdapter motionAdapter = new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent evt) {
                    Point p = evt.getPoint();
                    
                    for (int i=0; i<numSessions; i++) {
                        if (statusPanels[i].contains(SwingUtilities.convertPoint(sp, p, statusPanels[i]))
                        || namePanels[i].contains(SwingUtilities.convertPoint(sp, p, namePanels[i]))
                        || idPanels[i].contains(SwingUtilities.convertPoint(sp, p, idPanels[i]))) {
                            statusPanels[i].setBackground(Color.lightGray);
                            namePanels[i].setBackground(Color.lightGray);
                            idPanels[i].setBackground(Color.lightGray);
                        }
                        else {
                            statusPanels[i].setBackground(defaultColor);
                            namePanels[i].setBackground(defaultColor);
                            idPanels[i].setBackground(defaultColor);
                        }
                    }
                }
            };
            
            MouseAdapter mouseAdapter = new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    Point p = evt.getPoint();
                    
                    for (int i=0; i<numSessions; i++) {
                        if (statusPanels[i].contains(SwingUtilities.convertPoint(sp, p, statusPanels[i]))
                        || namePanels[i].contains(SwingUtilities.convertPoint(sp, p, namePanels[i]))
                        || idPanels[i].contains(SwingUtilities.convertPoint(sp, p, idPanels[i]))) {
                            
                            int action = JOptionPane.showConfirmDialog(sp, "Join Session '" + ids[i].getSessionName() + "'?", "Confirm Join", JOptionPane.YES_NO_OPTION);
                            if (action == JOptionPane.YES_OPTION)
                                processJoin(i);
                        }
                    }
                }
            };
            
            addMouseListener(mouseAdapter);
            addMouseMotionListener(motionAdapter);
        }
        
        private Color defaultColor;
        private JPanel[] idPanels, statusPanels, namePanels;
    }
    
}
