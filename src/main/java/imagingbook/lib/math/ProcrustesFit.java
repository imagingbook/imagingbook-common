package imagingbook.lib.math;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.pub.geometry.mappings.linear.AffineMapping;



public class ProcrustesFit {
	
	final boolean allowTranslation;
	final boolean allowScaling;
	
	final int m;	// number of samples
	final int n;	// dimension of samples
	
	double s = 1;			// scale
	RealMatrix Q = null;	// orthogonal (rotation) matrix
	RealVector t = null;	// translation vector
	
	public double getS() {
		return s;
	}
	
	public RealMatrix getQ() {
		return Q;
	}
	
	public RealVector getT() {
		return t;
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
		this(xA, xB, true, true);
	}
	
	public ProcrustesFit(List<double[]> xA, List<double[]> xB, boolean translation, boolean scaling) {
		if (xA.size() != xB.size())
			throw new IllegalArgumentException("sample lists must have same lengths");
		this.allowTranslation = translation;
		this.allowScaling = scaling;
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
		
		if (allowScaling) {
			s = B.getFrobeniusNorm() / A.getFrobeniusNorm();
		}
		
		SingularValueDecomposition svd = 
				new SingularValueDecomposition(B.multiply(A.transpose()));
		printSVD(svd);
		RealMatrix U = svd.getU();
		RealMatrix V = svd.getV();
		Q = U.multiply(V.transpose());
		
		if (allowTranslation) {
			RealVector aa = MatrixUtils.createRealVector(meanA);
			RealVector bb = MatrixUtils.createRealVector(meanB);
			t = bb.subtract(Q.operate(aa).mapMultiplyToSelf(s));
		}
		else {
			t = MatrixUtils.createRealVector(new double[n]);	// zero vector
		}
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
	
	// --------------------------------------------------------------------
	
	public static void main(String[] args) {
		int NDIGITS = 1;
		
		double scale = 11.5;
		System.out.println("original scale = " + scale);
		
		double alpha = 0.6;
		double[][] Q0 =
			{{ Math.cos(alpha), -Math.sin(alpha) },
			 { Math.sin(alpha),  Math.cos(alpha) }};
		
		RealMatrix R = MatrixUtils.createRealMatrix(Q0);
		System.out.println("original R = \n" + Matrix.toString(R.getData()));
		
		double[] t0 = {4, -3};
		
		List<double[]> lA = new ArrayList<double[]>();
		List<double[]> lB = new ArrayList<double[]>();
		
		lA.add(new double[] {2, 5});
		lA.add(new double[] {7, 3});
		lA.add(new double[] {0, 9});
		lA.add(new double[] {5, 4});
		
		for (double[] a : lA) {
			double[] b = R.operate(a);
			b[0] = roundToDigits(scale * b[0] + t0[0], NDIGITS);
			b[1] = roundToDigits(scale * b[1] + t0[1], NDIGITS);
			lB.add(b);
		}
		
		ProcrustesFit solver = new ProcrustesFit(lA, lB);
		
		System.out.println("s = " + solver.getS());
		System.out.println("Q = \n" + Matrix.toString(solver.getQ().getData()));
		System.out.println("t = \n" + Matrix.toString(solver.getT().toArray()));
		
		AffineMapping map = solver.getAffineMapping();
		System.out.println("map = \n" + map.toString());
	}
	
	private static double roundToDigits(double x, int ndigits) {
		int d = (int) Math.pow(10, ndigits);
		return Math.rint(x * d) / d;
	}

}
