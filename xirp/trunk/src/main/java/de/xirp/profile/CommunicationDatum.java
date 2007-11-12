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
 * CommunicationDatum.java
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

/**
 * Mapping between the data from the robot
 * and the data for the datapool.
 * <br><br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files 
 * to the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * 
 * @see javax.xml.bind.annotation
 * @see de.xirp.io.comm.data.Datapool
 */
@XmlType
public final class CommunicationDatum implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 5191427771358469948L;
	/**
	 * A {@link java.util.Vector} of 
	 * {@link de.xirp.profile.Option options}.
	 */
	@XmlElement(name = "option")//$NON-NLS-1$
	private Vector<Option> options = new Vector<Option>( );
	/**
	 * The receive format string.
	 */
	@XmlElement(name = "receiveformat")//$NON-NLS-1$
	private String receiveFormat = ""; //$NON-NLS-1$
	/**
	 * The {@link de.xirp.io.comm.data.Datapool datapool} 
	 * key.
	 */
	@XmlElement(name = "datapoolkey")//$NON-NLS-1$
	private String datapoolKey = ""; //$NON-NLS-1$

	/**
	 * Returns the {@link de.xirp.io.comm.data.Datapool datapool}
	 * key.
	 * 
	 * @return The datapool key.
	 * 
	 * @see de.xirp.io.comm.data.Datapool
	 */
	@XmlTransient
	public String getDatapoolKey() {
		return datapoolKey;
	}

	/**
	 * Sets the {@link de.xirp.io.comm.data.Datapool datapool}
	 * key.
	 * 
	 * @param datapoolKey
	 *            The datapool key to set.
	 *            
	 * @see de.xirp.io.comm.data.Datapool
	 */
	public void setDatapoolKey(String datapoolKey) {
		this.datapoolKey = datapoolKey;
	}

	/**
	 * Returns the {@link de.xirp.profile.Option options}.
	 * 
	 * @return The options.
	 */
	@XmlTransient
	public List<Option> getOptions() {
		return Collections.unmodifiableList(options);
	}

	/**
	 * Adds an {@link de.xirp.profile.Option} to the list
	 * of options.
	 * 
	 * @param option
	 * 			The option to add.
	 */
	public void addOption(Option option) {
		this.options.add(option);
	}

	/**
	 * Returns the receive {@link de.xirp.io.format.Format format}.
	 * 
	 * @return The receive format.
	 * 
	 * @see de.xirp.io.format.Format
	 */
	@XmlTransient
	public String getReceiveFormat() {
		return receiveFormat;
	}

	/**
	 * Sets the receive {@link de.xirp.io.format.Format format}.
	 * 
	 * @param receiveFormat
	 *            The receive format to set.
	 */
	public void setReceiveFormat(String receiveFormat) {
		this.receiveFormat = receiveFormat;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder( );
		builder.append("{Key = ").append(this.datapoolKey).append(", format = ") //$NON-NLS-1$ //$NON-NLS-2$
				.append(this.receiveFormat)
				.append(", options: ").append( //$NON-NLS-1$
				options)
				.append("}"); //$NON-NLS-1$
		return builder.toString( );
	}

}
