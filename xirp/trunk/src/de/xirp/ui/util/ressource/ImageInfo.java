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
 * ImageInfo.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 04.06.2006:		Created by Rabea Gransberger.
 */
package de.xirp.ui.util.ressource;

import org.eclipse.swt.graphics.Point;

import de.xirp.ui.util.ressource.ImageManager.SystemImage;

/**
 * Provides information about an image.
 * 
 * @author Rabea Gransberger
 * 
 */
public class ImageInfo {

	/**
	 * The identifier of the image
	 */
	private SystemImage type;
	/**
	 * Description of the image
	 */
	private String description;
	/**
	 * The original size of the image
	 */
	private Point size;

	/**
	 * Constructs a new information object with the given information
	 * 
	 * @param type
	 *            The identifier of the image
	 * @param description
	 *            Description of the image
	 * @param size
	 *            The original size of the image
	 */
	protected ImageInfo(SystemImage type, String description, Point size) {
		this.type = type;
		this.description = description;
		this.size = size;
	}

	/**
	 * Get's the description of the image, which says what the image
	 * looks like and/or what it is for.
	 * 
	 * @return the description of the image
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get's the original size of the image.
	 * 
	 * @return the size of the image
	 */
	public Point getSize() {
		return size;
	}

	/**
	 * Get's the type of this system image
	 * 
	 * @return the type
	 */
	public SystemImage getType() {
		return type;
	}

}
