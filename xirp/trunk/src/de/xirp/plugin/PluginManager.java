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
 * PluginManager.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.DeleteManager;
import de.xirp.managers.ManagerException;
import de.xirp.profile.ProfileManager;
import de.xirp.profile.Robot;
import de.xirp.ui.Application;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.widgets.dialogs.GenericDialog;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Util;

/**
 * This manager manages the plugins of the application and provides
 * methods for interaction with the plugins.<br>
 * It's possible to start and stop plugins, and to get plugins of a
 * specific type.
 * 
 * @author Rabea Gransberger
 */
public final class PluginManager extends AbstractManager {

	/**
	 * Log4j Logger for this Class
	 */
	private static Logger logClass = Logger.getLogger(PluginManager.class);

	/**
	 * Plugin directory for images inside of the jar
	 */
	private static final String IMAGE_PLUGIN_DIR = "images"; //$NON-NLS-1$
	/**
	 * Name of the directory with the help files inside the jar
	 */
	private static final String HELP_PLUGIN_DIR = "help"; //$NON-NLS-1$

	/**
	 * Name of the directory with the jar files inside the jar
	 */
	private static final String LIB_PLUGIN_DIR = "lib"; //$NON-NLS-1$

	/**
	 * Name of the directory with the windows only jar files inside
	 * the jar within {@link #LIB_PLUGIN_DIR}
	 */
	private static final String LIB_WINDOWS_PLUGIN_DIR = "windows"; //$NON-NLS-1$

	/**
	 * Name of the directory with the linux only jar files inside the
	 * jar within {@link #LIB_PLUGIN_DIR}
	 */
	private static final String LIB_LINUX_PLUGIN_DIR = "linux"; //$NON-NLS-1$

	/**
	 * Name of the directory with the windows dll lib files inside the
	 * jar within {@link #LIB_PLUGIN_DIR}
	 */
	private static final String LIB_DLL_PLUGIN_DIR = "dll"; //$NON-NLS-1$

	/**
	 * Name of the directory with the so lib files inside the jar
	 * within {@link #LIB_PLUGIN_DIR}
	 */
	private static final String LIB_SO_PLUGIN_DIR = "so"; //$NON-NLS-1$

	/**
	 * Directory separator inside the jar
	 */
	private static final String JAR_SEPARATOR = "/"; //$NON-NLS-1$

	/**
	 * HashMap containing the plugins for each robot, with the robots
	 * name as key
	 */
	private static final ConcurrentHashMap<String, RobotPluginContainer> robotPlugins = new ConcurrentHashMap<String, RobotPluginContainer>(Util.getOptimalMapSize(ProfileManager.getRobotCount( )));

	/**
	 * Flag which holds the running status of the PluginManager
	 */
	private static boolean started = false;

	/**
	 * List with all plugins which are not referenced by a robots
	 * profile. Maps from mainClass to container with all instances
	 */
	private static final ConcurrentHashMap<String, UnrefPluginContainer> unrefPlugins = new ConcurrentHashMap<String, UnrefPluginContainer>( );
	/**
	 * Properties for all Plugin Preferences
	 */
	private static PropertiesConfiguration props;

	/**
	 * Percent of time which is consumed by the loader
	 */
	private static final double LOADING_OFFSET = 0.35;
	/**
	 * Current base percentage when loading
	 */
	private double base = 0;

	/**
	 * This constructor should not be called by any other class but
	 * the {@link de.xirp.managers.ManagerFactory}. Use
	 * the statically provided methods.
	 * 
	 * @throws InstantiationException
	 *             if the constructor is called more than one time
	 */
	public PluginManager() throws InstantiationException {
		super( );
	}

