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
 * IPlugable.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 06.02.2006:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

import java.util.List;
import java.util.Locale;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolItem;

import de.xirp.report.Report;
import de.xirp.settings.Settings;
import de.xirp.ui.widgets.custom.XToolBar;

/**
 * Interface which defines methods that have to be implemented by any
 * plugin for this system.<br/> <br/> Each plugin has to define the
 * following constructor:<br/>
 * <code>IPlugable(String robotName, PluginInfo ownInfo)</code><br/>
 * otherwise it can't be instantiated.<br/> <br/> You may use the
 * <code>AbstractPlugin</code> class, if you don't want to extend
 * any other class.
 * 
 * @param <GAbstractPluginGUI>
 *            The type of the GUI for this plugin
 * @see AbstractPlugin
 * @author Rabea Gransberger
 */
public interface IPlugable<GAbstractPluginGUI extends AbstractPluginGUI> {

	/**
	 * Runs the plugin.
	 */
	public void run();

	/**
	 * Gets the type of the plugin.
	 * 
	 * @return type of the plugin
	 */
	public int getPluginType();

	/**
	 * Style of a window used when the plugin is shown in an own
	 * window.
	 * 
	 * @return One of the SWT style constants applicable for shells.
	 */
	public int getWindowStyle();

	/**
	 * Gets the VisualizationType of the plugin, which determines how
	 * the plugin is shown in the GUI.
	 * 
	 * @return type for visualization
	 */
	public int getVisualizationType();

	/**
	 * Asks the plugin which libraries (and other plugins) are
	 * required for the plugin to run.
	 * 
	 * @return List with fully qualified names of the other plugin
	 *         main classes, other needed java classes or a jar file
	 *         (has to end with .jar)
	 */
	public List<String> requiredLibs();

	/**
	 * Plugins may have expensive loading methods.<br>
	 * These should be called within this method to be sure that this
	 * plugin don't needs much time when run is called.
	 */
	public void preLoad();

	/**
	 * Stops the plugin and all active communication and processes of
	 * the plugin.
	 * 
	 * @return <code>true</code> if plugin was stopped successfully
	 */
	public boolean stop();

	/**
	 * Settings of the plugin that should be displayed if the settings
	 * option is called for this plugin.
	 * 
	 * @return the settings or <code>null</code> if none are
	 *         required
	 */
	public Settings getSettings();

	/**
	 * Some plugins have a GUI to display needed contents.
	 * 
	 * @param parentPanel
	 *            Parent Composite for the AbstractPluginGUI Composite
	 * @return Plugin GUI as Composite or <code>null</code> if the
	 *         visualization type is {@link VisualizationType#NONE} or
	 *         {@link VisualizationType#ROBOT_TOOLBAR}
	 */
	public GAbstractPluginGUI getGUI(Composite parentPanel);

	/**
	 * Each plugin belongs to a robot.
	 * 
	 * @param robotName
	 *            Name of the robot the plugin belongs to
	 */
	public void setRobot(String robotName);

	/**
	 * Gets the robot name the plugin belongs to.
	 * 
	 * @return the robot name the plugin belongs to
	 */
	public String getRobotName();

	/**
	 * Gets information about the plugin.
	 * 
	 * @return Information about the plugin as set at instantiation
	 *         time
	 */
	public PluginInfo getInfo();

	/**
	 * There may be more than one instance of the same plugin.
	 * 
	 * @param id
	 *            InstanceID of this Plugin
	 */
	public void setInstanceID(int id);

	/**
	 * Gets the instance ID of the plugin
	 * 
	 * @return instance ID of this plugin
	 */
	public int getInstanceID();

	/**
	 * Checks if the plugin is able to generate a report.
	 * 
	 * @return <code>true</code> if the plugin will give back a
	 *         report when called to do so with {@link #getReport()}
	 */
	public boolean hasReport();

	/**
	 * Generates a report.
	 * 
	 * @return the report or <code>null</code> if none available.
	 *         Please set {@link #hasReport()} to <code>false</code>
	 *         when returning <code>null</code>
	 */
	public Report getReport();

	/**
	 * Gets the (translated) name of the plugin.
	 * 
	 * @return the (translated) name of the plugin
	 */
	public String getName();

	/**
	 * Gets the key used for translation of the name of the plugin.
	 * Some application methods may need to get the key for
	 * on-the-fly-translations.
	 * 
	 * @return the key for the name or <code>null</code> if there is
	 *         not translation key
	 */
	public String getNameKey();

	/**
	 * Gets a description about the plugin. What it does and what it
	 * is for.
	 * 
	 * @return a textual description about the plugin
	 */
	public String getDescription();

	/**
	 * Gets the key used for translation of the description of the
	 * plugin. Some application methods may need to get the key for
	 * on-the-fly-translations.
	 * 
	 * @return the key for the description or <code>null</code> if
	 *         there is not translation key
	 */
	public String getDescriptionKey();

	/**
	 * There may be more than one instance of the same plugin.
	 * Therefor they have identifiers to easily distinguish between
	 * the instances.
	 * 
	 * @param id
	 *            the textual identifier for this plugins instance
	 */
	public void setIdentifier(String id);

	/**
	 * There may be more than one instance of the same plugin.
	 * Therefor they have identifiers to easily distinguish between
	 * the instances.
	 * 
	 * @return the textual identifier for this plugins instance
	 */
	public String getIdentifier();

	/**
	 * Determines the running status of this plugin.
	 * 
	 * @return <code>true</code> if the plugin is running
	 */
	public boolean isRunning();

	/**
	 * If the plugins type contains {@link PluginType#TOOLBAR} this
	 * method returns the tool bar of the plugin.<br>
	 * For specifying the location of the plugins tool bar, the
	 * visualization type has also be set to
	 * {@link VisualizationType#ROBOT_TOOLBAR}.
	 * 
	 * @param parent
	 *            the parent item of the cool bar for this tool bar
	 * @return the tool bar with items useful for the plugin
	 * @see #getPluginType()
	 * @see #getVisualizationType()
	 */
	public XToolBar getToolBar(CoolItem parent);

	/**
	 * Sets the locale for internationalization for this plugin. This
	 * method is called when the language is changed in the UI.
	 * 
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(Locale locale);

	/**
	 * Gets the handler used for translations for this plugin.
	 * 
	 * @return the translation handler
	 */
	public PluginI18NHandler getHandler();
}
