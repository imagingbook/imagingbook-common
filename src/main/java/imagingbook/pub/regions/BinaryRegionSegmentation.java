/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.regions;

import static imagingbook.pub.regions.NeighborhoodType.N4;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.basic.Point;

/**
 * Performs region segmentation on a given binary image.
 * This class is abstract, since the implementation depends
 * on the concrete region segmentation algorithm being used.
 * Concrete implementations (subclasses of this class) are 
 * {@link SegmentationBreadthFirst},
 * {@link SegmentationDepthFirst},
 * {@link SegmentationRecursive},
 * {@link SegmentationSequential},
 * {@link SegmentationRegionContour}.
 * 
 * All work is done by the constructor(s).
 * If the segmentation has failed for some reason,
 * {@link #getRegions()} returns {@code null}.
 * 
 * @version 2020/12/21
 */
public abstract class BinaryRegionSegmentation {
	
	public static final NeighborhoodType DEFAULT_NEIGHBORHOOD = N4;
	public static final int BACKGROUND = 0;
	public static final int FOREGROUND = 1;
	
	public static final int START_LABEL = 2;	// TODO: public for image generation only! check!
	
	protected final ImageProcessor ip;
	protected final int width;
	protected final int height;	
	protected final NeighborhoodType neighborhood;
	
	protected final int[][] labelArray;
	// label values in labelArray can be:
	//  0 ... unlabeled
	// -1 ... previously visited background pixel
	// >0 ... valid label
	protected final List<BinaryRegion> regions;
	
	private final boolean isSegmented;
	private int currentLabel = -1;
	protected int maxLabel = -1;	// the maximum label in the labels array
	
	
	// -------------------------------------------------------------------------
	
	protected BinaryRegionSegmentation(ByteProcessor ip, NeighborhoodType nh) {
		this.ip = ip;
		this.neighborhood = nh;
		this.width  = ip.getWidth();
		this.height = ip.getHeight();
		this.labelArray = makeLabelArray();
		this.isSegmented = applySegmentation();
		this.regions = (isSegmented) ? collectRegions() : null;
	}
	
