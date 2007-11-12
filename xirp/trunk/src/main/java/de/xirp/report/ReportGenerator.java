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
 * ReportGenerator.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 17.04.2006:		Created by Matthias Gernand.
 */
package de.xirp.report;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import de.xirp.db.ReportDatabaseUtil;
import de.xirp.report.data.ContentPart;
import de.xirp.report.data.ContentPartImage;
import de.xirp.report.data.ContentPartList;
import de.xirp.report.data.ContentPartTable;
import de.xirp.report.data.ContentPartTableRow;
import de.xirp.report.data.ContentPartText;
import de.xirp.report.data.ContentPartTextParagraph;
import de.xirp.report.data.Header;
import de.xirp.report.data.IContentPartItem;
import de.xirp.report.data.ContentPartList.ListType;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Util;

/**
 * The class generates a PDF from a
 * {@link de.xirp.report.Report} object. Therefore the
 * generator offers one publicly available static method. This method
 * gets a {@link de.xirp.report.Report} object as
 * parameter. The PDF is generated from the object and is saved to the
 * database and as file in the <code>reports</code> directory. The
 * file name is generated automatically from the available
 * informations.
 * 
 * @author Matthias Gernand
 * @see de.xirp.report.Report
 */
public final class ReportGenerator {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(ReportGenerator.class);
	/**
	 * The {@link com.lowagie.text.Document document} data structure
	 * for generating a PDF in memory.
	 * 
	 * @see com.lowagie.text.Document
	 */
	private static Document document = null;
	/**
	 * The {@link de.xirp.report.Report report} data
	 * structure which contains the report data.
	 * 
	 * @see de.xirp.report.Report
	 */
	private static Report report = null;
	/**
	 * A {@link com.lowagie.text.Font font} for the header.
	 * 
	 * @see com.lowagie.text.Font
	 */
	private static final Font HEADER = new Font(Font.HELVETICA, 24, Font.BOLD);
	/**
	 * A {@link com.lowagie.text.Font font} for the images.
	 * 
	 * @see com.lowagie.text.Font
	 */
	private static final Font IMAGE = new Font(Font.HELVETICA, 8, Font.ITALIC);
	/**
	 * A {@link com.lowagie.text.Font font} for the text.
	 * 
	 * @see com.lowagie.text.Font
	 */
	private static final Font TEXT = new Font(Font.HELVETICA, 12, Font.NORMAL);
	/**
	 * A {@link com.lowagie.text.Font font} for the paragraph headers.
	 * 
	 * @see com.lowagie.text.Font
	 */
	private static final Font PARA_HEADER = new Font(Font.HELVETICA,
			14,
			Font.BOLD);
	/**
	 * The counter for the images contained in the report.
	 */
	private static int imageCounter = 0;
	/**
	 * The counter for the tables contained in the report.
	 */
	private static int tableCounter = 0;
	// private static int videoCounter = 0;
	/**
	 * A {@link com.lowagie.text.Paragraph paragraph} in a PDF.
	 * 
	 * @see com.lowagie.text.Paragraph
	 */
	private static Paragraph para;
	/**
	 * A {@link com.lowagie.text.pdf.PdfWriter writer} which actually
	 * writes the PDF.
	 * 
	 * @see com.lowagie.text.pdf.PdfWriter
	 */
	private static PdfWriter writer;
	/**
	 * The first content
	 * {@link de.xirp.report.data.IContentPartItem item}.
	 * 
	 * @see de.xirp.report.data.IContentPartItem
	 */
	private static IContentPartItem first;
	/**
	 * The report {@link java.io.File}.
	 */
	private static File reportFile;

	/**
	 * Default constructor declared private to avoid user errors and
	 * misuse.
	 */
	private ReportGenerator() {

	}

	/**
	 * Generates the report as PDF and saves it to the
	 * <code>reports</code> directory. After that the report PDF
	 * data is stored it the database. Therefore the
	 * {@link de.xirp.report.ReportDescriptor} object is
	 * created. This object can be persisted in a database using
	 * Hibernate.
	 * 
	 * @param report
	 *            The report to generate the PDF for.
	 * @return The path to the generated PDF document.
	 * @throws ReportException
	 *             when one of the required data objects is
	 *             <code>null</code>.
	 * @throws IOException
	 *             if something went wrong saving the PDF.
	 * @throws DocumentException
	 *             if something went wrong generating the PDF.
	 * @throws MalformedURLException
	 *             if something went wrong saving the PDF.
	 * @see de.xirp.report.ReportDescriptor
	 */
	public static String generateReport(Report report) throws ReportException,
			MalformedURLException, DocumentException, IOException {
		ReportGenerator.report = report;
		ReportDescriptor data = new ReportDescriptor( );
		data.setFileName(generate( ));
		data.setCreationTime(report.getHeader( ).getDate( ).getTime( ));
		data.setRobotName(report.getHeader( ).getRobot( ).getName( ));
		data.setReportName(report.getName( ));
		try {
			ReportDatabaseUtil.storePDF(data);
		}
		catch (Exception e) {
			logClass.error("Error storing report: " + e.getMessage( ) +
					Constants.LINE_SEPARATOR, e);
		}
		return data.getFileName( );
	}

