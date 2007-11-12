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
 * Settings.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 29.07.2006:		Created by Rabea Gransberger.
 */
package de.xirp.settings;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SubsetConfiguration;
import org.apache.log4j.Logger;

import de.xirp.settings.IValue.SettingsState;
import de.xirp.util.Constants;
import de.xirp.util.GenericI18n;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * Data structure for showing preferences supporting translations and
 * defaults. Settings consist of one or more {@link SettingsPage}s,
 * each with one or more {@link Option}s each with one ore more
 * {@link IValue values}.
 * 
 * @author Rabea Gransberger
 */
public class Settings {

	/**
	 * The Logger of this class.
	 */
	private static Logger logClass = Logger.getLogger(Settings.class);

	/**
	 * The main-key of this preferences. The translations and saved
	 * values will start with this key.
	 */
	private String mainKey;
	/**
	 * The unique key of this particular preferences object to
	 * distinguish more than one independent preferences of the same
	 * type. It is used for saving the values only.
	 */
	private String uniqueKey;
	/**
	 * The resource bundle used for translations
	 */
	private II18nHandler handler;
	/**
	 * The properties used for saving the preferences
	 */
	private PropertiesConfiguration config;
	/**
	 * The pages of this preferences
	 */
	private Vector<SettingsPage> pages = new Vector<SettingsPage>( );
	/**
	 * The listeners waiting for saved changes of this preferences
	 */
	private Vector<SettingsChangedListener> listeners = new Vector<SettingsChangedListener>( );
	/**
	 * Flag if the settings should be persists when calling
	 * {@link #save()} or if it should only notify the listeners so
	 * that they can persist the settings on their own.
	 */
	private boolean doSave = true;

	/**
	 * Listener which is added to all the values to listen for changes
	 * and notify all local changed listeners.
	 */
	private SettingsChangedListener immediateChangeValueListener;

	/**
	 * The listeners waiting for changes of this preferences. These
	 * listeners are notified immediately, without waiting for them to
	 * be saved.
	 */
	private Vector<SettingsChangedListener> immediateChangeListeners = new Vector<SettingsChangedListener>( );

	/**
	 * Creates a new preferences object.
	 * 
	 * @param props
	 *            The properties used for saving the preferences
	 * @param mainKey
	 *            The main-key of this preferences. The translations
	 *            and saved values will start with this key
	 * @param uniqueKey
	 *            The unique key of this particular preferences object
	 *            to distinguish more than one independent preferences
	 *            of the same type. It is used for saving the values
	 *            only.
	 * @param handler
	 *            The handler used for translations
	 */
	public Settings(PropertiesConfiguration props, II18nHandler handler,
			String mainKey, String uniqueKey) {
		this.mainKey = mainKey;
		this.uniqueKey = uniqueKey;
		this.handler = handler;
		this.config = props;
	}

	/**
	 * Creates a new preferences object, which uses the default
	 * translator of the application.
	 * 
	 * @param props
	 *            The properties used for saving the preferences
	 * @param mainKey
	 *            The main-key of this preferences. The translations
	 *            and saved values will start with this key
	 * @param uniqueKey
	 *            The unique key of this particular preferences object
	 *            to distinguish more than one independent preferences
	 *            of the same type. It is used for saving the values
	 *            only.
	 */
	public Settings(PropertiesConfiguration props, String mainKey,
			String uniqueKey) {
		this.mainKey = mainKey;
		this.uniqueKey = uniqueKey;
		this.handler = I18n.getGenericI18n( );
		this.config = props;
	}

