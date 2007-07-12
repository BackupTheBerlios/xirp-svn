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
 * RobotSpecs.java
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the specification of the 
 * {@link de.xirp.profile.Robot robot}.
 * <br><br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files 
 * to the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * 
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Robot
 */
@XmlType
public final class RobotSpecs implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = -2116266862265725576L;
	/**
	 * The {@link de.xirp.profile.Height height} of the robot.
	 * 
	 * @see de.xirp.profile.Height
	 */
	@XmlElement(name = "height", required = true)//$NON-NLS-1$
	private Height height = null;
	/**
	 * The {@link de.xirp.profile.Width width} of the robot.
	 * 
	 * @see de.xirp.profile.Width
	 */
	@XmlElement(name = "width", required = true)//$NON-NLS-1$
	private Width width = null;
	/**
	 * The {@link de.xirp.profile.Length length} of the robot.
	 * 
	 * @see de.xirp.profile.Length
	 */
	@XmlElement(name = "length", required = true)//$NON-NLS-1$
	private Length length = null;
	/**
	 * The {@link de.xirp.profile.Weight weight} of the robot.
	 * 
	 * @see de.xirp.profile.Weight
	 */
	@XmlElement(name = "weight", required = true)//$NON-NLS-1$
	private Weight weight = null;

	/**
	 * Returns the {@link de.xirp.profile.Height height}
	 * of the robot.
	 * 
	 * @return The height.
	 * 
	 * @see de.xirp.profile.Height
	 */
	@XmlTransient
	public Height getHeight() {
		return height;
	}

	/**
	 * Sets the {@link de.xirp.profile.Height height}
	 * of the robot.
	 * 
	 * @param height 
	 * 			The height to set.
	 * 
	 * @see de.xirp.profile.Height
	 */
	public void setHeight(Height height) {
		this.height = height;
	}

	/**
	 * Returns the {@link de.xirp.profile.Length length}
	 * of the robot.
	 * 
	 * @return The length.
	 * 
	 * @see de.xirp.profile.Length length
	 */
	@XmlTransient
	public Length getLength() {
		return length;
	}

	/**
	 * Sets the {@link de.xirp.profile.Length length}
	 * of the robot.
	 * 
	 * @param length 
	 * 			The length to set.
	 * 
	 * @see de.xirp.profile.Length
	 */
	public void setLength(Length length) {
		this.length = length;
	}

	/**
	 * Returns the {@link de.xirp.profile.Weight weight}
	 * of the robot.
	 * 
	 * @return The weight.
	 * 
	 * @see de.xirp.profile.Weight
	 */
	@XmlTransient
	public Weight getWeight() {
		return weight;
	}

	/**
	 * Sets the {@link de.xirp.profile.Weight weight} 
	 * of the robot.
	 * 
	 * @param weight 
	 * 			The weight to set.
	 * 
	 * @see de.xirp.profile.Weight
	 */
	public void setWeight(Weight weight) {
		this.weight = weight;
	}

	/**
	 * Returns the {@link de.xirp.profile.Width width}
	 * of the robot.
	 * 
	 * @return The width.
	 * 
	 * @see de.xirp.profile.Width
	 */
	@XmlTransient
	public Width getWidth() {
		return width;
	}

	/**
	 * Sets the {@link de.xirp.profile.Width width}
	 * of the robot.
	 * 
	 * @param width 
	 * 			The width to set.
	 * 
	 * @see de.xirp.profile.Width
	 */
	public void setWidth(Width width) {
		this.width = width;
	}

}
