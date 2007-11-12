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
 * RobotPluginContainer.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 03.06.2006:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import de.xirp.io.comm.handler.IHandler;
import de.xirp.io.comm.lowlevel.ICommunicationInterface;
import de.xirp.io.comm.protocol.IProtocol;
import de.xirp.io.logging.RobotLogger;
import de.xirp.profile.Camera;
import de.xirp.profile.CommunicationInterface;
import de.xirp.profile.CommunicationProtocol;
import de.xirp.profile.Plugin;
import de.xirp.profile.Robot;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.collections.MultiValueHashMap;

/**
 * A container for plugins which are referenced from a profile of a
 * given robot.
 * 
 * @author Rabea Gransberger
 */
final class RobotPluginContainer {

	/**
	 * Log4j Logger for this Class
	 */
	private static RobotLogger logClass = RobotLogger.getLogger(RobotPluginContainer.class);

	/**
	 * Hashmap containing all plugins for the different types
	 */
	@SuppressWarnings("unchecked")
	private MultiValueHashMap<Integer, SecurePluginView> typedPlugins = new MultiValueHashMap<Integer, SecurePluginView>( );

	/**
	 * List of plugins of type communication
	 */
	@SuppressWarnings("unchecked")
	private ConcurrentHashMap<NameID, SecurePluginView> plugins = new ConcurrentHashMap<NameID, SecurePluginView>( );

	/**
	 * The robot to which the plugins belong
	 */
	private Robot robot;

	/**
	 * The main classes and identifiers of the plugins of the robot
	 */
	private Vector<NameID> referencedPlugins = new Vector<NameID>( );

	/**
	 * Initializes the plugin container. The container will search for
	 * referenced plugins for the robot immediately
	 * 
	 * @param robot
	 *            the robot to which the plugins belong
	 */
	protected RobotPluginContainer(Robot robot) {
		this.robot = robot;
		init( );
	}

	/**
	 * Reads the profile for the robot, and saves the plugins names
	 */
	private void init() {
		for (CommunicationProtocol comm : robot.getCommunicationSpecification( )
				.getCommunicationProtocols( )) {
			referencedPlugins.add(new NameID(comm.getMessageHandler( )));
			referencedPlugins.add(new NameID(comm.getClassName( )));
		}
		for (CommunicationInterface comm : robot.getCommunicationSpecification( )
				.getInterfaces( )) {
			referencedPlugins.add(new NameID(comm.getClassName( )));
		}

		// FIXME simultaneous
		// String className = robot.getMultimedia( ).getClaas( );
		// // For simultaneous, more than one plugin is needed
		// if (robot.getMultimedia( ).getVideo( ).isSimultanious( )) {
		// for (Camera cam : robot.getMultimedia( ).getVideo(
		// ).getCameras( )) {
		// referencedPlugins.add(new NameID(className, cam.getName(
		// )));
		// }
		// }
		// else {
		// referencedPlugins.add(new NameID(className));
		// }

		for (Plugin plugin : robot.getPlugins( ).getPlugins( )) {
			String identifier = plugin.getUniqueIdentifier( );
			if (plugin.isMultimedia( )) {
				String className = plugin.getClassName( );
				if (robot.getMultimedia( ).getVideo( ).isSimultaneous( )) {
					for (Camera cam : robot.getMultimedia( )
							.getVideo( )
							.getCameras( )) {
						referencedPlugins.add(new NameID(className,
								cam.getName( )));
					}
				}
				else {
					referencedPlugins.add(new NameID(className));
				}
			}
			else {
				referencedPlugins.add(new NameID(plugin.getClassName( ), identifier));
			}
		}
	}

	/**
	 * Gets the plugin for the given main class.<br>
	 * This method is unsafe, because it may return an already running
	 * plugin, which is not allowed to be modified. Be careful using
	 * this method.
	 * 
	 * @param mainClass
	 *            the main class of the plugin to get
	 * @return the plugin, if one found (else <code>null</code>)
	 */
	@SuppressWarnings("unchecked")
	protected SecurePluginView getPlugin(String mainClass) {
		return plugins.get(new NameID(mainClass));
	}

