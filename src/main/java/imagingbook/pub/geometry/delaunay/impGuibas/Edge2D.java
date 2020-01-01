package imagingbook.pub.geometry.delaunay.impGuibas;

/**
 * 2D edge class implementation.
 * 
 * @author Johannes Diemke
 */
public class Edge2D {

	protected final Vector2D a;
	protected final Vector2D b;

	/**
	 * Constructor of the 2D edge class used to create a new edge instance from two
	 * 2D vectors describing the edge's vertices.
	 * 
	 * @param a first vertex of the edge
	 * @param b second vertex of the edge
	 */
	protected Edge2D(Vector2D a, Vector2D b) {
		this.a = a;
		this.b = b;
	}

	protected double minDistance(Vector2D point) {
		return getClosestPoint(point).sub(point).mag();
	}

	private Vector2D getClosestPoint(Vector2D point) {
		Vector2D ab = b.sub(a);
		double t = point.sub(a).dot(ab) / ab.dot(ab); // TODO: check for zero denominator?
		if (t < 0.0) {
			t = 0.0;
		} else if (t > 1.0) {
			t = 1.0;
		}
		return a.add(ab.mult(t));
	}

	protected Distance getEdgeDistance(Vector2D point) {
		return new Distance(point);
	}

	/**
	 * Edge distance pack class implementation used to describe the distance to a
	 * given edge.
	 * 
	 * @author Johannes Diemke
	 */
	protected class Distance implements Comparable<Distance> {

		private final double distance;

		protected Distance(double dist) {
			this.distance = dist;
		}

		protected Distance(Vector2D point) {
			this(Edge2D.this.minDistance(point));
		}

		// ---------------------------------------------------

		protected double getDistance() {
			return distance;
		}

		protected Edge2D getEdge() {
			return Edge2D.this;
		}

		@Override
		public int compareTo(Distance o) {
			return Double.compare(this.distance, o.distance);
		}

	}

}