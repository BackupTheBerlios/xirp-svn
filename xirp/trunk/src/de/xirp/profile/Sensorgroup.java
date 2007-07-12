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
 * Sensorgroup.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 09.03.2006:		Created by Matthias Gernand.
 * 15.02.2007:		Added annotations for parsing with JAXB.
 */
package de.xirp.profile;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a group of
 * {@link de.xirp.profile.Sensor sensors} of a robot.
 * <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 */
@XmlType
public final class Sensorgroup implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 1878479629773713963L;
	/**
	 * Indicator whether the group is visible or not.
	 */
	@XmlAttribute(name = "visible", required = true)
	private boolean visible = false;
	/**
	 * The long name of the group
	 */
	@XmlAttribute(name = "longName", required = true)
	private String longName = ""; //$NON-NLS-1$
	/**
	 * The key under which the incoming data of the sensor group is
	 * saved in the
	 * {@link de.xirp.io.comm.data.Datapool datapool}.
	 * Each {@link de.xirp.profile.Sensor sensor} has its
	 * own key that fulfills the datapool key for a sensor.
	 * 
	 * @see de.xirp.profile.CommunicationDatum#getDatapoolKey()
	 */
	@XmlAttribute(name = "datapoolKey", required = true)
	private String datapoolKey = ""; //$NON-NLS-1$
	/**
	 * The {@link de.xirp.profile.Sensor sensors} of this
	 * group.
	 */
	@XmlElement(name = "sensor", required = true)
	private Vector<Sensor> sensors = new Vector<Sensor>( );

	/**
	 * Returns all {@link de.xirp.profile.Sensor sensors}
	 * of this group.
	 * 
	 * @return An unmodifiable list with the sensors.
	 * @see de.xirp.profile.Sensor
	 */
	@XmlTransient
	public List<Sensor> getSensors() {
		return Collections.unmodifiableList(sensors);
	}

	/**
	 * Adds an {@link de.xirp.profile.Sensor sensor} to
	 * this group.
	 * 
	 * @param sensor
	 *            The sensor to add.
	 * @see de.xirp.profile.Sensor
	 */
	public void addSensor(Sensor sensor) {
		this.sensors.add(sensor);
	}

	/**
	 * Returns <code>true</code>if a group should be visible.
	 * 
	 * @return The visible status of the group.
	 * @deprecated This was used when "Sensorgroup" was a
	 *             "SensorDisplay". It has no affect and will be
	 *             removed in a future release.
	 */
	@XmlTransient
	@Deprecated
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets the visible status of the sensor group.
	 * 
	 * @param visible
	 *            <code>true</code> if the group should be visible.
	 * @deprecated This was used when "Sensorgroup" was a
	 *             "SensorDisplay". It has no affect and will be
	 *             removed in a future release.
	 */
	@Deprecated
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Returns the long name of the sensor group.
	 * 
	 * @return The long name.
	 */
	@XmlTransient
	public String getLongName() {
		return longName;
	}

	/**
	 * Sets the long name of the sensor group.
	 * 
	 * @param longName
	 *            The long name to set.
	 */
	public void setLongName(String longName) {
		this.longName = longName;
	}

	/**
	 * Returns the
	 * {@link de.xirp.io.comm.data.Datapool datapool} key
	 * of this group.
	 * 
	 * @return The datapool key.
	 */
	@XmlTransient
	public String getDatapoolKey() {
		return datapoolKey;
	}

	/**
	 * Sets the
	 * {@link de.xirp.io.comm.data.Datapool datapool} key
	 * of this group.
	 * 
	 * @param datapoolKey
	 *            The datapool key to set.
	 */
	public void setDatapoolKey(String datapoolKey) {
		this.datapoolKey = datapoolKey;
	}

}
