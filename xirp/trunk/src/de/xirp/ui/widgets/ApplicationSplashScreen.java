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
 * ApplicationSplashScreen.java
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
package de.xirp.ui.widgets;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

import de.xirp.managers.ManagerFactory;
import de.xirp.ui.Application;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.ui.util.ressource.FontManager;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.util.Constants;
import de.xirp.util.CreateOnStartup;
import de.xirp.util.FutureRelease;
import de.xirp.util.I18n;
import de.xirp.util.Version;
import de.xirp.util.serialization.ObjectDeSerializer;
import de.xirp.util.serialization.ObjectSerializer;
import de.xirp.util.serialization.SerializationException;

/**
 * Splash screen which is shown at startup, while the application
 * loads.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class ApplicationSplashScreen {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(ApplicationSplashScreen.class);
	/**
	 * A string constant which is a key for the loading time of the
	 * managers.
	 */
	private static final String MANAGER_KEY = "manager"; //$NON-NLS-1$
	/**
	 * A string constant which is a key for the loading time of the
	 * UI.
	 */
	private static final String LOADING_APP_KEY = "loadingApp"; //$NON-NLS-1$
	/**
	 * The main display.
	 */
	private Display display;
	/**
	 * A message label.
	 */
	private Label mesg;
	/**
	 * The application to operate on.
	 */
	private Application app;
	/**
	 * The start up time file.
	 */
	private static final File startUpFile = new File(Constants.CONF_DIR +
			Constants.FS + "startUpTimes.dat"); //$NON-NLS-1$

	/**
	 * Constructs a splash screen for the given display. <br>
	 * <br>
	 * After the splash screen ran, the application itself is shown
	 * Note: Don't show the application before the splash screen ran.
	 * 
	 * @param display
	 *            The current display.
	 * @see de.xirp.ui.Application#show()
	 */
	public ApplicationSplashScreen(Display display) {
		this.display = display;
		start( );
	}

	/**
	 * Runs the splash screen and starts the loading of the
	 * application.
	 */
	private void start() {
		XShell shell = new XShell(display, SWT.NO_TRIM | SWT.DOUBLE_BUFFERED |
				(Version.DEVELOPMENT ? SWT.NONE : SWT.ON_TOP));
		shell.setText("Loading..."); //$NON-NLS-1$
		shell.setImage(ImageManager.getSystemImage(SystemImage.TRAY));

		// Font to use
		final Font TEXT = FontManager.getFont("Arial", 10, SWT.NORMAL); //$NON-NLS-1$
		// Colors to use
		final Color DARK_GREEN = ColorManager.getColor(29, 114, 36);
		final Color GREY_GREEN = ColorManager.getColor(170, 178, 144);
		// load the image into memory
		Image image = ImageManager.getFullPathImageCopy(Constants.IMAGE_DIR +
				File.separator + "splash.png"); //$NON-NLS-1$
		// get the image data
		ImageData imdata = image.getImageData( );

		// set the shell just large enough to hold the image
		shell.setSize(imdata.width, imdata.height);

		// get the bounds of the display to centre the shell on the
		// screen
		Rectangle r = display.getBounds( );
		int shellX = (r.width - imdata.width) / 2;
		int shellY = (r.height - imdata.height) / 2;
		shell.setLocation(shellX, shellY);

		// open the shell, and draw the image
		shell.open( );
		shell.forceFocus( );
		shell.forceActive( );

		// ProgressBar to show the loading status
		final ProgressBar pg = new ProgressBar(shell, SWT.HORIZONTAL);
		final int base = 100000;
		pg.setSize(shell.getSize( ).x - 10, 18);
		pg.setLocation(5, shell.getSize( ).y - pg.getSize( ).y - 5);
		pg.setForeground(DARK_GREEN);
		pg.setMinimum(0);
		pg.setMaximum(base);

		// Label that shows the version number
		Label version = new Label(shell, SWT.NONE);
		version.setSize(shell.getSize( ).x - 10, 17);
		version.setLocation(pg.getLocation( ).x, pg.getLocation( ).y -
				version.getSize( ).y - 25);
		version.setBackground(GREY_GREEN);
		version.setForeground(DARK_GREEN);
		version.setFont(TEXT);
		version.setText(Constants.BASE_NAME +
				" Version " + Version.MAJOR_VERSION + "." //$NON-NLS-1$ //$NON-NLS-2$
				+ Version.MINOR_VERSION + "." + Version.PATCH_LEVEL //$NON-NLS-1$
				+ " Revision " + Version.REVISION);//$NON-NLS-1$

		// Label that shows what is loaded at the moment
		mesg = new Label(shell, SWT.NONE);
		mesg.setSize(shell.getSize( ).x - 10, 17);
		mesg.setLocation(pg.getLocation( ).x, pg.getLocation( ).y -
				mesg.getSize( ).y - 5);
		mesg.setBackground(GREY_GREEN);
		mesg.setForeground(DARK_GREEN);
		mesg.setFont(TEXT);

		// Draw Application Start image
		GC gc = new GC(shell);
		gc.drawImage(image, 0, 0);

		/*
		 * Load data in the background and update progress bar and
		 * message.
		 */
		HashMap<String, Double> percents = new HashMap<String, Double>( );

		try {
			percents = ObjectDeSerializer.<HashMap<String, Double>> getObject(startUpFile);
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) +
					Constants.LINE_SEPARATOR, e);
		}
		catch (SerializationException e) {
			logClass.error("Error: " + e.getMessage( ) +
					Constants.LINE_SEPARATOR, e);
		}

		long overAllStart = System.nanoTime( );
		long start = System.nanoTime( );
		createDirectories( );

		mesg.setText(I18n.getString("ApplicationSplashScreen.gui.loadingApplication"));
		app = new Application( );
		double percentage = 0.006799464;
		if (percents.containsKey(LOADING_APP_KEY)) {
			percentage = percents.get(LOADING_APP_KEY);
		}
		pg.setSelection((int) (pg.getMaximum( ) * percentage));
		long end = System.nanoTime( );
		long diffLoadingApp = end - start;

		start = System.nanoTime( );
		mesg.setText(I18n.getString("ApplicationSplashScreen.gui.loadingProperties"));
		percentage = 0.493550042;
		if (percents.containsKey(MANAGER_KEY)) {
			percentage = percents.get(MANAGER_KEY);
		}
		ManagerFactory.start(pg, mesg, percentage);
		pg.setSelection((int) (pg.getMaximum( ) * percentage));
		end = System.nanoTime( );
		long diffManager = end - start;

		mesg.setText(I18n.getString("ApplicationSplashScreen.gui.loadUI"));
		app.runApp( );// 0.499650494
		pg.setSelection(pg.getMaximum( ));
		mesg.setText(I18n.getString("ApplicationSplashScreen.gui.loadCompleted"));
		long overAllEnd = System.nanoTime( );
		long overAllDiff = overAllEnd - overAllStart;

		double percentLoadingApp = diffLoadingApp / (double) overAllDiff;
		double percentLoadingManager = diffManager / (double) overAllDiff;

		percents.clear( );
		percents.put(LOADING_APP_KEY, percentLoadingApp);
		percents.put(MANAGER_KEY, percentLoadingManager);

		try {
			ObjectSerializer.<HashMap<String, Double>> writeToDisk(percents,
					startUpFile);
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) +
					Constants.LINE_SEPARATOR, e);
		}

		// Wait to show the user the 100% ProgressBar
		try {
			Thread.sleep(150);
		}
		catch (InterruptedException e) {
			logClass.error(e);
		}

		// dispose of the image, font, colors and shell
		gc.dispose( );
		image.dispose( );
		shell.dispose( );
		// Show the Application
		app.show( );
	}

	/**
	 * Creates all necessary directories. This may be redundant but is
	 * secures the application from heavy invalid path crashes.
	 * 
	 * @see de.xirp.util.CreateOnStartup
	 */
	private void createDirectories() {
		// Creates directories for all fields
		// which are annotated with @CreateOnStartup
		Field[] fields = Constants.class.getFields( );
		for (Field field : fields) {
			CreateOnStartup create = field.getAnnotation(CreateOnStartup.class);
			FutureRelease future = field.getAnnotation(FutureRelease.class);
			if (create != null && future == null) {
				try {
					String s = field.get(null).toString( );
					File f = new File(s);
					if (create.isDirectory( )) {
						if (create.override( )) {
							if (f.exists( )) {
								f.delete( );
							}
						}
						FileUtils.forceMkdir(f);
					}
					else {
						try {
							if (f.exists( )) {
								if (create.override( )) {
									f.delete( );
									f.createNewFile( );
								}
							}
							else {
								f.createNewFile( );
							}
						}
						catch (IOException e) {
							logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
						}
					}

				}
				catch (IllegalArgumentException e) {
					logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
				}
				catch (IllegalAccessException e) {
					logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
				}
				catch (IOException e) {
					logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
				}
			}
		}
	}
}
