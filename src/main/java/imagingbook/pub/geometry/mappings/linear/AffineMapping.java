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
	 * Creates an affine mapping from an arbitrary 2D triangle A to another triangle B.
	 * @param A1 point 1 of source triangle A
	 * @param A2 point 2 of source triangle A
	 * @param A3 point 3 of source triangle A
	 * @param B1 point 1 of source triangle B
	 * @param B2 point 2 of source triangle B
	 * @param B3 point 3 of source triangle B
	 * @return a new affine mapping
	 */
	public static AffineMapping fromTriangles(Point2D A1, Point2D A2, Point2D A3, Point2D B1, Point2D B2, Point2D B3) {
		double ax1 = A1.getX(), ax2 = A2.getX(), ax3 = A3.getX();
		double ay1 = A1.getY(), ay2 = A2.getY(), ay3 = A3.getY();
		double bx1 = B1.getX(), bx2 = B2.getX(), bx3 = B3.getX();
		double by1 = B1.getY(), by2 = B2.getY(), by3 = B3.getY();

		double S = ax1 * (ay3 - ay2) + ax2 * (ay1 - ay3) + ax3 * (ay2 - ay1); // TODO: check S for zero value and throw exception!
		double a00 = (ay1 * (bx2 - bx3) + ay2 * (bx3 - bx1) + ay3 * (bx1 - bx2)) / S;
		double a01 = (ax1 * (bx3 - bx2) + ax2 * (bx1 - bx3) + ax3 * (bx2 - bx1)) / S;
		double a10 = (ay1 * (by2 - by3) + ay2 * (by3 - by1) + ay3 * (by1 - by2)) / S;
		double a11 = (ax1 * (by3 - by2) + ax2 * (by1 - by3) + ax3 * (by2 - by1)) / S;
		double a02 = (ax1*(ay3*bx2-ay2*bx3) + ax2*(ay1*bx3-ay3*bx1) + ax3*(ay2*bx1-ay1*bx2)) / S;
		double a12 = (ax1*(ay3*by2-ay2*by3) + ax2*(ay1*by3-ay3*by1) + ax3*(ay2*by1-ay1*by2)) / S;
		return new AffineMapping(a00, a01, a02, a10, a11, a12);
	}
	
	/**
	 * Creates an affine mapping from an arbitrary 2D triangle A to another triangle B.
	 * @param A the first triangle
	 * @param B the second triangle
	 * @return a new affine mapping
	 */
	public static AffineMapping fromTriangles(Point2D[] A, Point2D[] B) {	// TODO: check length of A, B
		return AffineMapping.fromTriangles(A[0], A[1], A[2], B[0], B[1], B[2]);
	}
	// ---------------------------------------------------------------------------
	
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
	
	// ---------------------------------------------------------------------------
	
	/** Deviation limit for checking 0 and 1 values. */
	protected static double EPSILON = 1e-15;
	
	/**
	 * Checks if the given linear mapping could be affine, i.e. if the
	 * bottom row of its transformation matrix is (0, 0, 1). 
	 * Note that this is a necessary but not sufficient requirement.
	 * The threshold {@link EPSILON} is used in this check.
	 * @param lm a linear mapping
	 * @return true if the mapping could be affine
	 */
	public static boolean isAffine(LinearMapping lm) {
		if (Math.abs(lm.a20) > EPSILON) return false;
		if (Math.abs(lm.a21) > EPSILON) return false;
		if (Math.abs(lm.a22 - 1.0) > EPSILON) return false;
		return true;
	}

	/**
	 * Concatenates this mapping A with another affine mapping B and returns
	 * a new mapping C, such that C(x) = B(A(x)).
	 * @param B the second mapping
	 * @return the concatenated affine mapping
	 */
	public AffineMapping concat(AffineMapping B) {	// TODO: check if super method could be used instead
		double c00 = B.a00 * a00 + B.a01 * a10;
		double c01 = B.a00 * a01 + B.a01 * a11;
		double c02 = B.a00 * a02 + B.a01 * a12 + B.a02;

		double c10 = B.a10 * a00 + B.a11 * a10;
		double c11 = B.a10 * a01 + B.a11 * a11;
		double c12 = B.a10 * a02 + B.a11 * a12 + B.a12;
		
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

//	// warp parameter support (used in Lucas-Kanade-matcher) --------------------------
//
//	@Override
//	public int getWarpParameterCount() {
//		// a00 = p[0] + 1; a01 = p[1]; a10 = p[2]; a11 = p[3] + 1; a02 = p[4]; a12 = p[5];
//		return 6;
//	}
//
//	@Override
//	public double[] getWarpParameters() {
//		return new double[] { a00 - 1, a01, a10, a11 - 1, a02, a12 };
//	}
//	
//	public static AffineMapping fromWarpParameters(double[] p) {
//		return new AffineMapping(p[0] + 1, p[1], p[2], p[3] + 1, p[4], p[5]);
//	}
//
//	@Override
//	public double[][] getWarpJacobian(double[] xy) {
//		final double x = xy[0];
//		final double y = xy[1];
//		return new double[][]
//			{{x, y, 0, 0, 1, 0},
//			 {0, 0, x, y, 0, 1}};
//	}
	
	// ----------------------------------------------------------------------
	
	/**
	 * For testing only.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		PrintPrecision.set(6);
		double[][] a = 
				{{-2, 4, -3}, 
				{3, 7, 2}, 
				{0.0, 0.0, 1.000000}};
		System.out.println("a = \n" + Matrix.toString(a));
		System.out.println();
		double[][] ai = Matrix.inverse(a);
		System.out.println("ai = \n" + Matrix.toString(ai));
		
		LinearMapping Ai = new LinearMapping(ai);
		System.out.println("Ai is affine: " + isAffine(Ai));
		
		double[][] I = Matrix.multiply(a, ai);
		System.out.println("\ntest: should be the  identity matrix: = \n" + Matrix.toString(I));
	
	}

}




