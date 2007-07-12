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
 * Length.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 01.02.2007:		Created by Matthias Gernand.
 * 15.02.2007:		Added annotations for parsing with JAXB.
 */
package de.xirp.profile;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import de.xirp.profile.Units.DistanceUnit;

/**
 * This class holds informations about the length of the robot: the
 * value and the unit of this value. <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Units.DistanceUnit
 */
@XmlType
public final class Length implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 2633627891872814037L;
	/**
	 * The length value.
	 */
	@XmlValue
	private double length = -1.0;
	/**
	 * The
	 * {@link de.xirp.profile.Units.DistanceUnit unit} of
	 * the value.
	 */
	@XmlAttribute(name = "unit", required = true)
	private DistanceUnit unit = null;

	/**
	 * Returns the length value.
	 * 
	 * @return The length.
	 */
	@XmlTransient
	public double getLength() {
		return length;
	}

	/**
	 * Sets the length value.
	 * 
	 * @param length
	 *            The length to set.
	 */
	public void setLength(double length) {
		this.length = length;
	}

	/**
	 * Returns the
	 * {@link de.xirp.profile.Units.DistanceUnit unit} of
	 * the value.
	 * 
	 * @return The unit.
	 * @see de.xirp.profile.Units.DistanceUnit
	 */
	@XmlTransient
	public DistanceUnit getUnit() {
		return unit;
	}

	/**
	 * Sets the
	 * {@link de.xirp.profile.Units.DistanceUnit unit} of
	 * the value.
	 * 
	 * @param unit
	 *            The unit to set.
	 * @see de.xirp.profile.Units.DistanceUnit
	 */
	public void setUnit(DistanceUnit unit) {
		this.unit = unit;
	}
}
