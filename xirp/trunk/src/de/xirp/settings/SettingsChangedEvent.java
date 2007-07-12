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
 * SettingsChangedEvent.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 31.03.2006:		Created by Rabea Gransberger.
 */
package de.xirp.settings;

import java.util.EventObject;

/**
 * Event which is thrown when settings were changed by GUI.
 * 
 * @author Rabea Gransberger
 * 
 */
public class SettingsChangedEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Event with no data.
	 * 
	 * @param source
	 *            The <code>Settings</code> object that was changed.
	 */
	public SettingsChangedEvent(Object source) {
		super(source);
	}

}
