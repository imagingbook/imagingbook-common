package imagingbook.pub.corners.subpixel;

import static imagingbook.lib.math.Arithmetic.isZero;

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
 * @version 2020/10/03
 *
 */
public class MaxLocatorParabolic implements MaxLocator {
	
	private final double[] c = new double[5];	// polynomial coefficients
	
	// -----------------------------------------------------------
	
	public MaxLocatorParabolic() {
	}
	
	// -----------------------------------------------------------
	
	@Override
	public float[] locateMaximum(float[] s) {
		c[0] = s[0];
		c[1] = (s[1] - s[5]) / 2;
		c[2] = (s[7] - s[3]) / 2;
		c[3] = (s[1] - 2 * s[0] + s[5]) / 2;
		c[4] = (s[3] - 2 * s[0] + s[7]) / 2;
		
		if (isZero(c[3]) || isZero(c[4])) {
			return null;
		}
		
		float x = (float) (-0.5 * c[1] / c[3]);
		float y = (float) (-0.5 * c[2] / c[4]);
		float z = (float) (c[0] + c[1] * x + c[2] * y + c[3] * x * x + c[4] * y * y);
		return new float[] {x, y, z};
	}
	
// ---------------------------------------------------------

	public static void main(String[] args) {
		Method m = MaxLocator.Method.Parabolic;
		
		PrintPrecision.set(8);
		float[] samples = {16,9,7,11,8,15,14,12,10}; // = s_0,...,s_8, result xyz = {-0.40573445, 0.11285823, 16.62036324}
//		float[] samples = {40229.785156f, 33941.535156f, 25963.150391f, 39558.175781f, 39078.843750f, 33857.863281f, 39861.664063f, 38746.250000f, 33652.839844f};

		MaxLocator interp = m.getInstance(); //new SubPixelParabolic();
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
