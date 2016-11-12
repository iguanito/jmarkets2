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
 * SecurityPeriodBean.java
 *
 * Created on July 30, 2004, 4:35 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;

/**
 *
 * @author  Raj Advani
 */
public class SecurityPeriodBean implements java.io.Serializable {
    
    /** Creates a new instance of SecurityPeriodBean */
    public SecurityPeriodBean() {
    }
 
    /**
     * Getter for property maxPrice.
     * @return Value of property maxPrice.
     */
    public float getMaxPrice() {
        return maxPrice;
    }
    
    /**
     * Setter for property maxPrice.
     * @param maxPrice New value of property maxPrice.
     */
    public void setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    /**
     * Getter for property minPrice.
     * @return Value of property minPrice.
     */
    public float getMinPrice() {
        return minPrice;
    }
    
    /**
     * Setter for property minPrice.
     * @param minPrice New value of property minPrice.
     */
    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }
    
    /**
     * Getter for property timeOpen.
     * @return Value of property timeOpen.
     */
    public int getTimeOpen() {
        return timeOpen;
    }
    
    /**
     * Setter for property timeOpen.
     * @param timeOpen New value of property timeOpen.
     */
    public void setTimeOpen(int timeOpen) {
        this.timeOpen = timeOpen;
    }
    
    /**
     * Getter for property title.
     * @return Value of property title.
     */
    public java.lang.String getTitle() {
        return title;
    }
    
    /**
     * Setter for property title.
     * @param title New value of property title.
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }
    
    /**
     * Getter for property initials.
     * @return Value of property initials.
     */
    public int[] getInitials() {
        return this.initials;
    }
    
    /**
     * Setter for property initials.
     * @param initials New value of property initials.
     */
    public void setInitials(int[] initials) {
        this.initials = initials;
    }
    
    /**
     * Getter for property constraints.
     * @return Value of property constraints.
     */
    public int[] getConstraints() {
        return this.constraints;
    }
    
    /**
     * Setter for property constraints.
     * @param constraints New value of property constraints.
     */
    public void setConstraints(int[] constraints) {
        this.constraints = constraints;
    }
    
    public int getInitial(int group) {
        return initials[group];
    }
    
    public int getConstraint(int group) {
        return constraints[group];
    }
    
    public int getPrivelege(int group) {
        if (buyPriveleges[group] && !sellPriveleges[group])
            return JMConstants.BUYER_ROLE;
        else if (!buyPriveleges[group] && sellPriveleges[group])
            return JMConstants.SELLER_ROLE;
        else if (buyPriveleges[group] && sellPriveleges[group])
            return JMConstants.BOTH_ROLE;
        else
            return JMConstants.NEITHER_ROLE;
    }
    
    public boolean getAddSurplus(int group) {
        return addSurplus[group];
    }
    
    public boolean getAddDividend(int group) {
        return addDividend[group];
    }
    
    /**
     * Getter for property buyPrivelege.
     * @return Value of property buyPrivelege.
     */
    public boolean[] getBuyPriveleges() {
        return this.buyPriveleges;
    }    
    
    /**
     * Setter for property buyPrivelege.
     * @param buyPrivelege New value of property buyPrivelege.
     */
    public void setBuyPriveleges(boolean[] buyPriveleges) {
        this.buyPriveleges = buyPriveleges;
    }    
    
    /**
     * Getter for property sellPrivelege.
     * @return Value of property sellPrivelege.
     */
    public boolean[] getSellPriveleges() {
        return sellPriveleges;
    }
    
    /**
     * Setter for property sellPrivelege.
     * @param sellPrivelege New value of property sellPrivelege.
     */
    public void setSellPriveleges(boolean[] sellPriveleges) {
        this.sellPriveleges = sellPriveleges;
    }
    
    /** Getter for property addDividend.
     * @return Value of property addDividend.
     *
     */
    public boolean[] getAddDividend() {
        return addDividend;
    }
    
    /** Setter for property addDividend.
     * @param addDividend New value of property addDividend.
     *
     */
    public void setAddDividend(boolean[] addDividend) {
        this.addDividend = addDividend;
    }
    
    /** Getter for property addSurplus.
     * @return Value of property addSurplus.
     *
     */
    public boolean[] getAddSurplus() {
        return addSurplus;
    }
    
    /** Setter for property addSurplus.
     * @param addSurplus New value of property addSurplus.
     *
     */
    public void setAddSurplus(boolean[] addSurplus) {
        this.addSurplus = addSurplus;
    }
    
    private String title;
    private float minPrice;
    private float maxPrice;
    private int timeOpen;
    
    private boolean[] addDividend;
    private boolean[] addSurplus;
    private int[] initials;    
    private int[] constraints;
    private boolean[] buyPriveleges;
    private boolean[] sellPriveleges;   
     
}
