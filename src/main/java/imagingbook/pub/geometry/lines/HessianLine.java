/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.pub.geometry.lines;

import java.util.Locale;

import imagingbook.pub.geometry.basic.Point;

/**
 * This class represents a straight line in Hessian normal form, i.e., x *
 * cos(angle) + y * sin(angle) = radius.
 */
public class HessianLine extends AlgebraicLine {
	protected final double angle;
	protected final double radius;
	
	// static factory methods ----------------------------------------
	
	public static HessianLine create(Point p1, Point p2) {
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

	// ------------------------------------------------------------------------------
	
//	public static void main(String[] args) {
//		Point p1 = Point.create(30, 10);
//		Point p2 = Point.create(200, 100);
//		
//		HessianLine L = HessianLine.create(p1, p2);
//		System.out.println(L.toString());
//		
//		System.out.println("d1 = " + L.getDistance(p1));
//		System.out.println("d2 = " + L.getDistance(p2));
//		System.out.println("d3 = " + L.getDistance(Point.create(0, 0)));	
//	}
	
//	HessianLine <angle = 2.058, radius = -5.199>
//	d1 = 0.0
//	d2 = -5.329070518200751E-15
//	d3 = 5.198752449100363
}
