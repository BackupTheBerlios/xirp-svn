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
 * TesterBotPackage.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 29.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.testerbot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the generated values for all available
 * {@link de.xirp.testerbot.TesterBotPackage testerbot}
 * keys. The instances of this object are sent to the application, 
 * where the values are put in the 
 * {@link de.xirp.io.comm.data.Datapool datapool}.
 * The name of the key used in the
 * {@link de.xirp.io.comm.data.Datapool datapool} are
 * contained as constants.
 * 
 * @author Matthias Gernand
 */
public class TesterBotPackage implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	private static final long serialVersionUID = -7223963997947177866L;
	/**
	 * The first battery value.
	 */
	public static final String BATTERY_1 = "battery1"; //$NON-NLS-1$
	/**
	 * The second battery value.
	 */
	public static final String BATTERY_2 = "battery2"; //$NON-NLS-1$
	/**
	 * The first speed value.
	 */
	public static final String SPEED_1 = "speed_unique"; //$NON-NLS-1$
	/**
	 * The first infrared value.
	 */
	public static final String IR_1 = "ir_left_front"; //$NON-NLS-1$
	/**
	 * The second infrared value.
	 */
	public static final String IR_2 = "ir_right_front"; //$NON-NLS-1$
	/**
	 * The third infrared value.
	 */
	public static final String IR_3 = "ir_left_rear"; //$NON-NLS-1$
	/**
	 * The fourth infrared value.
	 */
	public static final String IR_4 = "ir_right_rear"; //$NON-NLS-1$
	/**
	 * The first gyro value.
	 */
	public static final String GYRO_1 = "gyro_nick_unique"; //$NON-NLS-1$
	/**
	 * The second gyro value.
	 */
	public static final String GYRO_2 = "gyro_roll_unique"; //$NON-NLS-1$
	/**
	 * The first sonic value.
	 */
	public static final String SONIC_1 = "sonic_front"; //$NON-NLS-1$
	/**
	 * The second sonic value.
	 */
	public static final String SONIC_2 = "sonic_rear"; //$NON-NLS-1$
	/**
	 * The third sonic value.
	 */
	public static final String SONIC_3 = "sonic_left"; //$NON-NLS-1$
	/**
	 * The fourth sonic value.
	 */
	public static final String SONIC_4 = "sonic_right"; //$NON-NLS-1$
	/**
	 * The first temperature value.
	 */
	public static final String TEMPERATURE_1 = "temperature_unique"; //$NON-NLS-1$
	/**
	 * The first compass value.
	 */
	public static final String COMPASS_1 = "compass_unique"; //$NON-NLS-1$
	/**
	 * The first laser value.
	 */
	public static final String LASER_1 = "laser_unique"; //$NON-NLS-1$
	/**
	 * The first thermopile value.
	 */
	public static final String THERMO_1 = "thermopile_unique"; //$NON-NLS-1$
	/**
	 * The first carbon dioxide value.
	 */
	public static final String CO2_1 = "co2_unique"; //$NON-NLS-1$
	/**
	 * The first message value.
	 */
	public static final String MESSAGE_1 = "message_unique"; //$NON-NLS-1$
	/**
	 * The mapping between the keys and the value.
	 */
	private Map<String, Object> keyValueMap;

	/**
	 * Constructs a new package.
	 */
	public TesterBotPackage() {
		keyValueMap = new HashMap<String, Object>( );
	}

	/**
	 * Sets the value of the given key to the given value.
	 * 
	 * @param key
	 * 			The key.
	 * @param value
	 * 			The value to set.
	 */
	public void set(String key, Object value) {
		keyValueMap.put(key, value);
	}

	/**
	 * Returns the value of the given key.
	 * 
	 * @param key
	 * 			The key.
	 * @return The value of the key.
	 */
	public Object get(String key) {
		try {
			return keyValueMap.get(key);
		}
		catch (NullPointerException e) {
			return -1;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return keyValueMap.toString( );
	}
}
