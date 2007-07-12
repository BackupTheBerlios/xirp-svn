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
 * Xirp.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * 					 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 15.01.2006:		Created by Matthias Gernand.
 */
package de.xirp;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.xirp.chart.fixedutils.CachedRessourceProvider;
import de.xirp.chart.fixedutils.SWTUtils;
import de.xirp.io.command.Command;
import de.xirp.io.command.CommandDefinition;
import de.xirp.io.command.CommandGamepadManager;
import de.xirp.io.command.CommandKeyManager;
import de.xirp.io.command.CommandManager;
import de.xirp.io.command.Command.GeneratorType;
import de.xirp.io.gamepad.GamepadEvent;
import de.xirp.io.gamepad.GamepadEventListener;
import de.xirp.io.gamepad.GamepadManager;
import de.xirp.io.gamepad.GamepadControl.AxisType;
import de.xirp.profile.ProfileManager;
import de.xirp.profile.Robot;
import de.xirp.ui.Application;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.widgets.ApplicationSplashScreen;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Version;

/**
 * Main class of the
 * {@value de.xirp.util.Constants#NON_UNICODE_BASE_NAME}
 * application.<br>
 * Logging is initialized, display created and application started.<br>
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class Xirp {

	/**
	 * The Logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(Xirp.class);
	/**
	 * Contains the starting time in milliseconds.
	 */
	private static long start = System.currentTimeMillis( );
	/**
	 * Will contain the time in milliseconds when the application is
	 * fully loaded.
	 */
	private static long stop;
	/**
	 * Will contain the difference between start and stop time.
	 */
	private static long startUp;
	/**
	 * The file reader which locks the file which locks the
	 * application.
	 */
	private static FileReader fr;
	/**
	 * The display of this application.
	 */
	private static Display display;

	/**
	 * Starts
	 * {@value de.xirp.util.Constants#NON_UNICODE_BASE_NAME}.<br>
	 * 
	 * @param args
	 *            none supported
	 */
	public static void main(String[] args) {
		run( );
	}

	/**
	 * Runs the application.
	 */
	protected static void run() {
		File lock = checkLockFile( );
		if (lock.exists( )) {
			showLockedMessage( );
		}
		else {
			lockApplication(lock);
		}

		try {
			/* Init Logging */
			initLogging( );
			checkForLanguageJar( );

			/*
			 * Initialize the I18n class before any other events may
			 * occur, because otherwise there will be problems with
			 * the language jars
			 */
			I18n.setLocale(new Locale("en")); //$NON-NLS-1$

			// Start the manager for bindings for keys to plugin
			// commands
			// GlobalPluginKeyManager.start( );
			display = Display.getDefault( );
			Display.setAppName(Constants.APP_NAME);

			/*
			 * add a global listener for key down events which will
			 * trigger a plugin command if a command is associated
			 * with the given key
			 */
			addKeyFilter( );

			addGamepadListener( );

			SWTUtils.setProvider(new CachedRessourceProvider( ));

			// Start screen. Loads application
			new ApplicationSplashScreen(display);

			registerMouseMonitorListeners( );

			stop = System.currentTimeMillis( );
			startUp = stop - start;

			logClass.info(I18n.getString("Xirp.log.startupTime", //$NON-NLS-1$
					Constants.NON_UNICODE_BASE_NAME,
					startUp) + Constants.LINE_SEPARATOR);
			
			/* Start event loop */
			installSWTEventLoop( );

			display.dispose( );
		}
		catch (Throwable e) {
			System.err.println(I18n.getString("Xirp.log.exceptionAtStartup", //$NON-NLS-1$
					Constants.LINE_SEPARATOR + e.getMessage( ) +
							Constants.LINE_SEPARATOR));
			e.printStackTrace( );
			logClass.fatal(I18n.getString("Xirp.log.exceptionAtStartup"), e); //$NON-NLS-1$
			display.dispose( );
			showFatalCrashMessage(e);
		}
	}

	/**
	 * Registers listeners needed to be able to monitor mouse events
	 * which occur on the UI.
	 */
	private static void registerMouseMonitorListeners() {
		// display.addFilter(SWT.MouseDown, new Listener( ) {
		//
		// @Override
		// public void handleEvent(Event event) {
		// MouseClickSaver.monitorClick(event.button);
		// }
		//
		// });
		//
		// display.addFilter(SWT.Arm, new Listener( ) {
		//
		// @Override
		// public void handleEvent(Event event) {
		// if (event.widget instanceof MenuItem) {
		// MenuItem itm = (MenuItem) event.widget;
		// Object data = itm.getParent( )
		// .getData(ApplicationMenu.DATA_IS_TOP_MENU);
		// if (data != null) {
		// MouseClickSaver.monitorClick( );
		// }
		// else {
		// MouseClickSaver.monitorMove( );
		// }
		// }
		//
		// }
		//
		// });
	}

	/**
	 * Displays a message box with the given cause.
	 * 
	 * @param e
	 *            the cause which is shown
	 */
	private static void showFatalCrashMessage(Throwable e) {
		Shell shell = new Shell(Display.getDefault( ), SWT.ON_TOP);
		MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
		box.setText(Constants.APP_NAME + " crashed!"); //$NON-NLS-1$
		box.setMessage(Constants.BASE_NAME +
				" has fatally crashed!" //$NON-NLS-1$
				+ Constants.LINE_SEPARATOR +
				"Error message: \"" //$NON-NLS-1$
				+ e.getMessage( ) +
				"\" from exception: \"" //$NON-NLS-1$
				+ e.getClass( ).getSimpleName( ) +
				"\"" //$NON-NLS-1$
				+ Constants.LINE_SEPARATOR +
				"Review the console output and the log files!"); //$NON-NLS-1$
		box.open( );
		System.exit(-1);
	}

	/**
	 * Installs the SWT event loop which keeps the application
	 * running.
	 */
	private static void installSWTEventLoop() {
		while (!Application.getApplication( ).getShell( ).isDisposed( )) {
			try {
				if (!display.readAndDispatch( )) {
					display.sleep( );
				}
			}
			catch (Throwable e) {
				logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Adds an event listener for the gamepad which will execute
	 * commands if a gamepad action which is bind to a command was
	 * triggered.
	 */
	private static void addGamepadListener() {
		GamepadManager.addGamepadEventListener(new GamepadEventListener( ) {

			public void axisChanged(GamepadEvent e) {
// logClass.debug(ToStringBuilder.reflectionToString(e)
// + Constants.LINE_SEPARATOR);
				for (AxisType type : e.getAxisTypes( )) {
					Float value = e.getValue(type);
					CommandDefinition gamepadCommand = CommandGamepadManager.getGamepadCommand(type.toString( ));

					executeCommand(gamepadCommand,
							GeneratorType.JOYSTICK,
							Collections.singletonList(value.toString( )));
				}

			}

			public void buttonPressed(GamepadEvent e) {
// logClass.debug(ToStringBuilder.reflectionToString(e)
// + Constants.LINE_SEPARATOR);
				for (Integer i : e.getPressed( )) {
					CommandDefinition gamepadCommand = CommandGamepadManager.getGamepadCommand(Integer.toString(i));
					executeCommand(gamepadCommand, GeneratorType.JOYSTICK);
				}
			}

		});
	}

	/**
	 * Adds a listener which will execute a command if a key which is
	 * bind to the command was pressed.
	 */
	private static void addKeyFilter() {
		display.addFilter(SWT.KeyDown, new Listener( ) {

			public void handleEvent(Event event) {
				KeyStroke stroke = KeyStroke.getInstance(event.stateMask,
						event.keyCode);
				CommandDefinition def = CommandKeyManager.getCommand(KeySequence.getInstance(stroke));
				executeCommand(def, GeneratorType.CHAR_INPUT_DEVICE);
			}
		});
	}

	/**
	 * Checks if language jars exist for the application.
	 */
	private static void checkForLanguageJar() {
		boolean error = false;

		File languages = new File(Constants.LANGUAGE_DIR);
		// get all jars from the languages folder
		if (!languages.exists( )) {
			logClass.warn("Could not find the directory for language bundles: " //$NON-NLS-1$
					+
					languages.getAbsolutePath( ) + Constants.LINE_SEPARATOR);
			error = true;
		}
		else {
			File files[] = languages.listFiles(new FilenameFilter( ) {

				public boolean accept(@SuppressWarnings("unused")
				File dir, String name) {
					return name.endsWith(".jar"); //$NON-NLS-1$
				}

			});
			if (files.length <= 0) {
				error = true;
			}
		}

		if (error) {
			int result = showLanguageJarMessage( );
			if (result == SWT.NO) {
				System.exit(-1);
			}
		}
	}

	/**
	 * Shows a message if there are no language jars.
	 * 
	 * @return returns the result of the message box which may be
	 *         {@link SWT#YES} or {@link SWT#NO}
	 */
	private static int showLanguageJarMessage() {
		Shell shell = new Shell(Display.getDefault( ), SWT.ON_TOP);
		MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES |
				SWT.NO);
		box.setText("No translations found"); //$NON-NLS-1$
		box.setMessage("The directory 'languages' which should contain jars for translations is missing." //$NON-NLS-1$
				+
				Constants.LINE_SEPARATOR + "Continue without translations?"); //$NON-NLS-1$
		int result = box.open( );
		return result;
	}

	/**
	 * Initializes the log4j logging.
	 * 
	 * @throws FactoryConfigurationError
	 *             thrown if an error occurred while configuring the
	 *             logging
	 */
	private static void initLogging() throws FactoryConfigurationError {
		File f = new File(Constants.LOG_DIR);
		if (!f.exists( )) {
			f.mkdirs( );
		}
		DOMConfigurator.configure(Constants.CONF_DIR + File.separator +
				"log4j.xml"); //$NON-NLS-1$

		// only show debug messages when development is on
		LoggerRepository repo = LogManager.getLoggerRepository( );
		if (!Version.DEVELOPMENT) {
			repo.setThreshold(Level.INFO);
		}
	}

	/**
	 * Tries to delete the lock file and returns it.
	 * 
	 * @return the lock file
	 */
	private static File checkLockFile() {
		File lock = new File(Constants.XIRP_LOCK_FILE);
		lock.delete( );
		return lock;
	}

	/**
	 * Locks the application by opening a file reader on the file.
	 * 
	 * @param lock
	 *            the file to lock
	 */
	private static void lockApplication(File lock) {
		try {
			lock.createNewFile( );
			lock.setExecutable(false);
			lock.setReadable(false);
			lock.setWritable(false);
			lock.deleteOnExit( );
			fr = new FileReader(lock);
			fr.read( );
		}
		catch (IOException e) {
			logClass.error("Error " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
	}

	/**
	 * Shows a message if the application was already locked and quits
	 * if the message box closes.
	 */
	private static void showLockedMessage() {
		Shell shell = new Shell(Display.getDefault( ), SWT.ON_TOP);
		MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
		box.setText(Constants.APP_NAME + " already running!"); //$NON-NLS-1$
		box.setMessage(Constants.BASE_NAME +
				" is already running in another instance!" //$NON-NLS-1$
				+ Constants.LINE_SEPARATOR +
				"Use the other instance or close it and try again to start!"); //$NON-NLS-1$

		box.open( );
		System.exit(-1);
	}

	/**
	 * Executes a command for the given {@link CommandDefinition}
	 * using the current robot or executing it for all robots if the
	 * control overview is active.
	 * 
	 * @param def
	 *            the definition of the command
	 * @param type
	 *            the generator which generated the command
	 */
	private static void executeCommand(CommandDefinition def, GeneratorType type) {
		if (def != null) {
			logClass.debug("Pressed key for " + def.toString( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR);
			List<Robot> robots = new ArrayList<Robot>( );
			Robot robot = ApplicationManager.getCurrentRobot( );
			if (robot != null) {
				robots.add(robot);
			}
			// for Control Overview Panel, search for a
			// matching Plugin for all robots
			else {
				for (Robot rbt : ProfileManager.getRobots( )) {
					robots.add(rbt);
				}
			}
			List<String> noParams = Collections.emptyList( );
			Command command = new Command(Xirp.class,
					System.currentTimeMillis( ),
					type,
					def,
					noParams);
			CommandManager.command(command, robots);
		}
	}

	/**
	 * Executes a command for the given {@link CommandDefinition}
	 * using the current robot or executing it for all robots if the
	 * control overview is active.
	 * 
	 * @param def
	 *            the definition of the command
	 * @param type
	 *            the generator which generated the command
	 * @param params
	 *            parameters for the command
	 */
	private static void executeCommand(CommandDefinition def,
			GeneratorType type, List<String> params) {
		if (def != null) {
			logClass.debug("Pressed key for " + def.toString( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR);
			List<Robot> robots = new ArrayList<Robot>( );
			Robot robot = null;
			try {
				robot = ApplicationManager.getCurrentRobot( );
			}
			catch (Exception e) {
				// do nothing
			}
			if (robot != null) {
				robots.add(robot);
			}
			// for Control Overview Panel, search for a
			// matching Plugin for all robots
			else {
				for (Robot rbt : ProfileManager.getRobots( )) {
					robots.add(rbt);
				}
			}
			// List<String> noParams = Collections.emptyList( );
			Command command = new Command(Xirp.class,
					System.currentTimeMillis( ),
					type,
					def,
					params);
			CommandManager.command(command, robots);
		}
	}
}
