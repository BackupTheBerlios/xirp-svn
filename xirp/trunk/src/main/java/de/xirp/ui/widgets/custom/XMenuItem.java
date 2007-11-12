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
 * XMenuItem.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 24.10.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import de.xirp.util.II18nHandler;
import de.xirp.util.mouse.MouseEventSaver;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities.
 * 
 * @author Matthias Gernand
 */
public class XMenuItem extends MenuItem {

	/**
	 * The locale
	 * {@link de.xirp.ui.widgets.custom.XWidgetLocaleHandler handler}
	 * for this widget.
	 * 
	 * @see de.xirp.ui.widgets.custom.XWidgetLocaleHandler
	 */
	private final XWidgetLocaleHandler localeHandler;

	// private final HWidgetAppearanceHandler appearanceHandler;

	// private final HWidgetAppearanceHandler appearanceHandler;

	/**
	 * Constructs a new menu item. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 */
	public XMenuItem(Menu parent, int style) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this);
		initSelectionListener( );
	}

	/**
	 * Constructs a new menu item. This constructor should be used in
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
	public XMenuItem(Menu parent, int style, II18nHandler handler) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this, handler);
		initSelectionListener( );
	}

	/**
	 * 
	 */
	private void initSelectionListener() {
		this.addSelectionListener(new SelectionListener( ) {

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				// System.out.println(event.widget.getDisplay( )
				// .getCursorLocation( )
				// + " " + event);
				MouseEventSaver.monitorClick( );
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				// System.out.println(event.widget.getDisplay( )
				// .getCursorLocation( )
				// + " " + event);
				MouseEventSaver.monitorClick( );
			}

		});

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
	 * Returns the text i18n key.
	 * 
	 * @return The i18n key.
	 */
	public String getTextLocaleKey() {
		return localeHandler.getTextKey( );
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
	 * Overridden, because we want to create a specialization of the
	 * class.
	 * 
	 * @see org.eclipse.swt.widgets.MenuItem#checkSubclass()
	 */
	@Override
	protected void checkSubclass() {
	}

}
