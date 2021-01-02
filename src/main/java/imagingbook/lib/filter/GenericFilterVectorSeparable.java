package imagingbook.lib.filter;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.PixelPack;

/**
 * A generic vector multi-pass filter with exactly 2 passes.
 * @author WB
 * @version 2021/01/02
 */
public abstract class GenericFilterVectorSeparable extends GenericFilterVector {


	public GenericFilterVectorSeparable(ImageProcessor ip, OutOfBoundsStrategy obs) {
		super(ip, obs);
	}

	@Override
	protected final float[] filterPixel(PixelPack source, int u, int v) {
		switch (getPass()) {
		case 0: return filterPixelX(source, u, v);
		case 1: return filterPixelY(source, u, v);
		default: throw new RuntimeException("invalid pass number " + getPass());
		}
	}
	
	@Override
	protected final int passesNeeded() {
		return 2;	// this filter needs 2 passes
	}
	
	// ------------------------------------------------------------------------

	// Apply a 1D filter in x-direction
	protected abstract float[] filterPixelX(PixelPack source, int u, int v);

	// Apply a 1D filter in y-direction
	protected abstract float[] filterPixelY(PixelPack source, int u, int v);
	
}