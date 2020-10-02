package imagingbook.pub.corners.subpixel;

public interface PolynomialInterpolator2D {
	
	public float getInterpolatedValue(double x, double y);
	public float[] getMaxPosition();

	public default float getInterpolatedValue(float[] xy) {
		return getInterpolatedValue(xy[0], xy[1]);
	}
	
	public double[] getCoefficients();
	public double[] getGradient(double x, double y);
	public double[][] getHessian(double x, double y);


}
