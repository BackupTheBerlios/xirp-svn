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
 * ContactDialog.java
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

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.swtplus.widgets.PList;

import de.xirp.mail.Contact;
import de.xirp.mail.MailManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.FontManager;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.*;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.ui.widgets.custom.XPList.ListType;
import de.xirp.util.Constants;
import de.xirp.util.serialization.ObjectSerializer;

/**
 * This dialog is used to edit or create contacts.
 * 
 * @author Matthias Gernand
 */
public class ContactDialog extends XDialog {

	/**
	 * The name of the font to use.
	 */
	private static final String ARIAL = "Arial"; //$NON-NLS-1$
	/**
	 * The logger for this class.
	 */
	private Logger logClass = Logger.getLogger(ContactDialog.class);
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
	private static final int HEIGHT = 400;
	/**
	 * The parent.
	 */
	private Shell parent;
	/**
	 * The dialog shell.
	 */
	private XShell dialogShell;
	/**
	 * The contact list.
	 * 
	 * @see de.xirp.mail.Contact
	 */
	private List<Contact> contacts;
	/**
	 * A list.
	 */
	private XPList pList;
	/**
	 * A styled text.
	 */
	private XStyledText nameText;
	/**
	 * A styled text.
	 */
	private XStyledText nickText;
	/**
	 * A styled text.
	 */
	private XStyledText departmentText;
	/**
	 * A styled text.
	 */
	private XStyledText phoneText;
	/**
	 * A styled text.
	 */
	private XStyledText mailText;
	/**
	 * A styled text.
	 */
	private XStyledText genderText;
	/**
	 * A button.
	 */
	private XButton edit;
	/**
	 * A button.
	 */
	private XButton newContact;
	/**
	 * A button.
	 */
	private XButton delete;
	/**
	 * The current contact.
	 * 
	 * @see de.xirp.mail.Contact
	 */
	private Contact current = null;
	/**
	 * A list item.
	 */
	private XPListItem currentItem;

