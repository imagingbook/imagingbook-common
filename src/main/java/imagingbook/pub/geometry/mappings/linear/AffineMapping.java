/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;

import java.awt.geom.Point2D;

import imagingbook.lib.math.Matrix;
import imagingbook.lib.settings.PrintPrecision;

/**
 * This class represents an affine transformation in 2D, which can be defined 
 * by three pairs of corresponding points.
 */
public class AffineMapping extends ProjectiveMapping {

	/**
	 * Creates the identity mapping.
	 */
	public AffineMapping() {
		super();
	}

	/**
	 * Creates an affine mapping from the specified matrix elements.
	 * @param a00 matrix element A_00
	 * @param a01 matrix element A_01
	 * @param a02 matrix element A_02
	 * @param a10 matrix element A_10
	 * @param a11 matrix element A_11
	 * @param a12 matrix element A_12
	 * @param inv set true if this mapping represents an inverse transformation
	 */
	public AffineMapping (
			double a00, double a01, double a02, 
			double a10, double a11, double a12) {
		super(a00, a01, a02, a10, a11, a12, 0, 0);
	}

	/**
	 * Creates a new affine mapping from an existing affine mapping.
	 * @param am a given projective mapping
	 */
	public AffineMapping(AffineMapping am) {
		super(am);
	}

	/**
	 * Creates a projective mapping between arbitrary triangles A, B.
	 * @param A1 point 1 of source triangle A
	 * @param A2 point 2 of source triangle A
	 * @param A3 point 3 of source triangle A
	 * @param B1 point 1 of source triangle B
	 * @param B2 point 2 of source triangle B
	 * @param B3 point 3 of source triangle B
	 */
	public AffineMapping(Point2D A1, Point2D A2, Point2D A3, Point2D B1, Point2D B2, Point2D B3) {
		super();
		double ax1 = A1.getX(), ax2 = A2.getX(), ax3 = A3.getX();
		double ay1 = A1.getY(), ay2 = A2.getY(), ay3 = A3.getY();
		double bx1 = B1.getX(), bx2 = B2.getX(), bx3 = B3.getX();
		double by1 = B1.getY(), by2 = B2.getY(), by3 = B3.getY();

		double S = ax1 * (ay3 - ay2) + ax2 * (ay1 - ay3) + ax3 * (ay2 - ay1); // TODO: check S for zero value and throw exception!
		a00 = (ay1 * (bx2 - bx3) + ay2 * (bx3 - bx1) + ay3 * (bx1 - bx2)) / S;
		a01 = (ax1 * (bx3 - bx2) + ax2 * (bx1 - bx3) + ax3 * (bx2 - bx1)) / S;
		a10 = (ay1 * (by2 - by3) + ay2 * (by3 - by1) + ay3 * (by1 - by2)) / S;
		a11 = (ax1 * (by3 - by2) + ax2 * (by1 - by3) + ax3 * (by2 - by1)) / S;
		a02 = (ax1*(ay3*bx2-ay2*bx3) + ax2*(ay1*bx3-ay3*bx1) + ax3*(ay2*bx1-ay1*bx2)) / S;
		a12 = (ax1*(ay3*by2-ay2*by3) + ax2*(ay1*by3-ay3*by1) + ax3*(ay2*by1-ay1*by2)) / S;
	}

	/**
	 * Creates a projective mapping between arbitrary triangles A, B.
	 * @param A points of the source triangle
	 * @param B points of the target triangle
	 */
	public AffineMapping(Point2D[] A, Point2D[] B) {	// TODO: check length of A, B
		this(A[0], A[1], A[2], B[0], B[1], B[2]);
	}

	/**
	 * Concatenates this mapping A with another affine mapping B and returns
	 * a new mapping C, such that C(x) = B(A(x)).
	 * @param B the second mapping
	 * @return the concatenated affine mapping
	 */
	public AffineMapping concat(AffineMapping B) {	
		double c00 = B.a00*a00 + B.a01*a10;
		double c01 = B.a00*a01 + B.a01*a11;
		double c02 = B.a00*a02 + B.a01*a12 + B.a02;
		
		double c10 = B.a10*a00 + B.a11*a10;
		double c11 = B.a10*a01 + B.a11*a11;
		double c12 = B.a10*a02 + B.a11*a12 + B.a12;
		
		return new AffineMapping(c00, c01, c02, c10, c11, c12);
	}


	/**
	 * {@inheritDoc}
	 * Note that inverting an affine transformation always yields
	 * another affine transformation.
	 */
	public AffineMapping getInverse() {
		double det = a00*a11 - a01*a10;	
		double b00 = a11 / det; 
		double b01 = - a01 / det; 
		double b02 = (a01*a12 - a02*a11) / det;	
		double b10 = - a10 / det; 
		double b11 = a00 / det; 
		double b12 = (a02*a10 - a00*a12) / det;
		
		return new AffineMapping(b00, b01, b02, b10, b11, b12);
	}

	/**
	 * {@inheritDoc}
	 * @return a new affine mapping
	 */
	public AffineMapping duplicate() {
		return new AffineMapping(this);
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
	
	public static AffineMapping fromWarpParameters(double[] p) {
//		a00 = p[0] + 1;
//		a01 = p[1];
//		a10 = p[2];
//		a11 = p[3] + 1;
//		a02 = p[4];
//		a12 = p[5];
		return new AffineMapping(p[0] + 1, p[1], p[2], p[3] + 1, p[4], p[5]);
	}

//	@Override
//	public void setWarpParameters(double[] p) {
//		a00 = p[0] + 1;
//		a01 = p[1];
//		a10 = p[2];
//		a11 = p[3] + 1;
//		a02 = p[4];
//		a12 = p[5];
//	}

	@Override
	public double[][] getWarpJacobian(double[] xy) {
		final double x = xy[0];
		final double y = xy[1];
		return new double[][]
				{{x, y, 0, 0, 1, 0},
			{0, 0, x, y, 0, 1}};
	}
	
	// ----------------------------------------------------------------------
	
	/**
	 * For testing only.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		PrintPrecision.set(6);
		double[][] A = 
				{{-2, 4, -3}, 
				{3, 7, 2}, 
				{0.0, 0.0, 1.000000}};
		System.out.println("A = \n" + Matrix.toString(A));
		System.out.println();
		double[][] Ai = Matrix.inverse(A);
		System.out.println("Ai = \n" + Matrix.toString(Ai));
		
		double[][] I = Matrix.multiply(A, Ai);
		System.out.println("\ntest: should be the  identity matrix: = \n" + Matrix.toString(I));
	}

}




