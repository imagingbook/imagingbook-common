package imagingbook.pub.geometry.delaunay.plugin;


import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.geometry.delaunay.Triangle;
import imagingbook.pub.geometry.delaunay.chew.Pnt;
import imagingbook.pub.geometry.delaunay.chew.Triangle2D;
import imagingbook.pub.geometry.delaunay.chew.TriangulationChew;




/**
 * TODO: UNFINISHED!!
 * This ImageJ plugin demonstrates the calculation of of the 2D Voronoi diagram for
 * a given set of points.
 * 
 * Requires a binary (grayscale) image with points valued > 0.
 * 
 * This plugin is based on applet code by Paul Chew (http://www.cs.cornell.edu/home/chew/Delaunay.html)
 * that is contained in the associated 'delaunay' package.
 * Usage: Create or open an grayscale image containing a point set (any pixel with value > 0 
 * is considered a foreground point, zero pixels are considered background). The plugin 
 * triangulates this point set and displays the triangulation as a new color image.
 * 
 * @author W. Burger
 * @version 2020/03/09
 */
public class Voronoi_Demo implements PlugInFilter {
	
	static String title = "Voronoi diagram";
	private static Color VoronoiColor = Color.blue;
	private static Color PointColor = Color.magenta;
	
	public int setup(String arg, ImagePlus im) {
		return DOES_8G + NO_CHANGES;
	}
	
	public void run(ImageProcessor ip) {
//		int width = ip.getWidth();
//		int height = ip.getHeight();
		
		List<Point> points = collectPoints(ip);
		IJ.log("Found " + points.size() + " image points.");
		
		TriangulationChew dt = new TriangulationChew(points);
				
		ImageProcessor cp = ip.convertToColorProcessor();
		
		// draw the Delaunay triangulation
		cp.setColor(VoronoiColor);
		drawVoronoiDiagram(dt, cp);

        // draw the original point set
		cp.setColor(PointColor);
		for (Point pnt : points) {
			draw(pnt, cp);
		}
		
		(new ImagePlus(title, cp)).show();
	}
	
	// ----------------------------------------------------------

	// TODO: NEEDS CLEANUP!
	private void drawVoronoiDiagram (TriangulationChew dt, ImageProcessor ip) {
    	// Keep track of sites done; no drawing for initial triangles sites
    	Point[] initPoints = dt.getSuperTriangle().getPoints();
    	Set<Point> processed = new HashSet<>(Arrays.asList(initPoints));
    	for (Triangle triangle : dt.getTriangles()) {
    		Triangle2D tr2D = toTriangle2D(triangle);
    		for (Point p : triangle.getPoints()) {
    			if (!processed.contains(p)) {
	    			processed.add(p);
	    			List<Triangle2D> triangles = dt.surroundingTriangles(toPnt(p), tr2D); //surroundingTriangles(pnt, triangle);
	    			List<Pnt> vertices = new ArrayList<>(triangles.size());
	    			for (Triangle2D tri : triangles) {
	    				Pnt center = tri.getCircumcenter();
	    				vertices.add(center);
	    			}
	    			draw(vertices.toArray(new Point[0]), ip);
    			}
    		}
    	}
    }
	
	// ---------------- REPLACE THESE --------------------
	
	Pnt toPnt(Point p) {
		return new Pnt(p.getX(), p.getX());
	}
	
	Triangle2D toTriangle2D(Triangle t) {
		return new Triangle2D(t.getPoints());
	}
	
	// ----------------------------------------------------------
	
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
	
	private void draw(Point[] vertices, ImageProcessor ip) {
        int[] x = new int[vertices.length];
        int[] y = new int[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            x[i] = (int) Math.round(vertices[i].getX());
            y[i] = (int) Math.round(vertices[i].getY());
        }
        Polygon poly = new Polygon(x, y, vertices.length);
        ip.drawPolygon(poly);
    }
	
	private void draw(Point p, ImageProcessor ip) {
		int x = (int) Math.round(p.getX());
		int y = (int) Math.round(p.getY());
		ip.drawRect(x - 1, y - 1, 3, 3);
	}
	
}
