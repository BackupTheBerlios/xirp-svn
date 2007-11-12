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
 * ComposeMailDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 19.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;

import de.xirp.chart.ChartUtil;
import de.xirp.db.ChartDatabaseUtil;
import de.xirp.db.Observed;
import de.xirp.db.Record;
import de.xirp.mail.Attachment;
import de.xirp.mail.Contact;
import de.xirp.mail.Mail;
import de.xirp.mail.MailManager;
import de.xirp.managers.DeleteManager;
import de.xirp.report.ReportDescriptor;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.*;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.ui.widgets.dialogs.XMessageBox.HMessageBoxType;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Util;
import de.xirp.util.serialization.ObjectSerializer;

/**
 * This dialog is used to compose a mail.
 * 
 * @author Matthias Gernand
 */
public class ComposeMailDialog extends XDialog {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(ComposeMailDialog.class);
	/**
	 * The width.
	 */
	private static final int WIDTH = 600;
	/**
	 * The height.
	 */
	private static final int HEIGHT = 400;
	/**
	 * The parent shell.
	 */
	private Shell parent;
	/**
	 * The dialogs shell.
	 */
	private XShell dialogShell;
	/**
	 * The mail to compose. see de.xirp.mail.Mail
	 */
	private Mail mail;
	/**
	 * The first contact list. see de.xirp.mail.Contact
	 */
	private List<Contact> toContactList = Collections.emptyList( );
	/**
	 * The second contact list. see de.xirp.mail.Contact
	 */
	private List<Contact> ccContactList = Collections.emptyList( );
	/**
	 * The attachments. see de.xirp.mail.Attachment
	 */
	private List<Attachment> attachmentList = Collections.emptyList( );
	/**
	 * A tool bar.
	 */
	private XToolBar toolBar;
	/**
	 * A menu.
	 */
	private Menu menu;
	/**
	 * A tool item
	 */
	private XToolItem attachment;
	/**
	 * A list.
	 */
	private XList ccs;
	/**
	 * A list.
	 */
	private XList tos;
	/**
	 * A text field.
	 */
	private XTextField subject;
	/**
	 * A text.
	 */
	private XText text;
	/**
	 * A flag indicating if the mail should be forwarded.
	 */
	private boolean forward = false;
	/**
	 * A tool item.
	 */
	private XToolItem manageContacts;
	/**
	 * A tool item.
	 */
	private XToolItem addContact;
	/**
	 * A tool item.
	 */
	private XToolItem delete;
	/**
	 * A list.
	 */
	private XList list;

	/**
	 * Constructs a new compose mail dialog.
	 * 
	 * @param parent
	 *            The parent.
	 */
	public ComposeMailDialog(Shell parent) {
		super(parent);
		this.parent = parent;
	}

	/**
	 * Opens the given mail to be edited or created. If the given mail
	 * was <code>null</code> the dialog is used to create a new
	 * mail.
	 * 
	 * @param mail
	 *            The mail.
	 * @see de.xirp.mail.Mail
	 */
	public void open(Mail mail) {
		this.mail = mail;
		if (mail != null) {
			forward = true;
		}

		dialogShell = new XShell(parent, SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});
		dialogShell.setSize(WIDTH, HEIGHT);
		if (forward) {
			dialogShell.setTextForLocaleKey("ComposeMailDialog.gui.title.forwardMessage"); //$NON-NLS-1$
		}
		else {
			dialogShell.setTextForLocaleKey("ComposeMailDialog.gui.title.composeMessage"); //$NON-NLS-1$
		}
		dialogShell.setImage(ImageManager.getSystemImage(SystemImage.SEND_MAIL));
		SWTUtil.setGridLayout(dialogShell, 3, false);

		toolBar = new XToolBar(dialogShell, SWT.FLAT);

