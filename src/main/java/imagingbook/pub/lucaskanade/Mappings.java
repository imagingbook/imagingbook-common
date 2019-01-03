package imagingbook.pub.lucaskanade;

/**
 * This class defines static helper methods to exchange parameters with
 * geometric mapping objects defined in package
 * {@link imagingbook.pub.geometry.mappings}.
 */
public abstract class Mappings {
	
	public abstract int getWarpParameterCount();	
	public abstract double[] getWarpParameters();
	public abstract double[][] getWarpJacobian(double[] x);

}
