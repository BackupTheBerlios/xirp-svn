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
 * GamepadControl.java
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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javolution.util.FastTable;

import org.apache.log4j.Logger;

import com.centralnexus.input.Joystick;
import com.centralnexus.input.JoystickListener;

import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This class adds some useful information to the standard information
 * which is available through the joystick package.<br>
 * All thrown events specify directly which buttons on the gamepad are
 * pressed or which axis changed.
 * 
 * @author Rabea Gransberger
 */
public class GamepadControl implements JoystickListener {

	/**
	 * The Logger for this class
	 */
	protected static final Logger logClass = Logger.getLogger(GamepadControl.class);

	/**
	 * Constants for the gamepad buttons
	 */
	private FastTable<Integer> buttons = new FastTable<Integer>(33);
	/**
	 * Listeners on this gamepad
	 */
	private FastTable<GamepadEventListener> listeners = new FastTable<GamepadEventListener>(5);

	/**
	 * Flag. Is their a connection to a gamepad
	 */
	private boolean connected = true;

	/**
	 * Axis types which currently have a value
	 */
	private Set<AxisType> valuedTypes = new TreeSet<AxisType>( );
	/**
	 * Math context used for rounding.
	 */
	private static final MathContext mathContext = new MathContext(3);

	/*****************************************************************
	 * Type of the axis of the gamepad.
	 * 
	 * @author Rabea Gransberger
	 */
	public enum AxisType {

		/**
		 * Point of View
		 */
		POV,
		/**
		 * X Axis
		 */
		X,
		/**
		 * Y Axis
		 */
		Y,
		/**
		 * Z Axis
		 */
		Z,
		/**
		 * R Axis
		 */
		R,
		/**
		 * U Axis
		 */
		U;

		/**
		 * The value of middle position for the POV-Axis which is not
		 * 0.0 because this is up.
		 */
		public static final double POV_ZERO = -0.01;
	}

	/**
	 * Constructs a new instance for getting events of a gamepad,
	 * tries to connect to the gamepad and initializes all listeners.<br/>
	 * Use the {@link GamepadManager} for receiving events.
	 * 
	 * @param id
	 *            Identification of the gamepad (try 0)
	 */
	GamepadControl(int id) {
		initButtons( );
		try {
			Joystick joy = Joystick.createInstance(id);
			joy.addJoystickListener(this);
			connected = true;
		}
		catch (UnsatisfiedLinkError e) {
			logClass.error(I18n.getString("GamepadControl.log.errorGettingInstanceOfGamepad") + Constants.LINE_SEPARATOR, //$NON-NLS-1$
					e);
			connected = false;
		}
		catch (IOException e) {
			logClass.error(I18n.getString("GamepadControl.log.errorGettingInstanceOfGamepad") + Constants.LINE_SEPARATOR, //$NON-NLS-1$
					e);
			connected = false;
		}

	}

	/**
	 * Checks if there's a connection to a gamepad.
	 * 
	 * @return <code>true</code> when connected to a gamepad
	 */
	protected boolean isConnected() {
		return connected;
	}

	/**
	 * Writes the buttons types of the joystick framework to a list
	 * for easier access
	 */
	private void initButtons() {
		buttons.add(Joystick.BUTTON1);
		buttons.add(Joystick.BUTTON2);
		buttons.add(Joystick.BUTTON3);
		buttons.add(Joystick.BUTTON4);
		buttons.add(Joystick.BUTTON5);
		buttons.add(Joystick.BUTTON6);
		buttons.add(Joystick.BUTTON7);
		buttons.add(Joystick.BUTTON8);
		buttons.add(Joystick.BUTTON9);
		buttons.add(Joystick.BUTTON10);
		buttons.add(Joystick.BUTTON11);
		buttons.add(Joystick.BUTTON12);
		buttons.add(Joystick.BUTTON13);
		buttons.add(Joystick.BUTTON14);
		buttons.add(Joystick.BUTTON15);
		buttons.add(Joystick.BUTTON16);
		buttons.add(Joystick.BUTTON17);
		buttons.add(Joystick.BUTTON18);
		buttons.add(Joystick.BUTTON19);
		buttons.add(Joystick.BUTTON20);
		buttons.add(Joystick.BUTTON21);
		buttons.add(Joystick.BUTTON22);
		buttons.add(Joystick.BUTTON23);
		buttons.add(Joystick.BUTTON24);
		buttons.add(Joystick.BUTTON25);
		buttons.add(Joystick.BUTTON26);
		buttons.add(Joystick.BUTTON27);
		buttons.add(Joystick.BUTTON28);
		buttons.add(Joystick.BUTTON29);
		buttons.add(Joystick.BUTTON30);
		buttons.add(Joystick.BUTTON31);
		buttons.add(Joystick.BUTTON32);
	}

