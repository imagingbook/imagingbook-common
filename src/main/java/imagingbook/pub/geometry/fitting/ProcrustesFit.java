package imagingbook.pub.geometry.fitting;

import java.util.ArrayList;
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
 * Usage example (also see the main() method of this class):
 * <pre>
 * {@code
 * List<double> X = ... // create list of m points (n-dimensional)
 * List<double> Y = ... // create list of m points (n-dimensional)
 * ProcrustesFit pf = new ProcrustesFit();
 * pf.fit(X, Y);
 * double err = pf.getError();
 * RealMatrix R = pf.getR();
 * RealVector t = pf.getT();
 * double c = pf.getScale();
 * double err = pf.getError();
 * RealMatrix M = pf.getTransformationMatrix();
 * }
 * </pre>
 * This is a preliminary version!
 * TODO: Implement common interface with other fitters; polish parameter handling.
 * 
 * @author W. Burger
 * @version 2017/03/21
 */
public class ProcrustesFit extends LinearFit {
	
	private final boolean allowTranslation;
	private final boolean allowScaling;
	private final boolean forceRotation;
	
	private int m;		// number of samples
	private int n;		// dimension of samples
	
	private RealMatrix R = null;					// orthogonal (rotation) matrix
	private RealVector t = null;					// translation vector
	private double c = 1;							// scale
	private double err = Double.POSITIVE_INFINITY;	// total (squared) error
	
	// --------------------------------------------------------------
	
	/**
	 * Full constructor. 
	 * @param allowTranslation If {@code true}, translation (t) between point sets is considered, 
	 * 		otherwise zero translation is assumed.
	 * @param allowScaling If {@code true}, scaling (c) between point sets is considered, 
	 * 		otherwise unit scale assumed.
	 * @param forceRotation If {@code true}, the orthogonal part of the transformation (Q)
	 * 		is forced to a true rotation and no reflection is allowed.
	 */
	public ProcrustesFit(boolean allowTranslation, boolean allowScaling, boolean forceRotation) {
		this.allowTranslation = allowTranslation;
		this.allowScaling = allowScaling;
		this.forceRotation = forceRotation;
	}
	
	/**
	 * Convenience constructor. 
	 */
	public ProcrustesFit() {
		this(true, true, true);
	}
	

