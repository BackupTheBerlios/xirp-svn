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
 * MazeListener.java
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

import de.xirp.util.FutureRelease;

/**
 * A listener for monitoring changes of a maze.
 * The method {@link de.xirp.ate.MazeListener#mazeModified(Maze maze)} 
 * will be called if something changes in the {@link de.xirp.ate.Maze}
 * data structure.
 * <br><br>
 * This is alpha API, may be removed in the future or
 * is planed to be integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 *
 */
@FutureRelease(version = "3.0.0")//$NON-NLS-1$
public interface MazeListener {

	/**
	 * Information that a {@link de.xirp.ate.Maze}
	 * structure has changed.
	 * 
	 * @param maze 
	 * 			The {@link de.xirp.ate.Maze} structure
	 * 			which has changed.
	 */
	public void mazeModified(Maze maze);

}
