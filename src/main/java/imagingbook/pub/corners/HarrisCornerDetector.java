/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.corners;

import ij.process.ImageProcessor;


/**
 * This is an implementation of the Harris corner detector, as described in
 * <blockquote>
 *  C. G. Harris and M. Stephens. A combined corner and edge
 *  detector. In C. J. Taylor, editor, “4th Alvey Vision Conference”,
 *  pp. 147–151, Manchester (1988).
 *  </blockquote>
 * This class extends {@link GradientCornerDetector} (where most
 * of the work is done) by defining a specific corner score function
 * and associated threshold.
 *
 * @author W. Burger
 * @version 2020/10/02
 */
public class HarrisCornerDetector extends GradientCornerDetector {
	
	public static double DEFAULT_THRESHOLD = 20000;

	public static class Parameters extends GradientCornerDetector.Parameters {
		/** Sensitivity parameter */
		public double alpha = 0.05;
		
		public Parameters() {
			scoreThreshold = DEFAULT_THRESHOLD;	// individual default threshold
		}
	}

	// ---------------------------------------------------------------------------
	
	public HarrisCornerDetector(ImageProcessor ip, Parameters params) {
		super(ip, params);
	}
	
	// ----------------------------------------------------------------------

	@Override	// pass as a function object?
	protected float getCornerScore(float A, float B, float C) {
		float alpha = (float) ((Parameters) params).alpha;
		float det = A * B - C * C;
		float trace = A + B;
		return det - alpha * (trace * trace);
	}
	
}
