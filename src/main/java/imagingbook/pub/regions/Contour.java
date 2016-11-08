/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.regions;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a closed contour as a sequence of
 * pixel coordinates. It implements the {@link Comparable}
 * interface for sorting contours by length.
 * It supports iteration over the points along the contour, 
 * e.g., by
 * <pre>
 * Contour C = ...;
 * for (Point p : C) {
 *    // process p ...
 * }
 * </pre>
 * 
 * @version 2016-11-08
 */
public class Contour implements Comparable<Contour>, Iterable<Point> {
	
	static private int INITIAL_SIZE = 50;
	
	private final int label;
	private final List<Point> points;
	
	/**
	 * Creates a new (empty) contour with the given region label.
	 * @param label the region label for this contour.
	 */
	public Contour (int label) {
		this.label = label;
		points = new ArrayList<Point>(INITIAL_SIZE);
	}
	
	protected void addPoint (Point p) {
		points.add(p);
	}
	
	//--------------------- retrieve contour points -------
	
	/**
	 * Get the list of contour points.
	 * @return a reference to the internal list of contour points.
	 */
	public List<Point> getPointList() {
		return points;
	}
	
	/**
	 * Get the contour points as an array.
	 * @return a new array of contour points.
	 */
	public Point[] getPointArray() {
		return points.toArray(new Point[0]);
	}
		
	//--------------------- contour statistics ------------
	
	/**
	 * Get the length of the contour.
	 * @return the number of points on the contour.
	 */
	public int getLength() {
		return points.size();
	}
	
	/**
	 * Get the region label associated with this contour.
	 * @return the region label of the contour.
	 */
	public int getLabel() {
		return label;
	}
	
	//--------------------- debug methods ------------------
	
//	private void printPoints () {
//		for (Point pt: points) {
//			IJ.log(pt.toString());
//		}
//	}
	
	@Override
	public String toString(){
		return
			"Contour " + label + ": " + this.getLength() + " points";
	}
	
	/**
	 * Get the polygon for this contour (for subsequent drawing).
	 * @return the polygon.
	 */
	public Path2D getPolygonPath() {
		return getPolygonPath(0.5, 0.5);	// offset by 0.5 to pass through pixel centers
	}
	
	/**
	 * Get the polygon for this contour (for subsequent drawing).
	 * An offset can be specified for shifting the contour positions
	 * at pixel centers (set to 0.5/0.5).
	 * 
	 * @param xOffset the horizontal offset.
	 * @param yOffset the vertical offset.
	 * @return a polygon.
	 */
	public Path2D getPolygonPath(double xOffset, double yOffset) {
		Path2D path = new Path2D.Float();
		Point[] pnts = this.getPointArray();
		if (pnts.length > 1){
			path.moveTo(pnts[0].x + xOffset, pnts[0].y + yOffset);
			for (int i = 1; i < pnts.length; i++) {
				path.lineTo(pnts[i].x + xOffset,  pnts[i].y + yOffset);
			}
			path.closePath();
		}
		else {	// mark single pixel region "X"
			double x = pnts[0].x;
			double y = pnts[0].y;
			path.moveTo(x + xOffset - 0.5, y + yOffset - 0.5);
			path.lineTo(x + xOffset + 0.5, y + yOffset + 0.5);
			path.moveTo(x + xOffset - 0.5, y + yOffset + 0.5);
			path.lineTo(x + xOffset + 0.5, y + yOffset - 0.5);
		}
		return path;
	}

		
	// Compare method for sorting contours by length (longer contours at front)
	@Override
	public int compareTo(Contour c2) {
		return c2.points.size() - this.points.size();
	}

	@Override
	public Iterator<Point> iterator() {
		return points.iterator();
	}

}
