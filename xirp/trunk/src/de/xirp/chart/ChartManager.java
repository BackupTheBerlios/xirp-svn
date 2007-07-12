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
 * ChartManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 14.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.util.RelativeDateFormat;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.util.Rotation;

import de.xirp.chart.ChartOptions.OptionName;
import de.xirp.db.ChartDatabaseUtil;
import de.xirp.db.Observed;
import de.xirp.db.Record;
import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.ui.widgets.dialogs.ChartConfigDialog.Interval;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This manager provides several methods for creating charts from
 * {@link de.xirp.db.Record}.
 * 
 * @author Matthias Gernand
 * @see de.xirp.db.Record
 */
public final class ChartManager extends AbstractManager {

	/**
	 * The "no data available" text.
	 */
	public static final String NO_DATA_AVAILABLE = I18n.getString("ChartManager.text.notdata"); //$NON-NLS-1$
	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(ChartManager.class);
	/**
	 * An option object.
	 */
	private static ChartOptions options;
	/**
	 * The robot name.
	 */
	private static String robotName;
	/**
	 * The possible threshold to show.
	 */
	private static double threshold = -1.0;

	/**
	 * Constructs a new manager instance. <br>
	 * <br>
	 * The manager is initialized on startup. Never call this on your
	 * own. Use the statically provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance already exists.
	 */
	public ChartManager() throws InstantiationException {
		super( );
	}

	/**
	 * Returns a time series chart. It is generated from the given
	 * {@link de.xirp.db.Record} and key array. The
	 * complete record is evaluated.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @return A time series chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 */
	public static JFreeChart createTimeSeriesChart(Record record, String[] keys) {
		return createTimeSeriesChart(record, keys, 0, 0, true);
	}

	/**
	 * Returns a time series chart. It is generated from the given
	 * {@link de.xirp.db.Record} and key array. The
	 * record is evaluated from the specified start to the specified
	 * stop time.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @param start
	 *            The starting time inside the record.
	 * @param stop
	 *            the stop time inside the record.
	 * @return A time series chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 */
	public static JFreeChart createTimeSeriesChart(Record record,
			String[] keys, long start, long stop) {
		return createTimeSeriesChart(record, keys, start, stop, false);
	}

	/**
	 * Returns a time series chart. It is generated from the given
	 * {@link de.xirp.db.Record} and key array. The
	 * record is evaluated from the start to the stop time specified
	 * in the given
	 * {@link de.xirp.ui.widgets.dialogs.ChartConfigDialog.Interval}.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @param interval
	 *            The interval to use.
	 * @return A time series chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 * @see de.xirp.ui.widgets.dialogs.ChartConfigDialog.Interval
	 */
	public static JFreeChart createTimeSeriesChart(Record record,
			String[] keys, Interval interval) {
		return createTimeSeriesChart(record,
				keys,
				interval.start.getTime( ),
				interval.stop.getTime( ),
				false);
	}

