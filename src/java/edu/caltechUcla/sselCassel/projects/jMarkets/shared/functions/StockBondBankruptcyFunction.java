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
 * StockBondBankruptcyFunction.java
 *
 * Created on August 9, 2004, 11:21 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.OfferBook;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;

/**
 *
 * @author  Raj Advani
 * @version $Id: StockBondBankruptcyFunction.java 260 2005-08-11 16:15:27Z raj $
 */
public class StockBondBankruptcyFunction implements BankruptcyFunction {
    
    /** Creates a new instance of StockBondBankruptcyFunction */
    public StockBondBankruptcyFunction() {
    }
    
    public boolean validateOffer(AbstractOffer offer, Trader trader, OfferBook offerBook, float bankruptcyCutoff) {
        int[] holdings = trader.getExecHoldings(offerBook).clone();
        float finalCashHoldings = trader.getCash();
        
        int finalStockHoldings = holdings[STOCK_HOLDINGS];
        int finalBondHoldings = holdings[BOND_HOLDINGS];
        
        boolean firstCheck = ((finalStockHoldings * stockPayoffState1) + (finalBondHoldings * bondPayoff) + finalCashHoldings) >= bankruptcyCutoff;
        boolean secondCheck = ((finalStockHoldings * stockPayoffState2) + (finalBondHoldings * bondPayoff) + finalCashHoldings) >= bankruptcyCutoff;
        boolean thirdCheck = ((finalStockHoldings * stockPayoffState3) + (finalBondHoldings * bondPayoff) + finalCashHoldings) >= bankruptcyCutoff;
        
        if (firstCheck && secondCheck && thirdCheck)
            return true;
        else {            
            offer.setInvalidMessage("The proposed order violates the bankruptcy constraint");
            return false;
        }     
    }
    
    public String[] getFields() {
        String[] fields = {Stock_Payoff_State1, Stock_Payoff_State2, Stock_Payoff_State3, Bond_Payoff};
        return fields;
    }
    
    public void setField(String field, int state, String value) {
        if(field == null || value == null)
            return; 
        try{
            if (field.equals(Stock_Payoff_State1))
                stockPayoffState1 = Integer.parseInt(value);
            if (field.equals(Stock_Payoff_State2))
                stockPayoffState2 = Integer.parseInt(value);
            if (field.equals(Stock_Payoff_State3))
                stockPayoffState3 = Integer.parseInt(value);
            
            if (field.equals(Bond_Payoff))
                bondPayoff = Integer.parseInt(value);
        }catch(Exception e){}
    }
    
    public String getName() {
        return "stock_bond";
    }
    
    public String[] getFields(int numOfSecurities, int numOfStates) {
        return getFields(); 
    }
    
    
    private int stockPayoffState1=0;
    private int stockPayoffState2=0;
    private int stockPayoffState3=0;
    private int bondPayoff=0; 
    
    private static int STOCK_HOLDINGS = 0;
    private static int BOND_HOLDINGS = 1;
    
    public static final String Stock_Payoff_State1="Period_Stock_Payoff_State1";
    public static final String Stock_Payoff_State2="Period_Stock_Payoff_State2";
    public static final String Stock_Payoff_State3="Period_Stock_Payoff_State3";
    public static final String Bond_Payoff="Bond_Payoff";
}
