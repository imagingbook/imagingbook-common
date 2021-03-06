package imagingbook.lib.image;

import ij.process.ImageProcessor;
import imagingbook.lib.image.access.ImageAccessor;
import imagingbook.lib.interpolation.InterpolationMethod;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.basic.Pnt2d.PntInt;
import imagingbook.pub.geometry.mappings.Mapping2D;

/**
 * This class defines methods to perform arbitrary geometric transformations
 * on images. The geometric transformation (mapping) must be specified at
 * construction, optionally a pixel interpolation method can be specified.
 * 
 * @version 2019/01/04
 *
 */
public class ImageMapper {
	
	/** Default pixel interpolation method (see {@link InterpolationMethod}). */
	public static InterpolationMethod DefaultInterpolationMethod = InterpolationMethod.Bicubic;	
	
	private final InterpolationMethod im;
	private final Mapping2D mp;
	
	/**
	 * Creates a new instance with the specified geometric mapping.
	 * The default pixel interpolation method is used
	 * (see {@link DefaultInterpolationMethod}).
	 * @param mp the geometric mapping
	 */
	public ImageMapper(Mapping2D mp) {
		this(mp, DefaultInterpolationMethod);
	}

	/**
	 * Creates a new instance with the specified geometric mapping
	 * and pixel interpolation method.
	 * @param mp the geometric mapping
	 * @param im the pixel interpolation method
	 */
	public ImageMapper(Mapping2D mp, InterpolationMethod im) {
		this.mp = mp;
		this.im = im;
	}
	
	// ---------------------------------------------------------------------
	
	/**
	 * Destructively transforms the passed image using this geometric
	 * mapping and the specified pixel interpolation method.
	 * The geometric mapping is supposed to be INVERTED, i.e. transforming
	 * target to source image coordinates!
	 * 
	 * @param ip target image to which this mapping is applied
	 */
	public void map(ImageProcessor ip) {
		// make a temporary copy of the image:
		ImageProcessor source = ip.duplicate();
		ImageProcessor target = ip;
		map(source, target);
		source = null;
	}
	
	// ---------------------------------------------------------------------
	
	/**
	 * Transforms the source image to the target image using this geometric
	 * mapping and the specified pixel interpolation method. Note that source
	 * and target must be different images!
	 * The geometric mapping is supposed to be INVERTED, i.e. transforming
	 * target to source image coordinates!
	 * 
	 * @param source input image (not modified)
	 * @param target output image (modified)
	 */
	public void map(ImageProcessor source, ImageProcessor target) {
		if (target == source) {
			throw new IllegalArgumentException("Source and target image must not be the same!");
		}
		ImageAccessor sourceAcc = ImageAccessor.create(source, null, im);
		ImageAccessor targetAcc = ImageAccessor.create(target);
		map(sourceAcc, targetAcc);
	}

	
	// ---------------------------------------------------------------------

	/**
	 * Transforms the source image to the target image using this geometric
	 * mapping and the specified pixel interpolation method.
	 * The two images are passed as instances of {@link ImageAccessor}.
	 * Note that source and target reference different images!
	 * The geometric mapping is supposed to be INVERTED, i.e. transforming
	 * target to source image coordinates!
	 * 
	 * @param sourceAcc accessor to the source image
	 * @param targetAcc accessor to the target image
	 */
	public void map(ImageAccessor sourceAcc, ImageAccessor targetAcc) {
		if (targetAcc.getProcessor() == sourceAcc.getProcessor()) {
			throw new IllegalArgumentException("Source and target image must not be the same!");
		}
		Mapping2D invMap = mp; 		// this always IS an inverse mapping!!
		ImageProcessor target = targetAcc.getProcessor();
		final int w = target.getWidth();
		final int h = target.getHeight();
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				Pnt2d sourcePt = invMap.applyTo(PntInt.from(u, v));
				float[] val = sourceAcc.getPix(sourcePt.getX(), sourcePt.getY());
				targetAcc.setPix(u, v, val);
			}
		}
	}

}
