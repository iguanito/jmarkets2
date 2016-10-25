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
 * BankruptcyFunctionBean.java
 *
 * Created on August 9, 2004, 11:05 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data;

import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  Raj Advani
 */
public class BankruptcyFunctionBean implements java.io.Serializable {
    
    private static Log log = LogFactory.getLog(BankruptcyFunctionBean.class);
    
    /** Creates a new instance of BankruptcyFunctionBean */
    public BankruptcyFunctionBean() {
    }
    
    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public java.lang.String getName() {
        return name;
    }
    
    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    /**
     * Getter for property fieldNames.
     * @return Value of property fieldNames.
     */
    public List getFieldNames() {
        return fieldNames;
    }
    
    /**
     * Setter for property fieldNames.
     * @param fieldNames New value of property fieldNames.
     */
    public void setFieldNames(List fieldNames) {
        this.fieldNames = fieldNames;
    }
    
    /**
     * Getter for property fieldValues.
     * @return Value of property fieldValues.
     */
    public List getFieldValues() {
        return fieldValues;
    }
    
    /**
     * Setter for property fieldValues.
     * @param fieldValues New value of property fieldValues.
     */
    public void setFieldValues(List fieldValues) {
        this.fieldValues = fieldValues;
    }
    
    /** Populate the fields of this bean by instantiating the class represented dynamically and
     *  getting the fields of the function */
    public void populateFields(int numSecurities, int numStates) {
        try {
            bankruptcyFunction = (BankruptcyFunction) Class.forName("edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions."+name+"Function").newInstance();
        }catch(Exception e) {
            System.out.println("Failed to instantiate class of name: " + name);
        }
        
        fieldNames = new ArrayList();
        String[] fields = bankruptcyFunction.getFields(numSecurities, numStates);
        for (int i=0; i<fields.length; i++)
            fieldNames.add(fields[i]);
        
        fieldValues = new ArrayList();
        for (int i=0; i<fields.length; i++) {
            BankruptcySecurityBean secBean = new BankruptcySecurityBean();
            secBean.populateFields(numStates);
            
            fieldValues.add(secBean);
        }
    }
    
    /**
     * Getter for property bankruptcyFunction.
     * @return Value of property bankruptcyFunction.
     */
    public edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.BankruptcyFunction getBankruptcyFunction() {
        return bankruptcyFunction;
    }
    
    /**
     * Setter for property bankruptcyFunction.
     * @param bankruptcyFunction New value of property bankruptcyFunction.
     */
    public void setBankruptcyFunction(edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.BankruptcyFunction bankruptcyFunction) {
        this.bankruptcyFunction = bankruptcyFunction;
    }
    
    /** Getter for property specName.
     * @return Value of property specName.
     *
     */
    public String getSpecName() {
        return specName;
    }
    
    /** Setter for property specName.
     * @param specName New value of property specName.
     *
     */
    public void setSpecName(String specName) {
        this.specName = specName;
    }
    
    /** The actual name of the function used */
    private String name;
    
    /** The name of this specification of this function. This the name displayed on the web forms */
    private String specName;
    
    private BankruptcyFunction bankruptcyFunction;
    
    private List fieldNames;
    private List fieldValues;
    
    
}
