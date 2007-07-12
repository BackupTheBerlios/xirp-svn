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
 * MailDescriptor.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 20.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.mail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.xirp.mail.Attachment.FileType;

/**
 * A instance of this class is the lightweight variant of the
 * {@link de.xirp.mail.Mail} object. Instead of the full
 * byte date of the attachments it holds a list of
 * {@link de.xirp.mail.AttachmentDescriptor}. It also
 * does not hold all of the {@link de.xirp.mail.Contact}
 * s objects. it does only hold the first recipient of the two
 * recipient lists.
 * 
 * @author Matthias Gernand
 * @see de.xirp.mail.AttachmentDescriptor
 * @see de.xirp.mail.Contact
 */
public class MailDescriptor implements Serializable, Comparable<MailDescriptor> {

	/**
	 * The serial version UID of this serializable.
	 */
	private static final long serialVersionUID = 1912769820726509098L;
	/**
	 * The first mail address of the recipient list of the
	 * corresponding {@link de.xirp.mail.Mail}.
	 */
	private String toMail = ""; //$NON-NLS-1$
	/**
	 * The first full name of the recipient list of the corresponding
	 * {@link de.xirp.mail.Mail}.
	 */
	private String toName = ""; //$NON-NLS-1$
	/**
	 * The first mail address of the carbon copy list of the
	 * corresponding {@link de.xirp.mail.Mail}.
	 */
	private String ccMail = ""; //$NON-NLS-1$
	/**
	 * The first full name of the carbon copy list of the
	 * corresponding {@link de.xirp.mail.Mail}.
	 */
	private String ccName = ""; //$NON-NLS-1$
	/**
	 * A list of
	 * {@link de.xirp.mail.AttachmentDescriptor}.
	 */
	private List<AttachmentDescriptor> attachmentDescriptors = new ArrayList<AttachmentDescriptor>( );
	/**
	 * The subject of the mail.
	 */
	private String subject = ""; //$NON-NLS-1$
	/**
	 * The text of the mail.
	 */
	private String text = ""; //$NON-NLS-1$
	/**
	 * The send {@link java.util.Date} of the mail.
	 */
	private Date sendDate = null;
	/**
	 * The descriptor key of this descriptor.
	 */
	private String descriptorKey = ""; //$NON-NLS-1$
	/**
	 * The folder key for this descriptor.
	 */
	private int folderKey = -1;
	/**
	 * This flag indicates whether or not the mail has been sent.
	 */
	private boolean sent = false;

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
	 * Returns a list of
	 * {@link de.xirp.mail.AttachmentDescriptor}.
	 * 
	 * @return The list of attachments.
	 * @see de.xirp.mail.AttachmentDescriptor
	 */
	public List<AttachmentDescriptor> getAttachmentDescriptors() {
		return attachmentDescriptors;
	}

	/**
	 * Clears the list of
	 * {@link de.xirp.mail.AttachmentDescriptor}.
	 * 
	 * @see de.xirp.mail.AttachmentDescriptor
	 */
	protected void clearAttachmentDescriptorList() {
		attachmentDescriptors.clear( );
	}

	/**
	 * Adds a {@link de.xirp.mail.AttachmentDescriptor}
	 * to the list of attachments. The
	 * {@link de.xirp.mail.AttachmentDescriptor} is
	 * constructed from the given file name,
	 * {@link de.xirp.mail.Attachment.FileType} and file
	 * size.
	 * 
	 * @param fileName
	 *            The name of the attachment file.
	 * @param fileType
	 *            The type of the attachment file.
	 * @param fileSize
	 *            The size of the attachment file.
	 * @see de.xirp.mail.AttachmentDescriptor
	 * @see de.xirp.mail.Attachment.FileType
	 */
	public void addAttachmentDescriptor(String fileName, FileType fileType,
			int fileSize) {
		AttachmentDescriptor ad = new AttachmentDescriptor( );
		ad.setFileName(fileName);
		ad.setFileType(fileType.name( ));
		ad.setFileSize(fileSize);
		this.attachmentDescriptors.add(ad);
	}

	/**
	 * Adds a {@link de.xirp.mail.AttachmentDescriptor}
	 * to the list of attachments.
	 * 
	 * @param ad
	 *            The descriptor to add.
	 * @see de.xirp.mail.AttachmentDescriptor
	 */
	public void addAttachmentDescriptor(AttachmentDescriptor ad) {
		this.attachmentDescriptors.add(ad);
	}

	/**
	 * Adds a {@link de.xirp.mail.AttachmentDescriptor}
	 * to the list of attachments. The
	 * {@link de.xirp.mail.AttachmentDescriptor} is
	 * generated from the given
	 * {@link de.xirp.mail.Attachment}.
	 * 
	 * @param a
	 *            The attachment to add a descriptor for.
	 * @see de.xirp.mail.AttachmentDescriptor
	 */
	public void addAttachmentDescriptor(Attachment a) {
		this.attachmentDescriptors.add(a.getAttachmentDescriptor( ));
	}

	/**
	 * Returns the carbon copy mail address.
	 * 
	 * @return The cc mail address.
	 */
	public String getCcMail() {
		return ccMail;
	}

