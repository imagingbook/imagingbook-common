/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.math;


import imagingbook.lib.math.Matrix;
import imagingbook.lib.settings.PrintPrecision;

/**
 * Instances of class calculate the real eigenvalues (&lambda;) and 
 * eigenvectors (x) for a 2x2 matrix M of the form
 * <pre>
 *   | A B |
 *   | C D | </pre>
 * There are typically (but not in general) two pairs of real-valued 
 * solutions (&lambda;, x).
 * such that M*x = &lambda;*x.
 * <p>
 * This implementation is inspired by Blinn, Jim: "Jim Blinn's Corner: 
 * Notation, Notation, Notation", Morgan Kaufmann (2002) -
 * Ch. 5 ("Consider the Lowly 2x2 Matrix").
 * Note that Blinn uses the (common computer graphics) notation 
 * x*M = &lambda;*x,
 * while this implementation adopts the notation 
 * M*x = &lambda;*x, 
 * i.e., the matrix M is transposed (elements B/C are exchanged).
 * </p>
 * 
 * @author W. Burger
 * @version 2020-02-08
 */
public class Eigensolver2x2 {
	
	private final boolean isReal;
	private final double[] eVals = {Double.NaN, Double.NaN};
	private final double[][] eVecs = new double[2][];
	
	public Eigensolver2x2(double[][] M) {
		this(M[0][0], M[0][1], M[1][0], M[1][1]);
	}
	
	public Eigensolver2x2(double A, double B, double C, double D) {
		this.isReal = makeEigenvectors(A, B, C, D);
	}
	
	private boolean makeEigenvectors(double A, double B, double C, double D) {
		final double R = (A + D) / 2;
		final double S = (A - D) / 2;
		final double T = Math.sqrt(S * S + B * C);	// = sqrt(rho) = NaN if negative argument
		eVals[0] = R + T;
		eVals[1] = R - T;
		if (Double.isNaN(T)) {
			return false;
		}
		if (A - D > 0) {
			eVecs[0] = new double[] { S + T, C };
			eVecs[1] = new double[] { B, -S - T };
		}
		else if (A - D < 0) {
			eVecs[0] = new double[] { B, -S + T};
			eVecs[1] = new double[] { S - T, C};
		}
		else {		// (A - D) == 0
			
			final double aB = Math.abs(B);
			final double aC = Math.abs(C);
			final double sBC = Math.sqrt(B * C);
			if (aB < aC) {							// |B| < |C|
				eVecs[0] = new double[] { sBC, C};
				eVecs[1] = new double[] {-sBC, C};
			}
			else if (aB > aC) { 					// |B| > |C|
				eVecs[0] = new double[] { B, sBC};
				eVecs[1] = new double[] { B, -sBC};
			}
			else {									// |B| == |C|
				eVecs[0] = new double[] { C, C};
				eVecs[1] = new double[] { -C, C};
			}
		}
		return true;
	}
	
	public boolean isReal() {
		return isReal;
	}
	
	/**
	 * Returns a vector holding the two real eigenvalues, which are both
	 * {@code NaN} if the associated matrix has no real eigenvalues.
	 * If the input matrix is symmetric, the first eigenvalue is the greater
	 * of the two.
	 * Note that the returned array is merely a reference to the internal array and 
	 * should thus be used read-only!
	 * @return as described
	 */
	public double[] getEigenvalues() {
		return this.eVals;
	}
	
	/**
	 * Returns a 2x2 array holding the two real eigenvectors.
	 * When used in the form
	 * <pre>
	 * double[][] X = getEigenvalues(); 
	 * </pre>
	 * then {@code X[0]} and {@code X[1]} yield the 1st and 2nd eigenvector,
	 * respectively. If the associated matrix has no real eigenvalues,
	 * then {@code X[0]} and {@code X[1]} are both {@code null}.
	 * Otherwise {@code X[k][0]} and {@code X[k][1]} are the x/y components
	 * of the k-th eigenvector.
	 * The ordering of the returned eigenvectors is the same as for the
	 * eigenvalues returned by {@link #getEigenvalues()}.
	 * @return as described
	 */
	public double[][] getEigenvectors() {
		return this.eVecs;
	}
		
	// for Testing only --------------------------------------------------------------
	
	static void checkEigen(double[][] M) {
		System.out.format("Checking M  = \n%s\n", Matrix.toString(M));
		Eigensolver2x2 es = new Eigensolver2x2(M);
		double[] eVals = es.getEigenvalues();
		double[][] eVecs = es.getEigenvectors();
		
		for (int i = 0; i < 2; i++) {
//			System.out.format("  Eigenpair %d:\n", (i+1));
			checkEigenPair(i+1, M, eVals[i], eVecs[i]);
		}
		System.out.println();
	}
	
	static void checkEigenPair(int i, double[][] M, double lambda, double[] x) {
		double[] Mx = Matrix.multiply(M, x);
		double[] lx = Matrix.multiply(lambda, x);
		double d = Matrix.normL2(Matrix.add(Mx, Matrix.multiply(-1, lx)));
		System.out.format("    \u03BB_%d  = %.6f\n", i, lambda);
		System.out.format("    x_%d = %s\n", i, Matrix.toString(x));
//		System.out.format("    M.x = %s\n", Matrix.toString(Mx));
//		System.out.format("    \u03BB.x = %s\n", Matrix.toString(lx));
//		System.out.format("    error = %.6f\n", d);
		if (d > 0.000001) {
			System.out.println("    **** WRONG ****");
		}
	}
	
	
	public static void main(String[] args) {
		PrintPrecision.set(6);
		{
			double[][] A = {
					{3, -2},
					{-4, 1}};
			checkEigen(A);
			// Mathematica: {5, -1} | {{-1, 1}, {1, 2}}
		}
		
		{
			double[][] A = {
					{-0.009562, 0.011933}, 
					{0.011933, -0.021158}};
			checkEigen(A);
			// Mathematica: {-0.028627, -0.002093} | {{-0.530554, 0.847651}, {-0.847651, -0.530554}}
		}

		{
			double[][] A = {
					{-0.004710, -0.006970},
					{-0.006970, -0.029195}};
			checkEigen(A);
			// Mathematica: {-0.0310401, -0.00286493} | {{0.255902, 0.966703}, {-0.966703, 0.255902}}
		}
		
		{
			double[][] A = {
					{0, 0},
					{0, 1}};
			checkEigen(A);
			// Mathematica: {1, 0} | {{0, 1}, {1, 0}}
		}
		
		{
			double[][] A = {
					{1, 0},
					{0, 0}};
			checkEigen(A);
			// Mathematica: {1, 0} | {{1, 0}, {0, 1}}
		}
		
		{	// Case 3.1
			double[][] A = {
					{1, 0},
					{-2, 1}};
			checkEigen(A);
			// Mathematica: {1, 1} | {{0, 1}, {0, 0}} !!!!
		}
		
		{	// Case 3.2
			double[][] A = {
					{1, -2},
					{0, 1}};
			checkEigen(A);
			// Mathematica: {1, 1} | {{1, 0}, {0, 0}} !!!!
		}
		{	// Case 3.3
			double[][] A = {
					{1, 2},
					{2, 1}};
			checkEigen(A);
			// Mathematica: {3, -1} | {{1, 1}, {-1, 1}}
		}
	}
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
