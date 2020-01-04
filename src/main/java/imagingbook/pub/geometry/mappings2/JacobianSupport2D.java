/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings2;

import imagingbook.pub.geometry.basic.Point;

/**
 * This interfaces defines methods for the parameterization of mappings
 * with 1D double vectors (used in Lucas-Kanade matcher etc.).
 * Currently only implemented by mapping classes {@code ProjectiveMapping}, 
 * {@code AffineMapping} and {@code Translation}.
 * This functionality is used, e.g., in package {@link imagingbook.pub.lucaskanade}.
 * 
 * <p> Use of an interface allows implementation by other (non-linear) mappings
 * that ore not sub-classes of {@code ProjectiveMapping}.</p>
 */
public interface JacobianSupport2D {
	
	/**
	 * Returns the parameters of the associated geometric transformation
	 * as a vector.
	 * @return parameter vector
	 */
	public double[] getParameters();
	
	/**
	 * Creates a new instance of this mapping from a parameter vector.
	 * Note that this is a non-static method, i.e., must be called
	 * on an instance of this mapping (which is usually available).
	 * @param p parameter vector
	 * @return a new instance of this mapping
	 */
	public Mapping2D fromParameters(double[] p);
	
	/** Returns the Jacobian matrix of this geometric transformation,
	 * evaluated for the given 2D position.
	 * @param xy 2D position
	 * @return the Jacobian matrix
	 */
	public double[][] getJacobian(Point xy);
	
}
