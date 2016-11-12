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
 * PeriodBean.java
 *
 * Created on July 28, 2004, 7:34 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data;

import java.util.*;

/**
 * @author Raj Advani
 */
public class PeriodBean implements java.io.Serializable {
    
    public int getNumSecurities() {
        return numSecurities;
    }
    
    public void setNumSecurities(int numSecurities) {
        this.numSecurities = numSecurities;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getNumDivisions() {
        return numDivisions;
    }
    
    public void setNumDivisions(int numDivisions) {
        this.numDivisions = numDivisions;
    }
    
    /**
     * Getter for property openDelay.
     * @return Value of property openDelay.
     */
    public int getOpenDelay() {
        return openDelay;
    }
    
    /**
     * Setter for property openDelay.
     * @param openDelay New value of property openDelay.
     */
    public void setOpenDelay(int openDelay) {
        this.openDelay = openDelay;
    }
    
    /**
     * Getter for property periodLength.
     * @return Value of property periodLength.
     */
    public int getPeriodLength() {
        return periodLength;
    }
    
    /**
     * Setter for property periodLength.
     * @param periodLength New value of property periodLength.
     */
    public void setPeriodLength(int periodLength) {
        this.periodLength = periodLength;
    }
    
    /**
     * Getter for property subjects.
     * @return Value of property subjects.
     */
    public java.util.List getSubjects() {
        return subjects;
    }
    
    /**
     * Setter for property subjects.
     * @param subjects New value of property subjects.
     */
    public void setSubjects(java.util.List subject) {
        this.subjects = subject;
    }
    
    /**
     * Getter for property securities.
     * @return Value of property securities.
     */
    public java.util.List getSecurities() {
        return securities;
    }
    
    /**
     * Setter for property securities.
     * @param securities New value of property securities.
     */
    public void setSecurities(java.util.List security) {
        this.securities = security;
    }
    
    /**
     * Getter for property groups.
     * @return Value of property groups.
     */
    public java.util.List getGroups() {
        return groups;
    }
    
    /**
     * Setter for property groups.
     * @param groups New value of property groups.
     */
    public void setGroups(java.util.List group) {
        this.groups = group;
    }
    
    /**
     * Getter for property numStates.
     * @return Value of property numStates.
     */
    public int getNumStates() {
        return numStates;
    }
    
    /**
     * Setter for property numStates.
     * @param numStates New value of property numStates.
     */
    public void setNumStates(int numStates) {
        this.numStates = numStates;
    }
    
    /** Getter for property payoffFunctionSpecs.
     * @return Value of property payoffFunctionSpecs.
     *
     */
    public List getPayoffFunctionSpecs() {
        return payoffFunctionSpecs;
    }
    
    /** Setter for property payoffFunctionSpecs.
     * @param payoffFunctionSpecs New value of property payoffFunctionSpecs.
     *
     */
    public void setPayoffFunctionSpecs(List payoffFunctionSpecs) {
        this.payoffFunctionSpecs = payoffFunctionSpecs;
    }
    
    /** Getter for property bankruptcyFunctionSpecs.
     * @return Value of property bankruptcyFunctionSpecs.
     *
     */
    public List getBankruptcyFunctionSpecs() {
        return bankruptcyFunctionSpecs;
    }
    
    /** Setter for property bankruptcyFunctionSpecs.
     * @param bankruptcyFunctionSpecs New value of property bankruptcyFunctionSpecs.
     *
     */
    public void setBankruptcyFunctionSpecs(List bankruptcyFunctionSpecs) {
        this.bankruptcyFunctionSpecs = bankruptcyFunctionSpecs;
    }
    
    public String getMarketEngine() {
        return marketEngine;
    }
    
    public void setMarketEngine(String marketEngine) {
        this.marketEngine = marketEngine;
    }
    
    public boolean isApplyTrigger() {
        return applyTrigger;
    }

    public void setApplyTrigger(boolean applyTrigger) {
        this.applyTrigger = applyTrigger;
    }
    
     public void setUseGui(boolean useGui){
        this.useGui = useGui;
    }
    
    public boolean getUseGui(){
        return useGui;
    }
    
    public void setUseEffCashValidation(boolean useEffCashValidation){
        this.useEffCashValidation = useEffCashValidation;
    }
    
    public boolean getUseEffCashValidation(){
        return useEffCashValidation;
    }
    
     public boolean isClosebook() {
        return closebook;
    }

     public void setClosebook(boolean closebook) {
         this.closebook = closebook;
     }
     
     public boolean isShowSuggestedClearingPrice() {
         return showSuggestedClearingPrice;
     }
     
     public void setShowSuggestedClearingPrice(boolean showSuggestedClearingPrice) {
         this.showSuggestedClearingPrice = showSuggestedClearingPrice;
     }
    
    private String marketEngine;
    
    /** The parameters chosen in the periodConfig form are stored in these
     *  lists of GroupPeriodBeans, SubjectPeriodBeans and SecurityPeriodBeans */
    private List groups;
    private List securities;
    private List subjects;
    
    /** Various parameters chosen in the periodMap form */
    private int numSecurities;
    private int numDivisions;
    private int numStates;
    private int id;
    private int openDelay;
    private int periodLength;
    private boolean applyTrigger; 
    
    /** The payoff and bankruptcy function specifications used for this period */
    private List payoffFunctionSpecs;
    private List bankruptcyFunctionSpecs;
    
    private boolean useGui;
    private boolean useEffCashValidation;
    private boolean closebook; 
    private boolean showSuggestedClearingPrice; 
                    

}
