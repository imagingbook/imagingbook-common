package imagingbook.pub.geometry.fitting.line;

import java.util.Random;

import imagingbook.pub.geometry.basic.Pnt2d;


public class LineSampler {
	
	static int RandomSeed = 11;
	
	private final Pnt2d p1, p2;
	
	public LineSampler(Pnt2d p1, Pnt2d p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public Pnt2d[] getPoints(int n, double sigma) {
		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();
		
		Random rd = new Random(RandomSeed);
		Pnt2d[] pts = new Pnt2d[n];
		
		for (int i = 0; i < n; i++) {
			double x = p1.getX() + dx * i / n + rd.nextGaussian() * sigma;
			double y = p1.getY() + dy * i / n + rd.nextGaussian() * sigma;
			pts[i] = Pnt2d.from(x, y);
		}
		
		return pts;
	}

}
