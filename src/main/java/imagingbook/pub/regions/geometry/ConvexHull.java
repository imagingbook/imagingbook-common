/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.pub.regions.geometry;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHull2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.MonotoneChain;

import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.regions.Contour;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;

/**
 * This class serves to calculate the convex hull of a binary region
 * or a closed contour, given as a sequence of point coordinates.
 * It is based on the convex hull implementation provided by the
 * Apache Commons Math library, in particular the classes 
 * {@link ConvexHull2D} and {@link MonotoneChain}.
 * If a region provides an outer contour, then this is used for 
 * the convex hull computation, otherwise the entire (inner) region
 * is used.
 * 
 * @author W. Burger
 * @version 2020/04/01
 */
public class ConvexHull {
	
	private final ConvexHull2D hull;
	
	// public constructors ------------------------
	
	public ConvexHull(BinaryRegion r) {
		Contour c = r.getOuterContour();
		if (c != null) {
			hull = makeConvexHull(collectPointList(c));
		}
		else {
			hull = makeConvexHull(collectPointList(r));
		}
	}
	
	public ConvexHull(Contour ctr) {
		hull = makeConvexHull(collectPointList(ctr));
	}
	
	// public methods ------------------------
	
	public Point[] getVertices() {
		return toPoint2D(hull.getVertices());
	}
	
	// --------------------------------------------------------------------
	
	private Collection<Vector2D> collectPointList(Contour ctr) {
//		IJ.log("making hull from contour");
		Collection<Vector2D> points = new ArrayList<Vector2D>();
		for (Point p : ctr.getPointArray()) {
			points.add(new Vector2D(p.getX(), p.getY()));
		}
		return points;
	}
	
	private Collection<Vector2D> collectPointList(BinaryRegion r) {
//		IJ.log("making hull from all region points");
		Collection<Vector2D> points = new ArrayList<Vector2D>();
		for (Point p : r) {
			points.add(new Vector2D(p.getX(), p.getY()));
		}
		return points;
	}
	
	private ConvexHull2D makeConvexHull(Collection<Vector2D> points) {
		return new MonotoneChain().generate(points);
	}
	
	private Point[] toPoint2D(Vector2D[] vecs) {
		Point[] pnts = new Point[vecs.length];
		for (int i = 0; i < vecs.length; i++) {
			pnts[i] = Point.create(vecs[i].getX(), vecs[i].getY());
		}
		return pnts;
	}

}
