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
 * SessionBean.java
 *
 * Created on August 4, 2004, 11:08 AM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.frontdesk.web.data;

import edu.caltechUcla.sselCassel.projects.jMarkets.shared.network.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.functions.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SubjectDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.GroupDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.PeriodDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SessionDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.MarketDef;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  Raj Advani
 */
public class SessionBean implements java.io.Serializable {
    
    public static Log log = LogFactory.getLog(SessionBean.class);
    
    /** Creates a new instance of SessionBean */
    public SessionBean() {
    }
    
    /** Start the session contained in this bean, returning the session id number
     *  created */
    public int startSession(String path, int experimenterId) {
        SessionDef sessionInfo = createSessionInfo();
        sessionInfo.setExperimenterId(experimenterId);
        sessionInfo.setManualControl(manualAdvance);
        return executeSession(path, name, sessionInfo);
    }
    
    public SessionDef createSessionInfo() {
        PeriodDef[] pinfo = new PeriodDef[periods.size()];
        for (int i=0; i<periods.size(); i++)
            pinfo[i] = createPeriodInfo((PeriodBean) periods.get(i));
        
        SessionDef sessionInfo = new SessionDef(timeoutLength, pinfo, this.isShowPastOrders(), this.isShowPastTransactions(), this.toString());
        return sessionInfo;
    }
    
