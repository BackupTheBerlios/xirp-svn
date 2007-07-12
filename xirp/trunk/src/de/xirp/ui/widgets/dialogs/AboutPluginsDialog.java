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
 * AboutPluginsDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):  
 *
 * Changes
 * -------
 * 24.03.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import de.xirp.plugin.IPlugable;
import de.xirp.plugin.PluginManager;
import de.xirp.plugin.VisualizationType;
import de.xirp.profile.Robot;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.*;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.util.Constants;
import de.xirp.util.II18nHandler;
import de.xirp.util.VersionComparator;

/**
 * Dialog to display information about the loaded plugins.
 * 
 * @author Matthias Gernand
 */
public final class AboutPluginsDialog extends XDialog {

	/**
	 * A string constant, for the handler.
	 */
	private static final String HANDLER = "handler"; //$NON-NLS-1$
	/**
	 * A string constant, for the description.
	 */
	private static final String DESCRIPTION = "description"; //$NON-NLS-1$
	/**
	 * Width of the dialog.
	 */
	private static final int WIDTH = 550;
	/**
	 * Height of the dialog.
	 */
	private static final int HEIGHT = 300;
	/**
	 * A string constant, for window.
	 */
	private static final String HASWINDOW = "hasWindow"; //$NON-NLS-1$
	/**
	 * A string constant, for main class.
	 */
	private static final String MAINCLASS = "mainClass"; //$NON-NLS-1$
	/**
	 * Shell of the dialog.
	 */
	private XShell dialogShell = null;
	/**
	 * Parent shell.
	 */
	private Shell parent;
	/**
	 * An image object.
	 */
	private Image image;
	/**
	 * The plugins.
	 */
	private List<IPlugable> plugins;
	/**
	 * A balloon window.
	 */
	private XBalloonWindow b;
	/**
	 * A point.
	 */
	private Point location;
	/**
	 * A button.
	 */
	private XButton preview;
	/**
	 * A generic {@link de.xirp.ui.widgets.dialogs.GenericDialog dialog}.
	 */
	private GenericDialog lastPreviewDialog;

