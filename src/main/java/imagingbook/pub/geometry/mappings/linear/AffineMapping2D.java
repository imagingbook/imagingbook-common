/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;

import imagingbook.lib.math.Arithmetic;
import imagingbook.lib.math.Matrix;
import imagingbook.lib.settings.PrintPrecision;
import imagingbook.pub.geometry.basic.Point;

/**
 * This class represents an affine transformation in 2D, which can be defined 
 * by three pairs of corresponding points.
 * It can be assumed that every instance of this class is indeed an affine mapping.
 */
public class AffineMapping2D extends ProjectiveMapping2D {

	/**
	 * Creates an affine mapping from an arbitrary 2D triangle A to another triangle B.
	 * In this case the solution is found in closed form 
	 * (see Burger/Burge 2016, Sec. 21.1.3, eq. 21.26).
	 * @param A1 point 1 of source triangle A
	 * @param A2 point 2 of source triangle A
	 * @param A3 point 3 of source triangle A
	 * @param B1 point 1 of source triangle B
	 * @param B2 point 2 of source triangle B
	 * @param B3 point 3 of source triangle B
	 * @return a new affine mapping
	 */
	public static AffineMapping2D from3Points(Point A1, Point A2, Point A3, Point B1, Point B2, Point B3) {
		double ax1 = A1.getX(), ax2 = A2.getX(), ax3 = A3.getX();
		double ay1 = A1.getY(), ay2 = A2.getY(), ay3 = A3.getY();
		double bx1 = B1.getX(), bx2 = B2.getX(), bx3 = B3.getX();
		double by1 = B1.getY(), by2 = B2.getY(), by3 = B3.getY();

		double S = ax1 * (ay3 - ay2) + ax2 * (ay1 - ay3) + ax3 * (ay2 - ay1); //
		if (Arithmetic.isZero(S)) {
			throw new ArithmeticException("from3Points(): division by zero!");
		}
		double a00 = (ay1 * (bx2 - bx3) + ay2 * (bx3 - bx1) + ay3 * (bx1 - bx2)) / S;
		double a01 = (ax1 * (bx3 - bx2) + ax2 * (bx1 - bx3) + ax3 * (bx2 - bx1)) / S;
		double a10 = (ay1 * (by2 - by3) + ay2 * (by3 - by1) + ay3 * (by1 - by2)) / S;
		double a11 = (ax1 * (by3 - by2) + ax2 * (by1 - by3) + ax3 * (by2 - by1)) / S;
		double a02 = (ax1*(ay3*bx2-ay2*bx3) + ax2*(ay1*bx3-ay3*bx1) + ax3*(ay2*bx1-ay1*bx2)) / S;
		double a12 = (ax1*(ay3*by2-ay2*by3) + ax2*(ay1*by3-ay3*by1) + ax3*(ay2*by1-ay1*by2)) / S;
		return new AffineMapping2D(a00, a01, a02, a10, a11, a12);
	}
	
	/**
	 * Creates an affine mapping from an arbitrary 2D triangle A to another triangle B.
	 * @param A the first triangle
	 * @param B the second triangle
	 * @return a new affine mapping
	 */
	public static AffineMapping2D from3Points(Point[] A, Point[] B) {
		return AffineMapping2D.from3Points(A[0], A[1], A[2], B[0], B[1], B[2]);
	}
	// ---------------------------------------------------------------------------
	
