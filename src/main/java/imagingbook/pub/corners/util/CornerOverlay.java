package imagingbook.pub.corners.util;

import java.awt.Color;

import ij.gui.PointRoi;
import ij.gui.Roi;
import imagingbook.lib.ij.CustomOverlay;
import imagingbook.pub.corners.Corner;

/**
 * A custom ImageJ overlay for drawing corners.
 * 
 * @author WB
 */
public class CornerOverlay extends CustomOverlay<Corner> {
	
	public static Color DefaultMarkerColor = Color.green;		// color of cross markers
	public static int DefaultMarkerType = 1;					// 0=hybrid, 1=cross, 2=dot, 3=circle (see PointRoi)
	public static int DefaultMarkerSize = 1;					// 0-6 (Tiny-XXXL, see PointRoi)
	
	public CornerOverlay() {
		strokeColor(DefaultMarkerColor);
	}
	
	@Override
	public Roi makeRoi(Corner c) {
		PointRoi roi = new PointRoi(c.getX(), c.getY());
		roi.setPointType(DefaultMarkerType);
		roi.setSize(DefaultMarkerSize);
		return roi;
	}
	
}
