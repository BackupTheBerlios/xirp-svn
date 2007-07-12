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
 * TesterBotSonic.java
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
 * Creator for range values for a sonic sensor.
 * 
 * @author Matthias Gernand
 */
public class TesterBotSonic extends SinusCurve {

	/**
	 * Maximum value to the sonic sensor
	 */
	private static final double MAX_SONIC_VALUE = 7500;

	/**
	 * Creates a new object which creates range values for a sonic
	 * sensor. The range is dependent to the starting flag which
	 * allows to generate two different sonic sensor objects.
	 * 
	 * @param runTime
	 *            the total time span for the creator.
	 * @param startingFlag
	 *            This flag ranges from 1 to 4 and determines how the
	 *            curve looks.<br>
	 *            1: Ranging from {@value #MAX_SONIC_VALUE} to
	 *            <code>0</code><br>
	 *            2: Ranging from <code>0</code> to
	 *            {@value #MAX_SONIC_VALUE}<br>
	 *            3: Ranging from {@value #MAX_SONIC_VALUE} to
	 *            <code>0</code><br>
	 *            4: Ranging from <code>0</code> to
	 *            {@value #MAX_SONIC_VALUE}<br>
	 *            Each curve is additionally shifted by the flags
	 *            value.
	 */
	public TesterBotSonic(long runTime, int startingFlag) {
		super(runTime,
				getMin(startingFlag),
				getMax(startingFlag),
				true,
				startingFlag);
	}

	/**
	 * Gets the minimum value for the curve.
	 * 
	 * @param startingFlag
	 *            the flag which determines how the curve looks.
	 * @return 1: {@value #MAX_SONIC_VALUE}<br>
	 *         2: <code>0</code> <br>
	 *         3: {@value #MAX_SONIC_VALUE} <br>
	 *         4: <code>0</code><br>
	 */
	private static double getMin(int startingFlag) {
		double a = 0;
		switch (startingFlag) {
			case 1:
				a = MAX_SONIC_VALUE;
				break;
			case 2:
				a = 0;
				break;
			case 3:
				a = MAX_SONIC_VALUE;
				break;
			case 4:
				a = 0;
				break;
			default:
				a = MAX_SONIC_VALUE;
		}
		return a;
	}

	/**
	 * Gets the maximum value for the curve.
	 * 
	 * @param startingFlag
	 *            the flag which determines how the curve looks.
	 * @return 1: <code>0</code> <br>
	 *         2: {@value #MAX_SONIC_VALUE}<br>
	 *         3: <code>0</code><br>
	 *         4: {@value #MAX_SONIC_VALUE} <br>
	 */
	private static double getMax(int startingFlag) {
		double a = 0;
		switch (startingFlag) {
			case 2:
				a = MAX_SONIC_VALUE;
				break;
			case 1:
				a = 0;
				break;
			case 4:
				a = MAX_SONIC_VALUE;
				break;
			case 3:
				a = 0;
				break;
			default:
				a = MAX_SONIC_VALUE;
		}
		return a;
	}
}
