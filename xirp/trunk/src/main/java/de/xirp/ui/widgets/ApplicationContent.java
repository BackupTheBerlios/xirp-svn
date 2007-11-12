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
 * ApplicationContent.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 16.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.LevelRangeFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;

import com.novocode.naf.swt.custom.FramedComposite;
import com.novocode.naf.swt.custom.SizeGrip;

import de.xirp.io.logging.XirpTextFieldAppender;
import de.xirp.plugin.PluginManager;
import de.xirp.profile.Profile;
import de.xirp.profile.ProfileManager;
import de.xirp.profile.Robot;
import de.xirp.ui.Application;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.XMenuItem;
import de.xirp.ui.widgets.custom.XTabFolder;
import de.xirp.ui.widgets.custom.XTabItem;
import de.xirp.ui.widgets.panels.ControlOverviewPanel;
import de.xirp.ui.widgets.panels.RobotPanel;

/**
 * This class holds the applications workspace.
 * 
 * @author Matthias Gernand
 */
public final class ApplicationContent {

	/**
	 * The parent.
	 */
	private Shell parent;
	/**
	 * The mail folder.
	 */
	private XTabFolder mainTabFolder;
	/**
	 * A framed composite.
	 * 
	 * @see com.novocode.naf.swt.custom.FramedComposite
	 */
	private FramedComposite lower;
	/**
	 * The status bar.
	 * 
	 * @see de.xirp.ui.widgets.ApplicationStatusBar
	 */
	private ApplicationStatusBar statusBar;
	/**
	 * The profile folders.
	 */
	private Map<Profile, XTabFolder> profileFolders = new HashMap<Profile, XTabFolder>( );
	/**
	 * The robot tabs.
	 */
	private Map<Robot, XTabItem> robotTabs = new HashMap<Robot, XTabItem>( );
	/**
	 * The application.
	 * 
	 * @see de.xirp.ui.Application
	 */
	private Application application;
	/**
	 * A tab item for the C.O.P.
	 */
	private XTabItem controlOverviewItem;
	/**
	 * The workspace panels.
	 */
	private List<RobotPanel> workspacePanels = new ArrayList<RobotPanel>( );
	/**
	 * The last index of the last shown profile.
	 */
	private int lastProfileIdx = 0;
	/**
	 * The last index of the last shown robot.
	 */
	private int lastRobotIdx = 0;
	/**
	 * A selection adapter.
	 */
	private SelectionAdapter mainTabSelListener;

	/**
	 * Constructs a new application content.
	 */
	public ApplicationContent() {
		this.application = Application.getApplication( );
		this.parent = application.getShell( );
		init( );
	}

