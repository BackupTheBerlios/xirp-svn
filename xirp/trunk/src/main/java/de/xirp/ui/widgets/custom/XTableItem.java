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
 * XTableItem.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 30.10.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

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
public class XTableItem extends TableItem {

	/**
	 * The
	 * {@link de.xirp.ui.event.LocaleChangedListener locale}
	 * change listener.
	 * 
	 * @see de.xirp.ui.event.LocaleChangedListener
	 */
	private LocaleChangedListener localeListener;
	/**
	 * The {@link de.xirp.util.II18nHandler handler}.
	 * 
	 * @see de.xirp.util.II18nHandler
	 */
	private II18nHandler handler;
	/**
	 * Keys for translation of the columns of table item
	 */
	private HashMap<Integer, String> textKeys = new HashMap<Integer, String>( );
	/**
	 * Arguments for the text of the columns of the table item
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
	 * Constructs a new table item. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param index
	 *            The index.
	 */
	public XTableItem(Table parent, int style, int index) {
		super(parent, style, index);
		this.handler = I18n.getGenericI18n( );
		init( );
	}

	/**
	 * Constructs a new table item. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 */
	public XTableItem(Table parent, int style) {
		super(parent, style);
		this.handler = I18n.getGenericI18n( );
		init( );
	}

	/**
	 * Constructs a new table item. This constructor should be used in
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
	public XTableItem(Table parent, int style, int index, II18nHandler handler) {
		super(parent, style, index);
		this.handler = handler;
		init( );
	}

	/**
	 * Constructs a new table item. This constructor should be used in
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
	public XTableItem(Table parent, int style, II18nHandler handler) {
		super(parent, style);
		this.handler = handler;
		init( );
	}

	/**
	 * Gets the texts for each column of the given table item.
	 * 
	 * @param item
	 *            the item to read the texts from
	 * @return an array with the texts for each column.
	 */
	private static String[] getText(TableItem item) {
		Table table = item.getParent( );
		String[] values = new String[table.getColumnCount( )];

		for (int i = 0; i < table.getColumnCount( ); i++) {
			values[i] = item.getText(i);
		}
		return values;
	}

	/**
	 * Gets the images for each column of the given table item.
	 * 
	 * @param item
	 *            the item to read the images from
	 * @return an array with the images for each column.
	 */
	private static Image[] getImages(TableItem item) {
		Table table = item.getParent( );
		Image[] values = new Image[table.getColumnCount( )];

		for (int i = 0; i < table.getColumnCount( ); i++) {
			values[i] = item.getImage(i);
		}
		return values;
	}

	/**
	 * Sets the given images for each column of the given item.
	 * 
	 * @param item
	 *            the item to set the images at
	 * @param images
	 *            an arrays of images for the columns of the item.
	 */
	private static void setImages(TableItem item, Image[] images) {
		Table table = item.getParent( );
		for (int i = 0; i < table.getColumnCount( ); i++) {
			Image image = images[i];
			if (image != null) {
				item.setImage(i, image);
			}
		}
	}

	/**
	 * Creates a copy of the given table item but using the given
	 * style for the returned table item. If the given item is an
	 * {@link XTableItem} translation capabilities are also copied.
	 * 
	 * @param item
	 *            the item to copy.
	 * @param style
	 *            the style to use for the returned item
	 * @return a new table item with all the properties of the old
	 *         table item but a new style.
	 */
	protected static XTableItem copy(TableItem item, int style) {
		Table table = item.getParent( );

		XTableItem newItm = new XTableItem(table, style);
		return copyInternal(item, newItm);
	}

	/**
	 * Creates a copy of the given table item. If the given item is an
	 * {@link XTableItem} translation capabilities are also copied.
	 * 
	 * @param item
	 *            the item to copy.
	 * @return a new table item with all the properties of the old
	 *         table item
	 */
	protected static XTableItem copy(TableItem item) {
		Table table = item.getParent( );

		XTableItem newItm = new XTableItem(table, item.getStyle( ));
		return copyInternal(item, newItm);
	}

