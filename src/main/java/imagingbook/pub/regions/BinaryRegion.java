package imagingbook.pub.regions;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.basic.Pnt2d.PntDouble;
import imagingbook.pub.geometry.basic.Pnt2d.PntInt;
import imagingbook.pub.regions.segment.BinaryRegionSegmentation;

/**
 * This class represents a connected component or binary region. 
 * It is implemented as a non-static inner class to {@link BinaryRegionSegmentation}
 * because it references common region labeling data.
 * A {@link BinaryRegion} instance does not have its own list or array of 
 * contained pixel coordinates but refers to the label array of the
 * enclosing {@link BinaryRegionSegmentation} instance.
 * Instances of this class support iteration over the contained pixel
 * coordinates of type {@link Pnt2d}, e.g., by
 * <pre>
 * import imagingbook.pub.geometry.basic.Point;
 * 
 * BinaryRegion R = ...;
 * for (Point p : R) {
 *    // process point p ...
 * }</pre>
 * The advantage of providing iteration only is that it avoids the
 * creation of (possibly large) arrays of pixel coordinates.
 */
public abstract class BinaryRegion implements Comparable<BinaryRegion>, Iterable<Pnt2d> {

	/**
	 * Returns the sum of the x-coordinates of the points
	 * contained in this region.
	 * @return the sum of x-values.
	 */
	public abstract long getX1Sum();

	/**
	 * Returns the sum of the y-coordinates of the points
	 * contained in this region.
	 * @return the sum of y-values.
	 */
	public abstract long getY1Sum();

	/**
	 * Returns the sum of the squared x-coordinates of the points
	 * contained in this region.
	 * @return the sum of squared x-values.
	 */
	public abstract long getX2Sum();

	/**
	 * Returns the sum of the squared y-coordinates of the points
	 * contained in this region.
	 * @return the sum of squared y-values.
	 */
	public abstract long getY2Sum();

	/**
	 * Returns the sum of the mixed x*y-coordinates of the points
	 * contained in this region.
	 * @return the sum of xy-values.
	 */
	public abstract long getXYSum();

	/**
	 * Calculates and returns a vector of (unnormalized) central moments:
	 * (mu10, mu01, mu20, mu02, mu11).
	 * @return vector of central moments
	 */
	public double[] getCentralMoments() {
		final int n = this.getSize();
		if (n == 0) {
			throw new IllegalStateException("empty region, moments are undefined");
		}
		double mu20 = getX2Sum() - getX1Sum() * getX1Sum() / (double) n;
		double mu02 = getY2Sum() - getY1Sum() * getY1Sum() / (double) n;
		double mu11 = getXYSum() - getX1Sum() * getY1Sum() / (double) n;
		return new double[] {mu20, mu02, mu11};
	}

	/**
	 * Returns the 2x2 covariance matrix for the pixel coordinates
	 * contained in this region:
	 * <pre>
	 * | &sigma;_20 &sigma;_11 | 
	 * | &sigma;_11 &sigma;_02 | 
	 * </pre>
	 * @return the covariance matrix
	 */
	public double[][] getCovarianceMatrix() {
		final int n = this.getSize();
		double[] mu = getCentralMoments(); // = [mu20, mu02, mu11]
		double[][] S = {
				{mu[0]/n, mu[2]/n},
				{mu[2]/n, mu[1]/n}};
		return S;
	}


	/**
	 * Get the size of this region.
	 * @return the number of region points.
	 */
	public abstract int getSize();

	/**
	 * Get the x/y axes-parallel bounding box as a rectangle
	 * (including the extremal coordinates).
	 * @return the bounding box rectangle.
	 */
	public abstract Rectangle getBoundingBox();


	/**
	 * Returns the center of this region as a 2D point.
	 * @return the center point of this region.
	 */
	public Pnt2d getCenter() {
		final int n = this.getSize();
		if (n == 0) {
			throw new IllegalStateException("empty region, center is undefined");
		}
		return PntDouble.from(((double)this.getX1Sum())/n, ((double)this.getY1Sum())/n);
	}

	public abstract void setOuterContour(Contour.Outer contr);

	/**
	 * Get the (single) outer contour of this region.
	 * Points on an outer contour are arranged in clockwise
	 * order.
	 * @return the outer contour.
	 */
	public abstract Contour getOuterContour();

	/**
	 * Get all inner contours of this region.
	 * Points on inner contours are arranged in counter-clockwise order.
	 * @return the list of inner contours.
	 */
	public abstract List<Contour> getInnerContours();	// sort!!!

	// Compare method for sorting by region size (larger regions at front)
	@Override
	public int compareTo(BinaryRegion other) {
//		return other.getSize() - this.getSize();
		return Integer.compare(other.getSize(), this.getSize());
	}

	/**
	 * Checks if the given pixel position is contained in this
	 * {@link BinaryRegion} instance.
	 * @param u x-coordinate
	 * @param v y-coordinate
	 * @return true if (u,v) is contained in this region
	 */
	public abstract boolean contains(int u, int v);

	// ------------------------------------------------------------
	// ------------------------------------------------------------


	/* Methods for attaching region properties dynamically.
	 * Properties can be used to hash results of region calculations
	 * to avoid multiple calculations.
	 * Currently, only 'double' values are supported.
	 * 
	 * E.g. calculate major axis angle theta for region r, then do
	 *    r.setProperty("angle", theta);
	 * and subsequently
	 *    double theta = r.getProperty("angle");
	 */

	private Map<Object, Double> properties = null;

	/**
	 * Sets the specified property of this region to the given value.
	 * @param key The key of the property.
	 * @param val The value associated with this property.
	 */
	public void setProperty(Object key, double val) {
		if (key == null) {
			throw new IllegalArgumentException("property key must not be null");
		}
		if (properties == null) {
			properties = new HashMap<>();
		}
		properties.put(key, val);
	}

	/**
	 * Retrieves the specified region property. 
	 * {@link IllegalArgumentException} is thrown if the property 
	 * is not defined for this region.
	 * 
	 * @param key The key of the property.
	 * @return The value associated with the specified property.
	 */
	public double getProperty(Object key) {
		if (key == null) {
			throw new IllegalArgumentException("property key must not be null");
		}
		Double value;
		if (properties == null || (value = properties.get(key)) == null) {
			throw new IllegalArgumentException("Region property " + key + " is undefined.");
		}
		return value.doubleValue();
	}

	/**
	 * Removes all properties attached to this region.
	 */
	public void clearProperties() {
		properties.clear();
	}
	

}