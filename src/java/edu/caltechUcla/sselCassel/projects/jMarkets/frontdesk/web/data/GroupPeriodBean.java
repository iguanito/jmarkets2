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
 * RolePeriodBean.java
 *
 * Created on July 30, 2004, 4:35 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data;

/**
 *
 * @author  Raj Advani
 */
public class GroupPeriodBean implements java.io.Serializable {
    
    /** Creates a new instance of GroupPeriodBean */
    public GroupPeriodBean() {
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int group) {
        this.id = group;
    }
    
    public float getCashInitial() {
        return cashInitial;
    }
    
    public void setCashInitial(float cashInitial) {
        this.cashInitial = cashInitial;
    }
    
    public java.lang.String getName() {
        return name;
    }
    
    public void setName(java.lang.String name) {
        this.name = name;
    }

    public java.lang.String getPayoffFunctionName() {
        return payoffFunctionName;
    }
    
    public void setPayoffFunctionName(java.lang.String payoffFunctionName) {
        this.payoffFunctionName = payoffFunctionName;
    }
    
    public java.lang.String getBankruptcyFunctionName() {
        return bankruptcyFunctionName;
    }
    
    public void setBankruptcyFunctionName(java.lang.String bankruptcyFunctionName) {
        this.bankruptcyFunctionName = bankruptcyFunctionName;
    }

    public float getBankruptcyCutoff() {
        return bankruptcyCutoff;
    }
    
    public void setBankruptcyCutoff(float bankruptcyCutoff) {
        this.bankruptcyCutoff = bankruptcyCutoff;
    }
    
    public boolean isAddCash() {
        return addCash;
    }
    
    public void setAddCash(boolean addCash) {
        this.addCash = addCash;
    }
    
    private int id;
    private String name;
    private float cashInitial;
    private String payoffFunctionName;
    private String bankruptcyFunctionName;
    private float bankruptcyCutoff;
    private boolean addCash;
}
