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
 * DatapoolAdapter.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 05.02.2007:		Created by Rabea Gransberger.
 */ 
package de.xirp.io.comm.data;

/**
 * Default implementation of the DatapoolListener interface which is
 * notified only if the data has changed
 * 
 * @author Rabea Gransberger
 */
public abstract class DatapoolAdapter implements DatapoolListener {

	/**
	 * This listener is only notified when the data has changed
	 * 
	 * @return <code>true</code>
	 * @see de.xirp.io.comm.data.DatapoolListener#notifyOnlyWhenChanged()
	 */
	public boolean notifyOnlyWhenChanged() {
		return true;
	}

}
