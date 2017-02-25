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
	final boolean allowReflection;
	
	final int m;	// number of samples
	final int n;	// dimension of samples
	
	double s = 1;			// scale
	RealMatrix Q = null;	// orthogonal (rotation) matrix
	RealVector t = null;	// translation vector
	double err1, err2;
	
	public double getS() {
		return s;
	}
	
	public RealMatrix getQ() {
		return Q;
	}
	
	public RealVector getT() {
		return t;
	}
	
	public double getError() {
		return err1;
	}
	
	public double getError2() {
		return err2;
	}
	
	public AffineMapping getAffineMapping() {
		if (n != 2)
			return null;
		AffineMapping map = new AffineMapping(
				s * Q.getEntry(0, 0), s * Q.getEntry(0, 1), t.getEntry(0),
				s * Q.getEntry(1, 0), s * Q.getEntry(1, 1), t.getEntry(1),
				false
				);
		return map;	
	}
	
	// --------------------------------------------------------------
	
	public ProcrustesFit(List<double[]> xA, List<double[]> xB) {
		this(xA, xB, true, true, false);
	}
	
	public ProcrustesFit(List<double[]> xA, List<double[]> xB, boolean translation, boolean scaling,
			boolean allowReflection) {
		if (xA.size() != xB.size())
			throw new IllegalArgumentException("sample lists must have same lengths");
		this.allowTranslation = translation;
		this.allowScaling = scaling;
		this.allowReflection = allowReflection;
		this.m = xA.size();
		this.n = xA.get(0).length;
		solve(xA, xB);
	}
	
	private void solve(List<double[]> xA, List<double[]> xB) {
		double[] meanA = null;
		double[] meanB = null;
		RealMatrix A, B;
		if (allowTranslation) {
			meanA = getMeanVec(xA);
			meanB = getMeanVec(xB);
			System.out.println("meanA = \n" + Matrix.toString(meanA));
			System.out.println("meanB = \n" + Matrix.toString(meanB));
			A = makeDataMatrix(xA, meanA);
			B = makeDataMatrix(xB, meanB);
		}
		else {
			A = makeDataMatrix(xA);
			B = makeDataMatrix(xB);
		}
		
		System.out.println("A = \n" + Matrix.toString(A.getData()));
		System.out.println("B = \n" + Matrix.toString(B.getData()));
		
		MatrixUtils.checkAdditionCompatible(A, B);	// A, B of same dimensions?
		
		RealMatrix BAt = B.multiply(A.transpose());
		
		
		SingularValueDecomposition svd = 
				new SingularValueDecomposition(BAt);
		printSVD(svd);
		int rank = svd.getRank();
		
		System.out.println("rank of BAt = " + rank + ", full = " + (rank >= n - 1));
		RealMatrix U = svd.getU();
		RealMatrix D = svd.getS();
		RealMatrix V = svd.getV();
		
		System.out.println("det(BAt) = " + det(BAt));
		System.out.println("det(U) = " + det(U));
		System.out.println("det(V) = " + det(V));
		
		double d = (rank >= n - 1) ?
			det(BAt) :	// is there a simpler way?
			det(U) * det(V);

		RealMatrix S = MatrixUtils.createRealIdentityMatrix(n);
		if (!allowReflection && d < 0)
			S.setEntry(n - 1, n - 1, -1);
		
		Q = U.multiply(S).multiply(V.transpose());
		
		if (allowScaling) {
//			s = B.getFrobeniusNorm() / A.getFrobeniusNorm();
			s = D.multiply(S).getTrace() / sqr(A.getFrobeniusNorm());
		}
		
		if (allowTranslation) {
			RealVector aa = MatrixUtils.createRealVector(meanA);
			RealVector bb = MatrixUtils.createRealVector(meanB);
			t = bb.subtract(Q.scalarMultiply(s).operate(aa));
		}
		else {
			t = MatrixUtils.createRealVector(new double[n]);	// zero vector
		}
		
		err1 = sqr(B.getFrobeniusNorm()) - sqr(D.multiply(S).getTrace()) / sqr(A.getFrobeniusNorm());
		err2 = calculateError(xA, xB);
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
	
	// calculate residual error
	double calculateError(List<double[]> lA, List<double[]> lB) {
		RealMatrix sQ = Q.scalarMultiply(s);
		double err = 0;
		for (int i = 0; i < lA.size(); i++) {
			RealVector ai = new ArrayRealVector(lA.get(i));
			RealVector bi = new ArrayRealVector(lB.get(i));
			RealVector aiT = sQ.operate(ai).add(t);
			double ei = aiT.subtract(bi).getNorm();
			err = err + sqr(ei);
		}
		return err;
	}
	
	private static RealMatrix makeDataMatrix(List<double[]> X) {
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
	
	private static RealMatrix makeDataMatrix(List<double[]> X, double[] meanX) {
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
