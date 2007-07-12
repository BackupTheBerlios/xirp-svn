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
 * StringValue.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 30.07.2006:		Created by Rabea Gransberger.
 */
package de.xirp.settings;

import de.xirp.util.II18nHandler;

/**
 * A preferences value with an internationalizable string.
 * 
 * @author Rabea Gransberger
 */
public class StringValue extends AbstractValue<String> {

	/**
	 * The key for internationalization
	 */
	private String key;

	/**
	 * Key for translation.
	 */
	private String translationKey;
	/**
	 * The bundle for internationalization which has to contain the
	 * key
	 */
	private II18nHandler handler;
	/**
	 * <code>true</code> if the string should not be translated.
	 */
	private boolean constant;
	/**
	 * Arguments for translation.
	 */
	private Object[] keyArgs;

	/**
	 * Constructs a new value.
	 * 
	 * @param handler
	 *            The bundle for internationalization which has to
	 *            contain the key
	 * @param translationKey
	 * @param key
	 *            The key for internationalization
	 * @param state
	 *            the state of the value
	 * @param constant
	 *            <code>true</code> if the key should not be
	 *            translated.
	 * @param keyArgs
	 */
	protected StringValue(II18nHandler handler, String translationKey,
			String key, SettingsState state, boolean constant,
			Object... keyArgs) {
		super(null, state);
		this.constant = constant;
		this.key = key;
		this.handler = handler;
		this.translationKey = translationKey;
		this.keyArgs = keyArgs;
	}

	/**
	 * Translates the key with the bundle or returns the constant
	 * string.
	 * 
	 * @return the translated value
	 */
	public String getDisplayValue() {
		if (constant) {
			return key;
		}
		return handler.getString(translationKey, keyArgs);
	}

	/**
	 * Gets the key for for saving language independent.
	 * 
	 * @return the key for translation
	 */
	public String getSaveValue() {
		return key;
	}

	/**
	 * Gets the key used for translation.
	 * 
	 * @return the key or <code>null</code> if this is a constant
	 *         value.
	 */
	@Override
	public String getKey() {
		if (constant) {
			return null;
		}
		return translationKey;
	}

	/**
	 * Gets the arguments used for translation.
	 * 
	 * @return the arguments or <code>null</code> if no arguments
	 *         were given.
	 */
	@Override
	public Object[] getKeyArgs() {
		if (constant) {
			return null;
		}
		return keyArgs;
	}

	/**
	 * @see de.xirp.settings.IValue#getSaveKey()
	 */
	@Override
	public String getSaveKey() {
		return key;
	}
}
