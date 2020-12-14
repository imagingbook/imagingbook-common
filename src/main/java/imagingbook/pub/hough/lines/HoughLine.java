/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.pub.hough.lines;

import static imagingbook.lib.math.Arithmetic.sqr;

import java.util.Locale;

import ij.gui.PolygonRoi;
import ij.gui.Roi;
import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.geometry.lines.AlgebraicLine;
import imagingbook.pub.geometry.lines.HessianLine;

/**
 * This class represents a straight line used in the Hough transform
 * for straight lines (see {@link imagingbook.pub.hough.HoughTransformLines}).
 * It inherits from {@link HessianLine} which is, in turn, a subclass of 
 * {@link AlgebraicLine}.
 * Unlike a {@link HessianLine} the reference point is not necessarily at
 * the coordinate origin but is arbitrary.
 */
public class HoughLine extends HessianLine implements Comparable<HoughLine> {
	private final int count;			// pixel votes for this line
	private final double xRef, yRef;	// reference point
	
	
	// static factory methods -------------------------------
	
	public static HoughLine fromPoints(Point p1, Point p2, Point pRef, int count) {
//		double xRef = pRef.getX();
//		double yRef = pRef.getY();
//		Point p1r = Point.create(p1.getX() - xRef, p1.getY() - yRef);
//		Point p2r = Point.create(p2.getX() - xRef, p2.getY() - yRef);
//		return new HoughLine(HessianLine.fromPoints(p1r, p2r), xRef, yRef, count);
		return new HoughLine(AlgebraicLine.fromPoints(p1, p2), pRef.getX(), pRef.getY(), count);
	}
	
	// constructors -----------------------------------------

	/**
	 * Constructor.
	 * @param angle the line's normal angle (see {@link HessianLine})
	 * @param radius the line's radius (distance to reference point)
	 * @param xRef reference point x-coordinate
	 * @param yRef reference point y-coordinate
	 * @param count pixel votes for this line
	 */
	public HoughLine(double angle, double radius, double xRef, double yRef, int count) {
		super(angle, radius);
		this.xRef = xRef;
		this.yRef = yRef;
		this.count = count;
	}
	
	/**
	 * Creates a new {@link HoughLine} instance from a given
	 * {@link AlgebraicLine} (or any subclass) instance.
	 * The line parameters are adjusted to the specified reference point
	 * (actually only parameter c is modified, since a change of reference point
	 * effects only a shift of the line).
	 * The two lines are equivalent, i.e., contain the same points (x,y).
	 * Thus the distance from a given point (x,y) is the same from the original
	 * line and the new line.
	 * @param L1
	 * @param xRef
	 * @param yRef
	 * @param count
	 */
	public HoughLine(AlgebraicLine L1, double xRef, double yRef, int count) {
		//this(hl.getAngle(), hl.getRadius(), xRef, yRef, count);
		super(L1.getA(), 
			  L1.getB(), 
			  L1.getC() + L1.getA()*(xRef-L1.getXref()) + L1.getB()*(yRef-L1.getYref())); // = a', b', c'
		this.xRef = xRef;
		this.yRef = yRef;
		this.count = count;
	}
	
	// getter/setter methods ------------------------------------------
	
	/**
	 * @return The accumulator count associated with this line.
	 */
	public int getCount() {
		return count;
	}
	
	@Override
	public double getXref() {
		return xRef;
	}
	
	@Override
	public double getYref() {
		return yRef;
	}
	
	// other methods ------------------------------------------
	
	/**
	 * Returns the perpendicular distance between this line and the point (x, y).
	 * The result may be positive or negative, depending on which side of the line
	 * (x, y) is located.
	 * 
	 * @param x x-coordinate of point position.
	 * @param y y-coordinate of point position.
	 * @return The perpendicular distance between this line and the point (x, y).
	 */
	@Override
	public double getDistance(double x, double y) {
		return super.getDistance(x - xRef, y - yRef);
	}
	
