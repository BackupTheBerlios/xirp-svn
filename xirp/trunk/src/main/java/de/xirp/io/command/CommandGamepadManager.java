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
 * CommandKeyManager.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de];
 * Contributor(s):   
 *
 * Changes
 * -------
 * 23.05.2007:		Created by Rabea Gransberger.
 */
package de.xirp.io.command;

/**
 * Manager for bindings of gamepad actions to commands
 * 
 * @author Rabea Gransberger
 */
public final class CommandGamepadManager extends
		AbstractCommandPersistManager<String> {

	/**
	 * The prefix for the save key
	 */
	private static final String SAVE_KEY = "Gamepad"; //$NON-NLS-1$
	/**
	 * Instance used for delegating the calls to the super
	 * implementation.
	 */
	private static CommandGamepadManager delegate;

	/**
	 * Creates a new CommandGamepadManager. The manager is initialized
	 * on startup. Never call this on your own. Use the statically
	 * provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	public CommandGamepadManager() throws InstantiationException {
		super(SAVE_KEY);
		delegate = this;
	}

	/**
	 * Just returns the given string.
	 * 
	 * @see de.xirp.io.command.AbstractCommandPersistManager#recoverObject(java.lang.String)
	 */
	@Override
	protected String recoverObject(String strg) {
		return strg;
	}

	/**
	 * Just returns the given string.
	 * 
	 * @see de.xirp.io.command.AbstractCommandPersistManager#saveObject(java.lang.Object)
	 */
	@Override
	protected String saveObject(String k) {
		return k;
	}

	/**
	 * Binds the given gamepad action string to the given command.
	 * 
	 * @param command
	 *            the command
	 * @param gamepad
	 *            the gamepad action string
	 */
	public static void addCommandGamepadMapping(CommandDefinition command,
			String gamepad) {
		delegate.addCommandMappingInternal(command, gamepad);
	}

	/**
	 * Gets the gamepad action string for the given command
	 * 
	 * @param command
	 *            the command to get the gamepad action for
	 * @return the gamepad action string for the command or
	 *         <code>null</code> if no action was found for the
	 *         given command
	 */
	public static String getGamepadConfig(CommandDefinition command) {
		return delegate.getObjectInternal(command);
	}

	/**
	 * Gets the command for the given gamepad action string.
	 * 
	 * @param gamepad
	 *            the gamepad action string
	 * @return the command for the gamepad action or <code>null</code>
	 *         if no command was found.
	 */
	public static CommandDefinition getGamepadCommand(String gamepad) {
		return delegate.getCommandInternal(gamepad);
	}

	/**
	 * Saves the bindings.
	 */
	public static void save() {
		delegate.store( );
	}
}
