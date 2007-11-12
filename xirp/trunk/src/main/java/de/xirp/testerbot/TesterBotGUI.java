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
 * TesterBotGUI.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 22.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.testerbot;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XGroup;
import de.xirp.ui.widgets.custom.XLabel;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XSpinner;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.ui.widgets.dialogs.XMessageBox;
import de.xirp.ui.widgets.dialogs.XMessageBox.HMessageBoxType;
import de.xirp.util.Constants;

/**
 * This class is a control dialog for the
 * {@link de.xirp.testerbot.TesterBotGUI testerbot}
 * server. The server can be started, stopped and the listening port
 * can be configured.
 * 
 * @author Matthias Gernand
 */
public class TesterBotGUI extends XDialog {

	/**
	 * The default port of the server.
	 */
	private static final short DEFAULT_PORT = 2005;
	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(TesterBotGUI.class);
	/**
	 * The refresh rate in milliseconds for the informations about the
	 * server shown in the dialog.
	 */
	private static final long REFRESH_RATE = 1000;
	/**
	 * A {@link de.xirp.ui.widgets.custom.XLabel},
	 * showing the connection status.
	 * 
	 * @see de.xirp.ui.widgets.custom.XLabel
	 */
	private static XLabel connStatus;
	/**
	 * A {@link de.xirp.ui.widgets.custom.XLabel},
	 * showing the number of connected clients.
	 * 
	 * @see de.xirp.ui.widgets.custom.XLabel
	 */
	private static XLabel numClients;
	/**
	 * A {@link de.xirp.ui.widgets.custom.XLabel},
	 * showing the number of send packages.
	 * 
	 * @see de.xirp.ui.widgets.custom.XLabel
	 */
	private static XLabel packSent;
	/**
	 * A {@link de.xirp.ui.widgets.custom.XLabel},
	 * showing the about of data sent.
	 * 
	 * @see de.xirp.ui.widgets.custom.XLabel
	 */
	private static XLabel bytesSent;
	/**
	 * The current listening port.
	 */
	private static short listeningPort = DEFAULT_PORT;
	/**
	 * The parent {@link org.eclipse.swt.widgets.Shell}.
	 */
	private Shell parent;
	/**
	 * The
	 * {@link de.xirp.ui.widgets.custom.XShell shell}
	 * of the dialog.
	 * 
	 * @see de.xirp.ui.widgets.custom.XShell
	 */
	private XShell dialogShell;
	/**
	 * The testerbot reference.
	 */
	private TesterBot bot;
	/**
	 * The start
	 * {@link de.xirp.ui.widgets.custom.XButton button}.
	 * 
	 * @see de.xirp.ui.widgets.custom.XButton
	 */
	private XButton start;
	/**
	 * The stop
	 * {@link de.xirp.ui.widgets.custom.XButton button}.
	 * 
	 * @see de.xirp.ui.widgets.custom.XButton
	 */
	private XButton stop;
	/**
	 * A {@link java.util.Timer timer} used for the refreshing of the
	 * informations shown.
	 */
	private Timer timer;

	/**
	 * Constructs a new control dialog.
	 * 
	 * @param parent
	 *            The parent shell.
	 */
	public TesterBotGUI(Shell parent) {
		super(parent);
		this.parent = parent;
	}

