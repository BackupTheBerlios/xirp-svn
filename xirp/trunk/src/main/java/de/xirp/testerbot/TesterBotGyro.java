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
 * TesterBotGyro.java
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
 * Creator for angle values for a gyro.
 * 
 * @author Matthias Gernand
 */
public class TesterBotGyro extends SinusCurve {

	/**
	 * The maximum value for the gyro.
	 */
	private static final double MAX_GYRO_VALUE = 35;

	/**
	 * Creates a new object which creates angle values for a gyro. The
	 * range is dependent to the starting flag which allows to
	 * generate two different gyro objects.
	 * 
	 * @param runTime
	 *            the total time span for the creator.
	 * @param startingFlag
	 *            <code>true</code> results in a sinus curve ranging
	 *            from <code>0</code> to {@value #MAX_GYRO_VALUE}.<br>
	 *            <code>false</code> results in a sinus curve
	 *            ranging from {@value #MAX_GYRO_VALUE} to
	 *            <code>0</code> .
	 */
	public TesterBotGyro(long runTime, boolean startingFlag) {
		super(runTime, startingFlag ? -MAX_GYRO_VALUE : MAX_GYRO_VALUE, startingFlag ? MAX_GYRO_VALUE : -MAX_GYRO_VALUE);
	}

}
