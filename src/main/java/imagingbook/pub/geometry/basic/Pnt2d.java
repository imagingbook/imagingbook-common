package imagingbook.pub.geometry.basic;

import static imagingbook.lib.math.Arithmetic.isZero;
import static imagingbook.lib.math.Arithmetic.sqr;

import java.awt.geom.Point2D;
import java.util.Locale;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import imagingbook.lib.math.Arithmetic;

/** 
 * Interface specifying the behavior of simple 2D points. 
 * It is used to adapt to different (legacy) point implementations 
 * with a common API. 
 * To some extent this is similar to the functionality provided by
 * {@link java.awt.geom.Point2D} and {@link java.awt.Point}
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
	
	double getX();
	double getY();
	
	// ----------------------------------------------------------
	
	public default double[] toDoubleArray() {
		return new double[] {this.getX(), this.getY()};
	}
	
	public default java.awt.geom.Point2D.Double toAwtPoint2D() {
		return new java.awt.geom.Point2D.Double(this.getX(), this.getY());
	}
	
	public default Vector2D toVector2D() {
		return new Vector2D(getX(), getY());
	}
	
	public default RealVector toRealVector() {
		return MatrixUtils.createRealVector(this.toDoubleArray());
	}
	
	public default Pnt2d duplicate() {
		throw new UnsupportedOperationException();
	}
	
	public default Pnt2d plus(Pnt2d p) {
		throw new UnsupportedOperationException();
	}
	
	public default Pnt2d plus(double dx, double dy) {
		throw new UnsupportedOperationException();
	}
	
	public default Pnt2d plus(int dx, int dy) {
		return plus((double)dx, (double)dy);
	}
	
	public default Pnt2d minus(Pnt2d p) {
		throw new UnsupportedOperationException();
	}
	
	// ----------------------------------------------------------

	public default boolean equals(Pnt2d p) {
		return isZero(this.getX() - p.getX()) && isZero(this.getY() - p.getY());
	}
	
	// ----------------------------------------------------------
	
	public default double distance2(Pnt2d p) {
		return sqr(this.getX() - p.getX()) + sqr(this.getY() - p.getY());
	}
	
	public default double distance(Pnt2d p) {
		return Math.sqrt(this.distance2(p));
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Immutable 2D point with {@code double} coordinates.
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
		
		public static final PntDouble ZERO = PntDouble.from(0, 0);

		public final double x, y;

		private PntDouble(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}
		
		@Override
		public PntDouble duplicate() {
			return new PntDouble(this.x, this.y);
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
		
		// static factory methods
		
		public static PntDouble from(double x, double y) {
			return new PntDouble(x, y);
		}
		
		public static PntDouble from(double[] xy) {
			return new PntDouble(xy[0], xy[1]);
		}
		
		public static PntDouble from(Pnt2d p) {
			return new PntDouble(p.getX(), p.getY());
		}
		
		public static PntDouble from(Point2D p) {
			return new PntDouble(p.getX(), p.getY());
		}
		
		public static PntDouble from(Vector2D vec) {
			return new PntDouble(vec.getX(), vec.getY());
		}	
		
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Immutable 2D point with {@code int} coordinates.
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
	public final class PntInt implements Pnt2d {
		
		public static final PntInt ZERO = PntInt.from(0, 0);
		
		public final int x, y;
		
		public PntInt(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}
		
		@Override
		public PntInt duplicate() {
			return new PntInt(this.x, this.y);
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
		
		// equality -----------------------------------
		
		public boolean equals(Pnt2d p) {
			if (p instanceof PntInt) {
				return this.equals((PntInt) p);
			}
			else {
				return Arithmetic.isZero(this.x - p.getX()) && Arithmetic.isZero(this.y - p.getY());
			}
		}
		
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
		
		// static factory methods --------------------------------------
		
		public static PntInt from(int x, int y) {
			return new PntInt(x, y);
		}
		
		public static PntInt from(PntInt p) {
			return new PntInt(p.x, p.y);
		}
		
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
		
		public static PntInt from(java.awt.Point p) {
			return new PntInt(p.x, p.y);
		}
		
		// static conversion methods
		
		public int[] toIntArray() {
			return new int[] {this.x, this.y};
		}
		
		public java.awt.Point toAwtPoint() {
			return new java.awt.Point(this.x, this.y);
		}
	}
}
