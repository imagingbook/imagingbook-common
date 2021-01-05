package imagingbook.lib.filter;

import imagingbook.lib.image.access.PixelPack;
import imagingbook.lib.image.access.PixelPack.PixelSlice;

public abstract class GenericFilterScalar extends GenericFilter {
	
	protected GenericFilterScalar(PixelPack pp) {
		super(pp);
	}
	
	// apply filter to a stack of pixel planes (1 pass)
	protected void filterAll(PixelPack source) {
		PixelSlice target = source.getEmptySlice();
		int depth = source.getDepth();
		for (int k = 0; k < depth; k++) {
			PixelSlice slice = source.getSlice(k);
			setupPass(slice);
			filterSlice(slice, target);	// default behavior: apply filter to each plane
			target.copyTo(slice); // copy target back to sources
		}
	}
	
	protected void filterSlice(PixelSlice source, PixelSlice target) {
		final int width = source.getWidth();
		final int height = source.getHeight();
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				target.setVal(u, v, filterPixel(source, u, v));
			}
		}
	}
	
	// called before every pass. override to perform
	// operations at the beginning of tasks.
	protected void setupPass(PixelSlice source) {
		// does nothing by default
	}

	// this method every scalar filter must implement
	protected abstract float filterPixel(PixelSlice source, int u, int v);
}
