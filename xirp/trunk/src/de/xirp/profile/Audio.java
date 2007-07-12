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
 * Audio.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 16.03.2006:		Created by Matthias Gernand.
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
 * The class represents the input/output audio devices of the robot.
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
public final class Audio implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = -5128781791291863801L;
	/**
	 * A {@link java.util.Vector} of 
	 * {@link de.xirp.profile.Microphone}.
	 */
	@XmlElement(name = "microphone", required = false)//$NON-NLS-1$
	private Vector<Microphone> microphones = new Vector<Microphone>( );
	/**
	 * A {@link java.util.Vector} of 
	 * {@link de.xirp.profile.Speaker}.
	 */
	@XmlElement(name = "speaker", required = false)//$NON-NLS-1$
	private Vector<Speaker> speakers = new Vector<Speaker>( );

	/**
	 * Returns an unmodifiable list with the
	 * {@link de.xirp.profile.Microphone audio}
	 * input devices.
	 * 
	 * @return The audio input devices.
	 * 
	 * @see de.xirp.profile.Microphone
	 */
	@XmlTransient
	public List<Microphone> getMicrophones() {
		return Collections.unmodifiableList(microphones);
	}

	/**
	 * Adds a {@link de.xirp.profile.Microphone audio} 
	 * input device to the audio input devices of this robot.
	 * 
	 * @param device
	 *            The device to add.
	 * 
	 * @see de.xirp.profile.Microphone
	 */
	public void addMicrophone(Microphone device) {
		this.microphones.add(device);
	}

	/**
	 * Adds a {@link de.xirp.profile.Speaker audio}
	 * output device to the audio output devices of this robot.
	 * 
	 * @param device
	 *            The device to add.
	 *            
	 * @see de.xirp.profile.Speaker           
	 */
	public void addSpeaker(Speaker device) {
		this.speakers.add(device);

	}

	/**
	 * Returns an unmodifiable list with the 
	 * {@link de.xirp.profile.Speaker audio} output
	 * devices.
	 * 
	 * @return The audio output devices.
	 * 
	 * @see de.xirp.profile.Speaker
	 */
	@XmlTransient
	public List<Speaker> getSpeakers() {
		return Collections.unmodifiableList(speakers);
	}

}
