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
 * ATEListener.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 08.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.ate;

import java.util.EventListener;

import de.xirp.util.FutureRelease;

/**
 * A listener for several purposes. The change of the class list,
 * the change of a maze and the change of a scenario can be 
 * notified.
 * <br><br>
 * <b>Note:</b> Scenarios haven't been implemented yet.
 * <br><br>
 * This is alpha API, may be removed in the future or
 * is planned to be integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 *
 */
@FutureRelease(version = "3.0.0")//$NON-NLS-1$
public interface ATEListener extends EventListener {

	/**
	 * Indicates that the list of Java classes has changed.
	 */
	public void classListChanged();

	/**
	 * Indicates that a maze has changed.
	 * 
	 * @param maze
	 * 			The changed maze.
	 */
	public void mazeChanged(Maze maze);

	/**
	 * Indicates that a scenario has changed.
	 * 
	 */
	public void scenarioChanged();

}
