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
 * XNegativeSlider.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 21.09.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;

import de.xirp.ui.event.LocaleChangedEvent;
import de.xirp.ui.event.LocaleChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * A Slider which supports negative values and tooltip translation.
 * 
 * @author Rabea Gransberger
 */
public class XNegativeSlider extends Slider {

	/**
	 * Initial minimum for the slider if no minimum was set: {@value}
	 */
	public static final int INITIAL_MINIMUM = 0;
	/**
	 * Initial maximum for the slider if no maximum was set: {@value}
	 */
	public static final int INITIAL_MAXIMUM = 100;

	/**
	 * Listener which is informed if the {@link java.util.Locale}
	 * which the application uses has changed.
	 */
	private LocaleChangedListener localeListener;
	/**
	 * Translation key for the tooltip of the widget
	 */
	private String tooltipKey;
	/**
	 * The arguments used for replacing parameters for the tooltip
	 * translation
	 */
	private Object[] objects;
	/**
	 * The handler used for translation
	 */
	private II18nHandler handler;
	/**
	 * The current maximum value of this slider
	 */
	private int maximum = INITIAL_MAXIMUM;
	/**
	 * The minimum value of this slider
	 */
	private int minimum = INITIAL_MINIMUM;
	/**
	 * Flag if the minimum was set
	 */
	private boolean minimumSet = false;
	/**
	 * Flag if the maximum was set
	 */
	private boolean maximumSet = false;

	/**
	 * Constructs a new negative slider with an initial minimum of
	 * {@value #INITIAL_MINIMUM} and maximum of
	 * {@value #INITIAL_MAXIMUM}. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 * @param style
	 */
	public XNegativeSlider(Composite parent, int style) {
		super(parent, style);
		this.handler = I18n.getGenericI18n( );
		init( );
	}

	/**
	 * * Constructs a new negative slider with an initial minimum of
	 * {@value #INITIAL_MINIMUM} and maximum of
	 * {@value #INITIAL_MAXIMUM}. This constructor should be used in
	 * plugin-UI environment, because the given
	 * {@link de.xirp.util.II18nHandler handler} is used
	 * for translations.
	 * 
	 * @param parent
	 * @param style
	 * @param handler
	 * @see de.xirp.util.II18nHandler
	 */
	public XNegativeSlider(Composite parent, int style, II18nHandler handler) {
		super(parent, style);
		this.handler = handler;
		init( );
	}

