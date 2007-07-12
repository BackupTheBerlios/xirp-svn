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
 * CommSpecDialog.java
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

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Shell;

import de.xirp.plugin.PluginType;
import de.xirp.profile.CommunicationInterface;
import de.xirp.profile.CommunicationSpecification;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XGroup;
import de.xirp.ui.widgets.custom.XImageLabel;
import de.xirp.ui.widgets.custom.XList;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.util.FutureRelease;

/**
 * This is alpha API, may be removed in the future or
 * is planed to be integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 */
@FutureRelease(version = "3.0.0")
public class CommSpecDialog extends XDialog {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(CommSpecDialog.class);
	private static final int HEIGHT = 500;
	private static final int WIDTH = 650;
	private Shell parent;
	private XShell dialogShell;
	private boolean edit = false;
	private CommunicationSpecification commSpecs;
	private XImageLabel il;
	private XList commInterfaces;
	private XList options;
	private XButton addInterface;
	private XButton removeInterface;

	/**
	 * Constructs an new 'edit comm specs' dialog
	 * 
	 * @param parent
	 *            The parent shell
	 * @param commSpecs
	 *            The commSpecs to edit.
	 */
	public CommSpecDialog(Shell parent, CommunicationSpecification commSpecs) {
		super(parent, SWT.DIALOG_TRIM | SWT.NO_TRIM);
		this.parent = parent;
		this.edit = true;
		this.commSpecs = commSpecs;
	}

	/**
	 * Constructs an new 'new comm specs' dialog.
	 * 
	 * @param parent
	 *            The parent shell
	 */
	public CommSpecDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.NO_TRIM);
		this.parent = parent;
		this.edit = false;
		this.commSpecs = new CommunicationSpecification( );
	}

	public void open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});

		if (edit) {
			dialogShell.setTextForLocaleKey("CommSpecDialog.gui.title.edit"); //$NON-NLS-1$
			dialogShell.setImage(ImageManager.getSystemImage(SystemImage.EDIT_COMM_SPEC));
		}
		else {
			dialogShell.setTextForLocaleKey("CommSpecDialog.gui.title.new"); //$NON-NLS-1$
			dialogShell.setImage(ImageManager.getSystemImage(SystemImage.NEW_COMM_SPEC));
		}
		dialogShell.setSize(WIDTH, HEIGHT);
		SWTUtil.setGridLayout(dialogShell, 3, true);

		il = new XImageLabel(dialogShell, SWT.NONE);
		SWTUtil.setGridData(il, true, false, SWT.FILL, SWT.BEGINNING, 3, 1);

		XGroup interfaces = new XGroup(dialogShell, SWT.NONE);
		interfaces.setTextForLocaleKey("Comm interfaces"); //$NON-NLS-1$
		SWTUtil.setGridData(interfaces, true, true, SWT.FILL, SWT.FILL, 3, 1);
		SWTUtil.setGridLayout(interfaces, 2, true);

		commInterfaces = new XList(interfaces, SWT.BORDER | SWT.SINGLE
				| SWT.V_SCROLL);
		SWTUtil.setGridData(commInterfaces,
				true,
				true,
				SWT.FILL,
				SWT.FILL,
				1,
				1);
		commInterfaces.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent ev) {
				XList l = (XList) ev.widget;
				if (l.getSelectionCount( ) > 1) {
					removeInterface.setEnabled(true);
				}
				else {
					removeInterface.setEnabled(false);
				}
			}

		});

		options = new XList(interfaces, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		SWTUtil.setGridData(options, true, true, SWT.FILL, SWT.FILL, 1, 1);

		addInterface = new XButton(interfaces);
		addInterface.setTextForLocaleKey("Add interface"); //$NON-NLS-1$
		SWTUtil.setGridData(addInterface, true, false, SWT.FILL, SWT.FILL, 1, 1);
		addInterface.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent ev) {
				PluginLookupDialog dialog = new PluginLookupDialog(dialogShell,
						PluginType.COMMUNICATION,
						false);
				List<String> result = dialog.open( );
				if (result != null && !result.isEmpty( )) {
					for (String s : result) {
						CommunicationInterface ci = new CommunicationInterface( );
						ci.setClassName(s);
						commSpecs.addInterface(ci);
					}
				}
				commInterfaces.removeAll( );
				loadCommSpecData( );
			}

		});

		removeInterface = new XButton(interfaces);
		removeInterface.setEnabled(false);
		removeInterface.setTextForLocaleKey("Remove interface class"); //$NON-NLS-1$
		SWTUtil.setGridData(removeInterface,
				true,
				false,
				SWT.FILL,
				SWT.FILL,
				1,
				1);
		removeInterface.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent ev) {

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
			loadCommSpecData( );
		}

		SWTUtil.blockDialogFromReturning(dialogShell);

	}

	private void save() {
		if (edit) {
			// ProfileGenerator.generateCMS(commSpecs,
			// commSpecs.getCmsFile( ));
		}
		else {
			// ProfileGenerator.generateCMS(commSpecs,
			// commSpecs.getCmsFile( ));
		}
	}

	/**
	 * 
	 */
	private void loadCommSpecData() {
		il.setDone(commSpecs.isComplete( ));
		for (CommunicationInterface i : commSpecs.getInterfaces( )) {
			commInterfaces.add(i.getClassName( ));
		}
	}

}
