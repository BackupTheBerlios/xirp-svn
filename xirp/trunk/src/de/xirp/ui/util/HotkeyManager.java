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
 * HotkeyManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 02.02.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.util;

import java.util.Collections;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.ini4j.Ini.Section;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.settings.PropertiesManager;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This manager manages the hotkeys and takes care of doublets,
 * because the hotkeys can be defined by the user.
 * 
 * @author Matthias Gernand
 */
public final class HotkeyManager extends AbstractManager {

	/**
	 * The logger for this class.
	 */
	private static Logger logClass = Logger.getLogger(HotkeyManager.class);

	// TODO: reserved
	public static final Hotkey CTRL_C = null;
	public static final Hotkey CTRL_V = null;
	public static final Hotkey CTRL_X = null;

	/**
	 * Enumeration with the existing "CTRL" hotkey types.
	 * 
	 * @author Matthias Gernand
	 */
	public static enum Ctrl {
		/**
		 * To quit the program.
		 */
		PROGRAM_QUIT,
		/**
		 * To open program preferences.
		 */
		PROGRAM_PREFERENCES,
		/**
		 * To open help.
		 */
		PROGRAM_HELP,
		/**
		 * To open mail dialog.
		 */
		OPEN_MAIL,
		/**
		 * To open the chart dialog.
		 */
		CREATE_CHART,
	}

	/**
	 * Enumeration with the existing CTRL+SHIFT hotkey types.
	 * 
	 * @author Matthias Gernand
	 */
	public static enum CtrlShift {
		/**
		 * To show program info.
		 */
		PROGRAM_INFO,
	}

	/**
	 * Enumeration with the existing CTRL+ALT hotkey types.
	 * 
	 * @author Matthias Gernand
	 */
	public static enum CtrlAlt {
		/**
		 * To show plugin info.
		 */
		PLUGIN_INFO,
		/**
		 * To manage the contacts.
		 */
		MANAGE_CONTACTS,
		/**
		 * To search for reports.
		 */
		SEARCH_REPORTS
	}

	/**
	 * Flag that indicates if the scan showed some doublets in CTRL
	 */
	private static boolean doublettesCTRL = false;
	/**
	 * Flag that indicates if the scan showed some doublets in
	 * CTRLSHIFT
	 */
	private static boolean doublettesCTRLSHIFT = false;
	/**
	 * Flag that indicates if the scan showed some doublets in<br>
	 * CTRLALT
	 */
	private static boolean doublettesCTRLALT = false;
	/**
	 * {@link java.util.Vector} containing the approved
	 * {@link de.xirp.ui.util.Hotkey accelerators}.
	 * 
	 * @see de.xirp.ui.util.Hotkey
	 */
	private static Vector<Hotkey> accels = new Vector<Hotkey>( );
	/**
	 * Section from the xirp.ini provided by the
	 * {@link de.xirp.settings.PropertiesManager}.
	 * 
	 * @see de.xirp.settings.PropertiesManager
	 */
	private static Section ctrl;
	/**
	 * Section from the xirp.ini provided by the
	 * {@link de.xirp.settings.PropertiesManager}.
	 * 
	 * @see de.xirp.settings.PropertiesManager
	 */
	private static Section ctrlShift;
	/**
	 * Section from the xirp.ini provided by the
	 * {@link de.xirp.settings.PropertiesManager}.
	 * 
	 * @see de.xirp.settings.PropertiesManager
	 */
	private static Section ctrlAlt;
	/**
	 * This string contains the key which was found twice or more, if
	 * it was found more than once.
	 */
	private String errorKey = ""; //$NON-NLS-1$

	/**
	 * Constructs a new manager. <br>
	 * <br>
	 * The manager is initialized on startup. Never call this on your
	 * own. Use the statically provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance already exists.
	 */
	public HotkeyManager() throws InstantiationException {
		super( );
	}

	/**
	 * Returns a CTRL
	 * {@link de.xirp.ui.util.Hotkey hotkey}.
	 * 
	 * @param hotkeyName
	 *            The name of the hotkey.
	 * @return The new hotkey object.
	 * @see de.xirp.ui.util.Hotkey
	 */
	public static Hotkey getHotkey(Ctrl hotkeyName) {
		return getHotkey(hotkeyName.name( ));
	}

