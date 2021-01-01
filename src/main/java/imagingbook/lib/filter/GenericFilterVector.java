package imagingbook.lib.filter;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.FloatPixelPack;
import imagingbook.lib.image.access.OutOfBoundsStrategy;

public abstract class GenericFilterVector extends GenericFilter {
	
	protected GenericFilterVector(ImageProcessor ip, OutOfBoundsStrategy obs) {
		super(ip, obs);
	}

	@Override 
	protected void filterAll(FloatPixelPack sources) {
		FloatPixelPack targets = sources.getEmptyCopy();
		for (int v = 0; v < this.imageHeight; v++) {
			for (int u = 0; u < this.imageWidth; u++) {
				targets.setPixel(u, v, filterPixel(sources, u, v)); // single pixel operation
			}
		}
		targets.copyTo(sources);	// copy targets back to sources
	}
	
	
	public void copyPixel(float[] source, float[] target) {
		System.arraycopy(source, 0, target, 0, source.length);
	}

	// calculate the result vector for a single pixel
	protected abstract float[] filterPixel(FloatPixelPack sources, int u, int v);

}
