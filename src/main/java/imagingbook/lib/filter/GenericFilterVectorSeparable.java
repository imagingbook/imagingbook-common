package imagingbook.lib.filter;

import imagingbook.lib.image.access.PixelPack;

/**
 * A generic vector multi-pass filter with exactly 2 passes.
 * @author WB
 * @version 2021/01/02
 */
public abstract class GenericFilterVectorSeparable extends GenericFilterVector {

	@Override
	protected final float[] doPixel(PixelPack pack, int u, int v) {
		switch (getPass()) {
		case 0: return filterPixelX(pack, u, v);
		case 1: return filterPixelY(pack, u, v);
		default: throw new RuntimeException("invalid pass number " + getPass());
		}
	}
	
	@Override
	protected final boolean finished() {
		return (getPass() >= 2);	// do exactly 2 passes
	}
	
	// ------------------------------------------------------------------------

	// Apply a 1D filter in x-direction
	protected abstract float[] filterPixelX(PixelPack pack, int u, int v);

	// Apply a 1D filter in y-direction
	protected abstract float[] filterPixelY(PixelPack pack, int u, int v);
	
}
