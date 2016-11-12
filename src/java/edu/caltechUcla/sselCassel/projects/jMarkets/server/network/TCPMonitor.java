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
 * ExpMonitor.java
 *
 * Created on October 8, 2004, 7:33 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.network;

/**
 *
 * @author  Administrator
 */
public interface TCPMonitor {
    
    public static int ADD_LOG_MESSAGE = 0;
    public static int INSERT_CHART_MESSAGE = 1;
    public static int CONNECT_CLIENT_MESSAGE = 2;
    public static int UPDATE_CHART_MESSAGE = 3;
    public static int SET_TIME_MESSAGE = 4;
    public static int UPDATE_STATUS_MESSAGE = 5;
    public static int UPDATE_METRICS_MESSAGE = 6;
    public static int SET_ALL_CONNECTED_MESSAGE = 7;
    public static int ENABLE_START_MESSAGE = 8;
    public static int UPDATE_NUM_OFFERS_MESSAGE = 9;
    public static int CONSTRUCT_INFO_PANEL_MESSAGE = 10; 
    public static int SET_BACKLOG_MESSAGE = 11;
    public static int ENABLE_STOP_MESSAGE = 12;
    public static int SET_CONNECTED_MESSAGE = 13;
    
    public static int BREAK_CONNECTION_MESSAGE = 14;
    public static int ENABLE_PERIOD_START_MESSAGE = 15;
    
}
