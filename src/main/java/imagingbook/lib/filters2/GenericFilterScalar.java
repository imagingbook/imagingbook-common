package imagingbook.lib.filters2;

public abstract class GenericFilterScalar extends GenericFilter {
	
	int w = 300;
	int h = 200;
	float[] target = null;	// sucks!
	
	// apply filter to a stack of pixel planes
	protected void applyTo(float[][] sources) {
		int depth = sources.length;
		if (target == null) 
			target = new float[sources[0].length];
		for (int k = 0; k < depth; k++) {
			filterComponent(sources[k], target);	// default behavior: apply filter to each plane
			// copy target back to sources
			System.arraycopy(target, 0, sources[k], 0, target.length);
		}
	}
	
	// apply filter to a single pixel plane (scalar)
	protected void filterComponent(float[] source, float[] target) {
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int i = 0; // the pixel index for (u,v)
				float p = filterPixel(source, u, v);	// single pixel operation
				target[i] = p; // copy result to target
			}
		}
	}

	// this method every scalar filter must implement
	protected abstract float filterPixel(float[] source, int u, int v);
}
