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
import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.geometry.mappings.linear.AffineMapping2D;


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
 * 
 * @author W. Burger
 * @version 2020-01-05
 */
public class ProcrustesFit extends AffineFit2D {
	
	private final boolean allowTranslation;
	private final boolean allowScaling;
	private final boolean forceRotation;
	
	private RealMatrix R = null;					// orthogonal (rotation) matrix
	private RealVector t = null;					// translation vector
	private double c = 1;							// scale
	private double err = Double.POSITIVE_INFINITY;	// total (squared) error
	
	// --------------------------------------------------------------
	
	/**
	 * Full constructor. 
	 * @param n dimensions
	 * @param allowTranslation if {@code true}, translation (t) between point sets is considered, 
	 * 		otherwise zero translation is assumed
	 * @param allowScaling if {@code true}, scaling (c) between point sets is considered, 
	 * 		otherwise unit scale assumed
	 * @param forceRotation if {@code true}, the orthogonal part of the transformation (Q)
	 * 		is forced to a true rotation and no reflection is allowed
	 */
	public ProcrustesFit(int n, boolean allowTranslation, boolean allowScaling, boolean forceRotation) {
		//super(n);
		this.allowTranslation = allowTranslation;
		this.allowScaling = allowScaling;
		this.forceRotation = forceRotation;
	}
	
	/**
	 * Convenience constructor. 
	 */
	public ProcrustesFit() {
		this(2, true, true, true);
	}
	

	@Override
	public void fit(List<Point> X, List<Point> Y) {
		if (X.size() != Y.size())
			throw new IllegalArgumentException("point sequences X, Y must have same length");
//		this.m = X.size();
//		if (X.get(0).length < n)
//			throw new IllegalArgumentException("dimensionality of samples must be >= " + n);
		
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
			
		double d = (svd.getRank() >= 2) ? det(QPt) : det(U) * det(V);
		
		RealMatrix D = MatrixUtils.createRealIdentityMatrix(2);
		if (d < 0 && forceRotation)
			D.setEntry(1, 1, -1);
		
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
			t = new ArrayRealVector(2);	// zero vector
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
		RealMatrix M = MatrixUtils.createRealMatrix(2, 3);
		M.setSubMatrix(cR.getData(), 0, 0);
		M.setColumnVector(2, t);
		return M;
	}
	
	/**
	 * Returns a 2D {@link AffineMapping} object, as defined in
	 * {@code imagingbook.pub.geometry.mappings.linear}.
	 * Throws an exception if the dimensionality of the data
	 * is not 2.
	 * @return An affine mapping object.
	 */
	public AffineMapping2D getAffineMapping2D() {
//		if (n != 2)
//			throw new UnsupportedOperationException("fit is not 2D");
		AffineMapping2D map = new AffineMapping2D(
				c * R.getEntry(0, 0), c * R.getEntry(0, 1), t.getEntry(0),
				c * R.getEntry(1, 0), c * R.getEntry(1, 1), t.getEntry(1));
		return map;	
	}
	
	// -----------------------------------------------------------------
	
	private double det(RealMatrix M) {
		return new LUDecomposition(M).getDeterminant();
	}
	
	private double sqr(final double x) {
		return x * x;
	}
	
	private double[] getMeanVec(List<Point> X) {
		//double[] sum = new double[X.get(0).length];
		final int m = X.size();
		double sumX = 0;
		double sumY = 0;
		for (Point x : X) {
			sumX = sumX + x.getX();
			sumY = sumY + x.getY();
//			for (int j = 0; j < x.length; j++) {
//				sum[j] = sum[j] + x[j];
//			}
		}
//		Matrix.multiplyD(1.0 / X.size(), sum);
		return new double[] {sumX / m, sumY / m};
	}
	
	private RealMatrix makeDataMatrix(List<Point> X) {
		final int m = X.size();
//		final int n = X.get(0).length;
		RealMatrix M = MatrixUtils.createRealMatrix(2, m);
		int i = 0;
		for (Point x : X) {
			RealVector xi = MatrixUtils.createRealVector(x.toArray());
			M.setColumnVector(i, xi);
			i++;
		}
		return M;
	}
	
	private RealMatrix makeDataMatrix(List<Point> X, double[] meanX) { // TODO: change double[]
		if (meanX == null) {
			return makeDataMatrix(X);
		}
		final int m = X.size();
		RealMatrix M = MatrixUtils.createRealMatrix(2, m);
		RealVector mean = MatrixUtils.createRealVector(meanX);
		int i = 0;
		for (Point x : X) {
			RealVector xi = MatrixUtils.createRealVector(x.toArray()).subtract(mean);
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
		
		List<Point> X = new ArrayList<>();
		List<Point> Y = new ArrayList<>();
		
		X.add(Point.create(2, 5));
		X.add(Point.create(7, 3));
		X.add(Point.create(0, 9));
		X.add(Point.create(5, 4));
		
		for (Point a : X) {
			Point b = Point.create(R0.operate(a.toArray()));
			double bx = roundToDigits(scale * b.getX() + t0[0], NDIGITS);
			double by = roundToDigits(scale * b.getY() + t0[1], NDIGITS);
			Y.add(Point.create(bx, by));
		}

		boolean allowTranslation = true;
		boolean allowScaling = true;
		boolean forceRotation = true;
		ProcrustesFit pf = new ProcrustesFit(2, allowTranslation, allowScaling, forceRotation);
		pf.fit(X, Y);

		System.out.println("R = \n" + Matrix.toString(pf.getR().getData()));
		System.out.println("t = " + Matrix.toString(pf.getT().toArray()));
		System.out.format("c = %.3f\n", pf.getScale());
		System.out.format("err1 = %.3f\n", pf.getError());
		
		RealMatrix M = pf.getTransformationMatrix();
		System.out.println("M = \n" + Matrix.toString(M.getData()));
	}

}

