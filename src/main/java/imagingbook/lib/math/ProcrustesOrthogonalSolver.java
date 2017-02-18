package imagingbook.lib.math;

import java.util.ArrayList;
import java.util.List;

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
		RealMatrix V = svd.getV();
//		System.out.println("U = " + U.toString());
//		System.out.println("V = " + V.toString());
//		System.out.println("S = " + svd.getS().toString());
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
		
		final int m = lA.size();
		final int n = lA.get(0).length;
		
		
		// calcutate centroids of A, B:
		RealVector Ap = new ArrayRealVector(n);
		for (double[] ai : lA) {
			Ap.combineToSelf(1, 1, new ArrayRealVector(ai));
		}
		Ap.mapMultiplyToSelf(1.0/m);
		
		RealVector Bp = new ArrayRealVector(n);
		for (double[] bi : lB) {
			Bp.combineToSelf(1, 1, new ArrayRealVector(bi));
		}
		Bp.mapMultiplyToSelf(1.0/m);
		
		System.out.println("Ap = \n" + Matrix.toString(Ap.toArray()));
		System.out.println("Bp = \n" + Matrix.toString(Bp.toArray()));
		
		
		List<double[]> lAp = new ArrayList<double[]>();
		for (double[] ai : lA) {
			RealVector aip = new ArrayRealVector(ai).subtract(Ap);
			lAp.add(aip.toArray());
		}
		
		List<double[]> lBp = new ArrayList<double[]>();
		for (double[] bi : lB) {
			RealVector bip = new ArrayRealVector(bi).subtract(Bp);
			lBp.add(bip.toArray());
		}	
		
		RealMatrix A = makeMatrix(lAp);
		RealMatrix B = makeMatrix(lBp);
		System.out.println("Ap = \n" + Matrix.toString(A.getData()));
		System.out.println("Bp = \n" + Matrix.toString(B.getData()));
		
		
		
		//ProcrustesOrthogonalSolver ps = new ProcrustesOrthogonalSolver(lA, lB);
		RealMatrix Q = solve(A, B);
		System.out.println("Q = \n" + Matrix.toString(Q.getData()));
		
		RealVector t = Bp.subtract(Q.operate(Ap));
		System.out.println("t = \n" + Matrix.toString(t.toArray()));
		
		RealMatrix II = Q.transpose().multiply(Q);
		System.out.println("Q^T * Q = \n" + Matrix.toString(II.getData()));
		
		// calculate residual
		double err = 0;
		for (int i = 0; i < lA.size(); i++) {
			RealVector ai = new ArrayRealVector(lA.get(i));
			RealVector bi = new ArrayRealVector(lB.get(i));
			double ei = Q.operate(ai).add(t).subtract(bi).getNorm();
			err = err + ei * ei;
		}
		
		System.out.println("error1 = " + err);
		
		double fn = Q.multiply(A).subtract(B).getFrobeniusNorm();
		System.out.println("error2 = " + fn*fn);
		
		double det = new LUDecomposition(Q).getDeterminant();
		System.out.println("det(Q) = " + det);
		
//		RealMatrix Q2 = Q.scalarMultiply(3);
//		double det2 = new LUDecomposition(Q2).getDeterminant();
//		System.out.println("det(Q2) = " + det2);	
	}
	
	public static void main(String[] args) {
		PrintPrecision.set(10);
		example1();
		example2();
		
	}

	
	static double roundToDigits(double x, int k) {
		int d = (int) Math.pow(10, k);
		return Math.rint(x * d) / d;
	}

}
