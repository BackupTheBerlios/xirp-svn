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
 * MailDialog.java
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
import java.util.Collection;

import javax.mail.MessagingException;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.swtplus.widgets.PList;

import de.xirp.mail.AttachmentDescriptor;
import de.xirp.mail.Contact;
import de.xirp.mail.Mail;
import de.xirp.mail.MailDescriptor;
import de.xirp.mail.MailManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.*;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.ui.widgets.custom.XPList.ListType;
import de.xirp.ui.widgets.dialogs.XMessageBox.HMessageBoxType;
import de.xirp.util.Constants;
import de.xirp.util.Util;
import de.xirp.util.serialization.ObjectSerializer;
import de.xirp.util.serialization.SerializationException;

/**
 * This dialog is used to present the send/not-send mails, and
 * to invoke the mail editor/contact editor from here. It is
 * also possible to export and print mails from within this dialog.
 * 
 * @author Matthias Gernand
 */
public class MailDialog extends XDialog {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(MailDialog.class);
	/**
	 * A string constant.
	 */
	private static final String ATTACHMENT_DESCRIPTOR = "attachmentdescriptor"; //$NON-NLS-1$
	/**
	 * A string constant.
	 */
	private static final String MAIL_DESCRIPTOR = "maildescriptor"; //$NON-NLS-1$
	/**
	 * The width of the dialog.
	 */
	private static final int WIDTH = 800;
	/**
	 * The height of the dialog.
	 */
	private static final int HEIGHT = 600;
	/**
	 * The parent.
	 */
	private Shell parent;
	/**
	 * The dialogs shell.
	 */
	private XShell dialogShell;
	/**
	 * A table.
	 */
	private XTable table;
	/**
	 * A text area.
	 */
	private XText mailText;
	/**
	 * A list.
	 */
	private XPList pList;
	/**
	 * The current attachment 
	 * {@link de.xirp.mail.AttachmentDescriptor descriptor}.
	 * 
	 * @see de.xirp.mail.AttachmentDescriptor
	 */
	@SuppressWarnings("unused")
	private AttachmentDescriptor currentAttachmentDescriptor;
	/**
	 * The current mail 
	 * {@link de.xirp.mail.MailDescriptor descriptor}.
	 * 
	 * @see de.xirp.mail.MailDescriptor
	 */
	private MailDescriptor currentMailDescriptor;
	/**
	 * A collection of mail 
	 * {@link de.xirp.mail.MailDescriptor descriptors}.
	 * 
	 * @see de.xirp.mail.MailDescriptor
	 */
	private Collection<MailDescriptor> mailDescriptors;
	/**
	 * A tool item.
	 */
	private XToolItem deleteMails;
	/**
	 * A tool item.
	 */
	private XToolItem saveMails;
	/**
	 * A tool item.
	 */
	private XToolItem printMails;
	/**
	 * A tool item.
	 */
	private XToolItem forwardMail;
	/**
	 * A tool item.
	 */
	private XToolItem manageContacts;
	/**
	 * A tool item.
	 */
	private XToolItem addContact;

