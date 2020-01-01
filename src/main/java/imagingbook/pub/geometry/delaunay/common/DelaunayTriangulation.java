package imagingbook.pub.geometry.delaunay.common;

import java.util.Collection;

import imagingbook.pub.geometry.delaunay.common.Point;
import imagingbook.pub.geometry.delaunay.common.Triangle;

public interface DelaunayTriangulation {
	
	Collection<Triangle> getTriangles();
	Collection<Point> getPoints();

}
