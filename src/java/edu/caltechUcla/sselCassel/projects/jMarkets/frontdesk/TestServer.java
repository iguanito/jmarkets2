/*
 * Copyright (C) 1998-2004, <a href="http://www.ssel.caltech.edu">SSEL</a>
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
 * TestServer.java
 *
 * Created on March 18, 2004, 3:18 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk;

import java.net.*;
import java.io.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SubjectDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.GroupDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.PeriodDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SessionDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.MarketDef;
import com.thoughtworks.xstream.XStream;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.*;
import java.util.*;

/**
 *
 * @author  Raj Advani
 */
public class TestServer {
    
    public static void main(String args[]) {
        try {
            if (args.length < 1 || args[0] == null) {
                System.out.println("You must specify a host for the jMarkets server with -Dhost");
                return;
            }
            
            if (args[0].endsWith("/"))
                args[0] += "servlet/ServletReceiver";
            else
                args[0] += "/servlet/ServletReceiver";
            
            URL host = new URL(args[0]);
            //URL host = new URL("http://localhost:8080/jMarkets15/servlet/ServletReceiver");
            
            if (args.length < 2 || args[1] == null) {
                System.out.println("You must specify a session file to load into the server");
                return;
            }
            String sessionPath = args[1];
            
            TestServer test = new TestServer(host, sessionPath);
            test.startGame();
        }catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /** Creates a new instance of TestServer */
    public TestServer(URL host, String sessionPath) {
        System.out.println("Loading session at path " + sessionPath + " into host " + host);
        
        this.host = host;
        this.sessionPath = sessionPath;
    }
    
    private SessionDef loadSampleGame(String path) throws Exception {
        File sessionFile = new File(path);
        FileInputStream inStream = new FileInputStream(sessionFile);
        
        XStream xstream = new XStream();
        xstream.alias("session", SessionBean.class);
        xstream.alias("periodDef", PeriodBean.class);
        xstream.alias("groupDef", GroupBean.class);
        xstream.alias("periodGroup", GroupPeriodBean.class);
        xstream.alias("payoffDef", PayoffBean.class);
        xstream.alias("bankruptcyDef", BankruptcyBean.class);
        xstream.alias("bankruptcyFunction", BankruptcyFunctionBean.class);
        xstream.alias("bankruptcySecurity", BankruptcySecurityBean.class);
        xstream.alias("payoffFunction", PayoffFunctionBean.class);
        xstream.alias("payoffSecurity", PayoffSecurityBean.class);
        xstream.alias("periodSecurity", SecurityPeriodBean.class);
        xstream.alias("periodSubject", SubjectPeriodBean.class);
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        String xml ="";
        while (true) {
            String line = reader.readLine();
            if (line == null)
                break;
            
            xml += line;
        }
        
        System.out.println("Received session xml definition: " + xml);
        SessionBean sessionBean = (SessionBean) xstream.fromXML(xml);
        
        reader.close();
        inStream.close();
        
        return sessionBean.createSessionInfo();
    }
    
    /**
     * @param args the command line arguments
     */
    public void startGame() {
        try {
            URL servlet = host;//new URL("http://localhost:8080/jmarkets/servlet/AuthServ");
            Request req = new Request(Request.SERVER_INIT_REQUEST);
            
            //System Information
            req.addStringInfo("name", "Test Experiment");
            req.addIntInfo("updateProtocol", JMConstants.HTTP_UPDATE_PROTOCOL);
            req.addIntInfo("updateTime", 100);
            
            SessionDef session = loadSampleGame(sessionPath);
            
            int numClients = session.getPeriod(0).getSubjectInfo().getNumSubjects();
            req.addIntInfo("numClients", numClients);
            
            req.addInfo("session", session);
            
            URLConnection servletConnection = servlet.openConnection();
            
            servletConnection.setDoInput(true);
            servletConnection.setDoOutput(true);
            servletConnection.setUseCaches(false);
            servletConnection.setDefaultUseCaches(false);
            servletConnection.setRequestProperty("Content-Type", "application/octet-stream");
            
            ObjectOutputStream outputToServlet = new ObjectOutputStream(servletConnection.getOutputStream());
            
            outputToServlet.writeObject(req);
            outputToServlet.flush();
            outputToServlet.close();
            
            ObjectInputStream inputFromServlet = new ObjectInputStream(servletConnection.getInputStream());
            Response res = (Response) inputFromServlet.readObject();
            inputFromServlet.close();
            
        }catch(IOException e) {
            e.printStackTrace();
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
            //waitTwoSeconds();
            //startGame();
        }
    }
    
    public synchronized void waitTwoSeconds() {
        try {
            System.out.println("Waiting for Tomcat to finish initializing...");
            wait(2000);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private URL host;
    private String sessionPath;
}
