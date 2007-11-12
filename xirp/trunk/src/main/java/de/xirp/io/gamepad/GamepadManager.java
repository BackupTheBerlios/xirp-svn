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
 * GamepadManager.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 15.03.2007:		Created by Rabea Gransberger.
 */
package de.xirp.io.gamepad;

import javolution.util.FastTable;
import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;

/**
 * Manager for using the gamepad. Objects interested in receiving
 * events from the gamepad may register at this manager for receiving
 * events.<br/> The gamepad can only be used if it's plugged in on
 * application startup.
 * 
 * @author Rabea Gransberger
 */
public final class GamepadManager extends AbstractManager {

	/**
	 * The gamepad control object which is the base for this manager
	 */
	private static GamepadControl control;
	/**
	 * Listeners on this gamepad
	 */
	private static FastTable<GamepadEventListener> listeners = new FastTable<GamepadEventListener>(5);

	/**
	 * Constructs a new GamepadManager. The manager is initialized on
	 * startup. Never call this on your own. Use the statically
	 * provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	public GamepadManager() throws InstantiationException {
		super( );
	}

	/**
	 * Tries to connect to the gamepad.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );

		control = new GamepadControl(0);
		if (control.isConnected( )) {
			for (GamepadEventListener listener : listeners) {
				control.addGamepadEventListener(listener);
			}
			listeners.clear( );
		}
	}

	/**
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
	}

	/**
	 * Adds a listener to a connected gamepad.
	 * 
	 * @param listener
	 *            the listener to add
	 * @see de.xirp.io.gamepad.GamepadControl#addGamepadEventListener(de.xirp.io.gamepad.GamepadEventListener)
	 */
	public static void addGamepadEventListener(GamepadEventListener listener) {
		if (control != null) {
			control.addGamepadEventListener(listener);
		}
		else {
			listeners.add(listener);
		}
	}

	/**
	 * Checks if a gamepad is connected and if we are connected to the
	 * gamepad.
	 * 
	 * @return <code>true</code> if the gamepad is connected
	 * @see de.xirp.io.gamepad.GamepadControl#isConnected()
	 */
	public static boolean isConnected() {
		return control.isConnected( );
	}

	/**
	 * Removes the listener from a the gamepad.
	 * 
	 * @param listener
	 *            the listener to remove
	 * @see de.xirp.io.gamepad.GamepadControl#removeGamepadEventListener(de.xirp.io.gamepad.GamepadEventListener)
	 */
	public static void removeGamepadEventListener(GamepadEventListener listener) {
		if (control != null) {
			control.removeGamepadEventListener(listener);
		}
		else {
			listeners.remove(listener);
		}
	}

}
