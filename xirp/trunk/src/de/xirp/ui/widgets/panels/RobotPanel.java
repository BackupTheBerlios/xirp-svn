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
 * RobotPanel.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 15.02.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.xirp.plugin.IPlugable;
import de.xirp.plugin.PluginManager;
import de.xirp.plugin.VisualizationType;
import de.xirp.profile.Robot;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.util.Variables;

/**
 * This UI class represents the content area which contains the
 * docking composite.
 * 
 * @author Matthias Gernand
 */
public final class RobotPanel extends XComposite {

	/**
	 * The active robot.
	 */
	private Robot robot;
	/**
	 * The tool bar for this content.
	 */
	private RobotToolbar toolbar;
	/**
	 * A log layout part.
	 * 
	 * @see de.xirp.ui.widgets.panels.LogLayoutPart
	 */
	private LogLayoutPart log;
	/**
	 * A live chart layout part.
	 * 
	 * @see de.xirp.ui.widgets.panels.LiveChartLayoutPart
	 */
	private LiveChartLayoutPart liveChart;
	/**
	 * A record layout part.
	 * 
	 * @see de.xirp.ui.widgets.panels.RecordLayoutPart
	 */
	private RecordLayoutPart record;
	/**
	 * A robot control overview part.
	 * 
	 * @see de.xirp.ui.widgets.panels.RobotOverviewLayoutPart
	 */
	private RobotOverviewLayoutPart overview;

	/**
	 * Constructs an new robot panel.
	 * 
	 * @param parent
	 *            The parent composite
	 * @param robot
	 *            The robot
	 */
	public RobotPanel(Composite parent, Robot robot) {
		super(parent, SWT.NONE);
		this.robot = robot;
		init( );
	}

	/**
	 * Initializes the content area and loads the docking workspace.
	 */
	private void init() {
		setBackground(ColorManager.getColor(Variables.BACKGROUND_COLOR));
		GridLayout gl = SWTUtil.setGridLayout(this, 1, true);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;

		toolbar = new RobotToolbar(this, robot);

		DockingComposite docking = new DockingComposite(this,
				"workspacelayout_" + robot.getName( )); //$NON-NLS-1$
		SWTUtil.setGridData(docking, true, true, SWT.FILL, SWT.FILL, 1, 1);

		List<IPlugable> plugins = PluginManager.getPlugins(robot.getName( ));

		for (IPlugable plugin : plugins) {
			if (VisualizationType.containsType(plugin,
					VisualizationType.EMBEDDED)) {
				docking.addPlugin(plugin);
			}
		}

		log = new LogLayoutPart(LogLayoutPart.class.getCanonicalName( ) +
				"_" + robot.getName( ),//$NON-NLS-1$
				docking.getDockingManager( ),
				robot.getName( ));
		docking.addPart(log);

		liveChart = new LiveChartLayoutPart(LiveChartLayoutPart.class.getCanonicalName( ) +
				"_" + robot.getName( ),//$NON-NLS-1$
				docking.getDockingManager( ),
				robot.getName( ));
		docking.addPart(liveChart);

		record = new RecordLayoutPart(RecordLayoutPart.class.getCanonicalName( ) +
				"_" + robot.getName( ),//$NON-NLS-1$
				docking.getDockingManager( ),
				robot.getName( ));
		docking.addPart(record);

		overview = new RobotOverviewLayoutPart(RobotOverviewLayoutPart.class.getCanonicalName( ) +
				"_" + robot.getName( ),//$NON-NLS-1$
				docking.getDockingManager( ),
				robot.getName( ));
		docking.addPart(overview);
	}

	/**
	 * Starts the stop watch.
	 */
	public void startWatch() {
		toolbar.startWatch( );
	}

	/**
	 * Pauses the timer.
	 */
	public void pauseWatch() {
		toolbar.pauseTime( );
	}

	/**
	 * Stops the timer.
	 */
	public void stopWatch() {
		toolbar.stopTime( );
	}

	/**
	 * Returns the robot tool bar.
	 * 
	 * @return The tool bar.
	 */
	public RobotToolbar getToolBar() {
		return toolbar;

	}

	/**
	 * Returns the log part.
	 * 
	 * @return The log part.
	 */
	public LogLayoutPart getLog() {
		return log;
	}

	/**
	 * Returns the live chart part.
	 * 
	 * @return the live chart part.
	 */
	public LiveChartLayoutPart getLiveChart() {
		return liveChart;
	}

	/**
	 * Returns the record part.
	 * 
	 * @return the record part.
	 */
	public RecordLayoutPart getRecord() {
		return record;
	}

	/**
	 * The robot overview part.
	 * 
	 * @return the overview part.
	 */
	public RobotOverviewLayoutPart getOverview() {
		return overview;
	}

}
