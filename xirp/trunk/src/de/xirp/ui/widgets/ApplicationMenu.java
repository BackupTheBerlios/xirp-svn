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
 * ApplicationMenu.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 20.02.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import de.xirp.io.comm.CommunicationManager;
import de.xirp.managers.ExternalProgram;
import de.xirp.managers.ExternalProgramManager;
import de.xirp.plugin.IPlugable;
import de.xirp.plugin.IPluginFilter;
import de.xirp.plugin.PluginManager;
import de.xirp.plugin.PluginType;
import de.xirp.plugin.VisualizationType;
import de.xirp.profile.*;
import de.xirp.report.Report;
import de.xirp.report.ReportGenerator;
import de.xirp.settings.PropertiesManager;
import de.xirp.ui.Application;
import de.xirp.ui.event.ContentChangedEvent;
import de.xirp.ui.event.ContentChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.Hotkey;
import de.xirp.ui.util.HotkeyManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.HotkeyManager.Ctrl;
import de.xirp.ui.util.HotkeyManager.CtrlAlt;
import de.xirp.ui.util.HotkeyManager.CtrlShift;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XMenuItem;
import de.xirp.ui.widgets.dialogs.CommSpecsLookupDialog;
import de.xirp.ui.widgets.dialogs.ProfileLookupDialog;
import de.xirp.ui.widgets.dialogs.RobotLookupDialog;
import de.xirp.ui.widgets.panels.RobotPanel;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Variables;

/**
 * This user interface class represents the applications menu.
 * 
 * @author Matthias Gernand
 */
public final class ApplicationMenu {

	/**
	 * The logger for this class.
	 */
	private static Logger logClass = Logger.getLogger(ApplicationMenu.class);
	/**
	 * A string constant.
	 */
	public static final String DATA_IS_TOP_MENU = "isTop"; //$NON-NLS-1$
	/**
	 * A string constant.
	 */
	private static final String PROFILE = "profile"; //$NON-NLS-1$
	/**
	 * A string constant.
	 */
	private static final String BOT = "bot"; //$NON-NLS-1$
	/**
	 * A string constant.
	 */
	private static final String TOOL = "tool"; //$NON-NLS-1$
	/**
	 * The parent shell.
	 */
	private Shell parent = null;
	/**
	 * The application itself.
	 */
	private Application application = null;
	/**
	 * A hot-key object.
	 */
	private Hotkey hotkey = null;
	/**
	 * An image object.
	 */
	private Image image = null;
	/**
	 * A menu item.
	 */
	private XMenuItem subMenuViewItemTools = null;
	/**
	 * A menu item.
	 */
	private XMenuItem menuBarItemPlugins = null;
	/**
	 * A menu item.
	 */
	private XMenuItem subMenuViewItemControlTab = null;
	/**
	 * A menu item.
	 */
	private XMenuItem menuBarItemReports = null;
	/**
	 * A menu item.
	 */
	private XMenuItem subMenuViewItemLocked = null;
	/**
	 * The plugins menu.
	 */
	private Menu subMenuPlugin = null;
	/**
	 * The reports menu.
	 */
	private Menu subMenuReports = null;
	/**
	 * A content change listener.
	 * 
	 * @see de.xirp.ui.event.ContentChangedListener
	 */
	private ContentChangedListener contentListener = null;
	/**
	 * A flag indicating the connection status of the robot.
	 */
	boolean disconnect = true;
	/**
	 * The robots menu items.
	 */
	private Map<String, XMenuItem> robotItems = new HashMap<String, XMenuItem>( );
	/**
	 * A menu item.
	 */
	private XMenuItem menuBarItemRobotProfile;
	/**
	 * A menu item.
	 */
	private XMenuItem menuBarItemExternalTools;
	/**
	 * A menu.
	 */
	private Menu subMenuExternalTools;
	/**
	 * A menu item.
	 */
	private XMenuItem menuBarItemView;

	/**
	 * Constructs a new application menu.
	 */
	public ApplicationMenu() {
		this.application = Application.getApplication( );
		this.parent = application.getShell( );
		init( );
	}

