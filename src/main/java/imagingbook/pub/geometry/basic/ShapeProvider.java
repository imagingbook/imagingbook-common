package imagingbook.pub.geometry.basic;

import java.awt.Shape;

/**
 * Implementing classes know how to create an AWT {@link Shape}.
 * @author WB
 *
 */
public interface ShapeProvider {
	
	/**
	 * Returns a scaled {@link Shape} for this object
	 * (default scale is 1). 
	 * Must be defined by implementing classes.
	 * 
	 * @param scale the scale of the shape
	 * @return a {@link Shape} instance
	 */
	public Shape getShape(double scale);
	
	/**
	 * Returns a {@link Shape} for this object at the
	 * default scale (1).
	 * @return a {@link Shape} instance
	 */
	public default Shape getShape() {
		return getShape(1);
	};
	

}
