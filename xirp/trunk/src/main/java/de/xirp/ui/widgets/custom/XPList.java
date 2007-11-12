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
 * XPList.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 22.10.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

import com.swtplus.widgets.PList;
import com.swtplus.widgets.PListItem;
import com.swtplus.widgets.list.IListStrategy;
import com.swtplus.widgets.list.ListBarListStrategy;
import com.swtplus.widgets.list.SimpleListStrategy;
import com.swtplus.widgets.list.TitleAndDescListStrategy;

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
public class XPList extends PList {

	/**
	 * The
	 * {@link de.xirp.ui.event.LocaleChangedListener locale}
	 * change listener.
	 * 
	 * @see de.xirp.ui.event.LocaleChangedListener *
	 */
	private LocaleChangedListener localeListener;
	/**
	 * The tool tip key.
	 */
	private String tooltipKey;
	/**
	 * The {@link de.xirp.util.II18nHandler handler}.
	 * 
	 * @see de.xirp.util.II18nHandler
	 */
	private II18nHandler handler;
	/**
	 * The args.
	 */
	private Object[] objects;
	/**
	 * The type of the list.
	 */
	private ListType type;

	/**
	 * Enumeration holding constants for indicating the type of the
	 * list.
	 * 
	 * @author Matthias Gernand
	 */
	public enum ListType {
		/**
		 * A simple list with image items.
		 */
		SIMPLE,
		/**
		 * A list with items containing bigger images.
		 */
		LIST_BAR,
		/**
		 * A list with items containing big images and a title and
		 * description.
		 */
		TITLE_AND_DESC;

		/**
		 * Returns the
		 * {@link com.swtplus.widgets.list.IListStrategy strategy} of
		 * the list.
		 * 
		 * @return The strategy.
		 */
		public IListStrategy getStrategy() {
			switch (this) {
				case SIMPLE:
					return new SimpleListStrategy(0);
				case LIST_BAR:
					return new ListBarListStrategy(ListBarListStrategy.HOVER |
							ListBarListStrategy.ROUNDED);
				case TITLE_AND_DESC:
					return new TitleAndDescListStrategy(0);
				default:
					return null;
			}
		}
	}

	/**
	 * Constructs a new list. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param type
	 *            The type.
	 * @see com.swtplus.widgets.PList
	 */
	public XPList(Composite parent, int style, ListType type) {
		super(parent, style, type.getStrategy( ));
		this.type = type;
		this.handler = I18n.getGenericI18n( );
		init( );
	}

	/**
	 * Constructs a new list. This constructor should be used in
	 * plugin-UI environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param type
	 *            The type.
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 * @see com.swtplus.widgets.PList
	 */
	public XPList(Composite parent, int style, ListType type,
			II18nHandler handler) {
		super(parent, style, type.getStrategy( ));
		this.handler = handler;
		init( );
	}

	/**
	 * Initializes the control and sets the listeners.
	 */
	private void init() {
		localeListener = new LocaleChangedListener( ) {

			public void localeChanged(LocaleChangedEvent event) {
				if (tooltipKey != null) {
					if (objects != null) {
						setToolTipTextForLocaleKey(tooltipKey, objects);
					}
					else {
						setToolTipTextForLocaleKey(tooltipKey);
					}
				}
				setItemTexts( );
				redraw( );
			}
		};
		ApplicationManager.addLocaleChangedListener(localeListener);

		addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent e) {
				ApplicationManager.removeLocaleChangedListener(localeListener);

			}

		});
	}

	/**
	 * Notifies all list items of a locale change. Workaround for
	 * buggy SWTPlus.
	 */
	private void setItemTexts() {
		for (PListItem itm : getItems( )) {
			if (itm instanceof XPListItem) {
				((XPListItem) itm).localeChanged( );
			}
		}
	}

	/**
	 * Overridden setToolTipText method. This method gets the
	 * translation of the given string and sets the new tooltip text
	 * in the actual language.
	 * 
	 * @see org.eclipse.swt.widgets.Control#setToolTipText(java.lang.String)
	 */
	public void setToolTipTextForLocaleKey(String key, Object... objects) {
		tooltipKey = key;
		this.objects = objects;
		if (handler != null) {
			super.setToolTipText(handler.getString(tooltipKey, objects));
		}
	}

	/**
	 * Returns the type of the list.
	 * 
	 * @return The type.
	 */
	public ListType getType() {
		return type;
	}
}
