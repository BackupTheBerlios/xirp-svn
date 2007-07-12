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
 * DoublePoint.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 29.04.2006:		Created by Rabea Gransberger.
 */
package de.xirp.util;

import java.awt.geom.Point2D;

/**
 * Class proving methods for handling points of datatype double
 * 
 * @author Rabea Gransberger
 */
public class DoublePoint extends Point2D.Double {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5560959071769593131L;

	/**
	 * Constructs a new object for calculations with double points
	 * 
	 * @param x
	 *            the x coordinate of the point
	 * @param y
	 *            the y coordinate of the point
	 */
	public DoublePoint(final double x, final double y) {
		super(x, y);
	}

	/**
	 * Copies the given point
	 * 
	 * @param other
	 *            the point to copy
	 */
	public DoublePoint(DoublePoint other) {
		super(other.x, other.y);
	}

	/**
	 * Multiplies this point component wise with the other point and
	 * returns the result. This point remains as is.
	 * 
	 * @param point
	 *            the point to multiplicate this point
	 * @return the point resulting from the multiplication
	 */
	public DoublePoint multiply(final DoublePoint point) {
		return new DoublePoint(x * point.x, y * point.y);
	}

	/**
	 * Divides this point component wise with the other point and
	 * returns the result. This point remains as is.
	 * 
	 * @param point
	 *            the point to divide this point with
	 * @return the point resulting from the division
	 */
	public DoublePoint divide(final DoublePoint point) {
		return new DoublePoint(x / point.x, y / point.y);
	}

	/**
	 * Divides this point component wise with the given factor and
	 * returns the result. This point remains as is.
	 * 
	 * @param factor
	 *            the factor to divide this point with
	 * @return the point resulting from the division
	 */
	public DoublePoint divide(final double factor) {
		return new DoublePoint(x / factor, y / factor);
	}

	/**
	 * Adds the given point component wise to this point and returns
	 * the result. This point remains as is.
	 * 
	 * @param point
	 *            the other to point to add to this point
	 * @return the point resulting from the calculation
	 */
	public DoublePoint add(final DoublePoint point) {
		return new DoublePoint(x + point.x, y + point.y);
	}

	/**
	 * Calculates the maximum of the two doubles of this object
	 * 
	 * @return the maximum double of this object
	 */
	public double max() {
		return Math.max(x, y);
	}

	/**
	 * Calculates the minimum of the two doubles of this object
	 * 
	 * @return the minimum double of this object
	 */
	public double min() {
		return Math.min(x, y);
	}

}
