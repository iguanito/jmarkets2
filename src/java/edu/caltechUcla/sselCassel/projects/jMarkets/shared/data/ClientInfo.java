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
 * ClientInfo.java
 *
 * Created on July 20, 2004, 4:00 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data;

/**
 * This encapsulates information about each connected client. It only contains
 * connection information. For market-related information about each client, see
 * the SubjectInfo class.
 *
 * @author  Raj Advani
 */
public class ClientInfo implements java.io.Serializable {
    
    /** Creates a new instance of ClientInfo */
    public ClientInfo() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getDbId() {
        return dbId;
    }
    
    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
    
    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
    
    /** The client's name */
    private String name;
    
    /** The client's id number as assigned by the server */
    private int id;
    
    /** True if this client is in testing mode */
    private boolean testMode;
    
    /** The client's database id */
    private int dbId;
}
