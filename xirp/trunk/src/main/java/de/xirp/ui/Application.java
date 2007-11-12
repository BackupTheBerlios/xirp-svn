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
 * Application.java
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
package de.xirp.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import de.xirp.managers.ManagerFactory;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.ApplicationContent;
import de.xirp.ui.widgets.ApplicationMenu;
import de.xirp.ui.widgets.ApplicationStatusBar;
import de.xirp.ui.widgets.ApplicationToolBar;
import de.xirp.ui.widgets.ApplicationTray;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Variables;

/**
 * This class represents the user interface of Xirp. It creates all
 * menus and panels.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class Application {

	/*
	 * Use 24x24 icons for menu entries and tool items
	 */

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(Application.class);
	/**
	 * The main shell of the user interface.
	 */
	private XShell shell;
	/**
	 * The Application itself.
	 */
	private static Application app;
	/**
	 * Thread containing the shutdown sequence
	 */
	private Thread thread;
	/**
	 * The applications menu.
	 */
	private ApplicationMenu appMenu;
	/**
	 * The applications tool bar.
	 */
	private ApplicationToolBar appToolBar;
	/**
	 * The applications content.
	 */
	private ApplicationContent appContent;
	/**
	 * The Applications tray.
	 */
	private ApplicationTray appTray;
	/**
	 * The width of the application.
	 */
	private final int WIDTH = 200;
	/**
	 * The height of the application.
	 */
	private final int HEIGHT = 200;

	/**
	 * This constructor initializes the main shell.
	 * 
	 * @see de.xirp.ui.Application#runApp( )
	 */
	public Application() {
		logClass.info(I18n.getString("Application.log.initialGUI")); //$NON-NLS-1$
		init( );
		app = this;
		logClass.info(I18n.getString("Application.log.initialGUIcompleted") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
	}

	/**
	 * This method is called, when an new instance of Application is
	 * created. The shell is created.
	 */
	private void init() {
		shell = new XShell( );
	}

	/**
	 * Runs the Application in background without showing it. Used to
	 * constructs the contents and pre load everything.
	 * 
	 * @see de.xirp.ui.Application#show( )
	 */
	public void runApp() {
		addShutdownHook( );

		logClass.info(I18n.getString("Application.log.createGUI")); //$NON-NLS-1$

		// Do rest of the shell!
		shell.setText(Constants.APP_NAME + " - " + Constants.LONG_NAME); //$NON-NLS-1$
		shell.setMinimumSize(WIDTH, HEIGHT);
		shell.setMaximized(true);
		shell.setImage(ImageManager.getSystemImage(SystemImage.TRAY));

		Monitor primaryMonitor = shell.getDisplay( ).getPrimaryMonitor( );
		Rectangle screenBounds = primaryMonitor.getBounds( );
		Rectangle shellBounds = shell.getBounds( );
		shell.setLocation((screenBounds.x + (screenBounds.width - shellBounds.width) / 2),
				(screenBounds.y + (screenBounds.height - shellBounds.height) / 2));

		GridLayout gl = SWTUtil.setGridLayout(shell, 1, true);
		SWTUtil.resetMargins(gl);
		SWTUtil.resetSpacings(gl);

		// ShellListender, listens for a shellClosed Event
		shell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				if (Variables.NO_PROFILES_LOADED) {
					e.doit = true;
				}
				else {
					e.doit = ApplicationManager.showQuitDialog( );
				}
				if (e.doit) {
					shutdown( );
					logClass.info(I18n.getString("Application.log.exitedAsExpected", Constants.NON_UNICODE_APP_NAME) + Constants.LINE_SEPARATOR); //$NON-NLS-1$ 
					System.exit(0);
				}
			}
		});

		// Do some creation (menu, status, content)
		appMenu = new ApplicationMenu( );
		appToolBar = new ApplicationToolBar( );
		appContent = new ApplicationContent( );
		appTray = new ApplicationTray( );

		appMenu.updateViewMenu( );

		// Layout the shell
		shell.layout( );
		logClass.info(I18n.getString("Application.log.createGUIcompleted") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
	}

	/**
	 * Adds a shutdown hook for the virtual machine which shuts down
	 * this application tidily by calling {@link #shutdown()}.
	 */
	private void addShutdownHook() {
		thread = new Thread( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				shutdown( );
			}

		};
		// FIXME: does not work. May have to do with daemon
		// threads
		// Runtime.getRuntime( ).addShutdownHook(thread);
	}

	/**
	 * Stops all managers and disposes the display.
	 */
	private void shutdown() {
		logClass.info(I18n.getString("Application.log.shuttingDown", Constants.NON_UNICODE_APP_NAME) + Constants.LINE_SEPARATOR); //$NON-NLS-1$

		ManagerFactory.stop( );

		SWTUtil.syncExec(new Runnable( ) {

			public void run() {
				SWTUtil.secureDispose(Application.getApplication( )
						.getAppTray( )
						.getTray( ));
				SWTUtil.secureDispose(getDisplay( ));
			}

		});

		Runtime.getRuntime( ).removeShutdownHook(thread);
	}

	/**
	 * Opens the application and tries to get the focus.
	 */
	public void show() {
		shell.open( );
		shell.forceFocus( );
		logClass.info(Constants.LINE_SEPARATOR +
				I18n.getString("Application.log.xirpStarted", Constants.NON_UNICODE_APP_NAME) + Constants.LINE_SEPARATOR); //$NON-NLS-1$

		if (Variables.NO_PROFILES_LOADED) {
			ApplicationManager.showNewProfileDialog( );
			shell.close( );
		}
		ApplicationManager.contentChanged( );
	}

	/**
	 * Gets the applications content.
	 * 
	 * @return The application content.
	 */
	public ApplicationContent getAppContent() {
		return appContent;
	}

	/**
	 * Gets the status bar of the application.
	 * 
	 * @return the status bar
	 */
	public ApplicationStatusBar getAppStatusBar() {
		return appContent.getStatusBar( );
	}

	/**
	 * Gets the applications menu.
	 * 
	 * @return The application menu.
	 */
	public ApplicationMenu getAppMenu() {
		return appMenu;
	}

	/**
	 * Gets the applications tool bar.
	 * 
	 * @return The application tool bar.
	 */
	public ApplicationToolBar getAppToolBar() {
		return appToolBar;
	}

	/**
	 * Gets the applications tray.
	 * 
	 * @return The application tray.
	 */
	public ApplicationTray getAppTray() {
		return appTray;
	}

	/**
	 * Gets the Display of the applications shell.
	 * 
	 * @return The application shells display.
	 */
	public Display getDisplay() {
		if (!shell.isDisposed( )) {
			return shell.getDisplay( );
		}
		return null;
	}

	/**
	 * Gets the top-level-shell on which this application is based.
	 * 
	 * @return The shell of the application.
	 */
	public Shell getShell() {
		return shell;
	}

	/**
	 * Gets the application.
	 * 
	 * @return The application instance.
	 */
	public static Application getApplication() {
		return app;
	}

}
