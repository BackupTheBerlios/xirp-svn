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
 * ATEAdapter.java
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
 * A adapter for the {@link ATEListener}. Using this adapter allows to
 * override only the method of interest.
 * <br><br>
 * This is alpha API, may be removed in the future or
 * is planed to be integrated in version 3.0.0.
 * 
 * @see de.xirp.ate.ATEListener
 * 
 * @author Matthias Gernand
 *
 */
@FutureRelease(version = "3.0.0")//$NON-NLS-1$
public class ATEAdapter implements ATEListener {

	/* (non-Javadoc)
	 * @see de.xirp.ate.ATEListener#classListChanged()
	 */
	public void classListChanged() {

	}

	/* (non-Javadoc)
	 * @see de.xirp.ate.ATEListener#mazeChanged()
	 */
	public void mazeChanged(Maze maze) {

	}

	/* (non-Javadoc)
	 * @see de.xirp.ate.ATEListener#scenarioChanged()
	 */
	public void scenarioChanged() {

	}

}
