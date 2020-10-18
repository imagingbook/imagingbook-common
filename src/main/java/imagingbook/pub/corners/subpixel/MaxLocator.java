package imagingbook.pub.corners.subpixel;

import static imagingbook.lib.math.Arithmetic.EPSILON_DOUBLE;
import static imagingbook.lib.math.Arithmetic.sqr;

import imagingbook.lib.math.Matrix;

/**
 * The common interface for all sub-pixel locators in this package.
 * @see QuadraticTaylor
 * @see QuadraticLeastSquares
 * @see Quartic
 * @author WB
 * @version 2020/10/03
 */
public abstract class MaxLocator {
	
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
	public abstract float[] getMax(float[] s);
	
	/**
	 * Enumeration of keys for {@link MaxLocator} methods.
	 */
	public enum Method {
		QuadraticTaylor, QuadraticLeastSquares, Quartic, None;
	}
	
	/**
	 * Creates a specific {@link MaxLocator} instance based on the supplied
	 * {@link Method} key.
	 * @param m the method
	 * @return a new {@link MaxLocator} instance
	 */
	public static MaxLocator getInstance(Method m) {
		MaxLocator ml = null;
		switch(m) {
		case QuadraticTaylor:
			ml = new QuadraticTaylor(); break;
		case QuadraticLeastSquares:
			ml = new QuadraticLeastSquares(); break;
		case Quartic:
			ml = new Quartic(); break;
		case None:
			break;
		}
		return ml;
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * 2D interpolator using second-order Taylor expansion to fit a quadratic
	 * polynomial of the form
	 * <br>
	 * f(x,y) = c_0 + c_1 x + c_2 y + c_3 x^2 + c_4 y^2  + c_5 xy
	 * <br>
	 * to the supplied samples values.
	 * @see MaxLocator#getMax(float[])
	 */
	public static class QuadraticTaylor extends MaxLocator {
		
		private final double[] c = new double[6];	// polynomial coefficients

		@Override
		public float[] getMax(float[] s) {
			c[0] = s[0];
			c[1] = (s[1] - s[5]) / 2;
			c[2] = (s[7] - s[3]) / 2;
			c[3] = (s[1] - 2*s[0] + s[5]) / 2;
			c[4] = (s[3] - 2*s[0] + s[7]) / 2;
			c[5] = (-s[2] + s[4] - s[6] + s[8]) / 4;
			
			double d = (4*c[3]*c[4] - sqr(c[5]));
			
			if (d < EPSILON_DOUBLE || c[3] >= 0) {	// not a maximum (minimum or saddle point)
				return null;
			}
			
			// max position:
			float x = (float) ((c[2]*c[5] - 2*c[1]*c[4]) / d);
			float y = (float) ((c[1]*c[5] - 2*c[2]*c[3]) / d);
			// max value:
			float z = (float) (c[0] + c[1]*x + c[2]*y + c[3]*x*x + c[4]*y*y + c[5]*x*y);
			return new float[] {x, y, z};
		}
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * 2D interpolator based on least-squares fitting a quadratic polynomial 
	 * <br>
	 * f(x,y) = c_0 + c_1 x + c_2 y + c_3 x^2 + c_4 y^2  + c_5 xy
	 * <br>
	 * to the supplied sample values.
	 * @see MaxLocator#getMax(float[])
	 */
	public static class QuadraticLeastSquares extends MaxLocator {
		
		private final double[] c = new double[6];	// polynomial coefficients

		@Override
		public float[] getMax(float[] s) {
			c[0] = (5 * s[0] + 2 * s[1] - s[2] + 2 * s[3] - s[4] + 2 * s[5] - s[6] + 2 * s[7] - s[8]) / 9;
			c[1] = (s[1] + s[2] - s[4] - s[5] - s[6] + s[8]) / 6;
			c[2] = (-s[2] - s[3] - s[4] + s[6] + s[7] + s[8]) / 6;
			c[3] = (-2 * s[0] + s[1] + s[2] - 2 * s[3] + s[4] + s[5] + s[6] - 2 * s[7] + s[8]) / 6;
			c[4] = (-2 * s[0] - 2 * s[1] + s[2] + s[3] + s[4] - 2 * s[5] + s[6] + s[7] + s[8]) / 6;
			c[5] = (-s[2] + s[4] - s[6] + s[8]) / 4;
			
			//System.out.println("c = " + Matrix.toString(c));
			
			double d = (4*c[3]*c[4] - sqr(c[5]));
			
			if (d < EPSILON_DOUBLE || c[3] >= 0) {	// not a maximum (minimum or saddle point)
				return null;
			}
			
			// max position:
			float x = (float) ((c[2]*c[5] - 2*c[1]*c[4]) / d);
			float y = (float) ((c[1]*c[5] - 2*c[2]*c[3]) / d);
			// max value:
			float z = (float) (c[0] + c[1]*x + c[2]*y + c[3]*x*x + c[4]*y*y + c[5]*x*y);
			return new float[] {x, y, z};
		}
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * 2D interpolator based on fitting a 'quartic' (i.e., 4th-order) polynomial
	 * <br>
	 * f(x,y) = c_0 + c_1 x + c_2 y + c_3 x^2 + c_4 y^2 + c_5 x y + c_6 x^2 y + c_7 x y^2 + c_8 x^2 y^2  
	 * <br>
	 * to the supplied sample values. The interpolation function passes through
	 * all sample values. The local maximum cannot be found in closed form but 
	 * is found iteratively, which is not guaranteed to succeed.
	 * @see MaxLocator#getMax(float[])
	 */
	public static class Quartic extends MaxLocator {
		static int DefaultMaxIterations = 20;		// iteration limit
		static double DefaulMaxDelta = 1e-6;			// smallest x/y move to continue search 
		static double DefaultMaxRad = 1.0;	// x/y search boundary (-xyLimit, +xyLimit)
		
		private final int maxIterations;
		private final double maxDelta;
		private final double maxRad;
//		private final double searchWindow;
		
		private final double[] c = new double[9];	// polynomial coefficients
		
		// -----------------------------------------------------------
		
		public Quartic() {
			this(DefaultMaxIterations, DefaulMaxDelta, DefaultMaxRad);
		}
				
		public Quartic(int maxIterations, double maxDelta, double maxRad) {
			this.maxIterations = maxIterations;
			this.maxDelta = maxDelta;
			this.maxRad = maxRad;
		}
		
		@Override
		public float[] getMax(float[] s) {
			c[0] = s[0];
			c[1] = (s[1] - s[5]) / 2;
			c[2] = (s[7] - s[3]) / 2;
			c[3] = (s[1] - 2 * s[0] + s[5]) / 2;
			c[4] = (s[3] - 2 * s[0] + s[7]) / 2;
			c[5] = (-s[2] + s[4] - s[6] + s[8]) / 4;
			c[6] = (-s[2] + 2 *s[3] - s[4] + s[6] - 2 * s[7] + s[8]) / 4;
			c[7] = (-2 * s[1] + s[2] - s[4] + 2 * s[5] - s[6] + s[8]) / 4;
			c[8] = s[0] + (-2 * s[1] + s[2] - 2 * s[3] + s[4] - 2 * s[5] + s[6] - 2 * s[7] + s[8]) / 4;
			
			//System.out.println("c = " + Matrix.toString(c));
			
			double d = (4*c[3]*c[4] - sqr(c[5]));
			
			if (d < EPSILON_DOUBLE || c[3] >= 0) {	// not a maximum (minimum or saddle point)
				return null;
			}
			
			double[] xyMax = getMaxPosition();
			if (xyMax == null) {	// did not converge!
				return null;
			}
			float z = getInterpolatedValue(xyMax);
			return new float[] {(float) xyMax[0], (float) xyMax[1], z};
		}
		
		private float getInterpolatedValue(double[] X) {
			final double x = X[0];
			final double y = X[1];
			return (float) (c[0] + c[1]*x + c[2]*y + c[3]*x*x + c[4]*y*y
							+ c[5]*x*y + c[6]*x*x*y + c[7]*x*y*y + c[8]*x*x*y*y);
		}
		
		private double[] getMaxPosition() {
			boolean done = false;
			int n = 0;
			double[] Xcur = {0, 0};

			while (!done && n < maxIterations && Matrix.normL2(Xcur) < maxRad) {
				double[] Xnext = this.getNextPos(Xcur);	
				if (Matrix.distL2(Xcur, Xnext) < maxDelta) {
					done = true;
				}
				else {
					Xcur[0] = Xnext[0];
					Xcur[1] = Xnext[1];
				}
			}
			
			return done ? Xcur : null;
		}
		
		/**
		 * Estimate max. position using Taylor expansion from the given position
		 * @param X Taylor expansion point
		 * @return the estimated maximum position
		 */
		private double[] getNextPos(double[] X) {
			double[] g = this.getGradient(X);
			double[][] Hi = this.getInverseHessian(X);
			return Matrix.add(X, Matrix.multiply(Matrix.multiply(-1, Hi), g));
		}
		
		private double[] getGradient(double[] X) {
			return this.getGradient(X[0], X[1]);
		}

		private double[] getGradient(double x, double y) {
			double gx = c[1] + 2*c[3]*x + c[5]*y + 2*c[6]*x*y + c[7]*y*y + 2*c[8]*x*y*y;
			double gy = c[2] + c[5]*x + 2*c[4]*y + 2*c[7]*x*y + c[6]*x*x + 2*c[8]*x*x*y;
			return new double[] {gx, gy};
		}
		
		private double[][] getInverseHessian(double[] X) {
			return this.getInverseHessian(X[0], X[1]);
		}
		
		private double[][] getInverseHessian(double x, double y) {
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
