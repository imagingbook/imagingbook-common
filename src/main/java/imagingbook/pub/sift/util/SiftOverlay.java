package imagingbook.pub.sift.util;

import java.awt.geom.Path2D;

import ij.gui.Roi;
import ij.gui.ShapeRoi;
import imagingbook.lib.ij.CustomOverlay;
import imagingbook.pub.sift.SiftDescriptor;

/**
 * Custom ImageJ overlay for drawing SIFT descriptors as
 * "M"-shaped markers.
 * @author WB
 * @version 2020/10/04
 */
public class SiftOverlay extends CustomOverlay<SiftDescriptor> {
	
	private final static double DisplayAngleOffset = -Math.PI / 2;
	private double featureScale = 1.0; // 1.5;
	
	public void setFeatureScale(double featureScale) {
		this.featureScale = featureScale;
	}
	
	@Override
	public Roi makeRoi(SiftDescriptor sd) {
		double x = sd.getX(); 
		double y = sd.getY();
		double scale = featureScale * sd.getScale();
		double orient = sd.getOrientation() + DisplayAngleOffset;
		double sin = Math.sin(orient);
		double cos = Math.cos(orient);
		Path2D poly = new Path2D.Double();	
		poly.moveTo(x + (sin - cos) * scale, y - (sin + cos) * scale);
		poly.lineTo(x + (sin + cos) * scale, y + (sin - cos) * scale);
		poly.lineTo(x, y);
		poly.lineTo(x - (sin - cos) * scale, y + (sin + cos) * scale);
		poly.lineTo(x - (sin + cos) * scale, y - (sin - cos) * scale);
		poly.closePath();
		return new ShapeRoi(poly);
	}

}
