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
 * Maximum.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 02.02.2007:		Created by Matthias Gernand.
 * 15.02.2007:		Added annotations for parsing with JAXB.
 */
package de.xirp.profile;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * This class represents a maximum of a value. It is used for
 * actuators and sensors. The unit of this value is the
 * {@link de.xirp.profile.Units.SensorValueUnit unit}
 * specified in the {@link de.xirp.profile.Actuator} or
 * {@link de.xirp.profile.Sensor}. The difference between
 * actuator and sensor is the fact that the maximum value of a sensor is 
 * specified in the {@link de.xirp.profile.SensorSpecs} of
 * the sensor. The maximum of the actuator is specified in the 
 * {@link de.xirp.profile.Actuator} class.
 * <br><br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files 
 * to the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * 
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Actuator
 * @see de.xirp.profile.Sensor
 * @see de.xirp.profile.SensorSpecs
 */
@XmlType
public final class Maximum implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 1126700583845547509L;
	/**
	 * The maximum value.
	 */
	@XmlValue
	private double maxValue = -1.0;

	/**
	 * Returns the maximum value.
	 * 
	 * @return The max value.
	 */
	@XmlTransient
	public double getMaxValue() {
		return maxValue;
	}

	/**
	 * Sets the maximum value.
	 * 
	 * @param maxValue 
	 * 			The max value to set.
	 */
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
}
