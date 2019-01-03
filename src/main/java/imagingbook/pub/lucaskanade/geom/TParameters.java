package imagingbook.pub.lucaskanade.geom;

/**
 * This interfaces prescribes methods for the parameterization of mappings
 * with 1D double vectors (used in Lucas-Kanade matcher etc.).
 */
public interface TParameters {

	public int getWarpParameterCount();	
	public double[] getWarpParameters();
	public double[][] getWarpJacobian(double[] x);
	
}
