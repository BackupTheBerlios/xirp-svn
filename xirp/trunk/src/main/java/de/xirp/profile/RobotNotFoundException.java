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
 * RobotNotFoundException.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 11.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.profile;

import de.xirp.util.I18n;

/**
 * An exception indicating, that a robot was not found.
 * This exception delivers a translated message.
 * 
 * @author Matthias Gernand
 *
 */
public class RobotNotFoundException extends Exception {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	private static final long serialVersionUID = -8068239132273497566L;

	/**
	 * Constructor for a robot name and a {@link java.lang.Throwable} as cause.
	 * 
	 * @param robotName
	 * 			The not found robot name.
	 * @param cause
	 * 			The cause.
	 */
	public RobotNotFoundException(String robotName, Throwable cause) {
		super(I18n.getString("RobotNotFoundException.exception.robotName", robotName), cause); //$NON-NLS-1$
	}

	/**
	 * Constructor for a robot name.
	 * 
	 * @param robotName
	 * 			The not found robot name.
	 */
	public RobotNotFoundException(String robotName) {
		super(I18n.getString("RobotNotFoundException.exception.robotName", robotName)); //$NON-NLS-1$
	}

}
