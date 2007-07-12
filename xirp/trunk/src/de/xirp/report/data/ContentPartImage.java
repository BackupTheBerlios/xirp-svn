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
 * ContentPartImage.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 17.04.2006:		Created by Matthias Gernand.
 * 03.07.2007:		Default constructor private.
 */
package de.xirp.report.data;

/**
 * This class represents a content part 
 * {@link de.xirp.report.data.IContentPartItem item}
 * containing an image with description.
 * 
 * @author Matthias Gernand
 * 
 * @see de.xirp.report.data.IContentPartItem
 */
public final class ContentPartImage implements IContentPartItem {

	/**
	 * Path to the image file.
	 */
	private String path = null;
	/**
	 * Short description of the image.
	 */
	private String shortDescription = null;

	/**
	 * Default constructor declared private to minimize
	 * broken images in the document.
	 */
	private ContentPartImage() {
		
	}
	
	/**
	 * Constructs a new image part.
	 * 
	 * @param path
	 * 			The path to the image.
	 * @param shortDescription
	 * 			The description of the image.
	 */
	public ContentPartImage(String path, String shortDescription) {
		this.path = path;
		this.shortDescription = shortDescription;
	}
	
	/**
	 * Returns the path to the image.
	 * 
	 * @return The path.
	 */
	public String getPath() {
		if (path == null) {
			return ""; //$NON-NLS-1$
		}
		return path;
	}

	/**
	 * Returns the short description of the image.
	 * 
	 * @return The short description.
	 */
	public String getShortDescription() {
		return shortDescription;
	}
}