	/**
	 * Creates a copy of the given table item. If the given item is an
	 * {@link XTableItem} translation capabilities are also copied.
	 * 
	 * @param item
	 *            the item to copy.
	 * @param innerCnt
	 *            the position of the new table item
	 * @return a new table item with all the properties of the old
	 *         table item
	 */
	protected static XTableItem copyWithCnt(TableItem item, int innerCnt) {
		Table table = item.getParent( );

		XTableItem newItm = new XTableItem(table, item.getStyle( ), innerCnt);
		return copyInternal(item, newItm);
	}

	/**
	 * Creates a copy of the given table item. If the given item is an
	 * {@link XTableItem} translation capabilities are also copied.
	 * 
	 * @param item
	 *            the item to copy.
	 * @param style
	 *            the style to use for the returned item
	 * @param innerCnt
	 *            the position of the new table item
	 * @return a new table item with all the properties of the old
	 *         table item
	 */
	protected static XTableItem copy(TableItem item, int style, int innerCnt) {
		Table table = item.getParent( );

		XTableItem newItm = new XTableItem(table, style, innerCnt);
		return copyInternal(item, newItm);
	}

	/**
	 * Copies colors, font, checked status, grayed status, images and
	 * data from the first table item to the second. If the first item
	 * is an {@link XTableItem} translation capabilities are also
	 * copied.
	 * 
	 * @param item
	 *            the item to read the properties from
	 * @param newItm
	 *            the item to copy the properties to
	 * @return the second item with all the properties from the first
	 *         item
	 */
	private static XTableItem copyInternal(TableItem item, XTableItem newItm) {
		if (item instanceof XTableItem) {
			XTableItem xItem = (XTableItem) item;
			newItm.args = xItem.args;
			newItm.textArgs = xItem.textArgs;
			newItm.textKey = xItem.textKey;
			newItm.textKeys = xItem.textKeys;
			newItm.handler = xItem.handler;
		}
		newItm.setBackground(item.getBackground( ));
		newItm.setForeground(item.getForeground( ));
		newItm.setFont(item.getFont( ));
		newItm.setChecked(item.getChecked( ));
		newItm.setGrayed(item.getGrayed( ));

		newItm.setAllData(getAllData(item));

		setImages(newItm, getImages(item));

		newItm.setText(getText(item));
		return newItm;
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

			public void widgetDisposed(DisposeEvent e) {
				ApplicationManager.removeLocaleChangedListener(localeListener);
			}

		});
	}

	/**
	 * If the table has more than one column, the text at the given
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
	 * @see org.eclipse.swt.widgets.TableItem#setText(int, String)
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
	 * @see org.eclipse.swt.widgets.TableItem#setText(String)
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
	 * @see org.eclipse.swt.widgets.TableItem#setText(String[])
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
	 * Reads the data from the given table item using reflection.
	 * 
	 * @param item
	 *            the item to read the data from
	 * @return the data read from the item.
	 */
	private static Object getAllData(TableItem item) {
		Object data = null;
		try {
			Field field = Widget.class.getDeclaredField("data"); //$NON-NLS-1$
			field.setAccessible(true);
			data = field.get(item);
		}
		catch (SecurityException e) {
			e.printStackTrace( );
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace( );
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace( );
		}
		catch (IllegalAccessException e) {
			e.printStackTrace( );
		}
		return data;
	}

	/**
	 * Reads the data set to this item using {@link #setData(Object)}
	 * and {@link #setData(String, Object)}.
	 * 
	 * @return the data of this item.
	 */
	protected Object getAllData() {
		return getAllData(this);
	}

	/**
	 * Sets the data to this item from the given object.
	 * 
	 * @param object
	 *            and Object [][] containing the keys and data for
	 *            this item.
	 * @see #getAllData()
	 */
	protected void setAllData(Object object) {
		if (object != null && object instanceof Object[]) {
			Object[] data = (Object[]) object;
			for (int i = 1; i < data.length; i += 2) {
				String key = (String) data[i];
				Object obj = data[i + 1];
				this.setData(key, obj);
			}
			this.setData(data[0]);
		}
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
