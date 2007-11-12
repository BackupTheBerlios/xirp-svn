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
 * ContentPartTableRow.java
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
 * This class represents a row in a table of a 
 * {@link de.xirp.report.Report report}.
 * 
 * @author Matthias Gernand
 * 
 * @see de.xirp.report.Report
 */
public final class ContentPartTableRow {

	/**
	 * The entries of the 
	 * {@link de.xirp.report.data.ContentPartTableRow row}.
	 * 
	 * @see de.xirp.report.data.ContentPartTableRow
	 */
	private Vector<String> entrys = new Vector<String>( );

	/**
	 * Constructs a new table {@link de.xirp.report.data.ContentPartTableRow row}
	 * for the given {@link de.xirp.report.data.ContentPartTableRow row} entries.
	 * 
	 * @param entries
	 *            The entries of the row.
	 */
	public ContentPartTableRow(String...entries) {
		for (String s : entries) {
			entrys.add(s);
		}
	}

	/**
	 * Constructs a new empty table row.
	 */
	public ContentPartTableRow() {

	}

	/**
	 * Adds an entry to the 
	 * {@link de.xirp.report.data.ContentPartTableRow row}.
	 * 
	 * @param entry
	 *            The entry to add.
	 * 
	 * @return A <code>boolean</code> indicating whether the add operation 
	 * 			was successful or not.
	 */
	public boolean addEntry(String entry) {
		return entrys.add(entry);
	}

	/**
	 * Adds entries from the given entries array.
	 * 
	 * @param entries
	 *            The entries.
	 */
	public void setEntrys(String...entries) {
		for (String s : entries) {
			this.entrys.add(s);
		}
	}

	/**
	 * Returns the entry count.
	 * 
	 * @return The count of entries.
	 */
	public int getEntryCount() {
		return entrys.size( );
	}

	/**
	 * Returns the entries.
	 * 
	 * @return An unmodifiable list with the entries.
	 */
	public List<String> getRowEntrys() {
		return Collections.unmodifiableList(entrys);
	}
}
