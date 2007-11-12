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
 * HelpManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 09.02.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.plugin.IPlugable;
import de.xirp.plugin.PluginManager;
import de.xirp.util.Constants;

/**
 * This manager manages the help topic file provided by the loaded
 * plugins and the help files of Xirp 2 itself. <br>
 * <br>
 * It unpacks all necessary HTML help files off the plugins jar to the
 * /help/plugins/&lt;pluginname&gt; directory and builds the
 * <code>index.html</code> page of the help pages. <br>
 * <br>
 * The pages can be viewed using the
 * {@link de.xirp.ui.widgets.dialogs.HelpDialog}.
 * 
 * @author Matthias Gernand
 */
public final class HelpManager extends AbstractManager {

	/**
	 * The Logger for this class.
	 */
	private static Logger logClass = Logger.getLogger(HelpManager.class);
	/**
	 * List of all loaded plugins
	 */
	@SuppressWarnings("unchecked")
	private List<IPlugable> plugins;

	/**
	 * Constructs a new help manager. The manager is initialized on
	 * startup. Never call this on your own. Use the statically
	 * provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	public HelpManager() throws InstantiationException {
		super( );
	}

	/**
	 * Calls {@link #clearDir( )} to delete all existing help files.
	 * Then calls {@link #extractAllHelp( )} to extract all plugin
	 * help files of the loaded plugins. Finally calls
	 * {@link #createNavigationHTML( )} to create the
	 * <code>index.html</code> page of the help files.
	 */
	private void init() {
		clearDir( );
		extractAllHelp( );
		createNavigationHTML( );
	}

	/**
	 * Extracts the contents of the help folder in each plugin jar to
	 * the local folder /help/plugins/&lt;pluginname&gt;.
	 */
	@SuppressWarnings("unchecked")
	private void extractAllHelp() {
		for (IPlugable aux : plugins) {
			if (aux.getInfo( ).hasHelp( )) {
				File tmp = new File(Constants.HELP_DIR + File.separator
						+ "plugins" + File.separator + aux.getName( )); //$NON-NLS-1$
				tmp.mkdirs( );
				PluginManager.extractHelp(aux.getInfo( ), tmp.getAbsolutePath( ));
			}
		}
	}

