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
import java.rmi.*;

/**
 * This class converts UI calls (which come from the MonitorServ) into RMI calls onto the 
 * RMIMonitorReceiver (which is on the admin-client end). This way, RemoteExceptions can be
 * caught here and converted into MonitorDisconnectedExceptions, which are recognized by the
 * MonitorServ and signal the MonitorServ to disocnnect from the problematic monitor. The flow
 * is as follows:
 *
 * MonitorServ --(method call to) --> RMIMonitorTransmitter --(RMI call to) --> RMIMonitorReceiver --(method call to)--> ServerGUI
 *
 * @author  Raj Advani
 */
public class RMIMonitorTransmitter implements MonitorTransmitter {
    
    public RMIMonitorTransmitter(RMIMonitor receiver) {
        this.receiver = receiver;
    }
    
    public void close() {
        //insert RMI closing statements here
    }
    
    public void addLogMessage(String logMsg, int p, String classInfo) throws MonitorDisconnectedException {
        try {
            receiver.addLogMessage(logMsg, p, classInfo);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void insertPriceChart(Vector priceChart) throws MonitorDisconnectedException {
        try {
            receiver.insertPriceChart(priceChart);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void connectClient(int client, String name) throws MonitorDisconnectedException {
        try {
            receiver.connectClient(client, name);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void updatePriceChart(String security, float time, float price) throws MonitorDisconnectedException {
        try {
            receiver.updatePriceChart(security, time, price);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void setTimeLeft(int time) throws MonitorDisconnectedException {
        try {
            receiver.setTimeLeft(time);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void updateExpStatus(String str) throws MonitorDisconnectedException {
        try {
            receiver.updateExpStatus(str);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void updateMetrics(int iterations, int num, float time) throws MonitorDisconnectedException {
        try {
            receiver.updateMetrics(iterations, num, time);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void setAllConnected(boolean allConnected) throws MonitorDisconnectedException {
        try {
            receiver.setAllConnected(allConnected);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void setStartExpButtonEnabled(boolean enabled) throws MonitorDisconnectedException {
        try {
            receiver.setStartExpButtonEnabled(enabled);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void updateNumOffers(int client, int offers) throws MonitorDisconnectedException {
        try {
            receiver.updateNumOffers(client, offers);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void constructInfoPeriodPanel() throws MonitorDisconnectedException {
        try {
            receiver.constructInfoPeriodPanel();
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void setOfferBacklog(int offers, boolean rejecting) throws MonitorDisconnectedException {
        try {
            receiver.setOfferBacklog(offers, rejecting);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void setStopExpButtonEnabled(boolean enabled) throws MonitorDisconnectedException {
        try {
            receiver.setStopExpButtonEnabled(enabled);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void setStartPeriodButtonEnabled(boolean enabled) throws MonitorDisconnectedException {
        try {
            receiver.setStartPeriodButtonEnabled(enabled);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public void setConnected(int client, boolean connected) throws MonitorDisconnectedException {
        try {
            receiver.setConnected(client, connected);
        }catch(RemoteException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    /** This is the remote class whose methods this transmitter calls */
    private RMIMonitor receiver;
}
