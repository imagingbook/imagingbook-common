package imagingbook.pub.edgepreservingfilters;

import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.util.ParameterBundle;

import static imagingbook.lib.math.Arithmetic.sqr;

public interface PeronaMalikF {
	
	public static class Parameters implements ParameterBundle {
		/** Number of iterations to perform */
		public int iterations = 10;
		/** Update rate (alpha) */
		public float alpha = 0.20f; 			
		/** Smoothness parameter (kappa) */
		public float kappa = 25;
		/** Specify the type of conductance function g(d) */
		public ConductanceFunction.Type conductanceFunType = ConductanceFunction.Type.g1;
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
	
	public interface ConductanceFunction {
		float eval(float d);
		
		public enum Type {
			g1, g2, g3, g4;
		}
		
		static ConductanceFunction get(Type type, float kappa) {
			switch (type) {
			case g1:
				return (d) -> (float) Math.exp(-sqr(d/kappa));
			case g2:
				return (d) -> 1.0f / (1.0f + sqr(d/kappa));
			case g3:
				return (d) -> (float) (1.0 / Math.sqrt(1.0f + sqr(d/kappa)));
			case g4:
				return (d) -> 
				(d <= 2 * kappa) ? sqr(1.0f - sqr(d / (2 * kappa))) : 0.0f;
			}
			return null;
		}
	}

}
