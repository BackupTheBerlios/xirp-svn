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
 * ReportSearchDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 05.07.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

import de.xirp.db.ReportDatabaseUtil;
import de.xirp.managers.PrintManager;
import de.xirp.report.ReportDescriptor;
import de.xirp.report.ReportManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.*;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.util.Util;

/**
 * This dialog is used to enter a search query for a database search
 * for saved reports.
 * 
 * @author Matthias Gernand
 * @see de.xirp.report.ReportDescriptor
 * @see de.xirp.report.Report
 */
public final class ReportSearchDialog extends XDialog {

	/**
	 * Width of the dialog.
	 */
	private static final int WIDTH = 600;
	/**
	 * Height of the dialog.
	 */
	private static final int HEIGHT = 400;
	/**
	 * The parent shell of the dialog.
	 */
	private Shell parent = null;
	/**
	 * The shell of the dialog window.
	 */
	private XShell dialogShell = null;
	/**
	 * A date time widget.
	 */
	private DateTime startDate;
	/**
	 * A date time widget.
	 */
	private DateTime startTime;
	/**
	 * A date time widget.
	 */
	private DateTime stopDate;
	/**
	 * A date time widget.
	 */
	private DateTime stopTime;
	/**
	 * A button.
	 */
	private XButton open;
	/**
	 * A check box.
	 */
	private XCheckBox enableTimeSearchOptions;
	/**
	 * A combo box.
	 */
	private XCombo robots;
	/**
	 * A table.
	 */
	private XTable table;
	/**
	 * A report descriptor.
	 * 
	 * @see de.xirp.report.ReportDescriptor
	 */
	private ReportDescriptor report;
	/**
	 * A button.
	 */
	private XButton ok;
	/**
	 * A button.
	 */
	private XButton print;

