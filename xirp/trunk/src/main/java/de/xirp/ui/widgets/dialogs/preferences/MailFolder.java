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
 * MailFolder.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.02.2007:		Created by Matthias Gernand.
 * 21.04.2007:		Added no-reply address option.
 */
package de.xirp.ui.widgets.dialogs.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.xirp.settings.IValue;
import de.xirp.settings.Option;
import de.xirp.settings.PropertiesManager;
import de.xirp.settings.Settings;
import de.xirp.settings.SettingsChangedEvent;
import de.xirp.settings.SettingsChangedListener;
import de.xirp.settings.SettingsPage;
import de.xirp.settings.IValue.SettingsState;
import de.xirp.settings.Option.OptionType;
import de.xirp.util.Util;

/**
 * A custom composite which shows the settings for the mail system.
 * 
 * @author Matthias Gernand
 */
public class MailFolder extends AbstractContentFolderComposite {

	/**
	 * {@link de.xirp.settings.Option} for the host.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option smtpHost;
	/**
	 * {@link de.xirp.settings.Option} for the port.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option smtpPort;
	/**
	 * {@link de.xirp.settings.Option} for the
	 * authentication flag.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option needsAuth;
	/**
	 * {@link de.xirp.settings.Option} for the user name.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option userName;
	/**
	 * {@link de.xirp.settings.Option} for the password.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option password;
	/**
	 * {@link de.xirp.settings.Option} for the no-reply
	 * address.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option noReply;

	/**
	 * Constructs a new mail folder.
	 * 
	 * @param parent
	 *            The parent.
	 */
	public MailFolder(Composite parent) {
		super(parent, SWT.NONE);
		initSettings( );
	}

	/**
	 * Initializes the {@link de.xirp.settings.Settings}
	 * object and builds the UI from it.
	 */
	private void initSettings() {
		settings = new Settings("MailFolder"); //$NON-NLS-1$
		SettingsPage settingsPage = settings.addPage("gui", "mailSettings", "mailDescription"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		smtpHost = settingsPage.addOption("host", OptionType.TEXTFIELD); //$NON-NLS-1$
		smtpHost.addStringValue("host", "127.0.0.1").setSavedValue(PropertiesManager.getSmtpHost( )); //$NON-NLS-1$ //$NON-NLS-2$

		smtpPort = settingsPage.addOption("port", OptionType.COMBOBOX); //$NON-NLS-1$
		int port = PropertiesManager.getSmtpPort( );
		smtpPort.addNonTranslatableNamedBooleanValue("25", SettingsState.NOT_SELECTED); //$NON-NLS-1$
		smtpPort.addNonTranslatableNamedBooleanValue("465", SettingsState.NOT_SELECTED); //$NON-NLS-1$
		smtpPort.addNonTranslatableNamedBooleanValue(Integer.toString(port), SettingsState.SELECTED);
		
		needsAuth = settingsPage.addOption("auth", OptionType.CHECKBOX); //$NON-NLS-1$
		needsAuth.addTranslatableNamedBooleanValue("auth", SettingsState.NOT_SELECTED).setSavedSelection(PropertiesManager.isNeedsAuthentication( )); //$NON-NLS-1$

		userName = settingsPage.addOption("user", OptionType.TEXTFIELD); //$NON-NLS-1$
		userName.addStringValue("user", "").setSavedValue(PropertiesManager.getSmtpUser( )); //$NON-NLS-1$ //$NON-NLS-2$

		String pwd = Util.decrypt(PropertiesManager.getSmtpPassword( ));
		password = settingsPage.addOption("password", OptionType.TEXTFIELD); //$NON-NLS-1$
		password.addPasswordValue("password", "").setSavedValue(pwd); //$NON-NLS-1$ //$NON-NLS-2$

		noReply = settingsPage.addOption("noReply", OptionType.TEXTFIELD); //$NON-NLS-1$
		noReply.addStringValue("noReply", "").setSavedValue(PropertiesManager.getNoReplyAddress( )); //$NON-NLS-1$ //$NON-NLS-2$

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
		IValue value = smtpHost.getSelectedValue( );
		PropertiesManager.setSmtpHost(value.getDisplayValue( ));
		value = smtpPort.getSelectedValue( );
		PropertiesManager.setSmtpPort(Integer.parseInt(value.getDisplayValue( )));
		value = userName.getSelectedValue( );
		PropertiesManager.setSmtpUser(value.getDisplayValue( ));
		value = needsAuth.getValues( ).get(0);
		PropertiesManager.setNeedsAuthentication(value.isSelected( ));
		value = password.getSelectedValue( );
		PropertiesManager.setSmtpPassword(Util.encrypt(value.getDisplayValue( )
				.trim( )));
		value = noReply.getSelectedValue( );
		PropertiesManager.setNoReplyAddress(value.getDisplayValue( ));
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
