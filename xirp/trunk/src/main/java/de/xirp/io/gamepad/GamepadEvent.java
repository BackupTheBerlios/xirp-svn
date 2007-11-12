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
 * GamepadEvent.java
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.centralnexus.input.Joystick;

import de.xirp.io.gamepad.GamepadControl.AxisType;

/**
 * Event for button press or axis change of joysticks/gamepads.
 * 
 * @author Rabea Gransberger
 */
public class GamepadEvent {

	/**
	 * Currently pressed buttons
	 */
	private List<Integer> pressed = new ArrayList<Integer>( );
	/**
	 * The joystick to get additional information from if needed
	 */
	private Joystick joystick;
	/**
	 * Values of the currently used Axis
	 */
	private Map<AxisType, Float> values = Collections.emptyMap( );

	/**
	 * Constructs a new gamepad event with the underlying joystick
	 * information class.
	 * 
	 * @param joystick
	 *            object with original information
	 */
	protected GamepadEvent(Joystick joystick) {
		this.joystick = joystick;
	}

	/**
	 * Sets the actual values for the activated axis.
	 * 
	 * @param values
	 *            the actual values for the activated axis
	 */
	protected void setValues(Map<AxisType, Float> values) {
		this.values = values;
	}

	/**
	 * Gets a map with the values for the axis.
	 * 
	 * @return the actual values for the activated axis
	 */
	public Map<AxisType, Float> getValues() {
		return Collections.unmodifiableMap(values);
	}

	/**
	 * Gets the value for an activated axis.
	 * 
	 * @param type
	 *            Type of the axis
	 * @return The Values of the axis [-1;1] or [0;270] for type POV
	 *         or <code>null</code> if the Axis is not active
	 */
	public Float getValue(AxisType type) {
		return values.get(type);
	}

	/**
	 * Gets the types of the currently activated axis.
	 * 
	 * @return types of the currently activated axis
	 */
	public Set<AxisType> getAxisTypes() {
		return Collections.unmodifiableSet(values.keySet( ));
	}

	/**
	 * The joystick form which the event was thrown.
	 * 
	 * @return original data of this event
	 */
// public Joystick getJoystick() {
// return joystick;
// }
	/**
	 * Sets the numbers of the pressed buttons.
	 * 
	 * @param pressed
	 *            the numbers of the pressed buttons
	 */
	protected void setPressed(List<Integer> pressed) {
		this.pressed = pressed;
	}

	/**
	 * Gets the numbers of the pressed buttons.
	 * 
	 * @return the numbers of the pressed buttons
	 */
	public List<Integer> getPressed() {
		return Collections.unmodifiableList(pressed);
	}

}