	/**
	 * Initializes the applications menu.
	 */
	private void init() {
		contentListener = new ContentChangedListener( ) {

			public void contentChanged(ContentChangedEvent event) {
				rearrangeMenu(event.getProfile( ), event.getRobot( ));
			}

		};

		ApplicationManager.addContentChangedListener(contentListener);

		Menu menuBar = new Menu(parent, SWT.BAR);
		menuBar.setData(DATA_IS_TOP_MENU, Boolean.TRUE);
		menuBar.addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent e) {
				ApplicationManager.removeContentChangedListener(contentListener);
				for (MenuItem item : menuBarItemView.getMenu( ).getItems( )) {
					if (item instanceof XMenuItem) {
						XMenuItem x = (XMenuItem) item;
						String textLocaleKey = x.getTextLocaleKey( );
						if (textLocaleKey != null) {
							PropertiesManager.setViewItem(textLocaleKey,
									x.getSelection( ));
						}
					}
				}
				PropertiesManager.store( );
			}

		});

		parent.setMenuBar(menuBar);

		// File
		createFileMenu(menuBar);

		// View
		createViewMenu(menuBar);

		// Robots
		menuBarItemRobotProfile = new XMenuItem(menuBar, SWT.CASCADE);
		menuBarItemRobotProfile.setTextForLocaleKey("Application.gui.menu.robotprofile"); //$NON-NLS-1$
		Menu subMenuProfile = new Menu(menuBarItemRobotProfile);

		// Robots --> Alle vorhandenen robot profile
		for (Profile profile : ProfileManager.getProfiles( )) {
			// top-level-cascading
			XMenuItem itm = new XMenuItem(subMenuProfile, SWT.CASCADE);
			itm.setText(profile.getName( ));
			Menu robots = new Menu(itm);

			for (Robot robot : profile.getRobots( )) {
				XMenuItem robotItm = new XMenuItem(robots, SWT.CHECK);
				robotItm.setText("&" + robot.getName( )); //$NON-NLS-1$
				robotItm.setData(BOT, robot);
				robotItm.setData(PROFILE, profile);
				// TODO
				robotItm.setSelection(true/* PropertiesManager.isCreateTabsOnStartup( ) */);
				robotItm.addSelectionListener(new SelectionAdapter( ) {

					@Override
					public void widgetSelected(SelectionEvent e) {
						final XMenuItem i = (XMenuItem) e.widget;
						Runnable runnable = new Runnable( ) {

							public void run() {
								showOrHideTab(i);
							}
						};
						SWTUtil.showBusyWhile(parent, runnable);
					}

				});
				robotItems.put(robot.getName( ), robotItm);
			}
			itm.setMenu(robots);
		}

		// new HMenuItem(subMenuProfile, SWT.SEPARATOR);

		menuBarItemRobotProfile.setMenu(subMenuProfile);
		menuBarItemRobotProfile.setEnabled(true);

		// Plugins
		menuBarItemPlugins = new XMenuItem(menuBar, SWT.CASCADE);
		menuBarItemPlugins.setTextForLocaleKey("ApplicationMenu.gui.menu.plugins"); //$NON-NLS-1$
		menuBarItemPlugins.setEnabled(false);
		subMenuPlugin = new Menu(menuBarItemPlugins);
		menuBarItemPlugins.setMenu(subMenuPlugin);

		// Reports
		menuBarItemReports = new XMenuItem(menuBar, SWT.CASCADE);
		menuBarItemReports.setTextForLocaleKey("ApplicationMenu.gui.menu.reports"); //$NON-NLS-1$
		menuBarItemReports.setEnabled(false);
		subMenuReports = new Menu(menuBarItemReports);
		menuBarItemReports.setMenu(subMenuReports);

		// Extras
		createExtrasMenu(menuBar);

		// External tools
		menuBarItemExternalTools = new XMenuItem(menuBar, SWT.CASCADE);
		menuBarItemExternalTools.setTextForLocaleKey("ApplicationMenu.menu.item.externalTools"); //$NON-NLS-1$
		menuBarItemExternalTools.setEnabled(false);

		subMenuExternalTools = new Menu(menuBarItemReports);
		menuBarItemExternalTools.setMenu(subMenuExternalTools);

		// Help (aka ?)
		createHelpMenu(menuBar);
	}

	/**
	 * Creates the help menu. .
	 * 
	 * @param menuBar
	 *            The menu bar.
	 */
	private void createHelpMenu(Menu menuBar) {
		XMenuItem menuBarItemHelp = new XMenuItem(menuBar, SWT.CASCADE);
		menuBarItemHelp.setTextForLocaleKey("Application.gui.menu.help"); //$NON-NLS-1$
		Menu subMenuHelp = new Menu(menuBarItemHelp);

		// ? --> About Xirp
		hotkey = HotkeyManager.getHotkey(CtrlShift.PROGRAM_INFO);
		XMenuItem subMenuHelpItemAbout = new XMenuItem(subMenuHelp, SWT.PUSH);
		subMenuHelpItemAbout.setTextForLocaleKey("Application.gui.menu.help.about.xirp", Constants.BASE_NAME_MAJOR_VERSION, hotkey.getFullAcceleratorName( )); //$NON-NLS-1$
		subMenuHelpItemAbout.setAccelerator(hotkey.getAccelerator( ));
		image = ImageManager.getSystemImage(SystemImage.ABOUT);
		subMenuHelpItemAbout.setImage(image);
		subMenuHelpItemAbout.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ApplicationManager.showAboutDialog(true);
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		// ? --> About Plugins
		hotkey = HotkeyManager.getHotkey(CtrlAlt.PLUGIN_INFO);
		XMenuItem subMenuHelpItemAboutPlugins = new XMenuItem(subMenuHelp,
				SWT.PUSH);
		subMenuHelpItemAboutPlugins.setTextForLocaleKey("Application.gui.menu.help.about.plugins", hotkey.getFullAcceleratorName( )); //$NON-NLS-1$
		subMenuHelpItemAboutPlugins.setAccelerator(hotkey.getAccelerator( ));
		image = ImageManager.getSystemImage(SystemImage.ABOUT);
		subMenuHelpItemAboutPlugins.setImage(image);
		subMenuHelpItemAboutPlugins.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ApplicationManager.showAboutDialog(false);
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		new XMenuItem(subMenuHelp, SWT.SEPARATOR);

		// ? --> Help
		hotkey = HotkeyManager.getHotkey(Ctrl.PROGRAM_HELP);
		XMenuItem subMenuHelpItemOnline = new XMenuItem(subMenuHelp, SWT.PUSH);
		subMenuHelpItemOnline.setTextForLocaleKey("Application.gui.menu.help.help", hotkey.getFullAcceleratorName( )); //$NON-NLS-1$
		subMenuHelpItemOnline.setAccelerator(hotkey.getAccelerator( ));
		image = ImageManager.getSystemImage(SystemImage.QUESTION);
		subMenuHelpItemOnline.setImage(image);
		subMenuHelpItemOnline.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ApplicationManager.showHelpDialog( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});
		menuBarItemHelp.setMenu(subMenuHelp);
		menuBarItemHelp.setEnabled(true);
	}

	/**
	 * Creates the extras menu.
	 * 
	 * @param menuBar
	 *            The menu bar.
	 */
	private void createExtrasMenu(Menu menuBar) {
		XMenuItem menuBarItemExtras = new XMenuItem(menuBar, SWT.CASCADE);
		menuBarItemExtras.setTextForLocaleKey("ApplicationMenu.gui.menu.extras"); //$NON-NLS-1$
		Menu subMenuExtras = new Menu(menuBarItemExtras);

		// Extras --> Mail
		hotkey = HotkeyManager.getHotkey(Ctrl.OPEN_MAIL);
		XMenuItem subMenuExtrasMail = new XMenuItem(subMenuExtras, SWT.PUSH);
		subMenuExtrasMail.setTextForLocaleKey("ApplicationMenu.gui.menu.extras.mail", hotkey.getFullAcceleratorName( )); //$NON-NLS-1$
		subMenuExtrasMail.setAccelerator(hotkey.getAccelerator( ));
		image = ImageManager.getSystemImage(SystemImage.SEND_MAIL);
		subMenuExtrasMail.setImage(image);
		subMenuExtrasMail.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ApplicationManager.showMailDialog( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		// Extras --> Kontakte
		hotkey = HotkeyManager.getHotkey(CtrlAlt.MANAGE_CONTACTS);
		XMenuItem subMenuExtrasContact = new XMenuItem(subMenuExtras, SWT.PUSH);
		subMenuExtrasContact.setTextForLocaleKey("ApplicationMenu.gui.menu.extras.contacts", hotkey.getFullAcceleratorName( )); //$NON-NLS-1$
		subMenuExtrasContact.setAccelerator(hotkey.getAccelerator( ));
		image = ImageManager.getSystemImage(SystemImage.CONTACTS);
		subMenuExtrasContact.setImage(image);
		subMenuExtrasContact.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ApplicationManager.showContactDialog( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		new XMenuItem(subMenuExtras, SWT.SEPARATOR);

		// Extras --> Kontakte
		hotkey = HotkeyManager.getHotkey(Ctrl.CREATE_CHART);
		XMenuItem subMenuExtrasChart = new XMenuItem(subMenuExtras, SWT.PUSH);
		subMenuExtrasChart.setTextForLocaleKey("ApplicationMenu.gui.menu.extras.chart", hotkey.getFullAcceleratorName( )); //$NON-NLS-1$
		subMenuExtrasChart.setAccelerator(hotkey.getAccelerator( ));
		image = ImageManager.getSystemImage(SystemImage.CHART);
		subMenuExtrasChart.setImage(image);
		subMenuExtrasChart.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ApplicationManager.showChartDialog( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		new XMenuItem(subMenuExtras, SWT.SEPARATOR);

		// Extras --> TesterBot
		XMenuItem subMenuExtrasTesterBot = new XMenuItem(subMenuExtras,
				SWT.PUSH);
		subMenuExtrasTesterBot.setTextForLocaleKey("ApplicationMenu.menu.extras.invokeTesterbot"); //$NON-NLS-1$
		image = ImageManager.getSystemImage(SystemImage.TESTERBOT);
		subMenuExtrasTesterBot.setImage(image);
		subMenuExtrasTesterBot.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ApplicationManager.showTesterBotDialog( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		menuBarItemExtras.setMenu(subMenuExtras);
		menuBarItemExtras.setEnabled(true);
	}

	/**
	 * Creates the view menu.
	 * 
	 * @param menuBar
	 *            The menu bar.
	 */
	private void createViewMenu(Menu menuBar) {
		menuBarItemView = new XMenuItem(menuBar, SWT.CASCADE);
		menuBarItemView.setTextForLocaleKey("Application.gui.menu.view"); //$NON-NLS-1$
		Menu subMenuView = new Menu(menuBarItemView);

		// View --> C.O.P. visible
		if (ProfileManager.getRobotCount( ) > 1) {
			subMenuViewItemControlTab = new XMenuItem(subMenuView, SWT.CHECK);
			subMenuViewItemControlTab.setTextForLocaleKey("Application.gui.menu.view.copVisible"); //$NON-NLS-1$
			subMenuViewItemControlTab.setSelection(true);

			subMenuViewItemControlTab.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (((XMenuItem) e.widget).getSelection( )) {
						Runnable runnable = new Runnable( ) {

							public void run() {
								application.getAppContent( )
										.createControlOverviewTab( );
							}
						};
						SWTUtil.showBusyWhile(parent, runnable);
					}
					else {
						Runnable runnable = new Runnable( ) {

							public void run() {
								application.getAppContent( )
										.removeControlOverviewTab( );
							}
						};
						SWTUtil.showBusyWhile(parent, runnable);
					}
				}
			});

			new XMenuItem(subMenuView, SWT.SEPARATOR);
		}
		// View --> Tools visible
		subMenuViewItemTools = new XMenuItem(subMenuView, SWT.CHECK);
		subMenuViewItemTools.setTextForLocaleKey("Application.gui.menu.view.toolsVisible"); //$NON-NLS-1$
		subMenuViewItemTools.setSelection(true);
		subMenuViewItemTools.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((XMenuItem) e.widget).getSelection( )) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							application.getAppToolBar( )
									.setToolBarVisible(true);
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
				else {
					Runnable runnable = new Runnable( ) {

						public void run() {
							application.getAppToolBar( )
									.setToolBarVisible(false);
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			}
		});

		// View --> Tools locked
		subMenuViewItemLocked = new XMenuItem(subMenuView, SWT.CHECK);
		subMenuViewItemLocked.setTextForLocaleKey("ApplicationMenu.gui.menu.toolsLocked"); //$NON-NLS-1$
		subMenuViewItemLocked.setSelection(false);
		subMenuViewItemLocked.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((XMenuItem) e.widget).getSelection( )) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							application.getAppToolBar( ).setToolBarLocked(true);
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
				else {
					Runnable runnable = new Runnable( ) {

						public void run() {
							application.getAppToolBar( )
									.setToolBarLocked(false);
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			}
		});

		// View --> Statusbar visible
		XMenuItem subMenuViewItemStatus = new XMenuItem(subMenuView, SWT.CHECK);
		subMenuViewItemStatus.setTextForLocaleKey("Application.gui.menu.view.statusVisible"); //$NON-NLS-1$
		subMenuViewItemStatus.setSelection(true);
		subMenuViewItemStatus.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((XMenuItem) e.widget).getSelection( )) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							application.getAppStatusBar( ).setVisible(true);
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
				else {
					Runnable runnable = new Runnable( ) {

						public void run() {
							application.getAppStatusBar( ).setVisible(false);
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			}
		});

		new XMenuItem(subMenuView, SWT.SEPARATOR);

		// View --> Logs visible
		XMenuItem subMenuViewItemLog = new XMenuItem(subMenuView, SWT.CHECK);
		subMenuViewItemLog.setTextForLocaleKey("ApplicationMenu.menu.view.logsVisible"); //$NON-NLS-1$
		subMenuViewItemLog.setSelection(true);
		subMenuViewItemLog.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((XMenuItem) e.widget).getSelection( )) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							for (RobotPanel rp : application.getAppContent( )
									.getRobotPanels( )) {
								rp.getLog( ).reAdd( );
							}
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
				else {
					for (RobotPanel rp : application.getAppContent( )
							.getRobotPanels( )) {
						rp.getLog( ).removePart( );
					}
				}
			}
		});

		// View --> Live charts visible
		XMenuItem subMenuViewItemChart = new XMenuItem(subMenuView, SWT.CHECK);
		subMenuViewItemChart.setTextForLocaleKey("ApplicationMenu.menu.view.liveChartsVisible"); //$NON-NLS-1$
		subMenuViewItemChart.setSelection(true);
		subMenuViewItemChart.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((XMenuItem) e.widget).getSelection( )) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							for (RobotPanel rp : application.getAppContent( )
									.getRobotPanels( )) {
								rp.getLiveChart( ).reAdd( );
							}
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
				else {
					for (RobotPanel rp : application.getAppContent( )
							.getRobotPanels( )) {
						rp.getLiveChart( ).removePart( );
					}
				}
			}
		});

		// View --> Recorder visible
		XMenuItem subMenuViewItemRecord = new XMenuItem(subMenuView, SWT.CHECK);
		subMenuViewItemRecord.setTextForLocaleKey("ApplicationMenu.menu.view.recorderVisible"); //$NON-NLS-1$
		subMenuViewItemRecord.setSelection(true);
		subMenuViewItemRecord.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((XMenuItem) e.widget).getSelection( )) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							for (RobotPanel rp : application.getAppContent( )
									.getRobotPanels( )) {
								rp.getRecord( ).reAdd( );
							}
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
				else {
					for (RobotPanel rp : application.getAppContent( )
							.getRobotPanels( )) {
						rp.getRecord( ).removePart( );
					}
				}
			}
		});

		// View --> Robot Overview visible
		XMenuItem subMenuViewItemOverview = new XMenuItem(subMenuView,
				SWT.CHECK);
		subMenuViewItemOverview.setTextForLocaleKey("ApplicationMenu.menu.view.robotOverviewVisible"); //$NON-NLS-1$
		subMenuViewItemOverview.setSelection(true);
		subMenuViewItemOverview.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((XMenuItem) e.widget).getSelection( )) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							for (RobotPanel rp : application.getAppContent( )
									.getRobotPanels( )) {
								rp.getOverview( ).reAdd( );
							}
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
				else {
					for (RobotPanel rp : application.getAppContent( )
							.getRobotPanels( )) {
						rp.getOverview( ).removePart( );
					}
				}
			}
		});

		menuBarItemView.setMenu(subMenuView);
		menuBarItemView.setEnabled(true);

		// updateViewMenu( );
	}

	/**
	 * Updates the view menu.
	 */
	public void updateViewMenu() {
		for (MenuItem item : menuBarItemView.getMenu( ).getItems( )) {
			if (item instanceof XMenuItem) {
				XMenuItem x = (XMenuItem) item;
				String textLocaleKey = x.getTextLocaleKey( );
				if (textLocaleKey != null) {
					boolean viewItemVisible = PropertiesManager.isViewItemVisible(textLocaleKey);
					if (x.getSelection( ) != viewItemVisible) {
						x.setSelection(viewItemVisible);
						Event event = new Event( );
						event.widget = x;
						x.notifyListeners(SWT.Selection, event);
					}
				}
			}
		}
	}

	/**
	 * Creates the file menu.
	 * 
	 * @param menuBar
	 *            The menu bar.
	 */
	private void createFileMenu(Menu menuBar) {
		XMenuItem menuBarItemFile = new XMenuItem(menuBar, SWT.CASCADE);
		menuBarItemFile.setTextForLocaleKey("Application.gui.menu.file"); //$NON-NLS-1$
		Menu subMenuFile = new Menu(menuBarItemFile);

		// File --> New profile
		// hotkey = HotkeyManager.getHotkey(Ctrl.PROGRAM_PREFERENCES);
		XMenuItem subMenuFileItemNewProfile = new XMenuItem(subMenuFile,
				SWT.PUSH);
		subMenuFileItemNewProfile.setTextForLocaleKey("ApplicationMenu.menu.file.newProfile"); //$NON-NLS-1$
		// subMenuFileItemSettings.setAccelerator(hotkey.getAccelerator(
		// ));
		image = ImageManager.getSystemImage(SystemImage.PROFILE_NEW);
		subMenuFileItemNewProfile.setImage(image);
		subMenuFileItemNewProfile.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ApplicationManager.showNewProfileDialog( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		// File --> Edit profile
		// hotkey = HotkeyManager.getHotkey(Ctrl.PROGRAM_PREFERENCES);
		XMenuItem subMenuFileItemEditProfile = new XMenuItem(subMenuFile,
				SWT.PUSH);
		subMenuFileItemEditProfile.setTextForLocaleKey("ApplicationMenu.menu.file.editProfile"); //$NON-NLS-1$
		// subMenuFileItemSettings.setAccelerator(hotkey.getAccelerator(
		// ));
		image = ImageManager.getSystemImage(SystemImage.PROFILE_EDIT);
		subMenuFileItemEditProfile.setImage(image);
		subMenuFileItemEditProfile.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ProfileLookupDialog dialog = new ProfileLookupDialog(parent);
						Profile profile = dialog.open( );
						if (profile != null) {
							ApplicationManager.showEditProfileDialog(profile);
						}
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		new XMenuItem(subMenuFile, SWT.SEPARATOR);

		// File --> New robot
		// hotkey = HotkeyManager.getHotkey(Ctrl.PROGRAM_PREFERENCES);
		XMenuItem subMenuFileItemNewRobot = new XMenuItem(subMenuFile, SWT.PUSH);
		subMenuFileItemNewRobot.setEnabled(false);
		subMenuFileItemNewRobot.setTextForLocaleKey("ApplicationMenu.menu.file.newRobot"); //$NON-NLS-1$
		// subMenuFileItemSettings.setAccelerator(hotkey.getAccelerator(
		// ));
		image = ImageManager.getSystemImage(SystemImage.NEW_ROBOT);
		subMenuFileItemNewRobot.setImage(image);
		subMenuFileItemNewRobot.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ApplicationManager.showNewRobotDialog( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		// File --> Edit robot
		// hotkey = HotkeyManager.getHotkey(Ctrl.PROGRAM_PREFERENCES);
		XMenuItem subMenuFileItemEditRobot = new XMenuItem(subMenuFile,
				SWT.PUSH);
		subMenuFileItemEditRobot.setEnabled(false);
		subMenuFileItemEditRobot.setTextForLocaleKey("ApplicationMenu.menu.file.editRobot"); //$NON-NLS-1$
		// subMenuFileItemSettings.setAccelerator(hotkey.getAccelerator(
		// ));
		image = ImageManager.getSystemImage(SystemImage.EDIT_ROBOT);
		subMenuFileItemEditRobot.setImage(image);
		subMenuFileItemEditRobot.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						RobotLookupDialog dialog = new RobotLookupDialog(parent);
						String botFileName = dialog.openSingle( );
						File file = new File(Constants.CONF_ROBOTS_DIR,
								botFileName);
						Robot robot = ProfileManager.getRobotFromFile(file);
						if (robot != null) {
							ApplicationManager.showEditRobotDialog(robot);
						}
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		new XMenuItem(subMenuFile, SWT.SEPARATOR);

		// File --> New comm spec
		// hotkey = HotkeyManager.getHotkey(Ctrl.PROGRAM_PREFERENCES);
		XMenuItem subMenuFileItemNewCommSpec = new XMenuItem(subMenuFile,
				SWT.PUSH);
		subMenuFileItemNewCommSpec.setEnabled(false);
		subMenuFileItemNewCommSpec.setTextForLocaleKey("ApplicationMenu.menu.file.newCommSpec"); //$NON-NLS-1$
		// subMenuFileItemSettings.setAccelerator(hotkey.getAccelerator(
		// ));
		image = ImageManager.getSystemImage(SystemImage.NEW_COMM_SPEC);
		subMenuFileItemNewCommSpec.setImage(image);
		subMenuFileItemNewCommSpec.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ApplicationManager.showNewCommSpecDialog( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		// File --> Edit comm spec
		// hotkey = HotkeyManager.getHotkey(Ctrl.PROGRAM_PREFERENCES);
		XMenuItem subMenuFileItemEditCommSpec = new XMenuItem(subMenuFile,
				SWT.PUSH);
		subMenuFileItemEditCommSpec.setEnabled(false);
		subMenuFileItemEditCommSpec.setTextForLocaleKey("ApplicationMenu.menu.file.editCommSpec"); //$NON-NLS-1$
		// subMenuFileItemSettings.setAccelerator(hotkey.getAccelerator(
		// ));
		image = ImageManager.getSystemImage(SystemImage.EDIT_COMM_SPEC);
		subMenuFileItemEditCommSpec.setImage(image);
		subMenuFileItemEditCommSpec.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						CommSpecsLookupDialog dialog = new CommSpecsLookupDialog(parent);
						String cmsFileName = dialog.openSingle( );
						File file = new File(Constants.CONF_COMMSPECS_DIR,
								cmsFileName);
						CommunicationSpecification comSpec = ProfileManager.getCommSpecFromFile(file);
						if (comSpec != null) {
							ApplicationManager.showEditCommSpecDialog(comSpec);
						}
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		new XMenuItem(subMenuFile, SWT.SEPARATOR);

		// File --> Settings
		hotkey = HotkeyManager.getHotkey(Ctrl.PROGRAM_PREFERENCES);
		XMenuItem subMenuFileItemSettings = new XMenuItem(subMenuFile, SWT.PUSH);
		subMenuFileItemSettings.setTextForLocaleKey("Application.gui.menu.file.settings", hotkey.getFullAcceleratorName( )); //$NON-NLS-1$
		subMenuFileItemSettings.setAccelerator(hotkey.getAccelerator( ));
		image = ImageManager.getSystemImage(SystemImage.PREFERENCES);
		subMenuFileItemSettings.setImage(image);
		subMenuFileItemSettings.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ApplicationManager.showPreferencesDialog( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});

		new XMenuItem(subMenuFile, SWT.SEPARATOR);

		// File --> Quit
		hotkey = HotkeyManager.getHotkey(Ctrl.PROGRAM_QUIT);
		XMenuItem subMenuFileItemQuit = new XMenuItem(subMenuFile, SWT.PUSH);
		subMenuFileItemQuit.setTextForLocaleKey("Application.gui.menu.file.quit", hotkey.getFullAcceleratorName( )); //$NON-NLS-1$
		subMenuFileItemQuit.setAccelerator(hotkey.getAccelerator( ));

		image = ImageManager.getSystemImage(SystemImage.QUIT);
		subMenuFileItemQuit.setImage(image);
		subMenuFileItemQuit.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						parent.close( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});
		menuBarItemFile.setMenu(subMenuFile);
		menuBarItemFile.setEnabled(true);
	}

	/**
	 * Rearranges the menu for the given profile and robot. Several
	 * methods for generating menu entries are called. But first of
	 * all the menu will be cleared. After that the new entries are
	 * generated.
	 * 
	 * @param profile
	 *            The new profile.
	 * @param robot
	 *            The new robot.
	 */
	private void rearrangeMenu(Profile profile, Robot robot) {
		clear( );
		if (profile != null) {
			addPluginEntrys(robot);
			addGeneralPluginEntrys( );
			addReportEntrys(robot);
			addGeneralReportEntrys( );
			addExternalToolsEntrys(profile);
		}
		else {
			addGeneralPluginEntrys( );
			addGeneralReportEntrys( );
		}

		if (!Variables.NO_PROFILES_LOADED) {
			menuBarItemPlugins.setEnabled(true);
			menuBarItemReports.setEnabled(true);
			menuBarItemExternalTools.setEnabled(true);
		}
	}

	/**
	 * Adds the external tools entries for the given profile.
	 * 
	 * @param profile
	 *            The profile.
	 * @see de.xirp.profile.Profile
	 */
	private void addExternalToolsEntrys(Profile profile) {
		ExternalTools et = profile.getExternalTools( );
		if (et != null) {
			Vector<Tool> tools = et.getTools( );

			for (final Tool tool : tools) {
				XMenuItem itm = new XMenuItem(subMenuExternalTools, SWT.PUSH);
				itm.setData(TOOL, tool);
				itm.setText("&" + tool.getName( )); //$NON-NLS-1$
				itm.addSelectionListener(new SelectionAdapter( ) {

					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
					 */
					@Override
					public void widgetSelected(SelectionEvent e) {
						final Tool tool = (Tool) e.widget.getData(TOOL);
						Vector<Executable> executables = tool.getExecutables( );
						List<ExternalProgram> test = new ArrayList<ExternalProgram>(executables.size( ));
						for (Executable exe : executables) {
							ExternalProgram extProg = new ExternalProgram(exe);
							test.add(extProg);
						}
						ExternalProgramManager.start(test);
					}
				});
			}
		}
	}

	/**
	 * Adds the report menu entries which will be available for all
	 * robots.
	 */
	private void addGeneralReportEntrys() {

		// Reports --> Suche
		hotkey = HotkeyManager.getHotkey(CtrlAlt.SEARCH_REPORTS);
		XMenuItem subMenuReportsSearch = new XMenuItem(menuBarItemReports.getMenu( ),
				SWT.PUSH);
		subMenuReportsSearch.setTextForLocaleKey("ApplicationMenu.gui.menu.reports.search", hotkey //$NON-NLS-1$
				.getFullAcceleratorName( ));
		subMenuReportsSearch.setAccelerator(hotkey.getAccelerator( ));
		subMenuReportsSearch.setImage(ImageManager.getSystemImage(SystemImage.SEARCH));
		subMenuReportsSearch.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ApplicationManager.showReportSearchDialog( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});
	}

	/**
	 * Clears the plugin and report menus. This is called, when the
	 * menus should be rearranged.
	 */
	private void clear() {
		for (MenuItem itm : subMenuPlugin.getItems( )) {
			SWTUtil.secureDispose(itm);
		}
		for (MenuItem itm : subMenuReports.getItems( )) {
			SWTUtil.secureDispose(itm);
		}
		for (MenuItem itm : subMenuExternalTools.getItems( )) {
			SWTUtil.secureDispose(itm);
		}
		menuBarItemPlugins.setEnabled(false);
		menuBarItemReports.setEnabled(false);
		menuBarItemExternalTools.setEnabled(false);
	}

	/**
	 * Shows oder hides a robot tab. This method is called when a
	 * selection event was fired when checking or un-checking an item
	 * in the robot menu.
	 * 
	 * @param item
	 *            The item on which the event occurred,
	 */
	private void showOrHideTab(XMenuItem item) {
		Robot robot = (Robot) item.getData(BOT);
		Profile profile = (Profile) item.getData(PROFILE);
		if (item.getSelection( )) {
			application.getAppContent( ).createRobotTab(profile, robot);
		}
		else {
			// boolean disconnect = true;
			// Check if connection to robot exists
			if (CommunicationManager.isConnected(robot.getName( ))) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						// Ask if gui should disconnect from Robot
						disconnect = ApplicationManager.showDisconnectDialog( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
			// Only close the Tab if connection to robot should be
			// closed or does not exist
			if (disconnect) {
				application.getAppContent( ).removeRobotTab(robot);
			}
			else {
				item.setSelection(true);
			}
		}
	}

	/**
	 * Adds all general entries to the plugins menu. This entries will
	 * available for all robots.
	 */
	private void addGeneralPluginEntrys() {
		// Plugins --> Preferences
		hotkey = HotkeyManager.getHotkey(Ctrl.PROGRAM_PREFERENCES);
		XMenuItem subMenuPluginsSettings = new XMenuItem(menuBarItemPlugins.getMenu( ),
				SWT.PUSH);
		subMenuPluginsSettings.setTextForLocaleKey("Application.gui.menu.pluginprefs", hotkey.getFullAcceleratorName( ));//$NON-NLS-1$
		subMenuPluginsSettings.setAccelerator(hotkey.getAccelerator( ));
		image = ImageManager.getSystemImage(SystemImage.PREFERENCES);
		subMenuPluginsSettings.setImage(image);
		subMenuPluginsSettings.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Runnable runnable = new Runnable( ) {

					public void run() {
						ApplicationManager.showPreferencesDialog( );
					}
				};
				SWTUtil.showBusyWhile(parent, runnable);
			}
		});
	}

	/**
	 * Adds all plugins with their defined entry to the plugins menu.
	 * 
	 * @param robot
	 *            Robot, the new robot.
	 */
	private void addPluginEntrys(Robot robot) {
		final List<IPlugable> plugins = PluginManager.getPlugins(robot.getName( ),
				new IPluginFilter( ) {

					public boolean filterPlugin(IPlugable plugin) {
						return !PluginType.containsType(plugin,
								PluginType.REPORT) &&
								!PluginType.containsType(plugin,
										PluginType.COMMUNICATION) &&
								!PluginType.containsType(plugin,
										PluginType.MESSAGE_HANDLER) &&
								!PluginType.containsType(plugin,
										PluginType.PROTOCOL) &&
								(VisualizationType.containsType(plugin,
										VisualizationType.WINDOW) || VisualizationType.containsType(plugin,
										VisualizationType.NONE));
					}

				});

		for (final IPlugable plugin : plugins) {
			final String claas = plugin.getInfo( ).getMainClass( );
			XMenuItem subMenuPluginsItem = new XMenuItem(menuBarItemPlugins.getMenu( ),
					SWT.PUSH);
			final boolean hasWindow = VisualizationType.containsType(plugin,
					VisualizationType.WINDOW);
			if (hasWindow) {
				subMenuPluginsItem.setImage(ImageManager.getSystemImage(SystemImage.WINDOW));
			}
			subMenuPluginsItem.setText("&" + plugin.getName( )); //$NON-NLS-1$
			subMenuPluginsItem.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							if (hasWindow) {
								PluginManager.startPluginInWindow(claas);
							}
							else {
								String robotName = Robot.NAME_NONE;
								Robot robot = ApplicationManager.getCurrentRobot( );
								if (robot != null) {
									robotName = robot.getName( );
								}
								PluginManager.runPlugin(robotName, claas);
							}
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});
		}

		if (plugins.size( ) > 0) {
			new XMenuItem(menuBarItemPlugins.getMenu( ), SWT.SEPARATOR);
		}
	}

	/**
	 * Adds all report plugins with their defined entry to the reports
	 * menu.
	 * 
	 * @param robot
	 *            Robot, the new robot.
	 */
	private void addReportEntrys(Robot robot) {
		final List<IPlugable> plugins = PluginManager.getPluginsForType(robot.getName( ),
				PluginType.REPORT);

		for (final IPlugable plugin : plugins) {

			final String clazz = plugin.getInfo( ).getMainClass( );
			XMenuItem subMenuPluginsItem = new XMenuItem(menuBarItemReports.getMenu( ),
					SWT.PUSH);
			subMenuPluginsItem.setText("&" + plugin.getName( )); //$NON-NLS-1$
			subMenuPluginsItem.addSelectionListener(new SelectionAdapter( ) {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Runnable runnable = new Runnable( ) {

						public void run() {
							final IPlugable plugin2 = PluginManager.runPlugin(ApplicationManager.getCurrentRobot( )
									.getName( ),
									clazz);
							if (plugin2 != null) {
								if (plugin2.hasReport( )) {
									final Report report = plugin2.getReport( );
									try {
										String name = ReportGenerator.generateReport(report);
										SWTUtil.openFile(Constants.REPORT_DIR +
												Constants.FS + name);
										// TODO open in Background

									}
									catch (Exception e) {
										logClass.error("Error: " + e.getMessage( ) + //$NON-NLS-1$
												Constants.LINE_SEPARATOR,
												e);
									}
								}
								else {
									logClass.warn(I18n.getString("ApplicationMenu.log.report.nodata", //$NON-NLS-1$
											clazz) +
											Constants.LINE_SEPARATOR);
								}
							}
							else {
								logClass.warn(I18n.getString("ApplicationMenu.log.report.noplugin", //$NON-NLS-1$
										clazz) +
										Constants.LINE_SEPARATOR);
							}
						}
					};
					SWTUtil.showBusyWhile(parent, runnable);
				}
			});

		}
		if (plugins.size( ) > 0) {
			new XMenuItem(menuBarItemReports.getMenu( ), SWT.SEPARATOR);
		}
	}

	/**
	 * Returns a menu item: subMenuViewItemControlTab.
	 * 
	 * @return The subMenuViewItemControlTab.
	 */
	public XMenuItem getSubMenuViewItemControlTab() {
		return subMenuViewItemControlTab;
	}

	/**
	 * Returns a menu item: subMenuViewItemTools.
	 * 
	 * @return The subMenuViewItemTools.
	 */
	public XMenuItem getSubMenuViewItemTools() {
		return subMenuViewItemTools;
	}

	/**
	 * Returns a menu item: subMenuViewItemLocked.
	 * 
	 * @return Returns the subMenuViewItemLocked.
	 */
	public XMenuItem getSubMenuViewItemLocked() {
		return subMenuViewItemLocked;
	}

	/**
	 * Returns a menu item for the given robot name.
	 * 
	 * @param robotName
	 *            the name of the robot to get the menu item for
	 * @return The robot menu item.
	 */
	public XMenuItem getRobotItem(String robotName) {
		return robotItems.get(robotName);
	}

	/**
	 * Sets the robot enabled.
	 * 
	 * @param b
	 *            <code>true</code>: enabled.<br>
	 *            <code>false</code>: not enabled.
	 */
	public void setRobotEnabled(boolean b) {
		menuBarItemRobotProfile.setEnabled(b);
	}
}
