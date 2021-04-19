/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.math;

//import imagingbook.lib.math.Matrix;
//import imagingbook.lib.settings.PrintPrecision;

/**
 * Implements an efficient, closed form algorithm for calculating the real 
 * eigenvalues (&lambda;) and eigenvectors (x) of a 2x2 matrix M of the form
 * <pre>
 *   | A B |
 *   | C D | </pre>
 * There are typically (but not in general) two pairs of real-valued 
 * solutions 
 * &lang;&lambda;<sub>1</sub>, x<sub>1</sub>&rang;,
 * &lang;&lambda;<sub>2</sub>, x<sub>2</sub>&rang;
 * such that M&middot;x<sub>k</sub> = &lambda;<sub>k</sub>&middot;x<sub>k</sub>.
 * <p>
 * This implementation is inspired by Blinn, Jim: "Jim Blinn's Corner: 
 * Notation, Notation, Notation", Morgan Kaufmann (2002) -
 * Ch. 5 ("Consider the Lowly 2x2 Matrix").
 * Note that Blinn uses the (common computer graphics) notation 
 * x&middot;M = &lambda;&middot;x,
 * while this implementation adopts the notation 
 * M&middot;x = &lambda;&middot;x, 
 * i.e., the matrix M is transposed (elements B/C are exchanged).
 * </p>
 * 
 * @author W. Burger
 * @version 2020-02-09
 */
public class Eigensolver2x2 implements Eigensolver {
	
	private final boolean isReal;
	private final double[] eVals = {Double.NaN, Double.NaN};
	private final double[][] eVecs = new double[2][];
	
	/**
	 * Constructor, takes a 2x2 matrix.
	 * @param M a 2x2 matrix
	 */
	public Eigensolver2x2(double[][] M) {
		this(M[0][0], M[0][1], M[1][0], M[1][1]);
	}
	
	/**
	 * Constructor, takes the individual elements of a 2x2 matrix:
	 * <pre>
	 *   A B
	 *   C D</pre>
	 * @param A matrix element M[0,0]
	 * @param B matrix element M[0,1]
	 * @param C matrix element M[1,0]
	 * @param D matrix element M[1,1]
	 */
	public Eigensolver2x2(double A, double B, double C, double D) {
		this.isReal = this.solve(A, B, C, D);
	}
	
	private boolean solve(double A, double B, double C, double D) {
		final double R = (A + D) / 2;
		final double S = (A - D) / 2;
		final double rho = S * S + B * C;
		
		if (rho < 0) {	// no real-valued eigenvalues
			return false;
		}
		
		final double T = Math.sqrt(rho);
		this.eVals[0] = R + T;
		this.eVals[1] = R - T;
		if (A - D > 0) {
			this.eVecs[0] = new double[] { S + T, C };
			this.eVecs[1] = new double[] { B, -S - T };
		}
		else if (A - D < 0) {
			this.eVecs[0] = new double[] { B, -S + T};
			this.eVecs[1] = new double[] { S - T, C};
		}
		else {		// (A - D) == 0
			
			final double aB = Math.abs(B);
			final double aC = Math.abs(C);
			final double sBC = Math.sqrt(B * C);
			if (aB < aC) {							// |B| < |C|
				this.eVecs[0] = new double[] { sBC, C};
				this.eVecs[1] = new double[] {-sBC, C};
			}
			else if (aB > aC) { 					// |B| > |C|
				this.eVecs[0] = new double[] { B,  sBC};
				this.eVecs[1] = new double[] { B, -sBC};
			}
			else {									// |B| == |C|
				this.eVecs[0] = new double[] { C, C};
				this.eVecs[1] = new double[] {-C, C};
			}
		}
		return true;
	}
	
	/**
	 * Returns {@code true} iff all eigenvalues of the associated matrix are real.
	 * @return as described
	 */
	@Override
	public boolean isReal() {
		return isReal;
	}
	
	/**
	 * Returns a vector holding the two real eigenvalues. Both are
	 * {@code NaN} if the associated matrix has no real eigenvalues.
	 * Otherwise the first eigenvalue is the greater of the two.
	 * Note that the method returns a reference to an internal
	 * array and thus results should be used read-only!
	 * @return a 2-element array of eigenvalues
	 */
	@Override
	public double[] getEigenvalues() {
		return this.eVals;
	}
	
