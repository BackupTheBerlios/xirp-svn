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
 * SpinnerValue.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 30.11.2006:		Created by Rabea Gransberger.
 */
package de.xirp.settings;

/**
 * Definitions for a spinner.
 * 
 * @author Rabea Gransberger
 */
public class SpinnerValue extends FreeValue {

	/**
	 * Minimum value for the spinner
	 */
	private double min;
	/**
	 * Maximum value for the spinner
	 */
	private double max;
	/**
	 * Step for the spinner
	 */
	private int step;
	/**
	 * Flag if doubles may be used.
	 */
	private boolean useDouble = false;

	/**
	 * Constructs a new value for displaying in a spinner.
	 * 
	 * @param value
	 *            the default value
	 * @param min
	 *            the minimum value for the spinner
	 * @param max
	 *            the maximum value for the spinner
	 * @param step
	 *            the step size for the spinner
	 */
	protected SpinnerValue(int value, int min, int max, int step) {
		super("key", String.valueOf(value), String.valueOf(value)); //$NON-NLS-1$
		this.min = min;
		this.max = max;
		this.step = step;
	}

	/**
	 * Constructs a new value for displaying in a spinner. A renderer
	 * for this spinner should be able to display double values.
	 * 
	 * @param value
	 *            the default value
	 * @param min
	 *            the minimum value for the spinner
	 * @param max
	 *            the maximum value for the spinner
	 * @param step
	 *            the step size for the spinner
	 */
	protected SpinnerValue(double value, double min, double max, int step) {
		super("key", String.valueOf(value), String.valueOf(value)); //$NON-NLS-1$
		this.min = min;
		this.max = max;
		this.step = step;
		this.useDouble = true;
	}

	/**
	 * Gets the maximum value for the spinner.
	 * 
	 * @return the maximum value
	 */
	public double getMax() {
		return max;
	}

	/**
	 * Gets the minimum value for the spinner.
	 * 
	 * @return the minimum value
	 */
	public double getMin() {
		return min;
	}

	/**
	 * Gets the step value for the spinner.
	 * 
	 * @return the step value
	 */
	public int getStep() {
		return step;
	}

	/**
	 * Gets the saved value of the spinner.
	 * 
	 * @return the saved value
	 */
	public double getSavedSpinnerValue() {
		return getInt(savedValue);
	}

	/**
	 * Gets the current value of the spinner.
	 * 
	 * @return the current value
	 */
	public double getCurrentSpinnerValue() {
		return getInt(currentValue);
	}

	/**
	 * Gets the default value of the spinner.
	 * 
	 * @return the default value
	 */
	public double getDefaultSpinnerValue() {
		return getInt(defaultValue);
	}

	/**
	 * Parses an <code>int</code> from the given string.
	 * 
	 * @param strg
	 *            the string to parse.
	 * @return the <code>int</code> parsed from the string or
	 *         <code>-1</code> if an exception occurred.
	 */
	private double getInt(String strg) {
		try {
			return Double.parseDouble(strg);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Sets the saved value for the spinner.
	 * 
	 * @param value
	 *            the saved value
	 */
	public void setSavedValue(double value) {
		super.setSavedValue(String.valueOf(value));
	}

	/**
	 * Sets the current value for the spinner.
	 * 
	 * @param newValue
	 *            the current value to set
	 * @param fromUI
	 *            <code>true</code> if the value was set from the
	 *            UI.
	 */
	public void setValue(double newValue, boolean fromUI) {
		super.setValue(String.valueOf(newValue), fromUI);
	}

	/**
	 * Sets the current value for the spinner.
	 * 
	 * @param newValue
	 *            the new value to set.
	 */
	public void setValue(double newValue) {
		setValue(newValue, false);
	}

	/**
	 * Checks if the UI displaying this spinner has to be capable of
	 * showing doubles.
	 * 
	 * @return <code>true</code> if doubles have to be displayed
	 */
	public boolean isUseDouble() {
		return useDouble;
	}

}
