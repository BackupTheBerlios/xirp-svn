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
 * ApplicationStatusBar.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * 					 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 17.01.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import de.xirp.io.comm.CommunicationManager;
import de.xirp.io.comm.DataAmount;
import de.xirp.io.comm.data.Datapool;
import de.xirp.io.comm.data.DatapoolEvent;
import de.xirp.io.comm.data.DatapoolException;
import de.xirp.io.comm.data.DatapoolListener;
import de.xirp.io.comm.data.DatapoolManager;
import de.xirp.io.event.ConnectionEvent;
import de.xirp.io.event.ConnectionListener;
import de.xirp.profile.PowerSource;
import de.xirp.profile.Robot;
import de.xirp.ui.Application;
import de.xirp.ui.event.ContentChangedEvent;
import de.xirp.ui.event.ContentChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.MessageManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.MessageManager.MessageType;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XLabel;
import de.xirp.ui.widgets.custom.XMenuItem;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This UI class represents the status bar of the application.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class ApplicationStatusBar extends Composite {

	/**
	 * A string constant which is a key under which the currently
	 * shown power source is saved.
	 */
	private static final String POWER = "power"; //$NON-NLS-1$
	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(ApplicationStatusBar.class);
	/**
	 * A robot object to identify the active robot.
	 */
	private Robot currentRobot;
	/**
	 * A label for the active robot.
	 */
	private XLabel activeRobot;
	/**
	 * A label for the actual voltage.
	 */
	private XLabel voltage;
	/**
	 * A label for the IO status.
	 */
	private XLabel inOutStatus;
	/**
	 * A label for the connection status.
	 */
	private XLabel connStatus;
	/**
	 * A label for displaying hints.
	 */
	private XLabel hint;
	/**
	 * A separator.
	 */
	private Label sep;
	/**
	 * A filling composite.
	 */
	private Composite fill;
	/**
	 * An image object.
	 */
	private Image image;
	/**
	 * A label.
	 */
	private Label battpic;
	/**
	 * A content change listener.
	 * 
	 * @see de.xirp.ui.event.ContentChangedListener
	 */
	private ContentChangedListener contentListener;
	/**
	 * A menu.
	 */
	private Menu battMenu;
	/**
	 * A datapool listener.
	 * 
	 * @see de.xirp.io.comm.data.DatapoolListener
	 * @see de.xirp.io.comm.data.Datapool
	 */
	private DatapoolListener datapoolListener;
	/**
	 * The current power source.
	 * 
	 * @see de.xirp.profile.PowerSource
	 */
	private PowerSource currentPowerSource;
	/**
	 * A datapool.
	 * 
	 * @see de.xirp.io.comm.data.Datapool
	 */
	private Datapool currentDatapool;
	/**
	 * The sent string.
	 */
	private String sent = "0.0"; //$NON-NLS-1$
	/**
	 * The receive string.
	 */
	private String recv = "0.0"; //$NON-NLS-1$
	/**
	 * A map.
	 */
	private Map<String, Integer> currentBattMap = new HashMap<String, Integer>( );
	/**
	 * A map.
	 */
	private Map<String, Boolean> currentWarnedMap = new HashMap<String, Boolean>( );
	/**
	 * A connection listener.
	 * 
	 * @see de.xirp.io.event.ConnectionListener
	 */
	private ConnectionListener commListener;
	/**
	 * A timer.
	 */
	private Timer timer;
	/**
	 * The update interval in milliseconds.
	 */
	private static final long IN_OUT_UPDATE_INTERVAL = 1000;

	/**
	 * Constructs a new status bar with the given parent composite and
	 * the style.
	 * 
	 * @param parent
	 *            The parent composite.
	 * @param style
	 *            The style.
	 */
	public ApplicationStatusBar(Composite parent, int style) {
		super(parent, style);
		init( );
	}

	/**
	 * Initializes the status bar.
	 */
	private void init() {
		initListeners( );

		SWTUtil.setGridLayout(this, 12, false);
		GridLayout gl = (GridLayout) this.getLayout( );
		gl.marginHeight = 0;
		gl.marginWidth = 5;
		gl.horizontalSpacing = 5;
		gl.verticalSpacing = 0;

		activeRobot = new XLabel(this, SWT.LEFT, true);

		sep = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gd = new GridData( );
		gd.heightHint = 25;
		sep.setLayoutData(gd);

		connStatus = new XLabel(this, SWT.NONE, true);

		sep = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		gd = new GridData( );
		gd.heightHint = 25;
		sep.setLayoutData(gd);

		fill = new Composite(this, SWT.NONE) {

			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {
				return new Point(wHint, 0);
			}
		};
		SWTUtil.setGridData(fill, true, false, SWT.FILL, SWT.BEGINNING, 1, 1);
		fill.setSize(0, 25);

		hint = new XLabel(this, SWT.CENTER, true);

		fill = new Composite(this, SWT.NONE) {

			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {
				return new Point(wHint, 0);
			}
		};
		SWTUtil.setGridData(fill, true, false, SWT.FILL, SWT.BEGINNING, 1, 1);
		fill.setSize(0, 25);

		sep = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		gd = new GridData( );
		gd.heightHint = 25;
		sep.setLayoutData(gd);

		inOutStatus = new XLabel(this, SWT.CENTER, true);
		inOutStatus.setToolTipTextForLocaleKey("ApplicationStatusBar.tooltip.InOut"); //$NON-NLS-1$
		gd = SWTUtil.setGridData(inOutStatus,
				false,
				false,
				SWT.CENTER,
				SWT.CENTER,
				1,
				1);
		gd.minimumWidth = 75;

		sep = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		gd = new GridData( );
		gd.heightHint = 25;
		sep.setLayoutData(gd);

		voltage = new XLabel(this, SWT.RIGHT, true);
		gd = SWTUtil.setGridData(voltage,
				false,
				false,
				SWT.CENTER,
				SWT.CENTER,
				1,
				1);
		gd.minimumWidth = 30;

		battpic = new Label(this, SWT.NONE);
		image = ImageManager.getSystemImage(SystemImage.BATTERY);
		battpic.setImage(image);

		battMenu = new Menu(getShell( ), SWT.POP_UP);
		battpic.setMenu(battMenu);

		setPowerSourceValue(0.0, ""); //$NON-NLS-1$
		setInOut( );
	}

	/**
	 * Initializes the listeners.
	 */
	private void initListeners() {
		contentListener = new ContentChangedListener( ) {

			public void contentChanged(ContentChangedEvent event) {
				rearrangeStatus(event.getRobot( ));
			}

		};
		ApplicationManager.addContentChangedListener(contentListener);

		addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent e) {
				ApplicationManager.removeContentChangedListener(contentListener);
			}
		});

		datapoolListener = new DatapoolListener( ) {

			public boolean notifyOnlyWhenChanged() {
				return true;
			}

			public void valueChanged(DatapoolEvent e) {
				final Object aux = e.getValue( );
				if (aux instanceof Number) {
					SWTUtil.asyncExec(new Runnable( ) {

						public void run() {
							setPowerSourceValue((Number) aux,
									currentPowerSource.getUnit( ).unitName( ));
						}

					});
				}
			}
		};

		timer = new Timer( );
		timer.schedule(new TimerTask( ) {

			@Override
			public void run() {
				updateSentReceived( );
			}

		}, 0, IN_OUT_UPDATE_INTERVAL);

		commListener = new ConnectionListener( ) {

			public void connectionEstablished(ConnectionEvent event) {
				if (event.getRobotName( ).equals(currentRobot.getName( ))) {
					setConnected(true);
				}
			}

			public void disconnected(ConnectionEvent event) {
				if (event.getRobotName( ).equals(currentRobot.getName( ))) {
					setConnected(false);
				}
			}

		};
		CommunicationManager.addConnectionListener(commListener);

		addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent ev) {
				if (currentDatapool != null && currentPowerSource != null) {
					currentDatapool.removeDatapoolReceiveListener(currentPowerSource.getDatapoolKey( ),
							datapoolListener);
				}
				if (currentRobot != null) {
					CommunicationManager.removeConnectionListener(commListener);
				}
				timer.cancel( );
			}

		});

	}

	/**
	 * Sets the in/out status.
	 */
	private void setInOut() {
		SWTUtil.asyncExec(new Runnable( ) {

			public void run() {
				if (SWTUtil.swtAssert(inOutStatus) && SWTUtil.swtAssert(image)) {
					inOutStatus.setText(sent + " / " + recv); //$NON-NLS-1$
					inOutStatus.pack( );
					inOutStatus.getParent( ).layout( );
				}
			}

		});
	}

	/**
	 * Sets the menu items for the power source selection menu for the
	 * given robot.
	 * 
	 * @param robot
	 *            The robot.
	 * @see de.xirp.profile.Robot
	 */
	private void setMenuItems(final Robot robot) {
		MenuItem[] items = battMenu.getItems( );
		for (int i = 0; i < items.length; i++) {
			MenuItem m = items[i];
			if (m.getSelection( )) {
				currentBattMap.put(currentRobot.getName( ), i);
			}
			SWTUtil.secureDispose(m);
		}
		if (robot != null) {
			SelectionAdapter selectionAdapter = new SelectionAdapter( ) {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent event) {
					XMenuItem itm = (XMenuItem) event.widget;
					if (itm.getSelection( )) {
						try {
							if (currentDatapool != null &&
									currentPowerSource != null) {
								currentDatapool.removeDatapoolReceiveListener(currentPowerSource.getDatapoolKey( ),
										datapoolListener);
							}
							currentDatapool = DatapoolManager.getDatapool(robot);
							currentPowerSource = (PowerSource) itm.getData(POWER);
							currentDatapool.addDatapoolReceiveListener(currentPowerSource.getDatapoolKey( ),
									datapoolListener);
						}
						catch (DatapoolException e) {
							logClass.error("Error: " + e.getMessage( ) + //$NON-NLS-1$
									Constants.LINE_SEPARATOR, e);
						}
					}
				}
			};
			for (PowerSource s : robot.getPowerSources( )) {
				XMenuItem itm = new XMenuItem(battMenu, SWT.RADIO);
				itm.setTextForLocaleKey("ApplicationStatusBar.menu.item.max", s.getName( ), s.getMax( ), s.getUnit( ).unitName( )); //$NON-NLS-1$
				itm.setData(POWER, s);
				itm.addSelectionListener(selectionAdapter);
			}

			int i = 0;
			if (currentBattMap.containsKey(robot.getName( ))) {
				i = currentBattMap.get(robot.getName( ));
			}
			MenuItem mi = battMenu.getItem(i);
			mi.setSelection(true);
			Event e = new Event( );
			e.widget = mi;
			mi.notifyListeners(SWT.Selection, e);
		}
	}

	/**
	 * Rearranges the status bar, when the robot has changed.
	 * 
	 * @param robot
	 *            The new robot.
	 * @see de.xirp.profile.Robot
	 */
	private void rearrangeStatus(Robot robot) {
		setMenuItems(robot);
		if (robot != null) {
			setRobot(robot);
		}
		else {
			setControlOverview( );
		}
	}

	/**
	 * Sets the data of the status bar, and sets the necessary text
	 * fields.
	 * 
	 * @param robot
	 *            The robot to set.
	 */
	public void setRobot(Robot robot) {
		currentRobot = robot;
		activeRobot.setText(currentRobot.getName( ));
		setConnected(CommunicationManager.isConnected(robot.getName( )));
		updateSentReceived( );
		battpic.setImage(image);
		layout( );
	}

	/**
	 * Sets the status bar to a null position.
	 */
	public void setEmpty() {
		activeRobot.setText(""); //$NON-NLS-1$
		hint.setText(""); //$NON-NLS-1$
		voltage.setText(""); //$NON-NLS-1$
		inOutStatus.setText(""); //$NON-NLS-1$
		connStatus.setText(""); //$NON-NLS-1$
		layout( );
	}

	/**
	 * Sets the status bar for the case that the C.O.P. is active.
	 */
	public void setControlOverview() {
		activeRobot.setTextForLocaleKey("StatusBar.gui.multipleRobots"); //$NON-NLS-1$
		hint.setTextForLocaleKey("StatusBar.gui.operateMultipleRobots"); //$NON-NLS-1$
		voltage.setText(""); //$NON-NLS-1$
		inOutStatus.setText(""); //$NON-NLS-1$
		connStatus.setText(""); //$NON-NLS-1$
		battpic.setImage(null);
		layout( );
	}

	/**
	 * Sets the hint of the bar corresponding to the connection
	 * status.
	 * 
	 * @param connected
	 *            <br>
	 *            <code>true</code>: text for the connected state.<br>
	 *            <code>false</code>: text for the disconnected
	 *            state.
	 */
	public void setConnected(final boolean connected) {
		SWTUtil.asyncExec(new Runnable( ) {

			@Override
			public void run() {
				encapsConnected(connected);
			}

		});

	}

	/**
	 * Sets the connected status text.
	 * 
	 * @param connected
	 *            <br>
	 *            <code>true</code>: The bot is connected.<br>
	 *            <code>false</code>: The bot is not connected.
	 */
	private void encapsConnected(boolean connected) {
		if (SWTUtil.swtAssert(this)) {
			if (connected) {
				hint.setTextForLocaleKey("StatusBar.gui.operateTheRobot"); //$NON-NLS-1$
				connStatus.setTextForLocaleKey("StatusBar.gui.connectedTo", currentRobot.getName( )); //$NON-NLS-1$
			}
			else {
				hint.setTextForLocaleKey("StatusBar.gui.toOperateCreateConnection", currentRobot.getName( )); //$NON-NLS-1$		
				connStatus.setTextForLocaleKey("StatusBar.gui.notConnected"); //$NON-NLS-1$
			}
			layout( );
		}
	}

	/**
	 * Sets the power source info value.
	 * 
	 * @param number
	 *            The value.
	 * @param unit
	 *            The uni text.
	 */
	private void setPowerSourceValue(Number number, String unit) {
		if (SWTUtil.swtAssert(voltage)) {
			voltage.setText(trimNumber(number) + " " + unit); //$NON-NLS-1$
			voltage.pack( );
		}
		if (currentPowerSource != null) {
			if (number.doubleValue( ) <= currentPowerSource.getWarningValue( )) {
				boolean warned = false;
				if (currentWarnedMap.containsKey(currentRobot.getName( ))) {
					warned = currentWarnedMap.get(currentRobot.getName( ));
				}
				if (!warned) {
					currentWarnedMap.put(currentRobot.getName( ), Boolean.TRUE);

					MessageManager.showToolTip(I18n.getString("ApplicationStatusBar.tipmessage.title"), //$NON-NLS-1$
							I18n.getString("ApplicationStatusBar.tipmessage.message", //$NON-NLS-1$
									currentPowerSource.getWarningValue( ),
									currentPowerSource.getUnit( ).unitName( )),
							MessageType.WARN);
				}
			}
		}
	}

	/**
	 * Trims the number to a formatted text.
	 * 
	 * @param number
	 *            The value.
	 * @return The formatted value as string.
	 */
	private String trimNumber(Number number) {
		return I18n.getDefaultDecimalFormat( ).format(number.doubleValue( ));
	}

	/**
	 * Sets the status bar visible.
	 * 
	 * @param b
	 *            <br>
	 *            <code>true</code>: bar is visible.<br>
	 *            <code>false</code>: bar is not visible.
	 */
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		ApplicationContent c = Application.getApplication( ).getAppContent( );
		if (c != null) {
			c.setStatusVisible(b);
		}
	}

	/**
	 * Updates the send/received info text.s
	 */
	private void updateSentReceived() {
		if (currentRobot != null) {
			DataAmount bytesReceived = CommunicationManager.getBytesReceived(currentRobot.getName( ));
			if (bytesReceived != null) {
				recv = bytesReceived.toString( );
			}
			DataAmount bytesSend = CommunicationManager.getBytesSend(currentRobot.getName( ));
			if (bytesSend != null) {
				sent = bytesSend.toString( );
			}
		}
		setInOut( );
	}
}
