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
 * XToolItem.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 05.06.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import de.xirp.util.II18nHandler;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities.
 * 
 * @author Matthias Gernand
 */
public class XToolItem extends ToolItem {

	/**
	 * The locale
	 * {@link de.xirp.ui.widgets.custom.XWidgetLocaleHandler handler}
	 * for this widget.
	 * 
	 * @see de.xirp.ui.widgets.custom.XWidgetLocaleHandler
	 */
	private final XWidgetLocaleHandler localeHandler;

	// private final HWidgetAppearanceHandler appearanceHandler;

	/**
	 * Constructs a new tool item. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param index
	 *            The index.
	 */
	public XToolItem(ToolBar parent, int style, int index) {
		super(parent, style, index);
		localeHandler = new XWidgetLocaleHandler(this);
	}

	/**
	 * Constructs a new tool item. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 */
	public XToolItem(ToolBar parent, int style) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this);
	}

	/**
	 * Constructs a new tool item. This constructor should be used in
	 * plugin-UI environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param index
	 *            The index.
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 */
	public XToolItem(ToolBar parent, int style, int index, II18nHandler handler) {
		super(parent, style, index);
		localeHandler = new XWidgetLocaleHandler(this, handler);
	}

	/**
	 * Constructs a new tool item. This constructor should be used in
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
	public XToolItem(ToolBar parent, int style, II18nHandler handler) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this, handler);
	}

	/**
	 * Adds a selection listener to the tool item. This is used when
	 * for the creation of the item in the chevrons menu if the cool
	 * bar is not completely visible. Overridden, because the listener
	 * must be available over the data object.
	 * 
	 * @see org.eclipse.swt.widgets.ToolItem#addSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	@Override
	public void addSelectionListener(SelectionListener listener) {
		super.addSelectionListener(listener);
		setData("listener", listener); //$NON-NLS-1$
	}

	/**
	 * Removes a selection listener. Overridden, because the listener
	 * must be removed from the data object of this widget.
	 * 
	 * @see org.eclipse.swt.widgets.ToolItem#removeSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	@Override
	public void removeSelectionListener(SelectionListener listener) {
		super.removeSelectionListener(listener);
		setData("listener", null); //$NON-NLS-1$
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
	 * Returns the tool tip text varargs array.
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
	 * @see org.eclipse.swt.widgets.ToolItem#checkSubclass()
	 */
	@Override
	protected void checkSubclass() {
	}
}
