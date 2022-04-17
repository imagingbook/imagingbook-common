package imagingbook.pub.ransac;

import imagingbook.pub.geometry.basic.Curve2d;
import imagingbook.pub.geometry.basic.Pnt2d;



public class RansacResult<T extends Curve2d> {
	
	private final T primitiveInit;
	private final T primitiveFinal;
	private final double score;
	private final Pnt2d[] draw;
	private final Pnt2d[] inliers;
	
	// full constructor (initially inliers = null)
	public RansacResult(Pnt2d[] draw, T primitiveInit, T primitiveFinal, double score, Pnt2d[] inliers) {
		this.primitiveInit = primitiveInit;
		this.primitiveFinal = primitiveFinal;
		this.score = score;
		this.draw = draw;
		this.inliers = inliers;
	}
	
//	// special constructor used to add inliers to an existing solution after final fitting
//	public RansacResult(RansacResult<T> solution, Pnt2d[] inliers) {
//		this(solution.getDraw(), solution.getPrimitiveInit(), solution.getScore(), inliers);
//	}
	
	/**
	 * Returns the initial primitive (e.g., a circle) obtained from the random draw.
	 * @return the initial primitive
	 */
	public T getPrimitiveInit() {
		return primitiveInit;
	}
	
	/**
	 * Returns the final primitive obtained by fitting to all inliers.
	 * @return the final primitive
	 */
	public T getPrimitiveFinal() {
		return primitiveFinal;
	}
	
	/**
	 * Returns the score (number of inliers) associated with this result.
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * Returns the randomly drawn points that lead to this result.
	 * @return array of points
	 */
	public Pnt2d[] getDraw() {
		return draw;
	}
	
	/**
	 * Returns the set of inliers (points) associated with this result.
	 * @return array of points
	 */
	public Pnt2d[] getInliers() {
		return inliers;
	}
	
	// ---------------------------------------------------------------------------
	

	
	
	public int compareTo(RansacResult<T> other) {
		return Double.compare(other.score, this.score);
	}

}
