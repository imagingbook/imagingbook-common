package imagingbook.lib.math;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.lib.settings.PrintPrecision;

public class ProcrustesOrthogonalSolver {
	
	static int NDIGITS = 1;
	
	final RealMatrix R;
	
	public ProcrustesOrthogonalSolver(List<double[]> xA, List<double[]> xB) {
		R = solve(makeMatrix(xA), makeMatrix(xB));
	}
	
	public RealMatrix getR() {
		return R;
	}
	
	private static RealMatrix makeMatrix(List<double[]> X) {
		final int m = X.size();
		final int n = X.get(0).length;
		RealMatrix M = MatrixUtils.createRealMatrix(n, m);
		int i = 0;
		for (double[] x : X) {
			M.setColumn(i, x);
			i++;
		}
		return M;
	}
	
	
	public static RealMatrix solve(RealMatrix A, RealMatrix B) {
		MatrixUtils.checkAdditionCompatible(A, B);	// A, B of same dimensions?
		RealMatrix BA = B.multiply(A.transpose());
		SingularValueDecomposition svd = new SingularValueDecomposition(BA);
		RealMatrix U = svd.getU();
		RealMatrix S = svd.getS();
		RealMatrix V = svd.getV();
		System.out.println("------ SVD ---------------");
		System.out.println("U = " + Matrix.toString(U.getData()));
		System.out.println("S = " + Matrix.toString(S.getData()));
		System.out.println("V = " + Matrix.toString(V.getData()));
		System.out.println("--------------------------");
		RealMatrix Q = U.multiply(V.transpose());
		return Q;
	}
	
	// -----------------------------------------------------------
	
