package imagingbook.pub.geometry.basic;

public class Ellipse {
	
	public final double xc, yc, ra, rb, theta;
	
	public Ellipse(double xc, double yc, double ra, double rb, double theta) {
		this.xc = xc;
		this.yc = yc;
		this.ra = ra;
		this.rb = rb;
		this.theta = theta;
	}
	
	public Ellipse(double xc, double yc, double ra, double rb) {
		this(xc, yc, ra, rb, 0);
	}

}
