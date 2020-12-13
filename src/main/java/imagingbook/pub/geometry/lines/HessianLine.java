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

import imagingbook.pub.geometry.basic.Point;

/**
 * This class represents a straight line in Hessian normal form, i.e., 
 * x * cos(angle) + y * sin(angle) = radius.
 * It is a specialization (subclass) of {@link AlgebraicLine}.
 * Instances are immutable.
 */
public class HessianLine extends AlgebraicLine {
	protected final double angle;
	protected final double radius;
	
	// static factory methods ----------------------------------------
	
	public static HessianLine create(Point p1, Point p2) {
		return new HessianLine(AlgebraicLine.create(p1, p2));
	}
	
	public static HessianLine create(double angle, double radius) {
		return new HessianLine(angle, radius);
	}
	
	// constructors --------------------------------------------------

	protected HessianLine(double angle, double radius) {
		super(Math.cos(angle), Math.sin(angle), -radius);	
		this.angle = angle;
		this.radius = radius;
	}
	
	// assumes that al is normalized
	protected HessianLine(AlgebraicLine al) {
		super(al);
		this.angle = Math.atan2(al.b, al.a);
		this.radius = -al.c / Math.sqrt(sqr(al.a) + sqr(al.b));
	}
	
	// getter methods ------------------------------------------
	
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

}
