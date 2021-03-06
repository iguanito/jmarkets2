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
 * Created on Oct 19, 2004, 11:21 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.OfferBook;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;

/**
 *
 * @author  Walter M. Yuan
 * @version $Id: GenericBankruptcyFunction.java 260 2005-08-11 16:15:27Z raj $
 */
public class GenericBankruptcyFunction implements BankruptcyFunction {
    
    /** Creates a new instance of StockBondBankruptcyFunction */
    public GenericBankruptcyFunction() {
    }
    
    public boolean validateOffer(AbstractOffer offer, Trader trader, OfferBook offerBook, float bankruptcyCutoff) {
        int[] finalSecurityHoldings = trader.getExecHoldings(offerBook).clone();
        float finalCashHoldings = trader.getExecCash(offerBook);
        
        //System.out.println("Bankruptcy validation: Pre-offer security holdings: " + finalSecurityHoldings[offer.getMarketId()] + " and cash holdings: " + finalCashHoldings);
        
        finalSecurityHoldings[offer.getMarketId()] += offer.getExecSecurityChange(offerBook);
        finalCashHoldings += offer.getExecCashChange(offerBook);
        
        System.out.println("Validating bankruptcy constraint with final security holdings: " + finalSecurityHoldings[offer.getMarketId()] + " and final cash holdings: " + finalCashHoldings);
        
        boolean[] stateChecks = new boolean[numOfStates];
        
        for(int j=0; j<stateChecks.length; j++) {
            float payoff = 0;
            
            for(int i=0; i<finalSecurityHoldings.length; i++)
                payoff += finalSecurityHoldings[i] * securityPayoffs[i][j];
            
            payoff += finalCashHoldings * securityPayoffs[securityPayoffs.length-1][j];
            stateChecks[j] = payoff >= bankruptcyCutoff;
        }
        
        for(int i=0; i<stateChecks.length; i++){
            if(!stateChecks[i])   {
                offer.setInvalidMessage("The proposed order violates the bankruptcy constraint");
                return false;
            }
        }
        
        return true;
    }
    
    public String[] getFields() {
        return null;
    }
    
    public String[] getFields(int numOfSecurities, int numOfStates) {
        this.numOfSecurities = numOfSecurities;
        this.numOfStates = numOfStates;
        
        fields = new String[numOfSecurities + 1];
        for (int i=0; i<numOfSecurities; i++)
            fields[i] = "Security " + i;
        
        fields[numOfSecurities] = "Cash";
        return fields;
    }
    
    public void setField(String field, int state, String value) {
        if(field == null || value == null)
            return;
        
        if (securityPayoffs == null)
            securityPayoffs = new float[numOfSecurities + 1][numOfStates];
        
        if(fields != null){
            for(int i=0; i<fields.length; i++){
                if(field.equals(fields[i])){
                    try{
                        securityPayoffs[i][state] = Float.parseFloat(value);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public String getName() {
        return Fn_Name;
    }
    
    private float[][] securityPayoffs;
    private String[] fields;
    
    private int numOfSecurities;
    private int numOfStates;
    
    public static final String Security_Payoff = "Period_Security_Payoff";
    
    public static final String Fn_Name = "Generic_Market";
}
