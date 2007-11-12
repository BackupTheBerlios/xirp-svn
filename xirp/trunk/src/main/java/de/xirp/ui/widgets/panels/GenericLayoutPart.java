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
 * GenericLayoutPart.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * 					 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 11.06.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import de.xirp.plugin.AbstractPluginGUI;
import de.xirp.plugin.IPlugable;
import de.xirp.ui.dock.DockingManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.ui.widgets.custom.XLabel;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * This layout part is used for integrating the user interfaces of
 * plugins into the docking workspace.
 * 
 * @author Rabea Gransberger
 * @author Matthias Gernand
 */
public class GenericLayoutPart extends AbstractLayoutPart {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(GenericLayoutPart.class);
	/**
	 * The plugin for which the part is created.
	 */
	private IPlugable plugin;

	/**
	 * Created a new generic layout part for the given plugin and
	 * docking manager.
	 * 
	 * @param plugin
	 *            The plugin
	 * @param dm
	 *            The docking manager.
	 */
	public GenericLayoutPart(IPlugable plugin, DockingManager dm) {
		super(plugin.getIdentifier( ) == null ? plugin.getInfo( )
				.getMainClass( ) : plugin.getInfo( ).getMainClass( ) + "_" //$NON-NLS-1$
				+ plugin.getIdentifier( ), dm, plugin.getRobotName( ));
		this.plugin = plugin;
	}

	/**
	 * Returns the name of the plugin loaded in this part.
	 * 
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#getName()
	 */
	@Override
	public String getName() {
		String fullName = plugin.getName( );
		String identi = plugin.getIdentifier( );
		if (identi != null) {
			fullName += " " + identi; //$NON-NLS-1$
		}
		return fullName;
	}

	/**
	 * Returns the i18n name key.
	 * 
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#getNameKey()
	 */
	@Override
	// TODO identifier
	public String getNameKey() {
		return plugin.getNameKey( );
	}

	/**
	 * Creates the content composite from the plugin.
	 * 
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Composite createContent(Composite parent) {
		AbstractPluginGUI gui = plugin.getGUI(parent);
		if (gui != null) {
			SWTUtil.setGridData(gui, true, true, SWT.FILL, SWT.FILL, 1, 1);
			return gui;
		}
		else {

			logClass.warn(I18n.getString("GenericLayoutPart.plugin.noGUI", //$NON-NLS-1$
					plugin.getInfo( ).getMainClass( )) +
					Constants.LINE_SEPARATOR);
			XComposite empty = new XComposite(parent, SWT.NONE);
			empty.setLayout(new FillLayout( ));
			XLabel l = new XLabel(empty, SWT.NONE);
			l.setTextForLocaleKey("GenericLayoutPart.label.plugin.gui.notfound"); //$NON-NLS-1$
			return empty;
		}
	}

	/**
	 * Returns the plugin of this part.
	 * 
	 * @return the plugin
	 */
	protected IPlugable getPlugin() {
		return plugin;
	}

	/**
	 * Returns the i18n handler of the plugin.
	 * 
	 * @see de.xirp.ui.dock.ILayoutPart#getI18nHandler()
	 */
	@Override
	public II18nHandler getI18nHandler() {
		return plugin.getHandler( );
	}

	/**
	 * Returns the i18n arguments.
	 * 
	 * @see de.xirp.ui.dock.ILayoutPart#getI18nArgs()
	 */
	@Override
	public Object[] getI18nArgs() {
		String identi = plugin.getIdentifier( );
		if (identi != null) {
			return new Object[] {identi};
		}
		return null;
	}

	/**
	 * Returns the contents class.
	 * 
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#getContentClass()
	 */
	@Override
	public Class<? extends Composite> getContentClass() {
		return AbstractPluginGUI.class;
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
