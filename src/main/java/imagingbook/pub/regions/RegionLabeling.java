/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.regions;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

/**
 * This class does the complete region labeling for a given image.
 * It is abstract, because the implementation of some parts depends
 * on the region labeling algorithm being used.
 * 
 * @version 2018/04/01
 */
public abstract class RegionLabeling {
	
	static final int BACKGROUND = 0;
	static final int FOREGROUND = 1;
	static final int START_LABEL = 2;

	protected final ImageProcessor ip;
	protected final int width;
	protected final int height;
	
	protected final int[][] labelArray;
	// label values in labelArray can be:
	//  0 ... unlabeled
	// -1 ... previously visited background pixel
	// >0 ... valid label
	
	protected int currentLabel;
	protected int maxLabel;	// the maximum label in the labels array
	protected List<BinaryRegion> regions;
	
	protected RegionLabeling(ByteProcessor ip) {
		this.ip = ip;
		width  = ip.getWidth();
		height = ip.getHeight();
		labelArray = initialize();
		applyLabeling();
		collectRegions();
	}
	
	protected int[][]  initialize() {
		int[][] lA = new int[width][height];	// label array
		// set all pixels to either FOREGROUND or BACKGROUND (by thresholding)
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				lA[u][v] = (ip.getPixel(u, v) > 0) ? FOREGROUND : BACKGROUND;
			}
		}
		return lA;
	}
	
	/**
	 * Get an unsorted list of all regions associated with this region labeling.
	 * @return the list of regions.
	 */
	public List<BinaryRegion> getRegions() {
		return getRegions(false);	// unsorted
	}
	
	/**
	 * Get a possibly sorted list of all regions associated with this region labeling.
	 * @param sort set {@code true} to sort regions by decreasing size.
	 * @return the list of regions.
	 */
	public List<BinaryRegion> getRegions(boolean sort) {
		if (regions == null) 
			return Collections.emptyList();
		else {
			List<BinaryRegion> rns = new ArrayList<BinaryRegion>(regions);
			if (sort) {
				Collections.sort(rns);
			}
			return rns;
		}
	}
	
	// This method must be implemented by any real sub-class:
	abstract void applyLabeling();
	
	// creates a container of BinaryRegion objects
	// collects the region pixels from the label image
	// and computes the statistics for each region
	void collectRegions() {
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
		regions = regionList;
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
	
	protected void resetLabel() {
		currentLabel = -1;
		maxLabel = -1;
	}
	
	int getNextLabel() {
		if (currentLabel < 1)
			currentLabel = START_LABEL;
		else
			currentLabel = currentLabel + 1;
		maxLabel = currentLabel;
		return currentLabel;
	}
	
	int getMaxLabel() {
		return maxLabel;
	}
	
	// --------------------------------------------------
	
	/**
	 * Utility method that creates an image of the internal
	 * label array.
	 * 
	 * @param color set {@code false} to get a 16-bit grayscale image,
	 * {@code true} for an RGB (24-bit) color image.
	 * @return an image of the label array.
	 */
	public ImageProcessor makeLabelImage(boolean color) {
		return (color) ?  makeLabelImageColor() : makeLabelImageGray();
	}

	private ColorProcessor makeLabelImageColor() {
		int[] colorLUT = new int[maxLabel+1];
		for (int i = START_LABEL; i <= maxLabel; i++) {
			colorLUT[i] = makeRandomColor();
		}
		ColorProcessor cp = new ColorProcessor(width, height);
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int lb = getLabel(u, v);
				if (lb >= 0 && lb < colorLUT.length) {
					cp.putPixel(u, v, colorLUT[lb]);
				}
			}
		}
		return cp;
	}
	
	private ShortProcessor makeLabelImageGray() {
		ShortProcessor sp = new ShortProcessor(width, height);
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int lb = getLabel(u, v);
				sp.set(u, v, (lb >= 0) ? lb : 0);
			}
		}
		sp.resetMinAndMax();
		return sp;
	}
	

	/**
	 * Find the region associated to the given label.
	 * @param label the label number.
	 * @return the region object associated with the given label
	 * 		or {@code null} if it does not exist.
	 */
	public BinaryRegion findRegion(int label) {
		if (label <= 0 || regions == null) return null;
		BinaryRegion regn = null;
		for (BinaryRegion r : regions) {
			if (r.getLabel() == label) {
				regn = r;
				break;
			}
		}
		return regn;
	}
	
	/**
	 * Finds the {@link BinaryRegion} instance associated with
	 * the given image position.
	 * @param u the horizontal position.
	 * @param v the vertical position.
	 * @return The associated {@link BinaryRegion} object or null if
	 * this {@link RegionLabeling} has no region at the given position.
	 */
	public BinaryRegion getRegion(int u, int v) {
		int label = getLabel(u, v);
		return findRegion(label);
	}
	
	protected int makeRandomColor() {
		double saturation = 0.2;
		double brightness = 0.2;
		float h = (float) Math.random();
		float s = (float) (saturation * Math.random() + 1 - saturation);
		float b = (float) (brightness * Math.random() + 1 - brightness);
		return Color.HSBtoRGB(h, s, b);
	}
	