	/**
	 * Returns the kth eigenvalue (lambda_k, k = 0, 1).
	 * {@code NaN} is returned if the no real eigenvalue exists.
	 * 
	 * @param k index 0 or 1
	 * @return the kth eigenvalue
	 */
	@Override
	public double getEigenvalue(int k) {
		return eVals[k];
	}
	
	/**
	 * Obsolete, use {@link #getEigenvalue(int)} instead.
	 * @return the first eigenvalue (lambda_1)
	 * @deprecated
	 */
	public double getEigenvalue1() {
		return this.eVals[0];
	}
	
	/**
	 * Obsolete, use {@link #getEigenvalue(int)} instead.
	 * @return the second eigenvalue (lambda_2)
	 * @deprecated
	 */
	public double getEigenvalue2() {
		return this.eVals[1];
	}
	
	/**
	 * Returns a 2x2 array holding the two real eigenvectors.
	 * When used in the form
	 * <pre>double[][] X = getEigenvalues();</pre>
	 * then {@code X[0]} and {@code X[1]} contain the 1st and 2nd eigenvector,
	 * respectively. If the associated matrix has no real eigenvalues,
	 * then {@code X[0]} and {@code X[1]} are both {@code null}.
	 * Otherwise {@code X[k][0]} and {@code X[k][1]} are the x/y components
	 * of the k-th eigenvector.
	 * The ordering of the returned eigenvectors is the same as for the
	 * eigenvalues returned by {@link #getEigenvalues()}.
	 * Note that the method returns a reference to an internal
	 * array and thus results should be used read-only!
	 * Obsolete, use {@link #getEigenvector(int)} instead.
	 * @return a 2-element array of 2D eigenvectors
	 * @deprecated
	 */
	public double[][] getEigenvectors() {
		return this.eVecs;
	}
	
	/**
	 * Returns the kth eigenvector, with k = 0 or 1.
	 * The ordering of the returned eigenvectors is the same as for the
	 * eigenvalues returned by {@link #getEigenvalues()}.
	 * Note that the method returns a reference to an internal
	 * array and thus results should be used read-only!
	 * @param k index 0 or 1
	 * @return the kth eigenvector
	 */
	@Override
	public double[] getEigenvector(int k) {
		return this.eVecs[k];
	}
	
	@Deprecated
	public double[] getEigenvector1() {
		return this.eVecs[0];
	}
	
	@Deprecated
	public double[] getEigenvector2() {
		return this.eVecs[1];
	}
		
