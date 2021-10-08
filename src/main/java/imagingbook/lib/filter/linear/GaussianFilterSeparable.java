/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.filter.linear;

/**
 * This class implements a separable 2D Gaussian filter by extending
 * {@link LinearFilterSeparable}.
 * 
 * @author WB
 * @version 2020/12/29
 */
public class GaussianFilterSeparable extends LinearFilterSeparable {
	
	/**
	 * Constructor.
	 * @param sigma the width of the 2D Gaussian in x- and y-direction
	 */
	public GaussianFilterSeparable(double sigma) {
		super(new GaussianKernel1D(sigma));
	}
	
	/**
	 * Constructor.
	 * TODO: Provide ways to filter only in ONE direction!
	 * 
	 * @param sigmaX the width of the 2D Gaussian in x-direction
	 * @param sigmaY the width of the 2D Gaussian in y-direction
	 */
	public GaussianFilterSeparable(double sigmaX, double sigmaY) {
		super(new GaussianKernel1D(sigmaX), new GaussianKernel1D(sigmaY));
	}
	
}
