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
 * HTTPUpdateServ.java
 *
 * Created on March 18, 2004, 5:02 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;

/**
 *
 * @author  Raj Advani
 *
 * The HTTP implementation of the UpdateServ. This class implements the doPost method so that every time
 * an update request is received from a client, that client's updateQueue is checked for responses and
 * the next in line is sent to the client
 */
public class HTTPUpdateServ extends UpdateServ {
    
    /** Creates a new instance of HttpUpdateServ */
    public HTTPUpdateServ() {
        super();
        log.info("The HTTP Update Servlet (HTTPUpdateServ) has been initialized");
    }
    
    public void initServ() {
        super.initServ();
        
        reauthDumping = new Hashtable();
        lastUpdates = new Hashtable();
        lastTimeReceived = new Hashtable();
        blacklist = new Hashtable();
        computerIds = new Hashtable();
    }
    
    /** Add a client, with the given database subject ID, to the current UpdateServ. This
     *  is called at the beginning of a session. Return true if the operation is successful */
    public boolean addClient(int client) {
        Integer clientInt = new Integer(client);
        
        if (super.addClient(client)) {
            reauthDumping.put(clientInt, Boolean.FALSE);
            lastUpdates.put(clientInt, new Response(Response.NO_UPDATE_RESPONSE));
            lastTimeReceived.put(clientInt, new Long(0));
            blacklist.put(clientInt, new Vector());
            computerIds.put(clientInt, "none");
            
            return true;
        }
        return false;
    }
    
    /** Delete a client form the UpdateServ queues. This is called when a client drops out of
     *  a session or when a session is completed */
    public boolean deleteClient(int client) {
        Integer clientInt = new Integer(client);
        
        if (super.deleteClient(client)) {
            reauthDumping.remove(clientInt);
            lastUpdates.remove(clientInt);
            lastTimeReceived.remove(clientInt);
            blacklist.remove(clientInt);
            computerIds.remove(clientInt);
            
            return true;
        }
        return false;
    }
    
    /** Override this method from the UpdateServ so that when the dispatcher checks
     *  connection status, the statuses will be updated according to the timer threshold.
     *  Without this the disconnect status would only be updated according to the
     *  queue threshold (which is fine in most situations) */
    public Hashtable getDisconnectStatus() {
        //log.debug("HTTPUpdateServ is getting disconnect status");
        
        long now = (new Date()).getTime();
        
        Enumeration clients = lastTimeReceived.keys();
        while (clients.hasMoreElements()) {
            Integer client = (Integer) clients.nextElement();
            long last = ((Long) lastTimeReceived.get(client)).longValue();
            
            if ((now - last) > DISCONNECT_TIME_THRESHOLD && last != 0) {
                log.debug("No update request has been received by client " + client + " in " + (now - last) + " ms -- setting to disconnected");
                setDisconnected(client.intValue());
            }
        }
        
        return super.getDisconnectStatus();
    }
    
    /** The HTTP implementation does the following things:
     *
     *  1) Checks to see if the update is for re-authentication. If so, dump all previously
     *     received updates for this client, and continue to dump them until the re-auth
     *     update is received by the client.
     *
     *  2) Check the size of the queue and use this to update the connection status of the
     *     client by comparing it to the threshold level
     *
     *  3) Check if the update is for killing a client that has been replaced by a newly
     *     re-authenticated client. If so, we must blacklist the computer that is to receive
     *     the kill response so that we no longer send updates to that computer. Blacklisted
     *     computers only receive kill responses. Note that a "computer" is defined by the IP
     *     address and time of login -- so a new JMarkets client can replace an old one on the
     *     same computer. The computer Id to blacklist is the computer Id currently on file */
    public void processUpdate(int client, Response res) {
        int size = 0;
        
        if (res.getType() == Response.REAUTH_KILL_RESPONSE) {
            String currentId = (String) computerIds.get(client);
            Vector blacklistedIds = (Vector) blacklist.get(client);
            blacklistedIds.add(currentId);            
        }
        
        else if (res.getType() == Response.REAUTH_RESPONSE) {
            dumpOfferBookUpdates(client);
            setReauthDumping(client, true);
            
            size = enqueueUpdate(client, res);
        }
        
        else if (!getReauthDumping(client)) {
            size = enqueueUpdate(client, res);
        }
        
        if (size > DISCONNECT_UPDATE_THRESHOLD)
            setDisconnected(client);
    }
    
    /** Set the given client to the given reauth dumping state (true or false). When in re-auth dumping
     *  state, no updates are queued for the given client. This is becuase, while re-authenticating, it is
     *  not necessary to send any updates to the client, as the reauth update will get the client back up
     *  to the proper state */
    private boolean setReauthDumping(int client, boolean dumping) {
        Integer clientInt = new Integer(client);
        
        if (!reauthDumping.containsKey(clientInt)) {
            log.warn("Cannot edit re-auth dumping state of client " + client + " -- this client is not in any session!");
            return false;
        }
        
        reauthDumping.put(clientInt, new Boolean(dumping));
        return true;
    }
    
    /** Get the re-auth dumping state of the given client */
    private boolean getReauthDumping(int client) {
        Integer clientInt = new Integer(client);
        
        if (!reauthDumping.containsKey(clientInt)) {
            log.warn("Cannot get re-auth dumping state of client " + client + " -- this client is not in any session!");
            return false;
        }
        
        return ((Boolean) reauthDumping.get(clientInt)).booleanValue();
    }
    
