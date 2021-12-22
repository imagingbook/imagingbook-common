package imagingbook.pub.regions.segment;

import java.awt.Rectangle;
import java.util.List;

import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.regions.Contour;

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
public interface BinaryRegion extends Comparable<BinaryRegion>, Iterable<Pnt2d> {

	/**
	 * Returns the sum of the x-coordinates of the points
	 * contained in this region.
	 * @return the sum of x-values.
	 */
	public long getX1Sum();

	/**
	 * Returns the sum of the y-coordinates of the points
	 * contained in this region.
	 * @return the sum of y-values.
	 */
	public long getY1Sum();

	/**
	 * Returns the sum of the squared x-coordinates of the points
	 * contained in this region.
	 * @return the sum of squared x-values.
	 */
	public long getX2Sum();

	/**
	 * Returns the sum of the squared y-coordinates of the points
	 * contained in this region.
	 * @return the sum of squared y-values.
	 */
	public long getY2Sum();

	/**
	 * Returns the sum of the mixed x*y-coordinates of the points
	 * contained in this region.
	 * @return the sum of xy-values.
	 */
	public long getXYSum();

	/**
	 * Calculates and returns a vector of (unnormalized) central moments:
	 * (mu10, mu01, mu20, mu02, mu11).
	 * @return vector of central moments
	 */
	public double[] getCentralMoments();

	/**
	 * Returns the 2x2 covariance matrix for the pixel coordinates
	 * contained in this region:
	 * <pre>
	 * | &sigma;_20 &sigma;_11 | 
	 * | &sigma;_11 &sigma;_02 | 
	 * </pre>
	 * @return the covariance matrix
	 */
	public double[][] getCovarianceMatrix();

	/**
	 * Get the label associated with this region.
	 * @return the region label.
	 */
	public int getLabel();		// remove!

	/**
	 * Get the size of this region.
	 * @return the number of region points.
	 */
	public int getSize();

	/**
	 * Get the x/y axes-parallel bounding box as a rectangle
	 * (including the extremal coordinates).
	 * @return the bounding box rectangle.
	 */
	public Rectangle getBoundingBox();


	/**
	 * Returns the center of this region as a 2D point.
	 * @return the center point of this region.
	 */
	public Pnt2d getCenter();

	public void setOuterContour(Contour.Outer contr);
	
	/**
	 * Get the (single) outer contour of this region.
	 * Points on an outer contour are arranged in clockwise
	 * order.
	 * @return the outer contour.
	 */
	public Contour.Outer getOuterContour();
	
	
	public void addInnerContour(Contour.Inner contr);

	/**
	 * Get all inner contours of this region.
	 * Points on inner contours are arranged in counter-clockwise order.
	 * @return the list of inner contours.
	 */
	public List<Contour.Inner> getInnerContours();	// sort!!!

	// Compare method for sorting by region size (larger regions at front)
	@Override
	public default int compareTo(BinaryRegion r2) {
		return r2.getSize() - this.getSize();
	}

	/**
	 * Checks if the given pixel position is contained in this
	 * {@link BinaryRegion} instance.
	 * @param u x-coordinate
	 * @param v y-coordinate
	 * @return true if (u,v) is contained in this region
	 */
	public boolean contains(int u, int v);
	
	/**
	 * Sets the specified property of this region to the given value.
	 * @param key The key of the property.
	 * @param val The value associated with this property.
	 */
	public void setProperty(Object key, double val);
	
	
	/**
	 * Retrieves the specified region property. 
	 * {@link IllegalArgumentException} is thrown if the property 
	 * is not defined for this region.
	 * 
	 * @param key The key of the property.
	 * @return The value associated with the specified property.
	 */
	public double getProperty(Object key);

}