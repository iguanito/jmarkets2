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
 * TestPayoff.java
 *
 * Created on August 10, 2004, 5:49 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SessionDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.functions.*;

/**
 * This payoff function simply returns 0 for all calls to getPayoff, regardless
 * of holdings, cash, or any other information. This is useful for practice periods,
 * etc.
 *
 * @author  Rajeev Advani
 */
public class ZeroPayoffFunction implements PayoffFunction {
    
    /** Creates a new instance of TestPayoff */
    public ZeroPayoffFunction() {
    }
    
    public String[] getFields() {
        String[] test = {"Blank Field (Do not enter anything here)"};
        return test;
    }
    
    public String getName() {
        return "Zero Payoff";
    }
    
    public float getPayoff(int subject, int period, SessionDef session, Trader[][] traders) {
        return 0;
    }
    
    public void setField(String field, int state, String value) {
        
    }
    
    public String[] getFields(int periodNum, int[] numOfSecurities, int[] numOfStates) {
        return getFields();
    }
    
    public String getPayoffMask() {
        return null;
    }
    
    public ClientPayoffFunction getClientPayoffFunction(){
        return new GenericClientPayoffFunction();
    }
    
}
