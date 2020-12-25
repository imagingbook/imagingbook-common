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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ij.IJ;
import ij.process.ByteProcessor;
import imagingbook.lib.tuples.Tuple;
import imagingbook.lib.tuples.Tuple2;
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
		for (int v = 0; v < height; v++) {	// scan top to bottom, left to right
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
							int[] xs = {u, v};
							int ds = 0;
							Contour.Outer c = traceContour(xs, ds, new Contour.Outer(label));
							outerContours.add(c);
							setLabel(u, v, label);
						}
					}
				} 
				else {	// hit a BACKGROUND pixel
					if (label != 0) { // exiting a region
						if (getLabel(u, v) == BACKGROUND) { // unlabeled - new inner contour
							int[] xs = {u - 1, v};
							int ds = (neighborType == N4) ? 2 : 1;
							// TODO: pass label instead of new contour!
							Contour.Inner c = traceContour(xs, ds, new Contour.Inner(label));
							innerContours.add(c);
						}
						label = 0;
					}
				}
			}
		}
		return true;
	}
	
	// Trace one contour starting at (xs,ys) in direction ds	
	private <T extends Contour> T traceContour(int[] Xas, final int ds, T contour) {
		final int label = contour.getLabel();	// C ist the (empty) contour
		Point Xs = Point.create(Xas);					
		Point X = Xs;			// start position
		
		Tuple2<Point, Integer> tup = findNextContourPointTupel(X, ds);
		X = tup.f0;
		int d = tup.f1;
		contour.addPoint(X);
		
		Point Xt = X; //Point.create(XA);
//		boolean home = (xs == xt && ys == yt);  	// true if single-pixel contour
		boolean home = samePointInt(Xs, Xt);
		
		while (!home) {
//			setLabel(XA[0], XA[1], label);
			setLabel(X, label);
//			int xp = XA[0], yp = XA[1];  	// keep Xp = previous contour point	
			Point Xp = X; //Point.create(XA);
			
			int dn = (d + 6) % 8;
			
//			d = findNextContourPoint(X, dn);
			tup = findNextContourPointTupel(X, dn);
//			XA = toIntArray(tup.f0);
			X = tup.f0;
			d = tup.f1;
			
			// are we back at the starting position?
			//home = (xp==xs && yp==ys && X[0]==xt && X[1]==yt); // back at start pos.
			home = (samePointInt(Xp, Xs) && samePointInt(tup.f0, Xt)); // back at start pos.
			if (!home) {
//				contour.addPoint(Point.create(XA));
				contour.addPoint(X);
			}
		}
		//System.out.println("traceContour: " +  neighborType + " "+ C.toString() + " duplicates=" + C.countDuplicatePoints());
		return contour;
	}
		
	private int[] toIntArray(Point p) {
		return new int[] {(int) p.getX(), (int) p.getY() };
	}
	
	private boolean samePointInt(Point p1, Point p2) {
		return (int)p1.getX()==(int)p2.getX() && (int)p1.getY()==(int)p2.getY();
	}
	
