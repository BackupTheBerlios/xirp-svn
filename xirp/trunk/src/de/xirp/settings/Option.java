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
 * Option.java
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.SubsetConfiguration;
import org.eclipse.swt.graphics.RGB;

import de.xirp.settings.IValue.SettingsState;
import de.xirp.ui.widgets.dialogs.preferences.renderer.*;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * An option of preferences. An option contains values and has a type
 * or renderer for displaying the values.
 * 
 * @author Rabea Gransberger
 */
public class Option {

	/**
	 * Type of the Option
	 */
	public enum OptionType {
		/**
		 * Values are Shown in checkbox group
		 */
		CHECKBOX,
		/**
		 * Values are Shown in radiobox group
		 */
		RADIOBUTTON,
		/**
		 * Values are Shown in combobox
		 */
		COMBOBOX,
		/**
		 * Textfield for manual Value Input
		 */
		TEXTFIELD,
		/**
		 * A color chooser
		 */
		COLOR,

		/**
		 * A spinner
		 */
		SPINNER,

		/**
		 * Unknown type. A renderer has to be specified
		 */
		UNKNOWN;
	}

	/**
	 * The configuration object
	 */
	private SubsetConfiguration sub;
	/**
	 * The bundle used for translation
	 */
	private II18nHandler bundle;
	/**
	 * key for the translation of the name of this option of the form
	 * <code>[mainKey].[subKey].[optionKey]</code>
	 */
	private String translationKey;
	/**
	 * The values of this option
	 */
	private ArrayList<IValue> values = new ArrayList<IValue>( );
	/**
	 * The type of this option. May be <code>null</code> if a
	 * renderer is specified.
	 */
	private OptionType type = OptionType.UNKNOWN;
	/**
	 * The renderer for this option
	 */
	private IOptionRenderer renderer;

	/**
	 * <code>true</code> if the options have already been loaded
	 * from a saved configuration or there is no configuration to load
	 * from
	 */
	private boolean loaded = false;

	/**
	 * The parent settings page of this option
	 */
	protected SettingsPage parentPage;

	/**
	 * Arguments used for translating the name of this option
	 */
	private Object[] nameKeyArgs;

	/**
	 * Constructs a new option. Protected to prevent direct calls.
	 * 
	 * @param parent
	 *            the parent settings page
	 * @param sub
	 *            The configuration object
	 * @param bundle
	 *            The bundle used for translation
	 * @param optionKey
	 *            key for the translation of the name of this option
	 *            of the form
	 *            <code>[mainKey].[subKey].[optionKey]</code>
	 * @param type
	 *            The type of this option. May be <code>null</code>
	 *            if a renderer is specified.
	 */
	protected Option(SettingsPage parent, SubsetConfiguration sub,
			II18nHandler bundle, String optionKey, OptionType type) {
		this.parentPage = parent;
		this.sub = sub;
		this.bundle = bundle;
		this.translationKey = optionKey;
		this.type = type;
		this.renderer = selectRenderer( );
		if (sub == null) {
			loaded = true;
		}
	}

	/**
	 * Get a renderer for the option type.
	 * 
	 * @return the render for the option type
	 */
	private IOptionRenderer selectRenderer() {
		switch (type) {
			case CHECKBOX:
				return new CheckBoxRenderer( );
			case TEXTFIELD:
				return new TextFieldRenderer( );
			case COMBOBOX:
				return new ComboRenderer( );
			case RADIOBUTTON:
				return new RadioButtonRenderer( );
			case COLOR:
				return new ColorChooserRenderer( );
			case SPINNER:
				return new SpinnerRenderer( );
			default:
				return new EmptyRenderer( );
		}
	}

