package imagingbook.lib.filter;

import imagingbook.lib.image.data.PixelPack;

public abstract class GenericFilterVector extends GenericFilter {

	private int iter = 0;
	private int iterMax = 1;	// for progress reporting only
	
	@Override 
	protected void runPass(PixelPack sourcePack, PixelPack targetPack) {
		final int width = sourcePack.getWidth();
		final int height = sourcePack.getHeight();
		iterMax = width * height;
		iter = 0;
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				targetPack.setPixel(u, v, doPixel(sourcePack, u, v)); // single pixel operation
				iter++;
			}
		}
		iter = 0;
	}
	
	// calculate the result vector for a single pixel
	
	protected abstract float[] doPixel(PixelPack source, int u, int v);
	
	// -----------------------------------------------------------------
	
	// helper method for copying vector pixels, TODO: should move to Utils or so
	public void copyPixel(float[] source, float[] target) {
		System.arraycopy(source, 0, target, 0, source.length);
	}
	
	@Override
	protected final double reportProgress(double subProgress) {
		double localProgress = (double) iter /iterMax;
		//System.out.println("GenericFilterVector: reportProgress() - returning " + localProgress);
		return super.reportProgress(localProgress);
	}

}
