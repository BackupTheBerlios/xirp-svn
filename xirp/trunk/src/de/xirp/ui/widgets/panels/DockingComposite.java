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
 * DockingComposite.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 10.05.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import de.xirp.plugin.IPlugable;
import de.xirp.ui.dock.DockingManager;
import de.xirp.ui.dock.ILayoutPart;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.util.Constants;

/**
 * A composite where the children can be arranged freely. The layout
 * is persisted to a XML file this layout is loaded again when the
 * composite is loaded.
 * 
 * @author Matthias Gernand
 */
public final class DockingComposite extends XComposite {

	/**
	 * The logger for this class.
	 */
	private static Logger logClass = Logger.getLogger(DockingComposite.class);
	/**
	 * The docking manager.
	 */
	private DockingManager dockingManager;
	/**
	 * A persist listener.
	 */
	private Listener persistPartListener;
	/**
	 * The path for the layout XML.
	 */
	private String path;

	/**
	 * Constructs a new docking composite for the given parent and
	 * file path.
	 * 
	 * @param parent
	 *            The parent composite.
	 * @param path
	 *            The path to the persisted layout.
	 */
	public DockingComposite(Composite parent, String path) {
		super(parent, SWT.NONE);
		this.path = path;
		if (!path.endsWith(".xml")) { //$NON-NLS-1$
			this.path += ".xml"; //$NON-NLS-1$
		}
		init( );
	}

	/**
	 * Initializes the composite. Loads a layout, if one is existing.
	 */
	private void init() {
		persistPartListener = new Listener( ) {

			public void handleEvent(Event event) {
				if (getParent( ).isDisposed( )) {
					return;
				}

				// TODO: some unwanted results may occur with the
				// commented code below
				XMLMemento memento = XMLMemento.createWriteRoot("layout"); //$NON-NLS-1$
				dockingManager.saveState(memento);
				try {
					File f = new File(Constants.CONF_WORKSPACELAYOUT_DIR +
							File.separator + path);
					if (f.exists( )) {
						f.delete( );
					}
					Writer w = new FileWriter(f, false);
					memento.save(w);
					logClass.debug("Saving layout to " + Constants.CONF_WORKSPACELAYOUT_DIR //$NON-NLS-1$
							+ File.separator + path + Constants.LINE_SEPARATOR);
					w.close( );
				}
				catch (IOException ex) {
					logClass.error("Error: " + ex.getMessage( ) //$NON-NLS-1$
							+ Constants.LINE_SEPARATOR, ex);
				}
			}
		};
		this.addListener(SWT.Dispose, persistPartListener);

		// allowed: (SWT.BOTTOM or SWT.TOP) and/or SWT.CLOSE
		dockingManager = new DockingManager(this, SWT.BOTTOM);

		try {
			FileReader fr = new FileReader(Constants.CONF_WORKSPACELAYOUT_DIR +
					File.separator + path);
			XMLMemento memento = XMLMemento.createReadRoot(fr);
			dockingManager.restoreState(memento);
			try {
				fr.close( );
			}
			catch (IOException ex) {
				logClass.error("Error: " + ex.getMessage( ) + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$
			}
		}
		catch (WorkbenchException ex) {
			logClass.error("Error: " + ex.getMessage( ) + Constants.LINE_SEPARATOR, ex); //$NON-NLS-1$ 
		}
		catch (FileNotFoundException ex) {
		}
	}

	/**
	 * Adds a plugin to the layout composite.
	 * 
	 * @param plugin
	 *            The plugin to add.
	 */
	public void addPlugin(IPlugable plugin) {
		dockingManager.addPart(new GenericLayoutPart(plugin, dockingManager));
	}

	/**
	 * Adds a layout part to the layout composite.
	 * 
	 * @param panel
	 *            The layout part.
	 */
	public void addPart(ILayoutPart panel) {
		dockingManager.addPart(panel);
	}

	/**
	 * Returns the docking manager of this composite.
	 * 
	 * @return DockingManager The docking manager of this composite.
	 */
	public DockingManager getDockingManager() {
		return dockingManager;
	}

}