	protected int[][] makeLabelArray() {
		int[][] lA = new int[width][height];	// label array
		// set all pixels to either FOREGROUND or BACKGROUND (by thresholding)
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				lA[u][v] = (ip.getPixel(u, v) > 0) ? FOREGROUND : BACKGROUND;
			}
		}
		return lA;
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * This method must be implemented by any concrete sub-class.
	 * @return true if successful.
	 */
	protected abstract boolean applySegmentation();
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getMaxLabel() {
		return maxLabel;
	}
	
	/**
	 * Get an unsorted list of all regions associated with this region labeling.
	 * See also {@link #getRegions(boolean)}.
	 * @return A (unsorted) list of regions or {@code null} if the segmentation has failed.
	 */
	public List<BinaryRegion> getRegions() {
		return getRegions(false);	// unsorted
	}
	
	/**
	 * Get a (optionally sorted) list of all regions associated with this region labeling.
	 * @param sort Set {@code true} to sort regions by size (largest regions first).
	 * @return The list of regions or {@code null} if the segmentation has failed.
	 */
	public List<BinaryRegion> getRegions(boolean sort) {
		if (regions == null) {
			return null;
		}
		else {
			BinaryRegion[] ra = regions.toArray(new BinaryRegion[0]);
			if (sort) {
				Arrays.sort(ra);
			}
			return Arrays.asList(ra);
		}
	}
	
	// -------------------------------------------------------------------------
	
	// creates a container of BinaryRegion objects
	// collects the region pixels from the label image
	// and computes the statistics for each region
	protected List<BinaryRegion> collectRegions() {
		BinaryRegion[] regionArray = new BinaryRegion[maxLabel + 1];
		for (int i = START_LABEL; i <= maxLabel; i++) {
			regionArray[i] = new BinaryRegion(i);
		}
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int label = getLabel(u, v);
				if (label >= START_LABEL && label <= maxLabel && regionArray[label] != null) {
					regionArray[label].addPixel(u, v);
				}
			}
		}
		// create a list of regions to return, collect nonempty regions
		List<BinaryRegion> regionList = new LinkedList<BinaryRegion>();
		for (BinaryRegion r: regionArray) {
			if (r != null && r.getSize() > 0) {
				r.update();	// compute the statistics for this region
				regionList.add(r);
			}
		}
		//regions = regionList;
		return regionList;
	}
	
	/**
	 * Get the label number for the specified image coordinate.
	 * @param u the horizontal coordinate.
	 * @param v the vertical coordinate.
	 * @return the label number for the given position.
	 */
	public int getLabel(int u, int v) {
		if (u >= 0 && u < width && v >= 0 && v < height)
			return labelArray[u][v];
		else
			return BACKGROUND;
	}
	
	protected void setLabel(int u, int v, int label) {
		if (u >= 0 && u < width && v >= 0 && v < height)
			labelArray[u][v] = label;
	}
	
	
	protected int getNextLabel() {
		if (currentLabel < 1)
			currentLabel = START_LABEL;
		else
			currentLabel = currentLabel + 1;
		maxLabel = currentLabel;
		return currentLabel;
	}

	
	// --------------------------------------------------

	/**
	 * Find the region associated to the given label.
	 * @param label the label number.
	 * @return the region object associated with the given label
	 * 		or {@code null} if it does not exist.
	 */
	public BinaryRegion findRegion(int label) {
		if (label <= 0 || regions == null) return null;
		BinaryRegion region = null;
		for (BinaryRegion r : regions) {	// TODO: inefficient, consider different data structure for regions!
			if (r.getLabel() == label) {
				region = r;
				break;
			}
		}
		return region;
	}
	
	/**
	 * Finds the {@link BinaryRegion} instance associated with
	 * the given image position.
	 * @param u the horizontal position.
	 * @param v the vertical position.
	 * @return The associated {@link BinaryRegion} object or null if
	 * this {@link BinaryRegionSegmentation} has no region at the given position.
	 */
	public BinaryRegion getRegion(int u, int v) {
		int label = getLabel(u, v);
		return findRegion(label);
	}
		
	// --------- Iteration over region pixels -----------------------------------
	
	/**
	 * Instances of this class are returned by {@link BinaryRegion#iterator()},
	 * which implements  {@link Iterable} for instances of class {@link Point}.
	 */
	private class RegionPixelIterator implements Iterator<Point> {
		private final int label;					// the corresponding region's label
		private final int uMin, uMax, vMin, vMax;	// coordinates of region's bounding box
		int uCur, vCur;								// current pixel position
		private Point pNext;						// coordinates of the next region pixel
		private boolean first;						// control flag

		RegionPixelIterator(BinaryRegion R) {
			label = R.getLabel();
			first = true;
			Rectangle bb = R.getBoundingBox();
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
		private Point findNext() {
			// start search for next region pixel at (u,v):
			int u = (first) ? uCur : uCur + 1;
			int v = vCur;
			first = false;
			while (v <= vMax) {
				while (u <= uMax) {
					if (getLabel(u, v) == label) { // next pixel found (uses surrounding labeling)
						uCur = u;
						vCur = v;
						return Point.create(uCur, vCur);
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
		public Point next() {
			if (pNext != null || hasNext()) {
				Point pn = pNext;
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

	} // end of RegionPixelIterator
	
	
	/**
	 * This class of {@link BinaryRegionSegmentation} represents a connected 
	 * component or binary region. 
	 * It is implemented as an inner class to {@link BinaryRegionSegmentation} because
	 * it references common region labeling data.
	 * A {@link BinaryRegion} instance does not have its own list or array of 
	 * contained pixel coordinates but refers to the region label-array of the
	 * enclosing {@link BinaryRegionSegmentation} instance.
	 * Instances of this class support iteration over the contained pixel
	 * coordinates of type {@link Point}, e.g., by
	 * <pre>
	 * import imagingbook.pub.geometry.basic.Point;
	 * 
	 * BinaryRegion R = ...;
	 * for (Point p : R) {
	 *    // process point p ...
	 * }
	 * </pre>
	 * The advantage of providing iteration only is that it avoids the
	 * creation of (possibly large) arrays of pixel coordinates.
	 */
	public class BinaryRegion implements Comparable<BinaryRegion>, Iterable<Point> {
		private final int label;				// the label of THIS region
		private int size = 0;
		private double xc = Double.NaN;
		private double yc = Double.NaN;
		private int left = Integer.MAX_VALUE;
		private int right = -1;
		private int top = Integer.MAX_VALUE;
		private int bottom = -1;
		
		private Contour outerContour;
		private List<Contour> innerContours;
		
		// summation variables used for various statistics
		private long x1Sum = 0;
		private long y1Sum = 0;
		private long x2Sum = 0;
		private long y2Sum = 0;
		private long xySum = 0;
		
		// ------- constructor --------------------------
		
		private BinaryRegion(int label) {
			this.label = label;
			this.outerContour = null;
			this.innerContours = null;
		}
		
		/**
		 * Obsolete - use {@link #getCentroid()} instead!
		 * Get the x-value of the region's centroid.
		 * @return the x-value of the region's centroid.
		 * @deprecated
		 */
		public double getXc() {
			return xc;
		}

		/**
		 * Obsolete - use {@link #getCentroid()} instead!
		 * Get the y-value of the region's centroid.
		 * @return the y-value of the region's centroid.
		 * @deprecated
		 */
		public double getYc() {
			return yc;
		}
		
		/**
		 * Returns the sum of the x-coordinates of the points
		 * contained in this region.
		 * 
		 * @return the sum of x-values.
		 */
		public long getX1Sum() {
			return x1Sum;
		}

		/**
		 * Returns the sum of the y-coordinates of the points
		 * contained in this region.
		 * 
		 * @return the sum of y-values.
		 */
		public long getY1Sum() {
			return y1Sum;
		}

		/**
		 * Returns the sum of the squared x-coordinates of the points
		 * contained in this region.
		 * 
		 * @return the sum of squared x-values.
		 */
		public long getX2Sum() {
			return x2Sum;
		}

		/**
		 * Returns the sum of the squared y-coordinates of the points
		 * contained in this region.
		 * 
		 * @return the sum of squared y-values.
		 */
		public long getY2Sum() {
			return y2Sum;
		}
		
		/**
		 * Returns the sum of the mixed x*y-coordinates of the points
		 * contained in this region.
		 * 
		 * @return the sum of xy-values.
		 */
		public long getXYSum() {
			return xySum;
		}

		// ------- public methods --------------------------
		
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
		 * Returns the centroid of this region as a 2D point.
		 * See also {@link getXc}, {@link getYc}.
		 * 
		 * @return the centroid of this region.
		 */
		public Point getCentroid() {
			if (Double.isNaN(xc))
				return null;
			else
				return Point.create(xc, yc);
		}
		
		// iteration --------------------------------------
		
		@Override
		public Iterator<Point> iterator() {
			return new RegionPixelIterator(this);
		}
		
		/**
		 * Use this method to add a single pixel to this region. Updates summation
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
		 * Call this method to update the region's statistics. For now only the
		 * center coordinates (xc, yc) are updated. Add additional statements as
		 * needed to update your own region statistics.
		 */
		protected void update() {
			if (size > 0) {
				xc = (double) x1Sum / size;
				yc = (double) y1Sum / size;
			}
		}
		
		/**
		 * Get the (single) outer contour of this region.
		 * Points on an outer contour are arranged in clockwise
		 * order.
		 * @return the outer contour.
		 */
		public Contour getOuterContour() {
			return outerContour;
		}
		
		protected void setOuterContour(Contour contr) {
			outerContour = contr;
		}
		
		/**
		 * Get all inner contours of this region.
		 * Points on inner contours are arranged in counter-clockwise
		 * order.
		 * @return the list of inner contours.
		 */
		public List<Contour> getInnerContours() {
			return innerContours;
		}
		
		protected void makeInnerContours() {
			if (innerContours == null) {
				innerContours = new LinkedList<Contour>();
			}
		}
		
		protected void addInnerContour(Contour contr) {
			makeInnerContours();
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
		
		// Compare method for sorting by region size (larger regions at front)
		@Override
		public int compareTo(BinaryRegion r2) {
			return r2.size - this.size;
		}
		
		/**
		 * Checks if the given pixel position is contained in this
		 * {@link BinaryRegion} instance.
		 * @param u x-coordinate
		 * @param v y-coordinate
		 * @return true if (u,v) is contained in this region
		 */
		public boolean contains(int u, int v) {
			return BinaryRegionSegmentation.this.getLabel(u, v) == this.label;
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
		
		private Map<String, Double> properties = null;
		
		/**
		 * Sets the specified property of this region to the given value.
		 * @param name Chosen name of the property.
		 * @param val Value associated with this property.
		 */
		public void setProperty(String name, double val) {
			if (properties == null) {
				properties = new HashMap<String, Double>();
			}
			properties.put(name, val);
		}
		
		/**
		 * Retrieves the specified region property. 
		 * {@link IllegalArgumentException} is thrown if the property 
		 * is not defined for this region.
		 * 
		 * @param name The name of the property.
		 * @return The value associated with the specified property.
		 */
		public double getProperty(String name) {
			Double value;
			if (properties == null || (value = properties.get(name)) == null) {
				throw new IllegalArgumentException("Region property " + name + " is undefined.");
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

}
