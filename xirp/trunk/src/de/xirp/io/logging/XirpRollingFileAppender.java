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
 * XirpRollingFileAppender.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.03.2006:		Created by Rabea Gransberger.
 */
package de.xirp.io.logging;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A RollingFileAppender with the option to ignore Throwables
 * 
 * @author Rabea Gransberger
 * @see org.apache.log4j.RollingFileAppender
 */
public class XirpRollingFileAppender extends RollingFileAppender {

	/**
	 * Flag determining if the Throwable should be written to File
	 */
	private boolean showThrowable = true;

	/**
	 * Gets the boolean which indicates if Throwables should be shown
	 * in file log or not.
	 * 
	 * @return <code>true</code> if Throwable will be shown in log
	 */
	public boolean getShowThrowable() {
		return showThrowable;
	}

	/**
	 * Sets if the Throwable should be shown in Log File.
	 * 
	 * @param showThrowable
	 *            <code>true</code> if Throwable should be shown in
	 *            log
	 */
	public void setShowThrowable(boolean showThrowable) {
		this.showThrowable = showThrowable;
	}

	/**
	 * Behaves like the
	 * {@link org.apache.log4j.RollingFileAppender#subAppend(org.apache.log4j.spi.LoggingEvent)}
	 * if Throwables should be shown. Otherwise it behaves like the
	 * FileAppender, without showing the Throwable but with RollOver.
	 * 
	 * @see org.apache.log4j.RollingFileAppender#subAppend(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void subAppend(LoggingEvent event) {
		if (showThrowable) {
			super.subAppend(event);
		}
		else {
			this.qw.write(this.layout.format(event));
			// Ignore Writing of Throwable
			if (this.immediateFlush) {
				this.qw.flush( );
			}
			if ((fileName != null)
					&& ((CountingQuietWriter) qw).getCount( ) >= maxFileSize) {
				this.rollOver( );
			}
		}
	}
}
