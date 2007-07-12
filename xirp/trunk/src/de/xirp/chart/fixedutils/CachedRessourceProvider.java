package de.xirp.chart.fixedutils;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.ui.util.ressource.FontManager;

/**
 * A resource provider implementation which uses the xirp resource
 * management.
 * 
 * @author Rabea Gransberger
 */
public class CachedRessourceProvider implements IRessourceProvider {

	/**
	 * @see de.xirp.chart.fixedutils.IRessourceProvider#getColor(int,
	 *      int, int)
	 * @see ColorManager#getColor(int, int, int)
	 */
	@Override
	public Color getColor(int r, int g, int b) {
		return ColorManager.getColor(r, g, b);
	}

	/**
	 * @see de.xirp.chart.fixedutils.IRessourceProvider#getColor(org.eclipse.swt.graphics.RGB)
	 * @see ColorManager#getColor(RGB)
	 */
	@Override
	public Color getColor(RGB rgb) {
		return ColorManager.getColor(rgb);
	}

	/**
	 * @see de.xirp.chart.fixedutils.IRessourceProvider#getFont(org.eclipse.swt.graphics.FontData)
	 * @see FontManager#getFont(FontData)
	 */
	@Override
	public Font getFont(FontData data) {
		return FontManager.getFont(data);
	}

	/**
	 * @see de.xirp.chart.fixedutils.IRessourceProvider#getFont(java.lang.String,
	 *      int, int)
	 * @see FontManager#getFont(String, int, int)
	 */
	@Override
	public Font getFont(String name, int height, int style) {
		return FontManager.getFont(name, height, style);
	}

}
