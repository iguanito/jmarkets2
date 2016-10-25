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
 * PeriodConfigForm.java
 *
 * Created on August 6, 2004, 11:20 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.forms;

import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.*;
import org.apache.struts.action.*;
import javax.servlet.http.*;
import java.util.List;

/**
 *
 * @author  Raj Advani
 */
public class PeriodConfigForm extends ActionForm {
    
    /** Creates a new instance of PeriodConfigForm */
    public PeriodConfigForm() {
        
    }
    
    /** Reset the checkbox fields, as required by struts specification */
    public void reset(ActionMapping mapping, HttpServletRequest req) {
        if (security != null) {
            for (int i=0; i<security.size(); i++) {
                SecurityPeriodBean bean = (SecurityPeriodBean) security.get(i);
                bean.setBuyPriveleges(new boolean[bean.getBuyPriveleges().length]);
                bean.setSellPriveleges(new boolean[bean.getSellPriveleges().length]);
                bean.setAddDividend(new boolean[bean.getAddDividend().length]);
                bean.setAddSurplus(new boolean[bean.getAddSurplus().length]);
            }
        }
        
        if (group != null) {
            for (int i=0; i<group.size(); i++) {
                GroupPeriodBean bean = (GroupPeriodBean) group.get(i);
                bean.setAddCash(false);
            }
        }
    }
    
    /**
     * Getter for property firstPeriod.
     * @return Value of property firstPeriod.
     */
    public java.lang.String getFirstPeriod() {
        return firstPeriod;
    }
    
    /**
     * Setter for property firstPeriod.
     * @param firstPeriod New value of property firstPeriod.
     */
    public void setFirstPeriod(java.lang.String firstPeriod) {
        this.firstPeriod = firstPeriod;
    }
    
    /**
     * Getter for property group.
     * @return Value of property group.
     */
    public java.util.List getGroup() {
        return group;
    }
    
    /**
     * Setter for property group.
     * @param group New value of property group.
     */
    public void setGroup(java.util.List group) {
        this.group = group;
    }
    
    /**
     * Getter for property groupNames.
     * @return Value of property groupNames.
     */
    public java.util.List getGroupNames() {
        return groupNames;
    }
    
    /**
     * Setter for property groupNames.
     * @param groupNames New value of property groupNames.
     */
    public void setGroupNames(java.util.List groupNames) {
        this.groupNames = groupNames;
    }
    
    /**
     * Getter for property lastPeriod.
     * @return Value of property lastPeriod.
     */
    public java.lang.String getLastPeriod() {
        return lastPeriod;
    }
    
    /**
     * Setter for property lastPeriod.
     * @param lastPeriod New value of property lastPeriod.
     */
    public void setLastPeriod(java.lang.String lastPeriod) {
        this.lastPeriod = lastPeriod;
    }
    
    /**
     * Getter for property period.
     * @return Value of property period.
     */
    public java.util.List getPeriod() {
        return period;
    }
    
    /**
     * Setter for property period.
     * @param period New value of property period.
     */
    public void setPeriod(java.util.List period) {
        this.period = period;
    }
    
    /**
     * Getter for property security.
     * @return Value of property security.
     */
    public java.util.List getSecurity() {
        return security;
    }
    
    /**
     * Setter for property security.
     * @param security New value of property security.
     */
    public void setSecurity(java.util.List security) {
        this.security = security;
    }
    
    /**
     * Getter for property subject.
     * @return Value of property subject.
     */
    public java.util.List getSubject() {
        return subject;
    }
    
    /**
     * Setter for property subject.
     * @param subject New value of property subject.
     */
    public void setSubject(java.util.List subject) {
        this.subject = subject;
    }
    
    /**
     * Getter for property copyFrom.
     * @return Value of property copyFrom.
     */
    public int getCopyFrom() {
        return copyFrom;
    }
    
    /**
     * Setter for property copyFrom.
     * @param copyFrom New value of property copyFrom.
     */
    public void setCopyFrom(int copyFrom) {
        this.copyFrom = copyFrom;
    }
    
    /** Gets the real save path: ex). d:\dev\jMarkets\session.jsm */
    public String getSavePath() {
        return getServlet().getServletContext().getRealPath(getPath());
    }
    
    /** Gets the relative save path used by the jsp pages: ex) http://localhost:8080/jMarkets/session.jsm */
    public String getRelativeSavePath() {
        return relativeSavePath;
    }
    
    public void setRelativeSavePath(String relativeSavePath) {
        this.relativeSavePath = relativeSavePath;
    }
    
    /** Gets the strict save path set by the Action classes: ex) /session.jsm */
    public java.lang.String getPath() {
        return path;
    }
    
    public void setPath(java.lang.String path) {
        this.path = path;
        this.relativeSavePath = "/jMarkets2" + path;
    }
    
    /**
     * Getter for property payoffFunctions.
     * @return Value of property payoffFunctions.
     */
    public java.util.List getPayoffFunctions() {
        return payoffFunctions;
    }
    
    /**
     * Setter for property payoffFunctions.
     * @param payoffFunctions New value of property payoffFunctions.
     */
    public void setPayoffFunctions(java.util.List payoffFunctions) {
        this.payoffFunctions = payoffFunctions;
    }
    
    /**
     * Getter for property bankruptcyFunctions.
     * @return Value of property bankruptcyFunctions.
     */
    public java.util.List getBankruptcyFunctions() {
        return bankruptcyFunctions;
    }
    
    /**
     * Setter for property bankruptcyFunctions.
     * @param bankruptcyFunctions New value of property bankruptcyFunctions.
     */
    public void setBankruptcyFunctions(java.util.List bankruptcyFunctions) {
        this.bankruptcyFunctions = bankruptcyFunctions;
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
    
    public void setUseGui(boolean useGui){
        this.useGui = useGui;
    }
    
    public void setUseEffCashValidation(boolean useEffCashValidation){
        this.useEffCashValidation = useEffCashValidation;
    }
    
    public boolean getUseGui(){
        return useGui;
    }
    
    public boolean getUseEffCashValidation(){
        return useEffCashValidation;
    }
    
    /*public void setForceBestPrice(boolean forceBestPrice ){
        this.forceBestPrice = forceBestPrice;
    }
    
    public boolean getForceBestPrice(){
        return forceBestPrice;
    }*/
    
    private int copyFrom;
    
    /** List of PeriodBean objects, which will contain all the period data (including what group was
     *  selected for each subject, the configurations of each security, etc.) */
    private List period;
    
    /** List of SecurityPeriodBeans (which are linked directly into each PeriodBean object) */
    private List security;
    
    /** The number of states in the period */
    private int numStates;
    
    /** List of SubjectPeriodBeans (linked directly into the PeriodBean objects) */
    private List subject;
    
    /** A list of all the groups available (integers) */
    private List group;
    
    /** A list of String names of each of the groups available */
    private List groupNames;
    
    /** Parameters used to determine whether to disable the 'next' and 'previous' period buttons */
    private String firstPeriod;
    private String lastPeriod;
    
    /** Parameters used for saving the SessionBean object */
    private String path;
    private String relativeSavePath;
    
    /** Lists of all the payoff and bankruptcy functions available -- used in periodMap so that
     *  users can choose the number of specifications they want of each payoff/bankruptcy function */
    private List payoffFunctions;
    private List bankruptcyFunctions;
    
    /** List of function beans used in the period config forms -- the beans corresponding to the
     *  actual specifications of the payoff functions */
    private List payoffFunctionSpecs;
    private List bankruptcyFunctionSpecs;
    
    private boolean useGui;
    private boolean useEffCashValidation;
    
}
