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
 * Multimedia.java
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the multimedia (audio and video) devices of
 * the robot. <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 */
@XmlType
public final class Multimedia implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = -2318040474907951962L;
	/**
	 * The video devices. Input and output devices.
	 * 
	 * @see de.xirp.profile.Video
	 */
	@XmlElement(name = "video", required = false)
	private Video video = null;
	/**
	 * The audio devices. Input and output.
	 * 
	 * @see de.xirp.profile.Audio
	 */
	@XmlElement(name = "audio", required = false)
	private Audio audio = null;

	/**
	 * Returns the audio devices.
	 * 
	 * @return The audio.
	 * @see de.xirp.profile.Audio
	 */
	@XmlTransient
	public Audio getAudio() {
		return audio;
	}

	/**
	 * Sets the audio devices.
	 * 
	 * @param audio
	 *            The audio to set.
	 * @see de.xirp.profile.Audio
	 */
	public void setAudio(Audio audio) {
		this.audio = audio;
	}

	/**
	 * Returns the video devices.
	 * 
	 * @return The video.
	 * @see de.xirp.profile.Video
	 */
	@XmlTransient
	public Video getVideo() {
		return video;
	}

	/**
	 * Sets the video devices.
	 * 
	 * @param video
	 *            The video to set.
	 * @see de.xirp.profile.Video
	 */
	public void setVideo(Video video) {
		this.video = video;
	}

}
