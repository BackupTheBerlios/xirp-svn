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
 * XTableColumn.java
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

import java.text.Collator;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import de.xirp.ui.util.SWTUtil;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities. Furthermore this table column supports
 * sorting the items.
 * 
 * @author Matthias Gernand
 */
public class XTableColumn extends TableColumn {

	/**
	 * The type used for comparison when sorting the columns items.
	 * 
	 * @author Rabea Gransberger
	 */
	public enum SortType {
		/**
		 * Alphanumerical sorting
		 */
		ALPHA,
		/**
		 * Numerical sorting using integers
		 */
		NUMERIC_INT,
		/**
		 * Numerical sorting using doubles
		 */
		NUMERIC_DOUBLE
	}

	/**
	 * The locale
	 * {@link de.xirp.ui.widgets.custom.XWidgetLocaleHandler handler}
	 * for this widget.
	 * 
	 * @see de.xirp.ui.widgets.custom.XWidgetLocaleHandler
	 */
	private final XWidgetLocaleHandler localeHandler;
	/**
	 * The
	 * {@link de.xirp.ui.widgets.custom.XTableColumn.SortListener sort}
	 * listener.
	 */
	private SortListener sortListener;

	/**
	 * Constructs a new table column. This constructor should be used
	 * in application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param index
	 *            The index.
	 */
	public XTableColumn(Table parent, int style, int index) {
		super(parent, style, index);
		localeHandler = new XWidgetLocaleHandler(this);
		setSortable(SortType.ALPHA);
	}

