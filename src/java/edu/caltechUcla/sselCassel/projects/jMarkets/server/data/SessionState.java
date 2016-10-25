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
 * SessionState.java
 *
 * Created on February 3, 2005, 2:18 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.data;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.*;
import java.util.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.earningstable.EarningsInfo;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SubjectDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.PeriodDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SessionDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.MarketDef;

/**
 *
 * @author  Raj Advani
 */
public class SessionState {
    
    /** Creates a new instance of SessionState */
    public SessionState(String name, int numClients, int updateTime, SessionDef session) {
        this.name = name;
        this.numClients = numClients;
        this.updateTime = updateTime;
        this.session = session;
        
        state = ADMIN_CONNECTING_STATE;
        numConnected = 0;
        numConnected = 0;
    }
    
    public int getState() {
        return state;
    }
    
    public void setState(int state) {
        this.state = state;
    }
    
    public int getNumConnected() {
        return numConnected;
    }
    
    public void setNumConnected(int numConnected) {
        this.numConnected = numConnected;
    }
    
    public int getNumClients() {
        return numClients;
    }
    
    public void setNumClients(int numClients) {
        this.numClients = numClients;
    }
    
    public int getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }
    
    public int getDatabaseId(int client) {
        SubjectDef sinfo = session.getPeriod(periodNum).getSubjectInfo();
        return sinfo.getDatabaseId(client);
    }
    
    public int[] getAllDatabaseIds() {
        SubjectDef sinfo = session.getPeriod(periodNum).getSubjectInfo();
        return sinfo.getDatabaseIds();
    }
    
    /** Add the given client to the session, by placing the name and
     *  database id mapping into the subject info object for each period. Return
     *  false if this client is already in the session */
    public boolean addClient(int client, int databaseId, String name) {
        int numPeriods = session.getNumPeriods();
        
        if (isConnected(databaseId, 0))
            return false;
        
        for (int i=0; i<numPeriods; i++) {
            SubjectDef sinfo = session.getPeriod(i).getSubjectInfo();
            sinfo.setDatabaseId(client, databaseId);
            sinfo.setName(client, name);
        }
        return true;
    }
    
    /** Check to see if the given client is connected in the given period. Return
     *  true if he/she is */
    public boolean isConnected(int databaseId, int periodNum) {
        SubjectDef sinfo = session.getPeriod(periodNum).getSubjectInfo();
        return sinfo.getId(databaseId) != -1;
    }
    
    /** Trim away all but numRemaining clients in every period */
    public void trimClients(int numRemaining) {
        for (int i=0; i<session.getNumPeriods(); i++) {
            PeriodDef pinfo = session.getPeriod(i);
            SubjectDef sinfo = pinfo.getSubjectInfo();
            sinfo.trimSubjects(numRemaining);
        }
        
        setNumClients(numRemaining);
    }
    
    public SessionDef getSession() {
        return session;
    }
    
    public void setSession(SessionDef session) {
        this.session = session;
    }
    
    public EarningsInfo[] getEarningsHistory() {
        return this.earningsHistory;
    }
    
    public void setEarningsHistory(EarningsInfo[] earningsHistory) {
        this.earningsHistory = earningsHistory;
    }
    
    public int getPeriodNum() {
        return periodNum;
    }
    
    public void setPeriodNum(int periodNum) {
        this.periodNum = periodNum;
    }
    
    public boolean isPeriodClosed() {
        return periodClosed;
    }
    
    public void setPeriodClosed(boolean periodClosed) {
        this.periodClosed = periodClosed;
    }
    
    public boolean[] getMarketClosed() {
        return marketClosed;
    }
    
    public void setMarketClosed(boolean[] marketClosed) {
        this.marketClosed = marketClosed;
    }
    
    public void setMarketClosed(int market) {
        marketClosed[market] = true;
    }
    
    public void resetMarketClosed(int periodNum) {
        PeriodDef pinfo = session.getPeriod(periodNum);
        MarketDef minfo = pinfo.getMarketInfo();
        
        marketClosed = new boolean[minfo.getNumMarkets()];
    }
    
    /** Getter for property marketLength.
     * @return Value of property marketLength.
     *
     */
    public int[] getMarketLength() {
        return this.marketLength;
    }
    
    /** Setter for property marketLength.
     * @param marketLength New value of property marketLength.
     *
     */
    public void setMarketLength(int[] marketLength) {
        this.marketLength = marketLength;
    }
    
    /** Getter for property periodLength.
     * @return Value of property periodLength.
     *
     */
    public int getPeriodLength() {
        return periodLength;
    }
    
    /** Setter for property periodLength.
     * @param periodLength New value of property periodLength.
     *
     */
    public void setPeriodLength(int periodLength) {
        this.periodLength = periodLength;
    }
    
    /** Getter for property openDelay.
     * @return Value of property openDelay.
     *
     */
    public int getOpenDelay() {
        return openDelay;
    }
    
    /** Setter for property openDelay.
     * @param openDelay New value of property openDelay.
     *
     */
    public void setOpenDelay(int openDelay) {
        this.openDelay = openDelay;
    }
    
    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public java.lang.String getName() {
        return name;
    }
    
    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    /**
     * Getter for property dividends.
     * @return Value of property dividends.
     */
    public float[] getDividends() {
        return this.dividends;
    }
    
    /**
     * Setter for property dividends.
     * @param dividends New value of property dividends.
     */
    public void setDividends(float[] dividends) {
        this.dividends = dividends;
    }
    
    public void setTraders(Trader[][] traders) {
        this.traders = traders;
    }
    
    public Trader[][] getTraders() {
        return traders;
    }
    
    public void setTraders(int period, Trader[] tr) {
        traders[period] = tr;
    }
    
    public Trader[] getTraders(int period) {
        return traders[period];
    }
    
    public boolean isManualControl(){
        return session.isManualControl();
    }
    
    public void setManualControl(boolean manual){
        session.setManualControl(manual);
    }
    
    /** The name of this session, given by the experimenter who starts it
     *  in the init request */
    private String name;
    
    /** Contains all the settings and market initialization information for the session */
    private SessionDef session;
    
    /** The number of clients to be used in this session */
    private int numClients;
    
    /** True if the given market is closed for this period */
    private boolean[] marketClosed;
    
    /** True if the current period has closed */
    private boolean periodClosed;
    
    /** The current period of this session */
    private int periodNum;
    
    /** An array of times remaining for each market in the period */
    private int[] marketLength;
    
    /** The time remaining in the current period, which is displayed in the gui */
    private int periodLength;
    
    /** The opening time remaining in the current period (time before opening markets) */
    private int openDelay;
    
    /** The number of clients currently connected */
    private int numConnected;
    
    /** The current state of the server */
    private int state;
    
    /** The number of milliseconds clients should wait between update requests when using the http update protocol */
    private int updateTime;
    
    /** An array of Trader objects; one for each period. For periods that have not yet transpired, 
     *  the Trader object is null. Indexed by period and then by subject id */
    private Trader[][] traders;
    
    /** An array of EarningsInfo objects that keep track of client earnings for the EarningsPanel */
    private EarningsInfo[] earningsHistory;
    
    /** Indexed by subject id number, this is the dividend received from the LAST period */
    private float[] dividends;
    
    /** The server is in this state when no game is running. To get out of this state it must be initialized
     *  by an experimenter with a SERVER_INIT_REQUEST */
    public static int SHUTDOWN_STATE = 0;
    
    /** The state when a game is in progress, and all clients have been authenticated */
    public static int GAME_RUNNING_STATE = 1;
    
    /** During this state the server is accepting CLIENT_AUTH_REQUESTS and connecting clients to the game */
    public static int ACCEPTING_CLIENTS_STATE = 2;
    
    /** During this state all CLIENT_AUTH_REQUESTS are rejected because all clients have connected */
    public static int CLIENTS_FULL_STATE = 3;
    
    /** During this state the AuthServ is waiting for the Admin ExpMonitor to connect */
    public static int ADMIN_CONNECTING_STATE = 4;   
}
