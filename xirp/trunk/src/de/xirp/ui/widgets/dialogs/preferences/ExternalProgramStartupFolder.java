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
 * ExternalProgramStartupFolder.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 16.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs.preferences;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.xirp.profile.Profile;
import de.xirp.profile.ProfileGenerator;
import de.xirp.profile.ProfileManager;
import de.xirp.profile.Tool;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.widgets.custom.XTabItem;
import de.xirp.util.Constants;

/**
 * A custom composite which shows the settings for the external
 * {@link de.xirp.profile.Tool tools} of all loaded
 * {@link de.xirp.profile.Profile profiles}.
 */
public class ExternalProgramStartupFolder extends
		AbstractContentFolderComposite {

	/**
	 * log4j logger for this class.
	 */
	private static Logger logClass = Logger.getLogger(ExternalProgramStartupFolder.class);
	/**
	 * The groups with the UI for the settings. One tab for each
	 * loaded profile.
	 */
	private List<ExternalProgramsGroup> groups = new ArrayList<ExternalProgramsGroup>( );
	/**
	 * Flag indicating a change.
	 */
	protected boolean change = false;

	/**
	 * Constructs a new external program startup folder.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	public ExternalProgramStartupFolder(Composite parent) {
		super(parent, SWT.NONE);
		init( );
	}

	/**
	 * initializes the items of the folder.
	 */
	private void init() {
		for (final String profileName : ProfileManager.getProfileNames( )) {

			Profile profile = ProfileManager.getProfile(profileName);

			XTabItem itemPlugins = new XTabItem(tabFolder, SWT.NONE);
			itemPlugins.setTextForLocaleKey("ExternalProgramStartupFolder.item.text.profile", profileName); //$NON-NLS-1$

			ExternalProgramsGroup grp = new ExternalProgramsGroup(tabFolder,
					SWT.NONE,
					profile,
					this);
			groups.add(grp);

			itemPlugins.setControl(grp);
		}

		finishInit( );
	}

	/**
	 * Saves the settings for external programs via the
	 * {@link de.xirp.profile.ProfileGenerator}.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#save()
	 */
	@Override
	public void save() {
		for (ExternalProgramsGroup grp : groups) {
			Vector<Tool> tools = grp.getTools( );
			Profile profile = ProfileManager.getProfile(grp.getProfileName( ));
			profile.getExternalTools( ).getTools( ).clear( );
			for (Tool tool : tools) {
				profile.getExternalTools( ).getTools( ).add(tool.clone( ));
			}
		}
		ApplicationManager.contentChanged( );
		for (Profile profile : ProfileManager.getProfiles( )) {
			try {
				ProfileGenerator.generatePRO(profile, profile.getProFile( ));
			}
			catch (FileNotFoundException e) {
				logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
			catch (JAXBException e) {
				logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * This folder does not support defaults.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#supportsDefaults()
	 */
	@Override
	public boolean supportsDefaults() {
		return false;
	}

	/**
	 * This folder does not support resets.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#supportsReset()
	 */
	@Override
	public boolean supportsReset() {
		return false;
	}

	/**
	 * A change must be notified from the groups by settings
	 * {@link #change} of this folder.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#hasChanged()
	 */
	@Override
	public boolean hasChanged() {
		return change;
	}
}
