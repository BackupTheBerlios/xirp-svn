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
 * LocaleChangedEvent.java
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
 * A locale change event.
 * 
 * @author Matthias Gernand
 */
public class LocaleChangedEvent extends EventObject {

	/**
	 * The serial version unique identifier
	 */
	private static final long serialVersionUID = 4001733326439177697L;

	/**
	 * Creates a new event.
	 * 
	 * @param source
	 *            The source
	 */
	public LocaleChangedEvent(Object source) {
		super(source);
	}
}
