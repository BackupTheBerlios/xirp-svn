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
 * CommunicationSpecification.java
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

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Represents the specification of the communication between robot and
 * the application. <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 * @see de.xirp.io.comm
 */
@XmlRootElement(name = "specification")
public final class CommunicationSpecification implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = -5452195235533017886L;
	/**
	 * <code>true</code> if the communication specifiation is
	 * complete
	 */
	@XmlAttribute(name = "complete", required = true)
	private boolean complete = false;
	/**
	 * A {@link java.util.Vector} containing the available
	 * {@link de.xirp.profile.CommunicationProtocol protocols}.
	 */
	@XmlElement(name = "communicationprotocol")
	private Vector<CommunicationProtocol> communications = new Vector<CommunicationProtocol>( );
	/**
	 * A {@link java.util.Vector} containing the interfaces which
	 * could be used for communication, f.e. TCI/IP.
	 */
	@XmlElement(name = "communicationinterface")
	private Vector<CommunicationInterface> interfaces = new Vector<CommunicationInterface>( );
	/**
	 * The CMS {@link java.io.File}.
	 */
	@XmlTransient
	private File cmsFile;

	/**
	 * Returns the list of communication
	 * {@link de.xirp.profile.CommunicationProtocol protocols}.
	 * 
	 * @return An unmodifiable list with the protocols.
	 * @see de.xirp.profile.CommunicationProtocol
	 */
	@XmlTransient
	public List<CommunicationProtocol> getCommunicationProtocols() {
		return Collections.unmodifiableList(communications);
	}

	/**
	 * Adds a communication
	 * {@link de.xirp.profile.CommunicationProtocol protocol}
	 * to the list of available protocols.
	 * 
	 * @param communication
	 *            The communication to add.
	 * @see de.xirp.profile.CommunicationProtocol
	 */
	public void addCommunicationProtocol(CommunicationProtocol communication) {
		this.communications.add(communication);
	}

	/**
	 * Returns the list of communication
	 * {@link de.xirp.profile.CommunicationInterface interfaces}.
	 * 
	 * @return The unmodifiable list of interfaces.
	 * @see de.xirp.profile.CommunicationInterface
	 */
	@XmlTransient
	public List<CommunicationInterface> getInterfaces() {
		return Collections.unmodifiableList(interfaces);
	}

	/**
	 * Adds a communication
	 * {@link de.xirp.profile.CommunicationInterface interface}
	 * to the list of interfaces.
	 * 
	 * @param inferface
	 *            The interface to add.
	 * @see de.xirp.profile.CommunicationInterface
	 */
	public void addInterface(CommunicationInterface inferface) {
		this.interfaces.add(inferface);
	}

	/**
	 * Returns whether or not he communication specification is
	 * complete.
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>: Comm-spec is complete.<br>
	 *         <code>false</code>: Comm-spec is not complete.<br>
	 */
	@XmlTransient
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Sets the completed state of the communication specification.
	 * 
	 * @param complete
	 *            <br>
	 *            <code>true</code>: Comm-spec is complete.<br>
	 *            <code>false</code>: Comm-spec is not complete.<br>
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	/**
	 * Returns the CMS {@link java.io.File}.
	 * 
	 * @return The cms file.
	 */
	@XmlTransient
	public File getCmsFile() {
		return cmsFile;
	}

	/**
	 * Sets the CMS {@link java.io.File}.
	 * 
	 * @param cmsFile
	 *            The cms file to set.
	 */
	public void setCmsFile(File cmsFile) {
		this.cmsFile = cmsFile;
	}
}
