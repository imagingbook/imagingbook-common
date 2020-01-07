package imagingbook.lib.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

public class ImageGraphics implements AutoCloseable {
	
	private static BasicStroke DEFAULT_STROKE = new BasicStroke();
	private static Color DEFAULT_COLOR = Color.white;
	private static boolean DEFAULT_ANTIALIASING = true;
	
	private ImageProcessor ip;
	private BufferedImage bi;
	private final Graphics2D g;
	
	private BasicStroke stroke = DEFAULT_STROKE;
	private Color color = DEFAULT_COLOR;
	
	// -------------------------------------------------------------
	
	public ImageGraphics(ImageProcessor ip) {
		this(ip, null, null);
	}
	
	public ImageGraphics(ImageProcessor ip, Color color, BasicStroke stroke) {
		this.ip = ip;
		this.bi = toBufferedImage(ip);
		
		if (color != null) this.color = color;
		if (stroke != null) this.stroke = stroke;
		
		this.g = (Graphics2D) bi.getGraphics();
		this.g.setColor(color);
		this.g.setColor(this.color);
		this.g.setStroke(this.stroke);
		this.setAntialiasing(DEFAULT_ANTIALIASING);
	}
	
	public void setAntialiasing(boolean on) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, on ? 
				RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, on ?
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
	
	public Graphics2D getGraphics() {
		return this.g;
	}
	
	public void update() {
		copyImageToProcessor(bi, ip);
	}

	@Override
	public void close() {
		update();
		ip = null;
		bi = null;
	}
	
	// -----------------------------------------------------------
	
	/**
	 * Needed, since IJ's conversion methods are not named consistently.
	 * @param ip
	 * @return
	 */
	private BufferedImage toBufferedImage(ImageProcessor ip) {
		if (ip instanceof ByteProcessor) {
			return ((ByteProcessor) ip).getBufferedImage(); 
		}
		else if (ip instanceof ShortProcessor) {
			return ((ShortProcessor) ip).get16BitBufferedImage(); 
		}
		else if (ip instanceof ColorProcessor) {
			return ((ColorProcessor) ip).getBufferedImage();
		}
		else {
			throw new IllegalArgumentException("Cannot create BufferedImage from " +
					ip.getClass().getName());
		}
	}
	
	/**
	 * Copies the contents of the {@link BufferedImage} to the specified 
	 * {@link ImageProcessor}. The size and type of the BufferedImage
	 * is assumed to match the ImageProcessor.
	 * 
	 * @param bi
	 * @param ip
	 */
	private void copyImageToProcessor(BufferedImage bi, ImageProcessor ip) {
		ImageProcessor ip2 = null;
		if (ip instanceof ByteProcessor) {
			ip2 = new ByteProcessor(bi); 
		}
		else if (ip instanceof ShortProcessor) {
			ip2 = new ShortProcessor(bi); 
		}
		else if (ip instanceof ColorProcessor) {
			ip2 = new ColorProcessor(bi); 
		}
		else {
			throw new IllegalArgumentException("Cannot create BufferedImage from " +
					ip.getClass().getName());
		}
		ip.copyBits(ip2, 0, 0, Blitter.COPY);
	}
	
	// -----------------------------------------------------------
	//  Convenience methods for drawing selected shapes with double coordinates
	// -----------------------------------------------------------
	
	public void drawLine(double x1, double y1, double x2, double y2) {
		g.draw(new Line2D.Double(x1, y1, x2, y2));
	}
	
	public void drawOval(double x, double y, double w, double h) {
		g.draw(new Ellipse2D.Double(x, y, w, h));
	}
	
	public void drawRectangle(double x, double y, double w, double h) {
		g.draw(new Rectangle2D.Double(x, y, w, h));
	}
	
	public void drawPolygon(Point2D ... points) {
		Path2D.Double p = new Path2D.Double();
		p.moveTo(points[0].getX(), points[0].getY());
		for (int i = 1; i < points.length; i++) {
			p.lineTo(points[i].getX(), points[i].getY());
		}
		p.closePath();
		g.draw(p);
	}
	
	// stroke-related methods -------------------------------------
	
	public void setColor(Color color) {
		this.color = color;
		g.setColor(color);
	}
	
	public void setColor(int gray) {
		if (gray < 0) gray = 0;
		if (gray > 255) gray = 255;
		this.setColor(new Color(gray, gray, gray));
	}
	
	public void setLineWidth(double width) {
		this.stroke = new BasicStroke((float)width, stroke.getEndCap(), stroke.getLineJoin());
		g.setStroke(this.stroke);
	}
	
	public void setStroke(BasicStroke stroke) {
		this.stroke = stroke;
		g.setStroke(this.stroke);
	}
	
	// ---------------------
	
	public void setEndCapButt() {
		this.setEndCap(BasicStroke.CAP_BUTT);
	}
	
	public void setEndCapRound() {
		this.setEndCap(BasicStroke.CAP_ROUND);
	}
	
	public void setEndCapSquare() {
		this.setEndCap(BasicStroke.CAP_SQUARE);
	}
	
	private void setEndCap(int cap) {
		setStroke(new BasicStroke(stroke.getLineWidth(), cap, stroke.getLineJoin()));
	}
	
	// ---------------------
	
	public void setLineJoinBevel() {
		setLineJoin(BasicStroke.JOIN_BEVEL);
	}
	
	public void setLineJoinMiter() {
		setLineJoin(BasicStroke.JOIN_MITER);
	}
	
	public void setLineJoinRound() {
		setLineJoin(BasicStroke.JOIN_ROUND);
	}
	
	private void setLineJoin(int join) {
		setStroke(new BasicStroke(stroke.getLineWidth(), stroke.getEndCap(), join));
	}
}
