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
 * RecordLookupDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 23.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import de.xirp.db.Record;
import de.xirp.db.RecordDatabaseUtil;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XTable;
import de.xirp.ui.widgets.custom.XTableColumn;
import de.xirp.ui.widgets.custom.XTableItem;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.util.Util;

/**
 * This dialog is used to look up one ore multiple records from the
 * database.
 * 
 * @author Matthias Gernand
 * @see de.xirp.db.Record
 */
public class RecordLookupDialog extends XDialog {

	/**
	 * The maximum selectable records.
	 */
	private static final int MAX_RECORDS = 4;
	/**
	 * The width of the dialog.
	 */
	private static final int WIDTH = 400;
	/**
	 * The height of the dialog.
	 */
	private static final int HEIGHT = 300;
	/**
	 * The parent shell.
	 */
	private Shell parent;
	/**
	 * The dialogs shell.
	 */
	private XShell dialogShell;
	/**
	 * The robot name.
	 */
	private String robotName;
	/**
	 * A button.
	 */
	private XButton ok;
	/**
	 * The selected records.
	 * 
	 * @see de.xirp.db.Record
	 */
	private List<Record> selectedRecords;
	/**
	 * The records from the database.
	 * 
	 * @see de.xirp.db.Record
	 */
	private List<Record> recordsFromDB;

	/**
	 * Constructs a new record look up dialog. All records in the
	 * database for the given robot name are selectable.
	 * 
	 * @param parent
	 *            The parent.
	 * @param robotName
	 *            the robot name.
	 * @see de.xirp.db.Record
	 */
	public RecordLookupDialog(Shell parent, String robotName) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
		this.robotName = robotName;
	}

	/**
	 * Constructs a new record look up dialog. All available records
	 * in the database are selectable.
	 * 
	 * @param parent
	 *            The parent.
	 * @see de.xirp.db.Record
	 */
	public RecordLookupDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
		this.robotName = null;
	}

	/**
	 * Opens the dialog for selecting a single record.
	 * 
	 * @return The selected record.
	 * @see de.xirp.db.Record
	 */
	public Record openSingle() {
		List<Record> aux = open(false);
		if (aux == null || aux.isEmpty( )) {
			return null;
		}
		return aux.get(0);
	}

	/**
	 * Opens the dialog for selecting up to four records.
	 * 
	 * @return The selected records.
	 * @see de.xirp.db.Record
	 */
	public List<Record> openMulti() {
		return open(true);
	}

	/**
	 * Opens the dialog for selecting a single or multiple records.
	 * The given flag <code>multi</code> indicates this.
	 * 
	 * @param multi
	 *            <br>
	 *            <code>true</code>: Multi select is available.<br>
	 *            <code>false</code>: Multi select is not
	 *            available.
	 * @return The selected records.
	 * @see de.xirp.db.Record
	 */
	private List<Record> open(boolean multi) {
		selectedRecords = new ArrayList<Record>( );
		// TODO: lazy wenn alle records angezeigt werden
		if (!Util.isEmpty(robotName)) {
			recordsFromDB = RecordDatabaseUtil.getAvailableRecordsForRobot(robotName);
		}
		else {
			recordsFromDB = RecordDatabaseUtil.getAllRecords( );
		}

		dialogShell = new XShell(parent, SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.ShellAdapter#shellClosed(org.eclipse.swt.events.ShellEvent)
			 */
			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});

		dialogShell.setSize(WIDTH, HEIGHT);
		dialogShell.setTextForLocaleKey("RecordLookupDialog.gui.title"); //$NON-NLS-1$
		Image image = ImageManager.getSystemImage(SystemImage.QUESTION);
		dialogShell.setImage(image);

		SWTUtil.setGridLayout(dialogShell, 2, true);

		final XTable table;
		if (multi) {
			table = new XTable(dialogShell, SWT.MULTI | SWT.BORDER |
					SWT.FULL_SELECTION | SWT.V_SCROLL);
		}
		else {
			table = new XTable(dialogShell, SWT.SINGLE | SWT.BORDER |
					SWT.FULL_SELECTION | SWT.V_SCROLL);
		}
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		String[] titles = {
				"RecordLookupDialog.table.column.robotName", "RecordLookupDialog.table.column.name", //$NON-NLS-1$ //$NON-NLS-2$
				"RecordLookupDialog.table.column.start", "RecordLookupDialog.table.column.stop", //$NON-NLS-1$ //$NON-NLS-2$ 
				"RecordLookupDialog.table.column.comment"}; //$NON-NLS-1$
		for (String t : titles) {
			XTableColumn column = new XTableColumn(table, SWT.NONE);
			column.setTextForLocaleKey(t);
			column.pack( );
		}
		SWTUtil.setGridData(table, true, true, SWT.FILL, SWT.FILL, 2, 1);

		for (Record r : recordsFromDB) {
			XTableItem itm = new XTableItem(table, SWT.NONE);
			itm.setText(0, r.getRobotName( ));
			itm.setText(1, r.getName( ));
			itm.setText(2, Util.getTimeAsString(r.getStart( ),
					"dd.MM.yyyy HH:mm:ss,SSS")); //$NON-NLS-1$
			itm.setText(3, Util.getTimeAsString(r.getStop( ),
					"dd.MM.yyyy HH:mm:ss,SSS")); //$NON-NLS-1$
			itm.setText(4, r.getComment( ));
		}

		table.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int count = table.getSelectionCount( );
				if (count > 0) {
					ok.setEnabled(true);
				}
				else {
					ok.setEnabled(false);
				}
				if (count > MAX_RECORDS) {
					table.deselect(table.getSelectionIndex( ));
				}
			}

		});

		SWTUtil.packTable(table);

		ok = new XButton(dialogShell, XButtonType.OK);
		SWTUtil.setGridData(ok, true, false, SWT.FILL, SWT.FILL, 1, 1);
		ok.setEnabled(false);
		ok.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int idx[] = table.getSelectionIndices( );
				for (int i = 0; i < idx.length; i++) {
					selectedRecords.add(recordsFromDB.get(idx[i]));
				}
				dialogShell.close( );
			}

		});

		XButton cancel = new XButton(dialogShell, XButtonType.CANCEL);
		SWTUtil.setGridData(cancel, true, false, SWT.FILL, SWT.FILL, 1, 1);
		cancel.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedRecords = Collections.emptyList( );
				dialogShell.close( );
			}

		});

		dialogShell.setDefaultButton(ok);
		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		SWTUtil.blockDialogFromReturning(dialogShell);

		return Collections.unmodifiableList(selectedRecords);
	}

}
