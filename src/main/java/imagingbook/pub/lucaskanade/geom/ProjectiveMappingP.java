package imagingbook.pub.lucaskanade.geom;

import imagingbook.pub.geometry.mappings.linear.LinearMapping;
import imagingbook.pub.geometry.mappings.linear.ProjectiveMapping;

public class ProjectiveMappingP extends ProjectiveMapping implements TParameters {
	
	public ProjectiveMappingP(double[] p) {
		super(p[0] + 1, p[1], p[6], p[2], p[3] + 1, p[7], p[4],  p[5]);
	}
	
	public ProjectiveMappingP(ProjectiveMapping m) {
		super(m);
	}
	
	@Deprecated
	public static ProjectiveMappingP fromWarpParameters(double[] p) {
		return (ProjectiveMappingP) new ProjectiveMapping(
				p[0] + 1,   p[1],        p[6],
				p[2],       p[3] + 1,    p[7],
				p[4],       p[5]             );
	}
	
	public int getWarpParameterCount() {
//		p[0] = M3x3[0][0] - 1;	// = a
//		p[1] = M3x3[0][1];		// = b
//		p[2] = M3x3[1][0];		// = c
//		p[3] = M3x3[1][1] - 1;	// = d
//		p[4] = M3x3[2][0];		// = e
//		p[5] = M3x3[2][1];		// = f
//		p[6] = M3x3[0][2];		// = tx
//		p[7] = M3x3[1][2];		// = ty
		return 8;
	}
	
	@Override
	public double[] getWarpParameters() {
		return new double[] { a00 - 1, a01, a10, a11 - 1, a20, a21, a02, a12 };
	}
	
	@Override
	public double[][] getWarpJacobian(double[] xy) {
		// see Baker 2003 "20 Years" Part 1, Eq. 99 (p. 46)
		final double x = xy[0];
		final double y = xy[1];
		final double a = a00 * x + a01 * y + a02;	// = alpha
		final double b = a10 * x + a11 * y + a12;	// = beta
		final double c = a20 * x + a21 * y + 1;	// = gamma
		final double cc = c * c;
		// TODO: check c for zero-value and throw exception, make more efficient
		return new double[][]
			{{x/c, y/c, 0,   0,   -(x*a)/cc, -(y*a)/cc, 1/c, 0  },
			 {0,   0,   x/c, y/c, -(x*b)/cc, -(y*b)/cc, 0,   1/c}};
	}
	
	// -------------------------------------------------------------------
	
	public static void main(String[] args) {
		ProjectiveMapping m1 = new ProjectiveMapping();
		System.out.println("m1 = \n" + m1.toString());
		double[][] A1 = m1.getTransformationMatrix();
		
		ProjectiveMapping m2 = new ProjectiveMapping(new LinearMapping(A1));
		System.out.println("m2 = \n" + m2.toString());
	}

}
