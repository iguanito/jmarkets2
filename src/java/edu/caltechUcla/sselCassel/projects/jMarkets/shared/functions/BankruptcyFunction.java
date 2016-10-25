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
 * BankruptcyFunction.java
 *
 * Created on August 9, 2004, 8:58 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.OfferBook;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.AbstractOffer;

/**
 *
 * @author  Raj Advani
 */
public interface BankruptcyFunction extends java.io.Serializable {
    
    public boolean validateOffer(AbstractOffer offer, Trader trader, OfferBook offerBook, float bankruptcyCutoff);
    
    public String[] getFields(int numSecurities, int numStates);
     
    public void setField(String field, int state, String value);
    
    public String getName();
}
