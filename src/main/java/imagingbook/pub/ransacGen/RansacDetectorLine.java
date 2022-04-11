package imagingbook.pub.ransacGen;

import static imagingbook.lib.math.Arithmetic.sqr;

import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.fitting.line.LineFit;
import imagingbook.pub.geometry.fitting.line.OrthogonalLineFitEigen;
import imagingbook.pub.geometry.line.AlgebraicLine;

// Generic version of RANSAC line detector
public class RansacDetectorLine extends RansacDetector<AlgebraicLine>{
	
	public static final int DefaultMaxIterations = 1000;
	public static final double DefaultDistanceThreshold = 2.0;
	public static final int DefaultMinSupportCount = 100;
	public static final double DefaultMinPairDistance = 25;
	
	private double minPairDistance;
	
	// constructors ------------------------------------
	
	public RansacDetectorLine(int maxIterations, double distanceThreshold, int minSupportCount, double minPairDistance) {
		super(maxIterations, distanceThreshold, minSupportCount);
		this.minPairDistance = minPairDistance;
	}
	
	public RansacDetectorLine() {
		this(DefaultMaxIterations, DefaultDistanceThreshold, DefaultMinSupportCount, DefaultMinPairDistance);
	}
	
	// ----------------------------------------------------------------
	
	@Override
	protected Pnt2d[] drawRandomPoints(Pnt2d[] points) {
		Pnt2d p1 = points[rand.nextInt(points.length)];
		while (p1 == null) {
			p1 = points[rand.nextInt(points.length)];
		}
		
		Pnt2d p2 = points[rand.nextInt(points.length)];
		while (p2 == null || p1.distanceSq(p2) < sqr(minPairDistance)) {
			p2 = points[rand.nextInt(points.length)];
		}
		return new Pnt2d[] {p1, p2};
	}

	@Override
	protected AlgebraicLine fitInitial(Pnt2d[] points) {
		return AlgebraicLine.from(points[0], points[1]);
	}
	
	@Override
	protected AlgebraicLine fitFinal(Pnt2d[] inliers) {
		LineFit fit = new OrthogonalLineFitEigen(inliers);
		return fit.getLine();
	}

	@Override
	protected RansacSolGeneric<AlgebraicLine> createSolution(
			Pnt2d[] drawnPoints, AlgebraicLine curve, double score, Pnt2d[] inliers) {
		return new RansacSolGeneric<AlgebraicLine>(drawnPoints, curve, score, inliers);
	}

}
