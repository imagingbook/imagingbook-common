package imagingbook.pub.geometry.basic;

import java.awt.geom.Point2D;

public class TestPoint {
	
	public static void main(String[] args) {
		Point2D.Double p1 = new Point2D.Double(1.5, 5.0);
		System.out.println(p1);
		p1.setLocation(0, 0);
		System.out.println(p1);
		
		p1.x = 2;
		System.out.println(p1);
		
		Point p2 = Point.create(3.0, 8.0);
		System.out.println(p2);
//		p2.setLocation(0, 0);
//		System.out.println(p2);
		
		System.out.println(p2 instanceof Point2D);
		System.out.println(p2 instanceof Point2D.Double);
		System.out.println(p2 instanceof java.awt.Point);
		
		
		Point p3 = Point.create(3, 8);
		System.out.println(p3.getClass());
		System.out.println(p3.getX() + " " + p3.getY());
		
		Point p4 = Point.create(p3);
		System.out.println(p4.getClass());
		
//		p3 = (Point) new java.awt.Point(4,5);
//		System.out.println(p3);
	}

}
