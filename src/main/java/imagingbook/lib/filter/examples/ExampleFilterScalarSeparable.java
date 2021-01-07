package imagingbook.lib.filter.examples;

import imagingbook.lib.filter.GenericFilterScalar;
import imagingbook.lib.filter.linear.LinearFilterSeparable;
import imagingbook.lib.image.access.PixelPack.PixelSlice;

/**
 * See also {@link LinearFilterSeparable}.
 * @author WB
 *
 */
public class ExampleFilterScalarSeparable extends GenericFilterScalar {

	protected ExampleFilterScalarSeparable() {
	}

	@Override
	protected float doPixel(int u, int v) {
		switch (getPass()) {
		case 0: return filterPixelX(source, u, v);
		case 1: return filterPixelY(source, u, v);
		default: throw new RuntimeException("invalid pass number " + getPass());
		}
	}
	
	@Override
	protected boolean finished() {
		return (getPass() >= 2);	// do exactly 2 passes
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
