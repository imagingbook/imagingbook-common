package imagingbook.lib.filter;

import ij.IJ;
import imagingbook.lib.image.access.PixelPack.PixelSlice;

public abstract class GenericFilterScalar extends GenericFilter {
	
	// apply filter to a stack of pixel planes (1 pass)
	protected void doPass() {
		IJ.log("GenericFilterScalar: doPass " + getPass());
		int depth = source.getDepth();
		for (int k = 0; k < depth; k++) {
			doSlice(k);	// default behavior: apply filter to each plane, place results in target
		}
	}
	
	// a bit wasteful, since we provide a separate target for each plane
	protected void doSlice(int k) {
		IJ.log("GenericFilterScalar: doSlice " + k);
		PixelSlice sourcePlane = source.getSlice(k); 
		PixelSlice targetPlane = target.getSlice(k);
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
