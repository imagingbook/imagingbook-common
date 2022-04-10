package imagingbook.pub.ransac;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ij.IJ;
import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.basic.PntUtils;
import imagingbook.pub.geometry.circle.GeometricCircle;
import imagingbook.pub.geometry.fitting.circle.algebraic.CircleFit3Points;
import imagingbook.pub.geometry.fitting.circle.algebraic.CircleFitAlgebraic;
import imagingbook.pub.geometry.fitting.circle.algebraic.CircleFitPratt;


public class RansacCircleDetector {
	
	private int maxIterations = 2000;
	private double distanceThreshold = 2.5;
	private int minSupportCount = 20;
	
	private Random rand = new Random(17);
	
	public RansacCircleDetector() {

	}
	
	// -----------------------------------------------------
	
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	public void setDistanceThreshold(double distanceThreshold) {
		this.distanceThreshold = distanceThreshold;
	}

	public void setMinSupportCount(int minSupportCount) {
		this.minSupportCount = minSupportCount;
	}

	// -----------------------------------------------------
	
	public RansacSolution getNextCircle(Pnt2d[] points) {
		double bestScore = -1;
		RansacSolution bestsolution = null;
		
		for (int i = 0; i < maxIterations; i++) {
			Pnt2d[] draw = drawRandomPoints(points);
			CircleFitAlgebraic fit = new CircleFit3Points(draw);
			GeometricCircle circle = fit.getGeometricCircle();
			if (circle == null) {
				continue;
			}
			double score = countInliers(circle, points);
			if (score >= minSupportCount && score > bestScore) {
				bestScore = score;
				bestsolution = new RansacSolution(draw, circle, score);
			}
		}
		
		if (bestsolution != null) {
			// refit the circle
			Pnt2d[] inliers = collectInliers(bestsolution.getCircle(), points, true);
//			listPnts(inliers);
			IJ.log("reFit: inliers  = " + inliers.length);
//			CircleFitAlgebraic fit2 = new CircleFitKasaA(inliers);	// works
//			CircleFitAlgebraic fit2 = new CircleFitKasaB(inliers);	// works
//			CircleFitAlgebraic fit2 = new CircleFitKasaOrig(inliers);	// fails
			CircleFitAlgebraic fit2 = new CircleFitPratt(inliers);	// fails
//			CircleFitAlgebraic fit2 = new CircleFitHyper(inliers);	// works
			GeometricCircle fitCircle = fit2.getGeometricCircle();
			IJ.log("fitCircle = " + fitCircle);
			bestsolution.setFitCircle(fitCircle);
			bestsolution.setInliers(inliers);
			
//			CircleFitGeometric geomFit = new CircleFitGeometricDist(inliers, bestsolution.circle);
//			IJ.log("geomCircle = " + geomFit.getCircle());
//			IJ.log("geomIterarions = " + geomFit.getIterations());
			
		}
		
		return bestsolution;
	}
	
	private void listPnts(Pnt2d[] pnts) {
		double[][] pa = PntUtils.toDoubleArray(pnts);
		IJ.log("pnts = \n" + Matrix.toString(pa));
	}
	
	
	private Pnt2d[] drawRandomPoints(Pnt2d[] points) {
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
	
	private int countInliers(GeometricCircle circle, Pnt2d[] points) {
		final Pnt2d xc = Pnt2d.from(circle.getXc(), circle.getYc());
		final double r = circle.getR();
		int count = 0;
		for (Pnt2d p : points) {
			if (p != null) {
				double d = p.distance(xc);
				if (Math.abs(d - r) < distanceThreshold) {
					count++;
				}
			}
		}
		return count;
	}
	
	private Pnt2d[] collectInliers(GeometricCircle circle, Pnt2d[] points, boolean remove) {
		final Pnt2d xc = Pnt2d.from(circle.getXc(), circle.getYc());
		final double r = circle.getR();
		List<Pnt2d> pList = new ArrayList<>();
		for (int i = 0; i < points.length; i++) {
			Pnt2d p = points[i];
			if (p != null) {
				double d = p.distance(xc);
				if (Math.abs(d - r) < distanceThreshold) {
					pList.add(p);
					if (remove) {
						points[i] = null;
					}
				}
			}
		}
		return pList.toArray(new Pnt2d[0]);
	}
		
	
	// -----------------------------------------------------
	
	public static class RansacSolution implements Comparable<RansacSolution> {

		private final GeometricCircle circle;
		private final double score;
		private final Pnt2d[] draw;
		
		private GeometricCircle fitLine = null;
		private Pnt2d[] inliers = null;
		
		RansacSolution(Pnt2d[] draw, GeometricCircle circle, double score) {
			this.draw = draw;
			this.circle = circle;
			this.score = score;
		}
		
		public GeometricCircle getCircle() {
			return circle;
		}

		public double getScore() {
			return score;
		}

		public Pnt2d[] getDraw() {
			return draw;
		}
		
		public GeometricCircle getFitCircle() {
			return fitLine;
		}

		public void setFitCircle(GeometricCircle fitLine) {
			this.fitLine = fitLine;
		}

		public Pnt2d[] getInliers() {
			return inliers;
		}

		public void setInliers(Pnt2d[] inliers) {
			this.inliers = inliers;
		}

		@Override
		public int compareTo(RansacSolution other) {
			return Double.compare(other.score, this.score);
		}
	}

}
