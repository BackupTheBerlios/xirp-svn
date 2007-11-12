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
 * CommSpecsLookupDialog.java
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XList;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.util.Constants;

/**
 * This dialog is used to look up comm-specs.
 * 
 * @author Matthias Gernand
 * @see de.xirp.profile.CommunicationSpecification
 */
public class CommSpecsLookupDialog extends XDialog {

	/**
	 * Width of the dialog.
	 */
	private static final int WIDTH = 300;
	/**
	 * Height of the dialog.
	 */
	private static final int HEIGHT = 200;
	/**
	 * The parent.
	 */
	private Shell parent;
	/**
	 * The dialogs shell.
	 */
	private XShell dialogShell;
	/**
	 * This list will contain the selection result.
	 */
	private List<String> commspecFileNames = new ArrayList<String>( );
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
	 * Constructs a new comm-spec look up dialog.
	 * 
	 * @param parent
	 *            The parent.
	 */
	public CommSpecsLookupDialog(Shell parent) {
		super(parent);
		this.parent = parent;
	}

	/**
	 * Opens the dialog for selection only one comm-spec.
	 * 
	 * @return The comm-spec-file file name.
	 */
	public String openSingle() {
		List<String> list = open(false);
		if (list != null && !list.isEmpty( )) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * Opens the dialog for selection multiple comm-specs.
	 * 
	 * @return The list of selected comm-spec-file file names.
	 */
	public List<String> open() {
		return open(true);
	}

	/**
	 * Opens the dialog. The flag <code>multi</code> indicates the
	 * selection mode. <code>true</code>: multi select allowed.
	 * 
	 * @param multi
	 *            <code>true</code>: multi select allowed.<br>
	 *            <code>false</code>: single select allowed.
	 * @return The selection result comm-spec-file file names.
	 */
	private List<String> open(boolean multi) {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			/**
			 * @see org.eclipse.swt.events.ShellAdapter#shellClosed(org.eclipse.swt.events.ShellEvent)
			 */
			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});

		dialogShell.setSize(WIDTH, HEIGHT);
		if (multi) {
			dialogShell.setTextForLocaleKey("CommSpecsLookupDialog.gui.title.multi"); //$NON-NLS-1$
		}
		else {
			dialogShell.setTextForLocaleKey("CommSpecsLookupDialog.gui.title.single"); //$NON-NLS-1$
		}
		Image image = ImageManager.getSystemImage(SystemImage.QUESTION);
		dialogShell.setImage(image);

		SWTUtil.setGridLayout(dialogShell, 2, true);

		if (multi) {
			list = new XList(dialogShell, SWT.BORDER | SWT.MULTI |
					SWT.H_SCROLL | SWT.V_SCROLL);
		}
		else {
			list = new XList(dialogShell, SWT.BORDER | SWT.SINGLE |
					SWT.H_SCROLL | SWT.V_SCROLL);
		}
		SWTUtil.setGridData(list, true, true, SWT.FILL, SWT.FILL, 2, 1);

		File dir = new File(Constants.CONF_COMMSPECS_DIR);
		File[] cmsFiles = dir.listFiles(new FilenameFilter( ) {

			public boolean accept(File dir, String name) {
				return name.endsWith(Constants.COMM_SPEC_POSTFIX);
			}

		});

		for (File f : cmsFiles) {
			Vector<String> itm = new Vector<String>( );
			CollectionUtils.addAll(itm, list.getItems( ));
			if (!itm.contains(f.getName( ))) {
				list.add(f.getName( ));
			}
		}
		list.addSelectionListener(new SelectionAdapter( ) {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
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

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				commspecFileNames = Arrays.asList(list.getSelection( ));
				dialogShell.close( );
			}
		});

		cancel = new XButton(dialogShell, XButtonType.CANCEL);
		SWTUtil.setGridData(cancel, true, false, SWT.FILL, SWT.CENTER, 1, 1);
		cancel.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.ShellAdapter#shellClosed(org.eclipse.swt.events.ShellEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				commspecFileNames.clear( );
				dialogShell.close( );
			}
		});

		dialogShell.setDefaultButton(ok);
		list.setSelectionWithEvent(0);

		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		SWTUtil.blockDialogFromReturning(dialogShell);

		return Collections.unmodifiableList(commspecFileNames);
	}
}
