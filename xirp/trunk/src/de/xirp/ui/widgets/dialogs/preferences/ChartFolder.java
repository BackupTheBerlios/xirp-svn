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
 * ChartFolder.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 19.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.xirp.settings.*;
import de.xirp.settings.IValue.SettingsState;
import de.xirp.settings.Option.OptionType;

/**
 * A custom composite which shows check boxes for the live chart
 * export flags.
 * 
 * @author Matthias Gernand
 */
public class ChartFolder extends AbstractContentFolderComposite {

	/**
	 * {@link de.xirp.settings.Option} for the export PDF
	 * flag.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option exportPDF;
	/**
	 * {@link de.xirp.settings.Option} for the export PNG
	 * flag.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option exportPNG;
	/**
	 * {@link de.xirp.settings.Option} for the export JPG
	 * flag.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option exportJPG;
	/**
	 * {@link de.xirp.settings.Option} for the export CSV
	 * flag.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option exportCSV;

	/**
	 * Constructs a new chart folder.
	 * 
	 * @param parent
	 *            The parent.
	 */
	public ChartFolder(Composite parent) {
		super(parent, SWT.NONE);
		initSettings( );
	}

	/**
	 * Initializes the {@link de.xirp.settings.Settings}
	 * object and builds the UI from it.
	 */
	private void initSettings() {
		settings = new Settings("ChartFolder"); //$NON-NLS-1$
		SettingsPage settingsPage = settings.addPage("gui", "chartSettings", "chartSettingsDescription"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		exportPDF = settingsPage.addOption("exportPDF", OptionType.CHECKBOX); //$NON-NLS-1$
		exportPDF.addTranslatableNamedBooleanValue("expPDF", SettingsState.NOT_SELECTED).setSavedSelection(PropertiesManager.isExportPDF( )); //$NON-NLS-1$

		exportPNG = settingsPage.addOption("exportPNG", OptionType.CHECKBOX); //$NON-NLS-1$
		exportPNG.addTranslatableNamedBooleanValue("expPNG", SettingsState.NOT_SELECTED).setSavedSelection(PropertiesManager.isExportPNG( )); //$NON-NLS-1$

		exportJPG = settingsPage.addOption("exportJPEG", OptionType.CHECKBOX); //$NON-NLS-1$
		exportJPG.addTranslatableNamedBooleanValue("expJPG", SettingsState.NOT_SELECTED).setSavedSelection(PropertiesManager.isExportJPG( )); //$NON-NLS-1$

		exportCSV = settingsPage.addOption("exportCSV", OptionType.CHECKBOX); //$NON-NLS-1$
		exportCSV.addTranslatableNamedBooleanValue("expCSV", SettingsState.NOT_SELECTED).setSavedSelection(PropertiesManager.isExportCSV( )); //$NON-NLS-1$

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
		IValue value = exportPDF.getValues( ).get(0);
		PropertiesManager.setExportPDF(value.isSelected( ));

		value = exportPNG.getValues( ).get(0);
		PropertiesManager.setExportPNG(value.isSelected( ));

		value = exportJPG.getValues( ).get(0);
		PropertiesManager.setExportJPG(value.isSelected( ));

		value = exportCSV.getValues( ).get(0);
		PropertiesManager.setExportCSV(value.isSelected( ));
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
