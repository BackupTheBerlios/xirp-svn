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
 * ChartUtil.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 26.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.io.CSV;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import de.xirp.db.Observed;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Util;

/**
 * Util class containing several export methods for charts and
 * datasets. Export to PDF, CSV, PNG and JPG are supported.
 * 
 * @see org.jfree.chart.JFreeChart
 * @author Matthias Gernand
 */
public final class ChartUtil {

	/**
	 * The time format string.
	 */
	private static final String TIME_FORMAT = "yyyy-MM-dd_HH-mm-ss-SSS"; //$NON-NLS-1$
	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(ChartUtil.class);

	/**
	 * Exports the given chart to PDF, JPG or PNG if the corresponding
	 * export flag is set and the chart is not null. The given dataset
	 * is exported to CSV if the CSV export flag is set and the
	 * dataset is not null.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param dataset
	 *            The dataset to export.
	 * @param robotName
	 *            The robot name for which the chart/dataset is
	 *            exported.
	 * @param pdf
	 *            <code>true</code> exports as PDF<br>
	 *            <code>false</code> do not export as PDF.
	 * @param png
	 *            <code>true</code> exports as PNG<br>
	 *            <code>false</code> do not export as PNG.
	 * @param jpg
	 *            <code>true</code> exports as JPG<br>
	 *            <code>false</code> do not export as JPG.
	 * @param csv
	 *            <code>true</code> exports as CSV<br>
	 *            <code>false</code> do not export as CSV.
	 * @see org.jfree.chart.JFreeChart
	 * @see org.jfree.data.time.TimeSeriesCollection
	 */
	public static void exportChart(JFreeChart chart,
			TimeSeriesCollection dataset, String robotName, boolean pdf,
			boolean png, boolean jpg, boolean csv) {
		if (chart != null) {
			if (pdf) {
				exportPDF(chart, robotName);
			}
			if (png) {
				exportPNG(chart, robotName);
			}
			if (jpg) {
				exportJPG(chart, robotName);
			}
		}
		if (csv && (dataset != null)) {
			exportCSV(dataset, robotName);
		}
	}

	/**
	 * Exports the given dataset as CSV.
	 * 
	 * @param dataset
	 *            The dataset to export.
	 * @param robotName
	 *            The robot name for which the dataset is exported.
	 * @see org.jfree.data.time.TimeSeriesCollection
	 */
	private static void exportCSV(TimeSeriesCollection dataset, String robotName) {
		for (Object obj : dataset.getSeries( )) {
			List<Observed> obs = new ArrayList<Observed>( );
			TimeSeries ts = (TimeSeries) obj;
			String observedKey = (String) ts.getKey( );
			for (Object obj2 : ts.getItems( )) {
				TimeSeriesDataItem tsdi = (TimeSeriesDataItem) obj2;
				Observed o = new Observed( );
				o.setObservedKey(observedKey);
				o.setTimestamp(tsdi.getPeriod( ).getStart( ).getTime( ));
				o.setValue(tsdi.getValue( ).doubleValue( ));
				obs.add(o);
			}
			exportCSV(obs, robotName);
		}
	}

