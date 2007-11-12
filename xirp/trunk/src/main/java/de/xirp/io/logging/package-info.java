/* 
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
 * package-info.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 23.05.2007:		Created by Rabea Gransberger.
 */
/**
 * Provides classes needed for logging.<br/>
 *
 * This package has a special log4j file appender, which writes
 * stack traces only on request to the file.
 * <p> 
 * In addition there is an appender which writes the logging events to a textfield which
 * is shown in the application and a special logger used for logging the
 * name of the robot at which an event occurred.
 */
package de.xirp.io.logging;