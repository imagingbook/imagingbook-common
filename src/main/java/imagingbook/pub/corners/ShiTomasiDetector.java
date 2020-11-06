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
 * @version 2020/10/05
 */
public class ShiTomasiDetector extends GradientCornerDetector {
	
	public static class Parameters extends GradientCornerDetector.Parameters {
		
		public Parameters() {
			scoreThreshold = 20000;	// individual default threshold
		}
	}
	
	public ShiTomasiDetector(ImageProcessor ip, Parameters params) {
		super(ip, params);
	}
	
	// --------------------------------------------------------------

	@Override
	protected float computeCornerScore(float A, float B, float C) {
		double rootExpr = sqr((A - B) / 2) + sqr(C);
		if (rootExpr < 0) {
			return UndefinedScoreValue;
		}
		double lambda2 = (A + B) / 2 - sqrt(rootExpr);
		return (float) sqr(lambda2); 	// returns lambda_2^2
	}

}
