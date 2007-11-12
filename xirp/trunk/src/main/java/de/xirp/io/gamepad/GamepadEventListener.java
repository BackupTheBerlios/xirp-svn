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
 * GamepadEventListener.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 19.04.2006:		Created by Rabea Gransberger.
 */
package de.xirp.io.gamepad;

import java.util.EventListener;

/**
 * Interface which may be implemented by classes which want to listen
 * for events of a connected gamepad/joystick.
 * 
 * @author Rabea Gransberger
 */
public interface GamepadEventListener extends EventListener {

	/**
	 * Event which occurs when a button on the gamepad is pressed.
	 * 
	 * @param e
	 *            data of the event
	 */
	public void buttonPressed(GamepadEvent e);

	/**
	 * Event which occurs when a axis on the gamepad is
	 * activated/used.
	 * 
	 * @param e
	 *            data of the event
	 */
	public void axisChanged(GamepadEvent e);
}
