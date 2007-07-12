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
 * ChartConfigDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 19.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.JFreeChart;

import de.xirp.chart.ChartManager;
import de.xirp.chart.ChartOptions;
import de.xirp.chart.ChartOptions.OptionName;
import de.xirp.db.ChartDatabaseUtil;
import de.xirp.db.Record;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.*;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.ui.widgets.custom.XStyledSpinner.SpinnerStyle;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This dialog is used to collect data about the chart that should be
 * generated.
 * 
 * @author Matthias Gernand
 */
public class ChartConfigDialog extends XDialog {

	/**
	 * A string constant.
	 */
	private static final String RECORD = "record"; //$NON-NLS-1$
	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(ChartConfigDialog.class);
	/**
	 * The height.
	 */
	private static final int HEIGHT = 850;
	/**
	 * The width.
	 */
	private static final int WIDTH = 600;
	/**
	 * The parent shell.
	 */
	private Shell parent;
	/**
	 * The dialogs shell.
	 */
	private XShell dialogShell;
	/**
	 * A list.
	 */
	private XList keysList;
	/**
	 * A button.
	 */
	private XButton lookup;
	/**
	 * The currently active
	 * {@link de.xirp.db.Record record}.
	 * 
	 * @see de.xirp.db.Record
	 */
	private Record currentRecord;
	/**
	 * Some {@link de.xirp.db.Record records}.
	 * 
	 * @see de.xirp.db.Record
	 */
	private List<Record> currentRecords = new ArrayList<Record>( );
	/**
	 * The selected start time.
	 */
	private long currentStartTime = 0;
	/**
	 * The selected stop time.
	 */
	private long currentStopTime = 0;
	/**
	 * The selected chart
	 * {@link de.xirp.ui.widgets.dialogs.ChartConfigDialog.ChartType type}.
	 */
	private ChartType selectedChartType = ChartType.TIME;
	/**
	 * A button.
	 */
	private XButton generate;
	/**
	 * A check box.
	 */
	private XCheckBox relative;
	/**
	 * A check box.
	 */
	private XCheckBox allKeys;
	/**
	 * A check box.
	 */
	private XCheckBox threeD;
	/**
	 * A check box.
	 */
	private XCheckBox exportPDF;
	/**
	 * A check box.
	 */
	private XCheckBox exportPNG;
	/**
	 * A check box.
	 */
	private XCheckBox exportJPEG;
	/**
	 * A check box.
	 */
	private XCheckBox exportCSV;
	/**
	 * The check boxes.
	 */
	private List<XCheckBox> optionCheckBoxes = new ArrayList<XCheckBox>( );
	/**
	 * The chart
	 * {@link de.xirp.chart.ChartOptions options}.
	 * 
	 * @see de.xirp.chart.ChartOptions
	 */
	private ChartOptions options = new ChartOptions( );
	/**
	 * A check box.
	 */
	private XCheckBox compareRecords;
	/**
	 * A map, for the selected time intervals.
	 */
	private Map<XTimeIntervalChooser, Interval> timeIntervalMap = new HashMap<XTimeIntervalChooser, Interval>( );
	/**
	 * A group.
	 */
	private XGroup time;
	/**
	 * Chart {@link de.xirp.chart.ChartOptions options}.
	 * 
	 * @see de.xirp.chart.ChartOptions
	 */
	private ChartOptions opt;
	/**
	 * A combo box.
	 */
	private XCombo robots;
	/**
	 * The robot name.
	 */
	private String robotName;
	/**
	 * A check box.
	 */
	private XCheckBox showThreshold;
	/**
	 * A label.
	 */
	private XLabel thresLabel;
	/**
	 * A styled spinner.
	 */
	private XStyledSpinner threshold;

	/**
	 * This class represents a time interval. The interval is defined
	 * by two {@link java.util.Date} objects.
	 * 
	 * @author Matthias Gernand
	 */
	public final class Interval {

		/**
		 * The start date.
		 */
		public Date start;
		/**
		 * The stop date.
		 */
		public Date stop;

		/**
		 * Constructs a new interval from the given start and stop
		 * dates.
		 * 
		 * @param start
		 *            The start time.
		 * @param stop
		 *            The stop time.
		 */
		public Interval(Date start, Date stop) {
			this.start = start;
			this.stop = stop;
		}
	}

	/**
	 * Enumeration holding constants for indicating the type of a
	 * chart.
	 * 
	 * @author Matthias Gernand
	 */
	private enum ChartType {
		/**
		 * A time chart.
		 */
		TIME,
		/**
		 * A pie chart.
		 */
		PIE,
		/**
		 * A bar chart.
		 */
		BAR,
		/**
		 * A scatter chart.
		 */
		SCATTER;

