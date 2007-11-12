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
 * AbstractValueCreator.java
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
 * An abstract class for a object which creates different values for a
 * testerbot for each time of a time span.
 * 
 * @author Matthias Gernand
 */
public abstract class AbstractValueCreator {

	/**
	 * The total run time for the values
	 */
	protected final long runTime;

	/**
	 * Constructs a new value creator for the given time span.
	 * 
	 * @param runTime
	 *            the time span for the creator in milliseconds
	 */
	public AbstractValueCreator(long runTime) {
		this.runTime = runTime;
	}

	/**
	 * Calculates a value for the given elapsed time within the time
	 * span.
	 * 
	 * @param elapsedTime
	 *            elapsed time in milliseconds.
	 * @return the value created
	 */
	protected abstract Object calculate(long elapsedTime);

}