	/**
	 * Generates the report and returns the path to the PDF file. This
	 * file is generated in the <code>reports</code> directory.
	 * 
	 * @return The path to the generated PDF document.
	 * @throws ReportException
	 *             when one of the required data objects is
	 *             <code>null</code>.
	 * @throws IOException
	 *             if something went wrong saving the PDF.
	 * @throws DocumentException
	 *             if something went wrong generating the PDF.
	 * @throws MalformedURLException
	 *             if something went wrong saving the PDF.
	 */
	private static String generate() throws ReportException,
			MalformedURLException, DocumentException, IOException {
		if (report.getName( ) == null) {
			throw new ReportException(I18n.getString("ReportGenerator.exception.reportNameIsNull")); //$NON-NLS-1$
		}
		else if (report.getName( ).equals("")) { //$NON-NLS-1$
			throw new ReportException(I18n.getString("ReportGenerator.exception.reportNameIsEmpty")); //$NON-NLS-1$
		}
		else if (report.getHeader( ) == null) {
			throw new ReportException(I18n.getString("ReportGenerator.exception.reportHeaderIsNUll")); //$NON-NLS-1$
		}
		else if (report.getContent( ) == null) {
			throw new ReportException(I18n.getString("ReportGenerator.exception.reportContentIsNull")); //$NON-NLS-1$
		}
		else {
			return generatePDF( );
		}
	}

	/**
	 * Generates the PDF from the
	 * {@link de.xirp.report.Report} and writes the PDF
	 * to a file. <br>
	 * <br>
	 * At first the title and header pages are created. After that the
	 * content pages are created.
	 * 
	 * @return The file name of the PDF.
	 * @throws IOException
	 *             if something went wrong saving the PDF.
	 * @throws DocumentException
	 *             if something went wrong generating the PDF.
	 * @throws MalformedURLException
	 *             if something went wrong saving the PDF.
	 * @see de.xirp.report.Report
	 */
	private static String generatePDF() throws DocumentException,
			MalformedURLException, IOException {
		String filename = report.getName( ).replaceAll(" ", "-") + "_report_" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ report.getHeader( ).getRobot( ).getName( ) + "_" //$NON-NLS-1$
				+ Util.getTimeAsString(report.getHeader( ).getDate( )) + ".pdf"; //$NON-NLS-1$

		reportFile = new File(Constants.REPORT_DIR + File.separator + filename);

		// creation of a document-object
		document = new Document(PageSize.A4);

		// we create a writer that listens to the document
		// and directs a PDF-stream to a file
		writer = PdfWriter.getInstance(document,
				new FileOutputStream(reportFile.getPath( )));
		writer.setPdfVersion(PdfWriter.VERSION_1_7);
		writer.setStrictImageSequence(true);

		// open the document
		document.open( );

		// add the title page
		addReportTitle( );
		addReportContent( );
		// close the document
		document.close( );

		return filename;
	}

	/**
	 * Adds the report title page to the PDF
	 * {@link com.lowagie.text.Document}.
	 * 
	 * @throws DocumentException
	 *             if something went wrong adding the page.
	 * @see com.lowagie.text.Document
	 */
	private static void addReportTitle() throws DocumentException {
		String heading = null;
		heading = I18n.getString("ReportGenerator.text.reportTitle", report.getName( ), //$NON-NLS-1$
				Constants.LINE_SEPARATOR,
				ApplicationManager.getCurrentRobot( ).getName( ));
		addTitle(heading);
		addHeader( );
	}

