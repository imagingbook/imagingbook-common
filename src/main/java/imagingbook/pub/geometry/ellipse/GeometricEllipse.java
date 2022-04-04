package imagingbook.pub.geometry.ellipse;

import static imagingbook.lib.math.Arithmetic.sqr;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Locale;

import imagingbook.lib.math.Arithmetic;
import imagingbook.lib.settings.PrintPrecision;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.basic.ShapeProvider;

/**
 * Represents an ellipse shape with arbitrary orientation.
 * @author WB
 *
 */
public class GeometricEllipse implements Ellipse, ShapeProvider {
	
	public final double ra, rb, xc, yc, theta;
	private final OrthogonalEllipseProjector projector;
	
	public GeometricEllipse(double ra, double rb, double xc, double yc, double theta) {
		this.xc = xc;
		this.yc = yc;
		if (ra >= rb) {	// make sure ra is always the longer axis!
			this.ra = ra;
			this.rb = rb;
			this.theta = Arithmetic.mod(theta, PI);	// theta = 0,...,pi always
		}
		else {
			this.ra = rb;
			this.rb = ra;
			this.theta = Arithmetic.mod(theta + PI/2, PI);	// theta = 0,...,pi always
		}
		this.projector = new OrthogonalEllipseProjector(this);
		
		if (this.ra < this.rb) {
			throw new RuntimeException(this.toString());
		}
	}
	
	public GeometricEllipse(double[] p) {
		this(p[0], p[1], p[2], p[3], p[4]);
	}
	
	public GeometricEllipse(double ra, double rb, double xc, double yc) {
		this(ra, rb, xc, yc, 0.0);
	}
	
	public GeometricEllipse scale(double scaleFactor) {
		return new GeometricEllipse(ra * scaleFactor, rb * scaleFactor, xc, yc, theta);
	}
	
	public static GeometricEllipse from(double[] p) {
		return new GeometricEllipse(p);
	}
	
	// see Eq. 19-23 at http://mathworld.wolfram.com/Ellipse.html
	public static GeometricEllipse from(AlgebraicEllipse ae) {
		final double A = ae.A;
		final double B = ae.B;
		final double C = ae.C;
		final double D = ae.D;
		final double E = ae.E;
		final double F = ae.F;
		
		final double p = sqr(B) - 4*A*C;
		
		if (p >= 0) {
			throw new IllegalArgumentException("B^2 - 4AC must be negative for an ellipse");
		}

		final double q = sqrt(sqr(A-C) + sqr(B));
		final double s = 2*(A*sqr(E) + C*sqr(D) + F*sqr(B) - B*D*E - 4*A*C*F);
		
		double xc = (2*C*D - B*E) / p;
		double yc = (2*A*E - B*D) / p;
		double ra = sqrt(s / (p*(- A - C + q)));
		double rb = sqrt(s / (p*(- A - C - q)));
		double theta = 0.5 * Math.atan2(-B, C - A);	// theta = -pi/2,...,+pi/2
		
		return (ra >= rb) ? // make sure ra >= rb (ra is the major axis)
			new GeometricEllipse(ra, rb, xc, yc, theta) :
			new GeometricEllipse(rb, ra, xc, yc, theta + PI/2) ;
	}
	
	// ---------------------------------------
	
	@Override
	public double[] getParameters() {
		return new double[] {ra, rb, xc, yc, theta};
	}
	
	public Pnt2d getCenter() {
		return Pnt2d.from(xc, yc);
	}
	
	public double getArea() {
		return this.ra * this.rb * Math.PI;
	}
	
	public double getAlgebraicDistance(Pnt2d p) {
		return AlgebraicEllipse.from(this).getAlgebraicDistance(p);
	}
	
	/**
	 * Returns the ellipse point closest to the specified point.
	 * To perform this calculation for multiple points on the
	 * same ellipse use {@link OrthogonalEllipseProjector}.
	 * 
	 * @see OrthogonalEllipseProjector
	 * @param pnt
	 * @return
	 */
	public Pnt2d getClosestPoint(Pnt2d pnt) {
		return projector.project(pnt);
	}
	
	public double[] getClosestPoint(double[] pnt) {
		return projector.project(pnt);
	}
	
	public GeometricEllipse disturb(double dra, double drb, double dxc, double dyc, double dtheta) {
		return new GeometricEllipse(ra + dra, rb + drb, xc + dxc, yc + dyc, theta + dtheta);
	}
		
	// ---------------------------------------------------------------------------------

