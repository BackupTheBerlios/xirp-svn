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
 * XCoolItem.java
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

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import de.xirp.ui.util.SWTUtil;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities.
 * 
 * @author Matthias Gernand
 */
public class XCoolItem extends CoolItem {

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
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(XCoolItem.class);
	/**
	 * The parent cool bar.
	 */
	private CoolBar parent;
	/**
	 * A menu which will show not visible items
	 */
	private Menu chevronMenu;

	/**
	 * Constructs a new cool item. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 */
	public XCoolItem(CoolBar parent, int style) {
		super(parent, style);
		this.parent = parent;
		localeHandler = new XWidgetLocaleHandler(this);
		initListeners( );
	}

	/**
	 * Constructs a new cool item. This constructor should be used in
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
	public XCoolItem(CoolBar parent, int style, II18nHandler handler) {
		super(parent, style);
		this.parent = parent;
		localeHandler = new XWidgetLocaleHandler(this, handler);
		initListeners( );
	}

	/**
	 * Initializes the control and sets the appearance listeners and
	 * creates the chevrons drop down menu.
	 */
	private void initListeners() {
		addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent event) {
				if (event.detail == SWT.ARROW) {
					CoolItem item = (CoolItem) event.widget;
					Rectangle itemBounds = item.getBounds( );
					Point pt = parent.toDisplay(new Point(itemBounds.x,
							itemBounds.y));
					itemBounds.x = pt.x;
					itemBounds.y = pt.y;
					ToolBar bar = (ToolBar) item.getControl( );

					for (ToolItem itm : bar.getItems( )) {
						Rectangle toolBounds = itm.getBounds( );
						pt = bar.toDisplay(new Point(toolBounds.x, toolBounds.y));
						toolBounds.x = pt.x;
						toolBounds.y = pt.y;

						/*
						 * Figure out the visible portion of the tool
						 * by looking at the intersection of the tool
						 * bounds with the cool item bounds.
						 */
						Rectangle intersection = itemBounds.intersection(toolBounds);

						/*
						 * If the tool is not completely within the
						 * cool item bounds, then it is partially
						 * hidden, and all remaining tools are
						 * completely hidden.
						 */
						if (!intersection.equals(toolBounds)) {
							break;
						}
					}

					/*
					 * Create a menu with items for each of the
					 * completely hidden buttons.
					 */
					SWTUtil.secureDispose(chevronMenu);
					chevronMenu = new Menu(parent);

					for (ToolItem itm : bar.getItems( )) {
						if ((itm.getStyle( ) & SWT.SEPARATOR) == 0) {
							if (itm instanceof XToolItem) {
								XMenuItem menuItem = new XMenuItem(chevronMenu,
										itm.getStyle( ));
								try {
									menuItem.setText(itm.getToolTipText( ));
								}
								catch (RuntimeException e) {
									logClass.info(I18n.getString("HCoolItem.log.addToolTipText", //$NON-NLS-1$
											e.getMessage( )) +
											Constants.LINE_SEPARATOR,
											e);
								}
								menuItem.setSelection(itm.getSelection( ));
								menuItem.setImage(itm.getImage( ));
								menuItem.setEnabled(itm.isEnabled( ));
								menuItem.addSelectionListener((SelectionListener) itm.getData("listener")); //$NON-NLS-1$
							}
							else {
								XMenuItem menuItem = new XMenuItem(chevronMenu,
										SWT.PUSH);
								menuItem.setText(itm.getToolTipText( ));
								menuItem.setImage(itm.getImage( ));
								menuItem.setEnabled(false);
							}
						}
					}

					/*
					 * Drop down the menu below the chevron, with the
					 * left edges aligned.
					 */
					pt = parent.toDisplay(new Point(event.x, event.y));
					chevronMenu.setLocation(pt.x, pt.y);
					chevronMenu.setVisible(true);
				}
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
	 * class
	 * 
	 * @see org.eclipse.swt.widgets.Widget#checkSubclass()
	 */
	@Override
	protected void checkSubclass() {
	}
}
