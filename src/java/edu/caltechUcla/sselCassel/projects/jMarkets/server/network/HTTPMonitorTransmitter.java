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
 * HTTPMonitorTransmitter.java
 *
 * Created on August 15, 2005, 12:14 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.network;

import edu.caltechUcla.sselCassel.projects.jMarkets.server.interfaces.MonitorUI;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.SessionIdentifier;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

/**
 *
 * @author Raj Advani, Walter Yuan
 */
public class HTTPMonitorTransmitter {
    
    /** Creates a new instance of HTTPMonitorTransmitter */
    public HTTPMonitorTransmitter(URL codeBase) {
        this.codeBase = codeBase;
    }
    
    /** Send the ServletReceiver a TERMINATE_SESSION_REQUEST. Return true if the operation
     *  was successful */
    public void stopExperiment(int sessionId) {
        try {
            URL host = new URL(codeBase + "servlet/ServletReceiver");
            
            Request req = new Request(Request.TERMINATE_SESSION_REQUEST);
            req.addIntInfo("sessionId", sessionId);
            
            Response res = sendRequest(host, req);
        }catch(MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
    /** Send the ServletReceiver a START_GAME_REQUEST. Return true if the operation was
     *  successful */
    public void startExperiment(int sessionId) {
        final int sid = sessionId;
        
        Runnable starter = new Runnable() {
            public void run() {
                try {
                    URL host = new URL(codeBase + "servlet/ServletReceiver");
                    
                    Request req = new Request(Request.START_GAME_REQUEST);
                    req.addIntInfo("sessionId", sid);
                    
                    Response res = sendRequest(host, req);
                    
                }catch(MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        };
        
        Thread stThread = new Thread(starter);
        stThread.start();
    }
    
    public void stopPeriod(int sessionId) {
        try {
            URL host = new URL(codeBase + "servlet/ServletReceiver");
            
            Request req = new Request(Request.STOP_PERIOD);
            req.addIntInfo("sessionId", sessionId);
            
            Response res = sendRequest(host, req);
        }catch(MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
    public void startPeriod(int sessionId) {
        try {
            URL host = new URL(codeBase + "servlet/ServletReceiver");
            
            Request req = new Request(Request.START_PERIOD);
            req.addIntInfo("sessionId", sessionId);
            
            Response res = sendRequest(host, req);
        }catch(MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isManualControl(int sessionId){
       try {
            URL host = new URL(codeBase + "servlet/ServletReceiver");
            System.out.print("HTTPMonitorTransmitter.isManualControl called for sessionId=="+sessionId);
            Request req = new Request(Request.IS_MANUAL_CONTROL);
            req.addIntInfo("sessionId", sessionId);
            Response res = sendRequest(host, req);
            return res.getBooleanInfo("manualControl");
        }catch(MalformedURLException e) {
            e.printStackTrace();
        }
       return false;
    }
    
    public void setManualControl(int sessionId, boolean mc){
        try {
            URL host = new URL(codeBase + "servlet/ServletReceiver");
            //System.out.print("HTTPMonitorTransmitter.isManualControl called for sessionId=="+sessionId);
            Request req = new Request(Request.SET_MANUAL_CONTROL);
            req.addBooleanInfo("mc", mc);
            req.addIntInfo("sessionId", sessionId);
            
            Response res = sendRequest(host, req);            
        }catch(MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
    /** Send the ServletReceiver a SESSION_QUERY_REQUEST and retrieve a list of the
     *  active sessions. From here the MonitorControl will choose a sesison to
     *  monitor/connect to */
    public SessionIdentifier[] getActiveSessions() {
        try {
            URL host = new URL(codeBase + "servlet/ServletReceiver");
            Request req = new Request(Request.SESSION_QUERY_REQUEST);
            
            Response res = sendRequest(host, req);
            
            SessionIdentifier[] identifiers = (SessionIdentifier[]) res.getInfo("identifiers");       
            receiverProtocol = res.getStringInfo("protocol");
            receiverPort = res.getIntInfo("port");
            
            return identifiers;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     *  Connect the given monitor UI to the given session through the receiverProtocol. This is simply a
     *  matter of creating a receiver of the appropriate type. The receiver will then attempt
     *  to connect to the server. Upon connection, the server will match a transmitter to that
     *  receiver and communication will be established. The receiver will make all calls on this
     *  MonitorUI, whenever instructed to do so by messages from the MonitorServ, which are passed
     *  to it via the transmitter
     */
    public void connect(MonitorUI monitor, int sessionId) {
        final String p = receiverProtocol;
        final int port = receiverPort;
        final int sid = sessionId;
        final String host = codeBase.getHost();//";//getDocumentBase().getHost();
        final MonitorUI ui = monitor;
        
        Runnable doUpdate = new Runnable() {
            public void run() {
                if (p.equalsIgnoreCase("rmi")) {
                    RMIMonitorReceiver receiver = new RMIMonitorReceiver(ui, host, port, sid);
                }
                
                else { //"tcp"
                    TCPMonitorReceiver receiver = new TCPMonitorReceiver(ui, host, port, sid);
                }
            }
        };
        Thread updater = new Thread(doUpdate);
        updater.start();
    }
    
    /** Send an HTTP POST request to the specified servlet containing the given request. Opens
     *  an input/output non-cached connection to the servlet and readies it for sending
     *  binary data. Then sends the request and returns the response of the Servlet. The retryReq
     *  is the request that should be sent if the READ fails. If the write fails then the regular
     *  req is sent again. If retryReq is null, then if the read fails this function will simply
     *  return null */
    public Response sendRequest(URL servlet, Request req) {
        try {
            URLConnection servletConnection = servlet.openConnection();
            servletConnection.setConnectTimeout(40000);
            servletConnection.setReadTimeout(60000);
            
            servletConnection.setDoInput(true);
            servletConnection.setDoOutput(true);
            servletConnection.setUseCaches(false);
            servletConnection.setDefaultUseCaches(false);
            servletConnection.setRequestProperty("Content-Type", "application/octet-stream");
            
            try {
                ObjectOutputStream outputToServlet = new ObjectOutputStream(servletConnection.getOutputStream());
                
                outputToServlet.writeObject(req);
                outputToServlet.flush();
                outputToServlet.close();
            }catch(SocketTimeoutException e) {
                System.out.println("Connect timeout -- retrying request");
                return sendRequest(servlet, req);
            }
            
            try {
                ObjectInputStream inputFromServlet = new ObjectInputStream(servletConnection.getInputStream());
                Response res = (Response) inputFromServlet.readObject();
                inputFromServlet.close();
                return res;
            }catch(SocketTimeoutException e) {
                System.out.println("Read timeout -- sending retry request");
                return sendRequest(servlet, req);
            }
            
        }catch(SocketTimeoutException e) {
            System.out.println("Read or connect timeout -- retrying request");
            sendRequest(servlet, req);
        }catch(BindException e) {
            System.out.println("Failed to bind to HTTP output port -- retrying");
            sendRequest(servlet, req);
        }catch(IOException e) {
            e.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String getReceiverProtocol() {
        return receiverProtocol;
    }
    
    public void setReceiverProtocol(String receiverProtocol) {
        this.receiverProtocol = receiverProtocol;
    }
    
    public int getReceiverPort() {
        return receiverPort;
    }
    
    public void setReceiverPort(int receiverPort) {
        this.receiverPort = receiverPort;
    }
    
    private String receiverProtocol;
    private int receiverPort;
    private URL codeBase;
}
