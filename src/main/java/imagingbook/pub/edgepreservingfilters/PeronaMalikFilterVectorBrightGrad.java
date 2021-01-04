/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import ij.process.ColorProcessor;
import imagingbook.lib.filter.GenericFilterVector;
import imagingbook.lib.image.access.PixelPack;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.ConductanceFunction;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.Parameters;


/**
 * Vector version with greatly reduced memory requirements.
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
public class PeronaMalikFilterVectorBrightGrad extends GenericFilterVector {
	
	private final float alpha;
	private final int T; 		// number of iterations
	private final ConductanceFunction g;
	
	// constructor - using default parameters
	public PeronaMalikFilterVectorBrightGrad (ColorProcessor ip) {
		this(ip, new Parameters());
	}
	
	// constructor - use this version to set all parameters
	public PeronaMalikFilterVectorBrightGrad (ColorProcessor ip, Parameters params) {
		super(ip, params.obs);
		this.T = params.iterations;
		this.alpha = params.alpha;
		this.g = ConductanceFunction.get(params.smoothRegions, params.kappa);
	}
	
	// ------------------------------------------------------
	
	@Override
	protected float[] filterPixel(PixelPack sources, int u, int v) {
		/*   
		 *  NH pixels:      directions:
		 *      I4              d3
		 *   I3 I0 I1        d2 x d0
		 *      I2              d1
		 */
		float[][] I = new float[5][];	// p[i][k]: 5 pixels from the 3x3 neigborhood
		I[0] = sources.getPixel(u, v);
		I[1] = sources.getPixel(u + 1, v);
		I[2] = sources.getPixel(u, v + 1);
		I[3] = sources.getPixel(u - 1, v);
		I[4] = sources.getPixel(u, v - 1);
		
		// calculate the brightness for the 5 pixels :
		float[] b = new float[5];
		for (int i = 0; i < I.length; i++) {
			b[i] = getBrightness(I[i]);
		}
		
		// brightness differences in 4 directions:
		float[] db = new float[4];
		db[0] = b[1] - b[0];		// A:  Dx[u][v] = I[u+1][v] - I[u][v]
		db[1] = b[2] - b[0];		// B:  Dy[u][v] = I[u][v+1] - I[u][v]
		db[2] = b[3] - b[0];		// C: -Dx[u-1][v] = -(I[u][v] - I[u-1][v]) = I[u-1][v] - I[u][v]
		db[3] = b[4] - b[0];		// D: -Dy[u][v-1] = -(I[u][v] - I[u][v-1]) = I[u][v-1] - I[u][v]

		float[] result = new float[3];
		// color differences in 4 directions i (for one component k)
		float[] dcol = new float[4];	
		for (int k = 0; k < 3; k++) {
			dcol[0] = I[1][k] - I[0][k]; 	// Ix[k][u][v];		
			dcol[1] = I[2][k] - I[0][k]; 	// Iy[k][u][v];
			dcol[2] = I[3][k] - I[0][k]; 	// (u > 0) ? -Ix[k][u - 1][v] : 0;
			dcol[3] = I[4][k] - I[0][k]; 	// (v > 0) ? -Iy[k][u][v - 1] : 0;		
			result[k] = I[0][k] +
					alpha * (g.eval(db[0]) * dcol[0] + 
							 g.eval(db[1]) * dcol[1] + 
							 g.eval(db[2]) * dcol[2] + 
							 g.eval(db[3]) * dcol[3]);
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
