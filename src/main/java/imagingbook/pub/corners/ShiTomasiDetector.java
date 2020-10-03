/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.corners;

import static imagingbook.lib.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

import ij.process.ImageProcessor;


/**
 * This is an implementation of the Shi-Tomasi corner detector, as 
 * described in
 * <blockquote>
 *  J. Shi and C. Tomasi. Good features to track. In “Proceedings
 *  of IEEE Conference on Computer Vision and Pattern Recognition,
 *  CVPR’94”, pp. 593–600, Seattle, WA, USA (1994).
 * </blockquote>
 * 
 * @author W. Burger
 * @version 2020/10/02
 */
public class ShiTomasiDetector extends AbstractGradientCornerDetector {
	
	public static class Parameters extends AbstractGradientCornerDetector.Parameters {
		// no additional parameters
	}

	public ShiTomasiDetector(ImageProcessor ip){
		this(ip, new Parameters());
	}
	
	public ShiTomasiDetector(ImageProcessor ip, Parameters params) {
		super(ip, params);
	}
	
	// --------------------------------------------------------------

	@Override
	public float computeScore(float A, float B, float C) {
		double lambda2 = (A + B) / 2 - sqrt(sqr((A - B) / 2) + sqr(C));
		return (float) sqr(lambda2); 	// returns lambda_2^2
	}

}
