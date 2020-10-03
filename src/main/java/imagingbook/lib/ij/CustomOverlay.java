package imagingbook.lib.ij;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.List;

import ij.gui.Overlay;
import ij.gui.Roi;

/**
 * Defines a subclass of ImageJ's {@link Overlay} class which can only 
 * accept elements of a specific type {@link T}.
 * This (abstract) class cannot be instantiated directly but
 * is supposed to be used as the superclass for specific
 * overlay types, e.g., for displaying corners, SIFT markers etc.
 * In the simplest case, the concrete subclass just needs to implement the method 
 * {@link #makeRoi(T)}, which is only concerned with geometry.
 * Stroke width and color are handled uniformly by this class ({@link CustomOverlay}).
 * By default, all coordinates are shifted by a <strong>half-pixel distance</strong> (0.5, 0.5) 
 * to display integer points at the associated pixel centers.
 * 
 * @author WB
 *
 * @param <T> type of elements allowed to be added to this overlay
 */
public abstract class CustomOverlay<T> extends Overlay {
	
	/**
	 * This method, which must be implemented by any subclass,
	 * defines how an instance of type {@link T} is 
	 * converted to an {@link Roi} object.
	 * 
	 * @param item an instance of type {@link T} to be added to the overlay
	 * @return the resulting {@link Roi} object
	 */
	public abstract Roi makeRoi(T item);
	
	public static Color DefaultStrokeColor = Color.green;
	public static double DefaultStrokeWidth = 0.25;
	
	private Color strokeColor = DefaultStrokeColor;
	private double strokeWidth = DefaultStrokeWidth;
	
	private final boolean adjustPixelOffset;
	
	// -----------------------------------------------------------
	
	/**
	 * Constructor, only to be invoked by a subclass constructor.
	 * @param adjustPixelOffset set true to activate half-pixel offset (default)
	 */
	public CustomOverlay(boolean adjustPixelOffset) {
		this.adjustPixelOffset = adjustPixelOffset;
	}
	
	/**
	 * Constructor, only to be invoked by a subclass constructor.
	 * Activates half-pixel offset by default.
	 */
	public CustomOverlay() {
		this(true);
	}
	
	// -----------------------------------------------------------
	
	/**
	 * Sets the stroke color for all items subsequently added to this overlay.
	 * The stroke color can be changed any time between insertions.
	 * Uncommonly named (like a directive) to avoid confusion with 
	 * the existing {@link Overlay#setStrokeColor(Color)} method.
	 * @param strokeColor the new stroke color to be used
	 */
	public void strokeColor(Color strokeColor) {
		this.strokeColor = strokeColor;
	}
	
	/**
	 * Sets the stroke width for all items subsequently added to this overlay.
	 * The stroke width can be changed any time between insertions.
	 * Uncommonly named (like a directive) to avoid confusion with 
	 * the existing {@link Overlay#setStrokeWidth(Double)} method. 
	 * @param strokeWidth
	 */
	public void strokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	
	// -----------------------------------------------------------
	
	/**
	 * Adds a single item of type {@link T} to this overlay.
	 * @param item an instance of type {@link T}
	 */
	public void add(T item) {
		Roi roi = makeRoi(item);
		if (adjustPixelOffset) {
			Rectangle2D r = roi.getFloatBounds();
			roi.setLocation(r.getX() + 0.5, r.getY() + 0.5);
		}
		roi.setStrokeWidth(this.strokeWidth);
		roi.setStrokeColor(this.strokeColor);
		this.add(roi);
	}
	
	/**
	 * Adds all items of type {@link T} in the supplied list to this overlay.
	 * @param items a list of instances of type {@link T}
	 */
	public void add(List<T> items) {
		for(T item : items) {
			this.add(item);
		}
	}
	
	//-------------------------------------------------------
	
//	public static void main(String[] args) {
//		
//		CustomOverlay<Corner> co = new CustomOverlay<>();
//		Corner c = new Corner(1,2,3);
//		co.add(c);
//	}
	

}
