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
 * CommandManager.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.profile.Robot;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.collections.MultiValueHashMap;

/**
 * Manager for commands which may be used to allow keyboard control
 * for a plugin
 * 
 * @author Rabea Gransberger
 */
public final class CommandManager extends AbstractManager {

	/**
	 * The Logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(CommandManager.class);

	/**
	 * May have the same keys because the plugins don't know each
	 * other: robotName -> {commandkey -> (CommandDefintion)*}
	 */
	private static final Map<String, MultiValueHashMap<String, CommandDefinition>> map = new HashMap<String, MultiValueHashMap<String, CommandDefinition>>( );
	/**
	 * A list of all command definitions used for easier access for
	 * the command preferences
	 */
	private static final List<CommandDefinition> commDefs = new ArrayList<CommandDefinition>( );

	/**
	 * Constructs a new CommandManager. The manager is initialized on
	 * startup. Never call this on your own. Use the statically
	 * provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	public CommandManager() throws InstantiationException {
		super( );
	}

	/**
	 * Registers a commandable and all its commands for the given
	 * robot
	 * 
	 * @param commandable
	 *            the commandable to register
	 * @param robotName
	 *            the robot for which the command should be registered
	 */
	public static void register(ICommandable commandable, String robotName) {
		// get or create a new multivaluehashmap for all the commands
		// of the robot
		MultiValueHashMap<String, CommandDefinition> commandMap = map.get(robotName);
		if (commandMap == null) {
			commandMap = new MultiValueHashMap<String, CommandDefinition>( );
		}
		// Get the definitions of this commandable
		List<CommandDefinition> defs = commandable.getCommandDefinitions( );
		// Add each definition to the map
		// And fill a global structure with all the definitions
		for (CommandDefinition definition : defs) {
			commandMap.put(definition.getKey( ), definition);
			commDefs.add(definition);
		}
		map.put(robotName, commandMap);
	}

	/**
	 * Registers a commandable and all its command for the given robot
	 * 
	 * @param commandable
	 *            the commandable to register
	 * @param robot
	 *            the robot for which the command should be registered
	 */
	public static void register(ICommandable commandable, Robot robot) {
		register(commandable, robot.getName( ));
	}

	/**
	 * De-registers a commandable and all its command for the given
	 * robot
	 * 
	 * @param commandable
	 *            the commandable to deregister
	 * @param robot
	 *            the robot for which the commands should be
	 *            de-registered
	 */
	public static void deregister(ICommandable commandable, Robot robot) {
		// get the multivaluehashmap for all the commands
		// of the robot
		MultiValueHashMap<String, CommandDefinition> commandMap = map.get(robot.getName( ));
		// if there's no map there are no commands for the given robot
		if (commandMap != null) {
			// Get the definitions of this commandable
			List<CommandDefinition> defs = commandable.getCommandDefinitions( );
			// Remove each definition from the map and definitionlist
			for (CommandDefinition definition : defs) {
				commandMap.remove(definition.getKey( ), definition);
				commDefs.remove(definition);
			}
		}

	}

	/**
	 * Execute the given command for the list of robots
	 * 
	 * @param command
	 *            the command to execute
	 * @param robots
	 *            the list of robots to execute the command for
	 */
	public static void command(Command command, List<Robot> robots) {
		// Iterate over the map of commands for each robot
		for (Robot robot : robots) {
			MultiValueHashMap<String, CommandDefinition> commandMap = map.get(robot.getName( ));
			if (commandMap != null) {
				// Get the definitions for a given key
				List<CommandDefinition> lst = commandMap.get(command.getCommandDefinition( )
						.getKey( ));
				// execute the command
				for (CommandDefinition def : lst) {
					if (def.getAllowedGeneratorTypes( )
							.contains(command.getType( ))) {
						def.getCommandable( ).executeCommand(command);
					}
					else {
						logClass.debug(I18n.getString("CommandManager.command.generator.notallowed", //$NON-NLS-1$
								def.getKey( ),
								command.getType( )) +
								Constants.LINE_SEPARATOR);
					}
				}
			}
		}
	}

	/**
	 * Get all command definitions independent of the robots.
	 * 
	 * @return the list of command definitions
	 */
	public static Collection<CommandDefinition> getRobotIndependentCommandDefinitions() {
		// map for commands dropping the robot name
		Map<String, CommandDefinition> commands = new HashMap<String, CommandDefinition>( );
		// iterate over the commands per robot
		for (Entry<String, MultiValueHashMap<String, CommandDefinition>> entry : map.entrySet( )) {
			MultiValueHashMap<String, CommandDefinition> internMap = entry.getValue( );
			// Add one definition for each key
			for (Entry<String, List<CommandDefinition>> internEntry : internMap.entrySet( )) {
				commands.put(internEntry.getKey( ), internEntry.getValue( )
						.get(0));
			}
		}
		return commands.values( );
	}

	/**
	 * Starts the command manager.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
	}

	/**
	 * Stops the command manager.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
	}
}