	/**
	 * Runs the plugin with the given main class and identifier.<br>
	 * If no plugin for the given identifier is found, a plugin for
	 * the given main class (and any identifier) is returned
	 * 
	 * @param id
	 *            object holding the main class and identifier
	 * @return the plugin or <code>null</code> if no plugin found
	 *         for main class and/or identifier
	 */
	@SuppressWarnings("unchecked")
	private SecurePluginView run(NameID id) {
		SecurePluginView infor = plugins.get(id);
		if (infor != null) {
			IPlugable plugin = infor.getOriginalPlugin( );
			if (!plugin.isRunning( )) {
				plugin.run( );
			}
		}
		else {
			for (SecurePluginView info : plugins.values( )) {
				IPlugable plug = info.getOriginalPlugin( );
				if (plug.getClass( )
						.getName( )
						.equalsIgnoreCase(id.getMainClass( ))) {
					if (!plug.isRunning( )) {
						plug.run( );
					}
					return info;
				}
			}
		}
		return infor;
	}

	/**
	 * Runs the plugin for the given main class and identifier.<br>
	 * If no plugin for the given identifier is found, a plugin for
	 * the given main class is returned.
	 * 
	 * @param mainClass
	 *            the main class for the plugin
	 * @param name
	 *            the identifier for the plugin
	 *            {@link IPlugable#getIdentifier()}
	 * @return the plugin or <code>null</code> if no plugin found
	 *         for main class and/or identifier
	 */
	@SuppressWarnings("unchecked")
	protected SecurePluginView run(String mainClass, String name) {
		return run(new NameID(mainClass, name));
	}

	/**
	 * Runs the plugin for the given main class
	 * 
	 * @param mainClass
	 *            the main class of the plugin
	 * @return the plugin or <code>null</code> if no plugin found
	 *         for the given main class
	 */
	@SuppressWarnings("unchecked")
	protected SecurePluginView run(String mainClass) {
		return run(new NameID(mainClass));
	}

	/**
	 * Stops all plugins of this container
	 */
	@SuppressWarnings("unchecked")
	protected void stopAll() {
		for (SecurePluginView info : plugins.values( )) {
			IPlugable plugin = info.getOriginalPlugin( );
			plugin.stop( );
		}
	}

