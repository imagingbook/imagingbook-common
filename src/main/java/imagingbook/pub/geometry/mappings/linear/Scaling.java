/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;


public class Scaling extends AffineMapping {

	/**
	 * Creates a mapping that scales along the x- and y-axis
	 * by the associated factors.
	 * 
	 * @param sx scale factor in x-direction
	 * @param sy scale factor in y-direction
	 */
	public Scaling(double sx, double sy) {
		super(
			sx, 0,  0,
			0,  sy, 0, false);
	}
	
	/**
	 * Creates a scaling that is uniform in x and y.
	 * @param s scale factor
	 */
	public Scaling(double s) {
		this(s, s);
	}
}


