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
 * Video.java
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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the video input/output devices of the robot.
 * <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 */
@XmlType
public final class Video implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 8661443361283931409L;
	/**
	 * The video input devices of the robot.
	 */
	@XmlElement(name = "camera", required = false)
	private Vector<Camera> cameras = new Vector<Camera>( );
	/**
	 * The video output devices of the robot.
	 */
	@XmlElement(name = "display", required = false)
	private Vector<Display> displays = new Vector<Display>( );
	/**
	 * <code>true</code> if cameras could be shown simultaneously in
	 * the UI.
	 */
	@XmlAttribute(name = "simultaneous", required = true)
	private boolean simultaneous = false;

	/**
	 * Returns the {@link de.xirp.profile.Camera video}
	 * input devices of the robot.
	 * 
	 * @return An unmodifiable list with the video input devices.
	 * @see de.xirp.profile.Camera
	 */
	@XmlTransient
	public List<Camera> getCameras() {
		return Collections.unmodifiableList(cameras);
	}

	/**
	 * Adds a {@link de.xirp.profile.Camera video} input
	 * device to the video input devices of this robot.
	 * 
	 * @param device
	 *            the device to add.
	 * @see de.xirp.profile.Camera
	 */
	public void addCamera(Camera device) {
		this.cameras.add(device);
	}

	/**
	 * Returns whether the camera can be shown simultaneously or not.
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>, if the cameras can be shown
	 *         simultaneously<br>
	 *         <code>false</code>, if the cameras can't be shown
	 *         simultaneously.
	 */
	@XmlTransient
	public boolean isSimultaneous() {
		return simultaneous;
	}

	/**
	 * Sets the simultaneous flag of the video object.
	 * 
	 * @param simultaneous
	 *            The simultaneous flag.<br>
	 *            <code>true</code>, if the cameras can be shown
	 *            simultaneously<br>
	 *            <code>false</code>, if the cameras can't be shown
	 *            simultaneously.
	 */
	public void setSimultaneous(boolean simultaneous) {
		this.simultaneous = simultaneous;
	}

	/**
	 * Adds a {@link de.xirp.profile.Display video}
	 * output device to the video output devices of this robot.
	 * 
	 * @param device
	 *            The device to add.
	 * @see de.xirp.profile.Display
	 */
	public void addDisplay(Display device) {
		this.displays.add(device);
	}

	/**
	 * Returns the {@link de.xirp.profile.Display video}
	 * output devices of the robot.
	 * 
	 * @return An unmodifiable list with the video output devices.
	 * @see de.xirp.profile.Display
	 */
	@XmlTransient
	public List<Display> getDisplays() {
		return Collections.unmodifiableList(displays);
	}

}
