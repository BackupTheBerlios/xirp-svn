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
 * ProfileDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 02.03.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Shell;

import de.xirp.profile.Profile;
import de.xirp.profile.ProfileGenerator;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XGroup;
import de.xirp.ui.widgets.custom.XImageLabel;
import de.xirp.ui.widgets.custom.XLabel;
import de.xirp.ui.widgets.custom.XList;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XTextField;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.ui.widgets.dialogs.XMessageBox.HMessageBoxType;
import de.xirp.util.Constants;
import de.xirp.util.Util;

/**
 * This dialog is used to create new and edit profiles.
 * 
 * @author Matthias Gernand
 */
public final class ProfileDialog extends XDialog {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(ProfileDialog.class);
	/**
	 * The height of the dialog.
	 */
	private static final int HEIGHT = 300;
	/**
	 * The width of the dialog.
	 */
	private static final int WIDTH = 350;
	/**
	 * The dialogs shell.
	 */
	private XShell dialogShell;
	/**
	 * The parent shell.
	 */
	private Shell parent;
	/**
	 * A flag indicating if the profile should be edited.
	 */
	private boolean edit = false;
	/**
	 * The profile to create of edit.
	 * 
	 * @see de.xirp.profile.Profile
	 */
	private Profile profile = null;
	/**
	 * A text field.
	 */
	private XTextField name;
	/**
	 * A image label.
	 * 
	 * @see de.xirp.ui.widgets.custom.XImageLabel
	 */
	private XImageLabel il;
	/**
	 * A list.
	 */
	private XList bots;
	/**
	 * A button.
	 */
	private XButton remove;

	/**
	 * Constructs an new 'edit profile' dialog.
	 * 
	 * @param parent
	 *            The parent shell.
	 * @param profile
	 *            The .pro file to edit.
	 */
	public ProfileDialog(Shell parent, Profile profile) {
		super(parent, SWT.DIALOG_TRIM | SWT.NO_TRIM);
		this.parent = parent;
		this.edit = true;
		this.profile = profile;
	}

