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
 * ApplicationTray.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 14.03.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;

import de.xirp.ui.Application;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.MessageManager.MessageType;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XMenuItem;
import de.xirp.ui.widgets.custom.XTrayItem;
import de.xirp.util.Constants;

/**
 * This user interface class represents the applications tray icon. It
 * provides a context menu for quick access of important features.
 * 
 * @author Matthias Gernand
 */
public final class ApplicationTray {

	// private static final Logger logClass =
	// Logger.getLogger(ApplicationTray.class);
	/**
	 * The application itself.
	 * 
	 * @see de.xirp.ui.Application
	 */
	private Application application;
	/**
	 * The parent shell.
	 */
	private Shell parent;
	/**
	 * The tray object.
	 */
	private Tray tray;
	/**
	 * The tool tip of the tray item.
	 */
	private ToolTip tip;
	/**
	 * The tray item.
	 */
	private XTrayItem item;

	/**
	 * Constructs a new application tray icon.
	 */
	public ApplicationTray() {
		this.application = Application.getApplication( );
		this.parent = application.getShell( );
		init( );
	}

	/**
	 * Creates the tray icon and its contents.
	 */
	private void init() {
		tray = parent.getDisplay( ).getSystemTray( );
		Image image = ImageManager.getSystemImage(SystemImage.TRAY);

		if (tray != null) {
			item = new XTrayItem(tray, SWT.NONE);
			item.setToolTipText(Constants.APP_NAME);
			final Menu menu = new Menu(parent, SWT.POP_UP);

			item.addSelectionListener(new SelectionAdapter( ) {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (parent.isVisible( )) {
						parent.setVisible(false);
						return;
					}
					if (!parent.isVisible( )) {
						parent.setVisible(true);
					}
				}
			});

			item.addListener(SWT.MenuDetect, new Listener( ) {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
				 */
				public void handleEvent(Event event) {
					menu.setVisible(true);
				}
			});

			item.setImage(image);

			XMenuItem mi = new XMenuItem(menu, SWT.PUSH);
			mi.setTextForLocaleKey("ApplicationTray.menu.item.reportSearch"); //$NON-NLS-1$
			mi.addSelectionListener(new SelectionAdapter( ) {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						/* (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						public void run() {
							ApplicationManager.showReportSearchDialog( );
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			new XMenuItem(menu, SWT.SEPARATOR);

			mi = new XMenuItem(menu, SWT.PUSH);
			mi.setTextForLocaleKey("ApplicationTray.menu.item.mail"); //$NON-NLS-1$
			mi.addSelectionListener(new SelectionAdapter( ) {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						/* (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						public void run() {
							ApplicationManager.showMailDialog( );
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			mi = new XMenuItem(menu, SWT.PUSH);
			mi.setTextForLocaleKey("ApplicationTray.menu.item.contacts"); //$NON-NLS-1$
			mi.addSelectionListener(new SelectionAdapter( ) {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						/* (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						public void run() {
							ApplicationManager.showContactDialog( );
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			new XMenuItem(menu, SWT.SEPARATOR);

			mi = new XMenuItem(menu, SWT.PUSH);
			mi.setTextForLocaleKey("ApplicationTray.menu.item.charts"); //$NON-NLS-1$
			mi.addSelectionListener(new SelectionAdapter( ) {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						/* (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						public void run() {
							ApplicationManager.showChartDialog( );
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			new XMenuItem(menu, SWT.SEPARATOR);

			mi = new XMenuItem(menu, SWT.PUSH);
			mi.setTextForLocaleKey("ApplicationTray.menu.item.testerbot"); //$NON-NLS-1$
			mi.addSelectionListener(new SelectionAdapter( ) {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						/* (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						public void run() {
							ApplicationManager.showTesterBotDialog( );
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			new XMenuItem(menu, SWT.SEPARATOR);

			mi = new XMenuItem(menu, SWT.PUSH);
			mi.setTextForLocaleKey("ApplicationTray.menu.item.preferences"); //$NON-NLS-1$
			mi.addSelectionListener(new SelectionAdapter( ) {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						/* (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						public void run() {
							ApplicationManager.showPreferencesDialog( );
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			new XMenuItem(menu, SWT.SEPARATOR);

			mi = new XMenuItem(menu, SWT.PUSH);
			mi.setTextForLocaleKey("ApplicationTray.menu.item.help"); //$NON-NLS-1$
			mi.addSelectionListener(new SelectionAdapter( ) {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						/* (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						public void run() {
							ApplicationManager.showHelpDialog( );
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			new XMenuItem(menu, SWT.SEPARATOR);

			mi = new XMenuItem(menu, SWT.PUSH);
			mi.setTextForLocaleKey("Application.gui.trayitem.menu.quit"); //$NON-NLS-1$
			mi.addSelectionListener(new SelectionAdapter( ) {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					parent.close( );
				}
			});
		}
	}

	/**
	 * Returns the tray icon.
	 * 
	 * @return The tray object.
	 * 
	 * @see org.eclipse.swt.widgets.Tray
	 */
	public Tray getTray() {
		return tray;
	}

	/**
	 * Returns the {@link org.eclipse.swt.widgets.ToolTip}
	 * object.
	 * 
	 * @return The ToolTip object of the tray.
	 * 
	 * @see org.eclipse.swt.widgets.ToolTip
	 */
	public ToolTip getToolTip() {
		return tip;
	}

	/**
	 * Shows the given message for the given MessageBoxType in a
	 * {@link org.eclipse.swt.widgets.ToolTip}.
	 * 
	 * @param title
	 *            The title of the message.
	 * @param message
	 *            The message to show.
	 * @param type
	 *            The type of the message.
	 * 
	 * @see de.xirp.ui.util.MessageManager.MessageType
	 */
	public void showToolTip(String title, String message, MessageType type) {
		if (tip == null) {
			switch (type) {
				case INFO:
					tip = new ToolTip(parent, SWT.BALLOON
							| SWT.ICON_INFORMATION);
					break;
				case WARN:
					tip = new ToolTip(parent, SWT.BALLOON | SWT.ICON_WARNING);
					break;
				case ERROR:
					tip = new ToolTip(parent, SWT.BALLOON | SWT.ICON_ERROR);
					break;
				default:
					tip = new ToolTip(parent, SWT.BALLOON
							| SWT.ICON_INFORMATION);
					break;
			}
			tip.setAutoHide(true);
			item.setToolTip(tip);
			tip.setMessage(message);
			tip.setText(title);
			tip.setVisible(true);
		}
		else {
			SWTUtil.secureDispose(tip);
			tip = null;
			showToolTip(title, message, type);
		}
	}
}
