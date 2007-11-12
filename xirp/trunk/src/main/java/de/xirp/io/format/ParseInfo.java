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
 * ParseInfo.java
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
 * This class contains information useful for parsing data received
 * from the robot.
 * 
 * @author Rabea Gransberger
 */
public final class ParseInfo {

	/**
	 * Key under which the parsed data should be set in the datapool
	 */
	private String datapoolKey;
	/**
	 * Format in which this data is received
	 */
	private FormatParser receiveFormat;

	/**
	 * This class contains information useful for parsing data
	 * received from the robot.
	 * 
	 * @param datapoolKey
	 *            Key under which the parsed data should be set in the
	 *            datapool
	 * @param receiveFormat
	 *            Format in which this data is received
	 */
	public ParseInfo(String datapoolKey, String receiveFormat) {
		this.datapoolKey = datapoolKey;
		this.receiveFormat = new FormatParser(receiveFormat);

	}

	/**
	 * Key under which this data should be saved in the datapool.
	 * 
	 * @return Returns the datapoolKey.
	 */
	public String getDatapoolKey() {
		return datapoolKey;
	}

	/**
	 * NOTE: Use {@link de.xirp.io.format.FormatParser}
	 * for parsing this format.
	 * 
	 * @return Returns the receiveFormat.
	 */
	public FormatParser getReceiveFormat() {
		return receiveFormat;
	}

}
