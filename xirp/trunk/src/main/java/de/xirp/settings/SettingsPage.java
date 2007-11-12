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
 * SettingsPage.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.SubsetConfiguration;

import de.xirp.settings.Option.OptionType;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * A page of a settings object. Settings consist of one or more
 * {@link SettingsPage}s, each with one or more {@link Option}s each
 * with one ore more {@link IValue values}.
 * 
 * @author Rabea Gransberger
 */
public class SettingsPage {

	/**
	 * The translation key for a short description of the page.<br>
	 * The key is of the form
	 * <code>[mainKey].[subKey].[shortDescriptionKey]</code>.
	 */
	private String shortDescriptionKey;
	/**
	 * The translation key for a long description of the page.<br>
	 * The key is of the form
	 * <code>[mainKey].[subKey].[longDescriptionKey]</code>.
	 */
	private String longDescriptionKey;
	/**
	 * The translation key for the name of page.<br>
	 * The key is of the form <code>[mainKey].[subKey]</code>.
	 */
	private String key;
	/**
	 * The configuration object
	 */
	private SubsetConfiguration sub = null;
	/**
	 * The bundle used for translation
	 */
	private II18nHandler bundle;
	/**
	 * The options of this page
	 */
	private Map<String, Option> options = new HashMap<String, Option>( );
	/**
	 * The order of the options
	 */
	private List<Option> optionOrder = new ArrayList<Option>( );
	/**
	 * The parent settings object of this page.
	 */
	protected Settings parentSettings;
	/**
	 * Arguments for the name key.
	 */
	private Object[] nameKeyArgs;
	/**
	 * Arguments for the short key.
	 */
	private Object[] shortKeyArgs;
	/**
	 * Arguments for the short key.
	 */
	private Object[] longKeyArgs;

	/**
	 * Constructs a new page of the preferences. Package protected so
	 * prevent calling directly.
	 * 
	 * @param parent
	 *            the parent settings of this page
	 * @param sub
	 *            The configuration object
	 * @param bundle
	 *            The bundle used for translation
	 * @param subKey
	 *            The translation key for the name of page.<br>
	 *            The key is of the form
	 *            <code>[mainKey].[subKey]</code>
	 * @param shortDescriptionKey
	 *            The translation key for a short description of the
	 *            page.<br>
	 *            The key is of the form
	 *            <code>[mainKey].[subKey].[shortDescriptionKey]</code>
	 * @param longDescriptionKey
	 *            The translation key for a long description of the
	 *            page.<br>
	 *            The key is of the form
	 *            <code>[mainKey].[subKey].[longDescriptionKey]</code>
	 */
	protected SettingsPage(Settings parent, SubsetConfiguration sub,
			II18nHandler bundle, String subKey, String shortDescriptionKey,
			String longDescriptionKey) {
		this.parentSettings = parent;
		this.sub = sub;
		this.key = subKey;
		this.shortDescriptionKey = shortDescriptionKey;
		this.longDescriptionKey = longDescriptionKey;
		this.bundle = bundle;
	}

	/**
	 * Sets the arguments to use when translating the long key.
	 * 
	 * @param longKeyArgs
	 *            the longKeyArgs to set
	 */
	public void setLongKeyArgs(Object... longKeyArgs) {
		this.longKeyArgs = longKeyArgs;
	}

	/**
	 * Sets the arguments to use when translating the page key.
	 * 
	 * @param pageKeyArgs
	 *            the pageKeyArgs to set
	 */
	public void setNameKeyArgs(Object... pageKeyArgs) {
		this.nameKeyArgs = pageKeyArgs;
	}

	/**
	 * Sets the arguments to use when translating the short key.
	 * 
	 * @param shortKeyArgs
	 *            the shortKeyArgs to set
	 */
	public void setShortKeyArgs(Object... shortKeyArgs) {
		this.shortKeyArgs = shortKeyArgs;
	}

	/**
	 * Adds an option to this page.
	 * 
	 * @param optionKey
	 *            the key for translating the name of this option.
	 *            <br>
	 *            If you have an option key of the form
	 *            <code>test.page1.option</code> you have to give
	 *            only <code>option</code> to this parameter,
	 *            because the other values come from the preferences
	 *            page.
	 * @param type
	 *            the type of this option, which may be
	 *            <code>null</code> if an other renderer is
	 *            specified
	 * @return the created option
	 */
	public Option addOption(String optionKey, OptionType type) {
		SubsetConfiguration sub2 = null;
		if (sub != null) {
			sub2 = new SubsetConfiguration(sub, optionKey, "."); //$NON-NLS-1$
		}
		if (options.containsKey(optionKey)) {
			throw new IllegalStateException(I18n.getString("Option key has to be unique.")); //$NON-NLS-1$
		}
		Option option = new Option(this, sub2, bundle, key + "." + optionKey, //$NON-NLS-1$
				type);
		options.put(optionKey, option);
		optionOrder.add(option);
		return option;
	}

