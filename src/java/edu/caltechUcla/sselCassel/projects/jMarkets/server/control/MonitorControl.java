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
 * MonitorControl.java
 *
 * Created on August 8, 2005, 6:26 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import edu.caltechUcla.sselCassel.projects.jMarkets.server.network.HTTPMonitorTransmitter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is the controller that is accessed by the monitor UI. It is implemented
 * by MonitorApplet and MonitorMain. This class starts up the Monitor UI and handles
 * message transmittal to the server via access to a transmitter object. This 
 * class does not handle message receiving, which is handled by the MonitorServ
 * and related classes
 *
 * @author Raj Advani, Walter Yuan
 */
public interface MonitorControl {
    
    public void startExperiment();
    
    public void stopExperiment();
    
    public void startPeriod(); 
    
    public void stopPeriod(); 
    
    public boolean isManualControl();
    public void setManualControl(boolean mc);
    
        //Log log = LogFactory.getLog(MonitorControl.class);
}
