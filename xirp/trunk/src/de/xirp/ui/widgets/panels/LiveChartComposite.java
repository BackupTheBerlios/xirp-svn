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
 * LiveChartComposite.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 27.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.RelativeDateFormat;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import de.xirp.chart.ChartManager;
import de.xirp.chart.ChartUtil;
import de.xirp.io.comm.data.Datapool;
import de.xirp.io.comm.data.DatapoolEvent;
import de.xirp.io.comm.data.DatapoolException;
import de.xirp.io.comm.data.DatapoolListener;
import de.xirp.io.comm.data.DatapoolManager;
import de.xirp.profile.ProfileManager;
import de.xirp.settings.PropertiesManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XChartComposite;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.ui.widgets.custom.XMenuItem;
import de.xirp.ui.widgets.custom.XToolBar;
import de.xirp.ui.widgets.custom.XToolItem;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This class represents the live chart composite.
 * 
 * @author Matthias Gernand
 *
 */
public class LiveChartComposite extends XComposite {

	/**
	 * The time.
	 */
	private static final int TIME = 60000;
	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(LiveChartComposite.class);
	/**
	 * The robot name.
	 */
	private String robotName;
	/**
	 * A chart composite.
	 */
	private XChartComposite cc;
	/**
	 * The series.
	 */
	private Map<String, TimeSeries> keySeriesMap = new HashMap<String, TimeSeries>( );
	/**
	 * The datapool.
	 * 
	 * @see de.xirp.io.comm.data.Datapool
	 */
	private Datapool pool;
	/**
	 * The data.
	 */
	private TimeSeriesCollection dataset;
	/**
	 * A datapool listener.
	 * 
	 * @see de.xirp.io.comm.data.DatapoolListener
	 */
	private DatapoolListener listener;
	/**
	 * The menu.
	 */
	private Menu keysMenu;
	/**
	 * The keys.
	 */
	private List<String> keysList = Collections.emptyList( );
	/**
	 * The chart.
	 */
	private JFreeChart chart;
	/**
	 * The tool item.
	 */
	private XToolItem startStop;
	/**
	 * The start date.
	 */
	private Date start;
	/**
	 * A tool item.
	 */
	private XToolItem keys;

	/**
	 * Constructs a new live chart composite.
	 * 
	 * @param parent
	 * 				The parent.
	 * @param robotName
	 * 				The robot name.
	 */
	public LiveChartComposite(Composite parent, String robotName) {
		super(parent, SWT.NONE);
		this.robotName = robotName;
		init( );
	}

