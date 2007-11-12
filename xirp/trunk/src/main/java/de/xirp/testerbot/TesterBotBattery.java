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
 * TesterBotBattery.java
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
 * Creates values for simulating a battery. The values will decrease
 * linear with the formula {@code f(x) = mx + b}
 * 
 * @author Matthias Gernand
 */
public class TesterBotBattery extends AbstractValueCreator {

	/**
	 * Maximum value for the battery
	 */
	private static final double MAX_BATT_VALUE = 5000;
	/**
	 * Minimum value for the battery
	 */
	private static final double MIN_BATT_VALUE = 0;
	/**
	 * Parameter b for the linear function
	 */
	private final double b;
	/**
	 * Parameter m for the linear function
	 */
	private final double m;

	/**
	 * Constructs a new object which simulates a linear decrease for
	 * battery values ranging from {@value #MAX_BATT_VALUE} to
	 * {@value #MIN_BATT_VALUE}.
	 * 
	 * @param runTime
	 *            the total run time for the value creator
	 */
	public TesterBotBattery(long runTime) {
		super(runTime);
		b = MAX_BATT_VALUE - Math.random( ) * 1000;
		m = (MIN_BATT_VALUE - b) / runTime;
	}

	/**
	 * Calculates a new battery value for the given time.
	 * 
	 * @see de.xirp.testerbot.AbstractValueCreator#calculate(long)
	 */
	@Override
	protected Number calculate(long elapsedTime) {
		double y = m * elapsedTime + b;
		return y;
	}
}
