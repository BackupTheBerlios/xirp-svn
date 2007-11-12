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
 * VirtualPanel.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 03.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels.virtual;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.xirp.ui.dock.ILayoutPart;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.ui.widgets.panels.DockingComposite;
import de.xirp.ui.widgets.panels.LogLayoutPart;
import de.xirp.util.FutureRelease;

/**
 * This is alpha API, may be removed in the future or
 * is planed to be integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 *
 */
@FutureRelease(version = "3.0.0") //$NON-NLS-1$
public class VirtualPanel extends XComposite {

	/**
	 * 
	 */
	private static final String VIRTUAL = "_virtual"; //$NON-NLS-1$

	/**
	 * @param parent
	 * @param style
	 */
	public VirtualPanel(Composite parent, int style) {
		super(parent, style);
		init( );
	}

	/**
	 * 
	 */
	private void init() {
		GridLayout gl = SWTUtil.setGridLayout(this, 1, true);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		
		DockingComposite docking = new DockingComposite(this,
				"workspacelayout_virtual"); //$NON-NLS-1$
		SWTUtil.setGridData(docking, true, true, SWT.FILL, SWT.FILL, 1, 1);
		
		ILayoutPart part = new LogLayoutPart(LogLayoutPart.class.getCanonicalName( )
				+ VIRTUAL, docking.getDockingManager( ), "virtbot"); //$NON-NLS-1$
		docking.addPart(part);
		
		part = new MazeLayoutPart(MazeLayoutPart.class.getCanonicalName( )
				+ VIRTUAL, docking.getDockingManager( ));
		docking.addPart(part);
		
		part = new ScenarioLayoutPart(ScenarioLayoutPart.class.getCanonicalName( )
				+ VIRTUAL, docking.getDockingManager( ));
		docking.addPart(part);
		
		part = new CodeLayoutPart(CodeLayoutPart.class.getCanonicalName( )
				+ VIRTUAL, docking.getDockingManager( ));
		docking.addPart(part);
	}

}
