/*
 * GenericClientPayoffFunction.java
 *
 * Created on September 7, 2006, 2:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.functions;

import java.util.Hashtable;

/**
 *
 * @author ccrabbe
 */
public class GenericClientPayoffFunction implements ClientPayoffFunction {
    
    /** Creates a new instance of GenericClientPayoffFunction */
    public GenericClientPayoffFunction() {
                
    }
    
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
    public float[][] getPotentialPayoffs(int period){
        float[][] pp = new float[numStates][numSecurities];
        for( int i=0; i<pp.length; i++ ){
            for( int j=0; j<pp[i].length; j++ ){
                pp[i][j] = Float.parseFloat((String)fields[i].get(Security_Payoff+"_"+j));
            }
        }
        return pp;
    }

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
    public void setField(String field, int state, String value){
        if( fields == null ){
            fields = new Hashtable[numStates];
            for( int i=0; i<numStates; i++ ){
                fields[i] = new Hashtable();
            }
        }
        fields[state].put(field, value);
    }           
    
    public void setNumStates(int ns){
        numStates = ns;        
    }
    
    public void setNumSecurities(int ns){
        numSecurities = ns;
    }
    
    public int getNumStates(){
        return numStates;
    }
    
    public int getNumSecurities(){
        return numSecurities;
    }
    
    /**
     * Return the name of this payoff function. Is displayed by the web configuration screen
     * @return The name of this payoff function
     */
    public String getName(){
        return "Generic";
    }
    
    private Hashtable[] fields;
    private int numStates=-2;
    private int numSecurities;
    public static final String Security_Payoff = "Period_Security_Payoff"; 
    public static final String Cash_ExchangeRate = "Cash_ExchangeRate";  
}
    

