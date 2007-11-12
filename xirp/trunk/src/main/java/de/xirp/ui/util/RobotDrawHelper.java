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
 * RobotDrawHelper.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 07.05.2007:		Created by Rabea Gransberger.
 */
package de.xirp.ui.util;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import de.xirp.profile.Length;
import de.xirp.profile.Position;
import de.xirp.profile.Robot;
import de.xirp.profile.RobotSpecs;
import de.xirp.profile.Sensor;
import de.xirp.profile.Width;
import de.xirp.profile.Position.Side;
import de.xirp.profile.Units.DistanceUnit;

/**
 * This class provides support for drawing the robot and it's sensors
 * in top-down view by calculating the positions if the sensor.
 * 
 * @author Rabea Gransberger
 */
public class RobotDrawHelper {

	/**
	 * The spacing at the boundaries of the size
	 */
	private int spacing;
	/**
	 * The robot which should be drawn
	 */
	private Robot robot;
	/**
	 * The original width of the robot in centimeter
	 */
	private double robotWidth;
	/**
	 * The original width of the robot in centimeter
	 */
	private double robotLength;
	/**
	 * The ratio of {@link #robotLength} and {@link #robotWidth}
	 */
	private double ratio;
	/**
	 * The rectangle of the robot ready to be drawn
	 */
	private Rectangle robotDrawRectangle;
	/**
	 * The center of the robots rectangle
	 */
	private Point center;
	/**
	 * With of the rectangle divided by the robots width
	 */
	private double pixelPerWidth;
	/**
	 * Length of the rectangle divided by the robots length
	 */
	private double pixelPerLength;
	/**
	 * Size which may be used for drawing
	 */
	private Point useableSize;

	/**
	 * Constructs a new robot draw helper which allows to draw the
	 * given robot and it's sensors.
	 * 
	 * @param spacing
	 *            the spacing for the left/right and top/bottom. The
	 *            spacing is subtracted from the size and results in
	 *            the usable size for drawing.
	 * @param robot
	 *            the robot which should be drawn
	 */
	public RobotDrawHelper(final int spacing, Robot robot) {
		this.spacing = spacing;
		this.robot = robot;
		init( );
	}

	/**
	 * Reads the basics from the robot and does some calculations.
	 */
	private void init() {
		RobotSpecs robotSpecs = robot.getRobotSpecs( );
		Length length = robotSpecs.getLength( );
		Width width = robotSpecs.getWidth( );

		robotWidth = toMillimeter(width.getWidth( ), width.getUnit( ));
		robotLength = toMillimeter(length.getLength( ), length.getUnit( ));

		ratio = robotLength / robotWidth;
	}

	/**
	 * Converts a value in the given unit to a value in
	 * {@link DistanceUnit#MILLIMETER}.
	 * 
	 * @param value
	 *            the value
	 * @param from
	 *            the unit of the value
	 * @return the value in millimeters.
	 */
	private double toMillimeter(double value, DistanceUnit from) {
		return DistanceUnit.convert(value, from, DistanceUnit.MILLIMETER);
	}

	/**
	 * This method should be called if the size of the area on which
	 * the robot should be drawn has changed. This results in a
	 * recalculation of the robots rectangle. You have to call
	 * {@link #getSensorLocation(Sensor)} for each sensor your would
	 * like to draw afterwards.
	 * 
	 * @param size
	 *            the new available size for drawing
	 */
	public void resized(final Point size) {
		Point sizeCopy = new Point(size.x, size.y);

		center = new Point(sizeCopy.x / 2, sizeCopy.y / 2);

		useableSize = new Point(sizeCopy.x - spacing, sizeCopy.y - spacing);

		sizeCopy.x -= spacing;
		sizeCopy.y -= spacing;

		int width = (int) Math.min(sizeCopy.x, sizeCopy.y / ratio);
		int height = (int) (width * ratio);

		Point start = new Point(center.x - width / 2, center.y - height / 2);
		robotDrawRectangle = new Rectangle(start.x, start.y, width, height);
		pixelPerWidth = width / robotWidth;
		pixelPerLength = height / robotLength;
	}

