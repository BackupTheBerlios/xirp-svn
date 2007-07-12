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
 * DefaultSensorData.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 15.02.2007:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

import de.xirp.profile.Position;
import de.xirp.profile.Units.SensorValueUnit;

/**
 * Extension for the {@link AbstractData} with predefined fields for
 * the default options for sensors defined in the profile.
 * 
 * @author Rabea Gransberger
 */
public class DefaultSensorData extends AbstractData {

	/**
	 * If a String is not given, this is the default value for it
	 */
	protected static final String NONE = "UNDEFINED"; //$NON-NLS-1$
	/**
	 * The unit for this sensor. The default is
	 * {@link de.xirp.profile.Units.SensorValueUnit#PERCENT}
	 */
	private SensorValueUnit unit = SensorValueUnit.PERCENT;
	/**
	 * Minimum value for this sensor in the given unit. The default is
	 * <code>0</code>
	 */
	private double min = 0;
	/**
	 * Maximum value for this sensor in the given unit. The default is
	 * <code>10</code>
	 */
	private double max = 10;
	/**
	 * The id of this sensor
	 */
	private int id = 0;
	/**
	 * The position of the sensor on the robot
	 */
	private Position position;
	/**
	 * The long name of the sensor group this sensor belongs to or
	 * {@value de.xirp.plugin.DefaultSensorData#NONE}
	 */
	private String longName = NONE;
	/**
	 * The datapool key which is concatenation of the groups and the
	 * sensors key. If no key was found the value is
	 * {@value de.xirp.plugin.DefaultSensorData#NONE}
	 */
	private String datapoolKey = NONE;
	/**
	 * The plugin this data belongs to.
	 */
	@SuppressWarnings("unchecked")
	protected AbstractPlugin plugin;

	/**
	 * Constructs a new default sensor data for the given plugin.
	 * 
	 * @param plugin
	 *            the plugin this data belongs to
	 */
	@SuppressWarnings("unchecked")
	public DefaultSensorData(AbstractPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Gets the unit for this sensor. The default is
	 * {@link de.xirp.profile.Units.SensorValueUnit#PERCENT}
	 * 
	 * @return the unit
	 */
	public final SensorValueUnit getUnit() {
		return unit;
	}

	/**
	 * Gets the Minimum value for this sensor in the given unit. The
	 * default is <code>0</code>.
	 * 
	 * @return the minimum
	 */
	public final double getMin() {
		return min;
	}

	/**
	 * Gets the Maximum value for this sensor in the given unit. The
	 * default is <code>10</code>.
	 * 
	 * @return the maximum
	 */
	public final double getMax() {
		return max;
	}

	/**
	 * Gets the id of this sensor.
	 * 
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Gets the position of the sensor on the robot.
	 * 
	 * @return the position
	 */
	public final Position getPosition() {
		return position;
	}

	/**
	 * Gets the long name of the sensor group this sensor belongs to
	 * or {@value de.xirp.plugin.DefaultSensorData#NONE}.
	 * 
	 * @return the longName of the sensor group
	 */
	public final String getLongName() {
		return longName;
	}

	/**
	 * Gets the datapool key which is concatenation of the groups and
	 * the sensors key. If no key was found the value is
	 * {@value de.xirp.plugin.DefaultSensorData#NONE}.
	 * 
	 * @return the datapoolKey
	 */
	public final String getDatapoolKey() {
		return datapoolKey;
	}

	/**
	 * Sets the unit for this sensor.
	 * 
	 * @param unit
	 *            the unit to set
	 */
	protected final void setUnit(SensorValueUnit unit) {
		this.unit = unit;
	}

	/**
	 * Sets the minimum value of this sensor.
	 * 
	 * @param min
	 *            the min to set
	 */
	public void setMin(double min) {
		this.min = min;
	}

	/**
	 * Sets the maximum value of this sensor.
	 * 
	 * @param max
	 *            the max to set
	 */
	public void setMax(double max) {
		this.max = max;
	}

	/**
	 * Sets the id of this sensor.
	 * 
	 * @param id
	 *            the id to set
	 */
	protected final void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the position of this sensor.
	 * 
	 * @param position
	 *            the position to set
	 */
	protected final void setPosition(Position position) {
		this.position = position;
	}

	/**
	 * Sets the long name of the sensor group.
	 * 
	 * @param longName
	 *            the longName to set
	 */
	protected final void setLongName(String longName) {
		this.longName = longName;
	}

	/**
	 * Sets the key for the datapool.
	 * 
	 * @param datapoolKey
	 *            the datapoolKey to set
	 */
	protected final void setDatapoolKey(String datapoolKey) {
		this.datapoolKey = datapoolKey;
	}

}
