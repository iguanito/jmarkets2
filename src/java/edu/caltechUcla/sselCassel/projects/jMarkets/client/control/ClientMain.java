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
 * ClientApplet.java
 *
 * Created on February 10, 2004, 5:37 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.control;

import edu.caltechUcla.sselCassel.projects.jMarkets.client.interfaces.ClientGUI;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import java.net.URL;


/**
 *
 * @author  Raj Advani
 * @version $Id: ClientApplet.java 260 2005-08-11 16:15:27Z raj $
 */
public class ClientMain {
    
    // This is a hack to avoid an ugly error message in 1.1.
    public ClientMain(URL host, int id, String name, boolean test, boolean fast) {
        init(host, id, name, test, fast);
    }
    
    /** Initialization method that will be called after the applet is loaded
     *  into the browser.
     */
    public void init(URL host, int id, String name, boolean test, boolean fast) {
        try {
            ClientInfo cinfo = new ClientInfo();
            cinfo.setName(name);
            cinfo.setDbId(id);
            cinfo.setTestMode(test);
            
            ui = new ClientGUI(host, cinfo, fast);
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]) {
        try {
            if (args.length < 1 || args[0] == null) {
                System.out.println("You must specify a host name with -Dhost=<some host>");
                return;
            }
            
            if (!args[0].endsWith("/"))
                args[0] += "/";
            
            URL host = new URL(args[0]);
            
            if (args[1] == null) {
                System.out.println("You must specify a name for this client with -Dname=<some name>");
                return;
            }
            String name = args[1];
            
            if (args[2] == null) {
                System.out.println("You must specify a database id for this client with -Did=<some integer>");
                return;
            }
            int id = Integer.parseInt(args[2]);
            
            boolean test = (args.length > 3 && args[3] != null && args[3].equalsIgnoreCase("true"));
            boolean fastMode = (args.length > 4 && args[4] != null && args[4].equalsIgnoreCase("true"));
            
            System.out.println("Starting new ClientMain with host " + host + ", id " + id + ", name " + name + ", test " + test + ", and firstSesion " + fastMode);
            ClientMain client = new ClientMain(host, id, name, test, fastMode);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /** The main Client JFrame */
    private ClientGUI ui;
}