	static void example1() {	// orthogonal (rotation only)
		System.out.println("************* Example 1 (orthogonal) ****************");
		double alpha = 0.6;
		
		double[][] Q0 =
			{{ Math.cos(alpha), -Math.sin(alpha) },
			 { Math.sin(alpha),  Math.cos(alpha) }};
		
		RealMatrix R = MatrixUtils.createRealMatrix(Q0);
		System.out.println("original R = \n" + Matrix.toString(R.getData()));
		
		List<double[]> lA = new ArrayList<double[]>();
		List<double[]> lB = new ArrayList<double[]>();
		
		lA.add(new double[] {2, 5});
		lA.add(new double[] {7, 3});
		lA.add(new double[] {0, 9});
		lA.add(new double[] {5, 4});
		
		for (double[] a : lA) {
			double[] b = R.operate(a);
			b[0] = roundToDigits(b[0], NDIGITS);
			b[1] = roundToDigits(b[1], NDIGITS);
			lB.add(b);
		}
		
		RealMatrix A = makeMatrix(lA);
		RealMatrix B = makeMatrix(lB);
		System.out.println("A = \n" + Matrix.toString(A.getData()));
		System.out.println("B = \n" + Matrix.toString(B.getData()));
		
		//ProcrustesOrthogonalSolver ps = new ProcrustesOrthogonalSolver(lA, lB);
		RealMatrix Q = solve(A, B);
		
		System.out.println("Q = \n" + Matrix.toString(Q.getData()));
		
		RealMatrix II = Q.transpose().multiply(Q);
		System.out.println("Q^T * Q = \n" + Matrix.toString(II.getData()));
		
		double fn = Q.multiply(A).subtract(B).getFrobeniusNorm();
		System.out.println("error = " + fn*fn);
		
		double det = new LUDecomposition(Q).getDeterminant();
		System.out.println("det(Q) = " + det);
		
//		RealMatrix Q2 = Q.scalarMultiply(3);
//		double det2 = new LUDecomposition(Q2).getDeterminant();
//		System.out.println("det(Q2) = " + det2);	
	}
	
	
	static void example2() {	// orthogonal (rotation only)
		System.out.println("************* Example 2 (rotation + translation) ****************");
		double alpha = 0.6;
		
		double[][] Q0 =
			{{ Math.cos(alpha), -Math.sin(alpha) },
			 { Math.sin(alpha),  Math.cos(alpha) }};
		
		double[] t0 = {4, -3};
		
		RealMatrix R = MatrixUtils.createRealMatrix(Q0);
		System.out.println("original R = \n" + Matrix.toString(R.getData()));
		
		List<double[]> lA = new ArrayList<double[]>();
		List<double[]> lB = new ArrayList<double[]>();
		
		lA.add(new double[] {2, 5});
		lA.add(new double[] {7, 3});
		lA.add(new double[] {0, 9});
		lA.add(new double[] {5, 4});
		
		for (double[] a : lA) {
			double[] b = R.operate(a);
			b[0] = roundToDigits(b[0] + t0[0], NDIGITS);
			b[1] = roundToDigits(b[1] + t0[1], NDIGITS);
			lB.add(b);
		}
		
		RealMatrix A = makeMatrix(lA);
		RealMatrix B = makeMatrix(lB);
		System.out.println("A = \n" + Matrix.toString(A.getData()));
		System.out.println("B = \n" + Matrix.toString(B.getData()));
		
		RealVector Amean = getMeanColumnVector(A);
		RealVector Bmean = getMeanColumnVector(B);
		
		System.out.println("Amean = \n" + Matrix.toString(Amean.toArray()));
		System.out.println("Bmean = \n" + Matrix.toString(Bmean.toArray()));
		
		// make A'
		RealMatrix Ap = subtractColumnVector(A, Amean);
		System.out.println("Ap = \n" + Matrix.toString(Ap.getData()));
		
		// make B'
		RealMatrix Bp = subtractColumnVector(B, Bmean);
		System.out.println("Bp = \n" + Matrix.toString(Bp.getData()));
		
		
		RealMatrix Q = solve(Ap, Bp);
		System.out.println("Q = \n" + Matrix.toString(Q.getData()));
		
		RealVector t = Bmean.subtract(Q.operate(Amean));
		System.out.println("t = \n" + Matrix.toString(t.toArray()));
		
		RealMatrix II = Q.transpose().multiply(Q);
		System.out.println("Q^T * Q = \n" + Matrix.toString(II.getData()));
		
		// calculate residual error
		double err = 0;
		for (int i = 0; i < lA.size(); i++) {
			RealVector ai = new ArrayRealVector(lA.get(i));
			RealVector bi = new ArrayRealVector(lB.get(i));
			double ei = Q.operate(ai).add(t).subtract(bi).getNorm();
			err = err + ei * ei;
		}
		
		System.out.println("error1 = " + err);
		
		double fn = Q.multiply(Ap).subtract(Bp).getFrobeniusNorm();
		System.out.println("error2 = " + fn*fn);
		
		double det = new LUDecomposition(Q).getDeterminant();
		System.out.println("det(Q) = " + det);	
	}
	
	
	static void example3() {	// orthogonal (rotation only)
		System.out.println("************* Example 3 (rotation + translation + scaling) ****************");
		double alpha = 0.6;	// rotation angle
		System.out.println("original alpha = " + alpha);
		
		double scale = 3.5;
		System.out.println("original scale = " + scale);
		
		
		double[][] Q0 =
			{{ Math.cos(alpha), -Math.sin(alpha) },
			 { Math.sin(alpha),  Math.cos(alpha) }};
		
		double[] t0 = {4, -3};
		
		RealMatrix R = MatrixUtils.createRealMatrix(Q0);
		System.out.println("original Q0 = \n" + Matrix.toString(R.getData()));
		
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
		
		// ----- solution ------
		
		RealMatrix A = makeMatrix(lA);
		RealMatrix B = makeMatrix(lB);
		System.out.println("A = \n" + Matrix.toString(A.getData()));
		System.out.println("B = \n" + Matrix.toString(B.getData()));
		
		RealVector Amean = getMeanColumnVector(A);
		RealVector Bmean = getMeanColumnVector(B);
		
		System.out.println("Amean = \n" + Matrix.toString(Amean.toArray()));
		System.out.println("Bmean = \n" + Matrix.toString(Bmean.toArray()));
		
		// make A'
		RealMatrix Ap = subtractColumnVector(A, Amean);
		System.out.println("Ap = \n" + Matrix.toString(Ap.getData()));
		
		// make B'
		RealMatrix Bp = subtractColumnVector(B, Bmean);
		System.out.println("Bp = \n" + Matrix.toString(Bp.getData()));
		
		// calculate common scale factor
//		double s = getScale(Ap, Bp);
		double s = Bp.getFrobeniusNorm() / Ap.getFrobeniusNorm();
		System.out.println("s = " + s); 
		
		RealMatrix Q = solve(Ap, Bp);
		System.out.println("Q = \n" + Matrix.toString(Q.getData()));
		
		RealMatrix sQ = Q.scalarMultiply(s);
		
		RealVector t = Bmean.subtract(sQ.operate(Amean));
		System.out.println("t = \n" + Matrix.toString(t.toArray()));
		
		// check if Q is orthogonal
		RealMatrix II = Q.transpose().multiply(Q);
		System.out.println("Q^T * Q = \n" + Matrix.toString(II.getData()));
		
		// calculate residual error
		double err = 0;
		for (int i = 0; i < lA.size(); i++) {
			RealVector ai = new ArrayRealVector(lA.get(i));
			RealVector bi = new ArrayRealVector(lB.get(i));
			double ei = sQ.operate(ai).add(t).subtract(bi).getNorm();
			err = err + ei * ei;
		}
		System.out.println("error1 = " + err);
		
//		double fn = Q.multiply(Ap).subtract(Bp).getFrobeniusNorm();
//		System.out.println("error2 = " + fn*fn);
		
		double det = new LUDecomposition(Q).getDeterminant();
		System.out.println("det(Q) = " + det);
		
	}

//	static RealVector getMeanColumnVector(RealMatrix M) {
//		final int ncols = M.getColumnDimension();
//		RealVector mean = M.getColumnVector(0);
//		for (int i = 1; i < ncols; i++) {
//			mean.combineToSelf(1, 1, M.getColumnVector(i));
//		}
//		mean.mapDivideToSelf(ncols);
//		return mean;
//	}
	
