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
 * MetricsUpdate.java
 *
 * Created on February 6, 2005, 10:30 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.updates;

import java.io.Serializable;

/**
 *
 * @author  Raj Advani
 */
public class MetricsUpdate implements Serializable {
    
    public MetricsUpdate(int sessionId, int iterations, int numTrans, float average) {
        this.sessionId = sessionId;
        this.iterations = iterations;
        this.numTrans = numTrans;
        this.average = average;
    }
    
    public int sessionId;
    public int iterations;
    public int numTrans;
    public float average;
}
