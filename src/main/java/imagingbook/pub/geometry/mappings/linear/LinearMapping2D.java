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
import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.geometry.mappings.Mapping2D;

/**
 * This class represents an arbitrary linear transformation in 2D.
 * Instances of this class and any subclass are immutable.
 */
public class LinearMapping2D implements Mapping2D {
	
	protected final double 
		a00, a01, a02,
		a10, a11, a12,
		a20, a21, a22;

	//  constructors -----------------------------------------------------
	
	/**
	 * Creates a new identity mapping.
	 */
	public LinearMapping2D() {
		a00 = 1; a01 = 0; a02 = 0;
		a10 = 0; a11 = 1; a12 = 0;
		a20 = 0; a21 = 0; a22 = 1;
	}
	
	/**
	 * Creates a linear mapping from a transformation matrix A.
	 * @param A a 3 x 3 matrix
	 */
	public LinearMapping2D(double[][] A) {
		a00 = A[0][0]; a01 = A[0][1]; a02 = A[0][2];
		a10 = A[1][0]; a11 = A[1][1]; a12 = A[1][2];
		a20 = A[2][0]; a21 = A[2][1]; a22 = A[2][2];
	}

	/**
	 * Creates an arbitrary linear mapping from the specified matrix elements.
	 * @param a00 matrix element A_00
	 * @param a01 matrix element A_01
	 * @param a02 matrix element A_02
	 * @param a10 matrix element A_10
	 * @param a11 matrix element A_11
	 * @param a12 matrix element A_12
	 * @param a20 matrix element A_20
	 * @param a21 matrix element A_21
	 * @param a22 matrix element A_22
	 */
	public LinearMapping2D (
			double a00, double a01, double a02, 
			double a10, double a11, double a12,
			double a20, double a21, double a22) {
		this.a00 = a00;  this.a01 = a01;  this.a02 = a02;
		this.a10 = a10;  this.a11 = a11;  this.a12 = a12;
		this.a20 = a20;  this.a21 = a21;  this.a22 = a22;
	}
	
	/**
	 * Creates a new linear mapping from an existing linear mapping.
	 * @param lm a given linear mapping
	 */
	public LinearMapping2D (LinearMapping2D lm) {
		this(lm.getTransformationMatrix());
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Scales the transformation matrix such that a22 becomes 1.
	 * Any linear mapping can be normalized and thereby be converted to
	 * a projective mapping (see {@link ProjectiveMapping2D}.
	 * @return the normalized linear mapping (i.e., a projective mapping)
	 */
	public ProjectiveMapping2D normalize() {
		if (Arithmetic.isZero(Math.abs(a22))) {
			throw new ArithmeticException("Zero value in a22, cannot normalize linear mapping!");
		}
		double b00 = a00/a22;	double b01 = a01/a22;	double b02 = a02/a22;
		double b10 = a10/a22;	double b11 = a11/a22;	double b12 = a12/a22;
		double b20 = a20/a22;	double b21 = a21/a22;
		return new ProjectiveMapping2D(b00, b01, b02, b10, b11, b12, b20, b21);
	}
	
	// ----------------------------------------------------------
	
	@Override
	public Point applyTo(Point pnt) {
		final double x = pnt.getX();
		final double y = pnt.getY();
		double h =  (a20 * x + a21 * y + a22);
		double x1 = (a00 * x + a01 * y + a02) / h;
		double y1 = (a10 * x + a11 * y + a12) / h;
		return Point.create(x1, y1);
	}
	
//	@Override
//	public double[] applyTo (double x, double y) {
//		double h =  (a20 * x + a21 * y + a22);
//		double x1 = (a00 * x + a01 * y + a02) / h;
//		double y1 = (a10 * x + a11 * y + a12) / h;
//		return new double[] {x1, y1};
//	}
	
	/**
	 * Calculates and returns the inverse mapping.
	 */
	public LinearMapping2D getInverse() {
		// System.out.println("LinearMapping getInverse()");
		double[][] ai = Matrix.inverse(this.getTransformationMatrix());
		return new LinearMapping2D(ai);
	}
	
	/**
	 * Concatenates this mapping A with another linear mapping B and returns
	 * a new mapping C, such that C(x) = B(A(x)).
	 * @param B the second mapping
	 * @return the concatenated mapping
	 */
	public LinearMapping2D concat(LinearMapping2D B) {
		double[][] C = Matrix.multiply(B.getTransformationMatrix(), this.getTransformationMatrix());
		return new LinearMapping2D(C);
	}
	
	/**
	 * Concatenates a sequence of linear mappings AA = (A1, A2, ..., An), with
	 * the result A(x) = A1(A2(...(An(x))...)). Thus, the mapping
	 * An is applied first and A1 last, with the associated transformation
	 * matrix a = a1 * a2 * ... * an.
	 * If AA is empty, the identity mapping is returned.
	 * If AA contains only a single mapping, a copy of this mapping is returned.
	 * 
	 * @param AA a (possibly empty) sequence of linear transformations
	 * @return the concatenated linear transformation 
	 */
	public static LinearMapping2D concatenate(LinearMapping2D... AA) {
		if (AA.length == 0) {
			return new LinearMapping2D();	// identity
		}
		else {
			double[][] a = AA[0].getTransformationMatrix();
			for (int i = 1; i < AA.length; i++) {
				a = Matrix.multiply(a, AA[i].getTransformationMatrix());
			}
			return new LinearMapping2D(a);
		}
	}
	
	/**
	 * Retrieves the transformation matrix for this mapping.
	 * @return the 3x3 transformation matrix
	 */
	public double[][] getTransformationMatrix() {
		return new double[][]
				{{a00, a01, a02},
				 {a10, a11, a12},
				 {a20, a21, a22}};
	}

	/**
	 * Returns a copy of this mapping.
	 * @return a new mapping of the same type
	 */
	public LinearMapping2D duplicate() {
		return new LinearMapping2D(this);
	}

	public String toString() {
		return Matrix.toString(getTransformationMatrix());
	}
	
	// -----------------------------------------------------------
	
//	/**
//	 * For testing only.
//	 * @param args ignored
//	 */
//	public static void main(String[] args) {
//		PrintPrecision.set(6);
//		double[][] A = 
//				{{-1.230769, 2.076923, -1.769231}, 
//				{-2.461538, 2.615385, -3.538462}, 
//				{-0.307692, 0.230769, 1.000000}};
//		System.out.println("A = " + Matrix.toString(A));
//		
//		double[][] Ai = Matrix.inverse(A);
//		System.out.println("Ai = " + Matrix.toString(Ai));
//		
//		double[][] I = Matrix.multiply(A, Ai);
//		System.out.println("\ntest: should be the  identity matrix: = \n" + Matrix.toString(I));
//		
//		ProjectiveMapping pA = (new LinearMapping2D(A)).normalize();
//		System.out.println("mapping pA = \n" + pA.toString());
//		
//		ProjectiveMapping AA1 = pA.concat(pA);
//		System.out.println("mapping AA1 = \n" + AA1.toString());
//		
//		ProjectiveMapping AA2 = LinearMapping2D.concatenate(pA, pA).normalize();
//		System.out.println("mapping AA1 = \n" + AA2.toString());
//	}


	
}




