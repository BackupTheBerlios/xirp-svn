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
 * PowerSource.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 02.03.2006:		Created by Matthias Gernand.
 * 15.02.2007:		Added annotations for parsing with JAXB.
 */
package de.xirp.profile;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import de.xirp.profile.Units.PowerSourceUnit;

/**
 * This class represents a power source of a robot. Power sources may
 * not only be batteries. The possible variants of power sources are
 * defined in the power source
 * {@link de.xirp.profile.Units.PowerSourceUnit unit}
 * enum. <br>
 * <br>
 * A power source has its own
 * {@link de.xirp.io.comm.data.Datapool datapool} key,
 * because it behaves like a sensor measuring the values of a power
 * source but has several special cases like being shown in the status
 * bar. Therefore the power sources must be identified directly via
 * the profile. <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 * @see de.xirp.io.comm.data.Datapool
 */
@XmlType
public final class PowerSource implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 4759445844198302814L;
	/**
	 * Indicates the value when a low amount warning should occur.
	 */
	@XmlAttribute(name = "warningValue", required = true)
	private double warningValue = 0.0;
	/**
	 * The maximum value of the power source.
	 */
	@XmlAttribute(name = "max", required = true)
	private double max = 0.0;
	/**
	 * The
	 * {@link de.xirp.profile.Units.PowerSourceUnit unit}
	 * of the power source value.
	 */
	@XmlAttribute(name = "unit", required = true)
	private PowerSourceUnit unit = null;
	/**
	 * The {@link de.xirp.io.comm.data.Datapool datapool}
	 * key of the power source.
	 */
	@XmlAttribute(name = "datapoolKey", required = true)
	private String datapoolKey = null;
	/**
	 * The name of the power source.
	 */
	@XmlValue
	private String name = ""; //$NON-NLS-1$

	/**
	 * Returns the maximum value of the power source.
	 * 
	 * @return The maximum value.
	 */
	@XmlTransient
	public double getMax() {
		return max;
	}

	/**
	 * Sets the maximum value of the power source.
	 * 
	 * @param max
	 *            The maximum value to set.
	 */
	public void setMax(double max) {
		this.max = max;
	}

	/**
	 * Returns the low amount warning level.
	 * 
	 * @return The warning value.
	 */
	@XmlTransient
	public double getWarningValue() {
		return warningValue;
	}

	/**
	 * Sets the low amount warning level.
	 * 
	 * @param warningValue
	 *            The warning value to set.
	 */
	public void setWarningValue(double warningValue) {
		this.warningValue = warningValue;
	}

	/**
	 * Returns the name of the power source.
	 * 
	 * @return The name.
	 */
	@XmlTransient
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the power source.
	 * 
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the
	 * {@link de.xirp.profile.Units.PowerSourceUnit unit}
	 * of the power source value.
	 * 
	 * @return The unit.
	 * @see de.xirp.profile.Units.PowerSourceUnit
	 */
	@XmlTransient
	public PowerSourceUnit getUnit() {
		return unit;
	}

	/**
	 * Sets the
	 * {@link de.xirp.profile.Units.PowerSourceUnit unit}
	 * of the power source value.
	 * 
	 * @param unit
	 *            The unit to set.
	 * @see de.xirp.profile.Units.PowerSourceUnit
	 */
	public void setUnit(PowerSourceUnit unit) {
		this.unit = unit;
	}

	/**
	 * Returns the
	 * {@link de.xirp.io.comm.data.Datapool datapool} key
	 * of the power source.
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
	 * of the power source.
	 * 
	 * @param datapoolKey
	 *            The datapool key to set.
	 */
	public void setDatapoolKey(String datapoolKey) {
		this.datapoolKey = datapoolKey;
	}

}
