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
 * InstanceUtil.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 10.12.2006:		Created by Rabea Gransberger.
 */
package de.xirp.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

/**
 * Class containing utility methods for creating instances.
 * 
 * @author Rabea Gransberger
 */
public final class InstanceUtil {

	/**
	 * The Logger of this class.
	 */
	private static final Logger logClass = Logger.getLogger(InstanceUtil.class);

	/**
	 * Creates an instance for the class of the given name using the
	 * arguments as parameter types for the constructor of the class
	 * and as parameters itself. This means that there must exactly be
	 * an constructor for the given arguments types (only instance of
	 * is not sufficient).
	 * 
	 * @param clazzName
	 *            the fully qualified name of the class to get an
	 *            instance for.
	 * @param args
	 *            the parameters given to the construct when creating
	 *            the instance.
	 * @return an object of the given class name or <code>null</code>
	 *         if something went wrong. Read the log output in this
	 *         case.
	 */
	@SuppressWarnings("unchecked")
	public static final Object createInstance(final String clazzName,
			final Object... args) {

		Class[] classNames = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			classNames[i] = args[i].getClass( );
		}

		try {
			Class<?> clazz = Class.forName(clazzName);
			Constructor<?> constructor = clazz.getDeclaredConstructor(classNames);
			return constructor.newInstance(args);
		}
		catch (ClassNotFoundException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		catch (SecurityException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		catch (NoSuchMethodException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		catch (IllegalArgumentException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		catch (InstantiationException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		catch (IllegalAccessException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		catch (InvocationTargetException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}

		return null;
	}

}
