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
 * by extending the {@link GenericFilter2D} class.
 * If applied to a vector-valued image (e.g. an RGB image) this
 * scalar filter is applied to each component, which is
 * automatically handled by {@link GenericFilter2D}.
 * Thus only the scalar version of the filter needs to be defined
 * (see  {@link #filterScalar(ScalarAccessor, ScalarAccessor, int, int)}).
 * 
 * @author WB
 * @version 2020/12/29
 */
public class LinearFilter2D extends GenericFilter2D  implements HasKernel2D {
	
	private final Kernel2D kernel;
	private final float[][] H;		// the kernel
	private final int width, height;		// width/height of the kernel
	private final int xc, yc;		// 'hot spot' coordinates
	
	/**
	 * The preferred constructor.
	 * @param kernel the 2D filter (convolution) kernel
	 */
	public LinearFilter2D(Kernel2D kernel) {
		this.kernel = kernel;
		this.H = kernel.getH();
		this.width = kernel.getWidth();
		this.height = kernel.getHeight();
		this.xc = kernel.getXc();
		this.yc = kernel.getYc();
	}
	
	// --------------------------------------------------------------
	
	@Override
	protected void filterScalar(ScalarAccessor ia, ScalarAccessor target, final int u, final int v) {
		double sum = 0;
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < width; i++) {
				int ui = u + i - xc;
				sum = sum + ia.getVal(ui, vj) * H[i][j];
			}
		}
		target.setVal(u, v, (float)sum);
 		//return (float)sum;
	}
	
	// --------------------------------------------------------------
	
	@Override
	public Kernel2D getKernel() {
		return this.kernel;
	}
}
