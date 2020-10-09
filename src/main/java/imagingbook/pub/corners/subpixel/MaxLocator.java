package imagingbook.pub.corners.subpixel;

import static imagingbook.lib.math.Arithmetic.isZero;

import imagingbook.lib.math.Matrix;

/**
 * The common interface for all sub-pixel locators in this package.
 * @see Quadratic1
 * @see Quadratic2
 * @see Quartic
 * @author WB
 * @version 2020/10/03
 */
public interface MaxLocator {
	
	/**
	 * Tries to locate the sub-pixel maximum from the 9 discrete sample values
	 * (s0,...,s8) taken from a 3x3 neigborhood, arranged in the following order:
	 * <pre>
	 * s4 s3 s2
	 * s5 s0 s1
	 * s6 s7 s8
	 * </pre>
	 * The center value (s0) is assumed to be at position (0,0).
	 * @param s a vector containing 9 sample values in the order described above
	 * @return a 3-element array [x,y,z], with the estimated maximum position (x,y) and the associated max. value (z). 
	 * The position is relative to the center coordinate (0,0).
	 * {@code null} is returned if the maximum position could not be located.
	 */
	public float[] getInterpolatedMax(float[] s);
	
	/**
	 * Enumeration of keys for {@link MaxLocator} methods.
	 */
	public enum Method {
		None(null),
		Quadratic1(Quadratic1.class),
		Quadratic2(Quadratic2.class),
		Quartic(Quartic.class);

		private Class<? extends MaxLocator> clazz;

		Method(Class<? extends MaxLocator> clazz) {
			this.clazz = clazz;
		}
	}
	
	/**
	 * Creates a specific {@link MaxLocator} instance based on the supplied
	 * {@link Method} key.
	 * @param m the method
	 * @return a new {@link MaxLocator} instance
	 */
	public static MaxLocator getInstance(Method m) {
		if (m == null || m.clazz == null) {
			return null;
		}
		MaxLocator ml = null;
		try {
			ml = m.clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) { }
		return ml;
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * 2D interpolator based on fitting a quadratic polynomial (axis-parallel
	 * paraboloid, with no mixed terms) of the form
	 * <br>
	 * f(x,y) = c_0 + c_1 x + c_2 y + c_3 x^2 + c_4 y^2 
	 * <br>
	 * to the supplied samples.
	 * Only 5 of the 9 supplied sample values are used (the 4 corner samples are ignored).
	 * @see MaxLocator#getInterpolatedMax(float[])
	 */
	public static class Quadratic1 implements MaxLocator {
		
		private final double[] c = new double[5];	// polynomial coefficients
		
		public float[] getInterpolatedMax(float[] s) {
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
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * 2D interpolator based on fitting a quadratic polynomial (non-axis-parallel 
	 * paraboloid, including mixed terms) of the form
	 * <br>
	 * f(x,y) = c_0 + c_1 x + c_2 y + c_3 x^2 + c_4 y^2  + c_5 xy
	 * <br>
	 * to the supplied samples. All 9 sample values are used.
	 * @see MaxLocator#getInterpolatedMax(float[])
	 */
	public static class Quadratic2 implements MaxLocator {
		
		private final double[] c = new double[6];	// polynomial coefficients

		@Override
		public float[] getInterpolatedMax(float[] s) {
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
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * 2D interpolator based on fitting a 'quartic' (i.e., 4th-order) polynomial  of the form
	 * <br>
	 * f(x,y) = c_0 + c_1 x + c_2 y + c_3 x^2 + c_4 y^2 + c_5 x y + c_6 x^2 y + c_7 x y^2 + c_8 x^2 y^2  
	 * <br>
	 * to the supplied samples. The underlying interpolation function passes through
	 * all sample values. The local maximum cannot be found in closed form but 
	 * is found iteratively, which is not guaranteed to succeed.
	 * All 9 sample values are used.
	 * @see MaxLocator#getInterpolatedMax(float[])
	 */
	public static class Quartic implements MaxLocator {
		static int DefaultMaxIterations = 20;		// iteration limit
		static double DefaulMinMove = 1e-6;			// smallest x/y move to continue search 
		static double DefaultSearchWindow = 0.6;	// x/y search boundary (-xyLimit, +xyLimit)
		
		private final int maxIterations;
		private final double minMove;
		private final double searchWindow;
		
		private final double[] c = new double[9];	// polynomial coefficients
		
		// -----------------------------------------------------------
		
		public Quartic() {
			this(DefaultMaxIterations, DefaulMinMove, DefaultSearchWindow);
		}
				
		public Quartic(int maxIterations, double minMove, double searchWindow) {
			this.maxIterations = maxIterations;
			this.minMove = minMove;
			this.searchWindow = searchWindow;
		}
		
		@Override
		public float[] getInterpolatedMax(float[] s) {
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
//				throw new RuntimeException("getMaxPosition() did not converge, s = \n" + toString(s));
			}
			if (!inside) {
				return null;
//				throw new RuntimeException("getMaxPosition() exceeded search limit!");
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
		
		private double[] getGradient(double[] xy) {
			return this.getGradient(xy[0], xy[1]);
		}

		private double[] getGradient(double x, double y) {
			double gx = c[1] + 2*c[3]*x + c[5]*y + 2*c[6]*x*y + c[7]*y*y + 2*c[8]*x*y*y;
			double gy = c[2] + c[5]*x + 2*c[4]*y + 2*c[7]*x*y + c[6]*x*x + 2*c[8]*x*x*y;
			return new double[] {gx, gy};
		}
		
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
	}
}
