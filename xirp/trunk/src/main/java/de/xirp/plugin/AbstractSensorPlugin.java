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
 * AbstractSensorPlugin.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 26.06.2006:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

import java.util.Collections;
import java.util.List;

import de.xirp.io.comm.data.DatapoolListener;
import de.xirp.io.comm.data.DatapoolUtil;
import de.xirp.io.logging.RobotLogger;
import de.xirp.profile.ProfileManager;
import de.xirp.profile.RobotNotFoundException;
import de.xirp.profile.Sensor;
import de.xirp.profile.Sensorgroup;
import de.xirp.profile.Units.SensorValueUnit;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This class provides a base implementation for sensor plugins. The
 * <code>PluginType</code> is set to {@link PluginType#SENSOR} and
 * methods for reading the sensor information from the profile are
 * provided.
 * 
 * @param <GAbstractData>
 *            The type for the data to use for this plugin. If none
 *            use {@link AbstractData}
 * @param <GAbstractPluginGUI>
 *            The type for the UI of this plugin. If none use
 *            {@link AbstractPluginGUI}
 * @author Rabea Gransberger
 */
public abstract class AbstractSensorPlugin<GAbstractData extends AbstractData, GAbstractPluginGUI extends AbstractPluginGUI>
		extends AbstractPlugin<GAbstractData, GAbstractPluginGUI> {

	/**
	 * The Logger for this class
	 */
	private static final RobotLogger logClass = RobotLogger.getLogger(AbstractSensorPlugin.class);
	/**
	 * The datapool key for this plugin. Only used if there's only one
	 * sensor.
	 */
	private String datapoolKey;

	/**
	 * Construct a new sensor plugin
	 * 
	 * @param robotName
	 *            name of the robot, this plugin is for
	 * @param ownInfo
	 *            information about the plugin, read from the
	 *            plugin.properties file
	 */
	public AbstractSensorPlugin(String robotName, PluginInfo ownInfo) {
		super(robotName, ownInfo);
	}

	/**
	 * {@inheritDoc}<br/><br/>Nothing is done in this
	 * implementation.
	 * 
	 * @see de.xirp.plugin.AbstractPlugin#runInternal()
	 */
	@Override
	protected void runInternal() {
		// nothing to do
	}

	/**
	 * {@inheritDoc}<br/><br/>Nothing is done in this
	 * implementation.
	 * 
	 * @return <code>true</code>
	 * @see de.xirp.plugin.AbstractPlugin#stopInternal()
	 */
	@Override
	protected boolean stopInternal() {
		return true;
	}

	/**
	 * @return {@link VisualizationType#EMBEDDED}
	 * @see de.xirp.plugin.IPlugable#getVisualizationType()
	 */
	@Override
	public int getVisualizationType() {
		return VisualizationType.EMBEDDED;
	}

	/**
	 * @return {@link PluginType#SENSOR}
	 * @see de.xirp.plugin.IPlugable#getPluginType()
	 */
	public int getPluginType() {
		return PluginType.SENSOR;
	}

	/**
	 * Gets the datapool key if it was set by calling
	 * {@link #getSensor()}
	 * 
	 * @return the datapoolKey or <code>null</code> if no key was
	 *         set
	 */
	protected String getDatapoolKey() {
		if (datapoolKey == null) {
			logClass.debug(robotName,
					I18n.getString("AbstractSensorPlugin.no.datapoolkey", this.getName( )) //$NON-NLS-1$
							+ Constants.LINE_SEPARATOR);
		}
		return datapoolKey;
	}

	/**
	 * Adds a receive listener for the datapool of the plugins robot
	 * if the datapool for this robot exists.<br/><br/> The listener
	 * is saved in an internal list for this plugin and will be
	 * removed from the datapool when the plugin stops, but you can
	 * still remove it on your own if necessary.
	 * 
	 * @param listener
	 *            the listener to add
	 * @see de.xirp.io.comm.data.Datapool#addDatapoolReceiveListener(String,
	 *      DatapoolListener)
	 */
	protected void addDatapoolReceiveListener(DatapoolListener listener) {
		if (getDatapoolKey( ) != null) {
			super.addDatapoolReceiveListener(datapoolKey, listener);
		}
	}

	/**
	 * Get's the sensor groups for this plugins sensors and robot from
	 * the profile with all the information needed for displaying this
	 * sensor
	 * 
	 * @return list of sensor groups
	 */
	protected List<Sensorgroup> getSensorgroups() {
		try {
			return ProfileManager.getRobot(robotName)
					.getSensorgroups(this.getClass( ).getName( ),
							this.uniqueIdentifier);
		}
		catch (RobotNotFoundException e) {
			logClass.error(this.robotName, "Error " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
		return Collections.emptyList( );
	}

	/**
	 * Get's the sensor group for this plugins sensor and robot from
	 * the profile with all the information needed for displaying this
	 * sensor. If there is more than one sensor group for this plugin
	 * the first one is returned and a log message is printed.
	 * 
	 * @return the sensor group or <code>null</code> if none
	 *         according found
	 */
	protected Sensorgroup getSensorgroup() {
		List<Sensorgroup> displays = getSensorgroups( );
		if (!displays.isEmpty( )) {
			if (displays.size( ) > 1) {
				logClass.info(robotName,
						I18n.getString("AbstractSensorPlugin.onlyone.group", this.getName( ), displays.size( )) //$NON-NLS-1$
								+ Constants.LINE_SEPARATOR);
			}
			Sensorgroup group = displays.get(0);
			return group;

		}
		return null;
	}

	/**
	 * Gets the sensor for this plugin. This method may be used if
	 * there only one sensor group which contains only one sensor.
	 * Otherwise alway the first is returned and log messages are
	 * printed. This method reads the datapool key from the sensor and
	 * set's it locally.
	 * 
	 * @return the sensor for this plugin or <code>null</code> if
	 *         none was found
	 */
	protected Sensor getSensor() {
		Sensorgroup group = getSensorgroup( );
		if (group != null) {
			Sensor sensor = getSensor(group);
			if (sensor != null) {
				this.datapoolKey = DatapoolUtil.createDatapoolKey(group, sensor);
			}
			return sensor;
		}
		else {
			return null;
		}
	}

	/**
	 * Gets the sensor from this sensor group. If there's more than
	 * one sensor, the first one is returned and a log message is
	 * printed
	 * 
	 * @param group
	 *            the sensor group
	 * @return the sensor or <code>null</code> if the sensor group
	 *         was <code>null</code>
	 */
	protected Sensor getSensor(Sensorgroup group) {
		if (group != null) {
			List<Sensor> sensors = group.getSensors( );
			if (!sensors.isEmpty( )) {
				if (sensors.size( ) > 1) {
					logClass.info(robotName,
							I18n.getString("AbstractSensorPlugin.onlyone.sensor", this.getName( ), sensors.size( )) //$NON-NLS-1$
									+ Constants.LINE_SEPARATOR);
				}
				return sensors.get(0);
			}
		}

		return null;
	}

	/**
	 * Reads the standard sensor data (everything but options) from
	 * the sensor for the group and writes them to the plugin data if
	 * the data is an instance of {@link DefaultSensorData}.
	 * 
	 * @param group
	 *            the group. Nothing is read if the group is
	 *            <code>null</code>.
	 * @param sensor
	 *            the sensor. Nothing is read if the sensor is
	 *            <code>null</code>.
	 */
	protected void readSensorDefaults(Sensorgroup group, Sensor sensor) {
		if (group == null || sensor == null) {
			return;
		}

		GAbstractData data = getPluginData( );
		if (data != null && data instanceof DefaultSensorData) {
			DefaultSensorData pluginData = (DefaultSensorData) data;

			if (sensor.getUnit( ) != null) {
				pluginData.setUnit(sensor.getUnit( ));
			}
			else {
				pluginData.setUnit(SensorValueUnit.PERCENT);
			}
			pluginData.setMax(sensor.getSpecs( ).getMaximum( ).getMaxValue( ));
			pluginData.setMin(sensor.getSpecs( ).getMinimum( ).getMinValue( ));
			pluginData.setId(sensor.getId( ));
			pluginData.setLongName(group.getLongName( ));
			pluginData.setDatapoolKey(DatapoolUtil.createDatapoolKey(group,
					sensor));
			pluginData.setPosition(sensor.getSpecs( ).getPosition( ));
		}
	}

}
