package imagingbook.pub.ransacGen;

import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.ellipse.AlgebraicEllipse;
import imagingbook.pub.geometry.ellipse.GeometricEllipse;
import imagingbook.pub.geometry.fitting.ellipse.algebraic.EllipseFit5Points;
import imagingbook.pub.geometry.fitting.ellipse.algebraic.EllipseFitAlgebraic;
import imagingbook.pub.geometry.fitting.ellipse.algebraic.EllipseFitFitzgibbonStable;
import imagingbook.pub.ransacGen.RansacDetector.RansacParameters;

// Generic version of RANSAC ellipse detector
public class RansacDetectorEllipse extends RansacDetector<GeometricEllipse>{
	
	public static class Parameters extends RansacParameters {
		
		public Parameters() {
			this.maxIterations = 1000;
			this.distanceThreshold = 2.0;
			this.minSupportCount = 100;
		}
		
	}
	
	// constructors ------------------------------------
	
	public RansacDetectorEllipse(Parameters params) {
		super(params);
	}
	
	public RansacDetectorEllipse() {
		this(new Parameters());
	}
	
	// ----------------------------------------------------------------
	
	@Override
	protected Pnt2d[] drawRandomPoints(Pnt2d[] points) {
		return drawRandomPoints(points, 5);
	}

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

//	@Override
//	protected RansacResult<GeometricEllipse> createSolution(
//			Pnt2d[] drawnPoints, GeometricEllipse curve, double score, Pnt2d[] inliers) {
//		return new RansacResult<GeometricEllipse>(drawnPoints, curve, score, inliers);
//	}

}
