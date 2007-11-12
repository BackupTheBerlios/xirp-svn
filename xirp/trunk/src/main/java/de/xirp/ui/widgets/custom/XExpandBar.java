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
 * XExpandBar.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 01.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;

import de.xirp.ui.event.AppearanceChangedEvent;
import de.xirp.ui.event.AppearanceChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.util.II18nHandler;
import de.xirp.util.Variables;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities.
 * 
 * @author Matthias Gernand
 */
public class XExpandBar extends ExpandBar {

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
	 * Constructs a new expand bar. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 */
	public XExpandBar(Composite parent, int style) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this);
		initListeners( );
	}

	/**
	 * Constructs a new expand bar. This constructor should be used in
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
	 * @see de.xirp.util.II18nHandler
	 */
	public XExpandBar(Composite parent, int style, II18nHandler handler) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this, handler);
		initListeners( );
	}

	/**
	 * Initializes the listeners.
	 */
	private void initListeners() {
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
	 * Returns the tool tip text i18n key.
	 * 
	 * @return The i18n key.
	 */
	public String getToolTipTextLocaleKey() {
		return localeHandler.getToolTipTextKey( );
	}

	/**
	 * Returns the tool tip text varargs array.
	 * 
	 * @return The varargs array.
	 */
	public Object[] getToolTipTextArgs() {
		return localeHandler.getToolTipTextArgs( );
	}

	/**
	 * Overridden, because we want to create a specialization of the
	 * class
	 * 
	 * @see org.eclipse.swt.widgets.Widget#checkSubclass()
	 */
	@Override
	protected void checkSubclass() {
	}
}
