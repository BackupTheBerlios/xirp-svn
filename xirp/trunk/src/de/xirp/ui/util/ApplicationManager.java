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
 * ApplicationManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 14.07.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.plugin.PluginManager;
import de.xirp.profile.CommunicationSpecification;
import de.xirp.profile.Profile;
import de.xirp.profile.Robot;
import de.xirp.settings.PropertiesManager;
import de.xirp.testerbot.TesterBotGUI;
import de.xirp.ui.Application;
import de.xirp.ui.event.*;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.ui.widgets.dialogs.*;
import de.xirp.ui.widgets.dialogs.XMessageBox.HMessageBoxType;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Variables;

/**
 * The class contains static methods which will be called by different
 * parts of the application. Like opening the quit dialog and so on.
 * 
 * @author Matthias Gernand
 */
public final class ApplicationManager extends AbstractManager {

	/**
	 * The logger of this class
	 */
	private static final Logger logClass = Logger.getLogger(ApplicationManager.class);
	/**
	 * A List of appearance change listeners.
	 */
	private static List<AppearanceChangedListener> appearanceListeners = new ArrayList<AppearanceChangedListener>( );
	/**
	 * A List of appearance change listeners.
	 */
	private static List<LocaleChangedListener> i18nListeners = new ArrayList<LocaleChangedListener>( );
	/**
	 * A List of content change listeners.
	 */
	private static List<ContentChangedListener> contentListeners = new ArrayList<ContentChangedListener>( );
	/**
	 * A List of voice change listeners.
	 */
	private static List<VoiceChangedListener> voiceListeners = new ArrayList<VoiceChangedListener>( );

	/**
	 * Constructs a new application manager. The manager is
	 * initialized on startup. Never call this on your own. Use the
	 * statically provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	public ApplicationManager() throws InstantiationException {
		super( );
	}

	/**
	 * Adds a content change listener to the list of listeners of the
	 * application. This listener is notified when another tab is
	 * activated and so the robot changed.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public static void addContentChangedListener(ContentChangedListener listener) {
		contentListeners.add(listener);
	}

	/**
	 * Removes a content change listener from the list of listeners of
	 * the application.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public static void removeContentChangedListener(
			ContentChangedListener listener) {
		contentListeners.remove(listener);
	}

	/**
	 * This method is called, when a tab was switched. All receivers
	 * of the content change listener event will be notified of this
	 * change.
	 */
	public static void contentChanged() {
		fireContentChangedEvent(new ContentChangedEvent(Application.getApplication( ),
				getCurrentProfile( ),
				getCurrentRobot( )));
	}

	/**
	 * All receivers of the content change listener will be notified
	 * of the change and will receive the event.
	 * 
	 * @param event
	 *            The event to fire.
	 */
	private static void fireContentChangedEvent(ContentChangedEvent event) {
		for (ContentChangedListener li : contentListeners) {
			li.contentChanged(event);
		}
	}

	/**
	 * Adds a voice change listener to the list of listeners of the
	 * application. This listener is notified when the voice which is
	 * used for text-to-speech output changes.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public static void addVoiceChangedListener(VoiceChangedListener listener) {
		voiceListeners.add(listener);
	}

	/**
	 * Removes a voice change listener from the list of listeners of
	 * the application.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public static void removeVoiceChangedListener(VoiceChangedListener listener) {
		voiceListeners.remove(listener);
	}

	/**
	 * This method is called, when the voice changed. All receivers of
	 * the voice change listener event will be notified of this
	 * change.
	 */
	public static void voiceChanged() {
		fireVoiceChangedEvent(new VoiceChangedEvent(Application.getApplication( ),
				PropertiesManager.getTTSVoice( )));
	}

	/**
	 * All receivers of the voice change listener will be notified of
	 * the change and will receive the event.
	 * 
	 * @param event
	 *            The event to fire.
	 */
	private static void fireVoiceChangedEvent(VoiceChangedEvent event) {
		for (VoiceChangedListener li : voiceListeners) {
			li.voiceChanged(event);
		}
	}

	/**
	 * Adds a locale change listener to the list of listeners of the
	 * application. This listener is notified when the locale which is
	 * used in the application changes.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public static void addLocaleChangedListener(LocaleChangedListener listener) {
		i18nListeners.add(listener);
	}

	/**
	 * Removes the locale change listener from the list of listeners
	 * of the application.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public static void removeLocaleChangedListener(
			LocaleChangedListener listener) {
		i18nListeners.remove(listener);
	}

	/**
	 * Sets the current locale and informs all listeners and plugin
	 * that the locale has changed.
	 */
	public static void localeChanged() {
		I18n.setLocale(PropertiesManager.getLocale( ));
		PluginManager.setLocale(I18n.getLocale( ));
		fireLocaleChangedEvent(new LocaleChangedEvent(Application.getApplication( )));
	}

