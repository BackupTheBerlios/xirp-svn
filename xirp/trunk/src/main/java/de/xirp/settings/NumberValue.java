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
 * NumberValue.java
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

/**
 * A preferences value type for constant numbers.
 * 
 * @author Rabea Gransberger
 */
public class NumberValue extends AbstractValue<Number> {

	/**
	 * Constructs a new value.
	 * 
	 * @param value
	 *            the number value
	 * @param state
	 *            the state of the value
	 */
	protected NumberValue(Number value, SettingsState state) {
		super(value, state);
	}

	/**
	 * @return {@link Number#toString()}
	 * @see de.xirp.settings.IValue#getDisplayValue()
	 */
	public String getDisplayValue() {
		return value.toString( );
	}

	/**
	 * @return {@link Number#toString()}
	 * @see de.xirp.settings.IValue#getSaveValue()
	 */
	public String getSaveValue() {
		return value.toString( );
	}

	/**
	 * @see de.xirp.settings.IValue#getSaveKey()
	 */
	@Override
	public String getSaveKey() {
		return value.toString( );
	}
}
