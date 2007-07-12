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
 * XTable.java
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities.
 * 
 * @author Matthias Gernand
 */
public class XTable extends Table {

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
	 * Constructs a new table. This constructor should be used in
	 * application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 */
	public XTable(Composite parent, int style) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this);

	}

	/**
	 * Constructs a new table. This constructor should be used in
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
	public XTable(Composite parent, int style, II18nHandler handler) {
		super(parent, style);
		localeHandler = new XWidgetLocaleHandler(this, handler);

	}

	/**
	 * Sorts the column with the given index using the current sort
	 * direction of the table.
	 * 
	 * @param column
	 *            the index of the column to sort
	 * @throws IllegalArgumentException
	 *             if the column to sort is not a {@link XTableColumn}
	 */
	public void resortColumn(int column) throws IllegalArgumentException {
		sortColumn(column, this.getSortDirection( ));
	}

	/**
	 * Sorts the column with the given index using the given
	 * direction.
	 * 
	 * @param column
	 *            the index of the column to sort
	 * @param direction
	 *            one of {@link SWT#UP} and {@link SWT#DOWN}
	 * @throws IllegalArgumentException
	 *             if the column to sort is not a {@link XTableColumn}
	 */
	public void sortColumn(int column, int direction)
			throws IllegalArgumentException {
		if (column >= 0 && column < this.getColumnCount( )) {
			TableColumn col = this.getColumn(column);
			if (col instanceof XTableColumn) {
				XTableColumn hCol = (XTableColumn) col;
				hCol.sort(direction);
			}
			else {
				throw new IllegalArgumentException(I18n.getString("XTable.exception.sortingNotPossible", col.getClass( ).getSimpleName( ))); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Sets the tooltip of the underlying widget which is translated
	 * from the given key replacing any parameters of the translation
	 * with the given arguments.
	 * 
	 * @param key
	 *            The key for translation.
	 * @param objects
	 *            The arguments to use for replacing parameters of the
	 *            translation.
	 */
	public void setToolTipTextForLocaleKey(String key, Object... objects) {
		localeHandler.setToolTipText(key, objects);
	}

	/**
	 * Gets the key used for translating the tooltip of this widget.
	 * 
	 * @return the tooltip key or <code>null</code> if none was set.
	 */
	public String getToolTipTextLocaleKey() {
		return localeHandler.getToolTipTextKey( );
	}

	/**
	 * Gets the arguments used for replacing parameters in the
	 * translation for the tooltip.
	 * 
	 * @return the arguments or <code>null</code> if none were set.
	 */
	public Object[] getToolTipTextArgs() {
		return localeHandler.getToolTipTextArgs( );
	}

	/**
	 * Sets the selection to the given zero relative row, throws an
	 * selection event and forces the focus on this table.
	 * 
	 * @param select
	 *            The row to select.
	 */
	public void setSelectionAndForceFocus(int select) {
		setSelection(select);
		forceFocus( );

		Event event = new Event( );
		event.widget = this;
		notifyListeners(SWT.Selection, event);
	}

	/**
	 * Sets the selection to the last row, throws an selection event
	 * and forces the focus on this table.
	 */
	public void selectLastAndForceFocus() {
		setSelectionAndForceFocus(getItemCount( ) - 1);
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
