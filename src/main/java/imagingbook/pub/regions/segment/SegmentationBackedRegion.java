package imagingbook.pub.regions.segment;

import java.awt.Rectangle;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.basic.Pnt2d.PntDouble;
import imagingbook.pub.geometry.basic.Pnt2d.PntInt;
import imagingbook.pub.regions.Contour;
import imagingbook.pub.regions.Contour.Inner;
import imagingbook.pub.regions.Contour.Outer;

/**
	 * This class represents a connected component or binary region. 
	 * It is implemented as a non-static inner class to {@link BinaryRegionSegmentation}
	 * because it references common region labeling data.
	 * A {@link SegmentationBackedRegion} instance does not have its own list or array of 
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
	public class SegmentationBackedRegion implements BinaryRegion {
		
		final int label;				// the label of THIS region
		private int size = 0;
		private double xc = Double.NaN;
		private double yc = Double.NaN;
		private int left = Integer.MAX_VALUE;
		private int right = -1;
		private int top = Integer.MAX_VALUE;
		private int bottom = -1;
		
		private Contour.Outer outerContour = null;
		private List<Contour.Inner> innerContours = null;
		
		// summation variables used for various statistics
		private long x1Sum = 0;
		private long y1Sum = 0;
		private long x2Sum = 0;
		private long y2Sum = 0;
		private long xySum = 0;
		
		private final BinaryRegionSegmentation segmentation;
		
		// ------- constructor --------------------------
		
		SegmentationBackedRegion(int label, BinaryRegionSegmentation seg) {
			this.label = label;
			this.segmentation = seg;
		}
		
		// ------- public methods --------------------------
		
		/**
		 * Returns the sum of the x-coordinates of the points
		 * contained in this region.
		 * @return the sum of x-values.
		 */
		public long getX1Sum() {
			return x1Sum;
		}

		/**
		 * Returns the sum of the y-coordinates of the points
		 * contained in this region.
		 * @return the sum of y-values.
		 */
		public long getY1Sum() {
			return y1Sum;
		}

		/**
		 * Returns the sum of the squared x-coordinates of the points
		 * contained in this region.
		 * @return the sum of squared x-values.
		 */
		public long getX2Sum() {
			return x2Sum;
		}

		/**
		 * Returns the sum of the squared y-coordinates of the points
		 * contained in this region.
		 * @return the sum of squared y-values.
		 */
		public long getY2Sum() {
			return y2Sum;
		}
		
		/**
		 * Returns the sum of the mixed x*y-coordinates of the points
		 * contained in this region.
		 * @return the sum of xy-values.
		 */
		public long getXYSum() {
			return xySum;
		}
		
		/**
		 * Calculates and returns a vector of (unnormalized) central moments:
		 * (mu10, mu01, mu20, mu02, mu11).
		 * @return vector of central moments
		 */
		public double[] getCentralMoments() {
			if (size == 0) {
				throw new IllegalStateException("empty region, moments are undefined");
			}
			double n = size;
//			double mu10 = ;	// always zero
//			double mu01 = ; // always zero
			double mu20 = x2Sum - x1Sum * x1Sum / n;
			double mu02 = y2Sum - y1Sum * y1Sum / n;
			double mu11 = xySum - x1Sum * y1Sum / n;
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
			double[] mu = getCentralMoments(); // = (mu20, mu02, mu11)
			double[][] S = {
					{mu[0]/size, mu[2]/size},
					{mu[2]/size, mu[1]/size}};
			return S;
		}
		
		/**
		 * Get the label associated with this region.
		 * @return the region label.
		 */
		public int getLabel() {
			return this.label;
		}
		
		/**
		 * Get the size of this region.
		 * @return the number of region points.
		 */
		public int getSize() {
			return this.size;
		}
		
		/**
		 * Get the x/y axes-parallel bounding box as a rectangle
		 * (including the extremal coordinates).
		 * @return the bounding box rectangle.
		 */
		public Rectangle getBoundingBox() {
			if (right < 0)
				return null;
			else
				return new Rectangle(left, top, right-left + 1, bottom - top + 1);
		}
	
		
		/**
		 * Returns the center of this region as a 2D point.
		 * @return the center point of this region.
		 */
		public Pnt2d getCenter() {
			if (size == 0) {
				throw new IllegalStateException("empty region, center is undefined");
			}
			return PntDouble.from((double) x1Sum / size, (double) y1Sum / size);
		}
		
		@Override
		public Iterator<Pnt2d> iterator() {
			return new RegionPixelIterator();
		}
		
		/**
		 * Adds a single pixel to this region and updates summation
		 * and boundary variables used to calculate various region statistics.
		 * 
		 * @param u x-position
		 * @param v y-position
		 */
		protected void addPixel(int u, int v) {
			size = size + 1;
			x1Sum = x1Sum + u;
			y1Sum = y1Sum + v;
			x2Sum = x2Sum + u * u;
			y2Sum = y2Sum + v * v;
			xySum = xySum + u * v;
			if (u < left)   left = u;
			if (v < top)    top = v;
			if (u > right)  right = u;
			if (v > bottom)	bottom = v;
		}
		
		/**
		 * Updates the region's statistics. 
		 * Does nothing but may be overridden by inheriting classes.
		 */
		protected void update() {
		}
		
		/**
		 * Get the (single) outer contour of this region.
		 * Points on an outer contour are arranged in clockwise
		 * order.
		 * @return the outer contour.
		 */
		public Contour.Outer getOuterContour() {
			return outerContour;
		}
		
		public void setOuterContour(Contour.Outer contr) {
			outerContour = contr;
		}
		
		/**
		 * Get all inner contours of this region.
		 * Points on inner contours are arranged in counter-clockwise order.
		 * @return the list of inner contours.
		 */
		public List<Contour.Inner> getInnerContours() {	// sort!!!
			return innerContours;
		}
		
		public void addInnerContour(Contour.Inner contr) {
			if (innerContours == null) {
				innerContours = new LinkedList<>();
			}
			innerContours.add(contr);
		}
		
		public String toString() {
			Formatter fm = new Formatter(new StringBuilder(), Locale.US);
			fm.format("Region %d", label);
			fm.format(", area = %d", size);
			fm.format(", bounding box = (%d, %d, %d, %d)", left, top, right, bottom );
			fm.format(", centroid = (%.2f, %.2f)", xc, yc);
			if (innerContours != null)
				fm.format(", holes = %d", innerContours.size());
			String s = fm.toString();
			fm.close();
			return s;
		}
		
