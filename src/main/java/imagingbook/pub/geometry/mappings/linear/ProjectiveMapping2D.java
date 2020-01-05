/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.lib.math.Arithmetic;
import imagingbook.lib.settings.PrintPrecision;
import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.geometry.mappings.JacobianSupport2D;


/**
 * This class represents a projective transformation in 2D (also known
 * as a "homography"). It can be specified uniquely by four pairs of corresponding
 * points.
 * It can be assumed that every instance of this class is indeed a projective mapping.
 */
public class ProjectiveMapping2D extends LinearMapping2D implements JacobianSupport2D {
	
	//  static methods -----------------------------------------------------
	
	/**
	 * Creates the most specific linear mapping from two sequences of corresponding
	 * 2D points.
	 * @param P first point sequence
	 * @param Q second point sequence
	 * @return a linear mapping derived from point correspondences
	 */
	public static ProjectiveMapping2D makeMapping(Point[] P, Point[] Q) {
		int n = Math.min(P.length, Q.length);
		if (n < 1) {
			throw new IllegalArgumentException("cannot create a mapping from zero points");
		}
		else if (n == 1) {
			return new Translation2D(P[0], Q[0]);
		}
		else if (n == 2) {	// TODO: similarity transformation?
			throw new UnsupportedOperationException("makeMapping: don't know yet how to handle 2 points");
		}
		else if (n == 3) {
			return AffineMapping2D.from3Points(P, Q);
		}
		else if (n == 4) {
			return ProjectiveMapping2D.from4Points(P, Q);
		}
		else {	// n > 4 (over-determined)
			return ProjectiveMapping2D.fromNPoints(P, Q);
		}
	}
	
	/**
	 * Creates the projective mapping from the unit square S to
	 * the arbitrary quadrilateral P, specified by four points.
	 * @param p0 point 0
	 * @param p1 point 1
	 * @param p2 point 2
	 * @param p3 point 3
	 * @return a new projective mapping
	 */
	public static ProjectiveMapping2D fromUnitSquareTo4Points(Point p0, Point p1, Point p2, Point p3) {
		double x0 = p0.getX(), x1 = p1.getX(), x2 = p2.getX(), x3 = p3.getX();
		double y0 = p0.getY(), y1 = p1.getY(), y2 = p2.getY(), y3 = p3.getY();
		double S = (x1 - x2) * (y3 - y2) - (x3 - x2) * (y1 - y2);
		if (Arithmetic.isZero(S)) {
			throw new ArithmeticException("fromUnitSquareTo4Points(): division by zero!");
		}
		double a20 = ((x0 - x1 + x2 - x3) * (y3 - y2) - (y0 - y1 + y2 - y3) * (x3 - x2)) / S;
		double a21 = ((y0 - y1 + y2 - y3) * (x1 - x2) - (x0 - x1 + x2 - x3) * (y1 - y2)) / S;
		double a00 = x1 - x0 + a20 * x1;
		double a01 = x3 - x0 + a21 * x3;
		double a02 = x0;
		double a10 = y1 - y0 + a20 * y1;
		double a11 = y3 - y0 + a21 * y3;
		double a12 = y0;
		return new ProjectiveMapping2D(a00, a01, a02, a10, a11, a12, a20, a21);
	}
	
	/**
	 * Creates a new projective mapping between arbitrary two quadrilaterals P, Q.
	 * @param p0 point 0 of source quad P
	 * @param p1 point 1 of source quad P
	 * @param p2 point 2 of source quad P
	 * @param p3 point 3 of source quad P
	 * @param q0 point 0 of target quad Q
	 * @param q1 point 1 of target quad Q
	 * @param q2 point 2 of target quad Q
	 * @param q3 point 3 of target quad Q
	 * @return a new projective mapping
	 */
	public static ProjectiveMapping2D from4Points(
			Point p0, Point p1, Point p2, Point p3, 
			Point q0, Point q1, Point q2, Point q3)	{
		ProjectiveMapping2D T1 = ProjectiveMapping2D.fromUnitSquareTo4Points(p0, p1, p2, p3);
		ProjectiveMapping2D T2 = ProjectiveMapping2D.fromUnitSquareTo4Points(q0, q1, q2, q3);
		ProjectiveMapping2D T1i = T1.getInverse();
		return T1i.concat(T2);		
	}
	
