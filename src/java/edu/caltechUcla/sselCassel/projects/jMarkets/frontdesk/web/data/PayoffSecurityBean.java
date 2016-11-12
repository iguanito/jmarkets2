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
 * BankruptcySecurityBean.java
 *
 * Created on January 30, 2005, 4:04 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data;

import java.util.*;

/**
 *
 * @author  Raj Advani
 */
public class PayoffSecurityBean implements java.io.Serializable {
    
    /** Creates a new instance of BankruptcySecurityBean */
    public PayoffSecurityBean() {
    }
    
    /** Getter for property stateValues.
     * @return Value of property stateValues.
     *
     */
    public List getStateValues() {
        return stateValues;
    }
    
    /** Setter for property stateValues.
     * @param stateValues New value of property stateValues.
     *
     */
    public void setStateValues(List stateValues) {
        this.stateValues = stateValues;
    }
    
    public void populateFields(int numStates) {
        stateValues = new ArrayList();
        for (int i=0; i<numStates; i++)
            stateValues.add("");
    }
    
    private List stateValues;
}