	/**
	 * Gets the value from the joystick for the given axis type.
	 * 
	 * @param j
	 *            the joystick to get the value from.
	 * @param type
	 *            the type of the axis to get a value for.
	 * @return the value for the axis.
	 */
	private float getValue(Joystick j, AxisType type) {
		float f = 0;
		switch (type) {
			case POV:
				f = j.getPOV( );
				break;
			case R:
				f = j.getR( );
				break;
			case U:
				f = j.getU( );
				break;
			case X:
				f = j.getX( );
				break;
			case Y:
				f = j.getY( );
				break;
			case Z:
				f = j.getZ( );
				break;
		}

		BigDecimal dec = new BigDecimal(f, mathContext);
		float round = dec.floatValue( );

		return round;
	}

	/**
	 * Receives an event if an axis change on the joystick/gamepad
	 * occurs.<br>
	 * Analyzes the event and fires it to all listeners.
	 * 
	 * @see com.centralnexus.input.JoystickListener#joystickAxisChanged(com.centralnexus.input.Joystick)
	 */
	public void joystickAxisChanged(Joystick j) {
		Map<AxisType, Float> values = new HashMap<AxisType, Float>(AxisType.values( ).length);

		for (AxisType type : AxisType.values( )) {
			// TODO what to do with U??
			if (type != AxisType.U) {
				float value = getValue(j, type);

				boolean changed = isChanged(value, type);
				// System.out.println(changed + ": " + type + " " +
				// value);
				if (changed) {
					values.put(type, value);
				}
			}
		}
		GamepadEvent event = new GamepadEvent(j);
		event.setValues(values);
		fireAxisChanged(event);
	}

	/**
	 * Checks if the given axis value is the value of the base
	 * position of the axis. For every axis but {@link AxisType#POV}
	 * this is near zero. For POV this is at the value given by
	 * {@link AxisType#POV_ZERO}.
	 * 
	 * @param axisType
	 *            the axis to check
	 * @param f
	 *            the value of the axis
	 * @return <code>true</code> if the axis is at its base position
	 */
	public static boolean isBasePosition(AxisType axisType, float f) {
		float abs = Math.abs(f);
		if (axisType == AxisType.POV) {
			if (f < 0) {
				return true;
			}
		}
		else if (abs <= 0.01) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a value of a gamepad may have changed because the
	 * basic value is not equal to <code>null</code>.
	 * 
	 * @param f
	 *            The Value to check for a change
	 * @param axisType
	 * @return <code>true</code> if the Value has changed
	 */
	private boolean isChanged(float f, AxisType axisType) {
		if (isBasePosition(axisType, f)) {
			if (valuedTypes.contains(axisType)) {
				valuedTypes.remove(axisType);
				return true;
			}
		}
		else {
			valuedTypes.add(axisType);
			return true;
		}

		return false;
	}

	/**
	 * Receives an event if a button on the joystick/gamepad is
	 * pressed.<br>
	 * Analyzes the event and forwards it to all listeners.
	 * 
	 * @param j
	 *            the joystick for which the event occurred
	 * @see com.centralnexus.input.JoystickListener#joystickAxisChanged(com.centralnexus.input.Joystick)
	 */
	public void joystickButtonChanged(final Joystick j) {
		List<Integer> pressed = new ArrayList<Integer>(buttons.size( ));
		// Alle the Buttons which are pressed
		int rawButtons = j.getButtons( );
		// Determine the different Buttons which are pressed
		for (int i = 0; i < buttons.size( ); i++) {
			int ret = rawButtons & buttons.get(i);
			if (ret != 0) {
				pressed.add(i + 1);
			}
		}
		GamepadEvent event = new GamepadEvent(j);
		event.setPressed(pressed);
		fireButtonPressed(event);
	}

	/**
	 * Forwards a button pressed event to all registered listeners.
	 * 
	 * @param e
	 *            The event which is passed to the listeners
	 */
	private void fireButtonPressed(GamepadEvent e) {
		for (GamepadEventListener listener : listeners) {
			listener.buttonPressed(e);
		}
	}

	/**
	 * Forwards an axis changed event to all registered listeners.
	 * 
	 * @param e
	 *            The event which is passed to the Listeners
	 */
	private void fireAxisChanged(GamepadEvent e) {
		for (GamepadEventListener listener : listeners) {
			listener.axisChanged(e);
		}
	}

	/**
	 * Adds a listener to listen for axis changed and button pressed
	 * events on the underlying joystick/gamepad.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	protected void addGamepadEventListener(GamepadEventListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes the joystick/gamepad Listener
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	protected void removeGamepadEventListener(GamepadEventListener listener) {
		listeners.remove(listener);
	}
}
