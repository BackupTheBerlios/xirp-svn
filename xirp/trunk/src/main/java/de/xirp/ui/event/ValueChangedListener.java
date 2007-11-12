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
 * ValueChangedListener.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 10.05.2006:		Created by Rabea Gransberger.
 */
package de.xirp.ui.event;

import java.util.EventListener;

/**
 * A listener for value changes.
 * 
 * @author Rabea Gransberger
 */
public interface ValueChangedListener extends EventListener {

	/**
	 * This method is called, when a value changed.
	 * 
	 * @param event
	 *            The event which occurred
	 */
	public void valueChanged(ValueChangedEvent event);
}
