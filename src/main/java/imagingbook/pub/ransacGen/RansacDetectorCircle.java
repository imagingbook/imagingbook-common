package imagingbook.pub.ransacGen;

import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.circle.GeometricCircle;
import imagingbook.pub.geometry.fitting.circle.algebraic.CircleFit3Points;
import imagingbook.pub.geometry.fitting.circle.algebraic.CircleFitAlgebraic;
import imagingbook.pub.geometry.fitting.circle.algebraic.CircleFitPratt;

// Generic version of RANSAC circle detector
public class RansacDetectorCircle extends RansacDetector<GeometricCircle>{
	
	public static final int DefaultMaxIterations = 1000;
	public static final double DefaultDistanceThreshold = 2.0;
	public static final int DefaultMinSupportCount = 100;
	
	// constructors ------------------------------------
	
	public RansacDetectorCircle(int maxIterations, double distanceThreshold, int minSupportCount) {
		super(maxIterations, distanceThreshold, minSupportCount);
	}
	
	public RansacDetectorCircle() {
		this(DefaultMaxIterations, DefaultDistanceThreshold, DefaultMinSupportCount);
	}
	
	// ----------------------------------------------------------------
	
	@Override
	protected Pnt2d[] drawRandomPoints(Pnt2d[] points) {
		Pnt2d p1 = points[rand.nextInt(points.length)];
		while (p1 == null) {
			p1 = points[rand.nextInt(points.length)];
		}
		
		Pnt2d p2 = points[rand.nextInt(points.length)];
		while (p2 == null || p2.equals(p1)) {
			p2 = points[rand.nextInt(points.length)];
		}
		
		Pnt2d p3 = points[rand.nextInt(points.length)];
		while (p3 == null || p2.equals(p1) || p3.equals(p2)) {
			p3 = points[rand.nextInt(points.length)];
		}
		
		return new Pnt2d[] {p1, p2, p3};
	}

	@Override
	protected GeometricCircle fitInitial(Pnt2d[] points) {
		CircleFitAlgebraic fit = new CircleFit3Points(points);
		return fit.getGeometricCircle();
	}
	
	@Override
	protected GeometricCircle fitFinal(Pnt2d[] inliers) {
//		CircleFitAlgebraic fit2 = new CircleFitKasaA(inliers);	// works
//		CircleFitAlgebraic fit2 = new CircleFitKasaB(inliers);	// works
//		CircleFitAlgebraic fit2 = new CircleFitKasaOrig(inliers);	// fails
		CircleFitAlgebraic fit2 = new CircleFitPratt(inliers);	// fails
//		CircleFitAlgebraic fit2 = new CircleFitHyper(inliers);	// works
		return fit2.getGeometricCircle();
	}

	@Override
	protected RansacSolGeneric<GeometricCircle> createSolution(
			Pnt2d[] drawnPoints, GeometricCircle curve, double score, Pnt2d[] inliers) {
		return new RansacSolGeneric<GeometricCircle>(drawnPoints, curve, score, inliers);
	}

}
