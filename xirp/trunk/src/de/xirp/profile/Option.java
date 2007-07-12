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
 * Option.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 16.02.2006:		Created by Matthias Gernand.
 * 15.02.2007:		Added annotations for parsing with JAXB.
 */
package de.xirp.profile;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * This class represents an option, which can be used in several other
 * profile objects:
 * <br><br>
 * <ul>
 * 	<li>{@link de.xirp.profile.Plugin}</li>
 *  <li>{@link de.xirp.profile.CommunicationDatum}</li>
 *  <li>{@link de.xirp.profile.CommunicationInterface}</li>
 *  <li>{@link de.xirp.profile.SensorSpecs}</li>
 * </ul>
 * <br><br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files 
 * to the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * 
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Plugin
 * @see de.xirp.profile.CommunicationDatum
 * @see de.xirp.profile.CommunicationInterface
 * @see de.xirp.profile.SensorSpecs
 */
@XmlType
public final class Option implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 4011507051109036175L;
	/**
	 * The name of the option.
	 */
	@XmlAttribute(name = "name", required = true)//$NON-NLS-1$
	private String name = ""; //$NON-NLS-1$
	/**
	 * The value of the option.
	 */
	@XmlValue
	private String value = ""; //$NON-NLS-1$

	/**
	 * Returns the name of the option.
	 * 
	 * @return The name.
	 */
	@XmlTransient
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the option.
	 * 
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the value of the option.
	 * 
	 * @return The value.
	 */
	@XmlTransient
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of the option.
	 * 
	 * @param value
	 *            The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name + " = " + value; //$NON-NLS-1$
	}

}
