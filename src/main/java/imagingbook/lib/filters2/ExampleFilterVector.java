package imagingbook.lib.filters2;

public class ExampleFilterVector extends GenericFilterVector {

	@Override
	protected float[] filterPixel(float[][] sources, int u, int v, int pass) {
		// TODO calculate and return the filter result for pixel u,v
		return new float[sources.length];
	}

}
