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
 * PayoffFunction.java
 *
 * Created on August 9, 2004, 8:58 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SessionDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.functions.ClientPayoffFunction;

/**
 * This is the base interface for creating payoff functions. To create a payoff function,
 * this must be implemented, and the class must be placed in this package. 
 *
 * @author  Raj Advani, Walter M. Yuan
 * @version $Id: PayoffFunction.java 356 2005-10-29 18:03:35Z raj $
 */
public interface PayoffFunction extends java.io.Serializable {
   
    /**
     * Get the payoff of the given subject in the given period. The session definition object
     * is passed here in case there is any information required there to determine payoffs.
     * The session definition object contains all the configuration information used to set-up
     * this session. Also, an array of Trader objects are passed in, indexed by period number
     * and then subject number. With this Trader array, this payoff function can access the 
     * holdings/cash of prevoius periods, in case this payoff depends on previous period
     * outcomes. 
     *
     * @param subject The subject ID number of the subject whose payoff we want to retrieve
     * @param period  The period ID of the period in which we want to retrieve payoff
     * @param session The session definition object that contains all configuration information
     * @param traders Array indexed by session and subject number that contains Trader information
     *
     * @return The payoff of the given subject in the given period
     */
    public float getPayoff(int subject, int period, SessionDef session, Trader[][] traders);
    
    /**
     * Get the configuration fields, in String form, that are used to configure this payoff
     * function in the web interface. The web interface will display the following fields
     * in the period configuration screen. The fields here must also match the fields used in
     * the setField function.
     *
     * @param periodNum The period this payoff function is used in, as sometimes field names can depend on this
     * @param numSecurities Int array indexed by period ID which contains the number of securities present in
     *                      each period
     * @param numStates Int array indexed by period ID which contains the number of states present in each period
     * @return An array containing the field names to be used
     */
    public String[] getFields(int periodNum, int[] numSecurities, int[] numStates); 
    
    /**
     * Set the value of the given field to the given value. The getFields function returns an array
     * of field names that are displayed in the web configuration screen. When the web configuration
     * screen is submitted, the values that the user populated those fields with are sent sequentially 
     * to this function. The implementing payoff function should store these values, which will probably
     * be used when getPayoff is called. 
     *
     * @param field The field name whose value is to be populated. Should match one of the field names returned
     *              by getFields
     * @param state The state of the field that will be populated. Each field can have a unique value for each
     *              state
     * @param value The value to populate the field with
     */
    public void setField(String field, int state, String value);
    
    /**
     * Return the name of this payoff function. Is displayed by the web configuration screen
     * @return The name of this payoff function
     */
    public String getName();
    
    /**
     *  Return the message the payoff function should send the clients after the end of
     *  the period in which this function is used. If NULL is returned, then the clients will
     *  get the standard payoff message: that is, they will see the result of the getPayoff
     *  function
     *
     * @return The end of period message
     */
    public String getPayoffMask();
           
    public ClientPayoffFunction getClientPayoffFunction();
}
