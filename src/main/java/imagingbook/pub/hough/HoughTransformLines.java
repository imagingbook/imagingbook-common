/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.pub.hough;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ij.IJ;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

/**
 * This class implements the Hough Transform for straight lines.
 * This version fixes the problem with vertical lines (theta = 0).
 * TODO: merge the two implementations, add bias correction
 * @author WB
 * @version 2015/11/13
 */
public class HoughTransformLines {
	
	public static class Parameters {
		/** Number of angular steps over [0, pi] */
		public int nAng = 256;	
		/** Number of radial steps in each pos/neg direction (accum. size = 2 * nRad + 1) */
		public int nRad = 128;	
		public boolean showProgress = true;
		public boolean showCheckImage = true;
		public boolean debug = false;
	}
	
	private final Parameters params;
	
	private final int nAng;				// number of angular steps over [0, pi]
	private final int nRad;				// number of radial steps in each pos/neg direction
	
	private final int M, N;				// size of the reference frame (image)
	private final double xc, yc; 		// reference point (x/y-coordinate of image center)
	
	private final double dAng;			// increment of angle
	private final double dRad; 			// increment of radius
	private final int    cRad;			// array index representing the zero radius
	
	private final int accWidth;			// width of the accumulator array (angular direction)
	private final int accHeight;		// height of the accumulator array (radial direction)
	private final int[][] accumulator; 		// accumulator array
	private final int[][] accumulatorMax;	// accumulator, with local maxima only
	
	private final double[] cosTable;	// tabulated cosine values
	private final double[] sinTable;	// tabulated sine values
	
	// --------------  public constructor(s) ------------------------
	
	/**
	 * Creates a new Hough transform from the image I.
	 * @param I input image, relevant (edge) points have pixel
	 * values greater 0.
	 * @param params parameter object.
	 */
	public HoughTransformLines(ImageProcessor I, Parameters params) {
		this(I.getWidth(), I.getHeight(), params);
		process(I);
	}
	
	/**
	 * Creates a new Hough transform from a sequence of 2D points.
	 * Parameters M, N are only used to specify the reference point
	 * (usually at the center of the image).
	 * Use this constructor if the relevant image points are collected 
	 * separately.
	 * @param points an array of 2D points.
	 * @param M width of the corresponding image plane.
	 * @param N height of the corresponding image plane.
	 * @param params parameter object.
	 */
	public HoughTransformLines(Point2D[] points, int M, int N, Parameters params) {
		this(M, N, params);
		process(points);
	}


	// Non-public constructor used by public constructors (to set up all final members variables.
	private HoughTransformLines(int M, int N, Parameters params) {
		this.params = (params == null) ? new Parameters() : params;
		this.M = M;
		this.N = N;
		this.xc = M / 2; // integer value
		this.yc = N / 2; // integer value
		this.nAng = params.nAng;
		this.nRad = params.nRad;
		this.dAng = Math.PI / nAng;
		this.dRad = 0.5 * Math.sqrt(M * M + N * N) / nRad;	// nRad radial steps over half the diagonal length
		this.cRad = nRad;
		this.accWidth = nAng;
		this.accHeight = nRad + 1 + nRad;
		this.accumulator    = new int[accWidth][accHeight]; // cells are initialized to zero!
		this.accumulatorMax = new int[accWidth][accHeight];
		this.cosTable = makeCosTable();
		this.sinTable = makeSinTable();
	}
	
	// --------------  public methods ------------------------
	
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
		findLocalMaxima();
		// create an array of n blank HoughLine objects (with initial count = -1):
		HoughLine[] lineArr = new HoughLine[maxLines];
		for (int i = 0; i < lineArr.length; i++) {
			lineArr[i] = new HoughLine(); // new HoughLine(0.0, 0.0, -1);
		}

