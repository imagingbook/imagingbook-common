package imagingbook.lib.filter;

import imagingbook.lib.image.access.PixelPack;
import imagingbook.lib.image.access.PixelPack.PixelSlice;

public abstract class GenericFilterScalar extends GenericFilter {
	
	// apply filter to a stack of pixel planes (1 pass)
	protected void doPass(PixelPack sourcePack, PixelPack targetPack) {
		int depth = source.getDepth();
		for (int k = 0; k < depth; k++) {
			doSlice(sourcePack.getSlice(k), targetPack.getSlice(k));	// default behavior: apply filter to each plane, place results in target
		}
	}
	
	// a bit wasteful, as we provide a separate target for each plane
	protected void doSlice(PixelSlice sourcePlane, PixelSlice targetPlane) {
		final int width = sourcePlane.getWidth();
		final int height = sourcePlane.getHeight();
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				targetPlane.setVal(u, v, doPixel(sourcePlane, u, v));
			}
		}
	}

	// this method every scalar filter must implement
	// calculate the result vector for a single pixel
	protected float doPixel(PixelSlice plane, int u, int v) {
		throw new UnsupportedOperationException("method 'float doPixel(u,v)' not implemented!");
	}
}
