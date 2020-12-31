package imagingbook.lib.filter.examples;

import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilterScalar;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.image.access.FloatPixelPack.Slice;

public class ExampleFilterScalarSeparable extends GenericFilterScalar {

	protected ExampleFilterScalarSeparable(ImageProcessor ip, OutOfBoundsStrategy obs) {
		super(ip, obs);
		// TODO Auto-generated constructor stub
	}

//	@Override
//	protected float filterPixel(float[] source, int u, int v) {
//		switch (pass) {
//		case 0: return filterPixelX(source, u, v);
//		case 1: return filterPixelY(source, u, v);
//		}
//		return 0; 
//	}
	
	@Override
	protected float filterPixel(Slice source, int u, int v) {
		switch (pass) {
		case 0: return filterPixelX(source, u, v);
		case 1: return filterPixelY(source, u, v);
		}
		return 0; 
	}
	
	@Override
	protected boolean doMorePasses() {
		return (pass < 2);	// this filter needs 2 passes
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
