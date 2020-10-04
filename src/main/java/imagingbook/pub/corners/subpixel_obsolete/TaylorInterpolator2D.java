package imagingbook.pub.corners.subpixel_obsolete;

import imagingbook.lib.math.Matrix;
import imagingbook.lib.settings.PrintPrecision;

/**
 * Second-order Taylor interpolator. 
 * Samples s0,...,s8 must be arranged in the following order:
 * <pre>
 *    s4 s3 s2
 *    s5 s0 s1
 *    s6 s7 s8 </pre>
 * All sample values are used.
 * @author WB
 * @version 2020/09/29
 * @deprecated
 */
public class TaylorInterpolator2D implements PolynomialInterpolator2D {
	
	private final double[] c;	// polynomial coefficients
	
	public TaylorInterpolator2D(float[] s) {
		c = new double[6];
		c[0] = s[0];
		c[1] = (s[1] - s[5]) / 2;
		c[2] = (s[7] - s[3]) / 2;
		c[3] = (s[1] - 2*s[0] + s[5]) / 2;
		c[4] = (s[3] - 2*s[0] + s[7]) / 2;
		c[5] = (s[4] + s[8] - s[2] - s[6]) / 4;
	}
	
	// ---------------------------------------------------------

	@Override
	public double[] getCoefficients() {
		return c;
	}

	@Override
	public double[] getGradient(double x, double y) {
		double gx = c[1] + 2*c[3]*x + c[5]*y;
		double gy = c[2] + c[5]*x + 2*c[4]*y;
		return new double[] {gx, gy};
	}

	@Override
	public double[][] getHessian(double x, double y) {
		double[][] H = {
				{2*c[3], c[5]}, 
				{c[5], 2*c[4]}};
		return H;
	}
	
	@Override
	public float getInterpolatedValue(double x, double y) {
		return (float) (c[0] + c[1]*x + c[2]*y + c[3]*x*x + c[4]*y*y + c[5]*x*y);
	}

	@Override
	public float[] getMaxPosition() {
		double a = 1.0 / (4*c[3]*c[4] - c[5]*c[5]);
		float xMax = (float) (a*(c[2]*c[5] - 2*c[1]*c[4]));
		float yMax = (float) (a*(c[1]*c[5] - 2*c[2]*c[3]));
		return new float[] {xMax, yMax};
	}
	
	// ---------------------------------------------------------

	public static void main(String[] args) {
		PrintPrecision.set(5);
		float[] samples = {16,9,7,11,8,15,14,12,10}; // = s_0,...,s_8
		TaylorInterpolator2D interp = new TaylorInterpolator2D(samples);
		
		System.out.println(TaylorInterpolator2D.class.getName());
		System.out.println("c = " + Matrix.toString(interp.getCoefficients()));
		
		double[][] iVals = new double[3][3];
		for (int v = -1; v <= 1; v++) {
			for (int u = -1; u <= 1; u++) {
				iVals[v+1][u+1] = interp.getInterpolatedValue(u, v);
			}
		}
		System.out.println("iVals = " + Matrix.toString(iVals));
		
		double[] g = interp.getGradient(0, 0);
		System.out.println("g(0,0) = " + Matrix.toString(g));
		System.out.println("H(0,0) = " + Matrix.toString(interp.getHessian(0, 0)));
		
		float[] pMax = interp.getMaxPosition();
		System.out.println("pMax = " + Matrix.toString(interp.getMaxPosition()));
		System.out.println("vMax = " + interp.getInterpolatedValue(pMax[0], pMax[1]));

	}
}
