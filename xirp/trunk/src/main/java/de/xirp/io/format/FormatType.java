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
 * FormatType.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 30.05.2006:		Created by Rabea Gransberger.
 */
package de.xirp.io.format;

/**
 * Types for format strings
 */
public enum FormatType {
	/**
	 * unsigned byte
	 */
	UBYTE,
	/**
	 * A Char, with optional length (results in a string)
	 */
	CHAR,
	/**
	 * A Byte
	 */
	BYTE,
	/**
	 * A short
	 */
	SHORT,
	/**
	 * An integer
	 */
	INTEGER,
	/**
	 * A double
	 */
	DOUBLE,
	/**
	 * A float, with specified floating point
	 */
	FLOAT,
	/**
	 * A long
	 */
	LONG;

	/**
	 * Gets the format type according to the format string in the xml
	 * 
	 * @param strg
	 *            the format string, like c,b,d...
	 * @return Type according to the format string or byte if no
	 *         format was found
	 */
	public static FormatType getType(final String strg) {
		String typeStrg = strg.trim( );
		if (typeStrg.equalsIgnoreCase("c")) { //$NON-NLS-1$
			return CHAR;
		}
		else if (typeStrg.equalsIgnoreCase("b")) { //$NON-NLS-1$
			return BYTE;
		}
		else if (typeStrg.equalsIgnoreCase("s")) { //$NON-NLS-1$
			return SHORT;
		}
		else if (typeStrg.equalsIgnoreCase("i")) { //$NON-NLS-1$
			return INTEGER;
		}
		else if (typeStrg.equalsIgnoreCase("d")) { //$NON-NLS-1$
			return DOUBLE;
		}
		else if (typeStrg.equalsIgnoreCase("f")) { //$NON-NLS-1$
			return FLOAT;
		}
		else if (typeStrg.equalsIgnoreCase("l")) { //$NON-NLS-1$
			return LONG;
		}
		else if (typeStrg.equalsIgnoreCase("u")) { //$NON-NLS-1$
			return UBYTE;
		}
		else {
			return BYTE;
		}
	}
}
