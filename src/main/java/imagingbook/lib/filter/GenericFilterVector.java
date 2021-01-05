package imagingbook.lib.filter;

import imagingbook.lib.image.access.PixelPack;

public abstract class GenericFilterVector extends GenericFilter {

	public GenericFilterVector(PixelPack pp) {
		super(pp);
	}

	@Override 
	protected void filterAll(PixelPack source) {
		PixelPack target = source.getEmptyCopy();
		setupPass(source);
		final int width = source.getWidth();
		final int height = source.getHeight();
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				target.setPixel(u, v, filterPixel(source, u, v)); // single pixel operation
			}
		}
		target.copyTo(source);	// copy targets back to sources
	}
	
	
	// called before every pass. override to perform
	// operations at the beginning of tasks.
	protected void setupPass(PixelPack source) {
		// does nothing by default
	}

	// calculate the result vector for a single pixel
	protected abstract float[] filterPixel(PixelPack sources, int u, int v);
	
	// -----------------------------------------------------------------
	
	// helper method for copying vector pixels
	public void copyPixel(float[] source, float[] target) {
		System.arraycopy(source, 0, target, 0, source.length);
	}

}
