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
 * TesterBotCompass.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.testerbot;

/**
 * Creator for angle values for a compass.
 * 
 * @author Matthias Gernand
 */
public class TesterBotCompass extends SinusCurve {

	/**
	 * The minimum and maximum value for the compass.
	 */
	private static final double MAX_COMPASS_VALUE = 180;

	/**
	 * Creates a new creator which creates angle values for a compass
	 * following a sinus curve and ranging from -{@value #MAX_COMPASS_VALUE}
	 * to {@value #MAX_COMPASS_VALUE}.
	 * 
	 * @param runTime
	 *            the total time for the creator.
	 */
	public TesterBotCompass(long runTime) {
		super(runTime, -MAX_COMPASS_VALUE, MAX_COMPASS_VALUE);
	}
}
