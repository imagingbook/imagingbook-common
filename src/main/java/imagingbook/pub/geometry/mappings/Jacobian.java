/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings;

/**
 * This interfaces prescribes methods for the parameterization of mappings
 * with 1D double vectors (used in Lucas-Kanade matcher etc.).
 * Currently only implemented by mapping classes {@link ProjectiveMapping}, 
 * {@link AffineMapping} and {@link Translation}.
 * An interface is used to allow implementation by other (non-linear) mappings
 * that ore not sub-classes of {@link ProjectiveMapping}.
 */
public interface Jacobian {
	
//	public int getWarpParameterCount();	
	public double[] getWarpParameters();
	public double[][] getWarpJacobian(double[] x);
	
}
