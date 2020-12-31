package imagingbook.lib.filter;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.FloatPixelPack;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.FloatPixelPack.Slice;

public abstract class GenericFilterScalar extends GenericFilter {
	
	protected GenericFilterScalar(ImageProcessor ip, OutOfBoundsStrategy obs) {
		super(ip, obs);
	}
	
	// apply filter to a stack of pixel planes
	protected void filterAll(FloatPixelPack sources) {
		Slice target = sources.getEmptySlice();
		int depth = sources.getDepth();
		for (int k = 0; k < depth; k++) {
			Slice source = sources.getSlice(k);
			filterSlice(source, target);	// default behavior: apply filter to each plane
			target.copyTo(source); // copy target back to sources
		}
	}
	
	protected void filterSlice(Slice source, Slice target) {
		for (int v = 0; v < this.imageHeight; v++) {
			for (int u = 0; u < this.imageWidth; u++) {
				target.setVal(u, v, filterPixel(source, u, v));
			}
		}
	}

	// this method every scalar filter must implement
	protected abstract float filterPixel(Slice source, int u, int v);
}
