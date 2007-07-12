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
 * PastMouseEvent.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 20.09.2006:		Created by Rabea Gransberger.
 */
package de.xirp.util.mouse;

import java.io.Serializable;

import org.eclipse.swt.graphics.Point;

import de.xirp.util.FutureRelease;

/**
 * Past event of the mouse in the UI which may be serialized.<br>
 * <br>
 * This is alpha API, may be removed in the future or is planed to be
 * integrated in version 3.0.0.
 * 
 * @author Rabea Gransberger
 */
@FutureRelease(version = "3.0.0")
public class PastMouseEvent implements Serializable {

	/**
	 * Unique ID for serialization
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The relative time at which the event occurred
	 */
	private long time;
	/**
	 * Coordinates of the mouse on the screen at the time of the event
	 */
	private Point coordinates;
	/**
	 * Button of the mouse which was pressed
	 */
	private int button;
	/**
	 * <code>true</code> if the mouse was only moved to the
	 * coordinates
	 */
	private boolean moveOnly = false;

	/**
	 * Creates a new object holding data of a mouse move event.
	 * 
	 * @param time
	 *            The relative time at which the event occurred
	 * @param coordinates
	 *            Coordinates of the mouse on the screen at the time
	 *            of the event
	 */
	public PastMouseEvent(long time, Point coordinates) {
		this.time = time;
		this.coordinates = coordinates;
		this.moveOnly = true;

	}

	/**
	 * Creates a new object holding data of a mouse click event.
	 * 
	 * @param time
	 *            The relative time at which the event occurred
	 * @param coordinates
	 *            Coordinates of the mouse on the screen at the time
	 *            of the event
	 * @param button
	 *            Button of the mouse which was pressed
	 */
	public PastMouseEvent(long time, Point coordinates, int button) {
		this.time = time;
		this.coordinates = coordinates;
		this.button = button;
		this.moveOnly = false;
	}

	/**
	 * Gets the relative time at which the event occurred.
	 * 
	 * @return the relative time of the event
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Gets the coordinates of the mouse on the screen at the time of
	 * the event.
	 * 
	 * @return the coordinates
	 */
	public Point getCoordinates() {
		return coordinates;
	}

	/**
	 * Gets the button of the mouse which was pressed. This is only
	 * available for mouse click events.
	 * 
	 * @return the button
	 * @see #isMoveOnly()
	 */
	public int getButton() {
		return button;
	}

	/**
	 * Checks if the mouse was clicked or moved.
	 * 
	 * @return <code>true</code> if the mouse was only moved but not
	 *         clicked.
	 */
	public boolean isMoveOnly() {
		return moveOnly;
	}

}