	@Override
	public Point getClosestLinePoint(Point p) {
		double s = 1.0; // 1.0 / (sqr(a) + sqr(b)); // assumed to be normalized
		double x = p.getX() - xRef;
		double y = p.getY() - yRef;
		double x0 = s * (sqr(b) * x - a * b * y - a * c);
		double y0 = s * (sqr(a) * y - a * b * x - b * c);
		return Point.create(x0 + xRef, y0 + yRef);
	}
	
	/**
	 * Required by the {@link Comparable} interface, used for sorting lines by their
	 * point count (in descending order, i.e., strong lines come first).
	 * @param other another {@link HoughLine} instance.
	 */
	@Override
	public int compareTo(HoughLine other) {
		return Integer.compare(other.count, this.count);
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <angle = %.3f, radius = %.3f, xRef = %.3f, yRef = %.3f, count = %d>",
				this.getClass().getSimpleName(), getAngle(), getRadius(), getXref(), getYref(), count);
	}
	
	
	// ------------------------------------------------------------------------------
	
	
//	/**
//	 * This is a brute-force drawing method which simply marks all image pixels that
//	 * are sufficiently close to the HoughLine hl. The drawing color for ip must be
//	 * previously set.
//	 * 
//	 * @param ip        the {@link ImageProcessor} instance to draw to.
//	 * @param thickness the thickness of the lines to be drawn.
//	 */
//	@Override
//	public void draw(ImageProcessor ip, double thickness) {
//		final int w = ip.getWidth();
//		final int h = ip.getHeight();
//		final double dmax = 0.5 * thickness;
//		for (int u = 0; u < w; u++) {
//			for (int v = 0; v < h; v++) {
//				// get the distance between (u,v) and the line hl:
//				double d = Math.abs(this.getDistance(u, v));
//				if (d <= dmax) {
//					ip.drawPixel(u, v);
//				}
//			}
//		}
//	}
	
	/**
	 * Creates a vector line to be used an element in an ImageJ graphic overlay
	 * (see {@link ij.gui.Overlay}). The length of the displayed line 
	 * is equivalent to the distance of the reference point (typically the
	 * image center) to the coordinate origin.
	 * @return the new line
	 * @deprecated
	 */
	public PolygonRoi makeLineRoi() {
		double length = Math.sqrt(xRef * xRef + yRef * yRef);
		return this.makeLineRoi(length);
	}
	
	/**
	 * Creates a vector line to be used an element in an ImageJ graphic overlay
	 * (see {@link ij.gui.Overlay}). The length of the displayed line 
	 * is measured from its center point (the point closest to the reference
	 * point) in both directions.
	 * 
	 * @param length the length of the line
	 * @return the new line
	 */
	public PolygonRoi makeLineRoi(double length) {
		// unit vector perpendicular to the line
		double dx = Math.cos(angle);	
		double dy = Math.sin(angle);
		// calculate the line's center point (closest to the reference point)
		double x0 = xRef + radius * dx;
		double y0 = yRef + radius * dy;
		// calculate the line end points (using normal vectors)
		float x1 = (float) (x0 + dy * length);
		float y1 = (float) (y0 - dx * length);
		float x2 = (float) (x0 - dy * length);
		float y2 = (float) (y0 + dx * length);
		float[] xpoints = { x1, x2 };
		float[] ypoints = { y1, y2 };
		return new PolygonRoi(xpoints, ypoints, Roi.POLYLINE);
	}
	
	
	// ------------------------------------------------------------------------------
	
//	public static void main(String[] args) {
//		Point p1 = Point.create(30, 10);
//		Point p2 = Point.create(200, 100);
//		
//		HoughLine L = HoughLine.create(p1, p2, 90, 60, 0);
//		System.out.println(L.toString());
//	}
	
	// HoughLine <angle = 2.058, radius = -16.116, xRef = 90.000, yRef = 60.000, count = 0>
}
