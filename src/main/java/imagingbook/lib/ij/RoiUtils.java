package imagingbook.lib.ij;

import java.awt.Polygon;

import ij.gui.Roi;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.process.FloatPolygon;
import imagingbook.pub.geometry.basic.Pnt2d;

/**
 * This class defines static ROI-related utility methods
 * to interface with ImageJ.
 * 
 * @author WB
 *
 */
public class RoiUtils {
	
	/**
	 * Retrieves the outline of the specified ROI as an
	 * array of {@link Pnt2d} points with {@code int}
	 * coordinates. Note that unless the ROI is of type
	 * {@link PolygonRoi} or {@link PointRoi} only the corner points of the
	 * bounding box are returned.
	 * Interpolated contour points are returned for a instance of {@link OvalRoi}.
	 * 
	 * @param roi the ROI
	 * @return the ROI's polygon coordinates
	 */
	public static Pnt2d[] getPolygonPointsInts(Roi roi) {
		Polygon pgn = roi.getPolygon();
		Pnt2d[] pts = new Pnt2d[pgn.npoints];
		for (int i = 0; i < pgn.npoints; i++) {
			pts[i] = Pnt2d.PntInt.from(pgn.xpoints[i], pgn.ypoints[i]);
		}
		return pts;
	}
	
	/**
	 * Retrieves the outline of the specified ROI as an
	 * array of {@link Pnt2d} points with {@code int}
	 * coordinates. Note that unless the ROI is of type
	 * {@link PolygonRoi} or {@link PointRoi} only the corner points of the
	 * bounding box are returned.
	 * Interpolated contour points are returned for a instance of {@link OvalRoi}.
	 * 
	 * @param roi the ROI
	 * @return the ROI's polygon coordinates
	 */
	public static Pnt2d[] getPolygonPointsFloat(Roi roi) {
		FloatPolygon pgn = roi.getFloatPolygon();
		Pnt2d[] pts = new Pnt2d[pgn.npoints];
		for (int i = 0; i < pgn.npoints; i++) {
			pts[i] = Pnt2d.PntDouble.from(pgn.xpoints[i], pgn.ypoints[i]);
		}
		return pts;
	}
	
	/**
	 * Returns a new overlay produced by joining two existing
	 * overlays, which remain unchanged. All involved ROIs are cloned.
	 * 
	 * @param olyA the first overlay
	 * @param olyB the second overlay
	 * @return the new overlay
	 */
	public static Overlay join(Overlay olyA, Overlay olyB) {
		Overlay oly = olyA.duplicate();
		for (Roi roi : olyB) {
			oly.add((Roi)roi.clone());
		}
		return oly;
	}
	
}
