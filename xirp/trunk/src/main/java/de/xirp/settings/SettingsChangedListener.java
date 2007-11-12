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
 * SettingsChangedListener.java
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

import java.util.EventListener;

/**
 * Listener that is informed when settings were changed.
 * 
 * @author Rabea Gransberger
 */
public interface SettingsChangedListener extends EventListener {

	/**
	 * Method which is called by settings object if it was changed.
	 * 
	 * @param event
	 *            Event that has the Settings Object as source
	 */
	public void settingsChanged(SettingsChangedEvent event);

}
