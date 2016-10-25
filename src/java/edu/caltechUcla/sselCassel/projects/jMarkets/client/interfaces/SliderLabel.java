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

package edu.caltechUcla.sselCassel.projects.jMarkets.client.interfaces;

import java.awt.*;
import javax.swing.*;

class SliderLabel extends JLabel{
    private int numBids, numOffers;
    
    public SliderLabel(){
        super();
    }
    
    public SliderLabel(String s ){
        super(s);
    }
    
    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);
        //setText(" ");
        if( numBids > 0 ){
            for( int i=0; i<numBids; i++ ){
                g.setColor(new Color(204, 153, 0));
                g.setFont(super.getFont());
                g.drawString("^", 0,8+4*i);
            }
        } else if( numOffers > 0 ){
            for( int i=0; i<numOffers; i++ ){
                g.setColor(Color.BLUE);
                g.drawString("^", 0,8+4*i);
            }
        }
        
    }
    
    public void setNumBids(int nb ){
        numBids = nb;
    }
    
    public void setNumOffers(int no){
        numOffers=no;
    }
};