	/**
	 * Sets a renderer for this option. This is only needed for custom
	 * renderer implementations. Otherwise the renderer is selected
	 * automatically according to the option type.
	 * 
	 * @param renderer
	 *            the renderer to use for this option
	 */
	public void setRenderer(IOptionRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Gets the renderer for rendering the option for the UI.
	 * 
	 * @return the renderer of this option
	 */
	public IOptionRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Get's translated name of this option.
	 * 
	 * @return the translated name of this option
	 */
	public String getName() {
		if (nameKeyArgs != null) {
			return bundle.getString(translationKey, nameKeyArgs);
		}
		return bundle.getString(translationKey);
	}

	/**
	 * Get's the key for translating the name of this option.
	 * 
	 * @return the translated name of this option
	 */
	public String getNameKey() {
		return translationKey;
	}

	/**
	 * Loads the saved preferences for this options values.
	 */
	@SuppressWarnings("unchecked")
	private void loadSavedPrefs() {
		if (sub == null) {
			loaded = true;
		}
		if (!loaded) {
			// Get a list of the key of this configuration
			Iterator it = sub.getKeys( );
			ArrayList<String> keys = new ArrayList<String>( );
			while (it.hasNext( )) {
				keys.add((String) it.next( ));
			}
			// Iterate over the values of this option an load
			// the saved value
			for (IValue value : values) {
				String val = value.getSaveKey( );
				if (keys.contains(val)) {
					Object obj = sub.getProperty(val);
					value.parseSavedValue(obj);
				}
			}
			loaded = true;
		}
	}

	/**
	 * Adds a new number value to this option. The number value is
	 * constant and may be selected or not.
	 * 
	 * @param value
	 *            the value
	 * @param state
	 *            the initial state
	 * @return the added value
	 */
	public NumberValue addNumberValue(Number value, SettingsState state) {
		NumberValue val = new NumberValue(value, state);
		addValue(val);
		return val;
	}

	/**
	 * Adds a new translatable named boolean value to this option. The
	 * value is a constant string determined by translating the given
	 * key and may be selected or not.
	 * 
	 * @param key
	 *            the key for translating the value
	 * @param state
	 *            the default state
	 * @param keyArgs
	 *            arguments used for translation
	 * @return the added value
	 */
	public StringValue addTranslatableNamedBooleanValue(String key,
			SettingsState state, Object... keyArgs) {
		return addNamedBooleanValue(key, state, false, keyArgs);
	}

	/**
	 * Adds a new named boolean value to this option. The value is a
	 * constant string given and may be selected or not.
	 * 
	 * @param key
	 *            the key for translation, or the value for a constant
	 *            string
	 * @param state
	 *            the default state
	 * @param constant
	 *            <code>true</code> if the first argument is a key,
	 *            <code>false</code> if it is a value
	 * @param keyArgs
	 *            arguments used for translation
	 * @return the added value
	 */
	private StringValue addNamedBooleanValue(String key, SettingsState state,
			boolean constant, Object... keyArgs) {
		StringValue val = new StringValue(bundle, translationKey + "." + key, //$NON-NLS-1$
				key,
				state,
				constant,
				keyArgs);
		addValue(val);
		return val;
	}

	/**
	 * Adds a non translatable named boolean value. This is a constant
	 * string which may be selected or not.
	 * 
	 * @param value
	 *            the value itself. It is not translated.
	 * @param state
	 *            the default state
	 * @return the added value
	 */
	public StringValue addNonTranslatableNamedBooleanValue(String value,
			SettingsState state) {
		return addNamedBooleanValue(value, state, true);
	}

	/**
	 * Adds a user chooseable value to this option.<br>
	 * These options may only be rendered with the text field style.
	 * 
	 * @param key
	 *            the key for saving this value
	 * @param value
	 *            the value used as default
	 * @return the added value
	 */
	public FreeValue addStringValue(String key, String value) {
		if (this.type != OptionType.TEXTFIELD && type != OptionType.UNKNOWN) {
			throw new IllegalStateException(I18n.getString("Option.exception.freeValuesOnlyBeAddedToTextfield")); //$NON-NLS-1$
		}
		FreeValue val = new FreeValue(key, value, value);
		addValue(val);
		return val;
	}

	/**
	 * Adds a user chooseable value to this option.<br>
	 * These options may only be rendered with the text field style.
	 * 
	 * @param key
	 *            the key for saving this value
	 * @param value
	 *            the value used as default
	 * @param maximum
	 *            the maximum length of the typed string
	 * @return the added value
	 */
	public FreeValue addStringValue(String key, String value, int maximum) {
		if (this.type != OptionType.TEXTFIELD && type != OptionType.UNKNOWN) {
			throw new IllegalStateException(I18n.getString("Option.exception.freeValuesOnlyBeAddedToTextfield")); //$NON-NLS-1$
		}
		FreeValue val = new FreeValue(key, value, value);
		val.setMaximumLength(maximum);
		addValue(val);
		return val;
	}

	/**
	 * Adds a user chooseable value to this option which is only
	 * displayed with stars and my therefor be used for password
	 * fields.<br>
	 * These options may only be rendered with the text field style.
	 * 
	 * @param key
	 *            the key for saving this value
	 * @param value
	 *            the value used as default
	 * @return the added value
	 */
	public FreeValue addPasswordValue(String key, String value) {
		if (this.type != OptionType.TEXTFIELD && type != OptionType.UNKNOWN) {
			throw new IllegalStateException(I18n.getString("Option.exception.freeValuesOnlyBeAddedToTextfield")); //$NON-NLS-1$
		}
		FreeValue val = new FreeValue(key, value, value);
		val.setPassword(true);
		addValue(val);
		return val;
	}

	/**
	 * Adds an RGB value to a color option type.
	 * 
	 * @param key
	 *            the key for saving the RGB value. Must be unique
	 *            inside the option
	 * @param r
	 *            the default red value
	 * @param g
	 *            the default green value
	 * @param b
	 *            the blue value
	 * @return the create value of this option
	 */
	public RGBValue addRGBValue(String key, int r, int g, int b) {
		RGB rgb = new RGB(r, g, b);
		return addRGBValue(key, rgb);
	}

	/**
	 * Adds an RGB value to a color option type.
	 * 
	 * @param key
	 *            the key for saving the RGB value. Must be unique
	 *            inside the option
	 * @param rgb
	 *            the default RGB value
	 * @return the create value of this option
	 */
	public RGBValue addRGBValue(String key, RGB rgb) {
		if (this.type != OptionType.COLOR && type != OptionType.UNKNOWN) {
			throw new IllegalStateException(I18n.getString("Option.exception.rgbValuesOnlyBeAddedToColor")); //$NON-NLS-1$
		}
		RGBValue val = new RGBValue(key,
				RGBValue.getString(rgb),
				RGBValue.getString(rgb));
		addValue(val);
		return val;
	}

	/**
	 * Creates a value which is displayed with the Spinner OptionType.
	 * 
	 * @param value
	 *            the default value
	 * @param min
	 *            minimum value of the spinner
	 * @param max
	 *            maximum value of the spinner
	 * @param step
	 *            step for the spinner
	 * @return the spinner value
	 */
	public SpinnerValue addSpinnerValue(int value, int min, int max, int step) {
		if (this.type != OptionType.SPINNER && type != OptionType.UNKNOWN) {
			throw new IllegalStateException(I18n.getString("Option.exception.spinnerValuesOnlyBeAddedToSpinner")); //$NON-NLS-1$
		}
		SpinnerValue val = new SpinnerValue(value, min, max, step);
		addValue(val);
		return val;
	}

	/**
	 * Creates a value which is displayed with the Spinner OptionType.
	 * A spinner capable of displaying doubles is used.
	 * 
	 * @param value
	 *            the default value
	 * @param min
	 *            minimum value of the spinner
	 * @param max
	 *            maximum value of the spinner
	 * @param step
	 *            step for the spinner
	 * @return the spinner value
	 */
	public SpinnerValue addSpinnerValue(double value, double min, double max,
			int step) {
		if (this.type != OptionType.SPINNER && type != OptionType.UNKNOWN) {
			throw new IllegalStateException(I18n.getString("Option.exception.spinnerValuesOnlyBeAddedToSpinner")); //$NON-NLS-1$
		}
		SpinnerValue val = new SpinnerValue(value, min, max, step);
		addValue(val);
		return val;
	}

	/**
	 * Adds a value to this option. Should only be used for custom
	 * value implementations.
	 * 
	 * @param value
	 *            the value to add
	 */
	@SuppressWarnings("unchecked")
	public void addValue(IValue value) {
		boolean contains = false;
		for (IValue act : values) {
			if (value.equals(act)) {
				if (act instanceof AbstractValue) {
					AbstractValue<?> val = (AbstractValue) act;
					val.setSavedSelection(value.isCurrentlySelected( ));
					val.setDefaultSelection(value.isCurrentlySelected( ));
				}
				else if (act instanceof FreeValue) {
					FreeValue val = (FreeValue) act;
					val.setSavedValue(value.getSaveValue( ));
					val.setDefaultValue(value.getSaveValue( ));
				}
				contains = true;
				break;
			}
		}
		if (!contains) {
			values.add(value);
			value.addLocaleChangeListener(parentPage.parentSettings.getImmediateChangeValueListener( ));
		}
	}

	/**
	 * Checks if anything of the option has changed.
	 * 
	 * @return <code>true</code> if any of the values of this option
	 *         had changed
	 */
	protected boolean hasChanged() {
		// Only check until one of the values have changes. This is
		// faster
		// than checking all till the end
		boolean changed = false;
		for (Iterator<IValue> it = values.iterator( ); it.hasNext( ) &&
				!changed;) {
			changed |= it.next( ).hasChanged( );
		}
		return changed;
	}

	/**
	 * Checks if only defaults are selected.
	 * 
	 * @return <code>true</code> if all the selected values are
	 *         defaults
	 */
	protected boolean isDefaultSelected() {
		boolean defAult = true;
		for (Iterator<IValue> it = values.iterator( ); it.hasNext( ) && defAult;) {
			defAult &= it.next( ).isDefaultSelected( );
		}
		return defAult;
	}

	/**
	 * Saves the changed parts of the option if any values have
	 * changed to the underlying properties file.
	 * 
	 * @param toFile
	 *            <code>true</code> if the changes should be saved
	 *            to a file or not.
	 * @return <code>true</code> if any changes have been saved
	 */
	protected boolean save(boolean toFile) {
		boolean saved = false;
		// Check if anything has changed prior to saving
		for (IValue value : values) {
			if (value.hasChanged( )) {
				value.save( );
				if (sub != null && toFile) {
					if (value instanceof AbstractValue) {
						sub.setProperty(value.getSaveValue( ),
								value.isSelected( ));
					}
					else if (value instanceof FreeValue) {
						FreeValue free = (FreeValue) value;
						sub.setProperty(free.getKey( ), value.getSaveValue( ));
					}
				}
				saved = true;
			}
		}
		return saved;
	}

	/**
	 * Reset the changes made to this option to the saved values.<br>
	 * The UI will be notified for every reset.
	 */
	protected void reset() {
		if (hasChanged( )) {
			for (IValue value : values) {
				value.reset( );
			}
		}
	}

	/**
	 * Restores the default values for this option.
	 */
	protected void restoreDefaults() {
		for (IValue value : values) {
			value.setToDefault( );
		}
	}

	/**
	 * Gets the type of this option.
	 * 
	 * @return the type
	 */
	public OptionType getType() {
		return type;
	}

	/**
	 * Gets the values of this option.
	 * 
	 * @return the values
	 */
	public List<IValue> getValues() {
		loadSavedPrefs( );
		return Collections.unmodifiableList(values);
	}

	/**
	 * Gets the selected values of this option.
	 * 
	 * @return the values
	 */
	public List<IValue> getSelectedValues() {
		loadSavedPrefs( );
		List<IValue> vals = new ArrayList<IValue>(values.size( ));
		for (IValue val : values) {
			if (val.isSelected( )) {
				vals.add(val);
			}
		}
		return Collections.unmodifiableList(vals);
	}

	/**
	 * Gets the selected value of this option.<br>
	 * Note: It is only safe to use this if the OptionType allows only
	 * one value to be selected.
	 * 
	 * @return the selected value or <code>null</code> if no value
	 *         is selected
	 */
	public IValue getSelectedValue() {
		loadSavedPrefs( );

		for (IValue val : values) {
			if (val.isSelected( )) {
				return val;
			}
		}
		return null;
	}

	/**
	 * Checks if the given display value is selected (in saved state).
	 * 
	 * @param value
	 *            the value to look for
	 * @return <code>true</code> if the value was found and if it is
	 *         selected
	 */
	public boolean isSelectedValue(String value) {
		loadSavedPrefs( );

		for (IValue val : values) {
			if (val.getDisplayValue( ).equals(value)) {
				if (val.isSelected( )) {
					return true;
				}
				break;
			}
		}
		return false;
	}

	/**
	 * Checks if the value for the given key is selected (in saved
	 * state).
	 * 
	 * @param key
	 *            the key to look for
	 * @return <code>true</code> if the key was found and if it is
	 *         selected
	 */
	public boolean isSelectedKey(String key) {
		loadSavedPrefs( );

		for (IValue val : values) {
			String valKey = val.getKey( );
			if (valKey != null && valKey.equals(key)) {
				if (val.isSelected( )) {
					return true;
				}
				break;
			}
		}
		return false;
	}

	/**
	 * Gets the handler used for translation by this option.
	 * 
	 * @return the handler used for translation
	 */
	public II18nHandler getI18n() {
		return bundle;
	}

	/**
	 * Gets the arguments that should be used when translating the
	 * name of this option.
	 * 
	 * @return the arguments for the name key
	 */
	public Object[] getNameKeyArgs() {
		return nameKeyArgs;
	}

	/**
	 * Sets the arguments that should be used when translating the
	 * name of this option.
	 * 
	 * @param keyArgs
	 *            the arguments for the name key
	 */
	public void setNameKeyArgs(Object... keyArgs) {
		this.nameKeyArgs = keyArgs;
	}

}
