/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/

package imagingbook.pub.regions;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import ij.IJ;

/**
 * Updated/checked: 2014-11-12
 *
 */
public class Contour implements Comparable<Contour> {
	
	static int INITIAL_SIZE = 50;
	
	private int label;
	private List<Point> points;
	
	public Contour (int label) {
		this.label = label;
		points = new ArrayList<Point>(INITIAL_SIZE);
	}
	
	protected void addPoint (Point p) {
		points.add(p);
	}
	
	//--------------------- retrieve contour points -------
	
	public List<Point> getPointList() {
		return points;
	}
	
	public Point[] getPointArray() {
		return points.toArray(new Point[0]);
	}
		
	//--------------------- contour statistics ------------
	
	public int getLength() {
		return points.size();
	}
	
	public int getLabel() {
		return label;
	}
	
	//--------------------- debug methods ------------------
	
	public void printPoints () {
		for (Point pt: points) {
			IJ.log(pt.toString());
		}
	}
	
	public String toString(){
		return
			"Contour " + label + ": " + this.getLength() + " points";
	}
	
	//--------------------- drawing ------------	
	
	public Path2D getPolygonPath() {
		return getPolygonPath(0.5, 0.5);	// offset by 0.5 to pass through pixel centers
	}
	
	public Path2D getPolygonPath(double xOffset, double yOffset) {
		Path2D path = new Path2D.Float();
		Point[] pnts = this.getPointArray();
		if (pnts.length > 1){
			path.moveTo(pnts[0].x + xOffset, pnts[0].y + yOffset);
			for (int i=1; i<pnts.length; i++) {
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

	
	//--------------------- compare method for sorting ------------------
	
	// Compare method for sorting contours by length (longer contours at front)
	public int compareTo(Contour c2) {
		return c2.points.size() - this.points.size();
	}

}
