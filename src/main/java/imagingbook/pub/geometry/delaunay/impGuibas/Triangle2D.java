package imagingbook.pub.geometry.delaunay.impGuibas;

import java.util.Arrays;

import imagingbook.pub.geometry.delaunay.common.Point;
import imagingbook.pub.geometry.delaunay.common.Triangle;
import imagingbook.pub.geometry.delaunay.impGuibas.Edge2D.Distance;

/**
 * 2D triangle class implementation.
 * 
 * @author Johannes Diemke
 */
public class Triangle2D implements Triangle {

	public final Vector2D a;
	public final Vector2D b;
	public final Vector2D c;
	private final boolean isOrientedCCW;

	/**
	 * Constructor of the 2D triangle class used to create a new triangle instance
	 * from three 2D vectors describing the triangle's vertices.
	 * 
	 * @param a The first vertex of the triangle
	 * @param b The second vertex of the triangle
	 * @param c The third vertex of the triangle
	 */
	public Triangle2D(Vector2D a, Vector2D b, Vector2D c) {
		this.a = a;
		this.b = b;
		this.c = c;
		isOrientedCCW = findIfOrientedCCW();
	}
	
	public Triangle2D(Point[] points) {
		this(new Vector2D(points[0]), new Vector2D(points[1]), new Vector2D(points[2]));
	}
	
	@Override
	public Point[] getPoints() {
		return new Point[] {a, b, c};
	}

	/**
	 * Tests if a 2D point lies inside this 2D triangle. See Real-Time Collision
	 * Detection, chap. 5, p. 206.
	 * wilbur: renamed, since 'contains' is used by collections
	 * @param point the point to be checked
	 * @return {@code true} iff the point lies inside this 2D triangle
	 */
	protected boolean containsPoint(Vector2D point) {
		double pab = point.sub(a).cross(b.sub(a));
		double pbc = point.sub(b).cross(c.sub(b));
		if (!hasSameSign(pab, pbc)) {
			return false;
		}
		double pca = point.sub(c).cross(a.sub(c));
		if (!hasSameSign(pab, pca)) {
			return false;
		}
		return true;
	}

	/**
	 * Tests if a given point lies in the circumcircle of this triangle. Let the
	 * triangle ABC appear in counterclockwise (CCW) order. Then when det &gt; 0,
	 * the point lies inside the circumcircle through the three points a, b and c.
	 * If instead det &lt; 0, the point lies outside the circumcircle. When det = 0,
	 * the four points are cocircular. If the triangle is oriented clockwise (CW)
	 * the result is reversed. 
	 * See Christer Ericson, Real-Time Collision Detection, CRC Press, 2004 (Ch. 3, p. 34).
	 * 
	 * @param point the point to be checked
	 * @return {@code true} iff the point lies inside the circumcircle through the
	 *         three points a, b, and c of the triangle
	 */
	protected boolean isPointInCircumCircle(Vector2D point) {
		final double a11 = a.x - point.x;
		final double a21 = b.x - point.x;
		final double a31 = c.x - point.x;

		final double a12 = a.y - point.y;
		final double a22 = b.y - point.y;
		final double a32 = c.y - point.y;
		
		final double a13 = a11 * a11 + a12 * a12;
		final double a23 = a21 * a21 + a22 * a22;
		final double a33 = a31 * a31 + a32 * a32;

		final double det = 
				a11 * a22 * a33 + a12 * a23 * a31 + 
				a13 * a21 * a32 - a13 * a22 * a31 - 
				a12 * a21 * a33 - a11 * a23 * a32;

		if (isOrientedCCW) {
			return det > 0.0;
		}
		else {
			return det < 0.0;
		}
	}
	
