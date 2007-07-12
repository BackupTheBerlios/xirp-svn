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
 * CommunicationProtocol.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 29.05.2006:		Created by Matthias Gernand.
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
 * Information about the communication between robot and application.
 * This class hold informations about the protocol spoken by the
 * robot. <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 * @see de.xirp.io.comm.data.Datapool
 * @see de.xirp.io.comm.protocol
 */
@XmlType
public final class CommunicationProtocol implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = -6722343870278064534L;
	/**
	 * The main class' name of the communication plugin.
	 */
	@XmlElement(name = "class")
	private String className = ""; //$NON-NLS-1$
	/**
	 * The message handler name (handles messages sent from
	 * application to robot).
	 */
	@XmlElement(name = "messagehandler")
	private String messageHandler = ""; //$NON-NLS-1$
	/**
	 * The communication data.
	 */
	@XmlElement(name = "datum")
	private Vector<CommunicationDatum> data = new Vector<CommunicationDatum>( );

	/**
	 * Returns the main class' name of the communication plugin.
	 * 
	 * @return The class name.
	 */
	@XmlTransient
	public String getClassName() {
		return StringUtils.trimToEmpty(className);
	}

	/**
	 * Sets the main class' name of the communication plugin.
	 * 
	 * @param className
	 *            The class name to set.
	 */
	public void setClassName(String className) {
		this.className = StringUtils.trimToEmpty(className);
	}

	/**
	 * Returns a list of mappings between the data from the robot and
	 * the data for the datapool.
	 * 
	 * @return An unmodifiable list with {@link CommunicationDatum}.
	 */
	@XmlTransient
	public List<CommunicationDatum> getDates() {
		return Collections.unmodifiableList(data);
	}

	/**
	 * Adds a mapping between the data from the robot and the data for
	 * the datapool.
	 * 
	 * @param data
	 *            The {@link CommunicationDatum} to add.
	 */
	public void addDatum(CommunicationDatum data) {
		this.data.add(data);
	}

	/**
	 * Returns the message handler main class name. The handler
	 * handles messages sent from application to robot.
	 * 
	 * @return The message handler class name.
	 */
	@XmlTransient
	public String getMessageHandler() {
		String trimToEmpty = StringUtils.trimToEmpty(messageHandler);
		return trimToEmpty;
	}

	/**
	 * Sets the message handler main class name. The handler handles
	 * messages sent from application to robot.
	 * 
	 * @param messageHandler
	 *            The message handler to set.
	 */
	public void setMessageHandler(String messageHandler) {
		this.messageHandler = messageHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder( );
		builder.append("{class = ").append(this.className).append(", handler = ") //$NON-NLS-1$ //$NON-NLS-2$
				.append(this.messageHandler)
				.append(", data: ").append( //$NON-NLS-1$
				this.data)
				.append("}"); //$NON-NLS-1$
		return builder.toString( );
	}
}
