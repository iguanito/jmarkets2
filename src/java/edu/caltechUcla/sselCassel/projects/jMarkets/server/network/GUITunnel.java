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
 * GUITunnel.java
 *
 * Created on October 8, 2004, 7:48 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.network;


import java.rmi.*;

import edu.caltechUcla.sselCassel.projects.jMarkets.server.control.DispatchServ;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  Administrator
 */
public class GUITunnel implements Remote, GUILink {
    
    /** Creates a new instance of GUITunnel */
    public GUITunnel(DispatchServ serv) {
        this.serv = serv;
    } 
    
    public void registerExpMonitor(RMIMonitor monitorReceiver, int sessionId) throws RemoteException {
        MonitorTransmitter transmitter = new RMIMonitorTransmitter(monitorReceiver);
        
        serv.registerMonitorTransmitter(transmitter, sessionId);      
        log.info("Experiment Monitor has registered a transmitter with the server RMI interface");
    }    
    
    private DispatchServ serv;
    
    private static Log log = LogFactory.getLog(GUITunnel.class);
}
