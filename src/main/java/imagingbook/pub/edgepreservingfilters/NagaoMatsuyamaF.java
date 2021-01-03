package imagingbook.pub.edgepreservingfilters;

import imagingbook.lib.image.access.OutOfBoundsStrategy;

public abstract class NagaoMatsuyamaF {

	public static class Parameters {
		/** Variance threshold */
		public double varThreshold = 0.0;	// 0,...,10
		/** Out-of-bounds strategy */
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NEAREST_BORDER;
	}
	
	protected static final int[][] R0 =
	{{-1,-1}, {0,-1}, {1,-1},
	 {-1, 0}, {0, 0}, {1, 0},
	 {-1, 1}, {0, 1}, {1, 1}};
	
	// -------------------
	
	private static final int[][] R1 =
	{{-2,-1}, {-1,-1},
	 {-2, 0}, {-1, 0}, {0, 0},
	 {-2, 1}, {-1, 1}};
	
	private static final int[][] R2 =
	{{-2,-2}, {-1,-2},
	 {-2,-1}, {-1,-1}, {0,-1},
	          {-1, 0}, {0, 0}};
	
	private static final int[][] R3 =
	{{-1,-2}, {0,-2}, {1,-2}, 
	 {-1,-1}, {0,-1}, {1,-1},
	          {0, 0}};

	private static final int[][] R4 =
	{        {1,-2}, {2,-2},
	 {0,-1}, {1,-1}, {2,-1},
	 {0, 0}, {1, 0}};
	
	private static final int[][] R5 =
	{        {1,-1}, {2,-1},
	 {0, 0}, {1, 0}, {2, 0},
	         {1, 1}, {2, 1}};
	
	private static final int[][] R6 =
	{{0,0}, {1,0},
	 {0,1}, {1,1}, {2,1},
	        {1,2}, {2,2}};

	private static final int[][] R7 =
	{        {0,0},
	 {-1,1}, {0,1}, {1,1},
	 {-1,2}, {0,2}, {1,2}};
	
	private static final int[][] R8 =
	{        {-1,0}, {0,0},
	 {-2,1}, {-1,1}, {0,1},
	 {-2,2}, {-1,2}};
	
	// -------------------
	
	protected static final int[][][] SubRegions = {R1, R2, R3, R4, R5, R6, R7, R8};

}
