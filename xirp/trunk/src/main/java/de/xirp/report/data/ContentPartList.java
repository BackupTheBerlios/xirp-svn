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
 * ContentPartList.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 10.05.2006:		Created by Matthias Gernand.
 */
package de.xirp.report.data;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * This class represents a content part 
 * {@link de.xirp.report.data.IContentPartItem item}
 * containing an list. The list 
 * {@link de.xirp.report.data.ContentPartList.ListType type}
 * can be specified.
 * 
 * @author Matthias Gernand
 * 
 * @see de.xirp.report.data.IContentPartItem
 * @see de.xirp.report.data.ContentPartList.ListType
 */
public final class ContentPartList implements IContentPartItem {

	/**
	 * Default list {@link de.xirp.report.data.ContentPartList.ListType type}
	 * is dashed.
	 * 
	 * @see de.xirp.report.data.ContentPartList.ListType
	 */
	private ListType type = ListType.DASH;
	/**
	 * The list items.
	 */
	private Vector<String> entrys = new Vector<String>( );

	/**
	 * An enumeration indicating the list type of the list.
	 * There are three possibilities:
	 * <br><br>
	 * <ul>
	 * 	<li>BULLET</li>
	 *  <li>NUMBER</li>
	 *  <li>DASH</li>
	 * </ul>
	 * 
	 * @author Matthias Gernand
	 * 
	 */
	public enum ListType {
		/**
		 * A bullet list.
		 */
		BULLET,
		/**
		 * A numbered list.
		 */
		NUMBER,
		/**
		 * A dashed list.
		 */
		DASH
	}

	/**
	 * Returns the list items.
	 * 
	 * @return An unmodifiable list with he items.
	 */
	public List<String> getEntrys() {
		return Collections.unmodifiableList(entrys);
	}

	/**
	 * Returns the list 
	 * {@link de.xirp.report.data.ContentPartList.ListType type}.
	 * 
	 * @return The list type.
	 * 
	 * @see de.xirp.report.data.ContentPartList.ListType
	 */
	public ListType getType() {
		return type;
	}

	/**
	 * Sets the list
	 * {@link de.xirp.report.data.ContentPartList.ListType type}.
	 * 
	 * @param type
	 *            The type to set.
	 *            
	 * @see de.xirp.report.data.ContentPartList.ListType
	 */
	public void setType(ListType type) {
		this.type = type;
	}

	/**
	 * Adds an item to the list of items.
	 * 
	 * @param entry
	 *            The entry to add.
	 * 
	 * @return A <code>boolean</code> indicating whether the add operation 
	 * 			was successful or not.
	 */
	public boolean addEntry(String entry) {
		return this.entrys.add(entry);
	}

}
