/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap08_Hough_Transformation;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Toolbar;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.hough.HoughTransformLines;
import imagingbook.pub.hough.HoughTransformLines.HoughLine;
import imagingbook.pub.hough.HoughTransformLines.Parameters;

import java.awt.Color;
import java.awt.geom.Point2D;

/** 
 * This plugin implements a simple Hough transform for straight lines.
 * It expects a binary (8-bit) image, with background = 0 and foreground (contour) 
 * pixels with values &gt; 0.
 * TODO: Use a vector overly instead of pixel painting (for the detected lines)
 * @author WB
 * @version 2014/02/06
*/
public class Find_Straight_Lines implements PlugInFilter {
	
	static int MaxLines = 5;			// number of strongest lines to be found
	static int MinPointsOnLine = 50;	// min. number of points on each line
	
	static boolean ShowAccumulator = true;
	static boolean ShowAccumulatorPeaks = true;
	static boolean ListStrongestLines = true;
	static boolean ShowLines = true;
	static boolean InvertOriginal = true;
	static double LineWidth = 1.0;
	static Color LineColor = Color.magenta;
	static boolean UsePickedColor = false;
	static boolean ShowReferencePoint = true;
	static Color ReferencePointColor = Color.green;
	
	ImagePlus imp;	

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		
		Parameters params = new Parameters();
			
		if (!showDialog(params)) //dialog canceled or error
			return; 

		// compute the Hough Transform and retrieve the strongest lines:
		HoughTransformLines ht = new HoughTransformLines(ip, params);
		HoughLine[] lines = ht.getLines(MinPointsOnLine, MaxLines);
		
		if (lines.length == 0) {
			IJ.log("No lines detected - check the input image and parameters!");
		}

		if (ShowAccumulator){
			IJ.log("ShowAccumulator");
			FloatProcessor accIp = ht.getAccumulatorImage();
			// flip because angle is in opposite direction (y running top to bottom):
			accIp.flipHorizontal(); 
			(new ImagePlus("HT of " + imp.getTitle(), accIp)).show();
		}
		
		if (ShowAccumulatorPeaks) {
			IJ.log("ShowAccumulatorPeaks");
			FloatProcessor maxIp = ht.getAccumulatorMaxImage();
			// flip because angle runs reverse (y running top to bottom):
			maxIp.flipHorizontal(); 
			(new ImagePlus("Maxima of " + imp.getTitle(), maxIp)).show();
		}
		
		if (ListStrongestLines) {
			for (int i = 0; i < lines.length; i++) {
				IJ.log(i + ": " + lines[i].toString());
			}
		}
		
		if (ShowLines) {
			ColorProcessor lineIp = ip.convertToColorProcessor();
			if (InvertOriginal) lineIp.invert();
			if (UsePickedColor) {
				IJ.log("use picked color");
				lineIp.setColor(Toolbar.getForegroundColor());
			}
			else {
				lineIp.setColor(LineColor);
			}
			
			for (HoughLine hl : lines){
				hl.draw(lineIp, LineWidth);
			}
			
			if (ShowReferencePoint) {
				lineIp.setColor(ReferencePointColor);
				Point2D pc = ht.getReferencePoint();
				int uu = (int) Math.round(pc.getX());
				int vv = (int) Math.round(pc.getY());
				drawCross(lineIp, uu, vv, 2);
			}
			
			(new ImagePlus(imp.getShortTitle()+"-lines", lineIp)).show();
		}
	}
	
	private void drawCross(ImageProcessor ip, int uu, int vv, int size) {
		ip.drawLine(uu - size, vv, uu + size, vv);
		ip.drawLine(uu, vv - size, uu, vv + size);
	}
	
	
	private boolean showDialog(Parameters params) {
		// display dialog , return false if canceled or on error.
		GenericDialog dlg = new GenericDialog("Hough Transform (lines)");
		dlg.addNumericField("Angle steps", params.nAng, 0);
		dlg.addNumericField("Radius steps", params.nRad, 0);
		dlg.addNumericField("Max. number of lines to show", MaxLines, 0);
		dlg.addNumericField("Min. number of points per line", MinPointsOnLine, 0);
		dlg.addCheckbox("Show accumulator image", ShowAccumulator);
		dlg.addCheckbox("Show accumulator peaks", ShowAccumulatorPeaks);
		dlg.addCheckbox("List strongest lines", ListStrongestLines);
		dlg.addCheckbox("Show lines", ShowLines);
		dlg.addNumericField("Line width", LineWidth, 1);
		dlg.addCheckbox("Draw with picked color", UsePickedColor);
		dlg.addCheckbox("Show reference point", ShowReferencePoint);
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;
		params.nAng = (int) dlg.getNextNumber();
		params.nRad = (int) dlg.getNextNumber();
		MaxLines = (int) dlg.getNextNumber();
		MinPointsOnLine = (int) dlg.getNextNumber();
		ShowAccumulator = dlg.getNextBoolean();
		ShowAccumulatorPeaks = dlg.getNextBoolean();
		ListStrongestLines = dlg.getNextBoolean();
		ShowLines = dlg.getNextBoolean();
		LineWidth = dlg.getNextNumber();
		UsePickedColor = dlg.getNextBoolean();
		ShowReferencePoint = dlg.getNextBoolean();
		if(dlg.invalidNumber()) {
			IJ.showMessage("Error", "Invalid input number");
			return false;
		}
		return true;
	}

}





	
	
