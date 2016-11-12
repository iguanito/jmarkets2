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
 * SessionIdentifier.java
 *
 * Created on February 7, 2005, 9:29 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data;

/**
 * A structure that contains a sessions ID and name. This is used when the
 * client and ExpMonitor select what session they want to join / monitor
 *
 * @author  Raj Advani
 */
public class SessionIdentifier implements java.io.Serializable {
    
    /** Creates a new instance of SessionIdentifier */
    public SessionIdentifier(int sessionId, String sessionName, String status, int numClients) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.status = status;
        this.numClients = numClients;
    }
    
    /**
     * Getter for property sessionId.
     * @return Value of property sessionId.
     */
    public int getSessionId() {
        return sessionId;
    }
    
    /**
     * Setter for property sessionId.
     * @param sessionId New value of property sessionId.
     */
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
    
    /**
     * Getter for property sessionName.
     * @return Value of property sessionName.
     */
    public java.lang.String getSessionName() {
        return sessionName;
    }
    
    /**
     * Setter for property sessionName.
     * @param sessionName New value of property sessionName.
     */
    public void setSessionName(java.lang.String sessionName) {
        this.sessionName = sessionName;
    }
    
    /**
     * Getter for property status.
     * @return Value of property status.
     */
    public java.lang.String getStatus() {
        return status;
    }
    
    /**
     * Setter for property status.
     * @param status New value of property status.
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }
    
    /**
     * Getter for property numClients.
     * @return Value of property numClients.
     */
    public int getNumClients() {
        return numClients;
    }
    
    /**
     * Setter for property numClients.
     * @param numClients New value of property numClients.
     */
    public void setNumClients(int numClients) {
        this.numClients = numClients;
    }
    
    private int numClients;
    private int sessionId;
    private String sessionName;
    private String status;
}