//		// Compare method for sorting by region size (larger regions at front)
//		@Override
//		public int compareTo(SegmentationBackedRegion r2) {
//			return r2.size - this.size;
//		}
		
		/**
		 * Checks if the given pixel position is contained in this
		 * {@link SegmentationBackedRegion} instance.
		 * @param u x-coordinate
		 * @param v y-coordinate
		 * @return true if (u,v) is contained in this region
		 */
		public boolean contains(int u, int v) {
//			return BinaryRegionSegmentation.this.getLabel(u, v) == this.label;
			return segmentation.getLabel(u, v) == this.label;
		}
		
		// ------------------------------------------------------------
		// ------------------------------------------------------------
		
		
		/* EXPERIMENTAL STUFF for attaching region properties dynamically:
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
		
		/**
		 * Instances of this class are returned by {@link SegmentationBackedRegion#iterator()},
		 * which implements  {@link Iterable} for instances of class {@link Pnt2d}.
		 */
		private class RegionPixelIterator implements Iterator<Pnt2d> {
			private final int label;					// the corresponding region's label
			private final int uMin, uMax, vMin, vMax;	// coordinates of region's bounding box
			private int uCur, vCur;						// current pixel position
			private Pnt2d pNext;						// coordinates of the next region pixel
			private boolean first;						// control flag

			RegionPixelIterator() {
				label = SegmentationBackedRegion.this.getLabel();
				Rectangle bb = SegmentationBackedRegion.this.getBoundingBox();
				first = true;
				uMin = bb.x;
				uMax = bb.x + bb.width;
				vMin = bb.y;
				vMax = bb.y + bb.height;
				uCur = uMin;
				vCur = vMin;
				pNext = null;
			}

			/** 
			 * Search from position (uCur, vCur) for the next valid region pixel.
			 * Return the next position as a Point or null if no such point can be found.
			 * Don't assume that (uCur, vCur) is a valid region pixel!
			 * 
			 * @return the next point
			 */
			private Pnt2d.PntInt findNext() {
				// start search for next region pixel at (u,v):
				int u = (first) ? uCur : uCur + 1;
				int v = vCur;
				first = false;
				while (v <= vMax) {
					while (u <= uMax) {
						if (segmentation.getLabel(u, v) == label) { // next pixel found (uses surrounding labeling)
							uCur = u;
							vCur = v;
							return PntInt.from(uCur, vCur);
						}
						u++;
					}
					v++;
					u = uMin;
				}
				uCur = uMax + 1;	// just to make sure we'll never enter the loop again
				vCur = vMax + 1;
				return null;		// no next pixel found
			}

			@Override
			public boolean hasNext() {
				if (pNext != null) {	// next element has been queried before but not consumed
					return true;
				}
				else {
					pNext = findNext();	// keep next pixel coordinates in pNext
					return (pNext != null);
				}
			}

			// Returns: the next element in the iteration
			// Throws: NoSuchElementException - if the iteration has no more elements.
			@Override
			public Pnt2d next() {
				if (pNext != null || hasNext()) {
					Pnt2d pn = pNext;
					pNext = null;		// "consume" pNext
					return pn;
				}
				else {
					throw new NoSuchElementException();
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		} // end of class RegionPixelIterator

	} // end of class BinaryRegion