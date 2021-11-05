package imagingbook.pub.geometry.basic;

public abstract class PntUtils {
	
	public static Pnt2d centroid(Pnt2d[] pts) {
		final int n = pts.length;
		double sx = 0;
		double sy = 0;
		for (int i = 0; i < n; i++) {
			sx = sx + pts[i].getX();
			sy = sy + pts[i].getY();
		}
		return Pnt2d.from(sx/n, sy/n);
	}
	
}
