package imagingbook.lib.filters2;

// PROBLEM - how should this WORK???
public class ExampleFilterScalarSeparable extends GenericFilterScalar {

	@Override
	protected float filterPixel(float[] source, int u, int v, int pass) {
		if (pass == 0) {
			return filterPixelX(source, u, v);
		}
		else {
			return filterPixelY(source, u, v);
		}
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
