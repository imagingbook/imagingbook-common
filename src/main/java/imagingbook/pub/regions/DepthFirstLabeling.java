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

import java.awt.Point;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Binary region labeler based on a depth-first flood filling
 * algorithm. 
 * 
 * @author WB
 * @version 2016-11-08
 */
public class DepthFirstLabeling extends RegionLabeling {
	
	/**
	 * Creates a new region labeling.
	 * 
	 * @param ip the binary input image with 0 values for background pixels and values &gt; 0
	 * for foreground pixels.
	 */
	public DepthFirstLabeling(ByteProcessor ip) {
		super(ip);
	}
	
	@Override
	void applyLabeling() {
		resetLabel();
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				if (getLabel(u, v) == FOREGROUND) {
					// start a new region
					int label = getNextLabel();
					floodFill(u, v, label);
				}
			}
		}
	}

	private void floodFill(int u, int v, int label) {
		Deque<Point> S = new LinkedList<Point>();	//stack contains pixel coordinates
		S.push(new Point(u, v));
		while (!S.isEmpty()){
			Point p = S.pop();
			int x = p.x;
			int y = p.y;
			if ((x >= 0) && (x < width) && (y >= 0) && (y < height)
					&& getLabel(x, y) == FOREGROUND) {
				setLabel(x, y, label);
				S.push(new Point(x + 1, y));
				S.push(new Point(x, y + 1));
				S.push(new Point(x, y - 1));
				S.push(new Point(x - 1, y));
			}
		}
	}

}
