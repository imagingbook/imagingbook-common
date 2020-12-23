/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.regions;

import static imagingbook.pub.regions.NeighborhoodType.N4;
import static imagingbook.pub.regions.NeighborhoodType.N8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ij.IJ;
import ij.process.ByteProcessor;
import imagingbook.pub.geometry.basic.Point;

/**
 * Binary region segmenter based on a combined region labeling
 * and contour tracing algorithm:
 * F. Chang, C. J. Chen, and C. J. Lu. A linear-time component labeling
 * algorithm using contour tracing technique. Computer Vision, Graphics,
 * and Image Processing: Image Understanding 93(2), 206-220 (2004).
 * Detected regions are 8-connected. 
 * 
 * @author WB
 * @version 2020/04/01
 */
public class SegmentationRegionContour extends BinaryRegionSegmentation implements ContourTracer { 
	
	static private final int VISITED = -1;
	
	private List<Contour.Outer> outerContours;
	private List<Contour.Inner> innerContours;
	
	/**
	 * Constructor. Creates a combined region and contour segmenter.
	 * @param ip A binary image with 0 values for background pixels and values &gt; 0
	 * for foreground pixels.
	 */
	public SegmentationRegionContour(ByteProcessor ip) {
		this(ip, DEFAULT_NEIGHBORHOOD);
	}
	
	public SegmentationRegionContour(ByteProcessor ip, NeighborhoodType nh) {
		super(ip, nh);
		attachOuterContours();	// attach each outer contours to the corresponding region
		attachInnerContours();	// attach all inner contours to the corresponding regions
	}
	
	// public methods required by interface ContourTracer (others are in inherited from super-class):
	
	@Override
	public List<Contour.Outer> getOuterContours() {
		return getOuterContours(false);
	}
	
	@Override
	public List<Contour.Outer> getOuterContours(boolean sort) {
		Contour.Outer[] oca = outerContours.toArray(new Contour.Outer[0]);
		if (sort) {
			Arrays.sort(oca);
		}
		return Arrays.asList(oca);
	}
	
	@Override
	public List<Contour.Inner> getInnerContours() {
		return getInnerContours(false);
	}
	
	@Override
	public List<Contour.Inner> getInnerContours(boolean sort) {
		Contour.Inner[] ica = innerContours.toArray(new Contour.Inner[0]);
		if (sort) {
			Arrays.sort(ica);
		}
		return Arrays.asList(ica);
	}
	
	// non-public methods ------------------------------------------------------------------
	
	@Override
	protected int[][] makeLabelArray() {
		// Create a label array which is "padded" by 1 pixel, i.e., 
		// 2 rows and 2 columns larger than the image:
		int[][] lA = new int[width+2][height+2];	// label array, initialized to zero
		outerContours = new ArrayList<>();
		innerContours = new ArrayList<>();
		return lA;
	}
	
	@Override
	protected boolean applySegmentation() {
		// scan top to bottom, left to right
		for (int v = 0; v < height; v++) {
			int label = 0;	// reset label, scan through horiz. line:
			for (int u = 0; u < width; u++) {
				if (ip.getPixel(u, v) > 0) {	// hit an unlabeled FOREGROUND pixel
					if (label != 0) { // keep using the same label
						setLabel(u, v, label);
					}
					else {	// label == 0
						label = getLabel(u, v);
						if (label == 0) {	// new (unlabeled) region is hit
							label = getNextLabel(); // assign a new region label
							// TODO: Point xs = <u,v> ... pass to traceContour instead!
							// TODO: pass label instead of new contour!
							Contour.Outer oc = traceContour(u, v, 0, new Contour.Outer(label));
							outerContours.add(oc);
							setLabel(u, v, label);
						}
					}
				} 
				else {	// hit a BACKGROUND pixel
					if (label != 0) { // exiting a region
						if (getLabel(u, v) == BACKGROUND) { // unlabeled - new inner contour
							int dS = (neighborType == N4) ? 2 : 1;
							// TODO: pass label instead of new contour!
							Contour.Inner ic = traceContour(u - 1, v, dS, new Contour.Inner(label));
							innerContours.add(ic);
						}
						label = 0;
					}
				}
			}
		}
		return true;
	}
	
	// Trace one contour starting at (xs,ys) in direction ds	
	private <T extends Contour> T traceContour(final int xs, final int ys, final int ds, T C) {
		final int label = C.getLabel();	// C ist the (empty) contour
		
		int[] X = {xs, ys};  						// start position
		int d = findNextContourPoint(X, ds);	// X is modified!
		C.addPoint(Point.create(X));
		
//		int xp = xs; int yp = ys; 		// Xp = previous contour point
		int xt = X[0]; int yt = X[1];	// Xt = successor of starting point (xs,ys)
		int u = X[0]; int v = X[1];		// X = (u,v) = current contour point
		
		boolean done = (xs == xt && ys == yt);  // true if single-pixel contour
		
//		while (!done) {
//			setLabel(u, v, label);
//			int[] Xn = {u, v};
//			int xp = u; //X[0];  	// keep Xp = previous contour point
//			int yp = v; //X[1];	
//			int dNext = (d + 6) % 8;
//			d = findNextContourPoint(Xn, dNext);
//			u = Xn[0]; 
//			v = Xn[1]; 
//			// are we back at the starting position?
//			done = (xp==xs && yp==ys && u==xt && v==yt);
//			if (!done) {
//				C.addPoint(Point.create(Xn[0], Xn[1]));
//			}
//		}
		
		while (!done) {
			setLabel(X[0], X[1], label);
			int xp = X[0], yp = X[1];  	// keep Xp = previous contour point	
			int dNext = (d + 6) % 8;
			d = findNextContourPoint(X, dNext);
			// are we back at the starting position?
			done = (xp==xs && yp==ys && X[0]==xt && X[1]==yt); // back at start pos.
			if (!done) {
				C.addPoint(Point.create(X));
			}
		}
		
		return C;
	}
	
