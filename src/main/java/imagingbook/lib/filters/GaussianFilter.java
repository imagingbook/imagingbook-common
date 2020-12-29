/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.filters;

/**
 * This class implements a 2D Gaussian filter by extending
 * {@link LinearFilter2D}.
 * 
 * @author wilbur
 * @version 2020/12/29
 */
public class GaussianFilter extends LinearFilter2D {

	public GaussianFilter(double sigma) {
		super(new GaussianKernel2D(sigma));
	}
	
	public GaussianFilter(double sigmaX, double sigmaY) {
		super(new GaussianKernel2D(sigmaX, sigmaY));
	}
	
}