	/**
	 * Starts the PluginManager. The Plugins for each robot are
	 * instantiated and loaded, and a list of unreferenced plugins is
	 * created. Multiple calls of this method have no effect.
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
		startUp( );
	}

	/**
	 * Stops the PluginManager.<br/><br/>All registered plugins are
	 * stopped for all robots, and all files in the delete list are
	 * deleted. All files which could not be deleted will be deleted
	 * on next startup.
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
		for (Robot robot : ProfileManager.getRobots( )) {
			stopPlugins(robot.getName( ));
		}
	}

	/**
	 * Starts the PluginManager and updates the splash screen. The
	 * Plugins for each robot are instantiated and loaded, and a list
	 * of unreferenced plugins is created. Multiple calls of this
	 * method have no effect.
	 */
	@SuppressWarnings("unchecked")
	private void startUp() {
		if (!started) {
			throwProgressEvent(I18n.getString("PluginManager.progress.searchingForPlugins")); //$NON-NLS-1$

			// look for plugins
			PluginLoader.searchPlugins(this);

			throwProgressEvent(I18n.getString("PluginManager.progress.loadingPlugins"), LOADING_OFFSET); //$NON-NLS-1$

			List<Robot> robots = ProfileManager.getRobots( );

			int robotCnt = robots.size( );
			double perRobot = 0;
			if (robotCnt != 0) {
				perRobot = (1 - LOADING_OFFSET) / robotCnt;
			}

			// Load the plugins for each robot
			for (Robot robot : robots) {
				RobotPluginContainer container = new RobotPluginContainer(robot);
				container.loadPlugins(this, perRobot);
				robotPlugins.put(robot.getName( ), container);
				base += perRobot;
			}
			// Remove all Referenced Plugins
			List<String> temp = new ArrayList<String>( );
			for (PluginInfo info : PluginLoader.getPlugins( )) {
				temp.add(info.getMainClass( ));
			}
			for (RobotPluginContainer container : robotPlugins.values( )) {
				for (IPlugable plugin : container.getPlugins( )) {
					PluginInfo info = plugin.getInfo( );
					temp.remove(info.getMainClass( ));
				}
			}

			// FIXME check this!!!
			// Create unref containers
// for (String mainClass : temp) {
			for (PluginInfo info : PluginLoader.getPlugins( )) {
				UnrefPluginContainer container = new UnrefPluginContainer(info);
				unrefPlugins.put(info.getMainClass( ), container);
			}
			started = true;
		}
	}

	/**
	 * Used when the {@link RobotPluginContainer} loads it's plugins
	 * to notify the application of the progress made.
	 * 
	 * @param message
	 *            the message to display
	 * @param percentage
	 *            the percentage of current progress of the
	 *            {@link RobotPluginContainer}
	 */
	protected void throwRobotProgressEvent(String message, double percentage) {
		super.throwProgressEvent(message, base + percentage + LOADING_OFFSET);
	}

	/**
	 * Used by the {@link PluginLoader} when it searches for plugins
	 * to notify the application of the progress made.
	 * 
	 * @param message
	 *            the message to display
	 * @param percentage
	 *            the percentage of progress
	 */
	protected void throwLoaderProgressEvent(String message, double percentage) {
		super.throwProgressEvent(message, percentage * LOADING_OFFSET);
	}

	/**
	 * Throws a progress event with just a message and default
	 * percentage.
	 * 
	 * @see de.xirp.managers.AbstractManager#throwProgressEvent(java.lang.String)
	 */
	@Override
	protected void throwProgressEvent(String message) {
		super.throwProgressEvent(message);
	}

	/**
	 * Extract libraries, images and jars of the given plugin.
	 * 
	 * @param info
	 *            the plugin information
	 */
	protected static void extractAll(PluginInfo info) {
		String path = getPluginLibPath(info);
		extractPluginLib(info, path);
		extractImages(info);
		extractDLLs(info);

		// add jars directory for deletion
		DeleteManager.deleteOnShutdown(path);
	}

	/**
	 * Gets new instances of all unreferenced plugins.
	 * 
	 * @param filter
	 *            filter which filters the plugins which are returned
	 * @return secure view with new instances of all unreferenced
	 *         plugins
	 * @see SecurePluginView
	 */
	@SuppressWarnings("unchecked")
	public static List<IPlugable> getUnreferencedPluginSamples(
			IPluginFilter filter) {
		ArrayList<IPlugable> filtered = new ArrayList<IPlugable>( );
		for (UnrefPluginContainer container : unrefPlugins.values( )) {
			IPlugable plugin = container.getSample( );
			if (plugin != null && filter.filterPlugin(plugin)) {
				filtered.add(plugin);
			}
		}
		return Collections.unmodifiableList(filtered);
	}

	/**
	 * Removes all white spaces from the given string which might have
	 * been read from the XML file.
	 * 
	 * @param mainClass
	 *            the string to remove the white spaces from
	 * @return the string without white spaces
	 */
	public static String strip(String mainClass) {
		return StringUtils.trimToEmpty(mainClass);
	}

