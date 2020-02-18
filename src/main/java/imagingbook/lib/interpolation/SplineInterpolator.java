/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.interpolation;

import imagingbook.lib.image.ImageAccessor;

public class SplineInterpolator extends PixelInterpolator {
	private final double a;	
	private final double b;

	
	public SplineInterpolator(ImageAccessor.Scalar ia) {
		this(0.5, 0.0); // default is a Catmull-Rom spline
	}
	
	public SplineInterpolator(double a, double b) {
		super();
		this.a = a;
		this.b = b;
	}
	
	
	@Override
	public float getInterpolatedValue(ImageAccessor.Scalar ia, double x0, double y0) {
		final int u0 = (int) Math.floor(x0);	//use floor to handle negative coordinates too
		final int v0 = (int) Math.floor(y0);
		double q = 0;
		for (int j = 0; j <= 3; j++) {
			int v = v0 + j - 1;
			double p = 0;
			for (int i = 0; i <= 3; i++) {
				int u = u0 + i - 1;
				float pixval = ia.getVal(u, v);
				p = p + pixval * w_cs(x0 - u);
			}
			q = q + p * w_cs(y0 - v);
		}
		return (float) q;
	}	
	
	private double w_cs(double x) {
		if (x < 0) 
			x = -x;
		double w = 0;
		if (x < 1) 
			w = (-6*a - 9*b + 12) * x*x*x + (6*a + 12*b - 18) * x*x - 2*b + 6;
		else if (x < 2) 
			w = (-6*a - b) * x*x*x + (30*a + 6*b) * x*x + (-48*a - 12*b) * x + 24*a + 8*b;
		return w/6;
	}

}
