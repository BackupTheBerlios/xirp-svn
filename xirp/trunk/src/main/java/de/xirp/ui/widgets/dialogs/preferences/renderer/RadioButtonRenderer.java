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
 * RadioButtonRenderer.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 29.10.2006:		Created by Rabea Gransberger.
 */
package de.xirp.ui.widgets.dialogs.preferences.renderer;

import java.util.HashMap;
import java.util.Observable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import de.xirp.settings.IValue;
import de.xirp.settings.Option;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.XCheckBox;
import de.xirp.ui.widgets.custom.XGroup;
import de.xirp.ui.widgets.custom.XRadioButton;

/**
 * Renders options in radio button style.<br>
 * Synchronization over observers with the current data is supported.<br>
 * 
 * @author Rabea Gransberger
 */
public class RadioButtonRenderer extends AbstractOptionRenderer {

	/**
	 * Mapping from save key of a value to it's radio button
	 */
	private HashMap<String, Button> map = new HashMap<String, Button>( );

	/**
	 * If there's more than one value an option group is created which
	 * holds the created radio buttons (one for each value). Otherwise
	 * the radio button is just added to the parent composite.<br>
	 * The radio button use the provided internationalization keys of
	 * the settings for displaying.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.renderer.IOptionRenderer#render(Composite,
	 *      Option)
	 */
	public void render(Composite parent, Option option) {
		XGroup group = new XGroup(parent, SWT.SHADOW_NONE, option.getI18n( ));
		group.setTextForLocaleKey(option.getNameKey( ), option.getNameKeyArgs( ));
		SWTUtil.setGridData(group, true, false, SWT.FILL, SWT.BEGINNING, 2, 1);
		SWTUtil.setGridLayout(group, 1, false);

		// Create Buttons and save the Button
		// which was last Indicated as Selected
		Button lastSelected = null;
		for (IValue value : option.getValues( )) {
			XRadioButton b = new XRadioButton(group, option.getI18n( ));
			map.put(value.getSaveValue( ), b);
			String key = value.getKey( );
			if (key != null) {
				b.setTextForLocaleKey(key, value.getKeyArgs( ));
			}
			else {
				b.setText(value.getDisplayValue( ));
			}
			b.setData(value);
			SWTUtil.setGridData(b, true, false, SWT.FILL, SWT.CENTER, 2, 1);

			if (value.isCurrentlySelected( ) || lastSelected == null) {
				lastSelected = b;
			}

			// Remember Selections for Saving
			b.addListener(SWT.Selection, new Listener( ) {

				public void handleEvent(Event event) {
					Widget widget = event.widget;
					if (widget instanceof XCheckBox) {
						XCheckBox b = (XCheckBox) widget;
						boolean select = b.getSelection( );
						IValue value = (IValue) b.getData( );
						value.setSelected(select, true);
					}
				}
			});
			value.addObserverToValue(this);
		}
		// Set Selection for Radio which allows only one
		if (lastSelected != null) {
			lastSelected.setSelection(true);
		}
	}

	/**
	 * If the observable is a {@link IValue value} the radio button
	 * corresponding to the value is updated with the values current
	 * selection state.
	 * 
	 * @param observable
	 *            the observable
	 * @param obj
	 *            (unused)
	 * @see java.util.Observer#update(java.util.Observable,
	 *      java.lang.Object)
	 */
	public void update(Observable observable, @SuppressWarnings("unused")
	Object obj) {
		if (observable instanceof IValue) {
			IValue val = (IValue) observable;
			String key = val.getSaveValue( );
			Button b = map.get(key);
			boolean isSelected = val.isCurrentlySelected( );
			b.setSelection(isSelected);
		}
	}
}
