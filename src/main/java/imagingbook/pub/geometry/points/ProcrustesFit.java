package imagingbook.pub.geometry.points;

import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.mappings.linear.AffineMapping;

/**
 * This class implements the n-dimensional Procrustes fit algorithm described in 
 * Shinji Umeyama, "Least-squares estimation of transformation parameters 
 * between two point patterns", IEEE Transactions on Pattern Analysis and 
 * Machine Intelligence 13.4 (Apr. 1991), pp. 376–380.
 * The 
 * 
 * @author W. Burger
 * @version 2017/03/01
 *
 */
public class ProcrustesFit {
	
	final boolean allowTranslation;
	final boolean allowScaling;
	final boolean forceRotation;
	
	final int m;		// number of samples
	final int n;		// dimension of samples
	
	final double c;		// scale
	final RealMatrix Q;	// orthogonal (rotation) matrix
	final RealVector t;	// translation vector
	final double err;	// total (squared) error
	
	// --------------------------------------------------------------
	
	/**
	 * Convenience constructor. The supplied point sequences must have the
	 * same length and order (i.e., points must be in correspondence).
	 * @param xA Sequence of n-dimensional points
	 * @param xB Sequence of n-dimensional points (reference)
	 */
	public ProcrustesFit(List<double[]> xA, List<double[]> xB) {
		this(xA, xB, true, true, true);
	}
	
	/**
	 * Full constructor. The supplied point sequences must have the
	 * same length and order (i.e., points must be in correspondence).
	 * @param xA Sequence of n-dimensional points
	 * @param xB Sequence of n-dimensional points (reference)
	 * @param allowTranslation If {@code true}, translation (t) between point sets is considered, 
	 * 		otherwise zero translation is assumed.
	 * @param allowScaling If {@code true}, scaling (c) between point sets is considered, 
	 * 		otherwise unit scale assumed.
	 * @param forceRotation If {@code true}, the orthogonal part of the transformation (Q)
	 * 		is forced to a true rotation and no reflection is allowed.
	 */
	public ProcrustesFit(List<double[]> xA, List<double[]> xB, 
			boolean allowTranslation, boolean allowScaling, boolean forceRotation) {
		if (xA.size() != xB.size())
			throw new IllegalArgumentException("point sequences xA, xB must have same length");
		
		this.allowTranslation = allowTranslation;
		this.allowScaling = allowScaling;
		this.forceRotation = forceRotation;
		this.m = xA.size();
		this.n = xA.get(0).length;
		
		double[] meanA = null;
		double[] meanB = null;
		
		if (this.allowTranslation) {
			meanA = getMeanVec(xA);
			meanB = getMeanVec(xB);
		}
		
		RealMatrix A = makeDataMatrix(xA, meanA);
		RealMatrix B = makeDataMatrix(xB, meanB);
		MatrixUtils.checkAdditionCompatible(A, B);	// A, B of same dimensions?
		
		RealMatrix BAt = B.multiply(A.transpose());
		SingularValueDecomposition svd = new SingularValueDecomposition(BAt);
		
		RealMatrix U = svd.getU();
		RealMatrix S = svd.getS();
		RealMatrix V = svd.getV();
			
		double d = (svd.getRank() >= n) ? det(BAt) : det(U) * det(V);
		
		RealMatrix D = MatrixUtils.createRealIdentityMatrix(n);
		if (d < 0 && forceRotation)
			D.setEntry(n - 1, n - 1, -1);
		
		Q = U.multiply(D).multiply(V.transpose());
		
		double normA = A.getFrobeniusNorm();
		double normB = B.getFrobeniusNorm();
		
		c = (this.allowScaling) ? 
				S.multiply(D).getTrace() / sqr(normA) : 1.0;
		
		if (allowTranslation) {
			RealVector ma = MatrixUtils.createRealVector(meanA);
			RealVector mb = MatrixUtils.createRealVector(meanB);
			t = mb.subtract(Q.scalarMultiply(c).operate(ma));
		}
		else {
			t = new ArrayRealVector(n);	// zero vector
		}
		
		err = sqr(normB) - sqr(S.multiply(D).getTrace() / normA);
	}
	
