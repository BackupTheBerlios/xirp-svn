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
 * LanguageFolder.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 16.03.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs.preferences;

import java.util.Locale;
import java.util.Set;

import org.eclipse.swt.SWT;

import de.xirp.settings.*;
import de.xirp.settings.IValue.SettingsState;
import de.xirp.settings.Option.OptionType;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.util.I18n;

/**
 * A custom composite which shows the settings for the language.
 * 
 * @author Matthias Gernand
 */
public final class LanguageFolder extends AbstractContentFolderComposite {

	/**
	 * {@link de.xirp.settings.Option} for the language
	 * to use.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option option;

	/**
	 * Constructs a new language folder.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	public LanguageFolder(XComposite parent) {
		super(parent, SWT.NONE);
		init( );
	}

	/**
	 * Gets a string for the given locale as it may be displayed in
	 * the combobox.
	 * 
	 * @param loc
	 *            the locale to get the string for
	 * @param dest
	 *            the current locale of the application
	 * @return a string for the given locale
	 */
	private String getLocaleString(Locale loc, Locale dest) {
		StringBuilder builder = new StringBuilder( );
		builder.append(loc.toString( ));
		builder.append(" | "); //$NON-NLS-1$
		builder.append(loc.getDisplayName(dest));
		return builder.toString( );

	}

	/**
	 * Initializes the {@link de.xirp.settings.Settings}
	 * object and builds the UI from it.
	 */
	private void init() {
		settings = new Settings("LanguageFolder"); //$NON-NLS-1$
		SettingsPage settingsPage = settings.addPage("gui", "setLanguage", "languageDescription"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		option = settingsPage.addOption("chooseLanguage", OptionType.COMBOBOX); //$NON-NLS-1$
		Set<Locale> locals = I18n.getAvailableLocals( );
		final Locale currentLocale = I18n.getLocale( );
		for (Locale locale : locals) {
			// TODO: add for i18n and not
			option.addNonTranslatableNamedBooleanValue(getLocaleString(locale,
					currentLocale), SettingsState.NOT_SELECTED);
		}
		Locale currentLoc = PropertiesManager.getLocale( );

		String current = getLocaleString(currentLoc, currentLocale);

		option.addNonTranslatableNamedBooleanValue(current,
				SettingsState.SELECTED);

		this.constructUIFromSettings(settings);

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
		IValue value = option.getSelectedValue( );
		if (value instanceof StringValue) {
			StringValue strg = (StringValue) value;
			PropertiesManager.setLocale(strg.getDisplayValue( ).split("[|]")[0].trim( )); //$NON-NLS-1$
		}
	}

	/**
	 * This folder does not support defaults.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#supportsDefaults()
	 */
	@Override
	public boolean supportsDefaults() {
		return false;
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
