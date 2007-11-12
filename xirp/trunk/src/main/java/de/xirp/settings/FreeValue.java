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
 * FreeValue.java
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

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * A value which allows free user input.
 * 
 * @author Rabea Gransberger
 */
public class FreeValue extends Observable implements IValue {

	/**
	 * The current value
	 */
	protected String currentValue;

	/**
	 * The saved value
	 */
	protected String savedValue = null;

	/**
	 * The key for saving the value
	 */
	protected String key;

	/**
	 * The default value
	 */
	protected String defaultValue;

	/**
	 * Flag which is <code>true</code> when this is a password field
	 */
	protected boolean password = false;

	/**
	 * The maximum length for the string
	 */
	protected int maximumLength = -1;
	/**
	 * Listeners which are notified when the state of the value
	 * changes.
	 */
	protected ArrayList<SettingsChangedListener> listeners = new ArrayList<SettingsChangedListener>( );

	/**
	 * Constructs a new preferences value which allows the user to
	 * input an own text
	 * 
	 * @param key
	 *            the key used for saving the typed value
	 * @param value
	 *            the initial value
	 * @param defaultValue
	 *            The default value, might be <code>null</code>
	 */
	protected FreeValue(String key, String value, String defaultValue) {
		this.currentValue = value;
		this.key = key;
		this.defaultValue = defaultValue;
		this.savedValue = value;
	}

	/**
	 * Checks if this value is a password value.
	 * 
	 * @return <code>true</code> if it's a password value.
	 */
	public boolean isPassword() {
		return password;
	}

	/**
	 * Sets if this value is a password value.
	 * 
	 * @param password
	 *            <code>true</code> if it's a password value
	 */
	public void setPassword(boolean password) {
		this.password = password;
	}

	/**
	 * Sets the maximum length this value allows for an input string.
	 * 
	 * @param maximumLength
	 *            the maximum length to set
	 */
	public void setMaximumLength(int maximumLength) {
		this.maximumLength = maximumLength;
	}

	/**
	 * Sets the maximum length this value allows for an input string.
	 * 
	 * @return the maximum length
	 */
	public int getMaximumLength() {
		return maximumLength;
	}

	/**
	 * Checks if the value has a maximum length defined.
	 * 
	 * @return <code>true</code> if this value defines a maximum
	 *         length
	 */
	public boolean hasMaximumLength() {
		return maximumLength > 0;
	}

	/**
	 * Gets the default value.
	 * 
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @see de.xirp.settings.IValue#getDisplayValue()
	 */
	public String getDisplayValue() {
		return currentValue;
	}

	/**
	 * Gets the key of this value used for saving the value.
	 * 
	 * @return the key of this value
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @see de.xirp.settings.IValue#getSaveKey()
	 */
	@Override
	public String getSaveKey() {
		return key;
	}

	/**
	 * @see de.xirp.settings.IValue#getSaveValue()
	 */
	public String getSaveValue() {
		return currentValue;
	}

	/**
	 * @see de.xirp.settings.IValue#hasChanged()
	 */
	@Override
	public boolean hasChanged() {
		if (savedValue == null) {
			return true;
		}
		return !currentValue.equals(savedValue);
	}

	/**
	 * @see de.xirp.settings.IValue#reset()
	 */
	public void reset() {
		setValue(savedValue, false);
	}

	/**
	 * @see de.xirp.settings.IValue#save()
	 */
	public void save() {
		savedValue = currentValue;
	}

	/**
	 * Sets the saved value when loading from the persisted
	 * preferences file.
	 * 
	 * @param value
	 *            the saved value
	 */
	public void setSavedValue(String value) {
		savedValue = value;
		currentValue = value;
	}

	/**
	 * Sets the default value.
	 * 
	 * @param value
	 *            the default value
	 */
	protected void setDefaultValue(String value) {
		defaultValue = value;
	}

