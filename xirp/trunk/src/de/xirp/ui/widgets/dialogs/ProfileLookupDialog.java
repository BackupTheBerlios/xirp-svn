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
 * ProfileLookupDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 16.01.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import de.xirp.profile.Profile;
import de.xirp.profile.ProfileManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XList;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.util.I18n;

/**
 * This dialog is used to look up loaded and uncompleted
 * profiles.
 * 
 * @author Matthias Gernand
 * 
 * @see de.xirp.profile.Profile
 */
public final class ProfileLookupDialog extends XDialog {

	/**
	 * Width of the dialog.
	 */
	private static final int WIDTH = 300;
	/**
	 * Height of the dialog.
	 */
	private static final int HEIGHT = 200;
	/**
	 * The shell of the dialog.
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
	 * The profile.
	 */
	private Profile profile;
	/**
	 * A list.
	 */
	private XList list;
	/**
	 * A button.
	 */
	private XButton ok;
	/**
	 * A button.
	 */
	private XButton cancel;
	/**
	 * The profiles.
	 */
	private List<Profile> profiles;

	/**
	 * Constructs a new dialog.
	 * 
	 * @param parent
	 *            the parent.
	 *            
	 * @see de.xirp.plugin.PluginType           
	 */
	public ProfileLookupDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
	}

	/**
	 * Opens the dialog.
	 * 
	 * @return The chosen profile.
	 * 
	 * @see de.xirp.profile.Profile
	 */
	public Profile open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ShellAdapter#shellClosed(org.eclipse.swt.events.ShellEvent)
			 */
			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});

		dialogShell.setSize(WIDTH, HEIGHT);
		dialogShell.setTextForLocaleKey("ProfileLookupDialog.gui.title"); //$NON-NLS-1$
		image = ImageManager.getSystemImage(SystemImage.QUESTION);
		dialogShell.setImage(image);

		SWTUtil.setGridLayout(dialogShell, 2, true);

		list = new XList(dialogShell, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL);

		SWTUtil.setGridData(list, true, true, SWT.FILL, SWT.FILL, 2, 1);
		profiles = new ArrayList<Profile>(ProfileManager.getProfiles( ));
		profiles.addAll(ProfileManager.getIncompleteProfiles( ));

		for (Profile p : profiles) {
			Vector<String> itm = new Vector<String>( );
			CollectionUtils.addAll(itm, list.getItems( ));
			if (!itm.contains(p)) {
				if (p.isComplete( )) {
					list.add(p.getName( )
							+ I18n.getString("ProfileLookupDialog.list.postfix.complete")); //$NON-NLS-1$
				}
				else {
					list.add(p.getName( )
							+ I18n.getString("ProfileLookupDialog.list.postfix.incomplete")); //$NON-NLS-1$
				}
			}
		}
		list.addSelectionListener(new SelectionAdapter( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ShellAdapter#shellClosed(org.eclipse.swt.events.ShellEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (list.getSelectionCount( ) > 0) {
					ok.setEnabled(true);
				}
				else {
					ok.setEnabled(false);
				}
			}
		});

		ok = new XButton(dialogShell, XButtonType.OK);
		ok.setEnabled(false);
		SWTUtil.setGridData(ok, true, false, SWT.FILL, SWT.CENTER, 1, 1);
		ok.addSelectionListener(new SelectionAdapter( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ShellAdapter#shellClosed(org.eclipse.swt.events.ShellEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int idx = list.getSelectionIndex( );
				profile = profiles.get(idx);
				dialogShell.close( );
			}
		});

		cancel = new XButton(dialogShell, XButtonType.CANCEL);
		SWTUtil.setGridData(cancel, true, false, SWT.FILL, SWT.CENTER, 1, 1);
		cancel.addSelectionListener(new SelectionAdapter( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ShellAdapter#shellClosed(org.eclipse.swt.events.ShellEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialogShell.close( );
			}
		});

		dialogShell.setDefaultButton(ok);
		list.setSelectionWithEvent(0);

		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		SWTUtil.blockDialogFromReturning(dialogShell);

		return profile;
	}
}
