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
 * VoiceChangedListener.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 20.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.event;

import java.util.EventListener;

/**
 * A voice change listener.
 * 
 * @author Matthias Gernand
 */
public interface VoiceChangedListener extends EventListener {

	/**
	 * This method is called, when the voice changed.
	 * 
	 * @param event
	 *            The event which occurred
	 */
	public void voiceChanged(VoiceChangedEvent event);
}
