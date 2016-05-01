/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap21_Geometrische_Operationen;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

public class Transform_Affine_Matrix implements PlugInFilter {
	
 
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
    	
    	if (showDialog())
            displayValues();


//    	Point2D p1 = new Point(0,0);
//    	Point2D p2 = new Point(400,0);
//    	Point2D p3 = new Point(400,400);
//		 
//    	Point2D q1 = new Point(0,60);
//    	Point2D q2 = new Point(400,20);
//    	Point2D q3 = new Point(300,400);  	
//		
//		AffineMapping map = new AffineMapping(p1, p2, p3, q1, q2, q3);
//		
//		map.applyTo(ip, PixelInterpolator.Method.Bicubic);
    }
    

    // Dialog example taken from http://rsbweb.nih.gov/ij/plugins/download/Dialog_Grid_Demo.java
    
    String[][] fieldNames = {
    		{ "a1", "a2", "a3" },
    		{ "a4", "a5", "a6" }};
    
    double[][] fieldValues = {
    		{ 1, 0, 0 },
    		{ 0, 1, 0 }};
	int gridWidth = fieldValues[0].length;
	int gridHeight = fieldValues.length;
	int gridSize = gridWidth * gridHeight;
	
	TextField[] tf = new TextField[gridSize];
	double[] value = new double[gridSize];

	boolean showDialog() {
		GenericDialog gd = new GenericDialog("Grid Example");
		gd.addPanel(makePanel(gd));
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		getValues();
		return true;
	}

	Panel makePanel(GenericDialog gd) {
		Panel panel = new Panel();
		panel.setLayout(new GridLayout(gridHeight, gridWidth * 2));
		int i = 0;
		for (int r = 0; r < gridHeight; r++) {
			for (int c = 0; c < gridWidth; c++) {
				tf[i] = new TextField("" + fieldValues[r][c]);
				panel.add(tf[i]);
				panel.add(new Label(fieldNames[r][c]));
				i++;
			}
		}
		return panel;
	}
	
	void getValues() {
		int i = 0; 
		for (int r = 0; r < gridHeight; r++) {
			for (int c = 0; c < gridWidth; c++) {
			String s = tf[i].getText();
			fieldValues[r][c] = getValue(s);
			i++;
			}
		}
	}
	
	void displayValues() {
		int i = 0; 
		for (int r = 0; r < gridHeight; r++) {
			for (int c = 0; c < gridWidth; c++) {
				IJ.log(i + "  " + fieldValues[r][c]);
				i++;
			}
		}
	}

	double getValue(String theText) {
		Double d;
		try {
			d = new Double(theText);
		} catch (NumberFormatException e) {
			d = null;
		}
		return (d == null) ? Double.NaN : d.doubleValue();
	}

}
