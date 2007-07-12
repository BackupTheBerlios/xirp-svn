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
 * IByteListener.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 05.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.io.comm.lowlevel;

import java.util.EventListener;

/**
 * Listener interface which is notified when bytes are send/received.
 * 
 * @author Matthias Gernand
 */
public interface IByteListener extends EventListener {

	/**
	 * Event containing the number of bytes send or received.
	 * 
	 * @param e
	 *            the event
	 */
	public void handleBytes(ByteEvent e);

}
