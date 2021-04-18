package imagingbook.pub.geometry.basic;

import static imagingbook.lib.math.Arithmetic.isZero;
import static imagingbook.lib.math.Arithmetic.sqr;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Locale;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

/** 
 * Interface specifying the behavior of simple 2D points. 
 * It is used to adapt to different (legacy) point implementations 
 * with a common API. 
 * To some extent this is similar to the functionality provided by
 * {@link Point2D} and {@link Point}
 * but was re-implemented to avoid dependency on AWT and for
 * more flexibility in naming and class structure.
 * Since defined as an interface, {@link Pnt2d} can be easily implemented 
 * by other point-like structures, e.g. corners 
 * (see {@link imagingbook.pub.corners.Corner}).
 * <br>
 * Two concrete (nested) classes are defined for points with {@code double}
 * and {@code int} coordinates, respectively.
 * See {@link Pnt2d.PntDouble} and {@link Pnt2d.PntInt} for how
 * to instantiate such point objects.
 * 
 */
public interface Pnt2d {
	
	/**
	 * The default tolerance for matching coordinates (1E-6).
	 */
	public final static double TOLERANCE = 1E-6;
	
	/**
	 * Returns the x-coordinate of this point.
	 * @return the x-coordinate value
	 */
	double getX();
	
	/**
	 * Returns the y-coordinate of this point.
	 * @return the y-coordinate value
	 */
	double getY();
	
	// ----------------------------------------------------------
	
	public static Pnt2d from(int x, int y) {
		return PntInt.from(x, y);
	}
	
