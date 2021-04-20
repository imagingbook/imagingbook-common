package imagingbook.lib.math;

public interface Eigensolver {
	
	public boolean isReal();
	public double[] getEigenvalues();
	public double getEigenvalue(int k);
	public double[] getEigenvector(int k);

}
