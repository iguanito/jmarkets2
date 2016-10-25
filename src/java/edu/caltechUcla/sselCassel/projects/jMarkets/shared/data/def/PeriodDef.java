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
 * PeriodDef.java
 *
 * Created on March 21, 2004, 11:45 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def;

import edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data.PeriodBean;

/**
 *
 * @author  Raj Advani
 * @version $Id: PeriodDef.java 207 2005-02-09 11:17:43Z raj $
 */
public class PeriodDef implements java.io.Serializable {
    
    /**
     * Creates a new instance of PeriodDef
     */
    public PeriodDef(PeriodBean pb, String marketEngine, SubjectDef subjectInfo, MarketDef marketInfo, GroupDef groupInfo) {
        
        this.openDelay = pb.getOpenDelay();
        this.periodLength = pb.getPeriodLength();
        this.marketEngine = marketEngine;
        this.subjectInfo = subjectInfo;
        this.marketInfo = marketInfo;
        this.groupInfo = groupInfo;
        this.applyTrigger = pb.isApplyTrigger();
        this.showSuggestedClearingPrice = pb.isShowSuggestedClearingPrice(); 
        this.closebook = pb.isClosebook(); 
    }
    
    public int getOpenDelay() {
        return openDelay;
    }
    
    public void setOpenDelay(int openDelay) {
        this.openDelay = openDelay;
    }
    
    public int getPeriodLength() {
        return periodLength;
    }
    
    public void setPeriodLength(int periodLength) {
        this.periodLength = periodLength;
    }
    
    public MarketDef getMarketInfo() {
        return marketInfo;
    }
    
    public void setMarketInfo(MarketDef marketInfo) {
        this.marketInfo = marketInfo;
    }
    
    public SubjectDef getSubjectInfo() {
        return subjectInfo;
    }
    
    public void setSubjectInfo(SubjectDef subjectInfo) {
        this.subjectInfo = subjectInfo;
    }
    
    public GroupDef getGroupInfo() {
        return groupInfo;
    }
    
    public void setGroupInfo(GroupDef groupInfo) {
        this.groupInfo = groupInfo;
    }
    
    public String getMarketEngine() {
        return marketEngine;
    }
    
    public void setMarketEngine(String marketEngine) {
        this.marketEngine = marketEngine;
    }
    
     public boolean isApplyTrigger() {
         return applyTrigger;
     }
     
     public void setApplyTrigger(boolean applyTrigger) {
         this.applyTrigger = applyTrigger;
     }
     
     public boolean isClosebook() {
        return closebook;
    }

    public void setClosebook(boolean closebook) {
        this.closebook = closebook;
    }

    public boolean isShowSuggestedClearingPrice() {
        return showSuggestedClearingPrice;
    }

    public void setShowSuggestedClearingPrice(boolean showSuggestedClearingPrice) {
        this.showSuggestedClearingPrice = showSuggestedClearingPrice;
    }
    
    private int openDelay;
    private int periodLength;
    private MarketDef marketInfo;
    private SubjectDef subjectInfo;
    private GroupDef groupInfo;
    private String marketEngine;
    private boolean applyTrigger; 
    private boolean closebook; 
    private boolean showSuggestedClearingPrice; 

}
