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
 * AppearanceFolder.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 30.05.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import com.swtplus.widgets.combo.NamedRGB;

import de.xirp.settings.*;
import de.xirp.settings.Option.OptionType;
import de.xirp.util.Constants;

/**
 * A custom composite which shows color combo boxes for choosing the
 * colors used in the application and XWidgets.
 * 
 * @author Matthias Gernand
 */
public final class AppearanceFolder extends AbstractContentFolderComposite {

	/**
	 * {@link de.xirp.settings.Option} for the tab color.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option tabColor;
	/**
	 * {@link de.xirp.settings.Option} for the tab text
	 * color.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option tabtextColor;
	/**
	 * {@link de.xirp.settings.Option} for the active tab
	 * text color.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option activeColor;
	/**
	 * {@link de.xirp.settings.Option} for the sash
	 * color.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option sashColor;
	/**
	 * {@link de.xirp.settings.Option} for the background
	 * color.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option backgroundColor;
	/**
	 * {@link de.xirp.settings.Option} for the panel
	 * header color.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option panelHeaderColor;
	/**
	 * {@link de.xirp.settings.Option} for the inactive
	 * tab text color.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option inactiveTabtextColor;

	/**
	 * Constructs a new appearance folder composite, which shows the
	 * settings for the colors used in the application.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	public AppearanceFolder(Composite parent) {
		super(parent, SWT.NONE);
		init( );
	}

	/**
	 * Initializes the {@link de.xirp.settings.Settings}
	 * object and builds the UI from it.
	 */
	private void init() {
		settings = new Settings("AppearanceFolder"); //$NON-NLS-1$
		SettingsPage settingsPage = settings.addPage("gui", "colorSettings", "colorSettingsCanBeDoneHere"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		tabColor = settingsPage.addOption("tabColor", OptionType.COLOR); //$NON-NLS-1$
		tabColor.addRGBValue("key", Constants.DEFAULT_COLOR_WHITE).setSavedRGB(PropertiesManager.getTabNamedRGB( ).getRGB( )); //$NON-NLS-1$

		tabtextColor = settingsPage.addOption("tabtextColor", OptionType.COLOR); //$NON-NLS-1$
		tabtextColor.addRGBValue("key", new RGB(0, 0, 0)).setSavedRGB(PropertiesManager.getTabTextColorNamedRGB( ).getRGB( )); //$NON-NLS-1$

		inactiveTabtextColor = settingsPage.addOption("inactiveTabtextColor", OptionType.COLOR); //$NON-NLS-1$
		inactiveTabtextColor.addRGBValue("key", new RGB(0, 0, 0)).setSavedRGB(PropertiesManager.getInactiveTabTextColorNamedRGB( ).getRGB( )); //$NON-NLS-1$

		activeColor = settingsPage.addOption("activeColor", OptionType.COLOR); //$NON-NLS-1$
		activeColor.addRGBValue("key", Constants.DEFAULT_COLOR_WHITE).setSavedRGB(PropertiesManager.getFocusColorNamedRGB( ).getRGB( )); //$NON-NLS-1$

		sashColor = settingsPage.addOption("sashColor", OptionType.COLOR); //$NON-NLS-1$
		sashColor.addRGBValue("key", Constants.DEFAULT_COLOR_WHITE).setSavedRGB(PropertiesManager.getSashNamedRGB( ).getRGB( )); //$NON-NLS-1$

		backgroundColor = settingsPage.addOption("backgroundColor", OptionType.COLOR); //$NON-NLS-1$
		backgroundColor.addRGBValue("key", Constants.DEFAULT_COLOR_WHITE).setSavedRGB(PropertiesManager.getBackgroundNamedRGB( ).getRGB( )); //$NON-NLS-1$

		panelHeaderColor = settingsPage.addOption("panelHeaderColor", OptionType.COLOR); //$NON-NLS-1$
		panelHeaderColor.addRGBValue("key", Constants.DEFAULT_COLOR_WHITE).setSavedRGB(PropertiesManager.getPanelHeaderColorNamedRGB( ).getRGB( )); //$NON-NLS-1$

		constructUIFromSettings(settings);

		settings.addSettingsChangedListener(new SettingsChangedListener( ) {

			public void settingsChanged(SettingsChangedEvent event) {
				saveToIni( );
			}

		});
		finishInit( );
	}

	/**
	 * Saves the values to the ini file.
	 */
	private void saveToIni() {
		IValue value = tabColor.getSelectedValue( );
		if (value instanceof RGBValue) {
			RGBValue rgb = (RGBValue) value;
			NamedRGB named = new NamedRGB("Custom", rgb.getRGB(rgb.getDisplayValue( ))); //$NON-NLS-1$
			PropertiesManager.setTabColor(named);
		}

		value = tabtextColor.getSelectedValue( );
		if (value instanceof RGBValue) {
			RGBValue rgb = (RGBValue) value;
			NamedRGB named = new NamedRGB("Custom", rgb.getRGB(rgb.getDisplayValue( ))); //$NON-NLS-1$
			PropertiesManager.setTabTextColor(named);
		}

		value = inactiveTabtextColor.getSelectedValue( );
		if (value instanceof RGBValue) {
			RGBValue rgb = (RGBValue) value;
			NamedRGB named = new NamedRGB("Custom", rgb.getRGB(rgb.getDisplayValue( ))); //$NON-NLS-1$
			PropertiesManager.setInactiveTabTextColor(named);
		}

		value = activeColor.getSelectedValue( );
		if (value instanceof RGBValue) {
			RGBValue rgb = (RGBValue) value;
			NamedRGB named = new NamedRGB("Custom", rgb.getRGB(rgb.getDisplayValue( ))); //$NON-NLS-1$
			PropertiesManager.setFocusColor(named);
		}

		value = sashColor.getSelectedValue( );
		if (value instanceof RGBValue) {
			RGBValue rgb = (RGBValue) value;
			NamedRGB named = new NamedRGB("Custom", rgb.getRGB(rgb.getDisplayValue( ))); //$NON-NLS-1$
			PropertiesManager.setSashColor(named);
		}

		value = backgroundColor.getSelectedValue( );
		if (value instanceof RGBValue) {
			RGBValue rgb = (RGBValue) value;
			NamedRGB named = new NamedRGB("Custom", rgb.getRGB(rgb.getDisplayValue( ))); //$NON-NLS-1$
			PropertiesManager.setBackgroundColor(named);
		}

		value = panelHeaderColor.getSelectedValue( );
		if (value instanceof RGBValue) {
			RGBValue rgb = (RGBValue) value;
			NamedRGB named = new NamedRGB("Custom", rgb.getRGB(rgb.getDisplayValue( ))); //$NON-NLS-1$
			PropertiesManager.setPanelHeaderColor(named);
		}
	}

	/**
	 * This folder supports defaults.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#supportsDefaults()
	 */
	@Override
	public boolean supportsDefaults() {
		return true;
	}

	/**
	 * This folder supports resets.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#supportsReset()
	 */
	@Override
	public boolean supportsReset() {
		return true;
	}
}
