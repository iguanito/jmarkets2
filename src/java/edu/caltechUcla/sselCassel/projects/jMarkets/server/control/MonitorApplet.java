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
 * MonitorApplet.java
 *
 * Created on August 8, 2005, 6:21 PM
 *
 * This class starts a Monitor UI from an applet context
 *
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import java.net.*;
import java.io.*;
//import org.apache.log4j.*;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.SessionIdentifier;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.interfaces.MonitorUI;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.network.HTTPMonitorTransmitter;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.network.RMIMonitorReceiver;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.network.TCPMonitorReceiver;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.interfaces.SessionSelectorDialog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Raj
 */
public class MonitorApplet extends JApplet implements MonitorControl {
    
    /** Creates a new instance of MonitorApplet */
    public MonitorApplet() {
        getRootPane().putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);
    }
    
    /** Applet method called upon startup */
    public void init() {
        transmitter = new HTTPMonitorTransmitter(getCodeBase());
        
        ui = new MonitorUI();
        ui.init(this);
        
        querySessions();
    }
    
    /** Send the ServletReceiver a TERMINATE_SESSION_REQUEST. Return true if the operation
     *  was successful */
    public void stopExperiment() {
        transmitter.stopExperiment(sessionId);
    }
    
    /** Send the ServletReceiver a START_GAME_REQUEST. Return true if the operation was
     *  successful */
    public void startExperiment() {
        transmitter.startExperiment(sessionId);
    }
    
    public void startPeriod() {
        transmitter.startPeriod(sessionId);
    }
    
    public void stopPeriod() {
        transmitter.stopPeriod(sessionId);
    }
    
    public boolean isManualControl() {
        //log.debug("MonitorApplet is calling transmitter.isManualControl with sessionId="+sessionId);
        System.out.println("MonitorApplet is calling transmitter.isManualControl with sessionId="+sessionId);
        return transmitter.isManualControl(sessionId);
    }
    
    public void setManualControl(boolean mc){
        transmitter.setManualControl(sessionId, mc);
    }
    
    
   /* private void setSessionId(int sessionId){
        this.sessionId = sessionId;
    }*/
    
    /** Send the ServletReceiver a SESSION_QUERY_REQUEST, then allow the administrator
     *  to choose a session to monitor. Once the session is chosen, re-create the clients
     *  panel to reflect the number of the clients in the chosen session. Then finish
     *  initialization by exporting this ExpMonitor via RMI to link up officially with the
     *  server. This operation is run on a separate thread so that the applet can finish
     *  initializing (ending its 'init' method) before the main GUI screen appears. This
     *  way the browser does not pop up and block the main GUI screen (since the browser
     *  gains focus when init is finished). If this controller is set up for fast mode then
     *  just monitor the first session without asking */
    public void querySessions() {
        final MonitorApplet con = this;
        
        Runnable sessionFinder = new Runnable() {
            public void run() {
                SessionIdentifier[] identifiers = transmitter.getActiveSessions();
                
                String fastMode = getParameter("fast");
                if (fastMode != null && fastMode.equalsIgnoreCase("true")) {
                    con.sessionId = identifiers[0].getSessionId();
                    final int numClients = identifiers[0].getNumClients();
                    
                    Runnable doUpdate = new Runnable() {
                        public void run() {
                            ui.constructClientsPanel(numClients);
                        }
                    };
                    SwingUtilities.invokeLater(doUpdate);
                    
                    transmitter.connect(ui, sessionId);
                    return;
                }
                
                sessionSelector = new SessionSelectorDialog(ui, identifiers) {
                    public void joinSession(int sessionId, int numClients) {
                        sessionSelector.setVisible(false);
                        sessionSelector.dispose();
                        //log.debug("MonitorApplet is setting its sessionId to:  "+sessionId);
                        con.sessionId = sessionId;
                        //setSessionId(sessionId);
                        System.out.println("Set monitor to session " + sessionId);
                        ui.constructClientsPanel(numClients);
                        
                        transmitter.connect(ui, sessionId);
                    }
                };
                sessionSelector.setVisible(true);
            }
        };
        
        Thread queryThread = new Thread(sessionFinder);
        queryThread.start();
    }
    
    /** The session ID that this GUI is monitoring */
    private int sessionId;
    
    /** The Monitor UI this applet is controlling */
    private MonitorUI ui;
    
    /** The dialog box used to select a session */
    private SessionSelectorDialog sessionSelector;
    
    /** The transmitter used to send requests to the server */
    private HTTPMonitorTransmitter transmitter;
    
     //private static Log log = LogFactory.getLog(MonitorApplet.class);
}
