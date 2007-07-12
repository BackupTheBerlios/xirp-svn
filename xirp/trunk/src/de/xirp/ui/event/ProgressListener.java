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
 * ProgressListener.java
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
 * A progress listener which is used to show the progress on
 * application startup.
 * 
 * @author Rabea Gransberger
 */
public interface ProgressListener extends EventListener {

	/**
	 * This method is called when progress is made.
	 * 
	 * @param event
	 *            the event containing information about the progress
	 */
	public void progress(ProgressEvent event);

}
