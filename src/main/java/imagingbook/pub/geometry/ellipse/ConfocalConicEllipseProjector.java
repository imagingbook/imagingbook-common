package imagingbook.pub.geometry.ellipse;

import static imagingbook.lib.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

import imagingbook.lib.settings.PrintPrecision;
import imagingbook.pub.geometry.basic.Pnt2d;


/**
 * Finds an approximate closest point on the ellipse
 * @author WB
 *
 */
public class ConfocalConicEllipseProjector extends EllipseProjector {
	
	public ConfocalConicEllipseProjector(GeometricEllipse ellipse) {
		super(ellipse);
	}
	
	// ellipse projection in quadrant 1 of u/v space
	@Override
	protected double[] projectCanonical(double[] uv) {
		// uv is supposed to be in quadrant 1 of canonical frame
		double u = uv[0];
		double v = uv[1];
		double u2 = sqr(u);
		double v2 = sqr(v);
		double fe2 = ra2 - rb2;
		double b = (u2 + v2 + fe2);
		double sa2 = 0.5 * (b - sqrt(sqr(b) - 4 * u2 * fe2));
		double sb2 = fe2 - sa2;	
		double c = 1 / sqrt(ra2 * sb2 + rb2 * sa2);	
		return new double[] {c * ra * sqrt(sa2 * (rb2 + sb2)), c * rb * sqrt(sb2 * (ra2 - sa2))};
	}
	
	// -------------------------------------------------

	public static void main(String[] args) {
		PrintPrecision.set(8);
		
//		Ellipse ell = new Ellipse(150, 80, 0, 0, 0);
//		Pnt2d p = Pnt2d.from(100, 110);
		
		// critical case: 
		 GeometricEllipse ell = new GeometricEllipse(353503.20032614, -9010.22308359, 353613.76725979, 987.23614032, 3.11555492);
		 Pnt2d p = Pnt2d.from(30.000000000, 210.000000000);
		
		EllipseProjector projector = 
				new ConfocalConicEllipseProjector(ell);
		
		
		System.out.println("p  = " + p);
		
		Pnt2d p0 = projector.project(p);
		System.out.println("p0 = " + p0);
		
		System.out.println("dist = " + projector.getDistance(p.toDoubleArray()));

	}
	

}
