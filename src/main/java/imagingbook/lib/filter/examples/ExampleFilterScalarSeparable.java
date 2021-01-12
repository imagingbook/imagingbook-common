package imagingbook.lib.filter.examples;

import imagingbook.lib.filter.GenericFilterScalar;
import imagingbook.lib.filter.linear.LinearFilterSeparable;
import imagingbook.lib.image.data.PixelPack.PixelSlice;

/**
 * See also {@link LinearFilterSeparable}.
 * @author WB
 * @deprecated
 */
public class ExampleFilterScalarSeparable extends GenericFilterScalar {

	@Override
	protected float doPixel(PixelSlice plane, int u, int v) {
		switch (getPass()) {
		case 0: return filterPixelX(plane, u, v);	// TODO: check if we nneed to copy back this plane??
		case 1: return filterPixelY(plane, u, v);
		default: throw new RuntimeException("invalid pass number " + getPass());
		}
	}
	
	@Override
	protected int passesRequired() {
		return 2;	// do exactly 2 passes
	}

	private float filterPixelX(PixelSlice source, int u, int v) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private float filterPixelY(PixelSlice source, int u, int v) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
