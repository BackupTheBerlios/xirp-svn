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
 * VoiceChangedEvent.java
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

import java.util.EventObject;

/**
 * A voice change event.
 * 
 * @author Matthias Gernand
 */
public class VoiceChangedEvent extends EventObject {

	/**
	 * The serial version unique identifier
	 */
	private static final long serialVersionUID = 5280962842820330485L;

	/**
	 * The name of the new active voice
	 */
	private String voiceName;

	/**
	 * Creates a new event.
	 * 
	 * @param source
	 *            The source of the event
	 * @param voiceName
	 *            the name of the new voice.
	 */
	public VoiceChangedEvent(Object source, String voiceName) {
		super(source);
		this.voiceName = voiceName;
	}

	/**
	 * Gets the name of the new active voice.
	 * 
	 * @return the voice name
	 */
	public String getVoiceName() {
		return voiceName;
	}
}
