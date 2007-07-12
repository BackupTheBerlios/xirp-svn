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
 * RobotDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 25.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Shell;

import de.xirp.profile.Robot;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XImageLabel;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.util.FutureRelease;

/**
 * This is alpha API, may be removed in the future or is planed to be
 * integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 */
@FutureRelease(version = "3.0.0")
public class RobotDialog extends XDialog {

	/**
	 * The logger for this class.
	 */
	// private static final Logger logClass =
	// Logger.getLogger(RobotDialog.class);
	private static final int HEIGHT = 300;
	private static final int WIDTH = 350;
	private Shell parent;
	private XShell dialogShell;
	private boolean edit = false;
	private Robot robot;
	private XImageLabel il;

	/**
	 * Constructs an new 'edit robot' dialog.
	 * 
	 * @param parent
	 *            The parent shell.
	 * @param robot
	 *            The robot to edit.
	 */
	public RobotDialog(Shell parent, Robot robot) {
		super(parent, SWT.DIALOG_TRIM | SWT.NO_TRIM);
		this.parent = parent;
		this.edit = true;
		this.robot = robot;
	}

	/**
	 * Constructs an new 'new robot' dialog.
	 * 
	 * @param parent
	 *            The parent shell.
	 */
	public RobotDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.NO_TRIM);
		this.parent = parent;
		this.edit = false;
		this.robot = new Robot( );
	}

	public void open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});

		if (edit) {
			dialogShell.setTextForLocaleKey("RobotDialog.gui.title.edit"); //$NON-NLS-1$
			dialogShell.setImage(ImageManager.getSystemImage(SystemImage.EDIT_ROBOT));
		}
		else {
			dialogShell.setTextForLocaleKey("RobotDialog.gui.title.new"); //$NON-NLS-1$
			dialogShell.setImage(ImageManager.getSystemImage(SystemImage.NEW_ROBOT));
		}
		dialogShell.setSize(WIDTH, HEIGHT);
		SWTUtil.setGridLayout(dialogShell, 3, true);

		il = new XImageLabel(dialogShell, SWT.NONE);
		SWTUtil.setGridData(il, true, false, SWT.FILL, SWT.BEGINNING, 3, 1);

		// TODO: ...

		XButton ok = new XButton(dialogShell, XButtonType.OK);
		SWTUtil.setGridData(ok, true, false, SWT.FILL, SWT.FILL, 1, 1);
		ok.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent ev) {
				save( );
				dialogShell.close( );
			}

		});

		XButton save = new XButton(dialogShell, XButtonType.SAVE);
		SWTUtil.setGridData(save, true, false, SWT.FILL, SWT.FILL, 1, 1);
		save.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent ev) {
				save( );
			}

		});

		XButton cancel = new XButton(dialogShell, XButtonType.CANCEL);
		SWTUtil.setGridData(cancel, true, false, SWT.FILL, SWT.FILL, 1, 1);
		cancel.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent ev) {
				dialogShell.close( );
			}

		});

		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		if (edit) {
			loadRobotData( );
		}

		SWTUtil.blockDialogFromReturning(dialogShell);

	}

	private void save() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	private void loadRobotData() {
		il.setDone(robot.isComplete( ));

	}

}