	/**
	 * Deletes the contents of the /help/plugins directory.
	 */
	private void clearDir() {
		File dir = new File(Constants.HELP_DIR + File.separator + "plugins"); //$NON-NLS-1$
		for (File aux : dir.listFiles( )) {
			if (aux.isDirectory( ) && !(aux.getName( ).equals(".svn"))) { //$NON-NLS-1$
				try {
					FileUtils.deleteDirectory(aux);
				}
				catch (IOException e) {
					logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Writes the navigation HTML-file. The links for existing plugins
	 * are added to the existing links of the standard program help.
	 */
	private void createNavigationHTML() {
		StringBuilder naviPage = new StringBuilder( );
		naviPage.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">").append(Constants.LINE_SEPARATOR); //$NON-NLS-1$
		naviPage.append("<HTML>").append(Constants.LINE_SEPARATOR); //$NON-NLS-1$
		naviPage.append("<HEAD>").append(Constants.LINE_SEPARATOR); //$NON-NLS-1$
		naviPage.append("	<META HTTP-EQUIV=\"CONTENT-TYPE\" CONTENT=\"text/html; charset=windows-1252\">").append(Constants.LINE_SEPARATOR); //$NON-NLS-1$
		naviPage.append("	<TITLE>nav frame</TITLE>").append(Constants.LINE_SEPARATOR); //$NON-NLS-1$
		naviPage.append("</HEAD>").append(Constants.LINE_SEPARATOR); //$NON-NLS-1$
		naviPage.append("<BODY>").append(Constants.LINE_SEPARATOR); //$NON-NLS-1$
		// naviPage.append("<UL>"+Constants.LINE_SEPARATOR);

		createXirpLinks(naviPage);
		createPluginLinks(naviPage);

		// naviPage.append("</UL>"+Constants.LINE_SEPARATOR);
		naviPage.append("</BODY>").append(Constants.LINE_SEPARATOR); //$NON-NLS-1$
		naviPage.append("</HTML>").append(Constants.LINE_SEPARATOR); //$NON-NLS-1$

		File f = new File(Constants.HELP_DIR + File.separator
				+ "nav_frame.html"); //$NON-NLS-1$
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(naviPage.toString( ));
			fw.close( );

		}
		catch (FileNotFoundException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * This method adds the needed plugin help links to the navigation
	 * HTML-file by appending them to the given string builder
	 * containing the navigation page.<br>
	 * 
	 * @param naviPage
	 *            string builder with the existing navigation HTML
	 */
	@SuppressWarnings("unchecked")
	private void createPluginLinks(StringBuilder naviPage) {
		naviPage.append("	<P><FONT SIZE=\"2\" FACE=\"Arial, sans-serif\">Xirp Plugin Help</FONT></P>").append(Constants.LINE_SEPARATOR); //$NON-NLS-1$
		for (IPlugable aux : plugins) {
			if (aux.getInfo( ).hasHelp( )) {
				naviPage.append(createLink("./plugins/" + aux.getName( ) + "/help.html", aux.getName( ))); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	/**
	 * This method adds the needed xirp help links to the navigation
	 * HTML-file by appending them to the given string builder
	 * containing the navigation page.
	 * 
	 * @param naviPage
	 *            string builder with the existing navigation HTML
	 */
	private void createXirpLinks(StringBuilder naviPage) {
		naviPage.append("	<P><FONT SIZE=\"2\" FACE=\"Arial, sans-serif\">Xirp Help</FONT></P>").append(Constants.LINE_SEPARATOR); //$NON-NLS-1$
		naviPage.append(createLink("./xirp/xirp_general.html", "General")); //$NON-NLS-1$//$NON-NLS-2$
		naviPage.append(createLink("./xirp/xirp_ini.html", "Configuration"));//$NON-NLS-1$//$NON-NLS-2$
		naviPage.append(createLink("./xirp/xirp_pro.html", "Profile files"));//$NON-NLS-1$//$NON-NLS-2$
		naviPage.append(createLink("./xirp/xirp_bot.html", "Robot files"));//$NON-NLS-1$//$NON-NLS-2$
		naviPage.append(createLink("./xirp/xirp_cms.html", "Comm-spec files"));//$NON-NLS-1$//$NON-NLS-2$
		naviPage.append(createLink("./xirp/xirp_workspace.html", "Workspace"));//$NON-NLS-1$//$NON-NLS-2$
		naviPage.append(createLink("./xirp/xirp_pro_edit.html",//$NON-NLS-1$
				"Profile editor"));//$NON-NLS-1$
		naviPage.append(createLink("./xirp/xirp_prefs.html", "Preferences"));//$NON-NLS-1$//$NON-NLS-2$
		naviPage.append(createLink("./xirp/xirp_report.html", "Report search"));//$NON-NLS-1$//$NON-NLS-2$
		naviPage.append(createLink("./xirp/xirp_chart.html", "Charting"));//$NON-NLS-1$//$NON-NLS-2$
		naviPage.append(createLink("./xirp/xirp_livechart.html",//$NON-NLS-1$
				"Live charting"));//$NON-NLS-1$
		naviPage.append(createLink("./xirp/xirp_record.html", "Recording"));//$NON-NLS-1$//$NON-NLS-2$
		naviPage.append(createLink("./xirp/xirp_mail.html", "Mail system"));//$NON-NLS-1$//$NON-NLS-2$
		naviPage.append(createLink("./xirp/xirp_testerbot.html", "TesterBot"));//$NON-NLS-1$//$NON-NLS-2$

	}

	/**
	 * Creates a link to the given HTML file using the given
	 * description as link name.
	 * 
	 * @param linkTo
	 *            the file to link to
	 * @param description
	 *            the description for the link
	 * @return string with a link and line separator
	 */
	private String createLink(String linkTo, String description) {
		StringBuilder buf = new StringBuilder( );
		buf.append("	<P><FONT SIZE=\"1.75\" FACE=\"Arial, sans-serif\"><A HREF=\"") //$NON-NLS-1$
				.append(linkTo)
				.append("\" TARGET=\"mainFrame\">")//$NON-NLS-1$
				.append(description)
				.append("</A></FONT></P>")//$NON-NLS-1$
				.append(Constants.LINE_SEPARATOR);
		return buf.toString( );
	}

	/**
	 * Starts the manager and creates the HTML help files.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
		this.plugins = PluginManager.getPluginSamples( );
		init( );
	}

	/**
	 * Does nothing for this manager.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
	}
}
