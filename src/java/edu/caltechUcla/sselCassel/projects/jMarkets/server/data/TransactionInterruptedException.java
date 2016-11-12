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
 * TransactionInterruptedException.java
 *
 * Created on October 15, 2004, 6:56 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.data;

/**
 *
 * @author  Raj Advani
 */
public class TransactionInterruptedException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>TransactionInterruptedException</code> without detail message.
     */
    public TransactionInterruptedException() {
        deadlock = false;
    }
    
    
    /**
     * Constructs an instance of <code>TransactionInterruptedException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TransactionInterruptedException(String msg) {
        super(msg);
        deadlock = false;
    }
    
    /**
     * Getter for property deadlock.
     * @return Value of property deadlock.
     */
    public boolean isDeadlock() {
        return deadlock;
    }
    
    /**
     * Setter for property deadlock.
     * @param deadlock New value of property deadlock.
     */
    public void setDeadlock(boolean deadlock) {
        this.deadlock = deadlock;
    }
    
    private boolean deadlock;
}
