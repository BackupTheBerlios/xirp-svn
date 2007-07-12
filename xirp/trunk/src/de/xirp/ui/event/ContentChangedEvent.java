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
 * ContentChangedEvent.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 01.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.event;

import java.util.EventObject;

import de.xirp.profile.Profile;
import de.xirp.profile.Robot;

/**
 * A content change event which holds the currently active profile and
 * robot of the application.
 * 
 * @author Matthias Gernand
 */
public class ContentChangedEvent extends EventObject {

	/**
	 * The serial version unique identifier
	 */
	private static final long serialVersionUID = 1366934693085527422L;
	/**
	 * The new current robot
	 */
	private Robot robot;
	/**
	 * The new current profile
	 */
	private Profile profile;

	/**
	 * A content change event.
	 * 
	 * @param source
	 *            The source
	 * @param profile
	 *            the new current profile
	 * @param robot
	 *            The new current robot
	 */
	public ContentChangedEvent(Object source, Profile profile, Robot robot) {
		super(source);
		this.robot = robot;
		this.profile = profile;
	}

	/**
	 * Gets the newly activated robot.
	 * 
	 * @return the current robot
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * Gets the newly activated profile.
	 * 
	 * @return the current profile
	 */
	public Profile getProfile() {
		return profile;
	}
}