	/**
	 * Checks if the given main class corresponds to an unreferenced
	 * plugin.
	 * 
	 * @param mainClass
	 *            the main class of the plugin to check
	 * @return <code>true</code> if this plugin is unreferenced
	 */
	public static boolean isUnreferencedPlugin(String mainClass) {
		return unrefPlugins.containsKey(strip(mainClass));
	}

	/**
	 * Gets the plugin container for a robot, containing all the
	 * plugins which are referenced by the robot.
	 * 
	 * @param robotName
	 *            name of the robot to get the container for
	 * @return container for controlling plugins of a robot
	 */
	protected static RobotPluginContainer getRobotPluginContainer(
			String robotName) {
		return robotPlugins.get(robotName);
	}

	/**
	 * Checks if the given class is a main class of a plugin
	 * 
	 * @param mainClass
	 *            the class to check
	 * @return <code>true</code> if the given class is a plugins
	 *         main class
	 */
	public static boolean isPlugin(String mainClass) {
		return PluginLoader.exists(strip(mainClass));
	}

	/**
	 * Gets the plugin of the given robot and the given main class.<br>
	 * Note: This method may return a plugin which is already running.
	 * Be careful when using this method. Prefer using
	 * {@link #runPlugin(String, String)} or
	 * {@link #runPlugin(String, String, String)}.
	 * 
	 * @param robotName
	 *            the name of the robot to get the plugin for
	 * @param mainClass
	 *            the main class of the plugin to get
	 * @return a secure view of the plugin for the robot and mainclass
	 *         (or <code>null</code> if no plugin found). This
	 *         plugin may already run, so be careful when using this
	 *         method.
	 * @see SecurePluginView
	 */
	@SuppressWarnings("unchecked")
	public static IPlugable getPlugin(String robotName, String mainClass) {
		RobotPluginContainer container = robotPlugins.get(robotName);
		if (container != null) {
			return container.getPlugin(strip(mainClass));
		}
		logClass.warn(I18n.getString("PluginManager.log.norobot.get", strip(mainClass), robotName) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		return null;
	}

	/**
	 * Returns the main classes of the plugins which are referenced by
	 * the profile of the given robot.
	 * 
	 * @param robotName
	 *            the name of the robot
	 * @return list with the main class names of the plugins which are
	 *         referenced by the robot (or an empty vector)
	 */
	public static List<String> getReferencedPluginsNames(String robotName) {
		RobotPluginContainer container = robotPlugins.get(robotName);
		if (container != null) {
			return container.getReferencedPlugins( );
		}
		return Collections.emptyList( );
	}

	/**
	 * Gets one instance of every plugin. These instances might be
	 * running and in use, so use this method to access information
	 * about the plugin only.
	 * 
	 * @return one instance of every registered plugin as secure views
	 * @see SecurePluginView
	 */
	@SuppressWarnings("unchecked")
	public static List<IPlugable> getPluginSamples() {
		return PluginLoader.getInstances( );
	}

	/**
	 * Gets all plugin instances.<br>
	 * This is every instance for every plugin
	 * 
	 * @return all plugins as secure views.
	 * @see SecurePluginView
	 */
	@SuppressWarnings("unchecked")
	private static List<IPlugable> getAllPlugins() {
		ArrayList<IPlugable> plugins = new ArrayList<IPlugable>( );
		for (RobotPluginContainer container : robotPlugins.values( )) {
			plugins.addAll(container.getPlugins( ));
		}
		for (UnrefPluginContainer container : unrefPlugins.values( )) {
			plugins.addAll(container.getAll( ));
		}
		return plugins;
	}

	/**
	 * Sets the given locale as current locale for all plugins.
	 * 
	 * @param locale
	 *            the new locale to set
	 * @see IPlugable#setLocale(Locale)
	 */
	@SuppressWarnings("unchecked")
	public static void setLocale(Locale locale) {
		List<IPlugable> plugins = getAllPlugins( );
		for (IPlugable plugin : plugins) {
			plugin.setLocale(locale);
		}
	}

	/**
	 * Gets an instance of every plugin of the given robot. These
	 * instances might be running and in use, so use this method to
	 * access information about the plugin only.
	 * 
	 * @param robotName
	 *            the name of the robot
	 * @return an instance of every plugin (as secure view) of the
	 *         robot
	 * @see SecurePluginView
	 */
	@SuppressWarnings("unchecked")
	public static List<IPlugable> getPlugins(String robotName) {
		RobotPluginContainer container = robotPlugins.get(robotName);
		if (container != null) {
			return container.getPlugins( );
		}
		return Collections.emptyList( );
	}

	/**
	 * Gets an instance of every plugin of the given robot. These
	 * instances might be running and in use, so use this method to
	 * access information about the plugin only.
	 * 
	 * @param robotName
	 *            the name of the robot
	 * @param filter
	 *            filter for filtering the plugins which are returned
	 * @return an instance of every plugin (as secure view) of the
	 *         robot
	 * @see SecurePluginView
	 */
	@SuppressWarnings("unchecked")
	public static List<IPlugable> getPlugins(String robotName,
			IPluginFilter filter) {
		RobotPluginContainer container = robotPlugins.get(robotName);
		if (container != null) {
			return container.getPlugins(filter);
		}
		return Collections.emptyList( );
	}

	/**
	 * Gets an instance of every plugin of the given robot for the
	 * given type. These instances might be running and in use, so use
	 * this method to access information about the plugin only.
	 * 
	 * @param robotName
	 *            the name of the robot
	 * @param type
	 *            the type the plugins returned should have. Use one
	 *            of the constants defined in {@link PluginType}
	 * @return an instance of every plugin (as secure view) of the
	 *         robot and given type
	 * @see SecurePluginView
	 */
	@SuppressWarnings("unchecked")
	public static List<IPlugable> getPluginsForType(String robotName, int type) {
		RobotPluginContainer container = robotPlugins.get(robotName);
		if (container != null) {
			return container.getPluginsForType(type);
		}
		return Collections.emptyList( );
	}

	/**
	 * Starts the given plugin class in an own window for the current
	 * robot of the application.
	 * 
	 * @param mainClass
	 *            the main class of the plugin to start.
	 */
	public static void startPluginInWindow(String mainClass) {
		String robotName = Robot.NAME_NONE;
		Robot robot = ApplicationManager.getCurrentRobot( );
		if (robot != null) {
			robotName = robot.getName( );
		}

		logClass.debug(I18n.getString("Application.log.runPluginForRobot", //$NON-NLS-1$
				strip(mainClass),
				robotName) + Constants.LINE_SEPARATOR);

		GenericDialog dialog = new GenericDialog(Application.getApplication( )
				.getShell( ));
		dialog.open(strip(mainClass), robotName, true);
	}

	/**
	 * Stops all plugins of the given robot.
	 * 
	 * @param robotName
	 *            the name of the robot, for which the robots should
	 *            be stopped
	 */
	public static void stopPlugins(String robotName) {
		RobotPluginContainer container = robotPlugins.get(robotName);
		if (container != null) {
			container.stopAll( );
		}
		else {
			logClass.warn(I18n.getString("PluginManager.log.norobot.stop.all", robotName) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		}
	}

	/**
	 * Stops a plugin of the given robot with the given main class.
	 * 
	 * @param robotName
	 *            the name of the robot
	 * @param mainClass
	 *            the main class of the plugin to stop
	 * @return <code>true</code> if the plugin was stopped
	 *         successfully
	 */
	public static boolean stopPlugin(String robotName, String mainClass) {
		RobotPluginContainer container = robotPlugins.get(robotName);
		if (container != null) {
			return container.stop(strip(mainClass));
		}
		logClass.warn(I18n.getString("PluginManager.log.norobot.stop", strip(mainClass), robotName) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		return false;
	}

	/**
	 * Stops the the specified instance of the plugin of the given
	 * robot.
	 * 
	 * @param robotName
	 *            the name of the robot
	 * @param mainClass
	 *            the main class of the plugin to stop
	 * @param name
	 *            name identifying the plugins instance (as
	 *            {@link IPlugable#getIdentifier()})
	 * @return <code>true</code> if the plugin was stopped
	 *         successfully
	 */
	public static boolean stopPlugin(String robotName, String mainClass,
			String name) {
		RobotPluginContainer container = robotPlugins.get(robotName);
		if (container != null) {
			return container.stop(strip(mainClass), name);
		}
		logClass.warn(I18n.getString("PluginManager.log.norobot.stop", strip(mainClass), robotName)); //$NON-NLS-1$
		return false;

	}

	/**
	 * Runs a plugin of the given robot with the given main class.<br/><br/>
	 * If there's no plugin with the given name for the robot, this
	 * method tries to find an unreferenced plugin with the given main
	 * class and tries to run this.
	 * 
	 * @param robotName
	 *            the name of the robot
	 * @param mainClass
	 *            the main class of the plugin to run
	 * @return the plugin (as secure view) or <code>null</code> if
	 *         the plugin was not found or could not be run
	 * @see SecurePluginView
	 */
	@SuppressWarnings("unchecked")
	public static IPlugable runPlugin(String robotName, String mainClass) {
		String mClass = strip(mainClass);
		IPlugable plugin = null;
// if (PluginManager.isUnreferencedPlugin(mClass)) {
// plugin = PluginManager.runUnreferencedPlugin(mClass, robotName);
// }
// else {
		RobotPluginContainer container = robotPlugins.get(robotName);
		if (container != null) {
			plugin = container.run(mClass);
			if (plugin == null) {
				logClass.warn(I18n.getString("PluginManager.log.unref.run", mClass, robotName) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
				plugin = PluginManager.runUnreferencedPlugin(mClass, robotName);
			}
		}

		else {
			logClass.warn(I18n.getString("PluginManager.log.norobot.run", mClass, robotName) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		}
// }
		return plugin;
	}

	/**
	 * Runs a plugin of the given robot with the given main class and
	 * identifier.<br/><br/>If no plugin for the given main class
	 * identifier is found, a plugin for the given main class (and any
	 * identifier) is returned.
	 * 
	 * @param robotName
	 *            the name of the robot
	 * @param mainClass
	 *            the main class of the plugin to run
	 * @param name
	 *            name identifying the plugins instance (as
	 *            {@link IPlugable#getIdentifier()})
	 * @return the plugin (as secure view) or <code>null</code> if
	 *         the plugin was not found or could not be run
	 * @see SecurePluginView
	 */
	@SuppressWarnings("unchecked")
	public static IPlugable runPlugin(String robotName, String mainClass,
			String name) {
		RobotPluginContainer container = robotPlugins.get(robotName);
		if (container != null) {
			SecurePluginView plug = container.run(strip(mainClass), name);
// if (plug != null) {
// plug.getOriginalPlugin( );
// }
			return plug;
		}

		logClass.warn(I18n.getString("PluginManager.log.norobot.run", strip(mainClass), robotName) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		return null;

	}

	/**
	 * Runs the unreferenced plugin for the given main class for a
	 * robot with no name ({@link de.xirp.profile.Robot#NAME_NONE}
	 * 
	 * @param mainClass
	 *            the main class of the plugin
	 * @return a secure view of the running unreferenced plugin or
	 *         <code>null</code> if no plugin was found
	 * @see SecurePluginView
	 */
	@SuppressWarnings("unchecked")
	public static IPlugable runUnreferencedPlugin(String mainClass) {
		return runUnreferencedPlugin(strip(mainClass), Robot.NAME_NONE);
	}

	/**
	 * Runs a plugin which is not referenced by any robot but sets the
	 * given robot name for the plugin.
	 * 
	 * @param mainClass
	 *            the main class of the plugin to run
	 * @param robotName
	 *            the name of the robot for this plugin
	 * @return the plugin (as secure view) or <code>null</code> if
	 *         the plugin was not found or could not be run
	 * @see SecurePluginView
	 */
	@SuppressWarnings("unchecked")
	public static IPlugable runUnreferencedPlugin(String mainClass,
			String robotName) {
		String mClass = strip(mainClass);
		if (PluginLoader.exists(mClass)) {
			UnrefPluginContainer container = unrefPlugins.get(mClass);
			if (container != null) {
				return container.run(robotName);
			}
			else {
				logClass.warn(I18n.getString("PluginManager.log.notunref", mClass) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
				return null;
			}
		}
		else {
			logClass.warn(I18n.getString("PluginManager.log.plugin.notexist", mClass) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * Stops an unreferenced plugins instance.
	 * 
	 * @param mainClass
	 *            the main class of the plugin to stop
	 * @param instance
	 *            the instance id of the plugin (as of
	 *            {@link IPlugable#getInstanceID()})
	 * @return <code>true</code> if the plugin was stopped
	 */
	public static boolean stopUnreferencedPlugin(String mainClass, int instance) {
		UnrefPluginContainer container = unrefPlugins.get(strip(mainClass));
		if (container != null) {
			return container.stop(instance);
		}
		return false;
	}

	/**
	 * Gets the path to which the libraries of a plugin are extracted.
	 * 
	 * @param info
	 *            the info of the plugin
	 * @return the path for the libraries
	 */
	protected static String getPluginLibPath(PluginInfo info) {
		File pluginLibDir = new File(Constants.PLUGIN_DIR + File.separator +
				"lib" + File.separator + info.getMainClass( )); //$NON-NLS-1$
		pluginLibDir.mkdirs( );

		String path = pluginLibDir.getAbsolutePath( );

		return path;
	}

	/**
	 * Extract the OS independent and appropriate depending jars to a
	 * plugins own directory for the jars.
	 * 
	 * @param info
	 *            the information about the plugin
	 * @param path
	 *            the path to which the plugins jars are extracted
	 */
	protected static void extractPluginLib(PluginInfo info, String path) {
		String notContains = LIB_LINUX_PLUGIN_DIR;
		if (!osWindows( )) {
			notContains = LIB_WINDOWS_PLUGIN_DIR;
		}

		final String notCont = notContains;

		extractFromJar(info, path, new Comparable<String>( ) {

			public int compareTo(String elementName) {
				if (elementName.startsWith(LIB_PLUGIN_DIR) &&
						!elementName.startsWith(LIB_PLUGIN_DIR + JAR_SEPARATOR +
								LIB_DLL_PLUGIN_DIR) &&
						!elementName.startsWith(LIB_PLUGIN_DIR + JAR_SEPARATOR +
								LIB_SO_PLUGIN_DIR) &&
						!elementName.startsWith(LIB_PLUGIN_DIR + JAR_SEPARATOR +
								notCont)) {
					return 0;
				}
				return -1;
			}

		}, LIB_PLUGIN_DIR, false);
	}

	/**
	 * Gets the URLs of all jars which are located exactly at the
	 * given directory path
	 * 
	 * @param path
	 *            the path to get the jar URLs from
	 * @return a list of URLs of the jars
	 */
	private static List<URL> getJarURLsFromPath(String path) {
		File libs = new File(path);
		File[] jars = libs.listFiles(new FilenameFilter( ) {

			public boolean accept(@SuppressWarnings("unused")
			File dir, String name) {
				return name.endsWith(".jar"); //$NON-NLS-1$
			}
		});

		if (jars != null) {
			ArrayList<URL> urls = new ArrayList<URL>(jars.length);

			for (File jar : jars) {
				try {
					urls.add(jar.toURI( ).toURL( ));
				}
				catch (MalformedURLException e) {
					logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
				}
			}
			return urls;
		}
		return new ArrayList<URL>( );
	}

	/**
	 * Get the URLs of all jars which are need for the current OS.
	 * 
	 * @param path
	 *            the base path for the jars
	 * @return list of URLs of the jars
	 */
	private static List<URL> getJarURLsForOS(String path) {
		List<URL> urls = getJarURLsFromPath(path);

		if (osWindows( )) {
			urls.addAll(getJarURLsFromPath(path + File.separator +
					LIB_WINDOWS_PLUGIN_DIR));
		}
		else {
			urls.addAll(getJarURLsFromPath(path + File.separator +
					LIB_LINUX_PLUGIN_DIR));
		}

		return urls;
	}

	/**
	 * Gets the URLs for the given plugins information.
	 * 
	 * @param info
	 *            the information about the plugin to get the URLs for
	 * @return the URLs for the jars of the plugin
	 */
	private static List<URL> getJarURLs(PluginInfo info) {
		String path = getPluginLibPath(info);

		List<URL> urls = getJarURLsForOS(path);
		try {
			urls.add(new File(path).toURI( ).toURL( ));
		}
		catch (MalformedURLException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		return urls;
	}

	/**
	 * Constructs a class loader for the given plugin information. The
	 * loader has information about the jars of this plugin.
	 * 
	 * @param info
	 *            information about the plugin
	 * @return a class loader which may be used for loading the plugin
	 */
	public static URLClassLoader getClassLoader(PluginInfo info) {
		File file = new File(info.getAbsoluteJarPath( ));

		List<URL> urls = getJarURLs(info);
		try {
			urls.add(file.toURI( ).toURL( ));
		}
		catch (MalformedURLException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}

		URLClassLoader classLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size( )]));
		return classLoader;
	}

	/**
	 * Gets an instance of a plugin which is specified by the
	 * PluginInfo Object by using a class loader for this plugin.
	 * 
	 * @param info
	 *            Information about the plugin
	 * @param robotName
	 *            name of the robot this plugin is for
	 * @return Instance of the requested plugin
	 * @throws Exception
	 *             exception when loading the plugin
	 */
	@SuppressWarnings("unchecked")
	protected static SecurePluginView getInstance(PluginInfo info,
			String robotName) throws Exception {
		// try {
		URLClassLoader classLoader = getClassLoader(info);
		Class<IPlugable> claas = (Class<IPlugable>) Class.forName(info.getMainClass( ),
				true,
				classLoader);
		Constructor[] constructors = claas.getConstructors( );
		Constructor useConstructor = null;
		for (Constructor constructor : constructors) {
			Class[] types = constructor.getParameterTypes( );
			if (types.length == 2) {
				// Locale locale = new Locale("");
				if ((types[0] == String.class) &&
						(types[1] == PluginInfo.class)) {
					useConstructor = constructor;
					break;
				}
			}
			else if (types.length == 2) {
				if ((types[0] == PluginInfo.class)) {
					useConstructor = constructor;
					break;
				}
			}
		}
		if (useConstructor == null) {
			logClass.error(I18n.getString("PluginManager.log.pluginCouldNotBeInstanciatedNoConstructorFound", //$NON-NLS-1$
					info.getMainClass( )));
		}
		else {
			Object[] arglist = new Object[useConstructor.getParameterTypes( ).length];
			if (arglist.length == 2) {
				arglist[0] = robotName;
				arglist[1] = info;
			}
			else if (arglist.length == 1) {
				arglist[0] = info;
			}
			IPlugable plugin = (IPlugable) useConstructor.newInstance(arglist);
			return new SecurePluginView(plugin);
		}
		return null;
	}

	/**
	 * Gets the properties with the preferences for all plugins.
	 * 
	 * @return Properties with plugin preferences
	 */
	public static PropertiesConfiguration getProperties() {
		if (props == null) {
			File f = new File(Constants.PLUGIN_PREFS_FILE);
			if (!f.exists( )) {
				try {
					f.createNewFile( );
				}
				catch (IOException e) {
					logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
				}
			}
			try {
				props = new PropertiesConfiguration(Constants.PLUGIN_PREFS_FILE);
				props.setAutoSave(true);
				logClass.debug(Constants.LINE_SEPARATOR);
			}
			catch (ConfigurationException e) {
				logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}

		}

		return props;
	}

	/**
	 * Checks if the operating system we are running on is windows.
	 * 
	 * @return <code>true</code> if this is windows
	 */
	private static boolean osWindows() {
		return SystemUtils.IS_OS_WINDOWS;
	}

	/**
	 * Gets the library path used on startup of this application.
	 * 
	 * @return the first entry of the library path.
	 */
	private static String getLibraryPath() {
		String path = ManagementFactory.getRuntimeMXBean( ).getLibraryPath( );
		// String path = System.getProperty("java.library.path");
		// //$NON-NLS-1$
		return path.split(File.pathSeparator)[0];
	}

	/**
	 * Extracts files from the plugins jar.
	 * 
	 * @param info
	 *            the information about the plugin
	 * @param destination
	 *            destination for extraction
	 * @param comparer
	 *            comparer which returns <code>0</code> if an
	 *            element from the jar should be extracted
	 * @param replace
	 *            string of the elements path which should be deleted
	 * @param deleteOnExit
	 *            <code>true</code> if the extracted files should be
	 *            deleted on exit of the application.
	 * @return <code>false</code> if an error occurred while
	 *         extraction
	 */
	private static boolean extractFromJar(PluginInfo info, String destination,
			Comparable<String> comparer, String replace, boolean deleteOnExit) {
		if (logClass.isTraceEnabled( )) {
			logClass.trace(Constants.LINE_SEPARATOR +
					"Extracting for Plugin: " + info.getDefaultName( ) + " to path " + destination + Constants.LINE_SEPARATOR); //$NON-NLS-1$ //$NON-NLS-2$
		}
		ZipInputStream zip = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(info.getAbsoluteJarPath( ));
			zip = new ZipInputStream(in);

			ZipEntry entry = null;
			while ((entry = zip.getNextEntry( )) != null) {
				// relative name with slashes to separate dirnames.
				String elementName = entry.getName( );
				// Check if it's an entry within Plugin Dir.
				// Only need to extract these

				if (comparer.compareTo(elementName) == 0) {
					// Remove Help Dir Name, because we don't like
					// to extract this parent dir
					elementName = elementName.replaceFirst(replace +
							JAR_SEPARATOR,
							"").trim( ); //$NON-NLS-1$ 

					if (!elementName.equalsIgnoreCase("")) { //$NON-NLS-1$
						// if parent dir for File does not exist,
						// create
						// it
						File elementFile = new File(destination, elementName);
						if (!elementFile.exists( )) {
							elementFile.getParentFile( ).mkdirs( );
							if (deleteOnExit) {
								DeleteManager.deleteOnShutdown(elementFile);
							}
						}

						// Only extract files, directorys are created
						// above with mkdirs
						if (!entry.isDirectory( )) {
							FileOutputStream fos = new FileOutputStream(elementFile);
							byte[] buf = new byte[1024];
							int len;
							while ((len = zip.read(buf)) > 0) {
								fos.write(buf, 0, len);
							}
							fos.close( );
							elementFile.setLastModified(entry.getTime( ));
						}
						logClass.trace("Extracted: " + elementName + Constants.LINE_SEPARATOR); //$NON-NLS-1$
					}
				}
				zip.closeEntry( );
			}

		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			return false;
		}
		finally {
			if (zip != null) {
				try {
					zip.close( );
				}
				catch (IOException e) {
					logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
				}
			}
			if (in != null) {
				try {
					in.close( );
				}
				catch (IOException e) {
					logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
				}
			}
		}

		return true;
	}

	/**
	 * Extracts all dll's or so's (if OS is not windows) for the given
	 * plugin. The files are deleted on shutdown.
	 * 
	 * @param info
	 *            the information about the plugin
	 * @return <code>false</code> if extraction failed
	 */
	protected static boolean extractDLLs(PluginInfo info) {
		String path = getLibraryPath( );

		String compareName = LIB_DLL_PLUGIN_DIR;
		if (!osWindows( )) {
			compareName = LIB_SO_PLUGIN_DIR;
		}

		final String compName = compareName;

		return extractFromJar(info, path, new Comparable<String>( ) {

			public int compareTo(String elementName) {
				if (elementName.startsWith(LIB_PLUGIN_DIR + JAR_SEPARATOR +
						compName)) {
					return 0;
				}
				return -1;
			}

		}, LIB_PLUGIN_DIR + JAR_SEPARATOR + compareName, true);
	}

	/**
	 * Extracts the plugins help directory from within the jar to the
	 * given path<br>
	 * Plugins can have html help files inside a directory named help
	 * in the upmost jar layer.<br>
	 * The files are deleted on shutdown.
	 * 
	 * @param info
	 *            Information about the plugin for which help should
	 *            be extracted
	 * @param path
	 *            Path to the directory to which help should be
	 *            extracted
	 * @return <code>true</code> if extraction was successful
	 */
	public static boolean extractHelp(PluginInfo info, String path) {
		return extractFromJar(info, path, new Comparable<String>( ) {

			public int compareTo(String elementName) {
				if (elementName.startsWith(HELP_PLUGIN_DIR)) {
					return 0;
				}
				return -1;
			}

		}, HELP_PLUGIN_DIR, true);
	}

	/**
	 * Extracts the images of the given plugin. The images are deleted
	 * on shutdown.
	 * 
	 * @param info
	 *            information about the plugin
	 * @return <code>true</code> if extraction was successful
	 */
	protected static boolean extractImages(PluginInfo info) {
		final String path = ImageManager.PLUGIN_BASE_PATH + File.separator +
				info.getMainClass( );
		DeleteManager.deleteOnShutdown(path);
		new File(path).mkdirs( );

		return extractFromJar(info, path, new Comparable<String>( ) {

			public int compareTo(String elementName) {
				if (elementName.startsWith(IMAGE_PLUGIN_DIR)) {
					return 0;
				}
				return -1;
			}

		}, IMAGE_PLUGIN_DIR, false);
	}

	/**
	 * Gets a translator for a plugin.
	 * 
	 * @param bundleName
	 *            name of the {@link java.util.ResourceBundle} for
	 *            translation
	 * @param plugin
	 *            plugin to get the translator for
	 * @return translator for the plugin
	 * @see java.util.ResourceBundle
	 */
	@SuppressWarnings("unchecked")
	public static PluginI18NHandler getTranslator(String bundleName,
			IPlugable plugin) {
		return new PluginI18NHandler(bundleName, plugin);
	}
}
