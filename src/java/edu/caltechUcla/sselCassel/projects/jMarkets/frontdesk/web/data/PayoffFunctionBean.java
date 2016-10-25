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
 * PayoffFunctionBean.java
 *
 * Created on August 9, 2004, 11:05 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data;

import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.*;

/**
 *
 * @author  Raj Advani
 */
public class PayoffFunctionBean implements java.io.Serializable {
    
    /** Creates a new instance of PayoffFunctionBean */
    public PayoffFunctionBean() {
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
    public void populateFields(int periodNum, int[] numSecurities, int[] numStates) {
        try {
            payoffFunction = (PayoffFunction) Class.forName("edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions."+name+"Function").newInstance();
        }catch(Exception e) {
            System.out.println("Failed to instantiate class of name: " + name);
        }
        
        fieldNames = new ArrayList();
        String[] fields = payoffFunction.getFields(periodNum, numSecurities, numStates);
        for (int i=0; i<fields.length; i++)
            fieldNames.add(fields[i]);
        
        fieldValues = new ArrayList();
        for (int i=0; i<fields.length; i++) {
            PayoffSecurityBean secBean = new PayoffSecurityBean();
            secBean.populateFields(numStates[periodNum]);
            
            fieldValues.add(secBean);
        }
    }
    
    /**
     * Getter for property payoffFunction.
     * @return Value of property payoffFunction.
     */
    public edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.PayoffFunction getPayoffFunction() {
        return payoffFunction;
    }
    
    /**
     * Setter for property payoffFunction.
     * @param payoffFunction New value of property payoffFunction.
     */
    public void setPayoffFunction(edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.PayoffFunction payoffFunction) {
        this.payoffFunction = payoffFunction;
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
    
    private PayoffFunction payoffFunction;
    
    private List fieldNames;
    private List fieldValues;
}
