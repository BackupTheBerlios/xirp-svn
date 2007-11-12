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
 * StopWatch.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 29.03.2006:		Created by Rabea Gransberger.
 */
package de.xirp.ui.widgets;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import de.xirp.settings.PropertiesManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.ui.util.ressource.FontManager;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XText;
import de.xirp.ui.widgets.custom.XToolBar;
import de.xirp.ui.widgets.custom.XToolItem;
import de.xirp.util.Util;

/**
 * Stop watch for measuring the time.
 * 
 * @author Rabea Gransberger
 */
public final class StopWatch {

	/**
	 * Parent tool bar.
	 */
	private XToolBar toolBar;
	/**
	 * Text field for displaying the actual elapsed time.
	 */
	private XText timerText;
	/**
	 * A button to start the watch.
	 */
	private XToolItem toolItemStartTimer;
	/**
	 * A button to pause the watch.
	 */
	private XToolItem toolItemPauseTimer;
	/**
	 * Flag is <code>true</code> if watch is running or paused.
	 */
	private boolean RUNNING;
	/**
	 * Flag is <code>true</code> if running and paused.
	 */
	private boolean PAUSED;
	/**
	 * Time at which the watch was started.
	 */
	private long start;
	/**
	 * The timer used to update the watch.
	 */
	private Timer timer;
	/**
	 * Time at which the watch was paused.
	 */
	private long pauseTime;
	/**
	 * Actual elapsed time.
	 */
	private long time;
	/**
	 * A button to stop the watch.
	 */
	private XToolItem toolItemStopTimer;
	/**
	 * Time at which a warning (time changes color) should occur.
	 */
	private long warningTime;
	/**
	 * Time at which the Time should end.
	 */
	private long finalTime;
	/**
	 * A color for the time text (warning).
	 */
	private static final RGB RED = new RGB(255, 0, 0);
	/**
	 * A color for the time text (warning < 80%).
	 */
	private static final RGB ORANGE = new RGB(243, 164, 75);
	/**
	 * A color for the time text (time's up).
	 */
	private static final RGB LILA = new RGB(175, 120, 175);
	/**
	 * A color for the time text (normal).
	 */
	private static final RGB BLACK = new RGB(0, 0, 0);

	/**
	 * Sets a Stopwatch on the given tool bar.
	 * 
	 * @param toolbar
	 *            Tool bar as parent for the stop watch tool items.
	 */
	public StopWatch(XToolBar toolbar) {
		this.toolBar = toolbar;
		warningTime = PropertiesManager.getTimerWarning( ) * 1000;
		finalTime = PropertiesManager.getTimerFinal( ) * 1000;
		init( );
	}

	/**
	 * Sets up the UI.
	 */
	private void init() {
		timerText = new XText(toolBar, SWT.NONE | SWT.READ_ONLY | SWT.CENTER
				| SWT.WRAP, false);
		timerText.setText(Util.getTimeAsString(time, "mm:ss")); //$NON-NLS-1$
		timerText.setSize(70, 30);
		timerText.setFont(FontManager.getFont("Arial", 15, SWT.BOLD)); //$NON-NLS-1$
		// Stop Timer im Widget is disposed
		timerText.addDisposeListener(new DisposeListener( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
			 */
			public void widgetDisposed(@SuppressWarnings("unused")
			DisposeEvent event) {
				if (timer != null) {
					timer.cancel( );
				}
			}
		});

		XToolItem separator = new XToolItem(toolBar, SWT.SEPARATOR | SWT.FLAT);
		separator.setWidth(timerText.getSize( ).x);
		separator.setControl(timerText);

		toolItemStartTimer = new XToolItem(toolBar, SWT.PUSH);
		toolItemStartTimer.setToolTipTextForLocaleKey("StopWatch.gui.toolTip.startTimer"); //$NON-NLS-1$
		final Image startTimer = ImageManager.getSystemImage(SystemImage.START);
		final Image startTimerDis = ImageManager.getSystemImage(SystemImage.START_DISABLED);
		toolItemStartTimer.setImage(startTimer);
		toolItemStartTimer.setDisabledImage(startTimerDis);
		toolItemStartTimer.addSelectionListener(new SelectionAdapter( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(@SuppressWarnings("unused")
			SelectionEvent e) {
				start( );
			}
		});

		toolItemPauseTimer = new XToolItem(toolBar, SWT.PUSH);
		toolItemPauseTimer.setToolTipTextForLocaleKey("StopWatch.gui.toolTip.pauseTimer"); //$NON-NLS-1$
		final Image pauseTimer = ImageManager.getSystemImage(SystemImage.PAUSE);
		final Image pauseTimerDis = ImageManager.getSystemImage(SystemImage.PAUSE_DISABLED);

		toolItemPauseTimer.setImage(pauseTimer);
		toolItemPauseTimer.setDisabledImage(pauseTimerDis);
		toolItemPauseTimer.setEnabled(false);
		toolItemPauseTimer.addSelectionListener(new SelectionAdapter( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(@SuppressWarnings("unused")
			SelectionEvent e) {
				pause( );
			}
		});

		toolItemStopTimer = new XToolItem(toolBar, SWT.PUSH);
		toolItemStopTimer.setToolTipTextForLocaleKey("StopWatch.gui.toolTip.stopResetTimer"); //$NON-NLS-1$
		final Image stopTimer = ImageManager.getSystemImage(SystemImage.STOP);
		final Image stopTimerDis = ImageManager.getSystemImage(SystemImage.STOP_DISABLED);
		toolItemStopTimer.setImage(stopTimer);
		toolItemStopTimer.setDisabledImage(stopTimerDis);
		toolItemStopTimer.addSelectionListener(new SelectionAdapter( ) {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(@SuppressWarnings("unused")
			SelectionEvent e) {
				stop( );
			}
		});

		reset( );
	}

