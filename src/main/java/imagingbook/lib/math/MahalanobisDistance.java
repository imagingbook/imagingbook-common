/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.math;

import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import ij.IJ;

/**
 * This class implements the Mahalanobis distance using the Apache commons math library.
 * See the numerical example in Theodoridis/Koutrumbras, "Pattern Recognition",
 * Academic Press 1999 (p. 27).
 * No statistical bias correction is applied.
 */
public class MahalanobisDistance extends VectorNorm {
	
	public static final double DEFAULT_MIN_DIAGONAL = 10E-15;
	
	private final int N;					// number of samples
	private final int M;					// feature dimension
	private final double[] mean;			// the distribution's mean vector (\mu)
	private final double[][] Cov;			// covariance matrix of size n x n
	private final double[][] iCov;			// inverse covariance matrix size n x n
	
	/**
	 * Create a new instance from an array of m-dimensional samples, e.g.
	 * samples = {{x1,y1}, {x2,y2},...,{xn,yn}} for a vector of n two-dimensional
	 * samples.
	 * @param samples A vector of length n with m-dimensional samples, i.e.
	 *      samples[k][i] represents the i-th component of the k-th sample.
	 * @param diagonalIncrement Quantity added to the diagonal values of the
	 *      covariance matrix to avoid singularity. 
	 */
	public MahalanobisDistance(double[][] samples, double diagonalIncrement) {
		N = samples.length;
		M = samples[0].length;
		if (N < 1 || M < 1) {
			throw new IllegalArgumentException("dimension less than 1");
		}
		if (diagonalIncrement < 0) {
			throw new IllegalArgumentException("diagonal increment must be positive");
		}

		mean = makeMeanVector(samples);
		RealMatrix S = new Covariance(samples, false).getCovarianceMatrix();
		
		// condition the covariance matrix to avoid singularity:
		S = conditionCovarianceMatrix(S, diagonalIncrement);
		Cov = S.getData();
		
		// get the inverse covariance matrix
		iCov = MatrixUtils.inverse(S).getData();	// not always symmetric?
	}
	
	/**
	 * Create a new instance from an array of m-dimensional samples, e.g.
	 * samples = {{x1,y1}, {x2,y2},...,{xn,yn}} for a vector of n two-dimensional
	 * samples.
	 * @param samples A vector of length n with m-dimensional samples, i.e.
	 *      samples[k][i] represents the i-th component of the k-th sample.
	 */
	public MahalanobisDistance(double[][] samples) {
		this(samples, DEFAULT_MIN_DIAGONAL);
	}
	
	// 
	/**
	 * Conditions the supplied covariance matrix by enforcing
	 * positive eigenvalues along its main diagonal. 
	 * @param S original covariance matrix
	 * @param minDiagonal the minimum positive value of diagonal elements
	 * @return conditioned covariance matrix
	 */
	private RealMatrix conditionCovarianceMatrix(RealMatrix S, double minDiagonal) {
		EigenDecomposition ed = new EigenDecomposition(S);  // S  ->  V . D . V^T
		RealMatrix V  = ed.getV();
		RealMatrix D  = ed.getD();	// diagonal matrix of eigenvalues
		RealMatrix VT = ed.getVT();
		for (int i = 0; i < D.getRowDimension(); i++) {
			D.setEntry(i, i, Math.max(D.getEntry(i, i), minDiagonal));	// setting eigenvalues to zero is not enough!
		}
		return V.multiply(D).multiply(VT);
	}
	
	// --------------------------------------------------------------
	
