package imagingbook.pub.geometry.icp;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.fitting.LinearFit;
import imagingbook.pub.geometry.fitting.ProcrustesFit;


/**
 * This class implements the Iterative Closest Point algorithm for
 * n-dimensional samples.
 * 
 * TODO: change to use a variable fitter (currently ProcrustesFit only).
 * 
 * @author W. Burger
 * @version 2017/03/21
 */
public class IterativeClosestPointMatcher {
	
	/**
	 * An instance of a class implementing this interface may be passed to the constructor
	 * of {@link IterativeClosestPointMatcher}; its {@code notify()} method
	 * is invoked after every iteration of the matcher for debugging
	 * or visualizing intermediate results.
	 */
	public interface IterationListener extends EventListener {
		public void notify(IterativeClosestPointMatcher matcher);
	}
	
	private final double tau;
	private final int kMax;
	private final int m, n;
	private int[] A;
	
	private int k = 0;
	private double eMin = Double.POSITIVE_INFINITY;
	private RealMatrix T;
	private boolean converged = false;
	
	private final IterationListener cb;
	private final List<double[]> X, Y;
	private final LinearFit fitter;
	
	public IterativeClosestPointMatcher(List<double[]> X, List<double[]> Y, 
			double tau, int kMax, LinearFit fitter, IterationListener cb) {
		this.X = X;
		this.Y = Y;
		this.tau = tau;
		this.kMax = kMax;
		this.fitter = fitter;
		this.cb = cb;
		
		this.m = X.size();
		this.n = X.get(0).length;
		this.A = new int[m];
		
		match();
	}
	
	private void match() {
		k = 0;
		eMin = Double.POSITIVE_INFINITY;
		T = initialTransformation();
		do {
			System.out.println("ITERATION k = " + k);
			A = associatePoints();
			double e = fitPoints(A);	// sets T
			System.out.println("T = " + Matrix.toString(T.getData()));
			System.out.println("e = " + e);
			double de = eMin - e;
			eMin = e;
			k = k + 1;
			converged = (0 <= de && de < tau);
			if (cb != null) 
				cb.notify(this);
		} while(!converged && k < kMax);
	}

	private RealMatrix initialTransformation() {
		RealMatrix iT = MatrixUtils.createRealMatrix(n, n + 1);
		iT.setSubMatrix(MatrixUtils.createRealIdentityMatrix(n).getData(), 0, 0);
		return iT;
	}
	
	private int[] associatePoints() {
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

	private double fitPoints(int[] A) {
		List<double[]> YY = new ArrayList<double[]>(A.length);

		for (int i = 0; i < A.length; i++) {
			YY.add(Y.get(A[i]));
		}
		
		ProcrustesFit pf = new ProcrustesFit(true, false, true);
		pf.fit(X, YY);
		
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
	
	public int getIteration() {
		return k;
	}

}
