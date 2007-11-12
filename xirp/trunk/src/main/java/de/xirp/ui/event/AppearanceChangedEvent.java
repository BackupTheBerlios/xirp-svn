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
 * AppearanceChangedEvent.java
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

import java.util.EventObject;

/**
 * An appearance change event.
 * 
 * @author Matthias Gernand
 */
public class AppearanceChangedEvent extends EventObject {

	/**
	 * The serial version unique identifier
	 */
	private static final long serialVersionUID = -419695286347202025L;

	/**
	 * An enum for indicating the type of appearance change. Unused at
	 * the moment.
	 * 
	 * @author Matthias Gernand
	 */
	public enum AppearanceType {
		/**
		 * 
		 */
		BACKGROUND;
	}

	/**
	 * Creates a new event.
	 * 
	 * @param source
	 *            The source of the event.
	 */
	public AppearanceChangedEvent(Object source) {
		super(source);
	}
}
