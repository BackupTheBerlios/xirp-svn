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
 * Package.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 10.03.2006:		Created by Rabea Gransberger.
 */
package de.xirp.io;

/**
 * This class may be used for data received as a byte array from the
 * robot coupled with an identifier.
 * 
 * @author Rabea Gransberger
 */
public class Package {

	/**
	 * Identification of the package
	 */
	private int id;
	/**
	 * Data of the package
	 */
	private byte[] data;

	/**
	 * Constructs a new package with given data.
	 * 
	 * @param id
	 *            identifier for the package
	 * @param data
	 *            data of the package
	 */
	public Package(int id, byte[] data) {
		this.id = id;
		this.data = new byte[data.length];
		System.arraycopy(data, 0, this.data, 0, data.length - 1);
	}

	/**
	 * Gets the data of the package.
	 * 
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Gets the identifier of the package.
	 * 
	 * @return identifier of the package
	 */
	public int getId() {
		return id;
	}

}
