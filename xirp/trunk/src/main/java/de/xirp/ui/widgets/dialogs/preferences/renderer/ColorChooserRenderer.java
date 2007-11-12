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
 * ColorChooserRenderer.java
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import com.swtplus.widgets.PCombo;
import com.swtplus.widgets.combo.NamedRGB;

import de.xirp.settings.IValue;
import de.xirp.settings.Option;
import de.xirp.settings.RGBValue;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.XLabel;
import de.xirp.ui.widgets.custom.XPCombo;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This renderer shows a color chooser for a single {@link RGBValue}
 * of an option.<br>
 * Synchronization over observers with the current data is supported.<br>
 * 
 * @author Rabea Gransberger
 */
public class ColorChooserRenderer extends AbstractOptionRenderer {

	/**
	 * log4j Logger of this Class
	 */
	private static Logger logClass = Logger.getLogger(ColorChooserRenderer.class);

	/**
	 * The combo box showing the color chooser
	 */
	private XPCombo pCombo;

	/**
	 * Creates a label using the options name and a color chooser
	 * widget with translated color names. Only the last value of of
	 * the option is used.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.renderer.IOptionRenderer#render(Composite,
	 *      Option)
	 */
	public void render(Composite parent, Option option) {
		XLabel l = new XLabel(parent, SWT.NONE, option.getI18n( ));
		l.setTextForLocaleKey(option.getNameKey( ), option.getNameKeyArgs( ));
		SWTUtil.setGridData(l, true, false, SWT.LEFT, SWT.CENTER, 1, 1);

		RGBValue last = null;
		for (IValue value : option.getValues( )) {
			if (value instanceof RGBValue) {
				last = (RGBValue) value;
			}
		}

		if (last != null) {
			final RGBValue actualSelection = last;
			actualSelection.addObserverToValue(this);
			pCombo = new XPCombo(parent,
					PCombo.BORDER | PCombo.READ_ONLY,
					XPCombo.ComboType.COLOR);
			pCombo.setI18nPrefix("swtplus."); //$NON-NLS-1$
			pCombo.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(@SuppressWarnings("unused")
				SelectionEvent event) {
					// update the data
					RGB rgb = ((NamedRGB) pCombo.getValue( )).getRGB( );
					actualSelection.setRGB(rgb, true);
				}

			});
			SWTUtil.setGridData(pCombo, true, false, SWT.FILL, SWT.CENTER, 1, 1);
			pCombo.setValue(new NamedRGB(actualSelection.getRGB(actualSelection.getDisplayValue( ))));
		}
		else {
			XLabel error = new XLabel(parent, SWT.NONE);
			error.setToolTipTextForLocaleKey("ColorChooserRenderer.log.rendererCanOnlyBeAppliedToRGBValue", option.getNameKey( ));//$NON-NLS-1$
			SWTUtil.setGridData(error, true, false, SWT.FILL, SWT.CENTER, 1, 1);
			logClass.warn(I18n.getString("ColorChooserRenderer.log.rendererCanOnlyBeAppliedToRGBValue", //$NON-NLS-1$
					option.getNameKey( )) +
					Constants.LINE_SEPARATOR);
		}
	}

	/**
	 * If the observable is a {@link RGBValue RGB} the color chooser
	 * is updated with the current color of the value.
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
		if (observable instanceof RGBValue) {
			RGBValue val = (RGBValue) observable;
			RGB rgb = val.getRGB(val.getDisplayValue( ));
			pCombo.setValue(new NamedRGB(rgb));
		}
	}
}
