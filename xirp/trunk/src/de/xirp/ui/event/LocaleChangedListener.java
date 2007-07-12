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
 * LocaleChangedListener.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 01.06.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.event;

import java.util.EventListener;

/**
 * A locale change listener.
 * 
 * @author Matthias Gernand
 */
public interface LocaleChangedListener extends EventListener {

	/**
	 * This method is called, when the locale use in the application
	 * changed.
	 * 
	 * @param event
	 *            The event which occurred
	 */
	public void localeChanged(LocaleChangedEvent event);
}
