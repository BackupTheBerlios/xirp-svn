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
 * AbstractPlugin.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * 					 Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 14.01.2006:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.Map.Entry;

import javolution.util.FastTable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolItem;

import de.xirp.io.comm.data.Datapool;
import de.xirp.io.comm.data.DatapoolException;
import de.xirp.io.comm.data.DatapoolListener;
import de.xirp.io.comm.data.DatapoolManager;
import de.xirp.io.comm.data.DatapoolMessage;
import de.xirp.io.comm.handler.IHandler;
import de.xirp.io.comm.handler.IHandlerMessage;
import de.xirp.io.logging.RobotLogger;
import de.xirp.profile.Robot;
import de.xirp.report.Report;
import de.xirp.settings.Settings;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.widgets.custom.XToolBar;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.collections.ConcurrentMultiValueHashMap;

/**
 * Abstract Plugin which implements some common methods for plugins.<br>
 * 
 * @param <GAbstractData>
 *            The type for the data to use for this plugin. If none
 *            use {@link AbstractData}
 * @param <GAbstractPluginGUI>
 *            The type for the UI of this plugin. If none use
 *            {@link AbstractPluginGUI}
 * @see IPlugable
 * @author Rabea Gransberger
 * @author Matthias Gernand
 */
public abstract class AbstractPlugin<GAbstractData extends AbstractData, GAbstractPluginGUI extends AbstractPluginGUI>
		implements IPlugable<GAbstractPluginGUI> {

	/**
	 * @return {@link SWT#DIALOG_TRIM} | {@link SWT#RESIZE} |
	 *         {@link SWT#MAX} | {@link SWT#MIN}
	 * @see de.xirp.plugin.IPlugable#getWindowStyle()
	 */
	@Override
	public int getWindowStyle() {
		return SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN;
	}

	/**
	 * log4j logger of this class
	 */
	private static RobotLogger logClass = RobotLogger.getLogger(AbstractPlugin.class);

	/**
	 * Current locale used for translation
	 */
	protected Locale locale;

	/**
	 * Some more information about this plugin which is read from the
	 * plugin.properties file
	 */
	protected PluginInfo ownInfo;

	/**
	 * Name of the robot this plugin belongs to
	 */
	protected String robotName = Robot.NAME_NONE;

	/**
	 * There may be more instances of the same plugin which are
	 * identified by the instance ID
	 */
	protected int instanceID = 1;

	/**
	 * Handler for translation of the plugins
	 */
	protected PluginI18NHandler handler;

	/**
	 * The preferences of this plugin
	 */
	protected Settings settings;

	/**
	 * Unique identifier of this plugin
	 */
	protected String uniqueIdentifier;

	/**
	 * Flag for the running status of the plugin <code>true</code>
	 * if the plugin is running
	 */
	protected boolean running = false;

	/**
	 * Default name for message bundles of the plugins
	 */
	protected static final String DEFAULT_BUNDLE_NAME = "messages"; //$NON-NLS-1$

	/**
	 * Separator for the package names in an internationalization
	 * bundle
	 */
	protected static final String BUNDLE_PACKAGE_SEPARATOR = "."; //$NON-NLS-1$

	/**
	 * UIs of this plugin which were already requested by the
	 * application
	 */
	protected Vector<GAbstractPluginGUI> guis = new Vector<GAbstractPluginGUI>( );

	/**
	 * Data of the plugin, used if UI and data are split
	 */
	protected GAbstractData pluginData;
	/**
	 * The datapool of the robot this plugin belongs to.<br/> May be
	 * <code>null</code> if there's no datapool for the robot or the
	 * datapool was not yet requested.
	 */
	protected Datapool datapool;
	/**
	 * The handler of the robot this plugin belongs to.<br/> May be
	 * <code>null</code> if there's no datapool for the robot or the
	 * datapool was not yet requested or there's no connection to the
	 * robot.
	 */
	private IHandler messageHandler;
	/**
	 * Locale list of datapool listeners which this plugin added to
	 * the datapool.
	 */
	private ConcurrentMultiValueHashMap<String, DatapoolListener> recvListeners = new ConcurrentMultiValueHashMap<String, DatapoolListener>( );

	/**
	 * Flag if the plugin is currently stopping.
	 */
	private boolean stopping = false;

	/**
	 * Main Constructor for a Plugin.<br>
	 * Initializes the translation, logging and data (if present).
	 * 
	 * @param robotName
	 *            Name of the Robot this Plugin is for. If you don't
	 *            know the name use
	 *            {@link de.xirp.profile.Robot#NAME_NONE}
	 * @param ownInfo
	 *            Information about this Plugin itself
	 */
	public AbstractPlugin(String robotName, PluginInfo ownInfo) {
		this.locale = I18n.getLocale( );
		if (robotName != null) {
			this.robotName = robotName;
			initDatapool( );
		}
		this.ownInfo = ownInfo;

		String bundlePath = this.getClass( ).getName( );
		bundlePath = bundlePath.substring(0,
				bundlePath.lastIndexOf(BUNDLE_PACKAGE_SEPARATOR));
		handler = PluginManager.getTranslator(bundlePath +
				BUNDLE_PACKAGE_SEPARATOR + DEFAULT_BUNDLE_NAME, this);
		getPluginData( );
	}

	/**
	 * This method is called if a user interface was disposed and
	 * calls resize on every existing UI of this plugin which is not
	 * yet disposed and does not equal the given UI.<br>
	 * This is a hack. Otherwise the UI which was removed from the
	 * layout when a UI of the same plugin is shown in an own window
	 * is not updated when the window is closed and it is re added to
	 * the layout.<br>
	 * Furthermore the disposed UI is removed from the list of UIs.
	 * 
	 * @param abstractPluginGUI
	 *            the UI which was closed
	 */
	protected void guiDisposed(@SuppressWarnings("unused")
	AbstractPluginGUI abstractPluginGUI) {
		List<GAbstractPluginGUI> toRemove = new ArrayList<GAbstractPluginGUI>( );
		for (GAbstractPluginGUI gui : guis) {
			if (!gui.isDisposing( ) && SWTUtil.swtAssert(gui)) {
				gui.resized( );
			}
			else {
				toRemove.add(gui);
			}
		}
		if (!stopping) {
			for (GAbstractPluginGUI gui : toRemove) {
				guis.remove(gui);
			}
		}
	}

	/**
	 * Adds a receive listener for the datapool of the plugins robot
	 * if the datapool for this robot exists.<br/><br/> The listener
	 * is saved in an internal list for this plugin and will be
	 * removed from the datapool when the plugin stops, but you can
	 * still remove it on your own if necessary.
	 * 
	 * @param key
	 *            the key to register on
	 * @param listener
	 *            the listener to add
	 * @see de.xirp.io.comm.data.Datapool#addDatapoolReceiveListener(String,
	 *      DatapoolListener)
	 */
	protected void addDatapoolReceiveListener(String key,
			DatapoolListener listener) {
		if (datapool != null) {
			datapool.addDatapoolReceiveListener(key, listener);
			recvListeners.put(key, listener);
		}
	}

	/**
	 * Sets the name of the robot this plugin belongs to if the robots
	 * name is known to the application after the plugin is loaded and
	 * initializes the datapool of this plugin for this robot.
	 * 
	 * @param robotName
	 *            the name of the robot this plugin belongs to
	 */
	public void setRobot(String robotName) {
		this.robotName = robotName;
		initDatapool( );
	}

	/**
	 * Gets the name of the robot this plugin belongs to
	 * 
	 * @return the name of the robot this plugin belongs to
	 */
	public final String getRobotName() {
		return this.robotName;
	}

	/**
	 * Gets the information about the plugin which was read from the
	 * plugin.properties file
	 * 
	 * @return information about the plugin
	 */
	public final PluginInfo getInfo() {
		return this.ownInfo;
	}

	/**
	 * For internal use only.
	 * 
	 * @see de.xirp.plugin.IPlugable#getInstanceID()
	 */
	public final int getInstanceID() {
		return instanceID;
	}

	/**
	 * For internal use only.
	 * 
	 * @see de.xirp.plugin.IPlugable#setInstanceID(int)
	 */
	public final void setInstanceID(int id) {
		instanceID = id;
	}

	/**
	 * Runs the plugin if it's not already running gets the internal
	 * UI of this plugin, adds the UI as observer to the plugin data
	 * and caches the UI (possibly the application will need more than
	 * one UI for this plugin and needs to keep track of all
	 * instances).
	 * 
	 * @see de.xirp.plugin.IPlugable#getGUI(org.eclipse.swt.widgets.Composite)
	 */
	public final GAbstractPluginGUI getGUI(@SuppressWarnings("unused")
	Composite parentPanel) {
		if (!running) {
			run( );
		}
		GAbstractPluginGUI gui = getGUIInternal(parentPanel);
		if (gui != null) {
			// handled by viewer base and abstractplugingui on
			// construction
			// getPluginData();
			// if (pluginData != null) {
			// pluginData.addObserver(gui);
			// }
			guis.add(gui);
		}
		return gui;
	}

	/**
	 * This method should be overwritten by plugins which have a gui.
	 * It is called by {@link #getGUI(Composite)}. <br/> This
	 * encapsulation ensures that all ever constructed UIs are saved
	 * locally in {@link #guis}} and may be accessed if events need
	 * to be passed to all of them.
	 * 
	 * @param parentPanel
	 *            Parent Composite for the AbstractPluginGUI Composite
	 * @return this abstract implementation returns <code>null</code>
	 * @see de.xirp.plugin.IPlugable#getGUI(org.eclipse.swt.widgets.Composite)
	 */
	protected GAbstractPluginGUI getGUIInternal(@SuppressWarnings("unused")
	Composite parentPanel) {
		return null;
	}

	/**
	 * @see de.xirp.plugin.IPlugable#getSettings()
	 */
	public Settings getSettings() {
		return null;
	}

	/**
	 * @see de.xirp.plugin.IPlugable#preLoad()
	 */
	public final void preLoad() {
		preLoadInternal( );
	}

	/**
	 * This methods is called by {@link #preLoad()} and does nothing
	 * in this default implementation.
	 * 
	 * @see de.xirp.plugin.IPlugable#preLoad()
	 */
	protected void preLoadInternal() {
		// do nothing
	}

	/**
	 * @return this abstract implementation returns <code>null</code>
	 * @see de.xirp.plugin.IPlugable#requiredLibs()
	 */
	public List<String> requiredLibs() {
		return null;
	}

	/**
	 * @return this abstract implementation returns <code>null</code>
	 * @see de.xirp.plugin.IPlugable#getReport()
	 */
	public Report getReport() {
		return null;
	}

	/**
	 * @return this abstract implementation returns <code>false</code>
	 * @see de.xirp.plugin.IPlugable#hasReport()
	 */
	public boolean hasReport() {
		return false;
	}

	/**
	 * Uses the given key, the robot name and the instance id or
	 * unique identifier (if available) for constructing a unique key
	 * for this plugin, usable for saving preferences
	 * 
	 * @param key
	 *            the original key
	 * @return Unique identifier for this plugins key
	 */
	protected String getRealKey(String key) {
		return key + "_" + getRealKey( ); //$NON-NLS-1$ 
	}

	/**
	 * Constructs a unique key constructed of the robots name unique
	 * identifier (if present) or instance id
	 * 
	 * @return Key containing robot name unique identifier/instance id
	 */
	protected String getRealKey() {
		if (uniqueIdentifier == null) {
			return getRobotName( ) + "_" + getInstanceID( ); //$NON-NLS-1$
		}
		return getRobotName( ) + "_" + uniqueIdentifier; //$NON-NLS-1$
	}

	/**
	 * @see de.xirp.plugin.IPlugable#getIdentifier()
	 */
	public String getIdentifier() {
		return uniqueIdentifier;
	}

	/**
	 * @see de.xirp.plugin.IPlugable#setIdentifier(java.lang.String)
	 */
	public void setIdentifier(String id) {
		uniqueIdentifier = id;
	}

	/**
	 * @see de.xirp.plugin.IPlugable#isRunning()
	 */
	public final boolean isRunning() {
		return running;
	}

	/**
	 * @return this abstract implementation returns <code>null</code>
	 * @see de.xirp.plugin.IPlugable#getToolBar(org.eclipse.swt.widgets.CoolItem)
	 */
	public XToolBar getToolBar(@SuppressWarnings("unused")
	CoolItem parent) {
		return null;
	}

	/**
	 * @return if there's a description key ({@link #getDescriptionKey()}
	 *         this key is translated otherwise the default
	 *         description from the plugin.properties file is returned
	 * @see de.xirp.plugin.IPlugable#getDescription()
	 */
	public String getDescription() {
		String key = getDescriptionKey( );
		if (key == null) {
			return ownInfo.getDefaultDescription( );
		}
		else {
			return handler.getString(key);
		}
	}

	/**
	 * Gets the name of the plugin.
	 * 
	 * @return if there's a name key ({@link #getNameKey()} this key
	 *         is translated otherwise the default name from the
	 *         plugin.properties file is returned
	 * @see de.xirp.plugin.IPlugable#getName()
	 */
	public String getName() {
		String key = getNameKey( );
		if (key == null) {
			return ownInfo.getDefaultName( );
		}
		else {
			return handler.getString(key);
		}
	}

	/**
	 * Translated String for the given key and the current local of
	 * the application
	 * 
	 * @param key
	 *            key for translation
	 * @param args
	 *            arguments which are used for replacing variables in
	 *            the text
	 * @return translation for the given key (or the key itself if no
	 *         translation was found)
	 */
	public String getI18nString(String key, Object... args) {
		if (handler == null) {
			return key;
		}
		return handler.getString(key, args);
	}

	/**
	 * Sets {@link #running} to <code>true</code> and than calls
	 * {@link #runInternal()}
	 * 
	 * @see de.xirp.plugin.IPlugable#run()
	 */
	public final void run() {
		running = true;
		runInternal( );
	}

	/**
	 * Called by {@link #run()}. Initializes data an starts the
	 * plugin.
	 * 
	 * @see de.xirp.plugin.IPlugable#run()
	 */
	protected abstract void runInternal();

	/**
	 * Sets {@link #running} to <code>false</code>, de-registers
	 * all datapool listeners {registered with
	 * {@link #addDatapoolReceiveListener(String, DatapoolListener)}},
	 * disposes all UI and than calls {@link #stopInternal()}
	 * 
	 * @see de.xirp.plugin.IPlugable#stop()
	 */
	public final boolean stop() {
		boolean stopped = stopInternal( );

		if (datapool != null) {
			for (Entry<String, FastTable<DatapoolListener>> entry : recvListeners.entrySet( )) {
				for (DatapoolListener listener : entry.getValue( )) {
					datapool.removeDatapoolReceiveListener(entry.getKey( ),
							listener);
				}

			}
		}
		stopping = true;
		for (GAbstractPluginGUI ui : guis) {
			SWTUtil.secureDispose(ui);
		}

		running = false;
		return stopped;
	}

	/**
	 * Called by {@link #stop()}. Free any data resources you don't
	 * need any more.
	 * 
	 * @return <code>true</code> if Plugin was stopped successfully
	 * @see de.xirp.plugin.IPlugable#stop()
	 */
	protected abstract boolean stopInternal();

	/**
	 * Notifies the UI that a property has changed. May be used by the
	 * plugins data, to notify the UI of changes. If there's no data
	 * for this plugin nothing happens but a log message is printed.
	 * <br/><br/> Example:<br/> If the getter in the data is named
	 * <code>getHorizontalValue</code> and <code>plugin</code> is
	 * an instance of this plugin<br/>
	 * <code>plugin.notifyUI("horizontalValue", this.horizontalValue, this.horizontalValue = horizontalValue); //$NON-NLS-1$</code>
	 * 
	 * @param propertyName
	 *            the name of the property which has changed
	 * @param oldValue
	 *            the old value of the property
	 * @param newValue
	 *            the new value of the property
	 */
	public final void notifyUI(String propertyName, Object oldValue,
			Object newValue) {
		if (pluginData != null) {
			pluginData.firePropertyChange(propertyName, oldValue, newValue);
		}
		else {
			logClass.warn(this.robotName,
					I18n.getString("AbstractPlugin.not.notified.nodata", //$NON-NLS-1$
							this.getClass( ).getName( ),
							propertyName) + Constants.LINE_SEPARATOR);
		}
	}

	/**
	 * Notifies the UI that a property has changed. May be used by the
	 * plugins data, to notify the UI of changes. If there's no data
	 * for this plugin nothing happens but a log message is printed.<br/><br/>
	 * Example:<br/> If the getter in the data is named
	 * <code>getHorizontalValue</code> and <code>plugin</code> is
	 * an instance of this plugin<br/>
	 * <code>plugin.notifyUI("horizontalValue"); //$NON-NLS-1$</code>
	 * 
	 * @param propertyName
	 *            the name of the property which has changed
	 */
	public final void notifyUI(String propertyName) {
		if (pluginData != null) {
			pluginData.firePropertyChange(propertyName, null, null);
		}
		else {
			logClass.warn(this.robotName,
					I18n.getString("AbstractPlugin.not.notified.nodata", //$NON-NLS-1$
							this.getClass( ).getName( ),
							propertyName) + Constants.LINE_SEPARATOR);
		}
	}

	/**
	 * Gets the data for this plugin. This call refers to
	 * {@link #getPluginDataInternal()} for the first call. All
	 * subsequent calls will return the same data again.
	 * 
	 * @return the data of this plugin, may be <code>null</code> if
	 *         no data is used
	 */
	public final GAbstractData getPluginData() {
		if (pluginData == null) {
			pluginData = getPluginDataInternal( );
		}
		return pluginData;
	}

	/**
	 * The abstract implementation has no data.
	 * 
	 * @return <code>null</code>
	 */
	protected GAbstractData getPluginDataInternal() {
		return null;
	}

	/**
	 * Gets the image for the given filename for this plugin.<br/><br/>
	 * This is shorthand for
	 * {@link ImageManager#getPluginImage(IPlugable, String)}
	 * 
	 * @param name
	 *            the filename of the image (like "image.png")
	 * @return the image or <code>null</code> if no image for the
	 *         name was found. Refer to the log for the error message
	 *         in this case.
	 */
	public final Image getImage(String name) {
		return ImageManager.getPluginImage(this, name);
	}

	/**
	 * Gets an image copy for the given filename for this plugin.<br/>
	 * This is shorthand for
	 * {@link ImageManager#getPluginImageCopy(IPlugable, String)}<br/><br/>
	 * <b>Note:</b> You have to dispose this image on your own,
	 * because it's not registered at the PluginManager. Use this
	 * method if you need to draw on the image, and therefor can't use
	 * the original one.
	 * 
	 * @param name
	 *            the filename of the image (like "image.png")
	 * @return the image or <code>null</code> if no image for the
	 *         name was found. Refer to the log for the error message
	 *         in this case.
	 */
	public final Image getImageCopy(String name) {
		return ImageManager.getPluginImageCopy(this, name);
	}

	/**
	 * The handler used for translations for this plugin
	 * 
	 * @return the translation handler
	 */
	public PluginI18NHandler getHandler() {
		return handler;
	}

	/**
	 * Changes the locale of the plugins handler and informs all ui's
	 * of this plugin that the locale has changed by
	 * {@link AbstractPluginGUI#localeChanged(Locale)}
	 * 
	 * @see de.xirp.plugin.IPlugable#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale locale) {
		handler.setLocale(locale);
		for (GAbstractPluginGUI gui : guis) {
			gui.localeChanged(locale);
		}
	}

	/**
	 * Initializes the datapool and message handler for the robot of
	 * this plugin
	 */
	private void initDatapool() {
		if (datapool == null || messageHandler == null) {
			try {
				datapool = DatapoolManager.getDatapool(this.getRobotName( ));
				messageHandler = datapool.getHandler( );
			}
			catch (DatapoolException e) {
				// logClass.error(this.robotName,"Error " +
				// e.getMessage() + Constants.LINE_SEPARATOR, e);
			}
		}
	}

	/**
	 * Forwards the given message to the handler, which will process
	 * and forward it to the datapool. If the message is
	 * <code>null</code> the message is not forwarded and a log
	 * message is printed. If there's no handler for this robot the
	 * message could not be forwarded to the datapool.
	 * 
	 * @param message
	 *            the message to forward to the datapool
	 */
	public void forwardToDatapoolOverHandler(IHandlerMessage message) {
		if (message == null) {
			try {
				throw new Exception( );
			}
			catch (Exception e1) {
				logClass.error(this.robotName,
						I18n.getString("AbstractPlugin.log.receivedEmptyMessage") + Constants.LINE_SEPARATOR, e1); //$NON-NLS-1$
			}
			return;
		}
		initDatapool( );
		if (messageHandler != null) {
			messageHandler.receiveToDatapool(message);
		}
		else {
			logClass.warn(this.robotName,
					I18n.getString("AbstractPlugin.not.notified.nohandler", //$NON-NLS-1$
							message) + Constants.LINE_SEPARATOR);
		}
	}

	/**
	 * Forwards the given data for the given key to the datapool.<br/>
	 * If there's no datapool, or the key is <code>null</code> or
	 * the value is <code>null</code> this method does nothing.
	 * 
	 * @param key
	 *            the key for the data
	 * @param value
	 *            the data itself
	 */
	public void sendToRobotOverDatapool(String key, Object value) {
		if (key == null || value == null) {
			return;
		}
		initDatapool( );
		if (datapool != null) {
			DatapoolMessage message = new DatapoolMessage(key, value);
			datapool.sendToRobot(message);
		}
	}
}
