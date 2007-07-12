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
 * AboutDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * 					 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 17.01.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import de.xirp.settings.PropertiesManager;
import de.xirp.speech.TextToSpeechManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.*;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.util.Constants;
import de.xirp.util.Version;

/**
 * Dialog to display information about the application.
 * 
 * @author Rabea Gransberger
 * @author Matthias Gernand
 */
public final class AboutDialog extends XDialog {

	/**
	 * The logger of this class.
	 */
	private static final Logger logClass = Logger.getLogger(AboutDialog.class);
	/**
	 * Width of the dialog.
	 */
	private static final int WIDTH = 250;
	/**
	 * Height of the dialog.
	 */
	private static final int HEIGHT = 200;
	/**
	 * Shell of the dialog.
	 */
	private XShell dialogShell;
	/**
	 * Parent shell.
	 */
	private Shell parent;
	/**
	 * An image object.
	 */
	private Image image;

	/**
	 * Constructs a new dialog on the parent shell.
	 * 
	 * @param parent
	 *            the parent shell.
	 */
	public AboutDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
	}

	/**
	 * Opens the about dialog with information about the application
	 * on it.
	 */
	public void open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});

		dialogShell.setSize(WIDTH, HEIGHT);
		dialogShell.setTextForLocaleKey("Application.gui.about.xirp.title", Constants.BASE_NAME_MAJOR_VERSION); //$NON-NLS-1$
		image = ImageManager.getSystemImage(SystemImage.ABOUT);
		dialogShell.setImage(image);

		SWTUtil.setGridLayout(dialogShell, 2, true);

		XLabel name = new XLabel(dialogShell, SWT.CENTER);
		name.setText((Constants.BASE_NAME + " - " + Constants.LONG_NAME)); //$NON-NLS-1$
		SWTUtil.setGridData(name, true, false, SWT.CENTER, SWT.BEGINNING, 2, 1);

		XLabel xirp = new XLabel(dialogShell, SWT.CENTER);
		xirp.setTextForLocaleKey("AboutDialog.gui.version", Constants.VERSION); //$NON-NLS-1$
		SWTUtil.setGridData(xirp, true, false, SWT.CENTER, SWT.BEGINNING, 2, 1);

		XTable table = new XTable(dialogShell, SWT.SINGLE | SWT.BORDER |
				SWT.FULL_SELECTION | SWT.V_SCROLL);
		SWTUtil.setGridData(table, true, true, SWT.FILL, SWT.FILL, 2, 1);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] titles = {
				"AboutDialog.table.column.name", "AboutDialog.table.column.version", //$NON-NLS-1$ //$NON-NLS-2$
				"AboutDialog.table.column.revision"}; //$NON-NLS-1$
		for (String t : titles) {
			XTableColumn column = new XTableColumn(table, SWT.NONE);
			column.setTextForLocaleKey(t);
			column.pack( );
		}
		String v = Version.MAJOR_VERSION + "." + Version.MINOR_VERSION + "." //$NON-NLS-1$ //$NON-NLS-2$
				+ Version.PATCH_LEVEL;

		XTableItem itm = new XTableItem(table, SWT.NONE);
		String[] str = new String[] {Constants.BASE_NAME, v, Constants.REVISION};
		itm.setText(str);

		SWTUtil.packTable(table);

		XButton tell = new XButton(dialogShell, XButtonType.CLOSE);
		tell.setTextForLocaleKey("AboutDialog.button.tellMore"); //$NON-NLS-1$
		tell.setEnabled(PropertiesManager.isEnableTTS( ));
		tell.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Thread thread = new Thread( ) {

					/*
					 * (non-Javadoc)
					 * 
					 * @see java.lang.Thread#run()
					 */
					@Override
					public void run() {
						try {
							TextToSpeechManager.speak("Xirp' stands for extendable interface for robotic purposes."); //$NON-NLS-1$
							Thread.sleep(25);
							TextToSpeechManager.speak("The application Xirp' aims to provide a feature-rich platform for different robotic purposes."); //$NON-NLS-1$
							Thread.sleep(25);
							TextToSpeechManager.speak("Xirp' is licenced under the Common Public Licence version 1' point 0."); //$NON-NLS-1$
							Thread.sleep(25);
							TextToSpeechManager.speak("If you find Xirp' useful, or have comments on our work', we will be glad to hear from you."); //$NON-NLS-1$
							Thread.sleep(25);
							TextToSpeechManager.speak("The software is provided' as is."); //$NON-NLS-1$
							Thread.sleep(25);
							TextToSpeechManager.speak("The current version is " //$NON-NLS-1$
									+
									Version.MAJOR_VERSION + "' point " //$NON-NLS-1$
									+ Version.MINOR_VERSION + "' point " //$NON-NLS-1$
									+ Version.PATCH_LEVEL);
						}
						catch (InterruptedException e) {
							logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
						}
					}

				};
				thread.start( );
				// SWTUtil.showBusyWhile(parent, new Runnable() {
				//
				// public void run() {
				// try {
				// TextToSpeechManager.speak("Xirp' stands for
				// extendable interface for robotic purposes.");
				// Thread.sleep(25);
				// TextToSpeechManager.speak("The application Xirp'
				// aims to provide a feature-rich platform for
				// different robotic purposes.");
				// Thread.sleep(25);
				// TextToSpeechManager.speak("Xirp' is licenced under
				// the Common Public Licence version 1' point 0.");
				// Thread.sleep(25);
				// TextToSpeechManager.speak("If you find Xirp'
				// useful, or have comments on our work', we will be
				// glad to hear from you.");
				// Thread.sleep(25);
				// TextToSpeechManager.speak("The software is
				// provided' as is.");
				// Thread.sleep(25);
				// TextToSpeechManager.speak("The current version is "
				// + Version.MAJOR_VERSION + "' point " +
				// Version.MINOR_VERSION + "' point " +
				// Version.PATCH_LEVEL);
				// }
				// catch (InterruptedException e) {
				// logClass.error("Error: " + e.getMessage() +
				// Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
				// }
				// }
				// });

			}
		});
		SWTUtil.setGridData(tell, true, false, SWT.FILL, SWT.FILL, 1, 1);

		XButton close = new XButton(dialogShell, XButtonType.CLOSE);
		close.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				dialogShell.close( );
			}
		});
		SWTUtil.setGridData(close, true, false, SWT.FILL, SWT.FILL, 1, 1);

		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

	}
}