	/**
	 * Creates the identity mapping.
	 */
	public AffineMapping2D() {
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
	public AffineMapping2D (
			double a00, double a01, double a02, 
			double a10, double a11, double a12) {
		super(a00, a01, a02, a10, a11, a12, 0, 0);
	}

	/**
	 * Creates a new affine mapping from an existing affine mapping.
	 * @param m an affine mapping
	 */
	public AffineMapping2D(AffineMapping2D m) {
		this(m.a00, m.a01, m.a02, m.a10, m.a11, m.a11);
	}
	
	// ----------------------------------------------------------
	
	/** Deviation limit for checking 0 and 1 values. */
	protected static double AffineTolerance = 1e-15;
	
	/**
	 * Checks if the given linear mapping could be affine, i.e. if the
	 * bottom row of its transformation matrix is (0, 0, 1). 
	 * Note that this is a necessary but not sufficient requirement.
	 * The threshold {@link AffineTolerance} is used in this check.
	 * @param lm a linear mapping
	 * @return true if the mapping could be affine
	 */
	public static boolean isAffine(LinearMapping2D lm) {
		if (Math.abs(lm.a20) > AffineTolerance) return false;
		if (Math.abs(lm.a21) > AffineTolerance) return false;
		if (Math.abs(lm.a22 - 1.0) > AffineTolerance) return false;
		return true;
	}

	/**
	 * Concatenates this mapping A with another affine mapping B and returns
	 * a new mapping C, such that C(x) = B(A(x)).
	 * @param B the second mapping
	 * @return the concatenated affine mapping
	 */
	public AffineMapping2D concat(AffineMapping2D B) {	// use some super method instead?
		double[][] C = Matrix.multiply(B.getTransformationMatrix(), this.getTransformationMatrix());
		return new AffineMapping2D(C[0][0], C[0][1], C[0][2], C[1][0], C[1][1], C[1][2]);
	}

	// Alternative (old) version:
//	public AffineMapping concat(AffineMapping B) {	// use some super method instead?
//		double c00 = B.a00 * a00 + B.a01 * a10;
//		double c01 = B.a00 * a01 + B.a01 * a11;
//		double c02 = B.a00 * a02 + B.a01 * a12 + B.a02;
//
//		double c10 = B.a10 * a00 + B.a11 * a10;
//		double c11 = B.a10 * a01 + B.a11 * a11;
//		double c12 = B.a10 * a02 + B.a11 * a12 + B.a12;
//		
//		return new AffineMapping(c00, c01, c02, c10, c11, c12);
//	}

	/**
	 * {@inheritDoc}
	 * Note that inverting an affine transformation always yields
	 * another affine transformation.
	 */
	public AffineMapping2D getInverse() {
		double det = a00*a11 - a01*a10;	
		double b00 = a11 / det; 
		double b01 = - a01 / det; 
		double b02 = (a01*a12 - a02*a11) / det;	
		double b10 = - a10 / det; 
		double b11 = a00 / det; 
		double b12 = (a02*a10 - a00*a12) / det;
		
		return new AffineMapping2D(b00, b01, b02, b10, b11, b12);
	}

	/**
	 * {@inheritDoc}
	 * @return a new affine mapping
	 */
	public AffineMapping2D duplicate() {
		return new AffineMapping2D(this);
	}

	// Jacobian support -------------------------------------

	@Override
	public double[] getParameters() {
		return new double[] { a00 - 1, a01, a10, a11 - 1, a02, a12 };
	}
	
	@Override
	public AffineMapping2D fromParameters(double[] p) {
		return new AffineMapping2D(p[0] + 1, p[1], p[2], p[3] + 1, p[4], p[5]);
	}

	@Override
	public double[][] getJacobian(Point xy) {
		final double x = xy.getX();
		final double y = xy.getY();
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
		double[][] a = 
				{{-2, 4, -3}, 
				{3, 7, 2}, 
				{0, 0, 1}};
		System.out.println("a = \n" + Matrix.toString(a));
		System.out.println();
		double[][] ai = Matrix.inverse(a);
		System.out.println("ai = \n" + Matrix.toString(ai));
		
		LinearMapping2D Ai = new LinearMapping2D(ai);
		System.out.println("Ai is affine: " + isAffine(Ai));
		
		double[][] I = Matrix.multiply(a, ai);
		System.out.println("\ntest: should be the  identity matrix: = \n" + Matrix.toString(I));
	
	}

}




