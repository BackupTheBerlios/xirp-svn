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
 * ValueChangedEvent.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 10.05.2006:		Created by Rabea Gransberger.
 */
package de.xirp.ui.event;

import java.util.EventObject;

/**
 * A value changed event containing the value as <code>int</code>
 * and <code>double</code> for more precision.
 * 
 * @author Rabea Gransberger
 */
public class ValueChangedEvent extends EventObject {

	/**
	 * The serialization ID.
	 */
	private static final long serialVersionUID = -419695286347202025L;
	/**
	 * The new value as <code>int</code>
	 */
	@SuppressWarnings("unused")
	private int value;

	/**
	 * The new value as double
	 */
	private double preciseValue;

	/**
	 * Creates a new event which contains the changed value.
	 * 
	 * @param source
	 *            The source
	 * @param value
	 *            The new value
	 */
	public ValueChangedEvent(Object source, int value) {
		super(source);
		this.value = value;
		this.preciseValue = value;
	}

	/**
	 * Creates a new event with a value of double precision.
	 * 
	 * @param source
	 *            The source
	 * @param value
	 *            The new value
	 */
	public ValueChangedEvent(Object source, double value) {
		super(source);
		this.value = (int) value;
		this.preciseValue = value;
	}

	/**
	 * The value as double
	 * 
	 * @return the preciseValue
	 */
	public double getPreciseValue() {
		return preciseValue;
	}

	/**
	 * The value as <code>int</code>.<br>
	 * Try {@linkplain #getPreciseValue()} to be sure to miss nothing
	 * 
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

}
