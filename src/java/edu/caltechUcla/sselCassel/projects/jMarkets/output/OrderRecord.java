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
 * OrderHistory.java
 *
 * Created on August 12, 2005, 11:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.output;

import java.util.Comparator;

/**
 *
 * @author Walter M. Yuan
 */
public class OrderRecord {
    
    private int periodId; 
    private int orderId; 
    private long entryTime; 
    private int subjectId; 
    private String security; 
    private String entryType; 
    private String orderType;
    private String orderStatus; 
    private float price; 
    private int orderSize; 
    
    
    /** Creates a new instance of OrderHistory */
    public OrderRecord() {
    }

    public int getPeriodId() {
        return periodId;
    }

    public void setPeriodId(int periodId) {
        this.periodId = periodId;
    }

    public long getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(long entryTime) {
        this.entryTime = entryTime;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getOrderSize() {
        return orderSize;
    }

    public void setOrderSize(int orderSize) {
        this.orderSize = orderSize;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public static final OrderRecordComparator COMPARATOR = new OrderRecordComparator();
    
    /**
     * Compare two orders by time entry. 
     * @author  Walter M. Yuan
     */
    private static class OrderRecordComparator implements Comparator<OrderRecord> {
        public int compare(OrderRecord o1, OrderRecord o2) {
            if(o1.getPeriodId() != o2.getPeriodId())
                return o1.getPeriodId() - o2.getPeriodId(); 
            if(o1.getEntryTime() != o2.getEntryTime())
                return (int)(o1.getEntryTime() - o2.getEntryTime());
            
            return o1.getEntryType().compareTo(o2.getEntryType()); 
        }
    }
    
}
