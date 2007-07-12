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
 * EditContactDialog.java
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Shell;

import de.xirp.mail.Contact;
import de.xirp.mail.Contact.Gender;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XCombo;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XLabel;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XTextField;
import de.xirp.ui.widgets.custom.XButton.XButtonType;

/**
 * This dialog is used to edit a contact.
 * 
 * @author Matthias Gernand
 */
public class EditContactDialog extends XDialog {

	/**
	 * The height of the dialog.
	 */
	private static final int HEIGHT = 250;
	/**
	 * The width of the dialog.
	 */
	private static final int WIDTH = 350;
	/**
	 * The parent.
	 */
	private Shell parent;
	/**
	 * The dialog shell.
	 */
	private XShell dialogShell;
	/**
	 * A text field.
	 */
	private XTextField firstName;
	/**
	 * A text field.
	 */
	private XTextField lastName;
	/**
	 * A text field.
	 */
	private XTextField nickName;
	/**
	 * A text field.
	 */
	private XTextField department;
	/**
	 * A text field.
	 */
	private XTextField phone;
	/**
	 * A text field.
	 */
	private XTextField mail;
	/**
	 * A combo box.
	 */
	private XCombo gender;
	/**
	 * A button.
	 */
	private XButton ok;
	/**
	 * A button.
	 */
	private XButton cancel;
	/**
	 * The {@link de.xirp.mail.Contact contact}.
	 * 
	 * @see de.xirp.mail.Contact
	 */
	private Contact contact;
	/**
	 * A focus listener.
	 */
	private FocusListener focusListener;

	/**
	 * Constructs a new contact editor dialog.
	 * 
	 * @param parent
	 *            The parent.
	 */
	public EditContactDialog(Shell parent) {
		super(parent);
		this.parent = parent;
		initListener( );
	}

	/**
	 * Initializes a focus listener. The active text fields content is
	 * selected.
	 */
	private void initListener() {
		focusListener = new FocusListener( ) {

			public void focusGained(FocusEvent ev) {
				XTextField field = (XTextField) ev.widget;
				field.selectAll( );

			}

			public void focusLost(FocusEvent ev) {
				XTextField field = (XTextField) ev.widget;
				field.clearSelection( );
			}

		};
	}

	/**
	 * Opens the contact editor dialog for the given contact.
	 * 
	 * @param c
	 *            the contact to edit
	 * @return the edited contact
	 */
	public Contact open(Contact c) {
		this.contact = c;
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});
		dialogShell.setSize(WIDTH, HEIGHT);
		dialogShell.setTextForLocaleKey("EditContactDialog.gui.title"); //$NON-NLS-1$
		dialogShell.setImage(ImageManager.getSystemImage(SystemImage.CONTACTS));
		SWTUtil.setGridLayout(dialogShell, 2, true);

		XLabel l = new XLabel(dialogShell, SWT.NONE);
		l.setTextForLocaleKey("EditContactDialog.label.firstName"); //$NON-NLS-1$
		SWTUtil.setGridData(l, true, false, SWT.FILL, SWT.FILL, 1, 1);

		firstName = new XTextField(dialogShell, SWT.NONE);
		firstName.setText(contact.getFirstName( ));
		firstName.addFocusListener(focusListener);
		SWTUtil.setGridData(firstName, true, false, SWT.FILL, SWT.FILL, 1, 1);

		l = new XLabel(dialogShell, SWT.NONE);
		l.setTextForLocaleKey("EditContactDialog.label.lastName"); //$NON-NLS-1$
		SWTUtil.setGridData(l, true, false, SWT.FILL, SWT.FILL, 1, 1);

		lastName = new XTextField(dialogShell, SWT.NONE);
		lastName.setText(contact.getLastName( ));
		lastName.addFocusListener(focusListener);
		SWTUtil.setGridData(lastName, true, false, SWT.FILL, SWT.FILL, 1, 1);

		l = new XLabel(dialogShell, SWT.NONE);
		l.setTextForLocaleKey("EditContactDialog.label.nickName"); //$NON-NLS-1$
		SWTUtil.setGridData(l, true, false, SWT.FILL, SWT.FILL, 1, 1);

		nickName = new XTextField(dialogShell, SWT.NONE);
		nickName.setText(contact.getNickName( ));
		nickName.addFocusListener(focusListener);
		SWTUtil.setGridData(nickName, true, false, SWT.FILL, SWT.FILL, 1, 1);

		l = new XLabel(dialogShell, SWT.NONE);
		l.setTextForLocaleKey("EditContactDialog.label.department"); //$NON-NLS-1$
		SWTUtil.setGridData(l, true, false, SWT.FILL, SWT.FILL, 1, 1);

		department = new XTextField(dialogShell, SWT.NONE);
		department.setText(contact.getDepartment( ));
		department.addFocusListener(focusListener);
		SWTUtil.setGridData(department, true, false, SWT.FILL, SWT.FILL, 1, 1);

		l = new XLabel(dialogShell, SWT.NONE);
		l.setTextForLocaleKey("EditContactDialog.label.phoneNumber"); //$NON-NLS-1$
		SWTUtil.setGridData(l, true, false, SWT.FILL, SWT.FILL, 1, 1);

		phone = new XTextField(dialogShell, SWT.NONE);
		phone.setText(contact.getPhone( ));
		phone.addFocusListener(focusListener);
		SWTUtil.setGridData(phone, true, false, SWT.FILL, SWT.FILL, 1, 1);

		l = new XLabel(dialogShell, SWT.NONE);
		l.setTextForLocaleKey("EditContactDialog.label.mailAddress"); //$NON-NLS-1$
		SWTUtil.setGridData(l, true, false, SWT.FILL, SWT.FILL, 1, 1);

		mail = new XTextField(dialogShell, SWT.NONE);
		mail.setText(contact.getMail( ));
		mail.addFocusListener(focusListener);
		SWTUtil.setGridData(mail, true, false, SWT.FILL, SWT.FILL, 1, 1);

		l = new XLabel(dialogShell, SWT.NONE);
		l.setTextForLocaleKey("EditContactDialog.label.gender"); //$NON-NLS-1$
		SWTUtil.setGridData(l, true, false, SWT.FILL, SWT.FILL, 1, 1);

		gender = new XCombo(dialogShell, SWT.READ_ONLY);
		for (String key : Gender.localeNameKeys( )) {
			gender.addForLocaleKey(key);
		}
		gender.select(contact.getGender( ).localeName( ));
		SWTUtil.setGridData(gender, true, false, SWT.FILL, SWT.FILL, 1, 1);

		ok = new XButton(dialogShell, XButtonType.OK);
		ok.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				contact.setFirstName(firstName.getText( ));
				contact.setLastName(lastName.getText( ));
				contact.setGender(Gender.getGenderForTranslatedKey(gender.getItem(gender.getSelectionIndex( ))));
				contact.setDepartment(department.getText( ));
				contact.setMail(mail.getText( ));
				contact.setNickName(nickName.getText( ));
				contact.setPhone(phone.getText( ));
				dialogShell.close( );
			}

		});
		SWTUtil.setGridData(ok, true, false, SWT.FILL, SWT.FILL, 1, 1);

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
		SWTUtil.setGridData(cancel, true, false, SWT.FILL, SWT.FILL, 1, 1);

		dialogShell.layout( );
		dialogShell.pack( );
		dialogShell.setSize(WIDTH, dialogShell.getSize( ).y);

		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		SWTUtil.blockDialogFromReturning(dialogShell);

		return contact;
	}
}
