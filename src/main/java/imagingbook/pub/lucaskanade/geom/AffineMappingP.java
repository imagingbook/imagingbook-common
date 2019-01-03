package imagingbook.pub.lucaskanade.geom;

import imagingbook.pub.geometry.mappings.linear.AffineMapping;

public class AffineMappingP extends AffineMapping implements TParameters {
	
	public AffineMappingP(double[] p) {
		super(p[0] + 1, p[1], p[2], p[3] + 1, p[4], p[5]);
	}

	@Deprecated
	public static AffineMappingP fromWarpParameters(double[] p) {
		return (AffineMappingP) new AffineMapping(p[0] + 1, p[1], p[2], p[3] + 1, p[4], p[5]);
	}
	
	@Override
	public int getWarpParameterCount() {
		// a00 = p[0] + 1; a01 = p[1]; a10 = p[2]; a11 = p[3] + 1; a02 = p[4]; a12 = p[5];
		return 6;
	}

	@Override
	public double[] getWarpParameters() {
		return new double[] { a00 - 1, a01, a10, a11 - 1, a02, a12 };
	}

	@Override
	public double[][] getWarpJacobian(double[] xy) {
		final double x = xy[0];
		final double y = xy[1];
		return new double[][]
			{{x, y, 0, 0, 1, 0},
			 {0, 0, x, y, 0, 1}};
	}

}
