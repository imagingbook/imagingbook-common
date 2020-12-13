/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.pub.geometry.lines;

import static imagingbook.lib.math.Arithmetic.sqr;

import java.util.Locale;

import ij.process.ImageProcessor;
import imagingbook.lib.math.Arithmetic;
import imagingbook.pub.geometry.basic.Point;

/**
 * This class represents an algebraic line of the form a * x + b * y + c = 0.
 * Instances are immutable and normalized such that ||(a,b)|| = 1.
 * @author WB
 *
 */
public class AlgebraicLine {
	
	protected final double a, b, c;

	// static factory methods ----------------------------------------
	
	public static AlgebraicLine create(double a, double b, double c) {
		double norm = Math.sqrt(sqr(a) + sqr(b));
		if (Arithmetic.isZero(norm)) {
			throw new IllegalArgumentException("a and b may not both be zero");
		}
		return new AlgebraicLine(a / norm, b / norm , c / norm);
	}
	
	public static AlgebraicLine create(Point p1, Point p2) {
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
	
	protected AlgebraicLine(AlgebraicLine al) {
		this(al.a, al.b, al.c);
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
	 * (x, y) is located. It is assumed that the line is normalized, i.e.,
	 * ||(a,b)|| = 1.
	 * 
	 * @param x x-coordinate of point position.
	 * @param y y-coordinate of point position.
	 * @return The perpendicular distance between this line and the point (x, y).
	 */
	public double getDistance(double x, double y) {
		return (a * x + b * y + c);
	}
	
	/**
	 * Returns the perpendicular distance between this line and the point p. The
	 * result may be positive or negative, depending on which side of the line p is
	 * located.
	 * 
	 * @param p point position.
	 * @return The perpendicular distance between this line and the point p.
	 */
	public double getDistance(Point p) {
		return getDistance(p.getX(), p.getY());
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <a = %.3f, b = %.3f, c = %.3f>",
				this.getClass().getSimpleName(), a, b, c);
	}
	
	
	// --------------------------------------------------------------------------------
	
	/**
	 * This is a brute-force drawing method which simply marks all image pixels that
	 * are sufficiently close to the HoughLine hl. The drawing color for ip must be
	 * previously set.
	 * 
	 * @param ip        the {@link ImageProcessor} instance to draw to.
	 * @param thickness the thickness of the lines to be drawn.
	 * @deprecated Use {@link Utils#draw(AlgebraicLine, ImageProcessor, double)} instead!
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
	
}
