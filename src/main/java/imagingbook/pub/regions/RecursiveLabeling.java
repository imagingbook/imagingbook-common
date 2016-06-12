/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.regions;

import ij.process.ByteProcessor;

/**
 * Updated/checked: 2014-11-12
 * @author WB
 *
 */
public class RecursiveLabeling extends RegionLabeling {

	public RecursiveLabeling(ByteProcessor ip) {
		super(ip);
	}
	
	void applyLabeling() {
		resetLabel();
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				if (getLabel(u, v) >= START_LABEL) {
					// start a new region
					int label = getNextLabel();
					floodFill(u, v, label);
				}
			}
		}
	}

	public void floodFill(int up, int vp, int label) {
		if ((up>=0) && (up<width) && (vp>=0) && (vp<height) && getLabel(up, vp)>=START_LABEL) {
			setLabel(up, vp, label);
			floodFill(up + 1, vp, label);
			floodFill(up, vp + 1, label);
			floodFill(up, vp - 1, label);
			floodFill(up - 1, vp, label);
		}
	}

}
