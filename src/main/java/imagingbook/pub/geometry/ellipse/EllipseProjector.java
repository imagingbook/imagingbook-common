package imagingbook.pub.geometry.ellipse;

import static imagingbook.lib.math.Arithmetic.sqr;
import static imagingbook.lib.math.Matrix.add;
import static imagingbook.lib.math.Matrix.multiply;
import static imagingbook.lib.math.Matrix.subtract;
import static imagingbook.lib.math.Matrix.transpose;
import static java.lang.Math.abs;
import static java.lang.Math.copySign;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import imagingbook.pub.geometry.basic.Pnt2d;


/*
 * Abstract superclass of ellipse projectors, used to find the
 * closest "contact" point on an ellipse for some given target point.
 * Defines specific methods for calculating the distance only, 
 * without returning the point itself.
 */
public abstract class EllipseProjector {
	
	protected final double ra, rb, ra2, rb2;
	protected final double[] xc;
	protected final double[][] R, Rt;			// rotation matrix
	
	protected EllipseProjector(GeometricEllipse ellipse) {
		this.xc = new double[] {ellipse.xc, ellipse.yc};
		this.ra = ellipse.ra;
		this.rb = ellipse.rb;
		this.ra2 = sqr(ra);
		this.rb2 = sqr(rb);
		this.R = new double[][]    // rotation matrix
				{{ cos(ellipse.theta), -sin(ellipse.theta) }, 
				 { sin(ellipse.theta),  cos(ellipse.theta) }};
		this.Rt = transpose(R);
	}
	
	// methods to be implemented by subclasses ----------------------------
	
	/**
	 * Calculates the projection point in canonical coordinates.
	 * 
	 * @param u1 target point in canonical coordinates.
	 * @return the associated "contact" point on the ellipse
	 */
	protected abstract double[] projectCanonical(double[] u1);
	
	// -------------------------------------------------------------------
	
	/**
	 * Projects the specified point onto the associated ellipse.
	 * 
	 * @param x the 2D point to be projected
	 * @return the closest point on the ellipse
	 */
//	public abstract double[] project(double[] xy);
	public double[] project(double[] x) {
		double[] u = toCanonicalFrame(x); 					// target point in u/v coordinates
		double[] u1 = toFirstQuadrant(u);					// target point in quadrant 1
		double[] ub1 = projectCanonical(u1);				// contact point in quadrant 1
		double[] ub = fromFirstQuadrant(ub1, u);			// contact point in u/v coordinates
		return this.fromCanonicalFrame(ub);					// contact point in x/y coordinates
	}
	
	/**
	 * Calculates the distance to the closest ellipse point
	 * (but not the point itself).
	 * @param x the 2D point to be projected
	 * @return the distance to the closest ellipse point
	 */
	public double getDistance(double[] x) {
		return Math.sqrt(getDistanceSq(x));			// d = ||u1 - ub1||
	}
	
	public double getDistanceSq(double[] x) {
		double[] u = toCanonicalFrame(x); 					// target point in u/v coordinates
		double[] u1 = toFirstQuadrant(u);					// target point in quadrant 1
		double[] ub1 = projectCanonical(u1);				// contact point in quadrant 1
		return sqr(u1[0] - ub1[0]) + sqr(u1[1] - ub1[1]);	// = ||u1 - ub1||^2
//		return Matrix.normL2squared(Matrix.subtract(u1, ub1));
	}
	
	/**
	 * Projects the specified point onto the associated ellipse.
	 * 
	 * @param pnt the 2D point to be projected
	 * @return the closest point on the ellipse
	 */
	public Pnt2d project(Pnt2d pnt) {
		return Pnt2d.from(project(pnt.toDoubleArray()));
	}
	
	public double[] toCanonicalFrame(double[] xy) {
		return multiply(Rt, subtract(xy, xc)); // point in canonical coordinates
	}
	
	public double[] fromCanonicalFrame(double[] uv) {
		return add(multiply(R, uv), xc);
	}
	
	public double[] toFirstQuadrant(double[] uv) {
		return new double[] {abs(uv[0]), abs(uv[1])};
	}
	
	public double[] fromFirstQuadrant(double[] uv, double[] uvOrig) {
		return new double[] {copySign(uv[0], uvOrig[0]), copySign(uv[1], uvOrig[1])};
	}

}