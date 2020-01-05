package imagingbook.pub.geometry.fitting;

import static imagingbook.lib.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.lib.math.Matrix;
import imagingbook.lib.settings.PrintPrecision;
import imagingbook.pub.geometry.basic.Point;


/**
 * Implements a 2-dimensional Procrustes fit, using the algorithm described in 
 * Shinji Umeyama, "Least-squares estimation of transformation parameters 
 * between two point patterns", IEEE Transactions on Pattern Analysis and 
 * Machine Intelligence 13.4 (Apr. 1991), pp. 376–380.
 * Usage example (also see the {@code main()} method of this class):
 * <pre>
 * Point[] P = ... // create sequence of 2D source points
 * Point[] Q = ... // create sequence of 2D target points
 * ProcrustesFit pf = new ProcrustesFit(P, Q);
 * double err = pf.getError();
 * RealMatrix R = pf.getR();
 * RealVector t = pf.getT();
 * double s = pf.getScale();
 * double err = pf.getError();
 * RealMatrix A = pf.getTransformationMatrix();
 * </pre>
 * 
 * @author W. Burger
 * @version 2020-01-05
 */
public class ProcrustesFit implements LinearFit2D {
	
	private RealMatrix R = null;					// orthogonal (rotation) matrix
	private RealVector t = null;					// translation vector
	private double s = 1;							// scale
	private double err = Double.POSITIVE_INFINITY;	// total (squared) error
	
	// --------------------------------------------------------------
	
	/**
	 * Convenience constructor, with
	 * parameters {@code allowTranslation}, {@code allowScaling} and {@code forceRotation}
	 * set to {@code true}.
	 * @param P the source points
	 * @param Q the target points
	 */
	public ProcrustesFit(Point[] P, Point[] Q) {
		this(P, Q, true, true, true);
	}
	
	/**
	 * Full constructor.
	 * @param P the first point sequence
	 * @param Q the second point sequence
	 * @param allowTranslation if {@code true}, translation (t) between point sets is considered, 
	 * 		otherwise zero translation is assumed
	 * @param allowScaling if {@code true}, scaling (s) between point sets is considered, 
	 * 		otherwise unit scale assumed
	 * @param forceRotation if {@code true}, the orthogonal part of the transformation (Q)
	 * 		is forced to a true rotation and no reflection is allowed
	 */
	public ProcrustesFit(Point[] P, Point[] Q, boolean allowTranslation, boolean allowScaling, boolean forceRotation) {
		AffineFit2D.checkSize(P, Q);
		
		double[] meanP = null;
		double[] meanY = null;
		
		if (allowTranslation) {
			meanP = getMeanVec(P);
			meanY = getMeanVec(Q);
		}
		
		RealMatrix vP = makeDataMatrix(P, meanP);
		RealMatrix vQ = makeDataMatrix(Q, meanY);
		MatrixUtils.checkAdditionCompatible(vP, vQ);	// P, Q of same dimensions?
		
		RealMatrix QPt = vQ.multiply(vP.transpose());
		SingularValueDecomposition svd = new SingularValueDecomposition(QPt);
		
		RealMatrix U = svd.getU();
		RealMatrix S = svd.getS();
		RealMatrix V = svd.getV();
			
		double d = (svd.getRank() >= 2) ? det(QPt) : det(U) * det(V);
		
		RealMatrix D = MatrixUtils.createRealIdentityMatrix(2);
		if (d < 0 && forceRotation)
			D.setEntry(1, 1, -1);
		
		R = U.multiply(D).multiply(V.transpose());
		
		double normP = vP.getFrobeniusNorm();
		double normQ = vQ.getFrobeniusNorm();
		
		s = (allowScaling) ? 
				S.multiply(D).getTrace() / sqr(normP) : 1.0;
		
		if (allowTranslation) {
			RealVector ma = MatrixUtils.createRealVector(meanP);
			RealVector mb = MatrixUtils.createRealVector(meanY);
			t = mb.subtract(R.scalarMultiply(s).operate(ma));
		}
		else {
			t = new ArrayRealVector(2);	// zero vector
		}
		
		// TODO: sqrt() seems to be OK! result is same order of magnitude but ca. 50 % of euclidean error
		err = Math.sqrt(sqr(normQ) - sqr(S.multiply(D).getTrace() / normP));	
	}
	
	// -----------------------------------------------------------------
	
