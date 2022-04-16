package imagingbook.pub.ransacGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import imagingbook.lib.util.ParameterBundle;
import imagingbook.pub.geometry.basic.Curve2d;
import imagingbook.pub.geometry.basic.Pnt2d;

public abstract class RansacDetector<T extends Curve2d> {
	
	public static class RansacParameters implements ParameterBundle {
		
		@DialogLabel("Max. iterations")
		public int maxIterations = 1000;
		
		@DialogLabel("Distance threshold")
		public double distanceThreshold = 2.0;
		
		@DialogLabel("Min. support count")
		public int minSupportCount = 100;

	}
	
	private final RansacParameters params;
	protected Random rand = new Random();	// should be private, provide method!
	
	protected RansacDetector(RansacParameters params) {
		this.params = params;
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
				if (d < params.distanceThreshold) {
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
				if (d < params.distanceThreshold) {
					pList.add(p);
					if (removeInliers) {
						points[i] = null;
					}
				}
			}
		}
		return pList.toArray(new Pnt2d[0]);
	}
	
	public RansacResult<T> getNextSolution(Pnt2d[] points) {
		return getNextSolution(points, true);
	}
			
	public RansacResult<T> getNextSolution(Pnt2d[] points, boolean removeInliers) {
		Pnt2d[] drawInit = null;
		double scoreInit = -1;
		T primitiveInit = null;
		
		for (int i = 0; i < params.maxIterations; i++) {
			Pnt2d[] draw = drawRandomPoints(points);
			T primitive = fitInitial(draw);
			if (primitive == null) {
				continue;
			}
			double score = countInliers(primitive, points);
			if (score >= params.minSupportCount && score > scoreInit) {
				scoreInit = score;
				drawInit = draw;
				primitiveInit = primitive;
			}
		}
		
		if (primitiveInit == null) {
			return null;
		}
		else {
			// refit the primitive to all inliers:
			Pnt2d[] inliers = collectInliers(primitiveInit, points, removeInliers);
			T primitiveFinal = fitFinal(inliers);	
			if (primitiveFinal != null)
				return new RansacResult<T>(drawInit, primitiveInit, primitiveFinal, scoreInit, inliers);
			else
				throw new RuntimeException("final fit failed!");
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
	
}