	/**
	 * Sets the carbon copy mail address.
	 * 
	 * @param ccMail
	 *            The cc mail address to set.
	 */
	public void setCcMail(String ccMail) {
		this.ccMail = ccMail;
	}

	/**
	 * Returns the carbon recipient copy name.
	 * 
	 * @return The cc name.
	 */
	public String getCcName() {
		return ccName;
	}

	/**
	 * Sets the carbon copy recipient name.
	 * 
	 * @param ccName
	 *            The cc name to set.
	 */
	public void setCcName(String ccName) {
		this.ccName = ccName;
	}

	/**
	 * Returns the send {@link java.util.Date}.
	 * 
	 * @return The send date.
	 */
	public Date getSendDate() {
		return sendDate;
	}

	/**
	 * Sets the send date.
	 * 
	 * @param sendDate
	 *            The send date to set.
	 */
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	/**
	 * Returns the subject.
	 * 
	 * @return The subject.
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject.
	 * 
	 * @param subject
	 *            The subject to set.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Returns the text.
	 * 
	 * @return The text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text.
	 * 
	 * @param text
	 *            The text to set.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Returns the recipient mail address.
	 * 
	 * @return The to mail address.
	 */
	public String getToMail() {
		return toMail;
	}

	/**
	 * Sets the recipient mail address.
	 * 
	 * @param toMail
	 *            The to mail address to set.
	 */
	public void setToMail(String toMail) {
		this.toMail = toMail;
	}

	/**
	 * Returns the recipient name.
	 * 
	 * @return The to name.
	 */
	public String getToName() {
		return toName;
	}

	/**
	 * Sets the recipient name.
	 * 
	 * @param toName
	 *            The to name to set.
	 */
	public void setToName(String toName) {
		this.toName = toName;
	}

	/**
	 * Compares one {@link de.xirp.mail.MailDescriptor}
	 * to another. The descriptors are compared by the send date.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(MailDescriptor md) {
		return sendDate.compareTo(md.getSendDate( ));
	}

	/**
	 * Returns the descriptor key.
	 * 
	 * @return The descriptor key.
	 */
	public String getDescriptorKey() {
		return descriptorKey;
	}

	/**
	 * Sets the descriptor key.
	 * 
	 * @param key
	 *            The descriptor key to set.
	 */
	public void setDescriptorKey(String key) {
		this.descriptorKey = key;
	}

	/**
	 * Returns the folder key.
	 * 
	 * @return The folder key.
	 */
	public int getFolderKey() {
		return folderKey;
	}

	/**
	 * Sets the folder key.
	 * 
	 * @param folderKey
	 *            The folder key to set.
	 */
	public void setFolderKey(int folderKey) {
		this.folderKey = folderKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME *
				result +
				((attachmentDescriptors == null) ? 0
						: attachmentDescriptors.hashCode( ));
		result = PRIME * result + ((ccMail == null) ? 0 : ccMail.hashCode( ));
		result = PRIME * result + ((ccName == null) ? 0 : ccName.hashCode( ));
		result = PRIME * result +
				((descriptorKey == null) ? 0 : descriptorKey.hashCode( ));
		result = PRIME * result + folderKey;
		result = PRIME * result +
				((sendDate == null) ? 0 : sendDate.hashCode( ));
		result = PRIME * result + ((subject == null) ? 0 : subject.hashCode( ));
		result = PRIME * result + ((text == null) ? 0 : text.hashCode( ));
		result = PRIME * result + ((toMail == null) ? 0 : toMail.hashCode( ));
		result = PRIME * result + ((toName == null) ? 0 : toName.hashCode( ));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass( ) != obj.getClass( )) {
			return false;
		}
		final MailDescriptor other = (MailDescriptor) obj;
		if (attachmentDescriptors == null) {
			if (other.attachmentDescriptors != null) {
				return false;
			}
		}
		else if (!attachmentDescriptors.equals(other.attachmentDescriptors)) {
			return false;
		}
		if (ccMail == null) {
			if (other.ccMail != null) {
				return false;
			}
		}
		else if (!ccMail.equals(other.ccMail)) {
			return false;
		}
		if (ccName == null) {
			if (other.ccName != null) {
				return false;
			}
		}
		else if (!ccName.equals(other.ccName)) {
			return false;
		}
		if (descriptorKey == null) {
			if (other.descriptorKey != null) {
				return false;
			}
		}
		else if (!descriptorKey.equals(other.descriptorKey)) {
			return false;
		}
		if (folderKey != other.folderKey) {
			return false;
		}
		if (sendDate == null) {
			if (other.sendDate != null) {
				return false;
			}
		}
		else if (!sendDate.equals(other.sendDate)) {
			return false;
		}
		if (subject == null) {
			if (other.subject != null) {
				return false;
			}
		}
		else if (!subject.equals(other.subject)) {
			return false;
		}
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		}
		else if (!text.equals(other.text)) {
			return false;
		}
		if (toMail == null) {
			if (other.toMail != null) {
				return false;
			}
		}
		else if (!toMail.equals(other.toMail)) {
			return false;
		}
		if (toName == null) {
			if (other.toName != null) {
				return false;
			}
		}
		else if (!toName.equals(other.toName)) {
			return false;
		}
		return true;
	}

}