	/**
	 * Creates a new preferences object, which is not saved when
	 * {@link #save} is called but does only notify the listeners.
	 * 
	 * @param mainKey
	 *            The main-key of this preferences. The translations
	 *            and saved values will start with this key
	 * @param uniqueKey
	 *            The unique key of this particular preferences object
	 *            to distinguish more than one independent preferences
	 *            of the same type. It is used for saving the values
	 *            only.
	 * @param handler
	 *            The handler used for translations
	 */
	public Settings(II18nHandler handler, String mainKey, String uniqueKey) {
		this.mainKey = mainKey;
		this.uniqueKey = uniqueKey;
		this.handler = handler;
		this.doSave = false;
	}

	/**
	 * Creates a new preferences object using the default translator
	 * of this application. This object is not saved automatically
	 * when {@link #save} is called but does only notify the
	 * listeners.
	 * 
	 * @param mainKey
	 *            The main-key of this preferences. The translations
	 *            and saved values will start with this key
	 * @param uniqueKey
	 *            The unique key of this particular preferences object
	 *            to distinguish more than one independent preferences
	 *            of the same type. It is used for saving the values
	 *            only.
	 */
	public Settings(String mainKey, String uniqueKey) {
		this.mainKey = mainKey;
		this.uniqueKey = uniqueKey;
		this.handler = I18n.getGenericI18n( );
		this.doSave = false;
	}

	/**
	 * Creates a new preferences object, which is not saved when
	 * {@link #save} is called but does only notify the listeners. The
	 * unique key is empty.
	 * 
	 * @param mainKey
	 *            The main-key of this preferences. The translations
	 *            and saved values will start with this key
	 * @param handler
	 *            The handler used for translations
	 */
	public Settings(II18nHandler handler, String mainKey) {
		this.mainKey = mainKey;
		this.uniqueKey = ""; //$NON-NLS-1$
		this.handler = handler;
		this.doSave = false;
	}

	/**
	 * Creates a new preferences object, which is not saved when
	 * {@link #save} is called but does only notify the listeners. It
	 * uses the default translator of the application and an empty
	 * unique key.
	 * 
	 * @param mainKey
	 *            The main-key of this preferences. The translations
	 *            and saved values will start with this key
	 */
	public Settings(String mainKey) {
		this.mainKey = mainKey;
		this.uniqueKey = ""; //$NON-NLS-1$
		this.handler = I18n.getGenericI18n( );
		this.doSave = false;
	}

	/**
	 * Check if this settings are saved automatically when calling
	 * {@link #save} or if it does only notify the listeners.
	 * 
	 * @return <code>true</code> if changes are saved automatically.
	 */
	public boolean isAutoSave() {
		return doSave;
	}

