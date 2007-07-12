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
 * ContentPart.java
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

import de.xirp.util.FutureRelease;

/**
 * This class represents a content part. Such a part consist of
 * several content part
 * {@link de.xirp.report.data.IContentPartItem items}.
 * 
 * @author Matthias Gernand
 * @see de.xirp.report.data.IContentPartItem
 */
public final class ContentPart {

	/**
	 * The content part
	 * {@link de.xirp.report.data.IContentPartItem items}.
	 * 
	 * @see de.xirp.report.data.IContentPartItem
	 */
	private Vector<IContentPartItem> items = new Vector<IContentPartItem>( );

	/**
	 * Returns the content part
	 * {@link de.xirp.report.data.IContentPartItem items}.
	 * 
	 * @return An unmodifiable list with the part items.
	 * @see de.xirp.report.data.IContentPartItem
	 */
	public List<IContentPartItem> getItems() {
		return Collections.unmodifiableList(items);
	}

	/**
	 * Adds a report content part
	 * {@link de.xirp.report.data.ContentPartImage image}
	 * item to the content part items.
	 * 
	 * @param image
	 *            A image part item.
	 * @return A <code>boolean</code> indicating whether the add
	 *         operation was successful or not.
	 * @see de.xirp.report.data.ContentPartImage
	 */
	public boolean addReportImage(ContentPartImage image) {
		return items.add(image);
	}

	/**
	 * Adds a report content
	 * {@link de.xirp.report.data.ContentPartText text}
	 * item to the report content part items.
	 * 
	 * @param text
	 *            A test part item.
	 * @return A <code>boolean</code> indicating whether the add
	 *         operation was successful or not.
	 * @see de.xirp.report.data.ContentPartText
	 */
	public boolean addReportText(ContentPartText text) {
		return items.add(text);
	}

	/**
	 * Adds a report content
	 * {@link de.xirp.report.data.ContentPartTable table}
	 * item to the report content part items.
	 * 
	 * @param table
	 *            A table part item
	 * @return A <code>boolean</code> indicating whether the add
	 *         operation was successful or not.
	 * @see de.xirp.report.data.ContentPartTable
	 */
	public boolean addReportTable(ContentPartTable table) {
		return items.add(table);
	}

	/**
	 * Adds a report content
	 * {@link de.xirp.report.data.ContentPartList list}
	 * item to the report content part items.
	 * 
	 * @param list
	 *            A list part item.
	 * @return A <code>boolean</code> indicating whether the add
	 *         operation was successful or not.
	 * @see de.xirp.report.data.ContentPartList
	 */
	public boolean addReportList(ContentPartList list) {
		return items.add(list);
	}

	/**
	 * Adds a report content
	 * {@link de.xirp.report.data.ContentPartVideo video}
	 * item to the report content part items. <br>
	 * <br>
	 * This is alpha API, may be removed in the future or is planed to
	 * be integrated in version 3.0.0. <br>
	 * <br>
	 * <b>Note:</b> Does noting at the moment.
	 * 
	 * @param video
	 *            A video part item.
	 * @return A <code>boolean</code> indicating whether the add
	 *         operation was successful or not.
	 * @see de.xirp.report.data.ContentPartVideo
	 */
	@FutureRelease(version = "3.0.0")
	public boolean addReportVideo(ContentPartVideo video) {
		// return items.add(video);
		return false;
	}

	/**
	 * Adds a report content
	 * {@link de.xirp.report.data.IContentPartItem item}
	 * to the report content part items. <br>
	 * <br>
	 * This is alpha API, may be removed in the future and is planed
	 * to be integrated in version 3.0.0. <br>
	 * <br>
	 * <b>Note:</b> Does noting at the moment.
	 * 
	 * @param item
	 *         A part item.
	 * @return A <code>boolean</code> indicating whether the add
	 *         operation was successful or not.
	 * @see de.xirp.report.data.IContentPartItem
	 */
	public boolean addReportItem(IContentPartItem item) {
		return items.add(item);
	}
}
