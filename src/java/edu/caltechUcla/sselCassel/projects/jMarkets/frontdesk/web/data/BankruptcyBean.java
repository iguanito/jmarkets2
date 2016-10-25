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
 * BankruptcyBean.java
 *
 * Created on August 9, 2004, 11:05 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data;

import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.*;

/**
 *
 * One bankruptcy bean is associated with each type of bankruptcy function. However, in any period
 * there can be multiple specifications of the same bankruptcy function -- for example, if two 
 * groups each use a GenericBankruptcyFunction with different parameters. This bean, therefore,
 * contains a List of the BankruptcyFunctionBeans, of which there is one per specification of 
 * the bankruptcy function type that is associated with this BankruptcyBean
 *
 * @author  Raj Advani
 */
public class BankruptcyBean implements java.io.Serializable {
    
    /** Creates a new instance of BankruptcyBean */
    public BankruptcyBean() {
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
    
    /** Populate the fields of this bean by instantiating the class represented dynamically and
     *  getting the fields of the function */
    public void populateFields(int numSecurities, int numStates) {
        
    }
   
    /** Getter for property numSpecs.
     * @return Value of property numSpecs.
     *
     */
    public int getNumSpecs() {
        return numSpecs;
    }
    
    /** Setter for property numSpecs.
     * @param numSpecs New value of property numSpecs.
     *
     */
    public void setNumSpecs(int numSpecs) {
        this.numSpecs = numSpecs;
    }
    
    /** The name of the bankruptcy function associated with this bean */
    private String name;
    
    /** The number of specifications that use the bankruptcy function associated with this bean */
    private int numSpecs;
    
    /** A list of the bankruptcy function specifications that use the bankruptcy function associated with this
     *  bean */
    private List bankruptcyFunctionBeans;
}
