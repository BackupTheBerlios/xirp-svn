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
 * Actuators.java
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
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents all servos of the robot as a list of groups.
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
public final class Actuators implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = -8901004724175360833L;
	/**
	 * The actuator count.
	 */
	@XmlAttribute(name = "count", required = true)
	private int count = 0;
	/**
	 * {@link java.util.Vector} with the
	 * {@link de.xirp.profile.ActuatorGroup}s of the
	 * robot.
	 * 
	 * @see de.xirp.profile.ActuatorGroup
	 */
	@XmlElement(name = "actuatorgroup", required = true)
	private Vector<ActuatorGroup> actuatorGroups = new Vector<ActuatorGroup>( );

	/**
	 * Returns the count of actuators of the robot.
	 * 
	 * @return The count.
	 */
	@XmlTransient
	public int getCount() {
		return count;
	}

	/**
	 * Sets the count of actuators of the robot.
	 * 
	 * @param count
	 *            The count to set.
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Returns an unmodifiable list of
	 * {@link de.xirp.profile.ActuatorGroup}s.
	 * 
	 * @return The actuator groups.
	 * @see de.xirp.profile.ActuatorGroup
	 */
	@XmlTransient
	public List<ActuatorGroup> getActuatorGroups() {
		return Collections.unmodifiableList(actuatorGroups);
	}

	/**
	 * Adds a {@link de.xirp.profile.ActuatorGroup} to
	 * the robot.
	 * 
	 * @param actuatorGroup
	 *            The group of actuators to add.
	 */
	public void addActuatorGroup(ActuatorGroup actuatorGroup) {
		this.actuatorGroups.add(actuatorGroup);
	}
}
