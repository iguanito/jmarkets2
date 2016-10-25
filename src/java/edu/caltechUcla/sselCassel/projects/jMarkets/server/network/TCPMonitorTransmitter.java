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
import java.net.*;
import java.io.*;

/**
 * This class converts UI calls onto the admin monitor into TCP messages, which it sends to the
 * TCPReceiver that communicates with this transmitter. There is one transmitter (and one receiver)
 * for each monitor. If any error is encountered in transmitting an error, this throws a
 * MonitorDisconnectedException back to the MonitorServ that called the method. This way the
 * MonitorServ can disconnect this monitor from the system. UI calls come from the MonitorServ.
 *
 * The flow is as follows:
 *
 * MonitorServ --(method call to)--> TCPMonitorTransmitter --(socket message to)--> TCPMonitorReceiver --(method call to)--> ServerGUI
 *
 * @author  Raj Advani
 */
public class TCPMonitorTransmitter implements MonitorTransmitter {
    
    public TCPMonitorTransmitter(Socket sock, ObjectInputStream ois, ObjectOutputStream oos) throws MonitorDisconnectedException {
        this.sock = sock;
        this.ois = ois;
        this.oos = oos;
    }
    
    public void close() throws MonitorDisconnectedException {
        try {
            oos.close();
            ois.close();
            sock.close();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void addLogMessage(String logMsg, int p, String classInfo) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.ADD_LOG_MESSAGE);
            oos.writeInt(p);
            oos.writeObject(classInfo);
            oos.writeObject(logMsg);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void insertPriceChart(Vector priceChart) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.INSERT_CHART_MESSAGE);
            oos.writeObject(priceChart);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void connectClient(int client, String name) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.CONNECT_CLIENT_MESSAGE);
            oos.writeInt(client);
            oos.writeObject(name);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void updatePriceChart(String security, float time, float price) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.UPDATE_CHART_MESSAGE);
            oos.writeObject(security);
            oos.writeFloat(time);
            oos.writeFloat(price);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void setTimeLeft(int time) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.SET_TIME_MESSAGE);
            oos.writeInt(time);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void updateExpStatus(String str) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.UPDATE_STATUS_MESSAGE);
            oos.writeObject(str);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void updateMetrics(int iterations, int num, float time) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.UPDATE_METRICS_MESSAGE);
            oos.writeInt(iterations);
            oos.writeInt(num);
            oos.writeFloat(time);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void setAllConnected(boolean allConnected) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.SET_ALL_CONNECTED_MESSAGE);
            oos.writeBoolean(allConnected);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void setStartExpButtonEnabled(boolean enabled) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.ENABLE_START_MESSAGE);
            oos.writeBoolean(enabled);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void updateNumOffers(int client, int offers) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.UPDATE_NUM_OFFERS_MESSAGE);
            oos.writeInt(client);
            oos.writeInt(offers);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void constructInfoPeriodPanel() throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.CONSTRUCT_INFO_PANEL_MESSAGE);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void setOfferBacklog(int offers, boolean rejecting) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.SET_BACKLOG_MESSAGE);
            oos.writeInt(offers);
            oos.writeBoolean(rejecting);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void setStopExpButtonEnabled(boolean enabled) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.ENABLE_STOP_MESSAGE);
            oos.writeBoolean(enabled);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void setStartPeriodButtonEnabled(boolean enabled) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.ENABLE_PERIOD_START_MESSAGE);
            oos.writeBoolean(enabled);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    public synchronized void setConnected(int client, boolean connected) throws MonitorDisconnectedException {
        try {
            oos.writeInt(TCPMonitor.SET_CONNECTED_MESSAGE);
            oos.writeInt(client);
            oos.writeBoolean(connected);
            oos.flush();
        }catch(IOException e) {
            throw new MonitorDisconnectedException(e.getMessage());
        }
    }
    
    /** The socket that connects this TCPMonitorTransmitter to a TCPMonitorReceiver */
    private Socket sock;
    
    /** The object output stream from the socket */
    private ObjectOutputStream oos;
    
    /** The object input stream from the socket */
    private ObjectInputStream ois;
}
