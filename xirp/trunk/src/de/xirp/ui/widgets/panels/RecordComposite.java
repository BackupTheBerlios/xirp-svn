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
 * RecordComposite.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 02.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

import de.xirp.db.Observed;
import de.xirp.db.Record;
import de.xirp.db.RecordDatabaseUtil;
import de.xirp.io.comm.CommunicationManager;
import de.xirp.io.comm.data.Datapool;
import de.xirp.io.comm.data.DatapoolEvent;
import de.xirp.io.comm.data.DatapoolException;
import de.xirp.io.comm.data.DatapoolListener;
import de.xirp.io.comm.data.DatapoolManager;
import de.xirp.io.event.ConnectionEvent;
import de.xirp.io.event.ConnectionListener;
import de.xirp.profile.ProfileManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XCheckBox;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.ui.widgets.custom.XGroup;
import de.xirp.ui.widgets.custom.XList;
import de.xirp.ui.widgets.custom.XToggleButton;
import de.xirp.ui.widgets.dialogs.InputDialog;
import de.xirp.util.Constants;

/**
 * This composite allows to start and stop recording of data of
 * sensors to the database.
 * 
 * @author Matthias Gernand
 */
public class RecordComposite extends XComposite {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(RecordComposite.class);
	/**
	 * The robot name.
	 */
	private String robotName;
	/**
	 * A group.
	 */
	private XGroup select;
	/**
	 * A list.
	 */
	private XList avail;
	/**
	 * A list.
	 */
	private XList chosen;
	/**
	 * A toggle button.
	 */
	private XToggleButton startStop;
	/**
	 * A check box.
	 */
	private XCheckBox observed;
	/**
	 * A datapool.
	 * 
	 * @see de.xirp.io.comm.data.Datapool
	 */
	private Datapool pool;
	/**
	 * A datapool listener.
	 * 
	 * @see de.xirp.io.comm.data.DatapoolListener
	 */
	private DatapoolListener observedListener;
	/**
	 * A record.
	 * 
	 * @see de.xirp.db.Record
	 */
	private Record recordObserved;
	/**
	 * The connection status.
	 */
	private boolean isConnected = false;
	/**
	 * A connection listener.
	 * 
	 * @see de.xirp.io.event.ConnectionListener
	 */
	private ConnectionListener connectionListener;

