package imagingbook.pub.corners.utils;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.List;

import ij.gui.Overlay;
import ij.gui.ShapeRoi;
import imagingbook.pub.corners.Corner;

// TODO: add size of cross parameter! Make color/width dynamic??
public class CornerOverlay extends Overlay {
	
	public static Color DefaultStrokeColor = Color.green;
	public static double DefaultStrokeWidth = 0.25;
	
	private static AffineTransform offset = AffineTransform.getTranslateInstance(0.5, 0.5);
	
	private final Color strokeColor;
	private final double strokeWidth;
	
	public CornerOverlay(List<Corner> corners, Color strokeColor, double strokeWidth) {
		this.strokeColor = strokeColor;
		this.strokeWidth = strokeWidth;
		for (Corner c : corners) {
			addCorner(c);
		}
	}
	
	public CornerOverlay(List<Corner> corners) {
		this(corners, DefaultStrokeColor, DefaultStrokeWidth);
	}
	
	public void addCorner(Corner c) {
		final double x = c.getX(); 
		final double y = c.getY(); 
		Path2D poly = new Path2D.Double();
		poly.moveTo(x - 2, y);
		poly.lineTo(x + 2, y);
		poly.moveTo(x, y - 2);
		poly.lineTo(x, y + 2);
		poly.closePath();
		poly.transform(offset);
		ShapeRoi roi = new ShapeRoi(poly);
		roi.setStrokeWidth(this.strokeWidth);
		roi.setStrokeColor(this.strokeColor);
		this.add(roi);
	}

}