	private double[] makeMeanVector(double[][] samples) {
		int n = samples.length;
		int m = samples[0].length;
		double[] mu = new double[m];
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < m; i++) {
				mu[i] = mu[i] + samples[k][i];
			}
		}
		for (int i = 0; i < m; i++) {
			mu[i] = mu[i] / n;
		}
		return mu;
	}
	
	public int getNumberOfSamples() {
		return N;
	}

	public int getSampleDimension() {
		return M;
	}
	
	//------------------------------------------------------------------------------------
	
	/**
	 * Returns the "conditioned" covariance matrix used for distance calculations.
	 * @return The conditioned covariance matrix
	 */
	public double[][] getCovarianceMatrix() {
		return Cov;
	}
	
	/**
	 * Returns the inverse of the conditioned covariance matrix.
	 * @return The inverse of the conditioned covariance matrix
	 */
	public double[][] getInverseCovarianceMatrix() {
		return iCov;
	}
	
	
	public double[] getMeanVector() {
		return mean;
	}
	
	//------------------------------------------------------------------------------------
	
	/**
	 * Returns the 'root' (U) of the inverse covariance matrix S^{-1},
	 * such that S^{-1} = U^T . U
	 * This matrix can be used to pre-transform the original sample
	 * vectors X (by X &#8594; U . X) to a space where distance measurement (in the Mahalanobis
	 * sense) can be calculated with the usual Euclidean norm.
	 * The matrix U is invertible in case the reverse mapping is required.
	 * 
	 * @return The matrix for pre-transforming the original sample vectors.
	 */
	public double[][] getWhiteningTransformation() {
		IJ.log("in whitening");
		double relativeSymmetryThreshold = 1.0E-6; 		// CholeskyDecomposition.DEFAULT_RELATIVE_SYMMETRY_THRESHOLD == 1.0E-15; too small!
        double absolutePositivityThreshold = 1.0E-10;	// CholeskyDecomposition.DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD == 1.0E-10;
		CholeskyDecomposition cd = 
				new CholeskyDecomposition(MatrixUtils.createRealMatrix(iCov),
						relativeSymmetryThreshold, absolutePositivityThreshold);
		RealMatrix U = cd.getLT();
		return U.getData();
	}
	
	//------------------------------------------------------------------------------------
	
	public double distance(double[] X, double[] Y) {
		return Math.sqrt(distance2(X, Y));
	}
	
	public double distance2(double[] X, double[] Y) {
		if (X.length != M || Y.length != M) {
			throw new IllegalArgumentException("vectors must be of length " + M);
		}
		// TODO: implement with methods from Matrix class
		// d^2(X,Y) = (X-Y)^T . S^{-1} . (X-Y)
		double[] dXY = new double[M]; // = (X-Y)
		for (int k = 0; k < M; k++) {
			dXY[k] = X[k] - Y[k];
		}
		double[] iSdXY = new double[M]; // = S^{-1} . (X-Y)
		for (int k = 0; k < M; k++) {
			for (int i = 0; i < M; i++) {
				iSdXY[k] += iCov[i][k] * dXY[i];
			}
		}
		double d2 = 0;	// final sum
		for (int k = 0; k < M; k++) {
			d2 += dXY[k] * iSdXY[k];
		}						// d = (X-Y)^T . S^{-1} . (X-Y)
		return d2;
	}
	
	/**
	 * Calculates the Mahalanobis distance between point X and
	 * the mean of the reference distribution.
	 * @param X an arbitrary K-dimensional vector
	 * @return Mahalanobis distance between the point X and
	 * 			the mean of the reference distribution.
	 */
	public double distance(double[] X) {
		return distance(X, mean);
	}

	/**
	 * Calculates the sqared Mahalanobis distance between point X and
	 * the mean of the reference distribution.
	 * @param X an arbitrary K-dimensional vector
	 * @return squared Mahalanobis distance between the point X and
	 * 			the mean of the reference distribution.
	 */
	public double distance2(double[] X) {
		return distance2(X, mean);
	}
	
	//----------- unsupported (but required) stuff -------------------------------------------------
	
	public double distance(int[] a, int[] b) {
		throw new UnsupportedOperationException();
	}

	public double distance2(int[] a, int[] b) {
		throw new UnsupportedOperationException();
	}

	public double magnitude(double[] X) {
		throw new UnsupportedOperationException();
	}

	public double magnitude(int[] X) {
		throw new UnsupportedOperationException();
	}

	public double getScale(int n) {
		return 1.0;
	}

	@Override
	public double distance(float[] a, float[] b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double distance2(float[] a, float[] b) {
		throw new UnsupportedOperationException();
	}
	
	// ----------------------------------------------------------------
	
	/** 
	 * Test example from Burger/Burge UTICS-C Appendix:
	 * N = 4 samples, K = 3 dimensions
	 * 
	 * @param args ignored
	 */
	public static void main(String[] args) {
		
		double[] X0 = {0, 0, 0};
		double[] X1 = {75, 37, 12};
		double[] X2 = {41, 27, 20};
		double[] X3 = {93, 81, 11};
		double[] X4 = {12, 48, 52};
		double[] X5 = {12, 48, 100};
		
		
		System.out.println("X0 = " + Matrix.toString(X0));
		System.out.println("X1 = " + Matrix.toString(X1));
		System.out.println("X2 = " + Matrix.toString(X2));
		System.out.println("X3 = " + Matrix.toString(X3));
		System.out.println("X4 = " + Matrix.toString(X4));
		System.out.println("X5 = " + Matrix.toString(X5));
		System.out.println();
		
		double[][] samples = {X0, X1, X2, X3, X4, X5};
		

		MahalanobisDistance mhd = new MahalanobisDistance(samples);
		
		double[] mu = mhd.getMeanVector();
		System.out.println("mu = " + Matrix.toString(mu));
		
		System.out.println();
		
		// covariance matrix Cov (3x3)
		double[][] cov = mhd.getCovarianceMatrix();
		System.out.println("cov = " + Matrix.toString(cov));
		
		System.out.println();
		
		double[][] icov =  mhd.getInverseCovarianceMatrix();
		System.out.println("icov = " + Matrix.toString(icov)); System.out.println();

		System.out.format("MH-dist(X1,X1) = %.3f\n", mhd.distance(X1, X1));
		System.out.format("MH-dist(X1,X2) = %.3f\n", mhd.distance(X1, X2));
		System.out.format("MH-dist(X2,X1) = %.3f\n", mhd.distance(X2, X1));
		System.out.format("MH-dist(X1,X3) = %.3f\n", mhd.distance(X1, X3));
		System.out.format("MH-dist(X1,X4) = %.3f\n", mhd.distance(X1, X4));
		System.out.format("MH-dist(X1,X5) = %.3f\n", mhd.distance(X1, X5));
		System.out.println();
		
		System.out.format("MH-dist(X1,mu) = %.3f\n", mhd.distance(X1, mu));
		System.out.format("MH-dist(X2,mu) = %.3f\n", mhd.distance(X2, mu));
		System.out.format("MH-dist(X3,mu) = %.3f\n", mhd.distance(X3, mu));
		System.out.format("MH-dist(X4,mu) = %.3f\n", mhd.distance(X4, mu));
		System.out.println();
		
		System.out.format("MH-dist(X5,mu) = %.3f\n", mhd.distance(X5, mu));
		System.out.format("MH-dist(X0,mu) = %.3f\n", mhd.distance(X0, mu));
		System.out.println();
		
		VectorNorm L2 = new VectorNorm.L2();
		System.out.format("L2-distance(X1,X2) = %.3f\n", L2.distance(X1, X2));
		System.out.format("L2-distance(X2,X1) = %.3f\n", L2.distance(X2, X1));
		
		// ------------------------------------------------------
		
		System.out.println();
		System.out.println("Testing pre-transformed Mahalanobis distances:");
		RealMatrix U = MatrixUtils.createRealMatrix(mhd.getWhiteningTransformation());
		System.out.println("U = " + Matrix.toString(U.getData()));
		double[] Y0 = U.operate(X0);
		double[] Y1 = U.operate(X1);
		double[] Y2 = U.operate(X2);
		double[] Y3 = U.operate(X3);
		double[] Y4 = U.operate(X4);
		double[] Y5 = U.operate(X5);
		
		System.out.println("Y0 = " + Matrix.toString(Y0));
		System.out.println("Y1 = " + Matrix.toString(Y1));
		System.out.println("Y2 = " + Matrix.toString(Y2));
		System.out.println("Y3 = " + Matrix.toString(Y3));
		System.out.println("Y4 = " + Matrix.toString(Y4));
		System.out.println("Y5 = " + Matrix.toString(Y5));
		
		System.out.format("pre-transformed MH-distance(X1,X2) = %.3f\n", L2.distance(Y1, Y2));
		System.out.format("pre-transformed MH-distance(X1,X3) = %.3f\n", L2.distance(Y1, Y3));
		System.out.format("pre-transformed MH-distance(X1,X4) = %.3f\n", L2.distance(Y1, Y4));
		System.out.format("pre-transformed MH-distance(X1,X5) = %.3f\n", L2.distance(Y1, Y5));
		
	}

/* Results: verified with Mathematica (bias-corrected):
X0 = {0.000, 0.000, 0.000}
X1 = {75.000, 37.000, 12.000}
X2 = {41.000, 27.000, 20.000}
X3 = {93.000, 81.000, 11.000}
X4 = {12.000, 48.000, 52.000}
X5 = {12.000, 48.000, 100.000}

mu = {55.250, 48.250, 23.750}

cov = {{1296.250, 442.583, -627.250}, 
{442.583, 550.250, -70.917}, 
{-627.250, -70.917, 370.917}}

icov = {{0.024, -0.014, 0.038}, 
{-0.014, 0.011, -0.022}, 
{0.038, -0.022, 0.063}}

MH-dist(X1,X1) = 0.000
MH-dist(X1,X2) = 2.449
MH-dist(X2,X1) = 2.449
MH-dist(X1,X3) = 2.449
MH-dist(X1,X4) = 2.449
MH-dist(X1,X5) = 11.714

MH-dist(X1,mu) = 1.500
MH-dist(X2,mu) = 1.500
MH-dist(X3,mu) = 1.500
MH-dist(X4,mu) = 1.500

MH-dist(X5,mu) = 12.613
MH-dist(X0,mu) = 10.214

L2-distance(X1,X2) = 36.332
L2-distance(X2,X1) = 36.332

Testing pre-transformed Mahalanobis distances:
U = {{0.155, -0.093, 0.245}, 
{0.000, 0.043, 0.008}, 
{0.000, 0.000, 0.052}}
pre-transformed MH-distance(X1,X2) = 2.449
pre-transformed MH-distance(X1,X3) = 2.449
pre-transformed MH-distance(X1,X4) = 2.449
pre-transformed MH-distance(X1,X5) = 11.714
*/
	
/* Results non-bias corrected:
X0 = {0.000, 0.000, 0.000}
X1 = {75.000, 37.000, 12.000}
X2 = {41.000, 27.000, 20.000}
X3 = {93.000, 81.000, 11.000}
X4 = {12.000, 48.000, 52.000}
X5 = {12.000, 48.000, 100.000}

mu = {55.250, 48.250, 23.750}

cov = {{972.188, 331.938, -470.438}, 
{331.938, 412.687, -53.188}, 
{-470.438, -53.188, 278.188}}

icov = {{0.032, -0.019, 0.051}, 
{-0.019, 0.014, -0.030}, 
{0.051, -0.030, 0.083}}

MH-dist(X1,X1) = 0.000
MH-dist(X1,X2) = 2.828
MH-dist(X2,X1) = 2.828
MH-dist(X1,X3) = 2.828
MH-dist(X1,X4) = 2.828
MH-dist(X1,X5) = 13.527

MH-dist(X1,mu) = 1.732
MH-dist(X2,mu) = 1.732
MH-dist(X3,mu) = 1.732
MH-dist(X4,mu) = 1.732

MH-dist(X5,mu) = 14.564
MH-dist(X0,mu) = 11.794

L2-distance(X1,X2) = 36.332
L2-distance(X2,X1) = 36.332

Testing pre-transformed Mahalanobis distances:
U = {{0.179, -0.108, 0.282}, 
{0.000, 0.050, 0.010}, 
{0.000, 0.000, 0.060}}
Y0 = {0.000, 0.000, 0.000}
Y1 = {12.840, 1.959, 0.719}
Y2 = {10.085, 1.536, 1.199}
Y3 = {11.044, 4.142, 0.660}
Y4 = {11.664, 2.888, 3.118}
Y5 = {25.218, 3.345, 5.996}
pre-transformed MH-distance(X1,X2) = 2.828
pre-transformed MH-distance(X1,X3) = 2.828
pre-transformed MH-distance(X1,X4) = 2.828
pre-transformed MH-distance(X1,X5) = 13.527
*/
}
