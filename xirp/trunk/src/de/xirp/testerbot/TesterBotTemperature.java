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
 * TesterBotTemperature.java
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

import java.util.Random;

/**
 * Creator for temperature values.
 * 
 * @author Matthias Gernand
 *
 */
public class TesterBotTemperature extends AbstractValueCreator {

	/**
	 * 
	 */
	private static final double DEFAULT_TEMPERATURE = 20.0;
	/**
	 * 
	 */
	private static final double MAX_TEMP_VALUE = 17.5;
	/**
	 * 
	 */
	private final Random random;
	/**
	 * 
	 */
	private final double a;
	/**
	 * 
	 */
	private final double b;

	/**
	 * Constructs a new creator for temperature values.
	 * The generated values span from {@value #MAX_TEMP_VALUE}
	 * to 35.0, the value will mostly be around the default
	 * temperature value of {@value #DEFAULT_TEMPERATURE} and
	 * only sometimes climb up to 35.0.
	 * 
	 * @param runTime
	 * 			The total time span for the creator.
	 */
	public TesterBotTemperature(long runTime) {
		super(runTime);
		random = new Random( );
		a = MAX_TEMP_VALUE;
		b = (2 * Math.PI) / runTime;
	}

	/**
	 * Returns the calculated temperature value.
	 * 
	 * @param elapsedTime
	 * 			The elapsed time in milliseconds.
	 * @return The calculated value.
	 * 
	 * @see de.xirp.testerbot.AbstractValueCreator#calculate(long)
	 */
	@Override
	protected Number calculate(long elapsedTime) {
		int aux;
		try {
			aux = (int) (runTime / elapsedTime);
		}
		catch (ArithmeticException e) {
			aux = 0;
		}
		if (aux % 2 == 0) {
			double y = a * Math.sin(b * elapsedTime);
			return y + MAX_TEMP_VALUE;
		}
		return (random.nextInt(150) / 100) + DEFAULT_TEMPERATURE;
	}

}