    /**
	 * Returns the mean squared error between this ellipse 
	 * and a set of 2D points.
	 *
	 * @param points a set of sample points (usually the points used for fitting)
	 */
	public double getError(Pnt2d[] points) {
		double sum2 = 0;
		for (Pnt2d p : points) {
			sum2 = sum2 + getError(p);
		}
		return sum2 / points.length;
	}
	
	/**
	 * Returns the squared distance of point to this ellipse.
	 * @param p
	 * @return
	 */
	public double getError(Pnt2d p) {
		return p.distanceSq(getClosestPoint(p));
	}
	
	// ------------------------------------------------------------------------------------------
	
	@Override
	public Shape getShape(double scale) {
		return this.getOuterShape();
	}
	
	@Override
	public Shape[] getShapes(double scale) {
		return new Shape[] {
				getOuterShape(),
				getCenterShape(scale),
				getAxisShape()
		};
	}
	
	@Deprecated	// TODO: should be private
	public Shape getOuterShape() {
		Ellipse2D oval = new Ellipse2D.Double(-ra, -rb, 2 * ra, 2 * rb);
		AffineTransform trans = new AffineTransform();
		trans.translate(xc, yc);
		trans.rotate(theta);
		return trans.createTransformedShape(oval);
	}
	
	@Deprecated	// TODO: should be private
	public Shape getCenterShape(double radius) {
		double dxa = 2 * radius * cos(theta);	// major axis is drawn longer
		double dya = 2 * radius * sin(theta);
		double dxb = 1 * radius * cos(theta + PI/2);
		double dyb = 1 * radius * sin(theta + PI/2);
		Path2D path = new Path2D.Double();
		path.moveTo(xc - dxa, yc - dya);
		path.lineTo(xc + dxa, yc + dya);
		path.moveTo(xc - dxb, yc - dyb);
		path.lineTo(xc + dxb, yc + dyb);
//		path.moveTo(xc - radius, yc);
//		path.lineTo(xc + radius, yc);
//		path.moveTo(xc, yc - radius);
//		path.lineTo(xc, yc + radius);
		return path;
	}
	
	@Deprecated	// TODO: should be private
	public Shape getAxisShape() {
		double dxa = ra * cos(theta);
		double dya = ra * sin(theta);
		double dxb = rb * cos(theta + PI/2);
		double dyb = rb * sin(theta + PI/2);
		Path2D path = new Path2D.Double();
		path.moveTo(xc - dxa, yc - dya);
		path.lineTo(xc + dxa, yc + dya);
		path.moveTo(xc - dxb, yc - dyb);
		path.lineTo(xc + dxb, yc + dyb);
		return path;
	}
	
	@Deprecated
	public Pnt2d getLeftAxisPoint() {
		return Pnt2d.from(xc - ra * cos(theta), yc - ra * sin(theta));
	}
	
	// -------------------------------
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s [ra=%.8f, rb=%.8f, xc=%.8f, yc=%.8f, theta=%.8f]", 
				this.getClass().getSimpleName(), ra, rb, xc, yc, theta);
	}
	
	// ----------------------------------------------------------
	
	public static void main(String[] args) {
		PrintPrecision.set(12);
		
//		Ellipse e1 = new Ellipse( 13, 77, 66, 33, Math.PI/2+9.5);
//		System.out.println("e1 = " + e1.toString());
//		AlgebraicEllipse a1 = AlgebraicEllipse.from(e1);
//		System.out.println("a1 = " + a1.toString());
//		
//		Ellipse e2 = Ellipse.from(a1);
//		System.out.println("e2 = " + e2.toString());
//		AlgebraicEllipse a2 = AlgebraicEllipse.from(e2);
//		System.out.println("a2 = " + a2.toString());
		
		GeometricEllipse ell = new GeometricEllipse(6, 5, 0, 0, 0);
		
		Pnt2d x = Pnt2d.from(0, -0.000000001);
		System.out.println("x  = " + x);
		
		Pnt2d xp = ell.getClosestPoint(x);
		System.out.println("xp = " + xp);
		
		// ---------------------------------------------
		
//		Ellipse eg = new Ellipse(120, 50, 200, 200, 0.4);
//		Ellipse eg = new Ellipse(120, 50, 200, 200, 0.0);
		GeometricEllipse eg1 = new GeometricEllipse(120, 50, 200, 200, Math.PI/2);
		System.out.println("eg1 = " + eg1.toString());
		System.out.println("eg = " + eg1.toString());
		AlgebraicEllipse ea = AlgebraicEllipse.from(eg1);
		System.out.println("ea = " + ea.toString());
		GeometricEllipse eg2 = GeometricEllipse.from(ea);
		System.out.println("eg2 = " + eg2.toString());
		
	}


}
