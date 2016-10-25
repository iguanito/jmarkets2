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
 * AuthPacket.java
 *
 * Created on February 3, 2005, 3:17 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.updates;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.PeriodDef;

/**
 *
 * @author  Raj Advani
 */
public class AuthUpdate {
    
    /** Creates a new instance of AuthPacket */
    public AuthUpdate(int status) {
        this.status = status;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getErrorMsg() {
        return errorMsg;
    }    
    
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }    

    public int getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
 
    public int getNumConnected() {
        return numConnected;
    }
    
    public void setNumConnected(int numConnected) {
        this.numConnected = numConnected;
    }
    
    public boolean isAllConnected() {
        return allConnected;
    }
    
    public void setAllConnected(boolean allConnected) {
        this.allConnected = allConnected;
    }

    public java.lang.String getName() {
        return name;
    }
    
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    /**
     * Getter for property periodInfo.
     * @return Value of property periodInfo.
     */
    public PeriodDef getPeriodInfo() {
        return periodInfo;
    }
    
    /**
     * Setter for property periodInfo.
     * @param periodInfo New value of property periodInfo.
     */
    public void setPeriodInfo(PeriodDef periodInfo) {
        this.periodInfo = periodInfo;
    }
    
    /** Getter for property replacement.
     * @return Value of property replacement.
     *
     */
    public boolean isReplacement() {
        return replacement;
    }
    
    /** Setter for property replacement.
     * @param replacement New value of property replacement.
     *
     */
    public void setReplacement(boolean replacement) {
        this.replacement = replacement;
    }
    
    public boolean isRetry() {
        return retry;
    }
    
    public void setRetry(boolean retry) {
        this.retry = retry;
    }
    
    /** These fields are used for all valid AuthPacket types */
    private int id;
    private int status;
    private String name;
    private int updateTime;
    
    /** Fields only used for auth success packets */
    private int numConnected;
    private boolean allConnected;
    
    /** Set to true if the re-authenticating client is replacing the currently connected client with the same db_id */
    private boolean replacement;
    
    /** Fields only used for re-auth success packets */
    private PeriodDef periodInfo;
    
    /** Fields used only for invalid packets */
    private String errorMsg;
    private boolean retry;
    
    public static int AUTH_FAILED = 0;
    public static int AUTH_SUCCESS = 1;
    public static int RE_AUTH_SUCCESS = 2;
}
