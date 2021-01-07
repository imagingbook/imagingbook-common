package imagingbook.pub.edgepreservingfilters;

import imagingbook.lib.filter.linear.Kernel2D;

public abstract class TschumperleDericheF {

	public static class Parameters {
		/** Number of smoothing iterations */
		public int iterations = 20;	
		/** Adapting time step */
		public double dt = 20.0;  		
		/** Gradient smoothing (sigma of Gaussian) */
		public double sigmaG  = 0.5;
		/** Structure tensor smoothing (sigma of Gaussian) */
		public double sigmaS  = 0.5;	
		/** Diff. limiter along minimal var. (small value = strong smoothing) */
		public float a1 = 0.25f;  		
		/** Diff. limiter along maximal var. (small value = strong smoothing) */
		public float a2 = 0.90f;
		/** The alpha value applied in the first pass. */
		public float initialAlpha = 0.5f;
//		/** Set true to apply the filter in linear RGB (assumes sRGB input) */
//		public boolean useLinearRgb = false;
	}
	
	private static final float C1 = (float) (2 - Math.sqrt(2.0)) / 4;
	private static final float C2 = (float) (Math.sqrt(2.0) - 1) / 2;
	
	// TODO: move outside
	private static final float[][] Hdx = // gradient kernel X
		{{-C1, 0, C1},
		 {-C2, 0, C2},
		 {-C1, 0, C1}};
	
	private static final float[][] Hdy = // gradient kernel Y
		{{-C1, -C2, -C1},
		 {  0,   0,   0},
		 { C1,  C2,  C1}};
	
	protected static final Kernel2D kernelDx = new Kernel2D(Hdx, 1, 1, false);
	protected static final Kernel2D kernelDy = new Kernel2D(Hdy, 1, 1, false);
}
