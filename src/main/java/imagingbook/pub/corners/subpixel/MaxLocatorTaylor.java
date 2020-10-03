package imagingbook.pub.corners.subpixel;

import static imagingbook.lib.math.Arithmetic.isZero;

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
 * @version 2020/10/03
 *
 */
public class MaxLocatorTaylor implements MaxLocator {
	
	private final double[] c = new double[6];	// polynomial coefficients
	
	// -----------------------------------------------------------
	
	public MaxLocatorTaylor() {
	}
	
	// -----------------------------------------------------------
	
	@Override
	public float[] locateMaximum(float[] s) {
		c[0] = s[0];
		c[1] = (s[1] - s[5]) / 2;
		c[2] = (s[7] - s[3]) / 2;
		c[3] = (s[1] - 2*s[0] + s[5]) / 2;
		c[4] = (s[3] - 2*s[0] + s[7]) / 2;
		c[5] = (s[4] + s[8] - s[2] - s[6]) / 4;
		
		double d = (4*c[3]*c[4] - c[5]*c[5]);
		if (isZero(d)) {
			return null;
		}
		
		// get max position:
		double a = 1.0 / d;
		float x = (float) (a*(c[2]*c[5] - 2*c[1]*c[4]));
		float y = (float) (a*(c[1]*c[5] - 2*c[2]*c[3]));
		// get max value:
		float z = (float) (c[0] + c[1]*x + c[2]*y + c[3]*x*x + c[4]*y*y + c[5]*x*y);
		return new float[] {x, y, z};
	}
	
	// ---------------------------------------------------------

	public static void main(String[] args) {
		Method m = MaxLocator.Method.Taylor;
		
		PrintPrecision.set(8);
		float[] samples = {16,9,7,11,8,15,14,12,10}; // = s_0,...,s_8, result xyz = {-0.40573445, 0.11285823, 16.62036324}
//		float[] samples = {40229.785156f, 33941.535156f, 25963.150391f, 39558.175781f, 39078.843750f, 33857.863281f, 39861.664063f, 38746.250000f, 33652.839844f};

		MaxLocator interp = m.getInstance(); //new SubPixelTaylor();
		System.out.println("interpolator = " + interp.getClass().getSimpleName());
		
		float[] xyz = interp.locateMaximum(samples);
		if (xyz != null) {
			System.out.println("xyz = " + Matrix.toString(xyz));
		}
		else {
			System.out.println("*** Max could not be located! ***");
		}
	}


}
