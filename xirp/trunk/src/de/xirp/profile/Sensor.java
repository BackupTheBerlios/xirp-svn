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
 * Sensor.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 15.02.2006:		Created by Matthias Gernand.
 * 15.02.2007:		Added annotations for parsing with JAXB.
 */
package de.xirp.profile;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import de.xirp.profile.Units.SensorValueUnit;

/**
 * This class represents a sensor specification.
 * <br><br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files 
 * to the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * 
 * @see javax.xml.bind.annotation
 */
@XmlType
public final class Sensor implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = -2216605907367838960L;
	/**
	 * The unique id of the sensor inside the 
	 * {@link de.xirp.profile.Sensorgroup group}.
	 * 
	 * @see de.xirp.profile.Sensorgroup
	 */
	@XmlAttribute(name = "id", required = true)//$NON-NLS-1$
	private int id = 0;
	/**
	 * The {@link de.xirp.profile.Units.SensorValueUnit unit}
	 * of the the sensor value.
	 * 
	 * @see de.xirp.profile.Units.SensorValueUnit
	 */
	@XmlAttribute(name = "unit", required = true)//$NON-NLS-1$
	private SensorValueUnit unit = null;
	/**
	 * The sub key of the sensor. The whole 
	 * {@link de.xirp.io.comm.data.Datapool datapool} key is
	 * constructed from the key for the group an underscore and the sub key:
	 * <br><b>
	 * <center><b>sensorgroup_subkey</b></center>
	 * 
	 * @see de.xirp.io.comm.data.Datapool
	 */
	@XmlAttribute(name = "subKey", required = true)//$NON-NLS-1$
	private String subKey = ""; //$NON-NLS-1$
	/**
	 * The {@link de.xirp.profile.SensorSpecs} of the sensor.
	 * 
	 * @see de.xirp.profile.SensorSpecs
	 */
	@XmlElement(name = "sensorspecs", required = true)//$NON-NLS-1$
	private SensorSpecs specs = null;

	/**
	 * Returns the {@link de.xirp.profile.SensorSpecs}
	 * of the sensor.
	 * 
	 * @see de.xirp.profile.SensorSpecs
	 * 
	 * @return The sensor specs.
	 */
	@XmlTransient
	public SensorSpecs getSpecs() {
		return specs;
	}

	/**
	 * Sets the {@link de.xirp.profile.SensorSpecs}
	 * of the sensor.
	 * 
	 * @see de.xirp.profile.SensorSpecs
	 * 
	 * @param specs
	 *            The sensor specs to set.
	 */
	public void setSpecs(SensorSpecs specs) {
		this.specs = specs;
	}

	/**
	 * Returns the unique id of the sensor inside the group.
	 * 
	 * @return The unique id.
	 */
	@XmlTransient
	public int getId() {
		return id;
	}

	/**
	 * Sets the unique id of the sensor inside the group.
	 * 
	 * @param id
	 *            The unique id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the sub key for this sensor.
	 * 
	 * @return The sub key.
	 */
	@XmlTransient
	public String getSubKey() {
		return subKey;
	}

	/**
	 * Sets the sub key for this sensor.
	 * 
	 * @param subKey 
	 * 			The sub key to set.
	 */
	public void setSubKey(String subKey) {
		this.subKey = subKey;
	}

	/**
	 * Returns the 
	 * {@link de.xirp.profile.Units.SensorValueUnit unit}
	 * of the value of the sensor.
	 * 
	 * @return The unit
	 */
	@XmlTransient
	public SensorValueUnit getUnit() {
		return unit;
	}

	/**
	 * Sets the 
	 * {@link de.xirp.profile.Units.SensorValueUnit unit}
	 * of the value of the sensor.
	 * 
	 * @param unit 
	 * 			The unit to set.
	 */
	public void setUnit(SensorValueUnit unit) {
		this.unit = unit;
	}
}
