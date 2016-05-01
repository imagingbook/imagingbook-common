/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap07_Eckpunkte;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.corners.Corner;
import imagingbook.pub.corners.HarrisCornerDetector;

import java.awt.Color;
import java.util.List;

/**
 * This plugin implements the Harris corner detector. It calculates the corner
 * positions and shows the result in a new color image.
 * 
 * @version 2013/08/22
 */
public class Find_Corners implements PlugInFilter {
	
	static int nmax = 0;						// number of corners to show
	static int cornerSize = 2;					// size of cross-markers
	static Color cornerColor = Color.green;		// color of cross markers
	
	ImagePlus im;

    public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_ALL + NO_CHANGES;
    }
    
    public void run(ImageProcessor ip) {
    	HarrisCornerDetector.Parameters params = new HarrisCornerDetector.Parameters();
		if (!showDialog(params)) {
			return;
		}
		
		HarrisCornerDetector cd = new HarrisCornerDetector(ip, params);
		List<Corner> corners = cd.findCorners();
		
		ColorProcessor R = ip.convertToColorProcessor();
		drawCorners(R, corners);
		(new ImagePlus("Corners from " + im.getShortTitle(), R)).show();
    }
    
	private boolean showDialog(HarrisCornerDetector.Parameters params) {
		// display dialog , return false if canceled or on error.
		GenericDialog dlg = new GenericDialog("Harris Corner Detector");
		dlg.addNumericField("Alpha", params.alpha, 3);
		dlg.addNumericField("Threshold", params.tH, 0);
		dlg.addCheckbox("Clean up corners", params.doCleanUp);
		dlg.addNumericField("Corners to show (0 = show all)", nmax, 0);
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;	
		params.alpha = dlg.getNextNumber();
		params.tH = (int) dlg.getNextNumber();
		params.doCleanUp = dlg.getNextBoolean();
		nmax = (int) dlg.getNextNumber();
		if(dlg.invalidNumber()) {
			IJ.error("Input Error", "Invalid input number");
			return false;
		}	
		return true;
	}
	
	//-------------------------------------------------------------------
	
	// Brightens the image ip. May not work with ShortProcessor and FloatProcessor
	@SuppressWarnings("unused")
	private void brighten(ImageProcessor ip) {	
		int[] lookupTable = new int[256];
		for (int i = 0; i < 256; i++) {
			lookupTable[i] = 128 + (i / 2);
		}
		ip.applyTable(lookupTable); 
	}
	
	private void drawCorners(ImageProcessor ip, List<Corner> corners) {
		ip.setColor(cornerColor);
		int n = 0;
		for (Corner c: corners) {
			drawCorner(ip, c);
			n = n + 1;
			if (nmax > 0 && n >= nmax) 
				break;
		}
	}
	
	private void drawCorner(ImageProcessor ip, Corner c) {
		int size = cornerSize;
		int x = Math.round(c.getX());
		int y = Math.round(c.getY());
		ip.drawLine(x - size, y, x + size, y);
		ip.drawLine(x, y - size, x, y + size);
	}
}