	static RealVector getMeanColumnVector(RealMatrix M) {
		double[] colSum = Matrix.sumColumns(M.getData());
		final int ncols = M.getColumnDimension();
//		RealVector mean = M.getColumnVector(0);
//		for (int i = 1; i < ncols; i++) {
//			mean.combineToSelf(1, 1, M.getColumnVector(i));
//		}
//		mean.mapDivideToSelf(ncols);
		Matrix.multiplyD(1.0/ncols, colSum);
		return new ArrayRealVector(colSum);
	}
	
	static RealMatrix subtractColumnVector(RealMatrix M, RealVector cv) {
		final int ncols = M.getColumnDimension();
		RealMatrix MM = M.copy();
		for (int i = 0; i < ncols; i++) {
			MM.setColumnVector(i, M.getColumnVector(i).subtract(cv));
		}
		return MM;
	}
	
	static RealMatrix subtractColumnVector2(RealMatrix M, RealVector cv) {
		final int ncols = M.getColumnDimension();
		if (M.getRowDimension() != cv.getDimension()) {
			throw new DimensionMismatchException(cv.getDimension(), ncols);
		}
		RealMatrix MM = M.copy();
		for (int i = 0; i < ncols; i++) {
			MM.setColumnVector(i, M.getColumnVector(i).subtract(cv));
		}
		return MM;
	}
	
	static double getScale(RealMatrix A, RealMatrix B) {
		double[][] a = A.getData();
		double[][] b = B.getData();
		int m = Matrix.getNumberOfColumns(a);
		int n = Matrix.getNumberOfRows(a);
//		System.out.println("    m = " + a[0].length);
//		System.out.println("    n = " + a.length);
		double sumA = 0;
		double sumB = 0;
		for (int r = 0; r < n; r++) {
			for (int c = 0; c < m; c++) {
				sumA = sumA + a[r][c] * a[r][c];
				sumB = sumB + b[r][c] * b[r][c];
			}
		}
		return Math.sqrt(sumB / sumA);
	}
	
	static double roundToDigits(double x, int k) {
		int d = (int) Math.pow(10, k);
		return Math.rint(x * d) / d;
	}
	
	public static void main(String[] args) {
		PrintPrecision.set(10);
//		example1();
//		example2();
		example3();
	}

}
