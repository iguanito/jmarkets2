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
 * TestClient.java
 *
 * Created on October 20, 2004, 5:21 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.control;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import java.util.Random;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.MarketDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.offers.BasicOffer;

/**
 *
 * @author  Raj 
 */
public class TestClient {
    
    /** Creates a new instance of TestClient */
    public TestClient(Client client) {
        this.client = client;
        this.stop = true;
        this.rand = new Random();
    }
    
    /** Generate a random order and output it. Return true if the offer is valid */
    private boolean generateRandomOrder() {
        int action = JMConstants.SELL_ACTION;
        if (client.getId() % 2 == 0)
            action = JMConstants.BUY_ACTION;
        
        int marketId = rand.nextInt(minfo.getNumMarkets());
        
        int priceId = rand.nextInt(minfo.getNumDivisions() / 2);
        if (client.getId() % 2 == 0)
            priceId += minfo.getNumDivisions() / 2;
        
        int units = rand.nextInt(4) + 1;
        
        return outputTransaction(action, marketId, priceId, units);
    }
    
    /** Tell the server that a bid/sell has been made at this price level for the
     *  given amount of units. Return true if the offer is valid */
    private boolean outputTransaction(int action, int marketId, int priceId, int units) {
        if (stop) {
            System.out.println(client.getId() + ": stop detected, test client abandoning order");
            return false;
        }
        
        BasicOffer offer = new BasicOffer();
        offer.setAction(action);
        offer.setUnits(units);
        offer.setMarketId(marketId);
        offer.setPriceId(priceId);
        offer.setPrice(minfo.getPriceWithNormalId(marketId, priceId));
        
        System.out.println(client.getId() + ": test client is ordering action " + action + " market " + marketId + " price " + priceId + " and units " + units);
        return client.outputTransaction(offer);
    }
    
    public synchronized void initPause() {
        try {
            wait(10000);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /** Tells the TestClient to start making random offers */
    public void startTestMode() {
        initPause();
        System.out.println(client.getId() + ": test client is starting random order generation");
        stop = false;
        
        Runnable test = new Runnable() {
            public void run() {
                while (!stop) {
                    try {
                        if (generateRandomOrder()) {
                            //System.out.println(client.getId() + ": random order was valid, pausing before moving to next order");
                            pause();
                            //System.out.println(client.getId() + ": pause complete, generating new order...");
                        }
                        else {
                            System.out.println(client.getId() + ": random order invalid, moving to next order immediately");
                            pause();
                        }
                    }catch(Exception e) {
                        System.out.println("Error in test-client loop, going back to top");
                        continue;
                    }
                }
            }
            
            private synchronized void pause() {
                try {
                    //System.out.println(client.getId() + ": entered synchronized pause function");
                    int pauseTime = rand.nextInt(5) + 1;
                    //System.out.println(client.getId() + ": generated pause time of " + pauseTime);
                    wait(pauseTime);
                    //System.out.println(client.getId() + ": pause complete");
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        Thread thr = new Thread(test);
        thr.start();
    }
    
    /** Stop making random offers */
    public void stopTestMode() {
        System.out.println(client.getId() + ": Test client is ceasing random order generation");
        stop = true;
    }
    
    
    /**
     * Getter for property minfo.
     * @return Value of property minfo.
     */
    public MarketDef getMarketInfo() {
        return minfo;
    }
    
    /**
     * Setter for property minfo.
     * @param minfo New value of property minfo.
     */
    public void setMarketInfo(MarketDef minfo) {
        this.minfo = minfo;
    }
    
    private boolean stop;
    private Random rand;
    
    private Client client;
    private MarketDef minfo;
}
