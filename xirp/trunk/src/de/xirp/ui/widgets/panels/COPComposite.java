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
 * COPComposite.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 30.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.xirp.plugin.IPlugable;
import de.xirp.plugin.IPluginFilter;
import de.xirp.plugin.PluginManager;
import de.xirp.plugin.PluginType;
import de.xirp.profile.Profile;
import de.xirp.profile.Robot;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.*;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.ui.widgets.dialogs.XMessageBox;
import de.xirp.ui.widgets.dialogs.XMessageBox.HMessageBoxType;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This class represents the control overview panel composite.
 * 
 * @author Matthias Gernand
 */
public class COPComposite extends XComposite {

	/**
	 * A string constant.
	 */
	private static final String BOT = "bot"; //$NON-NLS-1$
	/**
	 * The selected robots.
	 */
	private Set<Robot> selectedBots = new HashSet<Robot>( );
	/**
	 * A button.
	 */
	private XButton start;
	/**
	 * A profile.
	 */
	private Profile profile;
	/**
	 * A logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(COPComposite.class);
	/**
	 * The minimum width.
	 */
	private final int MIN_WIDTH = 250;

	/**
	 * Constructs a new cop composite.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param profile
	 *            The profile.
	 */
	public COPComposite(Composite parent, int style, Profile profile) {
		super(parent, style);
		this.profile = profile;
		init( );
	}

	/**
	 * Initializes the ui.
	 */
	private void init() {
		createSettingsGroup( );
	}

