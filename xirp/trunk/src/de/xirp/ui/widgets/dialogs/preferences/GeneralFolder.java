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
 * GeneralFolder.java
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

import de.xirp.settings.*;
import de.xirp.settings.Option.OptionType;
import de.xirp.ui.widgets.custom.XComposite;

/**
 * A custom composite which shows the settings for the database
 * access.
 * 
 * @author Matthias Gernand
 */
public final class GeneralFolder extends AbstractContentFolderComposite {

	/**
	 * {@link de.xirp.settings.Option} for the timer
	 * final value.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option timerFinal;
	/**
	 * {@link de.xirp.settings.Option} for the timer
	 * warning value.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option timerWaring;

	/**
	 * Constructs a new general folder composite for the given parent.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	public GeneralFolder(XComposite parent) {
		super(parent, SWT.NONE);
		initSettings( );
	}

	/**
	 * Initializes the {@link de.xirp.settings.Settings}
	 * object and builds the UI from it.
	 */
	private void initSettings() {
		settings = new Settings("GeneralFolder"); //$NON-NLS-1$
		SettingsPage settingsPage = settings.addPage("gui", "generalSystemSettings", "generalSystemSettingsDescription"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		timerWaring = settingsPage.addOption("timerWaring", OptionType.SPINNER); //$NON-NLS-1$
		timerWaring.addSpinnerValue(1000, 0, 500000, 60)
				.setSavedValue(PropertiesManager.getTimerWarning( ));

		timerFinal = settingsPage.addOption("timerFinal", OptionType.SPINNER); //$NON-NLS-1$
		timerFinal.addSpinnerValue(1200, 0, 500000, 60)
				.setSavedValue(PropertiesManager.getTimerFinal( ));

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
		IValue value = timerWaring.getSelectedValue( );
		if (value instanceof SpinnerValue) {
			SpinnerValue val = (SpinnerValue) value;
			PropertiesManager.setTimerWarning((int) val.getSavedSpinnerValue( ));
		}
		value = timerFinal.getSelectedValue( );
		if (value instanceof SpinnerValue) {
			SpinnerValue val = (SpinnerValue) value;
			PropertiesManager.setTimerFinal((int) val.getSavedSpinnerValue( ));
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