	/**
	 * Initializes the control and adds the locale changed listener.
	 */
	private void init() {
		localeListener = new LocaleChangedListener( ) {

			public void localeChanged(@SuppressWarnings("unused")
			LocaleChangedEvent event) {
				if (tooltipKey != null) {
					if (objects != null) {
						setToolTipTextForLocaleKey(tooltipKey, objects);
					}
					else {
						setToolTipTextForLocaleKey(tooltipKey);
					}
				}
			}
		};
		ApplicationManager.addLocaleChangedListener(localeListener);

		addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(@SuppressWarnings("unused")
			DisposeEvent e) {
				ApplicationManager.removeLocaleChangedListener(localeListener);
			}

		});

		// update tooltip
		addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent evt) {
				getSelection( );
			}

		});

	}

	/**
	 * @see org.eclipse.swt.widgets.Slider#getMaximum()
	 */
	@Override
	public int getMaximum() {
		return maximum;
	}

	/**
	 * @see org.eclipse.swt.widgets.Slider#getMinimum()
	 */
	@Override
	public int getMinimum() {
		return minimum;
	}

	/**
	 * @see org.eclipse.swt.widgets.Slider#getSelection()
	 */
	@Override
	public int getSelection() {
		int originalSelection = super.getSelection( );
		int ret = originalSelection;
		if (minimum < 0) {
			if ((getStyle( ) & SWT.VERTICAL) != 0) {
				ret = Math.abs(minimum) + maximum - originalSelection + minimum;
			}
			else {
				ret = minimum + originalSelection;
			}
		}
		updateTooltip(ret);
		return ret;
	}

	/**
	 * @throws SWTException
	 *             thrown if
	 *             <ul>
	 *             <li> the maximum was already set.</li>
	 *             <li> the minimum was not yet set.</li>
	 *             <li>the maximum is smaller then zero</li>
	 *             <li>the maximum is smaller then the minimum</li>
	 *             </ul>
	 * @see org.eclipse.swt.widgets.Slider#setMaximum(int)
	 */
	@Override
	public void setMaximum(int max) throws SWTException {
		if (maximumSet) {
			throw new SWTException(I18n.getString("HNegativeSlider.exception.subsequentCallToSetMaximumIgnored")); //$NON-NLS-1$
		}

		if (!minimumSet) {
			throw new SWTException(I18n.getString("HNegativeSlider.exception.callSetMinimumPriorToSetMaximum")); //$NON-NLS-1$
		}
		if (max < 0) {
			throw new SWTException(I18n.getString("HNegativeSlider.exception.maxLessZero", //$NON-NLS-1$
					max));
		}
		if (max < minimum) {
			throw new SWTException(I18n.getString("HNegativeSlider.exception.maxLessMin", //$NON-NLS-1$
					max,
					minimum));
		}

		if (minimum < 0) {
			super.setMaximum(max + Math.abs(minimum) + 10);
		}
		else {
			super.setMaximum(max);
		}
		maximum = max;

		maximumSet = true;
	}

	/**
	 * @throws SWTException
	 *             thrown if the minimum was already set
	 * @see org.eclipse.swt.widgets.Slider#setMinimum(int)
	 */
	@Override
	public void setMinimum(int min) {
		if (minimumSet) {
			throw new SWTException(I18n.getString("HNegativeSlider.exception.subsequentCallToSetMinimumIgnored")); //$NON-NLS-1$
		}
		if (min < 0) {
			super.setMinimum(0);
		}
		else {
			super.setMinimum(min);
		}

		minimum = min;

		minimumSet = true;
	}

	/**
	 * @see org.eclipse.swt.widgets.Slider#setSelection(int)
	 */
	@Override
	public void setSelection(int selection) {

		int boundedSelection = Math.max(minimum, selection);
		boundedSelection = Math.min(maximum, boundedSelection);

		int sel = boundedSelection;
		if (minimum < 0) {
			if ((getStyle( ) & SWT.VERTICAL) != 0) {
				sel = Math.abs(minimum) + maximum - boundedSelection + minimum;

			}
			else {
				sel = boundedSelection - minimum;
			}
		}
		updateTooltip(boundedSelection);

		super.setSelection(sel);
	}

	/**
	 * Sets the tooltip to the current value if no key is set or the
	 * key is set without any arguments.
	 * 
	 * @param value
	 *            the value to use for the tooltip
	 */
	private void updateTooltip(int value) {
		if (tooltipKey == null) {
			super.setToolTipText(Integer.toString(value));
		}
		else if (objects == null || objects.length == 0) {
			if (handler != null) {
				super.setToolTipText(handler.getString(tooltipKey, value));
			}
		}
	}

	/**
	 * Sets the tooltip of the underlying widget which is translated
	 * from the given key replacing any parameters of the translation
	 * with the given arguments.<br>
	 * If no tooltip is set, the current value of the slider is shown.
	 * If a key but not arguments were given the key is translated
	 * using the current value as argument.
	 * 
	 * @param key
	 *            The key for translation.
	 * @param objects
	 *            The arguments to use for replacing parameters of the
	 *            translation.
	 * @see org.eclipse.swt.widgets.Slider#setToolTipText(String)
	 */
	public void setToolTipTextForLocaleKey(String key, Object... objects) {
		tooltipKey = key;
		this.objects = objects;
		if (handler != null) {
			super.setToolTipText(handler.getString(tooltipKey, objects));
			// update tooltip
			getSelection( );
		}
	}

	/**
	 * Overridden, because we want to create a specialization of the
	 * class.
	 * 
	 * @see org.eclipse.swt.widgets.Widget#checkSubclass()
	 */
	@Override
	protected void checkSubclass() {
		// do nothing
	}

}
