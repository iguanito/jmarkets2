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
 * JMConstants.java
 *
 * Created on March 18, 2004, 3:20 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared;

/**
 *
 *
 * Contains numerous constants used by the JMarkets software
 * @author  Raj Advani, Walter M. Yuan
 * @version $Id: JMConstants.java 355 2005-10-29 17:49:12Z raj $
 */
public interface JMConstants {
    
    public static final String newline = System.getProperty("line.separator");
    
    public static final int HTTP_UPDATE_PROTOCOL = 0;
    public static final int RMI_UPDATE_PROTOCOL = 1;
    
    public static final int NO_ACTION=0; 
    public static final int BUY_ACTION = 1;
    public static final int SELL_ACTION = 2;
    public static final int CANCEL_ACTION = 3;
    public static final int EXECUTE_ACTION = 4;
    
    public static final String BUY_ORDER = "buy"; 
    public static final String SELL_ORDER = "sell"; 
    
    public static final String CONTINUOUS_MARKET_ENGINE = "continuous";
    public static final String CALL_MARKET_ENGINE = "call";
    
    //Visual execute does not affect the price chart
    public static final int VISUAL_EXECUTE_ACTION = 5;
    
    //Persistence/Database related
    public static final int LOAD_ALL_RECORDS = -9999;
    
    public static final String EXPERIMENTER = "experimenter";
    public static final String EXPERIMENTER_KEY = "experimenterId";
    
    public static final String SUBJECT_KEY = "clientId";
    public static final String SUBJECT = "client";
    
    public static final int BACKLOG_MAX_LEVEL = 10;
    public static final int BACKLOG_RESTART_LEVEL = 2;
    
    //Security privilege values
    public static final int BUYER_ROLE = 0;
    public static final int SELLER_ROLE = 1;
    public static final int BOTH_ROLE = 2;
    public static final int NEITHER_ROLE = 3;
    public static final String [] MARKET_ROLES = {"buyer", "seller", "both", "neither"}; 
   
    public static final int ACTION_WAIT = 0; 
    public static final int ACTION_START = 1; 
    public static final int ACTION_FINISH = 2;
    public static final int ACTION_ABORT = 3;
    
    public static final String [] ACTION_STR = {"waiting", "started", "finished", "aborted"}; 
    
    public static final String ORDER_META_MARKET="market"; 
    public static final String ORDER_META_LIMIT="limit";
    
    public static final int ORDER_VALID = 0; 
    public static final int ORDER_TRANSACTED=1; 
    public static final int ORDER_CANCELLED =2; 
    
    public static final String[] ORDER_STATUSES ={"valid", "transacted", "canceled"};  
    
    public static final int USER_ROLE = 100; 
    public static final int EXPERIMENTER_ROLE = 200; 
    public static final int ADMIN_ROLE = 300; 
    
}
