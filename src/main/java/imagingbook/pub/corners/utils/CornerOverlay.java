package imagingbook.pub.corners.utils;

import java.awt.geom.Path2D;

import ij.gui.Roi;
import ij.gui.ShapeRoi;
import imagingbook.lib.ij.CustomOverlay;
import imagingbook.pub.corners.Corner;

public class CornerOverlay extends CustomOverlay<Corner> {
	
	private double size = 2;
	
	public void setMarkerSize(double size) {
		this.size = size;
	}
	
	@Override
	public Roi makeRoi(Corner c) {
		double x = c.getX(); 
		double y = c.getY(); 
		Path2D poly = new Path2D.Double();
		poly.moveTo(x - size, y);
		poly.lineTo(x + size, y);
		poly.moveTo(x, y - size);
		poly.lineTo(x, y + size);
		return new ShapeRoi(poly);
	}

}
