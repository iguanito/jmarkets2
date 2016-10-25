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
 * EarningsRow.java
 *
 * Created on November 1, 2004, 7:21 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable;

/**
 *
 * @author  Administrator
 */
public class EarningsRow implements java.io.Serializable {
    public EarningsRow() {
        
    }
    
    public int getPeriod() {
        return period;
    }
    
    public void setPeriod(int period) {
        this.period = period;
    }
    
    public String getSecurity() {
        return security;
    }
    
    public void setSecurity(String security) {
        this.security = security;
    }
    
    public float getHoldings() {
        return holdings;
    }
    
    public void setHoldings(float holdings) {
        this.holdings = holdings;
    }
    
    public float getCumPayoff() {
        return cumPayoff;
    }
    
    public void setCumPayoff(float cumPayoff) {
        this.cumPayoff = cumPayoff;
    }
    
    public float getNumSales() {
        return numSales;
    }
    
    public void setNumSales(float numSales) {
        this.numSales = numSales;
    }
    
    public float getNumPurchases() {
        return numPurchases;
    }
    
    public void setNumPurchases(float numPurchases) {
        this.numPurchases = numPurchases;
    }
    
    private int period;
    private String security;
    private float holdings;
    private float cumPayoff;
    private float numSales;
    private float numPurchases;
}

