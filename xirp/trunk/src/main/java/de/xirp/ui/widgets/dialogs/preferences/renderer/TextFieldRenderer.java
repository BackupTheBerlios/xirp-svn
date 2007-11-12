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
 * TextFieldRenderer.java
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

import java.util.Observable;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

import de.xirp.settings.FreeValue;
import de.xirp.settings.IValue;
import de.xirp.settings.Option;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.XLabel;
import de.xirp.ui.widgets.custom.XTextField;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This renderer renders a single value of an option as a text field
 * for free input. Therefor the value must be of type
 * {@link FreeValue}. Synchronization over observers with the current
 * data is supported.
 * 
 * @author Rabea Gransberger
 */
public class TextFieldRenderer extends AbstractOptionRenderer {

	/**
	 * log4j Logger of this Class
	 */
	private static Logger logClass = Logger.getLogger(TextFieldRenderer.class);
	/**
	 * The text field of this options value
	 */
	private XTextField text;

	/**
	 * Creates a label with the name of the option and uses the last
	 * found {@link FreeValue value} of the option for a text field.
	 * Internationalization is supported.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.renderer.IOptionRenderer#render(Composite,
	 *      Option)
	 */
	public void render(Composite parent, Option option) {
		XLabel l = new XLabel(parent, SWT.NONE, option.getI18n( ));
		l.setTextForLocaleKey(option.getNameKey( ), option.getNameKeyArgs( ));
		SWTUtil.setGridData(l, true, false, SWT.LEFT, SWT.CENTER, 1, 1);

		FreeValue theValue = null;
		for (IValue value : option.getValues( )) {
			if (value instanceof FreeValue) {
				theValue = (FreeValue) value;
			}
		}
		if (theValue != null) {
			final FreeValue testValue = theValue;
			testValue.addObserverToValue(this);
			int style = SWT.BORDER | SWT.CENTER;
			if (testValue.isPassword( )) {
				style |= SWT.PASSWORD;
			}
			text = new XTextField(parent, style);
			if (testValue.hasMaximumLength( )) {
				text.setTextLimit(testValue.getMaximumLength( ));
			}
			SWTUtil.setGridData(text, true, false, SWT.FILL, SWT.CENTER, 1, 1);

			text.setText(testValue.getDisplayValue( ));
			text.addModifyListener(new ModifyListener( ) {

				public void modifyText(@SuppressWarnings("unused")
				ModifyEvent e) {
					String t = text.getText( );
					if (validate(t)) {
						String newT = checkString(t);
						if (!newT.equals(t)) {
							text.setText(newT);
						}
						testValue.setValue(newT, true);
					}
					else {
						text.setText(testValue.getDisplayValue( ));
					}
				}

			});
		}
		else {
			XLabel error = new XLabel(parent, SWT.NONE);
			error.setToolTipTextForLocaleKey("TextFieldRenderer.log.canOnlyRenderFreeValueWithTextfields", option.getNameKey( ));//$NON-NLS-1$
			SWTUtil.setGridData(error, true, false, SWT.FILL, SWT.CENTER, 1, 1);
			logClass.warn(I18n.getString("TextFieldRenderer.log.canOnlyRenderFreeValueWithTextfields", //$NON-NLS-1$
					option.getNameKey( )) +
					Constants.LINE_SEPARATOR);
		}
	}

	/**
	 * If the observable is a {@link FreeValue value} the text field
	 * corresponding to the value is updated with the values current
	 * text.
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
		if (observable instanceof FreeValue) {
			FreeValue val = (FreeValue) observable;
			String key = val.getDisplayValue( );
			text.setText(key);
		}
	}

}
