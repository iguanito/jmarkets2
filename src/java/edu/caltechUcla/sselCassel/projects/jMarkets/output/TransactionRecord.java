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
 * TransactionRecord.java
 *
 * Created on May 18, 2006, 1:30 PM
 *
 * 
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.output;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <a href="mailto:wmyuan@hss.caltech.edu">Walter Yuan</a>
 */
public class TransactionRecord {
    
    private int id; 
    private long entryTime; 
    private String marketType; 
    private float price; 
    
    private List <OrderRecord> orders; 
    
    /** Creates a new instance of TransactionRecord */
    public TransactionRecord() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(long entryTime) {
        this.entryTime = entryTime;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public List<OrderRecord> getOrders() {
        return orders;
    }
    
    public void addOrder(OrderRecord order){
        if(orders == null)
            orders = new ArrayList <OrderRecord>();
        
        orders.add(order); 
    }

    public void setOrders(List<OrderRecord> orders) {
        this.orders = orders;
    }
    
}
