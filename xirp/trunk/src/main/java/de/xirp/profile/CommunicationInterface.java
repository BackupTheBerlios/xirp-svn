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
 * CommunicationInterface.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 03.06.2006:		Created by Matthias Gernand.
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

import org.apache.commons.lang.StringUtils;

/**
 * Specification of an interface over which the application can
 * communicate with the robot. This class contains informations about
 * the communication medium plugins, f.e. TCI/IP or a serial
 * connection. <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 * @see de.xirp.io.comm.lowlevel
 */
@XmlType
public final class CommunicationInterface implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = -259779915861133373L;
	/**
	 * The main class of the communication interface plugin.
	 */
	@XmlElement(name = "class")
	private String clazz = ""; //$NON-NLS-1$
	/**
	 * {@link de.xirp.profile.Option Options} for the
	 * interface.
	 */
	@XmlElement(name = "option")
	private Vector<Option> options = new Vector<Option>( );

	/**
	 * Returns the main class name of the communication interface
	 * plugin.
	 * 
	 * @return Returns the class name.
	 */
	@XmlTransient
	public String getClassName() {
		return StringUtils.trimToEmpty(clazz);
	}

	/**
	 * Sets the main class name of the communication interface plugin.
	 * 
	 * @param clazz
	 *            The class name to set.
	 */
	public void setClassName(String clazz) {
		this.clazz = StringUtils.trimToEmpty(clazz);
	}

	/**
	 * Returns an list of
	 * {@link de.xirp.profile.Option options} for this
	 * communication interface.
	 * 
	 * @return An unmodifiable list containing the options.
	 */
	@XmlTransient
	public List<Option> getOptions() {
		return Collections.unmodifiableList(options);
	}

	/**
	 * Adds an {@link de.xirp.profile.Option} to this
	 * communication interface.
	 * 
	 * @param option
	 *            The option to add.
	 */
	public void addOption(Option option) {
		this.options.add(option);
	}
}
