/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.pub.hough;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * This class implements the Hough Transform for straight lines.
 * MODIFIED PARAMETER SPACE (no negative RADIUS)!
 * @author WB
 * @version 2015/11/13
 */
@Deprecated
public class HoughTransformLinesPosRadius {
	
	public static class Parameters {
		/** Number of angular steps over [0, pi] */
		public int nAng = 256;
		/** Number of radial steps (pos. radii only) */
		public int nRad = 256;
		public boolean showProgress = true;
		public boolean showCheckImage = true;
		public boolean debug = false;
	}
	
	private final Parameters params;
	
	private final int nAng;				// number of steps for the angle  (a = 0,...,2PI)
	private final int nRad;				// number of steps for the radius (r = 0,...,r_max)
	
	private final int M, N;				// size of the reference frame (image)
	private final double xc, yc; 		// reference point (x/y-coordinate of image center)
	
	private final double dAng;			// increment of angle
	private final double dRad; 			// increment of radius
	private final int    cRad;			// array index for zero radius (r[cRad] = 0)
	
	private final int[][] accumulator; 		// Hough accumulator array
	private final int[][] accumulatorMax;	// Hough accumulator, local maxima only
	
	private final double[] cosTable;
	private final double[] sinTable;
	
	// --------------  public constructor(s) ------------------------
	
	/**
	 * Creates a new Hough transform from the image I.
	 * @param I input image, relevant (edge) points have pixel
	 * values greater 0.
	 * @param params parameter object.
	 */
	public HoughTransformLinesPosRadius(ImageProcessor I, Parameters params) {
		this(I.getWidth(), I.getHeight(), params);
		process(I);
	}
	
	/**
	 * Creates a new Hough transform from a list of 2D points.
	 * Parameters M, N are only used to specify the reference point
	 * (usually at the center of the image).
	 * Use this constructor if the relevant image points are collected 
	 * separately.
	 * @param points an array of 2D points.
	 * @param M width of the corresponding image plane.
	 * @param N height of the corresponding image plane.
	 * @param params parameter object.
	 */
	public HoughTransformLinesPosRadius(Point2D[] points, int M, int N, Parameters params) {
		this(M, N, params);
		process(points);
	}

	// -------------- non-public constructor ------------------------
	
	// used by public constructors (sets up all final members):
	private HoughTransformLinesPosRadius(int M, int N, Parameters params) {
		this.params = (params == null) ? new Parameters() : params;
		this.M = M;
		this.N = N;
		this.xc = M / 2; 
		this.yc = N / 2;
		this.nAng = params.nAng;
		this.nRad = params.nRad;
		this.dAng = 2 * Math.PI / nAng;		// CHANGE
		this.dRad = 0.5 * Math.sqrt(M * M + N * N) / nRad;
		this.cRad = 0; // nRad / 2;	// CHANGE
		this.accumulator    = new int[nAng][nRad]; // cells are initialized to zero!
		this.accumulatorMax = new int[nAng][nRad];
		this.cosTable = makeCosTable();
		this.sinTable = makeSinTable();
	}
	
	// --------------  public methods ------------------------
	
	public int getnRad() {
		return nRad;
	}
	public int getnAng() {
		return nAng;
	}
	
	/**
	 * Finds and returns the parameters of the strongest lines with
	 * a specified min. pixel count. All objects in the returned array
	 * are valid, but the array may be empty.
	 * Note: Could perhaps be implemented more efficiently with insert-sort.
	 * @param amin the minimum accumulator value for each line.
	 * @param maxLines maximum number of (strongest) lines to extract.
	 * @return a possibly empty array of {@link HoughLine} objects.
	 */
	public HoughLine[] getLines(int amin, int maxLines) {
		findAccumulatorPeaks();
		// create an array of n blank HoughLine objects (with initial count = -1):
		HoughLine[] lineArr = new HoughLine[maxLines];
		for (int i = 0; i < lineArr.length; i++) {
			lineArr[i] = new HoughLine(0, 0, -1);
		}

		for (int ir = 0; ir < nRad; ir++) {
			for (int ia = 0; ia < nAng; ia++) {
				int hcount = accumulatorMax[ia][ir];
				if (hcount >= amin) {
					HoughLine last = lineArr[lineArr.length - 1];
					// last holds the weakest line found so far - replace it?
					if (hcount > last.count) {	
						last.angle = angleFromIndex(ia);	// replace the weakest line
						last.radius = radiusFromIndex(ir);
						last.count = hcount;
						// sort all lines for descending 'count':
						Arrays.sort(lineArr); // more efficient with insert sort?
					}
				}
			}
		}
		// lineArr is sorted by count (highest counts first).
		// collect all lines with count >= minPts into a new list:
		List<HoughLine> lineList = new ArrayList<HoughLine>();
		for (HoughLine hl : lineArr) {
			if (hl.getCount() < amin) break;
			lineList.add(hl);
		}
		// convert the list to an array and return:
		return lineList.toArray(new HoughLine[0]);
	}
	