	/**
	 * A free value has no selection state at all.
	 * 
	 * @return <code>true</code>
	 * @see de.xirp.settings.IValue#isSelected()
	 */
	public boolean isSelected() {
		return true;
	}

	/**
	 * @return <code>true</code> if this is a password value,
	 *         otherwise <code>true</code> if the default value
	 *         equals the current value. Otherwise <code>false</code>.
	 * @see de.xirp.settings.IValue#isDefaultSelected()
	 */
	public boolean isDefaultSelected() {
		if (password) {
			return true;
		}
		if (this.defaultValue != null) {
			return this.defaultValue.equals(currentValue);
		}
		return false;
	}

	/**
	 * Sets a new value to this preferences value.<br>
	 * This is shorthand for <code>setValue(newValue,false)</code>
	 * 
	 * @param newValue
	 *            the new value
	 */
	public void setValue(String newValue) {
		setValue(newValue, false);
	}

	/**
	 * Sets a new value to this preferences value.<br>
	 * If it's not called from the UI, the UI will be notified that
	 * the value has changed.
	 * 
	 * @param newValue
	 *            the new value
	 * @param fromUI
	 *            <code>true</code> if it's called from the UI
	 *            displaying this value
	 */
	public void setValue(String newValue, boolean fromUI) {
		this.currentValue = newValue;
		// notify the observers that the current state has changed
		// and they should update the UI
		if (!fromUI) {
			setChanged( );
			notifyObservers( );
		}

		notifyChange( );
	}

	/**
	 * @see de.xirp.settings.IValue#setSelected(boolean)
	 */
	public void setSelected(boolean selection) {
		setSelected(selection, false);
	}

	/**
	 * @see de.xirp.settings.IValue#addObserverToValue(java.util.Observer)
	 */
	public void addObserverToValue(Observer obs) {
		addObserver(obs);
	}

	/**
	 * This method has no effect for this value, because there is no
	 * selection but user typed text. Use {@link #getDisplayValue()}
	 * to get the value.
	 * 
	 * @param selection
	 *            unused
	 * @param fromUI
	 *            unused
	 */
	public void setSelected(@SuppressWarnings("unused")
	boolean selection, @SuppressWarnings("unused")
	boolean fromUI) {
		// nothing to do here
	}

	/**
	 * @see de.xirp.settings.IValue#setToDefault()
	 */
	public void setToDefault() {
		if (this.defaultValue != null) {
			setValue(defaultValue, false);
		}
		else {
			setValue("", false); //$NON-NLS-1$
		}
	}

	/**
	 * @return <code>true</code>
	 * @see de.xirp.settings.IValue#isCurrentlySelected()
	 */
	public boolean isCurrentlySelected() {
		return true;
	}

	/**
	 * @see de.xirp.settings.IValue#addLocaleChangeListener(SettingsChangedListener)
	 */
	public void addLocaleChangeListener(SettingsChangedListener listener) {
		listeners.add(listener);
	}

	/**
	 * Notifies all local listeners that the value has changed it's
	 * state.
	 */
	public void notifyChange() {
		for (SettingsChangedListener listener : listeners) {
			listener.settingsChanged(new SettingsChangedEvent(this));
		}
	}

	/**
	 * @see de.xirp.settings.IValue#removeLocaleChangeListener(SettingsChangedListener)
	 */
	public void removeLocaleChangeListener(SettingsChangedListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see #getSaveValue()
	 */
	@Override
	public String toString() {
		return getSaveValue( );
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj.toString( ).equalsIgnoreCase(this.toString( ));
	}

	/**
	 * {@inheritDoc}<br>
	 * No translation is supported.
	 * 
	 * @return <code>null</code>
	 * @see de.xirp.settings.IValue#getKeyArgs()
	 */
	public Object[] getKeyArgs() {
		return null;
	}

	/**
	 * @see de.xirp.settings.IValue#parseSavedValue(java.lang.Object)
	 */
	@Override
	public void parseSavedValue(Object object) {
		setSavedValue(object.toString( ));
	}
}
