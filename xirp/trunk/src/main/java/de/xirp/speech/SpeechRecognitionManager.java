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
 * SpeechRecognitionManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 05.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.speech;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.util.FutureRelease;

/**
 * This manager controls the speech recognition feature.
 * <br><br>
 * This is alpha API, may be removed in the future or
 * is planned to be integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 */
@FutureRelease(version = "3.0.0")
public final class SpeechRecognitionManager extends AbstractManager {

	/**
	 * Constructs a new manager.
	 * <br><br>
	 * The manager is initialized on startup. Never
	 * call this on your own. Use the statically provided
	 * methods.
	 * 
	 * @throws InstantiationException if an instance already exists.
	 */
	public SpeechRecognitionManager() throws InstantiationException {
		super( );
	}

	/**
	 * Does nothing.
	 * <br><br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * startup. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
	}

	/**
	 * Does nothing.
	 * <br><br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * shutdown. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
	}
}