	// -----------------------------------------------------------------
	
	/**
	 * Retrieves the estimated scale.
	 * @return The estimated scale (or 1 if {@code allowscaling = false}).
	 */
	public double getScale() {
		return c;
	}
	
	/**
	 * Retrieves the estimated orthogonal (rotation) matrix.
	 * @return The estimated rotation matrix.
	 */
	public RealMatrix getQ() {
		return Q;
	}
	
	/**
	 * Retrieves the estimated translation vector.
	 * @return The estimated translation vector.
	 */
	public RealVector getT() {
		return t;
	}
	
	/**
	 * Retrieves the total (squared) error for the estimated fit.
	 * @return The total error for the estimated fit.
	 */
	public double getError() {
		return err;
	}
	
	// calculate total error (for testing)
	/**
	 * Calculates the total error for the estimated fit as
	 * the sum of the squared Euclidean distances between the 
	 * transformed point set A and the reference set B.
	 * This method is provided for testing as an alternative to
	 * the quicker {@link getError} method.
	 * @param xA Sequence A of n-dimensional points.
	 * @param xB Sequence B of n-dimensional points (reference).
	 * @return The total error for the estimated fit.
	 */
	public double getEuclideanError(List<double[]> xA, List<double[]> xB) {
		RealMatrix cQ = Q.scalarMultiply(c);
		double ee = 0;
		for (int i = 0; i < xA.size(); i++) {
			RealVector ai = new ArrayRealVector(xA.get(i));
			RealVector bi = new ArrayRealVector(xB.get(i));
			RealVector aiT = cQ.operate(ai).add(t);
			double ei = aiT.subtract(bi).getNorm();
			ee = ee + sqr(ei);
		}
		return ee;
	}
	
	public AffineMapping getAffineMapping2D() {
		if (n != 2)
			return null;
		AffineMapping map = new AffineMapping(
				c * Q.getEntry(0, 0), c * Q.getEntry(0, 1), t.getEntry(0),
				c * Q.getEntry(1, 0), c * Q.getEntry(1, 1), t.getEntry(1),
				false
				);
		return map;	
	}
	
	// -----------------------------------------------------------------
	
	private double det(RealMatrix M) {
		return new LUDecomposition(M).getDeterminant();
	}
	
	private double sqr(final double x) {
		return x * x;
	}
	
	private double[] getMeanVec(List<double[]> X) {
		double[] sum = new double[X.get(0).length];
		for (double[] x : X) {
			for (int j = 0; j < x.length; j++) {
				sum[j] = sum[j] + x[j];
			}
		}
		Matrix.multiplyD(1.0 / X.size(), sum);
		return sum;
	}
	
	private RealMatrix makeDataMatrix(List<double[]> X) {
		final int m = X.size();
		final int n = X.get(0).length;
		RealMatrix M = MatrixUtils.createRealMatrix(n, m);
		int i = 0;
		for (double[] x : X) {
			RealVector xi = MatrixUtils.createRealVector(x);
			M.setColumnVector(i, xi);
			i++;
		}
		return M;
	}
	
	private RealMatrix makeDataMatrix(List<double[]> X, double[] meanX) {
		if (meanX == null) {
			return makeDataMatrix(X);
		}
		final int m = X.size();
		final int n = X.get(0).length;
		RealMatrix M = MatrixUtils.createRealMatrix(n, m);
		RealVector mean = MatrixUtils.createRealVector(meanX);
		int i = 0;
		for (double[] x : X) {
			RealVector xi = MatrixUtils.createRealVector(x).subtract(mean);
			M.setColumnVector(i, xi);
			i++;
		}
		return M;
	}
	
	void printSVD(SingularValueDecomposition svd) {
		RealMatrix U = svd.getU();
		RealMatrix S = svd.getS();
		RealMatrix V = svd.getV();
		System.out.println("------ SVD ---------------");
		System.out.println("U = " + Matrix.toString(U.getData()));
		System.out.println("S = " + Matrix.toString(S.getData()));
		System.out.println("V = " + Matrix.toString(V.getData()));
		System.out.println("--------------------------");
	}

}

