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
 * AppearanceChangedListener.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 01.06.200:		Created by Matthias Gernand.
 */
package de.xirp.ui.event;

import java.util.EventListener;

/**
 * A appearance change listener.
 * 
 * @author Matthias Gernand
 */
public interface AppearanceChangedListener extends EventListener {

	/**
	 * This method is called, when the appearance (look and feel of
	 * the application) changed.
	 * 
	 * @param event
	 *            The event which occurred
	 */
	public void appearanceChanged(AppearanceChangedEvent event);
}