	/**
	 * Creates a new projective mapping between arbitrary two quadrilaterals P, Q.
	 * @param P source quad
	 * @param Q target quad
	 * @return a new projective mapping
	 */
	public static final ProjectiveMapping2D from4Points(Point[] P, Point[] Q) {
		return ProjectiveMapping2D.from4Points(P[0], P[1], P[2], P[3], Q[0], Q[1], Q[2], Q[3]);
	}
	
	/**
	 * Maps between n &gt; 4 point pairs, finds a least-squares solution
	 * for the homography parameters.
	 * TODO: this is UNFINISHED code! check against DLT estimation of homography
	 * @param P sequence of points (source)
	 * @param Q sequence of points (target)
	 * @return a new projective mapping
	 */
	public static ProjectiveMapping2D fromNPoints(Point[] P, Point[] Q) {
		final int n = P.length;
		if (n < 4) {
			throw new IllegalArgumentException(ProjectiveMapping2D.class.getName() + ": fromNPoints() needs at least 4 points pairs");
		}
		double[] ba = new double[2 * n];
		double[][] Ma = new double[2 * n][];
		for (int i = 0; i < n; i++) {
			double x = P[i].getX();
			double y = P[i].getY();
			double u = Q[i].getX();
			double v = Q[i].getY();
			ba[2 * i + 0] = u;
			ba[2 * i + 1] = v;
			Ma[2 * i + 0] = new double[] { x, y, 1, 0, 0, 0, -u * x, -u * y };
			Ma[2 * i + 1] = new double[] { 0, 0, 0, x, y, 1, -v * x, -v * y };
		}
		
		RealMatrix M = MatrixUtils.createRealMatrix(Ma);
		RealVector b = MatrixUtils.createRealVector(ba);
		DecompositionSolver solver = new SingularValueDecomposition(M).getSolver();
		RealVector h = solver.solve(b);
		double a00 = h.getEntry(0);
		double a01 = h.getEntry(1);
		double a02 = h.getEntry(2);
		double a10 = h.getEntry(3);
		double a11 = h.getEntry(4);
		double a12 = h.getEntry(5);
		double a20 = h.getEntry(6);
		double a21 = h.getEntry(7);
		return new ProjectiveMapping2D(a00, a01, a02, a10, a11, a12, a20, a21);
	}
	
	//  constructors -----------------------------------------------------
	
	/**
	 * Creates the identity mapping.
	 */
	public ProjectiveMapping2D() {
		super();
	}
	
	/**
	 * Creates a projective mapping from the specified matrix elements.
	 * @param a00 matrix element A_00
	 * @param a01 matrix element A_01
	 * @param a02 matrix element A_02
	 * @param a10 matrix element A_10
	 * @param a11 matrix element A_11
	 * @param a12 matrix element A_12
	 * @param a20 matrix element A_20
	 * @param a21 matrix element A_21
	 */
	public ProjectiveMapping2D(
			double a00, double a01, double a02, 
			double a10, double a11, double a12, 
			double a20, double a21) {
		super(a00, a01, a02, a10, a11, a12, a20, a21, 1);
	}
	
	/**
	 * Creates a projective mapping from any linear mapping.
	 * The transformation matrix gets normalized to a22 = 1.
	 * @param m a linear mapping
	 */
	public ProjectiveMapping2D(LinearMapping2D m) {
		this(m.normalize());
	}
	
