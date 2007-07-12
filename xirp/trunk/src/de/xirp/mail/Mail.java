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
 * Mail.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.mail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * This class represents a mail. It is no lightweight object. It
 * contains {@link de.xirp.mail.Attachment} objects which
 * contain the attachment file in pure <code>byte[]</code> form. So
 * this object is only created when it is necessary. The
 * representation in the program is done by the
 * {@link de.xirp.mail.MailDescriptor}
 * 
 * @author Matthias Gernand
 * @see de.xirp.mail.MailDescriptor
 * @see de.xirp.mail.Attachment
 */
public final class Mail implements Serializable, Comparable<Mail> {

	/**
	 * The serial version UID of this serializable.
	 */
	private static final long serialVersionUID = 4370067209337361337L;
	/**
	 * A recipient list.
	 */
	private List<Contact> to = Collections.emptyList( );
	/**
	 * Another recipient list.
	 */
	private List<Contact> cc = Collections.emptyList( );
	/**
	 * The subject of the mail.
	 */
	private String subject = ""; //$NON-NLS-1$
	/**
	 * Test text of the mail.
	 */
	private String text = ""; //$NON-NLS-1$
	/**
	 * The attachments.
	 */
	private List<Attachment> attachments = Collections.emptyList( );
	/**
	 * The send {@link java.util.Date}.
	 */
	private Date sendDate = null;
	/**
	 * This flag indicates whether or not the mail has been sent.
	 */
	private transient boolean sent = false;

	/**
	 * Returns if the mail was sent.
	 * 
	 * @return A <code>boolean</code>. <br>
	 *         <code>true</code>: the mail was sent.<br>
	 *         <code>false</code>: the mail was not sent.<br>
	 */
	public boolean isSent() {
		return sent;
	}

	/**
	 * Sets if the mail was sent.
	 * 
	 * @param sent
	 *            <code>true</code>: the mail was sent.<br>
	 *            <code>false</code>: the mail was not sent.<br>
	 */
	public void setSent(boolean sent) {
		this.sent = sent;
	}

	/**
	 * Constructs a new {@link de.xirp.mail.Mail} object.
	 * The recipient and attachment lists are created.
	 */
	public Mail() {
		to = new ArrayList<Contact>( );
		cc = new ArrayList<Contact>( );
		attachments = new ArrayList<Attachment>( );
	}

	/**
	 * Returns a list of {@link de.xirp.mail.Attachment}.
	 * 
	 * @return The attachments list.
	 */
	public List<Attachment> getAttachments() {
		return attachments;
	}

	/**
	 * Adds a {@link de.xirp.mail.Attachment} to the
	 * attachment list.
	 * 
	 * @param attachment
	 *            An attachment.
	 */
	public void addAttachment(Attachment attachment) {
		this.attachments.add(attachment);
	}

	/**
	 * Returns a list of {@link de.xirp.mail.Contact}.
	 * This is the carbon copy list.
	 * 
	 * @return The cc list.
	 */
	public List<Contact> getCc() {
		return Collections.unmodifiableList(cc);
	}

	/**
	 * Adds a {@link de.xirp.mail.Contact} to the carbon
	 * copy receipient list.
	 * 
	 * @param contact
	 *            The contact to add.
	 */
	public void addCc(Contact contact) {
		this.cc.add(contact);
	}

	/**
	 * Returns the subnject of the
	 * {@link de.xirp.mail.Mail}.
	 * 
	 * @return The subject.
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject of the {@link de.xirp.mail.Mail}.
	 * 
	 * @param subject
	 *            The subject to set.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Returns the text of the {@link de.xirp.mail.Mail}.
	 * 
	 * @return The text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text of the {@link de.xirp.mail.Mail}.
	 * 
	 * @param text
	 *            The text to set.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Returns a list of {@link de.xirp.mail.Contact}.
	 * This is the recipient list.
	 * 
	 * @return The recipient list.
	 */
	public List<Contact> getTo() {
		return Collections.unmodifiableList(to);
	}

	/**
	 * Adds a {@link de.xirp.mail.Contact} to the
	 * recipient list.
	 * 
	 * @param contact
	 *            The contact to add.
	 */
	public void addTo(Contact contact) {
		this.to.add(contact);
	}

	/**
	 * Returns the send {@link java.util.Date} of the
	 * {@link de.xirp.mail.Mail}.
	 * 
	 * @return The send date.
	 */
	public Date getSendDate() {
		return sendDate;
	}

	/**
	 * Sets the send {@link java.util.Date} of the
	 * {@link de.xirp.mail.Mail}.
	 * 
	 * @param sendDate
	 *            The send date to set.
	 */
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	/**
	 * Returns the mail descriptor key which is the send date
	 * milliseconds as a string.
	 * 
	 * @return The send date milliseconds as string.
	 */
	protected String getMailDescriptorKey() {
		return Long.toString(getSendDate( ).getTime( ));
	}

	/**
	 * Returns a {@link de.xirp.mail.MailDescriptor}. It
	 * is build from the {@link de.xirp.mail.Mail} object
	 * and its content.
	 * 
	 * @return A mail descriptor for this mail.
	 * @see de.xirp.mail.MailDescriptor
	 * @see de.xirp.mail.AttachmentDescriptor
	 */
	protected MailDescriptor getMailDescriptor() {
		MailDescriptor md = new MailDescriptor( );

		Contact c = getTo( ).get(0);
		md.setToName(c.getLastName( ) + ", " + c.getFirstName( )); //$NON-NLS-1$
		md.setToMail(c.getMail( ));

		c = getCc( ).get(0);
		md.setCcName(c.getLastName( ) + ", " + c.getFirstName( )); //$NON-NLS-1$
		md.setCcMail(c.getMail( ));

		md.setSubject(getSubject( ));
		md.setText(getText( ));

		md.setDescriptorKey(getMailDescriptorKey( ));

		md.setSendDate(getSendDate( ));

		md.setSent(isSent( ));

		for (Attachment a : getAttachments( )) {
			md.addAttachmentDescriptor(a.getAttachmentDescriptor( ));
		}
		return md;
	}

	/**
	 * This method compares one {@link de.xirp.mail.Mail}
	 * to another. The send date is compared.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Mail mail) {
		return sendDate.compareTo(mail.getSendDate( ));
	}
}
