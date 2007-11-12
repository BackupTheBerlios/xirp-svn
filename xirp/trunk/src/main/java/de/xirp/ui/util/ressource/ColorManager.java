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
 * ColorManager.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.09.2006:		Created by Rabea Gransberger.
 */
package de.xirp.ui.util.ressource;

import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.ui.util.SWTUtil;
import de.xirp.util.Constants;

/**
 * Manager for colors. This managers allows to receive colors for SWT,
 * caches these colors internally and disposes them on shutdown to
 * prevent memory leaks and having more than one instance per color.
 * 
 * @author Rabea Gransberger
 */
public final class ColorManager extends AbstractManager {

	/**
	 * The Logger for this class.
	 */
	private static Logger logClass = Logger.getLogger(ColorManager.class);

	/**
	 * The display which is used for color creation.
	 */
	private static Display display = Display.getDefault( );
	/**
	 * The map of currently allocated colors
	 */
	private static FastMap<RGB, Color> colors = new FastMap<RGB, Color>( ).setShared(true);
	/**
	 * Object used for synchronization when creating new colors
	 */
	private static Object lock = new Object( );

	/**
	 * Creates a new manager for colors. The manager is initialized on
	 * startup. Never call this on your own. Use the statically
	 * provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	public ColorManager() throws InstantiationException {
		super( );
	}

	/**
	 * Gets a color for the given RGB value or creates a new one if it
	 * did not exist before. Creating a new color is synchronized.
	 * 
	 * @param rgb
	 *            the RGB value to get the color for
	 * @return the color
	 */
	private static Color internGetColor(RGB rgb) {
		Color color = colors.get(rgb);
		if (!SWTUtil.swtAssert(color)) {
			synchronized (lock) {
				// double check to prevent creating a color
				// which does exist now, because
				// the current thread had to wait
				// at the lock while another thread created the color
				color = colors.get(rgb);
				if (SWTUtil.swtAssert(color)) {
					return color;
				}
				color = new Color(display, rgb);
				// dispose any old listed colors
				// This should not happen due to synchronization
				// but we really don't want to have memory leaks
				Color old = colors.put(rgb, color);
				SWTUtil.secureDispose(old);
			}
		}
		return color;
	}

	/**
	 * Gets a color for the given RGB values. The color is registered
	 * at the manager and will be disposed automatically on shutdown.
	 * Don't dispose the color on your own, because it might be in use
	 * by others.
	 * 
	 * @param red
	 *            the amount of red in the color
	 * @param green
	 *            the amount of green in the color
	 * @param blue
	 *            the amount of blue in the color
	 * @return the color according to the RGB values given
	 */
	public static Color getColor(int red, int green, int blue) {
		RGB rgb = new RGB(red, green, blue);
		return getColor(rgb);
	}

	/**
	 * Gets a color for the given RGB value. The color is registered
	 * at the manager and will be disposed automatically on shutdown.
	 * Don't dispose the color on your own, because it might be in use
	 * by others.
	 * 
	 * @param rgb
	 *            object with the amounts for each part of the color
	 * @return the color according to the RGB values given
	 */
	public static Color getColor(RGB rgb) {
		if (rgb == null) {
			return null;
		}
		Color color = internGetColor(rgb);
		return color;
	}

	/**
	 * Returns the matching standard color for the given constant,
	 * which should be one of the color constants specified in class
	 * SWT. Any value other than one of the SWT color constants which
	 * is passed in will result in the color black. This color should
	 * not be free'd because it was allocated by the system, not the
	 * application.
	 * 
	 * @param swtConstant
	 *            the color constant
	 * @return the matching color
	 * @see Display#getSystemColor(int)
	 */
	public static Color getColor(int swtConstant) {
		return display.getSystemColor(swtConstant);
	}

	/**
	 * Gets a SWT color according to the given AWT color. The SWT
	 * color is registered at the manager and will be disposed
	 * automatically on shutdown. Don't dispose the color on your own,
	 * because it might be in use by others.
	 * 
	 * @param awtColor
	 *            the AWT color
	 * @return the SWT color according to the AWT color
	 */
	public static Color getColor(java.awt.Color awtColor) {
		return getColor(awtColor.getRed( ),
				awtColor.getGreen( ),
				awtColor.getBlue( ));
	}

	/**
	 * Nothing to do for this manager.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
	}

	/**
	 * Stops this manager and disposes all registered colors.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
		logClass.debug("Disposing " + colors.size( ) + " colors." + Constants.LINE_SEPARATOR); //$NON-NLS-1$ //$NON-NLS-2$
		for (Color color : colors.values( )) {
			SWTUtil.secureDispose(color);
		}
		colors.clear( );
	}
}
