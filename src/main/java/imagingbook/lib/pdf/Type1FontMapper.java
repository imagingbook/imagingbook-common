package imagingbook.lib.pdf;

import java.awt.Font;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;


public class Type1FontMapper extends DefaultFontMapper {
	
	public static boolean VERBOSE = false;
	private static String classname = Type1FontMapper.class.getSimpleName();

	static String[] namesMonospaced = {"DIALOG", "DIALOGINPUT", "MONOSPACED", "COURIER", "COURIER NEW"};
	static String[] namesSerif = {"SERIF", "TIMES", "TIMESROMAN", "TIMES NEW ROMAN"};

	private final HashSet<String> keysMonospaced;
	private final HashSet<String> keysSerif;

	public Type1FontMapper() {
		keysMonospaced = new HashSet<>(Arrays.asList(namesMonospaced));
		keysSerif      = new HashSet<>(Arrays.asList(namesSerif));
	}
	
	// --------------------------------------------------
	
	/**
	 * Adds the logical name of a font to be treated as monospaced.
	 * All such fonts will be replaced by {@code Courier} in the PDF.
	 * @param key the logical font name
	 * @return true if insertion was successful
	 */
	public boolean addKeyMonospaced(String key) {
		return keysMonospaced.add(key.toUpperCase());
	}
	
	/**
	 * Adds the logical name of a font to be treated as a serif font.
	 * All such fonts will be replaced by {@code Times} in the PDF.
	 * @param key the logical font name
	 * @return true if insertion was successful
	 */
	public boolean addKeySerif(String key) {
		return keysSerif.add(key.toUpperCase());
	}

	// --------------------------------------------------
	
	public BaseFont awtToPdf(Font font) {
		
		BaseFontParameters p = getBaseFontParameters(font.getFontName());
		if (p != null) {
			BaseFont bf = null;
			try {
				bf = BaseFont.createFont(p.fontName, p.encoding, p.embedded, p.cached, p.ttfAfm, p.pfb);
			} catch (DocumentException | IOException e) {
				System.out.println("***** BaseFont.createFont() failed! ***");
			}
			if (VERBOSE) System.out.println(classname + ": mapped to known font " + bf.getPostscriptFontName());
			return bf;
		}
		
		//		String logicalName = font.getFontName();
		String logicalName = font.getName();
		
		
		// extract up to first '.'
		int pos =  logicalName.indexOf('.');
		if (pos >= 0) {
			logicalName = logicalName.substring(0, pos);	//TODO: bug in DefaultFontMapper !
		}
//		System.out.println("logicalName2 = " + logicalName);

		boolean isIt = font.isItalic();
		boolean isBf = font.isBold();

		Type1StandardFont sf = null;

		String key = logicalName.toUpperCase();
		if (keysMonospaced.contains(key)) {
			if (isIt)
				sf = (isBf) ? Type1StandardFont.CourierBoldOblique : Type1StandardFont.CourierOblique;
			else
				sf = (isBf) ? Type1StandardFont.CourierBold : Type1StandardFont.Courier;
		}
		else if (keysSerif.contains(key)) {
			if (isIt)
				sf = (isBf) ? Type1StandardFont.TimesBoldItalic : Type1StandardFont.TimesItalic;
			else
				sf = (isBf) ? Type1StandardFont.TimesBold : Type1StandardFont.TimesRoman;
		}
		else {
			if (isIt)
				sf = (isBf) ? Type1StandardFont.HelveticaBoldOblique : Type1StandardFont.HelveticaOblique;
			else
				sf = (isBf) ? Type1StandardFont.HelveticaBold : Type1StandardFont.Helvetica;
		}

		if (VERBOSE) System.out.println(classname + ": mapped to Type1 font " + logicalName + " --> " + sf.toString());

		return (sf != null) ? sf.getBaseFont() : null;
	}

	@Override
	public Font pdfToAwt(BaseFont font, int size) {
		return null;
	}



	// ------------------------------------------------------------------------------------

	public static void main(String[] args) {
		FontMapper dfm = new Type1FontMapper();

		//		java.awt.Font awtFont1 = new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 18);
		java.awt.Font awtFont1 = new java.awt.Font(java.awt.Font.DIALOG, java.awt.Font.ITALIC, 18);
		BaseFont bf1 = dfm.awtToPdf(awtFont1);
		System.out.println("bf1 = " + bf1.getPostscriptFontName());
		System.out.println("bf1.isEmbedded = " + bf1.isEmbedded());

	}
}
