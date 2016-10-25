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
 * ExpMonitor.java
 *
 * Created on October 8, 2004, 7:33 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.network;

import java.util.Vector;
import java.rmi.server.*;
import java.rmi.*;
import java.net.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.interfaces.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.network.GUILink;

/**
 *
 * This class implements the Remote class RMIMonitor. It receives messages from the RMIMonitorTransmitter
 * and converts them into direct calls on the MonitorUI. See the RMIMonitorTransmitter comments for more
 * information on the server-monitor communication system
 *
 * @author  Raj Advani
 */
public class RMIMonitorReceiver implements RMIMonitor {
    
    public RMIMonitorReceiver(MonitorUI ui, String host, int port, int sessionId) {
        this.ui = ui;
        exportRMI(host, port, sessionId);
    }
    
    /** Export this object so that it is able to receive incoming calls from
     *  the RMIMonitorTransmitter. Then find the JMarkets server and register this
     *  object there */
    private void exportRMI(String host, int port, int sessionId) {
        try {
            System.out.println("Exporting the RMIMonitorReceiver");
            UnicastRemoteObject.exportObject(this);
            
            System.out.println("Constructing RMI server URL");
            GUILink link = (GUILink) SimpleObjectRegistry.findObject("link", host, port, port + 20);
            
            System.out.println("Registering RMIMonitorReceiver with server registrar");
            link.registerExpMonitor(this, sessionId);
            
            System.out.println("Successfully registered RMIMonitorReceiver with server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void addLogMessage(String logMsg, int p, String classInfo) {
        ui.addLogMessage(logMsg, p, classInfo);
    }
    
    public void insertPriceChart(Vector priceChart) {
        ui.insertPriceChart(priceChart);
    }
    
    public void connectClient(int client, String name) {
        ui.connectClient(client, name);
    }
    
    public void updatePriceChart(String security, float time, float price) {
        ui.updatePriceChart(security, time, price);
    }
    
    public void setTimeLeft(int time) {
        ui.setTimeLeft(time);
    }
    
    public void updateExpStatus(String str) {
        ui.updateExpStatus(str);
    }
    
    public void updateMetrics(int iterations, int num, float time) {
        ui.updateMetrics(iterations, num, time);
    }
    
    public void setAllConnected(boolean allConnected) {
        ui.setAllConnected(allConnected);
    }
    
    public void setStartExpButtonEnabled(boolean enabled) {
        ui.setStartExpButtonEnabled(enabled);
    }
    
    public void setStopExpButtonEnabled(boolean enabled) {
        ui.setStopExpButtonEnabled(enabled);
    }
    
    public void updateNumOffers(int client, int offers) {
        ui.updateNumOffers(client, offers);
    }
    
    public void constructInfoPeriodPanel() {
        ui.constructInfoPeriodPanel();
    }
    
    public void setOfferBacklog(int offers, boolean rejecting) {
        ui.setOfferBacklog(offers, rejecting);
    }
    
    public void setStartPeriodButtonEnabled(boolean enabled) {
        ui.setStartPeriodButtonEnabled(enabled);
    }
    
    public void setConnected(int client, boolean connected) {
        ui.setConnected(client, connected);
    }
    
    private MonitorUI ui;
}
