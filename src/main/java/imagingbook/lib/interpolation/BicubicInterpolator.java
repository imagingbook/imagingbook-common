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

public class BicubicInterpolator extends PixelInterpolator {
	
	private final double a;		// sharpness value
	
	public BicubicInterpolator() {
		this(0.5);
	}
	
	public BicubicInterpolator(double a) {
		this.a = a;
	}
	
	@Override
	public float getInterpolatedValue(ImageAccessor.Scalar ia, double x, double y) {
		final int u0 = (int) Math.floor(x);
		final int v0 = (int) Math.floor(y);
		double q = 0;
		for (int j = 0; j <= 3; j++) {
			int v = v0 - 1 + j;
			double p = 0;
			for (int i = 0; i <= 3; i++) {
				int u = u0 - 1 + i;
				float pixval = ia.getVal(u, v);	
				p = p + pixval * w_cub(x - u, a);
			}
			q = q + p * w_cub(y - v, a);
		}
		return (float) q;
	}
	
	private final double w_cub(double x, double a) {
		if (x < 0)
			x = -x;
		double z = 0;
		if (x < 1)
			z = (-a + 2) * x * x * x + (a - 3) * x * x + 1;
		else if (x < 2)
			z = -a * x * x * x + 5 * a * x * x - 8 * a * x + 4 * a;
		return z;
	}


}
