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
	
	public static AlgebraicLine fromPoints(Point p1, Point p2) {
		double a = p1.getY() - p2.getY();
		double b = p2.getX() - p1.getX();
		double c = -a * p1.getX() - b * p1.getY();
		return new AlgebraicLine(a, b, c);
	}
	
	// constructors --------------------------------------------------

	public AlgebraicLine(double a, double b, double c) {
		double norm = Math.sqrt(sqr(a) + sqr(b));
		if (Arithmetic.isZero(norm)) {
			throw new IllegalArgumentException("a and b may not both be zero");
		}
		this.a = a / norm;
		this.b = b / norm;
		this.c = c / norm;
	}
	
	public AlgebraicLine(AlgebraicLine L) {
		this(L.a, L.b, L.c);
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
	
	public double getXref() {
		return 0.0;
	}
	
	public double getYref() {
		return 0.0;
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
	
	/**
	 * Returns the point on the line that is closest to the specified
	 * 2D point. The line is assumed to be normalized.
	 * 
	 * @param p an arbitrary 2D point
	 * @return the closest line point
	 */
	public Point getClosestLinePoint(Point p) {
		double s = 1.0; // 1.0 / (sqr(a) + sqr(b)); // assumed to be normalized
		double x = p.getX();
		double y = p.getY();
		double x0 = s * (sqr(b) * x - a * b * y - a * c);
		double y0 = s * (sqr(a) * y - a * b * x - b * c);
		return Point.create(x0, y0);
	}
	
	// -------------------------------------------------------------------
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <a = %.3f, b = %.3f, c = %.3f>",
				this.getClass().getSimpleName(), a, b, c);
	}
	
}
