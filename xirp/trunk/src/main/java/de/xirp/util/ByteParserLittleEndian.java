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
 * ByteParserLittleEndian.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 31.03.2006:		Created by Rabea Gransberger.
 */
package de.xirp.util;

import java.nio.ByteOrder;

/**
 * Parser for reading received data in little endian byte order.
 * 
 * @author Rabea Gransberger
 */
public class ByteParserLittleEndian extends ByteParser {

	/**
	 * the byte order used for conversion. In this case little endian.
	 */
	private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

	/**
	 * Constructs a new parser which will work on the given data.
	 * 
	 * @param data
	 *            the byte array with the data which should be parsed.
	 */
	public ByteParserLittleEndian(byte[] data) {
		super(data, BYTE_ORDER);
	}
}