	public static Pnt2d from(double x, double y) {
		return PntDouble.from(x, y);
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Returns this point's coordinates as a new 2-element {@code double} array.
	 * @return the array
	 */
	public default double[] toDoubleArray() {
		return new double[] {this.getX(), this.getY()};
	}
	
	/**
	 * Returns this point's coordinates as a new AWT {@link Point2D.Double} point.
	 * @return the new point
	 */
	public default Point2D.Double toAwtPoint2D() {
		return new Point2D.Double(this.getX(), this.getY());
	}
	
	/**
	 * Returns this point's coordinates as a new {@link Vector2D} instance
	 * (for interfacing with Apache Commons Math).
	 * @return a new vector
	 */
	public default Vector2D toVector2D() {
		return new Vector2D(getX(), getY());
	}
	
	/**
	 * Returns this point's coordinates as a new {@link RealVector} instance
	 * (for interfacing with Apache Commons Math).
	 * @return a new vector
	 */
	public default RealVector toRealVector() {
		return MatrixUtils.createRealVector(this.toDoubleArray());
	}
	
	/**
	 * Returns a copy of this point which is of the same type as the original.
	 * @return a new instance.
	 */
	public default Pnt2d duplicate() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns a new point whose coordinates are the sum of this point and 
	 * the given point. The concrete type of the returned object depends on the type
	 * of the original points.
	 * @param p the point to be added
	 * @return a new point
	 */
	public default Pnt2d plus(Pnt2d p) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns a new point whose coordinates are the sum of this point and 
	 * the specified {@code double} coordinates.
	 * @param dx the x-coordinate to be added 
	 * @param dy the y-coordinate to be added 
	 * @return a new point
	 */
	public default Pnt2d plus(double dx, double dy) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns a new point whose coordinates are the sum of this point and 
	 * the specified {@code int} coordinates.
	 * The concrete type of the returned object depends on the type
	 * of the original point.
	 * @param dx the x-coordinate to be added 
	 * @param dy the y-coordinate to be added 
	 * @return a new point
	 */
	public default Pnt2d plus(int dx, int dy) {
		return plus((double)dx, (double)dy);
	}
	
	/**
	 * Returns a new point whose coordinates are the difference of this point and 
	 * the given point. The concrete type of the returned object depends on the type
	 * of the original points.
	 * @param p the point to be subtracted
	 * @return a new point
	 */
	public default Pnt2d minus(Pnt2d p) {
		throw new UnsupportedOperationException();
	}
	
	// ----------------------------------------------------------

	/**
	 * Tests if this point matches the given point, i.e., if
	 * both coordinate differences are zero (&lt; {@link #TOLERANCE}).
	 * 
	 * @param p the point to be matched to
	 * @return true if both points match
	 */
	public default boolean equals(Pnt2d p) {
		return this.equals(p, TOLERANCE);
	}
	
	/**
	 * Tests if this point matches the given point, i.e., if
	 * both coordinate differences are zero (&lt; than 
	 * the specified tolerance).
	 *  
	 * @param p the point to be matched to
	 * @param tolerance the tolerance (see also {@link #TOLERANCE}).
	 * @return true if both points match
	 */
	public default boolean equals(Pnt2d p, double tolerance) {
		return isZero(this.getX() - p.getX(), tolerance) 
			&& isZero(this.getY() - p.getY(), tolerance);
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Returns the squared L2 distance between this point and the
	 * given point.
	 * 
	 * @param p the other point
	 * @return the squared distance
	 */
	public default double distanceSq(Pnt2d p) {
		return sqr(this.getX() - p.getX()) + sqr(this.getY() - p.getY());
	}
	
	/**
	 * Returns the L2 (Euclidean) distance between this point and the
	 * given point. This method is equivalent to {@link #distL2(Pnt2d)}.
	 * 
	 * @param p the other point
	 * @return the distance
	 */
	public default double distance(Pnt2d p) {
		return Math.sqrt(this.distanceSq(p));
	}
	
	/**
	 * Returns the L2 (Euclidean) distance between this point and the
	 * given point. This method is equivalent to {@link #distance(Pnt2d)}.
	 * 
	 * @param p the other point
	 * @return the distance
	 */
	public default double distL2(Pnt2d p) {
		return distance(p);
	}
	
	/**
	 * Returns the L1 (Manhattan) distance between this point and the
	 * given point. 
	 * 
	 * @param p the other point
	 * @return the distance
	 */
	public default double distL1(Pnt2d p) {
		return Math.abs(this.getX() - p.getX()) + Math.abs(this.getY() - p.getY());
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Immutable 2D point implementation with {@code double} coordinates.
	 * This class implements the {@link Pnt2d} interface.
	 * <br>
	 * A public constructor ({@link #PntDouble(double, double)})
	 * is provided but the preferred way of instantiation is
	 * by one of the static factory methods, such as
	 * {@link #from(double, double)},
	 * {@link #from(double[])}, etc.
	 * <br>
	 * Access to the coordinate values is provided by the methods
	 * {@link #getX()} and {@link #getY()}, but the
	 * actual field variables {@link #x}, {@link #y} are also 
	 * publicly accessible (for better performance and less clutter).
	 */
	public class PntDouble implements Pnt2d {
		
		/**
		 * Singleton point instance with zero coordinates.
		 */
		public static final PntDouble ZERO = PntDouble.from(0.0, 0.0);

		public final double x, y;

		/**
		 * Constructor.
		 * @param x x-coordinate
		 * @param y y-coordinate
		 */
		protected PntDouble(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public PntDouble duplicate() {
			return new PntDouble(this.x, this.y);
		}
		
		// static factory methods ---------------------------------
		
		/**
		 * Returns a new {@link PntDouble} instance.
		 * @param xy x/y-coordinate array
		 * @return the new point
		 */
		public static PntDouble from(double[] xy) {
			return new PntDouble(xy[0], xy[1]);
		}
		
		/**
		 * Returns a new {@link PntDouble} instance.
		 * @param x x-coordinate value
		 * @param y y-coordinate value
		 * @return the new point
		 */
		public static PntDouble from(double x, double y) {
			return new PntDouble(x, y);
		}
		
		/**
		 * Returns a new {@link PntDouble} instance
		 * with the same coordinates as the given point.
		 * Equivalent to {@link #duplicate()}.
		 * @param p the original point
		 * @return the new point
		 */
		public static PntDouble from(Pnt2d p) {
			return new PntDouble(p.getX(), p.getY());
		}
		
		/**
		 * Returns a new {@link PntDouble} instance
		 * with the same coordinates as the given AWT {@link Point2D}.
		 * @param p the original AWT point
		 * @return the new point
		 */
		public static PntDouble from(Point2D p) {
			return new PntDouble(p.getX(), p.getY());
		}
		
		/**
		 * Returns a new {@link PntDouble} instance
		 * with the same coordinates as the given Apache Commons Math
		 * {@link Vector2D}.
		 * @param vec the original coordinate vector
		 * @return the new point
		 */
		public static PntDouble from(Vector2D vec) {
			return new PntDouble(vec.getX(), vec.getY());
		}	
		
		// getter methods
		
		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}
		
		
		// addition -----------------------------------
		
		@Override
		public PntDouble plus(Pnt2d p) {
			return new PntDouble(this.x + p.getX(), this.y + p.getY());
		}
		
		@Override
		public PntDouble plus(double dx, double dy) {
			return new PntDouble(this.x + dx, this.y + dy);
		}
		
		// subtraction -----------------------------------
		
		@Override
		public PntDouble minus(Pnt2d p) {
			return new PntDouble(this.x - p.getX(), this.y - p.getY());
		}
		
		// misc -----------------------------------
		
		@Override
		public String toString() {
			return String.format(Locale.US, "%s[%.3f, %.3f]", 
					getClass().getSimpleName(), x, y);
		}
		
		@Override
	    public int hashCode() {	// taken from awt.Point2D.Double
	        long bits = java.lang.Double.doubleToLongBits(this.x);
	        bits ^= java.lang.Double.doubleToLongBits(this.y) * 31;
	        return (((int) bits) ^ ((int) (bits >> 32)));
	    }	
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Immutable 2D point implementation with {@code int} coordinates.
	 * This class implements the {@link Pnt2d} interface.
	 * <br>
	 * A public constructor ({@link #PntInt(int, int)})
	 * is provided but the preferred way of instantiation is
	 * by one of the static factory methods, such as
	 * {@link #from(int, int)},
	 * {@link #from(int[])}, etc.
	 * <br>
	 * The {@code int} coordinates can only be retrieved via the
	 * publicly accessible field variables {@link #x}, {@link #y},
	 * while the methods {@link #getX()} and {@link #getY()}
	 * return {@code double} values for compatibility reasons.
	 * 
	 */
	public class PntInt implements Pnt2d {
		
		/**
		 * Singleton point instance with zero coordinates.
		 */
		public static final PntInt ZERO = PntInt.from(0, 0);
		
		public final int x, y;
		
		/**
		 * Constructor.
		 * @param x x-coordinate
		 * @param y y-coordinate
		 */
		protected PntInt(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public PntInt duplicate() {
			return new PntInt(this.x, this.y);
		}
		
		// static factory methods --------------------------------------
		
		/**
		 * Returns a new {@link PntInt} instance from
		 * a given point.
		 * @param p the original point
		 * @return the new point
		 */
		public static PntInt from(PntInt p) {
			return new PntInt(p.x, p.y);
		}
		
		/**
		 * Returns a new {@link PntInt} instance.
		 * @param x x-coordinate value
		 * @param y y-coordinate value
		 * @return the new point
		 */
		public static PntInt from(int x, int y) {
			return new PntInt(x, y);
		}
		
		/**
		 * Returns a new {@link PntInt} instance.
		 * @param xy x/y-coordinate array
		 * @return the new point
		 */
		public static PntInt from(int[] xy) {
			return new PntInt(xy[0], xy[1]);
		}
		
		public static PntInt from(Pnt2d p) {
			if (p instanceof PntInt) {
				return ((PntInt) p).duplicate();
			}
			else {
				throw new IllegalArgumentException("cannot convert to " +
							PntInt.class.getSimpleName());
			}
		}
		
		/**
		 * Returns a new {@link PntDouble} instance
		 * with the same coordinates as the given AWT {@link Point}.
		 * @param p the original AWT point
		 * @return the new point
		 */
		public static PntInt from(Point p) {
			return new PntInt(p.x, p.y);
		}
		
		// getter methods

		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}
		
		// addition -----------------------------------
		
		@Override
		public PntInt plus(Pnt2d p) {
			if (p instanceof PntInt) {		 
				return new PntInt(this.x + ((PntInt)p).x, this.y + ((PntInt)p).y);
			}
			else throw new IllegalArgumentException("cannot add " + p.getClass().getSimpleName() + " to " +
					this.getClass().getSimpleName());
		}
		
		@Override
		public PntInt plus(double dx, double dy) {
			throw new IllegalArgumentException("cannot add (double, double) to " +
					this.getClass().getSimpleName());
		}
		
		@Override
		public PntInt plus(int dx, int dy) {
			return new PntInt(this.x + dx, this.y + dy);
		}
		
		// subtraction -----------------------------------
		
		@Override
		public PntInt minus(Pnt2d p) {
			if (p instanceof PntInt) {		 
				return new PntInt(this.x - ((PntInt)p).x, this.y - ((PntInt)p).y);
			}
			else throw new IllegalArgumentException("cannot subtract " + p.getClass().getSimpleName() + " from " +
					this.getClass().getSimpleName());
		}
		
		// distance -----------------------------------
		
		/**
		 * Returns the L1 (Manhattan) distance between this point and the
		 * given point. 
		 * 
		 * @param p the other point
		 * @return the distance
		 */
		public int distL1(PntInt p) {
			return Math.abs(this.x - p.x) + Math.abs(this.y - p.y);
		}
		
		// equality -----------------------------------
		
		@Override
		public boolean equals(Pnt2d p) {
			if (p instanceof PntInt) {
				return this.equals((PntInt) p);
			}
			else {
				return Pnt2d.super.equals(p);
			}
		}
		
		/**
		 * Tests if this point matches the given point, i.e., if
		 * both coordinate differences are zero.
		 * Since both points are of type {@link PntInt} the match
		 * is exact.
		 * 
		 * @param p the point to be matched to
		 * @return true if both points match
		 */
		public boolean equals(PntInt p) {
			return (this.x == p.x) && (this.y == p.y);
		}
		
		// misc -----------------------------------
		
		
		@Override
		public String toString() {
			return String.format(Locale.US, "%s[%d, %d]", 
					getClass().getSimpleName(), x, y);
		}
		
		
		@Override
		public int hashCode() {
			return (17 + this.x) * 37 + this.y;	
		}
		
		// static conversion methods
		
		/**
		 * Returns this point's coordinates as a new 2-element {@code int} array.
		 * @return the array
		 */
		public int[] toIntArray() {
			return new int[] {this.x, this.y};
		}
		
		/**
		 * Returns this point's coordinates as a new AWT {@link Point} point.
		 * @return the new point
		 */
		public Point toAwtPoint() {
			return new Point(this.x, this.y);
		}
	}
}
