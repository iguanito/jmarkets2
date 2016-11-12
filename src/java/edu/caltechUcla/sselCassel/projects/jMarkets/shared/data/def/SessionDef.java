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
 * SessionDef.java
 *
 * Created on March 21, 2004, 11:55 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def;



/**
 * 
 * @author  Raj Advani, Walter M. Yuan
 * @version $Id: SessionDef.java 207 2005-02-09 11:17:43Z raj $
 */
public class SessionDef implements java.io.Serializable {
    
    private int experimenterId; 
    private int timeoutLength;
    private PeriodDef[] periods;    
    private String def; 
    private boolean manualControl; 
    private boolean showPastOrders; 
    private boolean showPastTransactions; 
    
    /**
     * Creates a new instance of SessionDef 
     */
    public SessionDef(int timeoutLength, PeriodDef[] periods, boolean showPastOrders, boolean showPastTransactions, String def) {
        this.timeoutLength = timeoutLength;
        this.periods = periods;
        this.showPastOrders = showPastOrders; 
        this.showPastTransactions = showPastTransactions; 
        this.def = def; 
    }
    
    public int getNumPeriods() {
        return periods.length;
    }
    
    public int getTimeoutLength() {
        return timeoutLength;
    }
    
    public void setTimeoutLength(int timeoutLength) {
        this.timeoutLength = timeoutLength;
    }
    
    public PeriodDef getPeriod(int period) {
        if (period < periods.length)
            return periods[period];
        else return null;
    }
    
    public PeriodDef[] getPeriods() {
        return periods;
    }
    
    public void setPeriods(PeriodDef[] periods) {
        this.periods = periods;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public int getExperimenterId() {
        return experimenterId;
    }

    public void setExperimenterId(int experimenterId) {
        this.experimenterId = experimenterId;
    }

    public boolean isManualControl() {
        return manualControl;
    }

    public void setManualControl(boolean manualControl) {
        this.manualControl = manualControl;
    }

    public boolean isShowPastOrders() {
        return showPastOrders;
    }

    public void setShowPastOrders(boolean showPastOrders) {
        this.showPastOrders = showPastOrders;
    }

    public boolean isShowPastTransactions() {
        return showPastTransactions;
    }

    public void setShowPastTransactions(boolean showPastTransactions) {
        this.showPastTransactions = showPastTransactions;
    }
}