//	// Trace one contour starting at (xs,ys) in direction ds	
//	private <T extends Contour> T traceContour(int[] Xs, final int ds, T contour) {
//		final int label = contour.getLabel();	// C ist the (empty) contour
//		int xs = Xs[0], ys = Xs[1];
//		
//		Point pS = Point.create(Xs);
//		//int[] X = {xs, ys};  						// start position
//		Point X = pS;  				// start position
//		
//		//int d = findNextContourPoint(X, ds);		// X is modified!
//		Tuple2<Point, Integer> tup = findNextContourPointTupel(X, ds);
//		X = tup.f0;
//		int d = tup.f1;
//		
////		contour.addPoint(Point.create(X));
//		contour.addPoint(X);
//		
////		int xt = X[0], yt = X[1];					// xt = immediate successor of starting point (xs,ys)
//		int xt = (int)X.getX(), yt = (int)X.getY();	
//		Point Xt = X;
//		
//		boolean home1 = (xs == xt && ys == yt);  	// true if single-pixel contour
//		boolean home = (pS.equals(Xt));
//		//System.out.println(home1 + " / " + home);
//		
//		while (!home) {
//			//setLabel(X[0], X[1], label);
//			setLabel((int)X.getX(), (int)X.getY(), label);
//			Point Xp = X;
//			//int xp = X[0], yp = X[1];  	// keep Xp = previous contour point	
//			int xp = (int)X.getX(), yp = (int)X.getY();	
//			
//			int dn = (d + 6) % 8;
//			//d = findNextContourPoint(X, dn);
//			tup = findNextContourPointTupel(Point.create(X), dn);
////			X[0] = (int) tup.f0.getX(); 
////			X[1] = (int) tup.f0.getY();
//			X = tup.f0;
//			d = tup.f1;
//			// are we back at the starting position?
////			home = (xp==xs && yp==ys && X[0]==xt && X[1]==yt); // back at start pos.
//			
//			home1 = (xp==xs && yp==ys && (int)X.getX()==xt && (int)X.getY()==yt);
//			home = (Xp.equals(pS) && X.equals(Xt)); // back at start pos.
//			//System.out.println("   " + home1 + " / " + home);
//			if (!home) {
////				contour.addPoint(Point.create(X));
//				contour.addPoint(X);
//			}
//		}
//		//System.out.println("traceContour: " +  neighborType + " "+ C.toString() + " duplicates=" + C.countDuplicatePoints());
//		return contour;
//	}
	
	private static final int[][] delta = {
			{ 1,0}, { 1, 1}, {0, 1}, {-1, 1}, 
			{-1,0}, {-1,-1}, {0,-1}, { 1,-1}};

	// --------------------------------------------------------------------
	
	private Tuple2<Point, Integer> findNextContourPointTupel(Point XY, int d0) {	// VERSION 2 (works fine!)
		int[] X = {(int) XY.getX(), (int) XY.getY()};
		// Starts at point X in direction d0,
		// returns the resulting tracing direction
		// and modifies X.
		int step = (neighborType == N4) ? 2 : 1;
		int d = d0;
		int i = 0;
		boolean done = false;
		while (i < 7 && !done) {	// N4: i = 0,2,4,6  N8: i = 0,1,2,3,4,5,6
			int x = X[0] + delta[d][0];
			int y = X[1] + delta[d][1];
			if (ip.getPixel(x, y) == BACKGROUND) {
				setLabel(x, y, VISITED);	// mark this background pixel not to be visited again
				d = (d + step) % 8;
			} 
			else {	// found a non-background pixel (next pixel to follow)
				X[0] = x; X[1] = y; // modify X (to be passed back)
				done = true;
			}
			i = i + step;
		}
		return (done) ? Tuple.of(Point.create(X), d) : Tuple.of(XY, d0);
	}
	
	private int findNextContourPoint(int[] X, int d0) {	// VERSION 2 (works fine!)
		// Starts at point X in direction d0,
		// returns the resulting tracing direction
		// and modifies X.	
		int step = (neighborType == N4) ? 2 : 1;
		int d = d0;
		int i = 0;
		boolean done = false;
		while (i < 7 && !done) {	// N4: i = 0,2,4,6  N8: i = 0,1,2,3,4,5,6
			int x = X[0] + delta[d][0];
			int y = X[1] + delta[d][1];
			if (ip.getPixel(x, y) == BACKGROUND) {
				setLabel(x, y, VISITED);	// mark this background pixel not to be visited again
				d = (d + step) % 8;
			} 
			else {	// found a non-background pixel (next pixel to follow)
				X[0] = x; X[1] = y; // modify X (to be passed back)
				done = true;
			}
			i = i + step;
		}
		return (done) ? d : d0;
	}
	
//	private int findNextContourPoint_1(int[] X, int dn) {	// VERSION 1 (= reference, works fine!)
//		// Starts at point pos in direction dir,
//		// returns the resulting tracing direction
//		// and modifies pt.
//		int d = dn;
//		int step = (neighborType == N4) ? 2 : 1;
//		boolean done = false;
//		for (int i = 0; i < 7 && !done; i += step) {	// N4: i = 0,2,4,6  N8: i = 0,1,2,3,4,5,6
//			int xn = X[0] + delta[d][0];
//			int yn = X[1] + delta[d][1];
//			if (ip.getPixel(xn, yn) == BACKGROUND) {
//				setLabel(xn, yn, VISITED);	// mark this background pixel not to be visited again
//				d = (d + step) % 8;
//			} 
//			else {	// found a non-background pixel (next pixel to follow)
//				X[0] = xn; // modify X (to be passed back)
//				X[1] = yn;
//				done = true;
//				return d;
//			}
//		}
//		return dn;
//	}
	
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
	
	protected void setLabel(Point p, int label) { // (u,v) are image coordinates
		setLabel((int)p.getX(), (int)p.getY(), label);
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
