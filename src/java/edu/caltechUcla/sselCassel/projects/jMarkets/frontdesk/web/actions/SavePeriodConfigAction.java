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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.BankruptcyBean;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.BankruptcyFunctionBean;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.GroupBean;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.GroupPeriodBean;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.PayoffBean;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.PayoffFunctionBean;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.PeriodBean;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.SecurityPeriodBean;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.SessionBean;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.SubjectPeriodBean;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.forms.PeriodConfigForm;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;

/**
 * @author Walter M. Yuan
 * @version $Revision: 1.4 $ $Date: 2005-10-29 10:49:12 -0700 (Sat, 29 Oct 2005) $
 */

public final class SavePeriodConfigAction extends JMarketsLookupDispatchAction {
    
    private static Log log = LogFactory.getLog(SavePeriodConfigAction.class);
    
    /** Increment the periodNum then call processForward to populate the periodConfigForm for
     *  the next period */
    public ActionForward next(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
        
        SessionBean sessionBean = (SessionBean) session.getAttribute("sessionBean");
        int periodNum = ((Integer) session.getAttribute("periodNum")).intValue();
        PeriodConfigForm periodForm = (PeriodConfigForm) form;
        
        //Increment the period number if moving to next period
        periodNum++;
        session.setAttribute("periodNum", new Integer(periodNum));
        processForward(periodNum, sessionBean, periodForm, session);
        
        return (mapping.findForward("success"));
    }
    
    /** Decrement the periodNumber then call processForward to populate the periodConfigForm for
     *  the previous period */
    public ActionForward prev(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
        
        SessionBean sessionBean = (SessionBean) session.getAttribute("sessionBean");
        int periodNum = ((Integer) session.getAttribute("periodNum")).intValue();
        PeriodConfigForm periodForm = (PeriodConfigForm) form;
        
        //Decrement the period number if moving to previous period
        periodNum--;
        session.setAttribute("periodNum", new Integer(periodNum));
        processForward(periodNum, sessionBean, periodForm, session);
        
        return (mapping.findForward("success"));
    }
    
    public ActionForward done(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
        
        SessionBean sessionBean = (SessionBean) session.getAttribute("sessionBean");
        
        String thisPath = request.getServletPath();
        String host = request.getRequestURL().toString();
        String rootPath = host.replaceAll(thisPath, "");
        
        int sessionId = sessionBean.startSession(rootPath, (Integer)session.getAttribute(JMConstants.EXPERIMENTER_KEY));
        
        session.setAttribute("maxClients", "" + sessionBean.getNumSubjects());
        
        return (mapping.findForward("finished"));
    }
    
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
        periodForm.setPath("/session.xml");
        String path = periodForm.getSavePath();
        
        SessionBean sessionBean = (SessionBean) session.getAttribute("sessionBean");
        sessionBean.serialize(path);
        
        session.setAttribute("relativeSavePath", periodForm.getRelativeSavePath());
        
        return (mapping.findForward("save"));
    }
    
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
        int destinationPeriod = ((Integer) session.getAttribute("periodNum")).intValue();
        int originPeriod = periodForm.getCopyFrom();
        
        sessionBean.copyPeriod(originPeriod, destinationPeriod);
        
        return (mapping.findForward("success"));
    }
    
    protected java.util.Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("period.button.next", "next");
        map.put("period.button.prev", "prev");
        map.put("period.button.done", "done");
        map.put("period.button.save", "save");
        map.put("period.button.copy", "copy");
        return map;
    }
    
    /** Setup the periodConfigForm for the periodNum given. Check the sessionBean's periods array (where
     *  all the data is saved) to populate the fields of the periodConfigForm with the data therein. If
     *  the fields of the periods array are null, then populate the periodConfigForm with default values */
    private void processForward(int periodNum, SessionBean sessionBean, PeriodConfigForm periodForm, HttpSession session) {
        List periods = sessionBean.getPeriods();
        int numPeriods = periods.size();
        
        //Needed for the payoff function
        int[] numSecurities = new int[numPeriods];
        int[] numStates = new int[numPeriods];
        for (int i=0; i<numPeriods; i++) {
            PeriodBean period = (PeriodBean) periods.get(i);
            
            numSecurities[i] = period.getNumSecurities();
            numStates[i] = period.getNumStates();
        }
        
        //Ensures that a user re-load on the final period will not load a non-existent period and crash server
        if (periodNum >= periods.size()) {
            periodNum--;
            session.setAttribute("periodNum", new Integer(periodNum));
            processForward(periodNum, sessionBean, periodForm, session);
            return;
        }
        if (periodNum < 0) {
            periodNum++;
            session.setAttribute("periodNum", new Integer(periodNum));
            processForward(periodNum, sessionBean, periodForm, session);
            return;
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