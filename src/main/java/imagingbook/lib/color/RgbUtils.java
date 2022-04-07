package imagingbook.lib.color;

import java.awt.Color;

import imagingbook.lib.math.Matrix;

public abstract class RgbUtils {

	public static int[] intToRgb(int p) {
		int[] RGB = new int[3];
		intToRgb(p, RGB);
		return RGB;
	}
	
	public static void intToRgb(int p, int[] RGB) {
		RGB[0] = ((p >> 16) & 0xFF);
		RGB[1] = ((p >> 8) & 0xFF);
		RGB[2] = (p & 0xFF);
	}

	public static int rgbToInt(int red, int grn, int blu) {
		return ((red & 0xff)<<16) | ((grn & 0xff)<<8) | blu & 0xff;
	}

	/**
	 * Interpolates linearly between two specified colors.
	 * @param ca first color (to be interpolated from)
	 * @param cb second color (to be interpolated to)
	 * @param t interpolation coefficient, must be in [0,1]
	 * @return the interpolated color
	 */
	public static Color interpolate(Color ca, Color cb, double t) {
		if (t < 0 || t > 1) {
			throw new IllegalArgumentException("interpolation coefficient must be in [0,1] but is " + t);
		}
		float[] a = ca.getRGBColorComponents(null);
		float[] b = cb.getRGBColorComponents(null);
		float[] c = Matrix.lerp(a, b, (float) t);
		return new Color(c[0], c[1], c[2]);
	}

	/**
	 * Interpolates linearly between the colors in the specified color palette.
	 * The interpolation coefficient must be in [0,1]. If 0, the first palette color
	 * is returned, if 1 the last color.
	 * @param palette an array of colors (at least 2)
	 * @param t interpolation coefficient, must be in [0,1]
	 * @return the interpolated color
	 */
	public static Color interpolate(Color[] palette, double t) {
		if (palette.length < 2) {
			throw new IllegalArgumentException("length of color palette must be at least 2 but is " + palette.length);
		}
		if (t < 0 || t > 1) {
			throw new IllegalArgumentException("interpolation coefficient must be in [0,1] but is " + t);
		}
		final int n = palette.length;
		double x = t * (n - 1);			// x = 0,...,n-1 (float palette index)
		int lo = (int) Math.floor(x);	// lower palette color index
		int hi = (int) Math.ceil(x);	// upper palette color index
		return interpolate(palette[lo], palette[hi], x - lo);
	}

//	/**
//	 * Forward Gamma correction (from linear to non-linear component values) for sRGB.
//	 * 
//	 * @param lc linear component value in [0,1]
//	 * @return gamma-corrected (non-linear) component value
//	 */
//	public static double gammaFwd(double lc) {	// input: linear component value in [0,1]
//		return (lc > 0.0031308) ?
//			(1.055 * Math.pow(lc, 1/2.4) - 0.055) :
//			(lc * 12.92);
//	}
//
//	/**
//	 * Inverse Gamma correction (from non-linear to linear component values) for sRGB.
//	 * 
//	 * @param nc non-linear (Gamma-corrected) component value in [0,1]
//	 * @return linear component value
//	 */
//	public static double gammaInv(double nc) {	// input: nonlinear component value in [0,1]
//		return (nc > 0.03928) ?
//			Math.pow((nc + 0.055)/1.055, 2.4) :
//			(nc / 12.92);
//	}

//	public static float[] sRgbToRgb(float[] srgb) {	// all components in [0,1]
//		float R = (float) RgbUtils.gammaInv(srgb[0]);
//		float G = (float) RgbUtils.gammaInv(srgb[1]);
//		float B = (float) RgbUtils.gammaInv(srgb[2]);
//		return new float[] {R,G,B};
//	}

//	public static float[] rgbToSrgb(float[] rgb) {	// all components in [0,1]
//		float sR = (float) RgbUtils.gammaFwd(rgb[0]);
//		float sG = (float) RgbUtils.gammaFwd(rgb[1]);
//		float sB = (float) RgbUtils.gammaFwd(rgb[2]);
//		return new float[] {sR,sG,sB};
//	}

}