	/**
	 * Constructs an new 'new profile' dialog.
	 * 
	 * @param parent
	 *            The parent shell.
	 */
	public ProfileDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.NO_TRIM);
		this.parent = parent;
		this.edit = false;
		this.profile = new Profile( );
	}

	/**
	 * Opens the dialog.
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

		if (edit) {
			dialogShell.setTextForLocaleKey("ProfileDialog.gui.title.edit"); //$NON-NLS-1$
			dialogShell.setImage(ImageManager.getSystemImage(SystemImage.PROFILE_EDIT));
		}
		else {
			dialogShell.setTextForLocaleKey("ProfileDialog.gui.title.new"); //$NON-NLS-1$
			dialogShell.setImage(ImageManager.getSystemImage(SystemImage.PROFILE_NEW));
		}
		dialogShell.setSize(WIDTH, HEIGHT);
		SWTUtil.setGridLayout(dialogShell, 3, true);

		il = new XImageLabel(dialogShell, SWT.NONE);
		SWTUtil.setGridData(il, true, false, SWT.FILL, SWT.BEGINNING, 3, 1);

		XGroup general = new XGroup(dialogShell, SWT.NONE);
		general.setTextForLocaleKey("ProfileDialog.group.general"); //$NON-NLS-1$
		SWTUtil.setGridData(general, true, true, SWT.FILL, SWT.FILL, 3, 1);
		SWTUtil.setGridLayout(general, 2, true);

		XLabel l = new XLabel(general, SWT.NONE);
		l.setTextForLocaleKey("ProfileDialog.label.nameOfProfile"); //$NON-NLS-1$
		SWTUtil.setGridData(l, true, false, SWT.FILL, SWT.CENTER, 1, 1);

		name = new XTextField(general, SWT.BORDER);
		SWTUtil.setGridData(name, true, false, SWT.FILL, SWT.CENTER, 1, 1);
		name.addModifyListener(new ModifyListener( ) {

			public void modifyText(ModifyEvent eve) {
				il.setDone(isCompleted( ));
			}

		});

		XGroup robots = new XGroup(dialogShell, SWT.NONE);
		robots.setTextForLocaleKey("ProfileDialog.group.robotFiles"); //$NON-NLS-1$
		SWTUtil.setGridData(robots, true, true, SWT.FILL, SWT.FILL, 3, 1);
		SWTUtil.setGridLayout(robots, 2, true);

		bots = new XList(robots, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL |
				SWT.V_SCROLL);
		SWTUtil.setGridData(bots, true, true, SWT.FILL, SWT.FILL, 2, 1);
		bots.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent ev) {
				if (bots.getSelectionCount( ) > 0) {
					remove.setEnabled(true);
				}
				else {
					remove.setEnabled(false);
				}
				il.setDone(isCompleted( ));
			}

		});

		XButton add = new XButton(robots);
		add.setTextForLocaleKey("ProfileDialog.button.add"); //$NON-NLS-1$
		SWTUtil.setGridData(add, true, false, SWT.FILL, SWT.FILL, 1, 1);
		add.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent ev) {
				RobotLookupDialog dialog = new RobotLookupDialog(dialogShell);
				List<String> names = dialog.open( );
				if (names != null) {
					for (String name : names) {
						bots.add(name);
					}
				}
				il.setDone(isCompleted( ));
			}

		});

		remove = new XButton(robots);
		remove.setEnabled(false);
		remove.setTextForLocaleKey("ProfileDialog.button.remove"); //$NON-NLS-1$
		SWTUtil.setGridData(remove, true, false, SWT.FILL, SWT.FILL, 1, 1);
		remove.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent ev) {
				bots.remove(bots.getSelectionIndices( ));
				il.setDone(isCompleted( ));
			}

		});

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
			loadProfileData( );
		}

		SWTUtil.blockDialogFromReturning(dialogShell);
	}

	/**
	 * Saved the {@link de.xirp.profile.Profile profile}.
	 * 
	 * @see de.xirp.profile.Profile
	 */
	private void save() {

		putValues( );

		XMessageBox box = new XMessageBox(dialogShell,
				HMessageBoxType.ERROR,
				XButtonType.CLOSE);
		box.setTextForLocaleKey("ProfileDialog.messagebox.title.errorSaving"); //$NON-NLS-1$
		if (edit) {
			try {
				ProfileGenerator.generatePRO(profile, profile.getProFile( ));
			}
			catch (Exception e) {
				logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
				box.setMessageForLocaleKey("ProfileDialog.messagebox.message.errorSaving", Constants.LINE_SEPARATOR, e.getMessage( )); //$NON-NLS-1$
				box.open( );
			}
		}
		else {
			InputDialog dialog = new InputDialog(dialogShell,
					"ProfileDialog.dialog.input.fileNameOfProfile"); //$NON-NLS-1$
			List<String> name = dialog.open( );
			if (name != null && !name.isEmpty( )) {
				File file = new File(Constants.CONF_PROFILES_DIR, name.get(0) +
						Constants.PROFILE_POSTFIX);
				profile.setProFile(file);
				try {
					ProfileGenerator.generatePRO(profile, file);
				}
				catch (Exception e) {
					logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
					box.setMessageForLocaleKey("ProfileDialog.messagebox.message.errorSaving", Constants.LINE_SEPARATOR, e.getMessage( )); //$NON-NLS-1$
					box.open( );
				}
			}
		}
	}

	/**
	 * Sets the entered values to the
	 * {@link de.xirp.profile.Profile profile}.
	 * 
	 * @see de.xirp.profile.Profile
	 */
	private void putValues() {
		profile.setName(name.getText( ));
		for (String name : bots.getItems( )) {
			profile.addRobotFileName(StringUtils.substringBeforeLast(name, ".")); //$NON-NLS-1$
		}
		profile.setComplete(isCompleted( ));
	}

	/**
	 * Returns <code>true</code> if the profile is completed.
	 * 
	 * @return <code>true> if the profile is completed.
	 */
	private boolean isCompleted() {
		boolean b = true;
		b = !Util.isEmpty(name.getText( )) & b;
		b = (bots.getItemCount( ) > 0) & b;
		return b;
	}

	/**
	 * Loads the data of the profile to the dialog ui.
	 */
	private void loadProfileData() {
		name.setText(profile.getName( ));
		il.setDone(profile.isComplete( ));
		for (String name : profile.getRobotFileNames( )) {
			bots.add(name + Constants.ROBOT_POSTFIX);
		}
	}
}
