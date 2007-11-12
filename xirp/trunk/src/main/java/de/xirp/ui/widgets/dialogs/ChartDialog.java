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
 * ChartDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 26.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.JFreeChart;

import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XChartComposite;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XShell;

/**
 * This dialog shows one or multiple charts.
 * 
 * @author Matthias Gernand
 * 
 * @see org.jfree.chart.JFreeChart
 */
public class ChartDialog extends XDialog {

	/**
	 * The width.
	 */
	private static final int WIDTH = 800;
	/**
	 * The height.
	 */
	private static final int HEIGHT = 600;
	/**
	 * The parent shell.
	 */
	private Shell parent;
	/**
	 * The charts to show.
	 * 
	 * @see org.jfree.chart.JFreeChart
	 */
	private List<JFreeChart> charts;
	/**
	 * The dialog shell.
	 */
	private XShell dialogShell;
	/**
	 * A chart composite.
	 */
	private XChartComposite cc;
	/**
	 * The robot name.
	 */
	private String robotName;

	/**
	 * Constructs a new chart dialog.
	 * 
	 * @param parent
	 * 			The parent.
	 * @param charts
	 * 			The charts to show.
	 * @param robotName
	 * 			The robot name.
	 * 
	 * @see org.jfree.chart.JFreeChart
	 */
	public ChartDialog(Shell parent, List<JFreeChart> charts, String robotName) {
		super(parent);
		this.parent = parent;
		this.charts = charts;
		this.robotName = robotName;
	}

	/**
	 * Opens the dialog which shows one one multiple charts.
	 * 
	 * @see org.jfree.chart.JFreeChart
	 */
	public void open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL | SWT.MAX);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});
		dialogShell.setSize(WIDTH, HEIGHT);
		dialogShell.setTextForLocaleKey("ChartDialog.gui.title"); //$NON-NLS-1$
		dialogShell.setImage(ImageManager.getSystemImage(SystemImage.CHART));

		if (this.charts.size( ) % 2 == 0) { //even
			SWTUtil.setGridLayout(dialogShell, 2, true);
		}
		else { // odd
			SWTUtil.setGridLayout(dialogShell, 1, true);
		}

		for (JFreeChart chart : this.charts) {
			cc = new XChartComposite(dialogShell,
					SWT.NONE,
					chart,
					true,
					robotName);
			SWTUtil.setGridData(cc, true, true, SWT.FILL, SWT.FILL, 1, 1);
		}

		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		SWTUtil.blockDialogFromReturning(dialogShell);
	}
}
