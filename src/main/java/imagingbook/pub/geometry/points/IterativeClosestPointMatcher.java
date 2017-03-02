package imagingbook.pub.geometry.points;

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
	private final int[] A;
	
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
		
		System.out.println("X length = " + mx);
		System.out.println("Y length = " + my);
		System.out.println("X dimension = " + X.get(0).length);
		
		match(X, Y);
	}
	
	private void match(List<double[]> X, List<double[]> Y) {
		k = 0;
		eMin = Double.POSITIVE_INFINITY;
		T = initialTransformation();
		
		do {
			System.out.println("k = " + k);
			System.out.println("T = " + Matrix.toString(T.getData()));
			associatePoints(X, Y);
			double e = fitPoints(X, Y);	// sets T
			double de = eMin - e;
			converged = (0 <= de && de < tau);
			eMin = e;
			k = k + 1;
		} while(!converged && k <= kMax);
	}

	private RealMatrix initialTransformation() {
		RealMatrix iT = MatrixUtils.createRealMatrix(n, n + 1);
		iT.setSubMatrix(MatrixUtils.createRealIdentityMatrix(n).getData(), 0, 0);
		return iT;
	}
	
	private void associatePoints(List<double[]> X, List<double[]> Y) {
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
			A[i] = jMin;
			i = i + 1;
		}
		
	}

	private double fitPoints(List<double[]> X, List<double[]> Y) {
		ProcrustesFit pf = new ProcrustesFit(X, Y);
		T = pf.getTransformationMatrix();
		return pf.getError();
	}

	public boolean hasConverged() {
		return converged;
	}

}
