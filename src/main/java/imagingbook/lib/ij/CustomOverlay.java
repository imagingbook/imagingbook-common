package imagingbook.lib.ij;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.List;

import ij.IJ;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;

/**
 * Defines a subclass of ImageJ's {@link Overlay} class
 * that accepts elements of a specific type {@link T}.
 * This (abstract) class cannot be instantiated directly but
 * is supposed to be used as the superclass for specific
 * overlay types, e.g., for displaying corners, SIFT markers etc.
 * (e.g., see {@link imagingbook.pub.sift.util.SiftOverlay}).
 * In the simplest case, the concrete subclass just needs to implement the method 
 * {@link #makeRoi(T)}, which is only concerned with geometry.
 * Stroke width and color are handled uniformly by this class ({@link CustomOverlay}).
 * The coordinates of added {@link ShapeRoi} instances are automatically 
 * shifted by a <strong>half-pixel distance</strong> (0.5, 0.5) 
 * to display integer points at the associated pixel centers.
 * <br>
 * Note: ImageJ draws all ROI types except {@link ShapeRoi} with a half-pixel offset (bug?).
 * This class redefines the methods {@link Overlay#add(Roi)} 
 * to perform the half-pixel shift.
 * 
 * @author WB
 * @version 2020/10/04
 *
 * @param <T> type of elements allowed to be added to this overlay
 */
public abstract class CustomOverlay<T> extends Overlay {
	
	public static Color DefaultStrokeColor = Color.gray;
	public static double DefaultStrokeWidth = 0.25;
	
	private Color strokeColor = DefaultStrokeColor;
	private double strokeWidth = DefaultStrokeWidth;
	
	// -----------------------------------------------------------
	
	/**
	 * This method, which must be implemented by any subclass,
	 * defines how an instance of type {@link T} is 
	 * converted to an {@link Roi} object.
	 * @param item an instance of type {@link T} to be added to the overlay
	 * @return the resulting {@link Roi} object
	 */
	public abstract Roi makeRoi(T item);
	
	// -----------------------------------------------------------
	
	@Override  // deactivated method:
	public void setStrokeColor(Color strokeColor) {
		IJ.log(CustomOverlay.class.getSimpleName() + ": setStrokeColor() has no effect, use strokeColor()!");
	}
	
	@Override  // deactivated method:
	public void setStrokeWidth(Double strokeWidth) {
		IJ.log(CustomOverlay.class.getSimpleName() + ": setStrokeWidth() has no effect, use strokeWidth()!");
	}
	
	// -----------------------------------------------------------
	
	/**
	 * Sets the stroke color for all items subsequently added to this overlay.
	 * The stroke color can be changed any time between insertions.
	 * Uncommonly named (like a directive) to avoid confusion with 
	 * the existing {@link Overlay#setStrokeColor(Color)} method,
	 * which modifies all elements currently contained in the overlay.
	 * @param strokeColor the new stroke color to be used
	 */
	public void strokeColor(Color strokeColor) {
		this.strokeColor = strokeColor;
	}
	
	/**
	 * Returns the overlay's current stroke color.
	 * Useful for modifying the value temporarily and then reinstating the
	 * previous value.
	 * @return as described
	 */
	public Color getStrokeColor() {
		return this.strokeColor;
	}
	

	
	/**
	 * Sets the stroke width for all items subsequently added to this overlay.
	 * The stroke width can be changed any time between insertions.
	 * Uncommonly named (like a directive) to avoid confusion with 
	 * the existing {@link Overlay#setStrokeWidth(Double)} method,
	 * which modifies all elements currently contained in the overlay.
	 * @param strokeWidth the new stroke width to be used
	 */
	public void strokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	
	/**
	 * Returns the overlay's current stroke width.
	 * Useful for modifying the value temporarily and then reinstating the
	 * previous value.
	 * @return as described
	 */
	public double getStrokeWidth() {
		return this.strokeWidth;
	}
	
	// -----------------------------------------------------------

	@Override
	public void add(Roi roi) {
		roi.setDrawOffset(true);
		// add half-pixel shift only to ShapeRoi instances (other ROI types are displayed with offset)
		if (roi instanceof ShapeRoi) {
			Rectangle2D r = roi.getFloatBounds();
			roi.setLocation(r.getX() + 0.5, r.getY() + 0.5);
		}
		super.add(roi);
    }
	
	// -----------------------------------------------------------
	
	/**
	 * Adds a single item of type {@link T} to this overlay.
	 * @param item an instance of type {@link T}
	 */
	public void addItem(T item) {
		Roi roi = makeRoi(item);
		roi.setStrokeWidth(this.strokeWidth);
		roi.setStrokeColor(this.strokeColor);
		this.add(roi);
	}
	
	/**
	 * Convenience method.
	 * Adds all items of type {@link T} in the supplied list to this overlay,
	 * using the same graphics (color, stroke width) settings.
	 * @param items a list of instances of type {@link T}
	 */
	public void addItems(List<T> items) {
		for(T item : items) {
			this.addItem(item);
		}
	}
	
	// -----------------------------------------------------------
	
	public void addToOverlay(Overlay otherOverlay) {
		Roi[] rois = this.toArray();
		for (Roi r : rois) {
			otherOverlay.add(r);
		}
	}

}
