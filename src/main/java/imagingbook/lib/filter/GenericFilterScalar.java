package imagingbook.lib.filter;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.PixelPack;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.PixelPack.PixelSlice;

public abstract class GenericFilterScalar extends GenericFilter {
	
	protected GenericFilterScalar(ImageProcessor ip, OutOfBoundsStrategy obs) {
		super(ip, obs);
	}
	
	// apply filter to a stack of pixel planes (1 pass)
	protected void filterAll(PixelPack sources) {
		PixelSlice target = sources.getEmptySlice();
		int depth = sources.getDepth();
		for (int k = 0; k < depth; k++) {
			PixelSlice source = sources.getSlice(k);
			setupPass(source);
			filterSlice(source, target);	// default behavior: apply filter to each plane
			target.copyTo(source); // copy target back to sources
		}
	}
	
	protected void filterSlice(PixelSlice source, PixelSlice target) {
		for (int v = 0; v < this.imgHeight; v++) {
			for (int u = 0; u < this.imgWidth; u++) {
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
