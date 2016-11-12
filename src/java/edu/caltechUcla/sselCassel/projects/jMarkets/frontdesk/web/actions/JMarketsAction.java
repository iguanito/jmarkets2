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
 * JMarketsAction.java
 *
 * Created on August 12, 2004, 11:12 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.actions;

import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;

/**
 *
 * @author  Raj Advani
 */
public abstract class JMarketsAction extends Action {
    
    /** Creates a new instance of JMarketsAction */
    public JMarketsAction() {
    }
    
    /** Check if experimenter is logged in */
    protected boolean isExperimenter(HttpSession session) {
        Boolean experimenter = (Boolean) session.getAttribute(JMConstants.EXPERIMENTER);
        if (experimenter != null && experimenter.booleanValue())
            return true;
        return false;
    }
    
    /** Check if client is logged in (experimenter's given access also) */
    protected boolean isClient(HttpSession session) {
        Boolean client = (Boolean) session.getAttribute(JMConstants.SUBJECT);
        if (client != null && client.booleanValue())
            return true;
        if (isExperimenter(session))
            return true;
        return false;
    }
    
    protected void loginExperimenter(HttpSession session, int expId) {
        if (expId > 0) {
            session.setAttribute(JMConstants.EXPERIMENTER, new Boolean(true));
            session.setAttribute(JMConstants.EXPERIMENTER_KEY, new Integer(expId));
        }
    }
    
    protected void loginClient(HttpSession session, int clientId) {
        if (clientId > 0) {
            session.setAttribute(JMConstants.SUBJECT, new Boolean(true));
            session.setAttribute(JMConstants.SUBJECT_KEY, new Integer(clientId));
        }
    }
    
    protected int getClientId(HttpSession session) {
        if (isClient(session))
            return ((Integer) session.getAttribute(JMConstants.SUBJECT_KEY)).intValue();
        return -1;
    }
    
}
