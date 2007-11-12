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
 * XTabFolder.java
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;

import de.xirp.ui.event.AppearanceChangedEvent;
import de.xirp.ui.event.AppearanceChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.util.II18nHandler;
import de.xirp.util.Variables;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities. Items on this folder may be closed with
 * a middle click on the item.
 * 
 * @author Matthias Gernand
 */
public class XTabFolder extends CTabFolder {

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

	/**
	 * Constructs a new tab folder. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 */
	public XTabFolder(Composite parent, int style) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this);
		initListeners( );
	}

	/**
	 * Constructs a new tab folder. This constructor should be used in
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
	public XTabFolder(Composite parent, int style, II18nHandler handler) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this, handler);
		initListeners( );
	}

	/**
	 * Initializes the control and sets the appearance listeners.
	 */
	private void initListeners() {

		setSelectionForeground(ColorManager.getColor(Variables.TAB_TEXT_COLOR));
		setSelectionBackground(ColorManager.getColor(Variables.TAB_COLOR));
		setSimple(false);
		setBorderVisible(true);
		setForeground(ColorManager.getColor(Variables.INACTIVE_TAB_TEXT_COLOR));
		setBackground(ColorManager.getColor(Variables.BACKGROUND_COLOR));
		setTabHeight(15);

		// Dipose Items on middle click
		addMouseListener(new MouseAdapter( ) {

			@Override
			public void mouseUp(MouseEvent evt) {

				if (evt.stateMask == SWT.BUTTON2) {

					for (CTabItem c : getItems( )) {
						if (c.getBounds( ).contains(evt.x, evt.y)) {
// System.out.println("Closing " + c.getText( ));
							c.dispose( );
							break;

						}
					}
				}
			}

		});

		appearanceListener = new AppearanceChangedListener( ) {

			public void appearanceChanged(AppearanceChangedEvent event) {
				setSelectionForeground(ColorManager.getColor(Variables.TAB_TEXT_COLOR));
				setSelectionBackground(ColorManager.getColor(Variables.TAB_COLOR));
				setForeground(ColorManager.getColor(Variables.INACTIVE_TAB_TEXT_COLOR));
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

}
