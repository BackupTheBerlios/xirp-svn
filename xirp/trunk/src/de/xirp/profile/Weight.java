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
 * Weight.java
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

import de.xirp.profile.Units.MassUnit;

/**
 * This class holds informations about the weight of the robot: the 
 * value and the unit of this value.
 * <br><br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files 
 * to the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * 
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Units.MassUnit
 */
@XmlType
public final class Weight implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 2999275864652550518L;
	/**
	 * The weight value.
	 */
	@XmlValue
	private double weight = -1.0;
	/**
	 * The {@link de.xirp.profile.Units.MassUnit unit} of the value.
	 */
	@XmlAttribute(name = "unit", required = true)//$NON-NLS-1$
	private MassUnit unit = null;

	/**
	 * Returns the {@link de.xirp.profile.Units.MassUnit unit}
	 * of the value.
	 * 
	 * @return The unit.
	 * 
	 * @see de.xirp.profile.Units.MassUnit
	 */
	@XmlTransient
	public MassUnit getUnit() {
		return unit;
	}

	/**
	 * Sets the {@link de.xirp.profile.Units.MassUnit unit}
	 * of the value.
	 * 
	 * @param unit 
	 * 			The unit to set.
	 * 
	 * @see de.xirp.profile.Units.MassUnit
	 */
	public void setUnit(MassUnit unit) {
		this.unit = unit;
	}

	/**
	 * Returns the weight value.
	 * 
	 * @return The weight.
	 */
	@XmlTransient
	public double getWeight() {
		return weight;
	}

	/**
	 * Sets the weight value.
	 * 
	 * @param weight 
	 * 			The weight to set.
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
}