	/**
	 * Get's the short description of this page.<br>
	 * That is the value for the key of the form
	 * <code>[mainKey].[subKey].[shortDescriptionKey]</code>
	 * 
	 * @return the translated name of this page
	 */
	public String getShortDescription() {
		if (shortKeyArgs == null) {
			return bundle.getString(shortDescriptionKey);
		}
		return bundle.getString(shortDescriptionKey, shortKeyArgs);
	}

	/**
	 * Get's the translated name of this page.<br>
	 * That is the value for the key of the form
	 * <code>[mainKey].[subKey].[longDescriptionKey]</code>
	 * 
	 * @return the translated name of this page
	 */
	public String getLongDescription() {
		if (longKeyArgs == null) {
			return bundle.getString(longDescriptionKey);
		}
		return bundle.getString(shortDescriptionKey, longKeyArgs);
	}

	/**
	 * Gets the handler used for translation by this page.
	 * 
	 * @return the handler used for translation
	 */
	public II18nHandler getI18n() {
		return bundle;
	}

	/**
	 * Gets the key for the long description.
	 * 
	 * @return the longDescriptionKey
	 */
	public String getLongDescriptionKey() {
		return longDescriptionKey;
	}

	/**
	 * Gets the key for the short description.
	 * 
	 * @return the shortDescriptionKey
	 */
	public String getShortDescriptionKey() {
		return shortDescriptionKey;
	}

	/**
	 * Gets the arguments to use when translating the long key.
	 * 
	 * @return the longKeyArgs
	 */
	public Object[] getLongKeyArgs() {
		return longKeyArgs;
	}

	/**
	 * Gets the arguments to use when translating the page key.
	 * 
	 * @return the pageKeyArgs
	 */
	public Object[] getNameKeyArgs() {
		return nameKeyArgs;
	}

	/**
	 * Gets the arguments to use when translating short key.
	 * 
	 * @return the shortKeyArgs
	 */
	public Object[] getShortKeyArgs() {
		return shortKeyArgs;
	}

	/**
	 * Get's the translated name of this page.<br>
	 * That is the value for the key of the form
	 * <code>[mainKey].[subKey]</code>
	 * 
	 * @return the translated name of this page
	 */
	public String getName() {
		if (nameKeyArgs == null) {
			return bundle.getString(key);
		}
		return bundle.getString(key, nameKeyArgs);
	}

	/**
	 * Gets the key for the name of the page.
	 * 
	 * @return key for the name of the page
	 */
	public String getNameKey() {
		return key;
	}

	/**
	 * Saves the changed parts of the page if any values have changed.
	 * 
	 * @param toFile
	 *            <code>true</code> if the changes should be
	 *            persisted. Otherwise only the states of the values
	 *            will change.
	 * @return <code>true</code> if any changes had been saved
	 */
	public boolean save(boolean toFile) {
		boolean saved = false;
		for (Option option : optionOrder) {
			saved = option.save(toFile) || saved;
		}
		return saved;
	}

	/**
	 * Reset the changes made to this page to the saved values.<br>
	 * The UI will be notified for every reset.
	 */
	public void reset() {
		for (Option option : optionOrder) {
			option.reset( );
		}
	}

	/**
	 * Restores the default values for this pages options.
	 */
	public void restoreDefaults() {
		for (Option option : optionOrder) {
			option.restoreDefaults( );
		}
	}

	/**
	 * Checks if anything on the page has changed.
	 * 
	 * @return <code>true</code> if any of the values of this page
	 *         had changed
	 */
	public boolean hasChanged() {
		// Only check until one of the options have changes. This is
		// faster
		// than checking all till the end
		boolean changed = false;
		for (Iterator<Option> it = optionOrder.iterator( ); it.hasNext( ) &&
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
		// Only check until one of the pages have changes. This is
		// faster
		// than checking all till the end
		boolean defAult = true;
		for (Iterator<Option> it = optionOrder.iterator( ); it.hasNext( ) &&
				defAult;) {
			defAult &= it.next( ).isDefaultSelected( );
		}
		return defAult;
	}

	/**
	 * Gets a list of all the options of this page in the order in
	 * which they were added to the page.
	 * 
	 * @return the unmodifiable list of options
	 */
	public Collection<Option> getOptions() {
		return Collections.unmodifiableCollection(optionOrder);
	}

	/**
	 * Gets the option for the given key.
	 * 
	 * @param nameKey
	 *            the key to get the option for. This is the same key
	 *            as provided to
	 *            {@link #addOption(String, OptionType)}.
	 * @return the option for the key or <code>null</code> if no
	 *         option was found for the given key
	 */
	public Option getOption(String nameKey) {
		return options.get(nameKey);
	}

}
