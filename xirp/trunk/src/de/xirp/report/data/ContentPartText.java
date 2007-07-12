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
 * ContentPartText.java
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
 * This class represents a content part
 * {@link de.xirp.report.data.IContentPartItem item}
 * containing text in
 * {@link de.xirp.report.data.ContentPartTextParagraph paragraphs}.
 * 
 * @author Matthias Gernand
 * @see de.xirp.report.data.IContentPartItem
 * @see de.xirp.report.data.ContentPartTextParagraph
 */
public final class ContentPartText implements IContentPartItem {

	/**
	 * The
	 * {@link de.xirp.report.data.ContentPartTextParagraph paragraphs}
	 * of the text item.
	 * 
	 * @see de.xirp.report.data.ContentPartTextParagraph
	 */
	private Vector<ContentPartTextParagraph> paragraphs = new Vector<ContentPartTextParagraph>( );

	/**
	 * Adds a
	 * {@link de.xirp.report.data.ContentPartTextParagraph paragraph}
	 * to the text item.
	 * 
	 * @param para
	 *            The paragraph to add.
	 * @return A <code>boolean</code> indicating whether the add
	 *         operation was successful or not.
	 * @see de.xirp.report.data.ContentPartTextParagraph
	 */
	public boolean addParagraph(ContentPartTextParagraph para) {
		return paragraphs.add(para);
	}

	/**
	 * Returns the
	 * {@link de.xirp.report.data.ContentPartTextParagraph paragraphs}
	 * of the text item.
	 * 
	 * @return The paragraphs.
	 */
	public List<ContentPartTextParagraph> getParagraphs() {
		return Collections.unmodifiableList(paragraphs);
	}
}
