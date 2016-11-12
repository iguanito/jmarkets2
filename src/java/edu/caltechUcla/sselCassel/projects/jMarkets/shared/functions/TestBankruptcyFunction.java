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
 * TestBankruptcyFunction.java
 *
 * Created on August 10, 2004, 10:01 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.OfferBook;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;

/**
 *
 * @author  Raj Advani
 */
public class TestBankruptcyFunction implements BankruptcyFunction {
    
    /** Creates a new instance of TestBankruptcyFunction */
    public TestBankruptcyFunction() {
    }
    
    public String[] getFields() {
        String[] fields = {"A", "B"};
        return fields;
    }
    
    
    
    public String getName() {
        return "test_fcn";
    }
    
    public void setField(String field, int state, String value) {
        if (field.equals("A"))
            a = Integer.parseInt(value);
        if (field.equals("B"))
            b = Integer.parseInt(value);
    }
    
    public boolean validateOffer(AbstractOffer offer, Trader trader, OfferBook offerBook, float bankruptcyCutoff) {
        /*
        for (int i=0; i<holdings.length; i++) {
            System.out.println("Holdings of " + i + ": " + holdings[i]);
            System.out.println("Exec Holdings of " + i + ": " + holdingsIfExecuted[i]);
        }
        System.out.println("Cash: " + cash);
        System.out.println("Exec Cash " + cashIfExecuted);
        System.out.println("Cutoff: " + bankruptcyCutoff);
        
        if (holdingsIfExecuted[1] >= 3) {
            offer.setInvalidMessage("Test bankruptcy function test invalidated this offer");
            return false;
        }*/
        
        return true;
    }
    
     public String[] getFields(int numOfSecurities, int numOfStates) {
        return getFields(); 
    }
    
    
    private int a, b;
}