	@Override
	public void fit(List<double[]> X, List<double[]> Y) {
		if (X.size() != Y.size())
			throw new IllegalArgumentException("point sequences X, Y must have same length");
		this.m = X.size();
		this.n = X.get(0).length;
		
		double[] meanX = null;
		double[] meanY = null;
		
		if (this.allowTranslation) {
			meanX = getMeanVec(X);
			meanY = getMeanVec(Y);
		}
		
		RealMatrix P = makeDataMatrix(X, meanX);
		RealMatrix Q = makeDataMatrix(Y, meanY);
		MatrixUtils.checkAdditionCompatible(P, Q);	// P, Q of same dimensions?
		
		RealMatrix QPt = Q.multiply(P.transpose());
		SingularValueDecomposition svd = new SingularValueDecomposition(QPt);
		
		RealMatrix U = svd.getU();
		RealMatrix S = svd.getS();
		RealMatrix V = svd.getV();
			
		double d = (svd.getRank() >= n) ? det(QPt) : det(U) * det(V);
		
		RealMatrix D = MatrixUtils.createRealIdentityMatrix(n);
		if (d < 0 && forceRotation)
			D.setEntry(n - 1, n - 1, -1);
		
		R = U.multiply(D).multiply(V.transpose());
		
		double normP = P.getFrobeniusNorm();
		double normQ = Q.getFrobeniusNorm();
		
		c = (this.allowScaling) ? 
				S.multiply(D).getTrace() / sqr(normP) : 1.0;
		
		if (allowTranslation) {
			RealVector ma = MatrixUtils.createRealVector(meanX);
			RealVector mb = MatrixUtils.createRealVector(meanY);
			t = mb.subtract(R.scalarMultiply(c).operate(ma));
		}
		else {
			t = new ArrayRealVector(n);	// zero vector
		}
		
		err = sqr(normQ) - sqr(S.multiply(D).getTrace() / normP);
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
	public RealMatrix getR() {
		return R;
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
	
	/**
	 * Calculates the total error for the estimated fit as
	 * the sum of the squared Euclidean distances between the 
	 * transformed point set X and the reference set Y.
	 * This method is provided for testing as an alternative to
	 * the quicker {@link getError} method.
	 * @param X Sequence of n-dimensional points.
	 * @param Y Sequence of n-dimensional points (reference).
	 * @return The total error for the estimated fit.
	 */
	public double getEuclideanError(List<double[]> X, List<double[]> Y) {
		RealMatrix cR = R.scalarMultiply(c);
		double ee = 0;
		for (int i = 0; i < X.size(); i++) {
			RealVector ai = new ArrayRealVector(X.get(i));
			RealVector bi = new ArrayRealVector(Y.get(i));
			RealVector aiT = cR.operate(ai).add(t);
			double ei = aiT.subtract(bi).getNorm();
			ee = ee + sqr(ei);
		}
		return ee;
	}
	
	@Override
	public RealMatrix getTransformationMatrix() {
		RealMatrix cR = R.scalarMultiply(c);
		RealMatrix M = MatrixUtils.createRealMatrix(n, n + 1);
		M.setSubMatrix(cR.getData(), 0, 0);
		M.setColumnVector(n, t);
		return M;
	}
	
	/**
	 * Returns a 2D {@link AffineMapping} object, as defined in
	 * {@code imagingbook.pub.geometry.mappings.linear}.
	 * Throws an exception if the dimensionality of the data
	 * is not 2.
	 * @return An affine mapping object.
	 */
	public AffineMapping getAffineMapping2D() {
		if (n != 2)
			throw new UnsupportedOperationException("fit is not 2D");
		AffineMapping map = new AffineMapping(
				c * R.getEntry(0, 0), c * R.getEntry(0, 1), t.getEntry(0),
				c * R.getEntry(1, 0), c * R.getEntry(1, 1), t.getEntry(1),
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
	
	private void printSVD(SingularValueDecomposition svd) {
		RealMatrix U = svd.getU();
		RealMatrix S = svd.getS();
		RealMatrix V = svd.getV();
		System.out.println("------ SVD ---------------");
		System.out.println("U = " + Matrix.toString(U.getData()));
		System.out.println("S = " + Matrix.toString(S.getData()));
		System.out.println("V = " + Matrix.toString(V.getData()));
		System.out.println("--------------------------");
	}
	
	private static double roundToDigits(double x, int ndigits) {
		int d = (int) Math.pow(10, ndigits);
		return Math.rint(x * d) / d;
	}

	// --------------------------------------------------------------------------------

	public static void main(String[] args) {
		int NDIGITS = 1;
		
		double scale = 3.5;
		System.out.println("original scale = " + scale);
		
		double alpha = 0.6;
		double[][] R0data =
			{{ Math.cos(alpha), -Math.sin(alpha) },
			 { Math.sin(alpha),  Math.cos(alpha) }};
		
		RealMatrix R0 = MatrixUtils.createRealMatrix(R0data);
		System.out.println("original R = \n" + Matrix.toString(R0.getData()));
		
		double[] t0 = {4, -3};
		System.out.println("original t = " + Matrix.toString(t0));
		
		List<double[]> X = new ArrayList<double[]>();
		List<double[]> Y = new ArrayList<double[]>();
		
		X.add(new double[] {2, 5});
		X.add(new double[] {7, 3});
		X.add(new double[] {0, 9});
		X.add(new double[] {5, 4});
		
		for (double[] a : X) {
			double[] b = R0.operate(a);
			b[0] = roundToDigits(scale * b[0] + t0[0], NDIGITS);
			b[1] = roundToDigits(scale * b[1] + t0[1], NDIGITS);
			Y.add(b);
		}

		boolean allowTranslation = true;
		boolean allowScaling = true;
		boolean forceRotation = true;
		ProcrustesFit pf = new ProcrustesFit(allowTranslation, allowScaling, forceRotation);
		pf.fit(X, Y);

		System.out.println("R = \n" + Matrix.toString(pf.getR().getData()));
		System.out.println("t = " + Matrix.toString(pf.getT().toArray()));
		System.out.format("c = %.3f\n", pf.getScale());
		System.out.format("err1 = %.3f\n", pf.getError());
		
		RealMatrix M = pf.getTransformationMatrix();
		System.out.println("M = \n" + Matrix.toString(M.getData()));
	}
	

}