	/**
	 * Adds the title page with the given header string to the PDF
	 * {@link com.lowagie.text.Document}.
	 * 
	 * @param heading
	 *            The header text.
	 * @throws DocumentException
	 *             if something went wrong adding the page.
	 * @see com.lowagie.text.Document
	 */
	private static void addTitle(String heading) throws DocumentException {

		para = getParagraph(heading, HEADER, Element.ALIGN_CENTER);
		document.add(para);
		addSkip(3);
		para = getParagraph(Constants.NON_UNICODE_APP_NAME,
				IMAGE,
				Image.ALIGN_CENTER);
		document.add(para);

		addSkip( );

		para = getParagraph(I18n.getString("ReportGenerator.report.header.website"), TEXT, //$NON-NLS-1$
				Element.ALIGN_CENTER);
		document.add(para);

		para = getParagraph(Constants.LINE_SEPARATOR,
				TEXT,
				Element.ALIGN_CENTER);
		TEXT.setColor(new Color(0, 0, 255));
		Anchor anchor = new Anchor(I18n.getString("ReportGenerator.report.websiteLink"), TEXT); //$NON-NLS-1$
		anchor.setReference(I18n.getString("ReportGenerator.report.websiteLink")); //$NON-NLS-1$
		anchor.setName("homepage"); //$NON-NLS-1$
		para.add(anchor);
		document.add(para);
		document.newPage( );
		TEXT.setColor(new Color(0, 0, 0));

		document.newPage( );
	}

	/**
	 * Adds the header page to the PDF
	 * {@link com.lowagie.text.Document}.
	 * 
	 * @throws DocumentException
	 *             if something went wrong adding the page.
	 * @see com.lowagie.text.Document
	 */
	private static void addHeader() throws DocumentException {
		Header header = report.getHeader( );

		para = getParagraph(header.getTitle( ), HEADER, Element.ALIGN_CENTER);
		document.add(para);
		addSkip(4);
		List list = getList(ListType.BULLET);
		list.add(new ListItem(I18n.getString("ReportGenerator.report.header.created.date") //$NON-NLS-1$
				+
				" " + Util.getTimeAsString(header.getDate( ), "dd.MM.yyyy"))); //$NON-NLS-1$ //$NON-NLS-2$
		list.add(new ListItem(I18n.getString("ReportGenerator.report.header.created.time") //$NON-NLS-1$
				+
				" " //$NON-NLS-1$
				+ Util.getTimeAsString(header.getDate( ), "HH:mm") //$NON-NLS-1$
				+ I18n.getString("ReportGenerator.report.oClock"))); //$NON-NLS-1$
		list.add(new ListItem(I18n.getString("ReportGenerator.report.header.created.plugin") //$NON-NLS-1$
				+
				" " + header.getPlugin( ).getName( ))); //$NON-NLS-1$
		list.add(new ListItem(I18n.getString("ReportGenerator.report.header.created.robot") //$NON-NLS-1$
				+
				" " + header.getRobot( ).getName( ))); //$NON-NLS-1$
		document.add(list);
	}

	/**
	 * Adds the reports
	 * {@link de.xirp.report.data.ContentPart content}
	 * pages.
	 * 
	 * @throws IOException
	 *             if something went wrong saving the PDF.
	 * @throws DocumentException
	 *             if something went wrong adding the page.
	 * @throws MalformedURLException
	 *             if something went wrong saving the PDF.
	 * @see de.xirp.report.data.ContentPart
	 */
	private static void addReportContent() throws MalformedURLException,
			IOException, DocumentException {
		addParts( );
	}

	/**
	 * Adds the parts to the PDF {@link com.lowagie.text.Document}.
	 * 
	 * @throws MalformedURLException
	 *             if something went wrong saving the PDF.
	 * @throws IOException
	 *             if something went wrong saving the PDF.
	 * @throws DocumentException
	 *             if something went wrong adding the page.
	 * @see de.xirp.report.data.ContentPart
	 * @see com.lowagie.text.Document
	 */
	private static void addParts() throws MalformedURLException, IOException,
			DocumentException {
		for (ContentPart part : report.getContent( ).getParts( )) {
			first = part.getItems( ).get(0);
			addPart(part, first);
		}
	}