//	private void printSummary() {
//		if (regions != null) {
//			IJ.log("Number of regions detected: " + regions.size());
//			for (BinaryRegion r : regions) {
//				IJ.log(r.toString());
//			}
//		} else
//			IJ.log("No regions found.");
//	}
	
	
	// --------- Iteration over region pixels -----------------------------------
	
	/**
	 * @deprecated
	 * This method is not supported any longer.
	 * Instead use {@code Iterable<Point>} capability of {@link BinaryRegion}
	 * to iterate directly over region points.
	 * 
	 * @param r the binary region.
	 * @return an {@link Iterable} to iterate over all region points.
	 */
	public Iterable<Point> getRegionPoints(final BinaryRegion r) {
		return r.getRegionPoints();
	}
	
	private class RegionPixelIterator implements Iterator<Point> {
		final int label;					// the corresponding region's label
		final int uMin, uMax, vMin, vMax;	// coordinates of region's bounding box
		int uCur, vCur;						// current pixel position
		Point pNext;						// coordinates of the next region pixel
		boolean first;						// control flag

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
						return new Point(uCur, vCur);
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
	 * This class of {@link RegionLabeling} represents a connected 
	 * component or binary region. 
	 * It is implemented as an inner class to {@link RegionLabeling} because
	 * it references common region labeling data.
	 * Instances of this class support iteration over the contained pixel
	 * coordinates of type {@link Point}, e.g., by
	 * <pre>
	 * import java.awt.Point;
	 * 
	 * BinaryRegion R = ...;
	 * for (Point p : R) {
	 *    // process point p ...
	 * }
	 * </pre>
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
		 * Get the x-value of the region's centroid.
		 * @return the x-value of the region's centroid.
		 */
		public double getXc() {
			return xc;
		}

		/**
		 * Get the y-value of the region's centroid.
		 * @return the y-value of the region's centroid.
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
		 * @deprecated
		 * Returns the centroid of this region as a 2D point.
		 * Use {@link getCenterPoint} or {@link getXc} and {@link getYc} instead.
		 * @return the centroid of this region.
		 */
		public Point2D getCenter() {
			if (Double.isNaN(xc))
				return null;
			else
				return new Point2D.Double(xc, yc);
		}
		
		
		/**
		 * Returns the centroid of this region as a 2D point.
		 * See also {@link getXc}, {@link getYc}.
		 * 
		 * @return the centroid of this region.
		 */
		public Point2D getCenterPoint() {
			if (Double.isNaN(xc))
				return null;
			else
				return new Point2D.Double(xc, yc);
		}
		
		// iteration --------------------------------------
		
		@Override
		public Iterator<Point> iterator() {
			return new RegionPixelIterator(this);
		}
		
		/**
		 * @deprecated
		 * Replaced by BinaryRegion implementing {@code Iterable<Point>}.
		 * @return iterator to be used in a for-loop. 
		 */
		public Iterable<Point> getRegionPoints() {
			//return RegionLabeling.this.getRegionPoints(this);
			return new Iterable<Point>() {	// anonymous class!
				public Iterator<Point> iterator() {
					return new RegionPixelIterator(BinaryRegion.this);
				}
			};
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
		 * An {@link IllegalArgumentException} is thrown if the property 
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
