/** 
 * ============================================================================
 * Xirp 2: eXtendable interface for robotic purposes.
 * ============================================================================
 * 
 * Copyright (C) 2005-2007, by Authors and Contributors listed in CREDITS.txt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at:
 *
 * 				http://www.opensource.org/licenses/cpl1.0.php
 *
 * ----------------------------
 * GradientUtil.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 07.05.2007:		Created by Rabea Gransberger.
 */
package de.xirp.ui.util;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.util.HashMap;

/**
 * Utility class for getting colors of a color gradient.
 * 
 * @author Rabea Gransberger
 */
public final class GradientUtil {

	/**
	 * Height of the color gradient line
	 */
	private static final int HEIGHT = 2;
	/**
	 * The raster for getting information about the gradients colors
	 */
	private Raster raster;
	/**
	 * The width of the color gradient line
	 */
	private int width;
	/**
	 * The default color which is returned if the requested value is
	 * out of range
	 */
	private Color defaultColor;

	/**
	 * Constructs a new color gradient with the given with and colors.
	 * 
	 * @param width
	 *            the width of the color gradient line. Use the real
	 *            width to be sure to have a matching resolution.
	 * @param dist
	 *            numbers ranging from 0.0 to 1.0 specifying the
	 *            distribution of colors along the gradient
	 * @param colors
	 *            array of colors corresponding to each fractional
	 *            value
	 */
	private GradientUtil(int width, float[] dist, Color[] colors) {
		this.width = width;
		Point2D start = new Point2D.Float(0, 0);
		Point2D end = new Point2D.Float(width, HEIGHT);
		LinearGradientPaint p = new LinearGradientPaint(start,
				end,
				dist,
				colors);
		PaintContext c = p.createContext(ColorModel.getRGBdefault( ),
				new Rectangle(0, 0, width, HEIGHT),
				new Rectangle(0, 0, width, HEIGHT),
				new AffineTransform( ),
				new RenderingHints(new HashMap<RenderingHints.Key, Object>( )));
		raster = c.getRaster(0, 0, width, HEIGHT);
		this.defaultColor = colors[0];
	}

	/**
	 * Constructs a new gradient using a width of <code>100000</code>
	 * to provide a high resolution for the colors.
	 * 
	 * @param dist
	 *            numbers ranging from 0.0 to 1.0 specifying the
	 *            distribution of colors along the gradient
	 * @param colors
	 *            array of colors corresponding to each fractional
	 *            value
	 */
	public GradientUtil(float[] dist, Color[] colors) {
		this(100000, dist, colors);
	}

	/**
	 * Constructs a new gradient using a width of <code>100000</code>
	 * and a equally sized color fractions.
	 * 
	 * @param colors
	 *            array of colors
	 */
	public GradientUtil(Color[] colors) {
		this(getDist(colors.length), colors);
	}

	/**
	 * Gets a distribution array for the given number of colors.
	 * 
	 * @param num
	 *            the number of colors
	 * @return array with floats containing equally sized color
	 *         fractions for the colors.
	 */
	private static float[] getDist(int num) {
		float[] f = new float[num];
		f[0] = 0.0f;

		float factor = 1.0f / num;

		for (int i = 1; i < num - 1; i++) {
			f[i] = factor * i;
		}

		f[num - 1] = 1.0f;
		return f;
	}

	/**
	 * Gets the color of this color gradient which lays at the given
	 * percentage position of the color gradient line.
	 * 
	 * @param percent
	 *            the percentage ranging from 0.0 to 1.0
	 * @return the color corresponding to the given percentage or the
	 *         first color of the gradient if the percentage is not in
	 *         range
	 */
	public Color getColor(float percent) {
		int x = (int) Math.rint(width * percent);
		try {
			double[] d = raster.getPixel(x - 1, 1, new double[4]);
			Color c = new Color((int) Math.rint(d[0]),
					(int) Math.rint(d[1]),
					(int) Math.rint(d[2]));
			return c;
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return defaultColor;
		}
	}
}
