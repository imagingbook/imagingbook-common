package imagingbook.lib.math;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;

import imagingbook.lib.math.Arithmetic.DivideByZeroException;
import imagingbook.lib.math.Matrix.IncompatibleDimensionsException;
import imagingbook.lib.math.Matrix.NonsquareMatrixException;

public class MatrixTest {
	
	static float TOLERANCE = 1E-6f;
	
//	static void assertArrayEquals(double[][] X, double[][] Y, double tol) {
//		for (int i = 0; i )
//	}
	
	static float[][] A = {
			{ -1, 2, 3 }, 
			{  4, 5, 6 }, 
			{  7, 8, 9 }};
	
	static double[][] B = {
			{ -1, 2, 3 }, 
			{ 4, 5, 6 }};

	@Test
	public void testMatrixRowsAndColumns() {
		assertEquals(3, Matrix.getNumberOfRows(A));
		assertEquals(3, Matrix.getNumberOfColumns(A));
		assertEquals(6.0, A[1][2], 1E-6);
		
		assertEquals(2, Matrix.getNumberOfRows(B));
		assertEquals(3, Matrix.getNumberOfColumns(B));
		assertEquals(2.0, A[0][1], TOLERANCE);
	}
	
	@Test
	public void testMatrixDeterminant() {
		assertEquals(6.0, Matrix.determinant3x3(A), TOLERANCE);
	}
	
	@Test(expected = IncompatibleDimensionsException.class)
	public void testMatrixDeterminantFail() {
		Matrix.determinant3x3(B);
	}

	@Test
	public void testMatrixRowColumnSums() {
		assertArrayEquals(new float[] {10, 15, 18}, Matrix.sumColumns(A), TOLERANCE);
		assertArrayEquals(new float[] {4, 15, 24}, Matrix.sumRows(A), TOLERANCE);
		
		assertArrayEquals(new double[] {3, 7, 9}, Matrix.sumColumns(B), TOLERANCE);
		assertArrayEquals(new double[] {4, 15}, Matrix.sumRows(B), TOLERANCE);
	}
	
	@Test
	public void testMatrixInverse() {
		float[][] Ai ={{-0.500f, 1.000f, -0.500f}, 
				{1.000f, -5.000f, 3.000f}, 
				{-0.500f, 3.666666667f, -2.1666666667f}};	
		assertArrayEquals(Ai, Matrix.inverse(A));
	}
	
	@Test(expected = NonsquareMatrixException.class)
	public void testMatrixInverseNFail() {
		Matrix.inverse(B);
	}
	
	@Test
	public void testMatrixRealMatrix() {
		RealMatrix Br = MatrixUtils.createRealMatrix(B);
		assertArrayEquals(B, Br.getData());
	}
	
	// --------------------------------------------------------------------

	@Test
	public void testMatrixJoin() {
		float[] v1 = {1,2,3};
		float[] v2 = {4,5,6,7};
		float[] v3 = {};
		float[] v4 = {8};		
		assertArrayEquals(new float[] {1, 2, 3, 4, 5, 6, 7, 8}, Matrix.join(v1, v2, v3, v4), TOLERANCE);
	}
	
	@Test
	public void testMatrixMinMax() {
		float[] x = {-20,30,60,-40, 0};
		double[] y = {-20,30,60,-40, 0};
		assertEquals(Matrix.min(x), -40, 1E-6);
		assertEquals(Matrix.min(y), -40, 1E-6);
		assertEquals(Matrix.max(x), 60, 1E-6);
		assertEquals(Matrix.max(y), 60, 1E-6);
	}
	
	// --------------------------------------------------------------------
	
	@Test
	public void testMatrixHomogeneous() {
		double[] x = {2, -7, 3};
		double[] xh = {2, -7, 3, 1};
		assertArrayEquals(xh, Matrix.toHomogeneous(x), TOLERANCE);
		assertArrayEquals(x, Matrix.toCartesian(xh), TOLERANCE);
		assertArrayEquals(x, Matrix.toCartesian(Matrix.multiply(-5, xh)), TOLERANCE);
	}
	
	@Test (expected = DivideByZeroException.class)
	public void testMatrixHomogeneousFail() {
		double[] xh = {2, -7, 3, 0};
		Matrix.toCartesian(xh);
	}
}
