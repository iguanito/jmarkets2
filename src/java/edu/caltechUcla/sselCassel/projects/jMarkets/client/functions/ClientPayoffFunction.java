/**
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

/**
 *  A clientside payoff-function derivative which allows for calculation of effective cash constraint
 * 
 */
package edu.caltechUcla.sselCassel.projects.jMarkets.client.functions;

/**
 *
 * @author ccrabbe
 */
public interface ClientPayoffFunction extends java.io.Serializable{
    
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
    public float[][] getPotentialPayoffs(int period);
    

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
    
    public void setNumStates(int ns);
    public void setNumSecurities(int ns);
    public int getNumStates();
    public int getNumSecurities();
    
    /**
     * Return the name of this payoff function. Is displayed by the web configuration screen
     * @return The name of this payoff function
     */
    public String getName();
}
