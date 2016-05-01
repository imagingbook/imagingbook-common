/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/

package imagingbook.pub.color.image;

/**
 * This is a utility class with static methods for gamma correction
 * used by LabColorSpace and LuvColorSpace color spaces.
 * Implemented with double values for better accuracy.
 * Should be modified to implement a a subclass of ColorSpace.
 */
public abstract class sRgbUtil {
	
	// specs according to official sRGB standard:
	static final double s = 12.92;
	static final double a0 = 0.0031308;
	static final double b0 = s * a0;	// 0.040449936
	static final double d = 0.055;
	static final double gamma = 2.4;
	
    public static double gammaFwd(double lc) {	// input: linear RGB component value in [0,1]
		return (lc <= a0) ?
			(lc * s) :
			((1 + d) * Math.pow(lc, 1 / gamma) - d);
    }
    
    public static double gammaInv(double nc) {	// input: nonlinear sRGB component value in [0,1]
    	return (nc <= b0) ?
    		(nc / s) :
			Math.pow((nc + d) / (1 + d), gamma);
    }
    
	public static float[] sRgbToRgb(float[] srgb) { // all components in [0,1]
		float R = (float) sRgbUtil.gammaInv(srgb[0]);
		float G = (float) sRgbUtil.gammaInv(srgb[1]);
		float B = (float) sRgbUtil.gammaInv(srgb[2]);
		return new float[] { R, G, B };
	}

	public static float[] rgbToSrgb(float[] rgb) { // all components in [0,1]
		float sR = (float) sRgbUtil.gammaFwd(rgb[0]);
		float sG = (float) sRgbUtil.gammaFwd(rgb[1]);
		float sB = (float) sRgbUtil.gammaFwd(rgb[2]);
		return new float[] { sR, sG, sB };
	}
    
    
	public static void main(String[] args) {
		for (int i = 0; i < 20; i++) {
			double lc = Math.random();
			double nc = gammaFwd(lc);
			System.out.format("lc = %.8f,  nc = %.8f, check = %.8f\n", lc, nc, lc-gammaInv(nc));
		}
		System.out.println("" + (s * a0));

	}
    
}
