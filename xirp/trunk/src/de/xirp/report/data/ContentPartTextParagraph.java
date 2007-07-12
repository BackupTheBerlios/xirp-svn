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
 * ContentPartTextParagraph.java
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

/**
 * This class represents a text paragraph in a 
 * {@link de.xirp.report.Report report}.
 * A paragraph can have a header.
 * 
 * @author Matthias Gernand
 * 
 * 
 * @see de.xirp.report.Report
 */
public final class ContentPartTextParagraph {

	/**
	 * The text of the paragraph.
	 */
	private String text = ""; //$NON-NLS-1$
	/**
	 * The header of the paragraph (may be empty).
	 */
	private String header = ""; //$NON-NLS-1$

	/**
	 * Returns the text of the paragraph.
	 * 
	 * @return The text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text of the paragraph.
	 * 
	 * @param text
	 *            The text to set.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Returns the header of the paragraph.
	 * 
	 * @return The header.
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Sets the header of the paragraph.
	 * 
	 * @param header
	 *            The header to set.
	 */
	public void setHeader(String header) {
		this.header = header;
	}
}