	/**
	 * Constructs a new table column. This constructor should be used
	 * in application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 */
	public XTableColumn(Table parent, int style) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this);
		setSortable(SortType.ALPHA);
	}

	/**
	 * Constructs a new table column. This constructor should be used
	 * in plugin-UI environment, because the
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
	public XTableColumn(Table parent, int style, int index, II18nHandler handler) {
		super(parent, style, index);
		localeHandler = new XWidgetLocaleHandler(this, handler);
		setSortable(SortType.ALPHA);
	}

	/**
	 * Constructs a new table column. This constructor should be used
	 * in plugin-UI environment, because the
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
	public XTableColumn(Table parent, int style, II18nHandler handler) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this, handler);
		setSortable(SortType.ALPHA);
	}

	/**
	 * Gets the comparator for the given sort type.
	 * 
	 * @param type
	 *            the type of sorting.
	 * @return the comparator for the given type or a alphanumerical
	 *         comparator as default.
	 */
	private Comparator<String> getComparator(SortType type) {
		switch (type) {
			case ALPHA:
				return new StringComparator( );
			case NUMERIC_INT:
				return new NumberIntComparator( );
			case NUMERIC_DOUBLE:
				return new NumberDoubleComparator( );
		}
		return new StringComparator( );
	}

	/**
	 * Enables sorting for this column for the given type of sorting.
	 * 
	 * @param type
	 *            the type used for sorting
	 */
	public void setSortable(SortType type) {
		setSortable(getComparator(type));
	}

	/**
	 * Enables sorting for this column using the given comparator for
	 * sorting.
	 * 
	 * @param comp
	 *            the comparator to use for sorting
	 */
	public void setSortable(Comparator<String> comp) {
		disableSortable( );
		sortListener = new SortListener(this, comp);
		addListener(SWT.Selection, sortListener);
		getParent( ).setSortColumn(this);
		getParent( ).setSortDirection(SWT.UP);
	}

	/**
	 * Disables sorting for this column.
	 */
	public void disableSortable() {
		if (sortListener != null) {
			removeListener(SWT.Selection, sortListener);
		}
	}

	/**
	 * Sorts this column in the given direction.
	 * 
	 * @param direction
	 *            one of {@link SWT#UP} or {@link SWT#DOWN}
	 */
	public void sort(int direction) {
		if (sortListener != null) {
			sortListener.sort(direction);
		}
	}

	/**
	 * Sets the text for this widget which is translated from the
	 * given key replacing any parameters of the translation with the
	 * given arguments.
	 * 
	 * @param key
	 *            The key for translation.
	 * @param objects
	 *            The arguments to use for replacing parameters of the
	 *            translation.
	 * @see #setText(String)
	 */
	public void setTextForLocaleKey(String key, Object... objects) {
		localeHandler.setText(key, objects);
	}

	/**
	 * Sets the tooltip for this widget which is translated from the
	 * given key replacing any parameters of the translation with the
	 * given arguments.
	 * 
	 * @param key
	 *            The key for translation.
	 * @param objects
	 *            The arguments to use for replacing parameters of the
	 *            translation.
	 * @see #setToolTipText(String)
	 */
	public void setToolTipTextForLocaleKey(String key, Object... objects) {
		localeHandler.setToolTipText(key, objects);
	}

	/**
	 * Gets the key used for translating the text for this column.
	 * 
	 * @return the i18n key
	 */
	public String getTextLocaleKey() {
		return localeHandler.getTextKey( );
	}

	/**
	 * Gets the key used for translating the tooltip text for this
	 * column.
	 * 
	 * @return The i18n key of the tooltip.
	 */
	public String getToolTipTextLocaleKey() {
		return localeHandler.getToolTipTextKey( );
	}

	/**
	 * Gets the arguments which are used when replacing parameters of
	 * the translation of the columns text.
	 * 
	 * @return the arguments for the text.
	 */
	public Object[] getTextArgs() {
		return localeHandler.getTextArgs( );
	}

	/**
	 * Gets the arguments which are used when replacing parameters of
	 * the translation of the columns tooltip text.
	 * 
	 * @return the arguments for the tooltip.
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
		// do nothing
	}

	/**
	 * Listener which handles the sorting of the table column.
	 * 
	 * @author Rabea Gransberger
	 */
	static class SortListener implements Listener {

		/**
		 * The column to sort
		 */
		private TableColumn theColumn;
		/**
		 * The comparator to use for sorting
		 */
		private Comparator<String> comp;
		/**
		 * The table to which the column belongs to
		 */
		private Table table;
		/**
		 * The index of the column which is sorted
		 */
		private int columnIndex;

		/**
		 * Creates a new listener which handles the sorting of the
		 * column.
		 * 
		 * @param theColumn
		 *            the column which should be sorted
		 * @param comp
		 *            the comparator used for sorting
		 */
		public SortListener(TableColumn theColumn, Comparator<String> comp) {
			this.theColumn = theColumn;
			this.comp = comp;
			this.table = theColumn.getParent( );

			init( );
		}

		/**
		 * Reads the column index of the column which should be
		 * sorted.
		 */
		private void init() {
			TableColumn[] columns = table.getColumns( );
			for (int i = 0; i < columns.length; i++) {
				TableColumn act = columns[i];
				if (act == theColumn) {
					columnIndex = i;
				}
			}
		}

		/**
		 * Sorts the column in the given direction.
		 * 
		 * @param direction
		 *            one of {@link SWT#UP} and {@link SWT#DOWN}
		 */
		public void sort(int direction) {
			table.setSortDirection(direction);
			table.setSortColumn(theColumn);
			sortInternal(table.getItems( ));
		}

		/**
		 * Sorts the given items of the table by comparing the text of
		 * the items of the current column using the comparator. The
		 * items are disposed and newly created while sorting.
		 * 
		 * @param items
		 *            the items to sort.
		 */
		private void sortInternal(TableItem[] items) {
			// Read the index which the column which should be sorted
			// currently has
			int index = table.getColumnOrder( )[columnIndex];
			// Quicksort
			for (int mainCnt = 1; mainCnt < items.length; mainCnt++) {
				TableItem mainItem = items[mainCnt];
				String mainValue = mainItem.getText(index);
				for (int innerCnt = 0; innerCnt < mainCnt; innerCnt++) {
					String innerValue = items[innerCnt].getText(index);
					// up
					boolean swap = comp.compare(mainValue, innerValue) < 0;
					// down
					if (table.getSortDirection( ) == SWT.DOWN) {
						swap = !swap;
					}

					// Swap the positions of the two items by
					// disposing one of them
					// and inserting it again before/after the other
					// one
					if (swap) {
						XTableItem.copyWithCnt(mainItem, innerCnt);
						SWTUtil.secureDispose(mainItem);

						items = table.getItems( );
						break;
					}
				}
			}
		}

		/**
		 * Sort the column using the opposite of the current sort
		 * direction.
		 */
		public void sort() {
			TableColumn old = table.getSortColumn( );
			TableItem[] items = table.getItems( );

			if (old == theColumn) {
				if (table.getSortDirection( ) == SWT.UP) {
					table.setSortDirection(SWT.DOWN);
				}
				else {
					table.setSortDirection(SWT.UP);
				}
			}
			sortInternal(items);
			table.setSortColumn(theColumn);
		}

		/**
		 * Triggers the sorting of this column.
		 * 
		 * @param e
		 *            (not used)
		 */
		public void handleEvent(@SuppressWarnings("unused")
		Event e) {
			sort( );
		}
	}

	/**
	 * Compares two strings alphanumerically using the current locale
	 * of the application.
	 * 
	 * @author Rabea Gransberger
	 */
	public static class StringComparator implements Comparator<String> {

		/**
		 * The comparator which is used for comparison.
		 */
		private Comparator<Object> comp;

		/**
		 * Constructs a new comparator for strings based on
		 * {@link Collator#getInstance(java.util.Locale)} with the
		 * currents application locale.
		 */
		public StringComparator() {
			comp = Collator.getInstance(I18n.getLocale( ));
		}

		/**
		 * Compares the source string to the target string according
		 * to the collation rules for this Collator. Returns an
		 * integer less than, equal to or greater than zero depending
		 * on whether the source String is less than, equal to or
		 * greater than the target string.
		 * 
		 * @param source
		 *            the source string.
		 * @param target
		 *            the target string.
		 * @return Returns an integer value. Value is less than zero
		 *         if source is less than target or target is
		 *         <code>null</code>, value is zero if source and
		 *         target are equal, value is greater than zero if
		 *         source is greater than target or source is
		 *         <code>null</code>.
		 * @see java.util.Comparator#compare(Object, Object)
		 */
		public int compare(String source, String target) {
			if (source == null) {
				return 1;
			}
			if (target == null) {
				return -1;
			}
			return comp.compare(source, target);
		}

	}

	/**
	 * Comparator for String which represent integers or longs.
	 * 
	 * @author Rabea Gransberger
	 */
	public static class NumberIntComparator implements Comparator<String> {

		/**
		 * First the given strings are check for <code>null</code>.
		 * If they are okay the strings are parsed to
		 * {@link java.lang.Long longs} and then compared.
		 * 
		 * @param source
		 *            the first number
		 * @param target
		 *            the second number
		 * @return 1: if source is <code>null</code> or not a
		 *         {@link java.lang.Long} or parsed source is greater
		 *         then parsed target.<br>
		 *         0: if both sources were parsed and are equal.<br>
		 *         -1: if target is <code>null</code> or not a
		 *         {@link java.lang.Long} or parsed target is greater
		 *         then parsed source.<br>
		 * @see java.util.Comparator#compare(java.lang.Object,
		 *      java.lang.Object)
		 */
		public int compare(String source, String target) {
			if (source == null) {
				return 1;
			}
			if (target == null) {
				return -1;
			}
			long val1 = 0;
			long val2 = 0;
			try {
				val1 = Long.parseLong(source);
			}
			catch (NumberFormatException e) {
				return 1;
			}
			try {
				val2 = Long.parseLong(target);
			}
			catch (NumberFormatException e) {
				return -1;
			}

			if (val1 < val2) {
				return -1;
			}
			else if (val1 == val2) {
				return 0;
			}
			else {
				return 1;
			}

		}
	}

	/**
	 * Comparator for Strings which represent doubles.
	 * 
	 * @author Rabea Gransberger
	 */
	public static class NumberDoubleComparator implements Comparator<String> {

		/**
		 * First the given strings are check for <code>null</code>.
		 * If they are okay the strings are parsed to
		 * {@link java.lang.Double doubles} and then compared.
		 * 
		 * @param source
		 *            the first number
		 * @param target
		 *            the second number
		 * @return 1: if source is <code>null</code> or not a
		 *         {@link java.lang.Double} or parsed source is
		 *         greater then parsed target.<br>
		 *         0: if both sources were parsed and are equal.<br>
		 *         -1: if target is <code>null</code> or not a
		 *         {@link java.lang.Double} or parsed target is
		 *         greater then parsed source.<br>
		 * @see java.util.Comparator#compare(java.lang.Object,
		 *      java.lang.Object)
		 */
		public int compare(String source, String target) {
			if (source == null) {
				return 1;
			}
			if (target == null) {
				return -1;
			}
			double val1 = 0;
			double val2 = 0;
			try {
				val1 = Double.parseDouble(source);
			}
			catch (NumberFormatException e) {
				return 1;
			}
			try {
				val2 = Double.parseDouble(target);
			}
			catch (NumberFormatException e) {
				return -1;
			}

			if (val1 < val2) {
				return -1;
			}
			else if (val1 == val2) {
				return 0;
			}
			else {
				return 1;
			}
		}
	}
}
