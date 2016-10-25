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
 * EarningsInfo.java
 *
 * Created on November 1, 2004, 6:06 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.orderstable;

import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;

/**
 *
 *
 * @author  Raj Advani, Walter M. Yuan
 */
public class OrdersInfo implements java.io.Serializable {
    
    /** Creates a new instance of OrdersInfo */
    public OrdersInfo() {
        orders = new TreeMap();
    }
    
    /** Return an array of OrdersRow objects, which graphically contain all the
     *  information about what this client has currently ordered. Return the information
     *  first ordered by security, then by price */
    public OrdersRow[] getOrdersInSecurityPriceOrder(boolean own) {
        Vector list = new Vector();
        
        Set securities = orders.keySet();
        Iterator it = securities.iterator();
        
        while (it.hasNext()) {
            String security = (String) it.next();
            TreeMap priceMap = (TreeMap) orders.get(security);
            
            Set prices = priceMap.keySet();
            Iterator pit = prices.iterator();
            
            while (pit.hasNext()) {
                float price = ((Float) pit.next()).floatValue();
                int[] units = (int[]) priceMap.get(new Float(price));
                OrdersRow row = null;
                String action = "Buy Order";
                int u = units[2];
                if (units[3] != 0) {
                    action = "Sell Order";
                    u = units[3];
                }
                if(u>0){
                    row = new OrdersRow();
                    row.security = security;
                    row.price = price;
                    row.action = action;
                    row.units = u;
                    row.own = true;
                    
                    list.add(row);
                }
                if(!own){
                    action = "Buy Order";
                    u = units[0] - units[2];
                    if(u >0){
                        row = new OrdersRow();
                        row.security = security;
                        row.price = price;
                        row.action = action;
                        row.units = u;
                        row.own = false;
                        list.add(row);
                    }
                    
                    
                    action = "Sell Order";
                    u = units[1]-units[3];
                    if(u>0){
                        row = new OrdersRow();
                        row.security = security;
                        row.price = price;
                        row.action = action;
                        row.units = u;
                        row.own = false;
                        list.add(row);
                    }
                }
            }
        }
        
        OrdersRow[] orow = new OrdersRow[list.size()];
        int j=0; 
        for (int i=orow.length-1; i>=0; i--){
            orow[j] = (OrdersRow) list.get(i);
            j++; 
        }
        
        return orow;
    }
    
    public OrdersRow[] getOrdersInPriceOrder(String security, boolean own) {
        Vector list = new Vector();
        
        TreeMap priceMap = (TreeMap) orders.get(security);
        if(priceMap == null || priceMap.size()==0)
            return new OrdersRow[0];; 
        
        Set prices = priceMap.keySet();
        Iterator pit = prices.iterator();
        
        while (pit.hasNext()) {
            float price = ((Float) pit.next()).floatValue();
            int[] units = (int[]) priceMap.get(new Float(price));
            OrdersRow row = null;
            String action = "Buy Order";
            int u = units[2];
            if (units[3] != 0) {
                action = "Sell Order";
                u = units[3];
            }
            if(u>0){
                row = new OrdersRow();
                row.security = security;
                row.price = price;
                row.action = action;
                row.units = u;
                row.own = true;
                
                list.add(row);
            }
            if(!own){
                action = "Buy Order";
                u = units[0] - units[2];
                if(u >0){
                    row = new OrdersRow();
                    row.security = security;
                    row.price = price;
                    row.action = action;
                    row.units = u;
                    row.own = false;
                    list.add(row);
                }
                
                
                action = "Sell Order";
                u = units[1]-units[3];
                if(u>0){
                    row = new OrdersRow();
                    row.security = security;
                    row.price = price;
                    row.action = action;
                    row.units = u;
                    row.own = false;
                    list.add(row);
                }
            }
        }
        
        OrdersRow[] orow = new OrdersRow[list.size()];
        int j=0;
        for (int i=orow.length-1; i>=0; i--){
            orow[j] = (OrdersRow) list.get(i);
            j++;
        }
        
        return orow;
    }
    
    /** Add the information for the given order to the hashtable. If the
     *  units for selling and buying are both zero, then just remove the information
     *  for this order from the hashtable */
    public void updateOrder(String name, float price, int[] units) {
        TreeMap security = (TreeMap) orders.get(name);
        if (security == null)
            security = new TreeMap();
        orders.put(name, security);
        
        if (isNoAction(units))
            security.remove(new Float(price));     
        else
            security.put(new Float(price), units);
    }
    
    public Iterator<String> getSecurityNames(){
        return orders.keySet().iterator(); 
    }
    
    private boolean isNoAction(int [] units){
        return units[0] == 0 && units[1] == 0 && units[2] ==0 && units[3] ==0 ;
    }
    
    
    /** A hashtable keyed by security name that contains inner hashtables that are keyed by
     *  price level. Within the inner hashtables there are units arrays that describe the
     *  player's current order on this price level */
    private TreeMap orders;
}