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
 * Content.java
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
 * This class represents the content of a report.
 * The content holds several content 
 * {@link de.xirp.report.data.ContentPart parts}.
 * 
 * @author Matthias Gernand
 * 
 * @see de.xirp.report.data.ContentPart
 */
public final class Content {

	/**
	 * The {@link de.xirp.report.data.ContentPart parts}
	 * of the content.
	 * 
	 * @see de.xirp.report.data.ContentPart
	 */
	private Vector<ContentPart> parts = new Vector<ContentPart>( );

	/**
	 * Adds a new report {@link de.xirp.report.data.ContentPart content}
	 * part to the report content parts.
	 * 
	 * @param part
	 *            The part to add.
	 * 
	 * @return A <code>boolean</code> indicating whether the add 
	 * 			operation was successful or not.
	 * 
	 * @see de.xirp.report.data.ContentPart
	 */
	public boolean addReportPart(ContentPart part) {
		return parts.add(part);
	}

	/**
	 * Returns the content 
	 * {@link de.xirp.report.data.ContentPart parts}.
	 * 
	 * @return The content parts.
	 */
	public List<ContentPart> getParts() {
		return Collections.unmodifiableList(parts);
	}
}
