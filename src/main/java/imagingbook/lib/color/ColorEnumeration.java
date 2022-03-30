package imagingbook.lib.color;

import java.awt.Color;

public interface ColorEnumeration {
	
	public Color getColor();
	
	public default int getRGB() {
		return getColor().getRGB();
	}

}
