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
 * PointUtil.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 29.01.2006:		Created by Rabea Gransberger.
 */
package de.xirp.ui.util;

import org.eclipse.swt.graphics.Point;

/**
 * Utility class for calculations with points like add, subtract,
 * multiplicate, divide.
 * 
 * @author Rabea Gransberger
 */
public class PointUtil {

	/**
	 * Returns the maximum of the <code>Point</code>.
	 * 
	 * @param point
	 *            the Point
	 * @return point.x or point.y whichever is greater
	 */
	public static int getMax(Point point) {
		if (point.x >= point.y) {
			return point.x;
		}
		else {
			return point.y;
		}
	}

	/**
	 * Returns the maximum of the <code>Point</code>.
	 * 
	 * @param point
	 *            the Point
	 * @return point.x or point.y whichever is smaller
	 */
	public static int getMin(Point point) {
		if (point.x <= point.y) {
			return point.x;
		}
		else {
			return point.y;
		}
	}

	/**
	 * Multiplicate a point with a constant factor, that is
	 * multiplicate each coordinate with the factor.
	 * 
	 * @param point
	 *            The point
	 * @param factor
	 *            A factor
	 * @return Point multiplied with factor
	 */
	public static Point multiplicate(Point point, double factor) {
		return new Point((int) (point.x * factor), (int) (point.y * factor));
	}

	/**
	 * Multiplicate two points.
	 * 
	 * @param point
	 *            First point
	 * @param multi
	 *            Second point
	 * @return Multiplication of the points
	 */
	public static Point multiplicate(Point point, Point multi) {
		return new Point(point.x * multi.x, point.y * multi.y);
	}

	/**
	 * Divide a <code>Point</code> with a constant divisor, that is
	 * divide each<br>
	 * coordinate with the factor.<br>
	 * 
	 * @param point
	 *            The point
	 * @param divisor
	 *            A divisor
	 * @return Point divided through divisor
	 */
	public static Point divide(Point point, double divisor) {
		return new Point((int) (point.x / divisor), (int) (point.y / divisor));
	}

	/**
	 * Divide two points.<br>
	 * 
	 * @param point
	 *            First point
	 * @param divisor
	 *            Second point
	 * @return Division of the points
	 */
	public static Point divide(Point point, Point divisor) {
		return new Point(point.x / divisor.x, point.y / divisor.y);
	}

	/**
	 * Add a summand to a <code>Point</code>.<br>
	 * Adds the summand to each coordinate.<br>
	 * 
	 * @param point
	 *            A point
	 * @param summand
	 *            The number to add to point
	 * @return Sum of point and summand
	 */
	public static Point add(Point point, int summand) {
		return new Point(point.x + summand, point.y + summand);
	}

	/**
	 * Sum up two points.<br>
	 * 
	 * @param point
	 *            First point
	 * @param summand
	 *            Second point
	 * @return Sum of points
	 */
	public static Point add(Point point, Point summand) {
		return new Point(point.x + summand.x, point.y + summand.y);
	}

	/**
	 * Subtract a summand from a <code>Point</code>.<br>
	 * Subtracts the summand from each coordinate<br>
	 * 
	 * @param point
	 *            A point
	 * @param summand
	 *            The number to subtract from the point
	 * @return Subtract of point and number
	 */
	public static Point subtract(Point point, int summand) {
		return new Point(point.x - summand, point.y - summand);
	}

	/**
	 * Subtract up two points.<br>
	 * 
	 * @param point
	 *            First point
	 * @param summand
	 *            Second point
	 * @return Subtract of points
	 */
	public static Point subtract(Point point, Point summand) {
		return new Point(point.x - summand.x, point.y - summand.y);
	}

	/**
	 * Give a single number representation for the <code>Point</code>.<br>
	 * 
	 * @param point
	 *            The point
	 * @param maxX
	 *            Maximum positions
	 * @return <code>point.x + point.y * maxX</code>
	 */
	public static int toSinglePoint(Point point, int maxX) {
		return point.x + point.y * maxX;
	}

	/**
	 * Extract <code>Point</code> from single number.<br>
	 * 
	 * @param pos
	 *            The single number
	 * @param maxX
	 *            Maximum number of positions
	 * @return <code>Point(pos % maxX,pos / maxX)</code>
	 */
	public static Point getPoint(int pos, int maxX) {
		int y = pos / maxX;
		int x = pos % maxX;
		return new Point(x, y);
	}
}
