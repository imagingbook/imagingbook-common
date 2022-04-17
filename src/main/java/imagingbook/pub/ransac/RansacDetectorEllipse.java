package imagingbook.pub.ransac;

import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.ellipse.AlgebraicEllipse;
import imagingbook.pub.geometry.ellipse.GeometricEllipse;
import imagingbook.pub.geometry.fitting.ellipse.algebraic.EllipseFit5Points;
import imagingbook.pub.geometry.fitting.ellipse.algebraic.EllipseFitAlgebraic;
import imagingbook.pub.geometry.fitting.ellipse.algebraic.EllipseFitFitzgibbonStable;

/**
 * RANSAC detector for ellipses.
 * 
 * @author WB
 * 
 * @see GeometricEllipse
 * @see RansacDetector
 */
public class RansacDetectorEllipse extends RansacDetector<GeometricEllipse>{
	
	// default parameters can be set here
	public static class Parameters extends RansacParameters {
		public Parameters() {
			this.maxIterations = 1000;
			this.distanceThreshold = 2.0;
			this.minSupportCount = 100;
		}
	}
	
	// constructors ------------------------------------
	
	public RansacDetectorEllipse(Parameters params) {
		super(5, params);
	}
	
	public RansacDetectorEllipse() {
		this(new Parameters());
	}
	
	// ----------------------------------------------------------------

	@Override
	protected GeometricEllipse fitInitial(Pnt2d[] points) {
		EllipseFitAlgebraic fit = new EllipseFit5Points(points);
		AlgebraicEllipse ellipse = fit.getEllipse();
		return (ellipse == null) ?  null : new GeometricEllipse(ellipse);
	}
	
	@Override
	protected GeometricEllipse fitFinal(Pnt2d[] inliers) {
		EllipseFitAlgebraic fit2 = new EllipseFitFitzgibbonStable(inliers);
		AlgebraicEllipse ellipse = fit2.getEllipse();
		return (ellipse == null) ?  null : new GeometricEllipse(ellipse);
	}

}
