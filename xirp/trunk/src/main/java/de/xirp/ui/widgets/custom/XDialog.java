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
 * XDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 03.11.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;

import de.xirp.ui.event.LocaleChangedEvent;
import de.xirp.ui.event.LocaleChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * This dialog is enabled with on-the-fly translation capabilities.
 * 
 * @author Matthias Gernand
 */
public class XDialog extends Dialog {

	/**
	 * The text key.
	 */
	private String textKey;
	/**
	 * The text args.
	 */
	private Object[] textArgs;
	/**
	 * The handler.
	 */
	private II18nHandler handler;
	/**
	 * The
	 * {@link de.xirp.ui.event.LocaleChangedListener locale}
	 * change listener.
	 * 
	 * @see de.xirp.ui.event.LocaleChangedListener
	 */
	private LocaleChangedListener localeListener;

	/**
	 * Constructs a new dialog. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 */
	public XDialog(Shell parent, int style) {
		super(parent, style);
		this.handler = I18n.getGenericI18n( );
		initListeners( );
	}

	/**
	 * Constructs a new dialog. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 */
	public XDialog(Shell parent) {
		super(parent);
		this.handler = I18n.getGenericI18n( );
		initListeners( );
	}

	/**
	 * Constructs a new dialog. This constructor should be used in
	 * plugin-ui environment, because the
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
	public XDialog(Shell parent, int style, II18nHandler handler) {
		super(parent, style);
		this.handler = handler;
		initListeners( );
	}

	/**
	 * Constructs a new dialog. This constructor should be used in
	 * plugin-ui environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param parent
	 *            The parent.
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 */
	public XDialog(Shell parent, II18nHandler handler) {
		super(parent);
		this.handler = handler;
		initListeners( );
	}

	/**
	 * Initializes the control and sets the appearance listeners.
	 */
	private void initListeners() {
		localeListener = new LocaleChangedListener( ) {

			public void localeChanged(LocaleChangedEvent event) {
				if (textKey != null) {
					if (textArgs == null) {
						setTextForLocaleKey(textKey);
					}
					else {
						setTextForLocaleKey(textKey, textArgs);
					}
				}
			}
		};
		ApplicationManager.addLocaleChangedListener(localeListener);
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
		this.textKey = key;
		this.textArgs = objects;
		setText(handler.getString(textKey, textArgs));
	}

	/**
	 * Returns the text key.
	 * 
	 * @return The text key.
	 */
	public String getTextKey() {
		return textKey;
	}

	/**
	 * Returns the text args.
	 * 
	 * @return The args.
	 */
	public Object[] getTextArgs() {
		return textArgs;
	}

	/**
	 * Returns the
	 * {@link de.xirp.util.II18nHandler handler}.
	 * 
	 * @return The handler.
	 * @see de.xirp.util.II18nHandler
	 */
	protected II18nHandler getHandler() {
		return handler;
	}
}
