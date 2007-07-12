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
 * FormatParser.java
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.xirp.util.ByteParser;
import de.xirp.util.I18n;

/**
 * Parser used for reading a format string.
 * 
 * @author Rabea Gransberger
 */
public class FormatParser {

	/**
	 * Log4j Logger for this class
	 */
	private static Logger logClass = Logger.getLogger(FormatParser.class);

	/**
	 * Formats parsed from a format string
	 */
	protected ArrayList<Format> formats = new ArrayList<Format>( );

	/**
	 * The original format
	 */
	protected String format;

	/**
	 * Constructs a new format parser with the given format.
	 * 
	 * @param strg
	 *            the format string for parsing.
	 */
	public FormatParser(String strg) {
		parse(strg);
		this.format = strg;
	}

	/**
	 * Gets the formats, which were parsed from the given string.
	 * 
	 * @return Returns an unmodifiable list view of the formats.
	 */
	public List<Format> getFormats() {
		return Collections.unmodifiableList(formats);
	}

	/**
	 * Parses the string with some split operations.
	 * 
	 * @param strg
	 *            the string to parse
	 */
	private void parse(String strg) {
		// split at %
		String[] arr = strg.split("%"); //$NON-NLS-1$
		// iterate over the string parts
		for (String format : arr) {
			// if the format is not empty
			if (!StringUtils.isBlank(format)) {
				// check if there is some length information
				if (format.indexOf("{") != -1) { //$NON-NLS-1$
					// split the length information
					String[] sub = format.split("[\\{\\}]"); //$NON-NLS-1$
					FormatType type = FormatType.getType(sub[0]);
					try {
						int length = Integer.parseInt(sub[1]);
						formats.add(new Format(type, length));
					}
					catch (NumberFormatException e) {
						logClass.warn(I18n.getString("FormatParser.log.couldNotReadFormat", format)); //$NON-NLS-1$
						formats.add(new Format(type));
					}
				}
				else {
					FormatType type = FormatType.getType(format);
					formats.add(new Format(type));
				}
			}
		}
	}

	/**
	 * Formats the given data according to this formatting data
	 * 
	 * @param parser
	 *            the data encapsulated in a parser
	 * @return Object with the formatted data<br>
	 *         NOTE: Floats are formatted to double for convenience.
	 */
	public Object formatData(ByteParser parser) {
		ArrayList<Object> parsedData = new ArrayList<Object>(formats.size( ));
		for (Format format : formats) {
			switch (format.getType( )) {
				case CHAR:
					if (format.getLength( ) == -1) {
						parsedData.add(parser.getNextString( ));
					}
					else {
						parsedData.add(parser.getNextString(format.getLength( )));
					}
					break;
				case BYTE:
					parsedData.add(parser.getNextByte( ));
					break;
				case UBYTE:
					parsedData.add(parser.getNextUnsignedByte( ));
					break;
				case SHORT:
					parsedData.add(parser.getNextShort( ));
					break;
				case INTEGER:
					parsedData.add(parser.getNextInt( ));
					break;
				case DOUBLE:
					parsedData.add(parser.getNextDouble( ));
					break;
				case FLOAT:
					Float f = parser.getNextFloat( );
					NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
					nf.setMaximumFractionDigits(format.getLength( ));
					double d = Double.parseDouble(nf.format(f));
					parsedData.add(d);
					break;
				case LONG:
					parsedData.add(parser.getNextLong( ));
					break;
			}
		}
		if (parsedData.size( ) == 1) {
			return parsedData.get(0);
		}
		return parsedData;
	}

	/**
	 * Gets the original format of this parser
	 * 
	 * @return Returns the format.
	 */
	public final String getFormat() {
		return format;
	}
}
