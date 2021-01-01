package imagingbook.lib.filter.examples;

import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilterScalar;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.FloatPixelPack.Slice;

public class ExampleFilterScalarSeparable extends GenericFilterScalar {

	protected ExampleFilterScalarSeparable(ImageProcessor ip, OutOfBoundsStrategy obs) {
		super(ip, obs);
	}

	@Override
	protected float filterPixel(Slice source, int u, int v) {
		switch (getPass()) {
		case 0: return filterPixelX(source, u, v);
		case 1: return filterPixelY(source, u, v);
		default: throw new RuntimeException("invalid pass number " + getPass());
		}
	}
	
	@Override
	protected int passesNeeded() {
		return 2;	// this filter needs 2 passes
	}

	private float filterPixelX(Slice source, int u, int v) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private float filterPixelY(Slice source, int u, int v) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
