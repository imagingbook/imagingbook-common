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
	double err1;			// error as defined in Umeyama1991 (Eq. 33)
	double err2;			// true Euclidean error 
	
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
		return err1;
	}
	
	public double getError2() {
		return err2;
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
			throw new IllegalArgumentException("sample lists must have same lengths");
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
		
//		RealMatrix Sigma = B.multiply(A.transpose()).scalarMultiply(1.0/m);
		RealMatrix Sigma = B.multiply(A.transpose());
		
		
		SingularValueDecomposition svd = 
				new SingularValueDecomposition(Sigma);
		printSVD(svd);
		int rank = svd.getRank();
		
		System.out.println("rank of BAt = " + rank + ", full = " + (rank >= n - 1));
		RealMatrix U = svd.getU();
		RealMatrix S = svd.getS();
		RealMatrix V = svd.getV();
		
		System.out.println("det(BAt) = " + det(Sigma));
		System.out.println("det(U) = " + det(U));
		System.out.println("det(V) = " + det(V));
		
		double d = (rank >= n) ?
			det(Sigma) :	// is there a simpler way?
			det(U) * det(V);

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
			t = MatrixUtils.createRealVector(new double[n]);	// zero vector
		}
		
//		err1 = sqr(B.getFrobeniusNorm()) - sqr(D.multiply(S).getTrace()) / sqr(A.getFrobeniusNorm());
		err1 = sqr(B.getFrobeniusNorm()) - sqr(D.multiply(S).getTrace() / (A.getFrobeniusNorm()));
		err2 = calculateEuclideanError(xA, xB);
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
	double calculateEuclideanError(List<double[]> lA, List<double[]> lB) {
		System.out.println("calculateEuclideanError:");
		RealMatrix cQ = Q.scalarMultiply(c);
		double err = 0;
		for (int i = 0; i < lA.size(); i++) {
			RealVector ai = new ArrayRealVector(lA.get(i));
			RealVector bi = new ArrayRealVector(lB.get(i));
			System.out.println("  b" + i + " = " + Matrix.toString(ai.toArray()));
			RealVector aiT = cQ.operate(ai).add(t);
			System.out.println("  a" + i + " = " + Matrix.toString(aiT.toArray()));
			double ei = aiT.subtract(bi).getNorm();
			System.out.println("    d1 = " + ei);
			System.out.println("    d2 = " + sqr(ei));
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

/*

**************** ProcrustesExample1 ****************
original scale = 11.5
original R = 
{{0.825, -0.565}, 
{0.565, 0.825}}
original t = {4.000, -3.000}
meanA = 
{3.500, 5.250}
meanB = 
{3.150, 69.525}
A = 
{{-1.500, 3.500, -3.500, 1.500}, 
{-0.250, -2.250, 3.750, -1.250}}
B = 
{{-12.650, 47.850, -57.550, 22.350}, 
{-12.125, 1.375, 12.875, -2.125}}
------ SVD ---------------
U = {{0.996, -0.095}, 
{-0.095, -0.996}}
S = {{549.137, 0.000}, 
{0.000, 23.028}}
V = {{0.768, -0.640}, 
{-0.640, -0.768}}
--------------------------
rank of BAt = 2, full = true
det(BAt) = 12645.412500000002
det(U) = -1.0000000000000002
det(V) = -1.0
s = 11.500802059812587
Q = 
{{0.825, -0.565}, 
{0.565, 0.825}}
t = 
{4.010, -3.037}
err1 = -24676.3317807789
err2 = 0.002211055276382421
map = 
{{9.493, -6.492, 4.010}, 
{6.492, 9.493, -3.037}, 
{0.000, 0.000, 1.000}}

**************** ProcrustesExample2 ****************
meanA = 
{0.333, 0.667}
meanB = 
{-0.333, 0.667}
A = 
{{-0.333, 0.667, -0.333}, 
{-0.667, -0.667, 1.333}}
B = 
{{0.333, -0.667, 0.333}, 
{-0.667, -0.667, 1.333}}
------ SVD ---------------
U = {{-0.290, -0.957}, 
{-0.957, 0.290}}
S = {{2.869, 0.000}, 
{0.000, 0.465}}
V = {{0.290, 0.957}, 
{-0.957, 0.290}}
--------------------------
rank of BAt = 2, full = true
det(BAt) = -1.3333333333333335
det(U) = -1.0
det(V) = 0.9999999999999999
s = 0.7211102550927977
Q = 
{{0.832, 0.555}, 
{-0.555, 0.832}}
t = 
{-0.800, 0.400}
err1 = -4.088888888888887
err2 = 1.5999999999999999
map = 
{{0.600, 0.400, -0.800}, 
{-0.400, 0.600, 0.400}, 
{0.000, 0.000, 1.000}}
*/
