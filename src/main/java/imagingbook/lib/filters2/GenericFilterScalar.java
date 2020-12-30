package imagingbook.lib.filters2;

public abstract class GenericFilterScalar extends GenericFilter {
	
	int w = 300;
	int h = 200;
	
	// apply filter to a stack of pixel planes
	protected void applyTo(float[][] sources, float[][] targets, int pass) {
		int depth = sources.length;
		for (int k = 0; k < depth; k++) {
			filterComponent(sources[k], targets[k], pass);	// default behavior: apply filter to each plane
		}
		
		// copy targets back to sources
		for (int k = 0; k < depth; k++) {
			System.arraycopy(targets[k], 0, sources[k], 0, targets[k].length);
		}
	}
	
	// apply filter to a single pixel plane (scalar)
	protected void filterComponent(float[] source, float[] target, int pass) {
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int i = 0; // the pixel index for (u,v)
				float p = filterPixel(source, u, v, pass);	// single pixel operation
				target[i] = p; // copy result to target
			}
		}
	}

	// this method every scalar filter must implement
	protected abstract float filterPixel(float[] source, int u, int v, int pass);
}
