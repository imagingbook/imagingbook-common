package imagingbook.pub.geometry.points;

import java.util.List;

import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.mappings.linear.AffineMapping;
import imagingbook.pub.geometry.mappings.linear.LinearMapping;

public class IterativeClosestPointMatcher {
	
	private final double tau;
	private final int kMax;
	private final int m, n;
	private final int[] A;
	
	private int k = 0;
	private double eMin = Double.POSITIVE_INFINITY;
	private LinearMapping T;
	
	
	public IterativeClosestPointMatcher(List<double[]> X, List<double[]> Y, double tau, int kMax) {
		this.tau = tau;
		this.kMax = kMax;
		this.m = X.size();
		this.n = Y.size();
		this.A = new int[m];
		
		match(X, Y);
	}
	
	private void match(List<double[]> X, List<double[]> Y) {
		k = 0;
		boolean converged;
		
		eMin = Double.POSITIVE_INFINITY;
		T = initialTransformation();
		do {
			associatePoints(X, Y);
			double e = fitPoints(X, Y);
			double de = eMin - e;
			converged = (0 <= de && de < tau);
			eMin = e;
			k = k + 1;
		} while(!converged && k <= kMax);
	}

	private LinearMapping initialTransformation() {
		return new AffineMapping();
	}
	
	private void associatePoints(List<double[]> X, List<double[]> Y) {
		int i = 0;
		for (double[] xi : X) {
			double[] xiT = T.applyTo(xi);
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
		// TODO Auto-generated method stub
		return 0;
	}


}
