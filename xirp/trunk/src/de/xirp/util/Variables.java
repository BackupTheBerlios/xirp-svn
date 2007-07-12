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
 * Variables.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 30.05.2006:		Created by Matthias Gernand.
 */
package de.xirp.util;

import org.eclipse.swt.graphics.RGB;

import de.xirp.profile.ProfileManager;
import de.xirp.settings.PropertiesManager;

/**
 * Variables which in contrast to the {@link Constants} may change
 * during application runtime. It contains {@link RGB} values which
 * represent the current look and feel of the application.
 * 
 * @author Matthias Gernand
 */
public final class Variables {

	/**
	 * The current background color.
	 */
	public static RGB BACKGROUND_COLOR = Constants.DEFAULT_COLOR_WHITE;
	/**
	 * The current focus color.
	 */
	public static RGB FOCUS_COLOR = Constants.DEFAULT_COLOR_WHITE;
	/**
	 * The current tab color.
	 */
	public static RGB TAB_COLOR = Constants.DEFAULT_COLOR_WHITE;
	/**
	 * The current tab text color.
	 */
	public static RGB TAB_TEXT_COLOR = Constants.DEFAULT_COLOR_BLACK;
	/**
	 * The current inactive tab text color.
	 */
	public static RGB INACTIVE_TAB_TEXT_COLOR = Constants.DEFAULT_COLOR_BLACK;
	/**
	 * The current sash color.
	 */
	public static RGB SASH_COLOR = Constants.DEFAULT_COLOR_WHITE;
	/**
	 * The current panel header color.
	 */
	public static RGB PANEL_HEADER_COLOR = Constants.DEFAULT_COLOR_WHITE;
	/**
	 * Flag which is <code>true</code> if no profiles were found
	 * during startup.
	 */
	public static boolean NO_PROFILES_LOADED = false;

	/**
	 * Initializes the variables used. If this constructor is not
	 * called, the default values are used.
	 */
	public Variables() {
		reloadVariables( );
	}

	/**
	 * Reloads the variables.
	 */
	public static void reloadVariables() {
		BACKGROUND_COLOR = PropertiesManager.getBackgroundNamedRGB( ).getRGB( );
		FOCUS_COLOR = PropertiesManager.getFocusColorNamedRGB( ).getRGB( );
		TAB_COLOR = PropertiesManager.getTabNamedRGB( ).getRGB( );
		INACTIVE_TAB_TEXT_COLOR = PropertiesManager.getInactiveTabTextColorNamedRGB( )
				.getRGB( );
		SASH_COLOR = PropertiesManager.getSashNamedRGB( ).getRGB( );
		TAB_TEXT_COLOR = PropertiesManager.getTabTextColorNamedRGB( ).getRGB( );
		PANEL_HEADER_COLOR = PropertiesManager.getPanelHeaderColorNamedRGB( )
				.getRGB( );
		NO_PROFILES_LOADED = (ProfileManager.getProfileCount( ) <= 0);
	}
}
