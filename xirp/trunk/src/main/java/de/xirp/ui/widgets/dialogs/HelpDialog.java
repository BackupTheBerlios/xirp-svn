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
 * HelpDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 06.02.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;

import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XToolBar;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.util.Constants;

/**
 * This dialog shows the help files of the application and the
 * plugins.
 * 
 * @author Matthias Gernand
 */
public final class HelpDialog extends XDialog {

	/**
	 * Width of the dialog.
	 */
	private static final int WIDTH = 800;
	/**
	 * Height of the dialog.
	 */
	private static final int HEIGHT = 650;
	/**
	 * The parent shell.
	 */
	private Shell parent;
	/**
	 * The shell of the dialog.
	 */
	private XShell dialogShell;
	/**
	 * A browser widget.
	 */
	private Browser browser;
	/**
	 * A tool item.
	 */
	private ToolItem toolItemBack;
	/**
	 * A tool item.
	 */
	private ToolItem toolItemForward;
	/**
	 * A tool item.
	 */
	private ToolItem toolItemHome;

	/**
	 * Constructs a new help dialog which shows the help files of the
	 * application and the loaded plugins.
	 * 
	 * @param parent
	 *            The parent shell.
	 */
	public HelpDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
	}

	/**
	 * Opens the help dialog with the HTML-help of the application.
	 */
	public void open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL | SWT.MAX);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(browser);
				SWTUtil.secureDispose(dialogShell);
			}
		});

		dialogShell.setSize(WIDTH, HEIGHT);
		dialogShell.setTextForLocaleKey("Application.dialogTitle.help", Constants.APP_NAME, " "); //$NON-NLS-1$ //$NON-NLS-2$
		dialogShell.setImage(ImageManager.getSystemImage(SystemImage.QUESTION));

		SWTUtil.setGridLayout(dialogShell, 1, false);

		XToolBar nav = new XToolBar(dialogShell, SWT.FLAT);
		GridData gd = SWTUtil.setGridData(nav,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				1,
				1);
		gd.heightHint = 30;

		toolItemBack = new ToolItem(nav, SWT.PUSH);
		toolItemBack.setToolTipText("Navigation back"); //$NON-NLS-1$
		toolItemBack.setImage(ImageManager.getSystemImage(SystemImage.ARROW_BACK));
		toolItemBack.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.back( );
			}
		});

		toolItemForward = new ToolItem(nav, SWT.PUSH);
		toolItemForward.setToolTipText("Navigation forward"); //$NON-NLS-1$
		toolItemForward.setImage(ImageManager.getSystemImage(SystemImage.ARROW_FORWARD));
		toolItemForward.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.forward( );
			}
		});

		toolItemHome = new ToolItem(nav, SWT.PUSH);
		toolItemHome.setToolTipText("Navigation home"); //$NON-NLS-1$
		toolItemHome.setImage(ImageManager.getSystemImage(SystemImage.HOME));
		toolItemHome.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.setUrl(Constants.HELP_DIR + File.separator
						+ "index.html"); //$NON-NLS-1$
			}
		});

		browser = new Browser(dialogShell, SWT.NONE);
		browser.setUrl(Constants.HELP_DIR + File.separator + "index.html"); //$NON-NLS-1$
		SWTUtil.setGridData(browser, true, true, SWT.FILL, SWT.FILL, 1, 1);

		XComposite below = new XComposite(dialogShell, SWT.FLAT);
		gd = SWTUtil.setGridData(below, true, false, SWT.FILL, SWT.END, 1, 1);
		gd.heightHint = 35;

		XButton close = new XButton(below, XButtonType.CLOSE);
		close.setLocation(10, 5);
		close.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				dialogShell.close( );
			}
		});

		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

	}
}
