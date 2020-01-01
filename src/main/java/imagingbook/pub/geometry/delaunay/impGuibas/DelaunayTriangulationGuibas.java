package imagingbook.pub.geometry.delaunay.impGuibas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import imagingbook.pub.geometry.delaunay.common.DelaunayTriangulation;
import imagingbook.pub.geometry.delaunay.common.Point;
import imagingbook.pub.geometry.delaunay.common.Triangle;
import imagingbook.pub.geometry.delaunay.common.Utils;
import imagingbook.pub.geometry.delaunay.impGuibas.Edge2D.Distance;

/**
 * A Java implementation of an incremental 2D Delaunay triangulation algorithm.
 * 
 * @author Johannes Diemke
 * Refactored by W. Burger (wilbur)
 * @version 2019-12-29
 */
public class DelaunayTriangulationGuibas implements DelaunayTriangulation {
	
	public static boolean SHUFFLE_INPUT_POINTS = true;	

	private final List<Vector2D> points;
	private final List<Triangle2D> triangles;
	private final Triangle2D superTriangle;

	/**
	 * Constructor.
	 * @param pointSet the point set to be triangulated
	 * @param shuffle set {@code true} to randomly shuffle the input points
	 */
	public DelaunayTriangulationGuibas(Collection<Point> pointSet, boolean shuffle) {
		if (pointSet == null || pointSet.size() < 3) {
			throw new IllegalArgumentException("Point set must contain at least 3 points.");
		}
		this.triangles = new ArrayList<>();
		this.superTriangle = new Triangle2D(Utils.makeOuterTriangle(pointSet));
		this.points = makePointset(pointSet, shuffle);
		triangulate();
	}
	
	public DelaunayTriangulationGuibas(Collection<Point> pointSet) {
		this(pointSet, SHUFFLE_INPUT_POINTS);
	}
	
	// -----------------------------------------------------------------------------
	
	/** 
	 * Converts the incoming points (of unknown type but implementing the {@link Point} interface)
	 * to instances of the local implementation ({@link Vector2D}).
	 * 
	 * @param inPoints the input points (must implement {@link Point})
	 * @param shuffle if {@code true}, the input point sequence is randomly permuted
	 * @return the new point sequence
	 */
	private List<Vector2D> makePointset(Collection<? extends Point> inPoints, boolean shuffle) {
		Vector2D[] outPoints = new Vector2D[inPoints.size()];
		int i = 0;
		for (Point ip : inPoints) {
			outPoints[i] = new Vector2D(ip);
			i++;
		}
		List<Vector2D> outList = Arrays.asList(outPoints);
		if (shuffle) {
			Collections.shuffle(outList);	// random permutation of pointset (in-place)
		}
		return outList;
	}
	
	private void triangulate() {
//		final Triangle2D superTriangle = //this.makeOuterTriangle(points);
//				new Triangle2D(Utils.makeOuterTriangle(points));
				
		triangles.add(superTriangle);
		
		for (Vector2D pnt : points) {
			Triangle2D triangle = findContainingTriangle(pnt);

			if (triangle == null) {
				/* If no containing triangle exists, then the vertex is not inside a triangle
				 * (this can also happen due to numerical errors) and lies on an edge. In order
				 * to find this edge we search all edges of the triangle soup and select the one
				 * which is nearest to the point we try to add. This edge is removed and four
				 * new edges are added.
				 */
				Edge2D edge = findNearestEdge(pnt);
				Triangle2D tr1 = findOneTriangleSharing(edge);
				Triangle2D tr2 = findNeighbour(tr1, edge);

				Vector2D oppositeVertex1 = tr1.getOppositeVertex(edge);
				Vector2D oppositeVertex2 = tr2.getOppositeVertex(edge);

				triangles.remove(tr1);
				triangles.remove(tr2);

				Triangle2D triangle1 = new Triangle2D(edge.a, oppositeVertex1, pnt);
				Triangle2D triangle2 = new Triangle2D(edge.b, oppositeVertex1, pnt);
				Triangle2D triangle3 = new Triangle2D(edge.a, oppositeVertex2, pnt);
				Triangle2D triangle4 = new Triangle2D(edge.b, oppositeVertex2, pnt);

				triangles.add(triangle1);
				triangles.add(triangle2);
				triangles.add(triangle3);
				triangles.add(triangle4);

				legalizeEdge(triangle1, new Edge2D(edge.a, oppositeVertex1), pnt);
				legalizeEdge(triangle2, new Edge2D(edge.b, oppositeVertex1), pnt);
				legalizeEdge(triangle3, new Edge2D(edge.a, oppositeVertex2), pnt);
				legalizeEdge(triangle4, new Edge2D(edge.b, oppositeVertex2), pnt);
			} 
			else { // pnt is inside the triangle <a,b,c>.
				Vector2D a = triangle.a;
				Vector2D b = triangle.b;
				Vector2D c = triangle.c;

				triangles.remove(triangle);

				Triangle2D triangle1 = new Triangle2D(a, b, pnt);
				Triangle2D triangle2 = new Triangle2D(b, c, pnt);
				Triangle2D triangle3 = new Triangle2D(c, a, pnt);

				triangles.add(triangle1);
				triangles.add(triangle2);
				triangles.add(triangle3);

				legalizeEdge(triangle1, new Edge2D(a, b), pnt);
				legalizeEdge(triangle2, new Edge2D(b, c), pnt);
				legalizeEdge(triangle3, new Edge2D(c, a), pnt);
			}
		}

		// Remove all triangles that contain vertices of the super triangle:
		removeTrianglesUsing(superTriangle.a);
		removeTrianglesUsing(superTriangle.b);
		removeTrianglesUsing(superTriangle.c);
	}
	
//	/**
//	 * In order for the in-circumcircle test to not consider the vertices of the
//	 * super triangle we have to start out with a big triangle containing the whole
//	 * point set. We have to scale the super triangle to be very large. Otherwise
//	 * the triangulation is not convex.
//	 * 
//	 * TODO: handle negative point coordinates!!!
//	 */
//	private Triangle2D makeOuterTriangle(Collection<Vector2D> points) {
//		double maxOfAnyCoordinate = 0.0d;
//		for (Vector2D vector : points) {
//			maxOfAnyCoordinate = Math.max(Math.max(vector.x, vector.y), maxOfAnyCoordinate);
//		}
//		maxOfAnyCoordinate *= 16.0d;
//		Vector2D p1 = new Vector2D(0.0d, 3.0d * maxOfAnyCoordinate);
//		Vector2D p2 = new Vector2D(3.0d * maxOfAnyCoordinate, 0.0d);
//		Vector2D p3 = new Vector2D(-3.0d * maxOfAnyCoordinate, -3.0d * maxOfAnyCoordinate);
//		return new Triangle2D(p1, p2, p3);
//	}


