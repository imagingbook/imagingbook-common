package imagingbook.pub.edgepreservingfilters;

import imagingbook.lib.filter.linear.Kernel2D;

public interface TschumperleDericheF {

	public static class Parameters {
		/** Number of smoothing iterations */
		public int iterations = 20;	
		/** Adapting time step */
		public double dt = 20.0;  		
		/** Gradient smoothing (sigma of Gaussian) */
		public double sigmaD  = 0.5;
		/** Structure tensor smoothing (sigma of Gaussian) */
		public double sigmaM  = 0.5;	
		/** Diff. limiter along minimal var. (small value = strong smoothing) */
		public float a0 = 0.25f;  		
		/** Diff. limiter along maximal var. (small value = strong smoothing) */
		public float a1 = 0.90f;
		/** The alpha value applied in the first pass. */
		public float alpha0 = 0.5f;
//		/** Set true to apply the filter in linear RGB (assumes sRGB input) */
//		public boolean useLinearRgb = false;
	}
	
	static final float C1 = (float) (2 - Math.sqrt(2.0)) / 4;
	static final float C2 = (float) (Math.sqrt(2.0) - 1) / 2;
	
	// TODO: move outside
	static final float[][] Hdx = // gradient kernel X
		{{-C1, 0, C1},
		 {-C2, 0, C2},
		 {-C1, 0, C1}};
	
	static final float[][] Hdy = // gradient kernel Y
		{{-C1, -C2, -C1},
		 {  0,   0,   0},
		 { C1,  C2,  C1}};
	
	static final Kernel2D kernelDx = new Kernel2D(Hdx, 1, 1, false);
	static final Kernel2D kernelDy = new Kernel2D(Hdy, 1, 1, false);
}
