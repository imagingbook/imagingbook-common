/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.regions;

import java.util.LinkedList;
import java.util.Queue;

import ij.process.ByteProcessor;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.basic.Pnt2d.PntInt;

/**
 * Binary region labeler based on a breadth-first flood filling
 * algorithm using a stack.
 * Detected regions are 8-connected.
 * 
 * @author WB
 * @version 2020/04/01
 */
public class SegmentationBreadthFirst extends BinaryRegionSegmentation {
	
	/**
	 * Constructor. Creates a new breadth-first (flood-fill) binary region segmenter.
	 * 
	 * @param ip A binary input image with 0 values for background pixels and values &gt; 0
	 * for foreground pixels.
	 */
	public SegmentationBreadthFirst(ByteProcessor ip) {
		this(ip, DEFAULT_NEIGHBORHOOD);
	}
	
	public SegmentationBreadthFirst(ByteProcessor ip, NeighborhoodType nh) {
		super(ip, nh);
	}
	
	@Override
	protected boolean applySegmentation() {
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				if (getLabel(u, v) == FOREGROUND) {
					// start a new region
					int label = getNextLabel();
					//IJ.log(String.format("assigning label %d at (%d,%d), maxLabel=%d", label, u, v, maxLabel));
					floodFill(u, v, label);
				}
			}
		}
		return true;
	}

	private void floodFill(int u, int v, int label) {
		Queue<Pnt2d> Q = new LinkedList<Pnt2d>();	//queue contains pixel coordinates
		Q.add(PntInt.from(u, v));
		while (!Q.isEmpty()) {
			Pnt2d p = Q.remove();	// get the next point to process
			int x = (int) p.getX();
			int y = (int) p.getY();
			if ((x >= 0) && (x < width) && (y >= 0) && (y < height) && getLabel(x, y) == FOREGROUND) {
				setLabel(x, y, label);
				Q.add(PntInt.from(x + 1, y));
				Q.add(PntInt.from(x, y + 1));
				Q.add(PntInt.from(x, y - 1));
				Q.add(PntInt.from(x - 1, y));
				if (neighborType == NeighborhoodType.N8) {
					Q.add(PntInt.from(x + 1, y + 1));
					Q.add(PntInt.from(x - 1, y + 1));
					Q.add(PntInt.from(x + 1, y - 1));
					Q.add(PntInt.from(x - 1, y - 1));
				}
			}
		}
	}

}
