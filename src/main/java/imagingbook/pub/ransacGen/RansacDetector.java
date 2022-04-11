package imagingbook.pub.ransacGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import imagingbook.pub.geometry.basic.Curve2d;
import imagingbook.pub.geometry.basic.Pnt2d;

public abstract class RansacDetector<T extends Curve2d> {
	
	protected int maxIterations = 2000;
	protected double distanceThreshold = 2.5;
	protected int minSupportCount = 20;	
	protected Random rand = new Random();
	
	// only to be called by inheriting classes
	protected RansacDetector(int maxIterations, double distanceThreshold, int minSupportCount) {
		this.maxIterations = maxIterations;
		this.distanceThreshold = distanceThreshold;
		this.minSupportCount = minSupportCount;
	}
	
	// -----------------------------------------------------------
	
	/**
	 * Resets the internal random number generator to use the
	 * specified seed. This is useful to obtain repeatable results.
	 * 
	 * @param seed seed for new {@link Random} instance
	 */
	public void setRandomSeed(long seed) {
		rand = new Random(seed);
	}
	
	// ----------------------------------------------------------
	
	protected int countInliers(T curve, Pnt2d[] points) {
		int count = 0;
		for (Pnt2d p : points) {
			if (p != null) {
				double d = curve.getDistance(p);
				if (d < distanceThreshold) {
					count++;
				}
			}
		}
		return count;
	}
	
	protected Pnt2d[] collectInliers(Curve2d curve, Pnt2d[] points, boolean removeInliers) {
		List<Pnt2d> pList = new ArrayList<>();
		for (int i = 0; i < points.length; i++) {
			Pnt2d p = points[i];
			if (p != null) {
				double d = curve.getDistance(p);
				if (d < distanceThreshold) {
					pList.add(p);
					if (removeInliers) {
						points[i] = null;
					}
				}
			}
		}
		return pList.toArray(new Pnt2d[0]);
	}
	
	public RansacSolGeneric<T> getNextSolution(Pnt2d[] points) {
		return getNextSolution(points, true);
	}
			
	public RansacSolGeneric<T> getNextSolution(Pnt2d[] points, boolean removeInliers) {
		double bestScore = -1;
		RansacSolGeneric<T> bestsolution = null;
		
		for (int i = 0; i < maxIterations; i++) {
			Pnt2d[] drawnPoints = drawRandomPoints(points);
			T curve = fitInitial(drawnPoints);
			if (curve == null) {
				continue;
			}
			double score = countInliers(curve, points);
//			IJ.log("   iteration " + i + ": score=" + score);
			if (score >= minSupportCount && score > bestScore) {
				bestScore = score;
				bestsolution = createSolution(drawnPoints, curve, score, null);
			}
		}
		
		if (bestsolution == null) {
			return null;
		}
		else {
			// refit the curve to all inliers:
			Pnt2d[] inliers = collectInliers(bestsolution.getCurve(), points, removeInliers);
			T curveFinal = fitFinal(inliers);			
			return createSolution(bestsolution.getDraw(), curveFinal, bestsolution.getScore(), inliers);
		}
	}
	
	protected Pnt2d[] drawRandomPoints(Pnt2d[] points, int k) {
		Pnt2d[] draw = new Pnt2d[k];
		for (int i = 0; i < draw.length; i++) {
			do {
				draw[i] = points[rand.nextInt(points.length)];
			}
			while (draw[i] == null);	// TODO: shaky!
		}

		return draw;
	}
	
	// abstract methods to be implemented by specific sub-classes -----------------------

	protected abstract Pnt2d[] drawRandomPoints(Pnt2d[] points);
	protected abstract T fitInitial(Pnt2d[] draw);
	protected abstract T fitFinal(Pnt2d[] inliers);
	protected abstract RansacSolGeneric<T> createSolution(Pnt2d[] drawPts, T curve, double score, Pnt2d[] inliers);
	
}
