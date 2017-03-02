package imagingbook.pub.geometry.points;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.lib.math.Matrix;


/**
 * This class implements the Iterative Closest Point algorithm for
 * n-dimensional samples.
 * 
 * @author W. Burger
 *
 */
public class IterativeClosestPointMatcher {
	
	private final double tau;
	private final int kMax;
	private final int mx, my, n;
	private int[] A;
	
	private int k = 0;
	private double eMin = Double.POSITIVE_INFINITY;
	private RealMatrix T;
	private boolean converged = false;
	
	
	public IterativeClosestPointMatcher(List<double[]> X, List<double[]> Y, double tau, int kMax) {
		this.tau = tau;
		this.kMax = kMax;
		this.mx = X.size();
		this.my = Y.size();
		this.n = X.get(0).length;
		this.A = new int[mx];
		
//		System.out.println("X length = " + mx);
//		System.out.println("Y length = " + my);
//		System.out.println("X dimension = " + X.get(0).length);
		
		match(X, Y);
	}
	
	private void match(List<double[]> X, List<double[]> Y) {
		k = 0;
		eMin = Double.POSITIVE_INFINITY;
		T = initialTransformation();
		
		do {
			System.out.println("ITERATION k = " + k);
			A = associatePoints(X, Y);
			double e = fitPoints(X, Y, A);	// sets T
			System.out.println("T = " + Matrix.toString(T.getData()));
			System.out.println("e = " + e);
			double de = eMin - e;
			converged = (0 <= de && de < tau);
			eMin = e;
			k = k + 1;
		} while(!converged && k < kMax);
	}

	private RealMatrix initialTransformation() {
		RealMatrix iT = MatrixUtils.createRealMatrix(n, n + 1);
		iT.setSubMatrix(MatrixUtils.createRealIdentityMatrix(n).getData(), 0, 0);
		return iT;
	}
	
	private int[] associatePoints(List<double[]> X, List<double[]> Y) {
		int[] A = new int[X.size()];
		int i = 0;
		for (double[] xi : X) {
			double[] xiT = T.operate(Matrix.toHomogeneous(xi)); // T.applyTo(xi);
			double dMin = Double.POSITIVE_INFINITY;
			int jMin = 0;
			int j = 0;
			for (double[] yi : Y) {
				double d = Matrix.normL2squared(Matrix.subtract(xiT,yi));
				if (d < dMin) {
					dMin = d;
					jMin = j;
				}
				j = j + 1;
			}
//			System.out.format("  A[%d] = %d, %.3f\n", i, jMin, dMin);
			A[i] = jMin;
			i = i + 1;
		}
		return A;
	}

	private double fitPoints(List<double[]> X, List<double[]> Y, int[] A) {
		List<double[]> YY = new ArrayList<double[]>(A.length);

		for (int i = 0; i < A.length; i++) {
			YY.add(Y.get(A[i]));
		}
		
		ProcrustesFit pf = new ProcrustesFit(X, YY, true, false, true);
		T = pf.getTransformationMatrix();
		System.out.println("EuclideanError = " + pf.getEuclideanError(X, Y));
		return pf.getError();
	}

	public boolean hasConverged() {
		return converged;
	}
	
	public RealMatrix getT() {
		return T;
	}

	public int[] getA() {
		return A;
	}

}
