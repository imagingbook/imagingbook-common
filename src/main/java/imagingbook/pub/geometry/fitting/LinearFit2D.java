package imagingbook.pub.geometry.fitting;

import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.geometry.mappings.linear.LinearMapping2D;

/**
 * Describes a fitter based on a linear transformation model.
 * @author WB
 */
public interface LinearFit2D {
	
	/**
	 * Returns the (3,3) or (2,3) transformation matrix A for this fit, such that
	 * {@code y_i ~ A * x_i} (with {@code x_i} in homogeneous coordinates).
	 * 
	 * @return the transformation matrix for this fit
	 */
	RealMatrix getTransformationMatrix();
	
	/**
	 * Retrieves the total (squared) error for the estimated fit.
	 * @return the fitting error
	 */
	double getError();
	
	
	default public double calculateError(Point[] P, Point[] Q, RealMatrix A) {
		final int m = Math.min(P.length,  Q.length);
		LinearMapping2D map = new LinearMapping2D(A.getData());
		double errSum = 0;
		for (int i = 0; i < m; i++) {
			Point p = P[i];
			Point q = Q[i];
			Point pp = map.applyTo(p);
			double e = Point.distance(q, pp);
			errSum = errSum + e * e;
		}
		return Math.sqrt(errSum);
	}
	
}