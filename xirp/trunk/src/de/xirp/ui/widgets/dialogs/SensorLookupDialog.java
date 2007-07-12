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
 * SensorLookupDialog.java
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
import java.util.Collections;
import java.util.List;

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

/**
 * This dialog is used to look up sensors from 
 * a profile.
 * 
 * @author Matthias Gernand
 * 
 * @deprecated This dialog is no longer used in Xirp. The use of this dialog
 * is discouraged.
 */
@Deprecated
public final class SensorLookupDialog extends XDialog {

	/**
	 * Width of the dialog.
	 */
	private static final int WIDTH = 400;
	/**
	 * Height of the dialog.
	 */
	private static final int HEIGHT = 250;
	/**
	 * The shell of the dialog.
	 */
	private XShell dialogShell;
	/**
	 * The parent shell.
	 */
	private Shell parent;
	/**
	 * The returned profile.
	 */
	private List<String> names = new ArrayList<String>( );
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
	 * Constructs a new dialog on the parent shell.
	 * 
	 * @param parent
	 *            the parent.           
	 */
	public SensorLookupDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
	}

	/**
	 * Opens the dialog.
	 */
	public List<String> open() {
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
		dialogShell.setTextForLocaleKey("SensorLookupDialog.gui.title"); //$NON-NLS-1$
		Image image = ImageManager.getSystemImage(SystemImage.QUESTION);
		dialogShell.setImage(image);

		SWTUtil.setGridLayout(dialogShell, 2, true);

		list = new XList(dialogShell, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);

		SWTUtil.setGridData(list, true, true, SWT.FILL, SWT.FILL, 2, 1);
		// for (SensorGroupInfo sgi : infos) {
		// Vector<String> itm = new Vector<String>( );
		// CollectionUtils.addAll(itm, list.getItems( ));
		// if (!itm.contains(sgi.longName)) {
		// list.add(sgi.longName);
		// }
		// }
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

				int[] sel = list.getSelectionIndices( );

				for (int i = 0; i < list.getSelectionCount( ); i++) {
					names.add(list.getItem(sel[i]));
				}
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

		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		SWTUtil.blockDialogFromReturning(dialogShell);

		return Collections.unmodifiableList(names);
	}
}
