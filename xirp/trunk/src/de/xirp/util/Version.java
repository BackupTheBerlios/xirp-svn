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
 * Version.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 02.02.2006:		Created by Matthias Gernand.
 */
package de.xirp.util;

/**
 * This Class defines versioning constants needed in Xirp.
 * The values are set by the <code>build.xml</code> ant build script.
 * 
 * @author Matthias Gernand
 */
public final class Version {

	/**
	 * Major number of the current version
	 */
	public static final int MAJOR_VERSION = 2;
	/**
	 * Minor number of the current version
	 */
	public static final int MINOR_VERSION = 4;
	/**
	 * Number of the patch level
	 */
	public static final int PATCH_LEVEL = 0;
	/**
	 * Revision number of the current version
	 */
	public static final int REVISION = 1300;
	/**
	 * Set <code>true</code> for development versions. Can be used to 
	 * print special debug messages.
	 */
	public static final boolean DEVELOPMENT = true;

}