	/**
	 * Starts the watch.
	 */
	public void start() {
		warningTime = PropertiesManager.getTimerWarning( ) * 1000;
		finalTime = PropertiesManager.getTimerFinal( ) * 1000;

		RUNNING = true;
		PAUSED = false;
		start = System.currentTimeMillis( );
		if (timer == null) {
			timer = new Timer( );
			timer.scheduleAtFixedRate(new TimerTask( ) {

				private Color lastForegroundColor;

				/* (non-Javadoc)
				 * @see java.util.TimerTask#run()
				 */
				@Override
				public void run() {
					if (RUNNING && !PAUSED) {
						long current = System.currentTimeMillis( );
						time = pauseTime + (current - start);
						final String timeStrg = Util.getTimeAsString(time,
								"mm:ss"); //$NON-NLS-1$
						if (SWTUtil.swtAssert(timerText)) {
							lastForegroundColor = ColorManager.getColor(BLACK);
							if (time < warningTime * .8) {
								lastForegroundColor = ColorManager.getColor(BLACK);
							}
							else if (time > (warningTime * .8)
									&& time < warningTime) {
								lastForegroundColor = ColorManager.getColor(ORANGE);
							}
							else if (time > warningTime && time < finalTime) {
								lastForegroundColor = ColorManager.getColor(RED);
							}
							else {
								lastForegroundColor = ColorManager.getColor(LILA);
							}
							SWTUtil.asyncExec(new Runnable( ) {

								/* (non-Javadoc)
								 * @see java.lang.Runnable#run()
								 */
								public void run() {
									timerText.setForeground(lastForegroundColor);
									timerText.setText(timeStrg);
								}
							});
						}
					}
				}
			},
					5,
					500);
		}
		toolItemStartTimer.setEnabled(false);
		toolItemPauseTimer.setEnabled(true);
		toolItemStopTimer.setEnabled(true);
	}

	/**
	 * Pauses the watch.
	 */
	public void pause() {
		if (RUNNING) {
			PAUSED = true;
			long current = System.currentTimeMillis( );
			time = pauseTime + (current - start);
			pauseTime = time;

			toolItemStartTimer.setEnabled(true);
			toolItemPauseTimer.setEnabled(false);
			toolItemStopTimer.setEnabled(true);

		}
	}

	/**
	 * Stops the watch and resets it.
	 */
	public void stop() {
		if (RUNNING || PAUSED) {
			long current = System.currentTimeMillis( );
			time = pauseTime + (current - start);
			timer.cancel( );
			timer = null;

			toolItemStartTimer.setEnabled(true);
			toolItemPauseTimer.setEnabled(false);
			toolItemStopTimer.setEnabled(true);
		}

		reset( );
	}

	/**
	 * Resets the time.
	 */
	private void reset() {
		RUNNING = false;
		PAUSED = false;
		time = 0;
		pauseTime = 0;
		start = 0;
		timerText.setText(Util.getTimeAsString(time, "mm:ss")); //$NON-NLS-1$
	}
}