	/**
	 * Test if this triangle is oriented counterclockwise (CCW). Let A, B and C be
	 * three 2D points. If det &gt; 0, C lies to the left of the directed line AB.
	 * Equivalently the triangle ABC is oriented counterclockwise. When det &lt; 0,
	 * C lies to the right of the directed line AB, and the triangle ABC is oriented
	 * clockwise. When det = 0, the three points are colinear. See Real-Time
	 * Collision Detection, chap. 3, p. 32
	 * 
	 * wilbur: Since triangles are immutable, this property can be pre-calculated.
	 * 
	 * @return {@code true} iff the triangle ABC is oriented counterclockwise (CCW)
	 */
	private boolean findIfOrientedCCW() {
		double a11 = a.x - c.x;
		double a21 = b.x - c.x;
		double a12 = a.y - c.y;
		double a22 = b.y - c.y;
		double det = a11 * a22 - a12 * a21;
		return det > 0.0;
	}

	/**
	 * Test if this triangle is oriented counterclockwise (CCW). Let A, B and C be
	 * three 2D points. If det &gt; 0, C lies to the left of the directed line AB.
	 * Equivalently the triangle ABC is oriented counterclockwise. When det &lt; 0,
	 * C lies to the right of the directed line AB, and the triangle ABC is oriented
	 * clockwise. When det = 0, the three points are colinear. See Real-Time
	 * Collision Detection, chap. 3, p. 32
	 * 
	 * @return {@code true} iff the triangle ABC is oriented counterclockwise (CCW)
	 */
	protected boolean isOrientedCCW() {
		return isOrientedCCW;
	}

	/**
	 * Returns true if this triangle contains the given edge.
	 * 
	 * @param edge the edge to be tested
	 * @return {@code true} iff this triangle contains the specified edge
	 */
	protected boolean containsEdge(Edge2D edge) {
		return (a == edge.a || b == edge.a || c == edge.a) && (a == edge.b || b == edge.b || c == edge.b);
	}

	/**
	 * Returns the vertex of this triangle opposite to the specified edge.
	 * wilbur: rewritten and renamed from 'getNoneEdgeVertex()', there was no
	 * check if the edge is actually part of the triangle.
	 * 
	 * @param edge the edge (which must be contained in this triangle)
	 * @return the triangle vertex opposite to the specified edge
	 */
	protected Vector2D getOppositeVertex(Edge2D edge) {
		final Vector2D p1 = edge.a;
		final Vector2D p2 = edge.b;
		if ((a == p1 && b == p2) || (a == p2 && b == p1)) {
			return c;
		}
		if ((a == p1 && c == p2) || (a == p2 && c == p1)) {
			return b;
		}
		if ((b == p1 && c == p2) || (b == p2 && c == p1)) {
			return a;
		} 
		throw new IllegalArgumentException("specified edge is not part of this triangle");
	}


	/**
	 * Checks if the given vertex is one of the triangle's vertices.
	 * 
	 * @param vertex the vertex to be checked
	 * @return {@code true} if the vertex is one of the corners of this triangle
	 */
	protected boolean hasVertex(Vector2D vertex) {
		return (a == vertex || b == vertex || c == vertex);
	}

	/**
	 * Calculates the minimum distance from the specified point to this triangle.
	 * The result is returned as an {@link Distance} instance.
	 * 
	 * @param point the point to be checked
	 * @return the edge of this triangle that is closest to the specified point
	 */
	protected Distance findNearestEdge(Vector2D point) {
		Distance[] edges = {
				new Edge2D(a, b).getEdgeDistance(point),
				new Edge2D(b, c).getEdgeDistance(point),
				new Edge2D(c, a).getEdgeDistance(point)	
			};
		Arrays.sort(edges);
		return edges[0];
	}

	/**
	 * Tests if the two arguments have the same sign.
	 * wilbur: reformulated (probably quicker)
	 * 
	 * @param a first quantity
	 * @param b second quantity
	 * @return {@code true} iff both arguments have the same sign
	 */
	private boolean hasSameSign(double a, double b) {
		//return Math.signum(a) == Math.signum(b);
		return a * b >= 0;
	}

	@Override
	public String toString() {
		return "Triangle2D[" + a + ", " + b + ", " + c + "]";
	}



}