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
 * PreferencesDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 20.01.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.swtplus.widgets.PList;
import com.swtplus.widgets.PListItem;

import de.xirp.plugin.IPlugable;
import de.xirp.plugin.PluginManager;
import de.xirp.profile.ProfileManager;
import de.xirp.profile.Robot;
import de.xirp.settings.PropertiesManager;
import de.xirp.settings.Settings;
import de.xirp.settings.SettingsChangedEvent;
import de.xirp.settings.SettingsChangedListener;
import de.xirp.ui.Application;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XPList;
import de.xirp.ui.widgets.custom.XPListItem;
import de.xirp.ui.widgets.custom.XSashForm;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.ui.widgets.dialogs.XMessageBox.HMessageBoxType;
import de.xirp.ui.widgets.dialogs.preferences.*;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This dialog is used to set the systems and plugins settings.
 * 
 * @author Matthias Gernand
 */
public final class PreferencesDialog extends XDialog {

	/**
	 * The logger for this class.
	 */
	private static Logger logClass = Logger.getLogger(PreferencesDialog.class);
	/**
	 * Width of the dialog.
	 */
	private static final int WIDTH = 700;
	/**
	 * Height of the dialog.
	 */
	private static final int HEIGHT = 575;
	/**
	 * The parent shell.
	 */
	private Shell parent;
	/**
	 * The dialogs shell.
	 */
	private XShell dialogShell;
	/**
	 * A composite.
	 */
	private XComposite below;
	/**
	 * The content composite.
	 */
	private XComposite content;
	/**
	 * A stack layout.
	 */
	private StackLayout sl;
	/**
	 * The folder for general settings.
	 */
	private GeneralFolder generalFolder;
	/**
	 * The folder for appearance settings.
	 */
	private AppearanceFolder appearanceFolder;
	/**
	 * The folder for language settings.
	 */
	private LanguageFolder languageFolder;
	/**
	 * The folder for hot key settings.
	 */
	private HotkeysFolder hotkeyFolder;
	/**
	 * The folder for DB settings.
	 */
	private DBFolder dbFolder;
	/**
	 * A vector containing the folders.
	 */
	private Vector<AbstractContentFolderComposite> internalFolders = new Vector<AbstractContentFolderComposite>( );
	/**
	 * A vector containing the generic folders.
	 */
	private Vector<Object> genericFolders = new Vector<Object>( );
	/**
	 * A list object.
	 */
	private XPList pList;
	/**
	 * A vector containing the plugin-items of the list.
	 */
	private Vector<PListItem> pluginsItems = new Vector<PListItem>( );
	/**
	 * A vector containing the internal-items of the list.
	 */
	private Vector<PListItem> internalItems = new Vector<PListItem>( );
	/**
	 * Flag if changes should be saved on closing.
	 */
	private boolean doSave = false;
	/**
	 * The sash form containing the index and content.
	 */
	private XSashForm form;
	/**
	 * The reset button.
	 */
	private XButton reset;
	/**
	 * The restore defaults button.
	 */
	private XButton defaults;
	/**
	 * The apply button.
	 */
	private XButton apply;
	/**
	 * The folder for external tools settings.
	 */
	private ExternalProgramStartupFolder externalProgFolder;
	/**
	 * The folder for mail settings.
	 */
	private MailFolder mailFolder;
	/**
	 * The folder for speech settings.
	 */
	private SpeechFolder speechFolder;
	/**
	 * The folder for chart settings.
	 */
	private ChartFolder chartFolder;