	/**
	 * Constructs a new record composite.
	 * 
	 * @param parent
	 *            The parent.
	 * @param robotName
	 *            The robot name.
	 */
	public RecordComposite(Composite parent, String robotName) {
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
				CommunicationManager.removeConnectionListener(connectionListener);
				pool.removeDatapoolReceiveListener(observedListener);
			}

		});

		initDatapool( );

		SWTUtil.setGridLayout(this, 1, true);

		XGroup opt = new XGroup(this, SWT.NONE);
		opt.setTextForLocaleKey("RecordComposite.group.title.recordingOptions"); //$NON-NLS-1$
		SWTUtil.setGridData(opt, true, false, SWT.FILL, SWT.BEGINNING, 1, 1);
		SWTUtil.setGridLayout(opt, 1, true);

		observed = new XCheckBox(opt);
		observed.setTextForLocaleKey("RecordComposite.checkbox.enableObservedMode"); //$NON-NLS-1$
		SWTUtil.setGridData(observed,
				true,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				1,
				1);
		observed.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				XCheckBox box = (XCheckBox) e.widget;
				select.setEnabled(box.getSelection( ));
			}

		});

		select = new XGroup(this, SWT.NONE);
		select.setEnabled(false);
		select.setTextForLocaleKey("RecordComposite.group.title.selectKeys"); //$NON-NLS-1$
		SWTUtil.setGridData(select, true, true, SWT.FILL, SWT.FILL, 1, 1);
		SWTUtil.setGridLayout(select, 5, true);

		avail = new XList(select, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		SWTUtil.setGridData(avail, true, true, SWT.FILL, SWT.FILL, 2, 1);
		for (String key : ProfileManager.getSensorDatapoolKeys(robotName)) {
			avail.add(key);
		}

		XComposite holder = new XComposite(select, SWT.NONE);
		SWTUtil.setGridData(holder, false, false, SWT.CENTER, SWT.CENTER, 1, 1);
		SWTUtil.setGridLayout(holder, 1, true);

		XButton add = new XButton(holder);
		add.setText(">>>"); //$NON-NLS-1$
		SWTUtil.setGridData(add, false, false, SWT.FILL, SWT.CENTER, 1, 1);
		add.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (avail.getSelectionCount( ) > 0) {
					List<String> keys = new ArrayList<String>( );
					Collections.addAll(keys, chosen.getItems( ));
					for (String key : avail.getSelection( )) {
						if (!keys.contains(key)) {
							chosen.add(key);
						}
					}
				}
				setStartStop( );
			}
		});

		XButton remove = new XButton(holder);
		remove.setText("<<<"); //$NON-NLS-1$
		SWTUtil.setGridData(remove, false, false, SWT.FILL, SWT.CENTER, 1, 1);
		remove.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				chosen.remove(chosen.getSelectionIndices( ));
				setStartStop( );
			}

		});

		chosen = new XList(select, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		SWTUtil.setGridData(chosen, true, true, SWT.FILL, SWT.FILL, 2, 1);

		startStop = new XToggleButton(this);
		startStop.setTextForLocaleKey("RecordComposite.button.startRecord"); //$NON-NLS-1$
		startStop.setEnabled(false);
		startStop.setImage(ImageManager.getSystemImage(SystemImage.DATABASE_START_RECORD));
		SWTUtil.setGridData(startStop, true, false, SWT.FILL, SWT.END, 1, 1);
		startStop.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				XToggleButton btn = (XToggleButton) e.widget;
				boolean selection = btn.getSelection( );
				observed.setEnabled(!selection);
				select.setEnabled(!selection);
				if (selection) {
					btn.setTextForLocaleKey("RecordComposite.button.stopRecord"); //$NON-NLS-1$
					btn.setImage(ImageManager.getSystemImage(SystemImage.DATABASE_STOP_RECORD));
				}
				else {
					btn.setTextForLocaleKey("RecordComposite.button.startRecord"); //$NON-NLS-1$
					btn.setImage(ImageManager.getSystemImage(SystemImage.DATABASE_START_RECORD));
				}
				setRecordingEnabled(selection);
			}

		});
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

		observedListener = new DatapoolListener( ) {

			public void valueChanged(DatapoolEvent e) {
				try {
					saveObservedValue(e.getTimestamp( ),
							e.getKey( ),
							(Number) e.getValue( ));
				}
				catch (ClassCastException ex) {
					logClass.info("Error: " + ex.getMessage( ) + " (check your protocol for key: " + e.getKey( ) + ")" + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}

			public boolean notifyOnlyWhenChanged() {
				return false;
			}

		};

		connectionListener = new ConnectionListener( ) {

			public void connectionEstablished(ConnectionEvent event) {
				if (event.getRobotName( ).equals(robotName)) {
					isConnected = true;
					SWTUtil.asyncExec(new Runnable( ) {

						@Override
						public void run() {
							setStartStop( );
						}

					});

				}
			}

			public void disconnected(ConnectionEvent event) {
				if (event.getRobotName( ).equals(robotName)) {
					isConnected = false;
					SWTUtil.asyncExec(new Runnable( ) {

						@Override
						public void run() {
							setStartStop( );
						}

					});
				}
			}
		};

		CommunicationManager.addConnectionListener(connectionListener);
	}

	/**
	 * Saves the observed value.
	 * 
	 * @param timestamp
	 *            The time stamp.
	 * @param key
	 *            The key.
	 * @param value
	 *            The value.
	 */
	private void saveObservedValue(long timestamp, String key, Number value) {
		Observed obs = new Observed(timestamp, key, value.doubleValue( ));
		recordObserved.addObserved(obs);
	}

	/**
	 * Persists the recording.
	 * 
	 * @param record
	 *            The record.
	 * @see de.xirp.db.Record
	 */
	private void persistRecording(final Record record) {

		SWTUtil.showBusyWhile(getShell( ), new Runnable( ) {

			public void run() {
				InputDialog id = new InputDialog(getShell( ),
						"RecordComposite.inputdialog.value.one.name", //$NON-NLS-1$
						"RecordComposite.inputdialog.value.two.comment"); //$NON-NLS-1$
				List<String> result = id.open(1);

				if (result != null && !result.isEmpty( )) {
					record.setName(result.get(0));
					record.setComment(result.get(1));
				}
				RecordDatabaseUtil.persistRecord(record);
			}
		});
	}

	/**
	 * Sets the start stop status of the record panel.
	 */
	private void setStartStop() {
		if (SWTUtil.swtAssert(this)) {
			if ((chosen.getItemCount( ) > 0) && isConnected) {
				startStop.setEnabled(true);
			}
			else {
				startStop.setEnabled(false);
			}
		}
	}

	/**
	 * Sets the recording enabled or disabled.
	 * 
	 * @param enabled
	 *            <code>true</code>: enabled.
	 */
	private void setRecordingEnabled(boolean enabled) {
		if (enabled) {
			if (observed.getSelection( )) {
				recordObserved = new Record(robotName);
				recordObserved.setStart(new Date( ).getTime( ));
				recordObserved.setName(""); //$NON-NLS-1$
				recordObserved.setComment(""); //$NON-NLS-1$
				for (String key : chosen.getItems( )) {
					pool.addDatapoolReceiveListener(key, observedListener);
				}
			}
		}
		else {
			if (observed.getSelection( )) {
				for (String key : chosen.getItems( )) {
					pool.removeDatapoolReceiveListener(key, observedListener);
				}
				recordObserved.setStop(new Date( ).getTime( ));
				persistRecording(recordObserved);
			}
		}
	}
}
