/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/

package imagingbook.lib.interpolation;

import imagingbook.lib.image.ImageAccessor;

public class LanczosInterpolator extends PixelInterpolator {
	
	private final int order;	// order (tap count) of this interpolator
	
	public LanczosInterpolator(ImageAccessor.Scalar ia) {
		this(2);
	}
	
	public LanczosInterpolator(int N) {
		super();
		this.order = N; // order >= 2
	}
	
	@Override
	public float getInterpolatedValue(ImageAccessor.Scalar ia, double x, double y) {
		final int u0 = (int) Math.floor(x);	//use floor to handle negative coordinates too
		final int v0 = (int) Math.floor(y);
		double q = 0;
		for (int j = 0; j <= 2*order-1; j++) {
			int v = v0 + j - order + 1;
			double p = 0;
			for (int i = 0; i <= 2*order-1; i++) {
				int u = u0 + i - order + 1;
				float pixval = ia.getVal(u, v);
				p = p + pixval * w_Ln(x - u);
			}
			q = q + p * w_Ln(y - v);
		}
		return (float) q;
	}	
	
	
	static final double pi = Math.PI;
	static final double pi2 = pi*pi;
	
	private double w_Ln(double x) { // 1D Lanczos interpolator of order n
		x = Math.abs(x);
		if (x < 0.001) return 1.0;
		if (x < order) {
			return order * (Math.sin(pi*x / order) * Math.sin(pi * x)) / (pi2 * x * x);
		}
		else return 0.0;
	}


}
