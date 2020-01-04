package imagingbook.pub.geometry.delaunay.plugin;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjLogStream;
import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.geometry.basic.Triangle;
import imagingbook.pub.geometry.delaunay.DelaunayTriangulation;
import imagingbook.pub.geometry.delaunay.chew.TriangulationChew;
import imagingbook.pub.geometry.delaunay.guibas.TriangulationGuibas;


/**
 * <p>This ImageJ plugin demonstrates the calculation of of the 2D Delaunay triangulation for
 * the given set of points.
 * </p>
 * <p>
 * How to use: Create or open a binary image containing a point set. Any pixel with value &gt; 0 
 * is considered a foreground point, zero-value pixels are considered background). The plugin 
 * triangulates the foreground point set and displays the triangulation as a vector overlay
 * in a new image.
 * </p>
 * <p>
 * The following triangulation methods can be selected (both should yield similar results):
 * </p>
 * <ol>
 * <li><strong>Chew:</strong> Incremental triangulation algorithm proposed by Paul Chew.</li>
 * <li><strong>Guibas:</strong> Randomized incremental algorithm proposed by Guibas et al. (faster).</li>
 * </ol>
 * <p>
 * Note that the actual implementations reside in separate sub-packages and do not depend on ImageJ.
 * </p>
 * 
 * @author W. Burger
 * @version 2020-01-01
 */
public class Delaunay_Demo_Plugin implements PlugInFilter {

	private static final String title = Delaunay_Demo_Plugin.class.getSimpleName();
	private static final String[] TriangulationMethods = {"Chew", "Guibas"};
	
	private static int theMethod = 1;	// Guibas is 10 x faster
	
	private static Color DelaunayColor = Color.green;
	private static Color PointColor = Color.magenta;
	private static float StrokeWidth = 0.1f;
	private static double PointRadius = 1.5;

	static {
		IjLogStream.redirectSystem();
	}

	public int setup(String arg, ImagePlus im) {
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		if (!runDialog())
			return;

		List<Point> points = collectPoints(ip);
		
		DelaunayTriangulation dt = null;
		switch (theMethod) {
		case 0: dt = new TriangulationChew(points); break;		// Chew
		case 1: dt = new TriangulationGuibas(points); break;	// Guibas
		}

		ImageProcessor cp = ip.convertToByteProcessor();
		Overlay oly = makeOverlay(dt);

		ImagePlus im = new ImagePlus(title, cp);
		im.setOverlay(oly);
		im.show();
	}

	// ---------------------------------------------------------------------------

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addChoice("Select triangulation method", 
				TriangulationMethods, TriangulationMethods[theMethod]);
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		theMethod = gd.getNextChoiceIndex();
		return true;
	}
	
	// ---------------------------------------------------------------------------
	
	private List<Point> collectPoints(ImageProcessor ip) {
		List<Point> vertices = new ArrayList<>();
		int M = ip.getWidth();
		int N = ip.getHeight();
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				float val = ip.getPixelValue(u, v);
				if (val > 0) {
					vertices.add(new Point.Imp(u, v));
				}
			}
		}
		return vertices;
	}
	
	// ---------------------------------------------------------------------------
	
	private Overlay makeOverlay(DelaunayTriangulation dt) {
		Collection<Triangle> triangles = dt.getTriangles();
		Collection<Point> allPoints = dt.getPoints();
		Overlay oly = new Overlay();

		double r = PointRadius;
		for (Point p : allPoints) {
			double x = p.getX();
			double y = p.getY();
			Roi roi = new ShapeRoi(new Rectangle2D.Double(x - r, y - r, 2 * r, 2 * r));
			roi.setStrokeColor(PointColor);
			roi.setStrokeWidth(StrokeWidth);
			oly.add(roi);
		}

		Path2D path = new Path2D.Double();
		for (Triangle trgl : triangles) {
			Point[] pts = trgl.getPoints();
			Point a = pts[0];
			Point b = pts[1];
			Point c = pts[2];
			path.moveTo(a.getX(), a.getY());
			path.lineTo(b.getX(), b.getY());
			path.lineTo(c.getX(), c.getY());
			path.lineTo(a.getX(), a.getY());
		}
		Roi roi = new ShapeRoi(path);
		roi.setStrokeColor(DelaunayColor);
		roi.setStrokeWidth(StrokeWidth);
		oly.add(roi);
		
		oly.translate(0.5, 0.5);
		return oly;
	}
}