		/**
		 * Returns the translated name of the chart type.
		 * 
		 * @return The translated name.
		 */
		public String getName() {
			switch (this) {
				case TIME:
					return I18n.getString("ChartConfigDialog.ChartType.chartname.timeChart"); //$NON-NLS-1$
				case PIE:
					return I18n.getString("ChartConfigDialog.ChartType.chartname.pieChart"); //$NON-NLS-1$
				case BAR:
					return I18n.getString("ChartConfigDialog.ChartType.chartname.barChart"); //$NON-NLS-1$
				case SCATTER:
					return I18n.getString("ChartConfigDialog.ChartType.chartname.scatterChart"); //$NON-NLS-1$
				default:
					return null;
			}
		}

		/**
		 * Returns the chart type for the given chart type name.
		 * 
		 * @param name
		 *            The chart type name.
		 * @return The chart type.
		 */
		public static ChartType getTypeForName(String name) {
			if (name.equals(TIME.getName( ))) {
				return TIME;
			}
			else if (name.equals(PIE.getName( ))) {
				return PIE;
			}
			else if (name.equals(BAR.getName( ))) {
				return BAR;
			}
			else if (name.equals(SCATTER.getName( ))) {
				return SCATTER;
			}
			else {
				return null;
			}
		}
	}

	/**
	 * Enumeration holding constants for indicating the chart
	 * generation mode.
	 * 
	 * @author Matthias Gernand
	 */
	private enum Mode {
		/**
		 * A single {@link de.xirp.db.Record record} is
		 * plotted.
		 * 
		 * @see de.xirp.db.Record
		 */
		SINGLE,
		/**
		 * Multiple {@link de.xirp.db.Record records} are
		 * plotted.
		 * 
		 * @see de.xirp.db.Record
		 */
		MULTI,
		/**
		 * A single {@link de.xirp.db.Record record} is
		 * plotted via the multi record mode..
		 * 
		 * @see de.xirp.db.Record
		 */
		SINGLE_VIA_MULTI,
		/**
		 * An undefined mode.
		 */
		UNDEFINED;
	}

	/**
	 * Constructs a new chart configuration dialog.
	 * 
	 * @param parent
	 *            The parent.
	 */
	public ChartConfigDialog(Shell parent) {
		super(parent);
		this.parent = parent;
	}

