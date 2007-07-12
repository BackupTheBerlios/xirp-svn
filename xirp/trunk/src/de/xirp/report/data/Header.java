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
 * Header.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 17.04.2006:		Created by Matthias Gernand.
 */
package de.xirp.report.data;

import java.util.Date;

import de.xirp.plugin.IPlugable;
import de.xirp.profile.Robot;

/**
 * This class represents the header of a
 * {@link de.xirp.report.Report report}.
 * 
 * @author Matthias Gernand
 * @see de.xirp.report.Report
 */
public final class Header {

	/**
	 * The title of the
	 * {@link de.xirp.report.Report report}.
	 * 
	 * @see de.xirp.report.Report
	 */
	private String title = null;
	/**
	 * The {@link de.xirp.profile.Robot robot} for which
	 * the {@link de.xirp.report.Report report} was
	 * generated.
	 * 
	 * @see de.xirp.report.Report
	 * @see de.xirp.profile.Robot
	 */
	private Robot robot = null;
	/**
	 * The {@link de.xirp.plugin.IPlugable plugin} which
	 * generated the {@link de.xirp.report.Report report}.
	 * 
	 * @see de.xirp.report.Report
	 * @see de.xirp.plugin.IPlugable
	 */
	private IPlugable plugin = null;
	/**
	 * The creation {@link java.util.Date} of the
	 * {@link de.xirp.report.Report report}.
	 * 
	 * @see de.xirp.report.Report
	 */
	private Date date = null;

	/**
	 * The default constructor is declared private. Use the
	 * {@link de.xirp.report.data.Header#Header(String, Robot)}
	 * or
	 * {@link de.xirp.report.data.Header#Header(String, Robot, IPlugable)}
	 * constructors instead.
	 * 
	 * @see de.xirp.report.data.Header#Header(String,
	 *      Robot)
	 * @see de.xirp.report.data.Header#Header(String,
	 *      Robot, IPlugable)
	 */
	private Header() {

	}

	/**
	 * Constructs a new header using the given title,
	 * {@link de.xirp.profile.Robot robot} and
	 * {@link de.xirp.plugin.IPlugable plugin}. The
	 * creation date is the moment this constructor is called.
	 * 
	 * @param title
	 *            The title of the report.
	 * @param robot
	 *            The robot the report is generated for.
	 * @param plugin
	 *            The plugin which generated the report.
	 * @see de.xirp.profile.Robot
	 * @see de.xirp.plugin.IPlugable
	 */
	public Header(String title, Robot robot, IPlugable plugin) {
		this.title = title;
		this.robot = robot;
		this.plugin = plugin;
		date = new Date(System.currentTimeMillis( ));
	}

	/**
	 * Constructs a new header using the given title,
	 * {@link de.xirp.profile.Robot robot}. The creation
	 * date is the moment this constructor is called.
	 * 
	 * @param title
	 *            The title of the report.
	 * @param robot
	 *            The robot the report is generated for.
	 * @see de.xirp.profile.Robot
	 */
	public Header(String title, Robot robot) {
		this.title = title;
		this.robot = robot;
		date = new Date(System.currentTimeMillis( ));
	}

	/**
	 * Returns the creation {@link java.util.Date}.
	 * 
	 * @return The creation date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the
	 * {@link de.xirp.plugin.IPlugable plugin} which
	 * generates the report.
	 * 
	 * @return The plugin.
	 * @see de.xirp.plugin.IPlugable
	 */
	public IPlugable getPlugin() {
		return plugin;
	}

	/**
	 * Returns the {@link de.xirp.profile.Robot robot}
	 * the report is generated for.
	 * 
	 * @return The robot.
	 * @see de.xirp.profile.Robot
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * Returns the title of the report.
	 * 
	 * @return The title.
	 */
	public String getTitle() {
		return title;
	}
}
