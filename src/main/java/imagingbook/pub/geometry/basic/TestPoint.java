package imagingbook.pub.geometry.basic;

import imagingbook.pub.geometry.basic.Pnt2d.PntDouble;
import imagingbook.pub.geometry.basic.Pnt2d.PntInt;

public class TestPoint {
	
	public static void main(String[] args) {
//		Point2D.Double p1 = new Point2D.Double(1.5, 5.0);
//		System.out.println(p1);
//		p1.setLocation(0, 0);
//		System.out.println("p1 = " + p1);
//		
//		p1.x = 2;
//		System.out.println("p1 (mod) = " + p1);
		
		Pnt2d p2 = PntDouble.from(3.0, 8.0);
		System.out.println("p2 = " + p2);
//		p2.setLocation(0, 0);
//		System.out.println(p2);
		
//		System.out.println(p2 instanceof Point2D);
//		System.out.println(p2 instanceof Point2D.Double);
//		System.out.println(p2 instanceof java.awt.Point);
		
		Pnt2d p3 = PntInt.from(3, 8);
		System.out.println("p3 = " + p3);
		System.out.println(p3.getX() + " " + p3.getY());

		Pnt2d.PntInt p4 = PntInt.from(3, 8);
		System.out.println(p4.x + " " + p4.y);
		
//		p3 = (Point) new java.awt.Point(4,5);
//		System.out.println(p3);
	}

}
