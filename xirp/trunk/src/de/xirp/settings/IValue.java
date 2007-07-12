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
 * IValue.java
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

import java.util.Observer;

/**
 * Interface for the values of preferences
 * 
 * @author Rabea Gransberger
 */
public interface IValue {

	/**
	 * Selection state of a preference value
	 */
	public enum SettingsState {
		/**
		 * The value is selected
		 */
		SELECTED,
		/**
		 * The value is not selected
		 */
		NOT_SELECTED;

		/**
		 * Converts the given boolean to a settings state.
		 * 
		 * @param b
		 *            the boolean to convert
		 * @return <code>true</code> is converted to
		 *         {@link #SELECTED}, <code>false</code> is
		 *         converted to {@link #NOT_SELECTED}
		 */
		public static SettingsState fromBoolean(boolean b) {
			if (b) {
				return SELECTED;
			}
			return NOT_SELECTED;
		}

		/**
		 * Converts the current settings state to a boolean.
		 * 
		 * @return <code>true</code> if {@link #SELECTED},
		 *         <code>false</code> if {@link #NOT_SELECTED}
		 */
		public boolean toBoolean() {
			if (this == SELECTED) {
				return true;
			}
			return false;
		}
	}

	/**
	 * Checks if the value has changed, that is if the saved state is
	 * not equal to the current state.
	 * 
	 * @return <code>true</code> if the value has changed
	 */
	public boolean hasChanged();

	/**
	 * Resets the value, that is overriding the current state with the
	 * saved state.<br>
	 * Please remember to notify the observers when extending
	 * {@link java.util.Observable}.
	 */
	public void reset();

	/**
	 * Gets the string representation of this value for displaying in
	 * the UI.<br>
	 * The display value is different of the save value on values
	 * which need localization.
	 * 
	 * @return The value to display at the UI
	 */
	public String getDisplayValue();

	/**
	 * Gets the string representation of this value when saving the
	 * preferences.<br>
	 * The display value is different of the save value on values
	 * which need localization.
	 * 
	 * @return The value used for saving
	 */
	public String getSaveValue();

	/**
	 * If the value is translatable this method should return the key
	 * that is then used for translation.
	 * 
	 * @return the key or <code>null</code> if the value is not
	 *         translatable.
	 */
	public String getKey();

	/**
	 * Arguments that should be used when translating the key.
	 * 
	 * @return arguments for the key
	 */
	public Object[] getKeyArgs();

	/**
	 * Resets the value, that is overriding the saved state with
	 * current state.<br>
	 * This method doesn't make this values state persistent, but only
	 * updates it's local saved state. Use
	 * {@link Option#save(boolean)} to persist the values states.<br>
	 */
	public void save();

	/**
	 * Checks the saved state of the value.
	 * 
	 * @return <code>true</code> if the saved state means that the
	 *         value is selected
	 */
	public boolean isSelected();

	/**
	 * Checks if the default value is currently selected.
	 * 
	 * @return <code>true</code> if the default is selected.
	 */
	public boolean isDefaultSelected();

	/**
	 * Checks the current state of the value.
	 * 
	 * @return <code>true</code> if the current state means that the
	 *         value is selected
	 */
	public boolean isCurrentlySelected();

	/**
	 * Sets the values selection state to the given parameters value.<br>
	 * This is shorthand for
	 * <code>setSelected(selection, false)</code>
	 * 
	 * @param selection
	 *            <code>true</code> if the value should be selected
	 */
	public void setSelected(boolean selection);

	/**
	 * Sets the values selection state to the given parameters value.<br>
	 * 
	 * @param selection
	 *            <code>true</code> if the value should be selected
	 * @param fromUI
	 *            <code>true</code> if this method was called from
	 *            the UI displaying this value.<br/> Is this the
	 *            case, don't notify the observers to prevent infinite
	 *            loops.
	 */
	public void setSelected(boolean selection, boolean fromUI);

	/**
	 * If extending {@link java.util.Observable} this method should
	 * take care that the given Observer is added to this Observable
	 * 
	 * @param obs
	 *            The Observer to add to this Observable (if present)
	 */
	public void addObserverToValue(Observer obs);

	/**
	 * Sets this values current value to the default if it has an
	 * default. Otherwise leaves the current value unchanged.<br>
	 * Please remember to notify the observers when extending
	 * {@link java.util.Observable}.
	 */
	public void setToDefault();

	/**
	 * Adds a listener to the value which is notified when the value
	 * changes. This is not equal to adding an observer to this value,
	 * because the listener is notified even when the change comes for
	 * the UI.
	 * 
	 * @param listener
	 *            the listener to add to the value.
	 */
	public void addLocaleChangeListener(SettingsChangedListener listener);

	/**
	 * Removes the listener from the value which was notified when the
	 * value changed.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeLocaleChangeListener(SettingsChangedListener listener);

	/**
	 * Gets the key under which the value is saved.
	 * 
	 * @return the key for saving
	 */
	public String getSaveKey();

	/**
	 * Parses the saved value out of the given object.
	 * 
	 * @param object
	 *            the object with the saved value.
	 */
	public void parseSavedValue(Object object);
}