	private static final int[][] delta = {
			{ 1,0}, { 1, 1}, {0, 1}, {-1, 1}, 
			{-1,0}, {-1,-1}, {0,-1}, { 1,-1}};

	private int findNextContourPoint(int[] X, int d0) {	// VERSION 3
		// Starts at point pos in direction dir,
		// returns the resulting tracing direction
		// and modifies pt.
//		int d = d0;
		int dd = d0;
		final int step = (neighborType == N4) ? 2 : 1;
		final int steps = (neighborType == N4) ? 3 : 7;
//		for (int i = 0, j = 0; i < 7; i += step, j++) {
		for (int i = 0; i < steps; i++) {
			dd = (d0 + i * step) % 8;
//			System.out.format("d=%d dd=%d (step=%d, j=%d)\n", d, dd, step, j);
			int x = X[0] + delta[dd][0];
			int y = X[1] + delta[dd][1];
			if (ip.getPixel(x, y) == BACKGROUND) {
				setLabel(x, y, VISITED);	// mark surrounding background pixels
//				d = (d + step) % 8;
			} 
			else {	// found a non-background pixel (next pixel to follow)
				X[0] = x; X[1] = y;
				break;
			}
		}
		return dd;
	}	
	
	private int findNextContourPoint_2(int[] X, int d0) {	// VERSION 2
		// Starts at point pos in direction dir,
		// returns the resulting tracing direction
		// and modifies pt.
//		int d = d0;
		int dd = d0;
		final int step = (neighborType == N4) ? 2 : 1;
		final int steps = (neighborType == N4) ? 3 : 7;
//		for (int i = 0, j = 0; i < 7; i += step, j++) {
		for (int i = 0; i < steps; i++) {
			dd = (d0 + i * step) % 8;
//			System.out.format("d=%d dd=%d (step=%d, j=%d)\n", d, dd, step, j);
			int x = X[0] + delta[dd][0];
			int y = X[1] + delta[dd][1];
			if (ip.getPixel(x, y) == BACKGROUND) {
				setLabel(x, y, VISITED);	// mark surrounding background pixels
//				d = (d + step) % 8;
			} 
			else {	// found a non-background pixel (next pixel to follow)
				X[0] = x; X[1] = y;
				break;
			}
		}
		return dd;
	}
	
	private int findNextContourPoint_1(int[] X, int d0) {
		// Starts at point pos in direction dir,
		// returns the resulting tracing direction
		// and modifies pt.
		int d = d0;
		final int step = (neighborType == N4) ? 2 : 1;
		for (int i = 0; i < 7; i += step) {
			int x = X[0] + delta[d][0];
			int y = X[1] + delta[d][1];
			if (ip.getPixel(x, y) == BACKGROUND) {
				setLabel(x, y, VISITED);	// mark surrounding background pixels
				d = (d + step) % 8;
			} 
			else {	// found a non-background pixel (next pixel to follow)
				X[0] = x; 
				X[1] = y; 
				break;
			}
		}
		return d;
	}
	
	private void attachOuterContours() {
		for (Contour.Outer c : outerContours) {
			int label = c.getLabel();
			BinaryRegion reg = getRegion(label);
			if (reg == null) {
				IJ.log("Error: Could not associate outer contour with label " + label);
			}
			else {
				reg.setOuterContour(c);
			}
		}
	}
	
	private void attachInnerContours() {
//		for (BinaryRegion r : regions) {
//			r.makeInnerContours();	// ensure that every region has a (empty) list of inner contours
//		}
		for (Contour.Inner c : innerContours) {
			int label = c.getLabel();
			BinaryRegion reg = getRegion(label);
			if (reg == null) {
				IJ.log("Error: Could not associate inner contour with label " + label);
			}
			else {
				reg.addInnerContour(c);
			}
		}
	}

	// access methods to the label array (which is padded!)
	@Override
	public int getLabel(int u, int v) {	// (u,v) are image coordinates
		if (u >= -1 && u <= width && v >= -1 && v <= height)
			return labelArray[u + 1][v + 1]; 	// label array is padded (offset = 1)
		else
			return BACKGROUND;
		//return labelArray[u + 1][v + 1];	// original version
	}
	
	@Override
	protected void setLabel(int u, int v, int label) { // (u,v) are image coordinates
		if (u >= -1 && u <= width && v >= -1 && v <= height) {
			labelArray[u + 1][v + 1] = label;
		}
	}
	
//	private List<Contour> copyContours(List<Contour> cntrs, boolean sort) {
//		if (cntrs == null)
//			return Collections.emptyList(); 
//		else {
//			List<Contour> cc = new ArrayList<Contour>(cntrs);
//			if (sort) {
//				Collections.sort(cc);
//			}
//			return cc;
//		}
//	}
	
}
