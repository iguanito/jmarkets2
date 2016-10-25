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
 * PayoffUpdate.java
 *
 * Created on August 17, 2005, 7:34 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.updates;

/**
 *
 * @author Raj
 */
public class PayoffUpdate {
    
    /** Creates a new instance of PayoffUpdate */
    public PayoffUpdate(float[] payoffs, String[] masks) {
        this.payoffs = payoffs;
        this.masks = masks;
    }
    
    public String[] getMasks() {
        return masks;
    }
    
    public void setMask(String[] masks) {
        this.masks = masks;
    }
    
    public float[] getPayoffs() {
        return payoffs;
    }
    
    public void setPayoffs(float[] payoffs) {
        this.payoffs = payoffs;
    }
    
    private String[] masks;
    private float[] payoffs;
}

