package imagingbook.lib.pdf;

import java.io.IOException;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;

import imagingbook.lib.pdf.fonts.type1.RLOC;
import imagingbook.lib.util.resource.ResourceLocation;
import imagingbook.lib.util.resource.ResourceLocation.Resource;

/**
 * This class collects the set of 14 Type-1 standard fonts that every
 * PS/PDF reader is supposed to have available by default.
 * The associated resource directory contains both .AFM and .PFB files.
 * iText/OpenPDF never embeds any of these fonts on the exported PDF,
 * which may cause problems during pre-press checking, where generally
 * ALL fonts should be embedded.
 * 
 * @author WB
 *
 */
public enum Type1StandardFont {
	
	Courier("Courier.afm"),
	CourierBold("Courier-Bold.afm"),
	CourierBoldOblique("Courier-BoldOblique.afm"),
	CourierOblique("Courier-Oblique.afm"),	
	Helvetica("Helvetica.afm"),
	HelveticaBold("Helvetica-Bold.afm"),
	HelveticaBoldOblique("Helvetica-BoldOblique.afm"),
	HelveticaOblique("Helvetica-Oblique.afm"),	
	TimesRoman("Times-Roman.afm"),
	TimesBold("Times-Bold.afm"),
	TimesBoldItalic("Times-BoldItalic.afm"),
	TimesItalic("Times-Italic.afm"),	
	Symbol("Symbol.afm"),
	ZapfDingbats("ZapfDingbats.afm")
	;
	
	private final String afmName;
	private BaseFont basefont;
	
	private static ResourceLocation Resources = new RLOC();	// location of font resources

	/**
	 * Constructor.
	 * @param afmName the name of the associated AFM file
	 */
	Type1StandardFont(String afmName) {
		this.afmName = afmName;
		this.basefont = null;	// direct initialization makes problems (exception reading reseources)
	}
	
	/** 
	 * Returns the {@link BaseFont} instance associatedwith this enum element.
	 * The font is created when this method is called the first time.
	 * 
	 * @return the {@link BaseFont} instance associated with this font
	 */
	public BaseFont getBaseFont() {
		if (basefont == null) {
			try {
				basefont = BaseFont.createFont(getFontPath(this.afmName), "", BaseFont.EMBEDDED);
			} catch (DocumentException | IOException e) { 
				System.out.println("Trouble: " + e);
			}
		}
		return basefont;
	}

	static String getFontPath(String fileName) {
		Resource res = Resources.getResource(fileName);
		return res.getPath().toString();
	}
	
	// -----------------------------------------------
	
	public static void main(String[] args) {
		System.out.println("Anchor is in JAR: " + Resources.insideJAR());
		for (Type1StandardFont sf : Type1StandardFont.values()) {
			System.out.println(sf.toString() + ": " + sf.getBaseFont().getPostscriptFontName());
		}
	}
}

