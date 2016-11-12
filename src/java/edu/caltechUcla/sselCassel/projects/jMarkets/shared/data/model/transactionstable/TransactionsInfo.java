/*
 * TransactionsInfo.java
 *
 * Created on November 1, 2004, 6:06 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.transactionstable;

import java.util.*;
//import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.transactionstable.TransactionsRow;

/**
 *
 *
 * @author  Christopher Crabbe, ccrabbe@princeton.edu
 */
public class TransactionsInfo implements java.io.Serializable {
    
    /** Creates a new instance of OrdersInfo */
    public TransactionsInfo() {
        orders = new TreeMap();
    }
    
    /** Return an array of OrdersRow objects, which graphically contain all the
     *  information about what this client has currently ordered. Return the information
     *  first ordered by security, then by price */
    public TransactionsRow[] getTransactionsInTimeOrder() {
        Vector list = new Vector();
        
        Set securities = orders.keySet();
        Iterator it = securities.iterator();
        
        while (it.hasNext()) {
            String security = (String) it.next();
            TreeMap priceMap = (TreeMap) orders.get(security);
            
            Set keys = priceMap.keySet();
            Iterator pit = keys.iterator();
            Map <Double, String> sortedKeys = new HashMap<Double, String>();
            List <Double> prices = new ArrayList<Double>();
            float j=0;
            while(pit.hasNext()){
                String key = (String)pit.next();
                StringTokenizer st = new StringTokenizer(key, "-");
                double price = Double.parseDouble(st.nextToken()) + j*0.0001;
                sortedKeys.put(price, (String)key);
                prices.add(price);
                j+=1;
            }
            
            Collections.sort(prices);
            
            for(int i=prices.size()-1; i>=0; i--){
                TransactionsRow row = (TransactionsRow) priceMap.get(sortedKeys.get(prices.get(i)));
                list.add(row);
            }
        }

        TransactionsRow[] orow = new TransactionsRow[list.size()];
        for (int i=0; i<orow.length; i++)
            orow[i] = (TransactionsRow) list.get(i);
        
        return orow;
    }
    
    /** Return an array of OrdersRow objects, which graphically contain all the
     *  information about what this client has currently ordered. Return the information
     *  first ordered by security, then by price */
    public TransactionsRow[] getTransactionsInTimeOrder(String security) {
        Vector list = new Vector();
        
        TreeMap priceMap = (TreeMap) orders.get(security);
        if(priceMap == null || priceMap.size()==0)
            return new TransactionsRow[0];;
            
            Set prices = priceMap.keySet();
            Iterator pit = prices.iterator();
            
            Map <Double, String> sortedKeys = new HashMap<Double, String>();
            List <Double> sortedPrices = new ArrayList<Double>();
            float j=0;
            while(pit.hasNext()){
                String key = (String)pit.next();
                StringTokenizer st = new StringTokenizer(key, "-");
                double price = Double.parseDouble(st.nextToken()) + j*0.0001;
                sortedKeys.put(price, (String)key);
                sortedPrices.add(price);
                j+=1;
            }
            
            Collections.sort(sortedPrices);
            
            for(int i=prices.size()-1; i>=0; i--){
                TransactionsRow row = (TransactionsRow) priceMap.get(sortedKeys.get(sortedPrices.get(i)));
                list.add(row);
            }
            
            
            TransactionsRow[] orow = new TransactionsRow[list.size()];
            for (int i=0; i<orow.length; i++)
                orow[i] = (TransactionsRow) list.get(i);
            
            return orow;
    }

    /** Add the information for the given transaction to the hashtable. If the
     *  units for selling and buying are both zero, then just remove the information
     *  for this order from the hashtable */
    public void updateTransaction(String action, String name, float txnPrice, float stdPrice, int unitsTraded, long time, boolean owned) {
        TreeMap security = (TreeMap) orders.get(name);
        if (security == null)
            security = new TreeMap();
        orders.put(name, security);
        
        String key = String.valueOf(stdPrice)+ "-" + name + "-" + action + "-" + String.valueOf(owned) + "-" + String.valueOf(time); 
        
        if( security.containsKey(key) ){
            TransactionsRow row =(TransactionsRow)security.get(key);
            row.units+=unitsTraded;
            security.put(key, row);
        } else {
            TransactionsRow row = new TransactionsRow();
            row.action = action;
            row.transactedPrice = txnPrice;
            //row.standingPrice = stdPrice;
            row.units = unitsTraded;
            row.security = name;
            row.owned = owned;
            security.put(key, row);
        }
    }
    
    public Iterator<String> getSecurityNames(){
        return orders.keySet().iterator(); 
    }
    
    
    /** A hashtable keyed by security name that contains inner hashtables that are keyed by
     *  price level. Within the inner hashtables there are units arrays that describe the
     *  player's current order on this price level */
    private TreeMap orders;
}