	/**
	 * Constructs a new contact dialog.
	 * 
	 * @param parent
	 * 			The parent.
	 */
	public ContactDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
		contacts = MailManager.getContacts( );
	}

	/**
	 * Opens the dialog.
	 */
	public void open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});
		dialogShell.setSize(WIDTH, HEIGHT);
		dialogShell.setTextForLocaleKey("ContactDialog.gui.title.manageContacts"); //$NON-NLS-1$
		dialogShell.setImage(ImageManager.getSystemImage(SystemImage.CONTACTS));
		SWTUtil.setGridLayout(dialogShell, 3, true);

		pList = new XPList(dialogShell,
				PList.BORDER | PList.SINGLE,
				ListType.TITLE_AND_DESC);
		SWTUtil.setGridData(pList, true, true, SWT.FILL, SWT.FILL, 1, 2);
		pList.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setContactInfo((Contact) e.item.getData(CONTACT));
				currentItem = (XPListItem) e.item;
			}

		});

		XComposite info = new XComposite(dialogShell, SWT.NONE);
		SWTUtil.setGridData(info, true, true, SWT.FILL, SWT.FILL, 2, 1);
		SWTUtil.setGridLayout(info, 2, false);

		nameText = new XStyledText(info, SWT.WRAP, true);
		nameText.setEnabled(false);
		nameText.setEditable(false);
		nameText.setWordWrap(true);
		nameText.setFont(FontManager.getFont(ARIAL, 16, SWT.BOLD));
		nameText.setVisible(false);
		SWTUtil.setGridData(nameText,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				2,
				1);

		nickText = new XStyledText(info, SWT.WRAP, true);
		nickText.setEnabled(false);
		nickText.setEditable(false);
		nickText.setWordWrap(true);
		nickText.setFont(FontManager.getFont(ARIAL, 12, SWT.BOLD));
		nickText.setVisible(false);
		SWTUtil.setGridData(nickText,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				2,
				1);

		departmentText = new XStyledText(info, SWT.WRAP, true);
		departmentText.setEnabled(false);
		departmentText.setEditable(false);
		departmentText.setWordWrap(true);
		departmentText.setFont(FontManager.getFont(ARIAL, 10, SWT.NORMAL));
		departmentText.setVisible(false);
		SWTUtil.setGridData(departmentText,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				2,
				1);

		phoneText = new XStyledText(info, SWT.WRAP, true);
		phoneText.setEnabled(false);
		phoneText.setEditable(false);
		phoneText.setWordWrap(true);
		phoneText.setFont(FontManager.getFont(ARIAL, 10, SWT.NORMAL));
		phoneText.setVisible(false);
		SWTUtil.setGridData(phoneText,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				2,
				1);

		mailText = new XStyledText(info, SWT.WRAP, true);
		mailText.setEnabled(false);
		mailText.setEditable(false);
		mailText.setWordWrap(true);
		mailText.setFont(FontManager.getFont(ARIAL, 10, SWT.NORMAL));
		mailText.setVisible(false);
		SWTUtil.setGridData(mailText,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				2,
				1);

		genderText = new XStyledText(info, SWT.WRAP, true);
		genderText.setEnabled(false);
		genderText.setEditable(false);
		genderText.setWordWrap(true);
		genderText.setFont(FontManager.getFont(ARIAL, 10, SWT.NORMAL));
		genderText.setVisible(false);
		SWTUtil.setGridData(genderText,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				2,
				1);

		info = new XComposite(dialogShell, SWT.NONE);
		SWTUtil.setGridData(info, true, false, SWT.FILL, SWT.END, 2, 1);
		SWTUtil.setGridLayout(info, 4, true);

		newContact = new XButton(info);
		newContact.setTextForLocaleKey("ContactDialog.button.newContact"); //$NON-NLS-1$
		newContact.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Contact aux = new Contact( );

					Contact clone = ObjectSerializer.<Contact> deepCopy(aux);

					EditContactDialog dialog = new EditContactDialog(dialogShell);
					if (!clone.equals(dialog.open(aux))) {
						MailManager.addContact(aux);
						addItem(aux);
						pList.redraw( );
					}
				}
				catch (IOException ex) {
					logClass.error("Error: " + ex.getMessage( ) + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$
				}
			}
		});
		SWTUtil.setGridData(newContact, true, true, SWT.FILL, SWT.FILL, 1, 1);

		edit = new XButton(info);
		edit.setTextForLocaleKey("ContactDialog.button.edit"); //$NON-NLS-1$
		edit.setToolTipTextForLocaleKey("ContactDialog.button.tooltip.editContact"); //$NON-NLS-1$
		edit.setEnabled(false);
		edit.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Contact clone = ObjectSerializer.<Contact> deepCopy(current);

					EditContactDialog dialog = new EditContactDialog(dialogShell);
					Contact aux = dialog.open(current);
					if (!clone.equals(aux)) {
						MailManager.deleteContact(current);
						MailManager.addContact(aux);
						updateItem(currentItem, aux);
						pList.redraw( );
					}
				}
				catch (IOException ex) {
					logClass.error("Error: " + ex.getMessage( ) + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$
				}
			}
		});
		SWTUtil.setGridData(edit, true, true, SWT.FILL, SWT.FILL, 1, 1);

		delete = new XButton(info);
		delete.setTextForLocaleKey("ContactDialog.button.delete"); //$NON-NLS-1$
		delete.setEnabled(false);
		delete.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				MailManager.deleteContact(current);
				SWTUtil.secureDispose(currentItem);
				pList.redraw( );
				delete.setEnabled(false);
				setContactInfo(null);
			}

		});
		SWTUtil.setGridData(delete, true, true, SWT.FILL, SWT.FILL, 1, 1);

		XButton close = new XButton(info, XButtonType.CLOSE);
		close.addSelectionListener(new SelectionAdapter( ) {

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
		SWTUtil.setGridData(close, true, true, SWT.FILL, SWT.FILL, 1, 1);

		for (Contact c : contacts) {
			addItem(c);
		}
		pList.redraw( );

		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		SWTUtil.blockDialogFromReturning(dialogShell);

		return;
	}

	/**
	 * Updates the list item for the given item and contact.
	 * 
	 * @param item
	 * 			The item to set.
	 * @param c
	 * 			The contact.
	 * 
	 * @see de.xirp.mail.Contact
	 */
	private void updateItem(XPListItem item, Contact c) {
		item.setText(c.getLastName( ) + ", " + c.getFirstName( )); //$NON-NLS-1$
		item.setDescriptionForLocaleKey("ContactDialog.item.mailPhone", //$NON-NLS-1$
				(c.getMail( ) + Constants.LINE_SEPARATOR),
				c.getPhone( ));
		Image img;
		switch (c.getGender( )) {
			case MALE:
				img = ImageManager.getSystemImage(SystemImage.MALE);
				break;
			case FEMALE:
				img = ImageManager.getSystemImage(SystemImage.FEMALE);
				break;
			default:
				img = ImageManager.getSystemImage(SystemImage.UNDEF_GENDER);
		}
		item.setImage(img);
		item.setData(CONTACT, c);
		setContactInfo(c);
	}

	/**
	 * Adds the given 
	 * {@link de.xirp.mail.Contact contact}.
	 * 
	 * @param c
	 * 			The contact to add.
	 */
	private void addItem(Contact c) {
		XPListItem itm = new XPListItem(pList, SWT.NONE);
		itm.setText(c.getLastName( ) + ", " + c.getFirstName( )); //$NON-NLS-1$
		itm.setDescriptionForLocaleKey("ContactDialog.item.mailPhone", //$NON-NLS-1$
				(c.getMail( ) + Constants.LINE_SEPARATOR),
				c.getPhone( ));

		Listener list = new Listener( ) {

			public void handleEvent(Event event) {
				// TODO
			}
		};
		// pList.addListener(SWT.MouseDoubleClick, list);
		pList.addListener(SWT.MouseDown, list);
		// pList.addListener(SWT.MouseEnter, list);
		// pList.addListener(SWT.MouseExit, list);
		// pList.addListener(SWT.MouseHover, list);
		// pList.addListener(SWT.MouseMove, list);
		pList.addListener(SWT.MouseUp, list);
		// pList.addListener(SWT.Selection, list);

		Image img;
		switch (c.getGender( )) {
			case MALE:
				img = ImageManager.getSystemImage(SystemImage.MALE);
				break;
			case FEMALE:
				img = ImageManager.getSystemImage(SystemImage.FEMALE);
				break;
			default:
				img = ImageManager.getSystemImage(SystemImage.UNDEF_GENDER);
		}
		itm.setImage(img);
		itm.setData(CONTACT, c);
	}

	/**
	 * Sets the info about the selected 
	 * {@link de.xirp.mail.Contact contact} from
	 * the data of the given
	 * {@link de.xirp.mail.Contact contact}.
	 * 
	 * @param c
	 * 			The contact.
	 * 
	 * @see de.xirp.mail.Contact contact
	 */
	private void setContactInfo(Contact c) {
		if (c == null) {
			current = c;
			nameText.setText(""); //$NON-NLS-1$
			nickText.setText(""); //$NON-NLS-1$
			departmentText.setText(""); //$NON-NLS-1$
			phoneText.setText(""); //$NON-NLS-1$
			mailText.setText(""); //$NON-NLS-1$
			genderText.setText(""); //$NON-NLS-1$
		}
		else {
			current = c;
			nameText.setText(c.getFirstName( ) + " " + c.getLastName( )); //$NON-NLS-1$
			nickText.setTextForLocaleKey("ContactDialog.internal.nick.aka", c.getNickName( )); //$NON-NLS-1$
			departmentText.setTextForLocaleKey("ContactDialog.internal.department.workingAt", c.getDepartment( )); //$NON-NLS-1$
			phoneText.setTextForLocaleKey("ContactDialog.internal.phone.phoneNumber", c.getPhone( )); //$NON-NLS-1$
			mailText.setTextForLocaleKey("ContactDialog.internal.mail.eMailAddress", c.getMail( )); //$NON-NLS-1$
			genderText.setTextForLocaleKey("ContactDialog.internal.gender.gender", c.getGender( ).localeName( )); //$NON-NLS-1$

			delete.setEnabled(true);
			edit.setEnabled(true);
			genderText.setVisible(true);
			mailText.setVisible(true);
			phoneText.setVisible(true);
			departmentText.setVisible(true);
			nickText.setVisible(true);
			nameText.setVisible(true);
		}
	}
}