	/**
	 * Creates the settings group.
	 */
	private void createSettingsGroup() {
		SWTUtil.setGridLayout(this, 1, true);
		final XGroup group = new XGroup(this, SWT.NONE);
		GridData gd = SWTUtil.setGridData(group,
				true,
				true,
				SWT.CENTER,
				SWT.CENTER,
				1,
				1);
		gd.minimumHeight = 300;
		gd.minimumWidth = 400;
		group.setTextForLocaleKey("COPComposite.group.settings"); //$NON-NLS-1$
		SWTUtil.setGridLayout(group, 1, true);

		XTree tree = new XTree(group, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL |
				SWT.H_SCROLL);
		SWTUtil.setGridData(tree, true, true, SWT.FILL, SWT.FILL, 1, 1);

		tree.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				XTreeItem itm = (XTreeItem) event.item;
				Robot robot = (Robot) itm.getData(BOT);
				if (itm.isChecked( )) {
					selectedBots.add(robot);
				}
				else {
					selectedBots.remove(robot);
				}

				if (selectedBots.size( ) > 0) {
					start.setEnabled(true);
				}
				else {
					start.setEnabled(false);
				}
			}

		});

		for (Robot robot : profile.getRobots( )) {
			XTreeItem ti = new XTreeItem(tree, SWT.NONE);
			ti.setText(robot.getName( ));
			ti.setData(BOT, robot);
		}

		start = new XButton(group);
		start.setEnabled(false);
		SWTUtil.setGridData(start, true, false, SWT.FILL, SWT.END, 1, 1);
		start.setTextForLocaleKey("COPComposite.button.start"); //$NON-NLS-1$
		start.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				Rectangle r = getBounds( );
				int aux = r.width / selectedBots.size( );

				if (aux >= MIN_WIDTH) {
					SWTUtil.secureDispose(group);
					createControls( );
				}
				else {
					XMessageBox box = new XMessageBox(getShell( ),
							HMessageBoxType.INFO,
							XButtonType.OK);
					box.setTextForLocaleKey("COPComposite.box.text.notEnoughSpace"); //$NON-NLS-1$
					box.setMessageForLocaleKey("COPComposite.box.msg.notEnoughSpace", //$NON-NLS-1$
							Constants.LINE_SEPARATOR,
							selectedBots.size( ));
					box.open( );
				}
			}

		});
		layout( );
	}

	/**
	 * Creates the controls.
	 */
	private void createControls() {
		GridLayout gl = SWTUtil.setGridLayout(this, selectedBots.size( ), true);
		SWTUtil.resetSpacings(gl);
		SWTUtil.resetMargins(gl);

		Robot[] robots = selectedBots.toArray(new Robot[] {});
		List<Robot> robos = Arrays.asList(robots);
		Collections.sort(robos);

		// HGroup settings = new HGroup(this, SWT.NONE);
		// SWTUtil.setGridData(settings, true, true, SWT.FILL,
		// SWT.FILL, 1, 1);
		// SWTUtil.setGridLayout(settings, 5, true);
		// settings.setTextForLocaleKey("Einstellungen");
		//		
		// HLabel l = new HLabel(this, SWT.NONE);
		// SWTUtil.setGridData(l, true, true, SWT.FILL, SWT.FILL, 1,
		// 1);
		// l.setTextForLocaleKey("");

		for (Robot robot : robos) {
			XLabel l = new XLabel(this, SWT.NONE);
			l.setText(robot.getName( ));
			SWTUtil.setGridData(l, true, false, SWT.CENTER, SWT.BEGINNING, 1, 1);
		}

		for (Robot robot : robos) {
			Composite rc = createRobotComposite(robot);
			SWTUtil.setGridData(rc, true, true, SWT.FILL, SWT.FILL, 1, 1);
		}
		layout( );
	}

	/**
	 * Creates a composite for the given robot.
	 * 
	 * @param robot
	 *            The robot.
	 * @return composite with all plugins of the robot as overview.
	 * @see de.xirp.profile.Robot
	 */
	private Composite createRobotComposite(Robot robot) {
		XSashForm form = new XSashForm(this, SWT.VERTICAL | SWT.SMOOTH);
		form.SASH_WIDTH = 3;
		SWTUtil.setGridData(form, true, true, SWT.FILL, SWT.FILL, 1, 1);
		int x[] = new int[2];

		XTabFolder sensorFolder = new XTabFolder(form, SWT.DOWN);
		SWTUtil.setGridData(sensorFolder, true, true, SWT.FILL, SWT.FILL, 1, 1);

		IPluginFilter filter = new IPluginFilter( ) {

			public boolean filterPlugin(IPlugable plugin) {
				return PluginType.containsType(plugin, PluginType.SENSOR) |
						PluginType.containsType(plugin, PluginType.MULTIMEDIA);
			}

		};
		for (IPlugable plugin : PluginManager.getPlugins(robot.getName( ),
				filter)) {
			Composite com = plugin.getGUI(sensorFolder);
			if (SWTUtil.swtAssert(com)) {
				XTabItem itm = new XTabItem(sensorFolder,
						SWT.NONE,
						plugin.getHandler( ));
				itm.setControl(com);
				itm.setTextForLocaleKey(plugin.getNameKey( ));
			}
			else {
				logClass.warn(I18n.getString("COPComposite.log.warn.noGUISensor", //$NON-NLS-1$
						plugin.getName( )) +
						Constants.LINE_SEPARATOR);
			}
		}

		sensorFolder.setSelection(0);

		XTabFolder controlFolder = new XTabFolder(form, SWT.DOWN);
		SWTUtil.setGridData(controlFolder, true, true, SWT.FILL, SWT.FILL, 1, 1);

		filter = new IPluginFilter( ) {

			public boolean filterPlugin(IPlugable plugin) {
				return PluginType.containsType(plugin, PluginType.ROBOT_CONTROL);
			}

		};
		for (IPlugable plugin : PluginManager.getPlugins(robot.getName( ),
				filter)) {
			Composite com = plugin.getGUI(controlFolder);
			if (SWTUtil.swtAssert(com)) {
				XTabItem itm = new XTabItem(controlFolder,
						SWT.NONE,
						plugin.getHandler( ));
				itm.setControl(com);
				itm.setTextForLocaleKey(plugin.getNameKey( ));
			}
			else {
				logClass.warn(I18n.getString("COPComposite.log.warn.noGUIControl", //$NON-NLS-1$
						plugin.getName( )) +
						Constants.LINE_SEPARATOR);
			}
		}
		controlFolder.setSelection(0);

		x[0] = 50;
		x[1] = 50;
		form.setWeights(x);

		return form;
	}
}
