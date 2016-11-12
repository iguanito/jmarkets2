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
 * A logging utility that interfaces with log4j to display logging messages
 * on the ServerGUI. This is also used during integrated testing to detect
 * any errors
 *
 * guiAppender.java
 *
 * Created on October 13, 2001, 8:23 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.control;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.*;

/**
 *
 * @author  Raj Advani
 * @version $Id: guiAppender.java 232 2005-04-24 05:10:19Z wmyuan $
 */
public class guiAppender extends AppenderSkeleton {
    
    /** Creates new guiAppender */
    public guiAppender(Monitor monitorServ) {
        this.monitorServ = monitorServ;
        closed = false;
    }
    
    /** This appender does not require a layout - it is only for use in the multistage
     *  project GUI */
    public boolean requiresLayout() {
        return false;
    }
    
    /** No closing procedures are necessary */
    public void close() {
        closed = true;
        addLogMessage("null", -99, "null");
    }
    
    protected void append(org.apache.log4j.spi.LoggingEvent loggingEvent) {
        try {
            if (closed || loggingEvent.getLevel().toInt() <= Priority.DEBUG_INT)
                return;
            
            Priority p = loggingEvent.getLevel();
            String priority = p.toString();
            String thread = "[" + loggingEvent.getThreadName() + "]";
            String message = loggingEvent.getRenderedMessage();
            
            String className = "";
            String logStr;
            
            //parse and send the logging message to the serverUI
            /*
            if (ui.isDisplayLocationInfo()) {
                LocationInfo locInfo = loggingEvent.getLocationInformation();
                className = locInfo.getClassName();
                String methodName = locInfo.getMethodName();
                String fileName = locInfo.getFileName();
                String truncFileName = fileName.substring(0,fileName.length() - 5);
                logStr = priority + ":  [" + truncFileName + "." + methodName + "]  :  " + message;
            }*/
            
           // else
            logStr = priority + " " + thread + ": " + message;
            
            monitorServ.logMessage(logStr, loggingEvent.getLevel().toInt(), className);

        }catch(Exception e) {
            log.error("Failed to establish a remote connection to the ExpMonitor to log a message", e);
        }
    }
    
    /** Add a LogMessage to the queue so that the updater thread can later send it to the GUI */
    public void addLogMessage(String logStr, int level, String className) {
        LogMessage logMessage = new LogMessage(logStr, level, className);
        logMessages.add(logMessage);
    }
    
    /** Start the updater thread, which checks the logMessages vector for new log updates and adds
     *  them to the ExpMonitor */
    public void startUpdaterThread() {
        /*
        logMessages = new Vector();
         
        Runnable updater = new Runnable() {
            public void run() {
                try {
                    while (true) {
                        LogMessage logMessage = getNextUpdate();
                        if (logMessage.level == -99) {
                            break;
                        }
                        ui.addLogMessage(logMessage.logStr, logMessage.level, logMessage.className);
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
         
            private synchronized LogMessage getNextUpdate() throws InterruptedException {
                try {
                    if (!logMessages.isEmpty() && logMessages.size() > 0)
                        return (LogMessage) logMessages.remove(0);
                    else
                        wait(1000);
                    return getNextUpdate();
                }catch(Exception e) {
                    System.out.println("Log updater lost synchronization -- resynchronizing");
                    return getNextUpdate();
                }
            }
        };
         
        Thread updateThr = new Thread(updater);
        updateThr.start();
         */
    }
    
    private Vector logMessages;
    private boolean closed;
    private Monitor monitorServ;
    private static Log log = LogFactory.getLog(guiAppender.class);
    
    
    /** Encapsulates a log message that the gui appender update thread sends to the
     *  ExpMonitor */
    class LogMessage {
        public LogMessage(String logStr, int level, String className) {
            this.logStr = logStr;
            this.level = level;
            this.className = className;
        }
        
        public String logStr;
        public String className;
        public int level;
    }
}
