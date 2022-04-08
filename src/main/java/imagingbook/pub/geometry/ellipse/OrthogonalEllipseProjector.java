package imagingbook.pub.geometry.ellipse;

import static imagingbook.lib.math.Arithmetic.sqr;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.pow;

import imagingbook.lib.settings.PrintPrecision;
import imagingbook.pub.geometry.basic.Pnt2d;


/**
 * Calculates orthogonal projections of points onto an ellipse.
 * @author WB
 *
 */
public class OrthogonalEllipseProjector extends EllipseProjector {
	
	private static double NewtonMinStep = 1e-6;
	private static int MaxIterations = 100;
	
	private int lastIterationCount = -1;	// number of Newton iterations performed in last projection
	private int totalIterationCount = -1;	// number of Newton iterations performed since instance was created
	
	
	public OrthogonalEllipseProjector(GeometricEllipse ellipse) {
		super(ellipse);
	}
	
	@Override
	protected double[] projectCanonical(double[] u1) {
		// coordinates of p (mapped to first quadrant)
		final double u = u1[0];	
		final double v = u1[1]; 
		
		double[] ub = null;	// the unknown ellipse point

		if (u + v < 1e-6) {	// (u,v) is very close to the ellipse center; u,v >= 0
			ub = new double[] {0, rb};
		}
		else {						
			double t = max(ra * u - ra2, rb * v - rb2);
			double gprev = Double.POSITIVE_INFINITY;
			double deltaT, deltaG;
			int k = 0;
			do {
				k = k + 1;
				double g  = sqr((ra * u) / (t + ra2)) + sqr((rb * v) / (t + rb2)) - 1;
				double dg = 2 * (sqr(ra * u) / pow(t + ra2, 3) + sqr(rb * v) / pow(t + rb2, 3));
				deltaT = g / dg;
				t = t + deltaT; 			// Newton iteration
				
				// in rare cases g(t) is very flat and checking deltaT is not enough for convergence!
				deltaG = g - gprev;			// change of g value
				gprev = g;	
				
			}  while(abs(deltaT) > NewtonMinStep && abs(deltaG) > NewtonMinStep && k < MaxIterations);
			
			lastIterationCount = k;		// remember iteration count
			totalIterationCount += k;
			
			if (k >= MaxIterations) {
				throw new RuntimeException("max. mumber of iterations exceeded");
			}
			
			ub = new double[] {ra2 * u / (t + ra2), rb2 * v / (t + rb2)};
		}
		return ub;
	}
	

	// for statistics only
	
	public int getLastIterationCount() {
		if (lastIterationCount < 0) {
			throw new IllegalStateException("no projection calculated yet");
		}
		return this.lastIterationCount;
	}
	
	public int getTotalIterationCount() {
		if (totalIterationCount < 0) {
			throw new IllegalStateException("no projection calculated yet");
		}
		return this.totalIterationCount;
	}
	

	
	// Alternative solution ---------------------------------------
	// from BoofCV (georegression.fitting.curves.ClosestPointEllipseAngle_F64
	// (does not converge either in critical case) 
	
	public static Pnt2d project2(Pnt2d point, GeometricEllipse ellipse) {
		final int maxIterations = 100;
		final double tol = 1e-8;
		
		final double Ct = Math.cos(ellipse.theta);
		final double St = Math.sin(ellipse.theta);
		
		// put point into ellipse's coordinate system
		final double xc = point.getX() - ellipse.xc;
		final double yc = point.getY() - ellipse.yc;
//
		double u =  Ct*xc + St*yc;
		double v = -St*xc + Ct*yc;

		// initial guess for the angle
		double alpha = Math.atan2( ellipse.ra*v , ellipse.rb*u);
		
		double a2_m_b2 = ellipse.ra*ellipse.ra - ellipse.rb*ellipse.rb;

		// use Newton's Method to find the solution
		int i = 0;
		for(; i < maxIterations; i++ ) {
			double Ca = Math.cos(alpha);
			double Sa = Math.sin(alpha);

			double f = a2_m_b2*Ca*Sa - u*ellipse.ra*Sa + v*ellipse.rb*Ca;
			if( Math.abs(f) < tol )
				break;

			double d = a2_m_b2*(Ca*Ca - Sa*Sa) - u*ellipse.ra*Ca - v*ellipse.rb*Sa;
			alpha = alpha - f/d;
		}
		System.out.println("iter = " + i);
		
		// compute solution in ellipse coordinate frame
		u = ellipse.ra*Math.cos(alpha);
		v = ellipse.rb*Math.sin(alpha);

		// put back into original coordinate system
		double closestx = Ct*u - St*v + ellipse.xc;
		double closesty = St*u + Ct*v + ellipse.yc;
		return Pnt2d.from(closestx, closesty);
	}
	

	// -------------------------------------------------

	public static void main(String[] args) {
		PrintPrecision.set(8);
		
//		Ellipse ell = new Ellipse(5, 3, 1, 1, 1.1);
//		Pnt2d p = Pnt2d.from(6, 1);
		
		// critical case: 
		 GeometricEllipse ell = new GeometricEllipse(353503.20032614, -9010.22308359, 353613.76725979, 987.23614032, 3.11555492);
		 Pnt2d p = Pnt2d.from(30.000000000, 210.000000000);
		
		EllipseProjector projector = 
				new OrthogonalEllipseProjector(ell);
		
		System.out.println("p  = " + p);
		
		Pnt2d p0 = projector.project(p);
		System.out.println("p0 = " + p0);
		
		System.out.println("dist = " + projector.getDistance(p.toDoubleArray()));
	}

}