	/**
	 * Informs all listeners that the locale has changed.
	 * 
	 * @param event
	 *            the locale change event
	 */
	private static void fireLocaleChangedEvent(LocaleChangedEvent event) {
		for (LocaleChangedListener li : i18nListeners) {
			li.localeChanged(event);
		}
	}

	/**
	 * Adds a appearance change listener to the list of listeners of
	 * the application. This listener is notified if the look and feel
	 * (the colors) of the application change.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public static void addAppearanceChangedListener(
			AppearanceChangedListener listener) {
		appearanceListeners.add(listener);
	}

	/**
	 * Removes a appearance change listener from the list of listeners
	 * of the application.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public static void removeAppearanceChangedListener(
			AppearanceChangedListener listener) {
		appearanceListeners.remove(listener);
	}

	/**
	 * This method is called, when the appearance changed. All
	 * receivers of the appearance change listener event will be
	 * notified of this change.
	 */
	public static void appearanceChanged() {
		Variables.reloadVariables( );
		fireAppearanceChangedEvent(new AppearanceChangedEvent(Application.getApplication( )));
	}

	/**
	 * All receivers of the appearance change listener will be
	 * notified of the change and will receive the event.
	 * 
	 * @param event
	 *            The event to fire.
	 */
	private static void fireAppearanceChangedEvent(AppearanceChangedEvent event) {
		for (AppearanceChangedListener li : appearanceListeners) {
			li.appearanceChanged(event);
		}
	}

