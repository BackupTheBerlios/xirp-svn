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
 * ActuatorGroup.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 04.06.2006:		Created by Matthias Gernand.
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
 * A group of actuators of the robot. The group has a unique name and
 * contains a list of {@link de.xirp.profile.Actuator}s .
 * <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Actuator
 */
@XmlType
public final class ActuatorGroup implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 3091099437144342573L;
	/**
	 * The name of this actuator group.
	 */
	@XmlAttribute(name = "name", required = true)
	private String name = ""; //$NON-NLS-1$
	/**
	 * The {@link de.xirp.profile.Actuator}s of this
	 * actuator group in a {@link java.util.Vector}.
	 * 
	 * @see de.xirp.profile.Actuator
	 */
	@XmlElement(name = "actuator", required = true)
	private Vector<Actuator> actuators = new Vector<Actuator>( );

	/**
	 * Returns the name of the actuator group.
	 * 
	 * @return The name.
	 */
	@XmlTransient
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the actuator group.
	 * 
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns an unmodifiable list of the
	 * {@link de.xirp.profile.Actuator}s of this group.
	 * 
	 * @return The actuators.
	 * @see de.xirp.profile.Actuator
	 */
	@XmlTransient
	public List<Actuator> getActuators() {
		return Collections.unmodifiableList(actuators);
	}

	/**
	 * Adds and {@link de.xirp.profile.Actuator} to this
	 * actuator group.
	 * 
	 * @param actuator
	 *            The actuator to add.
	 * @see de.xirp.profile.Actuator
	 */
	public void addActuator(Actuator actuator) {
		this.actuators.add(actuator);
	}
}
