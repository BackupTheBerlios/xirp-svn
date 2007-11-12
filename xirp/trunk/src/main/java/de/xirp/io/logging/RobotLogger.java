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
 * RobotLogger.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 10.02.2007:		Created by Rabea Gransberger.
 */
package de.xirp.io.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * Logger which should be used when logging events from a plugin which
 * handles more than one robot.
 * 
 * @author Rabea Gransberger
 */
public class RobotLogger {

	/**
	 * Prefix for logging events for robots
	 */
	public static final String ROBOT_IDENTIFIER = "ROBOT_"; //$NON-NLS-1$
	/**
	 * Delimiter between robot name and event text
	 */
	public static final String ROBOT_DELIMITER = ":::"; //$NON-NLS-1$

	/**
	 * The original log4j logger
	 */
	private Logger delegate;

	/**
	 * Creates a new robot logger for the given class.<br/> This
	 * logger is based on a log4j logger so be sure that the logger
	 * for one class is only created once (make it static final).
	 * 
	 * @param clazz
	 *            the class for which the logger is created.
	 */
	private RobotLogger(Class<?> clazz) {
		delegate = Logger.getLogger(clazz);
	}

	/**
	 * Creates a new robot logger for the given class.<br/> This
	 * logger is based on a log4j logger so be sure that the logger
	 * for one class is only created once (make it static final).
	 * 
	 * @param clazz
	 *            the class for which the logger is created.
	 * @return the new logger for logging robot events
	 */
	public static RobotLogger getLogger(Class<?> clazz) {
		return new RobotLogger(clazz);
	}

	/**
	 * Logs a message which was caused by a given throwable at trace
	 * level.
	 * 
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @param th
	 *            the throwable which was the cause for this message
	 * @see org.apache.log4j.Category#log(org.apache.log4j.Priority,Object,
	 *      Throwable)
	 */
	public void trace(String robotName, String message, Throwable th) {
		log(Level.TRACE, robotName, message, th);
	}

	/**
	 * Logs a message which was caused by a given throwable at trace
	 * level.
	 * 
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @see org.apache.log4j.Category#log(org.apache.log4j.Priority,java.lang.Object)
	 */
	public void trace(String robotName, String message) {
		log(Level.TRACE, robotName, message);
	}

	/**
	 * Logs a message which was caused by a given throwable at debug
	 * level.
	 * 
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @param th
	 *            the throwable which was the cause for this message
	 * @see org.apache.log4j.Category#debug(Object, Throwable)
	 */
	public void debug(String robotName, String message, Throwable th) {
		log(Level.DEBUG, robotName, message, th);
	}

	/**
	 * Logs a message which was caused by a given throwable at debug
	 * level.
	 * 
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @see org.apache.log4j.Category#debug(java.lang.Object)
	 */
	public void debug(String robotName, String message) {
		log(Level.DEBUG, robotName, message);
	}

	/**
	 * Logs a message which was caused by a given throwable at info
	 * level.
	 * 
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @param th
	 *            the throwable which was the cause for this message
	 * @see org.apache.log4j.Category#info(Object, Throwable)
	 */
	public void info(String robotName, String message, Throwable th) {
		log(Level.INFO, robotName, message, th);
	}

	/**
	 * Logs a message which was caused by a given throwable at info
	 * level.
	 * 
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @see org.apache.log4j.Category#info(java.lang.Object)
	 */
	public void info(String robotName, String message) {
		log(Level.INFO, robotName, message);
	}

	/**
	 * Logs a message which was caused by a given throwable at warn
	 * level.
	 * 
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @param th
	 *            the throwable which was the cause for this message
	 * @see org.apache.log4j.Category#warn(Object, Throwable)
	 */
	public void warn(String robotName, String message, Throwable th) {
		log(Level.WARN, robotName, message, th);
	}

	/**
	 * Logs a message which was caused by a given throwable at warn
	 * level.
	 * 
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @see org.apache.log4j.Category#warn(java.lang.Object)
	 */
	public void warn(String robotName, String message) {
		log(Level.WARN, robotName, message);
	}

	/**
	 * Logs a message which was caused by a given throwable at error
	 * level.
	 * 
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @param th
	 *            the throwable which was the cause for this message
	 * @see org.apache.log4j.Category#error(Object, Throwable)
	 */
	public void error(String robotName, String message, Throwable th) {
		log(Level.ERROR, robotName, message, th);
	}

	/**
	 * Logs a message which was caused by a given throwable at error
	 * level.
	 * 
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @see org.apache.log4j.Category#error(java.lang.Object)
	 */
	public void error(String robotName, String message) {
		log(Level.ERROR, robotName, message);
	}

	/**
	 * Logs a message which was caused by a given throwable at fatal
	 * level.
	 * 
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @param th
	 *            the throwable which was the cause for this message
	 * @see org.apache.log4j.Category#fatal(Object, Throwable)
	 */
	public void fatal(String robotName, String message, Throwable th) {
		log(Level.FATAL, robotName, message, th);
	}

	/**
	 * Logs a message which was caused by a given throwable at fatal
	 * level.
	 * 
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @see org.apache.log4j.Category#fatal(java.lang.Object)
	 */
	public void fatal(String robotName, String message) {
		log(Level.FATAL, robotName, message);
	}

	/**
	 * Logs a message which was caused by a given throwable at the
	 * given level.
	 * 
	 * @param prio
	 *            the level for the message to log
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @param th
	 *            the throwable which was the cause for this message
	 * @see org.apache.log4j.Category#log(org.apache.log4j.Priority,
	 *      java.lang.Object, java.lang.Throwable)
	 */
	public void log(Priority prio, String robotName, String message,
			Throwable th) {
		delegate.log(prio, getRobotMessage(robotName, message), th);
	}

	/**
	 * Logs a message at the given level.
	 * 
	 * @param prio
	 *            the level for the message to log
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @see org.apache.log4j.Category#log(org.apache.log4j.Priority,
	 *      java.lang.Object)
	 */
	public void log(Priority prio, String robotName, String message) {
		delegate.log(prio, getRobotMessage(robotName, message));
	}

	/**
	 * Logs a message which was caused by a given throwable at the
	 * given level.
	 * 
	 * @param what
	 * @param prio
	 *            the level for the message to log
	 * @param robotName
	 *            the name of the robot to which the message belongs
	 *            to
	 * @param message
	 *            the message which should be logger for the robot
	 * @param th
	 *            the throwable which was the cause for this message
	 * @see org.apache.log4j.Category#log(java.lang.String,
	 *      org.apache.log4j.Priority, java.lang.Object,
	 *      java.lang.Throwable)
	 */
	public void log(String what, Priority prio, String robotName,
			String message, Throwable th) {
		delegate.log(what, prio, getRobotMessage(robotName, message), th);
	}

	/**
	 * Creates a debug string for this logger
	 * 
	 * @see org.apache.log4j.Category#toString()
	 */
	@Override
	public String toString() {
		return delegate.toString( );
	}

	/**
	 * Writes the robot name into the log message to get a message
	 * which may be forward to the original log4j logger
	 * 
	 * @param robotName
	 *            the name of the robot
	 * @param message
	 *            the original message
	 * @return a message containing the robot name and original
	 *         message
	 */
	private String getRobotMessage(String robotName, String message) {
		return ROBOT_IDENTIFIER + robotName + ROBOT_DELIMITER + message;
	}

}