		for (int ri = 0; ri < accHeight; ri++) {
			for (int ai = 0; ai < accWidth; ai++) {
				int hcount = accumulatorMax[ai][ri];
				if (hcount >= amin) {
					HoughLine last = lineArr[lineArr.length - 1];
					// last holds the weakest line found so far - replace it?
					if (hcount > last.count) {	
						last.angle = angleFromIndex(ai);	// replace the weakest line
						last.radius = radiusFromIndex(ri);
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
	 * @param ai angle index [0,...,nAng-1]
	 * @return Angle [0,...,PI] for angle index ai
	 */
	public double angleFromIndex(int ai) {
		return ai * dAng;
	}
	
	/**
	 * Calculates the actual radius for radius index ri.
	 * @param ri radius index [0,...,nRad-1].
	 * @return Radius [-maxRad,...,maxRad] with respect to reference point (xc, yc).
	 */
	public double radiusFromIndex(int ri) {
		return (ri - cRad) * dRad;
	}
	
	private int radiusToIndex(double rad) {
		return cRad + (int) Math.rint(rad / dRad);
	}
	
//	public int[][] getAccumulator() {
//		return accumulator;
//	}
	
//	public int[][] getAccumulatorMax() {
//		return accumulatorMax;
//	}
	
	/**
	 * Creates and returns an image of the 2D accumulator array.
	 * @return A FloatProcessor (since accumulator values may be large).
	 */
	public FloatProcessor getAccumulatorImage() {
		return new FloatProcessor(accumulator);
	}
	
	/**
	 * Creates and returns an image of the extended 2D accumulator array,
	 * produced by adding a vertically mirrored copy of the accumulator
	 * to its right end.
	 * @return A FloatProcessor (since accumulator values may be large).
	 */
	public FloatProcessor getAccumulatorImageExtended() {
		FloatProcessor fp = new FloatProcessor(2 * accWidth, accHeight);
		for (int ai = 0; ai < accWidth; ai++) {
			for (int ri = 0; ri < accHeight; ri++) {
				fp.setf(ai, ri, accumulator[ai][ri]);
				if (ri > 0) {
					fp.setf(accWidth + ai, ri, accumulator[ai][accHeight - ri]);
				}
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
		return new FloatProcessor(accumulatorMax);
	}
	
	// --------------  nonpublic methods ------------------------
	
	private double[] makeCosTable() {
		double[] cosTab = new double[nAng];
		for (int ai = 0; ai < nAng; ai++) {
			double angle = dAng * ai;
			cosTab[ai] = Math.cos(angle);
		}
		return cosTab;
	}
	
	private double[] makeSinTable() {
		double[] sinTab = new double[nAng];
		for (int ai = 0; ai < nAng; ai++) {
			double angle = dAng * ai;
			sinTab[ai] = Math.sin(angle);
		}
		return sinTab;
	}
	
	private void process(ImageProcessor ip) {
//		ByteProcessor check = new ByteProcessor(M, N);
		if (params.showProgress) IJ.showStatus("filling accumulator ...");
		for (int v = 0; v < N; v++) {
			if (params.showProgress) IJ.showProgress(v, N);
			for (int u = 0; u < M; u++) {
				if ((0xFFFFFF & ip.get(u, v)) != 0) { // this is a foreground (edge) pixel - use ImageAccessor??
					processPoint(u, v);
//					check.putPixel(u, v, 128);
				}
			}
		}
		if (params.showProgress) 
			IJ.showProgress(1, 1);
//		if (params.showCheckImage)
//			(new ImagePlus("Check", check)).show();
	}
	
	private void process(Point2D[] points) {
		if (params.showProgress) IJ.showStatus("filling accumulator ...");
		for (int i = 0; i < points.length; i++) {
			if (params.showProgress && i % 50 == 0) IJ.showProgress(i, points.length);
			Point2D p = points[i];
			if (p != null) {
				processPoint(p.getX(), p.getY());
			}
		}
		if (params.showProgress) IJ.showProgress(1, 1);
	}


	private void processPoint(double u, double v) {
		final double x = u - xc;
		final double y = v - yc;
		for (int ai = 0; ai < accWidth; ai++) {
//			double theta = dAng * ai;
//			double r = x * Math.cos(theta) + y * Math.sin(theta);
			double r = x * cosTable[ai] + y * sinTable[ai];	// sin/cos tables improve speed!
			int ri =  radiusToIndex(r); // cRad + (int) Math.rint(r / dRad); - changed
			if (ri >= 0 && ri < accHeight) {
				accumulator[ai][ri]++;
			}
		}
	}
	

	/**
	 * This version considers that the accumulator is not truly
	 * periodic but the continuation at its right boundary is
	 * vertically mirrored.
	 */
	private void findLocalMaxima() {
		if (params.showProgress) IJ.showStatus("finding local maxima");
		int count = 0;
		for (int aC = 0; aC < accWidth; aC++) {	// center angle index
			// angle index ai is treated cyclically but the accumulator 
			// must be mirrored vertically at boundaries:
			int a0 = aC - 1;	// left angle index
			boolean mL = false;	// left vertical mirror flag
			if (a0 < 0) {
				a0 = accWidth - 1;	
				mL = true;	// left vertical mirror required
			}			
			int a1 = aC + 1;	// right angle index
			boolean mR = false; // right vertical mirror flag
			if (a1 >= accWidth) {
				a1 = 0;	
				mR = true;	// right vertical mirror required
			}
			
			for (int rC = 1; rC < accHeight - 1; rC++) {
				// do we need to swap vertical on either side?
				int r0 = (mL) ? accHeight - rC + 1 : rC - 1; 
				int r1 = (mR) ? accHeight - rC - 1 : rC + 1;			
				int vC = accumulator[aC][rC];
				// this test is critical if 2 identical cell values 
				// appear next to each other!
				boolean ismax =
						vC > accumulator[a1][rC] &&	// 0
						vC > accumulator[a1][r0] &&	// 1
						vC > accumulator[aC][r0] &&	// 2
						vC > accumulator[a0][r0] &&	// 3
						vC > accumulator[a0][rC] &&	// 4
						vC > accumulator[a0][r1] &&	// 5
						vC > accumulator[aC][r1] &&	// 6
						vC > accumulator[a1][r1] ;	// 7					
				if (ismax) {
					accumulatorMax[aC][rC] = vC;
					count++;
					if (params.debug && vC > 50) {
						IJ.log("found max at " + aC + " / " + rC);
					}
				}
			}
		}
		if (params.debug) IJ.log("found maxima: " + count);
	}
	
	
	// old version
//	private void findLocalMaxima() {
//	if (params.showProgress) IJ.showStatus("finding local maxima");
//	int count = 0;
//	for (int ia = 0; ia < accWidth; ia++) {
//		// angle index ai is treated cyclically:
//		final int a1 = (ia > 0) ? ia - 1 : accWidth - 1;
//		final int a2 = (ia < accWidth - 1) ? ia + 1 : 0;
//		for (int ir = 1; ir < accHeight - 1; ir++) {
//			int ha = accumulator[ia][ir];
//			// this test is critical if 2 identical cell values 
//			// appear next to each other!
//			boolean ismax =
//				ha > accumulator[a1][ir - 1] &&
//				ha > accumulator[a1][ir]     &&
//				ha > accumulator[a1][ir + 1] &&
//				ha > accumulator[ia][ir - 1] &&
//				ha > accumulator[ia][ir + 1] &&
//				ha > accumulator[a2][ir - 1] &&
//				ha > accumulator[a2][ir]     &&
//				ha > accumulator[a2][ir + 1] ;
//			if (ismax) {
//				accumulatorMax[ia][ir] = ha;
//				count++;
//				if (params.debug && ha > 50){
//					IJ.log("found max at " + ia + " / " + ir);
//				}
//			}
//		}
//	}
//	if (params.debug) IJ.log("found maxima: " + count);
//}
	
	/**
	 * This class represents a straight line in Hessian normal form, 
	 * i.e., x * cos(angle) + y * sin(angle) = radius.
	 * It is implemented as a non-static inner class of {@link HoughTransformLines}
	 * since its instances refer to the particular enclosing Hough transform object.
	 */
	public class HoughLine implements Comparable<HoughLine> {
		private double angle;
		private double radius;
		private int count;
		
		private HoughLine() {
			this(0.0, 0.0, -1);
		}

		/**
		 * Public constructor (only available with an enclosing HoughTransformLines instance!)
		 * 
		 * @param angle angle
		 * @param radius radius
		 * @param count count
		 */
		public HoughLine(double angle, double radius, int count) {
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
		 * point (xc, yc) of the enclosing  {@link HoughTransformLines}
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
			// Note that the reference point is a property of the 
			// containing HoughTransform object:
			return HoughTransformLines.this.getReferencePoint();
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
		 * This is a brute-force drawing method which simply marks all
		 * image pixels that are sufficiently close to the HoughLine hl.
		 * The drawing color for ip must be previously set.
		 * @param ip The ImageProcessor to draw to.
		 * @param thickness The thickness of the lines to be drawn.
		 */
		public void draw(ImageProcessor ip, double thickness) {
			final int w = ip.getWidth();
			final int h = ip.getHeight();
			final double dmax = 0.5 * thickness;
			for (int u = 0; u < w; u++) {
				for (int v = 0; v < h; v++) {
					// get the distance between (u,v) and the line hl:
					double d = Math.abs(this.getDistance(u, v));
					if (d <= dmax) {
						ip.drawPixel(u, v);
					}
				}
			}
			
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


