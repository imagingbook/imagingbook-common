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
 * Separable linear convolution filter implemented
 * by extending the {@link GenericFilter2D} class.
 * If applied to a vector-valued image (e.g. an RGB image) this
 * scalar filter is applied to each component, which is
 * automatically handled by {@link GenericFilter2D}.
 * Thus only the scalar version of the filter needs to be defined
 * (see  {@link #filterScalar(ScalarAccessor, int, int)}).
 * 
 * @author WB
 * @version 2020/12/29
 */
public class LinearFilter2DSeparable extends GenericFilter2D {
	
	private final float[] Hx;				// the horizontal kernel
	private final float[] Hy;				// the vertical kernel
	private final int width, height;		// width/height of the kernel
	private final int xc, yc;				// 'hot spot' coordinates
	
	/**
	 * The preferred constructor.
	 * @param kernel the 2D filter (convolution) kernel
	 */
	public LinearFilter2DSeparable(Kernel1D kernelX, Kernel1D kernelY) {
		this.Hx = kernelX.getH();
		this.Hy = kernelY.getH();
		this.width = kernelX.getWidth();
		this.height = kernelY.getWidth();
		this.xc = kernelX.getXc();
		this.yc = kernelY.getXc();
	}
	
	// --------------------------------------------------------------
	
	@Override
	protected float filterScalar(ScalarAccessor ia, final int u, final int v) {
		float sum = 0;
		// perform horizontal 1D convolution in row v
		for (int i = 0; i < width; i++) {
			int ui = u + i - xc;
			sum = sum + ia.getVal(ui, v) * Hx[i];
		}
		// perform vertical 1D convolution in column u
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			sum = sum + ia.getVal(u, vj) * Hy[j];
		}
 		return sum;
	}
	
	// --------------------------------------------------------------
	
	/**
	 * Returns the horizontal kernel of this filter as a 1D {@code float} array.
	 * Provided for sub-classes who create their own kernel.
	 * 
	 * @return the horizontal filter kernel (no copy)
	 */
	public float[] getKernelX() {
		return this.Hx;
	}
	
	/**
	 * Returns the vertical kernel of this filter as a 1D {@code float} array.
	 * Provided for sub-classes who create their own kernel.
	 * 
	 * @return the vertical filter kernel (no copy)
	 */
	public float[] getKernelY() {
		return this.Hy;
	}
}
