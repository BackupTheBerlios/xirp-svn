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
 * AttachmentDescriptor.java
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

import de.xirp.io.comm.DataAmount;

/**
 * This class is the lightweight representation of a
 * {@link de.xirp.mail.Attachment}. It does not contain
 * the file content but all necessary information about the
 * {@link de.xirp.mail.Attachment}.
 * 
 * @author Matthias Gernand
 * @see de.xirp.mail.Attachment
 */
public class AttachmentDescriptor implements Serializable {

	/**
	 * The serial version UID if this serializable.
	 */
	private static final long serialVersionUID = -2896349019156494120L;
	/**
	 * The file name.
	 */
	private String fileName = ""; //$NON-NLS-1$
	/**
	 * The file type.
	 */
	private String fileType = ""; //$NON-NLS-1$
	/**
	 * The size of the file.
	 */
	private long fileSize = -1;

	/**
	 * Returns the file name.
	 * 
	 * @return The file name.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 * 
	 * @param fileName
	 *            The file name to set.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Returns the file size in bytes.
	 * 
	 * @return The file size.
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * Gets the file size as formated string.
	 * 
	 * @return the file size as string
	 * @see DataAmount#toString()
	 */
	public String getFileSizeString() {
		return new DataAmount(fileSize).add(0).toString( );
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * Returns the file type.
	 * 
	 * @return The file type.
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * Sets the file type.
	 * 
	 * @param fileType
	 *            The file type to set.
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
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
		result = PRIME * result +
				((fileName == null) ? 0 : fileName.hashCode( ));
		result = PRIME * result + (int) (fileSize ^ (fileSize >>> 32));
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
		final AttachmentDescriptor other = (AttachmentDescriptor) obj;
		if (fileName == null) {
			if (other.fileName != null) {
				return false;
			}
		}
		else if (!fileName.equals(other.fileName)) {
			return false;
		}
		if (fileSize != other.fileSize) {
			return false;
		}
		return true;
	}
}
