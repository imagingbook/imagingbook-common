package imagingbook.pub.geometry.mappings2;

import imagingbook.pub.geometry.basic.Point;

public interface Mapping2D extends Cloneable {
	
	/**
	 * Applies this mapping to a single 2D point.
	 * 
	 * @param pnt the original point
	 * @return the transformed point
	 */
	Point applyTo (Point pnt);
	
	
	/**
	 * The inverse of this mapping is calculated (if possible)
	 * and returned. Implementing classes are supposed to
	 * override this default method.
	 * 
	 * @return the inverse mapping
	 */
	default Mapping2D getInverse() {
		throw new UnsupportedOperationException("Cannot invert mapping " + this.toString());
	}

}
