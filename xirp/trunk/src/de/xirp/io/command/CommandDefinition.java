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
 * CommandDefinition.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 10.02.2007:		Created by Rabea Gransberger.
 */
package de.xirp.io.command;

import java.util.Collections;
import java.util.List;

import de.xirp.io.command.Command.GeneratorType;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * Definition for a command.
 * 
 * @author Rabea Gransberger
 */
public class CommandDefinition {

	/**
	 * The key used for translating the commands name
	 */
	private String key;
	/**
	 * The names for the parameters of the command
	 */
	private List<String> parameterNames;
	/**
	 * The handler used for translation
	 */
	private II18nHandler handler;
	/**
	 * the commandable which will receive the command
	 */
	private ICommandable commandable;

	/**
	 * The class name which is used instead of the commandable if this
	 * is a dummy definition
	 */
	private String className = null;
	/**
	 * Flag if this is a dummy
	 */
	private boolean dummy = false;
	/**
	 * List of allowed generators for executing this command.
	 */
	private List<GeneratorType> allowedTypes;

	/**
	 * Constructs a new CommandDefinition for the given commandable
	 * and a name-key which is translated with the standard handler.
	 * 
	 * @param commandable
	 *            the commandable which will receive the command
	 * @param key
	 *            the key for translation
	 * @param parameterNames
	 *            the names of the parameters for this command
	 * @param allowedTypes
	 *            List of allowed generators for executing this
	 *            command.
	 */
	public CommandDefinition(ICommandable commandable, String key,
			List<String> parameterNames, List<GeneratorType> allowedTypes) {
		this(commandable,
				key,
				parameterNames,
				allowedTypes,
				I18n.getGenericI18n( ));
	}

	/**
	 * Constructs a new CommandDefintion for the given commandable and
	 * a name-key which is translated with the given handler.
	 * 
	 * @param commandable
	 *            the commandable which will receive the command
	 * @param key
	 *            the key for translation
	 * @param parameterNames
	 *            the names of the parameters for this command
	 * @param allowedTypes
	 * @param handler
	 *            the handler used for translation
	 */
	public CommandDefinition(ICommandable commandable, String key,
			List<String> parameterNames, List<GeneratorType> allowedTypes,
			II18nHandler handler) {
		this.commandable = commandable;
		this.key = key;
		this.parameterNames = parameterNames;
		this.handler = handler;
		this.allowedTypes = allowedTypes;
	}

	/**
	 * Constructs a command definition dummy with just the class name
	 * of the commandable and the key for translation.
	 * 
	 * @param mainClass
	 *            the main class of the commandable
	 * @param commandKey
	 *            the name-key for translation
	 */
	private CommandDefinition(String mainClass, String commandKey) {
		dummy = true;
		this.key = commandKey;
		this.className = mainClass;

	}

	/**
	 * Constructs a CommandDefinition with just the main class of the
	 * commandable and the key for translation.
	 * 
	 * @param mainClass
	 *            the main class of the commandable
	 * @param commandKey
	 *            the name-key for translation
	 * @return the definition
	 */
	public static CommandDefinition getSimpleCommandDef(String mainClass,
			String commandKey) {
		CommandDefinition def = new CommandDefinition(mainClass, commandKey);
		return def;
	}

	/**
	 * @return the allowedTypes
	 */
	public List<GeneratorType> getAllowedGeneratorTypes() {
		return allowedTypes;
	}

	/**
	 * The handler used for translation.
	 * 
	 * @return the handler
	 */
	public II18nHandler getHandler() {
		return handler;
	}

	/**
	 * The key used for translating the commands name.
	 * 
	 * @return the name
	 */
	public String getKey() {
		return key;
	}

	/**
	 * the commandable which will receive the command.
	 * 
	 * @return the commandable
	 */
	public ICommandable getCommandable() {
		return commandable;
	}

	/**
	 * The names for the parameters of the command.
	 * 
	 * @return the parameterNames
	 */
	public List<String> getParameterNames() {
		return Collections.unmodifiableList(parameterNames);
	}

	/**
	 * Gets the string which identifies the command. This string is
	 * equal for a dummy and a real.
	 * 
	 * @return the identifier string consisting of class name and key
	 */
	public String getIdentifierString() {
		if (!dummy) {
			return this.commandable.getClass( ).getName( ) +
					"_" + this.getKey( ); //$NON-NLS-1$
		}
		else {
			return this.className + "_" + this.getKey( ); //$NON-NLS-1$
		}
	}

	/**
	 * Gets the identifier string for debugging purposes.
	 */
	@Override
	public String toString() {
		return getIdentifierString( );
	}
}
