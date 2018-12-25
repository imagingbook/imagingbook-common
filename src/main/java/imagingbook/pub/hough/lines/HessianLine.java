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

/**
 * This class represents a straight line in Hessian normal form, i.e., x *
 * cos(angle) + y * sin(angle) = radius.
 */
public class HessianLine extends AlgebraicLine {
	protected final double angle;
	protected final double radius;
	
	// static factory methods ----------------------------------------
	
	public static HessianLine create(Point2D p1, Point2D p2) {
		return new HessianLine(AlgebraicLine.create(p1, p2));
	}
	
	// constructors --------------------------------------------------

	public HessianLine(double angle, double radius) {
		super(Math.cos(angle), Math.sin(angle), -radius);	
		this.angle = angle;
		this.radius = radius;
	}
	
	public HessianLine(AlgebraicLine line) {
		super(line.a, line.b, line.c);
		this.angle = Math.atan2(line.b, line.a);
		this.radius = -line.c / Math.sqrt(line.a * line.a + line.b * line.b);
	}
	
	// getter/setter methods ------------------------------------------
	
	public double getAngle() {
		return angle;
	}

	public double getRadius() {
		return radius;
	}
	
	// -------------------------------------------------------------------
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s <angle = %.3f, radius = %.3f>",
				this.getClass().getSimpleName(), angle, radius);
	}

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
		return a * x + b * y + c;
	}


	// ------------------------------------------------------------------------------
	
//	public static void main(String[] args) {
//		Point2D p1 = new Point(30, 10);
//		Point2D p2 = new Point(200, 100);
//		
//		HessianLine L = HessianLine.create(p1, p2);
//		System.out.println(L.toString());
//		
//	}
}
