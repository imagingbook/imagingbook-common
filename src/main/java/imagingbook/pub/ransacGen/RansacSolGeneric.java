package imagingbook.pub.ransacGen;

import imagingbook.pub.geometry.basic.Curve2d;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.circle.GeometricCircle;



public class RansacSolGeneric<T extends Curve2d> {
	
	private final T curve;
	private final double score;
	private final Pnt2d[] draw;
	private final Pnt2d[] inliers;
	
	// full constructor (initially inliers = null)
	public RansacSolGeneric(Pnt2d[] draw, T shape, double score, Pnt2d[] inliers) {
		this.curve = shape;
		this.score = score;
		this.draw = draw;
		this.inliers = inliers;
	}
	
	// special constructor used to add inliers to an existing solution after final fitting
	public RansacSolGeneric(RansacSolGeneric<T> solution, Pnt2d[] inliers) {
		this(solution.getDraw(), solution.getCurve(), solution.getScore(), inliers);
	}
	
	public T getCurve() {
		return curve;
	}
	
	public double getScore() {
		return score;
	}

	public Pnt2d[] getDraw() {
		return draw;
	}
	
	public Pnt2d[] getInliers() {
		return inliers;
	}
	
	// ---------------------------------------------------------------------------
	

	
	
	public int compareTo(RansacSolGeneric<T> other) {
		return Double.compare(other.score, this.score);
	}

}
