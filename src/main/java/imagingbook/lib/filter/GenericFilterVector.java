package imagingbook.lib.filter;

import imagingbook.lib.image.access.PixelPack;

public abstract class GenericFilterVector extends GenericFilter {

	public int iter = 0, iterMax = 1;	// for progress reporting only
	
	@Override 
	protected void doPass(PixelPack sourcePack, PixelPack targetPack) {
		final int width = sourcePack.getWidth();
		final int height = sourcePack.getHeight();
		iterMax = width * height;
		iter = 0;
		for (int v = 0; v < height; v++) {
//			if (v % 10 == 0) 
//				System.out.println("    v = " + v + " prog = " + this.getProgress());
			for (int u = 0; u < width; u++) {
				targetPack.setPixel(u, v, doPixel(sourcePack, u, v)); // single pixel operation
				iter++;
			}
		}
		iter = 0;
	}
	
	// calculate the result vector for a single pixel
	protected float[] doPixel(PixelPack source, int u, int v) {
		throw new UnsupportedOperationException("method 'float[] doPixel(u,v)' not implemented!");
	}
	
	// -----------------------------------------------------------------
	
	// helper method for copying vector pixels, TODO: should move to Utils or so
	public void copyPixel(float[] source, float[] target) {
		System.arraycopy(source, 0, target, 0, source.length);
	}
	

	public double reportProgress(double subProgress) {
		double localProgress = (double) iter /iterMax;
		//System.out.println("GenericFilterVector: reportProgress() - returning " + localProgress);
		return super.reportProgress(localProgress);
	}

}
