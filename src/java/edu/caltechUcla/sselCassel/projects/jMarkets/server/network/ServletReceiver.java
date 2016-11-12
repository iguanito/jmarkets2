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
 * ServletReceiver.java
 *
 * Created on February 3, 2005, 10:50 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.network;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.server.control.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  Raj Advani
 */
public class ServletReceiver extends HttpServlet {
    
    /** Creates a new instance of ServletReceiver */
    public ServletReceiver() {
    }
    
    /** Initialize the ServletReceiver by loading the jmarkets.properties file and creating the
     *  database connector and writer */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        log.info("The Servlet Receiver has been initialized");
        
        //load the configuration file
        try {
            String relativePath = getInitParameter("properties");
            System.out.println(relativePath);
            String propsPath = getServletContext().getRealPath(relativePath);
            System.out.println(propsPath);
            
            Properties props = new Properties();
            InputStream stream = new FileInputStream(propsPath);
            props.load(stream);
            
            log.info("Loaded jmarkets.properties file, initializing DispatchServ");
            
            dispatcher = new DispatchServ(props, receiverPath);
            
        }catch(Exception e) {
            log.error("Error when loading jmarkets.properties file: " + e);
            e.printStackTrace();
        }
    }
    
    public void doGet(HttpServletRequest httpReq, HttpServletResponse httpRes) throws ServletException, IOException {
        log.debug("ServletReceiver has received a 'GET' request -- discarding");
    }
    
    public void doPost(HttpServletRequest httpReq, HttpServletResponse httpRes) throws ServletException, IOException {
        try {
            Request req = receive(httpReq);
            Response res = null;
            
            if (req.getType() == Request.TRANSACTION_REQUEST)
                res = dispatcher.processTransactionRequest(req);
            
            else if (req.getType() == Request.SESSION_QUERY_REQUEST)
                res = dispatcher.processSessionQueryRequest(req); 

            else if (req.getType() == Request.SERVER_INIT_REQUEST)
                res = dispatcher.processInitRequest(req);
            
            else if (req.getType() == Request.CLIENT_AUTH_REQUEST)
                res = dispatcher.processAuthRequest(req);
            
            else if (req.getType() == Request.START_GAME_REQUEST)
                res = dispatcher.processStartRequest(req);
            
            else if (req.getType() == Request.TERMINATE_SESSION_REQUEST)
                res = dispatcher.processTerminateRequest(req);
            
            else if (req.getType() == Request.UPDATE_REQUEST || req.getType() == Request.RETRY_UPDATE_REQUEST)
                res = dispatcher.processUpdateRequest(req);
            
            else if (req.getType() == Request.STOP_PERIOD )
                res = dispatcher.processStopPeriodRequest(req);
            
            else if (req.getType() == Request.START_PERIOD )
            res = dispatcher.processStartPeriodRequest(req);
            
            else if (req.getType() == Request.IS_MANUAL_CONTROL )
                res = dispatcher.processIsManualControlRequest(req);
            
            else if (req.getType() == Request.SET_MANUAL_CONTROL )
                res = dispatcher.processSetManualControlRequest(req);
            
            else
                res = dispatcher.processUnknownRequest(req);
            
            respond(httpRes, res);
        }
        catch(Exception e) {
            log.error("Failed to send request to dispatcher for processing", e);
        }
    }
    
    public void destroy(){
        log.error("HEY, LOOK!  SERVLETRECEIVER.destroy() HAS BEEN CALLED!");
        super.destroy();
        dispatcher.destroy();
    }
    
    /** Receive a request object from the client given the HttpServletRequest. If the object
     *  sent is not a request, throw an exception */
    protected Request receive(HttpServletRequest r) {
        try {
            ObjectInputStream inputFromApplet = new ObjectInputStream(r.getInputStream());
            Request req = (Request) inputFromApplet.readObject();
            inputFromApplet.close();
         
            return req;
        }catch(IOException e) {
            log.error("Failed to receive request", e);
        }catch(ClassNotFoundException e) {
            log.error("Failed to receive request", e);
        }
        return null;
    }
    
    /** Send the given reponse to the client waiting for the given HttpServletResponse */
    protected void respond(HttpServletResponse r, Response res) {
        try {
            OutputStream out = r.getOutputStream();
            ObjectOutputStream outputToApplet = new ObjectOutputStream(out);
            outputToApplet.writeObject(res);
            
            outputToApplet.flush();
            outputToApplet.close();
        }catch(IOException e) {
            log.error("Failed to respond to request", e);
        }
    }
    
    private Dispatcher dispatcher;
    
    private static String receiverPath = "servlet/ServletReceiver";
    private static Log log = LogFactory.getLog(ServletReceiver.class);
}
