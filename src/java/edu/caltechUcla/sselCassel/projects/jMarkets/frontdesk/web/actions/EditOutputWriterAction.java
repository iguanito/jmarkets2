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
 * EditSessionConfigServlet.java
 *
 * Created on July 27, 2004, 2:49 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.actions;

import edu.caltechUcla.sselCassel.projects.jMarkets.server.control.ControlServ;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import org.apache.struts.util.MessageResources;

/**
 * @author Walter M. Yuan
 * @version $Revision: 1.1 $ $Date: 2005-02-09 03:17:43 -0800 (Wed, 09 Feb 2005) $
 */

public final class EditOutputWriterAction extends JMarketsAction {
    
    
    // ----------------------------------------------------- Instance Variables
    
    /**
     * The <code>Log</code> instance for this application.
     */
    private static Log log = LogFactory.getLog(EditOutputWriterAction.class);
    
    
    // --------------------------------------------------------- Public Methods
    
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {
        
        // Extract attributes and parameters we will need
        Locale locale = getLocale(request);
        MessageResources messages = getResources(request);
        HttpSession session = request.getSession();
        
        if ("request".equals(mapping.getScope())){
            request.setAttribute(mapping.getAttribute(), form);
        }else{
            session.setAttribute(mapping.getAttribute(), form);
        }
        
        if (!isExperimenter(session))
            return (mapping.findForward("login_fail"));
        
        int experimenterId = (Integer)session.getAttribute(JMConstants.EXPERIMENTER_KEY);
                
        List <LabelValueBean> sessions = ControlServ.dbw.getExperimentsByExperimenterId(experimenterId);
        session.setAttribute("sessions", sessions);

        return mapping.findForward("success");
    }
    
}
