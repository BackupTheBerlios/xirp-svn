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
 * Attachment.java
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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import de.xirp.util.I18n;

/**
 * This class is the non-lightweight variant of an attachment.
 * It contains informations abouut the file like name and type.
 * It also contains the whole file as <code>byte[]</code>.
 *  
 * @author Matthias Gernand
 *
 */
public final class Attachment implements Serializable {

	/**
	 * The serial version UID of this serializable.
	 */
	private static final long serialVersionUID = -4934612090458446942L;
	/**
	 * The file conttent.
	 */
	private byte[] content;
	/**
	 * The file name.
	 */
	private String fileName = ""; //$NON-NLS-1$
	/**
	 * The {@link java.io.File} of the attachment.
	 */
	private transient File attachment = null;
	/**
	 * The file type of the attachment.
	 */
	private FileType fileType = FileType.UNSUPPORTED;

	/**
	 * Enum holding constants for indicating 
	 * the supported file types. At the moment there
	 * are 4 file types supported:
	 * <br><br>
	 * <ul>
	 * 	<li>PDF</li>
	 * 	<li>JPG</li>
	 * 	<li>PNG</li>
	 * 	<li>CSV</li>
	 * </li>
	 * 
	 * @author Matthias Gernand
	 *
	 */
	public enum FileType {
		/**
		 * PDF file type.
		 */
		PDF,
		/**
		 * JPG file type.
		 */
		JPG,
		/**
		 * PNG file type.
		 */
		PNG,
		/**
		 * CSV file type.
		 */
		CSV,
		/**
		 * A unsupported fyle type.
		 */
		UNSUPPORTED;

		/**
		 * Returns the available file extensions.
		 * 
		 * @return The available extensions as <code>String[]</code>.
		 */
		public static String[] extensions() {
			String x[] = new String[4];
			x[0] = "*.pdf"; //$NON-NLS-1$
			x[1] = "*.png"; //$NON-NLS-1$
			x[2] = "*.jpg"; //$NON-NLS-1$
			x[3] = "*.csv"; //$NON-NLS-1$
			return x;
		}

		/**
		 * Returns the availablle translated file descriptions.
		 * 
		 * @return The available file descriptions as <code>String[]</code>.
		 */
		public static String[] extensionsNames() {
			String x[] = new String[4];
			x[0] = I18n.getString("Attachment.fileDescription.pdfDoc"); //$NON-NLS-1$
			x[1] = I18n.getString("Attachment.fileDescription.pngFile"); //$NON-NLS-1$
			x[2] = I18n.getString("Attachment.fileDescription.jpgFile"); //$NON-NLS-1$
			x[3] = I18n.getString("Attachment.fileDescription.csvFile"); //$NON-NLS-1$
			return x;
		}
	}

	/**
	 * Returns the file type.
	 * 
	 * @return The file type.
	 */
	public FileType getFileType() {
		return fileType;
	}

	/**
	 * Constructs a new {@link de.xirp.mail.Attachment} from
	 * the given {@link java.io.File}. The content of the file is read into
	 * the <code>content</code> array.
	 * 
	 * @param attachment
	 * 			The file to load.
	 * 
	 * @throws IOException if something went wrong while handling the file.
	 * 
	 * @see de.xirp.mail.Attachment
	 */
	public Attachment(File attachment) throws IOException {
		this.attachment = attachment;
		if (this.attachment != null) {
			String ext = null;
			try {
				ext = FilenameUtils.getExtension(this.attachment.getName( ));
				fileType = FileType.valueOf(ext.toUpperCase( ));
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(I18n.getString("Attachment.exception.unsupportedFileType", ext)); //$NON-NLS-1$
			}
			content = readAttachmentFileToByteArray( );
		}
		else {
			throw new IOException(I18n.getString("Attachment.exception.attachmentWasNull")); //$NON-NLS-1$
		}
		fileName = this.attachment.getName( );
	}

	/**
	 * Reads the attachment {@link java.io.File} field to a byte
	 * array.
	 * 
	 * @return The content of the file as byte array.
	 * 
	 * @throws IOException if the attachment field is null.
	 * 
	 */
	private byte[] readAttachmentFileToByteArray() throws IOException {
		return FileUtils.readFileToByteArray(attachment);
	}

	/**
	 * Returns the content of the {@link de.xirp.mail.Attachment}.
	 * 
	 * @return The content.
	 * 
	 * @see de.xirp.mail.Attachment
	 */
	public byte[] getAttachmentFileContent() {
		return content;
	}

	/**
	 * Returns the file name.
	 * 
	 * @return The file name.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Returns the {@link java.io.File} of the attachment.
	 * 
	 * @return The attachment file.
	 */
	public File getFile() {
		return this.attachment;
	}

	/**
	 * Returns the {@link de.xirp.mail.AttachmentDescriptor}
	 * for this {@link de.xirp.mail.Attachment}. It is build
	 * frommthe inforamtions inside this {@link de.xirp.mail.Attachment}.
	 * 
	 * @return The attachment descriptor.
	 */
	public AttachmentDescriptor getAttachmentDescriptor() {
		AttachmentDescriptor ad = new AttachmentDescriptor( );
		ad.setFileName(getFileName( ));
		ad.setFileType(getFileType( ).name( ));
		ad.setFileSize(getAttachmentFileContent( ).length);
		return ad;
	}

	/**
	 * Returns whether or not a {@link de.xirp.mail.Attachment} 
	 * is printable.
	 * 
	 * @return A <code>boolean</code>.<br>
	 * 			<code>true</code>: attachment is printable.<br>
	 *			<code>false</code>: attachment is not printable.<br>
	 */
	public boolean isPrintable() {
		switch (getFileType( )) {
			case PDF:
				return true;
			case JPG:
				return true;
			case PNG:
				return true;
			case CSV:
				return true;
			default:
				return false;
		}
	}
}
