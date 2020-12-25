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
import static imagingbook.lib.math.Arithmetic.isZero;

import java.util.Locale;

//import imagingbook.lib.math.Arithmetic;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.basic.Pnt2d.PntDouble;

/**
 * This class represents an algebraic line of the form a * x + b * y + c = 0.
 * Instances are immutable and normalized such that ||(a,b)|| = 1.
 * @author WB
 *
 */
public class AlgebraicLine {
	
	protected final double a, b, c;

	// static factory methods ----------------------------------------
	
	public static AlgebraicLine fromPoints(Pnt2d p1, Pnt2d p2) {
		double a = p1.getY() - p2.getY();
		double b = p2.getX() - p1.getX();
		double c = -a * p1.getX() - b * p1.getY();
		return new AlgebraicLine(a, b, c);
	}
	
	// constructors --------------------------------------------------

	public AlgebraicLine(double a, double b, double c) {
		double norm = Math.sqrt(sqr(a) + sqr(b));
		if (isZero(norm)) {
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
		return (a * (x - this.getXref()) + b * (y - this.getYref()) + c);
	}
	
	/**
	 * Returns the perpendicular distance between this line and the point p. The
	 * result may be positive or negative, depending on which side of the line p is
	 * located.
	 * 
	 * @param p point position.
	 * @return The perpendicular distance between this line and the point p.
	 */
	public double getDistance(Pnt2d p) {
		return getDistance(p.getX(), p.getY());
	}
	
	/**
	 * Returns the point on the line that is closest to the specified
	 * 2D point. The line is assumed to be normalized.
	 * 
	 * @param p an arbitrary 2D point
	 * @return the closest line point
	 */
	public Pnt2d getClosestLinePoint(Pnt2d p) {
		final double s = 1.0; // 1.0 / (sqr(a) + sqr(b)); // assumed to be normalized
		final double xr = this.getXref();
		final double yr = this.getYref();
		double xx = p.getX() - xr;
		double yy = p.getY() - yr;
		double x0 = xr + s * (sqr(b) * xx - a * b * yy - a * c);
		double y0 = yr + s * (sqr(a) * yy - a * b * xx - b * c);
		return PntDouble.from(x0, y0);
	}
	
//	@Override
//	public Point getClosestLinePoint(Point p) {
//		double s = 1.0; // 1.0 / (sqr(a) + sqr(b)); // assumed to be normalized
//		double xx = p.getX() - xRef;
//		double yy = p.getY() - yRef;
//		double x0 = xRef + s * (sqr(b) * xx - a * b * yy - a * c);
//		double y0 = yRef + s * (sqr(a) * yy - a * b * xx - b * c);
//		return Point.create(x0, y0);
//	}
	
//	/**
//	 * Returns the point on the line that is closest to the orgin.
//	 * This is equivalent to {@code getClosestLinePoint(Point.create(0, 0))}.
//	 * See also {@link #getClosestLinePoint(Point)}.
//	 * @return the closest line point
//	 */
//	public Point getClosestLinePoint() {
//		return getClosestLinePoint(Point.ZERO);
//	}
	
	// -------------------------------------------------------------------
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof AlgebraicLine) {
			AlgebraicLine L1 = this;
			AlgebraicLine L2 = (AlgebraicLine) other;
			double delta = 1E-6;
			// get two different points on L1:
			Pnt2d xA = L1.getClosestLinePoint(PntDouble.ZERO);
			Pnt2d xB = PntDouble.from(xA.getX() - L1.b, xA.getY() + L1.a);
			// check if both points are L2 too:
			return (isZero(L2.getDistance(xA), delta) && isZero(L2.getDistance(xB), delta));
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <a = %.3f, b = %.3f, c = %.3f>",
				this.getClass().getSimpleName(), a, b, c);
	}
	
}