	/**
	 * Retrieves the estimated scale.
	 * @return The estimated scale (or 1 if {@code allowscaling = false}).
	 */
	public double getScale() {
		return s;
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
	
	// --------------------------------------------------------
	
	@Override
	public RealMatrix getTransformationMatrix() {
		RealMatrix cR = R.scalarMultiply(s);
		RealMatrix M = MatrixUtils.createRealMatrix(2, 3);
		M.setSubMatrix(cR.getData(), 0, 0);
		M.setColumnVector(2, t);
		return M;
	}
	
	@Override
	public double getError() {
		return err;
	}
	
	/**
	 * Calculates the total error for the estimated fit as
	 * the sum of the squared Euclidean distances between the 
	 * transformed point set X and the reference set Y.
	 * This method is provided for testing as an alternative to
	 * the quicker {@link getError} method.
	 * @param P Sequence of n-dimensional points.
	 * @param Q Sequence of n-dimensional points (reference).
	 * @return The total error for the estimated fit.
	 */
	private double getEuclideanError(Point[] P, Point[] Q) {
		int m = Math.min(P.length,  Q.length);
		RealMatrix cR = R.scalarMultiply(s);
		double errSum = 0;
		for (int i = 0; i < m; i++) {
			RealVector ai = new ArrayRealVector(P[i].toArray());
			RealVector bi = new ArrayRealVector(Q[i].toArray());
			RealVector aiT = cR.operate(ai).add(t);
			double ei = aiT.subtract(bi).getNorm();
			errSum = errSum + ei;
		}
		return errSum;
	}
	
	private double getEuclideanError2(Point[] P, Point[] Q) {	
		return AffineFit2D.getError(P, Q, getTransformationMatrix());
	}
	
	
//	/**
//	 * Returns a 2D {@link AffineMapping} object, as defined in
//	 * {@code imagingbook.pub.geometry.mappings.linear}.
//	 * Throws an exception if the dimensionality of the data
//	 * is not 2.
//	 * @return An affine mapping object.
//	 */
//	public AffineMapping2D getAffineMapping2D() {
//		AffineMapping2D map = new AffineMapping2D(
//				s * R.getEntry(0, 0), s * R.getEntry(0, 1), t.getEntry(0),
//				s * R.getEntry(1, 0), s * R.getEntry(1, 1), t.getEntry(1));
//		return map;	
//	}
	
	// -----------------------------------------------------------------
	
	private double det(RealMatrix M) {
		return new LUDecomposition(M).getDeterminant();
	}
	
	private double[] getMeanVec(Point[] points) {
		//double[] sum = new double[X.get(0).length];
		double sumX = 0;
		double sumY = 0;
		for (Point p : points) {
			sumX = sumX + p.getX();
			sumY = sumY + p.getY();
		}
		return new double[] {sumX / points.length, sumY / points.length};
	}
	
	private RealMatrix makeDataMatrix(Point[] points, double[] meanX) {
		RealMatrix M = MatrixUtils.createRealMatrix(2, points.length);
		RealVector mean = MatrixUtils.createRealVector(meanX);
		int i = 0;
		for (Point p : points) {
			RealVector cv = MatrixUtils.createRealVector(p.toArray());
			if (meanX != null) {
				cv = cv.subtract(mean);
			}
			M.setColumnVector(i, cv);
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
		PrintPrecision.set(6);
		int NDIGITS = 1;
		
		boolean allowTranslation = true;
		boolean allowScaling = true;
		boolean forceRotation = true;
		
		double a = 0.6;
		double[][] R0data =
			{{ Math.cos(a), -Math.sin(a) },
			 { Math.sin(a),  Math.cos(a) }};
		
		RealMatrix R0 = MatrixUtils.createRealMatrix(R0data);
		double[] t0 = {4, -3};
		double s = 3.5;
		
		System.out.format("original alpha: a = %.6f\n", a);
		System.out.println("original rotation: R = \n" + Matrix.toString(R0.getData()));
		System.out.println("original translation: t = " + Matrix.toString(t0));
		System.out.format("original scale: s = %.6f\n", s);
		System.out.println();
		
		Point[] P = {
				Point.create(2, 5),
				Point.create(7, 3),
				Point.create(0, 9),
				Point.create(5, 4)
		};
		
		Point[] Q = new Point[P.length];
		
		for (int i = 0; i < P.length; i++) {
			Point q = Point.create(R0.operate(P[i].toArray()));
			// noise!
			double qx = roundToDigits(s * q.getX() + t0[0], NDIGITS);
			double qy = roundToDigits(s * q.getY() + t0[1], NDIGITS);
			Q[i] = Point.create(qx, qy);
		}
		
		//P[0] = Point.create(2, 0);	// to provoke a large error
		
		ProcrustesFit pf = new ProcrustesFit(P, Q, allowTranslation, allowScaling, forceRotation);

		System.out.format("estimated alpha: a = %.6f\n", Math.acos(pf.getR().getEntry(0, 0)));
		System.out.println("estimated rotation: R = \n" + Matrix.toString(pf.getR().getData()));
		System.out.println("estimated translation: t = " + Matrix.toString(pf.getT().toArray()));
		System.out.format("estimated scale: s = %.6f\n", pf.getScale());
		
		System.out.println();
		System.out.format("fitting error = %.6f\n", pf.getError());
		System.out.format("euclidean error1 = %.6f\n", pf.getEuclideanError(P, Q));
		System.out.format("euclidean error2 = %.6f\n", pf.getEuclideanError2(P, Q));
		
		RealMatrix M = pf.getTransformationMatrix();
		System.out.println("transformation matrix: A = \n" + Matrix.toString(M.getData()));
	}



}