	/**
	 * Returns a CTRL+ALT
	 * {@link de.xirp.ui.util.Hotkey hotkey}.
	 * 
	 * @param hotkeyName
	 *            The name of the hotkey
	 * @return The new hotkey object.
	 * @see de.xirp.ui.util.Hotkey
	 */
	public static Hotkey getHotkey(CtrlAlt hotkeyName) {
		return getHotkey(hotkeyName.name( ));
	}

	/**
	 * Returns a CTRL+SHIFT
	 * {@link de.xirp.ui.util.Hotkey hotkey}.
	 * 
	 * @param hotkeyName
	 *            The name of the hotkey
	 * @return The new hotkey object.
	 * @see de.xirp.ui.util.Hotkey
	 */
	public static Hotkey getHotkey(CtrlShift hotkeyName) {
		return getHotkey(hotkeyName.name( ));
	}

	/**
	 * Checks the {@link de.xirp.ui.util.Hotkey hotkeys}
	 * for doublets. Returns <code>true</code> if one is found.
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>: if doublets is found.<br>
	 *         <code>false</code>: if no doublets were found.<br>
	 */
	private boolean areThereDoublets() {
		boolean doublettes = (doublettesCTRL | doublettesCTRLALT | doublettesCTRLSHIFT);
		return doublettes;
	}

