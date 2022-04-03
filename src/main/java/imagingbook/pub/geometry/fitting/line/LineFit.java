package imagingbook.pub.geometry.fitting.line;

import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.line.AlgebraicLine;

public interface LineFit {
	
	public abstract int getSize();
	
	public abstract double[] getLineParameters();

	public default AlgebraicLine getLine() {
		double[] p = this.getLineParameters();
		if (p == null) {
			return null;
		}
		else {
			return new AlgebraicLine(p);
		}
	}
	
//	public abstract Pnt2d[] getPoints();
	
	public default double getOrthogonalError(Pnt2d[] points) {
//		final Pnt2d[] points = getPoints();
		AlgebraicLine line = getLine();
		return line.getSquareError(points);
	}
}
