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
 * SecurePluginView.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 10.10.2006:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolItem;

import de.xirp.io.comm.CommunicationManager;
import de.xirp.report.Report;
import de.xirp.settings.Settings;
import de.xirp.ui.widgets.ApplicationToolBar;
import de.xirp.ui.widgets.custom.XToolBar;
import de.xirp.ui.widgets.dialogs.AboutPluginsDialog;
import de.xirp.ui.widgets.dialogs.GenericDialog;
import de.xirp.ui.widgets.dialogs.PreferencesDialog;
import de.xirp.ui.widgets.panels.COPComposite;
import de.xirp.ui.widgets.panels.GenericLayoutPart;
import de.xirp.ui.widgets.panels.RobotToolbar;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.collections.ConcurrentMultiValueHashMap;

/**
 * Secure View for plugins which allows only some classes to access
 * the methods of a plugin
 * 
 * @param <GAbstractPluginGUI>
 *            The type of the GUI for this plugin
 * @author Rabea Gransberger
 */
public final class SecurePluginView<GAbstractPluginGUI extends AbstractPluginGUI>
		implements IPlugable<GAbstractPluginGUI> {

	/**
	 * Log4j Logger for this Class
	 */
	private static Logger logClass = Logger.getLogger(SecurePluginView.class);
	/**
	 * Index where the method which was originally called may be found
	 * in the stack trace
	 */
	private static int methodIndex = 3;

	/**
	 * Map containing all classes for each method for which access to
	 * this method is allowed
	 */
	private static ConcurrentMultiValueHashMap<String, String> accessControl = new ConcurrentMultiValueHashMap<String, String>( );

	static {
		accessControl.put("getSettings", PreferencesDialog.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getIdentifier", GenericDialog.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getIdentifier", "GenericDialog.1"); //$NON-NLS-1$ //$NON-NLS-2$
		accessControl.put("getInstanceID", "GenericDialog.1"); //$NON-NLS-1$ //$NON-NLS-2$
		accessControl.put("getIdentifier", GenericLayoutPart.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getNameKey", GenericLayoutPart.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getGUI", GenericLayoutPart.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getGUI", COPComposite.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getNameKey", COPComposite.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getNameKey", "GenericDialog.2"); //$NON-NLS-1$ //$NON-NLS-2$
		accessControl.put("getNameKey", GenericDialog.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getNameKey", PreferencesDialog.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getNameKey", RobotToolbar.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getGUI", GenericDialog.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getToolBar", RobotToolbar.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getToolBar", ApplicationToolBar.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getDescriptionKey", AboutPluginsDialog.class.getSimpleName( )); //$NON-NLS-1$
		accessControl.put("getIdentifier", "AbstractLayoutPart.3"); //$NON-NLS-1$ //$NON-NLS-2$
		accessControl.put("requiredLibs", PluginLoader.class.getSimpleName( )); //$NON-NLS-1$ 
		accessControl.put("setLocale", PluginManager.class.getSimpleName( )); //$NON-NLS-1$ 
		accessControl.put("getOriginalPlugin", RobotPluginContainer.class.getSimpleName( )); //$NON-NLS-1$ 
		accessControl.put("getOriginalPlugin", CommunicationManager.class.getSimpleName( )); //$NON-NLS-1$ 
		accessControl.put("getOriginalPlugin", UnrefPluginContainer.class.getSimpleName( )); //$NON-NLS-1$ 

		/**
		 * For Java 1.6 the method index has changed
		 */
		if (SystemUtils.IS_JAVA_1_6) {
			methodIndex = 2;
		}
	}

	/**
	 * The original plugin of this secure view
	 */
	private IPlugable<GAbstractPluginGUI> originalPlugin;

	/**
	 * Constructs a new Secure View of a plugin
	 * 
	 * @param plugin
	 *            the plugin to secure (may be a secure view, if this
	 *            is the case the view is replaced with this one)
	 * @throws InstantiationException
	 *             if the given plugin was <code>null</code>
	 */
	protected SecurePluginView(IPlugable<GAbstractPluginGUI> plugin)
			throws InstantiationException {
		if (plugin == null) {
			throw new InstantiationException("The given plugin must not be null."); //$NON-NLS-1$
		}
		this.originalPlugin = getRealPlugin(plugin);
	}

	/**
	 * Gets the real plugin of a plugin which may be a secure view.
	 * 
	 * @param plugin
	 *            The plugin to get the original plugin from
	 * @return a plugin which is not a secure view
	 */
	@SuppressWarnings("unchecked")
	private IPlugable<GAbstractPluginGUI> getRealPlugin(
			IPlugable<GAbstractPluginGUI> plugin) {
		if (plugin instanceof SecurePluginView) {
			SecurePluginView<GAbstractPluginGUI> secure = (SecurePluginView<GAbstractPluginGUI>) plugin;
			return getRealPlugin(secure.getOriginalPlugin( ));
		}
		return plugin;
	}

	/**
	 * Gets the original plugin out of this secure view.
	 * 
	 * @return the original plugin
	 */
	public IPlugable<GAbstractPluginGUI> getOriginalPlugin() {
		if (accessAllowed( )) {
			return originalPlugin;
		}
		return null;
	}

	/**
	 * Prints a log message that the access to the given method was
	 * not allowed.
	 * 
	 * @param methodName
	 *            the method name for which the access wasn't allowed
	 */
	private void accessNotAllowed(String methodName) {
		try {
			throw new IllegalAccessException(I18n.getString("SecurePluginView.exception.noPermission1", //$NON-NLS-1$
					methodName,
					getName( )));
		}
		catch (IllegalAccessException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Prints a log message that the access to the given method was
	 * not allowed from the given class
	 * 
	 * @param methodName
	 *            the name of the method
	 * @param callerClassName
	 *            the name of the class which tried to call this
	 *            method
	 */
	private void accessNotAllowed(String methodName, String callerClassName) {
		try {
			throw new IllegalAccessException(I18n.getString("SecurePluginView.exception.noPermission2", //$NON-NLS-1$
					callerClassName,
					methodName,
					getName( )));
		}
		catch (IllegalAccessException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Checks if the access to the last called method was allowed by
	 * the class which originally called the method. The called method
	 * and caller class are determined using the currents thread stack
	 * trace.
	 * 
	 * @return <code>true</code> if access to the method is allowed
	 *         for the caller class, <code>false</code> otherwise.
	 *         In this case a log message is printed.
	 */
	private boolean accessAllowed() {
		StackTraceElement[] elems = Thread.currentThread( ).getStackTrace( );
		String methodName = elems[methodIndex].getMethodName( );
		String callerClassName = elems[methodIndex + 1].getClassName( );

		callerClassName = ClassUtils.getShortClassName(callerClassName);
		if (!accessControl.get(methodName).contains(callerClassName)) {
			accessNotAllowed(methodName, callerClassName);
			return false;
		}
		return true;
	}

	/**
	 * Gets the description of the original plugin.
	 * 
	 * @return the description of the plugin
	 * @see de.xirp.plugin.IPlugable#getDescription()
	 */
	public String getDescription() {
		return originalPlugin.getDescription( );
	}

	/**
	 * Gets the key used for translating the description for this
	 * plugin if access is allowed for the caller class.
	 * 
	 * @return the key for the description or an empty string if
	 *         access was not allowed.
	 * @see de.xirp.plugin.IPlugable#getDescriptionKey()
	 */
	public String getDescriptionKey() {
		if (accessAllowed( )) {
			return originalPlugin.getDescriptionKey( );
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * Gets the UI of the current plugin if access is allowed for the
	 * caller class.
	 * 
	 * @param parentPanel
	 *            the parent panel to build the UI upon
	 * @return the UI or <code>null</code> if access was not
	 *         allowed.
	 * @see de.xirp.plugin.IPlugable#getGUI(org.eclipse.swt.widgets.Composite)
	 */
	public GAbstractPluginGUI getGUI(Composite parentPanel) {
		if (accessAllowed( )) {
			return originalPlugin.getGUI(parentPanel);
		}
		return null;
	}

	/**
	 * Gets the identifier of the current plugin if access is allowed
	 * for the caller class.
	 * 
	 * @return the identifier of the plugin or <code>null</code> if
	 *         access was not allowed.
	 * @see de.xirp.plugin.IPlugable#getIdentifier()
	 */
	public String getIdentifier() {
		if (accessAllowed( )) {
			return originalPlugin.getIdentifier( );
		}
		return null;
	}

	/**
	 * Gets the information about the plugin.
	 * 
	 * @return the information about the plugin.
	 * @see de.xirp.plugin.IPlugable#getInfo()
	 */
	public PluginInfo getInfo() {
		return originalPlugin.getInfo( );
	}

	/**
	 * Access for this method is not allowed.
	 * 
	 * @return <code>-1</code>
	 * @see de.xirp.plugin.IPlugable#getInstanceID()
	 */
	public int getInstanceID() {
		if (accessAllowed( )) {
			return originalPlugin.getInstanceID( );
		}
		return -1;
	}

	/**
	 * Gets the name of the plugin.
	 * 
	 * @return the name of the plugin.
	 * @see de.xirp.plugin.IPlugable#getName()
	 */
	public String getName() {
		return originalPlugin.getName( );
	}

	/**
	 * Gets the key used for translating the name of the plugin if
	 * access is allowed for the caller class.
	 * 
	 * @return the name key of the plugin or an empty string if access
	 *         was not allowed.
	 * @see de.xirp.plugin.IPlugable#getNameKey()
	 */
	public String getNameKey() {
		if (accessAllowed( )) {
			return originalPlugin.getNameKey( );
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * Gets the type of the plugin.
	 * 
	 * @return the type of the plugin
	 * @see de.xirp.plugin.IPlugable#getPluginType()
	 */
	public int getPluginType() {
		return originalPlugin.getPluginType( );
	}

	/**
	 * Gets the report of this plugin.
	 * 
	 * @return the report of this plugin.
	 * @see de.xirp.plugin.IPlugable#getReport()
	 */
	public Report getReport() {
		return originalPlugin.getReport( );
	}

	/**
	 * Gets the robot name of this plugin
	 * 
	 * @return the name of the robot of this plugin
	 * @see de.xirp.plugin.IPlugable#getRobotName()
	 */
	public String getRobotName() {
		return originalPlugin.getRobotName( );
	}

	/**
	 * Gets the settings of this plugin if access is allowed for the
	 * caller class.
	 * 
	 * @return the settings of this plugin or <code>null</code> if
	 *         access was not allowed.
	 * @see de.xirp.plugin.IPlugable#getSettings()
	 */
	public Settings getSettings() {
		if (accessAllowed( )) {
			return originalPlugin.getSettings( );
		}
		return null;
	}

	/**
	 * Gets the toolbar of this plugin if access is allowed for the
	 * caller class.
	 * 
	 * @param parent
	 *            the parent for the toolbar
	 * @return the toolbar or <code>null</code> if access was not
	 *         allowed.
	 * @see de.xirp.plugin.IPlugable#getToolBar(org.eclipse.swt.widgets.CoolItem)
	 */
	public XToolBar getToolBar(CoolItem parent) {
		if (accessAllowed( )) {
			return originalPlugin.getToolBar(parent);
		}
		return null;
	}

	/**
	 * Gets the visualization type for this plugin.
	 * 
	 * @return the visualization type
	 * @see de.xirp.plugin.IPlugable#getVisualizationType()
	 */
	public int getVisualizationType() {
		return originalPlugin.getVisualizationType( );
	}

	/**
	 * Checks if this plugin may generate a report.
	 * 
	 * @return <code>true</code> if the plugin has a report
	 * @see de.xirp.plugin.IPlugable#hasReport()
	 */
	public boolean hasReport() {
		return originalPlugin.hasReport( );
	}

	/**
	 * Checks if the plugin is running.
	 * 
	 * @return <code>true</code> if the plugin is running.
	 * @see de.xirp.plugin.IPlugable#isRunning()
	 */
	public boolean isRunning() {
		return originalPlugin.isRunning( );
	}

	/**
	 * Access to this method is not allowed.
	 * 
	 * @see de.xirp.plugin.IPlugable#preLoad()
	 */
	public void preLoad() {
		accessNotAllowed("preLoad"); //$NON-NLS-1$
	}

	/**
	 * Gets the list of required libraries for this plugin if access
	 * is allowed for the caller class.
	 * 
	 * @return list of required plugins or an empty list of access is
	 *         not allowed.
	 * @see de.xirp.plugin.IPlugable#requiredLibs()
	 */
	@SuppressWarnings("unchecked")
	public List<String> requiredLibs() {
		if (accessAllowed( )) {
			List<String> reqLibs = originalPlugin.requiredLibs( );
			if (reqLibs != null) {
				return Collections.unmodifiableList(reqLibs);
			}
			return Collections.emptyList( );
		}
		return Collections.emptyList( );
	}

	/**
	 * Access to this method is not allowed. Use the
	 * {@link PluginManager} to run a plugin.
	 * 
	 * @see de.xirp.plugin.IPlugable#run()
	 */
	public void run() {
		accessNotAllowed("run"); //$NON-NLS-1$
	}

	/**
	 * Access to this method is not allowed.
	 * 
	 * @param id
	 *            (unused)
	 * @see de.xirp.plugin.IPlugable#setIdentifier(java.lang.String)
	 */
	public void setIdentifier(@SuppressWarnings("unused")
	String id) {
		accessNotAllowed("setIdentifier"); //$NON-NLS-1$
	}

	/**
	 * Access to this method is not allowed.
	 * 
	 * @param id
	 *            (unused)
	 * @see de.xirp.plugin.IPlugable#setInstanceID(int)
	 */
	public void setInstanceID(@SuppressWarnings("unused")
	int id) {
		accessNotAllowed("setInstanceID"); //$NON-NLS-1$
	}

	/**
	 * Access to this method is not allowed.
	 * 
	 * @param robotName
	 *            (unused)
	 * @see de.xirp.plugin.IPlugable#setRobot(java.lang.String)
	 */
	public void setRobot(@SuppressWarnings("unused")
	String robotName) {
		accessNotAllowed("setRobot"); //$NON-NLS-1$
	}

	/**
	 * Access to this method is not allowed. Use the
	 * {@link PluginManager} to stop a plugin.
	 * 
	 * @return <code>false</code>
	 * @see de.xirp.plugin.IPlugable#stop()
	 */
	public boolean stop() {
		accessNotAllowed("stop"); //$NON-NLS-1$
		return false;
	}

	/**
	 * Sets the locale for this plugin if access for the caller class
	 * is allowed.
	 * 
	 * @see de.xirp.plugin.IPlugable#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale locale) {
		if (accessAllowed( )) {
			originalPlugin.setLocale(locale);
		}
	}

	/**
	 * Gets the handler used for translations for this plugin.
	 * 
	 * @return the translation handler of this plugin
	 * @see de.xirp.plugin.IPlugable#getHandler()
	 */
	public PluginI18NHandler getHandler() {
		return originalPlugin.getHandler( );
	}

	/**
	 * @see de.xirp.plugin.IPlugable#getWindowStyle()
	 */
	@Override
	public int getWindowStyle() {
		return originalPlugin.getWindowStyle( );
	}

}
