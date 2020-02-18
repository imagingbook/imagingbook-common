/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.pub.hough.lines;

import java.awt.geom.Point2D;
import java.util.Locale;

import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import imagingbook.pub.hough.HoughTransformLines;

/**
 * This class represents a straight line in Hessian normal form, i.e.,
 * x * cos(angle) + y * sin(angle) = radius. 
 * It is implemented as a non-static inner class of {@link HoughTransformLines} 
 * since instances reference the enclosing Hough transform object.
 */
public class HoughLine extends HessianLine implements Comparable<HoughLine> {
	private final int count;
	private final double xRef;
	private final double yRef;

	/**
	 * Public constructor
	 * @param angle the line's normal angle
	 * @param radius the line's radius (distance to reference point)
	 * @param xRef reference point x-coordinate
	 * @param yRef reference point y-coordinate
	 * @param count count
	 */
	public HoughLine(double angle, double radius, double xRef, double yRef, int count) {
		super(angle, radius);
		this.xRef = xRef;
		this.yRef = yRef;
		this.count = count;
	}
	
	public HoughLine(HessianLine hline, double xRef, double yRef, int count) {
		this(hline.getAngle(), hline.getRadius(), xRef, yRef, count);
	}
	
	public static HoughLine create(Point2D p1, Point2D p2, double xRef, double yRef, int count) {
		Point2D p1r = new Point2D.Double(p1.getX() - xRef, p1.getY() - yRef);
		Point2D p2r = new Point2D.Double(p2.getX() - xRef, p2.getY() - yRef);
		return new HoughLine(HessianLine.create(p1r, p2r), xRef, yRef, count);
	}
	
	// getter/setter methods ------------------------------------------
	
	/**
	 * @return The accumulator count associated with this line.
	 */
	public int getCount() {
		return count;
	}
	
	public double getXref() {
		return xRef;
	}
	
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
	
	/**
	 * This is a brute-force drawing method which simply marks all image pixels that
	 * are sufficiently close to the HoughLine hl. The drawing color for ip must be
	 * previously set.
	 * 
	 * @param ip        the {@link ImageProcessor} instance to draw to.
	 * @param thickness the thickness of the lines to be drawn.
	 */
	@Override
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
	 * Creates a vector line to be used an element in an ImageJ graphic overlay
	 * (see {@link ij.gui.Overlay}). The length of the displayed line 
	 * is equivalent to the distance of the reference point (typically the
	 * image center) to the coordinate origin.
	 * @return the new line
	 */
	public PolygonRoi makeLine() {
		double length = Math.sqrt(xRef * xRef + yRef * yRef);
		return this.makeLine(length);
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
	public PolygonRoi makeLine(double length) {
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
	
	/**
	 * Required by the {@link Comparable} interface, used for sorting lines by their
	 * point count.
	 * @param hl2 another {@link HoughLine} instance.
	 */
	public int compareTo(HoughLine hl2) {
		HoughLine hl1 = this;
		if (hl1.count > hl2.count)
			return -1;
		else if (hl1.count < hl2.count)
			return 1;
		else
			return 0;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <angle = %.3f, radius = %.3f, xRef = %.3f, yRef = %.3f, count = %d>",
				this.getClass().getSimpleName(), getAngle(), getRadius(), getXref(), getYref(), count);
	}
	
	// ------------------------------------------------------------------------------
	
//	public static void main(String[] args) {
//		Point2D p1 = new Point(30, 10);
//		Point2D p2 = new Point(200, 100);
//		
//		HoughLine L = HoughLine.create(p1, p2, 90, 60, 0);
//		System.out.println(L.toString());
//		
//	}
}