	/**
	 * Constructs the dialog and opens it.
	 */
	public void open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});
		dialogShell.setSize(WIDTH, HEIGHT);
		dialogShell.setTextForLocaleKey("ChartConfigDialog.gui.title.createChart"); //$NON-NLS-1$
		dialogShell.setImage(ImageManager.getSystemImage(SystemImage.CHART));
		SWTUtil.setGridLayout(dialogShell, 2, true);

		XGroup chartType = new XGroup(dialogShell, SWT.NONE);
		chartType.setTextForLocaleKey("ChartConfigDialog.group.chartType"); //$NON-NLS-1$
		SWTUtil.setGridData(chartType, true, false, SWT.FILL, SWT.FILL, 1, 1);
		SWTUtil.setGridLayout(chartType, 2, true);

		XLabel label = new XLabel(chartType, SWT.NONE);
		label.setTextForLocaleKey("ChartConfigDialog.label.chooseChartType"); //$NON-NLS-1$
		SWTUtil.setGridData(label, true, false, SWT.FILL, SWT.FILL, 1, 1);

		final XCombo types = new XCombo(chartType, SWT.BORDER | SWT.READ_ONLY);
		SWTUtil.setGridData(types, true, false, SWT.FILL, SWT.FILL, 1, 1);
		for (ChartType type : ChartType.values( )) {
			types.add(type.getName( ));
		}
		types.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedChartType = ChartType.getTypeForName(types.getItem(types.getSelectionIndex( )));
				setOptions( );
			}

		});

		XGroup compare = new XGroup(dialogShell, SWT.NONE);
		compare.setTextForLocaleKey("ChartConfigDialog.group.compareRecords"); //$NON-NLS-1$
		compare.setToolTipTextForLocaleKey("ChartConfigDialog.group.tooltip.compareRecords"); //$NON-NLS-1$
		SWTUtil.setGridData(compare, true, false, SWT.FILL, SWT.FILL, 1, 1);
		SWTUtil.setGridLayout(compare, 1, true);

		compareRecords = new XCheckBox(compare);
		compareRecords.setTextForLocaleKey("ChartConfigDialog.checkbox.compareRecords"); //$NON-NLS-1$
		compareRecords.setSelection(false);
		compareRecords.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!compareRecords.getSelection( )) {
					try {
						currentRecord = currentRecords.get(0);
					}
					catch (Exception ex) {
						logClass.debug("Error: " + ex.getMessage( ) + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$
					}
					setTimes( );
				}
				else {
					if (currentRecord != null) {
						currentRecords = new ArrayList<Record>( );
						currentRecords.add(currentRecord);
					}
					setTimes( );
				}
			}

		});
		SWTUtil.setGridData(compareRecords,
				true,
				false,
				SWT.FILL,
				SWT.FILL,
				1,
				1);

		XGroup general = new XGroup(dialogShell, SWT.NONE);
		general.setTextForLocaleKey("ChartConfigDialog.group.generalValues"); //$NON-NLS-1$
		SWTUtil.setGridData(general, true, false, SWT.FILL, SWT.FILL, 1, 1);
		SWTUtil.setGridLayout(general, 2, false);

		label = new XLabel(general, SWT.NONE);
		label.setTextForLocaleKey("ChartConfigDialog.label.robotsInDatabase"); //$NON-NLS-1$
		SWTUtil.setGridData(label, true, false, SWT.FILL, SWT.CENTER, 1, 1);

		robots = new XCombo(general, SWT.READ_ONLY);
		SWTUtil.setGridData(robots, true, false, SWT.FILL, SWT.CENTER, 1, 1);
		for (String robot : ChartDatabaseUtil.getAvailableRobots( )) {
			robots.add(robot);
		}
		robots.select(-1);
		robots.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int idx = ((XCombo) e.widget).getSelectionIndex( );
				robotName = ((XCombo) e.widget).getItem(idx);
				if (idx >= 0) {
					lookup.setEnabled(true);
				}
				else {
					lookup.setEnabled(false);
				}
			}

		});

		label = new XLabel(general, SWT.NONE);
		label.setTextForLocaleKey("ChartConfigDialog.label.lookupRecords"); //$NON-NLS-1$
		SWTUtil.setGridData(label, true, false, SWT.FILL, SWT.FILL, 1, 1);

		lookup = new XButton(general, XButtonType.LOOKUP);
		SWTUtil.setGridData(lookup, true, false, SWT.FILL, SWT.FILL, 1, 1);
		lookup.setEnabled(false);
		lookup.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int idx = robots.getSelectionIndex( );
				String selection = robots.getItem(idx);
				RecordLookupDialog rld = new RecordLookupDialog(dialogShell,
						selection);

				if (compareRecords.getSelection( )) {
					List<Record> records = rld.openMulti( );
					if (!records.isEmpty( ) && records != null) {
						currentRecords = records;
						setKeysList( );
						setTimes( );
						generate.setEnabled(false);
					}
				}
				else {
					Record record = rld.openSingle( );
					if (record != null) {
						currentRecord = record;
						setKeysList( );
						setTimes( );
						generate.setEnabled(false);
					}
				}
				if (allKeys.getSelection( )) {
					generate.setEnabled(true);
				}
			}

		});

		XGroup keys = new XGroup(dialogShell, SWT.NONE);
		keys.setTextForLocaleKey("ChartConfigDialog.group.chooseKey"); //$NON-NLS-1$
		SWTUtil.setGridData(keys, true, false, SWT.FILL, SWT.FILL, 1, 1);
		SWTUtil.setGridLayout(keys, 1, true);

		keysList = new XList(keys, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		SWTUtil.setGridData(keysList, true, true, SWT.FILL, SWT.FILL, 1, 1);
		keysList.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (keysList.getSelectionCount( ) > 0) {
					generate.setEnabled(true);
				}
				else {
					generate.setEnabled(false);
				}
			}

		});

		time = new XGroup(dialogShell, SWT.NONE);
		time.setTextForLocaleKey("ChartConfigDialog.group.timeInterval"); //$NON-NLS-1$
		SWTUtil.setGridData(time, true, false, SWT.FILL, SWT.FILL, 2, 1);
		SWTUtil.setGridLayout(time, 1, false);

		XGroup chartOptions = new XGroup(dialogShell, SWT.NONE);
		chartOptions.setTextForLocaleKey("ChartConfigDialog.group.chartOptions"); //$NON-NLS-1$
		SWTUtil.setGridData(chartOptions, true, false, SWT.FILL, SWT.FILL, 2, 1);
		SWTUtil.setGridLayout(chartOptions, 2, true);

		relative = new XCheckBox(chartOptions);
		relative.setTextForLocaleKey("ChartConfigDialog.checkbox.relativeValues"); //$NON-NLS-1$
		relative.setToolTipTextForLocaleKey("ChartConfigDialog.checkbox.tooltip.relativeValues"); //$NON-NLS-1$
		relative.setSelection(false);
		relative.setEnabled(false);
		optionCheckBoxes.add(relative);
		SWTUtil.setGridData(relative, true, false, SWT.FILL, SWT.FILL, 2, 1);
		relative.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectedChartType == ChartType.PIE) {
					if (relative.getSelection( )) {
						allKeys.setSelection(true);
						keysList.selectAll( );
						keysList.setEnabled(false);
						generate.setEnabled(true);
					}
					else {
						allKeys.setSelection(false);
						keysList.setEnabled(true);
						keysList.deselectAll( );
						generate.setEnabled(false);
					}
				}
				setOptions( );
			}

		});

		allKeys = new XCheckBox(chartOptions);
		allKeys.setTextForLocaleKey("ChartConfigDialog.checkbox.allKeys"); //$NON-NLS-1$
		allKeys.setToolTipTextForLocaleKey("ChartConfigDialog.checkbox.tooltip.allKeys"); //$NON-NLS-1$
		allKeys.setSelection(false);
		allKeys.setEnabled(false);
		optionCheckBoxes.add(allKeys);
		allKeys.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (allKeys.getSelection( )) {
					keysList.selectAll( );
					keysList.setEnabled(false);
					generate.setEnabled(true);
				}
				else {
					keysList.setEnabled(true);
					keysList.deselectAll( );
					generate.setEnabled(false);
				}
			}

		});
		SWTUtil.setGridData(allKeys, true, false, SWT.FILL, SWT.FILL, 2, 1);

		// showAverage = new HCheckBox(chartOptions);
		// showAverage.setTextForLocaleKey("Show average");
		// showAverage
		// .setToolTipTextForLocaleKey("Show average line in
		// regression chart.");
		// showAverage.setSelection(false);
		// showAverage.setEnabled(false);
		// optionCheckBoxes.add(showAverage);
		// SWTUtil.setGridData(showAverage, true, false, SWT.FILL,
		// SWT.FILL, 2, 1);

		showThreshold = new XCheckBox(chartOptions);
		showThreshold.setTextForLocaleKey("ChartConfigDialog.checkbox.threshold"); //$NON-NLS-1$
		showThreshold.setToolTipTextForLocaleKey("ChartConfigDialog.checkbox.tooltip.threshold"); //$NON-NLS-1$
		showThreshold.setSelection(false);
		showThreshold.setEnabled(false);
		optionCheckBoxes.add(showThreshold);
		SWTUtil.setGridData(showThreshold,
				true,
				false,
				SWT.FILL,
				SWT.FILL,
				2,
				1);
		showThreshold.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setThresholdSpinner( );
			}

		});

		threeD = new XCheckBox(chartOptions);
		threeD.setTextForLocaleKey("ChartConfigDialog.checkbox.threeDee"); //$NON-NLS-1$
		threeD.setToolTipTextForLocaleKey("ChartConfigDialog.checkbox.tooltip.threeDee"); //$NON-NLS-1$
		threeD.setSelection(false);
		threeD.setEnabled(false);
		optionCheckBoxes.add(threeD);
		SWTUtil.setGridData(threeD, true, false, SWT.FILL, SWT.FILL, 2, 1);

		exportPDF = new XCheckBox(chartOptions);
		exportPDF.setTextForLocaleKey("ChartConfigDialog.checkbox.export.pdf"); //$NON-NLS-1$
		exportPDF.setToolTipTextForLocaleKey("ChartConfigDialog.checkbox.tooltip.export.pdf"); //$NON-NLS-1$
		exportPDF.setSelection(false);
		exportPDF.setEnabled(false);
		optionCheckBoxes.add(exportPDF);
		SWTUtil.setGridData(exportPDF, true, false, SWT.FILL, SWT.FILL, 2, 1);

		exportPNG = new XCheckBox(chartOptions);
		exportPNG.setTextForLocaleKey("ChartConfigDialog.checkbox.export.png"); //$NON-NLS-1$
		exportPNG.setToolTipTextForLocaleKey("ChartConfigDialog.checkbox.tooltip.export.png"); //$NON-NLS-1$
		exportPNG.setSelection(false);
		exportPNG.setEnabled(false);
		optionCheckBoxes.add(exportPNG);
		SWTUtil.setGridData(exportPNG, true, false, SWT.FILL, SWT.FILL, 2, 1);

		exportJPEG = new XCheckBox(chartOptions);
		exportJPEG.setTextForLocaleKey("ChartConfigDialog.checkbox.export.jpg"); //$NON-NLS-1$
		exportJPEG.setToolTipTextForLocaleKey("ChartConfigDialog.checkbox.tooltip.export.jpg"); //$NON-NLS-1$
		exportJPEG.setSelection(false);
		exportJPEG.setEnabled(false);
		optionCheckBoxes.add(exportJPEG);
		SWTUtil.setGridData(exportJPEG, true, false, SWT.FILL, SWT.FILL, 2, 1);

		exportCSV = new XCheckBox(chartOptions);
		exportCSV.setTextForLocaleKey("ChartConfigDialog.checkbox.export.csv"); //$NON-NLS-1$
		exportCSV.setToolTipTextForLocaleKey("ChartConfigDialog.checkbox.tooltip.export.csv"); //$NON-NLS-1$
		exportCSV.setSelection(false);
		exportCSV.setEnabled(false);
		optionCheckBoxes.add(exportCSV);
		SWTUtil.setGridData(exportCSV, true, false, SWT.FILL, SWT.FILL, 2, 1);

		thresLabel = new XLabel(chartOptions, SWT.NONE);
		thresLabel.setTextForLocaleKey("ChartConfigDialog.label.thresholdToShoe"); //$NON-NLS-1$
		SWTUtil.setGridData(thresLabel, true, false, SWT.FILL, SWT.FILL, 1, 1);
		thresLabel.setEnabled(false);

		threshold = new XStyledSpinner(chartOptions,
				SWT.BORDER,
				SpinnerStyle.ALL);
		SWTUtil.setGridData(threshold, true, false, SWT.FILL, SWT.FILL, 1, 1);
		threshold.setEnabled(false);
		threshold.setIncrement(1);
		threshold.setMinimum(Double.MIN_VALUE);
		threshold.setMaximum(Double.MAX_VALUE);

		generate = new XButton(dialogShell);
		generate.setTextForLocaleKey("ChartConfigDialog.button.generateChart"); //$NON-NLS-1$
		generate.setEnabled(false);
		SWTUtil.setGridData(generate, true, false, SWT.FILL, SWT.END, 2, 1);
		generate.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				SWTUtil.showBusyWhile(dialogShell, new Runnable( ) {

					public void run() {
						showChart(dialogShell, generateChart( ));
					}

				});
			}

		});

		dialogShell.pack( );
		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		SWTUtil.blockDialogFromReturning(dialogShell);

		return;
	}

	/**
	 * Enables or disables the threshold label and spinner
	 */
	private void setThresholdSpinner() {
		if (showThreshold.getSelection( )) {
			thresLabel.setEnabled(true);
			threshold.setEnabled(true);
		}
		else {
			thresLabel.setEnabled(false);
			threshold.setEnabled(false);
		}

	}

	/**
	 * Sets the chart
	 * {@link de.xirp.chart.ChartOptions options}.
	 */
	private void setOptions() {
		/*
		 * exportCSV, exportPNG, exportJPEG, exportPDF, allKeys, 3D,
		 * relative, average
		 */
		// reset options
		options = new ChartOptions( );
		for (XCheckBox box : optionCheckBoxes) {
			box.setEnabled(false);
		}

		// general available options
		options.set(OptionName.PNG_EXPORT, true);
		options.set(OptionName.JPG_EXPORT, true);
		options.set(OptionName.PDF_EXPORT, true);
		options.set(OptionName.USE_ALL_KEYS, true);
		options.set(OptionName.USE_RELATIVE, true);

		// set options
		switch (selectedChartType) {
			case TIME:
				options.set(OptionName.SHOW_THRESHOLD, true);
				options.set(OptionName.CSV_EXPORT, true);
				break;
			case PIE:
				options.set(OptionName.THREE_D, true);
				break;
			case BAR:
				options.set(OptionName.SHOW_THRESHOLD, true);
				options.set(OptionName.CSV_EXPORT, true);
				break;
			case SCATTER:
				options.set(OptionName.SHOW_THRESHOLD, true);
				options.set(OptionName.CSV_EXPORT, true);
			default:
				break;
		}

		// set check boxes
		exportCSV.setEnabled(options.is(OptionName.CSV_EXPORT));
		exportPNG.setEnabled(options.is(OptionName.PNG_EXPORT));
		exportJPEG.setEnabled(options.is(OptionName.JPG_EXPORT));
		exportPDF.setEnabled(options.is(OptionName.PDF_EXPORT));
		allKeys.setEnabled(options.is(OptionName.USE_ALL_KEYS));
		threeD.setEnabled(options.is(OptionName.THREE_D));
		relative.setEnabled(options.is(OptionName.USE_RELATIVE));
		showThreshold.setEnabled(options.is(OptionName.SHOW_THRESHOLD));

		if (relative.getSelection( ) && selectedChartType == ChartType.PIE) {
			allKeys.setEnabled(false);
		}

		disableTimeChoosers( );
	}

	/**
	 * Disables the time choosers.
	 */
	private void disableTimeChoosers() {
		if (selectedChartType == ChartType.PIE) {
			for (XTimeIntervalChooser tic : timeIntervalMap.keySet( )) {
				tic.setEnabled(false);
			}
		}
		else {
			for (XTimeIntervalChooser tic : timeIntervalMap.keySet( )) {
				tic.setEnabled(true);
			}
		}
	}

	/**
	 * Sets the time intervals.
	 */
	private void setTimes() {
		timeIntervalMap.clear( );
		for (Control c : time.getChildren( )) {
			SWTUtil.secureDispose(c);
		}
		XTimeIntervalChooser tic;
		XLabel label;

		if (compareRecords.getSelection( )) {
			final XSpinner range = new XSpinner(time, SWT.BORDER);
			range.setMinimum(1);
			range.setMaximum(getMinimum( ));
			range.setIncrement(1000);
			range.setSelection(1000);
			range.addSelectionListener(new SelectionAdapter( ) {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					for (XTimeIntervalChooser tic : timeIntervalMap.keySet( )) {
						tic.setRange(new Date(range.getSelection( )));
						Event ev = new Event( );
						ev.widget = range;
						tic.notifyListeners(SWT.Selection, ev);
					}

				}

			});

			SWTUtil.setGridData(range,
					true,
					false,
					SWT.FILL,
					SWT.BEGINNING,
					1,
					1);

			for (Record record : currentRecords) {
				label = new XLabel(time, SWT.NONE);
				label.setTextForLocaleKey("ChartConfigDialog.label.intervalFor", record.getName( )); //$NON-NLS-1$				

				tic = new XTimeIntervalChooser(time, SWT.NONE, true);
				tic.setFormat("HH:mm:ss,SSS"); //$NON-NLS-1$
				tic.setEnabled(false);
				tic.addSelectionListener(new SelectionAdapter( ) {

					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
					 */
					@Override
					public void widgetSelected(SelectionEvent e) {
						XTimeIntervalChooser aux = (XTimeIntervalChooser) e.widget;
						timeIntervalMap.put(aux,
								new Interval(aux.getChosenStartDate( ),
										aux.getChosenStopDate( )));
					}
				});
				SWTUtil.setGridData(tic,
						true,
						false,
						SWT.FILL,
						SWT.BEGINNING,
						1,
						1);

				tic.setData(RECORD, record);
				tic.setStart(new Date(record.getStart( )));
				tic.setStop(new Date(record.getStop( )));
				tic.setEnabled(true);
				timeIntervalMap.put(tic, null);
			}
			Event ev = new Event( );
			ev.widget = range;
			range.notifyListeners(SWT.Selection, ev);
		}
		else {
			try {
				label = new XLabel(time, SWT.NONE);
				label.setTextForLocaleKey("ChartConfigDialog.label.intervalFor", currentRecord.getName( )); //$NON-NLS-1$					
				SWTUtil.setGridData(label,
						true,
						false,
						SWT.FILL,
						SWT.BEGINNING,
						1,
						1);

				tic = new XTimeIntervalChooser(time, SWT.NONE);
				tic.setFormat("HH:mm:ss,SSS"); //$NON-NLS-1$
				tic.setEnabled(false);
				SWTUtil.setGridData(tic,
						true,
						false,
						SWT.FILL,
						SWT.BEGINNING,
						1,
						1);
				tic.addSelectionListener(new SelectionAdapter( ) {

					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
					 */
					@Override
					public void widgetSelected(SelectionEvent e) {
						currentStartTime = ((XTimeIntervalChooser) e.widget).getChosenStartDate( )
								.getTime( );
						currentStopTime = ((XTimeIntervalChooser) e.widget).getChosenStopDate( )
								.getTime( );
					}

				});
				tic.setData(RECORD, currentRecord);
				tic.setStart(new Date(currentRecord.getStart( )));
				tic.setStop(new Date(currentRecord.getStop( )));
				tic.setEnabled(true);
				timeIntervalMap.put(tic, null);
			}
			catch (RuntimeException e) {
				logClass.debug("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
		}
		dialogShell.pack( );
		time.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.layout( );

		disableTimeChoosers( );
	}

	/**
	 * Must return the minimum of the differences between stop and
	 * start times.
	 * 
	 * @return The minimum of the differences.
	 */
	private int getMinimum() {
		int value = Integer.MAX_VALUE;
		for (Record record : currentRecords) {
			value = Math.min(value,
					(int) (record.getStop( ) - record.getStart( )));
		}
		return value;
	}

	/**
	 * Generates one or multiple charts from the entered data.
	 * 
	 * @return The charts.
	 * @see org.jfree.chart.JFreeChart
	 */
	private List<JFreeChart> generateChart() {
		Mode mode = getMode( );
		opt = new ChartOptions( );
		opt.set(OptionName.CSV_EXPORT, exportCSV.getSelection( ));
		opt.set(OptionName.PNG_EXPORT, exportPNG.getSelection( ));
		opt.set(OptionName.JPG_EXPORT, exportJPEG.getSelection( ));
		opt.set(OptionName.PDF_EXPORT, exportPDF.getSelection( ));
		opt.set(OptionName.USE_ALL_KEYS, allKeys.getSelection( ));
		opt.set(OptionName.THREE_D, threeD.getSelection( ));
		opt.set(OptionName.USE_RELATIVE, relative.getSelection( ));
		// opt.set(OptionName.SHOW_AVERAGE, showAverage.getSelection(
		// ));
		opt.set(OptionName.SHOW_THRESHOLD, showThreshold.getSelection( ));
		ChartManager.setOptions(opt);
		ChartManager.setRobotName(robotName);
		ChartManager.setThreshold(threshold.getSelectionDouble( ));

		switch (selectedChartType) {
			case TIME:
				return timeChart(mode);
			case PIE:
				return pieChart(mode);
			case BAR:
				return barChart(mode);
			case SCATTER:
				return scatterChart(mode);
			default:
				return null;
		}
	}

	/**
	 * Generates one or multiple scatter charts from the entered data.
	 * 
	 * @param mode
	 *            The mode.
	 * @return The charts.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.ui.widgets.dialogs.ChartConfigDialog.Mode
	 */
	private List<JFreeChart> scatterChart(Mode mode) {
		boolean origTime = useOriginalTime( );
		List<JFreeChart> charts = new ArrayList<JFreeChart>( );
		switch (mode) {
			case SINGLE:
				if (origTime) {
					charts.add(ChartManager.createScatterChart(currentRecord,
							keysList.getSelection( )));
				}
				else {
					charts.add(ChartManager.createScatterChart(currentRecord,
							keysList.getSelection( ),
							currentStartTime,
							currentStopTime));
				}
				return charts;
			case MULTI:
				Map<Record, Interval> recordIntervalMap = new HashMap<Record, Interval>( );
				Record r;
				for (XTimeIntervalChooser tic : timeIntervalMap.keySet( )) {
					r = (Record) tic.getData(RECORD);
					recordIntervalMap.put(r,
							new Interval(tic.getChosenStartDate( ),
									tic.getChosenStopDate( )));
				}
				return ChartManager.createScatterChart(recordIntervalMap,
						keysList.getSelection( ));
			case SINGLE_VIA_MULTI:
				List<Interval> l = new ArrayList<Interval>(timeIntervalMap.values( ));
				charts.add(ChartManager.createScatterChart(currentRecords.get(0),
						keysList.getSelection( ),
						l.get(0)));
				return charts;
			default:
				return null;
		}
	}

	/**
	 * Generates one or multiple time charts for the entered data.
	 * 
	 * @param mode
	 *            The mode.
	 * @return The charts.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.ui.widgets.dialogs.ChartConfigDialog.Mode
	 */
	private List<JFreeChart> timeChart(Mode mode) {
		boolean origTime = useOriginalTime( );
		List<JFreeChart> charts = new ArrayList<JFreeChart>( );
		switch (mode) {
			case SINGLE:
				if (origTime) {
					charts.add(ChartManager.createTimeSeriesChart(currentRecord,
							keysList.getSelection( )));
					break;
				}
				charts.add(ChartManager.createTimeSeriesChart(currentRecord,
						keysList.getSelection( ),
						currentStartTime,
						currentStopTime));
				break;
			case MULTI:
				Map<Record, Interval> recordIntervalMap = new HashMap<Record, Interval>( );
				Record r;
				for (XTimeIntervalChooser tic : timeIntervalMap.keySet( )) {
					r = (Record) tic.getData(RECORD);
					recordIntervalMap.put(r,
							new Interval(tic.getChosenStartDate( ),
									tic.getChosenStopDate( )));
				}
				charts = ChartManager.createTimeSeriesChart(recordIntervalMap,
						keysList.getSelection( ));
				break;
			case SINGLE_VIA_MULTI:
				List<Interval> l = new ArrayList<Interval>(timeIntervalMap.values( ));
				charts.add(ChartManager.createTimeSeriesChart(currentRecords.get(0),
						keysList.getSelection( ),
						l.get(0)));
				break;
			default:
				return null;
		}
		return charts;
	}

	/**
	 * Generates one or multiple pie charts for the entered data.
	 * 
	 * @param mode
	 *            The mode.
	 * @return The charts.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.ui.widgets.dialogs.ChartConfigDialog.Mode
	 */
	private List<JFreeChart> pieChart(Mode mode) {
		List<JFreeChart> charts = new ArrayList<JFreeChart>( );
		switch (mode) {
			case SINGLE:
				charts.add(ChartManager.createPieChart(currentRecord,
						keysList.getSelection( )));
				return charts;
			case MULTI:
				charts = ChartManager.createPieChart(currentRecords,
						keysList.getSelection( ));
				return charts;
			case SINGLE_VIA_MULTI:
				charts.add(ChartManager.createPieChart(currentRecords.get(0),
						keysList.getSelection( )));
				return charts;
			default:
				return null;
		}
	}

	/**
	 * Generates one or multiple bar charts for the entered data.
	 * 
	 * @param mode
	 *            The mode.
	 * @return The charts.
	 * @see org.jfree.chart.JFreeChart
	 * @see de.xirp.ui.widgets.dialogs.ChartConfigDialog.Mode
	 */
	private List<JFreeChart> barChart(Mode mode) {
		boolean origTime = useOriginalTime( );
		List<JFreeChart> charts = new ArrayList<JFreeChart>( );
		switch (mode) {
			case SINGLE:
				if (origTime) {
					charts.add(ChartManager.createBarChart(currentRecord,
							keysList.getSelection( )));
				}
				else {
					charts.add(ChartManager.createBarChart(currentRecord,
							keysList.getSelection( ),
							currentStartTime,
							currentStopTime));
				}
				return charts;
			case MULTI:
				Map<Record, Interval> recordIntervalMap = new HashMap<Record, Interval>( );
				Record r;
				for (XTimeIntervalChooser tic : timeIntervalMap.keySet( )) {
					r = (Record) tic.getData(RECORD);
					recordIntervalMap.put(r,
							new Interval(tic.getChosenStartDate( ),
									tic.getChosenStopDate( )));
				}
				return ChartManager.createBarChart(recordIntervalMap,
						keysList.getSelection( ));
			case SINGLE_VIA_MULTI:
				List<Interval> l = new ArrayList<Interval>(timeIntervalMap.values( ));
				charts.add(ChartManager.createBarChart(currentRecords.get(0),
						keysList.getSelection( ),
						l.get(0)));
				return charts;
			default:
				return null;
		}
	}

	/**
	 * Returns the generation mode.
	 * 
	 * @return The mode.
	 * @see de.xirp.ui.widgets.dialogs.ChartConfigDialog.Mode
	 */
	private Mode getMode() {
		if (compareRecords.getSelection( )) {
			if (currentRecords.size( ) == 1) {
				return Mode.SINGLE_VIA_MULTI;
			}
			else if (currentRecords.size( ) > 1) {
				return Mode.MULTI;
			}
		}
		else {
			return Mode.SINGLE;
		}
		return Mode.UNDEFINED;
	}

	/**
	 * Opens the chart dialog for the given list of charts.
	 * 
	 * @param parent
	 *            The parent shell.
	 * @param charts
	 *            The charts to show.
	 * @see de.xirp.ui.widgets.dialogs.ChartDialog
	 * @see org.jfree.chart.JFreeChart
	 */
	private void showChart(Shell parent, List<JFreeChart> charts) {
		if (charts != null && !charts.isEmpty( )) {
			ChartDialog cd = new ChartDialog(parent, charts, robotName);
			cd.open( );
		}
	}

	/**
	 * Returns if the original time or the selected time should be
	 * used.
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>: use original time.<br>
	 *         <code>false</code>: don't use original time.<br>
	 */
	private boolean useOriginalTime() {
		if (compareRecords.getSelection( )) {
			return false;
		}
		if (currentStartTime != currentRecord.getStart( )) {
			return false;
		}
		else if (currentStopTime != currentRecord.getStop( )) {
			return false;
		}
		return true;
	}

	/**
	 * Sets the keys list. The keys of the selected record are added
	 * to the list.
	 * 
	 * @see de.xirp.db.Record
	 */
	private void setKeysList() {
		keysList.removeAll( );

		Collection<String> keys;
		if (compareRecords.getSelection( )) {
			keys = ChartDatabaseUtil.getCommonObservedKeysForRecordList(currentRecords);
		}
		else {
			keys = ChartDatabaseUtil.getObservedKeysForRecord(currentRecord);
		}
		for (String keyName : keys) {
			keysList.add(keyName);
		}

		if (allKeys.getSelection( )) {
			keysList.selectAll( );
		}
	}
}
