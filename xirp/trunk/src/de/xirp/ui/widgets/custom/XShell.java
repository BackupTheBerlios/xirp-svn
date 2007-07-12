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
 * XShell.java
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
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.xirp.ui.event.AppearanceChangedEvent;
import de.xirp.ui.event.AppearanceChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.util.II18nHandler;
import de.xirp.util.Variables;
import de.xirp.util.mouse.MouseEventSaver;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities.
 * 
 * @author Matthias Gernand
 */
public class XShell extends Shell {

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

	// private final HWidgetAppearanceHandler appearanceHandler;

	// private final HWidgetAppearanceHandler appearanceHandler;

	/**
	 * Constructs a new shell. This constructor should be used in
	 * application-ui environment.
	 */
	public XShell() {
		super( );
		localeHandler = new XWidgetLocaleHandler(this);
		initListeners( );
	}

	/**
	 * Constructs a new shell for the given style. This constructor
	 * should be used in application-UI environment.
	 * 
	 * @param style
	 *            The style.
	 */
	public XShell(int style) {
		super(style);
		localeHandler = new XWidgetLocaleHandler(this);
		initListeners( );
	}

	/**
	 * Constructs a new shell for the given display. This constructor
	 * should be used in application-UI environment.
	 * 
	 * @param display
	 *            The display.
	 */
	public XShell(Display display) {
		super(display);
		localeHandler = new XWidgetLocaleHandler(this);
		initListeners( );
	}

	/**
	 * Constructs a new shell for the given display and style. This
	 * constructor should be used in application-UI environment.
	 * 
	 * @param display
	 *            The display.
	 * @param style
	 *            The style.
	 */
	public XShell(Display display, int style) {
		super(display, style);
		localeHandler = new XWidgetLocaleHandler(this);
		initListeners( );
	}

	/**
	 * Constructs a new shell given shell. This constructor should be
	 * used in application-UI environment.
	 * 
	 * @param parent
	 *            The parent shell.
	 */
	public XShell(Shell parent) {
		super(parent);
		localeHandler = new XWidgetLocaleHandler(this);
		initListeners( );
	}

	/**
	 * Constructs a new shell for the given shell and style. This
	 * constructor should be used in application-ui environment.
	 * 
	 * @param parent
	 *            The parent shell.
	 * @param style
	 *            The style.
	 */
	public XShell(Shell parent, int style) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this);
		initListeners( );
	}

	/**
	 * Constructs a new shell. This constructor should be used in
	 * plugin-ui environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 */
	public XShell(II18nHandler handler) {
		super( );
		localeHandler = new XWidgetLocaleHandler(this, handler);
		initListeners( );
	}

	/**
	 * Constructs a new shell for the given style. This constructor
	 * should be used in plugin-UI environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param style
	 *            The style.
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 */
	public XShell(int style, II18nHandler handler) {
		super(style);
		localeHandler = new XWidgetLocaleHandler(this, handler);
		initListeners( );
	}

	/**
	 * Constructs a new shell for the given display. This constructor
	 * should be used in plugin-UI environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param display
	 *            The display.
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 */
	public XShell(Display display, II18nHandler handler) {
		super(display);
		localeHandler = new XWidgetLocaleHandler(this, handler);
		initListeners( );
	}

	/**
	 * Constructs a new shell for the given display and style. This
	 * constructor should be used in plugin-UI environment, because
	 * the {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param display
	 *            The display.
	 * @param style
	 *            The style.
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 */
	public XShell(Display display, int style, II18nHandler handler) {
		super(display, style);
		localeHandler = new XWidgetLocaleHandler(this, handler);
		initListeners( );
	}

	/**
	 * Constructs a new shell for the given shell. This constructor
	 * should be used in plugin-UI environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param parent
	 *            The parent shell.
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 */
	public XShell(Shell parent, II18nHandler handler) {
		super(parent);
		localeHandler = new XWidgetLocaleHandler(this, handler);
		initListeners( );
	}

	/**
	 * Constructs a new shell for the given shell and style. This
	 * constructor should be used in plugin-UI environment, because
	 * the {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param parent
	 *            The parent shell.
	 * @param style
	 *            The style.
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 */
	public XShell(Shell parent, int style, II18nHandler handler) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this, handler);
		initListeners( );
	}

	/**
	 * Initializes the control and sets the appearance listeners.
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

		addShellListener(new ShellListener( ) {

			@Override
			public void shellClosed(ShellEvent event) {
				MouseEventSaver.monitorClick( );
			}

			@Override
			public void shellActivated(ShellEvent arg0) {
				MouseEventSaver.monitorClick( );
			}

			@Override
			public void shellDeactivated(ShellEvent arg0) {
				MouseEventSaver.monitorClick( );
			}

			@Override
			public void shellDeiconified(ShellEvent arg0) {
				MouseEventSaver.monitorClick( );
			}

			@Override
			public void shellIconified(ShellEvent arg0) {
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
	 * @see org.eclipse.swt.widgets.Widget#checkSubclass()
	 */
	@Override
	protected void checkSubclass() {
	}

}
