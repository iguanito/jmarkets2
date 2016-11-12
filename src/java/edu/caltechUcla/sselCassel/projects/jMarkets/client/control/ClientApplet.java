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
import javax.swing.JApplet;


/**
 *
 * @author  Raj Advani
 * @version $Id: ClientApplet.java 260 2005-08-11 16:15:27Z raj $
 */
public class ClientApplet extends JApplet {
    
    // This is a hack to avoid an ugly error message in 1.1.
    public ClientApplet() {
        getRootPane().putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);
    }
    
    /** Initialization method that will be called after the applet is loaded
     *  into the browser.
     */
    public void init() {
        try {
            URL host = getCodeBase();
            int id = Integer.parseInt(getParameter("id"));
            String name = getParameter("name");
            String tmode = getParameter("test");
            String fmode = getParameter("fast");
            
            boolean test = false;
            if (tmode != null && tmode.equalsIgnoreCase("true"))
                test = true;
            
            boolean fast = false;
            if (fmode != null && fmode.equalsIgnoreCase("true"))
                fast = true;
            
            ClientInfo cinfo = new ClientInfo();
            cinfo.setName(name);
            cinfo.setDbId(id);
            cinfo.setTestMode(test);
            
            ui = new ClientGUI(host, cinfo, fast); 
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /** The main Client JFrame */
    private ClientGUI ui;
}
