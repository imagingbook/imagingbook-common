/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2018 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.pub.hough.lines;

import java.awt.geom.Point2D;
import java.util.Locale;

import ij.process.ImageProcessor;

/**
 * This class represents an algebraic line of the form a * x + b * y + c = 0.
 * All instances are normalized such that ||(a,b)|| = 1.
 * Instances of this class are immutable.
 * @author WB
 *
 */
public class AlgebraicLine {
	
	protected final double a;
	protected final double b;
	protected final double c;
	
	// static factory methods ----------------------------------------
	
	public static AlgebraicLine create(double a, double b, double c) {
		double norm = Math.sqrt(a * a + b * b);
		return new AlgebraicLine(a / norm, b / norm , c / norm);
	}
	
	public static AlgebraicLine create(Point2D p1, Point2D p2) {
		double a = p1.getY() - p2.getY();
		double b = p2.getX() - p1.getX();
		double c = -a * p1.getX() - b * p1.getY();
		return create(a, b, c);
	}
	
	// constructors --------------------------------------------------
	
	protected AlgebraicLine(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	// getter/setter methods ------------------------------------------
	
	public final double getA() {
		return a;
	}

	public final double getB() {
		return b;
	}

	public final double getC() {
		return c;
	}

	// other methods ------------------------------------------
	
	/**
	 * Returns the perpendicular distance between this line and the point (x, y).
	 * The result may be positive or negative, depending on which side of the line
	 * (x, y) is located. It is assumed that the line is normalized
	 * 
	 * @param x x-coordinate of point position.
	 * @param y y-coordinate of point position.
	 * @return The perpendicular distance between this line and the point (x, y).
	 */
	public double getDistance(double x, double y) {
		return (a * x + b * y + c) / Math.sqrt(a * a + b * b);
	}
	
	
	/**
	 * Returns the perpendicular distance between this line and the point p. The
	 * result may be positive or negative, depending on which side of the line p is
	 * located.
	 * 
	 * @param p point position.
	 * @return The perpendicular distance between this line and the point p.
	 */
	public double getDistance(Point2D p) {
		return getDistance(p.getX(), p.getY());
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <a = %.3f, b = %.3f, c = %.3f>",
				this.getClass().getSimpleName(), a, b, c);
	}
	
	/**
	 * This is a brute-force drawing method which simply marks all image pixels that
	 * are sufficiently close to the HoughLine hl. The drawing color for ip must be
	 * previously set.
	 * 
	 * @param ip        The ImageProcessor to draw to.
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
	
	// ------------------------------------------------------------------------------
	
//	public static void main(String[] args) {
//		Point2D p1 = new Point(30, 10);
//		Point2D p2 = new Point(200, 100);
//		
//		AlgebraicLine L = AlgebraicLine.create(p1, p2);
//		System.out.println(L.toString());
//		
//	}
}
