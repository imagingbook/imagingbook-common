package imagingbook.lib.filtersOBSOLETE;

import imagingbook.lib.filter.kernel.Kernel2D;

public interface HasKernel2D {
	
	/**
	 * Returns the effective 2D filter kernel.
	 * 
	 * @return the 2D filter kernel
	 */
	public Kernel2D getKernel();

}
