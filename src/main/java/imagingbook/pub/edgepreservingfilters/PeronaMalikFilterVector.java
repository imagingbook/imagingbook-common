/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import static imagingbook.lib.math.Matrix.subtract;

import imagingbook.lib.filter.GenericFilterVector;
import imagingbook.lib.math.Matrix;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.ColorMode;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.ConductanceFunction;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.Parameters;

/**
 * Vector simplified version with greatly reduced memory requirements.
 * This code is based on the Anisotropic Diffusion filter proposed by Perona and Malik,
 * as proposed in Pietro Perona and Jitendra Malik, "Scale-space and edge detection 
 * using anisotropic diffusion", IEEE Transactions on Pattern Analysis 
 * and Machine Intelligence, vol. 12, no. 4, pp. 629-639 (July 1990).
 * 
 * The filter operates on all types of grayscale (scalar) and RGB color images.
 * This class is based on the ImageJ API and intended to be used in ImageJ plugins.
 * How to use: consult the source code of the related ImageJ plugins for examples.
 * 
 * @author W. Burger
 * @version 2021/01/04
 */	
public class PeronaMalikFilterVector extends GenericFilterVector {
	
	private final float alpha;
	private final int T; 		// number of iterations
	private final ConductanceFunction g;
	private ColorMode colorMode;
	
	// constructor - using default parameters
	public PeronaMalikFilterVector () {
		this(new Parameters());
	}
	
	// constructor - use this version to set all parameters
	public PeronaMalikFilterVector (Parameters params) {
		this.T = params.iterations;
		this.alpha = params.alpha;
		this.g = ConductanceFunction.get(params.conductanceFunType, params.kappa);
		this.colorMode = params.colorMode;
	}
	
	// ------------------------------------------------------
	
	@Override
	protected float[] doPixel(int u, int v) {
		/*   
		 *  NH pixels:      directions:
		 *      p4              3
		 *   p3 p0 p1         2 x 0
		 *      p2              1
		 */
		float[][] p = new float[5][];	// p[i][k]: 5 pixels from the 3x3 neigborhood
		p[0] = source.getPixel(u, v);
		p[1] = source.getPixel(u + 1, v);
		p[2] = source.getPixel(u, v + 1);
		p[3] = source.getPixel(u - 1, v);
		p[4] = source.getPixel(u, v - 1);
		
		float[] result = p[0].clone();
		
		switch (colorMode) {
		case BrightnessGradient:
			float b0 = getBrightness(p[0]);
			for (int i = 1; i <= 4; i++) {
				float bi = getBrightness(p[i]);
				for (int k = 0; k < 3; k++) {
					float gi = g.eval(Math.abs(bi - b0));
					result[k] = result[k] + alpha * gi * (p[i][k] - p[0][k]);
				}
			}
			break;
		case ColorGradient:
			for (int i = 1; i <= 4; i++) {
				float[] D = subtract(p[i], p[0]);
				float gi = g.eval(Matrix.normL2(D));	// g applied to color gradient magnitude
				for (int k = 0; k < 3; k++) {
					result[k] = result[k] + alpha * gi * D[k];
				}
			}
			break;
		default:
			throw new RuntimeException("color mode option " + colorMode.name() +
					" not implemented in class " + this.getClass().getSimpleName());
		}
		
		return result;
	}

	@Override
	protected final boolean finished() {
		return (getPass() >= T);	// this filter needs T passes
	}
	
	private final float getBrightness(float[] p) {
		return 0.299f * p[0] + 0.587f * p[1] + 0.114f * p[2];
	}
	
}
