package imagingbook.pub.mser;

import imagingbook.lib.math.eigen.Eigensolver2x2;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.ellipse.GeometricEllipse;
import imagingbook.pub.regions.BinaryRegion;

/**
 * TODO: Move to BinaryRegion!
 * @author WB
 *
 */
public abstract class Utils {
	
	public static GeometricEllipse makeEllipse(BinaryRegion r) {
		final double n = r.getSize();
		Pnt2d xc = r.getCenter();
		double[] moments = r.getCentralMoments(); // = (mu20, mu02, mu11)
		final double mu20 = moments[0];
		final double mu02 = moments[1];
		final double mu11 = moments[2];
		
		Eigensolver2x2 es = new Eigensolver2x2(mu20, mu11, mu11, mu02);
		double ra = 2 * Math.sqrt(es.getEigenvalue(0) / n);	// correct (see Book p.238)
		double rb = 2 * Math.sqrt(es.getEigenvalue(1) / n);
		double[] x0 = es.getEigenvector(0);
		double theta = Math.atan2(x0[1], x0[0]);
		return new GeometricEllipse(xc.getX(), xc.getY(), ra, rb, theta);
	}

}
