package imagingbook.pub.edgepreservingfilters;

import imagingbook.lib.image.access.OutOfBoundsStrategy;

import static imagingbook.lib.math.Arithmetic.sqr;

public abstract class PeronaMalik {
	
	public static class Parameters {
		/** Number of iterations to perform */
		public int iterations = 10;
		/** Update rate (alpha) */
		public float alpha = 0.20f; 			
		/** Smoothness parameter (kappa) */
		public float kappa = 25;
		/** Specify the type of conductivity function c() */
		public boolean smoothRegions = true;
		/** Specify the color mode */
		public ColorMode colorMode = ColorMode.SeparateChannels;
		/** Out-of-bounds strategy */
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NEAREST_BORDER;
	}
	
	public enum ColorMode  {
		SeparateChannels, 
		BrightnessGradient, 
		ColorGradient;
	}
	
	protected interface ConductanceFunction {
		float eval(float d);

//		static ConductanceFunction G1(final float kappa) {
//			return (d) -> (float) Math.exp(-sqr(d/kappa));
//		}
//		
//		static ConductanceFunction G2(final float kappa) {
//			return (d) -> 1.0f / (1.0f + sqr(d/kappa));
//		}
		
		static ConductanceFunction get(boolean smooth, float kappa) {
			return (smooth) ? 
					(d) -> (float) Math.exp(-sqr(d/kappa)) :	// = g2(d)
					(d) -> 1.0f / (1.0f + sqr(d/kappa)); 		// = g1(d)
		}
	}

}
