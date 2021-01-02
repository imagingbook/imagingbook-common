package imagingbook.lib.filter;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.PixelPack;

public abstract class GenericFilterVector extends GenericFilter {
	
	protected GenericFilterVector(ImageProcessor ip, OutOfBoundsStrategy obs) {
		super(ip, obs);
	}

	@Override 
	protected void filterAll(PixelPack sources) {
		PixelPack targets = sources.getEmptyCopy();
		setupPass(sources);
		for (int v = 0; v < this.imgHeight; v++) {
			for (int u = 0; u < this.imgWidth; u++) {
				targets.setPixel(u, v, filterPixel(sources, u, v)); // single pixel operation
			}
		}
		targets.copyTo(sources);	// copy targets back to sources
	}
	
	
	// called before every pass. override to perform
	// operations at the beginning of tasks.
	protected void setupPass(PixelPack source) {
		// does nothing by default
	}

	// calculate the result vector for a single pixel
	protected abstract float[] filterPixel(PixelPack sources, int u, int v);
	
	// -----------------------------------------------------------------
	
	public void copyPixel(float[] source, float[] target) {
		System.arraycopy(source, 0, target, 0, source.length);
	}

}
