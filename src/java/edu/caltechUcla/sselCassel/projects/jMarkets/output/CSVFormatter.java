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
 * CSVFormatter.java
 *
 * Created on January 30, 2005, 5:58 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.output;

/**
 *
 * @author  Raj Advani
 */
public class CSVFormatter implements OutputFormatter {
    
    /** Creates a new instance of CSVFormatter */
    public CSVFormatter() {
    }
    
    public String formatTable(OutputTable table) {
        String title = table.getTitle();
        String[] headers = table.getHeaders();
        String[][] data = table.getData();
        
        String newline =  System.getProperty("line.separator");
        String separator = ",";
        
        StringBuffer sb = new StringBuffer();
        sb.append(title).append(newline);
        
        for (int i=0; i<headers.length - 1; i++)
            sb.append(headers[i]).append(separator);
        sb.append(headers[headers.length - 1]);
        sb.append(newline);

        if(data != null){
            for (String[] aData : data) {
                for (int j = 0; j < aData.length - 1; j++) {
                    sb.append(aData[j]).append(separator);
                }
                sb.append(aData[aData.length - 1]);
                sb.append(newline);
            }
        }

        return sb.toString();
    }
}
