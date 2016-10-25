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

/**
 * The MonitorServ uses this class to communicate with administrator monitors. The flow 
 * goes as follows:
 *
 * MonitorServ --> MonitorTransmitter --> Receiver --> Monitor UI
 *
 * The Receiver is on the admin client side, and is either an RMIMonitor or a TCPMonitor (or
 * other network protocols as these become supported) *
 *
 * @author  Raj Advani
 */
public interface MonitorTransmitter {
    
    public void close() throws MonitorDisconnectedException;
    
    public void addLogMessage(String logMsg, int p, String classInfo) throws MonitorDisconnectedException;
    
    public void insertPriceChart(Vector priceChart) throws MonitorDisconnectedException;
    
    public void connectClient(int client, String name) throws MonitorDisconnectedException;
    
    public void updatePriceChart(String security, float time, float price) throws MonitorDisconnectedException;
  
    public void setTimeLeft(int time) throws MonitorDisconnectedException;

    public void updateExpStatus(String str) throws MonitorDisconnectedException;
    
    public void updateMetrics(int iterations, int num, float time) throws MonitorDisconnectedException;
    
    public void setAllConnected(boolean allConnected) throws MonitorDisconnectedException;
    
    public void setStartExpButtonEnabled(boolean enabled) throws MonitorDisconnectedException;
    
    public void updateNumOffers(int client, int offers) throws MonitorDisconnectedException;
    
    public void constructInfoPeriodPanel() throws MonitorDisconnectedException;
    
    public void setOfferBacklog(int offers, boolean rejecting) throws MonitorDisconnectedException;
      
    public void setStopExpButtonEnabled(boolean enabled) throws MonitorDisconnectedException;
    
    public void setStartPeriodButtonEnabled(boolean enabled) throws MonitorDisconnectedException;
    
    public void setConnected(int client, boolean connected) throws MonitorDisconnectedException;
}