    /**
     * Connect to the JMarkets server and start the session defined by the given SessionDef object.
     *  Return the session Id of the created session
     */
    private int executeSession(String path, String name, SessionDef session) {
        try {
            URL servlet = new URL(path + "/servlet/ServletReceiver");
            Request req = new Request(Request.SERVER_INIT_REQUEST);
            
            req.addIntInfo("numClients", numSubjects);
            req.addIntInfo("updateProtocol", JMConstants.HTTP_UPDATE_PROTOCOL);
            req.addIntInfo("updateTime", 100);
            req.addStringInfo("name", name);
            req.addInfo("session", session);
            
            URLConnection servletConnection = servlet.openConnection();
            
            servletConnection.setDoInput(true);
            servletConnection.setDoOutput(true);
            servletConnection.setUseCaches(false);
            servletConnection.setDefaultUseCaches(false);
            servletConnection.setRequestProperty("Content-Type", "application/octet-stream");
            
            ObjectOutputStream outputToServlet = new ObjectOutputStream(servletConnection.getOutputStream());
            
            outputToServlet.writeObject(req);
            outputToServlet.flush();
            outputToServlet.close();
            
            ObjectInputStream inputFromServlet = new ObjectInputStream(servletConnection.getInputStream());
            Response res = (Response) inputFromServlet.readObject();
            int sessionId = res.getIntInfo("sessionId");
            
            inputFromServlet.close();
            
            return sessionId;
            
        }catch(IOException e) {
            e.printStackTrace();
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /** Save this object to the given path as a file */
    public void serialize(String path) {
        try {
            File file = new File(path);
            file.createNewFile();
            
            FileOutputStream outStream = new FileOutputStream(file);
            PrintStream printer = new PrintStream(outStream);
            //ObjectOutputStream objStream = new ObjectOutputStream(outStream);
            
            log.info("Wrote session definition to xml");
            printer.print(this.toString());
            
            printer.flush();
            printer.close();
            
            //objStream.writeObject(xml);
            //objStream.close();
            outStream.close();
        }catch(IOException e) {
            System.out.println("IO error writing session to a file");
            e.printStackTrace();
        }
    }
    
    /**
     * Create a PeriodDef object out of a PeriodBean object
     */
    private PeriodDef createPeriodInfo(PeriodBean periodBean) {
        List groupBeans = periodBean.getGroups();
        List securityBeans = periodBean.getSecurities();
        List subjectBeans = periodBean.getSubjects();
        
        int openDelay = periodBean.getOpenDelay();
        int periodLength = periodBean.getPeriodLength();
        int numMarkets = periodBean.getNumSecurities();
        int numDivisions = periodBean.getNumDivisions();
        
        boolean useGui = periodBean.getUseGui();
        boolean useEffCashValidation = periodBean.getUseEffCashValidation();
        
        String[] marketTitles = new String[numMarkets];
        float[] minPrices = new float[numMarkets];
        float[] maxPrices = new float[numMarkets];
        int[] marketTime = new int[numMarkets];
        
        for (int i=0; i<marketTitles.length; i++) {
            SecurityPeriodBean secBean = (SecurityPeriodBean) securityBeans.get(i);
            
            marketTitles[i] = secBean.getTitle();
            minPrices[i] = secBean.getMinPrice();
            maxPrices[i] = secBean.getMaxPrice();
            marketTime[i] = secBean.getTimeOpen();
        }
        
        log.info("SessionBean.createPeriodInfo() is creating a new MarketDef:  useGui="+useGui+", useEffCashValidation="+useEffCashValidation);
        MarketDef minfo = new MarketDef(numMarkets, marketTitles, minPrices, maxPrices, marketTime, numDivisions, useGui, useEffCashValidation);
        
        SubjectDef sinfo = new SubjectDef(numSubjects);
        for (int i=0; i<numSubjects; i++){
            sinfo.assignGroup(i, ((SubjectPeriodBean) subjectBeans.get(i)).getGroup());
            sinfo.addAnnouncement(i, ((SubjectPeriodBean) subjectBeans.get(i)).getAnnouncement());
        }

        String[] groupTitles = new String[numGroups];
        for (int i=0; i<groupTitles.length; i++)
            groupTitles[i] = ((GroupBean) groups.get(i)).getName();
        
        GroupDef ginfo = new GroupDef(numGroups, numMarkets, groupTitles);
        for (int i=0; i<groupBeans.size(); i++) {
            GroupPeriodBean group = (GroupPeriodBean) groupBeans.get(i);
            int groupNum = group.getId();
            
            ginfo.setCashInitial(groupNum, group.getCashInitial());
            ginfo.setPayoffFunction(groupNum, getPayoffFunction(periodBean, group.getPayoffFunctionName()));
            ginfo.setClientPayoffFunction(groupNum, getClientPayoffFunction(periodBean, group.getPayoffFunctionName()));
            ginfo.setBankruptcyFunction(groupNum, getBankruptcyFunction(periodBean, group.getBankruptcyFunctionName()));
            ginfo.setBankruptcyCutoff(groupNum, group.getBankruptcyCutoff());
            ginfo.setAddCash(groupNum, group.isAddCash());
            
            for (int j=0; j<numMarkets; j++) {
                SecurityPeriodBean security = (SecurityPeriodBean) securityBeans.get(j);
                
                ginfo.setSecurityInitial(groupNum, j, security.getInitial(groupNum));
                ginfo.setSecurityShortConstraint(groupNum, j, security.getConstraint(groupNum));
                ginfo.setSecurityPrivilege(groupNum, j, security.getPrivelege(groupNum));
                ginfo.setAddSurplus(groupNum, j, security.getAddSurplus(groupNum));
                ginfo.setAddDividend(groupNum, j, security.getAddDividend(groupNum));
            }
            
        }
        String marketEngine = periodBean.getMarketEngine();
        
        PeriodDef pinfo = new PeriodDef(periodBean, marketEngine, sinfo, minfo, ginfo);
        return pinfo;
    }
    
    /** Copy the bean contents of the origin period into the destination period. Only copy
     *  the properties that are set in the period config form, not in the period map form */
    public void copyPeriod(int originPeriod, int destinationPeriod) {
        PeriodBean origin = (PeriodBean) periods.get(originPeriod);
        PeriodBean dest = (PeriodBean) periods.get(destinationPeriod);
        
        dest.setMarketEngine(new String(origin.getMarketEngine()));
        
        List originGroups = origin.getGroups();
        List destGroups = dest.getGroups();
        
        if (originGroups != null && destGroups != null) {
            int max = Math.min(originGroups.size(), destGroups.size());
            for (int i=0; i<max; i++) {
                GroupPeriodBean oGroup = (GroupPeriodBean) originGroups.get(i);
                GroupPeriodBean dGroup = (GroupPeriodBean) destGroups.get(i);
                
                dGroup.setId(oGroup.getId());
                dGroup.setCashInitial(oGroup.getCashInitial());
                dGroup.setName(new String(oGroup.getName()));
                dGroup.setPayoffFunctionName(new String(oGroup.getPayoffFunctionName()));
                dGroup.setBankruptcyFunctionName(new String(oGroup.getBankruptcyFunctionName()));
                dGroup.setBankruptcyCutoff(oGroup.getBankruptcyCutoff());
                dGroup.setAddCash(oGroup.isAddCash());
            }
        }
        
        List originSecurities = origin.getSecurities();
        List destSecurities = dest.getSecurities();
        
        if (originSecurities != null && destSecurities != null) {
            int max = Math.min(originSecurities.size(), destSecurities.size());
            for (int i=0; i<max; i++) {
                SecurityPeriodBean oSecurity = (SecurityPeriodBean) originSecurities.get(i);
                SecurityPeriodBean dSecurity = (SecurityPeriodBean) destSecurities.get(i);
                
                boolean[] buyPriveleges = new boolean[oSecurity.getBuyPriveleges().length];
                for (int j=0; j<buyPriveleges.length; j++)
                    buyPriveleges[j] = oSecurity.getBuyPriveleges()[j];
                dSecurity.setBuyPriveleges(buyPriveleges);
                
                boolean[] sellPriveleges = new boolean[oSecurity.getSellPriveleges().length];
                for (int j=0; j<sellPriveleges.length; j++)
                    sellPriveleges[j] = oSecurity.getSellPriveleges()[j];
                dSecurity.setSellPriveleges(sellPriveleges);
                
                int[] constraints = new int[oSecurity.getConstraints().length];
                for (int j=0; j<constraints.length; j++)
                    constraints[j] = oSecurity.getConstraints()[j];
                dSecurity.setConstraints(constraints);
                
                int[] initials = new int[oSecurity.getInitials().length];
                for (int j=0; j<initials.length; j++)
                    initials[j] = oSecurity.getInitials()[j];
                dSecurity.setInitials(initials);
                
                boolean[] addSurplus = new boolean[oSecurity.getAddSurplus().length];
                for (int j=0; j<addSurplus.length; j++)
                    addSurplus[j] = oSecurity.getAddSurplus()[j];
                dSecurity.setAddSurplus(addSurplus);
                
                boolean[] addDividend = new boolean[oSecurity.getAddDividend().length];
                for (int j=0; j<addDividend.length; j++)
                    addDividend[j] = oSecurity.getAddDividend()[j];
                dSecurity.setAddDividend(addDividend);
                
                dSecurity.setMaxPrice(oSecurity.getMaxPrice());
                dSecurity.setMinPrice(oSecurity.getMinPrice());
                dSecurity.setTimeOpen(oSecurity.getTimeOpen());
                dSecurity.setTitle(new String(oSecurity.getTitle()));
            }
        }
        
        List originSubjects = origin.getSubjects();
        List destSubjects = dest.getSubjects();
        
        if (originSubjects != null && destSubjects != null) {
            int max = Math.min(originSubjects.size(), destSubjects.size());
            for (int i=0; i<max; i++) {
                SubjectPeriodBean oSubject = (SubjectPeriodBean) originSubjects.get(i);
                SubjectPeriodBean dSubject = (SubjectPeriodBean) destSubjects.get(i);
                
                dSubject.setGroup(oSubject.getGroup());
                dSubject.setId(oSubject.getId());
                dSubject.setAnnouncement(oSubject.getAnnouncement());
            }
        }
        
        List originPFunctions = origin.getPayoffFunctionSpecs();
        List destPFunctions = dest.getPayoffFunctionSpecs();
        
        if (originPFunctions != null && destPFunctions != null) {
            int max = Math.min(originPFunctions.size(), destPFunctions.size());
            for (int i=0; i<max; i++) {
                PayoffFunctionBean oPayoff = (PayoffFunctionBean) originPFunctions.get(i);
                PayoffFunctionBean dPayoff = (PayoffFunctionBean) destPFunctions.get(i);
                
                dPayoff.setName(new String(oPayoff.getName()));
                dPayoff.setFieldNames(new ArrayList(oPayoff.getFieldNames()));
                
                ArrayList dvalues = new ArrayList();
                ArrayList ovalues = (ArrayList) oPayoff.getFieldValues();
                for (int j=0; j<ovalues.size(); j++) {
                    PayoffSecurityBean dState = new PayoffSecurityBean();
                    PayoffSecurityBean oState = (PayoffSecurityBean) ovalues.get(j);
                    dState.setStateValues(new ArrayList(oState.getStateValues()));
                    
                    dvalues.add(dState);
                }
                dPayoff.setFieldValues(dvalues);
            }
        }
        
        List originBFunctions = origin.getBankruptcyFunctionSpecs();
        List destBFunctions = dest.getBankruptcyFunctionSpecs();
        
        if (originBFunctions != null && destBFunctions != null) {
            int max = Math.min(originBFunctions.size(), destBFunctions.size());
            for (int i=0; i<max; i++) {
                BankruptcyFunctionBean oBank = (BankruptcyFunctionBean) originBFunctions.get(i);
                BankruptcyFunctionBean dBank = (BankruptcyFunctionBean) destBFunctions.get(i);
                
                dBank.setName(new String(oBank.getName()));
                dBank.setFieldNames(new ArrayList(oBank.getFieldNames()));
                
                ArrayList dvalues = new ArrayList();
                ArrayList ovalues = (ArrayList) oBank.getFieldValues();
                for (int j=0; j<ovalues.size(); j++) {
                    BankruptcySecurityBean dState = new BankruptcySecurityBean();
                    BankruptcySecurityBean oState = (BankruptcySecurityBean) ovalues.get(j);
                    dState.setStateValues(new ArrayList(oState.getStateValues()));
                    
                    dvalues.add(dState);
                }
                dBank.setFieldValues(dvalues);
            }
        }
    }
    
    /** Return a List with all the available payoff functions by using dynamic class loading */
    public List getAvailablePayoffFunctions() {
        try {
            List payoffFunctions = new ArrayList();
            String pckgname = "edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions";
            String name = "/edu/caltechUcla/sselCassel/projects/jMarkets/shared/functions";
            
            // Get a File object for the package
            URL url = SessionBean.class.getResource(name);
            
            URI uri = new URI(url.getPath());
            
            URL ori_url = new URL(url.getProtocol() + ":" + uri.getPath());
            
            File directory = new File(ori_url.getFile());
            
            if (directory.exists()) {
                // Get the list of the files contained in the package
                String [] files = directory.list();
                for (int i=0;i<files.length;i++) {
                    // we are only interested in .class files
                    if (files[i].endsWith(".class")) {
                        // removes the .class extension
                        String classname = files[i].substring(0,files[i].length()-6);
                        
                        try {
                            // Try to create an instance of the object
                            Object o = Class.forName(pckgname+"."+classname).newInstance();
                            
                            if (o instanceof PayoffFunction && classname.endsWith("Function")) {
                                PayoffBean payoffBean = new PayoffBean();
                                String payoffName = classname.substring(0, classname.length()-8);
                                payoffBean.setName(payoffName);
                                
                                payoffFunctions.add(payoffBean);
                            }
                        } catch (ClassNotFoundException e) {
                            log.warn("Error loading payoff function class", e);
                        } catch (InstantiationException e) {
                            //log.warn("Loaded verifier class does not have a default constructor!" + MSConstants.newline + e);
                        } catch (IllegalAccessException e) {
                            log.warn("Loaded payoff function class is not public!", e);
                        }
                    }
                }
            }
            
            //check the jar file if the verifiers are not in the file system
            else {
                try {
                    JarURLConnection conn = (JarURLConnection)url.openConnection();
                    String starts = conn.getEntryName();
                    JarFile jfile = conn.getJarFile();
                    Enumeration e = jfile.entries();
                    while (e.hasMoreElements()) {
                        ZipEntry entry = (ZipEntry)e.nextElement();
                        String entryname = entry.getName();
                        if (entryname.startsWith(starts)
                        &&(entryname.lastIndexOf('/')<=starts.length())
                        &&entryname.endsWith(".class")) {
                            String classname = entryname.substring(0,entryname.length()-6);
                            if (classname.startsWith("/"))
                                classname = classname.substring(1);
                            classname = classname.replace('/','.');
                            try {
                                // Try to create an instance of the object
                                Object o = Class.forName(classname).newInstance();
                                
                                if (o instanceof PayoffFunction && classname.endsWith("Function")) {
                                    String cname = classname.substring(classname.lastIndexOf('.')+1);
                                    
                                    PayoffBean payoffBean = new PayoffBean();
                                    String payoffName = cname.substring(0, cname.length()-8);
                                    payoffBean.setName(payoffName);
                                    
                                    payoffFunctions.add(payoffBean);
                                }
                            } catch (ClassNotFoundException cnfex) {
                                log.warn("Error loading payoff function class", cnfex);
                            } catch (InstantiationException iex) {
                                // We try to instanciate an interface
                                // or an object that does not have a
                                // default constructor
                            } catch (IllegalAccessException iaex) {
                                log.warn("Loaded payoff function class is not public!", iaex);
                            }
                        }
                    }
                } catch (IOException e) {
                    log.warn("Unknown IO Error", e);
                }
            }
            
            return payoffFunctions;
        }catch(Exception e) {
            log.error("Failed to return a list of payoff functions", e);
        }
        return null;
    }
    
    /** Return a List with all the available bankruptcy functions by using dynamic class loading */
    public List getAvailableBankruptcyFunctions() {
        try {
            List bankruptcyFunctions = new ArrayList();
            String pckgname = "edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions";
            String name = "/edu/caltechUcla/sselCassel/projects/jMarkets/shared/functions";
            
            // Get a File object for the package
            URL url = SessionBean.class.getResource(name);
            URI uri = new URI(url.getPath());
            
            URL ori_url = new URL(url.getProtocol() + ":" + uri.getPath());
            
            File directory = new File(ori_url.getFile());
            
            if (directory.exists()) {
                // Get the list of the files contained in the package
                String [] files = directory.list();
                for (int i=0;i<files.length;i++) {
                    // we are only interested in .class files
                    if (files[i].endsWith(".class")) {
                        // removes the .class extension
                        String classname = files[i].substring(0,files[i].length()-6);
                        
                        try {
                            // Try to create an instance of the object
                            Object o = Class.forName(pckgname+"."+classname).newInstance();
                            
                            if (o instanceof BankruptcyFunction && classname.endsWith("Function")) {
                                BankruptcyBean bankruptcyBean = new BankruptcyBean();
                                String bankruptcyName = classname.substring(0, classname.length()-8);
                                bankruptcyBean.setName(bankruptcyName);
                                
                                bankruptcyFunctions.add(bankruptcyBean);
                            }
                        } catch (ClassNotFoundException e) {
                            log.warn("Error loading bankruptcy function class", e);
                        } catch (InstantiationException e) {
                            //log.warn("Loaded verifier class does not have a default constructor!" + MSConstants.newline + e);
                        } catch (IllegalAccessException e) {
                            log.warn("Loaded bankruptcy function class is not public!", e);
                        }
                    }
                }
            }
            
            //check the jar file if the verifiers are not in the file system
            else {
                try {
                    JarURLConnection conn = (JarURLConnection)url.openConnection();
                    String starts = conn.getEntryName();
                    JarFile jfile = conn.getJarFile();
                    Enumeration e = jfile.entries();
                    while (e.hasMoreElements()) {
                        ZipEntry entry = (ZipEntry)e.nextElement();
                        String entryname = entry.getName();
                        if (entryname.startsWith(starts)
                        &&(entryname.lastIndexOf('/')<=starts.length())
                        &&entryname.endsWith(".class")) {
                            String classname = entryname.substring(0,entryname.length()-6);
                            if (classname.startsWith("/"))
                                classname = classname.substring(1);
                            classname = classname.replace('/','.');
                            try {
                                // Try to create an instance of the object
                                Object o = Class.forName(classname).newInstance();
                                
                                if (o instanceof BankruptcyFunction && classname.endsWith("Function")) {
                                    String cname = classname.substring(classname.lastIndexOf('.')+1);
                                    
                                    BankruptcyBean bankruptcyBean = new BankruptcyBean();
                                    String bankruptcyName = cname.substring(0, cname.length()-8);
                                    bankruptcyBean.setName(bankruptcyName);
                                    
                                    bankruptcyFunctions.add(bankruptcyBean);
                                }
                            } catch (ClassNotFoundException cnfex) {
                                log.warn("Error loading bankruptcy function class", cnfex);
                            } catch (InstantiationException iex) {
                                // We try to instanciate an interface
                                // or an object that does not have a
                                // default constructor
                            } catch (IllegalAccessException iaex) {
                                log.warn("Loaded bankruptcy function class is not public!", iaex);
                            }
                        }
                    }
                } catch (IOException e) {
                    log.warn("Unknown IO Error", e);
                }
            }
            
            return bankruptcyFunctions;
        }catch(Exception e) {
            log.error("Failed to return a list of bankruptcy functions", e);
        }
        return null;
    }
    
    /**
     * Given the specName of a payoff function specified in the period config pages, return the
     *  actual payoff function. The entire payoff / bankruptcy function algorithm works as follows:
     *
     *  In the periodMap page, users indicate how many specifications they want of each function
     *  Each TYPE of function is given a name, each SPECIFICATION of each type is given a specName
     *  For each TYPE of function chosen, a PayoffBean is created that contains a list of the specifications
     *  For each SPECIFICATION of each chosen function, a PayoffFunctionBean is created
     *  Therefore, types are identified by names and specifications are identified by specNames
     *
     *  In the periodConfig pages, the fields of each specification are filled out (this fills out the
     *  values in the PayoffFunctionBean. Also in the periodConfig pages each group is set to use a
     *  certain specification, identified by specName. This fills in the payoffFunctionName field in the
     *  GroupPeriodBean. Therefore, each GroupPeriodBean identifies the payoff function it is using
     *  by the specName.
     *
     *  When the session is started, the sessionBean (this object) looks at the GroupPeriodBean when building
     *  the actual JMarkets session objects (GroupDef, MarketDef, PeriodDef, etc.) It needs to construct
     *  the actual PayoffFunction that corresponds to the specification set out in the PayoffFunctionBean that
     *  is being used by the GroupPeriodBean. Again, the GroupPeriodBean indicates what PayoffFunctionBean
     *  it is associated with through the specName.
     *
     *  In order to create the PayoffFunction, therefore, this function first needs to find the PayoffFunctionBean
     *  that is associated with the specName. It does this by looping through all the PayoffFunctionBeans
     *  (specificaitons) used in the current session. When the correct one is found, the PayoffFunction is
     *  created, populated, and returned
     *
     */
    private PayoffFunction getPayoffFunction(PeriodBean period, String name) {
        List payoffFunctions = period.getPayoffFunctionSpecs();
        for (int i=0; i<payoffFunctions.size(); i++) {
            PayoffFunctionBean bean = (PayoffFunctionBean) payoffFunctions.get(i);
            
            if (bean.getSpecName().equals(name)) {
                PayoffFunction func = bean.getPayoffFunction();
                
                for (int j=0; j<bean.getFieldNames().size(); j++) {
                    for (int s=0; s<period.getNumStates(); s++) {
                        String field = (String) bean.getFieldNames().get(j);
                        String value = (String) ((PayoffSecurityBean) bean.getFieldValues().get(j)).getStateValues().get(s);
                        func.setField(field, s, value);
                    }
                }
                return func;
            }
        }
        return null;
    }
    
    
    private ClientPayoffFunction getClientPayoffFunction(PeriodBean period, String name) {
        List payoffFunctions = period.getPayoffFunctionSpecs();
        for (int i=0; i<payoffFunctions.size(); i++) {
            PayoffFunctionBean bean = (PayoffFunctionBean) payoffFunctions.get(i);
            
            if (bean.getSpecName().equals(name)) {
                PayoffFunction func = bean.getPayoffFunction();
                ClientPayoffFunction cpf = func.getClientPayoffFunction();
                cpf.setNumStates(period.getNumStates());
                cpf.setNumSecurities(period.getNumSecurities());
                for (int j=0; j<bean.getFieldNames().size(); j++) {
                    for (int s=0; s<period.getNumStates(); s++) {
                        String field = (String) bean.getFieldNames().get(j);
                        String value = (String) ((PayoffSecurityBean) bean.getFieldValues().get(j)).getStateValues().get(s);
                        cpf.setField(field, s, value);
                    }
                }
                return cpf;
            }
        }
        return null;
    }
    
    /** Given a bankruptcy function name, finds the corresponding bankruptcy function in the PeriodBean
     *  object of the given period. Fills the BankruptcyFunction with the fields in the
     *  BankruptcyFunctionBean. See getPayoffFunction above for more details */
    private BankruptcyFunction getBankruptcyFunction(PeriodBean period, String name) {
        List bankruptcyFunctions = period.getBankruptcyFunctionSpecs();
        for (int i=0; i<bankruptcyFunctions.size(); i++) {
            BankruptcyFunctionBean bean = (BankruptcyFunctionBean) bankruptcyFunctions.get(i);
            
            if (bean.getSpecName().equals(name)) {
                BankruptcyFunction func = bean.getBankruptcyFunction();
                
                for (int j=0; j<bean.getFieldNames().size(); j++) {
                    for (int s=0; s<period.getNumStates(); s++) {
                        String field = (String) bean.getFieldNames().get(j);
                        String value = (String) ((BankruptcySecurityBean) bean.getFieldValues().get(j)).getStateValues().get(s);
                        func.setField(field, s, value);
                    }
                }
                return func;
            }
        }
        return null;
    }
    
    /**
     * Getter for property numSubjects.
     * @return Value of property numSubjects.
     */
    public int getNumSubjects() {
        return numSubjects;
    }
    
    /**
     * Setter for property numSubjects.
     * @param numSubjects New value of property numSubjects.
     */
    public void setNumSubjects(int numSubjects) {
        this.numSubjects = numSubjects;
    }
    
    /**
     * Getter for property timeoutLength.
     * @return Value of property timeoutLength.
     */
    public int getTimeoutLength() {
        return timeoutLength;
    }
    
    /**
     * Setter for property timeoutLength.
     * @param timeoutLength New value of property timeoutLength.
     */
    public void setTimeoutLength(int timeoutLength) {
        this.timeoutLength = timeoutLength;
    }
    
    /**
     * Getter for property numGroups.
     * @return Value of property numGroups.
     */
    public int getNumGroups() {
        return numGroups;
    }
    
    /**
     * Setter for property numGroups.
     * @param numGroups New value of property numGroups.
     */
    public void setNumGroups(int numGroups) {
        this.numGroups = numGroups;
    }
    
    /**
     * Getter for property periods.
     * @return Value of property periods.
     */
    public java.util.List getPeriods() {
        return periods;
    }
    
    /**
     * Setter for property periods.
     * @param periods New value of property periods.
     */
    public void setPeriods(java.util.List periods) {
        this.periods = periods;
    }
    
    /**
     * Getter for property groups.
     * @return Value of property groups.
     */
    public java.util.List getGroups() {
        return groups;
    }
    
    /**
     * Setter for property groups.
     * @param groups New value of property groups.
     */
    public void setGroups(java.util.List groups) {
        this.groups = groups;
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
    
    /** Getter for property payoffFunctions.
     * @return Value of property payoffFunctions.
     *
     */
    public List getPayoffFunctions() {
        return payoffFunctions;
    }
    
    /** Setter for property payoffFunctions.
     * @param payoffFunctions New value of property payoffFunctions.
     *
     */
    public void setPayoffFunctions(List payoffFunctions) {
        this.payoffFunctions = payoffFunctions;
    }
    
    /** Getter for property bankruptcyFunctions.
     * @return Value of property bankruptcyFunctions.
     *
     */
    public List getBankruptcyFunctions() {
        return bankruptcyFunctions;
    }
    
    /** Setter for property bankruptcyFunctions.
     * @param bankruptcyFunctions New value of property bankruptcyFunctions.
     *
     */
    public void setBankruptcyFunctions(List bankruptcyFunctions) {
        this.bankruptcyFunctions = bankruptcyFunctions;
    }
    
    public boolean isShowPastTransactions() {
        return showPastTransactions;
    }

    public void setShowPastTransactions(boolean showPastTransactions) {
        this.showPastTransactions = showPastTransactions;
    }

    public boolean isShowPastOrders() {
        return showPastOrders;
    }

    public void setShowPastOrders(boolean showPastOrders) {
        this.showPastOrders = showPastOrders;
    }
    
    public void setManualAdvance(boolean manualAdvance){
        this.manualAdvance = manualAdvance;
    }
    
    public boolean getManualAdvance(){
        return manualAdvance;
    }
    
    public String toString(){
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
        
        return xstream.toXML(this);
    }
    
    private String name;
    private List periods;
    private List groups;
    
    private int numSubjects;
    private int numGroups;
    private boolean showPastOrders; 
    private boolean showPastTransactions; 
    private int timeoutLength;
    private boolean manualAdvance;
    
    private List payoffFunctions;
    private List bankruptcyFunctions;

}