	/**
	 * Opens the control dialog.
	 */
	public void open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM | SWT.MIN);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				dialogShell.dispose( );
			}
		});

		dialogShell.addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent e) {
				if (timer != null) {
					timer.cancel( );
				}
				if (bot != null && bot.isRunning( )) {
					try {
						bot.stopServer( );
					}
					catch (IOException ex) {
						logClass.error("Error: " + ex.getMessage( ) + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$
					}
				}
			}

		});

		dialogShell.setSize(350, 275);
		dialogShell.setTextForLocaleKey("TesterBotGUI.gui.title"); //$NON-NLS-1$
		Image image = ImageManager.getSystemImage(SystemImage.TESTERBOT);
		dialogShell.setImage(image);
		SWTUtil.setGridLayout(dialogShell, 1, true);

		XGroup serverInfo = new XGroup(dialogShell, SWT.NONE);
		serverInfo.setTextForLocaleKey("TesterBotGUI.group.serverInfo"); //$NON-NLS-1$
		SWTUtil.setGridData(serverInfo, true, true, SWT.FILL, SWT.FILL, 1, 1);
		SWTUtil.setGridLayout(serverInfo, 2, false);

		XLabel l = new XLabel(serverInfo, SWT.NONE);
		l.setTextForLocaleKey("TesterBotGUI.group.serverStatus"); //$NON-NLS-1$
		SWTUtil.setGridData(l, true, false, SWT.BEGINNING, SWT.FILL, 1, 1);

		connStatus = new XLabel(serverInfo, SWT.NONE);
		connStatus.setTextForLocaleKey("TesterBotGUI.label.unknown"); //$NON-NLS-1$
		SWTUtil.setGridData(connStatus, true, true, SWT.FILL, SWT.FILL, 1, 1);

		l = new XLabel(serverInfo, SWT.NONE);
		l.setTextForLocaleKey("TesterBotGUI.label.numberOfClients"); //$NON-NLS-1$
		SWTUtil.setGridData(l, true, false, SWT.BEGINNING, SWT.FILL, 1, 1);

		numClients = new XLabel(serverInfo, SWT.NONE);
		numClients.setTextForLocaleKey("TesterBotGUI.label.unknown"); //$NON-NLS-1$
		SWTUtil.setGridData(numClients, true, true, SWT.FILL, SWT.FILL, 1, 1);

		l = new XLabel(serverInfo, SWT.NONE);
		l.setTextForLocaleKey("TesterBotGUI.label.packagesSent"); //$NON-NLS-1$
		SWTUtil.setGridData(l, true, false, SWT.BEGINNING, SWT.FILL, 1, 1);

		packSent = new XLabel(serverInfo, SWT.NONE);
		packSent.setTextForLocaleKey("TesterBotGUI.label.unknown"); //$NON-NLS-1$
		SWTUtil.setGridData(packSent, true, true, SWT.FILL, SWT.FILL, 1, 1);

		l = new XLabel(serverInfo, SWT.NONE);
		l.setTextForLocaleKey("TesterBotGUI.label.bytesSent"); //$NON-NLS-1$
		SWTUtil.setGridData(l, true, false, SWT.BEGINNING, SWT.FILL, 1, 1);

		bytesSent = new XLabel(serverInfo, SWT.NONE);
		bytesSent.setTextForLocaleKey("TesterBotGUI.label.unknown"); //$NON-NLS-1$
		SWTUtil.setGridData(bytesSent, true, true, SWT.FILL, SWT.FILL, 1, 1);

		XGroup serverSettings = new XGroup(dialogShell, SWT.NONE);
		serverSettings.setTextForLocaleKey("TesterBotGUI.group.serverSettings"); //$NON-NLS-1$
		SWTUtil.setGridData(serverSettings,
				true,
				true,
				SWT.FILL,
				SWT.FILL,
				1,
				1);
		SWTUtil.setGridLayout(serverSettings, 2, true);

		l = new XLabel(serverSettings, SWT.NONE);
		l.setTextForLocaleKey("TesterBotGUI.label.listeningPort"); //$NON-NLS-1$

		XSpinner port = new XSpinner(serverSettings, SWT.BORDER);
		port.setIncrement(1);
		port.setMinimum(1);
		port.setMaximum(65536);
		port.setSelection(DEFAULT_PORT);
		SWTUtil.setGridData(port, true, false, SWT.END, SWT.CENTER, 1, 1);
		port.addModifyListener(new ModifyListener( ) {

			public void modifyText(ModifyEvent e) {
				XSpinner s = (XSpinner) e.widget;
				listeningPort = (short) s.getSelection( );
			}

		});

		XGroup serverControl = new XGroup(dialogShell, SWT.NONE);
		serverControl.setTextForLocaleKey("TesterBotGUI.group.serverControl"); //$NON-NLS-1$
		SWTUtil.setGridData(serverControl,
				true,
				false,
				SWT.FILL,
				SWT.FILL,
				1,
				1);
		SWTUtil.setGridLayout(serverControl, 2, true);

		start = new XButton(serverControl);
		SWTUtil.setGridData(start, true, false, SWT.FILL, SWT.FILL, 2, 1);
		start.setTextForLocaleKey("TesterBotGUI.button.startServer"); //$NON-NLS-1$
		start.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					bot = new TesterBot(listeningPort);
					bot.startServer( );
					start.setEnabled(false);
					stop.setEnabled(true);
					setInfo( );
				}
				catch (IOException ex) {
					logClass.error("Error: " + ex.getMessage( ) + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$
					XMessageBox box = new XMessageBox(dialogShell,
							HMessageBoxType.ERROR,
							XButtonType.CLOSE);
					box.setTextForLocaleKey("TesterBotGUI.messagebox.title.couldNotStart"); //$NON-NLS-1$
					box.setMessageForLocaleKey("TesterBotGUI.messagebox.message.couldNotStart", Constants.LINE_SEPARATOR, ex.getMessage( )); //$NON-NLS-1$
					box.open( );
				}
			}
		});

		stop = new XButton(serverControl);
		SWTUtil.setGridData(stop, true, false, SWT.FILL, SWT.FILL, 2, 1);
		stop.setEnabled(false);
		stop.setTextForLocaleKey("TesterBotGUI.button.stopServer"); //$NON-NLS-1$
		stop.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					bot.stopServer( );
					start.setEnabled(true);
					stop.setEnabled(false);
					setInfo( );
				}
				catch (IOException ex) {
					logClass.error("Error: " + ex.getMessage( ) + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$
					XMessageBox box = new XMessageBox(dialogShell,
							HMessageBoxType.ERROR,
							XButtonType.CLOSE);
					box.setTextForLocaleKey("TesterBotGUI.messagebox.title.errorStopping"); //$NON-NLS-1$
					box.setMessageForLocaleKey("TesterBotGUI.messagebox.message.errorStopping", Constants.LINE_SEPARATOR, ex.getMessage( )); //$NON-NLS-1$
					box.open( );
				}
			}

		});

		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		timer = new Timer( );
		TimerTask task = new TimerTask( ) {

			@Override
			public void run() {
				setInfo( );
			}

		};
		timer.scheduleAtFixedRate(task, 1000, REFRESH_RATE);
	}

	/**
	 * Sets the informations retrieved from the running server. The
	 * number of connected clients, the connection status, the about
	 * of sent data and the sent number of packages are retrieved from
	 * the server. To tank load off the server this query is executed
	 * every {@value #REFRESH_RATE} milliseconds.
	 */
	private void setInfo() {
		SWTUtil.asyncExec(new Runnable( ) {

			public void run() {
				if (bot != null) {
					if (SWTUtil.swtAssert(numClients)) {
						numClients.setText(Long.toString(bot.getClientNumber( )));
					}
					if (SWTUtil.swtAssert(connStatus)) {
						connStatus.setTextForLocaleKey(bot.isRunning( ) ? "TesterBotGUI.label.status.on" : "TesterBotGUI.label.status.off"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					if (SWTUtil.swtAssert(packSent)) {
						packSent.setText(Long.toString(bot.getPacksSend( )));
					}
					if (SWTUtil.swtAssert(bytesSent)) {
						bytesSent.setText(bot.getDataAmount( ).toString( ));
					}
				}
			}
		});
	}
}
