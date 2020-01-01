package imagingbook.pub.geometry.delaunay.impGuibas;

import imagingbook.pub.geometry.delaunay.common.Point;

/**
 * 2D vector class implementation.
 * 
 * @author Johannes Diemke
 */
public class Vector2D implements Point {

	public final double x;
	public final double y;

	/**
	 * Constructor of the 2D vector class used to create new vector instances.
	 * 
	 * @param x the x coordinate of the new vector
	 * @param y the y coordinate of the new vector
	 */
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructor from generic 'Point2D'.
	 * 
	 * @param pt
	 */
	public Vector2D(Point pt) {
		this.x = pt.getX();
		this.y = pt.getY();
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	/**
	 * Subtracts the given vector from this.
	 * 
	 * @param vector the vector to be subtracted from this
	 * @return a new instance holding the result of the vector subtraction
	 */
	public Vector2D sub(Vector2D vector) {
		return new Vector2D(this.x - vector.x, this.y - vector.y);
	}

	/**
	 * Adds the given vector to this.
	 * 
	 * @param vector the vector to be added to this
	 * @return a new instance holding the result of the vector addition
	 */
	public Vector2D add(Vector2D vector) {
		return new Vector2D(this.x + vector.x, this.y + vector.y);
	}

	/**
	 * Multiplies this by the given scalar.
	 * 
	 * @param scalar the scalar to be multiplied by this
	 * @return a new instance holding the result of the multiplication
	 */
	public Vector2D mult(double scalar) {
		return new Vector2D(this.x * scalar, this.y * scalar);
	}

	/**
	 * Computes the magnitude or length of this.
	 * 
	 * @return the magnitude of this
	 */
	public double mag() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	/**
	 * Computes the dot product of this and the given vector.
	 * 
	 * @param vector the vector to be multiplied by this
	 * @return a new instance holding the result of the multiplication
	 */
	public double dot(Vector2D vector) {
		return this.x * vector.x + this.y * vector.y;
	}

	/**
	 * Computes the 2D pseudo cross product Dot(Perp(this), vector) of this and the
	 * given vector.
	 * 
	 * @param vector the vector to be multiplied to the perpendicular vector of this
	 * @return a new instance holding the result of the pseudo cross product
	 */
	public double cross(Vector2D vector) {
		return this.y * vector.x - this.x * vector.y;
	}

	@Override
	public String toString() {
		return "Vector2D[" + x + ", " + y + "]";
	}

}