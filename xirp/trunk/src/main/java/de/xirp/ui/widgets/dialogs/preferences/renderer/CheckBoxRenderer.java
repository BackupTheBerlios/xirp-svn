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
 * CheckBoxRenderer.java
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
import java.util.List;
import java.util.Observable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import de.xirp.settings.IValue;
import de.xirp.settings.Option;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.XCheckBox;
import de.xirp.ui.widgets.custom.XGroup;

/**
 * Renders options in check box style.<br>
 * Synchronization over observers with the current data is supported.<br>
 * 
 * @author Rabea Gransberger
 */
public class CheckBoxRenderer extends AbstractOptionRenderer {

	/**
	 * Mapping from save key of a value to it's checkbox
	 */
	private HashMap<String, XCheckBox> map = new HashMap<String, XCheckBox>( );

	/**
	 * If there's more than one value an option group is created which
	 * holds the created checkboxes (one for each value). Otherwise
	 * the check box is just added to the parent composite.<br>
	 * The check boxes use the provided internationalization keys of
	 * the settings for displaying.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.renderer.IOptionRenderer#render(Composite,
	 *      Option)
	 */
	public void render(Composite parent, Option option) {
		List<IValue> values = option.getValues( );
		if (values.size( ) > 1) {
			XGroup group = new XGroup(parent, SWT.SHADOW_NONE, option.getI18n( ));
			group.setTextForLocaleKey(option.getNameKey( ),
					option.getNameKeyArgs( ));
			SWTUtil.setGridData(group,
					true,
					false,
					SWT.FILL,
					SWT.BEGINNING,
					2,
					1);
			SWTUtil.setGridLayout(group, 1, false);

			// Create Buttons
			for (IValue value : values) {
				XCheckBox b = new XCheckBox(group, option.getI18n( ));
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

				b.setSelection(value.isCurrentlySelected( ));

				// update data
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
		}
		// show a single value outside of a group
		else if (values.size( ) == 1) {
			final IValue value = values.get(0);

			XCheckBox b = new XCheckBox(parent, option.getI18n( ));
			map.put(value.getSaveValue( ), b);
			b.setTextForLocaleKey(option.getNameKey( ), option.getNameKeyArgs( ));
			b.setData(value);
			SWTUtil.setGridData(b, true, false, SWT.FILL, SWT.CENTER, 2, 1);

			b.setSelection(value.isCurrentlySelected( ));

			// update data
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
	}

	/**
	 * If the observable is a {@link IValue value} the checkbox
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
			XCheckBox b = map.get(key);
			boolean isSelected = val.isCurrentlySelected( );
			b.setSelection(isSelected);
		}
	}
}
