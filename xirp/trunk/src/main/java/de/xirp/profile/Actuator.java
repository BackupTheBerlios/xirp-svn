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
 * Actuator.java
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import de.xirp.profile.Units.ActuatorValueUnit;

/**
 * This class represents an actuator of a robot.
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
public final class Actuator implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = -7068508520548941516L;
	/**
	 * The id of the actuator inside the group.
	 */
	@XmlAttribute(name = "id", required = true)//$NON-NLS-1$
	private int id = -1;
	/**
	 * The unit of the actuator value, f.e. degrees for a servo motor.
	 * 
	 * @see de.xirp.profile.Units.ActuatorValueUnit
	 */
	@XmlAttribute(name = "unit", required = true)//$NON-NLS-1$
	private ActuatorValueUnit unit = null;
	/**
	 * Name of the actuator.
	 */
	@XmlAttribute(name = "name", required = true)//$NON-NLS-1$
	private String name = ""; //$NON-NLS-1$
	/**
	 * The minimum value the actuator can reach.
	 * 
	 * @see de.xirp.profile.Minimum
	 */
	@XmlElement(name = "minimum", required = true)//$NON-NLS-1$
	private Minimum minimum = null;
	/**
	 * The maximum value the actuator can reach.
	 * 
	 * @see de.xirp.profile.Maximum
	 */
	@XmlElement(name = "maximum", required = true)//$NON-NLS-1$
	private Maximum maximum = null;

	/**
	 * Returns the id of the actuator.
	 * 
	 * @return The id.
	 */
	@XmlTransient
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of the actuator.
	 * 
	 * @param id
	 *            The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the name of the actuator.
	 * 
	 * @return The name.
	 */
	@XmlTransient
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the actuator.
	 * 
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the maximum value of the actuator.
	 * 
	 * @return The maximum value.
	 * 
	 * @see de.xirp.profile.Maximum
	 */
	@XmlTransient
	public Maximum getMaximum() {
		return maximum;
	}

	/**
	 * Sets the maximum value of the actuator.
	 * 
	 * @param maximum 
	 * 			The maximum value to set.
	 * 
	 * @see de.xirp.profile.Maximum
	 */
	public void setMaximum(Maximum maximum) {
		this.maximum = maximum;
	}

	/**
	 * Returns the minimum of the actuator.
	 * 
	 * @return The minimum value.
	 * 
	 * @see de.xirp.profile.Minimum
	 */
	@XmlTransient
	public Minimum getMinimum() {
		return minimum;
	}

	/**
	 * Sets the minimum value of the actuator.
	 * 
	 * @param minimum 
	 * 			The minimum value to set.
	 * 
	 * @see de.xirp.profile.Minimum
	 */
	public void setMinimum(Minimum minimum) {
		this.minimum = minimum;
	}

	/**
	 * Returns the unit of the actuator.
	 * 
	 * @return The unit.
	 * 
	 * @see de.xirp.profile.Units.ActuatorValueUnit
	 */
	@XmlTransient
	public ActuatorValueUnit getUnit() {
		return unit;
	}

	/**
	 * Sets the unit of the actuator.
	 * 
	 * @param unit 
	 * 			The unit to set.
	 * 
	 * @see de.xirp.profile.Units.ActuatorValueUnit
	 */
	public void setUnit(ActuatorValueUnit unit) {
		this.unit = unit;
	}
}
