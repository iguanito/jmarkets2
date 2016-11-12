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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.*;
import java.io.*;
import org.apache.struts.upload.*;
import com.thoughtworks.xstream.XStream;
import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.*;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * @author Walter M. Yuan
 * @version $Revision: 1.2 $ $Date: 2005-10-29 10:49:12 -0700 (Sat, 29 Oct 2005) $
 */

public final class SaveSessionConfigAction extends JMarketsAction {
    
    
    // ----------------------------------------------------- Instance Variables
    
    /**
     * The <code>Log</code> instance for this application.
     */
    private static Log log = LogFactory.getLog(SaveSessionConfigAction.class);
    
    
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
        
        DynaValidatorForm sessionForm = (DynaValidatorForm) form;
        
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
      
        SessionBean sessionBean = createSessionBean(session, sessionForm);
        if (sessionBean == null) {
            ActionMessages aerrors = new ActionMessages();
            aerrors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.session.file"));
            saveErrors(request, aerrors);
            
            // Remove the obsolete form bean
            if (mapping.getAttribute() != null) {
                if ("request".equals(mapping.getScope()))
                    request.removeAttribute(mapping.getAttribute());
                else
                    session.removeAttribute(mapping.getAttribute());
            }
            
            return (mapping.findForward("failure"));
        }
       
        session.setAttribute("sessionBean", sessionBean);
        
        // Remove the obsolete form bean
        if (mapping.getAttribute() != null) {
            if ("request".equals(mapping.getScope()))
                request.removeAttribute(mapping.getAttribute());
            else
                session.removeAttribute(mapping.getAttribute());
        }
        
        return (mapping.findForward("success"));
    }
    
    /** Given the DynaValidator sessionForm, create the SessionBean object. This occurs in one
     *  of two ways: either by parsing an uploaded SessionBean file, or by creating a new
     *  SessionBean from scratch (if no file was uploaded) */
    private SessionBean createSessionBean(HttpSession session, DynaValidatorForm sessionForm) {
        try {
            boolean loadFromFile = ((Boolean) sessionForm.get("loadFromFile")).booleanValue();
            
            if (!loadFromFile) {
                SessionBean sessionBean = new SessionBean();
                sessionBean.setTimeoutLength(((Integer) sessionForm.get("timeoutLength")).intValue());
                sessionBean.setNumSubjects(((Integer) sessionForm.get("numSubjects")).intValue());
                sessionBean.setNumGroups(((Integer) sessionForm.get("numGroups")).intValue());
                sessionBean.setName((String) sessionForm.get("name"));
                sessionBean.setShowPastOrders(((Boolean)sessionForm.get("showPastOrders")).booleanValue());
                sessionBean.setShowPastTransactions(((Boolean)sessionForm.get("showPastTransactions")).booleanValue());
                sessionBean.setManualAdvance(((Boolean)sessionForm.get("manualAdvance")).booleanValue());
                
                session.setAttribute("numPeriods", sessionForm.get("numPeriods"));
                return sessionBean;
            } else {
                FormFile sessionFile = (FormFile) sessionForm.get("sessionFile");
                
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
                
                InputStream inStream = sessionFile.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
                String xml ="";
                while (true) {
                    String line = reader.readLine();
                    if (line == null)
                        break;
                    
                    xml += line;
                }
                
                log.info("Received session xml definition");
                SessionBean sessionBean = (SessionBean) xstream.fromXML(xml);
                
                
                int numPeriods = sessionBean.getPeriods().size();
                session.setAttribute("numPeriods", new Integer(numPeriods));
                
                reader.close();
                inStream.close();
                
                return sessionBean;
            }
        }catch(Exception e) {
            log.error("Error reading a session file", e);
        }
        return null;
    }
}
