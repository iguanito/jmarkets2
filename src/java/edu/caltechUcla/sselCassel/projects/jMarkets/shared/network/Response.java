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
 * Response.java
 *
 * Created on March 17, 2004, 1:40 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.network;

import java.util.Hashtable;

/**
 *
 * All transactions from server to client are encased in a Response object. The
 * object has an integer type and any amount of other information which is
 * transferred via a Hashtable.
 *
 *@author  Raj Advani
 *@version $Id: Response.java 238 2005-06-03 00:08:20Z raj $
 */
public class Response implements java.io.Serializable {
    
    /** Creates a new instance of Response */
    public Response(int type) {
        this.type = type;
        this.info = new Hashtable();
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public Object getInfo(Object key) {
        return info.get(key);
    }
    
    public void addInfo(Object key, Object value) {
        info.put(key, value);
    }
    
    public String getStringInfo(Object key) {
        try {
            return (String) info.get(key);
        }catch(Exception e) {
            //log.error("Failed to parse a string in information object" + MSConstants.newline + e);
        }
        return null;
    }
    
    public void addStringInfo(Object key, String value) {
        try {
            info.put(key, value);
        }catch(Exception e) {
            //log.error("Failed to place a string value into information object" + MSConstants.newline + e);
        }
    }
    
    public int getIntInfo(Object key) {
        try {
            return ((Integer) info.get(key)).intValue();
        }catch(Exception e) {
            //log.error("Failed to parse an int in information object" + MSConstants.newline + e);
        }
        return -1;
    }
    
    public void addIntInfo(Object key, int value) {
        try {
            info.put(key, new Integer(value));
        }catch(Exception e) {
            //log.error("Failed to place an int value into information object" + MSConstants.newline + e);
        }
    }
    
    public long getLongInfo(Object key) {
        try {
            return ((Long) info.get(key)).longValue();
        }catch(Exception e) {
            
        }
        return -1;
    }
    
    public void addLongInfo(Object key, long value) {
        try {
            info.put(key, new Long(value));
        }catch(Exception e) {
            
        }
    }
    
    public float getFloatInfo(Object key) {
        try {
            return ((Float) info.get(key)).floatValue();
        }catch(Exception e) {
            //log.error("Failed to parse a float in information object" + MSConstants.newline + e);
        }
        return -1;
    }
    
    public void addFloatInfo(Object key, float value) {
        try {
            info.put(key, new Float(value));
        }catch(Exception e) {
            //log.error("Failed to place a float value into information object" + MSConstants.newline + e);
        }
    }
    
    public boolean getBooleanInfo(Object key) {
        try {
            return ((Boolean) info.get(key)).booleanValue();
        }catch(Exception e) {
            //log.error("Failed to parse a boolean in information object" + MSConstants.newline + e);
        }
        return false;
    }
    
    public void addBooleanInfo(Object key, boolean value) {
        try {
            info.put(key, new Boolean(value));
        }catch(Exception e) {
            //log.error("Failed to place a boolean value into information object" + MSConstants.newline + e);
        }
    }
    
    public Hashtable getTable() {
        return info;
    }
    
    private int type;
    private Hashtable info;
    
    /** Sent in response to a client authentication request. Returns the information needed for the Communicator to
     *  establish and update connection to the server */
    public static int AUTH_CONFIRM_RESPONSE = 0;
    
    /** Sent in response to a client authentication request when a game is already running and the client is an active
     *  player */
    public static int REAUTH_RESPONSE = 1;
    
    /** Sent in response to a client authentication request if the authentication was rejected. Returns a string message
     *  detailing why the authentication was rejected */
    public static int AUTH_REJECT_RESPONSE = 3;
    
    /** Sent in response to the experimenter creating a new session */
    public static int SERVER_INIT_RESPONSE = 4;
    
    /** Sent in response to an UPDATE_REQUEST when there are no updates to process (only used in HTTP update protocol) */
    public static int NO_UPDATE_RESPONSE = 5;
    
    /** General acknowledgement response, used often in AuthServ to UpdateServ one-way (or other one-way) communications */
    public static int GENERAL_ACK_RESPONSE = 6;
    
    /** Sent every second to the clients to update their timers */
    public static int TIME_UPDATE_RESPONSE = 7;
    
    /** Sent to clients in response to requests with invalid session IDs */
    public static int INVALID_SESSION_RESPONSE = 8;
    
    /** The following are various responses (updates) to UPDATE_REQUESTS, all self-explanatory */
    public static int NEW_PERIOD_UPDATE = 9;
    
    /** Sent whenever the client side offer book needs to be updated */
    public static int OFFER_BOOK_UPDATE = 10;
    
    /** Sent to clients after all end period notifications have been received and payoffs calculated */
    public static int END_PERIOD_UPDATE = 11;
    
    /** Sent when a client sends an invalid (antiquated key) offer to the server */
    public static int OFFER_INVALID_UPDATE = 12;
    
    /** Sent whenever a market is closed */
    public static int CLOSE_MARKET_UPDATE = 13;
    
    /** Sent to the clients or Expmonitor to display a list of active sessions */
    public static int SESSION_QUERY_RESPONSE = 14;
    
    /** Sent to a client that has been replaced by a newly re-authenticated client */
    public static int REAUTH_KILL_RESPONSE = 15;
    
    public static int IS_MANUAL_CONTROL_RESPONSE = 16;
}
