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
 * AbstractValue.java
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
 * An abstract class for preference values with a generic type
 * parameter.<br>
 * It extends {@link Observable} additionally for supporting
 * synchronization of the values and the UI displaying the values.
 * 
 * @param <T>
 *            The type of the value
 * @author Rabea Gransberger
 */
public abstract class AbstractValue<T> extends Observable implements IValue {

	/**
	 * The saved state of this value
	 */
	protected SettingsState savedState = SettingsState.NOT_SELECTED;
	/**
	 * The current state of this value. May or may not be saved
	 */
	protected SettingsState currentState;
	/**
	 * The current state of this value. May or may not be saved
	 */
	protected SettingsState defaultState;
	/**
	 * The value
	 */
	protected T value;

	/**
	 * Listeners which are notified when the state of the value
	 * changes.
	 */
	protected ArrayList<SettingsChangedListener> listeners = new ArrayList<SettingsChangedListener>( );

	/**
	 * Constructs a new value.
	 * 
	 * @param value
	 *            the value
	 * @param state
	 *            the state of the value used as current, default and
	 *            saved state
	 */
	protected AbstractValue(T value, SettingsState state) {
		this.value = value;
		this.currentState = state;
		this.defaultState = state;
		this.savedState = state;
	}

	/**
	 * @see de.xirp.settings.IValue#parseSavedValue(java.lang.Object)
	 */
	@Override
	public void parseSavedValue(Object object) {
		boolean selected = Boolean.valueOf(object.toString( )).booleanValue( );
		setSavedSelection(selected);
	}

	/**
	 * @see de.xirp.settings.IValue#hasChanged()
	 */
	@Override
	public boolean hasChanged() {
		return savedState != currentState;
	}

	/**
	 * Gets the original value of this preferences value.
	 * 
	 * @return the value
	 */
	protected T getValue() {
		return value;
	}

	/**
	 * @see de.xirp.settings.IValue#reset()
	 */
	public void reset() {
		currentState = savedState;
		// notify the observers that the current state has changed
		// and they should update the UI
		setChanged( );
		notifyObservers( );
	}

	/**
	 * @see de.xirp.settings.IValue#save()
	 */
	public void save() {
		savedState = currentState;
	}

	/**
	 * Sets the saved selection of this value when loading from the
	 * persisted preferences file.
	 * 
	 * @param selected
	 *            <code>true</code> if the value is selected in the
	 *            file
	 */
	public void setSavedSelection(boolean selected) {
		savedState = SettingsState.fromBoolean(selected);
		currentState = savedState;
	}

	/**
	 * Sets the default selection to the given state.
	 * 
	 * @param selected
	 *            <code>true</code> if the value should be selected
	 *            as default
	 */
	protected void setDefaultSelection(boolean selected) {
		defaultState = SettingsState.fromBoolean(selected);
	}

	/**
	 * @see de.xirp.settings.IValue#isSelected()
	 */
	public boolean isSelected() {
		return savedState.toBoolean( );
	}

	/**
	 * @see de.xirp.settings.IValue#isDefaultSelected()
	 */
	public boolean isDefaultSelected() {
		return defaultState == currentState;
	}

	/**
	 * @see de.xirp.settings.IValue#isCurrentlySelected()
	 */
	public boolean isCurrentlySelected() {
		return currentState.toBoolean( );
	}

	/**
	 * @see de.xirp.settings.IValue#setSelected(boolean)
	 */
	public void setSelected(boolean selection) {
		setSelected(selection, false);
	}

	/**
	 * @see de.xirp.settings.IValue#setSelected(boolean,
	 *      boolean)
	 */
	public void setSelected(boolean selection, boolean fromUI) {
		currentState = SettingsState.fromBoolean(selection);
		// notify the observers that the current state has changed
		// and they should update the UI
		if (!fromUI) {
			setChanged( );
			notifyObservers( );
		}
		notifyChange( );
	}

	/**
	 * @see de.xirp.settings.IValue#addObserverToValue(java.util.Observer)
	 */
	public void addObserverToValue(Observer obs) {
		addObserver(obs);
	}

	/**
	 * @see de.xirp.settings.IValue#setToDefault()
	 */
	public void setToDefault() {
		setSelected(defaultState.toBoolean( ), false);
	}

	/**
	 * @see de.xirp.settings.IValue#getKey()
	 */
	public String getKey() {
		return null;
	}

	/**
	 * @see de.xirp.settings.IValue#getKeyArgs()
	 */
	public Object[] getKeyArgs() {
		return null;
	}

	/**
	 * @see #getSaveValue( )
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
}
