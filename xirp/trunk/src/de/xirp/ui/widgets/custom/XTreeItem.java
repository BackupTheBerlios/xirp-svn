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
 * XTreeItem.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 30.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import java.util.HashMap;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

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
 * @author Rabea Gransberger
 */
public class XTreeItem extends TreeItem {

	/**
	 * Listener which is called if the {@link java.util.Locale} used
	 * by the application has changed.
	 */
	private LocaleChangedListener localeListener;
	/**
	 * The handler to use for internationalization.
	 */
	private II18nHandler handler;
	/**
	 * Keys for translation of the columns of tree item
	 */
	private HashMap<Integer, String> textKeys = new HashMap<Integer, String>( );
	/**
	 * Arguments for the text of the columns of the tree item
	 */
	private HashMap<Integer, Object[]> args = new HashMap<Integer, Object[]>( );
	/**
	 * The text key for a single column item.
	 */
	private String textKey;
	/**
	 * The text arguments for a single column item.
	 */
	private Object[] textArgs;

	/**
	 * Constructs a new tree item. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param index
	 *            The index.
	 */
	public XTreeItem(Tree parent, int style, int index) {
		super(parent, style, index);
		this.handler = I18n.getGenericI18n( );
		init( );
	}

	/**
	 * Constructs a new tree item. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 */
	public XTreeItem(Tree parent, int style) {
		super(parent, style);
		this.handler = I18n.getGenericI18n( );
		init( );
	}

	/**
	 * Constructs a new tree item. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param index
	 *            the index.
	 */
	public XTreeItem(TreeItem parent, int style, int index) {
		super(parent, style, index);
		this.handler = I18n.getGenericI18n( );
		init( );
	}

	/**
	 * Constructs a new tree item. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 */
	public XTreeItem(TreeItem parent, int style) {
		super(parent, style);
		this.handler = I18n.getGenericI18n( );
		init( );
	}

	/**
	 * Constructs a new tree item. This constructor should be used in
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
	public XTreeItem(Tree parent, int style, int index, II18nHandler handler) {
		super(parent, style, index);
		this.handler = handler;
		init( );
	}

	/**
	 * Constructs a new tree item. This constructor should be used in
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
	public XTreeItem(Tree parent, int style, II18nHandler handler) {
		super(parent, style);
		this.handler = handler;
		init( );
	}

	/**
	 * Constructs a new tree item. This constructor should be used in
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
	public XTreeItem(TreeItem parent, int style, int index, II18nHandler handler) {
		super(parent, style, index);
		this.handler = handler;
		init( );
	}

	/**
	 * Constructs a new tree item. This constructor should be used in
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
	public XTreeItem(TreeItem parent, int style, II18nHandler handler) {
		super(parent, style);
		this.handler = handler;
		init( );
	}

	/**
	 * Initializes the control and sets the listeners.
	 */
	private void init() {
		localeListener = new LocaleChangedListener( ) {

			public void localeChanged(@SuppressWarnings("unused")
			LocaleChangedEvent event) {
				int cnt = getParent( ).getItemCount( );
				for (int i = 0; i < cnt; i++) {
					String key = textKeys.get(i);
					if (key != null) {
						Object[] objects = args.get(i);
						if (objects == null) {
							setTextForLocaleKey(i, key);
						}
						else {
							setTextForLocaleKey(i, key, objects);
						}
					}
				}
				if (textKey != null) {
					if (textArgs != null) {
						setTextForLocaleKey(textKey, textArgs);
					}
					else {
						setTextForLocaleKey(textKey);
					}
				}
			}
		};
		ApplicationManager.addLocaleChangedListener(localeListener);

		addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(@SuppressWarnings("unused")
			DisposeEvent e) {
				ApplicationManager.removeLocaleChangedListener(localeListener);
			}

		});
	}

	/**
	 * Returns whether a tree item is checked or not.
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>: item is checked.<br>
	 *         <code>false</code>: item is not checked.<br>
	 * @see #getChecked()
	 */
	public boolean isChecked() {
		return this.getChecked( );
	}

	/**
	 * If the tree has more than one column, the text at the given
	 * column index is set by this method.
	 * 
	 * @param index
	 *            the index of the column at which the text should be
	 *            set
	 * @param key
	 *            the key for translation
	 * @param objects
	 *            the arguments used for replacing parameters of the
	 *            translation
	 * @see org.eclipse.swt.widgets.TreeItem#setText(int, String)
	 */
	public void setTextForLocaleKey(int index, String key, Object... objects) {
		textKeys.put(index, key);
		args.put(index, objects);
		super.setText(index, handler.getString(key, objects));
	}

	/**
	 * Sets the text of this item using a key for translation.
	 * 
	 * @param key
	 *            the key for translation
	 * @param objects
	 *            the arguments used for replacing parameters of the
	 *            translation
	 * @see org.eclipse.swt.widgets.TreeItem#setText(String)
	 */
	public void setTextForLocaleKey(String key, Object... objects) {
		textKey = key;
		textArgs = objects;
		super.setText(handler.getString(key, objects));
	}

	/**
	 * Sets the text for the columns of this item using keys for
	 * translation.<br>
	 * If you want to use parameters for the translations use
	 * {@link #setTextForLocaleKey(int, String, Object...)}.
	 * 
	 * @param keys
	 *            array with the translation keys for the items.
	 * @see org.eclipse.swt.widgets.TreeItem#setText(String[])
	 */
	public void setTextForLocaleKey(String[] keys) {
		String[] newStrings = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			newStrings[i] = handler.getString(keys[i]);
			textKeys.put(i, keys[i]);
		}
		super.setText(newStrings);
	}

	/**
	 * Overridden, because we want to create a specialization of the
	 * class.
	 * 
	 * @see org.eclipse.swt.widgets.Widget#checkSubclass()
	 */
	@Override
	protected void checkSubclass() {
		// do nothing
	}

}