	/**
	 * Constructs a new mail dialog.
	 * 
	 * @param parent
	 * 			The parent.
	 */
	public MailDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
		mailDescriptors = MailManager.getMailDescriptors( );
	}

	/**
	 * Opens the dialog.
	 */
	public void open() {
		try {
			MailManager.checkSettings( );
			dialogShell = new XShell(parent, SWT.DIALOG_TRIM
					| SWT.APPLICATION_MODAL);

			dialogShell.addShellListener(new ShellAdapter( ) {

				@Override
				public void shellClosed(ShellEvent e) {
					SWTUtil.secureDispose(dialogShell);
				}
			});
			dialogShell.setSize(WIDTH, HEIGHT);
			dialogShell.setTextForLocaleKey("MailDialog.gui.title"); //$NON-NLS-1$
			dialogShell.setImage(ImageManager.getSystemImage(SystemImage.SEND_MAIL));
			SWTUtil.setGridLayout(dialogShell, 4, true);

			XToolBar toolBar = new XToolBar(dialogShell, SWT.FLAT);

			XToolItem newMail = new XToolItem(toolBar, SWT.PUSH);
			newMail.setToolTipTextForLocaleKey("MailDialog.tool.tooltip.newMail"); //$NON-NLS-1$
			newMail.setImage(ImageManager.getSystemImage(SystemImage.SEND_MAIL));
			newMail.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							ComposeMailDialog dialog = new ComposeMailDialog(dialogShell);
							dialog.open( );
							mailDescriptors = MailManager.getMailDescriptors( );
							table.removeAll( );
							setTableContent( );
							SWTUtil.packTable(table);
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			forwardMail = new XToolItem(toolBar, SWT.PUSH);
			forwardMail.setEnabled(false);
			forwardMail.setToolTipTextForLocaleKey("MailDialog.tool.tooltip.forwardMail"); //$NON-NLS-1$
			forwardMail.setImage(ImageManager.getSystemImage(SystemImage.FORWARD_MAIL));
			forwardMail.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							ComposeMailDialog dialog = new ComposeMailDialog(dialogShell);
							Mail mail;
							try {
								mail = MailManager.getMail(currentMailDescriptor);
								dialog.open(mail);
								table.removeAll( );
								setTableContent( );
								SWTUtil.packTable(table);
							}
							catch (Exception ex) {
								XMessageBox box = new XMessageBox(dialogShell,
										HMessageBoxType.ERROR,
										XButtonType.CLOSE);
								box.setTextForLocaleKey("MailDialog.messagebox.title.errorReading"); //$NON-NLS-1$
								box.setMessageForLocaleKey("MailDialog.messagebox.message.couldNotOpen", //$NON-NLS-1$
										Constants.LINE_SEPARATOR,
										ex.getMessage( ));
								box.open( );
							}
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			new XToolItem(toolBar, SWT.SEPARATOR);

			printMails = new XToolItem(toolBar, SWT.PUSH);
			printMails.setToolTipTextForLocaleKey("MailDialog.tool.tooltip.printMail"); //$NON-NLS-1$
			printMails.setEnabled(false);
			printMails.setImage(ImageManager.getSystemImage(SystemImage.PRINT));
			printMails.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							try {
								MailManager.printMail(currentMailDescriptor);
							}
							catch (MessagingException e) {
								logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
							}
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			saveMails = new XToolItem(toolBar, SWT.PUSH);
			saveMails.setToolTipTextForLocaleKey("MailDialog.tool.tooltip.saveMail"); //$NON-NLS-1$
			saveMails.setEnabled(false);
			saveMails.setImage(ImageManager.getSystemImage(SystemImage.SAVE));
			saveMails.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							DirectoryDialog dialog = new DirectoryDialog(dialogShell);
							String path = dialog.open( );

							if (!Util.isEmpty(path)) {
								try {
									MailManager.exportMail(path,
											currentMailDescriptor);
								}
								catch (SerializationException ex) {
									logClass.error("Error: " + ex.getMessage( ) //$NON-NLS-1$
											+ Constants.LINE_SEPARATOR, ex);
								}
								catch (IOException ex) {
									logClass.error("Error: " + ex.getMessage( ) //$NON-NLS-1$
											+ Constants.LINE_SEPARATOR, ex);
								}
							}
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			new XToolItem(toolBar, SWT.SEPARATOR);

			deleteMails = new XToolItem(toolBar, SWT.PUSH);
			deleteMails.setToolTipTextForLocaleKey("MailDialog.tool.tooltip.deleteMail"); //$NON-NLS-1$
			deleteMails.setEnabled(false);
			deleteMails.setImage(ImageManager.getSystemImage(SystemImage.DELETE));
			deleteMails.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							MailManager.deleteMail(currentMailDescriptor);
							mailDescriptors = MailManager.getMailDescriptors( );
							table.removeAll( );
							setTableContent( );
							pList.removeAll( );
							pList.redraw( );
							forwardMail.setEnabled(false);
							deleteMails.setEnabled(false);
							printMails.setEnabled(false);
							saveMails.setEnabled(false);
							mailText.setText(""); //$NON-NLS-1$
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			new XToolItem(toolBar, SWT.SEPARATOR);

			manageContacts = new XToolItem(toolBar, SWT.PUSH);
			manageContacts.setToolTipTextForLocaleKey("MailDialog.tool.tooltip.contacts"); //$NON-NLS-1$
			manageContacts.setEnabled(true);
			manageContacts.setImage(ImageManager.getSystemImage(SystemImage.CONTACTS));
			manageContacts.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							ContactDialog dialog = new ContactDialog(dialogShell);
							dialog.open( );
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			addContact = new XToolItem(toolBar, SWT.PUSH);
			addContact.setToolTipTextForLocaleKey("MailDialog.tool.tooltip.newContact"); //$NON-NLS-1$
			addContact.setEnabled(true);
			addContact.setImage(ImageManager.getSystemImage(SystemImage.ADD_CONTACT));
			addContact.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							try {
								Contact aux = new Contact( );

								Contact clone = ObjectSerializer.<Contact> deepCopy(aux);

								EditContactDialog dialog = new EditContactDialog(dialogShell);
								if (!clone.equals(dialog.open(aux))) {
									MailManager.addContact(aux);
								}
							}
							catch (IOException ex) {
								logClass.error("Error: " + ex.getMessage( ) //$NON-NLS-1$
										+ Constants.LINE_SEPARATOR, ex);
							}
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

			SWTUtil.setGridData(toolBar,
					true,
					false,
					SWT.FILL,
					SWT.BEGINNING,
					4,
					1);

			table = new XTable(dialogShell, SWT.SINGLE | SWT.BORDER
					| SWT.FULL_SELECTION | SWT.V_SCROLL);
			SWTUtil.setGridData(table, true, true, SWT.FILL, SWT.FILL, 3, 1);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			String[] titles = {
					"MailDialog.table.column.sent", "MailDialog.table.column.to", //$NON-NLS-1$ //$NON-NLS-2$
					"MailDialog.table.column.cc", "MailDialog.table.column.subject", //$NON-NLS-1$ //$NON-NLS-2$
					"MailDialog.table.column.sentDate"}; //$NON-NLS-1$
			for (String t : titles) {
				XTableColumn column = new XTableColumn(table, SWT.NONE);
				column.setTextForLocaleKey(t);
				column.pack( );
			}

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
					currentAttachmentDescriptor = (AttachmentDescriptor) e.item.getData(ATTACHMENT_DESCRIPTOR);
				}
			});

			mailText = new XText(dialogShell, SWT.BORDER | SWT.V_SCROLL
					| SWT.H_SCROLL, true);
			mailText.setEditable(false);
			mailText.setEnabled(false);
			SWTUtil.setGridData(mailText, true, true, SWT.FILL, SWT.FILL, 3, 1);

			setTableContent( );

			table.addSelectionListener(new SelectionAdapter( ) {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (table.getSelectionCount( ) == 1) {
						deleteMails.setEnabled(true);
						printMails.setEnabled(true);
						saveMails.setEnabled(true);
						forwardMail.setEnabled(true);
						pList.setEnabled(true);
						currentMailDescriptor = (MailDescriptor) e.item.getData(MAIL_DESCRIPTOR);
						mailText.setText(currentMailDescriptor.getText( ));
						setAttachmentList(currentMailDescriptor);
					}
					else {
						forwardMail.setEnabled(false);
						deleteMails.setEnabled(false);
						printMails.setEnabled(false);
						saveMails.setEnabled(false);
						mailText.setText(""); //$NON-NLS-1$
					}
				}
			});

			SWTUtil.packTable(table);

			dialogShell.layout( );
			SWTUtil.centerDialog(dialogShell);
			dialogShell.open( );

			SWTUtil.blockDialogFromReturning(dialogShell);

			return;
		}
		catch (MessagingException ex) {
			XMessageBox box = new XMessageBox(parent,
					HMessageBoxType.ERROR,
					XButtonType.CLOSE);
			box.setTextForLocaleKey("MailDialog.messagebox.title.settingsInsufficient"); //$NON-NLS-1$
			box.setMessageForLocaleKey("MailDialog.messagebox.message.settingsInsufficient", //$NON-NLS-1$
					Constants.LINE_SEPARATOR,
					ex.getMessage( ));
			box.open( );
			return;
		}
	}

	/**
	 * Sets the content of the table.
	 */
	private void setTableContent() {
		for (MailDescriptor md : mailDescriptors) {
			String firstToName = md.getToName( );
			String firstCcName = md.getCcName( );

			XTableItem itm = new XTableItem(table, SWT.NONE);

			if (md.isSent( )) {
				itm.setImage(0,
						ImageManager.getSystemImage(SystemImage.DONE_SMALL));
			}
			else {
				itm.setImage(0,
						ImageManager.getSystemImage(SystemImage.NOT_DONE_SMALL));
			}

			String[] str = new String[] {
					Boolean.toString(md.isSent( )),
					firstToName,
					firstCcName,
					md.getSubject( ),
					Util.getTimeAsString(md.getSendDate( ),
							"dd.MM.yyyy HH:mm:ss")}; //$NON-NLS-1$
			itm.setText(str);
			itm.setData(MAIL_DESCRIPTOR, md);
		}
	}

	/**
	 * Sets the attachment list for the given mail
	 * {@link de.xirp.mail.MailDescriptor descriptor}.
	 * 
	 * @param md
	 * 			The mail descriptor.
	 * 
	 * @see de.xirp.mail.MailDescriptor
	 */
	private void setAttachmentList(MailDescriptor md) {
		pList.removeAll( );
		if (md != null) {
			for (AttachmentDescriptor a : md.getAttachmentDescriptors( )) {
				XPListItem itm = new XPListItem(pList, SWT.NONE);
				itm.setText(a.getFileName( ));

				itm.setDescriptionForLocaleKey("MailDialog.list.item.description", //$NON-NLS-1$
						(a.getFileType( ) + Constants.LINE_SEPARATOR),
						a.getFileSizeString( ));

				Image img = null;
				String ft = a.getFileType( );
				if (ft.equalsIgnoreCase("PDF")) { //$NON-NLS-1$
					img = ImageManager.getSystemImage(SystemImage.PDF);
				}
				else if (ft.equalsIgnoreCase("CSV")) {//$NON-NLS-1$
					img = ImageManager.getSystemImage(SystemImage.CSV);
				}
				else if (ft.equalsIgnoreCase("PNG")//$NON-NLS-1$
						|| ft.equalsIgnoreCase("JPG")) { //$NON-NLS-1$
					img = ImageManager.getSystemImage(SystemImage.IMAGE);
				}
				if (img == null) {
					img = ImageManager.getImageForExtension(FilenameUtils.getExtension(a.getFileName( )),
							48,
							48);
				}
				if (img != null) {
					itm.setImage(img);
				}

				itm.setData(ATTACHMENT_DESCRIPTOR, a);
			}
		}
		pList.redraw( );
	}
}