	/**
	 * Adds a new page to this preferences.<br>
	 * The resource bundle and properties for saving are inherited
	 * from this preferences object.
	 * 
	 * @param subKey
	 *            the sub-key used for translation and saving.<br>
	 *            If you have some preferences called
	 *            <code>test</code> and a page called
	 *            <code>page1</code>, then the key for saving is
	 *            <code>test.[uniqueyKey].page1</code> and the key
	 *            for translation is <code>test.page1</code>.
	 * @param shortDescriptionKey
	 *            The translation key for a short description of the
	 *            page.<br>
	 *            The key in the file has to be
	 *            <code>[mainKey].[subKey].[shortDescriptionKey]</code>
	 * @param longDescriptionKey
	 *            The translation key for a long description of the
	 *            page.<br>
	 *            The key in the file has to be
	 *            <code>[mainKey].[subKey].[longDescriptionKey]</code>
	 * @return the created page
	 */
	public SettingsPage addPage(String subKey, String shortDescriptionKey,
			String longDescriptionKey) {

		initListener( );

		SubsetConfiguration sub = null;
		if (config != null) {
			sub = new SubsetConfiguration(config, mainKey + "." //$NON-NLS-1$
					+ uniqueKey + "." + subKey, "."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		SettingsPage page = new SettingsPage(this,
				sub,
				handler,
				mainKey + "." //$NON-NLS-1$
						+ subKey,
				mainKey +
						"." + subKey + (shortDescriptionKey != null ? "." + shortDescriptionKey : ""), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				(longDescriptionKey != null ? mainKey +
						"." + subKey + "." + longDescriptionKey : null)); //$NON-NLS-1$ //$NON-NLS-2$
		pages.add(page);
		return page;
	}

	/**
	 * Initializes the immediate changed listener if it's not already
	 * initialized. This listener is added to the values to be
	 * notified if the value changes.
	 */
	private void initListener() {
		if (immediateChangeValueListener == null) {
			immediateChangeValueListener = new SettingsChangedListener( ) {

				public void settingsChanged(SettingsChangedEvent event) {
					for (SettingsChangedListener listener : immediateChangeListeners) {
						listener.settingsChanged(event);
					}
				}

			};
		}

	}

	/**
	 * Saves the changed parts of the preferences and fires a
	 * {@link SettingsChangedEvent} if the preferences had really
	 * changed. The preferences are only persisted if properties for
	 * saving were given.
	 */
	public void save() {
		// remember if anything has changed which has to be saved
		// it's faster than checking previously with hasChanged
		boolean saved = false;
		for (SettingsPage page : pages) {
			saved |= page.save(doSave);
		}

		// if anything has changed save the preferences to the file
		// and fire the changed event
		if (saved) {
			fireChangedEvent(new SettingsChangedEvent(this));
			if (doSave) {
				try {
					config.save( );
				}
				catch (ConfigurationException e) {
					logClass.debug("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Checks if anything of the preferences has changed.
	 * 
	 * @return <code>true</code> if any of the values of this
	 *         preferences had changed
	 */
	public boolean hasChanged() {
		// Only check until one of the pages have changes. This is
		// faster
		// than checking all till the end
		boolean changed = false;

		for (Iterator<SettingsPage> it = pages.iterator( ); it.hasNext( ) &&
				!changed;) {
			changed |= it.next( ).hasChanged( );
		}
		return changed;
	}

	/**
	 * Checks if the defaults are selected everywhere.
	 * 
	 * @return <code>true</code> if the defaults are selected at
	 *         every value.
	 */
	public boolean isDefaultSelected() {
		// Only check until we reach one page which has
		// not its default set
		boolean defAult = true;
		for (Iterator<SettingsPage> it = pages.iterator( ); it.hasNext( ) &&
				defAult;) {
			defAult &= it.next( ).isDefaultSelected( );
		}
		return defAult;
	}

	/**
	 * Resets the changes made to this preferences to the saved
	 * values.<br>
	 * The UI will be notified for every reset.
	 */
	public void reset() {
		for (SettingsPage page : pages) {
			page.reset( );
		}
	}

	/**
	 * Restores the default values for this preferences options.
	 */
	public void restoreDefaults() {
		for (SettingsPage page : pages) {
			page.restoreDefaults( );
		}
	}

	/**
	 * Adds a listener to the list of listeners which will be notified
	 * if the settings were changed and saved.
	 * 
	 * @param listener
	 *            Listener to add to list
	 */
	public void addSettingsChangedListener(SettingsChangedListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the given listener from the list of listeners which are
	 * informed when the settings changed.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeSettingsChangedListener(SettingsChangedListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Adds a listener which is notified immediately when the settings
	 * have changed even if they are not yet saved.
	 * 
	 * @param listener
	 *            the listener to add to the list of listeners
	 */
	public void addImmediateChangeListener(SettingsChangedListener listener) {
		immediateChangeListeners.add(listener);
	}

	/**
	 * Removes the listener which was notified immediately when the
	 * settings had changed even if they were not saved.
	 * 
	 * @param listener
	 *            the listener to remove from the list of listeners
	 */
	public void removeImmediateChangeListener(SettingsChangedListener listener) {
		immediateChangeListeners.remove(listener);
	}

	/**
	 * Calls settingsChanged for each Listener.
	 * 
	 * @param event
	 *            Event which informs about the changed settings
	 */
	private void fireChangedEvent(SettingsChangedEvent event) {
		for (SettingsChangedListener listener : listeners) {
			listener.settingsChanged(event);
		}
	}

	/**
	 * Gets a list of all the pages of this preferences.
	 * 
	 * @return the unmodifiable list of pages
	 */
	public List<SettingsPage> getPages() {
		return Collections.unmodifiableList(pages);
	}

	/**
	 * Gets the handler which should be used to translate all the keys
	 * of this preferences.
	 * 
	 * @return the handler
	 */
	public II18nHandler getI18nHandler() {
		return handler;
	}

	/**
	 * Gets the listener which should be added to each value of this
	 * preferences to be notified when anything changes.
	 * 
	 * @return a listener
	 */
	protected SettingsChangedListener getImmediateChangeValueListener() {
		return immediateChangeValueListener;
	}

	private static void addCheckboxOption(Settings settings) {
		SettingsPage settingsPage = settings.addPage("checkpage",
				"shortdescription",
				"description");

		Option option = settingsPage.addOption("checkbox",
				Option.OptionType.CHECKBOX);
		try {
			option.addNonTranslatableNamedBooleanValue("Nicht übersetzbar",
					SettingsState.SELECTED);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addNumberValue(30.56, SettingsState.SELECTED);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addPasswordValue("somekey", "PasswordValue");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addRGBValue("somergb", 255, 255, 0);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addSpinnerValue(20.5, -10, 20, 1);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addStringValue("somestrg", "Freier String");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addTranslatableNamedBooleanValue("value1",
					SettingsState.SELECTED,
					"Ein Argument");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
	}

	private static void addRadioOption(Settings settings) {
		SettingsPage settingsPage = settings.addPage("radiopage",
				"shortdescription",
				"description");

		Option option = settingsPage.addOption("radio",
				Option.OptionType.RADIOBUTTON);
		try {
			option.addNonTranslatableNamedBooleanValue("Nicht übersetzbar",
					SettingsState.SELECTED);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addNumberValue(30.56, SettingsState.SELECTED);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addPasswordValue("somekey", "PasswordValue");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addRGBValue("somergb", 255, 255, 0);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addSpinnerValue(20.5, -10, 20, 1);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addStringValue("somestrg", "Freier String");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addTranslatableNamedBooleanValue("value1",
					SettingsState.SELECTED,
					"Ein Argument");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
	}

	private static void addComboOption(Settings settings) {
		SettingsPage settingsPage = settings.addPage("combopage",
				"shortdescription",
				"description");

		Option option = settingsPage.addOption("combo",
				Option.OptionType.COMBOBOX);
		try {
			option.addNonTranslatableNamedBooleanValue("Nicht übersetzbar",
					SettingsState.SELECTED);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addNumberValue(30.56, SettingsState.SELECTED);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addPasswordValue("somekey", "PasswordValue");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addRGBValue("somergb", 255, 255, 0);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addSpinnerValue(20.5, -10, 20, 1);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addStringValue("somestrg", "Freier String");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		try {
			option.addTranslatableNamedBooleanValue("value1",
					SettingsState.SELECTED,
					"Ein Argument");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
	}

	private static void addTextfieldOptions(Settings settings) {
		SettingsPage settingsPage = settings.addPage("textpage",
				"shortdescription",
				"description");

		Option option = settingsPage.addOption("textfield",
				Option.OptionType.TEXTFIELD);
		try {
			option.addNonTranslatableNamedBooleanValue("Nicht übersetzbar",
					SettingsState.SELECTED);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}

		option = settingsPage.addOption("textfield2",
				Option.OptionType.TEXTFIELD);
		try {
			option.addNumberValue(30.56, SettingsState.SELECTED);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("textfield3",
				Option.OptionType.TEXTFIELD);
		try {
			option.addPasswordValue("somekey", "PasswordValue");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("textfield4",
				Option.OptionType.TEXTFIELD);
		try {
			option.addRGBValue("somergb", 255, 255, 0);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("textfield5",
				Option.OptionType.TEXTFIELD);
		try {
			option.addSpinnerValue(20.5, -10, 20, 1);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("textfield6",
				Option.OptionType.TEXTFIELD);
		try {
			option.addStringValue("somestrg", "Freier String");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("textfield7",
				Option.OptionType.TEXTFIELD);
		try {
			option.addTranslatableNamedBooleanValue("value1",
					SettingsState.SELECTED,
					"Ein Argument");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
	}

	private static void addColorOptions(Settings settings) {
		SettingsPage settingsPage = settings.addPage("colorpage",
				"shortdescription",
				"description");

		Option option = settingsPage.addOption("color", Option.OptionType.COLOR);
		try {
			option.addNonTranslatableNamedBooleanValue("Nicht übersetzbar",
					SettingsState.SELECTED);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("color2", Option.OptionType.COLOR);
		try {
			option.addNumberValue(30.56, SettingsState.SELECTED);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("color3", Option.OptionType.COLOR);
		try {
			option.addPasswordValue("somekey", "PasswordValue");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("color4", Option.OptionType.COLOR);
		try {
			option.addRGBValue("somergb", 255, 255, 0);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("color5", Option.OptionType.COLOR);
		try {
			option.addSpinnerValue(20.5, -10, 20, 1);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("color6", Option.OptionType.COLOR);
		try {
			option.addStringValue("somestrg", "Freier String");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("color7", Option.OptionType.COLOR);
		try {
			option.addTranslatableNamedBooleanValue("value1",
					SettingsState.SELECTED,
					"Ein Argument");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
	}

	private static void adSpinnerOptions(Settings settings) {
		SettingsPage settingsPage = settings.addPage("spinnerpage",
				"shortdescription",
				"description");

		Option option = settingsPage.addOption("spinner",
				Option.OptionType.SPINNER);
		try {
			option.addNonTranslatableNamedBooleanValue("Nicht übersetzbar",
					SettingsState.SELECTED);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("spinner2", Option.OptionType.SPINNER);
		try {
			option.addNumberValue(30.56, SettingsState.SELECTED);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("spinner3", Option.OptionType.SPINNER);
		try {
			option.addPasswordValue("somekey", "PasswordValue");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("spinner4", Option.OptionType.SPINNER);
		try {
			option.addRGBValue("somergb", 255, 255, 0);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("spinner5", Option.OptionType.SPINNER);
		try {
			option.addSpinnerValue(20.5, -10, 20, 1);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("spinner5a", Option.OptionType.SPINNER);
		try {
			option.addSpinnerValue(15, -10, 20, 1);
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("spinner6", Option.OptionType.SPINNER);
		try {
			option.addStringValue("somestrg", "Freier String");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
		option = settingsPage.addOption("spinner7", Option.OptionType.SPINNER);
		try {
			option.addTranslatableNamedBooleanValue("value1",
					SettingsState.SELECTED,
					"Ein Argument");
		}
		catch (IllegalStateException e) {
			e.printStackTrace( );
		}
	}

	/**
	 * Gets the example settings which provide one page for each
	 * option type, and each value for each option type.
	 * 
	 * @return example settings
	 */
	public static Settings getExampleSettings() {
		final GenericI18n genericI18n = new GenericI18n(Settings.class.getPackage( )
				.getName( ) +
				".messages"); //$NON-NLS-1$
		genericI18n.setLocale(I18n.getLocale( ));
		Settings settings = new Settings(genericI18n, "Example.settings.main"); //$NON-NLS-1$

		addCheckboxOption(settings);
		addRadioOption(settings);
		addComboOption(settings);
		addColorOptions(settings);
		addTextfieldOptions(settings);
		adSpinnerOptions(settings);

		return settings;

	}
}
