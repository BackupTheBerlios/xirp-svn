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
 * SpinnerRenderer.java
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
import org.eclipse.swt.widgets.Composite;

import de.xirp.settings.IValue;
import de.xirp.settings.Option;
import de.xirp.settings.SpinnerValue;
import de.xirp.ui.event.ValueChangedEvent;
import de.xirp.ui.event.ValueChangedListener;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.XLabel;
import de.xirp.ui.widgets.custom.XStyledSpinner;
import de.xirp.ui.widgets.custom.XStyledSpinner.SpinnerStyle;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * Renders options in spinner style.<br>
 * Synchronization over observers with the current data is supported.<br>
 * 
 * @author Rabea Gransberger
 */
public class SpinnerRenderer extends AbstractOptionRenderer {

	/**
	 * log4j Logger of this Class
	 */
	private static Logger logClass = Logger.getLogger(SpinnerRenderer.class);

	/**
	 * The spinner which is used
	 */
	private XStyledSpinner spinner;

	/**
	 * Creates a label with the name of the option. Creates a
	 * {@link XStyledSpinner spinner} for the last option found which
	 * is of type {@link SpinnerValue}. Internationalization is
	 * supported.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.renderer.IOptionRenderer#render(Composite,
	 *      Option)
	 */
	public void render(Composite parent, Option option) {
		XLabel l = new XLabel(parent, SWT.NONE, option.getI18n( ));
		l.setTextForLocaleKey(option.getNameKey( ), option.getNameKeyArgs( ));
		SWTUtil.setGridData(l, true, false, SWT.LEFT, SWT.CENTER, 1, 1);

		SpinnerValue last = null;
		for (IValue value : option.getValues( )) {
			if (value instanceof SpinnerValue) {
				last = (SpinnerValue) value;
			}
		}

		if (last != null) {
			last.addObserverToValue(this);
			final SpinnerValue actualSelection = last;
			boolean isNegative = actualSelection.getMin( ) < 0;
			boolean isDouble = actualSelection.isUseDouble( );
			spinner = new XStyledSpinner(parent,
					SWT.NONE,
					(isNegative ? SpinnerStyle.NEGATIVE : SpinnerStyle.NORMAL) |
							(isDouble ? SpinnerStyle.DOUBLE
									: SpinnerStyle.NORMAL));
			spinner.setMinimum(actualSelection.getMin( ));
			spinner.setMaximum(actualSelection.getMax( ));
			spinner.setIncrement(actualSelection.getStep( ));
			spinner.addValueChangedListener(new ValueChangedListener( ) {

				public void valueChanged(@SuppressWarnings("unused")
				ValueChangedEvent event) {
					actualSelection.setValue(spinner.getSelectionDouble( ),
							true);
				}

			});

			SWTUtil.setGridData(spinner,
					true,
					false,
					SWT.FILL,
					SWT.CENTER,
					1,
					1);
			spinner.setSelection(actualSelection.getCurrentSpinnerValue( ));
		}
		else {
			XLabel error = new XLabel(parent, SWT.NONE);
			error.setToolTipTextForLocaleKey("SpinnerRenderer.log.rendererCanOnlyBeApplyedToSpinnervalue", option.getNameKey( ));//$NON-NLS-1$
			SWTUtil.setGridData(error, true, false, SWT.FILL, SWT.CENTER, 1, 1);
			logClass.warn(I18n.getString("SpinnerRenderer.log.rendererCanOnlyBeApplyedToSpinnervalue", //$NON-NLS-1$
					option.getNameKey( )) +
					Constants.LINE_SEPARATOR);
		}
	}

	/**
	 * If the observable is a {@link SpinnerValue spinner} value the
	 * spinner is updated with the current value.
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
		if (observable instanceof SpinnerValue) {
			SpinnerValue val = (SpinnerValue) observable;
			spinner.setSelection(val.getCurrentSpinnerValue( ));
		}
	}

}
