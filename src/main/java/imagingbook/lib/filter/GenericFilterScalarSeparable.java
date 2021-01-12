package imagingbook.lib.filter;

import imagingbook.lib.image.access.PixelPack;
import imagingbook.lib.image.access.PixelPack.PixelSlice;

/**
 * A generic scalar multi-pass filter with exactly 2 passes.
 * @author WB
 * @version 2021/01/02
 */
public abstract class GenericFilterScalarSeparable extends GenericFilter {
	
	// for progress reporting only
	private int slice;
	private int sliceMax = 1;
	
	private int iter = 0;
	private int iterMax = 1;	// for progress reporting only
	
	
	// apply filter to a stack of pixel planes (1 pass)
	@Override
	protected void runPass(PixelPack source, PixelPack target) {
		sliceMax = source.getDepth();
		for (int k = 0; k < sliceMax; k++) {
			//IJ.log("+++++++ starting slice " + k);
			this.slice = k;
			runSlice(source.getSlice(k), target.getSlice(k));	// default behavior: apply filter to each plane, place results in target
		}
	}
	
	// a bit wasteful, as we provide a separate target for each plane
	private void runSlice(PixelSlice source, PixelSlice target) {
		final int width = source.getWidth();
		final int height = source.getHeight();
		this.iterMax = width * height * 2;
		this.iter = 0;
		
		// X-part
		//IJ.log("doing X-part =============================== ");
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				target.setVal(u, v, doPixelX(source, u, v));
				this.iter++;
			}
		}
		
		target.copyTo(source);
		
		// Y-part
		//IJ.log("doing Y-part =============================== ");
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				target.setVal(u, v, doPixelY(source, u, v));
				this.iter++;
			}
		}
		
		this.iter = 0;
	}
	
	// ------------------------------------------------------------------------

	// Apply a 1D filter in x-direction
	protected abstract float doPixelX(PixelSlice source, int u, int v);

	// Apply a 1D filter in y-direction
	protected abstract float doPixelY(PixelSlice source, int u, int v);
	
	// ------------------------------------------------------------------------
	
	@Override
	protected final double reportProgress(double subProgress) {
		double loopProgress = (this.iter + subProgress) / this.iterMax;
		//IJ.log("   loopProgress = " + loopProgress);
		double sliceProgress = (this.slice + loopProgress) / this.sliceMax;
		//IJ.log("   sliceProgress = " + sliceProgress);
		//System.out.println("reportProgress: GenericFilterScalar " + localProgress);
		return super.reportProgress(sliceProgress);
	}

	
}
