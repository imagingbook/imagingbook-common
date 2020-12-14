package imagingbook.pub.corners.subpixel_obsolete;


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
 * @version 2020/09/29
 * @deprecated
 */
public class QuarticInterpolator2D implements PolynomialInterpolator2D {
	
	private static int maxIterationsDefault = 100;
	
	private final double[] c;	// polynomial coefficients
	
	private final int maxIterations;
	private final double minMove = 1e-6;	// smallest move to continue search 
	private final double searchWindow = 0.6;		// x/y search boundary (-xyLimit, +xyLimit)
	
	@SuppressWarnings("unused")
	private final float[] s;	// TODO: for testing only - remove!
	
	public QuarticInterpolator2D(float[] s, int maxIterations) {
		this.s = s;	// TODO: for testing only - remove!
		this.maxIterations = maxIterations;
		c = new double[9];
		c[0] = s[0];
		c[1] = (s[1] - s[5]) / 2;
		c[2] = (s[7] - s[3]) / 2;
		c[3] = (s[1] - 2 * s[0] + s[5]) / 2;
		c[4] = (s[3] - 2 * s[0] + s[7]) / 2;
		c[5] = (s[4] + s[8] - s[2] - s[6]) / 4;
		c[6] = (s[6] + s[8] - s[2] - s[4]) / 4 - c[2];
		c[7] = (s[2] + s[8] - s[4] - s[6]) / 4 - c[1];
		c[8] = s[0] - (s[1] + s[3] + s[5] + s[7]) / 2 + (s[2] + s[4] + s[6] + s[8]) / 4;
		// TODO: check for zero values of c[3], c[4]
	}
	
	public QuarticInterpolator2D(float[] s) {
		this(s, maxIterationsDefault);
	}
	
	// ---------------------------------------------------------

	@Override
	public double[] getCoefficients() {
		return c;
	}
	
	public double[] getGradient(double[] xy) {
		return this.getGradient(xy[0], xy[1]);
	}

	@Override
	public double[] getGradient(double x, double y) {
		double gx = c[1] + 2*c[3]*x + c[5]*y + 2*c[6]*x*y + c[7]*y*y + 2*c[8]*x*y*y;
		double gy = c[2] + c[5]*x + 2*c[4]*y + 2*c[7]*x*y + c[6]*x*x + 2*c[8]*x*x*y;
		return new double[] {gx, gy};
	}
	
	public double[][] getHessian(double[] xy) {
		return this.getHessian(xy[0], xy[1]);
	}

	@Override
	public double[][] getHessian(double x, double y) {
		double[][] H = {
				{2*(c[3] + c[6]*y + c[8]*y*y), c[5] + 2*(c[6]*x + c[7]*y) + 4*c[8]*x*y}, 
				{c[5] + 2*(c[6]*x + c[7]*y) + 4*c[8]*x*y, 2*(c[4] + c[7]*x + c[8]*x*x)}
				};
		return H;
	}
	
	public double[][] getInverseHessian(double[] xy) {
		return this.getInverseHessian(xy[0], xy[1]);
	}
	
	public double[][] getInverseHessian(double x, double y) {
		double R = 2*(c[4] + x*(c[7] + c[8]*x));
		double S = 2*(c[3] + y*(c[6] + c[8]*y));
		double T = c[5] + 2*c[6]*x + 2*c[7]*y + 4*c[8]*x*y;
		double a = 1.0 / (R*S - T*T);
		double[][] Hi = {
				{ a*R, -a*T}, 
				{-a*T,  a*S}};
		return Hi;
	}
	
	@Override
	public float getInterpolatedValue(double x, double y) {
		return (float) (c[0] + c[1]*x + c[2]*y + c[3]*x*x + c[4]*y*y
						+ c[5]*x*y + c[6]*x*x*y + c[7]*x*y*y + c[8]*x*x*y*y);
	}
	
	private boolean closeEnough(double a, double b) {
		return Math.abs(a - b) < minMove;
	}

	@Override
	public float[] getMaxPosition() {
//		System.out.format("Searching for max position (maxIterations=%d, minDelta=%f)\n", 
//				maxIterations, minMove);
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
			
//			double dx = Xcur[0] - Xnext[0];
//			double dy = Xcur[1] - Xnext[1];
//			System.out.format("   X(%d) = %s, d = (%.6f,%.6f)\n", n, Matrix.toString(Xcur), dx, dy);
			
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
		return new float[] {(float) Xcur[0], (float) Xcur[1]};
	}
	
	/**
	 * Estimate max. position using Taylor expansion from the given position
	 * @param X Taylor expansion point
	 * @return the estimated maximum position
	 */
	public double[] getNextMaxPosition(double[] X) {
		double[] g = this.getGradient(X);
		double[][] Hi = this.getInverseHessian(X);
		return Matrix.add(X, Matrix.multiply(Matrix.multiply(-1, Hi), g));
	}
	
	// ---------------------------------------------------------

	public static void main(String[] args) {
		PrintPrecision.set(8);
		float[] samples = {16,9,7,11,8,15,14,12,10}; // = s_0,...,s_8
//		float[] samples = {40229.785156f, 33941.535156f, 25963.150391f, 39558.175781f, 39078.843750f, 33857.863281f, 39861.664063f, 38746.250000f, 33652.839844f};

		QuarticInterpolator2D interp = new QuarticInterpolator2D(samples);
		
		System.out.println(QuarticInterpolator2D.class.getName());
		System.out.println("c = " + Matrix.toString(interp.getCoefficients()));
		
		double[][] iVals = new double[3][3];
		for (int v = -1; v <= 1; v++) {
			for (int u = -1; u <= 1; u++) {
				iVals[v+1][u+1] = interp.getInterpolatedValue(u, v);
			}
		}
		System.out.println("iVals = " + Matrix.toString(iVals));

		System.out.println("g(0,0) = " + Matrix.toString(interp.getGradient(0, 0)));
		System.out.println("g(0.5,-0.75) = " + Matrix.toString(interp.getGradient(0.5, -0.75)));
		
		System.out.println("H(0,0) = " + Matrix.toString(interp.getHessian(0, 0)));
		System.out.println("H(0.5,-0.75) = " + Matrix.toString(interp.getHessian(0.5, -0.75)));
		
		float[] pMax = interp.getMaxPosition();
		if (pMax != null) {
			System.out.println("pMax = " + Matrix.toString(pMax));
			System.out.println("vMax = " + interp.getInterpolatedValue(pMax));
		}
		else {
			System.out.println("*** pMax could not be located! ***");
		}
	}
}
