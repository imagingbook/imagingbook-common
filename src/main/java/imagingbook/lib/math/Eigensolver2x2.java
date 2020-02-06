/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.math;


/**
 * The this class calculates the real eigenvalues/eigenvectors for a 2x2
 * matrix
 * <pre>
 *   | a b |
 *   | c d | </pre>
 * The implementation is inspired by Blinn, Jim: Consider the lowly 2x2 matrix. 
 * IEEE Computer Graphics and Applications, 16(2):82-88, 1996.
 * 
 * @author W. Burger
 * @version 2020-02-06
 */
public class Eigensolver2x2 {
	
	private final double a, b, c, d, r, s, t;
	
	public Eigensolver2x2(double[][] A) {
		this(A[0][0], A[0][1], A[1][0], A[1][1]);
	}
	
	public Eigensolver2x2(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.r = (a + d) / 2;
		this.s = (a - d) / 2;
		this.t = Math.sqrt(s * s + b * c);	// = NaN if (s * s + b * c) < 0
	}
	
	public double getEigenvalue1() {
		return Double.isNaN(t) ? Double.NaN : r + t;
	}
	
	public double getEigenvalue2() {
		return Double.isNaN(t) ? Double.NaN : r - t;
	}
	
	public double[] getEigenvector1() {
		if (Double.isNaN(t)) 
			return null;
		return ((a - d) >= 0) ? new double[] {s + t, c} :  new double[] {b, -s + t};
	}
	
	public double[] getEigenvector2() {
		if (Double.isNaN(t)) 
			return null;
		return ((a - d) >= 0) ? new double[] {b, -s - t} :  new double[] {s - t, c};
	}

	public EigenPair[] getEigenPairs() {
		if (Double.isNaN(t)) {
			return null;
		}
		else {
			EigenPair[] eps = new EigenPair[2];
			eps[0] = new EigenPair(getEigenvalue1(), getEigenvector1());
			eps[1] = new EigenPair(getEigenvalue2(), getEigenvector2());
			return eps;
		}
	}

	/**
	 * EigenPair is a tuple <eigenvalue, eigenvector> and represents
	 * the solution to an eigen problem.
	 */
	public class EigenPair {
		final double eival;
		final double[] eivec;

		public EigenPair(double eival, double[] eivec) {
			this.eival = eival;
			this.eivec = eivec;
		}

		public double getEigenvalue() {
			return this.eival;
		}

		public double[] getEigenvector() {
			return this.eivec;
		}
		
		public String toString() {
			if (eivec == null)
				return "no valid eigenvalue / eigenvector";
			else {
				return String.format("eigenvalue: %.5f | eigenvector: %s", eival, Matrix.toString(eivec)) ;
			}
		}
	}
	
	
	// for Testing:
	public static void main(String[] args) {
		{
			double[][] A = {
					{3, -2},
					{-4, 1}
			};

			EigenPair[] eigenpairs = new Eigensolver2x2(A).getEigenPairs();
			System.out.println(eigenpairs[0].toString());
			System.out.println(eigenpairs[1].toString());
			System.out.println();
			/*
			 * eigenvalue: 5,00000 | eigenvector: {4.000, -4.000}
			 * eigenvalue: -1,00000 | eigenvector: {-2.000, -4.000}
			 */
		}
		
		{
			double[][] A = {
					{-0.009562, 0.011933}, 
					{0.011933, -0.021158}
			};

			EigenPair[] eigenpairs = new Eigensolver2x2(A).getEigenPairs();
			System.out.println(eigenpairs[0].toString());
			System.out.println(eigenpairs[1].toString());
			System.out.println();
			/*
			 * eigenvalue: -0.00209 | eigenvector: {0.019, 0.012}
			 * eigenvalue: -0.02863 | eigenvector: {0.012, -0.019}
			 */
		}

		{
			double[][] A = {
					{-0.004710, -0.006970},
					{-0.006970, -0.029195}};

			EigenPair[] eigenpairs = new Eigensolver2x2(A).getEigenPairs();
			System.out.println(eigenpairs[0].toString());
			System.out.println(eigenpairs[1].toString());
			System.out.println();
			/*
			 * eigenvalue: -0.00286 | eigenvector: {0.026, -0.007}
			 * eigenvalue: -0.03104 | eigenvector: {-0.007, -0.026}
			 */
		}
	}
	

}
