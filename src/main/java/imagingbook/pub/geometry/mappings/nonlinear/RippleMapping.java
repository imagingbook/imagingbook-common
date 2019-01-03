/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.nonlinear;

import imagingbook.pub.geometry.mappings.Mapping;

/**
 * A non-linear mapping that produces a ripple effect.
 * The transformation is implicitly inverted, i.e., maps target to source image
 * coordinates.
 * 
 * @author WB
 *
 */
public class RippleMapping extends Mapping {
	final double xWavel; // = 20;
	final double yWavel ; // = 100;
	final double xAmpl ; // = 0;
	final double yAmpl ; // = 10;
   
	public RippleMapping (double xWavel, double xAmpl, double yWavel, double yAmpl) {
		this.xWavel = xWavel;
		this.yWavel = yWavel;
		this.xAmpl = xAmpl;
		this.yAmpl = yAmpl;
	}
	
	@Deprecated
	public static RippleMapping create(double xW, double xAmpl, double yW, double yAmpl){
		return new RippleMapping(xW, xAmpl, yW, yAmpl);
	}

	@Override
	public double[] applyTo (double x, double y){
		double xx = x + xAmpl * Math.sin(y / xWavel);
		double yy = y + yAmpl * Math.sin(x / yWavel);
		return new double[] {xx, yy};
	}
}




