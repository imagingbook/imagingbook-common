package imagingbook.lib.filter;

import imagingbook.lib.image.data.PixelPack;
import imagingbook.lib.image.data.PixelPack.PixelSlice;

public abstract class GenericFilterScalar extends GenericFilter {
	
	// for progress reporting only
	private int slice;
	private int sliceMax = 1;
	
	private int iter;
	private int iterMax = 1;
	
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
	private void runSlice(PixelSlice sourcePlane, PixelSlice targetPlane) {
		final int width = sourcePlane.getWidth();
		final int height = sourcePlane.getHeight();
		this.iterMax = width * height;
		this.iter = 0;
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				targetPlane.setVal(u, v, doPixel(sourcePlane, u, v));
				this.iter++;
			}
		}
		this.iter = 0;
	}

	// this method every scalar filter must implement
	// calculate the result value for a single pixel
	protected abstract float doPixel(PixelSlice plane, int u, int v);
	
	// -------------------------------------------------------------------

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
