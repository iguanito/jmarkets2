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
 * SubjectDef.java
 *
 * Created on July 6, 2004, 6:16 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.def;

/**
 *
 * @author  Raj Advani
 */
public class SubjectDef implements java.io.Serializable {
    
    /**
     * Creates a new instance of SubjectDef 
     */ 
    public SubjectDef(int numSubjects) {
        subjectGroups = new int[numSubjects];
        databaseIds = new int[numSubjects];
        names = new String[numSubjects];
        this.announcements = new String[numSubjects];
    }
    
    public void assignGroup(int subject, int group) {
        subjectGroups[subject] = group;
    }

    public void addAnnouncement(int subject, String announcement){
        this.announcements[subject] = announcement;
    }
    
    public int getGroup(int subject) {
        return subjectGroups[subject];
    }

    public String getAnnouncement(int subject){
        return this.announcements[subject];
    }

    public int[] getAllGroups() {
        return subjectGroups;
    }
    
    public int getNumSubjects() {
        return subjectGroups.length;
    }
    
    /**
     * Get the ID of the subject who has the given database ID. Return -1
     *  if that client is not attached to this SubjectDef 
     */
    public int getId(int subjectId_db) {
        for (int i=0; i<databaseIds.length; i++) {
            if (databaseIds[i] == subjectId_db)
                return i;
        }
        return -1;
    }
    
    public int getDatabaseId(int subject) {
        return databaseIds[subject];
    }
    
    public void setDatabaseId(int subject, int dbId) {
        databaseIds[subject] = dbId;
    }
    
    public int[] getDatabaseIds() {
        return databaseIds;
    }
    
    public String getName(int subject) {
        return names[subject];
    }
    
    public void setName(int subject, String name) {
        names[subject] = name;
    }
    
    /** Called if a game is started before all clients are connected. Keeps only
     *  the first numSubjects players and trims the rest */
    public void trimSubjects(int numSubjects) {
        int[] newSubjects = new int[numSubjects];
        int[] newDatabaseIds = new int[numSubjects];
        String[] newNames = new String[numSubjects];
        String [] newAnnouncements = new String[numSubjects];
        
        if (numSubjects > subjectGroups.length)
            return;
        
        for (int i=0; i<numSubjects; i++) {
            newSubjects[i] = subjectGroups[i];
            newDatabaseIds[i] = databaseIds[i];
            newNames[i] = names[i];
            newAnnouncements[i] = announcements[i];
        }
        
        this.subjectGroups = newSubjects;
        this.databaseIds = newDatabaseIds;
        this.names = newNames;
        this.announcements = newAnnouncements; 
    }
         
    /**
     * Getter for property names.
     * @return Value of property names.
     */
    public java.lang.String[] getNames() {
        return this.names;
    }
    
    /**
     * Setter for property names.
     * @param names New value of property names.
     */
    public void setNames(java.lang.String[] names) {
        this.names = names;
    }
    
    /** This array, indexed by player ID, contains the group of each subject */
    private int[] subjectGroups;
    
    /** Contains the database ID of each subject */
    private int[] databaseIds;
    
    /** Contains the name of each subject */
    private String[] names;

    private String[] announcements;
}
