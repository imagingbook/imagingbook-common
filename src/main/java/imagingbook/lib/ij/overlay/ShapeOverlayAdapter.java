package imagingbook.lib.ij.overlay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import ij.gui.Overlay;
import ij.gui.ShapeRoi;
import ij.gui.TextRoi;

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
	
	public static Color DefaultTextColor = Color.black;
	public static float DefaultStrokeWidth = 0.5f;
	public static Font DefaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
	
	
	
	private static final AffineTransform PixelOffsetTransform = 
						new AffineTransform(1, 0, 0, 1, 0.5, 0.5); // AWT transformation!
	
	private final Overlay overlay;
	private ColoredStroke stroke;
	private Color textColor = DefaultTextColor;
	private Font font = DefaultFont;
	
	private boolean pixelOffset = true;
	
	// ----------------------------------------------------------
	
	public ShapeOverlayAdapter(Overlay oly, ColoredStroke stroke) {
		this.overlay = oly;
		this.stroke = stroke;
	}
	
	public ShapeOverlayAdapter(Overlay oly) {
		this(oly, makeDefaultStroke());
	}
	
	public ShapeOverlayAdapter() {
		this(new Overlay());
	}
	
	// ----------------------------------------------------------
	
	public ColoredStroke getStroke() {
		return stroke;
	}
	
	public void setStroke(ColoredStroke stroke) {
		this.stroke = stroke;
	}
	
	public void setTextColor(Color color) {
		this.textColor = color;
	}
	
	public void setFont(Font font) {
		this.font = font;
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
	
	// TODO: check font/color settings
	public void addText(double x, double y, String text) {
		TextRoi troi = new TextRoi(x, y, text, font);
		troi.setStrokeColor(textColor);
		overlay.add(troi);
	}

	// ----------------------------------------------------------
	
	private static ColoredStroke makeDefaultStroke() {
		ColoredStroke stroke = new ColoredStroke();
		stroke.setLineWidth(DefaultStrokeWidth);
		stroke.setStrokeColor(DefaultTextColor);
		return stroke;
	}

}
