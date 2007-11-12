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

import org.apache.log4j.Logger;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.ParseException;

import de.xirp.util.Constants;
import de.xirp.util.XirpKeyFormatter;

/**
 * A manager for binding key sequences to commands.
 * 
 * @author Rabea Gransberger
 */
public final class CommandKeyManager extends
		AbstractCommandPersistManager<KeySequence> {

	/**
	 * log4j Logger of this Class
	 */
	private static final Logger logClass = Logger.getLogger(CommandKeyManager.class);

	/**
	 * The prefix for the save key
	 */
	private static final String SAVE_KEY = "KeySequence"; //$NON-NLS-1$
	/**
	 * Instance used for delegating the calls to the super
	 * implementation.
	 */
	private static CommandKeyManager delegate;
	/**
	 * Formatter used for formatting the key sequences
	 */
	private static final XirpKeyFormatter formatter = new XirpKeyFormatter( );

	/**
	 * Creates a new CommandKeyManager. The manager is initialized on
	 * startup. Never call this on your own. Use the statically
	 * provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	public CommandKeyManager() throws InstantiationException {
		super(SAVE_KEY);
		delegate = this;
	}

	/**
	 * Calls {@link KeySequence#getInstance(String)} for the given
	 * string.
	 * 
	 * @return the key sequence or <code>null</code> if no sequence
	 *         could be parsed from the given string.
	 * @see de.xirp.io.command.AbstractCommandPersistManager#recoverObject(java.lang.String)
	 */
	@Override
	protected KeySequence recoverObject(String strg) {
		try {
			return KeySequence.getInstance(strg);
		}
		catch (ParseException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
		return null;
	}

	/**
	 * Calls {@link KeySequence#toString()} to get a re-parsable
	 * string of the given key sequence.
	 * 
	 * @see de.xirp.io.command.AbstractCommandPersistManager#saveObject(java.lang.Object)
	 */
	@Override
	protected String saveObject(KeySequence k) {
		return k.toString( );
	}

	/**
	 * Gets the human readable key name for the given command
	 * 
	 * @param command
	 *            the command
	 * @return human readable key name for the given command
	 */
	public static String getKeyName(CommandDefinition command) {
		KeySequence seq = getKeySequence(command);
		if (seq == null) {
			return null;
		}
		else {
			return format(seq);
		}
	}

	/**
	 * Gets the {@link org.eclipse.jface.bindings.keys.KeySequence}
	 * for the given command
	 * 
	 * @param command
	 *            the plugin command to get the key sequence for
	 * @return the key sequence or <code>null</code> if none found
	 *         for the given plugin command
	 */
	public static KeySequence getKeySequence(CommandDefinition command) {
		return delegate.getObjectInternal(command);
	}

	/**
	 * Formats the given key sequence into a human readable localized
	 * format.<br>
	 * <b>Note:</b> This method does not depend on this manager, and
	 * may be used at any time, even before {@link #start()} was
	 * called.
	 * 
	 * @param seq
	 *            the key sequence to format
	 * @return key sequence in human readable localized format, or
	 *         <code>null</code> if the sequence could not be
	 *         formatted
	 */
	public static String format(KeySequence seq) {
		return formatter.format(seq);
	}

	/**
	 * Adds a mapping between a plugin command and a key.<br>
	 * When the key is pressed the command will be sent to the plugin
	 * (if existing for the given robot).<br>
	 * Mappings to <code>null</code> are not save, so if you want to
	 * clear a mapping, use <code>null</code> as value for the key
	 * sequence.<br>
	 * 
	 * @param command
	 *            the plugin command
	 * @param seq
	 *            the key sequence (yet only a key stroke is
	 *            supported)
	 */
	public static void addCommandKeyMapping(CommandDefinition command,
			KeySequence seq) {
		delegate.addCommandMappingInternal(command, seq);
	}

	/**
	 * Saves the bindings to the property file used for command
	 * bindings.
	 */
	public static void save() {
		delegate.store( );
	}

	/**
	 * Gets the plugin command for the given key
	 * 
	 * @param seq
	 *            the key which was hit
	 * @return the plugin command for the given key
	 */
	public static CommandDefinition getCommand(KeySequence seq) {
		return delegate.getCommandInternal(seq);
	}

}
