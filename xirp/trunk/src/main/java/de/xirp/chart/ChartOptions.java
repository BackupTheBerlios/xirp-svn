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
 * ChartOptions.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 26.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.chart;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds some option flags that are used when generating a
 * chart. The options are collected from the chart config dialog and
 * then passed through to the
 * {@link de.xirp.chart.ChartManager}.
 * 
 * @author Matthias Gernand
 * @see de.xirp.chart.ChartManager
 * @see de.xirp.ui.widgets.dialogs.ChartConfigDialog
 */
public final class ChartOptions {

	/**
	 * A map holding the option flags.
	 */
	private Map<OptionName, Boolean> options;

	/**
	 * An enum with constants for indicating an option flag. There are
	 * eight options available. <br>
	 * <br>
	 * <ul>
	 * <li>CSV_EXPORT</li>
	 * <li>PNG_EXPORT</li>
	 * <li>JPEG_EXPORT</li>
	 * <li>PDF_EXPORT</li>
	 * <li>USE_ALL_KEYS</li>
	 * <li>THREE_D</li>
	 * <li>USE_RELATIVE</li>
	 * <li>SHOW_THRESHOLD</li>
	 * </ul>
	 * 
	 * @author Matthias Gernand
	 */
	public enum OptionName {
		/**
		 * Export CSV automatically or not.
		 */
		CSV_EXPORT, // 1
		/**
		 * Export PNG automatically or not.
		 */
		PNG_EXPORT, // 2
		/**
		 * Export JPG automatically or not.
		 */
		JPG_EXPORT, // 3
		/**
		 * Export PDF automatically or not.
		 */
		PDF_EXPORT, // 4
		/**
		 * Use all keys when generating the chart.
		 */
		USE_ALL_KEYS, // 5
		/**
		 * Generate a 3D chart.
		 */
		THREE_D, // 6
		/**
		 * Use relative time/amount values.
		 */
		USE_RELATIVE, // 7
		// SHOW_AVERAGE; // 8
		/**
		 * Show a threshold line in the chart.
		 */
		SHOW_THRESHOLD
		// 9
		/* ... */
	}

	/**
	 * Constructs a new
	 * {@link de.xirp.chart.ChartOptions} object. All
	 * option flag are set to <code>false</code>.
	 */
	public ChartOptions() {
		options = new HashMap<OptionName, Boolean>( );
		init( );
	}

	/**
	 * Initializes the options with all option flags set to
	 * <code>false</code>.
	 */
	private void init() {
		for (OptionName n : OptionName.values( )) {
			options.put(n, Boolean.FALSE);
		}
	}

	/**
	 * Returns the value of the given option flag name.
	 * 
	 * @param name
	 *            The <code>OptionName</code> to get the value of.
	 * @return A <code>boolean</code>: the value if the flag.
	 */
	public boolean is(OptionName name) {
		return options.get(name).booleanValue( );
	}

	/**
	 * Sets the value of the given option flag to the given value.
	 * 
	 * @param name
	 *            The <code>OptionName</code> to set the value of.
	 * @param b
	 *            The value of the option flag.
	 */
	public void set(OptionName name, boolean b) {
		options.put(name, b);
	}
}