	/**
	 * Constructs new search dialog.
	 * 
	 * @param parent
	 *            The parent.
	 */
	public ReportSearchDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
	}

	/**
	 * Opens the search dialog.
	 * 
	 * @return The selected report entry.
	 */
	public ReportDescriptor open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});

		dialogShell.setSize(WIDTH, HEIGHT);
		dialogShell.setTextForLocaleKey("ReportSearchDialog.gui.title"); //$NON-NLS-1$
		dialogShell.setImage(ImageManager.getSystemImage(SystemImage.SEARCH));

		SWTUtil.setGridLayout(dialogShell, 5, true);

		XGroup searchOptions = new XGroup(dialogShell, SWT.NONE);
		searchOptions.setTextForLocaleKey("ReportSearchDialog.group.title.searchOptions"); //$NON-NLS-1$
		SWTUtil.setGridData(searchOptions,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				5,
				1);
		SWTUtil.setGridLayout(searchOptions, 3, false);

		enableTimeSearchOptions = new XCheckBox(searchOptions);
		enableTimeSearchOptions.setSelection(false);
		enableTimeSearchOptions.setTextForLocaleKey("ReportSearchDialog.checkbox.enableTimeOptions"); //$NON-NLS-1$
		SWTUtil.setGridData(enableTimeSearchOptions,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				3,
				1);
		enableTimeSearchOptions.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				XCheckBox box = (XCheckBox) e.widget;
				startDate.setEnabled(box.getSelection( ));
				startTime.setEnabled(box.getSelection( ));
				stopDate.setEnabled(box.getSelection( ));
				stopTime.setEnabled(box.getSelection( ));
			}

		});

		XLabel label = new XLabel(searchOptions, SWT.NONE);
		label.setTextForLocaleKey("ReportSearchDialog.label.startingTime"); //$NON-NLS-1$
		SWTUtil.setGridData(label, true, false, SWT.FILL, SWT.BEGINNING, 1, 1);

		startDate = new DateTime(searchOptions, SWT.DATE | SWT.LONG);
		startDate.setEnabled(false);
		SWTUtil.setGridData(startDate,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				1,
				1);

		startTime = new DateTime(searchOptions, SWT.TIME | SWT.SHORT);
		SWTUtil.setGridData(startTime,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				1,
				1);
		startTime.setEnabled(false);

		label = new XLabel(searchOptions, SWT.NONE);
		label.setTextForLocaleKey("ReportSearchDialog.label.endTime"); //$NON-NLS-1$
		SWTUtil.setGridData(label, true, false, SWT.FILL, SWT.BEGINNING, 1, 1);

		stopDate = new DateTime(searchOptions, SWT.DATE | SWT.LONG);
		SWTUtil.setGridData(stopDate,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				1,
				1);
		stopDate.setEnabled(false);

		stopTime = new DateTime(searchOptions, SWT.TIME | SWT.SHORT);
		SWTUtil.setGridData(stopTime,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				1,
				1);
		stopTime.setEnabled(false);

		label = new XLabel(searchOptions, SWT.NONE);
		label.setTextForLocaleKey("ReportSearchDialog.label.robotsInDatabase"); //$NON-NLS-1$
		SWTUtil.setGridData(label, true, false, SWT.FILL, SWT.BEGINNING, 1, 1);

		robots = new XCombo(searchOptions, SWT.BORDER | SWT.READ_ONLY);
		SWTUtil.setGridData(robots, true, false, SWT.FILL, SWT.BEGINNING, 2, 1);
		for (String robotName : ReportDatabaseUtil.getAvailableRobots( )) {
			robots.add(robotName);
		}

		XGroup searchResult = new XGroup(dialogShell, SWT.NONE);
		searchResult.setTextForLocaleKey("ReportSearchDialog.group.title.searchResult"); //$NON-NLS-1$
		SWTUtil.setGridData(searchResult, true, true, SWT.FILL, SWT.FILL, 5, 1);
		SWTUtil.setGridLayout(searchResult, 1, false);

		table = new XTable(searchResult, SWT.SINGLE | SWT.BORDER |
				SWT.FULL_SELECTION | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		SWTUtil.setGridData(table, true, true, SWT.FILL, SWT.FILL, 1, 1);
		table.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				XTable table = (XTable) e.widget;
				if (table.getSelectionCount( ) > 0) {
					open.setEnabled(true);
					print.setEnabled(true);
					ok.setEnabled(true);
				}
				else {
					open.setEnabled(false);
					print.setEnabled(false);
					ok.setEnabled(false);
				}
			}

		});

		String[] keys = new String[] {
				"ReportSearchDialog.table.column.id", "ReportSearchDialog.table.column.reportName", "ReportSearchDialog.table.column.creationTime", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"ReportSearchDialog.table.column.robotName", "ReportSearchDialog.table.column.fileName"}; //$NON-NLS-1$ //$NON-NLS-2$
		for (String key : keys) {
			XTableColumn column = new XTableColumn(table, SWT.NONE);
			column.setTextForLocaleKey(key);
			column.pack( );
		}

		ok = new XButton(dialogShell, XButtonType.OK);
		SWTUtil.setGridData(ok, true, false, SWT.FILL, SWT.END, 1, 1);
		ok.setEnabled(false);
		ok.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				report = getReport( );
				dialogShell.close( );
			}

		});

		XButton search = new XButton(dialogShell);
		search.setTextForLocaleKey("ReportSearchDialog.button.searchForReports"); //$NON-NLS-1$
		SWTUtil.setGridData(search, true, false, SWT.FILL, SWT.END, 1, 1);
		search.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				SWTUtil.showBusyWhile(dialogShell, new Runnable( ) {

					public void run() {
						startSearch( );
					}

				});
			}

		});

		open = new XButton(dialogShell);
		open.setTextForLocaleKey("ReportSearchDialog.button.openSelectedReport"); //$NON-NLS-1$
		open.setEnabled(false);
		SWTUtil.setGridData(open, true, false, SWT.FILL, SWT.END, 1, 1);
		open.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				SWTUtil.showBusyWhile(dialogShell, new Runnable( ) {

					public void run() {
						openSelectedReport( );
					}

				});
			}

		});

		print = new XButton(dialogShell);
		print.setTextForLocaleKey("ReportSearchDialog.button.printSelectedReport"); //$NON-NLS-1$
		print.setEnabled(false);
		SWTUtil.setGridData(print, true, false, SWT.FILL, SWT.END, 1, 1);
		print.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				SWTUtil.showBusyWhile(dialogShell, new Runnable( ) {

					public void run() {
						printSelectedReport( );
					}

				});
			}

		});

		XButton close = new XButton(dialogShell, XButtonType.CLOSE);
		SWTUtil.setGridData(close, true, false, SWT.FILL, SWT.END, 1, 1);
		close.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				report = null;
				dialogShell.close( );
			}

		});

		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		SWTUtil.blockDialogFromReturning(dialogShell);
		return report;
	}

	/**
	 * Prints the selected report.
	 * 
	 * @see de.xirp.report.ReportDescriptor
	 * @see de.xirp.managers.PrintManager#print(ReportDescriptor)
	 */
	private void printSelectedReport() {
		ReportDescriptor rd = getReport( );
		PrintManager.print(rd);
	}

	/**
	 * Opens the selected report in the associated PDF viewing tool.
	 * 
	 * @see de.xirp.report.ReportDescriptor
	 * @see de.xirp.report.ReportManager#viewReport(ReportDescriptor)
	 */
	private void openSelectedReport() {
		ReportDescriptor rd = getReport( );
		if (rd != null) {
			ReportManager.viewReport(rd);
		}
	}

	/**
	 * Returns the report descriptor for the selected info entry.
	 * 
	 * @return The corresponding report descriptor.
	 * @see de.xirp.report.ReportDescriptor
	 * @see de.xirp.db.ReportDatabaseUtil#getReport(long)
	 */
	private ReportDescriptor getReport() {
		int idx = table.getSelectionIndex( );
		long id = Long.parseLong(table.getItem(idx).getText(0));
		return ReportDatabaseUtil.getReport(id);
	}

	/**
	 * Starts the search for the entered query options.
	 */
	private void startSearch() {
		List<ReportDescriptor> reports;
		boolean useTime = enableTimeSearchOptions.getSelection( );
		Date start;
		Date stop;
		String robotName;
		try {
			robotName = robots.getItem(robots.getSelectionIndex( ));
		}
		catch (IllegalArgumentException e) {
			robotName = null;
		}

		if (useTime) {
			GregorianCalendar calendar = new GregorianCalendar(startDate.getYear( ),
					startDate.getMonth( ),
					startDate.getDay( ),
					startTime.getHours( ),
					startTime.getMinutes( ),
					startTime.getSeconds( ));
			start = new Date(calendar.getTimeInMillis( ));
			calendar = new GregorianCalendar(stopDate.getYear( ),
					stopDate.getMonth( ),
					stopDate.getDay( ),
					stopTime.getHours( ),
					stopTime.getMinutes( ),
					stopTime.getSeconds( ));
			stop = new Date(calendar.getTimeInMillis( ));
		}
		else {
			start = new Date(0);
			stop = new Date( );
		}

		reports = ReportDatabaseUtil.searchForReports(start, stop, robotName);

		table.removeAll( );
		for (ReportDescriptor report : reports) {
			XTableItem itm = new XTableItem(table, SWT.NONE);
			itm.setText(0, new Long(report.getId( )).toString( ));
			itm.setText(1, report.getReportName( ));
			itm.setText(2, Util.getTimeAsString(report.getCreationTime( ),
					"dd.MM.yyyy HH:mm:ss")); //$NON-NLS-1$
			itm.setText(3, report.getRobotName( ));
			itm.setText(4, report.getFileName( ));
		}
		SWTUtil.packTable(table);
	}
}
