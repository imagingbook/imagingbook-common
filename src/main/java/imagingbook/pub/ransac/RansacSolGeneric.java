package imagingbook.pub.ransac;

import imagingbook.pub.geometry.basic.Pnt2d;


public class RansacSolGeneric<T> {
	
	private T shape;
	private final double score;
	private final Pnt2d[] draw;
	
	public RansacSolGeneric(Pnt2d[] draw, T shape, double score) {
		this.shape = shape;
		this.score = score;
		this.draw = draw;
	}
	
	public T getShape() {
		return shape;
	}
	
	public double getScore() {
		return score;
	}

	public Pnt2d[] getDraw() {
		return draw;
	}

}
