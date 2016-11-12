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
 * GroupDef.java
 *
 * Created on July 7, 2004, 2:26 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.functions.*;

/**
 *
 * @author  Raj Advani
 */
public class GroupDef implements java.io.Serializable {
    
    /** Creates a new instance of groupInfo */
    public GroupDef(int numGroups, int numMarkets, String[] groupTitles) {
        this.numGroups = numGroups;
        this.groupTitles = groupTitles;
        
        cashInitials = new float[numGroups];
        securityInitials = new int[numGroups][numMarkets];
        securityShortConstraints = new int[numGroups][numMarkets];
        securityPrivileges = new int[numGroups][numMarkets];
        
        addSurplus = new boolean[numGroups][numMarkets];
        addDividend = new boolean[numGroups][numMarkets];
        addCash = new boolean[numGroups];
        
        payoffFunctions = new PayoffFunction[numGroups];
        clientPayoffFunctions = new ClientPayoffFunction[numGroups];
        bankruptcyFunctions = new BankruptcyFunction[numGroups];
        bankruptcyCutoffs = new float[numGroups];
        
        groupId_dbs = new int[numGroups];
    }
    
    public void setCashInitial(int group, float amount) {
        cashInitials[group] = amount;
    }
    
    public float getCashInitial(int group) {
        return cashInitials[group];
    }
    
    public void setSecurityInitial(int group, int security, int amount) {
        securityInitials[group][security] = amount;
    }
    
    public int getSecurityInitial(int group, int security) {
        return securityInitials[group][security];
    }
    
    public void setAddCash(int group, boolean ac) {
        addCash[group] = ac;
    }
    
    public boolean getAddCash(int group) {
        return addCash[group];
    }
    
    public void setAddSurplus(int group, int security, boolean as) {
        addSurplus[group][security] = as;
    }
    
    public boolean getAddSurplus(int group, int security) {
        return addSurplus[group][security];
    }
    
    public void setAddDividend(int group, int security, boolean ad) {
        addDividend[group][security] = ad;
    }
    
    public boolean getAddDividend(int group, int security) {
        return addDividend[group][security];
    }
    
    public float[] getCashInitials() {
        return cashInitials;
    }
    
    public void setCashInitials(float[] cashInitials) {
        this.cashInitials = cashInitials;
    }
    
    public int[][] getSecurityInitials() {
        return securityInitials;
    }
    
    public void setSecurityInitials(int group, int[] initials) {
        securityInitials[group] = initials;
    }
    
    public int[] getSecurityInitials(int group) {
        return securityInitials[group];
    }
    
    public void setSecurityInitials(int[][] securityInitials) {
        this.securityInitials = securityInitials;
    }
    
    public void setSecurityShortConstraint(int group, int security, int constraint) {
        securityShortConstraints[group][security] = constraint;
    }
    
    public int getSecurityShortConstraint(int group, int security) {
        return securityShortConstraints[group][security];
    }
    
    public int[][] getSecurityShortConstraints() {
        return securityShortConstraints;
    }
    
    public int[] getSecurityShortConstraints(int group) {
        return securityShortConstraints[group];
    }
    
    public void setSecurityShortConstraints(int[][] securityShortConstraints) {
        this.securityShortConstraints = securityShortConstraints;
    }
    
    public void setSecurityShortConstraints(int group, int[] constraints) {
        securityShortConstraints[group] = constraints;
    }
    
    public void setSecurityPrivilege(int group, int security, int role) {
        securityPrivileges[group][security] = role;
    }
    
    public int getSecurityPrivilege(int group, int security) {
        return securityPrivileges[group][security];
    }
    
    public int[] getSecurityPrivileges(int group) {
        return securityPrivileges[group];
    }
    
    public void setSecurityPrivileges(int group, int[] roles) {
        securityPrivileges[group] = roles;
    }
    
    public void setSecurityPrivileges(int[][] securityPriveleges) {
        this.securityPrivileges = securityPriveleges;
    }
    
