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
 * LogComposite.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 13.02.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

import de.xirp.io.logging.XirpTextFieldAppender;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.util.Variables;

/**
 * Panel for logging all relevant information of the application. <br>
 * <br>
 * All log messages of Log4j with level >= info are written to this
 * {@link de.xirp.ui.widgets.panels.LogComposite}.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class LogComposite extends XComposite {

	/**
	 * The styled text which will contain the log messages.
	 */
	private StyledText log;
	/**
	 * The robot name.
	 */
	private String robotName;

	/**
	 * Constructs a new panel for logging application information.
	 * 
	 * @param parent
	 *            Parent composite.
	 * @param robotName
	 *            the name of the robot, this log is for
	 */
	public LogComposite(Composite parent, String robotName) {
		super(parent, SWT.NONE);
		init( );
		parent.addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent e) {
				removeAppender( );
			}
		});
		this.robotName = robotName;
		addAppender( );
	}

	/**
	 * Initializes the log panel.
	 */
	private void init() {
		setBackground(ColorManager.getColor(Variables.BACKGROUND_COLOR));
		SWTUtil.setGridLayout(this, 1, false);

		log = new StyledText(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
		SWTUtil.setGridData(log, true, true, SWT.FILL, SWT.FILL, 1, 1);
	}

	/**
	 * Removes log4j appender.
	 */
	private void removeAppender() {
		XirpTextFieldAppender.removeTextField(robotName);
	}

	/**
	 * Adds log4j appender.
	 */
	public void addAppender() {
		XirpTextFieldAppender.addTextField(robotName, log);
	}

	/**
	 * Returns the log text area.
	 * 
	 * @return the log area.
	 */
	public StyledText getLog() {
		return log;
	}
}
