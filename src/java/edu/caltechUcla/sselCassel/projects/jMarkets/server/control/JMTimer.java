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
 * JMTimer.java
 *
 * Created on February 4, 2005, 11:06 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The JMTimer is used for all JMarkets timing mechanisms. The most important of these is the
 * period timer, which monitors for market and period closure. See the JMTimerTask comments
 * for more descriptions on how this timer operates.
 *
 * The JMTimer class is a timer on which all timers for all sessions can be scheduled. It contains
 * a link to the dispatcher so that it can callback to the dispatcher whenever an event of
 * significance (period closure, market closure, etc.) occurs. This way the JMTimer need not interact
 * directly with any underlying data structures, and does not directly update the clients.
 *
 * @author  Raj Advani
 */
public class JMTimer {
    
    /** Creates a new instance of JMTimer */
    public JMTimer(DispatchServ dispatcher) {
        this.dispatcher = dispatcher;
        timer = new Timer();
        sessionTasks = new Hashtable();
        
    }
    
    public void cancel(){
        timer.cancel();
    }
    
    /** This schedules a new period timer for the given session. The dispatcher will be
     *  called whenever an event occurs, such as a market closing or a period closing. The
     *  dispatcher will then route these events to the ControlServ (to update the Session State)
     *  and the UpdateServ (to inform the clients) */
    public boolean schedulePeriodTimer(int sessionId, int periodNum, int timeoutLength, int periodLength, int openDelay, int[] marketLength) {
        JMTimerTask task = new JMTimerTask(sessionId, periodNum, periodLength, openDelay, marketLength);
        
        log.info("Waiting " + timeoutLength + " seconds for clients to finish period initialization...");
        timer.scheduleAtFixedRate(task, timeoutLength * 1000, 1000);
        sessionTasks.put(new Integer(sessionId), task);
        
        return true;
    }
    
    /** Terminate the timer for the given session */
    public void terminateSession(int sessionId) {
        TimerTask task = (TimerTask) sessionTasks.get(new Integer(sessionId));
        if (task == null) {
            log.warn("Could not terminate JM timer for session " + sessionId + " -- no timer exists for that session!");
            return;
        }
        
        task.cancel();
        log.debug("Timer for session " + sessionId + " has been canceled");
    }
    
    private Timer timer;
    private DispatchServ dispatcher;
    private Map sessionTasks;
    
    
    /** This is the TimerTask used by all JMarkets period timers. This TimerTask checks each second
     *  to see if time synchronizations are needed with the clients, and it informs the dispatcher
     *  whenever a market closes or whenever the period as a whole closes. The dispatcher routes
     *  these callbacks to the clients through the UpdateServ, and to the underlying data structures
     *  through the ControlServ */
    class JMTimerTask extends TimerTask {
        
        public JMTimerTask(int sessionId, int periodNum, int periodLength, int openDelay, int[] marketLength) {
            this.sessionId = sessionId;
            this.periodNum = periodNum;
            this.periodLength = periodLength;
            this.marketLength = marketLength;
            this.openDelay = openDelay;
        }
        
        /** Based on the current market and period timers, return true if a time event should
         *  be sent to the dispatcher. Return false otherwise */
        private boolean checkForTimeEvent(int openDelay, int periodLength, int[] marketLength) {
            boolean sendTimeUpdate = false;
            
            if (openDelay >= -1)
                sendTimeUpdate = true;
            
            return sendTimeUpdate;
        }
        
        /** Check the market timers to see if any market has closed. If a market has closed,
         *  tell the dispatcher */
        private boolean updateMarketClosure(int[] marketLength) {
            for (int i=0; i<marketLength.length; i++) {
                if (marketLength[i] == 0) {
                    dispatcher.processMarketClosure(sessionId, periodNum, i);
                    return true;
                }
            }
            return false;
        }
        
        /** Check the period timer to see if the period has closed. If so, tell the dispatcher
         *  and cancel this timer task */
        private boolean updatePeriodClosure(int periodLength) {
            if (periodLength == 0) {
                this.cancel();
                dispatcher.processPeriodClosure(sessionId, periodNum);
                
                return true;
            }
            return false;
        }
        
        /** Check if the period has opened (open delay expired). Tell the dispatcher */
        private boolean updatePeriodOpening(int openDelay) {
            if (openDelay == 0) {
                dispatcher.processPeriodOpening(sessionId, periodNum);
                return true;
            }
            
            return false;
        }
        
        /** This is called each second. It first updates the display times on the Server Monitor,
         *  then checks for market closure, period closure, and time synchronization updates. These
         *  are all processed by callbacks to the dispatcher. Finally, the times are decremented
         *  by one second */
        public void run() {
            try {
                dispatcher.setTimeLeft(sessionId, openDelay, periodLength, marketLength);
                
                boolean sendTimeUpdate = checkForTimeEvent(openDelay, periodLength, marketLength);
                if (sendTimeUpdate) 
                    dispatcher.processTimeEvent(sessionId, periodNum, openDelay, periodLength, marketLength);
                
                updatePeriodOpening(openDelay);
                //updateMarketClosure(marketLength);
                updatePeriodClosure(periodLength);
                
                if (openDelay < 0) {
                    periodLength--;
                    
                    /*for (int i=0; i<marketLength.length; i++) {
                        if (marketLength[i] >= 0)
                            marketLength[i]--;
                    }*/
                }
                
                openDelay--;
                
            }catch(Exception e) {
                log.error("Failed to update the server-side period/market timers", e);
            }
        }
        
        private int sessionId;
        private int periodNum;
        private int periodLength;
        private int openDelay;
        private int[] marketLength;
    }
    
    private static Log log = LogFactory.getLog(JMTimer.class);
}
