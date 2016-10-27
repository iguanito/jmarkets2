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
 * SavePeriodConfigServlet.java
 *
 * Created on July 27, 2004, 2:49 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import java.util.*;

import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.forms.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.*;
import org.apache.struts.action.ActionMessages;

/**
 * @author Walter M. Yuan
 * @version $Revision: 1.2 $ $Date: 2005-10-29 10:49:12 -0700 (Sat, 29 Oct 2005) $
 */

public final class SavePeriodMapAction extends JMarketsAction {
    
    
    // ----------------------------------------------------- Instance Variables
    
    /**
     * The <code>Log</code> instance for this application.
     */
   private static Log log = LogFactory.getLog(SavePeriodMapAction.class);
    
    
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
        HttpSession session = request.getSession();
        
        // Validate the transactional control token
        ActionMessages errors = new ActionMessages();
        
        isTokenValid(request, log.isDebugEnabled());
        resetToken(request);
        
        if (!isExperimenter(session))
            return (mapping.findForward("login_fail"));
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            saveToken(request);
            return (mapping.getInputForward());
        }
        
        PeriodConfigForm periodForm = (PeriodConfigForm) form;
        SessionBean sessionBean = (SessionBean) session.getAttribute("sessionBean");
        
        sessionBean.setPeriods(periodForm.getPeriod());
        sessionBean.setGroups(periodForm.getGroupNames());
        sessionBean.setPayoffFunctions(periodForm.getPayoffFunctions());
        sessionBean.setBankruptcyFunctions(periodForm.getBankruptcyFunctions());
        
        session.setAttribute("periodNum", new Integer(0));
        
        processForward(sessionBean, periodForm);
       
        return (mapping.findForward("success"));
    }
    
    /** Setup the periodConfigForm for the first period. Check the sessionBean's periods array (where
     *  all the data is saved) to populate the fields of the periodConfigForm with the data therein. If
     *  the fields of the periods array are null, then populate the periodConfigForm with default values */
    private void processForward(SessionBean sessionBean, PeriodConfigForm periodForm) {
        int periodNum = 0;
        
        List periods = sessionBean.getPeriods();
        int numPeriods = periods.size();
        
        int[] numSecurities = new int[numPeriods];
        int[] numStates = new int[numPeriods];
        for (int i=0; i<numPeriods; i++) {
            PeriodBean period = (PeriodBean) periods.get(i);
            
            numSecurities[i] = period.getNumSecurities();
            numStates[i] = period.getNumStates();
        }
        
        PeriodBean period = (PeriodBean) periods.get(periodNum);
        if (periodNum == 0)
            periodForm.setFirstPeriod("true");
        else
            periodForm.setFirstPeriod("false");
        if (periodNum == (numPeriods-1))
            periodForm.setLastPeriod("true");
        else
            periodForm.setLastPeriod("false");
        
        int numSubjects = sessionBean.getNumSubjects();
        
        if (period.getSecurities() == null) {
            List security = new ArrayList();
            for (int i=0; i<period.getNumSecurities(); i++) {
                SecurityPeriodBean sec = new SecurityPeriodBean();
                
                int[] constraints = new int[sessionBean.getNumGroups()];
                int[] initials = new int[sessionBean.getNumGroups()];
                boolean[] buyPriveleges = new boolean[sessionBean.getNumGroups()];
                boolean[] sellPriveleges = new boolean[sessionBean.getNumGroups()];
                boolean[] addSurplus = new boolean[sessionBean.getNumGroups()];
                boolean[] addDividend = new boolean[sessionBean.getNumGroups()];
                
                sec.setConstraints(constraints);
                sec.setInitials(initials);
                sec.setBuyPriveleges(buyPriveleges);
                sec.setSellPriveleges(sellPriveleges);
                sec.setAddSurplus(addSurplus);
                sec.setAddDividend(addDividend);
                
                security.add(sec);
            }
            period.setSecurities(security);
        }
        
        if (period.getGroups() == null) {
            List names = sessionBean.getGroups();
            List group = new ArrayList();
            
            for (int i=0; i<sessionBean.getNumGroups(); i++) {
                GroupPeriodBean gr = new GroupPeriodBean();
                gr.setId(i);
                gr.setName(((GroupBean) names.get(i)).getName());
                group.add(gr);
            }
            period.setGroups(group);
        }
        
        if (period.getSubjects() == null) {
            List subject = new ArrayList();
            for (int i=0; i<numSubjects; i++) {
                SubjectPeriodBean subj = new SubjectPeriodBean();
                subj.setId(i);
                subject.add(subj);
            }
            period.setSubjects(subject);
        }
        
        if (period.getPayoffFunctionSpecs() == null) {
            List payoffFunctionSpecs = new ArrayList();
            List payoffBeans = sessionBean.getPayoffFunctions();
            
            for (int i=0; i<payoffBeans.size(); i++) {
                PayoffBean pBean = (PayoffBean) payoffBeans.get(i);
                int numSpecs = pBean.getNumSpecs();
                
                for (int j=0; j<numSpecs; j++) {
                    PayoffFunctionBean payoffFunctionSpec = new PayoffFunctionBean();
                    
                    payoffFunctionSpec.setName(pBean.getName());
                    payoffFunctionSpec.setSpecName(pBean.getName() + " Spec " + j);
                    payoffFunctionSpec.populateFields(periodNum, numSecurities, numStates);
                    
                    payoffFunctionSpecs.add(payoffFunctionSpec);
                }
            }
            period.setPayoffFunctionSpecs(payoffFunctionSpecs);
        }
        
        if (period.getBankruptcyFunctionSpecs() == null) {
            List bankruptcyFunctionSpecs = new ArrayList();
            List bankruptcyBeans = sessionBean.getBankruptcyFunctions();
            
            for (int i=0; i<bankruptcyBeans.size(); i++) {
                BankruptcyBean bBean = (BankruptcyBean) bankruptcyBeans.get(i);
                int numSpecs = bBean.getNumSpecs();

                for (int j=0; j<numSpecs; j++) {
                    BankruptcyFunctionBean bankruptcyFunctionSpec = new BankruptcyFunctionBean();
                    
                    bankruptcyFunctionSpec.setName(bBean.getName());
                    bankruptcyFunctionSpec.setSpecName(bBean.getName() + " Spec " + j);
                    bankruptcyFunctionSpec.populateFields(period.getNumSecurities(), period.getNumStates());
                    
                    bankruptcyFunctionSpecs.add(bankruptcyFunctionSpec);
                }
            }
            period.setBankruptcyFunctionSpecs(bankruptcyFunctionSpecs);
        }
        
        periodForm.setSecurity(period.getSecurities());
        periodForm.setGroup(period.getGroups());
        periodForm.setSubject(period.getSubjects());
        periodForm.setNumStates(period.getNumStates());
        periodForm.setBankruptcyFunctionSpecs(period.getBankruptcyFunctionSpecs());
        periodForm.setPayoffFunctionSpecs(period.getPayoffFunctionSpecs());
    }
}