	/**
	 * @return The reference point used by this Hough transform.
	 */
	public Point2D getReferencePoint() {
		return new Point2D.Double(xc, yc);
	}
	
	/**
	 * Calculates the actual angle (in radians) for angle index {@code ai}
	 * @param i angle index [0,...,nAng-1]
	 * @return Angle [0,...,PI] for angle index ai
	 */
	public double angleFromIndex(int i) {
		return i * dAng;
	}
	
	/**
	 * Calculates the actual radius for radius index ri.
	 * @param j radius index [0,...,nRad-1].
	 * @return Radius [-maxRad,...,maxRad] with respect to reference point (xc, yc).
	 */
	public double radiusFromIndex(int j) {
		return (j - cRad) * dRad;
	}
	
	public int[][] getAccumulator() {
		return accumulator;
	}
	
	public int[][] getAccumulatorMax() {
		return accumulatorMax;
	}
	
	/**
	 * Creates and returns an image of the 2D accumulator array.
	 * @return A FloatProcessor (since accumulator values may be large).
	 */
	public FloatProcessor getAccumulatorImage() {
		FloatProcessor fp = new FloatProcessor(nAng, nRad);
		for (int ir = 0; ir < nRad; ir++) {
			for (int ia = 0; ia < nAng; ia++) {
				fp.setf(ia, ir, accumulator[ia][ir]);
			}
		}
		fp.resetMinAndMax();
		return fp;
	}
	
	/**
	 * Creates and returns an image of the local maxima of the
	 * 2D accumulator array.
	 * @return A FloatProcessor (since accumulator values may be large).
	 */
	public FloatProcessor getAccumulatorMaxImage() {
		FloatProcessor fp = new FloatProcessor(nAng, nRad);
		for (int ir = 0; ir < nRad; ir++) {
			for (int ia = 0; ia < nAng; ia++) {
				fp.setf(ia, ir, accumulatorMax[ia][ir]);
			}
		}
		fp.resetMinAndMax();
		return fp;
	}
	
	// --------------  nonpublic methods ------------------------
	
	private double[] makeCosTable() {
		double[] cosTab = new double[nAng];
		for (int ia = 0; ia < nAng; ia++) {
			double theta = dAng * ia;
			cosTab[ia] = Math.cos(theta);
		}
		return cosTab;
	}
	
	private double[] makeSinTable() {
		double[] sinTab = new double[nAng];
		for (int ia = 0; ia < nAng; ia++) {
			double theta = dAng * ia;
			sinTab[ia] = Math.sin(theta);
		}
		return sinTab;
	}
	
	private void process(ImageProcessor ip) {
		ByteProcessor check = new ByteProcessor(M, N);
		if (params.showProgress) IJ.showStatus("filling accumulator ...");
		for (int v = 0; v < N; v++) {
			if (params.showProgress) IJ.showProgress(v, N);
			for (int u = 0; u < M; u++) {
				if ((0xFFFFFF & ip.get(u, v)) != 0) { // this is a foreground (edge) pixel - use ImageAccessor??
					doOnePoint(u, v);
					check.putPixel(u, v, 128);
				}
			}
		}
		if (params.showProgress) 
			IJ.showProgress(1, 1);
		if (params.showCheckImage)
			(new ImagePlus("Check", check)).show();
	}
	
	private void process(Point2D[] points) {
		if (params.showProgress) IJ.showStatus("filling accumulator ...");
		for (int i = 0; i < points.length; i++) {
			if (params.showProgress && i % 50 == 0) IJ.showProgress(i, points.length);
			Point2D p = points[i];
			if (p != null) {
				doOnePoint(p.getX(), p.getY());
			}
		}
		if (params.showProgress) IJ.showProgress(1, 1);
	}

	public int closestRadialIndex(double r) {
		return cRad + (int) Math.rint(r / dRad);
	}

