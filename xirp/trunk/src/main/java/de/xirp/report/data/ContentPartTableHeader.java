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
 * ContentPartTableHeader.java
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

/**
 * This class represents a header of a table in a 
 * {@link de.xirp.report.Report report}.
 * 
 * @author Matthias Gernand
 * 
 */
public final class ContentPartTableHeader {

	/**
	 * The column headers of the table.
	 */
	private Vector<String> columnHeaders = new Vector<String>( );

	/**
	 * Default constructor made private.
	 */
	public ContentPartTableHeader() {}
	
	/**
	 * Constructs a new table header for the given column headers.
	 * 
	 * @param columnHeaders
	 * 			The column headers.
	 */
	public ContentPartTableHeader(String...columnHeaders) {
		for (String header : columnHeaders) {
			addColumn(header);
		}
	}
	
	/**
	 * Adds a column for the given string.
	 * 
	 * @param columnHeader
	 *            The title of the column header.
	 * 
	 * @return A <code>boolean</code> indicating whether the add operation 
	 * 			was successful or not.
	 */
	public boolean addColumn(String columnHeader) {
		return columnHeaders.add(columnHeader);
	}

	/**
	 * Returns the column header names.
	 * 
	 * @return An unmodifiable list with the column header names.
	 */
	public List<String> getColumnHeaders() {
		return Collections.unmodifiableList(columnHeaders);
	}

	/**
	 * Returns the column count.
	 * 
	 * @return A <code>int</code> with the column headers count.
	 */
	public int getColumnCount() {
		return columnHeaders.size( );
	}
}
