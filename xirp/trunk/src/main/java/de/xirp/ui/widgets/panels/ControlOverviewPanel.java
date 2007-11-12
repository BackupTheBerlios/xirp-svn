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
 * ControlOverviewPanel.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 14.03.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.xirp.profile.Profile;
import de.xirp.profile.ProfileManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.ui.widgets.custom.XTabFolder;
import de.xirp.ui.widgets.custom.XTabItem;
import de.xirp.util.I18n;

/**
 * This UI class represents the C.O.P. (Control Overview Panel). It
 * contains all control panels, sensor displays and multimedia plugins
 * of the available robots.
 * 
 * @author Matthias Gernand
 */
public final class ControlOverviewPanel extends XComposite {

	/**
	 * Constructs a new control overview panel.
	 * 
	 * @param parent
	 *            The parent composite
	 */
	public ControlOverviewPanel(Composite parent) {
		super(parent, SWT.NONE);
		init( );
	}

	/**
	 * Initializes the control overview panel by loading the needed
	 * controls and setting the layout.
	 */
	private void init() {
		GridLayout layout = SWTUtil.setGridLayout(this, 1, true);
		SWTUtil.resetMargins(layout);

		XTabFolder folder = new XTabFolder(this, SWT.NONE);
		SWTUtil.setGridData(folder, true, true, SWT.FILL, SWT.FILL, 1, 1);

		for (Profile profile : ProfileManager.getProfiles( )) {
			createTab(profile, folder);
		}
		folder.setSelection(0);
	}

	/**
	 * Creates the COP tab for the given profile in the given tab
	 * folder.
	 * 
	 * @param profile
	 *            The profile.
	 * @param folder
	 *            The folder.
	 */
	private void createTab(Profile profile, XTabFolder folder) {
		XTabItem itm = new XTabItem(folder, SWT.NONE);
		itm.setTextForLocaleKey("ControlOverviewPanel.item.title.cop", profile.getName( )); //$NON-NLS-1$

		COPComposite comp = new COPComposite(folder, SWT.NONE, profile);

		itm.setControl(comp);

	}
}
