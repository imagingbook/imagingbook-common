/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.regions;

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
			int curLabel = 0;	// reset label, scan through horiz. line:
			for (int u = 0; u < width; u++) {
				if (ip.getPixel(u, v) > 0) {	// hit an unlabeled FOREGROUND pixel
					if (curLabel != 0) { // keep using the same label
						setLabel(u, v, curLabel);
					}
					else {	// label == 0
						curLabel = getLabel(u, v);
						if (curLabel == 0) {	// new (unlabeled) region is hit
							curLabel = getNextLabel(); // assign a new region label
							//IJ.log(String.format("assigning label %d at (%d,%d), maxLabel=%d", label, u, v, maxLabel));
							int dS = 0;
							Contour.Outer oc = traceContour(u, v, dS, new Contour.Outer(curLabel));
							outerContours.add(oc);
							setLabel(u, v, curLabel);
						}
					}
				} 
				else {	// hit a BACKGROUND pixel
					if (curLabel != 0) { // exiting a region
						if (getLabel(u, v) == BACKGROUND) { // unlabeled - new inner contour
							int dS = (neighborhood == N8) ? 1 : 2;
							Contour.Inner ic = traceContour(u - 1, v, dS, new Contour.Inner(curLabel));
							innerContours.add(ic);
						}
						curLabel = 0;
					}
				}
			}
		}
		return true;
	}
	
	// Trace one contour starting at (xS,yS) in direction dS	
	private <T extends Contour> T traceContour(int xS, int yS, int dS, T contr) {
		int label = contr.getLabel();
		int xT, yT; // T = successor of starting point (xS,yS)
		int xP, yP; // P = previous contour point
		int xC, yC; // C = current contour point
		int[] pt = {xS, yS};  // start point
		int dNext = findNextContourPoint(pt, dS);
		contr.addPoint(Point.create(pt[0], pt[1]));
		xP = xS; yP = yS;
		xC = xT = pt[0];
		yC = yT = pt[1];
		
		boolean done = (xS == xT && yS == yT);  // true if isolated pixel
		while (!done) {
			setLabel(xC, yC, label);
			int[] pn = {xC, yC};
			int dSearch = (dNext + 6) % 8;
			dNext = findNextContourPoint(pn, dSearch);
			xP = xC;  yP = yC;	
			xC = pn[0]; 
			yC = pn[1]; 
			// are we back at the starting position?
			done = (xP==xS && yP==yS && xC==xT && yC==yT);
			if (!done) {
				contr.addPoint(Point.create(pn[0], pn[1]));
			}
		}
		return contr;
	}
	
	static final int[][] delta = {
			{ 1,0}, { 1, 1}, {0, 1}, {-1, 1}, 
			{-1,0}, {-1,-1}, {0,-1}, { 1,-1}};
	
	private int findNextContourPoint(int[] pos, int dir) {
		// Starts at point pos in direction dir,
		// returns the resulting tracing direction
		// and modifies pt.
		final int step = (neighborhood == N8) ? 1 : 2;
		for (int i = 0; i < 7; i += step) {
			int x = pos[0] + delta[dir][0];
			int y = pos[1] + delta[dir][1];
			if (ip.getPixel(x, y) == BACKGROUND) {
				setLabel(x, y, VISITED);	// mark surrounding background pixels
				dir = (dir + step) % 8;
			} 
			else {	// found a non-background pixel (next pixel to follow)
				pos[0] = x; 
				pos[1] = y; 
				break;
			}
		}
		return dir;
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
