package imagingbook.pub.ransac;


import static imagingbook.lib.math.Arithmetic.sqr;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.fitting.line.LineFit;
import imagingbook.pub.geometry.fitting.line.OrthogonalLineFitEigen;
import imagingbook.pub.geometry.line.AlgebraicLine;

/**
 * RANSAC straight line detector.
 * 
 * @author WB
 *
 */
public class RansacLineDetector {
	
	private int maxIterations = 2000;
	private double distanceThreshold = 2.5;
	private double minPairDistance = 25;
	private int minSupportCount = 20;
	
	private Random rand = new Random(17);
	
	
	public RansacLineDetector() {

	}
	
	// -----------------------------------------------------
	
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	public void setDistanceThreshold(double distanceThreshold) {
		this.distanceThreshold = distanceThreshold;
	}

	public void setMinPairDistance(double minPairDistance) {
		this.minPairDistance = minPairDistance;
	}

	public void setMinSupportCount(int minSupportCount) {
		this.minSupportCount = minSupportCount;
	}

	// -----------------------------------------------------
	
	public RansacSolution getNextLine(Pnt2d[] points) {
		double bestScore = -1;
		RansacSolution bestsolution = null;
		
		for (int i = 0; i < maxIterations; i++) {
			Pnt2d[] draw = drawRandomPoints(points);
			AlgebraicLine line = AlgebraicLine.from(draw[0], draw[1]);
			double score = countInliers(line, points);
			if (score >= minSupportCount && score > bestScore) {
				bestScore = score;
				bestsolution = new RansacSolution(draw, line, score);
			}
		}
		
		if (bestsolution != null) {
			// refit the line
			Pnt2d[] inliers = collectInliers(bestsolution.getLine(), points, true);
			LineFit fit = new OrthogonalLineFitEigen(inliers);
			AlgebraicLine fitLine = fit.getLine();
			bestsolution.setFitLine(fitLine);
			bestsolution.setInliers(inliers);
		}
		
		return bestsolution;
	}
	
	
	private Pnt2d[] drawRandomPoints(Pnt2d[] points) {
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
	
	private int countInliers(AlgebraicLine line, Pnt2d[] points) {
		int count = 0;
		for (Pnt2d p : points) {
			if (p != null && Math.abs(line.getDistance(p)) < distanceThreshold) {
				count++;
			}
		}
		return count;
	}
	
	private Pnt2d[] collectInliers(AlgebraicLine line, Pnt2d[] points, boolean remove) {
		List<Pnt2d> pList = new ArrayList<>();
		for (int i = 0; i < points.length; i++) {
			Pnt2d p = points[i];
			if (p != null && Math.abs(line.getDistance(p)) < distanceThreshold) {
				pList.add(p);
				if (remove) {
					points[i] = null;
				}
			}
		}
		return pList.toArray(new Pnt2d[0]);
	}
	
	// -----------------------------------------------------
	
	public static class RansacSolution implements Comparable<RansacSolution> {

		private final AlgebraicLine line;
		private final double score;
		private final Pnt2d[] draw;
		
		private AlgebraicLine fitLine = null;
		private Pnt2d[] inliers = null;
		
		RansacSolution(Pnt2d[] draw, AlgebraicLine line, double score) {
			this.draw = draw;
			this.line = line;
			this.score = score;
		}
		
		public AlgebraicLine getLine() {
			return line;
		}

		public double getScore() {
			return score;
		}

		public Pnt2d[] getDraw() {
			return draw;
		}
		
		public AlgebraicLine getFitLine() {
			return fitLine;
		}

		public void setFitLine(AlgebraicLine fitLine) {
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
