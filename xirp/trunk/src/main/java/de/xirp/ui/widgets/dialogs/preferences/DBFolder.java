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
 * DBFolder.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 17.03.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs.preferences;

import org.eclipse.swt.SWT;

import de.xirp.settings.IValue;
import de.xirp.settings.Option;
import de.xirp.settings.PropertiesManager;
import de.xirp.settings.Settings;
import de.xirp.settings.SettingsChangedEvent;
import de.xirp.settings.SettingsChangedListener;
import de.xirp.settings.SettingsPage;
import de.xirp.settings.IValue.SettingsState;
import de.xirp.settings.Option.OptionType;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.util.Constants;
import de.xirp.util.Util;

/**
 * A custom composite which shows the settings for the database
 * access.
 * 
 * @author Matthias Gernand
 */
public final class DBFolder extends AbstractContentFolderComposite {

	/**
	 * {@link de.xirp.settings.Option} for the driver.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option driver;
	/**
	 * {@link de.xirp.settings.Option} for the user name.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option user;
	/**
	 * {@link de.xirp.settings.Option} for the port
	 * number.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option port;
	/**
	 * {@link de.xirp.settings.Option} for the IP
	 * address.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option ip;
	/**
	 * {@link de.xirp.settings.Option} for the password.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option password;

	/**
	 * Constructs a new database folder.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	public DBFolder(XComposite parent) {
		super(parent, SWT.NONE);
		initSettings( );
	}

	/**
	 * Initializes the {@link de.xirp.settings.Settings}
	 * object and builds the UI from it.
	 */
	private void initSettings() {
		settings = new Settings("DBFolder"); //$NON-NLS-1$
		SettingsPage settingsPage = settings.addPage("gui", "databaseSettings", "databaseDescription"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		ip = settingsPage.addOption("ip", OptionType.TEXTFIELD); //$NON-NLS-1$
		ip.addStringValue("ip", "127.0.0.1").setSavedValue(PropertiesManager.getDbIP( )); //$NON-NLS-1$ //$NON-NLS-2$

		port = settingsPage.addOption("port", OptionType.TEXTFIELD); //$NON-NLS-1$
		port.addStringValue("port", "3306").setSavedValue(PropertiesManager.getDbPort( )); //$NON-NLS-1$ //$NON-NLS-2$

		user = settingsPage.addOption("user", OptionType.TEXTFIELD); //$NON-NLS-1$
		user.addStringValue("user", "root").setSavedValue(PropertiesManager.getDbUser( )); //$NON-NLS-1$ //$NON-NLS-2$

		String pwd = Util.decrypt(PropertiesManager.getDbPassword( ));
		password = settingsPage.addOption("password", OptionType.TEXTFIELD); //$NON-NLS-1$
		password.addPasswordValue("password", "").setSavedValue(pwd); //$NON-NLS-1$ //$NON-NLS-2$

		driver = settingsPage.addOption("databaseDriver", OptionType.COMBOBOX); //$NON-NLS-1$

		String value = null;
		for (int i = 0; i < Constants.DB_DRIVERS[0].length; i++) {
			if (Constants.DB_DRIVERS[0][i].equals(PropertiesManager.getDefaultDriver( ))) {
				value = Constants.DB_DRIVERS[0][i] + " | " //$NON-NLS-1$
						+ Constants.DB_DRIVERS[1][i];
			}
			driver.addNonTranslatableNamedBooleanValue(Constants.DB_DRIVERS[0][i] +
					" | " //$NON-NLS-1$
					+ Constants.DB_DRIVERS[1][i],
					SettingsState.NOT_SELECTED);
		}
		if (value != null) {
			driver.addNonTranslatableNamedBooleanValue(value,
					SettingsState.SELECTED);
		}

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
		IValue value = ip.getSelectedValue( );
		PropertiesManager.setDbIP(value.getDisplayValue( ));
		value = port.getSelectedValue( );
		PropertiesManager.setDbPort(value.getDisplayValue( ));
		value = user.getSelectedValue( );
		PropertiesManager.setDbUser(value.getDisplayValue( ));
		value = password.getSelectedValue( );
		PropertiesManager.setDbPassword(Util.encrypt(value.getDisplayValue( )
				.trim( )));
		value = driver.getSelectedValue( );
		PropertiesManager.setDefaultDriver(value.getDisplayValue( )
				.split("[|]")[0].trim( )); //$NON-NLS-1$	
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
