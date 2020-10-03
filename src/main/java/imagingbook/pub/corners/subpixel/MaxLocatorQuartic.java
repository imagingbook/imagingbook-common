package imagingbook.pub.corners.subpixel;

import imagingbook.lib.math.Matrix;
import imagingbook.lib.settings.PrintPrecision;

/**
 * 2D interpolator based on fitting a quartic polynomial to the supplied samples.
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
public class MaxLocatorQuartic implements MaxLocator {
	
	static int DefaultMaxIterations = 20;		// iteration limit
	static double DefaulMinMove = 1e-6;			// smallest x/y move to continue search 
	static double DefaultSearchWindow = 0.6;	// x/y search boundary (-xyLimit, +xyLimit)
	
	private final int maxIterations;
	private final double minMove;
	private final double searchWindow;
	
	private final double[] c = new double[9];	// polynomial coefficients
	
	// -----------------------------------------------------------
	
	public MaxLocatorQuartic() {
		this(DefaultMaxIterations, DefaulMinMove, DefaultSearchWindow);
	}
			
	public MaxLocatorQuartic(int maxIterations, double minMove, double searchWindow) {
		this.maxIterations = maxIterations;
		this.minMove = minMove;
		this.searchWindow = searchWindow;
	}
	
	// -----------------------------------------------------------
	
	@Override
	public float[] locateMaximum(float[] s) {
		c[0] = s[0];
		c[1] = (s[1] - s[5]) / 2;
		c[2] = (s[7] - s[3]) / 2;
		c[3] = (s[1] - 2 * s[0] + s[5]) / 2;
		c[4] = (s[3] - 2 * s[0] + s[7]) / 2;
		c[5] = (s[4] + s[8] - s[2] - s[6]) / 4;
		c[6] = (s[6] + s[8] - s[2] - s[4]) / 4 - c[2];
		c[7] = (s[2] + s[8] - s[4] - s[6]) / 4 - c[1];
		c[8] = s[0] - (s[1] + s[3] + s[5] + s[7]) / 2 + (s[2] + s[4] + s[6] + s[8]) / 4;
		
		double[] xyMax = getMaxPosition();
		if (xyMax == null) {	// did not converge!
			return null;
		}
		float z = getInterpolatedValue(xyMax);
		return new float[] {(float) xyMax[0], (float) xyMax[1], z};
	}
	
	// -----------------------------------------------------------
	
	private double[] getGradient(double[] xy) {
		return this.getGradient(xy[0], xy[1]);
	}

	private double[] getGradient(double x, double y) {
		double gx = c[1] + 2*c[3]*x + c[5]*y + 2*c[6]*x*y + c[7]*y*y + 2*c[8]*x*y*y;
		double gy = c[2] + c[5]*x + 2*c[4]*y + 2*c[7]*x*y + c[6]*x*x + 2*c[8]*x*x*y;
		return new double[] {gx, gy};
	}
	
//	public double[][] getHessian(double[] xy) {
//		return this.getHessian(xy[0], xy[1]);
//	}
//

//	public double[][] getHessian(double x, double y) {
//		double[][] H = {
//				{2*(c[3] + c[6]*y + c[8]*y*y), c[5] + 2*(c[6]*x + c[7]*y) + 4*c[8]*x*y}, 
//				{c[5] + 2*(c[6]*x + c[7]*y) + 4*c[8]*x*y, 2*(c[4] + c[7]*x + c[8]*x*x)}
//				};
//		return H;
//	}
	
	private double[][] getInverseHessian(double[] xy) {
		final double x = xy[0];
		final double y = xy[1];
		double R = 2*(c[4] + x*(c[7] + c[8]*x));
		double S = 2*(c[3] + y*(c[6] + c[8]*y));
		double T = c[5] + 2*c[6]*x + 2*c[7]*y + 4*c[8]*x*y;
		double a = 1.0 / (R*S - T*T);	// TODO: check denominator for zero!
		double[][] Hi = {
				{ a*R, -a*T}, 
				{-a*T,  a*S}};
		return Hi;
	}
	
	
	private float getInterpolatedValue(double[] xy) {
		final double x = xy[0];
		final double y = xy[1];
		return (float) (c[0] + c[1]*x + c[2]*y + c[3]*x*x + c[4]*y*y
						+ c[5]*x*y + c[6]*x*x*y + c[7]*x*y*y + c[8]*x*x*y*y);
	}
	
	private boolean closeEnough(double a, double b) {
		return Math.abs(a - b) < minMove;
	}

	private double[] getMaxPosition() {
		double[] Xcur = {0, 0};
		boolean inside = true;
		int n;
		for (n = 1; n <= maxIterations; n++) {
			
			if (Xcur[0] < -searchWindow || Xcur[0] > searchWindow || Xcur[1] < -searchWindow || Xcur[1] > searchWindow) {
				inside = false;
				break;
			}
			
			double[] Xnext = this.getNextMaxPosition(Xcur);
			if (closeEnough(Xcur[0], Xnext[0]) && closeEnough(Xcur[1], Xnext[1])) {
				break;
			}
			
			Xcur[0] = Xnext[0];
			Xcur[1] = Xnext[1];
		}
		
		if (n >= maxIterations) {
			return null;
//			throw new RuntimeException("getMaxPosition() did not converge, s = \n" + toString(s));
		}
		if (!inside) {
			return null;
//			throw new RuntimeException("getMaxPosition() exceeded search limit!");
		}
		return Xcur;
	}
	
	/**
	 * Estimate max. position using Taylor expansion from the given position
	 * @param X Taylor expansion point
	 * @return the estimated maximum position
	 */
	private double[] getNextMaxPosition(double[] X) {
		double[] g = this.getGradient(X);
		double[][] Hi = this.getInverseHessian(X);
		return Matrix.add(X, Matrix.multiply(Matrix.multiply(-1, Hi), g));
	}
	
	// ---------------------------------------------------------

	public static void main(String[] args) {
		Method m = MaxLocator.Method.Quartic;
		
		PrintPrecision.set(8);
		float[] samples = {16,9,7,11,8,15,14,12,10}; // = s_0,...,s_8, result xyz = {-0.40573445, 0.11285823, 16.62036324}
//		float[] samples = {40229.785156f, 33941.535156f, 25963.150391f, 39558.175781f, 39078.843750f, 33857.863281f, 39861.664063f, 38746.250000f, 33652.839844f};

		MaxLocator interp = m.getInstance(); //new SubPixelQuartic();
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
