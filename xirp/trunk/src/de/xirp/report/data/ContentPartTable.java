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
 * ContentPartTable.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 17.04.2006:		Created by Matthias Gernand.
 */
package de.xirp.report.data;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import de.xirp.report.ReportException;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This class represents a content part
 * {@link de.xirp.report.data.IContentPartItem item}
 * containing a table with a description.
 * 
 * @author Matthias Gernand
 * @see de.xirp.report.data.IContentPartItem
 */
public final class ContentPartTable implements IContentPartItem {

	/**
	 * The table
	 * {@link de.xirp.report.data.ContentPartTableHeader header}.
	 * 
	 * @see de.xirp.report.data.ContentPartTableHeader
	 */
	private ContentPartTableHeader header = null;
	/**
	 * The
	 * {@link de.xirp.report.data.ContentPartTableRow rows}
	 * of the table.
	 * 
	 * @see de.xirp.report.data.ContentPartTableRow
	 */
	private Vector<ContentPartTableRow> rows = new Vector<ContentPartTableRow>( );
	/**
	 * The short description of the table.
	 */
	private String shortDescription = ""; //$NON-NLS-1$

	/**
	 * Constructs an empty table part item.
	 */
	public ContentPartTable() {

	}
	
	/**
	 * Constructs a new table part item for the given table
	 * {@link de.xirp.report.data.ContentPartTableHeader header}
	 * and
	 * {@link de.xirp.report.data.ContentPartTableRow rows}.
	 * 
	 * @param header
	 *            The header of the table.
	 * @param rows
	 *            The rows of the table.
	 * @param shortDescription
	 * 			  The description of the table.
	 * @throws ReportException
	 *             when
	 *             {@link de.xirp.report.data.ContentPartTable#check()}
	 *             recognizes an error.
	 * @see de.xirp.report.data.ContentPartTable#check()
	 * @see de.xirp.report.data.ContentPartTableHeader
	 * @see de.xirp.report.data.ContentPartTableRow
	 */
	public ContentPartTable(ContentPartTableHeader header,
			Vector<ContentPartTableRow> rows, String shortDescription) throws ReportException {
		this.header = header;
		addTableRows(rows);
		this.shortDescription = shortDescription;
		check( );
	}

	/**
	 * Checks the table object for inconsistencies. If a
	 * {@link de.xirp.report.data.ContentPartTableRow row}
	 * has more entries than the table a
	 * {@link de.xirp.report.ReportException exception}
	 * is thrown.
	 * 
	 * @throws ReportException
	 *             when a
	 *             {@link de.xirp.report.data.ContentPartTableRow row}
	 *             has to much entries.
	 */
	private void check() throws ReportException {
		for (ContentPartTableRow row : rows) {
			if (row.getEntryCount( ) > header.getColumnCount( )) {
				throw new ReportException(I18n.getString("ContentPartTable.exception.toMuchEntries") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Returns the
	 * {@link de.xirp.report.data.ContentPartTableHeader header}
	 * of the table.
	 * 
	 * @return The header.
	 * @see de.xirp.report.data.ContentPartTableHeader
	 */
	public ContentPartTableHeader getHeader() {
		return header;
	}

	/**
	 * Sets the
	 * {@link de.xirp.report.data.ContentPartTableHeader header}
	 * of the table. A
	 * {@link de.xirp.report.data.ContentPartTable#check()}
	 * is done afterwards.
	 * 
	 * @param header
	 *            The header to set.
	 * @throws ReportException
	 *             when
	 *             {@link de.xirp.report.data.ContentPartTable#check()}
	 *             recognizes an error.
	 * @see de.xirp.report.data.ContentPartTableHeader
	 */
	public void setHeader(ContentPartTableHeader header) throws ReportException {
		this.header = header;
		check( );
	}

	/**
	 * Returns the description of the table.
	 * 
	 * @return The short description.
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * Sets the description of the table.
	 * 
	 * @param shortDescription
	 *            The short description to set.
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	/**
	 * Returns the table
	 * {@link de.xirp.report.data.ContentPartTableRow rows}.
	 * 
	 * @return The rows.
	 * @see de.xirp.report.data.ContentPartTableRow
	 */
	public List<ContentPartTableRow> getRows() {
		return Collections.unmodifiableList(rows);
	}

	/**
	 * Adds a {@link java.util.Vector} of
	 * {@link de.xirp.report.data.ContentPartTableRow rows}
	 * to the
	 * {@link de.xirp.report.data.ContentPartTableRow rows}
	 * in the table. A
	 * {@link de.xirp.report.data.ContentPartTable#check()}
	 * is done afterwards.
	 * 
	 * @param rows
	 *            The rows to add.
	 * @return A <code>boolean</code> indicating whether the add
	 *         operation was successful or not.
	 * @throws ReportException
	 *             when
	 *             {@link de.xirp.report.data.ContentPartTable#check()}
	 *             recognizes an error.
	 * @see de.xirp.report.data.ContentPartTable#check()
	 * @see de.xirp.report.data.ContentPartTableRow
	 */
	public boolean addTableRows(Vector<ContentPartTableRow> rows)
			throws ReportException {
		boolean b = this.rows.addAll(rows);
		check( );
		return b;
	}

	/**
	 * Adds a single
	 * {@link de.xirp.report.data.ContentPartTableRow row}
	 * to the
	 * {@link de.xirp.report.data.ContentPartTableRow rows}
	 * in the table. A
	 * {@link de.xirp.report.data.ContentPartTable#check()}
	 * is done afterwards.
	 * 
	 * @param row
	 *            The row to add.
	 * @return A <code>boolean</code> indicating whether the add
	 *         operation was successful or not.
	 * @throws ReportException
	 *             when
	 *             {@link de.xirp.report.data.ContentPartTable#check()}
	 *             recognizes an error.
	 * @see de.xirp.report.data.ContentPartTable#check()
	 * @see de.xirp.report.data.ContentPartTableRow
	 */
	public boolean addTableRow(ContentPartTableRow row) throws ReportException {
		boolean b = rows.add(row);
		check( );
		return b;
	}

	/**
	 * Returns the count of columns in the table.
	 * 
	 * @return An <code>int</code> with count of columns.
	 */
	public int getColumnCount() {
		return header.getColumnCount( );
	}

}
