package imagingbook.lib.filter;

import imagingbook.lib.image.data.PixelPack;

/**
 * A generic vector multi-pass filter with exactly 2 passes.
 * @author WB
 * @version 2021/01/02
 */
public abstract class GenericFilterVectorSeparable extends GenericFilter { // GenericFilterVector
	
	private int iter = 0;
	private int iterMax = 1;	// for progress reporting only
	
	@Override 
	protected void runPass(PixelPack source, PixelPack target) {
		final int width = source.getWidth();
		final int height = source.getHeight();
		iterMax = width * height * 2;
		iter = 0;
		
		// X-part
		//IJ.log("X-part +++++++++++++++++++++++++++++++++");
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				target.setVec(u, v, doPixelX(source, u, v)); // single pixel operation
				iter++;
			}
		}
		
		target.copyTo(source);
		
		// Y-part
		//IJ.log("Y-part +++++++++++++++++++++++++++++++++");
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				target.setVec(u, v, doPixelY(source, u, v)); // single pixel operation
				iter++;
			}
		}
		iter = 0;
	}
	
//	@Override
//	protected final float[] doPixel(PixelPack pack, int u, int v) {
//		switch (getPass()) {
//		case 0: return filterPixelX(pack, u, v);
//		case 1: return filterPixelY(pack, u, v);
//		default: throw new RuntimeException("invalid pass number " + getPass());
//		}
//	}
	
//	@Override
//	protected final int passesRequired() {
//		return 2;	// do exactly 2 passes
//	}
	
	// ------------------------------------------------------------------------

	// Apply a 1D filter in x-direction
	protected abstract float[] doPixelX(PixelPack pack, int u, int v);

	// Apply a 1D filter in y-direction
	protected abstract float[] doPixelY(PixelPack pack, int u, int v);
	
	// ------------------------------------------------------------------------

	
	@Override
	protected final double reportProgress(double subProgress) {
		double localProgress = (double) iter /iterMax;
		//System.out.println("GenericFilterVector: reportProgress() - returning " + localProgress);
		return super.reportProgress(localProgress);
	}
	
	
	
}