	/**
	 * Creates a preferences dialog for xirp and plugin settings.
	 * 
	 * @param parent
	 *            The parent shell.
	 */
	public PreferencesDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
	}

	/**
	 * Opens the preferences dialog.
	 */
	public void open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				if (doSave) {
					save( );
				}
				else {
					if (!askAndSave( )) {
						logClass.debug("Preferences not saved." + Constants.LINE_SEPARATOR); //$NON-NLS-1$
					}
				}
				SWTUtil.secureDispose(dialogShell);
			}
		});

		dialogShell.setTextForLocaleKey("Application.gui.dialog.title.prefs"); //$NON-NLS-1$
		dialogShell.setSize(WIDTH, HEIGHT);

		dialogShell.setImage(ImageManager.getSystemImage(SystemImage.PREFERENCES));

		SWTUtil.setGridLayout(dialogShell, 1, true);

		form = new XSashForm(dialogShell, SWT.HORIZONTAL | SWT.SMOOTH);
		form.SASH_WIDTH = 3;
		SWTUtil.setGridData(form, true, true, SWT.FILL, SWT.FILL, 1, 1);
		int x[] = new int[2];

		createPList( );
		createContent( );
		createPListItems( );
		createHButtons( );

		x[0] = 25;
		x[1] = 75;
		form.setWeights(x);
		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );
	}

	/**
	 * Creates the navigation list on the left side.
	 */
	private void createPList() {
		/*
		 * Use 16x16 icons in XPList!
		 */
		pList = new XPList(form, PList.BORDER, XPList.ListType.SIMPLE);
		SWTUtil.setGridData(pList, true, true, SWT.FILL, SWT.FILL, 1, 1);
	}

	/**
	 * Creates the content area on the right.
	 */
	private void createContent() {
		content = new XComposite(form, SWT.NONE);
		SWTUtil.setGridData(content, true, true, SWT.FILL, SWT.FILL, 1, 1);

		generalFolder = new GeneralFolder(content);
		generalFolder.setTitleKey("PreferencesDialog.gui.general.title"); //$NON-NLS-1$
		generalFolder.setImagePath(Constants.SYSTEM_ICON_DIR + File.separator +
				"general.png"); //$NON-NLS-1$
		appearanceFolder = new AppearanceFolder(content);
		appearanceFolder.setTitleKey("PreferencesDialog.gui.appearance.title"); //$NON-NLS-1$
		appearanceFolder.setImagePath(Constants.SYSTEM_ICON_DIR +
				File.separator + "appearance.png"); //$NON-NLS-1$
		languageFolder = new LanguageFolder(content);
		languageFolder.setTitleKey("PreferencesDialog.gui.language.title"); //$NON-NLS-1$
		languageFolder.setImagePath(Constants.SYSTEM_ICON_DIR + File.separator +
				"language.png"); //$NON-NLS-1$
		hotkeyFolder = new HotkeysFolder(content);
		hotkeyFolder.setTitleKey("PreferencesDialog.gui.shortcuts.title"); //$NON-NLS-1$
		hotkeyFolder.setImagePath(Constants.SYSTEM_ICON_DIR + File.separator +
				"keybindings.png"); //$NON-NLS-1$
		externalProgFolder = new ExternalProgramStartupFolder(content);
		externalProgFolder.setTitleKey("PreferencesDialog.gui.externalProg.title"); //$NON-NLS-1$
		externalProgFolder.setImagePath(Constants.SYSTEM_ICON_DIR +
				File.separator + "external_programs.png"); //$NON-NLS-1$
		dbFolder = new DBFolder(content);
		dbFolder.setTitleKey("PreferencesDialog.gui.db.title"); //$NON-NLS-1$
		dbFolder.setImagePath(Constants.SYSTEM_ICON_DIR + File.separator +
				"database.png"); //$NON-NLS-1$
		mailFolder = new MailFolder(content);
		mailFolder.setTitleKey("PreferencesDialog.gui.mail.title"); //$NON-NLS-1$
		mailFolder.setImagePath(Constants.SYSTEM_ICON_DIR + File.separator +
				"mail.png"); //$NON-NLS-1$
		speechFolder = new SpeechFolder(content);
		speechFolder.setTitleKey("PreferencesDialog.gui.speech.title"); //$NON-NLS-1$
		speechFolder.setImagePath(Constants.SYSTEM_ICON_DIR + File.separator +
				"speech.png"); //$NON-NLS-1$
		chartFolder = new ChartFolder(content);
		chartFolder.setTitleKey("PreferencesDialog.gui.chart.title"); //$NON-NLS-1$
		chartFolder.setImagePath(Constants.SYSTEM_ICON_DIR + File.separator +
				"chart_small.png"); //$NON-NLS-1$

		internalFolders.add(generalFolder);
		internalFolders.add(appearanceFolder);
		internalFolders.add(languageFolder);
		internalFolders.add(hotkeyFolder);
		internalFolders.add(externalProgFolder);
		internalFolders.add(dbFolder);
		internalFolders.add(mailFolder);
		internalFolders.add(speechFolder);
		internalFolders.add(chartFolder);

		// Robot plugins
		for (Robot robot : ProfileManager.getRobots( )) {
			genericFolders.add(robot);
			for (IPlugable plugin : PluginManager.getPlugins(robot.getName( ))) {
				Settings settings = null;
				try {
					settings = plugin.getSettings( );
				}
				catch (Exception e) {
					logClass.warn(I18n.getString("PreferencesDialog.log.pageNotCreated", //$NON-NLS-1$
							(plugin.getName( ) + Constants.LINE_SEPARATOR),
							(e.toString( ) + Constants.LINE_SEPARATOR)),
							e);
				}
				if (settings != null) {
					GenericFolder genFold = new GenericFolder(content, settings);
					String name = plugin.getNameKey( );
					genFold.setTitleKey(name);
					genFold.setImagePath(Constants.SYSTEM_ICON_DIR +
							File.separator + "puzzle.png"); //$NON-NLS-1$
					genFold.setHandler(plugin.getHandler( ));
					genericFolders.add(genFold);
				}
			}
		}

		sl = new StackLayout( );
		sl.topControl = null;
		content.setLayout(sl);
	}

	/**
	 * Creates the items in the list on the left side.
	 */
	private void createPListItems() {

		for (AbstractContentFolderComposite aux : internalFolders) {
			XPListItem tmp = new XPListItem(pList, SWT.NONE);
			tmp.setTextForLocaleKey(aux.getTitleKey( ));
			tmp.setImage(ImageManager.getFullPathImage(aux.getImagePath( )));
			tmp.setData(aux);
			internalItems.add(tmp);
		}

		for (Object aux : genericFolders) {
			if (aux instanceof Robot) {
				// Separator item
				new PListItem(pList, SWT.NONE);

				XPListItem tmp = new XPListItem(pList, SWT.NONE);
				tmp.setTextForLocaleKey("PreferencesDialog.item.text", ((Robot) aux).getName( )); //$NON-NLS-1$
				tmp.setImage(ImageManager.getSystemImage(SystemImage.ROBOT_HEAD));
			}
			if (aux instanceof GenericFolder) {
				GenericFolder genericFolder = (GenericFolder) aux;
				if (genericFolder.getSettings( ) != null) {
					XPListItem tmp = new XPListItem(pList,
							SWT.NONE,
							genericFolder.getHandler( ));
					tmp.setTextForLocaleKey(genericFolder.getTitleKey( ));
					tmp.setImage(ImageManager.getFullPathImage(genericFolder.getImagePath( )));
					tmp.setData(aux);
					pluginsItems.add(tmp);
				}
			}
			if (aux instanceof String) {
				// Separator item
				new PListItem(pList, SWT.NONE);

				XPListItem tmp = new XPListItem(pList, SWT.NONE);
				tmp.setText((String) aux);
				tmp.setImage(ImageManager.getSystemImage(SystemImage.ROBOT_HEAD));
			}
		}
		pList.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (e.item instanceof PListItem) {
					setTopControl((AbstractContentFolderComposite) ((PListItem) e.item).getData( ));
				}
			}
		});
	}

	/**
	 * Creates the save and close buttons below.
	 */
	private void createHButtons() {
		below = new XComposite(dialogShell, SWT.NONE);

		SWTUtil.setGridData(below, true, false, SWT.FILL, SWT.FILL, 2, 1);
		SWTUtil.setGridLayout(below, 5, true);

		XButton ok = new XButton(below, XButtonType.OK);
		SWTUtil.setGridData(ok, true, false, SWT.FILL, SWT.CENTER, 1, 1);
		ok.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				doSave = true;
				dialogShell.close( );
			}
		});

		apply = new XButton(below, XButtonType.APPLY);
		apply.setEnabled(false);
		SWTUtil.setGridData(apply, true, true, SWT.FILL, SWT.FILL, 1, 1);
		apply.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					boolean done = false;

					public void run() {
						Thread thread = new Thread(new Runnable( ) {

							public void run() {
								SWTUtil.asyncExec(new Runnable( ) {

									public void run() {
										save( );
									}
								});

								if (parent.getDisplay( ).isDisposed( )) {
									return;
								}

								done = true;
								parent.getDisplay( ).wake( );
							}
						}, "PrefsSave"); //$NON-NLS-1$
						thread.start( );
						while (!done && !parent.getShell( ).isDisposed( )) {
							if (!parent.getDisplay( ).readAndDispatch( )) {
								parent.getDisplay( ).sleep( );
							}
						}
					}
				};

				BusyIndicator.showWhile(parent.getDisplay( ), runnable);
			}
		});
		reset = new XButton(below, XButtonType.RESET);
		reset.setEnabled(false);
		SWTUtil.setGridData(reset, true, false, SWT.FILL, SWT.CENTER, 1, 1);
		reset.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				reset( );
			}
		});
		defaults = new XButton(below, XButtonType.DEFAULT);
		defaults.setEnabled(false);
		SWTUtil.setGridData(defaults, true, false, SWT.FILL, SWT.CENTER, 1, 1);
		defaults.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				defaults( );
			}
		});

		XButton cancel = new XButton(below, XButtonType.CANCEL);
		SWTUtil.setGridData(cancel, true, false, SWT.FILL, SWT.CENTER, 1, 1);
		cancel.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				doSave = false;
				dialogShell.close( );
			}
		});
	}

	/**
	 * Returns the currently active folder.
	 * 
	 * @return The currently active folder or <code>null</code> if
	 *         no folder is selected.
	 */
	private AbstractContentFolderComposite getTopControl() {
		Control control = sl.topControl;
		if (control instanceof AbstractContentFolderComposite) {
			AbstractContentFolderComposite abs = (AbstractContentFolderComposite) control;
			return abs;
		}
		return null;
	}

	/**
	 * Asks the user if his changes, if some were made, should be
	 * saved or not.
	 * 
	 * @return <code>true</code> if the changes were saved.
	 */
	private boolean askAndSave() {
		// get the old top control and check if it needs to be saved
		AbstractContentFolderComposite abs = getTopControl( );
		if (abs != null) {
			if (abs.hasChanged( )) {
				XMessageBox box = new XMessageBox(Application.getApplication( )
						.getShell( ),
						HMessageBoxType.QUESTION,
						XButtonType.YES,
						XButtonType.NO);
				box.setTextForLocaleKey("PreferencesDialog.messagebox.title"); //$NON-NLS-1$
				box.setMessageForLocaleKey("PreferencesDialog.messagebox.message"); //$NON-NLS-1$
				boolean save;
				XButtonType type = box.open( );
				if (type.equals(XButtonType.YES)) {
					save = true;
				}
				else {
					save = false;
				}
				// save or leave as it is
				if (save) {
					save( );
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Sets the top control of the stack layout used to show the
	 * folders on the right and checks if the old top folder needs to
	 * be saved.
	 * 
	 * @param folder
	 *            The folder to be shown.
	 */
	private void setTopControl(final AbstractContentFolderComposite folder) {
		askAndSave( );

		sl.topControl = folder;
		// check if reset and restore defaults is supported
		// and update the buttons accordingly
		this.reset.setEnabled(false);
		this.apply.setEnabled(false);
		this.defaults.setEnabled(false);
		if (folder != null) {
			this.defaults.setEnabled(folder.supportsDefaults( ) &&
					!folder.isDefaultSelected( ));

			folder.addSettingsChangeListener(new SettingsChangedListener( ) {

				public void settingsChanged(SettingsChangedEvent event) {
					boolean resetFlag = false;
					boolean applyFlag = false;
					boolean defaultFlag = folder.supportsDefaults( ) &&
							!folder.isDefaultSelected( );
					if (folder.hasChanged( )) {
						if (folder.supportsReset( )) {
							resetFlag = true;
						}
						applyFlag = true;
					}
					if (SWTUtil.swtAssert(apply)) {
						apply.setEnabled(applyFlag);
					}
					if (SWTUtil.swtAssert(reset)) {
						reset.setEnabled(resetFlag);
					}
					if (SWTUtil.swtAssert(defaults)) {
						defaults.setEnabled(defaultFlag);
					}
				}

			});
		}
		content.layout( );
	}

	/**
	 * Saves the changes made to the settings in the current folder.
	 */
	private void save() {
		logClass.info(I18n.getString("PreferencesDialog.log.saving")); //$NON-NLS-1$
		// get the current folder
		AbstractContentFolderComposite abs = getTopControl( );
		if (abs != null) {
			// if there were any changes made save them and
			// update appearance and language information
			// if needed
			boolean hasChanged = abs.hasChanged( );
			if (hasChanged) {
				abs.save( );
				if (!(abs instanceof GenericFolder)) {
					PropertiesManager.store( );
					if (abs instanceof LanguageFolder) {
						ApplicationManager.localeChanged( );
					}
					else if (abs instanceof AppearanceFolder) {
						ApplicationManager.appearanceChanged( );
					}
				}
			}

			this.reset.setEnabled(false);
			this.apply.setEnabled(false);
		}
		logClass.info(I18n.getString("PreferencesDialog.log.finished") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
	}

	/**
	 * Resets the changes made to the settings in the folders to the
	 * saved settings.
	 */
	private void reset() {
		AbstractContentFolderComposite abs = getTopControl( );
		if (abs != null) {
			abs.reset( );
		}
	}

	/**
	 * Resets the changes made to the settings in the folders to the
	 * default settings.
	 */
	private void defaults() {
		AbstractContentFolderComposite abs = getTopControl( );
		if (abs != null) {
			abs.restoreDefaults( );
		}
	}
}
