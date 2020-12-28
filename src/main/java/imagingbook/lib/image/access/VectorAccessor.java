package imagingbook.lib.image.access;

import ij.process.ImageProcessor;
import imagingbook.lib.interpolation.InterpolationMethod;

/** 
 * Accessor for vector-valued images with arbitrary depth 
 * (number of components).
 *
 */
public abstract class VectorAccessor extends ImageAccessor {
	
	protected final int depth;
	protected final ScalarAccessor[] componentAccessors;

	protected VectorAccessor(ImageProcessor ip, int depth, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		super(ip, obs, ipm);
		this.depth = depth;
		this.componentAccessors = new ScalarAccessor[this.depth];
		for (int k = 0; k < depth; k++) {
			componentAccessors[k] = makeComponentAccessor(k);
		}
	}
	
	/**
	 * Real sublasses must know how to create an accessor to
	 * the image's k-th component.
	 * 
	 * @param k
	 * @return
	 */
	protected abstract ScalarAccessor makeComponentAccessor(int k);
	
	@Override
	public int getDepth() {
		return this.depth;
	}
	
	@Override
	public ScalarAccessor getComponentAccessor(int k) {
		checkComponentIndex(k);
		return componentAccessors[k];
	}
	
	@Override
	public float getVal(int u, int v, int k) {
		checkComponentIndex(k);
		return componentAccessors[k].getVal(u, v);
	}
	
	@Override
	public float getVal(double x, double y, int k) {
		checkComponentIndex(k);
		return componentAccessors[k].getVal(x, y);
	}
	
	@Override
	public void setVal(int u, int v, int k, float val) {
		checkComponentIndex(k);
		componentAccessors[k].setVal(u, v, val);
	}
	
	// ---------------------------------------------------------------------
	
	@Override
	public void setDefaultValue(float val) {
		for (int k = 0; k < depth; k++) {
			componentAccessors[k].setDefaultValue(val);
		}
	}
	
	@Override
	public void setDefaultValue(float[] vals) {
		for (int k = 0; k < depth; k++) {
			componentAccessors[k].setDefaultValue(vals[k]);
		}
	}
	
	protected void checkComponentIndex(int k) {
		if (k < 0 || k >= depth) {
			throw new IllegalArgumentException("invalid component index " + k);
		}
	}

}
