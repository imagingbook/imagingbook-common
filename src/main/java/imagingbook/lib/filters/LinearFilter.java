/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.filters;

import imagingbook.lib.image.access.ScalarAccessor;


/**
 * Generic linear convolution filter implemented
 * by extending the GenericFilter class.
 */
public class LinearFilter extends GenericFilter {
	
	private final float[][] kernel2d;
//	private final float[] rgb = new float[3];

	private final int kernelWidth, kernelHeight;	// width/height of the kernel
	private final int kernelCtrX, kernelCtrY;	// center coordinates of the kernel
	
	public LinearFilter(float[][] kernel2d) {
		this.kernel2d = kernel2d;
		this.kernelWidth = kernel2d.length;
		this.kernelHeight = kernel2d[0].length;
		this.kernelCtrX = kernelWidth / 2;
		this.kernelCtrY = kernelHeight / 2;
	}
	
	// --------------------------------------------------------------
	
	@Override
	public float filterScalar(ScalarAccessor ia, int u, int v) {
		float sum = 0;
		for (int j = 0; j < kernelHeight; j++) {
			int vj = v + j - kernelCtrY;
			for (int i = 0; i < kernelWidth; i++) {
				int ui = u + i - kernelCtrX;
				sum = sum + ia.getVal(ui, vj) * kernel2d[i][j];
			}
		}
 		return sum;
	}
	
//	@Override
//	public float[] filterVector(ImageAccessor ia, int u, int v) {
//		float sumR = 0;	// sum of weighted red
//		float sumG = 0;	// sum of weighted green
//		float sumB = 0;	// sum of weighted blue
//		for (int j = 0; j < kernelHeight; j++) {
//			int vj = v + j - kernelCtrY;
//			for (int i = 0; i < kernelWidth; i++) {
//				int ui = u + i - kernelCtrX;
//				float[] val = ia.getPix(ui, vj);
//				float w = kernel2d[i][j];
//				sumR = sumR + val[0] * w;
//				sumG = sumG + val[1] * w;
//				sumB = sumB + val[2] * w;
//			}
//		}
//		rgb[0] = sumR;
//		rgb[1] = sumG;
//		rgb[2] = sumB;
//		return rgb;
//		
//		// apply the same filter independently to the 3 scalar-valued components:
//		for (int k = 0; k < 3; k++) {
//			rgb[k] = filterPixel(ia.getComponentAccessor(k), u, v);
//		}
//		return rgb;
// 	}
	
	// --------------------------------------------------------------
	
	/**
	 * Returns the kernel of this filter as a 2D {@code float} array.
	 * @return the filter kernel
	 */
	public float[][] getKernel() {
		return this.kernel2d;
	}
}