		XToolItem send = new XToolItem(toolBar, SWT.PUSH);
		if (mail == null) {
			send.setToolTipTextForLocaleKey("ComposeMailDialog.tool.item.tooltip.send"); //$NON-NLS-1$
			send.setImage(ImageManager.getSystemImage(SystemImage.SEND_MAIL));
		}
		else {
			send.setToolTipTextForLocaleKey("ComposeMailDialog.tool.item.tooltip.forward"); //$NON-NLS-1$
			send.setImage(ImageManager.getSystemImage(SystemImage.FORWARD_MAIL));
		}
		send.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Runnable runnable = new Runnable( ) {

					public void run() {
						try {
							if (forward) {
								getMail( ).setSubject(I18n.getString("ComposeMailDialog.internal.mail.subject", getMail( ).getSubject( ))); //$NON-NLS-1$
							}
							else {
								buildMail( );
							}
							MailManager.sendMail(getMail( ));
							dialogShell.close( );
						}
						catch (MessagingException ex) {
							XMessageBox box = new XMessageBox(dialogShell,
									HMessageBoxType.INFO,
									XButtonType.OK);
							box.setTextForLocaleKey("ComposeMailDialog.messagebox.title.errorSendingMail"); //$NON-NLS-1$
							box.setMessageForLocaleKey("ComposeMailDialog.mesagebox.message.errorSendingMail", //$NON-NLS-1$
									Constants.LINE_SEPARATOR,
									ex.getMessage( ));
							box.open( );
						}
						catch (EmailException ex) {
							XMessageBox box = new XMessageBox(dialogShell,
									HMessageBoxType.ERROR,
									XButtonType.OK);
							box.setTextForLocaleKey("ComposeMailDialog.messagebox.title.errorSendingMail"); //$NON-NLS-1$
							box.setMessageForLocaleKey("ComposeMailDialog.mesagebox.message.errorSendingMail", //$NON-NLS-1$
									Constants.LINE_SEPARATOR,
									ex.getMessage( ));
							box.open( );
							dialogShell.close( );
						}
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		if (!forward) {

			new ToolItem(toolBar, SWT.SEPARATOR);

			menu = new Menu(dialogShell, SWT.POP_UP);
			XMenuItem filesystem = new XMenuItem(menu, SWT.PUSH);
			filesystem.setTextForLocaleKey("ComposeMailDialog.menu.item.fromFileSystem"); //$NON-NLS-1$
			filesystem.addSelectionListener(new SelectionAdapter( ) {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					FileDialog dialog = new FileDialog(dialogShell, SWT.OPEN);
					dialog.setFilterPath(Constants.USER_DIR);
					String names[] = Attachment.FileType.extensionsNames( );
					dialog.setFilterNames(names);
					String xts[] = Attachment.FileType.extensions( );
					dialog.setFilterExtensions(xts);
					String path = dialog.open( );

					if (!Util.isEmpty(path)) {
						try {
							Attachment a = new Attachment(new File(path));
							attachmentList.add(a);
							list.add(a.getFileName( ));
						}
						catch (IOException ex) {
							logClass.error("Error: " + ex.getMessage( ) //$NON-NLS-1$
									+ Constants.LINE_SEPARATOR, ex);
						}
					}
				}
			});

			XMenuItem log = new XMenuItem(menu, SWT.PUSH);
			log.setTextForLocaleKey("ComposeMailDialog.menu.item.logFromDatabase"); //$NON-NLS-1$
			log.addSelectionListener(new SelectionAdapter( ) {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					RecordLookupDialog dialog = new RecordLookupDialog(dialogShell);
					Record record = dialog.openSingle( );
					if (record != null) {
						try {
							List<Observed> obs = ChartDatabaseUtil.getObservedListForRecord(record);

							File file = new File(Constants.TMP_DIR,
									record.getName( ) +
											"_" //$NON-NLS-1$
											+ record.getRobotName( ) +
											"_" + record.getId( ) + ".csv"); //$NON-NLS-1$ //$NON-NLS-2$
							ChartUtil.exportCSV(obs, file);
							Attachment a = new Attachment(file);
							attachmentList.add(a);
							list.add(a.getFileName( ));
							DeleteManager.deleteOnShutdown(file);
						}
						catch (IOException ex) {
							logClass.error("Error: " + ex.getMessage( ) + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$
						}
					}
				}
			});

			XMenuItem report = new XMenuItem(menu, SWT.PUSH);
			report.setTextForLocaleKey("ComposeMailDialog.menu.item.reportFromDatabase"); //$NON-NLS-1$
			report.addSelectionListener(new SelectionAdapter( ) {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					ReportSearchDialog dialog = new ReportSearchDialog(dialogShell);
					ReportDescriptor rd = dialog.open( );
					if (rd != null) {
						try {
							File file = new File(Constants.TMP_DIR,
									rd.getFileName( ));
							FileUtils.writeByteArrayToFile(file,
									rd.getPdfData( ));
							Attachment a = new Attachment(file);
							attachmentList.add(a);
							list.add(a.getFileName( ));
						}
						catch (IOException ex) {
							logClass.error("Error: " + ex.getMessage( ) + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$
						}
					}
				}

			});

			XMenuItem charts = new XMenuItem(menu, SWT.PUSH);
			charts.setTextForLocaleKey("ComposeMailDialog.menu.item.chartFromDatabase"); //$NON-NLS-1$
			charts.addSelectionListener(new SelectionAdapter( ) {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					FileDialog dialog = new FileDialog(dialogShell, SWT.OPEN);
					String names[] = Attachment.FileType.extensionsNames( );
					dialog.setFilterNames(names);
					String xts[] = Attachment.FileType.extensions( );
					dialog.setFilterExtensions(xts);
					dialog.setFilterPath(Constants.CHART_DIR);
					String path = dialog.open( );

					if (!Util.isEmpty(path)) {
						try {
							Attachment a = new Attachment(new File(path));
							attachmentList.add(a);
							list.add(a.getFileName( ));
						}
						catch (IOException ex) {
							logClass.error("Error: " + ex.getMessage( ) //$NON-NLS-1$
									+ Constants.LINE_SEPARATOR, ex);
						}
					}
				}

			});

			attachment = new XToolItem(toolBar, SWT.DROP_DOWN);
			attachment.setToolTipTextForLocaleKey("ComposeMailDialog.tool.item.tooltip.attachFile"); //$NON-NLS-1$
			attachment.setImage(ImageManager.getSystemImage(SystemImage.ATTACHMENT));
			attachment.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							Rectangle rect = attachment.getBounds( );
							Point pt = new Point(rect.x, rect.y + rect.height);
							pt = toolBar.toDisplay(pt);
							menu.setLocation(pt.x, pt.y);
							menu.setVisible(true);
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

		}

