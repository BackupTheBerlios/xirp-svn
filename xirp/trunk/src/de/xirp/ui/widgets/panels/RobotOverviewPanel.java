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
 * RobotOverviewPanel.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * 					 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 12.02.2007:		Created by Matthias Gernand.
 * 07.05.2007:		Implemented first working version.
 */
package de.xirp.ui.widgets.panels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.xirp.io.comm.CommunicationManager;
import de.xirp.io.comm.data.Datapool;
import de.xirp.io.comm.data.DatapoolUtil;
import de.xirp.io.event.ConnectionEvent;
import de.xirp.io.event.ConnectionListener;
import de.xirp.profile.*;
import de.xirp.profile.Units.SensorValueUnit;
import de.xirp.ui.event.LocaleChangedEvent;
import de.xirp.ui.event.LocaleChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.RobotDrawHelper;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * Panel which shows a top-down view of the robot with all it's
 * sensors as listed in the profile. More information is shown on
 * mouse over in a tool tip. If there's a connection to the robot the
 * current values of the sensors are also displayed. {@link Number}
 * values are formatted as doubles with 2 decimals all other values
 * are displayed using the <code>toString</code> method.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class RobotOverviewPanel extends XComposite {

	/**
	 * String used for highlighting a value in the plain text.
	 */
	private static final String HIGHLIGHT = " ---- "; //$NON-NLS-1$

	/**
	 * Key for the data of a widget under which the datapool key of
	 * the sensor is stored.
	 */
	private static final String DATA_DATAPOOL_KEY = "datapoolKey"; //$NON-NLS-1$

	/**
	 * Key for the data of a widget under which the name of the sensor
	 * is stored.
	 */
	private static final String DATA_SENSOR_NAME = "sensorName"; //$NON-NLS-1$

	/**
	 * Key for the data of a widget under which the
	 * {@link Sensor sensor} object of the sensor is stored.
	 */
	private static final String DATA_SENSOR = "sensor"; //$NON-NLS-1$

	/**
	 * Key for the data of a widget under which the current tooltip
	 * text for the widget is stored.
	 */
	private static final String DATA_TOOLTIP = "tooltip"; //$NON-NLS-1$

	/**
	 * Free border of the panel in which nothing is painted.
	 */
	private static final int SPACING = 10;
	/**
	 * Additional space needed for paint a sensor.
	 */
	private static final int SENSOR_SPACING = 30;
	/**
	 * The log4j logger of this class.
	 */
	private static final Logger logClass = Logger.getLogger(RobotOverviewPanel.class);
	/**
	 * The robot for this overview.
	 */
	private Robot robot;
	/**
	 * A canvas for the body of the robot.
	 */
	private Canvas body;
	/**
	 * Canvasses for the sensors of the robot.
	 */
	private List<Canvas> canvasses = new ArrayList<Canvas>( );
	/**
	 * The draw helper util object used for drawing the sensors at the
	 * correct position
	 */
	private RobotDrawHelper drawHelper;
	/**
	 * The datapool used for getting the current values for the
	 * sensors if the robot is connected
	 */
	private Datapool datapool;
	/**
	 * <code>true</code> if the robot is connected
	 */
	private boolean connected = false;

	/**
	 * Creates a new robot overview panel for the given robot and
	 * reads all needed information from the profile.
	 * 
	 * @param parent
	 *            The parent composite on which the elements could be
	 *            layed out.
	 * @param robotName
	 *            the name of the robot for which this panel is for.
	 */
	public RobotOverviewPanel(Composite parent, String robotName) {
		super(parent, SWT.NONE);
		try {
			this.robot = ProfileManager.getRobot(robotName);
		}
		catch (RobotNotFoundException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		init( );
	}

	/**
	 * Initializes locale changed and connection listener and reads
	 * the sensor data from the profile and creates the canvasses.
	 */
	private void init() {
		if (robot != null) {
			initListeners( );
			drawHelper = new RobotDrawHelper(SPACING + SENSOR_SPACING, robot);

			body = new Canvas(this, SWT.NONE);
			body.setBackground(this.getDisplay( )
					.getSystemColor(SWT.COLOR_BLACK));

			List<Integer> colors = new ArrayList<Integer>( );
			colors.add(SWT.COLOR_RED);
			colors.add(SWT.COLOR_DARK_RED);
			colors.add(SWT.COLOR_GREEN);
			colors.add(SWT.COLOR_DARK_GREEN);
			colors.add(SWT.COLOR_YELLOW);
			colors.add(SWT.COLOR_DARK_YELLOW);
			colors.add(SWT.COLOR_BLUE);
			colors.add(SWT.COLOR_DARK_BLUE);
			colors.add(SWT.COLOR_MAGENTA);
			colors.add(SWT.COLOR_DARK_MAGENTA);
			colors.add(SWT.COLOR_CYAN);
			colors.add(SWT.COLOR_DARK_CYAN);

			// read the sensors from the profile
			int pos = 0;
			for (Sensorgroup group : robot.getSensorgroups( )) {
				for (Sensor sensor : group.getSensors( )) {
					String datapoolKey = DatapoolUtil.createDatapoolKey(group,
							sensor);

					// Create the canvas and set the data
					final Canvas sensorCanvas = new Canvas(this, SWT.NONE);

					sensorCanvas.setData(DATA_SENSOR, sensor);
					sensorCanvas.setData(DATA_SENSOR_NAME, group.getLongName( ));
					sensorCanvas.setData(DATA_DATAPOOL_KEY, datapoolKey);

					sensorCanvas.setBackground(ColorManager.getColor(colors.get(pos)));
					sensorCanvas.setSize(20, 20);
					sensorCanvas.moveAbove(body);
					canvasses.add(sensorCanvas);
					sensorCanvas.addMouseTrackListener(new MouseTrackAdapter( ) {

						@Override
						public void mouseEnter(@SuppressWarnings("unused")
						MouseEvent event) {
							// if there's a connection to the robot,
							// read the current value from the
							// datapool
							// and update the tool tip text.
							if (connected) {
								String tooltip = (String) sensorCanvas.getData(DATA_TOOLTIP);

								String datapoolKey = (String) sensorCanvas.getData(DATA_DATAPOOL_KEY);
								if (datapool != null) {
									Object value = datapool.getValue(datapoolKey);
									if (value != null) {

										tooltip += Constants.LINE_SEPARATOR +
												I18n.getString("RobotOverviewPanel.tooltip.value"); //$NON-NLS-1$

										if (value instanceof Number) {
											Number n = (Number) value;
											tooltip += I18n.getDefaultDecimalFormat( )
													.format(n.doubleValue( ));
										}
										else {
											tooltip += value.toString( );
										}
									}
								}
								sensorCanvas.setToolTipText(tooltip);
							}
						}

					});

				}
				pos++;
				if (pos > colors.size( )) {
					pos = 0;
				}

			}

			translate( );
		}

	}

	/**
	 * Initializes the resized ({@link #resized()}), locale changed ({@link #translate()})
	 * and connection listener.
	 */
	private void initListeners() {
		this.addControlListener(new ControlAdapter( ) {

			@Override
			public void controlResized(@SuppressWarnings("unused")
			ControlEvent event) {
				resized( );
			}

		});

		ApplicationManager.addLocaleChangedListener(new LocaleChangedListener( ) {

			@Override
			public void localeChanged(@SuppressWarnings("unused")
			LocaleChangedEvent event) {
				translate( );
			}

		});

		CommunicationManager.addConnectionListener(new ConnectionListener( ) {

			@Override
			public void connectionEstablished(ConnectionEvent event) {
				if (event.getRobotName( ).equals(robot.getName( ))) {
					connected = true;
				}

			}

			@Override
			public void disconnected(ConnectionEvent event) {
				if (event.getRobotName( ).equals(robot.getName( ))) {
					connected = false;
				}
			}

		});

	}

	/**
	 * Translates the tool tip of the given sensor canvas for the
	 * current language and updates the data at the widget
	 * accordingly.
	 * 
	 * @param sensorCanvas
	 *            the sensor canvas
	 */
	private void translateSensor(Canvas sensorCanvas) {
		Sensor sensor = (Sensor) sensorCanvas.getData(DATA_SENSOR);
		String name = (String) sensorCanvas.getData(DATA_SENSOR_NAME);
		String datapoolKey = (String) sensorCanvas.getData(DATA_DATAPOOL_KEY);

		SensorSpecs specs = sensor.getSpecs( );
		StringBuilder builder = new StringBuilder( );
		builder.append(HIGHLIGHT)
				.append(name)
				.append(HIGHLIGHT)
				.append(Constants.LINE_SEPARATOR);
		SensorValueUnit unit = sensor.getUnit( );
		builder.append(I18n.getString("RobotOverviewPanel.tooltip.range")) //$NON-NLS-1$
				.append(specs.getMinimum( ).getMinValue( ))
				.append(" - ") //$NON-NLS-1$
				.append(specs.getMaximum( ).getMaxValue( ))
				.append(" ") //$NON-NLS-1$
				.append(unit != null ? unit.unitName( ) : "?") //$NON-NLS-1$
				.append(Constants.LINE_SEPARATOR);
		builder.append(I18n.getString("RobotOverviewPanel.tooltip.id")) //$NON-NLS-1$
				.append(sensor.getId( ))
				.append(Constants.LINE_SEPARATOR);
		builder.append(I18n.getString("RobotOverviewPanel.tooltip.datapool")).append(datapoolKey); //$NON-NLS-1$
		List<Option> options = specs.getOptions( );
		if (!options.isEmpty( )) {
			builder.append(Constants.LINE_SEPARATOR)
					.append(Constants.LINE_SEPARATOR);
		}

		for (Iterator<Option> it = options.iterator( ); it.hasNext( );) {
			Option option = it.next( );
			builder.append(option.getName( )).append(" = ") //$NON-NLS-1$
					.append(option.getValue( ));
			if (it.hasNext( )) {
				builder.append(Constants.LINE_SEPARATOR);
			}
		}
		String tooltip = builder.toString( );
		sensorCanvas.setData(DATA_TOOLTIP, tooltip);
		sensorCanvas.setToolTipText(tooltip);
	}

	/**
	 * Translates everything in this overview for the current
	 * language.
	 */
	private void translate() {
		if (robot != null) {
			RobotSpecs robotSpecs = robot.getRobotSpecs( );
			Length length = robotSpecs.getLength( );
			Width width = robotSpecs.getWidth( );
			Height height = robotSpecs.getHeight( );
			Weight weight = robotSpecs.getWeight( );

			StringBuilder buf = new StringBuilder( );
			buf.append(HIGHLIGHT)
					.append(robot.getName( ))
					.append(HIGHLIGHT)
					.append(Constants.LINE_SEPARATOR);

			buf.append(I18n.getString("RobotOverviewPanel.tooltip.width")) //$NON-NLS-1$
					.append(width.getWidth( ))
					.append(" ") //$NON-NLS-1$
					.append(width.getUnit( ).unitName( ))
					.append(Constants.LINE_SEPARATOR);
			buf.append(I18n.getString("RobotOverviewPanel.tooltip.length")) //$NON-NLS-1$
					.append(length.getLength( ))
					.append(" ") //$NON-NLS-1$
					.append(length.getUnit( ).unitName( ))
					.append(Constants.LINE_SEPARATOR);
			buf.append(I18n.getString("RobotOverviewPanel.tooltip.height")) //$NON-NLS-1$
					.append(height.getHeight( ))
					.append(" ") //$NON-NLS-1$
					.append(height.getUnit( ).unitName( ))
					.append(Constants.LINE_SEPARATOR);
			buf.append(I18n.getString("RobotOverviewPanel.tooltip.weight")) //$NON-NLS-1$
					.append(weight.getWeight( ))
					.append(" ") //$NON-NLS-1$
					.append(weight.getUnit( ).unitName( ));

			body.setToolTipText(buf.toString( ));

			for (Canvas sensorCanvas : canvasses) {
				translateSensor(sensorCanvas);
			}
		}
	}

	/**
	 * Recalculates the positions of all canvasses for the new size.
	 */
	protected void resized() {
		Point size = getSize( );

		drawHelper.resized(size);

		Rectangle robotRectangle = drawHelper.getRobotDrawRectangle( );
		body.setSize(robotRectangle.width, robotRectangle.height);
		body.setLocation(robotRectangle.x, robotRectangle.y);

		for (Canvas sensorCanvas : canvasses) {
			Sensor sensor = (Sensor) sensorCanvas.getData(DATA_SENSOR);

			Point sensorPosition = drawHelper.getSensorLocation(sensor);

			double scale = Math.max(drawHelper.getPixelPerLength( ),
					drawHelper.getPixelPerWidth( ));

			int newSize = (int) Math.rint(40 * scale);
			sensorCanvas.setSize(newSize, newSize);
			Point sensorSize = sensorCanvas.getSize( );
			if (sensorPosition != null) {
				sensorPosition.x -= sensorSize.x / 2;
				sensorPosition.y -= sensorSize.y / 2;

				sensorCanvas.setLocation(sensorPosition);
			}
		}
	}

}
