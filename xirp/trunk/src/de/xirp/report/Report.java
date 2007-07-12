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
 * Report.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 17.04.2006:		Created by Matthias Gernand.
 */
package de.xirp.report;

import de.xirp.report.data.Content;
import de.xirp.report.data.Header;

/**
 * This class represents a report. A report contains a 
 * {@link de.xirp.report.data.Header header},
 * {@link de.xirp.report.data.Content content} and
 * a name.
 * 
 * @author Matthias Gernand
 * 
 */
public final class Report {

	/**
	 * The report {@link de.xirp.report.data.Header header}.
	 * 
	 * @see de.xirp.report.data.Header
	 */
	private Header header = null;
	/**
	 * The report {@link de.xirp.report.data.Content content}.
	 * 
	 * @see de.xirp.report.data.Content
	 */
	private Content content = null;
	/**
	 * The name of the report.
	 */
	private String name = ""; //$NON-NLS-1$

	/**
	 * Default-constructor declared private to avoid misuse and user
	 * errors.
	 */
	private Report() {

	}

	/**
	 * Constructs a new report from the given 
	 * {@link de.xirp.report.data.Header header},
	 * {@link de.xirp.report.data.Content content}
	 * and report name.
	 * 
	 * @param header
	 *            The report header.
	 * 
	 * @param content
	 *            The report content.
	 * 
	 * @param name
	 *            The report name.
	 *            
	 * @see de.xirp.report.data.Header
	 * @see de.xirp.report.data.Content
	 */
	public Report(Header header, Content content, String name) {
		this.name = name;
		this.header = header;
		this.content = content;
	}

	/**
	 * Returns the {@link de.xirp.report.data.Content content}
	 * of the report.
	 * 
	 * @return The content.
	 * 
	 * @see de.xirp.report.data.Content
	 */
	public Content getContent() {
		return content;
	}

	/**
	 * Returns the {@link de.xirp.report.data.Header header}
	 * of the report.
	 * 
	 * @return The header.
	 * 
	 * @see de.xirp.report.data.Header
	 */
	public Header getHeader() {
		return header;
	}

	/**
	 * Returns the name of the report.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
}
