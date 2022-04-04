package imagingbook.pub.geometry.fitting.ellipse;

import static imagingbook.lib.math.Arithmetic.mod;
import static java.lang.Math.PI;

import java.util.Random;

import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.ellipse.GeometricEllipse;

public class EllipseSampler {
	
	static int RandomSeed = 11;
	
	private final GeometricEllipse ellipse;
	
	public EllipseSampler(GeometricEllipse ellipse) {
		this.ellipse = ellipse;
	}
	
	/**
	 * Create an array of (x,y) coordinates on an ellipse of radii (a,b) and
	 * rotated r radians. Random noise is added if noise > 0.
	 * @param xc x-center
	 * @param yc y-center
	 * @param ra first axis length
	 * @param rb second axis length
	 * @param theta angle of rotation
	 * @param n number of points
	 * @param startAngle initial angle
	 * @param arcAngle arc angle
	 * @param sigma intensity of random noise
	 * @return
	 */
	public Pnt2d[] getPoints(int n, double startAngle, double arcAngle, double sigma) {
		Pnt2d[] points = new Pnt2d[n];
		Random rd= new Random(RandomSeed);
		
		double xc = ellipse.xc;
		double yc = ellipse.yc;
		double ra = ellipse.ra;
		double rb = ellipse.rb;
		double theta = ellipse.theta;
		
		startAngle = mod(startAngle, 2 * PI);	
		arcAngle   = mod(arcAngle, 2 * PI);
		if (arcAngle == 0)
			arcAngle = 2 * PI;

		
//		double dAngle;
//		if (endAngle > startAngle) {
//			dAngle = endAngle - startAngle;
//		}
//		else if (endAngle < startAngle) {
//			dAngle = endAngle + 2 * PI - startAngle;
//		}
//		else {	// endAngle == startAngle
//			dAngle = 2 * PI;
//		}

		final double cosTh = Math.cos(theta);
		final double sinTh = Math.sin(theta);
		
		for (int i = 0; i < n; i++) {
			double alpha = startAngle + arcAngle * i / n;
			double x0 = ra * Math.cos(alpha) + sigma * rd.nextGaussian();
			double y0 = rb * Math.sin(alpha) + sigma * rd.nextGaussian();
			double x = x0 * cosTh - y0 * sinTh + xc;
			double y = x0 * sinTh + y0 * cosTh + yc;
			points[i] = Pnt2d.from(x, y);
		}
		return points;
	}

}