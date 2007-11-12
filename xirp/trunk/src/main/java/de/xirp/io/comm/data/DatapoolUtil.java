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
 * DatapoolUtil.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de];
 * Contributor(s):   
 *
 * Changes
 * -------
 * 25.05.2007:		Created by Rabea Gransberger.
 */
package de.xirp.io.comm.data;

import de.xirp.profile.Sensor;
import de.xirp.profile.Sensorgroup;
import de.xirp.util.Constants;
import de.xirp.util.Util;

/**
 * Utility methods which may be helpful when dealing with the
 * datapool.
 * 
 * @author Rabea Gransberger
 */
public final class DatapoolUtil {

	/**
	 * Creates datapool key from the given datapool key of the sensor
	 * group and the sub-key of the sensor.
	 * 
	 * @param sensorGroupKey
	 *            the datapool key of the sensor group
	 * @param subKey
	 *            the sub-key of the sensor
	 * @return the datapool key which may be used for
	 *         {@link Datapool#addDatapoolReceiveListener(String, DatapoolListener)}
	 *         as first argument
	 */
	private static String createDatapoolKey(String sensorGroupKey, String subKey) {
		if (Util.isEmpty(subKey)) {
			return sensorGroupKey;
		}
		return sensorGroupKey + Constants.KEY_SUBKEY_SEPARATOR + subKey;
	}

	/**
	 * Creates datapool key from the given sensor group and sensor.
	 * 
	 * @param sensorGroup
	 *            the sensor group
	 * @param sensor
	 *            a sensor of the group
	 * @return the datapool key which may be used for
	 *         {@link Datapool#addDatapoolReceiveListener(String, DatapoolListener)}
	 *         as first argument
	 */
	public static String createDatapoolKey(Sensorgroup sensorGroup,
			Sensor sensor) {
		return createDatapoolKey(sensorGroup.getDatapoolKey( ),
				sensor.getSubKey( ));
	}
}
