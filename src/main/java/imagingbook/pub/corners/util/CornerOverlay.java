package imagingbook.pub.corners.util;

import java.awt.geom.Path2D;

import ij.gui.ShapeRoi;
import imagingbook.lib.ij.CustomOverlay;
import imagingbook.pub.corners.Corner;

/**
 * A custom ImageJ overlay for drawing corners.
 * 
 * @author WB
 */
public class CornerOverlay extends CustomOverlay<Corner> {
	
	
//	public static int DefaultMarkerType = 1;					// 0=hybrid, 1=cross, 2=dot, 3=circle (see PointRoi)
//	public static int DefaultMarkerSize = 1;					// 0-6 (Tiny-XXXL, see PointRoi)
	
	private double cornerSize = 2;
	
	public void setCornerSize(double cornerSize) {
		this.cornerSize = cornerSize;
	}
	
//	public CornerOverlay(double cornerSize) {
//		this.cornerSize = cornerSize;
//	}
	
//	@Override
//	public Roi makeRoi(Corner c) {
//		PointRoi roi = new PointRoi(c.getX(), c.getY());
//		roi.setPointType(DefaultMarkerType);
//		roi.setSize(DefaultMarkerSize);
//		return roi;
//		return makeCustomCorner(c);
//	}
	
	@Override
	public ShapeRoi makeRoi(Corner c) {
		double xc = c.getX() + 0.5;
		double yc = c.getY() + 0.5;
		Path2D path = new Path2D.Double();
		path.moveTo(xc - cornerSize, yc);
		path.lineTo(xc + cornerSize, yc);
		path.moveTo(xc, yc - cornerSize);
		path.lineTo(xc, yc + cornerSize);
		ShapeRoi cross = new ShapeRoi(path);
		cross.setStrokeWidth(this.getStrokeWidth());
		cross.setStrokeColor(this.getStrokeColor());
		return cross;
	}
}
