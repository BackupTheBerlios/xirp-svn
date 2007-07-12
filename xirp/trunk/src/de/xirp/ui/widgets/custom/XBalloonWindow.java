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
 * XBalloonWindow.java
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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.novocode.naf.swt.custom.BalloonWindow;

import de.xirp.ui.event.LocaleChangedEvent;
import de.xirp.ui.event.LocaleChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities.
 * 
 * @author Matthias Gernand
 */
public class XBalloonWindow extends BalloonWindow {

	/**
	 * The message key.
	 */
	private String messageKey;
	/**
	 * The message args.
	 */
	private Object[] messageArgs;
	/**
	 * The {@link de.xirp.util.II18nHandler handler}.
	 */
	private II18nHandler handler;
	/**
	 * The
	 * {@link de.xirp.ui.event.LocaleChangedListener locale}
	 * change listener.
	 */
	private LocaleChangedListener localeListener;

	/**
	 * Constructs a new balloon window. This constructor should be
	 * used in application-UI environment.
	 * 
	 * @param display
	 *            The display.
	 * @param style
	 *            The style.
	 * @see com.novocode.naf.swt.custom.BalloonWindow
	 */
	public XBalloonWindow(Display display, int style) {
		super(display, style);
		this.handler = I18n.getGenericI18n( );
		initListeners( );
	}

	/**
	 * Constructs a new balloon window. This constructor should be
	 * used in application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @see com.novocode.naf.swt.custom.BalloonWindow
	 */
	public XBalloonWindow(Shell parent, int style) {
		super(parent, style);
		this.handler = I18n.getGenericI18n( );
		initListeners( );
	}

	/**
	 * Constructs a new balloon window. This constructor should be
	 * used in plugin-UI environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param display
	 *            The display.
	 * @param style
	 *            The style.
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 * @see com.novocode.naf.swt.custom.BalloonWindow
	 */
	public XBalloonWindow(Display display, int style, II18nHandler handler) {
		super(display, style);
		this.handler = handler;
		initListeners( );
	}

	/**
	 * Constructs a new balloon window. This constructor should be
	 * used in plugin-UI environment, because the
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
	 * @see com.novocode.naf.swt.custom.BalloonWindow
	 */
	public XBalloonWindow(Shell parent, int style, II18nHandler handler) {
		super(parent, style);
		this.handler = handler;
		initListeners( );
	}

	/**
	 * Initializes the listeners.
	 */
	private void initListeners() {
		localeListener = new LocaleChangedListener( ) {

			public void localeChanged(LocaleChangedEvent event) {
				if (messageKey != null) {
					if (messageArgs == null) {
						setMessageForLocaleKey(messageKey);
					}
					else {
						setMessageForLocaleKey(messageKey, messageArgs);
					}
				}
			}
		};
		ApplicationManager.addLocaleChangedListener(localeListener);
	}

	/**
	 * Sets the corresponding message form the given i18n key and the
	 * varargs to the widget, using the locale handler.
	 * 
	 * @param key
	 *            The i18n key.
	 * @param objects
	 *            The varargs to set.
	 */
	public void setMessageForLocaleKey(String key, Object... objects) {
		this.messageKey = key;
		this.messageArgs = objects;
		setMessage(handler.getString(messageKey, messageArgs));
	}

	/**
	 * Returns the message key.
	 * 
	 * @return The message key.
	 */
	public String getMessageKey() {
		return messageKey;
	}

	/**
	 * Returns the message args.
	 * 
	 * @return The args.
	 */
	public Object[] getMessageArgs() {
		return messageArgs;
	}
}
