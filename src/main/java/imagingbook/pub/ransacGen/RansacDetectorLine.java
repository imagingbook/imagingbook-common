package imagingbook.pub.ransacGen;

import static imagingbook.lib.math.Arithmetic.sqr;

import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.fitting.line.LineFit;
import imagingbook.pub.geometry.fitting.line.OrthogonalLineFitEigen;
import imagingbook.pub.geometry.line.AlgebraicLine;

// Generic version of RANSAC line detector
public class RansacDetectorLine extends RansacDetector<AlgebraicLine>{
	
//	public static final int DefaultMaxIterations = 1000;
//	public static final double DefaultDistanceThreshold = 2.0;
//	public static final int DefaultMinSupportCount = 100;
//	public static final double DefaultMinPairDistance = 25;
	
	private final Parameters params;
	
	public static class Parameters extends RansacParameters {
		
		@DialogLabel("Min. distance between pairs")
		public int minPairDistance;
		
		public Parameters() {
			this.maxIterations = 1000;
			this.distanceThreshold = 2.0;
			this.minSupportCount = 100;
			this.minPairDistance = 25;
		}	
	}
	
	// constructors ------------------------------------
	
	public RansacDetectorLine(Parameters params) {
		super(params);
		this.params = params;
	}
	
	public RansacDetectorLine() {
		this(new Parameters());
	}
	
	// ----------------------------------------------------------------
	
	@Override
	protected Pnt2d[] drawRandomPoints(Pnt2d[] points) {
		Pnt2d p1 = points[rand.nextInt(points.length)];
		while (p1 == null) {
			p1 = points[rand.nextInt(points.length)];
		}
		
		Pnt2d p2 = points[rand.nextInt(points.length)];
		while (p2 == null || p1.distanceSq(p2) < sqr(params.minPairDistance)) {
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

//	@Override
//	protected RansacResult<AlgebraicLine> createSolution(
//			Pnt2d[] drawnPoints, AlgebraicLine curve, double score, Pnt2d[] inliers) {
//		return new RansacResult<AlgebraicLine>(drawnPoints, curve, score, inliers);
//	}

}
