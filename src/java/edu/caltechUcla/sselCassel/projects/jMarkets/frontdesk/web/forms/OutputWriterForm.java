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
 * PeriodConfigForm.java
 *
 * Created on August 6, 2004, 11:20 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.forms;

import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.*;
import org.apache.struts.action.ActionForm;
import java.util.List;

/**
 *
 * @author  Raj Advani
 */
public class OutputWriterForm extends ActionForm {
    
    /** Creates a new instance of PeriodConfigForm */
    public OutputWriterForm() {
    }
      
    /** Gets the real save path: ex). d:\dev\jMarkets\session.jsm */
    public String getSavePath() {
        return getServlet().getServletContext().getRealPath(getPath());
    }
    
    /** Gets the relative save path used by the jsp pages: ex) http://localhost:8080/jMarkets/session.jsm */
    public String getRelativeSavePath() {
        return relativeSavePath;
    }
    
    public void setRelativeSavePath(String relativeSavePath) {
        this.relativeSavePath = relativeSavePath;
    }
    
    /** Gets the strict save path set by the Action classes: ex) /session.jsm */
    public java.lang.String getPath() {
        return path;
    }
    
    public void setPath(java.lang.String path) {
        this.path = path;
        this.relativeSavePath = "/jMarkets2" + path;
    }
   
    /** Getter for property sessionId.
     * @return Value of property sessionId.
     *
     */
    public int getSessionId() {
        return sessionId;
    }
    
    /** Setter for property sessionId.
     * @param sessionId New value of property sessionId.
     *
     */
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
    
    private int sessionId;
    private String path;
    private String relativeSavePath;
}
