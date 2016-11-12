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
import edu.caltechUcla.sselCassel.projects.jMarkets.server.interfaces.*;

/**
 *
 * This class implements the class TCPMonitor. It receives messages from the TCPMonitorTransmitter
 * and converts them into direct calls on the MonitorUI. See the TCPMonitorTransmitter comments for more
 * information on the server-monitor communication system
 *
 * @author  Raj Advani
 */
public class TCPMonitorReceiver implements TCPMonitor {
    
    public TCPMonitorReceiver(MonitorUI ui, String host, int port, int sessionId) {
        this.ui = ui;
        
        try {
            System.out.println("Creating a socket connection to server at host: " + host + " port: " + port);
            
            sock = new Socket(host, port);
            oos = new ObjectOutputStream(sock.getOutputStream());
            ois = new ObjectInputStream(sock.getInputStream());
            
            oos.flush();
            oos.writeInt(sessionId);
            oos.flush();
            
            System.out.println("Socket connection established, session id " + sessionId + " sent to server");
            
        }catch(UnknownHostException uhe) {
            uhe.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
        
        if (ois != null) {
            startReceiver();
        } else {
            System.out.println("Error occured opening input stream, shutting down monitor");
        }
    }
    
    /** Starts a thread that waits on socket messages. Processes each message by calling the appropriate
     *  method */
    private void startReceiver() {
        Runnable receiver = new Runnable() {
            public void run() {
                System.out.println("TCP Receiver started -- receiving monitor messages from server");
                
                while (true) {
                    try {
                        int messageType = ois.readInt();
                        
                        if (messageType == TCPMonitor.BREAK_CONNECTION_MESSAGE) {
                            System.out.println("Breaking TCP Monitor connection from server");
                            ois.close();
                            break;
                        } else
                            processMessage(messageType);
                    }catch(IOException e) {
                        System.out.println("IO error reading from monitor input stream, disconnecting monitor");
                        e.printStackTrace();
                        return;
                    }catch(Exception e) {
                        System.out.println("Error reading from monitor input stream, reading next message");
                        e.printStackTrace();
                    }
                }
                
                try {
                    ois.close();
                    oos.close();
                    sock.close();
                }catch(Exception e) {
                    System.out.println("Failed to shut down socket connections to server");
                }
            }
        };
        
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();
    }
    
    /** Routes the message to the given processing function given the message type */
    private void processMessage(int type) {
        if (type == TCPMonitor.ADD_LOG_MESSAGE)
            addLogMessage();
        else if (type == TCPMonitor.INSERT_CHART_MESSAGE)
            insertPriceChart();
        else if (type == TCPMonitor.CONNECT_CLIENT_MESSAGE)
            connectClient();
        else if (type == TCPMonitor.UPDATE_CHART_MESSAGE)
            updatePriceChart();
        else if (type == TCPMonitor.SET_TIME_MESSAGE)
            setTimeLeft();
        else if (type == TCPMonitor.UPDATE_STATUS_MESSAGE)
            updateExpStatus();
        else if (type == TCPMonitor.UPDATE_METRICS_MESSAGE)
            updateMetrics();
        else if (type == TCPMonitor.SET_ALL_CONNECTED_MESSAGE)
            setAllConnected();
        else if (type == TCPMonitor.ENABLE_START_MESSAGE)
            setStartExpButtonEnabled();
        else if (type == TCPMonitor.UPDATE_NUM_OFFERS_MESSAGE)
            updateNumOffers();
        else if (type == TCPMonitor.CONSTRUCT_INFO_PANEL_MESSAGE)
            constructInfoPeriodPanel();
        else if (type == TCPMonitor.SET_BACKLOG_MESSAGE)
            setOfferBacklog();
        else if (type == TCPMonitor.ENABLE_STOP_MESSAGE)
            setStopExpButtonEnabled();
        else if (type == TCPMonitor.ENABLE_PERIOD_START_MESSAGE)
            setStartPeriodButtonEnabled();
        else if (type == TCPMonitor.SET_CONNECTED_MESSAGE)
            setConnected();
    }
    
    
    public void addLogMessage() {
        try {
            int p = ois.readInt();
            String classInfo = (String) ois.readObject();
            String logMsg = (String) ois.readObject();
            
            ui.addLogMessage(logMsg, p, classInfo);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void insertPriceChart() {
        try {
            Vector priceChart = (Vector) ois.readObject();
            
            ui.insertPriceChart(priceChart);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void connectClient() {
        try {
            int client = ois.readInt();
            String name = (String) ois.readObject();
            
            ui.connectClient(client, name);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updatePriceChart() {
        try {
            String security = (String) ois.readObject();
            float time = ois.readFloat();
            float price = ois.readFloat();
            
            ui.updatePriceChart(security, time, price);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setTimeLeft() {
        try {
            int time = ois.readInt();
            
            ui.setTimeLeft(time);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updateExpStatus() {
        try {
            String str = (String) ois.readObject();
            
            ui.updateExpStatus(str);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updateMetrics() {
        try {
            int iterations = ois.readInt();
            int num = ois.readInt();
            float time = ois.readFloat();
            
            ui.updateMetrics(iterations, num, time);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setAllConnected() {
        try {
            boolean allConnected = ois.readBoolean();
            
            ui.setAllConnected(allConnected);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setStartExpButtonEnabled() {
        try {
            boolean enabled = ois.readBoolean();
            
            ui.setStartExpButtonEnabled(enabled);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setStartPeriodButtonEnabled(){
        try{
            boolean enabled = ois.readBoolean();
            
            ui.setStartPeriodButtonEnabled(enabled);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updateNumOffers() {
        try {
            int client = ois.readInt();
            int offers = ois.readInt();
            
            ui.updateNumOffers(client, offers);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void constructInfoPeriodPanel() {
        ui.constructInfoPeriodPanel();
    }
    
    public void setOfferBacklog() {
        try {
            int offers = ois.readInt();
            boolean rejecting = ois.readBoolean();
            
            ui.setOfferBacklog(offers, rejecting);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setStopExpButtonEnabled() {
        try {
            boolean enabled = ois.readBoolean();
            
            ui.setStopExpButtonEnabled(enabled);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setConnected() {
        try {
            int client = ois.readInt();
            boolean connected = ois.readBoolean();
            
            ui.setConnected(client, connected);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /** The socket that connects this TCPMonitorReceiver to a TCPMonitorTransmitter */
    private Socket sock;
    
    /** The object input stream from the socket */
    private ObjectInputStream ois;
    
    /** The object output stream from the socket */
    private ObjectOutputStream oos;
    
    /**
     * The MonitorUI monitor
     */
    private MonitorUI ui;
}