	/**
	 * Adds a
	 * {@link de.xirp.report.data.ContentPart part} to
	 * the PDF {@link com.lowagie.text.Document}.
	 * 
	 * @param part
	 *            The content part to add.
	 * @param first
	 *            The first added item.
	 * @throws MalformedURLException
	 *             if something went wrong saving the PDF.
	 * @throws IOException
	 *             if something went wrong saving the PDF.
	 * @throws DocumentException
	 *             if something went wrong adding the page.
	 * @see de.xirp.report.data.ContentPart
	 * @see de.xirp.report.data.IContentPartItem
	 * @see com.lowagie.text.Document
	 */
	private static void addPart(ContentPart part, IContentPartItem first)
			throws MalformedURLException, IOException, DocumentException {

		if (first instanceof ContentPartTable) {
			document.setPageSize(PageSize.A4.rotate( ));
		}
		document.newPage( );

		for (IContentPartItem item : part.getItems( )) {
			if (item instanceof ContentPartImage) {
				logClass.debug("IMAGE part created in report." + Constants.LINE_SEPARATOR); //$NON-NLS-1$
				addImage((ContentPartImage) item);
				addSkip( );
			}
			if (item instanceof ContentPartText) {
				logClass.debug("TEXT part created in report." + Constants.LINE_SEPARATOR); //$NON-NLS-1$
				addText((ContentPartText) item);
				addSkip( );
			}
			if (item instanceof ContentPartTable) {
				logClass.debug("TABLE part created in report." + Constants.LINE_SEPARATOR); //$NON-NLS-1$
				if (!first.equals(item)) {
					document.setPageSize(PageSize.A4.rotate( ));
					document.newPage( );
				}
				addTable((ContentPartTable) item);
				document.setPageSize(PageSize.A4);
				document.newPage( );
			}
			if (item instanceof ContentPartList) {
				logClass.debug("LIST part created in report." + Constants.LINE_SEPARATOR); //$NON-NLS-1$
				addList((ContentPartList) item);
				addSkip( );
			}
			// if (item instanceof ContentPartVideo) {
			// logClass.debug("VIDEO part created in
			// report."+Constants.LINE_SEPARATOR);
			// //$NON-NLS-1$
			// addVideo((ContentPartVideo) item);
			// addSkip( );
			// }
		}
		logClass.debug("Part finished." + Constants.LINE_SEPARATOR); //$NON-NLS-1$
	}

	/**
	 * Adds a
	 * {@link de.xirp.report.data.ContentPartText text}
	 * item to the PDF {@link com.lowagie.text.Document}.
	 * 
	 * @param item
	 *            The item to add.
	 * @throws DocumentException
	 *             if something went wrong adding the page.
	 * @see de.xirp.report.data.ContentPartText
	 * @see com.lowagie.text.Document
	 */
	private static void addText(ContentPartText item) throws DocumentException {
		for (ContentPartTextParagraph cptp : item.getParagraphs( )) {
			if (!cptp.getHeader( ).equals("")) { //$NON-NLS-1$
				para = getParagraph(cptp.getHeader( ),
						PARA_HEADER,
						Element.ALIGN_LEFT);
				document.add(para);
			}
			para = getParagraph(cptp.getText( ), TEXT, Element.ALIGN_JUSTIFIED);
			document.add(para);
			addSkip( );
		}
	}

	/**
	 * Adds a
	 * {@link de.xirp.report.data.ContentPartList list}
	 * item to the PDF {@link com.lowagie.text.Document}.
	 * 
	 * @param contentPartlist
	 *            The list item to add.
	 * @throws DocumentException
	 *             if something went wrong adding the page.
	 * @see de.xirp.report.data.ContentPartList
	 * @see com.lowagie.text.Document
	 */
	private static void addList(ContentPartList contentPartlist)
			throws DocumentException {
		List list = getList(contentPartlist.getType( ));

		for (String item : contentPartlist.getEntrys( )) {
			list.add(new ListItem(item));
		}
		document.add(list);
	}

	/**
	 * Adds a
	 * {@link de.xirp.report.data.ContentPartImage image}
	 * item to the PDF {@link com.lowagie.text.Document}.
	 * 
	 * @param item
	 *            The image item to add.
	 * @throws IOException
	 *             if something went wrong saving the PDF.
	 * @throws DocumentException
	 *             if something went wrong adding the page.
	 * @throws MalformedURLException
	 *             if something went wrong saving the PDF.
	 * @see de.xirp.report.data.ContentPartImage
	 * @see com.lowagie.text.Document
	 */
	private static void addImage(ContentPartImage item)
			throws MalformedURLException, IOException, DocumentException {

		imageCounter++;

		String path = ""; //$NON-NLS-1$
		File file = new File(item.getPath( ));
		if (file.exists( )) {
			path = item.getPath( );
		}

		Image image = Image.getInstance(path);
		image.setAlignment(Image.MIDDLE);
		int maxWidth = 500;
		float plain = image.plainWidth( );
		if (plain > maxWidth) {
			float percentage = maxWidth / plain;
			image.scalePercent(percentage * 100);
		}
		document.add(image);
		document.add(getParagraph(I18n.getString("ReportGenerator.report.document.image") //$NON-NLS-1$
				+
				" " + imageCounter + ": " + item.getShortDescription( ), //$NON-NLS-1$ //$NON-NLS-2$
				IMAGE,
				Element.ALIGN_CENTER));

	}

