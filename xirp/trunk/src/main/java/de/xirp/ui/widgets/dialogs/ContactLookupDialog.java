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
 * ContactLookupDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.02.2007:		Created by Matthias Gernand.
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import de.xirp.mail.Contact;
import de.xirp.mail.MailManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XTable;
import de.xirp.ui.widgets.custom.XTableColumn;
import de.xirp.ui.widgets.custom.XTableItem;
import de.xirp.ui.widgets.custom.XButton.XButtonType;

/**
 * This dialog is used to lookup saved contacts.
 * 
 * @author Matthias Gernand
 */
public class ContactLookupDialog extends XDialog {

	/**
	 * A string constant.
	 */
	private static final String CONTACT = "contact"; //$NON-NLS-1$
	/**
	 * The width of the dialog.
	 */
	private static final int WIDTH = 600;
	/**
	 * The height of the dialog.
	 */
	private static final int HEIGHT = 300;
	/**
	 * The parent.
	 */
	private Shell parent;
	/**
	 * The dialog shell.
	 */
	private XShell dialogShell;
	/**
	 * The {@link de.xirp.mail.Contact contact} list.
	 * 
	 * @see de.xirp.mail.Contact
	 */
	private List<Contact> contacts = Collections.emptyList( );
	/**
	 * A button.
	 */
	private XButton ok;
	/**
	 * A table.
	 */
	private XTable table;
	/**
	 * A button.
	 */
	private XButton cancel;

	/**
	 * Constructs a new contact lookup dialog.
	 * 
	 * @param parent
	 * 			The parent.
	 */
	public ContactLookupDialog(Shell parent) {
		super(parent);
		this.parent = parent;
	}

	/**
	 * Opens the dialog.
	 * 
	 * @return The selected contacts.
	 */
	public List<Contact> open() {
		contacts = new ArrayList<Contact>( );
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});
		dialogShell.setSize(WIDTH, HEIGHT);
		dialogShell.setTextForLocaleKey("ContactLookupDialog.gui.title"); //$NON-NLS-1$
		dialogShell.setImage(ImageManager.getSystemImage(SystemImage.CONTACTS));
		SWTUtil.setGridLayout(dialogShell, 2, true);

		table = new XTable(dialogShell, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION | SWT.V_SCROLL);
		SWTUtil.setGridData(table, true, true, SWT.FILL, SWT.FILL, 2, 1);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] titles = {
				"ContactLookupDialog.table.column.firstName", "ContactLookupDialog.table.column.lastName", "ContactLookupDialog.table.column.nickName", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"ContactLookupDialog.table.column.department", "ContactLookupDialog.table.column.phone", "ContactLookupDialog.table.column.eMail", "ContactLookupDialog.table.column.gender"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		for (String t : titles) {
			XTableColumn column = new XTableColumn(table, SWT.NONE);
			column.setTextForLocaleKey(t);
			column.pack( );
		}

		for (Contact c : MailManager.getContacts( )) {
			XTableItem itm = new XTableItem(table, SWT.NONE);
			String[] str = new String[] {c.getFirstName( ), c.getLastName( ),
					c.getNickName( ), c.getDepartment( ), c.getPhone( ),
					c.getMail( ), c.getGender( ).localeName( )};
			itm.setText(str);
			itm.setData(CONTACT, c);
		}

		SWTUtil.packTable(table);

		ok = new XButton(dialogShell, XButtonType.OK);
		ok.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TableItem itm : table.getSelection( )) {
					contacts.add((Contact) itm.getData(CONTACT));
				}
				dialogShell.close( );
			}

		});
		SWTUtil.setGridData(ok, true, false, SWT.FILL, SWT.END, 1, 1);

		cancel = new XButton(dialogShell, XButtonType.CANCEL);
		cancel.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialogShell.close( );
			}

		});
		SWTUtil.setGridData(cancel, true, false, SWT.FILL, SWT.END, 1, 1);

		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		SWTUtil.blockDialogFromReturning(dialogShell);

		return Collections.unmodifiableList(contacts);
	}

}
