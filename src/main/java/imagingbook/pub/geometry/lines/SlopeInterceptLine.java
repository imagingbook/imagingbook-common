package imagingbook.pub.geometry.lines;

import imagingbook.pub.geometry.basic.Pnt2d;

/**
 * This class represents a line in slope-intercept form: y = A x + C.
 * Instances are immutable. Not all possible lines in the 2D plane
 * can be represented.
 *
 */
public class SlopeInterceptLine {
	
	private final double A, C;

	public SlopeInterceptLine(double A, double C) {
		this.A = A;
		this.C = C;
	}
	
	public double getA() {
		return A;
	}

	public double getC() {
		return C;
	}
	
	public double[] getParameters() {
		return new double[] {A, C};
	}
	
}
