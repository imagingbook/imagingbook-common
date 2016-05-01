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

import java.awt.color.ColorSpace;

/*
 * This class implements a D65-based sRGBcolor space without performing
 * chromatic adaptation between D50 and D65, as required by Java's profile 
 * connection space. Everything is D65!
 */

public class sRgb65ColorSpace extends ColorSpace {

	private static final long serialVersionUID = 1L;

	public sRgb65ColorSpace() {
		super(ColorSpace.TYPE_RGB, 3);
	}

	// XYZ (D65) -> sRGB
	public float[] fromCIEXYZ(float[] xyz) {
		final double X = xyz[0];
		final double Y = xyz[1];
		final double Z = xyz[2];
		
		// XYZ -> RGB (linear components)
		final double r =  3.240479 * X + -1.537150 * Y + -0.498535 * Z;
		final double g = -0.969256 * X +  1.875992 * Y +  0.041556 * Z;
		final double b =  0.055648 * X + -0.204043 * Y +  1.057311 * Z;
		// RGB -> sRGB (nonlinear components)
		float rr = (float) sRgbUtil.gammaFwd(r);
		float gg = (float) sRgbUtil.gammaFwd(g);
		float bb = (float) sRgbUtil.gammaFwd(b);			
		return new float[] {rr,gg,bb} ;
	}

	public float[] fromRGB(float[] srgb) {
		return srgb;
	}

	// sRGB -> XYZ (D65)
	public float[] toCIEXYZ(float[] srgb) {
		// get linear rgb components:
		final double r = sRgbUtil.gammaInv(srgb[0]);
		final double g = sRgbUtil.gammaInv(srgb[1]);
		final double b = sRgbUtil.gammaInv(srgb[2]);
		
		// convert to XYZ (Poynton / ITU 709) 
		final float x = (float) (0.412453 * r + 0.357580 * g + 0.180423 * b);
		final float y = (float) (0.212671 * r + 0.715160 * g + 0.072169 * b);
		final float z = (float) (0.019334 * r + 0.119193 * g + 0.950227 * b);
		return new float[] {x, y, z};
	}

	@Override
	public float[] toRGB(float[] srgb) {
		return srgb;
	}


}
