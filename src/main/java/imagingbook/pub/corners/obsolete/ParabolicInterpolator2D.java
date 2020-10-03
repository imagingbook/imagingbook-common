package imagingbook.pub.corners.obsolete;

import imagingbook.lib.math.Matrix;
import imagingbook.lib.settings.PrintPrecision;

/**
 * 2D interpolator based on fitting a quadratic polynomial (parabolic function) to the supplied samples.
 * Samples s0,...,s8 must be arranged in the following order:
 * <pre>
 *    s4 s3 s2
 *    s5 s0 s1
 *    s6 s7 s8 </pre>
 * Only 5 of the 9 supplied sample values are used (the corner samples are ignored).
 * @author WB
 * @version 2020/09/29
 * @deprecated
 */
public class ParabolicInterpolator2D implements PolynomialInterpolator2D {
	
	private final double[] c;	// polynomial coefficients
	
	public ParabolicInterpolator2D(float[] s) {
		c = new double[5];
		c[0] = s[0];
		c[1] = (s[1] - s[5]) / 2;
		c[2] = (s[7] - s[3]) / 2;
		c[3] = (s[1] - 2 * s[0] + s[5]) / 2;
		c[4] = (s[3] - 2 * s[0] + s[7]) / 2;
		// TODO: check for zero values of c[3], c[4]
	}
	
	// ---------------------------------------------------------

	@Override
	public double[] getCoefficients() {
		return c;
	}

	@Override
	public double[] getGradient(double x, double y) {
		double gx = c[1] + 2 * c[3] * x;
		double gy = c[2] + 2 * c[4] * y;
		return new double[] {gx, gy};
	}

	@Override
	public double[][] getHessian(double x, double y) {
		double[][] H = {
				{2 * c[3], 0}, 
				{0, 2 * c[4]}
				};
		return H;
	}
	
	@Override
	public float getInterpolatedValue(double x, double y) {
		return (float) (c[0] + c[1] * x + c[2] * y + c[3] * x * x + c[4] * y * y);
	}

	@Override
	public float[] getMaxPosition() {
		float xMax = (float) (-0.5 * c[1] / c[3]);
		float yMax = (float) (-0.5 * c[2] / c[4]);
		return new float[] {xMax, yMax};
	}
	
	// ---------------------------------------------------------

	public static void main(String[] args) {
		PrintPrecision.set(5);
		float[] samples = {16,9,7,11,8,15,14,12,10}; // = s_0,...,s_8
		ParabolicInterpolator2D interp = new ParabolicInterpolator2D(samples);
		
		System.out.println(ParabolicInterpolator2D.class.getName());
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
