/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Kap14_Colorimetrische_Farbraeume;

import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_ProfileRGB;
import java.io.IOException;

import ij.IJ;
import ij.plugin.PlugIn;
import imagingbook.lib.ij.IjUtils;
import imagingbook.lib.math.Matrix;
import imagingbook.lib.settings.PrintPrecision;

public class ICC_Profile_Example implements PlugIn {

	public void run(String arg0) {
		// TODO Auto-generated method stub
		String path = IjUtils.askForOpenPath("Select an ICC profile file (.icc or .icm)");
		if (path == null) return;
		
		IJ.log("path = " + path);
		ICC_Profile iccProfile = null;
		
		try {iccProfile = ICC_ProfileRGB.getInstance(path);
		} catch (IOException e) {}
		
		if (iccProfile == null) {
			IJ.error("Could not open ICC profile file " + path);
			return;
		}
		
		PrintPrecision.set(5);
		
		ICC_ColorSpace iccColorSpace = new ICC_ColorSpace(iccProfile);
		int nComp = iccColorSpace.getNumComponents();
		if (nComp != 3) {
			IJ.error("Color space must have 3 components, this one has " + nComp);
			return;
		}
		
		IJ.log("scannerCs = " + iccColorSpace);
		IJ.log("scannerCs type = " + iccColorSpace.getType());
		IJ.log("scannerCs ncomp = " + iccColorSpace.getNumComponents());
		
	
		// specify a device-specific color:
		float[] deviceColor = {0.77f, 0.13f, 0.89f};
		//float[] deviceColor = {0.0f, 0.0f, 0.0f};
		IJ.log("device color = " + Matrix.toString(deviceColor));
		
		// convert to sRGB:
		float[] sRGBColor = iccColorSpace.toRGB(deviceColor);
		IJ.log("sRGB = " + Matrix.toString(sRGBColor));
		
		// convert to (D50-based) XYZ:
		float[] XYZColor = iccColorSpace.toCIEXYZ(deviceColor);
		IJ.log("XYZ = " + Matrix.toString(XYZColor));
		
		deviceColor = iccColorSpace.fromCIEXYZ(XYZColor);
		IJ.log("device color (check) = " + Matrix.toString(deviceColor));
		
		// list sRGB Values:
		for (int ri = 0; ri <= 10; ri++) {
			for (int gi = 0; gi <= 10; gi++) {
				for (int bi = 0; bi <= 10; bi++) {
					float[] devCol = {ri * 0.1f, gi * 0.1f, bi * 0.1f};
					float[] sRGB = iccColorSpace.toRGB(devCol);
					IJ.log(Matrix.toString(devCol) + " -> " + Matrix.toString(sRGB));
				}
			}
		}
		
		
	}

}
