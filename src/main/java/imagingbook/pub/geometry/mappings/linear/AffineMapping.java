/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;

import java.awt.geom.Point2D;

public class AffineMapping extends ProjectiveMapping {
	
	/**
	 * Creates the identity mapping.
	 */
	public AffineMapping() {
		super();
	}
   
	public AffineMapping (
			double a11, double a12, double a13, 
			double a21, double a22, double a23, 
			boolean inv) {
		super(
				a11, a12, a13, 
				a21, a22, a23, 
				0, 0, inv);
	}
	
	public AffineMapping(LinearMapping lm) {
		super(lm);
		a20 = 0;
		a21 = 0;
		a22 = 1;
	}
	
	public AffineMapping(Point2D A1, Point2D A2, Point2D A3, Point2D B1, Point2D B2, Point2D B3) {
		super();
		double ax1 = A1.getX(), ax2 = A2.getX(), ax3 = A3.getX();
		double ay1 = A1.getY(), ay2 = A2.getY(), ay3 = A3.getY();
		double bx1 = B1.getX(), bx2 = B2.getX(), bx3 = B3.getX();
		double by1 = B1.getY(), by2 = B2.getY(), by3 = B3.getY();
		
		double S = ax1*(ay3-ay2) + ax2*(ay1-ay3) + ax3*(ay2-ay1); // TODO: check S for zero value and throw exception!
		a00 = (ay1*(bx2-bx3) + ay2*(bx3-bx1) + ay3*(bx1-bx2)) / S;
		a01 = (ax1*(bx3-bx2) + ax2*(bx1-bx3) + ax3*(bx2-bx1)) / S;
		a10 = (ay1*(by2-by3) + ay2*(by3-by1) + ay3*(by1-by2)) / S;
		a11 = (ax1*(by3-by2) + ax2*(by1-by3) + ax3*(by2-by1)) / S;
		a02 = 
				(ax1*(ay3*bx2-ay2*bx3) + ax2*(ay1*bx3-ay3*bx1) + ax3*(ay2*bx1-ay1*bx2)) / S;
		a12 = 
				(ax1*(ay3*by2-ay2*by3) + ax2*(ay1*by3-ay3*by1) + ax3*(ay2*by1-ay1*by2)) / S;
	}
	
	public AffineMapping(Point2D[] A, Point2D[] B) {
		this(A[0], A[1], A[2], B[0], B[1], B[2]);
	}
	
	public AffineMapping concat(AffineMapping B) {	// TODO: more general arguments?
		AffineMapping A = new AffineMapping(this);
		A.concatDestructive(B);
		return A;
	}
	
	@Override
	public AffineMapping invert() {
		AffineMapping pm = new AffineMapping(this);
		pm.invertDestructive();
		return pm;
	}
	
	@Override
	public AffineMapping duplicate() {
		return (AffineMapping) this.clone();
	}
	
	// warp parameter support (used in Lucas-Kanade-matcher) --------------------------
	
	@Override
	public int getWarpParameterCount() {
		return 6;
	}
	
	@Override
	public double[] getWarpParameters() {
		double[] p = new double[] {
			a00 - 1, 
			a01,
			a10,
			a11 - 1,
			a02,
			a12 };
		return p;
	}

	@Override
	public void setWarpParameters(double[] p) {
		a00 = p[0] + 1;
		a01 = p[1];
		a10 = p[2];
		a11 = p[3] + 1;
		a02 = p[4];
		a12 = p[5];
	}
	
	@Override
	public double[][] getWarpJacobian(double[] xy) {
		final double x = xy[0];
		final double y = xy[1];
		return new double[][]
				{{x, y, 0, 0, 1, 0},
				 {0, 0, x, y, 0, 1}};
	}
	
}




