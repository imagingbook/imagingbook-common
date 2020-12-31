/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.color.filters;

import java.util.Arrays;

import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilter;
import imagingbook.lib.filter.GenericFilterScalar;
import imagingbook.lib.image.access.FloatPixelPack.Slice;
import imagingbook.lib.image.access.OutOfBoundsStrategy;

/**
 * Ordinary (scalar) median filter for color images implemented
 * by extending the {@link GenericFilter} class.
 * Color images are filtered individually in all channels.
 * 
 * @author W. Burger
 * @version 2020/12/31
 */
public class ScalarMedianFilter extends GenericFilterScalar {
	
	final Parameters params;
	final FilterMask mask;
	final int maskCount;
	final float[] p;
	final int medianIndex;
	final int maskCenter;
	final int[][] maskArray;
	
	public ScalarMedianFilter(ImageProcessor ip) {
		this(ip, new Parameters());
	}
	
	public ScalarMedianFilter(ImageProcessor ip, Parameters params) {
		super(ip, params.obs);
		this.params = params;
		this.mask = new FilterMask(params.radius);
		this.maskCount = mask.getCount();
		this.p = new float[maskCount];
		this.medianIndex = maskCount/2;
		this.maskCenter = mask.getCenter();
		this.maskArray = mask.getMask();
	}

	public static class Parameters {
		/** Filter radius */
		public double radius = 3.0;
		/** Out-of-bounds strategy */
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NEAREST_BORDER;
	}

	//-------------------------------------------------------------------------------------
	
	@Override
	protected float filterPixel(Slice source, int u, int v) {
		int k = 0;
		for (int i = 0; i < maskArray.length; i++) {
			int ui = u + i - maskCenter;
			for (int j = 0; j < maskArray[0].length; j++) {
				if (maskArray[i][j] > 0) {
					int vj = v + j - maskCenter;
					p[k] = source.getVal(ui, vj);
					k = k + 1;
				}
			}
		}
		Arrays.sort(p);
		return p[medianIndex];
	}

}
