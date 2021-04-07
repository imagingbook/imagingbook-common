package imagingbook.pub.geometry.basic;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

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
		this(xc, yc, ra, rb, 0.0);
	}
	
	public double getArea() {
		return this.ra * this.rb * Math.PI;
	}
	
	
	public Shape getShape() {
		Ellipse2D oval = new Ellipse2D.Double(-ra, -rb, 2 * ra, 2 * rb);
		AffineTransform trans = new AffineTransform();
		trans.translate(xc, yc);
		trans.rotate(theta);
		return trans.createTransformedShape(oval);
	}

}
