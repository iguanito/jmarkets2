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
 * ConnectionMonitor.java
 *
 * Created on February 8, 2005, 2:53 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.network;

import java.util.*;

import edu.caltechUcla.sselCassel.projects.jMarkets.server.control.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used by the dispatcher to start a simple thread that periodically
 * checks the connection status of all the clients via the updateServ, and uses
 * this information to update the experiment monitors via the monitorServ
 *
 * @author  Raj Advani
 */
public class ConnectionMonitor {
    
    /** Creates a new instance of ConnectionMonitor */
    public ConnectionMonitor(Controller controlServ, Updater updateServ, Monitor monitorServ) {
        this.controlServ = controlServ;
        this.updateServ = updateServ;
        this.monitorServ = monitorServ;
        
        alreadyDisconnected = new Vector();
    }
    
    /** Start the monitor in a new thread */
    public void start() {
        Runnable doUpdate = new Runnable() {
            public void run() {
                startMonitor();
            }
        };
        
        monitorThread = new Thread(doUpdate);
        monitorThread.start();
    }
    
    /** Every STATUS_CHECK_PERIOD milliseconds, retrieve a list of sessionIds from the ControlServ,
     *  and a list of connection statuses from the UpdateServ. Run through the connection status
     *  for each client in each session, and update the server monitors if anyone's connection
     *  status has changed. Changes are monitored using the alreadyDisconnected Vector stored in
     *  this object */
    public void startMonitor() {
        while (!stopMonitor) {
            waitPeriod();
            
            int[] sessionIds = controlServ.getSessionIds();
            Hashtable disconnected = updateServ.getDisconnectStatus();
            
            for (int i=0; i<sessionIds.length; i++) {
                int[] clients = controlServ.getSessionClients(sessionIds[i]);
                if (clients == null)
                    continue;
                
                for (int j=0; j<clients.length; j++) {
                    if (clients[j] < 0)
                        continue;
                    
                    Integer client = new Integer(clients[j]);
                    boolean discon = ((Boolean) disconnected.get(client)).booleanValue();
                    
                    if (!alreadyDisconnected.contains(client)) {
                        
                        if (discon) {
                            log.warn("Client " + client + " in session " + sessionIds[i] + " has lost connection");
                            monitorServ.updateConnectionStatus(sessionIds[i], j, false);
                            alreadyDisconnected.add(client);
                        }
                    }
                    
                    else if (!discon) {
                        log.info("Client " + client + " in session " + sessionIds[i] + " has re-established connection");
                        monitorServ.updateConnectionStatus(sessionIds[i], j, true);
                        alreadyDisconnected.remove(client);
                    }
                }
            }
        }
    }
    
    private synchronized void waitPeriod() {
        try {
            wait(STATUS_CHECK_PERIOD);
        }catch(InterruptedException e) {
            log.error("Connection monitor has been interrupted", e);
        }
    }
    
    public void stopMonitor(){
        log.error("ConnectionMonitor.stopMonitor() called...");
        stopMonitor = true;
    }
    
    private Controller controlServ;
    private Updater updateServ;
    private Monitor monitorServ;
    private Thread monitorThread;
    private boolean stopMonitor=false;
    
    /** In order to limit the number of calls made to the MonitorServ, the ConnectionMonitor
     *  stores a list of what clients are currently registered as disconnected. This way it
     *  only updates the ExpMonitors if a client is newly registered as disconnected */
    private Vector alreadyDisconnected;
    
    private int STATUS_CHECK_PERIOD = 5000;
    
    private Log log = LogFactory.getLog(ConnectionMonitor.class);
}