	private void doOnePoint(double u, double v) {
		final double x = u - xc;
		final double y = v - yc;
		for (int ia = 0; ia < nAng; ia++) {
//			double theta = dAng * ai;
//			double r = x * Math.cos(theta) + y * Math.sin(theta);
			double r = x * cosTable[ia] + y * sinTable[ia];	// sin/cos tables improve speed!
			int ir =  closestRadialIndex(r); //cRad + (int) Math.rint(r / dRad);
			if (ir >= 0 && ir < nRad) {
				//accumulator[ia][ir]++;	
				accumulator[ia][ir]+= 1;	
			}
		}
	}
	
	// TODO: lines with ZERO radius cannot be detected!!
	private void findAccumulatorPeaks() {
		if (params.showProgress) IJ.showStatus("finding local maxima");
		int count = 0;
		for (int ia = 0; ia < nAng; ia++) {
			// angle index ai is treated cyclically:
			final int a1 = (ia > 0) ? ia - 1 : nAng - 1;
			final int a2 = (ia < nAng-1) ? ia + 1 : 0;
			for (int ir = 1; ir < nRad - 1; ir++) {
				int ha = accumulator[ia][ir];
				// this test is critical if 2 identical cell values 
				// appear next to each other!
				boolean ismax =
					ha > accumulator[a1][ir - 1] &&
					ha > accumulator[a1][ir]   &&
					ha > accumulator[a1][ir + 1] &&
					ha > accumulator[ia][ir - 1] &&
					ha > accumulator[ia][ir + 1] &&
					ha > accumulator[a2][ir - 1] &&
					ha > accumulator[a2][ir]   &&
					ha > accumulator[a2][ir + 1] ;
				if (ismax) {
					accumulatorMax[ia][ir] = ha;
					count++;
				}
			}
		}
		if (params.debug) IJ.log("found maxima: " + count);
	}
	
	/**
	 * This class represents a straight line in Hessian normal form, 
	 * i.e., x * cos(angle) + y * sin(angle) = radius.
	 * It is implemented as a non-static inner class of {@link HoughTransformLinesPosRadius}
	 * since its instances refer to the particular enclosing Hough transform object.
	 */
	public class HoughLine implements Comparable<HoughLine> {
		private double angle;
		private double radius;
		private int count;

		// public constructor (only available with an enclosing HoughTransformLines instance!)
		public HoughLine(double angle, double radius, int count){
			this.angle  = angle;	
			this.radius = radius;	
			this.count  = count;	
		}
		
		/**
		 * @return The angle of this line.
		 */
		public double getAngle() {
			return angle;
		}
		
		/**
		 * @return The radius of this line with respect to the reference
		 * point (xc, yc) of the enclosing  {@link HoughTransformLinesPosRadius}
		 * instance.
		 */
		public double getRadius() {
			return radius;
		}
		
		/**
		 * @return The accumulator count associated with this line.
		 */
		public int getCount() {
			return count;
		}
		
		public Point2D getReferencePoint() {
			return HoughTransformLinesPosRadius.this.getReferencePoint();
		}
			
		/**
		 * Returns the perpendicular distance between this line and the point (x, y).
		 * The result may be positive or negative, depending on which side of
		 * the line (x, y) is located.
		 * @param x x-coordinate of point position.
		 * @param y y-coordinate of point position.
		 * @return The perpendicular distance between this line and the point (x, y).
		 */
		public double getDistance(double x, double y) {
			final double xs = x - xc;
			final double ys = y - yc;
			return Math.cos(angle) * xs + Math.sin(angle) * ys - radius;
		}
		
		/**
		 * Returns the perpendicular distance between this line and the point p.
		 * The result may be positive or negative, depending on which side of
		 * the line p is located.
		 * @param p point position.
		 * @return The perpendicular distance between this line and the point p.
		 */
		public double getDistance(Point2D p) {
			return getDistance(p.getX(), p.getY());
		}
		
		/**
		 * Required by the {@link Comparable} interface, used for sorting
		 * lines by their point count.
		 * @param hl2 another {@link HoughLine} instance.
		 */
		public int compareTo (HoughLine hl2){
			HoughLine hl1 = this;
			if (hl1.count > hl2.count)
				return -1;
			else if (hl1.count < hl2.count)
				return 1;
			else
				return 0;
		}
		
		public String toString() {
			return String.format(Locale.US, "%s <angle = %.3f, radius = %.3f, count = %d>", 
					HoughLine.class.getSimpleName(), angle, radius, count);
		}

	} // end of class HoughLine
	
} // end of class LinearHT







