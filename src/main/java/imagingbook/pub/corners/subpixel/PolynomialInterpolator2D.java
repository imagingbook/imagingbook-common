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
	
	// ----------------------------------
	
	public enum SubpixelMethod {
		None {
			@Override
			public PolynomialInterpolator2D getInterpolator(float[] s) {
				return null;}
		},
		Parabolic {
			@Override
			public ParabolicInterpolator2D getInterpolator(float[] s) {
				return new ParabolicInterpolator2D(s);}
		}, 
		Quartic {
			@Override
			public QuarticInterpolator2D getInterpolator(float[] s) {
				return new QuarticInterpolator2D(s);}
		},  
		Taylor {
			@Override
			public TaylorInterpolator2D getInterpolator(float[] s) {
				return new TaylorInterpolator2D(s);}
		};

		public abstract PolynomialInterpolator2D getInterpolator(float[] s);
	}
	

//	public static PolynomialInterpolator2D getSubpixelInterpolator(SubpixelMethod method, float[] s) {
//		switch (method) {
//		case None:
//			return null;
//		case Parabolic:
//			return new ParabolicInterpolator2D(s);
//		case Quartic:
//			return new QuarticInterpolator2D(s);
//		case Taylor:
//			return new TaylorInterpolator2D(s);
//		default:
//			return null;	// TODO: throw exception?
//		}
//	}


}