	/**
	 * This method legalizes edges by recursively flipping all illegal edges.
	 * 
	 * @param triangle  the triangle
	 * @param edge      the edge to be legalized
	 * @param newVertex the new vertex
	 */
	private void legalizeEdge(Triangle2D triangle, Edge2D edge, Vector2D newVertex) {
		Triangle2D neighbourTriangle = findNeighbour(triangle, edge);

		// If the triangle has a neighbor, then legalize the edge
		if (neighbourTriangle != null) {
			if (neighbourTriangle.isPointInCircumCircle(newVertex)) {
				triangles.remove(triangle);
				triangles.remove(neighbourTriangle);

				Vector2D noneEdgeVertex = neighbourTriangle.getOppositeVertex(edge);

				Triangle2D triangle1 = new Triangle2D(noneEdgeVertex, edge.a, newVertex);
				Triangle2D triangle2 = new Triangle2D(noneEdgeVertex, edge.b, newVertex);

				triangles.add(triangle1);
				triangles.add(triangle2);

				legalizeEdge(triangle1, new Edge2D(noneEdgeVertex, edge.a), newVertex);
				legalizeEdge(triangle2, new Edge2D(noneEdgeVertex, edge.b), newVertex);
			}
		}
	}

	/**
	 * Returns the triangles of the triangulation in form of a vector of 2D
	 * triangles. The initial 'superTriangle' is removed.
	 * The resulting triangles should form a convex structure.
	 * 
	 * @return the triangles of this triangulation.
	 */
	@Override
	public Collection<Triangle> getTriangles() {
		return Collections.unmodifiableList(triangles);
	}
	
	@Override
	public Collection<Point> getPoints() {
		return Collections.unmodifiableList(points);
	}
	
	// wilbur: Methods ported from 'triangleSoup' -----------------------
	
	/**
	 * Returns the triangle that contains the specified point or null if no
	 * such triangle exists.
	 * @param point the query point
	 * @return the containing triangle or {@code null} if none was found
	 */
	private Triangle2D findContainingTriangle(Vector2D point) {
		for (Triangle2D triangle : triangles) {
			if (triangle.containsPoint(point)) {
				return triangle;
			}
		}
		return null;
	}
	
	/**
	 * Returns the triangle edge nearest to the specified point.
	 * wilbur: modified massively (no lists, array, no sorting)
	 * @param point the query point
	 * @return the triangle edge nearest to the specified point
	 */
	private Edge2D findNearestEdge(Vector2D point) {
		Edge2D minEdge = null;
		double minDist = Double.POSITIVE_INFINITY;
		for (Triangle2D tri : triangles) {
			Distance ed = tri.findNearestEdge(point);
			double dist = ed.getDistance();
			if (dist < minDist) {
				minDist = dist;
				minEdge = ed.getEdge();
			}
		}
		return minEdge;
	}
	
	/**
	 * Returns one of the possible triangles sharing the specified edge. Based on
	 * the ordering of the triangles in this triangle soup the returned triangle may
	 * differ. To find the other triangle that shares this edge use the
	 * {@link findNeighbour(Triangle2D triangle, Edge2D edge)} method.
	 * @param edge The edge
	 * @return Returns one triangle that shares the specified edge
	 */
	private Triangle2D findOneTriangleSharing(Edge2D edge) {
		for (Triangle2D triangle : triangles) {
			if (triangle.containsEdge(edge)) {
				return triangle;
			}
		}
		return null;
	}
	
	/**
	 * Returns the neighbor triangle of the specified triangle sharing the same edge
	 * as specified. If no neighbor sharing the same edge exists null is returned.
	 * TODO: searching over ALL triangles seems to be unnecessarily expensive
	 * 
	 * @param tri1 The triangle
	 * @param edge     The edge
	 * @return The triangles neighbor triangle sharing the same edge or null if no
	 *         triangle exists
	 */
	private Triangle2D findNeighbour(Triangle2D tri1, Edge2D edge) {
		for (Triangle2D tri2 : triangles) {
			if (tri2.containsEdge(edge) && tri2 != tri1) {
				return tri2;
			}
		}
		return null;
	}
	
	/**
	 * Removes all triangles that contain the specified corner point.
	 * @param point the corner point
	 */
	private void removeTrianglesUsing(Vector2D point) {
		List<Triangle2D> trianglesToBeRemoved = new LinkedList<>();
		for (Triangle2D triangle : triangles) {
			if (triangle.hasVertex(point)) {
				trianglesToBeRemoved.add(triangle);
			}
		}
		triangles.removeAll(trianglesToBeRemoved);
	}



}