	/**
	 * Returns the position where the doublets were found to keep
	 * better track of the errors.
	 * 
	 * @return A message where the doublets was found.
	 */
	private String whereAreDoublets() {
		String doublettes = ""; //$NON-NLS-1$
		if (doublettesCTRL) {
			doublettes = doublettes +
					"hotkey.CRTL.properties: " + errorKey + " "; //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (doublettesCTRLSHIFT) {
			doublettes = doublettes +
					"hotkey.CRTL+SHIFT.properties: " + errorKey + " "; //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (doublettesCTRLALT) {
			doublettes = doublettes +
					"hotkey.CRTL+ALT.properties: " + errorKey + " --> PlugIns"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return doublettes;
	}

	/**
	 * Gets the sections for the
	 * {@link de.xirp.ui.util.Hotkey hotkeys} from the
	 * {@link de.xirp.settings.PropertiesManager} and
	 * searches for doublets.
	 * 
	 * @see de.xirp.settings.PropertiesManager
	 * @see de.xirp.ui.util.Hotkey
	 */
	private void init() {
		ctrl = PropertiesManager.getCtrl( );
		ctrlShift = PropertiesManager.getCtrlShift( );
		ctrlAlt = PropertiesManager.getCtrlAlt( );

		Vector<String> control = new Vector<String>(Ctrl.values( ).length);
		Vector<String> controlshift = new Vector<String>(CtrlShift.values( ).length);
		Vector<String> controlalt = new Vector<String>(CtrlAlt.values( ).length);

		// Fill Vectors
		for (Ctrl val : Ctrl.values( )) {
			final String name = val.name( );
			final String parsed = parse(name);
			final String ctrlString = ctrl.get(parsed);
			control.add(ctrlString.toUpperCase( ));
		}
// for (int i = 0; i < Ctrl.values( ).length; i++) {
// final String name = Ctrl.values( )[i].name( );
// final String parsed = parse(name);
// final String ctrlString = ctrl.get(parsed);
// control.add(ctrlString.toUpperCase( ));
// }
		for (int i = 0; i < CtrlShift.values( ).length; i++) {
			controlshift.add((ctrlShift.get(parse(CtrlShift.values( )[i].name( )))).toUpperCase( ));
		}
		for (int i = 0; i < CtrlAlt.values( ).length; i++) {
			controlalt.add((ctrlAlt.get(parse(CtrlAlt.values( )[i].name( )))).toUpperCase( ));
		}

		// Check for doublettes, if one is found doublettes{*} ist set
		// to true
		doublettesCTRL = checkForDoublets(control);
		doublettesCTRLSHIFT = checkForDoublets(controlshift);
		doublettesCTRLALT = checkForDoublets(controlalt);

		// Now construct the Accelerators --> CTRL+A => SWT.CRTL | 'A'
		createAccelerators( );
	}

	/**
	 * Returns a {@link de.xirp.ui.util.Hotkey hotkey}
	 * for the given key name.
	 * 
	 * @param keyName
	 *            The key name.
	 * @return A new hotkey object.
	 */
	private static Hotkey getHotkey(String keyName) {

		for (Hotkey key : accels) {
			if (key.getVariableName( ).equals(keyName)) {
				return key;
			}
		}
		return null;
	}

	/**
	 * Builds the accelerators.
	 */
	private void createAccelerators() {
		String nameTmp;
		int accTmp;
		String fullNameTmp;

		char tmp;

		if (!doublettesCTRL) {
			for (int i = 0; i < Ctrl.values( ).length; i++) {
				nameTmp = Ctrl.values( )[i].name( );
				tmp = ((ctrl.get(parse(Ctrl.values( )[i].name( ))).toUpperCase( )).toCharArray( ))[0];
				accTmp = SWT.CTRL | tmp;
				fullNameTmp = I18n.getString("HotkeyManager.internal.CtrlPlus") + tmp; //$NON-NLS-1$
				accels.add(new Hotkey(nameTmp, accTmp, fullNameTmp));
				nameTmp = ""; //$NON-NLS-1$
				accTmp = ' ';
			}
		}
		if (!doublettesCTRLSHIFT) {
			for (int i = 0; i < CtrlShift.values( ).length; i++) {
				nameTmp = CtrlShift.values( )[i].name( );
				tmp = ((ctrlShift.get(parse(CtrlShift.values( )[i].name( ))).toUpperCase( )).toCharArray( ))[0];
				accTmp = SWT.CTRL | SWT.SHIFT | tmp;
				fullNameTmp = I18n.getString("HotkeyManager.internal.CtrlPlusShiftPlus") + tmp; //$NON-NLS-1$
				accels.add(new Hotkey(nameTmp, accTmp, fullNameTmp));
				nameTmp = ""; //$NON-NLS-1$
				accTmp = ' ';
			}
		}
		if (!doublettesCTRLALT) {
			for (int i = 0; i < CtrlAlt.values( ).length; i++) {
				nameTmp = CtrlAlt.values( )[i].name( );
				tmp = ((ctrlAlt.get(parse(CtrlAlt.values( )[i].name( ))).toUpperCase( )).toCharArray( ))[0];
				accTmp = SWT.CTRL | SWT.ALT | tmp;
				fullNameTmp = I18n.getString("HotkeyManager.internal.CtrlPlusAltPlus") + tmp; //$NON-NLS-1$
				accels.add(new Hotkey(nameTmp, accTmp, fullNameTmp));
				nameTmp = ""; //$NON-NLS-1$
				accTmp = ' ';
			}
		}
	}

	/**
	 * Checks if the given {@link java.util.Vector} contains doublets.
	 * 
	 * @param accels
	 *            The hotkeys.
	 * @return A <code>boolean</code><br>
	 *         <code>true</code> if the given vector contains
	 *         doublets.<br>
	 *         <code>false</code> if the given vector does not
	 *         contain doublets.<br>
	 */
	private boolean checkForDoublets(Vector<String> accels) {

		for (String auxTmp : accels) {
			int cnt = Collections.frequency(accels, auxTmp);
			if (cnt > 1) {
				errorKey = auxTmp;
				return true;
			}
		}
		return false;
	}

	/**
	 * This method converts all upper-cases to lower-cases.
	 * 
	 * @param varName
	 *            The variable name.
	 * @return The converted string.
	 */
	private String parse(String varName) {
		return varName.toLowerCase( );
	}

	/**
	 * Starts the manager and loads the
	 * {@link de.xirp.ui.util.Hotkey hotkeys}. <br>
	 * <br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * startup. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 * @throws ManagerException
	 *             if doublets are found.
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
		logClass.info(I18n.getString("HotkeyManager.log.starting")); //$NON-NLS-1$		
		logClass.info(I18n.getString("HotkeyManager.log.checking")); //$NON-NLS-1$
		init( );
		if (areThereDoublets( )) {
			logClass.info(I18n.getString("HotkeyManager.log.failed") + Constants.LINE_SEPARATOR); //$NON-NLS-1$ 
			logClass.info(I18n.getString("HotkeyManager.log.foundDoublettes")); //$NON-NLS-1$
			logClass.info(I18n.getString("HotkeyManager.log.lookAt") + whereAreDoublets( ) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			throw new ManagerException(new HotkeyException(I18n.getString("HotkeyManager.log.foundDoublettesIn") //$NON-NLS-1$
					+
					whereAreDoublets( )));
		}

		logClass.info(I18n.getString("HotkeyManager.log.finished") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
	}

	/**
	 * Does nothing. <br>
	 * <br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * shutdown. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
	}
}