	/**
	 * Gets the size which is used for drawing. This is the overall
	 * size minus the spacing.
	 * 
	 * @return the usable size
	 */
	public Point getUsableSize() {
		return useableSize;
	}

	/**
	 * Gets the location at which the given sensor should be drawn on
	 * the underlying drawing area.
	 * 
	 * @param sensor
	 *            the sensor which should be drawn
	 * @return the location at which the sensor should be drawn
	 */
	@SuppressWarnings("incomplete-switch")
	public Point getSensorLocation(Sensor sensor) {
		Position position = sensor.getSpecs( ).getPosition( );
		Point sensorOrigin = center;
		Point sensorPosition = center;
		Side side = position.getSide( );
		if (side != null) {
			int x = (int) (pixelPerWidth * position.getX( ));
			int y = (int) (pixelPerLength * position.getY( ));

			switch (side) {
				case FRONT:
					sensorOrigin = new Point(robotDrawRectangle.x +
							robotDrawRectangle.width, robotDrawRectangle.y);
					sensorPosition = new Point(sensorOrigin.x - x,
							sensorOrigin.y);
					break;
				case LEFT:
					sensorOrigin = new Point(robotDrawRectangle.x,
							robotDrawRectangle.y);
					sensorPosition = new Point(sensorOrigin.x, sensorOrigin.y +
							x);
					break;
				case RIGHT:
					sensorOrigin = new Point(robotDrawRectangle.x +
							robotDrawRectangle.width, robotDrawRectangle.y +
							robotDrawRectangle.height);
					sensorPosition = new Point(sensorOrigin.x, sensorOrigin.y -
							x);
					break;
				case REAR:
					sensorOrigin = new Point(robotDrawRectangle.x,
							robotDrawRectangle.y + robotDrawRectangle.height);
					sensorPosition = new Point(sensorOrigin.x + x,
							sensorOrigin.y);
					break;
				case TOP:
				case INSIDE:
					sensorOrigin = new Point(robotDrawRectangle.x,
							robotDrawRectangle.y + robotDrawRectangle.height);
					sensorPosition = new Point(sensorOrigin.x + x,
							sensorOrigin.y - y);
			}

			return sensorPosition;
		}
		return null;
	}

	/**
	 * Gets the number of pixels for one width.
	 * 
	 * @return the pixels per width
	 */
	public double getPixelPerWidth() {
		return pixelPerWidth;
	}

	/**
	 * Gets the number of pixels for one length.
	 * 
	 * @return the pixels per length
	 */
	public double getPixelPerLength() {
		return pixelPerLength;
	}

	/**
	 * Gets the rectangle which represents the robot and could be
	 * drawn to the drawing area.
	 * 
	 * @return the robots rectangle
	 */
	public Rectangle getRobotDrawRectangle() {
		return robotDrawRectangle;
	}

	/**
	 * Gets the center of the robots rectangle.
	 * 
	 * @return the center
	 */
	public Point getCenter() {
		return center;
	}

	/**
	 * Gets the spacing which is used to be able to have boundaries in
	 * which nothing is drawn.
	 * 
	 * @return the spacing
	 */
	public int getSpacing() {
		return spacing;
	}

	/**
	 * Sets the spacing which is used to be able to have boundaries in
	 * which nothing is drawn
	 * 
	 * @param spacing
	 *            the spacing to set
	 */
	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}

	/**
	 * Gets the robot of this helper.
	 * 
	 * @return the robot
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * Gets the original width of the robot.
	 * 
	 * @return the robots width
	 */
	public double getRobotWidth() {
		return robotWidth;
	}

	/**
	 * Gets the original length of the robot.
	 * 
	 * @return the robots length
	 */
	public double getRobotLength() {
		return robotLength;
	}

	/**
	 * Gets the ratio of length to width ({@literal length/width}.
	 * 
	 * @return the ratio
	 */
	public double getRatio() {
		return ratio;
	}
}
