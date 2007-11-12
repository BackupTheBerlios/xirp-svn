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
 * Format.java
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
 * This class holds formats which can be used to format byte arrays.<br>
 * To specify the formats in the specification use this tabular:<br>
 * <table border="1">
 * <tr>
 * <th>Spec Key</th>
 * <th>Datatype</th>
 * </tr>
 * <tr>
 * <td>%u</td>
 * <td>UBYTE</td>
 * </tr>
 * <tr>
 * <td>%c</td>
 * <td>CHAR</td>
 * </tr>
 * <tr>
 * <td>%b</td>
 * <td>BYTE</td>
 * </tr>
 * <tr>
 * <td>%s</td>
 * <td>SHORT</td>
 * </tr>
 * <tr>
 * <td>%i</td>
 * <td>INTEGER</td>
 * </tr>
 * <tr>
 * <td>%d</td>
 * <td>DOUBLE</td>
 * </tr>
 * <tr>
 * <td>%f</td>
 * <td>FLOAT</td>
 * </tr>
 * <tr>
 * <td>%l</td>
 * <td>LONG</td>
 * </tr>
 * </table> Because there is no String format you can use
 * <code>%c{5}</code> for a String with a length of 5.<br>
 * <br>
 * To specify the fraction digits of a float use <code>%f{2}</code>
 * for 2 fraction digits. Floats are converted to doubles when using
 * the <code>FormatParser</code>.
 * 
 * @author Rabea Gransberger
 * @see FormatParser#FormatParser(String)
 */
public class Format {

	/**
	 * Type of the format
	 */
	protected FormatType type;
	/**
	 * Length of this format, only usable for char and float
	 */
	protected int length = -1;

	/**
	 * Constructs a new format with the given type
	 * 
	 * @param type
	 *            the type of the format
	 */
	public Format(FormatType type) {
		this.type = type;
	}

	/**
	 * Constructs a new format with the given type and length
	 * 
	 * @param type
	 *            type of the format
	 * @param length
	 *            length which (the default implementation of
	 *            {@link de.xirp.io.format.FormatParser}
	 *            uses it only for char and float)
	 */
	public Format(FormatType type, int length) {
		this.type = type;
		this.length = length;
	}

	/**
	 * Gets the length of this format.<br>
	 * The "length" is defined as:<br>
	 * %c{5} results in a string with 5 characters<br>
	 * %f{2} results in a double with to after the decimal point
	 * 
	 * @return Returns a number specifying options for CHAR and FLOAT
	 *         types
	 */
	public int getLength() {
		return length;
	}

	/**
	 * The type of a format which may be a float, int etc.
	 * 
	 * @return Returns the type.
	 */
	public FormatType getType() {
		return type;
	}

	/**
	 * Gets the type name and length of this format for debugging
	 * purposes.
	 */
	@Override
	public String toString() {
		return type.name( ) + "_" + length; //$NON-NLS-1$
	}

}
