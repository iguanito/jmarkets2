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
 * HTTPUpdater.java
 *
 * Created on March 18, 2004, 1:01 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.network;

import java.net.*;
import java.io.*;
import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.control.*;

/**
 *
 * @author  Raj Advani
 *
 * This class (thread) periodically checks the server's "update" servlet for updates.
 * Whenever an update is received it is relayed to the processUpdate function in the
 * Communicator. The advantage of this update implementation is that it is firewall safe.
 * The disadvantage is that it results in a large amount of useless HTTP connections/updates
 * being made to the server. The other implementation is RMIUpdater, which establishes an
 * RMI connection with the "update" servlet so that the server can itself relay updates
 * to the processUpdate function when they are ready. The RMI implementation reduces the
 * network traffic (is more efficient) but is not firewall safe since RMI opens ports
 * randomly
 */
public class HTTPUpdater implements Runnable {
    
    /** Creates a new instance of HTTPUpdater */
    public HTTPUpdater(Communicator com, URL updateServ, int period, int id, int sessionId) {
        this.com = com;
        this.updateServ = updateServ;
        this.period = period;
        this.id = id;
        this.sessionId = sessionId;
        
        try {
            compId = InetAddress.getLocalHost().getHostAddress() + "-" + (new Date()).getTime();
        }catch(Exception e) {
            System.out.println("Error generating computer ID");
            compId = "unknown";
        }
        
        System.out.println("Computer ID: " + compId);
    }
    
    /** Periodically check for new updates */
    public synchronized void run() {
        try {
            while (true) {
                Request req = new Request(Request.UPDATE_REQUEST);
                req.addIntInfo("id", id);
                req.addIntInfo("sessionId", sessionId);
                req.addStringInfo("computerId", compId);
                
                Request retryReq = new Request(Request.RETRY_UPDATE_REQUEST);
                retryReq.addIntInfo("id", id);
                retryReq.addIntInfo("sessionId", sessionId);
                retryReq.addStringInfo("computerId", compId);
                
                Response res = com.sendRequest(updateServ, req, retryReq);
                
                if (res == null)
                    continue;
                
                if (res.getType() == Response.INVALID_SESSION_RESPONSE) {
                    System.out.println("Received invalid session response -- disconnecting updater");
                    break;
                }
                
                if (res.getType() == Response.REAUTH_KILL_RESPONSE) {
                    com.processUpdate(res);
                    break;
                }
                
                if (res.getType() != Response.NO_UPDATE_RESPONSE) {
                    com.processUpdate(res);
                    continue;
                }
                
                wait(period);
            }
        }catch(Exception e) {
            e.printStackTrace();
            run();
        }
    }
    
    /** A code that identifies this client's computer: IP Address + Time of instantiation */
    private String compId;
    
    /** The URL of the updater servlet */
    private URL updateServ;
    
    /** The amount of time to wait between requests for updates */
    private int period;
    
    /** The main client communicator */
    private Communicator com;
    
    /** Identification info used to get updates from the server */
    private int id, sessionId;
}
