package imagingbook.lib.ij.overlay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import ij.gui.Overlay;
import ij.gui.ShapeRoi;

/**
 * This is an adapter for ImageJ's {@link Overlay} class to ease the insertion of
 * AWT {@link Shape} elements.
 * Shapes are only geometric descriptions but have no stroke width, color
 * etc. attached. These are supplied by {@link ShapeOverlayAdapter}'s state
 * when insertion is done with {@link #addShape(Shape)}
 * or explicitly specified by calling {@link #addShape(Shape, ColoredStroke)}.
 * 
 * When a shape is inserted into the associated overlay it is converted to an ImageJ
 * {@link ShapeRoi} instance whose stroke/fill properties are set. 
 * 
 * See also {@link ColoredStroke}.
 * 
 * @author WB
 * @version 2021/10/26
 *
 */
public class ShapeOverlayAdapter {
	
	public static Color DefaultStrokeColor = Color.black;
	public static float DefaultStrokeWidth = 0.5f;
	
	private static final AffineTransform PixelOffsetTransform = 
						new AffineTransform(1, 0, 0, 1, 0.5, 0.5); // AWT transformation!
	
	private final Overlay overlay;
	private ColoredStroke stroke;
	private boolean pixelOffset = true;
	
	// ----------------------------------------------------------
	
	public ShapeOverlayAdapter(Overlay oly, ColoredStroke stroke) {
		this.overlay = oly;
		this.stroke = stroke;
	}
	public ShapeOverlayAdapter(Overlay oly) {
		this(oly, makeDefaultStroke());
	}
	
	// ----------------------------------------------------------
	
	public ColoredStroke getStroke() {
		return stroke;
	}
	public void setStroke(ColoredStroke stroke) {
		this.stroke = stroke;
	}
	
	public void setPixelOffset(boolean pixelOffset) {
		this.pixelOffset = pixelOffset;
	}
	
	public Overlay getOverlay() {
		return this.overlay;
	}
	
	// ----------------------------------------------------------
	
	protected ShapeRoi shapeToRoi(Shape s, ColoredStroke stroke) {
		s = (pixelOffset) ? PixelOffsetTransform.createTransformedShape(s) : s;
		ShapeRoi roi = new ShapeRoi(s);
		BasicStroke bs = stroke.getBasicStroke();
		roi.setStrokeWidth(bs.getLineWidth());
		roi.setStrokeColor(stroke.getStrokeColor());
		roi.setFillColor(stroke.getFillColor());
		roi.setStroke(bs);
		return roi;
	}
	
	public void addShape(Shape s) {
		overlay.add(shapeToRoi(s, this.stroke));
	}
	
	public void addShape(Shape s, ColoredStroke stroke) {
		overlay.add(shapeToRoi(s, stroke));
	}

	// ----------------------------------------------------------
	
	private static ColoredStroke makeDefaultStroke() {
		ColoredStroke stroke = new ColoredStroke();
		stroke.setLineWidth(DefaultStrokeWidth);
		stroke.setStrokeColor(DefaultStrokeColor);
		return stroke;
	}

}
