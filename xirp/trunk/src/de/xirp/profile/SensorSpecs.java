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
 * SensorSpecs.java
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
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a sensor specification holding the minimum
 * and maximum range, the position and optional values.
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
public final class SensorSpecs implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = -8048891017911945039L;
	/**
	 * The {@link de.xirp.profile.Position position} of the sensor.
	 * 
	 * @see de.xirp.profile.Position
	 */
	@XmlElement(name = "position", required = true)//$NON-NLS-1$
	private Position position = null;
	/**
	 * The {@link de.xirp.profile.Minimum minimum} value 
	 * of the sensor.
	 * 
	 * @see de.xirp.profile.Minimum
	 */
	@XmlElement(name = "minimum", required = true)//$NON-NLS-1$
	private Minimum minimum = null;
	/**
	 * The {@link de.xirp.profile.Maximum maximum} value 
	 * of the sensor.
	 * 
	 * @see de.xirp.profile.Maximum
	 */
	@XmlElement(name = "maximum", required = true)//$NON-NLS-1$
	private Maximum maximum = null;
	/**
	 * {@link de.xirp.profile.Option Optional} sensor 
	 * specification values.
	 */
	@XmlElement(name = "option", required = false)//$NON-NLS-1$
	private Vector<Option> options = new Vector<Option>( );

	/**
	 * Returns the {@link de.xirp.profile.Maximum maximum}
	 * value of the {@link de.xirp.profile.Sensor sensor}.
	 * 
	 * @return The maximum value.
	 * 
	 * @see de.xirp.profile.Maximum
	 * @see de.xirp.profile.Sensor
	 */
	@XmlTransient
	public Maximum getMaximum() {
		return maximum;
	}

	/**
	 * Sets the  {@link de.xirp.profile.Maximum maximum}
	 * value of the {@link de.xirp.profile.Sensor sensor}.
	 * 
	 * @param maximum 
	 * 			The maximum to set.
	 * 
	 * @see de.xirp.profile.Maximum
	 * @see de.xirp.profile.Sensor
	 */
	public void setMaximum(Maximum maximum) {
		this.maximum = maximum;
	}

	/**
	 * Returns the {@link de.xirp.profile.Minimum minimum}
	 * value of the {@link de.xirp.profile.Sensor sensor}.
	 * 
	 * @return The minimum value.
	 * 
	 * @see de.xirp.profile.Minimum
	 * @see de.xirp.profile.Sensor
	 */
	@XmlTransient
	public Minimum getMinimum() {
		return minimum;
	}

	/**
	 * Sets the  {@link de.xirp.profile.Minimum minimum}
	 * value of the {@link de.xirp.profile.Sensor sensor}.
	 * 
	 * @param minimum 
	 * 			The minimum to set.
	 * 
	 * @see de.xirp.profile.Minimum
	 * @see de.xirp.profile.Sensor
	 */
	public void setMinimum(Minimum minimum) {
		this.minimum = minimum;
	}

	/**
	 * Returns the {@link de.xirp.profile.Option optional} 
	 * values.
	 * 
	 * @return The options.
	 * 
	 * @see de.xirp.profile.Option
	 */
	@XmlTransient
	public List<Option> getOptions() {
		return Collections.unmodifiableList(options);
	}

	/**
	 * Adds an {@link de.xirp.profile.Option option} 
	 * to the sensor.
	 * 
	 * @param option
	 *            The option to to add.
	 *            
	 * @see de.xirp.profile.Option
	 */
	public void addOption(Option option) {
		this.options.add(option);
	}

	/**
	 * Returns the {@link de.xirp.profile.Position position}
	 * of the sensor.
	 * 
	 * @see de.xirp.profile.Position
	 * 
	 * @return The position.
	 */
	@XmlTransient
	public Position getPosition() {
		return position;
	}

	/**
	 * Sets the  {@link de.xirp.profile.Position position}
	 * of the sensor.
	 * 
	 * @see de.xirp.profile.Position
	 * 
	 * @param position
	 *            The position to set.
	 */
	public void setPosition(Position position) {
		this.position = position;
	}

}
