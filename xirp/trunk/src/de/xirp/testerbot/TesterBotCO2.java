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
 * TesterBotCO2.java
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
 * Creator for CO<sub>2</sub> values.
 * 
 * @author Matthias Gernand
 */
public class TesterBotCO2 extends SinusCurve {

	/**
	 * The maximum CO<sub>2</sub> value.
	 */
	private static final double MAX_CO2_VALUE = 20;

	/**
	 * Creates a new creator which creates CO<sub>2</sub> values
	 * following a sinus curve and ranging from <code>0</code> to
	 * {@value #MAX_CO2_VALUE}.
	 * 
	 * @param runTime
	 *            the total time span for the creator
	 */
	public TesterBotCO2(long runTime) {
		super(runTime, 0, MAX_CO2_VALUE);
	}
}
