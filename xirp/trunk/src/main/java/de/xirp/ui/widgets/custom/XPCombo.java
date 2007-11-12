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
 * XPCombo.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.04.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;

import com.swtplus.widgets.PCombo;
import com.swtplus.widgets.combo.ColorComboStrategy;
import com.swtplus.widgets.combo.IComboStrategy;
import com.swtplus.widgets.combo.ListComboStrategy;
import com.swtplus.widgets.combo.TableComboStrategy;
import com.swtplus.widgets.combo.TreeComboStrategy;

import de.xirp.ui.event.LocaleChangedEvent;
import de.xirp.ui.event.LocaleChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;
import de.xirp.util.Variables;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public class XPCombo extends PCombo {

	/**
	 * The
	 * {@link de.xirp.ui.event.LocaleChangedListener locale}
	 * change listener.
	 * 
	 * @see de.xirp.ui.event.LocaleChangedListener *
	 */
	private LocaleChangedListener localeListener;
	/**
	 * The {@link de.xirp.util.II18nHandler handler}.
	 * 
	 * @see de.xirp.util.II18nHandler
	 */
	private II18nHandler handler;
	/**
	 * The tool tip args.
	 */
	private Object[] toolTipArgs;
	/**
	 * The tool tip key.
	 */
	private String toolTipKey;
	/**
	 * The i18n prefix.
	 */
	private String i18nPrefix = ""; //$NON-NLS-1$
	/**
	 * A resource {@link java.util.ResourceBundle bundle}.
	 * 
	 * @see java.util.ResourceBundle
	 */
	private ResourceBundle bundle;

	/**
	 * Enumeration holding constants for indicating the type of this
	 * combo.
	 * 
	 * @author Matthias Gernand
	 */
	public enum ComboType {
		/**
		 * A color chooser.
		 */
		COLOR,
		/**
		 * A combo opening a list.
		 */
		LIST,
		/**
		 * A combo opening a tree.
		 */
		TREE,
		/**
		 * A combo opening a table.
		 */
		TABLE;

		/**
		 * Returns the
		 * {@link com.swtplus.widgets.combo.IComboStrategy strategy}
		 * of the type.
		 * 
		 * @return The strategy.
		 */
		public IComboStrategy getStrategy() {
			switch (this) {
				case COLOR:
					return new ColorComboStrategy(SWT.NONE);
				case LIST:
					return new ListComboStrategy(0);
				case TREE:
					return new TreeComboStrategy(SWT.NONE, SWT.NONE);
				case TABLE:
					return new TableComboStrategy(SWT.NONE, SWT.NONE);
				default:
					return null;
			}
		}
	}

	/**
	 * Constructs a new combo. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param type
	 *            The type.
	 * @see com.swtplus.widgets.PCombo
	 */
	public XPCombo(Composite parent, int style, ComboType type) {
		super(parent, style, type.getStrategy( ));
		this.handler = I18n.getGenericI18n( );
		init( );
	}

	/**
	 * Constructs a new combo. This constructor should be used in
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
	 * @see com.swtplus.widgets.PCombo
	 */
	public XPCombo(Composite parent, int style, ComboType type,
			II18nHandler handler) {
		super(parent, style, type.getStrategy( ));
		this.handler = handler;
		init( );
	}

	/**
	 * Initializes the control and sets the listeners.
	 */
	private void init() {
		addFocusListener(new FocusListener( ) {

			public void focusGained(FocusEvent e) {
				setBackground(ColorManager.getColor(Variables.FOCUS_COLOR));

			}

			public void focusLost(FocusEvent e) {
				setBackground(ColorManager.getColor(Constants.DEFAULT_COLOR_WHITE));

			}

		});

		localeListener = new LocaleChangedListener( ) {

			public void localeChanged(LocaleChangedEvent event) {
				if (toolTipArgs == null) {
					setToolTipTextForLocaleKey(toolTipKey);
				}
				else {
					setToolTipTextForLocaleKey(toolTipKey, toolTipArgs);
				}
				bundle = I18n.getGenericI18n( )
						.getBundle( )
						.getResourceBundle( );
				setBundle(i18nPrefix, bundle);
			}
		};
		ApplicationManager.addLocaleChangedListener(localeListener);

		addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent e) {
				ApplicationManager.removeLocaleChangedListener(localeListener);
			}

		});
		bundle = I18n.getGenericI18n( ).getBundle( ).getResourceBundle( );
		setBundle(i18nPrefix, bundle);
	}

	/**
	 * Sets the i18n prefix.
	 * 
	 * @param i18nPrefix
	 *            The prefix to set.
	 */
	public void setI18nPrefix(String i18nPrefix) {
		this.i18nPrefix = i18nPrefix;
		if (this.i18nPrefix == null) {
			this.i18nPrefix = ""; //$NON-NLS-1$
		}
		setBundle(i18nPrefix, bundle);
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
		this.toolTipKey = key;
		this.toolTipArgs = objects;
		super.setToolTipText(handler.getString(key, objects));
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
