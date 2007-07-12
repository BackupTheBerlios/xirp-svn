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
 * GlobalPluginKeyManager.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.08.2006:		Created by Rabea Gransberger.
 */
package de.xirp.io.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.util.Constants;
import de.xirp.util.collections.BidiHashMap;

/**
 * Abstract manager for persisting bindings to commands of
 * commandables.
 * 
 * @param <K>
 *            Type of the object which describes the binding to a
 *            command
 * @see ICommandable
 * @author Rabea Gransberger
 */
public abstract class AbstractCommandPersistManager<K> extends AbstractManager {

	/**
	 * log4j Logger of this Class
	 */
	private static final Logger logClass = Logger.getLogger(AbstractCommandPersistManager.class);
	/**
	 * Key used as prefix when saving the commands
	 */
	private final String SAVE_KEY;

	/**
	 * Bidirectional mapping between the commands identifier and the
	 * object which was binded to the command
	 */
	private final BidiHashMap<String, K> commandMapping = new BidiHashMap<String, K>( );

	/**
	 * Mapping from the identifier of the command to the command
	 * definition
	 */
	private final Map<String, CommandDefinition> identifierToCommand = new HashMap<String, CommandDefinition>( );

	/**
	 * Constructs a new CommandPersistManager. The manager is
	 * initialized on startup. Never call this on your own. Use the
	 * statically provided methods.
	 * 
	 * @param saveKey
	 *            the key used as prefix when saving the commands. The
	 *            key is concatenated with the class name to be sure
	 *            the resulting key is unique.
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	public AbstractCommandPersistManager(String saveKey)
			throws InstantiationException {
		super( );
		this.SAVE_KEY = this.getClass( ).getSimpleName( ) + saveKey;
	}

	/**
	 * The bindings are saved to a properties file as strings. This
	 * method has to be implemented to get an re-parseable string out
	 * of the binding object.
	 * 
	 * @param object
	 *            the object which has to be converted to a string
	 * @return the string representing the object
	 * @see #recoverObject(String)
	 */
	protected abstract String saveObject(K object);

	/**
	 * Recovers the original object saved to the properties file from
	 * the given string
	 * 
	 * @param strg
	 *            the string which represents the object
	 * @return the object parsed from the string or <code>null</code>
	 *         if it could not be parsed
	 * @see #saveObject(Object)
	 */
	protected abstract K recoverObject(String strg);

	/**
	 * Binds the given object to the given command.<br>
	 * Mappings to <code>null</code> are not save, so if you want to
	 * clear a mapping, use <code>null</code> as value for the key
	 * sequence.<br>
	 * 
	 * @param command
	 *            the plugin command
	 * @param object
	 *            the object to bind to the command
	 */
	protected void addCommandMappingInternal(CommandDefinition command, K object) {
		commandMapping.put(command.getIdentifierString( ), object);
		identifierToCommand.put(command.getIdentifierString( ), command);
	}

	/**
	 * Gets the bind object for the given command
	 * 
	 * @param command
	 *            the plugin command to get the object for
	 * @return the object or <code>null</code> if none found for the
	 *         given plugin command
	 */
	protected K getObjectInternal(CommandDefinition command) {
		K object = commandMapping.get(command.getIdentifierString( ));
		return object;
	}

	/**
	 * Gets the plugin command for the given object
	 * 
	 * @param object
	 *            the object to get the command for
	 * @return the plugin command for the given object or
	 *         <code>null</code> if no mapping exists
	 */
	protected CommandDefinition getCommandInternal(K object) {
		String key = commandMapping.getKey(object);
		return identifierToCommand.get(key);
	}

	/**
	 * Clears all mappings and (re-)loads the mappings from the
	 * according preferences file into the local structure.
	 */
	private void reload() {
		commandMapping.clear( );
		identifierToCommand.clear( );

		Properties props = new Properties( );
		File f = new File(Constants.COMMAND_BINDINGS_FILE);
		if (f.exists( )) {
			try {
				props.load(new FileInputStream(f));
				for (Entry<Object, Object> entry : props.entrySet( )) {
					String pluginCommand = (String) entry.getKey( );
					int underscoreIndex = pluginCommand.lastIndexOf("_"); //$NON-NLS-1$
					String mainClass = pluginCommand.substring(0,
							underscoreIndex);
					String commandKey = pluginCommand.substring(underscoreIndex + 1);

					if (pluginCommand.startsWith(SAVE_KEY)) {

						mainClass = mainClass.replaceFirst(SAVE_KEY, ""); //$NON-NLS-1$
						CommandDefinition def = CommandDefinition.getSimpleCommandDef(mainClass,
								commandKey);
						String value = (String) entry.getValue( );
						K recoverObject = recoverObject(value);
						if (recoverObject != null) {
							addCommandMappingInternal(def, recoverObject);
						}
					}
				}
			}
			catch (FileNotFoundException e) {
				logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
						+ Constants.LINE_SEPARATOR, e);
			}
			catch (IOException e) {
				logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
						+ Constants.LINE_SEPARATOR, e);
			}
		}
	}

	/**
	 * Saves the mappings to the according preferences file.<br>
	 * Mappings to <code>null</code> are not saved, so if you want
	 * to clear a mapping, use <code>null</code> as value.<br>
	 * Even if the saving process fails, the mappings are kept while
	 * the application is running.
	 */
	protected void store() {
		Properties props = new Properties( );
		try {
			FileInputStream fileInputStream = new FileInputStream(Constants.COMMAND_BINDINGS_FILE);
			props.load(fileInputStream);
			fileInputStream.close( );
		}
		catch (FileNotFoundException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}

		List<Object> toRemove = new ArrayList<Object>( );
		for (Object entry : props.keySet( )) {
			if (entry.toString( ).startsWith(SAVE_KEY)) {
				toRemove.add(entry);
			}
		}
		for (Object obj : toRemove) {
			props.remove(obj);
		}

		for (Entry<String, K> entry : commandMapping.entrySet( )) {
			if (entry.getValue( ) != null) {
				props.put(SAVE_KEY + entry.getKey( ),
						saveObject(entry.getValue( )));
			}
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(Constants.COMMAND_BINDINGS_FILE);
			props.store(fileOutputStream, "Mapping from commands to keys"); //$NON-NLS-1$
			fileOutputStream.close( );
		}
		catch (FileNotFoundException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
	}

	/**
	 * Starts up this manager and loads the bindings for the commands.
	 * 
	 * @throws ManagerException
	 *             not actually thrown by this implementation
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
		reload( );
	}

	/**
	 * Stops this manager. The mappings are not saved automatically
	 * when the manager stops. You have to call {@link #store()} from
	 * within your application if needed.
	 * 
	 * @throws ManagerException
	 *             not actually thrown by this implementation
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );

	}
}
