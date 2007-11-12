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
 * Hotkey.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 02.02.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.util;

/**
 * This class represents a SWT Shortcut. It contains the bitwise or'ed
 * accelerator, the variable name and the full accelerator name. <br>
 * <br>
 * Accelerator: SWT.CTRL | 'A' <br>
 * variableName: "A"<br>
 * fullAcceleratorName: "Ctrl+A"
 * 
 * @see de.xirp.ui.util.HotkeyManager
 * @author Matthias Gernand
 */
public final class Hotkey {

	/**
	 * The shortcut short name Example: "A"
	 */
	private String variableName = ""; //$NON-NLS-1$
	/**
	 * The bitwise or'ed SWT accelerator: Example: SWT.CTRL | 'A'
	 */
	private int accelerator = 0;
	/**
	 * The long name of the accelerator. Example: "Strg+A"
	 */
	private String fullAcceleratorName = ""; //$NON-NLS-1$

	/**
	 * Constructs a new Hotkey with the given variable name,
	 * accelerator and full accelerator name.
	 * 
	 * @param variableName
	 *            The variable name
	 * @param accelerator
	 *            The bitwise or'ed accelerator
	 * @param fullAcceleratorName
	 *            The full accelerator name
	 */
	public Hotkey(String variableName, int accelerator,
			String fullAcceleratorName) {
		this.variableName = variableName;
		this.accelerator = accelerator;
		this.fullAcceleratorName = "\t" + fullAcceleratorName; //$NON-NLS-1$
	}

	/**
	 * Returns the bitwise or'ed accelerator.
	 * 
	 * @return The accelerator.
	 */
	public int getAccelerator() {
		return accelerator;
	}

	/**
	 * Returns the full accelerator name.
	 * 
	 * @return The fullAcceleratorName.
	 */
	public String getFullAcceleratorName() {
		return fullAcceleratorName;
	}

	/**
	 * Returns the variable name.
	 * 
	 * @return The variableName.
	 */
	public String getVariableName() {
		return variableName;
	}
}
