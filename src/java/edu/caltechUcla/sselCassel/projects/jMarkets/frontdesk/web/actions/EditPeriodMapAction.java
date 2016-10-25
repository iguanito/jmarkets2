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
 * EditPeriodConfigServlet.java
 *
 * Created on July 27, 2004, 2:49 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.actions;

import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.forms.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author Walter M. Yuan
 * @version $Revision: 1.2 $ $Date: 2005-04-11 10:36:59 -0700 (Mon, 11 Apr 2005) $
 */

public final class EditPeriodMapAction extends JMarketsAction {
    
    
    // ----------------------------------------------------- Instance Variables
    
    /**
     * The <code>Log</code> instance for this application.
     */
    private static Log log = LogFactory.getLog(EditPeriodMapAction.class);
    
    
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
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Extract attributes we will need
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
 
        PeriodConfigForm periodForm = (PeriodConfigForm) form;
        SessionBean sessionBean = (SessionBean) session.getAttribute("sessionBean");
        int numPeriods = ((Integer) session.getAttribute("numPeriods")).intValue();
        int numGroups = sessionBean.getNumGroups();
        
        if (sessionBean.getPeriods() == null) {
            List period = new ArrayList();
            for (int i=0; i<numPeriods; i++) {
                PeriodBean per = new PeriodBean();
                per.setNumSecurities(1);
                per.setNumStates(1);
                per.setNumDivisions(5);
                per.setOpenDelay(5);
                per.setPeriodLength(100);
                per.setId(i);
                period.add(per);
            }
            periodForm.setPeriod(period);
        }
        else
            periodForm.setPeriod(sessionBean.getPeriods());
   
        if (sessionBean.getGroups() == null) {
            List group = new ArrayList();
            for (int i=0; i<numGroups; i++) {
                GroupBean gr = new GroupBean();
                gr.setId(i);
                group.add(gr);
            }
            periodForm.setGroupNames(group);
        }
        else
            periodForm.setGroupNames(sessionBean.getGroups());
        
        if (sessionBean.getPayoffFunctions() == null)
            periodForm.setPayoffFunctions(sessionBean.getAvailablePayoffFunctions());
        else
            periodForm.setPayoffFunctions(sessionBean.getPayoffFunctions());
        
        if (sessionBean.getBankruptcyFunctions() == null)
            periodForm.setBankruptcyFunctions(sessionBean.getAvailableBankruptcyFunctions());
        else
            periodForm.setBankruptcyFunctions(sessionBean.getBankruptcyFunctions());
        
        // Set a transactional control token to prevent double posting
        if(log.isDebugEnabled())
            saveToken(request);
        else{
            log.debug(" Setting transactional control token");
            saveToken(request);
        }
      
        return (mapping.findForward("success"));
    }
}