    public int getNumGroups() {
        return numGroups;
    }
    
    public void setNumGroups(int numGroups) {
        this.numGroups = numGroups;
    }
    
    public String getGroupTitle(int group) {
        return groupTitles[group];
    }
    
    public void setGroupTitle(int group, String title) {
        groupTitles[group] = title;
    }
    
    public int getGroupId_db(int groupId) {
        return groupId_dbs[groupId];
    }
    
    public void setGroupId_db(int groupId, int groupId_db) {
        groupId_dbs[groupId] = groupId_db;
    }
    
    public PayoffFunction[] getPayoffFunctions() {
        return this.payoffFunctions;
    }
    
    public PayoffFunction getPayoffFunction(int group) {
        return payoffFunctions[group];
    }
    
    public void setPayoffFunctions(PayoffFunction[] payoffFunctions) {
        this.payoffFunctions = payoffFunctions;
    }
    
    public void setPayoffFunction(int group, PayoffFunction payoffFunction) {
        payoffFunctions[group] = payoffFunction;
    }
    
    public ClientPayoffFunction[] getClientPayoffFunctions() {
        return this.clientPayoffFunctions;
    }
    
    public ClientPayoffFunction getClientPayoffFunction(int group) {
        return clientPayoffFunctions[group];
    }
    
    public void setClientPayoffFunctions(ClientPayoffFunction[] clientPayoffFunctions){
        this.clientPayoffFunctions = clientPayoffFunctions;
    }
    
    public void setClientPayoffFunction(int group, ClientPayoffFunction clientPayoffFunction) {
        clientPayoffFunctions[group] = clientPayoffFunction;
    }
    
    
    public BankruptcyFunction[] getBankruptcyFunctions() {
        return this.bankruptcyFunctions;
    }
    
    public BankruptcyFunction getBankruptcyFunction(int group) {
        return bankruptcyFunctions[group];
    }
    
    public void setBankruptcyFunctions(BankruptcyFunction[] bankruptcyFunctions) {
        this.bankruptcyFunctions = bankruptcyFunctions;
    }
    
    public void setBankruptcyFunction(int group, BankruptcyFunction bankruptcyFunction) {
        bankruptcyFunctions[group] = bankruptcyFunction;
    }
    
    public float[] getBankruptcyCutoffs() {
        return this.bankruptcyCutoffs;
    }
    
    public float getBankruptcyCutoff(int group) {
        return bankruptcyCutoffs[group];
    }
    
    public void setBankruptcyCutoffs(float[] bankruptcyCutoffs) {
        this.bankruptcyCutoffs = bankruptcyCutoffs;
    }
    
    public void setBankruptcyCutoff(int group, float bankruptcyCutoff) {
        bankruptcyCutoffs[group] = bankruptcyCutoff;
    }
    
    /** Return true if the given group is allowed to bid on the given security */
    public boolean canBid(int group, int security) {
        int role = getSecurityPrivilege(group, security);
        if (role == JMConstants.BUYER_ROLE || role == JMConstants.BOTH_ROLE)
            return true;
        else
            return false;
    }
    
    /** Return true if the given group is allowed to ask on the given security */
    public boolean canAsk(int group, int security) {
        int role = getSecurityPrivilege(group, security);
        if (role == JMConstants.SELLER_ROLE || role == JMConstants.BOTH_ROLE)
            return true;
        else
            return false;
    }
    
    private int numGroups;
    private float[] cashInitials;
    private int[][] securityInitials;
    private int[][] securityShortConstraints;
    private int[][] securityPrivileges;
    
    private boolean[][] addSurplus;
    private boolean[][] addDividend;
    
    private boolean[] addCash;
    
    private String[] groupTitles;
    private PayoffFunction[] payoffFunctions;
    private ClientPayoffFunction[] clientPayoffFunctions;
    private BankruptcyFunction[] bankruptcyFunctions;
    private float[] bankruptcyCutoffs;
    
    /** Maps group ids to their database ids */
    private int[] groupId_dbs;
    
    
}