	// for Testing only --------------------------------------------------------------
	
//	@SuppressWarnings("unused")
//	private static void checkEigen(double[][] M) {
//		System.out.format("Checking M  = \n%s\n", Matrix.toString(M));
//		Eigensolver2x2 es = new Eigensolver2x2(M);
//		double[] eVals = es.getEigenvalues();
//		double[][] eVecs = es.getEigenvectors();
//		
//		for (int i = 0; i < 2; i++) {
//			checkEigenPair(i+1, M, eVals[i], eVecs[i]);
//			System.out.println();
//		}
//		System.out.println();
//	}
//	
//	private static void checkEigenPair(int i, double[][] M, double lambda, double[] x) {
//		double[] Mx = Matrix.multiply(M, x);
//		double[] lx = Matrix.multiply(lambda, x);
//		double d = Matrix.normL2(Matrix.add(Mx, Matrix.multiply(-1, lx)));
//		System.out.format("    \u03BB_%d  = %.6f\n", i, lambda);
//		System.out.format("    x_%d = %s\n", i, Matrix.toString(x));
//		System.out.format("    M.x = %s\n", Matrix.toString(Mx));
//		System.out.format("    \u03BB.x = %s\n", Matrix.toString(lx));
//		System.out.format("    error = %.6f\n", d);
//		if (d > 0.000001) {
//			System.out.println("    **** WRONG ****");
//		}
//	}
//	
//	
//	public static void main(String[] args) {
//		PrintPrecision.set(6);
//		{
//			double[][] M = {
//					{3, -2},
//					{-4, 1}};
//			checkEigen(M);
//			// Mathematica: {5, -1} | {{-1, 1}, {1, 2}}
//		}
//		
//		{
//			double[][] M = {
//					{-0.009562, 0.011933}, 
//					{0.011933, -0.021158}};
//			checkEigen(M);
//			// Mathematica: {-0.028627, -0.002093} | {{-0.530554, 0.847651}, {-0.847651, -0.530554}}
//		}
//
//		{
//			double[][] M = {
//					{-0.004710, -0.006970},
//					{-0.006970, -0.029195}};
//			checkEigen(M);
//			// Mathematica: {-0.0310401, -0.00286493} | {{0.255902, 0.966703}, {-0.966703, 0.255902}}
//		}
//		
//		{
//			double[][] M = {
//					{0, 0},
//					{0, 1}};
//			checkEigen(M);
//			// Mathematica: {1, 0} | {{0, 1}, {1, 0}}
//		}
//		
//		{
//			double[][] M = {
//					{1, 0},
//					{0, 0}};
//			checkEigen(M);
//			// Mathematica: {1, 0} | {{1, 0}, {0, 1}}
//		}
//		
//		{	// Case 3.1
//			double[][] M = {
//					{1, 0},
//					{-2, 1}};
//			checkEigen(M);
//			// Mathematica: {1, 1} | {{0, 1}, {0, 0}} !!!!
//		}
//		
//		{	// Case 3.2
//			double[][] M = {
//					{1, -2},
//					{0, 1}};
//			checkEigen(M);
//			// Mathematica: {1, 1} | {{1, 0}, {0, 0}} !!!!
//		}
//		{	// Case 3.3
//			double[][] M = {
//					{1, 2},
//					{2, 1}};
//			checkEigen(M);
//			// Mathematica: {3, -1} | {{1, 1}, {-1, 1}}
//		}
//	}
	
}
	
/*
 
Checking M  = 
{{3.000000, -2.000000}, 
{-4.000000, 1.000000}}
    λ_1  = 5.000000
    x_1 = {4.000000, -4.000000}
    λ_2  = -1.000000
    x_2 = {-2.000000, -4.000000}

Checking M  = 
{{-0.009562, 0.011933}, 
{0.011933, -0.021158}}
    λ_1  = -0.002093
    x_1 = {0.019065, 0.011933}
    λ_2  = -0.028627
    x_2 = {0.011933, -0.019065}

Checking M  = 
{{-0.004710, -0.006970}, 
{-0.006970, -0.029195}}
    λ_1  = -0.002865
    x_1 = {0.026330, -0.006970}
    λ_2  = -0.031040
    x_2 = {-0.006970, -0.026330}

Checking M  = 
{{0.000000, 0.000000}, 
{0.000000, 1.000000}}
    λ_1  = 1.000000
    x_1 = {0.000000, 1.000000}
    λ_2  = 0.000000
    x_2 = {-1.000000, 0.000000}

Checking M  = 
{{1.000000, 0.000000}, 
{0.000000, 0.000000}}
    λ_1  = 1.000000
    x_1 = {1.000000, 0.000000}
    λ_2  = 0.000000
    x_2 = {0.000000, -1.000000}

Checking M  = 
{{1.000000, 0.000000}, 
{-2.000000, 1.000000}}
    λ_1  = 1.000000
    x_1 = {-0.000000, -2.000000}
    λ_2  = 1.000000
    x_2 = {0.000000, -2.000000}

Checking M  = 
{{1.000000, -2.000000}, 
{0.000000, 1.000000}}
    λ_1  = 1.000000
    x_1 = {-2.000000, -0.000000}
    λ_2  = 1.000000
    x_2 = {-2.000000, 0.000000}

Checking M  = 
{{1.000000, 2.000000}, 
{2.000000, 1.000000}}
    λ_1  = 3.000000
    x_1 = {2.000000, 2.000000}
    λ_2  = -1.000000
    x_2 = {-2.000000, 2.000000}

*/
