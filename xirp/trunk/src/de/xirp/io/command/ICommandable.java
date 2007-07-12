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
 * ICommandable.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 10.02.2007:		Created by Rabea Gransberger.
 */
package de.xirp.io.command;

import java.util.List;

/**
 * Interface for a class which likes to be commandable by keyboard
 * input f.e.
 * 
 * @author Rabea Gransberger
 */
public interface ICommandable {

	/**
	 * A list of defined commands for this commandable
	 * 
	 * @return the list
	 */
	public List<CommandDefinition> getCommandDefinitions();

	/**
	 * Execute the given command
	 * 
	 * @param command
	 *            the command to execute
	 * @return <code>true</code> if the command was executed
	 */
	public boolean executeCommand(Command command);

}
