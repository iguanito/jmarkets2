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
 * PriceChart.java
 *
 * Created on September 2, 2004, 3:26 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.interfaces;

import java.awt.Font;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.XYLineAndShapeRenderer;
import org.jfree.data.XYDataItem;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;



/**
 *
 * @author  Raj Advani
 */
public class PriceChart {
    
    /** Creates a new instance of PriceChart */
    public PriceChart() {
        lines = new Hashtable();
    }
    
    public void test() {
        XYSeries testSeries = new XYSeries("Test Series");
        testSeries.add(10, 10);
        testSeries.add(20, 20);
        testSeries.add(30, 30);
        
        XYSeriesCollection collection = new XYSeriesCollection(testSeries);
        
        NumberAxis yAxis = new NumberAxis("Y Axis");
        NumberAxis xAxis = new NumberAxis("X Axis");
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        XYPlot plot = new XYPlot(collection, xAxis, yAxis, renderer);
        
        JFreeChart chart = new JFreeChart("Price Chart", new Font("Arial", 0, 12), plot, true);
        ChartPanel chartPanel = new ChartPanel(chart, false, false, false, false, false);
        
        JFrame frame = new JFrame();
        frame.getContentPane().add(chartPanel);
        frame.setVisible(true);
        frame.pack();
        
    }
    
    public static void main(String args[]) {
        PriceChart chart = new PriceChart();
        //chart.test();
        
        chart.addSecurity("Stock");
        chart.addPoint("Stock", 10, 10);
        chart.addPoint("Stock", 20, 20);
        chart.addPoint("Stock", 30, 30);
        
        float[][] points = new float[5][2];
        points[0][0] = 2;
        points[0][1] = 2;
        points[1][0] = 3;
        points[1][1] = 3;
        points[2][0] = 4;
        points[2][1] = 4;
        points[3][0] = 5;
        points[3][1] = 5;
        points[4][0] = 6;
        points[4][1] = 6;
        
        chart.addSecurity("Bonds", points);
        
        JFrame frame = new JFrame();
        frame.getContentPane().add(chart.getChartPanel());
        frame.setVisible(true);
        frame.pack();
    }
    
    
    public JPanel getChartPanel() {
        XYSeriesCollection collection = new XYSeriesCollection();
        
        Enumeration en = lines.elements();
        while (en.hasMoreElements()) {
            XYSeries series = (XYSeries) en.nextElement();
            collection.addSeries(series);
        }

        NumberAxis yAxis = new NumberAxis("Price");
        NumberAxis xAxis = new NumberAxis("Time");
 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        XYPlot plot = new XYPlot(collection, xAxis, yAxis, renderer);
        
        JFreeChart chart = new JFreeChart("Price Chart", new Font("Arial", 0, 12), plot, true);
        ChartPanel chartPanel = new ChartPanel(chart, false, false, false, false, false);

        return chartPanel;
    }
    
    public void addSecurity(String name) {
        XYSeries security = new XYSeries(name);
        lines.put(name, security);
    }
    
    public void addSecurity(String name, float[][] points) {
        XYSeries security = new XYSeries(name);
        for (int i=0; i<points.length; i++)
            security.add(points[i][0], points[i][1], true);
        lines.put(name, security);
    }
    
    public void addPoint(String name, float time, float price) {
        XYSeries security = (XYSeries) lines.get(name);
        if (security != null)
            security.add(time, price);
    }
    
    /**
     * Getter for property lines.
     * @return Value of property lines.
     */
    public java.util.Hashtable getLines() {
        return lines;
    }
    
    /**
     * Setter for property lines.
     * @param lines New value of property lines.
     */
    public void setLines(java.util.Hashtable lines) {
        this.lines = lines;
    }
    
    /** Constructs the securities Vector, used for transferring price chart data from
     *  server to client */
    public Vector getSecurities() {
        securities = new Vector();
        
        Enumeration en = lines.elements();
        while (en.hasMoreElements()) {
            Vector secVect = new Vector();  
            
            XYSeries series = (XYSeries) en.nextElement();
            String name = series.getName();
            secVect.add(name);
            
            for (int i=0; i<series.getItemCount(); i++) {
                XYDataItem point = series.getDataItem(i);
                float time = ((Number) point.getX()).floatValue();
                float price = ((Number) point.getY()).floatValue();
                
                float[] p = new float[2];
                p[0] = time;
                p[1] = price;
                                
                secVect.add(p);
            }
            securities.add(secVect);
        }
        return securities;
    }
    
    /** Parses the securities Vector into the price chart */
    public void setSecurities(Vector securities) {
        for (int i=0; i<securities.size(); i++) {
            Vector secVect = (Vector) securities.get(i);
            String name = (String) secVect.get(0);
            
            addSecurity(name);
            for (int j=1; j<secVect.size(); j++) {
                float[] point = (float[]) secVect.get(j);
                addPoint(name, point[0], point[1]);
            }
        }
    }
    
    /** A Hashtable of XYSeries objects that contain the lines */
    private Hashtable lines;
    
    /** A Vector that contains the securities. Each security is a Vector itself
     *  with the first entry being the security name and each entry thereafter being
     *  a point */
    private Vector securities;
}
