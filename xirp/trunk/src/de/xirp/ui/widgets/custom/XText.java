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
 * XText.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 01.06.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.xirp.ui.event.AppearanceChangedEvent;
import de.xirp.ui.event.AppearanceChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.util.Constants;
import de.xirp.util.II18nHandler;
import de.xirp.util.Variables;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities.
 * 
 * @author Matthias Gernand
 */
public class XText extends Text {

	/**
	 * A
	 * {@link de.xirp.ui.event.AppearanceChangedListener appearance}
	 * change listener.
	 * 
	 * @see de.xirp.ui.event.AppearanceChangedListener
	 */
	private AppearanceChangedListener appearanceListener;
	/**
	 * The locale
	 * {@link de.xirp.ui.widgets.custom.XWidgetLocaleHandler handler}
	 * for this widget.
	 * 
	 * @see de.xirp.ui.widgets.custom.XWidgetLocaleHandler
	 */
	private final XWidgetLocaleHandler localeHandler;
	/**
	 * A flag indicating if the focus color should be used.
	 */
	private boolean focusColor = false;

	// private final HWidgetAppearanceHandler appearanceHandler;

	/**
	 * Constructs a new text. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param focusColor
	 *            A flag, indicating if the text should change it's
	 *            background color on focus events.
	 */
	public XText(Composite parent, int style, boolean focusColor) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this);
		this.focusColor = focusColor;
		initListeners( );
	}

	/**
	 * Constructs a new text. This constructor should be used in
	 * plugin-UI environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param handler
	 *            The handler.
	 * @param focusColor
	 *            A flag, indicating if the text should change it's
	 *            background color on focus events.
	 * @see de.xirp.util.II18nHandler
	 */
	public XText(Composite parent, int style, II18nHandler handler,
			boolean focusColor) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this, handler);
		this.focusColor = focusColor;
		initListeners( );
	}

	/**
	 * Initializes the control and sets the appearance listeners if
	 * requested.
	 */
	private void initListeners() {
		if (focusColor) {
			addFocusListener(new FocusAdapter( ) {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.FocusAdapter#focusGained(org.eclipse.swt.events.FocusEvent)
				 */
				@Override
				public void focusGained(FocusEvent e) {
					setBackground(ColorManager.getColor(Variables.FOCUS_COLOR));
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
				 */
				@Override
				public void focusLost(FocusEvent e) {
					setBackground(ColorManager.getColor(Constants.DEFAULT_COLOR_WHITE));
				}

			});
		}
		else {
			setBackground(ColorManager.getColor(Variables.BACKGROUND_COLOR));

			appearanceListener = new AppearanceChangedListener( ) {

				public void appearanceChanged(AppearanceChangedEvent event) {
					setBackground(ColorManager.getColor(Variables.BACKGROUND_COLOR));
				}
			};

			ApplicationManager.addAppearanceChangedListener(appearanceListener);

			addDisposeListener(new DisposeListener( ) {

				public void widgetDisposed(DisposeEvent e) {
					ApplicationManager.removeAppearanceChangedListener(appearanceListener);
				}
			});
		}
	}

	/**
	 * Sets the corresponding text form the given i18n key and the
	 * varargs to the widget, using the locale handler.
	 * 
	 * @param key
	 *            The i18n key.
	 * @param objects
	 *            The varargs to set.
	 */
	public void setTextForLocaleKey(String key, Object... objects) {
		localeHandler.setText(key, objects);
	}

	/**
	 * Sets the corresponding tool tip text form the given i18n key
	 * and the varargs to the widget, using the locale handler.
	 * 
	 * @param key
	 *            The i18n key.
	 * @param objects
	 *            The varargs to set.
	 */
	public void setToolTipTextForLocaleKey(String key, Object... objects) {
		localeHandler.setToolTipText(key, objects);
	}

	/**
	 * Returns the text i18n key.
	 * 
	 * @return The i18n key.
	 */
	public String getTextLocaleKey() {
		return localeHandler.getTextKey( );
	}

	/**
	 * Returns the tool tip text i18n key.
	 * 
	 * @return The i18n key.
	 */
	public String getToolTipTextLocaleKey() {
		return localeHandler.getToolTipTextKey( );
	}

	/**
	 * Returns the text varargs array.
	 * 
	 * @return The varargs array.
	 */
	public Object[] getTextArgs() {
		return localeHandler.getTextArgs( );
	}

	/**
	 * Returns the text varargs array.
	 * 
	 * @return The varargs array.
	 */
	public Object[] getToolTipTextArgs() {
		return localeHandler.getToolTipTextArgs( );
	}

	/**
	 * Overridden, because we want to create a specialization of the
	 * class.
	 * 
	 * @see org.eclipse.swt.widgets.Widget#checkSubclass()
	 */
	@Override
	protected void checkSubclass() {
	}

}
