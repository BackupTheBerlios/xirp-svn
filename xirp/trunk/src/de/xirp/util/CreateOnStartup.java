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
 * CreateOnStartup.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 20.02.2007:		Created by Rabea Gransberger.
 */
package de.xirp.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for directories or files which should be created on
 * startup of the application. This does only work for constants
 * listed in {@link Constants}.
 * 
 * @author Rabea Gransberger
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target( {ElementType.FIELD})
public @interface CreateOnStartup {

	/**
	 * Checks if an already existing file or directory should be
	 * overridden.
	 * 
	 * @return <code>true</code> if an already existing file or
	 *         directory should be overridden with a new
	 *         file/directory on startup
	 */
	boolean override() default false;

	/**
	 * Checks if a directory or file should be created.
	 * 
	 * @return <code>true</code> if a directory should be created,
	 *         <code>false</code> for a file.
	 */
	boolean isDirectory() default true;
}