	/**
	 * Stops the plugin with the given main class and identifier.<br>
	 * If no plugin was found, the plugin with the given main class is
	 * stopped.
	 * 
	 * @param id
	 *            main class and identifier of the plugin
	 * @return <code>true</code> if the plugin was stopped,
	 *         <code>false</code> if the plugin wasn't stopped or
	 *         there is no plugin for the given name
	 */
	@SuppressWarnings("unchecked")
	private boolean stop(NameID id) {
		IPlugable plugin = plugins.get(id);
		if (plugin != null) {
			plugin = ((SecurePluginView) plugin).getOriginalPlugin( );
			if (plugin.isRunning( )) {
				return plugin.stop( );
			}
			else {
				return true;
			}
		}
		else {
			for (SecurePluginView info : plugins.values( )) {
				IPlugable plug = info.getOriginalPlugin( );
				if (plug.getClass( )
						.getName( )
						.equalsIgnoreCase(id.getMainClass( ))) {
					if (plug.isRunning( )) {
						return plug.stop( );
					}
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Stops the plugin with the given main class and identifier.<br>
	 * If no plugin was found, the plugin with the given main class is
	 * stopped.
	 * 
	 * @param mainClass
	 *            main class of the plugin
	 * @param name
	 *            the identifier of the plugin
	 *            {@link IPlugable#getIdentifier()}
	 * @return <code>true</code> if the plugin was stopped,
	 *         <code>false</code> if the plugin wasn't stopped or
	 *         there is no plugin for the given name
	 */
	protected boolean stop(String mainClass, String name) {
		return stop(new NameID(mainClass, name));
	}

	/**
	 * Stops the plugin with the given main class.
	 * 
	 * @param mainClass
	 *            the main class of the plugin to stop
	 * @return <code>true</code> if the plugin was stopped,
	 *         <code>false</code> if the plugin wasn't stopped or
	 *         there is no plugin for the given name
	 */
	protected boolean stop(String mainClass) {
		return stop(new NameID(mainClass));
	}

	/**
	 * Loads the plugins and updates the progress bar while loading
	 * 
	 * @param manager
	 *            the manager used for notifying of progress made
	 * @param perRobot
	 *            percentage for all plugins of this container
	 */
	@SuppressWarnings("unchecked")
	protected void loadPlugins(PluginManager manager, final double perRobot) {

		double perRobotToUse = perRobot;
		if (perRobotToUse == 0) {
			perRobotToUse = 1;
		}
		double perPlugin = perRobotToUse / referencedPlugins.size( );

		double cnt = 0;
		for (NameID id : referencedPlugins) {
			if (PluginLoader.exists(id.getMainClass( ))) {
				manager.throwRobotProgressEvent(I18n.getString("RobotPluginContainer.progress.loadingForRobot", robot.getName( ), id.getKey( )), cnt); //$NON-NLS-1$
				cnt += perPlugin;

				try {
					SecurePluginView info = PluginManager.getInstance(PluginLoader.getInfo(id.getMainClass( )),
							robot.getName( ));

					IPlugable plugin = info.getOriginalPlugin( );
					plugin.setIdentifier(id.getId( ));

					if (checkType(info)) {
						plugin.preLoad( );
						plugins.put(id, info);
					}
				}
				catch (Exception e) {
					logClass.error(robot.getName( ),
							"Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
				}

			}
			else {
				logClass.info(robot.getName( ),
						I18n.getString("RobotPluginContainer.log.plugin.notfound", id.getMainClass( ), robot.getName( )) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			}
			try {
				// sleep to show the user it's going fast, but shows
				// him something
				Thread.sleep(25);
			}
			catch (InterruptedException e) {
				logClass.error(robot.getName( ),
						"Error " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Gets all the main classes of the referenced plugins of this
	 * container
	 * 
	 * @return main classes of the plugins referenced by the robot of
	 *         this container
	 */
	protected List<String> getReferencedPlugins() {
		Vector<String> ref = new Vector<String>(referencedPlugins.size( ));
		for (NameID id : referencedPlugins) {
			ref.add(id.getMainClass( ));
		}
		return Collections.unmodifiableList(ref);
	}

	/**
	 * Gets all the plugins of this container which have the
	 * {@link VisualizationType#WINDOW}
	 * 
	 * @return all plugins of this container with
	 *         {@link VisualizationType#WINDOW}
	 */
	@SuppressWarnings("unchecked")
	protected List<IPlugable> getReferencedWindowPlugins() {
		ArrayList<IPlugable> windowed = new ArrayList<IPlugable>( );
		for (IPlugable plugin : plugins.values( )) {
			if (VisualizationType.containsType(plugin, VisualizationType.WINDOW)) {
				windowed.add(plugin);
			}
		}
		return Collections.unmodifiableList(windowed);
	}

	/**
	 * Checks if the plugin implements the correct classes according
	 * to it's type
	 * 
	 * @param plugin
	 *            Plugin to check
	 * @return <code>true</code> If plugin has the correct type
	 */
	@SuppressWarnings("unchecked")
	private boolean checkType(SecurePluginView plugin) {
		if (plugin == null) {
			return false;
		}
		boolean ret = true;
		int type = plugin.getPluginType( );
		if (PluginType.containsType(type, PluginType.COMMUNICATION)) {
			if (!(plugin.getOriginalPlugin( ) instanceof ICommunicationInterface)) {
				ret = false;
			}
		}
		if (PluginType.containsType(type, PluginType.PROTOCOL)) {
			if (!(plugin.getOriginalPlugin( ) instanceof IProtocol)) {
				ret = false;
			}
		}
		if (PluginType.containsType(type, PluginType.MESSAGE_HANDLER)) {
			if (!(plugin.getOriginalPlugin( ) instanceof IHandler)) {
				ret = false;
			}
		}

		if (!ret) {
			logClass.debug(robot.getName( ),
					I18n.getString("RobotPluginContainer.log.pluginDoesNotMeetTheSpecifications", //$NON-NLS-1$
							plugin.getPluginType( )));
		}
		else {
			putToType(plugin);
		}

		return ret;
	}

	/**
	 * Puts the plugin to the list which holds the plugins for each
	 * type
	 * 
	 * @param plugin
	 *            the plugin to sort into the plugins type list
	 */
	@SuppressWarnings("unchecked")
	private void putToType(IPlugable plugin) {
		for (Integer type : PluginType.getTypeVector( )) {
			if (PluginType.containsType(plugin, type)) {
				if (plugin instanceof SecurePluginView) {
					typedPlugins.put(type, (SecurePluginView) plugin);
				}
				else {
					try {
						typedPlugins.put(type, new SecurePluginView(plugin));
					}
					catch (InstantiationException e) {
						logClass.error(robot.getName( ), "Error: " //$NON-NLS-1$
								+
								e.getMessage( ) + Constants.LINE_SEPARATOR, e);
					}
				}
			}
		}
	}

	/**
	 * Gets all plugins of this container
	 * 
	 * @return the plugins of this container as unmodifiable list
	 */
	@SuppressWarnings("unchecked")
	protected List<IPlugable> getPlugins() {
		return Collections.unmodifiableList(new ArrayList<IPlugable>(plugins.values( )));
	}

	/**
	 * Gets plugins of this robot using a filter.
	 * 
	 * @param filter
	 *            the filter for filtering which plugins should be
	 *            returned
	 * @return an unmodifiable list of plugins
	 */
	@SuppressWarnings("unchecked")
	public List<IPlugable> getPlugins(IPluginFilter filter) {
		ArrayList<IPlugable> filtered = new ArrayList<IPlugable>( );
		for (IPlugable plugin : plugins.values( )) {
			if (filter.filterPlugin(plugin)) {
				filtered.add(plugin);
			}
		}
		return Collections.unmodifiableList(filtered);
	}

	/**
	 * Gets the plugins of this container as map, with a combination
	 * of mainClass and identifier as key
	 * 
	 * @return plugins as map with key as mainClass[_identifier]
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, IPlugable> getPluginsAsMap() {
		HashMap<String, IPlugable> ret = new HashMap<String, IPlugable>( );
		for (Entry<NameID, SecurePluginView> entry : plugins.entrySet( )) {
			ret.put(entry.getKey( ).getMainClass( ), entry.getValue( ));
		}
		return Collections.unmodifiableMap(ret);
	}

	/**
	 * Gets the plugins for a given plugin type
	 * 
	 * @param type
	 *            a plugin type of {@link PluginType}
	 * @return all plugins for the given type of this container
	 */
	@SuppressWarnings("unchecked")
	protected List<IPlugable> getPluginsForType(int type) {
		return Collections.unmodifiableList(new ArrayList<IPlugable>(typedPlugins.get(type)));
	}

	/**
	 * Combination of mainClass and identifier for plugins
	 */
	class NameID {

		/**
		 * the fully qualified name of the main class of the plugin
		 */
		private String mainClass;
		/**
		 * the instance identifier of the plugin or <code>null</code>
		 */
		private String id;

		/**
		 * Constructs a new identifier object for plugin instances
		 * 
		 * @param mainClass
		 *            the fully qualified name of the main class of
		 *            the plugin
		 * @param id
		 *            the instance identifier of the plugin or
		 *            <code>null</code>
		 */
		public NameID(String mainClass, String id) {
			this.mainClass = mainClass;
			this.id = id;
		}

		/**
		 * Constructs a new identifier object for plugin instances
		 * without an instance identifier
		 * 
		 * @param mainClass
		 *            the fully qualified name of the main class of
		 *            the plugin
		 */
		public NameID(String mainClass) {
			this.mainClass = mainClass;
		}

		/**
		 * Gets the textual instance identifier of the plugin
		 * 
		 * @return the textual instance identifier of the plugin
		 */
		public String getId() {
			return id;
		}

		/**
		 * TSets the textual instance identifier of the plugin
		 * 
		 * @param id
		 *            the textual instance identifier of the plugin
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * Gets the main class of the plugin
		 * 
		 * @return the mainClass
		 */
		public String getMainClass() {
			return mainClass;
		}

		/**
		 * Sets the main class of the plugin
		 * 
		 * @param mainClass
		 *            the mainClass to set
		 */
		public void setMainClass(String mainClass) {
			this.mainClass = mainClass;
		}

		/**
		 * Gets the unique key indentifying the plugin constructed
		 * from the main class and identifier if available
		 * 
		 * @return unique key indentifying the plugin
		 */
		public String getKey() {
			if (id == null) {
				if (mainClass != null) {
					return mainClass;
				}
				else {
					return ""; //$NON-NLS-1$
				}
			}
			else {
				return mainClass + "_" + id; //$NON-NLS-1$
			}
		}

		/**
		 * @see #getKey()
		 */
		@Override
		public String toString() {
			return getKey( );
		}

		/**
		 * Equals for the {@link #toString()}
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			return this.toString( ).equalsIgnoreCase(obj.toString( ));
		}

		/**
		 * HashCode for the {@link #toString()}
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return this.toString( ).hashCode( );
		}

	}

}
