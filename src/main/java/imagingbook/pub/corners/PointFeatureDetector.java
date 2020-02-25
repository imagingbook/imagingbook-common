package imagingbook.pub.corners;

import java.util.List;

import imagingbook.pub.geometry.basic.Point;

public interface PointFeatureDetector {
	
	List<? extends Point> getPoints();

}