    /** Dump the offer book updates in the queue of the given client. Called upon re-authentication, because
     *  the client will receive a fresh offerbook that includes all these updates. Does not dump CLOSE PERIOD
     *  or other system updates because these must be sent to the client */
    public void dumpOfferBookUpdates(int client) {
        dumpUpdates(client);
    }
    
    /** This is called whenever an update request is received. If the reponse is received from a blacklisted 
     *  computer, then send that computer a reauth-kill-response, indicating that the computer has been replaced
     *  by a newer one. If there is no record of computer Id correspnoding to the database id (client) given 
     *  in the request, then that means this is the first update request received, and the computer id given
     *  should be used -- accordingly in this case the computer Id on file is updated. Also update the computer
     *  ID on file when a re-auth request is sent out, becuase that indicates a new computer has replaced
     *  the old one. Note that we need the computer Ids on file because this way we can tell which computer to
     *  blacklist when the server adds a re-auth-kill response to the queue */
    public Response processUpdateRequest(Request req) {
        int client = req.getIntInfo("id");
        String compId = req.getStringInfo("computerId");
        
        Vector blackListedIds = (Vector) blacklist.get(new Integer(client));
        if (blackListedIds.contains(compId))
            return new Response(Response.REAUTH_KILL_RESPONSE);
        
        String currentId = (String) computerIds.get(new Integer(client));
        if (currentId.equalsIgnoreCase("none"))
            computerIds.put(new Integer(client), compId);
        
        Response res = dequeueUpdate(client);
        setConnected(client);
        
        if (res.getType() == Response.END_PERIOD_UPDATE)
            log.info("HTTPUpdateServ is sending an END_PERIOD_UPDATE to client id " + client);
        
        if (res.getType() == Response.NEW_PERIOD_UPDATE)
            log.debug("HTTPUpdateServ is sending a NEW_PERIOD_UPDATE to client id " + client);
        
        if (res.getType() == Response.REAUTH_RESPONSE) {
            log.debug("HTTPUpdateServ is sending a REAUTH_RESPONSE to client id " + client);
            setReauthDumping(client, false);
            computerIds.put(new Integer(client), compId);
        }
        
        setLastUpdate(client, res);
        
        return res;
    }
    
    public Response processRetryRequest(Request req) {
        int client = req.getIntInfo("id");
        
        log.info("Received update retry request from client " + client + " -- resending last update");
        return getLastUpdate(client);
    }
    
    /** Store the last update sent to the given client (dbId), in case we receive a retry request.
     *  Also store the time when we last sent this response, so that we can detect when a client
     *  is disconnected. If the last sent response is a large one (re-auth or new period), then give
     *  the client more time to process without being mistakenly taken for disconnected by adding
     *  a few extra seconds to the last received update time */
    public boolean setLastUpdate(int client, Response update) {
        Integer clientInt = new Integer(client);
        
        if (!lastUpdates.containsKey(clientInt) || !lastTimeReceived.containsKey(clientInt)) {
            log.warn("Cannot update last update of client " + client + " -- this client is not connected to any session!");
            return false;
        }
        
        lastUpdates.put(clientInt, update);
        
        lastTimeReceived.put(clientInt, new Long((new Date()).getTime()));
        if (update.getType() == Response.NEW_PERIOD_UPDATE || update.getType() == Response.REAUTH_RESPONSE) {
            lastTimeReceived.put(clientInt, new Long((new Date()).getTime() + 10000));
        }
        if (update.getType() == Response.END_PERIOD_UPDATE) {
            lastTimeReceived.put(clientInt, new Long((new Date()).getTime() + 25000));
        }
        
        return true;
    }
    
    public Response getLastUpdate(int client) {
        Integer clientInt = new Integer(client);
        
        if (!lastUpdates.containsKey(clientInt)) {
            log.warn("Cannot get last update of client " + client + " -- this client is not connected to any session!");
            return new Response(Response.INVALID_SESSION_RESPONSE);
        }
        
        return (Response) lastUpdates.get(clientInt);
    }
    
    /** This maps database ID to current computer ID. Computer IDs are used for killing clients that
     *  are being replaced by newly re-authenticated clients */
    private Hashtable computerIds;
    
    /** This Vector contains a list of computer IDs for each database ID that have been blacklisted
     *  from the update server. This occurs when a client is 'replaced' by a newly authenticated
     *  client */
    private Hashtable blacklist;
    
    /** Hashtable that contains the re-auth dumping states for each connected client */
    private Hashtable reauthDumping;
    
    /** Stores the last sent response of each client, so that if the client fails to read an update,
     *  the update can be sent again via a UPDATE_RETRY_REQUEST */
    private Hashtable lastUpdates;
    
    /** Stores the time of the last received update request from the given client */
    private Hashtable lastTimeReceived;
    
    /** Set a client to disconnected status whenever the number of updates exceeds this amount. When the
     *  client starts receiving updates again, set to connected */
    private static int DISCONNECT_UPDATE_THRESHOLD = 10;
    
    /** Also set a client to disconnected whenever the number of milliseconds that have passed without
     *  receiving an update request exceeds this amount */
    private static int DISCONNECT_TIME_THRESHOLD = 5000;
}
