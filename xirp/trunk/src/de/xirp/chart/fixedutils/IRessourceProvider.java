package de.xirp.chart.fixedutils;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * Provider for resources which have to be disposed after use.
 * 
 * @author Rabea Gransberger
 */
public interface IRessourceProvider {

	/**
	 * Gets the color for the given RGB values.
	 * 
	 * @param r
	 *            red part
	 * @param g
	 *            green part
	 * @param b
	 *            blue part
	 * @return the color
	 */
	public Color getColor(int r, int g, int b);

	/**
	 * Gets the color for the given RGB values.
	 * 
	 * @param rgb
	 *            red,green,blue
	 * @return the color
	 */
	public Color getColor(RGB rgb);

	/**
	 * Gets the font which is specified by the given font data.
	 * 
	 * @param data
	 *            the data from which the font is created.
	 * @return the font
	 */
	public Font getFont(FontData data);

	/**
	 * Gets the font which is specified by the given data.
	 * 
	 * @param name
	 *            the name of the font
	 * @param height
	 *            the height of the font
	 * @param style
	 *            the style of the font which is one of the swt style
	 *            constants.
	 * @return the font
	 */
	public Font getFont(String name, int height, int style);
}