	// private static void addVideo(ContentPartVideo video)
	// throws DocumentException {
	// // Testweise mal ein video eingefügt.
	// document.add(new
	// Paragraph("Test"+Constants.LINE_SEPARATOR+Constants.LINE_SEPARATOR+Constants.LINE_SEPARATOR));
	// //$NON-NLS-1$
	// PdfContentByte cb = writer.getDirectContent( );
	// Annotation a = new Annotation(100f, 700f, 200f, 800f,
	// "./cards.mpg", //$NON-NLS-1$
	// "video/mpeg", true); //$NON-NLS-1$
	// document.add(a);
	// // draw rectangles to show where the annotations were added
	// cb.rectangle(100, 700, 100, 100);
	// cb.stroke( );
	// }

	/**
	 * Adds a
	 * {@link de.xirp.report.data.ContentPartTable table}
	 * item to the PDF {@link com.lowagie.text.Document}.
	 * 
	 * @param reportTable
	 *            The table to add.
	 * @throws DocumentException
	 *             if something went wrong adding the page.
	 * @see de.xirp.report.data.ContentPartTable
	 * @see com.lowagie.text.Document
	 */
	private static void addTable(ContentPartTable reportTable)
			throws DocumentException {

		tableCounter++;
		PdfPTable table = new PdfPTable(reportTable.getColumnCount( ));

		for (String cellHeader : reportTable.getHeader( ).getColumnHeaders( )) {
			table.addCell(cellHeader);
		}
		logClass.debug("Table row count: " + reportTable.getRows( ).size( ) //$NON-NLS-1$
				+ Constants.LINE_SEPARATOR);
		for (ContentPartTableRow row : reportTable.getRows( )) {
			for (int i = 0; i < reportTable.getColumnCount( ); i++) {
				table.addCell(row.getRowEntrys( ).get(i));
			}
		}

		// cell = new PdfPCell(new Paragraph("cell test1"));
		// cell.setBorderColor(new Color(255, 0, 0));
		// table.addCell(cell);
		// cell = new PdfPCell(new Paragraph("cell test2"));
		// cell.setColspan(2);
		// cell.setBackgroundColor(new Color(0xC0, 0xC0, 0xC0));
		// table.addCell(cell);

		document.add(table);
		document.add(getParagraph(I18n.getString("ReportGenerator.report.document.table") + " " //$NON-NLS-1$ //$NON-NLS-2$
				+ tableCounter + ": " //$NON-NLS-1$
				+ reportTable.getShortDescription( ),
				IMAGE,
				Element.ALIGN_CENTER));
	}

	/**
	 * Returns a default formatted
	 * {@link com.lowagie.text.Paragraph paragraph}.
	 * 
	 * @param text
	 *            The text for the paragraph.
	 * @param font
	 *            The font to use.
	 * @param align
	 *            The text alignment.
	 * @return The default formatted paragraph.
	 * @see com.lowagie.text.Paragraph
	 */
	private static Paragraph getParagraph(String text, Font font, int align) {
		Paragraph para = new Paragraph(text, font);
		para.setFirstLineIndent(0);
		para.setAlignment(align);
		return para;
	}

	/**
	 * Adds a one-line skip to the PDF
	 * {@link com.lowagie.text.Document}.
	 * 
	 * @throws DocumentException
	 *             if something went wrong adding the skip.
	 * @see com.lowagie.text.Document
	 */
	private static void addSkip() throws DocumentException {
		addSkip(1);
	}

	/**
	 * Adds a skip for the given count of lines to the PDF
	 * {@link com.lowagie.text.Document}.
	 * 
	 * @param count
	 *            The count of lines to skip.
	 * @throws DocumentException
	 *             if something went wrong adding the skip.
	 * @see com.lowagie.text.Document
	 */
	private static void addSkip(int count) throws DocumentException {
		Paragraph para;
		if (count == 1) {
			para = new Paragraph(Constants.LINE_SEPARATOR +
					Constants.LINE_SEPARATOR);
			document.add(para);
		}
		else {
			for (int i = 0; i < count; i++) {
				para = new Paragraph(Constants.LINE_SEPARATOR +
						Constants.LINE_SEPARATOR);
				document.add(para);
			}
		}
	}

	/**
	 * Returns a default formatted list for the given list type.
	 * 
	 * @param type
	 *            The list type.
	 * @return A default formatted list.
	 */
	private static List getList(ListType type) {
		switch (type) {
			case BULLET:
				List list = new List(false, 20);
				list.setListSymbol(new Chunk("\u2022", FontFactory.getFont( //$NON-NLS-1$
				FontFactory.HELVETICA,
						12,
						Font.BOLD)));
				return list;
			case DASH:
				return new List(false, 20);
			case NUMBER:
				return new List(true, 20);
		}
		return null;
	}
}
