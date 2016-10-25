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
 * PenalvaPayoffFunction.java
 *
 * Created on August 16, 2005, 12:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.functions;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.Trader;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.GroupDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.PeriodDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SessionDef;
import edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def.SubjectDef;
import java.util.StringTokenizer;
import edu.caltechUcla.sselCassel.projects.jMarkets.client.functions.*;

/**
 *
 * @author Raj
 */
public class PenalvaPayoffFunction implements PayoffFunction {
    
    /** Creates a new instance of PenalvaPayoffFunction */
    public PenalvaPayoffFunction() {
    }
    
    public float getPayoff(int subject, int period, SessionDef session, Trader[][] traders) {
        PeriodDef inputPeriodDef = session.getPeriod(inputPeriod);
        Trader inputTrader = traders[inputPeriod][subject];
        GroupDef groupDef = inputPeriodDef.getGroupInfo();
        SubjectDef subjectDef = inputPeriodDef.getSubjectInfo();
        
        int[] initialHoldings = inputTrader.getInitialHoldings();
        int[] finalHoldings = inputTrader.getHoldings();
        int[] numPurchases = inputTrader.getTotalPurchases();
        int[][] numSales = inputTrader.getNumSales();
        int[] numSalesToLosers = getNumSalesToLosers(numSales, subjectDef, groupDef);
        float cash = inputTrader.getCash();
        
        float securityPayoffs = 0;
        for (int i=0; i<initialHoldings.length; i++) {
            float sp = getSecurityPayoff(i, initialHoldings[i], finalHoldings[i], numPurchases[i], numSalesToLosers[i]);
            System.out.println("Found security payoff " + sp + " for security " + i + " with IH " + initialHoldings[i] + ", FH " + finalHoldings[i] + ", NP " + numPurchases[i] + ", NSTL " + numSalesToLosers[i]);
            
            securityPayoffs += sp;
        }
        
        float cashPayoff = cash * cashConstraint;
        
        if (isLoser(subject, subjectDef, groupDef))
            return securityPayoffs + cashPayoff - loserPenalty;
        else
            return securityPayoffs + cashPayoff;
    }
    
    private float getSecurityPayoff(int security, int initialHoldings, int finalHoldings, int numPurchases, int numSalesToLosers) {
        if (!flags[security])
            return finalHoldings * constants[security][0];
        else
            return (initialHoldings * constants[security][0]) + (numPurchases * constants[security][1]) + (numSalesToLosers * constants[security][2]);
    }
    
    /** Get the number of sales this trader made to losers in each market */
    private int[] getNumSalesToLosers(int[][] numSales, SubjectDef subjectDef, GroupDef groupDef) {
        int[] loserSales = new int[numSales.length];
        
        for (int m=0; m<loserSales.length; m++) {
            for (int s=0; s<numSales[m].length; s++) {
                if (isLoser(s, subjectDef, groupDef)) {
                    loserSales[m] += numSales[m][s];
                    System.out.println("Adding loser sales " + numSales[m][s] + " from selling to subject " + s);
                }
            }
        }
        
        return loserSales;
    }
    
    /** Return true if the subject identified by the given subject id is in a loser group
     *  for the input period */
    private boolean isLoser(int subjectId, SubjectDef subjectDef, GroupDef groupDef) {
        int groupNum = subjectDef.getGroup(subjectId);
        String name = groupDef.getGroupTitle(groupNum);
        
        if (lossGroupNames == null)  {
            System.out.println("No loss groups specified, is null");
            return false;
        }
        
        if (lossGroupNames.length < 1) {
            System.out.println("No loss groups specified, is empty");
            return false;
        }
        
        for (int i=0; i<lossGroupNames.length; i++) {
            if (name.equalsIgnoreCase(lossGroupNames[i]))
                return true;
        }
        
        return false;
    }
    
    public String[] getFields(int periodNum, int[] numSecurities, int[] numStates) {
        int maxSecurities = 0;
        for (int i=0; i<numSecurities.length; i++) {
            if (numSecurities[i] > maxSecurities)
                maxSecurities = numSecurities[i];
        }
        
        constants = new float[maxSecurities][3];
        flags = new boolean[maxSecurities];
        
        String[] fields = new String[5 + 4 * maxSecurities];
        fields[0] = "Period";
        fields[1] = "Loss Groups (Comma Separate Names)";
        
        for (int i=2; i < (2 + 3 * maxSecurities); i += 3) {
            fields[i] = "C (" + ((i - 2) / 3) + ", 1)";
            fields[i + 1] = "C (" + ((i - 2) / 3) + ", 2)";
            fields[i + 2] = "C (" + ((i - 2) / 3) + ", 3)";
        }
        
        for (int i=(2 + 3 * maxSecurities); i< (2 + 4 * maxSecurities); i++)
            fields[i] = "Q (" + (i - 2 - 3 * maxSecurities) + ")";
        
        fields[fields.length - 3] = "Cash Constraint";
        fields[fields.length - 2] = "Loser Penalty";
        fields[fields.length - 1] = "Mask Payoff";
        
        return fields;
    }
    
    public String getName() {
        return "Penalva";
    }
    
    /** Only works for one state, state 0 */
    public void setField(String field, int state, String value) {
        if(field == null || value == null || state > 0)
            return;
        
        if (field.equalsIgnoreCase("Period")) {
            inputPeriod = Integer.parseInt(value.trim());
            return;
        }
        
        if (field.startsWith("Loss Groups")) {
            StringTokenizer tokenizer = new StringTokenizer(value, ",");
            lossGroupNames = new String[tokenizer.countTokens()];
            int index = 0;
            
            while (tokenizer.hasMoreTokens()) {
                lossGroupNames[index] = tokenizer.nextToken().trim();
                System.out.println("Stored loss group: " + lossGroupNames[index]);
                index++;
            }
            
            return;
        }
        
        if (field.startsWith("C") && !field.startsWith("Cash")) {
            StringTokenizer tokenizer = new StringTokenizer(field, "(,)");
            tokenizer.nextToken();
            
            int security = Integer.parseInt(tokenizer.nextToken().trim());
            int index = Integer.parseInt(tokenizer.nextToken().trim()) - 1;
            constants[security][index] = Float.parseFloat(value.trim());
            
            System.out.println("Stored Constants (" + security + "," + index + ") = " + constants[security][index]);
            
            return;
        }
        
        if (field.startsWith("Q")) {
            StringTokenizer tokenizer = new StringTokenizer(field, "()");
            tokenizer.nextToken();
            
            int index = Integer.parseInt(tokenizer.nextToken());
            int v = Integer.parseInt(value.trim());
            flags[index] = (v == 1);
            
            System.out.println("Stored Flag (" + index + ") = " + flags[index]);
            
            return;
        }
        
        if (field.startsWith("Cash")) {
            cashConstraint = Float.parseFloat(value.trim());
            return;
        }
        
        if (field.startsWith("Loser Penalty")) {
            loserPenalty = Float.parseFloat(value.trim());
            return;
        }
        
        if (field.startsWith("Mask Payoff")) {
            int bit = Integer.parseInt(value);
            maskPayoff = (bit == 1);
        }
    }
    
    public String getPayoffMask() {
        if (maskPayoff)
            return "Payoff will be revealed at end of last period";
        else
            return null;
    }
    
    public ClientPayoffFunction getClientPayoffFunction(){
        return new GenericClientPayoffFunction();
    }
    
    
    private boolean maskPayoff;
    private String[] lossGroupNames;
    private float[][] constants;
    private boolean[] flags;
    private int inputPeriod;
    private float cashConstraint;
    private float loserPenalty;
}
