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
 * SinusCurve.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 07.05.2007:		Created by Rabea Gransberger.
 */
package de.xirp.testerbot;

/**
 * A sinus curve generator for the testerbot for the function
 * {@code f(x) = a * sin(bx + c) + d}.
 * 
 * @author Rabea Gransberger
 */
public class SinusCurve extends AbstractValueCreator {

	/**
	 * The amplitude (a) for the sinus curve.
	 */
	private final double amplitude;
	/**
	 * The period (b) for the sinus curve.
	 */
	private final double period;
	/**
	 * The phase (c) for the sinus curve.
	 */
	private final double phase;
	/**
	 * The shif (d) for the sinus curve.
	 */
	private final double shift;

	/**
	 * Constructs a new sinus curve by calculating the function
	 * {@code f(x) = a * sin(bx + c) + d} from the given parameters.
	 * 
	 * @param runTime
	 *            the time the sinus curve should span.
	 * @param min
	 *            the minimum value for the curve
	 * @param max
	 *            the maximum value for the curve
	 */
	protected SinusCurve(long runTime, double min, double max) {
		this(runTime, min, max, false);
	}

	/**
	 * Constructs a new sinus curve by calculating the function
	 * {@code f(x) = a * sin(bx + c) + d} from the given parameters
	 * which may start at it's maximum value.
	 * 
	 * @param runTime
	 *            the time the sinus curve should span.
	 * @param min
	 *            the minimum value for the curve
	 * @param max
	 *            the maximum value for the curve
	 * @param startAtMax
	 *            <code>true</code> if the curve should start at
	 *            it's maximum value
	 */
	protected SinusCurve(long runTime, double min, double max,
			boolean startAtMax) {
		this(runTime, min, max, startAtMax, 1.0);
	}

	/**
	 * Constructs a new sinus curve by calculating the function
	 * {@code f(x) = a * sin(bx + c) + d} from the given parameters
	 * which may start at it's maximum value and may have an
	 * additional phase (c).
	 * 
	 * @param runTime
	 *            the time the sinus curve should span.
	 * @param min
	 *            the minimum value for the curve
	 * @param max
	 *            the maximum value for the curve
	 * @param startAtMax
	 *            <code>true</code> if the curve should start at
	 *            it's maximum value
	 * @param additionalPhase
	 *            additional phase which is added to the calculated
	 *            phase (c).
	 */
	protected SinusCurve(long runTime, double min, double max,
			boolean startAtMax, double additionalPhase) {
		// TODO phase als prozent angeben
		super(runTime);
		double delta = max - min;
		this.amplitude = delta / 2.0;
		this.shift = delta / 2.0 + min;
		this.period = (2 * Math.PI) / runTime;
		double d = -shift;
		if (startAtMax) {
			d = max - shift;
		}
		this.phase = Math.asin(d / amplitude) + additionalPhase;
		// System.out.println(this.getClass( ).getSimpleName( ) + ": "
		// + amplitude
		// + "*sin(" + period + "x+" + phase + ")+" + shift);

		// y = a * sin(b*x+c)+d
		// max = a * sin(c)+d
		// max - d / a
	}

	/**
	 * Calculates the value for the given time by using the sinus
	 * function of this curve.
	 */
	@Override
	protected Number calculate(long elapsedTime) {
		double y = amplitude * Math.sin(period * elapsedTime + phase) + shift;
		return y;
	}
}