	/**
	 * Initializes the listeners.
	 */
	private void init() {
		addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent e) {
				pool.removeDatapoolReceiveListener(listener);
			}

		});

		initDatapool( );
		keysList = ProfileManager.getSensorDatapoolKeys(robotName);
		dataset = new TimeSeriesCollection( );
		chart = createChart(dataset);

		SWTUtil.setGridLayout(this, 1, true);

		final XToolBar toolBar = new XToolBar(this, SWT.FLAT);
		SWTUtil.setGridData(toolBar, true, false, SWT.FILL, SWT.BEGINNING, 1, 1);

		keysMenu = new Menu(getShell( ), SWT.POP_UP);

		keys = new XToolItem(toolBar, SWT.DROP_DOWN | SWT.FLAT);
		keys.setImage(ImageManager.getSystemImage(SystemImage.ADD));
		keys.setToolTipTextForLocaleKey("LiveChartComposite.tooltip.startOrStop"); //$NON-NLS-1$
		keys.addListener(SWT.Selection, new Listener( ) {

			public void handleEvent(Event event) {
				Rectangle rect = keys.getBounds( );
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = toolBar.toDisplay(pt);
				keysMenu.setLocation(pt.x, pt.y);
				keysMenu.setVisible(true);
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR | SWT.VERTICAL);

		XToolItem timeMode = new XToolItem(toolBar, SWT.CHECK | SWT.FLAT);
		timeMode.setImage(ImageManager.getSystemImage(SystemImage.ABSOLUTE));
		timeMode.setToolTipTextForLocaleKey("LiveChartComposite.tooltip.switchTimeMode"); //$NON-NLS-1$
		timeMode.addSelectionListener(new SelectionAdapter( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				XToolItem itm = (XToolItem) e.widget;
				if (itm.getSelection( )) {
					if (chart != null && start != null) {
						XYPlot plot = chart.getXYPlot( );
						DateAxis axis = new DateAxis(I18n.getString("LiveChartComposite.text.relativeTime")); //$NON-NLS-1$
						RelativeDateFormat rdf = new RelativeDateFormat(start);
						axis.setDateFormatOverride(rdf);
						plot.setDomainAxis(axis);
						ValueAxis vaxis = plot.getDomainAxis( );
						vaxis.setAutoRange(true);
						vaxis.setFixedAutoRange(60000);
					}
					itm.setImage(ImageManager.getSystemImage(SystemImage.RELATIVE));
				}
				else {
					if (chart != null) {
						XYPlot plot = chart.getXYPlot( );
						plot.setDomainAxis(new DateAxis(I18n.getString("LiveChartComposite.text.absoluteTime"))); //$NON-NLS-1$
						ValueAxis vaxis = plot.getDomainAxis( );
						vaxis.setAutoRange(true);
						vaxis.setFixedAutoRange(60000);
					}
					itm.setImage(ImageManager.getSystemImage(SystemImage.ABSOLUTE));
				}
			}

		});

		new ToolItem(toolBar, SWT.SEPARATOR | SWT.VERTICAL);

		startStop = new XToolItem(toolBar, SWT.CHECK | SWT.FLAT);
		startStop.setImage(ImageManager.getSystemImage(SystemImage.START));
		startStop.setToolTipTextForLocaleKey("LiveChartComposite.tooltip.startOrStop"); //$NON-NLS-1$
		startStop.setEnabled(false);
		startStop.addSelectionListener(new SelectionAdapter( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				final XToolItem itm = (XToolItem) e.widget;
				final boolean enabled = itm.getSelection( );
				keys.setEnabled(!enabled);
				if (enabled) {
					setPlottingEnabled(enabled);
					itm.setImage(ImageManager.getSystemImage(SystemImage.STOP));
				}
				else {
					SWTUtil.showBusyWhile(getShell( ), new Runnable( ) {

						public void run() {
							setPlottingEnabled(enabled);
							itm.setImage(ImageManager.getSystemImage(SystemImage.START));
						}
					});
				}
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR | SWT.VERTICAL);

		//		XToolItem thresholdItm = new XToolItem(toolBar, SWT.SEPARATOR);
		//		thresholdItm.setWidth(75);
		//
		//		//TODO: Double spinner, remove of old thres line
		//		XSpinner threshold = new XSpinner(toolBar, SWT.BORDER);
		//		threshold.setIncrement(1);
		//		threshold.setMaximum(1);
		//		threshold.setMaximum(Integer.MAX_VALUE);
		//		threshold.setEnabled(false);
		//		threshold.addSelectionListener(new SelectionAdapter( ) {
		//
		//			/* (non-Javadoc)
		//			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		//			 */
		//			@Override
		//			public void widgetSelected(SelectionEvent e) {
		//				XSpinner spn = (XSpinner) e.widget;
		//				double value = spn.getSelection( );
		//				if (chart != null && value > 0) {
		//					XYPlot plot = (XYPlot) chart.getPlot( );
		//					Marker marker = new ValueMarker(value);
		//					marker.setPaint(Color.orange);
		//					marker.setAlpha(0.8f);
		//					plot.addRangeMarker(marker);
		//				}
		//			}
		//
		//		});
		//		thresholdItm.setControl(threshold);

		initKeysMenu( );

		cc = new XChartComposite(this, SWT.NONE, null, false, robotName);
		SWTUtil.setGridData(cc, false, true, SWT.FILL, SWT.FILL, 1, 1);
	}

	/**
	 * Initializes the keys menu.
	 */
	private void initKeysMenu() {
		for (String key : keysList) {
			XMenuItem itm = new XMenuItem(keysMenu, SWT.CHECK);
			itm.setText(key);
			itm.setSelection(false);
			itm.addSelectionListener(new SelectionAdapter( ) {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (getSelectedMenuItems( ).size( ) > 0) {
						startStop.setEnabled(true);
					}
					else {
						startStop.setEnabled(false);
					}
				}
			});
		}
	}

	/**
	 * Enables or disables the plotting.
	 * 
	 * @param enabled
	 * 			<code>true</code>: plotting enabled.
	 */
	private void setPlottingEnabled(boolean enabled) {
		if (enabled) {
			start = new Date( );
			for (String key : getSelectedMenuItems( )) {
				addKey(key);
			}
			cc.setChartAndRobotName(chart, robotName);
		}
		else {
			for (String key : getSelectedMenuItems( )) {
				removeKey(key);
			}
			ChartUtil.exportChart(chart,
					dataset,
					robotName,
					PropertiesManager.isExportPDF( ),
					PropertiesManager.isExportPNG( ),
					PropertiesManager.isExportJPG( ),
					PropertiesManager.isExportCSV( ));
			dataset = new TimeSeriesCollection( );
			chart = createChart(dataset);
			cc.setChartAndRobotName(null, robotName);
		}
	}

	/**
	 * Returns the selected menu item names.
	 * 
	 * @return The menu item names.
	 */
	private List<String> getSelectedMenuItems() {
		List<String> strings = new ArrayList<String>( );
		for (MenuItem itm : keysMenu.getItems( )) {
			if (itm.getSelection( )) {
				strings.add(itm.getText( ));
			}
		}
		return strings;
	}

	/**
	 * Adds the given key.
	 * 
	 * @param key
	 * 			The key.
	 */
	private void addKey(String key) {
		TimeSeries ts = new TimeSeries(key, Millisecond.class);
		ts.setMaximumItemAge(TIME);
		keySeriesMap.put(key, ts);
		dataset.addSeries(ts);
		pool.addDatapoolReceiveListener(key, listener);
	}

	/**
	 * Removes the given key.
	 * 
	 * @param key
	 * 			The key.
	 */
	private void removeKey(String key) {
		TimeSeries ts = keySeriesMap.remove(key);
		if (ts != null) {
			dataset.removeSeries(ts);
		}
		pool.removeDatapoolReceiveListener(key, listener);
	}

	/**
	 * Initializes the datapool.
	 */
	private void initDatapool() {
		try {
			pool = DatapoolManager.getDatapool(robotName);
		}
		catch (DatapoolException e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}

		listener = new DatapoolListener( ) {

			public void valueChanged(DatapoolEvent e) {
				try {
					addValueToChart(e.getKey( ),
							(Number) e.getValue( ),
							e.getTimestamp( ));
				}
				catch (ClassCastException ex) {
					logClass.info("Error: " + ex.getMessage( ) + " (check your protocol for key: " + e.getKey( ) + ")" + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}

			public boolean notifyOnlyWhenChanged() {
				return false;
			}
		};
	}

	/**
	 * Adds the given value to the chart.
	 * 
	 * @param key
	 * 				The key.
	 * @param number
	 * 				The value.
	 * @param timestamp 
	 * 				The time stamp.
	 */
	private void addValueToChart(final String key, final Number number,
			final long timestamp) {
		SWTUtil.asyncExec(new Runnable( ) {

			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				if (!isDisposed( )) {
					try {
						keySeriesMap.get(key)
								.addOrUpdate(new Millisecond(new Date(timestamp)),
										number.doubleValue( ));
					}
					catch (NullPointerException e) {
						logClass.trace("Trace: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
					}
				}
			}
		});
	}

	/**
	 * Creates a chart for the given data set.
	 * 
	 * @param dataset
	 * 				The data.
	 * @return The chart.
	 */
	private JFreeChart createChart(XYDataset dataset) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(null,
				I18n.getString("LiveChartComposite.text.time"), //$NON-NLS-1$
				I18n.getString("LiveChartComposite.text.value"), dataset, true, true, false); //$NON-NLS-1$

		XYPlot plot = chart.getXYPlot( );
		plot.setDomainAxis(new DateAxis(I18n.getString("LiveChartComposite.text.absoluteTime"))); //$NON-NLS-1$
		ValueAxis vaxis = plot.getDomainAxis( );
		vaxis.setAutoRange(true);
		vaxis.setFixedAutoRange(TIME);

		plot.setNoDataMessage(ChartManager.NO_DATA_AVAILABLE);

		return chart;
	}
}