	/**
	 * Creates a projective mapping from an existing instance.
	 * @param m a projective mapping
	 */
	public ProjectiveMapping2D(ProjectiveMapping2D m) {
		//this(m.getTransformationMatrix());
		this(m.a00, m.a01, m.a02, m.a10, m.a11, m.a12, m.a20, m.a21);
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Concatenates this mapping A with another linear mapping B and returns
	 * a new mapping C, such that C(x) = B(A(x)).
	 * @param B the second mapping
	 * @return the concatenated mapping
	 */
	public ProjectiveMapping2D concat(ProjectiveMapping2D B) {
		LinearMapping2D C = LinearMapping2D.concatenate(B, this);
		return new ProjectiveMapping2D(C);
	}
	
	/**
	 * {@inheritDoc}
	 * @return a new projective mapping
	 */
	public ProjectiveMapping2D duplicate() {
		return new ProjectiveMapping2D(this);
	}
	
	/**
	 * {@inheritDoc}
	 * Note that the inverse A' of a projective transformation matrix A is again a linear transformation
	 * but its a'2' element is generally not 1. Scaling A' to A'' = A' / a22' yields a projective transformation
	 * that reverses A. While A * A' = I, the result of A * A'' is a scaled identity matrix.
	 * @return the inverse projective transformation
	 */
	public ProjectiveMapping2D getInverse() {
		return new ProjectiveMapping2D(super.getInverse());
	}
	
	// Jacobian support -------------------------------------

	@Override
	public double[] getParameters() {
		return new double[] { a00 - 1, a01, a10, a11 - 1, a20, a21, a02, a12 };
	}
	
	@Override
	public ProjectiveMapping2D fromParameters(double[] p) {
		return new ProjectiveMapping2D(
				p[0] + 1,   p[1],        p[6],
				p[2],       p[3] + 1,    p[7],
				p[4],       p[5]             );
	}
	
	@Override
	public double[][] getJacobian(Point xy) {
		// see Baker 2003 "20 Years" Part 1, Eq. 99 (p. 46)
		final double x = xy.getX();
		final double y = xy.getY();
		final double a = a00 * x + a01 * y + a02;	// = alpha
		final double b = a10 * x + a11 * y + a12;	// = beta
		final double c = a20 * x + a21 * y + 1;		// = gamma
		if (Arithmetic.isZero(c)) {
			throw new ArithmeticException("getJacobian(): division by zero!");
		}
		final double cc = c * c;
		return new double[][]
			{{x/c, y/c, 0,   0,   -(x*a)/cc, -(y*a)/cc, 1/c, 0  },
			 {0,   0,   x/c, y/c, -(x*b)/cc, -(y*b)/cc, 0,   1/c}};
	}
	
	// -----------------------------------------------------------------
	
	/**
	 * For testing only.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		PrintPrecision.set(6);

		// book example:
		Point[] A = {
				Point.create(2, 5),
				Point.create(4, 6),
				Point.create(7, 9),
				Point.create(5, 9),
				Point.create(5.2, 9.1)	// 5 points, overdetermined!
				};
		
		Point[] B = {
				Point.create(4, 3),
				Point.create(5, 2),
				Point.create(9, 3),
				Point.create(7, 5),
				Point.create(7, 4.9)	// 5 points, overdetermined!
				};
		
		ProjectiveMapping2D pm = ProjectiveMapping2D.fromNPoints(A, B);
		
		System.out.println("\nprojective mapping = \n" + pm.toString());
		
		for (int i = 0; i < A.length; i++) {
			Point Bi = pm.applyTo(A[i]);
			System.out.println(A[i].toString() + " -> " + Bi.toString());
		}
		
		System.out.println("pm is of class " + pm.getClass().getName());
		ProjectiveMapping2D pmi = pm.getInverse();
		pmi = pmi.normalize();
		System.out.println("\ninverse projective mapping (normalized) = \n" + pmi.toString());
		
		for (int i = 0; i < B.length; i++) {
			Point Ai = pmi.applyTo(B[i]);
			System.out.println(B[i].toString() + " -> " + Ai.toString());
		}
		
		ProjectiveMapping2D testId = pm.concat(pmi);
		System.out.println("\ntest: should be a scaled identity matrix: = \n" + testId.toString());
	}
}