	/**
	 * Returns a list of time series charts. They are generated from
	 * the given <code>Map&lt;Record, Interval&gt;</code> and the
	 * key array. A chart is generated for each
	 * {@link de.xirp.db.Record} in the map using the
	 * associated
	 * {@link de.xirp.ui.widgets.dialogs.ChartConfigDialog.Interval}
	 * and the keys array.
	 * 
	 * @param recordIntervalMap
	 *            Records mapped to the interval to use.
	 * @param keys
	 *            The keys to use.
	 * @return A <code>List&lt;JFreeChart&gt;</code> with time
	 *         series charts.
	 */
	public static List<JFreeChart> createTimeSeriesChart(
			Map<Record, Interval> recordIntervalMap, String[] keys) {

		List<JFreeChart> charts = new ArrayList<JFreeChart>( );
		Interval i;
		for (Record record : recordIntervalMap.keySet( )) {
			i = recordIntervalMap.get(record);
			try {
				charts.add(createTimeSeriesChart(record,
						keys,
						i.start.getTime( ),
						i.stop.getTime( ),
						false));
			}
			catch (RuntimeException e) {
				logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
						+ Constants.LINE_SEPARATOR, e);
			}
		}
		return charts;
	}

	/**
	 * Returns a time series chart. The chart is generated from the
	 * given {@link de.xirp.db.Record} and key array. The
	 * record is evaluated from the given start to the given stop
	 * time. The flag <code>origTime</code> is an indicator for the
	 * database query. If it is <code>true</code> the start and stop
	 * time is overridden and the original time of the record is used.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @param start
	 *            The start time.
	 * @param stop
	 *            The stop time.
	 * @param origTime
	 *            Flag for indicating if the original time should be
	 *            used.
	 * @return A time series chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 */
	private static JFreeChart createTimeSeriesChart(Record record,
			String[] keys, long start, long stop, boolean origTime) {

		String chartTitle = createChartTitle(record, keys);

		List<Observed> all = new ArrayList<Observed>( );
		TimeSeriesCollection dataset = createTimeSeriesAndFillAllObservedList(all,
				record,
				keys,
				origTime,
				start,
				stop);

		JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle,
				I18n.getString("ChartManager.text.time"), I18n.getString("ChartManager.text.value"), dataset, true, true, false); //$NON-NLS-1$ //$NON-NLS-2$

		XYPlot plot = (XYPlot) chart.getPlot( );
		setXYPlot(plot, new Date(record.getStart( )));

		exportAutomatically(all, chart);

		return chart;
	}

	/**
	 * Returns a list of pie charts. They are genrated from the given
	 * list of records and the key array. The option if relative
	 * values should be used is done inside this method querying the
	 * <code>options</code> field:
	 * <code>options.is(OptionName.USE_RELATIVE)</code>.
	 * 
	 * @param records
	 *            The <code>List&lt;Record&gt;</code> containing the
	 *            data.
	 * @param keys
	 *            The keys to use.
	 * @return A <code>List&lt;JFreeChart&gt;</code> with pie
	 *         charts.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 * @see de.xirp.chart.ChartOptions
	 * @see de.xirp.chart.ChartOptions.OptionName
	 */
	public static List<JFreeChart> createPieChart(List<Record> records,
			String[] keys) {
		List<JFreeChart> charts = new ArrayList<JFreeChart>( );
		boolean relative = options.is(OptionName.USE_RELATIVE);
		for (Record record : records) {
			charts.add(createPieChart(record, keys, relative));
		}
		return charts;
	}

	/**
	 * Returns a pie chart. The chart is generated from the given
	 * {@link de.xirp.db.Record} and key array. The
	 * option if relative values should be used is done inside this
	 * method querying the <code>options</code> field:
	 * <code>options.is(OptionName.USE_RELATIVE)</code>.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            the keys to use.
	 * @return A pie chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 * @see de.xirp.chart.ChartOptions
	 * @see de.xirp.chart.ChartOptions.OptionName
	 */
	public static JFreeChart createPieChart(Record record, String[] keys) {
		boolean relative = options.is(OptionName.USE_RELATIVE);
		return createPieChart(record, keys, relative);
	}

	/**
	 * Returns a pie chart. The chart is generated from the given
	 * {@link de.xirp.db.Record} and key array. The flag
	 * <code>relative</code> indicated whether or not relative
	 * values should be used.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @param relative
	 *            Flag indicating if relative values should be used.
	 * @return A pie chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 */
	private static JFreeChart createPieChart(Record record, String[] keys,
			boolean relative) {
		String chartTitle = (relative ? I18n.getString("ChartManager.text.relative") : I18n.getString("ChartManager.text.absolute")) //$NON-NLS-1$ //$NON-NLS-2$
				+
				I18n.getString("ChartManager.text.occurencesOfKey") + record.getName( ) + ": "; //$NON-NLS-1$ //$NON-NLS-2$

		DefaultPieDataset dataset = new DefaultPieDataset( );

		Map<String, Long> values;
		values = ChartDatabaseUtil.getValuesForRecord(record, keys, relative);

		for (String s : values.keySet( )) {
			dataset.setValue(s, values.get(s));
		}

		JFreeChart chart;
		if (options.is(OptionName.THREE_D)) {
			chart = ChartFactory.createPieChart3D(chartTitle,
					dataset,
					true,
					true,
					false);

			PiePlot3D plot = (PiePlot3D) chart.getPlot( );
			plot.setStartAngle(290);
			plot.setDirection(Rotation.CLOCKWISE);
			plot.setForegroundAlpha(0.5f);
			plot.setNoDataMessage(NO_DATA_AVAILABLE);
			plot.setLabelGenerator(new CustomLabelGenerator(options));
		}
		else {
			chart = ChartFactory.createPieChart(chartTitle,
					dataset,
					true,
					true,
					false);

			PiePlot plot = (PiePlot) chart.getPlot( );
			plot.setSectionOutlinesVisible(true);
			plot.setNoDataMessage(NO_DATA_AVAILABLE);
			plot.setCircular(false);
			plot.setLabelGap(0.02);
			plot.setLabelGenerator(new CustomLabelGenerator(options));
		}

		exportAutomatically(null, chart);

		return chart;
	}

	/**
	 * Returns a bar chart. The chart is generated from the given
	 * {@link de.xirp.db.Record} and key array. The
	 * record is evaluated completely.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @return A bar chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 */
	public static JFreeChart createBarChart(Record record, String[] keys) {
		return createBarChart(record, keys, 0, 0, true);
	}

	/**
	 * Returns a bar chart. The chart is generated from the given
	 * {@link de.xirp.db.Record} and key array. The
	 * record is evaluated from the given start to the given stop
	 * time.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @param start
	 *            The start time.
	 * @param stop
	 *            The stop time.
	 * @return A bar chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 */
	public static JFreeChart createBarChart(Record record, String[] keys,
			long start, long stop) {
		return createBarChart(record, keys, start, stop, false);
	}

	/**
	 * Returns a list of bar charts. The charts are generated using
	 * the given record->interval map and the key array. One chart is
	 * generated for each record in the map. The records are evaluated
	 * for the mapped interval.
	 * 
	 * @param recordIntervalMap
	 *            The records with the data and the interval.
	 * @param keys
	 *            The keys to use.
	 * @return A list of bar charts.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 * @see de.xirp.ui.widgets.dialogs.ChartConfigDialog.Interval
	 */
	public static List<JFreeChart> createBarChart(
			Map<Record, Interval> recordIntervalMap, String[] keys) {

		List<JFreeChart> charts = new ArrayList<JFreeChart>( );
		Interval i;
		for (Record record : recordIntervalMap.keySet( )) {
			i = recordIntervalMap.get(record);
			try {
				charts.add(createBarChart(record,
						keys,
						i.start.getTime( ),
						i.stop.getTime( ),
						false));
			}
			catch (RuntimeException e) {
				logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
						+ Constants.LINE_SEPARATOR, e);
			}
		}
		return charts;
	}

	/**
	 * Returns a bar chart. The chart is generated form the given
	 * {@link de.xirp.db.Record} and key array. The
	 * record is evaluated using the start and stop time in the given
	 * {@link de.xirp.ui.widgets.dialogs.ChartConfigDialog.Interval}.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @param interval
	 *            The time interval to use.
	 * @return A bar chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 * @see de.xirp.ui.widgets.dialogs.ChartConfigDialog.Interval
	 */
	public static JFreeChart createBarChart(Record record, String[] keys,
			Interval interval) {
		return createBarChart(record,
				keys,
				interval.start.getTime( ),
				interval.stop.getTime( ),
				false);
	}

	/**
	 * Returns a bar chart. The chart is generated from the given
	 * {@link de.xirp.db.Record} and key array. The
	 * record is evaluated from the given start to the given stop
	 * time, or is evaluated completely if the flag
	 * <code>origTime</code> is set to <code>true</code>.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @param start
	 *            The start time.
	 * @param stop
	 *            The stop time.
	 * @param origTime
	 *            A flag indicating if the original time should be
	 *            used.
	 * @return A bar chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 */
	private static JFreeChart createBarChart(Record record, String[] keys,
			long start, long stop, boolean origTime) {

		String chartTitle = createChartTitle(record, keys);

		List<Observed> all = new ArrayList<Observed>( );
		TimeSeriesCollection dataset = createTimeSeriesAndFillAllObservedList(all,
				record,
				keys,
				origTime,
				start,
				stop);

		JFreeChart chart = ChartFactory.createXYBarChart(chartTitle,
				I18n.getString("ChartManager.text.time"), //$NON-NLS-1$
				true,
				I18n.getString("ChartManager.text.value"), dataset, PlotOrientation.VERTICAL, true, true, //$NON-NLS-1$
				false);

		XYPlot plot = (XYPlot) chart.getPlot( );
		setXYPlot(plot, new Date(record.getStart( )));

		exportAutomatically(all, chart);

		return chart;
	}

	/**
	 * Returns a scatter chart. The chart is generated from the given
	 * {@link de.xirp.db.Record} and key array. The
	 * record is evaluated completely.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @return A scatter chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 */
	public static JFreeChart createScatterChart(Record record, String[] keys) {
		return createScatterChart(record, keys, 0, 0, true);
	}

	/**
	 * Returns a scatter chart. The chart is generated from the given
	 * {@link de.xirp.db.Record} and key array. The
	 * record is evaluated from the given start to the given stop
	 * time.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @param start
	 *            The start time.
	 * @param stop
	 *            The stop time.
	 * @return A scatter chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 */
	public static JFreeChart createScatterChart(Record record, String[] keys,
			long start, long stop) {
		return createScatterChart(record, keys, start, stop, false);
	}

	/**
	 * Returns a list of scatter charts. The charts are generated from
	 * the given record->interval map and key array. One chart is
	 * generated for each {@link de.xirp.db.Record} in
	 * the map. The records are evaluated using the mapped
	 * {@link de.xirp.ui.widgets.dialogs.ChartConfigDialog.Interval}.
	 * 
	 * @param recordIntervalMap
	 *            The records with the data and the intervals.
	 * @param keys
	 *            The keys to use.
	 * @return A list of scatter charts.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 * @see de.xirp.ui.widgets.dialogs.ChartConfigDialog.Interval
	 */
	public static List<JFreeChart> createScatterChart(
			Map<Record, Interval> recordIntervalMap, String[] keys) {
		List<JFreeChart> charts = new ArrayList<JFreeChart>( );
		Interval i;
		for (Record record : recordIntervalMap.keySet( )) {
			i = recordIntervalMap.get(record);
			try {
				charts.add(createScatterChart(record,
						keys,
						i.start.getTime( ),
						i.stop.getTime( ),
						false));
			}
			catch (RuntimeException e) {
				logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
						+ Constants.LINE_SEPARATOR, e);
			}
		}
		return charts;
	}

	/**
	 * Returns a scatter chart. The chart is generated from the given
	 * {@link de.xirp.db.Record} and key array. The
	 * record is evaluated using the start and stop time from the
	 * given
	 * {@link de.xirp.ui.widgets.dialogs.ChartConfigDialog.Interval}.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @param interval
	 *            The time interval to use.
	 * @return A scatter chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 * @see de.xirp.ui.widgets.dialogs.ChartConfigDialog.Interval
	 */
	public static JFreeChart createScatterChart(Record record, String[] keys,
			Interval interval) {
		return createScatterChart(record,
				keys,
				interval.start.getTime( ),
				interval.stop.getTime( ),
				false);
	}

	/**
	 * Returns a scatter chart. The chart is generated fromm the given
	 * {@link de.xirp.db.Record} and key array. The
	 * record is evaluated from the given start to the given stop time
	 * or evaluated completely if the <code>origTime</code> flag is
	 * set to <code>true</code>.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @param start
	 *            The start time.
	 * @param stop
	 *            The stop time.
	 * @param origTime
	 *            A flag indicating if the original time should be
	 *            used.
	 * @return A scatter chart.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.db.Record
	 */
	private static JFreeChart createScatterChart(Record record, String[] keys,
			long start, long stop, boolean origTime) {

		String chartTitle = createChartTitle(record, keys);

		List<Observed> all = new ArrayList<Observed>( );
		TimeSeriesCollection dataset = createTimeSeriesAndFillAllObservedList(all,
				record,
				keys,
				origTime,
				start,
				stop);

		JFreeChart chart = ChartFactory.createScatterPlot(chartTitle,
				I18n.getString("ChartManager.text.time"), //$NON-NLS-1$
				I18n.getString("ChartManager.text.value"), dataset, PlotOrientation.VERTICAL, true, true, false); //$NON-NLS-1$

		XYPlot plot = (XYPlot) chart.getPlot( );
		setXYPlot(plot, new Date(record.getStart( )));
		XYDotRenderer dr = new XYDotRenderer( );
		dr.setDotWidth(3);
		dr.setDotHeight(3);
		plot.setRenderer(dr);

		exportAutomatically(all, chart);

		return chart;
	}

	/**
	 * Returns a string containing a chart title. The title is
	 * generated from the given {@link de.xirp.db.Record}
	 * and key array. <br>
	 * <br>
	 * Is contains the name of the record and the used keys.
	 * 
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @return A <code>String</code> containing the chart title.
	 * @see de.xirp.db.Record
	 */
	private static String createChartTitle(Record record, String[] keys) {
		String chartTitle = I18n.getString("ChartManager.text.observedKeys") + record.getName( ) //$NON-NLS-1$
				+ ": "; //$NON-NLS-1$
		for (String keyname : keys) {
			chartTitle = chartTitle + "'" + keyname + "' "; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return chartTitle;
	}

	/**
	 * This method creates a
	 * {@link org.jfree.data.time.TimeSeriesCollection} from the given
	 * {@link de.xirp.db.Record} and key array. The
	 * record is evaluated from the given start to the given stop time
	 * or is evaluated completely if the flag <code>origTime</code>
	 * is set to <code>true</code>. <br>
	 * <br>
	 * The method also fills the given
	 * <code>List&lt;Observed&gt;</code> with the found
	 * {@link de.xirp.db.Observed} value objects. This
	 * list is used by the
	 * {@link de.xirp.chart.ChartManager#exportAutomatically(List, JFreeChart)}
	 * method. <br>
	 * <br>
	 * This is done because the code base is the the. The
	 * {@link de.xirp.db.Observed} objects are created
	 * from a database query. To create a chart the date must be
	 * contained in a {@link org.jfree.data.time.TimeSeriesCollection}.
	 * The export methods in
	 * {@link de.xirp.chart.ChartUtil} works with a
	 * <code>List&lt;Observed&gt;</code>. The database query
	 * delivers a <code>List&lt;Observed&gt;</code>, so the values
	 * are converted to a
	 * {@link org.jfree.data.time.TimeSeriesCollection} and then are
	 * copied to the <code>all</code> list to be used laster on in
	 * the
	 * {@link de.xirp.chart.ChartManager#exportAutomatically(List, JFreeChart)}
	 * method.
	 * 
	 * @param all
	 *            <code>null</code> or empty List <b>not</b>
	 *            permitted.<br>
	 *            Must be a <code>new</code> List.
	 * @param record
	 *            The record containing the data.
	 * @param keys
	 *            The keys to use.
	 * @param origTime
	 *            A flag indicating if the original time should be
	 *            used.
	 * @param stop
	 *            The start time.
	 * @param start
	 *            The stop time.
	 * @return A <code>TimeSeriesCollection</code> with the values
	 *         of the record for the time interval.
	 * @see org.jfree.data.time.TimeSeriesCollection
	 * @see de.xirp.db.Observed
	 * @see de.xirp.db.Record
	 * @see de.xirp.chart.ChartUtil
	 * @see de.xirp.chart.ChartManager#exportAutomatically(java.util.List,
	 *      org.jfree.chart.JFreeChart)
	 * @see org.jfree.chart.JFreeChart
	 */
	private static TimeSeriesCollection createTimeSeriesAndFillAllObservedList(
			List<Observed> all, Record record, String[] keys, boolean origTime,
			long start, long stop) {

		TimeSeriesCollection dataset = new TimeSeriesCollection( );
		Date date = new Date( );
		List<Observed> obs;
		for (String keyname : keys) {
			TimeSeries ts = new TimeSeries(keyname, Millisecond.class);
			if (origTime) {
				obs = ChartDatabaseUtil.getObservedList(record, keyname);
			}
			else {
				obs = ChartDatabaseUtil.getObservedList(record,
						keyname,
						start,
						stop);
			}
			for (Observed o : obs) {
				date.setTime(o.getTimestamp( ));
				ts.addOrUpdate(new Millisecond(date), o.getValue( ));
			}
			all.addAll(obs);
			dataset.addSeries(ts);
		}
		return dataset;
	}

	/**
	 * Sets some values on the given
	 * {@link org.jfree.chart.plot.XYPlot} corresponding to some
	 * options of the {@link de.xirp.chart.ChartOptions}
	 * field. <br>
	 * <br>
	 * If <code>options.is(OptionName.SHOW_THRESHOLD)</code> is
	 * <code>true</code> a threshold line is painted to the chart
	 * using the <code>threshold</code> field. <br>
	 * <br>
	 * If <code>options.is(OptionName.USE_RELATIVE)</code> is
	 * <code>true</code> the date axis of the plot gets a title
	 * indicating that relative values are used. If the flag is
	 * <code>false</code> the plot gets a title indicating that
	 * absolute values are used.
	 * 
	 * @param plot
	 *            The plot to alter.
	 * @param start
	 *            The start time.
	 * @see de.xirp.chart.ChartOptions
	 * @see org.jfree.chart.plot.XYPlot
	 */
	private static void setXYPlot(XYPlot plot, Date start) {
		plot.setNoDataMessage(NO_DATA_AVAILABLE);

		if (options.is(OptionName.SHOW_THRESHOLD)) {
			Marker marker = new ValueMarker(threshold);
			marker.setPaint(Color.orange);
			marker.setAlpha(0.8f);
			plot.addRangeMarker(marker);
		}

		if (options.is(OptionName.USE_RELATIVE)) {
			DateAxis axis = new DateAxis(I18n.getString("ChartManager.text.relativeTime")); //$NON-NLS-1$
			RelativeDateFormat rdf = new RelativeDateFormat(start);
			axis.setDateFormatOverride(rdf);
			plot.setDomainAxis(axis);
		}
		else {
			plot.setDomainAxis(new DateAxis(I18n.getString("ChartManager.text.absoluteTime"))); //$NON-NLS-1$
		}
	}

	/**
	 * This method exports the values inside the given
	 * <code>List&lt;Observed&gt;</code> as CSV file if
	 * <code>options.is(OptionName.CSV_EXPORT)</code> is true. <br>
	 * <br>
	 * The given chart is exported as PNG, JPG and PDF if the
	 * corresponding option flags are <code>true</code>:<br>
	 * <ul>
	 * <li><code>options.is(OptionName.PNG_EXPORT)</code></li>
	 * <li><code>options.is(OptionName.JPG_EXPORT)</code></li>
	 * <li><code>options.is(OptionName.PDF_EXPORT)</code></li>
	 * </ul>
	 * 
	 * @param obs
	 *            The list of values for csv export.
	 * @param chart
	 *            The chart to export.
	 * @see de.xirp.db.Observed
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.chart.ChartUtil
	 */
	private static void exportAutomatically(List<Observed> obs, JFreeChart chart) {
		if (options.is(OptionName.CSV_EXPORT)) {
			if (obs != null && !obs.isEmpty( )) {
				ChartUtil.exportCSV(obs, robotName);
			}
		}
		if (options.is(OptionName.PNG_EXPORT)) {
			ChartUtil.exportPNG(chart, robotName);
		}
		if (options.is(OptionName.JPG_EXPORT)) {
			ChartUtil.exportJPG(chart, robotName);
		}
		if (options.is(OptionName.PDF_EXPORT)) {
			ChartUtil.exportPDF(chart, robotName);
		}
	}

	/**
	 * Sets the options object.
	 * 
	 * @param options
	 *            The new chart options.
	 */
	public static void setOptions(ChartOptions options) {
		ChartManager.options = options;
	}

	/**
	 * Does nothing. <br>
	 * <br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * startup. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
	}

	/**
	 * Does nothing. <br>
	 * <br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * shutdown. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
	}

	/**
	 * Sets the robot name.
	 * 
	 * @param robotName
	 *            The new robot name.
	 */
	public static void setRobotName(String robotName) {
		ChartManager.robotName = robotName;
	}

	/**
	 * Sets the threshold.
	 * 
	 * @param threshold
	 *            The new threshold value.
	 */
	public static void setThreshold(double threshold) {
		ChartManager.threshold = threshold;

	}
}
