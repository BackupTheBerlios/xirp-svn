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
 * DatapoolListener.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * 					 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 05.02.2007:		Created by Matthias Gernand.
 */ 
package de.xirp.io.comm.data;

import java.util.EventListener;

/**
 * Listener interface for Datapool events
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public interface DatapoolListener extends EventListener {

	/**
	 * Called when the value, the listener is registered to, has
	 * changed
	 * 
	 * @param e
	 *            the event with the data
	 */
	public void valueChanged(DatapoolEvent e);

	/**
	 * Test if the listener should only be notified if the value has
	 * changed
	 * 
	 * @return the implementation should return <code>true</code> if
	 *         this listener should only be notified by the datapool
	 *         if the value has changed and not for every new received
	 *         value
	 */
	public boolean notifyOnlyWhenChanged();

}
