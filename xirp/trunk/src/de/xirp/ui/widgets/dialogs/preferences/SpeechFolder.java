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
 * SpeechFolder.java
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
import de.xirp.speech.TextToSpeechManager;
import de.xirp.ui.util.ApplicationManager;

/**
 * A custom composite which shows the settings for the speech system.
 * 
 * @author Matthias Gernand
 */
public class SpeechFolder extends AbstractContentFolderComposite {

	/**
	 * {@link de.xirp.settings.Option} for the enabled
	 * flag..
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option enableTTS;
	/**
	 * {@link de.xirp.settings.Option} for the voice.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option TTSVoice;

	/**
	 * Constructs a new speech folder.
	 * 
	 * @param parent
	 *            The parent.
	 */
	public SpeechFolder(Composite parent) {
		super(parent, SWT.NONE);
		initSettings( );
	}

	/**
	 * Initializes the {@link de.xirp.settings.Settings}
	 * object and builds the UI from it.
	 */
	private void initSettings() {
		settings = new Settings("SpeechFolder"); //$NON-NLS-1$
		SettingsPage settingsPage = settings.addPage("gui", "speechSettings", "speechSettingsDescription"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		TTSVoice = settingsPage.addOption("TTSVoice", OptionType.COMBOBOX); //$NON-NLS-1$
		for (String voices : TextToSpeechManager.getAvailableVoices( )) {
			TTSVoice.addNonTranslatableNamedBooleanValue(voices,
					SettingsState.NOT_SELECTED);
		}
		String voice = PropertiesManager.getTTSVoice( );
		if (!TextToSpeechManager.isVoiceAvailable(voice)) {
			voice = "kevin"; //$NON-NLS-1$
		}
		TTSVoice.addNonTranslatableNamedBooleanValue(voice,
				SettingsState.SELECTED);

		enableTTS = settingsPage.addOption("enableTTS", OptionType.CHECKBOX); //$NON-NLS-1$
		enableTTS.addTranslatableNamedBooleanValue("entts", SettingsState.NOT_SELECTED).setSavedSelection(PropertiesManager.isEnableTTS( )); //$NON-NLS-1$

		constructUIFromSettings(settings);

		settings.addSettingsChangedListener(new SettingsChangedListener( ) {

			public void settingsChanged(SettingsChangedEvent event) {
				saveToIni( );
				ApplicationManager.voiceChanged( );
			}

		});

		finishInit( );

	}

	/**
	 * Initializes the {@link de.xirp.settings.Settings}
	 * object and builds the UI from it.
	 */
	private void saveToIni() {
		IValue value = TTSVoice.getSelectedValue( );
		PropertiesManager.setTTSVoice(value.getDisplayValue( ));

		value = enableTTS.getValues( ).get(0);
		PropertiesManager.setEnableTTS(value.isSelected( ));
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
	 * this folder supports resets.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#supportsReset()
	 */
	@Override
	public boolean supportsReset() {
		return true;
	}

}
