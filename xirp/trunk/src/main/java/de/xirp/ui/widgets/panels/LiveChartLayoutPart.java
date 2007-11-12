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
 * LiveChartLayoutPart.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 27.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels;

import org.eclipse.swt.widgets.Composite;

import de.xirp.ui.dock.DockingManager;

/**
 * This part is used to show the live chart panel.
 * 
 * @author Matthias Gernand
 *
 */
public class LiveChartLayoutPart extends AbstractLayoutPart {

	/**
	 * The robot name.
	 */
	private String robotName;

	/**
	 * Constructs a new live chart layout part.
	 * 
	 * @param id
	 * 			The id.
	 * @param dm
	 * 			The docking manager.
	 * @param robotName
	 * 			The robot name.
	 */
	public LiveChartLayoutPart(String id, DockingManager dm, String robotName) {
		super(id, dm, robotName);
		this.robotName = robotName;
	}

	/**
	 * Creates the live chart content composite.
	 * 
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Composite createContent(Composite parent) {
		return new LiveChartComposite(parent, robotName);
	}

	/**
	 * Returns the i18n name key.
	 * 
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#getNameKey()
	 */
	@Override
	public String getNameKey() {
		return "LiveChartLayoutPart.gui.tabTitle"; //$NON-NLS-1$
	}

	/**
	 * Returns the contents class.
	 * 
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#getContentClass()
	 */
	@Override
	public Class<? extends Composite> getContentClass() {
		return LiveChartComposite.class;
	}

	/**
	 * This part can be shown in an own window.
	 * 
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#canShowInWindow()
	 */
	@Override
	public boolean canShowInWindow() {
		return true;
	}
}