	/**
	 * Exports the list of {@link de.xirp.db.Observed} as
	 * CSV to the given {@link java.io.File}.
	 * 
	 * @param obs
	 *            The list of <code>Observed</code> to export.
	 * @param writeTo
	 *            The file to write the CSV to.
	 * @see de.xirp.db.Observed
	 */
	public static void exportCSV(List<Observed> obs, File writeTo) {
		final String COLUMN_SEP = ", "; //$NON-NLS-1$
		final String TEXT_SEP = "\""; //$NON-NLS-1$
		final String LINE_SEP = Constants.LINE_SEPARATOR;

		StringBuilder buf = new StringBuilder(256);
		buf.append(TEXT_SEP +
				I18n.getString("ChartUtil.text.timestamp") + TEXT_SEP + COLUMN_SEP + TEXT_SEP + I18n.getString("ChartUtil.text.key") + TEXT_SEP + COLUMN_SEP + TEXT_SEP + I18n.getString("ChartUtil.text.value") + TEXT_SEP + LINE_SEP); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		for (Observed o : obs) {
			buf.append(TEXT_SEP + o.getTimestamp( ) + TEXT_SEP + COLUMN_SEP +
					TEXT_SEP + o.getObservedKey( ) + TEXT_SEP + COLUMN_SEP +
					TEXT_SEP + o.getValue( ) + TEXT_SEP + LINE_SEP);
		}

		try {
			FileUtils.writeStringToFile(writeTo, buf.toString( ), "unicode"); //$NON-NLS-1$
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Exports the list of {@link de.xirp.db.Observed} as
	 * CSV.
	 * 
	 * @param obs
	 *            The list of <code>Observed</code> to export.
	 * @param robotName
	 *            The robot name for which the dataset is exported.
	 * @see de.xirp.db.Observed
	 */
	public static void exportCSV(List<Observed> obs, String robotName) {
		createDirectories(robotName);

		final String COLUMN_SEP = ", "; //$NON-NLS-1$
		final String TEXT_SEP = "\""; //$NON-NLS-1$
		final String LINE_SEP = Constants.LINE_SEPARATOR;

		StringBuilder buf = new StringBuilder(256);
		buf.append(TEXT_SEP +
				I18n.getString("ChartUtil.text.timestamp") + TEXT_SEP + COLUMN_SEP + TEXT_SEP + I18n.getString("ChartUtil.text.key") + TEXT_SEP + COLUMN_SEP + TEXT_SEP + I18n.getString("ChartUtil.text.value") + TEXT_SEP + LINE_SEP); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		for (Observed o : obs) {
			buf.append(TEXT_SEP + o.getTimestamp( ) + TEXT_SEP + COLUMN_SEP +
					TEXT_SEP + o.getObservedKey( ) + TEXT_SEP + COLUMN_SEP +
					TEXT_SEP + o.getValue( ) + TEXT_SEP + LINE_SEP);
		}

		String fileName = "exported_data_" + Util.getTimeAsString(new Date( ), TIME_FORMAT); //$NON-NLS-1$
		fileName = fileName + ".csv"; //$NON-NLS-1$
		File f = new File(Constants.CHART_DIR + File.separator + robotName +
				File.separator + "csv", fileName); //$NON-NLS-1$

		try {
			FileUtils.writeStringToFile(f, buf.toString( ), "unicode"); //$NON-NLS-1$
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Exports the given chart as PNG.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param robotName
	 *            The robot name for which the dataset is exported.
	 * @see org.jfree.chart.JFreeChart
	 */
	public static void exportPNG(JFreeChart chart, String robotName) {
		createDirectories(robotName);
		String fileName = generateFileName(chart);
		fileName = fileName + ".png"; //$NON-NLS-1$
		File f = new File(Constants.CHART_DIR + File.separator + robotName +
				File.separator + "png", fileName); //$NON-NLS-1$
		exportPNG(chart, 800, 600, f);
	}

	/**
	 * Exports the given chart as JPG.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param robotName
	 *            The robot name for which the dataset is exported.
	 * @see org.jfree.chart.JFreeChart
	 */
	public static void exportJPG(JFreeChart chart, String robotName) {
		createDirectories(robotName);
		String fileName = generateFileName(chart);
		fileName = fileName + ".jpg"; //$NON-NLS-1$
		File f = new File(Constants.CHART_DIR + File.separator + robotName +
				File.separator + "jpg", fileName); //$NON-NLS-1$
		exportJPG(chart, 800, 600, f);
	}

	/**
	 * Exports the given chart as PDF.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param robotName
	 *            The robot name for which the dataset is exported.
	 * @see org.jfree.chart.JFreeChart
	 */
	public static void exportPDF(JFreeChart chart, String robotName) {
		createDirectories(robotName);
		String fileName = generateFileName(chart);
		fileName = fileName + ".pdf"; //$NON-NLS-1$
		File f = new File(Constants.CHART_DIR + File.separator + robotName +
				File.separator + "pdf", fileName); //$NON-NLS-1$
		exportPDF(chart, 800, 600, f);
	}

	/**
	 * Creates the chart directories for the given robot name. A
	 * directory of the mane of the robot is created in the
	 * <code>charts</code> folder. Four directories for CSV, PDF,
	 * PNG and JPG files are created in that robot folder.
	 * 
	 * @param robotName
	 *            The robot name for which the directories are
	 *            created.
	 */
	private static void createDirectories(String robotName) {
		File top = new File(Constants.CHART_DIR);
		File robot = new File(top, robotName);
		robot.mkdirs( );
		File csv = new File(robot, "csv"); //$NON-NLS-1$
		csv.mkdirs( );
		File pdf = new File(robot, "pdf"); //$NON-NLS-1$
		pdf.mkdirs( );
		File png = new File(robot, "png"); //$NON-NLS-1$
		png.mkdirs( );
		File jpg = new File(robot, "jpg"); //$NON-NLS-1$
		jpg.mkdirs( );
	}

	/**
	 * Exports the given chart as PDF in the specified size.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param width
	 *            The desired width of the PDF.
	 * @param height
	 *            The desired height of the PDF.
	 * @see org.jfree.chart.JFreeChart
	 */
	public static void exportPDF(JFreeChart chart, int width, int height) {
		String fileName = generateFileName(chart);
		fileName = fileName + ".pdf"; //$NON-NLS-1$
		File f = new File(Constants.CHART_DIR, fileName);
		exportPDF(chart, width, height, f);
	}

	/**
	 * Exports the given chart as PNG in the specified size.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param width
	 *            The desired width of the PNG.
	 * @param height
	 *            The desired height of the PNG.
	 * @see org.jfree.chart.JFreeChart
	 */
	public static void exportPNG(JFreeChart chart, int width, int height) {
		String fileName = generateFileName(chart);
		fileName = fileName + ".png"; //$NON-NLS-1$
		File f = new File(Constants.CHART_DIR, fileName);
		exportPNG(chart, width, height, f);
	}

	/**
	 * Exports the given chart as JPG in the specified size.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param width
	 *            The desired width of the JPG.
	 * @param height
	 *            The desired height of the JPG.
	 * @see org.jfree.chart.JFreeChart
	 */
	public static void exportJPG(JFreeChart chart, int width, int height) {
		String fileName = generateFileName(chart);
		fileName = fileName + ".jpg"; //$NON-NLS-1$
		File f = new File(Constants.CHART_DIR, fileName);
		exportJPG(chart, width, height, f);
	}

	/**
	 * Generates a file name from a chart. The name consist of two
	 * parts: <br>
	 * <br>
	 * <code>exported_chart_</code> as prefix and the current time
	 * formated using the
	 * {@link de.xirp.chart.ChartUtil#TIME_FORMAT} format
	 * string.
	 * 
	 * @param chart
	 *            The chart to generate a file name for.
	 * @return A <code>String</code> containing the generated file
	 *         name.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.util.Util#getTimeAsString(java.util.Date
	 *      date, java.lang.String format)
	 */
	private static String generateFileName(JFreeChart chart) {
		return "exported_chart_" + Util.getTimeAsString(new Date( ), TIME_FORMAT); //$NON-NLS-1$
	}

	/**
	 * Exports the given chart as PDF in the specified size. The
	 * export is written to the given {@link java.io.File}.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param width
	 *            The desired width of the PDF.
	 * @param height
	 *            The desired height of the PDF.
	 * @param file
	 *            The <code>File<code> to write the PDF to.
	 * 
	 * @see org.jfree.chart.JFreeChart
	 */
	private static void exportPDF(JFreeChart chart, int width, int height,
			File file) {
		exportPDF(chart, width, height, file.getAbsolutePath( ));
	}

	/**
	 * Exports the given chart as PNG in the specified size. The
	 * export is written to the given {@link java.io.File}.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param width
	 *            The desired width of the PNG.
	 * @param height
	 *            The desired height of the PNG.
	 * @param file
	 *            The <code>File<code> to write the PNG to.
	 * 
	 * @see org.jfree.chart.JFreeChart
	 */
	private static void exportPNG(JFreeChart chart, int width, int height,
			File file) {
		exportPNG(chart, width, height, file.getAbsolutePath( ));
	}

	/**
	 * Exports the given chart as JPG in the specified size. The
	 * export is written to the given {@link java.io.File}.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param width
	 *            The desired widthdraw of the JPG.
	 * @param height
	 *            The desired height of the JPG.
	 * @param file
	 *            The <code>File<code> to write the JPG to.
	 * 
	 * @see org.jfree.chart.JFreeChart
	 */
	private static void exportJPG(JFreeChart chart, int width, int height,
			File file) {
		exportJPG(chart, width, height, file.getAbsolutePath( ));
	}

	/**
	 * Exports the given chart as PNG in the specified size. The
	 * export is written to the given path.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param width
	 *            The desired width of the PNG.
	 * @param height
	 *            The desired height of the PNG.
	 * @param path
	 *            The path to write the PNG to.
	 * @see org.jfree.chart.JFreeChart
	 */
	private static void exportPNG(JFreeChart chart, int width, int height,
			String path) {

		try {
			ChartUtilities.saveChartAsPNG(new File(path), chart, width, height);
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
	}

	/**
	 * Exports the given chart as JPG in the specified size. The
	 * export is written to the given path.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param width
	 *            The desired width of the JPG.
	 * @param height
	 *            The desired height of the JPG.
	 * @param path
	 *            The path to write the JPG to.
	 * @see org.jfree.chart.JFreeChart
	 */
	private static void exportJPG(JFreeChart chart, int width, int height,
			String path) {

		try {
			ChartUtilities.saveChartAsJPEG(new File(path), chart, width, height);
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
	}

	/**
	 * Exports the given chart as PDF in the specified size. The
	 * export is written to the given path.
	 * 
	 * @param chart
	 *            The chart to export.
	 * @param width
	 *            The desired width of the PDF.
	 * @param height
	 *            The desired height of the PDF.
	 * @param path
	 *            The path to write the PDF to.
	 * @see org.jfree.chart.JFreeChart
	 */
	private static void exportPDF(JFreeChart chart, int width, int height,
			String path) {
		Document document = new Document(new Rectangle(width, height));
		try {
			PdfWriter writer;
			writer = PdfWriter.getInstance(document, new FileOutputStream(path));
			document.open( );
			PdfContentByte cb = writer.getDirectContent( );
			PdfTemplate tp = cb.createTemplate(width, height);
			Graphics2D g2d = tp.createGraphics(width,
					height,
					new DefaultFontMapper( ));
			Rectangle2D r2d = new Rectangle2D.Double(0, 0, width, height);
			chart.draw(g2d, r2d);
			g2d.dispose( );
			cb.addTemplate(tp, 0, 0);
		}
		catch (Exception e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
		document.close( );
	}

	/**
	 * Returns a {@link org.jfree.data.category.CategoryDataset}
	 * generated from a CSV {@link java.io.File}.
	 * 
	 * @param csvFile
	 *            The file to read the values from.
	 * @return A <code>CategoryDataset</code> generated from a csv
	 *         file.
	 * @see org.jfree.data.category.CategoryDataset
	 */
	public static CategoryDataset getCategoryDatasetFromCSV(File csvFile) {
		CSV csv = new CSV(',', '"');
		CategoryDataset dataset = null;
		try {
			FileReader fr = new FileReader(csvFile);
			dataset = csv.readCategoryDataset(fr);
		}
		catch (FileNotFoundException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
		return dataset;
	}
}
