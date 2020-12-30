package imagingbook.lib.filters2;

public class ExampleFilterScalarSeparable extends GenericFilterScalar {

	@Override
	protected float filterPixel(float[] source, int u, int v) {
		switch (pass) {
		case 0: return filterPixelX(source, u, v);
		case 1: return filterPixelY(source, u, v);
		}
		return 0; 
	}
	
	@Override
	protected boolean morePasses() {
		return (pass < 2);	// this filter needs 2 passes
	}

	private float filterPixelX(float[] source, int u, int v) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private float filterPixelY(float[] source, int u, int v) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