	/**
	 * Initializes the listeners and the content.
	 */
	private void init() {
		mainTabSelListener = new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					lastProfileIdx = mainTabFolder.getSelectionIndex( );
					XTabItem itm = (XTabItem) e.item;
					XTabFolder fold = (XTabFolder) itm.getControl( );
					fold.setSelection(lastRobotIdx);
					ApplicationManager.contentChanged( );
				}
				catch (RuntimeException ex) {
					ApplicationManager.contentChanged( );
				}
			}
		};

		createAppender( );
		createContentArea( );
		createStatusBar( );
	}

	/**
	 * Creates the log appenders.
	 */
	private void createAppender() {
		Logger root = Logger.getRootLogger( );
		// PatternLayout layout = new PatternLayout("%m%n");
		XirpTextFieldAppender appender = new XirpTextFieldAppender( );
		// appender.setLayout(layout);
		LevelRangeFilter filter = new LevelRangeFilter( );
		filter.setLevelMin(Level.INFO);
		appender.addFilter(filter);
		root.addAppender(appender);
	}

	/**
	 * Creates the status bar.
	 */
	private void createStatusBar() {
		statusBar = new ApplicationStatusBar(lower, SWT.NONE);
		SWTUtil.setGridData(statusBar, true, false, SWT.FILL, SWT.END, 1, 1);

		// A Windows-Style Size-Grip is set to the lower right.
		SizeGrip sizeGrip = new SizeGrip(lower, SWT.FLAT);
		GridData gd = new GridData( );
		gd.heightHint = 25;
		sizeGrip.setLayoutData(gd);
		statusBar.setVisible(true);
		statusBar.layout( );
	}

	/**
	 * Creates the content area.
	 */
	private void createContentArea() {
		mainTabFolder = new XTabFolder(parent, SWT.TOP | SWT.MULTI);
		mainTabFolder.addSelectionListener(mainTabSelListener);

		// create profile folders
		int number = 0;
		for (Profile profile : ProfileManager.getProfiles( )) {
			XTabFolder folder = createProfileFolder(profile);
			folder.setTabHeight(20);

			// create robot tab items
			for (Robot robot : profile.getRobots( )) {
				createRobotTab(profile, robot, false);
				number++;
			}
			folder.setSelection(lastRobotIdx);
			folder.layout( );
		}

		if (number > 1) {
			createControlOverviewTab( );
		}

		SWTUtil.setGridData(mainTabFolder, true, true, SWT.FILL, SWT.FILL, 1, 1);
		mainTabFolder.setSelection(lastProfileIdx);
		mainTabFolder.layout( );

		lower = new FramedComposite(parent, SWT.SHADOW_IN);
		GridData gd = SWTUtil.setGridData(lower,
				true,
				false,
				SWT.FILL,
				SWT.END,
				1,
				1);
		gd.heightHint = 25;

		GridLayout gl = SWTUtil.setGridLayout(lower, 2, false);
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
	}

	/**
	 * Creates the profile folder for the given profile.
	 * 
	 * @param profile
	 *            The profile.
	 * @return The folder.
	 */
	private XTabFolder createProfileFolder(final Profile profile) {
		// create profile tabs
		final XTabItem itm = new XTabItem(mainTabFolder, SWT.CLOSE);
		itm.setText(profile.getName( ));
		itm.setToolTipText(profile.getName( ));
		itm.addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent e) {
				SWTUtil.secureDispose(itm.getControl( ));
			}

		});

		// create robot tab folder
		final XTabFolder folder = new XTabFolder(mainTabFolder, SWT.TOP |
				SWT.MULTI);
		folder.setData(profile);
		itm.setControl(folder);
		folder.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				lastRobotIdx = folder.getSelectionIndex( );
				ApplicationManager.contentChanged( );
			}

		});
		folder.addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent e) {
				profileFolders.remove(profile);
			}

		});

		profileFolders.put(profile, folder);
		return folder;
	}

	/**
	 * Returns the robot panels.
	 * 
	 * @return The list of robot panels.
	 */
	public List<RobotPanel> getRobotPanels() {
		return Collections.unmodifiableList(workspacePanels);
	}

	/**
	 * Returns the profile corresponding to the active tab.
	 * 
	 * @return The profile.
	 * @see de.xirp.profile.Profile
	 */
	public Profile getCurrentProfile() {
		try {
			Profile profile = (Profile) ((XTabFolder) mainTabFolder.getSelection( )
					.getControl( )).getSelection( ).getParent( ).getData( );
			return profile;
		}
		catch (RuntimeException e) {
			return null;
		}
	}

	/**
	 * Returns the robot corresponding to the active robot tab.
	 * 
	 * @return The robot.
	 * @see de.xirp.profile.Robot
	 */
	public Robot getCurrentRobot() {
		try {
			Robot robot = (Robot) ((XTabFolder) mainTabFolder.getSelection( )
					.getControl( )).getSelection( ).getData( );
			return robot;
		}
		catch (RuntimeException e) {
			return null;
		}
	}

	/**
	 * Returns the status bar.
	 * 
	 * @return The status bar.
	 */
	public ApplicationStatusBar getStatusBar() {
		return statusBar;
	}

	/**
	 * Creates the control overview tab.
	 */
	public void createControlOverviewTab() {
		controlOverviewItem = new XTabItem(mainTabFolder, SWT.CLOSE);
		controlOverviewItem.setTextForLocaleKey("ApplicationContent.gui.tabTitle.controlOverview"); //$NON-NLS-1$
		controlOverviewItem.addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent e) {
				if (!application.getAppMenu( )
						.getSubMenuViewItemControlTab( )
						.isDisposed( )) {
					application.getAppMenu( )
							.getSubMenuViewItemControlTab( )
							.setSelection(false);
				}
			}
		});
		ControlOverviewPanel cop = new ControlOverviewPanel(mainTabFolder);
		controlOverviewItem.setControl(cop);

	}

	/**
	 * Removes the control overview tab.
	 */
	public void removeControlOverviewTab() {
		SWTUtil.secureDispose(controlOverviewItem);
	}

	/**
	 * Creates a robot tab for the given profile and robot.
	 * 
	 * @param profile
	 *            The profile.
	 * @param robot
	 *            The robot.
	 * @param fireContentChange
	 *            <code>true</code>: a content change event is
	 *            fired.<br>
	 */
	private void createRobotTab(Profile profile, Robot robot,
			boolean fireContentChange) {
		XTabFolder folder = profileFolders.get(profile);

		if (folder == null) {
			folder = createProfileFolder(profile);
			folder.setData(profile);
		}

		final XTabItem itm = new XTabItem(folder, SWT.CLOSE);
		itm.setText(robot.getName( ));
		itm.setToolTipText(robot.getName( ));
		itm.setData(robot);

		itm.addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent e) {
				Robot robot = (Robot) ((CTabItem) e.widget).getData( );
				setRobotViewMenuEntrys(robot);
				removeRobotTab(robot);
			}
		});
		if (fireContentChange) {
			ApplicationManager.contentChanged( );
		}

		RobotPanel workspace = new RobotPanel(profileFolders.get(profile),
				robot);
		workspacePanels.add(workspace);

		itm.setControl(workspace);
		robotTabs.put(robot, itm);
	}

	/**
	 * Creates a robot tab for the given profile and robot with firing
	 * of a content change event.
	 * 
	 * @param profile
	 *            The profile.
	 * @param robot
	 *            The robot.
	 */
	public void createRobotTab(Profile profile, Robot robot) {
		createRobotTab(profile, robot, true);
	}

	/**
	 * Sets the selection of the robot view entries.
	 * 
	 * @param robot
	 *            The robot.
	 */
	private void setRobotViewMenuEntrys(Robot robot) {
		XMenuItem item = application.getAppMenu( )
				.getRobotItem(robot.getName( ));
		if (SWTUtil.swtAssert(item)) {
			item.setSelection(false);
		}
	}

	/**
	 * Removes the robot tab.
	 * 
	 * @param robot
	 *            The robot.
	 */
	public void removeRobotTab(Robot robot) {
		XTabItem itm = robotTabs.remove(robot);
		if (itm != null) {
			workspacePanels.remove(itm.getControl( ));
			PluginManager.stopPlugins(robot.getName( ));
			SWTUtil.secureDispose(itm.getControl( ));
			SWTUtil.secureDispose(itm);
		}
	}

	/**
	 * Starts all stop watches.
	 */
	public void startWatches() {
		for (RobotPanel p : workspacePanels) {
			p.startWatch( );
		}
	}

	/**
	 * Pauses all stop watches.
	 */
	public void pauseWatches() {
		for (RobotPanel p : workspacePanels) {
			p.pauseWatch( );
		}
	}

	/**
	 * Stops all stop watches.
	 */
	public void stopWatches() {
		for (RobotPanel p : workspacePanels) {
			p.stopWatch( );
		}
	}

	/**
	 * Sets the status bar visible or invisible.
	 * 
	 * @param visible
	 *            <br>
	 *            <code>true</code>: status visible.<br>
	 *            <code>false</code>: status not visible.
	 */
	public void setStatusVisible(boolean visible) {
		if (visible) {
			((GridData) lower.getLayoutData( )).heightHint = 25;
			lower.layout( );
			statusBar.layout( );
			parent.layout( );
		}
		else {
			((GridData) lower.getLayoutData( )).heightHint = 0;
			lower.layout( );
			statusBar.layout( );
			parent.layout( );
		}
	}
}