		new XToolItem(toolBar, SWT.SEPARATOR);

		delete = new XToolItem(toolBar, SWT.PUSH);
		delete.setToolTipTextForLocaleKey("ComposeMailDialog.tool.item.tooltip.deleteSelection"); //$NON-NLS-1$
		delete.setEnabled(false);
		delete.setImage(ImageManager.getSystemImage(SystemImage.DELETE));
		delete.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						deleteSelections( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		new XToolItem(toolBar, SWT.SEPARATOR);

		manageContacts = new XToolItem(toolBar, SWT.PUSH);
		manageContacts.setToolTipTextForLocaleKey("ComposeMailDialog.tool.item.tooltip.manageContacts"); //$NON-NLS-1$
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
		addContact.setToolTipTextForLocaleKey("ComposeMailDialog.tool.item.tooltip.newContact"); //$NON-NLS-1$
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
							logClass.error("Error: " + ex.getMessage( ) + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$
						}
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		SWTUtil.setGridData(toolBar, true, false, SWT.FILL, SWT.BEGINNING, 3, 1);

		XButton to = new XButton(dialogShell);
		to.setTextForLocaleKey("ComposeMailDialog.button.to"); //$NON-NLS-1$
		to.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				ContactLookupDialog dialog = new ContactLookupDialog(dialogShell);
				for (Contact c : dialog.open( )) {
					addToContact(c);
				}
			}

		});

		SWTUtil.setGridData(to, false, false, SWT.LEFT, SWT.BEGINNING, 1, 1);

		tos = new XList(dialogShell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		SWTUtil.setGridData(tos, true, false, SWT.FILL, SWT.FILL, 1, 1);
		tos.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDeleteStatus( );
			}

		});
		tos.addKeyListener(new KeyAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					int idx = tos.getSelectionIndex( );
					tos.remove(idx);
					toContactList.remove(idx);
				}
			}
		});

		list = new XList(dialogShell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL |
				SWT.MULTI);
		SWTUtil.setGridData(list, true, false, SWT.FILL, SWT.FILL, 1, 3);
		list.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDeleteStatus( );
			}

		});
		list.addKeyListener(new KeyAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					int idx = list.getSelectionIndex( );
					list.remove(idx);
					attachmentList.remove(idx);
				}
			}
		});

		XButton cc = new XButton(dialogShell);
		cc.setTextForLocaleKey("ComposeMailDialog.button.cc"); //$NON-NLS-1$
		cc.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				ContactLookupDialog dialog = new ContactLookupDialog(dialogShell);
				for (Contact c : dialog.open( )) {
					addCcContact(c);
				}
			}

		});
		SWTUtil.setGridData(cc, false, false, SWT.LEFT, SWT.BEGINNING, 1, 1);

		ccs = new XList(dialogShell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		SWTUtil.setGridData(ccs, true, false, SWT.FILL, SWT.FILL, 1, 1);
		ccs.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDeleteStatus( );
			}

		});
		ccs.addKeyListener(new KeyAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					int idx = ccs.getSelectionIndex( );
					ccs.remove(idx);
					ccContactList.remove(idx);
				}
			}
		});

		XLabel l = new XLabel(dialogShell, SWT.CENTER);
		l.setTextForLocaleKey("ComposeMailDialog.label.subject"); //$NON-NLS-1$
		SWTUtil.setGridData(l, false, false, SWT.CENTER, SWT.CENTER, 1, 1);

		subject = new XTextField(dialogShell, SWT.NONE);
		SWTUtil.setGridData(subject, true, false, SWT.FILL, SWT.BEGINNING, 1, 1);

		text = new XText(dialogShell, SWT.MULTI | SWT.BORDER, true);
		SWTUtil.setGridData(text, true, true, SWT.FILL, SWT.FILL, 3, 1);

		if (forward) {
			setMailContent( );
		}
		else {
			this.mail = new Mail( );
			toContactList = new ArrayList<Contact>( );
			ccContactList = new ArrayList<Contact>( );
			attachmentList = new ArrayList<Attachment>( );
		}

		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		SWTUtil.blockDialogFromReturning(dialogShell);

		return;
	}

	/**
	 * Deletes the selected contacts.
	 */
	private void deleteSelections() {
		int idxList[] = list.getSelectionIndices( );
		int idxTo[] = tos.getSelectionIndices( );
		int idxCc[] = ccs.getSelectionIndices( );
		if (idxList.length > 0) {
			list.remove(idxList);
			List<Attachment> l = new ArrayList<Attachment>( );
			for (int i = 0; i < idxList.length; i++) {
				l.add(attachmentList.get(idxList[i]));
			}
			attachmentList.removeAll(l);
		}
		List<Contact> cl = new ArrayList<Contact>( );
		if (idxTo.length > 0) {
			tos.remove(idxTo);
			for (int i = 0; i < idxTo.length; i++) {
				cl.add(toContactList.get(idxTo[i]));
			}
			toContactList.removeAll(cl);
		}
		cl = new ArrayList<Contact>( );
		if (idxCc.length > 0) {
			ccs.remove(idxCc);
			for (int i = 0; i < idxCc.length; i++) {
				cl.add(ccContactList.get(idxCc[i]));
			}
			ccContactList.removeAll(cl);
		}
	}

	/**
	 * Sets the delete status of the mail.
	 */
	private void setDeleteStatus() {
		if ((tos.getSelectionCount( ) > 0) || (ccs.getSelectionCount( ) > 0) ||
				(list.getSelectionCount( ) > 0)) {
			delete.setEnabled(true);
		}
		else {
			delete.setEnabled(false);
		}
	}

	/**
	 * Constructs the mail from the entered data.
	 */
	private void buildMail() {
		mail.setSubject(I18n.getString("ComposeMailDialog.mail.subject.noreply", Constants.BASE_NAME_MAJOR_VERSION, subject.getText( ))); //$NON-NLS-1$
		mail.setText(text.getText( ));
		for (Contact c : toContactList) {
			mail.addTo(c);
		}
		for (Contact c : ccContactList) {
			mail.addCc(c);
		}
		for (Attachment a : attachmentList) {
			mail.addAttachment(a);
		}
	}

	/**
	 * Adds the given
	 * {@link de.xirp.mail.Contact contact} to the
	 * receivers list.
	 * 
	 * @param c
	 *            The contact to add.
	 * @see de.xirp.mail.Contact
	 */
	private void addToContact(Contact c) {
		String toAdd = c.getLastName( ) + ", " + c.getFirstName( ) + " - " //$NON-NLS-1$ //$NON-NLS-2$
				+ c.getMail( );
		boolean b = true;
		for (String s : tos.getItems( )) {
			if (s.equals(toAdd)) {
				b = false;
			}
		}
		if (b) {
			tos.add(toAdd);
			toContactList.add(c);
		}
	}

	/**
	 * Adds the given
	 * {@link de.xirp.mail.Contact contact} to the carbon
	 * copy list.
	 * 
	 * @param c
	 *            The contact to add.
	 * @see de.xirp.mail.Contact
	 */
	private void addCcContact(Contact c) {
		String toAdd = c.getLastName( ) + ", " + c.getFirstName( ) + " - " //$NON-NLS-1$ //$NON-NLS-2$
				+ c.getMail( );
		boolean b = true;
		for (String s : ccs.getItems( )) {
			if (s.equals(toAdd)) {
				b = false;
			}
		}
		if (b) {
			ccs.add(toAdd);
			ccContactList.add(c);
		}
	}

	/**
	 * Adds the given
	 * {@link de.xirp.mail.Contact contacts} to the
	 * receivers list.
	 * 
	 * @param contacts
	 *            The contacts to add.
	 * @see de.xirp.mail.Contact
	 */
	private void addToContacts(List<Contact> contacts) {
		for (Contact c : contacts) {
			String toAdd = c.getLastName( ) + ", " + c.getFirstName( ) + " - " //$NON-NLS-1$ //$NON-NLS-2$
					+ c.getMail( );
			boolean b = true;
			for (String s : tos.getItems( )) {
				if (s.equals(toAdd)) {
					b = false;
				}
			}
			if (b) {
				tos.add(toAdd);
			}
		}
	}

	/**
	 * Adds the given
	 * {@link de.xirp.mail.Contact contacts} to the
	 * carbon copy list.
	 * 
	 * @param contacts
	 *            The contacts to add.
	 * @see de.xirp.mail.Contact
	 */
	private void addCcContacts(List<Contact> contacts) {
		for (Contact c : contacts) {
			String toAdd = c.getLastName( ) + ", " + c.getFirstName( ) + " - " //$NON-NLS-1$ //$NON-NLS-2$
					+ c.getMail( );
			boolean b = true;
			for (String s : ccs.getItems( )) {
				if (s.equals(toAdd)) {
					b = false;
				}
			}
			if (b) {
				ccs.add(toAdd);
			}
		}
	}

	/**
	 * Sets the content of the mail from the entered data.
	 */
	private void setMailContent() {
		toContactList = new ArrayList<Contact>(mail.getTo( ));
		ccContactList = new ArrayList<Contact>(mail.getCc( ));
		attachmentList = new ArrayList<Attachment>(mail.getAttachments( ));

		addToContacts(toContactList);
		addCcContacts(ccContactList);

		for (Attachment a : attachmentList) {
			list.add(a.getFileName( ));
		}

		subject.setText(mail.getSubject( ));
		text.setText(mail.getText( ));
	}

	/**
	 * Returns the {@link de.xirp.mail.Mail mail}.
	 * 
	 * @return The mail.
	 * @see de.xirp.mail.Mail
	 */
	private Mail getMail() {
		return mail;
	}

	/**
	 * Opens the dialog to create a new mail.
	 * 
	 * @see de.xirp.mail.Mail
	 */
	public void open() {
		open(null);
	}
}
