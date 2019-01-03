package imagingbook.pub.lucaskanade.geom;

import imagingbook.pub.geometry.mappings.linear.Translation;

public class TranslationP extends Translation implements TParameters {
	
	public TranslationP(double[] p) {
		super(p[0], p[1]);
	}
	
	@Deprecated
	public static TranslationP fromWarpParameters(double[] p) {
		return (TranslationP) new Translation(p[0], p[1]);
	}
	
	@Override
	public int getWarpParameterCount() {
		return 2;
	}
	
	@Override
	public double[] getWarpParameters() {
		double[] p = new double[] {a02,	a12};
		return p;
	}
	
	private static final double[][] JT =	// this transformation has a constant Jacobian
		{{1, 0},
		 {0, 1}};
	
	@Override
	public double[][] getWarpJacobian(double[] X) {
		return JT;
	}


}