	/**
	 * Constructs a new dialog on the parent shell.
	 * 
	 * @param parent
	 *            the parent shell.
	 */
	public AboutPluginsDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
	}

	/**
	 * Opens the about dialog with the description of the plugins on
	 * it.
	 */
	public void open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			@Override
			public void shellClosed(ShellEvent e) {
				if (lastPreviewDialog != null) {
					lastPreviewDialog.close( );
				}
				SWTUtil.secureDispose(dialogShell);
			}
		});

		dialogShell.setSize(WIDTH, HEIGHT);
		dialogShell.setTextForLocaleKey("Application.gui.about.plugins.title"); //$NON-NLS-1$
		image = ImageManager.getSystemImage(SystemImage.ABOUT);
		dialogShell.setImage(image);
		SWTUtil.setGridLayout(dialogShell, 2, true);

		XLabel name = new XLabel(dialogShell, SWT.CENTER);
		name.setText(Constants.BASE_NAME);
		SWTUtil.setGridData(name, true, false, SWT.CENTER, SWT.BEGINNING, 2, 1);

		XLabel loadedPlugins = new XLabel(dialogShell, SWT.CENTER);
		loadedPlugins.setTextForLocaleKey("AboutPluginsDialog.gui.loadedPlugins"); //$NON-NLS-1$
		SWTUtil.setGridData(loadedPlugins,
				true,
				false,
				SWT.CENTER,
				SWT.BEGINNING,
				2,
				1);

		final XTable table = new XTable(dialogShell, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION | SWT.V_SCROLL);
		SWTUtil.setGridData(table, true, true, SWT.FILL, SWT.FILL, 2, 1);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setToolTipTextForLocaleKey("AboutPluginsDialog.table.tooltip", Constants.LINE_SEPARATOR); //$NON-NLS-1$
		String[] titles = {
				"", "AboutPluginsDialog.table.column.plugin", //$NON-NLS-2$//$NON-NLS-1$
				"AboutPluginsDialog.table.column.version", "AboutPluginsDialog.table.column.author"}; //$NON-NLS-1$ //$NON-NLS-2$
		for (String t : titles) {
			XTableColumn column = new XTableColumn(table, SWT.NONE);
			column.setTextForLocaleKey(t);
			column.pack( );
		}

		XTableColumn column = (XTableColumn) table.getColumn(2);
		column.setSortable(new VersionComparator( ));

		table.addMouseListener(new MouseAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.MouseAdapter#mouseDown(org.eclipse.swt.events.MouseEvent)
			 */
			@Override
			public void mouseDown(MouseEvent e) {
				location = parent.getDisplay( ).map(table, parent, e.x, e.y);
			}

		});

		table.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (b != null) {
					try {
						b.close( );
					}
					catch (RuntimeException e1) {

					}
				}
				XTableItem item = (XTableItem) e.item;
				String msgKey = (String) item.getData(DESCRIPTION);
				II18nHandler handler = (II18nHandler) item.getData(HANDLER);
				Boolean hasWindow = (Boolean) item.getData(HASWINDOW);
				preview.setEnabled(hasWindow);

				if (lastPreviewDialog != null) {
					lastPreviewDialog.close( );
				}
				b = new XBalloonWindow(dialogShell, SWT.NONE, handler);
				b.setMessageForLocaleKey(msgKey);
				location.y += 35;
				b.getStyledText( ).setEditable(false);
				b.getStyledText( ).setEnabled(false);
				b.setLocation(location);
				b.open( );

			}

		});

		plugins = PluginManager.getPluginSamples( );
		for (IPlugable plugin : plugins) {
			final XTableItem itm = new XTableItem(table, SWT.NONE);
			String[] str = new String[] {"", plugin.getName( ), //$NON-NLS-1$
					plugin.getInfo( ).getVersion( ),
					plugin.getInfo( ).getAuthor( )};
			boolean hasWindow = VisualizationType.containsType(plugin,
					VisualizationType.WINDOW)
					|| VisualizationType.containsType(plugin,
							VisualizationType.EMBEDDED);

			if (hasWindow) {
				itm.setImage(ImageManager.getSystemImage(SystemImage.DONE_SMALL));
			}
			else {
				itm.setImage(ImageManager.getSystemImage(SystemImage.NOT_DONE_SMALL));
			}
			itm.setText(str);
			itm.setData(DESCRIPTION, plugin.getDescriptionKey( ));
			itm.setData(MAINCLASS, plugin.getInfo( ).getMainClass( ));
			itm.setData(HANDLER, plugin.getHandler( ));
			itm.setData(HASWINDOW, Boolean.valueOf(hasWindow));
		}

		SWTUtil.packTable(table);

		preview = new XButton(dialogShell);
		preview.setTextForLocaleKey("AboutPluginsDialog.button.preview"); //$NON-NLS-1$
		preview.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem itm = table.getItem(table.getSelectionIndex( ));
				Boolean hasWindow = (Boolean) itm.getData(HASWINDOW);
				if (hasWindow) {
					String mainClass = (String) itm.getData(MAINCLASS);
					lastPreviewDialog = new GenericDialog(AboutPluginsDialog.this.dialogShell);
					lastPreviewDialog.open(mainClass, Robot.NAME_NONE, true);
				}
			}
		});
		preview.setEnabled(false);
		SWTUtil.setGridData(preview,
				true,
				false,
				GridData.END,
				SWT.BOTTOM,
				1,
				1);

		XButton close = new XButton(dialogShell, XButtonType.CLOSE);
		close.addSelectionListener(new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				dialogShell.close( );
			}
		});
		SWTUtil.setGridData(close,
				true,
				false,
				GridData.HORIZONTAL_ALIGN_BEGINNING,
				SWT.BOTTOM,
				1,
				1);

		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

	}
}
