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
 * StockBondPayoffFunction.java
 *
 * Created on August 9, 2004, 9:02 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SessionDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.functions.*;

/**
 *
 * @author  Raj Advani
 */
public class StockBondPayoffFunction implements PayoffFunction {
    
    /** Creates a new instance of StockBondPayoffFunction */
    public StockBondPayoffFunction() {
        
    }
    
    public float getPayoff(int subject, int period, SessionDef session, edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader[][] traders) {
        Trader tr = traders[period][subject];
        return (tr.getHoldings(STOCK_HOLDINGS) * stockPayoff) + (tr.getHoldings(BOND_HOLDINGS) * bondPayoff) + tr.getCash()*cashExchangeRate;
    }
    
    public String[] getFields(int periodNum, int[] numSecurities, int[] numStates) {
        String [] fields = {Stock_Payoff,  Bond_Payoff, Cash_ExchangeRate};
        return fields;
    }
    
    public void setField(String field, int state, String value) {
        if(field == null || value == null)
            return;
        
        if (field.equals(Stock_Payoff)){
            try{
                stockPayoff = Integer.parseInt(value);
            }catch(Exception e){
                
            }
        }
        
        if (field.equals(Bond_Payoff)){
            try{
                bondPayoff = Integer.parseInt(value);
            }catch(Exception e){
                
            }
        }
        
        if (field.equals(Cash_ExchangeRate)){
            try{
                cashExchangeRate = Float.parseFloat(value);
            }catch(Exception e){
                
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
    
    
    private int stockPayoff = 0;
    private int bondPayoff = 0;
    private float cashExchangeRate = 1;
    
    private static int STOCK_HOLDINGS = 0;
    private static int BOND_HOLDINGS = 1;
    
    
    public static final String Stock_Payoff = "Period_Stock_Payoff";
    public static final String Bond_Payoff = "Bond_Payoff";
    public static final String Cash_ExchangeRate = "Cash_ExchangeRate";
    
    public static final String Fn_Name = "stock_bond";
}
