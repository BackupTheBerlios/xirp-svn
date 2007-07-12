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
 * FontManager.java
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.ui.util.SWTUtil;
import de.xirp.util.Constants;

/**
 * Manager for fonts. This managers allows to receive fonts for SWT,
 * caches these fonts internally and disposes them on shutdown to
 * prevent memory leaks and having more than one instance per font.
 * 
 * @author Rabea Gransberger
 */
public final class FontManager extends AbstractManager {

	/**
	 * The Logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(FontManager.class);
	/**
	 * The display which is used for color creation.
	 */
	private static Display display = Display.getDefault( );
	/**
	 * The map of currently allocated fonts
	 */
	private static FastMap<FontData, Font> fonts = new FastMap<FontData, Font>( ).setShared(true);
	/**
	 * Object used for synchronization when creating new fonts
	 */
	private static Object lock = new Object( );

	/**
	 * Creates a new manager for fonts. The manager is initialized on
	 * startup. Never call this on your own. Use the statically
	 * provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	public FontManager() throws InstantiationException {
		super( );
	}

	/**
	 * Gets a color for the given data or creates a new one if it did
	 * not exist before. Creating a new color is synchronized.
	 * 
	 * @param data
	 *            the data to get the font for
	 * @return the font
	 */
	private static Font internGetFont(FontData data) {
		Font font = fonts.get(data);
		if (!SWTUtil.swtAssert(font)) {
			synchronized (lock) {
				// double check to prevent creating a font
				// which does exist now, because
				// the current thread had to wait
				// at the lock while another thread created the font
				font = fonts.get(data);
				if (SWTUtil.swtAssert(font)) {
					return font;
				}
				font = new Font(display, data);
				// dispose any old listed fonts
				// This should not happen due to synchronization
				// but we really don't want to have memory leaks
				Font old = fonts.put(data, font);
				SWTUtil.secureDispose(old);
			}

		}
		return font;
	}

	/**
	 * Gets a font for the given data. The font is registered at the
	 * manager and will be disposed automatically on shutdown. Don't
	 * dispose the font on your own, because it might be in use by
	 * others.
	 * 
	 * @param data
	 *            the data for the font
	 * @return the font according to the data given
	 */
	public static Font getFont(FontData data) {
		Font font = internGetFont(data);
		return font;
	}

	/**
	 * Gets a font for the given data. The font is registered at the
	 * manager and will be disposed automatically on shutdown. Don't
	 * dispose the font on your own, because it might be in use by
	 * others.
	 * 
	 * @param name
	 *            the name of the font
	 * @param height
	 *            the size of the font
	 * @param style
	 *            a bitwise combination of
	 *            {@link org.eclipse.swt.SWT#NORMAL},
	 *            {@link org.eclipse.swt.SWT#ITALIC} and
	 *            {@link org.eclipse.swt.SWT#BOLD}
	 * @return the font according to the data given
	 */
	public static Font getFont(String name, int height, int style) {
		FontData data = new FontData(name, height, style);
		return getFont(data);
	}

	/**
	 * Returns a reasonable font for applications to use. On some
	 * platforms, this will match the "default font" or "system font"
	 * if such can be found. This font should not be free'd because it
	 * was allocated by the system, not the application. <br>
	 * <br>
	 * Typically, applications which want the default look should
	 * simply not set the font on the widgets they create. Widgets are
	 * always created with the correct default font for the class of
	 * user-interface component they represent.
	 * 
	 * @return a font
	 * @see Display#getSystemFont()
	 */
	public static Font getSystemFont() {
		return display.getSystemFont( );
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
	 * Stops this manager and disposes all registered fonts.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
		logClass.debug("Disposing " + fonts.size( ) + " fonts." //$NON-NLS-1$ //$NON-NLS-2$
				+ Constants.LINE_SEPARATOR);
		for (Font font : fonts.values( )) {
			SWTUtil.secureDispose(font);
		}
		fonts.clear( );
	}
}
