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
 * RobotOverviewLayoutPart.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 02.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels;

import org.eclipse.swt.widgets.Composite;

import de.xirp.ui.dock.DockingManager;

/**
 * A layout part providing the {@link RobotOverviewPanel}.
 * 
 * @author Matthias Gernand
 */
public class RobotOverviewLayoutPart extends AbstractLayoutPart {

	/**
	 * The name of the robot of this part.
	 */
	private String robotName;

	/**
	 * Creates a new layout part for a robot overview.
	 * 
	 * @param id
	 *            identifier for this part
	 * @param dm
	 *            the docking manager
	 * @param robotName
	 *            the name of the robot
	 */
	public RobotOverviewLayoutPart(String id, DockingManager dm,
			String robotName) {
		super(id, dm, robotName);
		this.robotName = robotName;
	}

	/**
	 * This panel may not be shown in an own window.
	 * 
	 * @return <code>false</code>
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#canShowInWindow()
	 */
	@Override
	public boolean canShowInWindow() {
		return false;
	}

	/**
	 * Creates a new {@link RobotOverviewPanel} for this part.
	 * 
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Composite createContent(Composite parent) {
		return new RobotOverviewPanel(parent, robotName);
	}

	/**
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#getContentClass()
	 */
	@Override
	public Class<? extends Composite> getContentClass() {
		return RobotOverviewPanel.class;
	}

	/**
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#getNameKey()
	 */
	@Override
	public String getNameKey() {
		return "RobotOverviewPanel.gui.tabTitle"; //$NON-NLS-1$
	}

}
