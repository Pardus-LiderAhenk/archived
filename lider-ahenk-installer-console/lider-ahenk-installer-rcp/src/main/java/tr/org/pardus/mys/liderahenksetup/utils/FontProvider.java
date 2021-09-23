package tr.org.pardus.mys.liderahenksetup.utils;

import java.util.Set;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class FontProvider {

	private static FontProvider instance = null;

	public static final String LABEL_FONT = "label-font";
	public static final String HEADER_FONT = "header-font";
	public static final String INPUT_FONT = "input-font";

	public synchronized static FontProvider getInstance() {
		if (instance == null) {
			instance = new FontProvider();
			instance.registerSystemWideFonts();
		}
		return instance;
	}

	private FontProvider() {
	}

	private FontRegistry fontRegistry = null;

	private void registerSystemWideFonts() {
		if (fontRegistry == null) {
			fontRegistry = new FontRegistry();
		}
		// Load Ubuntu Light
		Font f = loadFont("Ubuntu-L.ttf", "Ubuntu-Light", 10, SWT.NORMAL);
		fontRegistry.put(INPUT_FONT, f.getFontData());

		// Load Ubuntu Regular
		f = loadFont("Ubuntu-R.ttf", "Ubuntu", 10, SWT.NORMAL);
		fontRegistry.put(LABEL_FONT, f.getFontData());

		// Load Ubuntu Bold
		f = loadFont("Ubuntu-B.ttf", "Ubuntu-Bold", 14, SWT.BOLD);
		fontRegistry.put(HEADER_FONT, f.getFontData());
	}

	public Font get(String symbolicName) {
		if (fontRegistry != null && symbolicName != null) {
			return fontRegistry.get(symbolicName);
		}
		return null;
	}

	private static Font loadFont(String fileName, String fontName, int fontSize, int style) {
		boolean isFontLoaded = Display.getCurrent().loadFont(fileName);
		if (isFontLoaded) {
			return new Font(Display.getCurrent(), fontName, fontSize, style);
		}
		return null;
	}

	public static void displayFonts() {
		// display all scalable fonts in the system
		FontData[] fd = Display.getCurrent().getFontList(null, true);
		for (int i = 0; i < fd.length; i++) {
			System.out.println(fd[i].getName());
		}
		// and the non-scalable ones
		fd = Display.getCurrent().getFontList(null, false);
		for (int i = 0; i < fd.length; i++) {
			System.out.println(fd[i].getName());
		}
	}

	public void dispose() {
		if (fontRegistry != null) {
			Set<String> symNames = fontRegistry.getKeySet();
			for (String symName : symNames) {
				Font font = fontRegistry.get(symName);
				if (font != null) {
					font.dispose();
				}
			}
		}
	}

}
