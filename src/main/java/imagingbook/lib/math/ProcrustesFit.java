package imagingbook.lib.math;

import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.pub.geometry.mappings.linear.AffineMapping;


public class ProcrustesFit {
	
	final boolean allowTranslation;
	final boolean allowScaling;
	final boolean forceRotation;
	
	final int m;	// number of samples
	final int n;	// dimension of samples
	
	double c = 1;			// scale
	RealMatrix Q = null;	// orthogonal (rotation) matrix
	RealVector t = null;	// translation vector
	double err;				// total (squared) error
	
	public double getScale() {
		return c;
	}
	
	public RealMatrix getQ() {
		return Q;
	}
	
	public RealVector getT() {
		return t;
	}
	
	public double getError() {
		return err;
	}
	
	public AffineMapping getAffineMapping() {
		if (n != 2)
			return null;
		AffineMapping map = new AffineMapping(
				c * Q.getEntry(0, 0), c * Q.getEntry(0, 1), t.getEntry(0),
				c * Q.getEntry(1, 0), c * Q.getEntry(1, 1), t.getEntry(1),
				false
				);
		return map;	
	}
	
	// --------------------------------------------------------------
	
	public ProcrustesFit(List<double[]> xA, List<double[]> xB) {
		this(xA, xB, true, true, true);
	}
	
	public ProcrustesFit(List<double[]> xA, List<double[]> xB, boolean translation, boolean scaling,
			boolean forceRotation) {
		if (xA.size() != xB.size())
			throw new IllegalArgumentException("point sequences xA, xB must have same length");
		this.allowTranslation = translation;
		this.allowScaling = scaling;
		this.forceRotation = forceRotation;
		this.m = xA.size();
		this.n = xA.get(0).length;
		solve(xA, xB);
	}
	
	private void solve(List<double[]> xA, List<double[]> xB) {
		double[] meanA = null;
		double[] meanB = null;
		
		if (allowTranslation) {
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
		
		if (allowScaling) {
			c = S.multiply(D).getTrace() / sqr(A.getFrobeniusNorm());
		}
		
		if (allowTranslation) {
			RealVector aa = MatrixUtils.createRealVector(meanA);
			RealVector bb = MatrixUtils.createRealVector(meanB);
			t = bb.subtract(Q.scalarMultiply(c).operate(aa));
		}
		else {
			t = new ArrayRealVector(n);	// zero vector
		}
		
		err = sqr(B.getFrobeniusNorm()) - sqr(S.multiply(D).getTrace() / (A.getFrobeniusNorm()));
	}
	
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
	
	// calculate total error (for testing)
	public double getEuclideanError(List<double[]> lA, List<double[]> lB) {
		RealMatrix cQ = Q.scalarMultiply(c);
		double ee = 0;
		for (int i = 0; i < lA.size(); i++) {
			RealVector ai = new ArrayRealVector(lA.get(i));
			RealVector bi = new ArrayRealVector(lB.get(i));
			RealVector aiT = cQ.operate(ai).add(t);
			double ei = aiT.subtract(bi).getNorm();
			ee = ee + sqr(ei);
		}
		return ee;
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

}

