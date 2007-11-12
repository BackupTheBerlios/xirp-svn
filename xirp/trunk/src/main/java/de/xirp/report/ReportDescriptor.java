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
 * ReportDescriptor.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 09.05.2006:		Created by Matthias Gernand.
 */
package de.xirp.report;

import java.io.File;
import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.xirp.util.Constants;

/**
 * This class represents a generated report. This data is stored into the
 * database. If the file name is set, the data of the PDF is set as 
 * <code>byte[]</code>. This <code>byte[]</code> is also persisted 
 * to the database.
 * <br><br>
 * The class is annotated with annotations for persisting Java beans.
 * This class is persisted to a database using Hibernate..
 * 
 * @author Matthias Gernand
 * 
 */
@Entity
@Table(name = "REPORTS")//$NON-NLS-1$
public final class ReportDescriptor {

	/**
	 * The logger for this class.
	 */
	private transient static final Logger logClass = Logger.getLogger(ReportDescriptor.class);
	/**
	 * The id of the entry in the database, generated automatically.
	 */
	private long id;
	/**
	 * The creation time of the report in milliseconds.
	 */
	private long creationTime;
	/**
	 * The name of the report.
	 */
	private String reportName = ""; //$NON-NLS-1$
	/**
	 * The robots name for which the report was generated.
	 */
	private String robotName = ""; //$NON-NLS-1$
	/**
	 * The filename under which the report was saved.
	 */
	private String fileName = ""; //$NON-NLS-1$
	/**
	 * The PDF in <code>byte[]</code> form.
	 */
	private byte[] pdfData;

	/**
	 * Constructs a new default {@link de.xirp.report.ReportDescriptor} 
	 * object. Must be declared public because Hibernate needs this.
	 */
	public ReportDescriptor() {

	}

	/**
	 * Returns the creation time of the report.
	 * 
	 * @return The creation time.
	 */
	@Column(name = "REPORT_CREATION_TIME")//$NON-NLS-1$
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * Returns the file name of the report.
	 * 
	 * @return The file name.
	 */
	@Column(name = "REPORT_FILENAME")//$NON-NLS-1$
	public String getFileName() {
		return fileName;
	}

	/**
	 * Returns the id of the report in the database.
	 * 
	 * @return The id.
	 */
	@Id
	@GeneratedValue
	@Column(name = "REPORT_ID")//$NON-NLS-1$
	public long getId() {
		return id;
	}

	/**
	 * Returns the PDF data of the report in the database.
	 * 
	 * @return The PDF data.
	 */
	@Lob
	@Column(name = "REPORT_PDF_DATA")//$NON-NLS-1$
	public byte[] getPdfData() {
		return pdfData;
	}

	/**
	 * Returns the name of the report.
	 * 
	 * @return The report name.
	 */
	@Column(name = "REPORT_NAME")//$NON-NLS-1$
	public String getReportName() {
		return reportName;
	}

	/**
	 * Returns the robot name of the robot the report
	 * was generated for.
	 * 
	 * @return The robot name.
	 */
	@Column(name = "REPORT_ROBOTNAME")//$NON-NLS-1$
	public String getRobotName() {
		return robotName;
	}

	/**
	 * Sets the creation time of the report.
	 * 
	 * @param creationTime 
	 * 			The creation time to set.
	 */
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * Sets the file name of the report. 
	 * Furthermore the byte data of the given file
	 * is written to the data byte array.
	 * 
	 * @param fileName 
	 * 				The fileName to set.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
		try {
			setPdfData(FileUtils.readFileToByteArray(new File(Constants.REPORT_DIR,
					this.fileName)));
		}
		catch (IOException e) {
			setPdfData(new byte[] {});
			logClass.warn("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
	}

	/**
	 * Sets the id of the descriptor. This <b>must</b> be private
	 * because Hibernate automatically sets the id when persisting
	 * the bean to the database. This method is then called by
	 * reflection.
	 * 
	 * @param id 
	 * 			The id to set.
	 */
	@SuppressWarnings("unused")//$NON-NLS-1$
	private void setId(Long id) {
		this.id = id;
	}

	/**
	 * Sets the byte data of the PDF.
	 * 
	 * @param pdfData 
	 * 			The PDF data to set.
	 */
	private void setPdfData(byte[] pdfData) {
		this.pdfData = pdfData;
	}

	/**
	 * Sets the report name.
	 * 
	 * @param reportName 
	 * 			The report name to set.
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	/**
	 * Sets the robot name.
	 * 
	 * @param robotName 
	 * 			The robot name to set.
	 */
	public void setRobotName(String robotName) {
		this.robotName = robotName;
	}
}
