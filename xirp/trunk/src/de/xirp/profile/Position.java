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
 * Position.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 15.02.2006:		Created by Matthias Gernand.
 * 15.02.2007:		Added annotations for parsing with JAXB.
 */
package de.xirp.profile;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import de.xirp.profile.Units.DistanceUnit;

/**
 * This class represents the position of a sensor on the robot. <br>
 * <br>
 * This class is annotated with annotations for mapping Java beans to
 * XML. Xirp uses JAXB to map the profile/robot/comm-spec XML files to
 * the corresponding Java beans and vice versa.
 * 
 * @author Matthias Gernand
 * @see javax.xml.bind.annotation
 * @see de.xirp.profile.Sensor
 * @see de.xirp.profile.Units.DistanceUnit
 */
@XmlType
public final class Position implements Serializable {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	@XmlTransient
	private static final long serialVersionUID = 8475328219705343580L;

	/**
	 * Enumeration that indicates the position where the sensor is
	 * attached to. There are two possibilities of attachment: <br>
	 * <br>
	 * <ul>
	 * <li>TORSO</li>
	 * <li>EXTREMITY</li>
	 * </ul>
	 * 
	 * @author Matthias Gernand
	 */
	@XmlEnum(String.class)
	public static enum Attached {
		/**
		 * Indicates that something is attached to the torso.
		 */
		TORSO,
		/**
		 * Indicates that something is attached to the extremities.
		 */
		EXTREMITY,
		/**
		 * An undefined attached value.
		 */
		UNDEFINED
	}

	/**
	 * Enumeration that indicates the side where the sensor is
	 * attached to. There are seven possible sides: <br>
	 * <br>
	 * <ul>
	 * <li>FRONT</li>
	 * <li>REAR</li>
	 * <li>LEFT</li>
	 * <li>RIGHT</li>
	 * <li>INSIDE</li>
	 * <li>TOP</li>
	 * <li>BOTTOM</li>
	 * </ul>
	 * 
	 * @author Matthias Gernand
	 */
	@XmlEnum(String.class)
	public static enum Side {
		/**
		 * Sensor attached to the front of the robot.
		 */
		FRONT,
		/**
		 * Sensor attached to the rear of the robot.
		 */
		REAR,
		/**
		 * Sensor attached to the left of the robot.
		 */
		LEFT,
		/**
		 * Sensor attached to the right of the robot.
		 */
		RIGHT,
		/**
		 * Sensor is located inside the robot.
		 */
		INSIDE,
		/**
		 * Sensor attached at the top of the robot.
		 */
		TOP,
		/**
		 * Sensor attached at the bottom of the robot.
		 */
		BOTTOM,
		/**
		 * An undefined attachment side.
		 */
		UNDEFINED
	}

	/**
	 * Attached to torso or extremity.
	 */
	@XmlAttribute(name = "attached", required = true)
	private Attached attached = null;
	/**
	 * The side where the sensors is attached to.
	 */
	@XmlAttribute(name = "side", required = true)
	private Side side = null;
	/**
	 * Indent on the x axis, the unit is defined in the field
	 * {@link de.xirp.profile.Position#unit unit}. <br>
	 * <br>
	 * The point of origin is always the left bottom corner of a area.
	 * The schema below shown f.e. the left side of a robot. <br>
	 * <br>
	 * ____________________________________<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |:::::::::::::::::::::::::::: left side of the robot
	 * :::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |Point of origin
	 * [(0,0)]:::::::::::::::::::::::::::::::::::::::::::::::::::|<br> |<--:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * ------------------------------------------------<br>
	 * <br>
	 * <br>
	 * Just to make "left bottom corner" better understandable.
	 * 
	 * @see de.xirp.profile.Units.DistanceUnit
	 */
	@XmlAttribute(name = "x", required = true)
	private int x = -1;
	/**
	 * Indent on the y axis, the unit is defined in the field
	 * {@link de.xirp.profile.Position#unit unit}. <br>
	 * <br>
	 * The point of origin is always the left bottom corner of a area.
	 * The schema below shown f.e. the left side of a robot. <br>
	 * <br>
	 * ____________________________________<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |:::::::::::::::::::::::::::: left side of the robot
	 * :::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |Point of origin
	 * [(0,0)]:::::::::::::::::::::::::::::::::::::::::::::::::::|<br> |<--:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * ------------------------------------------------<br>
	 * <br>
	 * <br>
	 * Just to make "left bottom corner" better understandable.
	 * 
	 * @see de.xirp.profile.Units.DistanceUnit
	 */
	@XmlAttribute(name = "y", required = true)
	private int y = -1;
	/**
	 * The unit of the distance values is always
	 * {@link de.xirp.profile.Units.DistanceUnit#MILLIMETER millimeters}.
	 * <br>
	 * <br>
	 * The point of origin is always the left bottom corner of a area.
	 * The schema below shown f.e. the left side of a robot. <br>
	 * <br>
	 * ____________________________________<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |:::::::::::::::::::::::::::: left side of the robot
	 * :::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * |Point of origin
	 * [(0,0)]:::::::::::::::::::::::::::::::::::::::::::::::::::|<br> |<--:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|<br>
	 * ------------------------------------------------<br>
	 * <br>
	 * <br>
	 * Just to make "left bottom corner" better understandable.
	 * 
	 * @see de.xirp.profile.Units.DistanceUnit
	 */
	@XmlTransient
	private final DistanceUnit unit = Units.DistanceUnit.MILLIMETER;

	/**
	 * Returns the
	 * {@link de.xirp.profile.Position.Attached attachment}
	 * area.
	 * 
	 * @return The attachment area.
	 */
	@XmlTransient
	public Attached getAttached() {
		return attached;
	}

	/**
	 * Sets the
	 * {@link de.xirp.profile.Position.Attached attachment}
	 * area.
	 * 
	 * @param attached
	 *            The attachment area to set.
	 */
	public void setAttached(Attached attached) {
		this.attached = attached;
	}

	/**
	 * Returns the attachment
	 * {@link de.xirp.profile.Position.Side side}.
	 * 
	 * @return The attachment side.
	 */
	@XmlTransient
	public Side getSide() {
		return side;
	}

	/**
	 * Sets the attachment
	 * {@link de.xirp.profile.Position.Side side}.
	 * 
	 * @param side
	 *            The attachment side to set.
	 */
	public void setSide(Side side) {
		this.side = side;
	}

	/**
	 * Returns the x indent in
	 * {@link de.xirp.profile.Units.DistanceUnit#MILLIMETER millimeters}.
	 * 
	 * @return The x indent.
	 */
	@XmlTransient
	public int getX() {
		return x;
	}

	/**
	 * Sets the x indent in
	 * {@link de.xirp.profile.Units.DistanceUnit#MILLIMETER millimeters}.
	 * 
	 * @param x
	 *            The x indent to set.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Returns the y indent in
	 * {@link de.xirp.profile.Units.DistanceUnit#MILLIMETER millimeters}.
	 * 
	 * @return The y indent.
	 */
	@XmlTransient
	public int getY() {
		return y;
	}

	/**
	 * Sets the y indent in
	 * {@link de.xirp.profile.Units.DistanceUnit#MILLIMETER millimeters}.
	 * 
	 * @param y
	 *            The y indent to set.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Returns the unit of the position values.
	 * 
	 * @return The unit.
	 */
	@XmlTransient
	public DistanceUnit getUnit() {
		return unit;
	}
}
