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
 * TesterBotSpeed.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 19.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.testerbot;

/**
 * Creator for speed values.
 * 
 * @author Matthias Gernand
 */
public class TesterBotSpeed extends SinusCurve {

	/**
	 * Maximum speed value.
	 */
	private static final double MAX_SPEED_VALUE = 100;

	/**
	 * Creates speed values following a sinus curve and ranging from
	 * <code>0</code> to {@value #MAX_SPEED_VALUE}.
	 * 
	 * @param runTime
	 *            the total time span for the creator
	 */
	public TesterBotSpeed(long runTime) {
		super(runTime, 0, MAX_SPEED_VALUE);
	}
}
