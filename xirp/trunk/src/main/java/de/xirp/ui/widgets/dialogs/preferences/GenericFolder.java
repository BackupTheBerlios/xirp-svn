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
 * GenericFolder.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * 					 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 17.03.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.xirp.settings.Settings;

/**
 * An generic folder for showing settings objects.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class GenericFolder extends AbstractContentFolderComposite {

	/**
	 * The logger of this class.
	 */
	// private static Logger logClass = Logger.getLogger(GenericFolder.class);
	/**
	 * Constructs a new generic folder with the given parent composite
	 * and {@link de.xirp.settings.Settings settings}.
	 * 
	 * @param parent
	 *            The parent composite
	 * @param settings
	 *            The plugins settings
	 *            
	 * @see de.xirp.settings.Settings         
	 */
	public GenericFolder(Composite parent, Settings settings) {
		super(parent, SWT.NONE);
		this.settings = settings;
		init( );
	}

	/**
	 * Initializes the contents of the folder.
	 */
	private void init() {
		constructUIFromSettings(settings);
		finishInit( );
	}

	/**
	 * Returns the plugins 
	 * {@link de.xirp.settings.Settings settings}.
	 * 
	 * @return The settings.
	 * 
	 * @see de.xirp.settings.Settings
	 */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * This folder supports defaults.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#supportsDefaults()
	 */
	@Override
	public boolean supportsDefaults() {
		return true;
	}

	/**
	 * This folder supports resets.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#supportsReset()
	 */
	@Override
	public boolean supportsReset() {
		return true;
	}
}
