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
 * GenericPayoffFunction.java
 *
 * Created on October 19, 2004, 9:02 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SessionDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.functions.*;

/**
 *
 * @author  Walter M. Yuan
 * @version $Id: GenericPayoffFunction.java 272 2005-08-18 01:26:01Z raj $
 */
public class NaturalLogPayoffFunction implements PayoffFunction {
    
    /** Creates a new instance of StockBondPayoffFunction */
    public NaturalLogPayoffFunction() {
        
    }
    
    public float getPayoff(int subject, int period, SessionDef session, edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader[][] traders) {
        Trader tr = traders[period][subject];
        float cash = tr.getCash();
        int[] holdings = tr.getHoldings();
        
        float cashExchange = securityPayoffs[securityPayoffs.length-1];
        float b = securityPayoffs[securityPayoffs.length-2];
        float a = securityPayoffs[securityPayoffs.length-3];
        
        float totalPayoff = cash * cashExchange;
        
        //System.out.println("Cash: " + cash + ", Weight: " + cashWeight);
        
        if(holdings == null)
            return totalPayoff;
        
        for(int i=0; i<holdings.length; i++){
            totalPayoff +=  holdings[i]*securityPayoffs[i];
            //System.out.println("Security " + i + ": " + holdings[i] + ", Weight: " + securityPayoffs[i]);
        }
        
        return (float)(a*Math.log(b*totalPayoff));
    }
    
    public String[] getFields(int periodNum, int[] numSecurities, int[] numStates) {
        int numOfSecurities = numSecurities[periodNum];
        int numOfStates = numStates[periodNum];
        
        securityPayoffs = new float[numOfSecurities + 3];
        
        fields = new String[numOfSecurities + 3];
        for(int i=0; i<numOfSecurities; i++){
            fields[i] = Security_Payoff + "_" + i;
        }
        
        fields[numOfSecurities] = ParamA;
        fields[numOfSecurities+1] = ParamB;
        fields[numOfSecurities+2] = Cash_ExchangeRate;
        return fields;
    }
    
    
    public void setField(String field, int state, String value) {
        if(field == null || value == null || state > 0)
            return;
        
        if(fields != null){
            for(int i=0; i<fields.length; i++){
                if(field.equals(fields[i])){
                    try{
                        securityPayoffs[i] = Float.parseFloat(value);
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public String getName() {
        return Fn_Name;
    }
    
    public String getPayoffMask() {
        return null;
    }
    
    public ClientPayoffFunction getClientPayoffFunction(){
        return new GenericClientPayoffFunction();
    }
    
    
    private float [] securityPayoffs = null;
    
    private String [] fields = null;
    
    public static final String ParamA = "Param_A";
    public static final String ParamB = "Param_B";
    
    public static final String Security_Payoff = "Period_Security_Payoff";
    public static final String Cash_ExchangeRate = "Cash_ExchangeRate";
    
    public static final String Fn_Name = "Natural Log";
}
