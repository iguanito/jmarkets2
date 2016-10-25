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
 * Request.java
 *
 * Created on March 17, 2004, 1:38 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.network;

import java.util.Hashtable;

/**
 *
 * All transactions from client to server are encased in a Request object. The
 * object has an integer type and any amount of other information which is
 * transferred via a Hashtable.
 *
 *@author  Raj Advani
 *@version $Id: Request.java 207 2005-02-09 11:17:43Z raj $
 */
public class Request implements java.io.Serializable {
     
    /** Creates a new instance of Request */
    public Request(int type) {
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
    
    /** Client authentication request, the first request sent by each client to the server */
    public static int CLIENT_AUTH_REQUEST = 0;
    
    /** Sent by the experimenter to initiate a new session */
    public static int SERVER_INIT_REQUEST = 1;
    
    /** Sent by the client when using HTTP update protocol. Sent periodically to check the server for updates */
    public static int UPDATE_REQUEST = 2;
    
    /** Sent by the client when using HTTP update protocol. Sent when a read update fails on the client side, 
     *  so that the client can try getting the lost response again */
    public static int RETRY_UPDATE_REQUEST = 3;
    
    /** The AuthServ sends this request to the UpdateServ to indicate initialize the UpdateServ */
    public static int INIT_UPDATESERV_REQUEST = 4;
    
    /** Servlets send this request to the UpdateServ to make the UpdateServ add the enclosed update
     *  to the queue */
    public static int ADD_UPDATE_REQUEST = 5;
    
    /** Clients send this request to the TradeServ whenever they conduct a transaction */
    public static int TRANSACTION_REQUEST = 6;
    
    /** Sent by the experimenter to terminate the current session */
    public static int TERMINATE_SESSION_REQUEST = 7;
    
    /** Sent from the ExpMonitor to the AuthServ to start the game */
    public static int START_GAME_REQUEST = 8;
    
    /** Sent from the clients or Expmonitor to request a list of active sessions */
    public static int SESSION_QUERY_REQUEST = 9;
    
    /** Sent by the experimenter to start a period */
    public static int START_PERIOD = 10; 
    
    /** Sent by the experimenter to stop a period */
    public static int STOP_PERIOD = 11; 
    
    public static int IS_MANUAL_CONTROL = 12;
    public static int SET_MANUAL_CONTROL = 13;
    
    
}
