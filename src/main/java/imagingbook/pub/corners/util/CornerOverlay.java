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
	
	private static int MarkerType = 1;					// 0=hybrid, 1=cross, 2=dot, 3=circle (see PointRoi)
	private static int MarkerSize = 1;					// 0-6 (Tiny-XXXL, see PointRoi)
	private static Color MarkerColor = Color.green;		// color of cross markers
	
	@Override
	public Roi makeRoi(Corner c) {
		PointRoi roi = new PointRoi(c.getX(), c.getY());
		roi.setPointType(MarkerType);
		roi.setStrokeColor(MarkerColor);
		roi.setSize(MarkerSize);
		return roi;
	}
	
}