	/**
	 * Is called when the shell is called to close. A dialog is opened
	 * and asks the user if he or she really wants to quit the
	 * application. <br>
	 * <br>
	 * If [YES] is clicked the method returns <code>true</code>.
	 * This flag is passed through to the ShellClosedListener to
	 * e.doit.
	 * 
	 * @return boolean <br>
	 *         <code>true</code> closes the application<br>
	 *         <code>false</code> does not close the application
	 * @see de.xirp.ui.widgets.dialogs.XMessageBox
	 */
	public static boolean showQuitDialog() {
		XMessageBox box = new XMessageBox(Application.getApplication( )
				.getShell( ),
				HMessageBoxType.QUESTION,
				XButtonType.YES,
				XButtonType.NO);
		box.setTextForLocaleKey("QuitDialog.gui.title"); //$NON-NLS-1$
		box.setMessageForLocaleKey("QuitDialog.gui.message"); //$NON-NLS-1$

		XButtonType type = box.open( );
		if (type.equals(XButtonType.YES)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Opens the preferences dialog.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.PreferencesDialog
	 */
	public static void showPreferencesDialog() {
		PreferencesDialog prefs = new PreferencesDialog(Application.getApplication( )
				.getShell( ));
		prefs.open( );
	}

	/**
	 * Opens the about application dialog or the about plugins dialog.
	 * 
	 * @param aboutApplication
	 *            <code>true</code> about plugins info is shown<br>
	 *            <code>false</code> about application info is shown<br>
	 * @see de.xirp.ui.widgets.dialogs.AboutDialog
	 * @see de.xirp.ui.widgets.dialogs.AboutPluginsDialog
	 */
	public static void showAboutDialog(boolean aboutApplication) {
		if (aboutApplication) {
			AboutDialog about = new AboutDialog(Application.getApplication( )
					.getShell( ));
			about.open( );
		}
		else {
			AboutPluginsDialog about = new AboutPluginsDialog(Application.getApplication( )
					.getShell( ));
			about.open( );
		}
	}

	/**
	 * Opens the dialog with the help of the application and plugins.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.HelpDialog
	 */
	public static void showHelpDialog() {
		HelpDialog help = new HelpDialog(Application.getApplication( )
				.getShell( ));
		help.open( );
	}

	/**
	 * Opens the dialog which allows to search for saved reports.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.ReportSearchDialog
	 */
	public static void showReportSearchDialog() {
		ReportSearchDialog rsd = new ReportSearchDialog(Application.getApplication( )
				.getShell( ));
		try {
			rsd.open( );
		}
		catch (RuntimeException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Shows the dialog which asks the user if a connection to a robot
	 * should be terminated.
	 * 
	 * @return <code>true</code> if the connection to the robot
	 *         should be closed.<br>
	 *         <code>false</code> if the connection to the robot
	 *         should not be closed.
	 */
	public static boolean showDisconnectDialog() {
		XMessageBox box = new XMessageBox(Application.getApplication( )
				.getShell( ),
				HMessageBoxType.QUESTION,
				XButtonType.YES,
				XButtonType.NO);
		box.setTextForLocaleKey("DisconnectDialog.gui.dialogTitle"); //$NON-NLS-1$
		box.setMessageForLocaleKey("DisconnectDialog.gui.message"); //$NON-NLS-1$

		XButtonType type = box.open( );
		if (type.equals(XButtonType.YES)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Opens the dialog which allows to write and manager mails.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.MailDialog
	 */
	public static void showMailDialog() {
		MailDialog dialog = new MailDialog(Application.getApplication( )
				.getShell( ));
		dialog.open( );
	}

	/**
	 * Opens the dialog which allows to manager contacts which are
	 * used to send mails.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.ContactDialog
	 */
	public static void showContactDialog() {
		ContactDialog dialog = new ContactDialog(Application.getApplication( )
				.getShell( ));
		dialog.open( );
	}

	/**
	 * Opens the dialog which allows to create a new profile.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.ProfileDialog
	 */
	public static void showNewProfileDialog() {
		ProfileDialog profile = new ProfileDialog(Application.getApplication( )
				.getShell( ));
		profile.open( );
	}

	/**
	 * Opens the dialog which allows to create a edit the given
	 * profile.
	 * 
	 * @param profile
	 *            the profile to edit
	 * @see de.xirp.ui.widgets.dialogs.ProfileDialog
	 */
	public static void showEditProfileDialog(Profile profile) {
		ProfileDialog dialog = new ProfileDialog(Application.getApplication( )
				.getShell( ), profile);
		dialog.open( );
	}

	/**
	 * Opens the dialog which allows to create a new robot.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.RobotDialog
	 */
	public static void showNewRobotDialog() {
		RobotDialog profile = new RobotDialog(Application.getApplication( )
				.getShell( ));
		profile.open( );
	}

	/**
	 * Opens the dialog which allows to edit the given robot.
	 * 
	 * @param robot
	 *            the robot to edit.
	 * @see de.xirp.ui.widgets.dialogs.RobotDialog
	 */
	public static void showEditRobotDialog(Robot robot) {
		RobotDialog dialog = new RobotDialog(Application.getApplication( )
				.getShell( ), robot);
		dialog.open( );
	}

	/**
	 * Opens the dialog which allows to create new communication
	 * specifications.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.CommSpecDialog
	 */
	public static void showNewCommSpecDialog() {
		CommSpecDialog profile = new CommSpecDialog(Application.getApplication( )
				.getShell( ));
		profile.open( );
	}

	/**
	 * Opens the dialog which allows to edit the given communication
	 * specifications.
	 * 
	 * @param commSpec
	 *            the communication specifications to edit
	 * @see de.xirp.ui.widgets.dialogs.CommSpecDialog
	 */
	public static void showEditCommSpecDialog(
			CommunicationSpecification commSpec) {
		CommSpecDialog dialog = new CommSpecDialog(Application.getApplication( )
				.getShell( ), commSpec);
		dialog.open( );
	}

	/**
	 * Opens the dialog which allows to specify how a generated chart
	 * of data in the database should look.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.ChartConfigDialog
	 */
	public static void showChartDialog() {
		ChartConfigDialog chart = new ChartConfigDialog(Application.getApplication( )
				.getShell( ));
		try {
			chart.open( );
		}
		catch (RuntimeException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the currently active profile. The current profile is
	 * the profile, which is the one of the active tab.
	 * 
	 * @return The current profile.
	 */
	public static Profile getCurrentProfile() {
		return Application.getApplication( )
				.getAppContent( )
				.getCurrentProfile( );
	}

	/**
	 * Returns the currently active robot. The current robot is the
	 * robot, which is the one of the active tab.
	 * 
	 * @return The current robot.
	 */
	public static Robot getCurrentRobot() {
		return Application.getApplication( ).getAppContent( ).getCurrentRobot( );
	}

	/**
	 * Opens the dialog which allows to control the testerbot.
	 * 
	 * @see de.xirp.testerbot.TesterBotGUI
	 */
	public static void showTesterBotDialog() {
		TesterBotGUI tbg = new TesterBotGUI(Application.getApplication( )
				.getShell( ));
		tbg.open( );
	}

	/**
	 * Starts the application manager.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
	}

	/**
	 * Stops the application manager and clears all listener lists.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
		appearanceListeners.clear( );
		i18nListeners.clear( );
		contentListeners.clear( );
		voiceListeners.clear( );
	}
}
