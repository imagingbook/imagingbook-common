package imagingbook.pub.geometry.mappings;

import imagingbook.pub.geometry.basic.Point;

public interface Mapping2D extends Cloneable {
	
	/**
	 * Applies this mapping to a single 2D point.
	 * 
	 * @param pnt the original point
	 * @return the transformed point
	 */
	Point applyTo (Point pnt);
	
	default Point[] applyTo(Point[] pnts) {
		Point[] outPnts = new Point[pnts.length];
		for (int i = 0; i < pnts.length; i++) {
			outPnts[i] = applyTo(pnts[i]);
		}
		return outPnts;
	}
	
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

	/**
	 * Returns the Jacobian matrix for this mapping, evaluated at
	 * the given 2D point.
	 * This method is only implemented for selected mappings.
	 * @param xy the 2D position to calculate the Jacobian for
	 * @return the Jacobian matrix
	 */
	default double[][] getJacobian(Point xy) {
		throw new UnsupportedOperationException("No Jacobian available for this mapping");
	}
}
