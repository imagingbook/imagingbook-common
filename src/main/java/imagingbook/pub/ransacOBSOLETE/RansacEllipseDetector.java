package imagingbook.pub.ransacOBSOLETE;

import static imagingbook.lib.math.Arithmetic.sqr;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ij.IJ;
import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.basic.PntUtils;
import imagingbook.pub.geometry.ellipse.AlgebraicEllipse;
import imagingbook.pub.geometry.ellipse.ConfocalConicEllipseProjector;
import imagingbook.pub.geometry.ellipse.EllipseProjector;
import imagingbook.pub.geometry.ellipse.GeometricEllipse;
import imagingbook.pub.geometry.fitting.ellipse.algebraic.EllipseFit5Points;
import imagingbook.pub.geometry.fitting.ellipse.algebraic.EllipseFitAlgebraic;
import imagingbook.pub.geometry.fitting.ellipse.algebraic.EllipseFitFitzgibbonStable;

@Deprecated
public class RansacEllipseDetector {
	
	private int maxIterations = 1000;
	private double distanceThreshold = 2.5;
	private int minSupportCount = 10;
	
//	private Random rand = new Random(17);
	private Random rand = new Random();
	
	public RansacEllipseDetector() {

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
	
	public RansacSolution getNextItem(Pnt2d[] points) {
		IJ.log("maxIterations = " + maxIterations);
		double bestScore = -1;
		RansacSolution bestsolution = null;
		
		for (int i = 0; i < maxIterations; i++) {
			Pnt2d[] draw = drawRandomPoints(points, 5);
//			IJ.log("iteration = " + i);
//			IJ.log("draw = " + Arrays.toString(draw));
			
//			EllipseFitAlgebraic fit = new EllipseFitFitzgibbonStable(draw);
//			EllipseFitAlgebraic fit = new EllipseFitTaubin2(draw);
			EllipseFitAlgebraic fit = new EllipseFit5Points(draw);
			
			AlgebraicEllipse ellipse = fit.getEllipse();
			if (ellipse == null) {
				continue;
			}
			
			GeometricEllipse eg = GeometricEllipse.from(ellipse);
			double err = eg.getMeanSquareError(draw);
			if (err > 2) {
				continue;
			}
			
//			IJ.log("ellipse = " + eg);
//			IJ.log("error=" + err);
			
	
			double score = countInliers(ellipse, points);
//			IJ.log("score = " + score);
			if (score >= minSupportCount && score > bestScore) {
				bestScore = score;
				bestsolution = new RansacSolution(draw, ellipse, score);
			}
			
		}
		
//		IJ.log("\nbestscore = " + bestScore);
		
		if (bestsolution != null) {
			GeometricEllipse eg = GeometricEllipse.from(bestsolution.getEllipse());
//			IJ.log("ellipse fitting error  = " + eg.getError(bestsolution.getDraw()));
			
			
			// refit the circle
			Pnt2d[] inliers = collectInliers(bestsolution.getEllipse(), points, true);
//			listPnts(inliers);
//			IJ.log("reFit: inliers  = " + inliers.length);
			EllipseFitAlgebraic fit2 = new EllipseFitFitzgibbonStable(inliers);
			AlgebraicEllipse fitEllipse = fit2.getEllipse();
//			IJ.log("fitCircle = " + fitEllipse);
			bestsolution.setFitEllipse(fitEllipse);
			bestsolution.setInliers(inliers);			
		}
		
		return bestsolution;
	}
	
	private void listPnts(Pnt2d[] pnts) {
		double[][] pa = PntUtils.toDoubleArray(pnts);
		IJ.log("pnts = \n" + Matrix.toString(pa));
	}
	
	
	private Pnt2d[] drawRandomPoints(Pnt2d[] points, int k) {
		 Pnt2d[] draw = new Pnt2d[k];
		for (int i = 0; i < draw.length; i++) {
			do {
				draw[i] = points[rand.nextInt(points.length)];
			}
			while (draw[i] == null);	// TODO: shaky!
		}
		
		return draw;
	}
	
	private int countInliers(AlgebraicEllipse ea, Pnt2d[] points) {
//		IJ.log("counting inliers " + distanceThreshold);
		double distanceThreshold2 = sqr(distanceThreshold);
		int count = 0;
		EllipseProjector projector = new ConfocalConicEllipseProjector(GeometricEllipse.from(ea));
		for (Pnt2d p : points) {
			if (p != null) {
//				double d = ea.getAlgebraicDistance(p);
//				double d = ea.getSampsonDistance(p);
				double d2 = projector.getDistanceSq(p.toDoubleArray());
//				IJ.log("distance " + p.toString() + " = " + d);
				if (Math.abs(d2) < distanceThreshold2) {
					count++;
				}
			}
		}
//		IJ.log("counting inliers done " + count);
		return count;
	}
	
	private Pnt2d[] collectInliers(AlgebraicEllipse ea, Pnt2d[] points, boolean remove) {
		List<Pnt2d> pList = new ArrayList<>();
		double distanceThreshold2 = sqr(distanceThreshold);
		EllipseProjector projector = new ConfocalConicEllipseProjector(GeometricEllipse.from(ea));
		for (int i = 0; i < points.length; i++) {
			Pnt2d p = points[i];
			if (p != null) {
//				double d = ea.getAlgebraicDistance(p);
//				double d = ea.getSampsonDistance(p);
				double d2 = projector.getDistanceSq(p.toDoubleArray());
				if (d2 < distanceThreshold2) {
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

		private final AlgebraicEllipse ellipse;
		private final double score;
		private final Pnt2d[] draw;
		
		private AlgebraicEllipse fitEllipse = null;
		private Pnt2d[] inliers = null;
		
		RansacSolution(Pnt2d[] draw, AlgebraicEllipse ellipse, double score) {
			this.draw = draw;
			this.ellipse = ellipse;
			this.score = score;
		}
		
		public AlgebraicEllipse getEllipse() {
			return ellipse;
		}

		public double getScore() {
			return score;
		}

		public Pnt2d[] getDraw() {
			return draw;
		}
		
		public AlgebraicEllipse getFitEllipse() {
			return fitEllipse;
		}

		public void setFitEllipse(AlgebraicEllipse fitLine) {
			this.fitEllipse = fitLine;
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
