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
 * PluginLoader.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * 					 Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 06.02.2006:		Created by Matthias Gernand.
 */
package de.xirp.plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;

import de.xirp.profile.Robot;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.collections.MultiValueHashMap;

/**
 * Class which loads plugins.<br>
 * Plugins have to implement the {@link IPlugable} interface and have
 * to define a <code>plugin.properties</code> file to specify the
 * fully qualified path to the plugins main class.<br>
 * Plugins have to be placed in the plugins folder of the application
 * in order to be found by this loader.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class PluginLoader {

	/**
	 * Log4j Logger for this class
	 */
	private static Logger logClass = Logger.getLogger(PluginLoader.class);
	/**
	 * List of loaded plugins identified by their name
	 */
	private static HashMap<String, PluginInfo> plugins = new HashMap<String, PluginInfo>( );
	/**
	 * A list of instances for all plugins found by this plugin loader
	 */
	@SuppressWarnings("unchecked")
	private static List<IPlugable> instances;

	/**
	 * A map of references to a given plugin<br/> main class to
	 * plugin -> main class from plugin. The map is cleared after the
	 * loading process.
	 */
	private static MultiValueHashMap<String, String> refs = new MultiValueHashMap<String, String>( );
	/**
	 * List of plugins which full fill all needs. List is cleared
	 * after the loading process.
	 */
	private static List<String> currentPluginList;

	/**
	 * Looks for plugins in the plugins directory. Plugins which needs
	 * are not full filled are not accepted.
	 * 
	 * @param manager
	 *            instance of the plugin manager which is used for
	 *            notifying the application about the loading
	 *            progress.
	 */
	protected static void searchPlugins(PluginManager manager) {
		logClass.info(I18n.getString("PluginLoader.log.searchPlugins")); //$NON-NLS-1$
		// Get all Files in the Plugin Directory with
		// Filetype jar
		File pluginDir = new File(Constants.PLUGIN_DIR);
		File[] fileList = pluginDir.listFiles(new FilenameFilter( ) {

			public boolean accept(@SuppressWarnings("unused")
			File dir, String filename) {
				return filename.endsWith(".jar"); //$NON-NLS-1$
			}

		});

		if (fileList != null) {
			double perFile = 1.0 / fileList.length;
			double cnt = 0;
			try {
				// Iterate over all jars and try to find
				// the plugin.properties file
				// The file is loaded and Information
				// extracted to PluginInfo
				// Plugin is added to List of Plugins
				for (File f : fileList) {
					String path = f.getAbsolutePath( );
					if (!plugins.containsKey(path)) {
						manager.throwLoaderProgressEvent(I18n.getString("PluginLoader.progress.searchingInFile", f.getName( )), cnt); //$NON-NLS-1$
						JarFile jar = new JarFile(path);
						JarEntry entry = jar.getJarEntry("plugin.properties"); //$NON-NLS-1$
						if (entry != null) {
							InputStream input = jar.getInputStream(entry);
							Properties props = new Properties( );
							props.load(input);
							String mainClass = props.getProperty("plugin.mainclass"); //$NON-NLS-1$
							PluginInfo info = new PluginInfo(path,
									mainClass,
									props);
							String packageName = ClassUtils.getPackageName(mainClass) +
									"." + AbstractPlugin.DEFAULT_BUNDLE_NAME; //$NON-NLS-1$
							String bundleBaseName = packageName.replaceAll("\\.", "/"); //$NON-NLS-1$  //$NON-NLS-2$
							for (Enumeration<JarEntry> entries = jar.entries( ); entries.hasMoreElements( );) {
								JarEntry ent = entries.nextElement( );
								String name = ent.getName( );
								if (name.indexOf(bundleBaseName) != -1) {
									String locale = name.substring(name.indexOf(AbstractPlugin.DEFAULT_BUNDLE_NAME));
									locale = locale.replaceAll(AbstractPlugin.DEFAULT_BUNDLE_NAME +
											"_", //$NON-NLS-1$
											""); //$NON-NLS-1$
									locale = locale.substring(0,
											locale.indexOf(".properties")); //$NON-NLS-1$
									Locale loc = new Locale(locale);
									info.addAvailableLocale(loc);
								}
							}
							PluginManager.extractAll(info);
							if (isPlugin(info)) {
								plugins.put(mainClass, info);
							}
						}
					}
					cnt += perFile;

				}
			}
			catch (IOException e) {
				logClass.error(I18n.getString("PluginLoader.log.errorSearchingforPlugins") + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
		}
		checkAllNeeds( );
		logClass.info(I18n.getString("PluginLoader.log.searchPluginsCompleted") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
	}

	/**
	 * Checks the needs of all plugins.
	 * 
	 * @see IPlugable#requiredLibs()
	 */
	@SuppressWarnings("unchecked")
	private static void checkAllNeeds() {
		HashMap<String, IPlugable> plugables = new HashMap<String, IPlugable>( );
		MultiValueHashMap<String, String> refs = new MultiValueHashMap<String, String>( );

		// list of all plugins
		List<String> fullPluginList = new ArrayList<String>(plugins.size( ));
		for (PluginInfo info : plugins.values( )) {
			fullPluginList.add(info.getMainClass( ));
		}

		ClassLoader loader = logClass.getClass( ).getClassLoader( );

		// Read the list of available jars from the class path
		String cp = ManagementFactory.getRuntimeMXBean( ).getClassPath( );
		// String cp = System.getProperty("java.class.path");
		// //$NON-NLS-1$
		String[] jars = cp.split(File.pathSeparator);
		List<String> jarList = new ArrayList<String>(jars.length);
		for (String jar : jars) {
			jarList.add(FilenameUtils.getName(jar));
		}
		// The initial list of current plugins equals the full list
		// every plugin which does not full fill the needs
		// it removed from this list
		currentPluginList = new ArrayList<String>(fullPluginList);
		for (PluginInfo info : plugins.values( )) {
			try {
				SecurePluginView view = PluginManager.getInstance(info,
						Robot.NAME_NONE);
				plugables.put(info.getMainClass( ), view);
				boolean check = checkNeeds(view, loader, jarList);
				if (!check) {
					// remove plugins which reference this plugin
					removeRefs(info.getMainClass( ));
				}
			}
			catch (Exception e) {
				logClass.trace(e, e);
			}
		}
		// Remove all plugins of the full list
		// which are no more contained in the current list
		for (String clazz : fullPluginList) {
			if (!currentPluginList.contains(clazz)) {
				plugins.remove(clazz);
				plugables.remove(clazz);
			}
		}
		instances = new ArrayList<IPlugable>(plugables.values( ));
		refs.clear( );
		refs = null;
		currentPluginList.clear( );
		currentPluginList = null;
		fullPluginList.clear( );
		fullPluginList = null;
	}

	/**
	 * Removes plugins from the current list which reference the given
	 * plugin
	 * 
	 * @param mainClass
	 *            the main class of the plugin to remove the
	 *            references for
	 */
	private static void removeRefs(String mainClass) {
		currentPluginList.remove(mainClass);
		// remove plugins which reference this plugin
		for (String otherClass : refs.get(mainClass)) {
			removeRefs(otherClass);
			logClass.info(I18n.getString("PluginLoader.log.removingPluginBecauseOfDependentPlugin", otherClass, mainClass) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		}
	}

	/**
	 * Checks if the given plugin needs other plugins to run.
	 * 
	 * @param plugin
	 *            plugin to check
	 * @param jarList
	 *            list of jar files on the class path
	 * @param loader
	 *            the current class loader used to check if a class is
	 *            available
	 * @return <code>true</code> if all other Plugins needed by this
	 *         Plugin are available
	 * @see IPlugable#requiredLibs()
	 */
	@SuppressWarnings("unchecked")
	private static boolean checkNeeds(IPlugable plugin, ClassLoader loader,
			List<String> jarList) {
		if (plugin == null) {
			return true;
		}

		List<String> req = plugin.requiredLibs( );
		if (req == null) {
			return true;
		}
		boolean ret = true;
		for (String claas : req) {
			if (claas.endsWith(".jar")) { //$NON-NLS-1$
				if (!jarList.contains(claas)) {
					ret &= false;
					logClass.warn(I18n.getString("PluginLoader.log.removingPluginBecauseOfMissingLib", plugin.getName( ), claas) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
				}
			}
			else if (!currentPluginList.contains(claas)) {
				boolean hasClass = true;
				try {
					loader.loadClass(claas);
				}
				catch (ClassNotFoundException e) {
					logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
					hasClass = false;
				}

				if (!hasClass) {
					logClass.warn(I18n.getString("PluginLoader.log.removingPluginBecauseOfMissingLib", plugin.getName( ), claas) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
					ret &= false;
					break;
				}
			}
			else {
				refs.put(claas, plugin.getInfo( ).getMainClass( ));
			}
		}
		return ret;
	}

	/**
	 * Checks if a plugin for the given main class does exist
	 * 
	 * @param mainClass
	 *            the main class to look for
	 * @return <code>true</code> if a plugin for this main class
	 *         does exist
	 */
	protected static boolean exists(String mainClass) {
		return plugins.containsKey(mainClass);
	}

	/**
	 * Gets the information about the specified plugin
	 * 
	 * @param mainClass
	 *            fully qualified path to the plugins main class
	 * @return information about the plugin or <code>null</code> if
	 *         plugin does not exist
	 */
	protected static PluginInfo getInfo(String mainClass) {
		return plugins.get(mainClass);
	}

	/**
	 * Gets information about all the found plugins
	 * 
	 * @return Information about found plugins
	 * @see #searchPlugins(PluginManager)
	 */
	protected static Collection<PluginInfo> getPlugins() {
		return Collections.unmodifiableCollection(plugins.values( ));
	}

	/**
	 * Gets an instance of every plugin which was loaded and accepted
	 * by this plugin loader
	 * 
	 * @return unmodifiable list of plugin instances
	 */
	@SuppressWarnings("unchecked")
	protected static List<IPlugable> getInstances() {
		return Collections.unmodifiableList(instances);
	}

	/**
	 * Tests if the given class implements <code>IPlugable</code>
	 * and therefore is a plugin.
	 * 
	 * @param info
	 *            information about the plugin
	 * @return <code>true</code> if this class is a plugin,
	 *         <code>false</code> otherwise
	 */
	@SuppressWarnings("unchecked")
	private static boolean isPlugin(PluginInfo info) {
		try {
			URLClassLoader classLoader = PluginManager.getClassLoader(info);
			Class<?> test = Class.forName(info.getMainClass( ),
					true,
					classLoader);
			boolean isPlugin = IPlugable.class.isAssignableFrom(test);

			return isPlugin;
		}
		catch (ClassNotFoundException e) {
			logClass.error(I18n.getString("PluginLoader.log.errorSearchingforPlugins") + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		return false;
	}
}
