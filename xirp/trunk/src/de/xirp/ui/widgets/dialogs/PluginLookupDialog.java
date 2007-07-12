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
 * PluginLookupDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 20.04.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import de.xirp.plugin.IPlugable;
import de.xirp.plugin.PluginManager;
import de.xirp.plugin.PluginType;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XList;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.custom.XButton.XButtonType;

/**
 * This dialog is used to look up a plugin loaded 
 * by the application.
 * 
 * TODO: type = blah | blub
 * 
 * @author Matthias Gernand
 */
public final class PluginLookupDialog extends XDialog {

	/**
	 * Width of the dialog.
	 */
	private static final int WIDTH = 400;
	/**
	 * Height of the dialog.
	 */
	private static final int HEIGHT = 250;
	/**
	 * HShell of the dialog.
	 */
	private XShell dialogShell;
	/**
	 * Parent shell.
	 */
	private Shell parent;
	/**
	 * An image object.
	 */
	private Image image;
	/**
	 * A vector where the class names are stored.
	 */
	private java.util.List<String> classes = new ArrayList<String>( );
	/**
	 * A list.
	 */
	private XList list;
	/**
	 * A button.
	 */
	private XButton ok;
	/**
	 * A button.
	 */
	private XButton cancel;
	/**
	 * Flag indicating if all plugin typed should be
	 * used to provide the list of plugins loaded.
	 */
	private boolean all = true;
	/**
	 * The plugin type to offer.
	 */
	private int type;
	/**
	 * Flag indicating if multi select is allowed or not.
	 */
	private boolean multi = false;

	/**
	 * Constructs a new dialog on the parent shell for the given
	 * plugin type.
	 * 
	 * @param parent
	 *            the parent.
	 * @param type
	 *            The type of plugin classes to offer.
	 * @param multi
	 *            Indicates if one or more entries can be selected.<br>
	 *            <code>true</code> multi select allowed.<br>
	 *            <code>false</code> multi select not allowed.
	 *            
	 * @see de.xirp.plugin.PluginType           
	 */
	public PluginLookupDialog(Shell parent, int type, boolean multi) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
		this.all = false;
		this.type = type;
		this.multi = multi;
	}

	/**
	 * Constructs a new dialog on the parent shell for all plugin
	 * types.
	 * 
	 * @param parent
	 *            the parent.
	 * @param multi
	 *            Indicates if one or more entries can be selected.<br>
	 *            <code>true</code> multi select allowed.<br>
	 *            <code>false</code> multi select not allowed.
	 */
	public PluginLookupDialog(Shell parent, boolean multi) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
		this.all = true;
		this.multi = multi;
	}

	/**
	 * Opens the class lookup dialog with the fully qualified class
	 * names of the loaded plugins. Corresponding to the requested
	 * type(s) or all types.
	 * 
	 * @return The chosen class name(s).
	 */
	public java.util.List<String> open() {
		dialogShell = new XShell(parent, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);

		dialogShell.addShellListener(new ShellAdapter( ) {

			/**
			 * @see org.eclipse.swt.events.ShellAdapter#shellClosed(org.eclipse.swt.events.ShellEvent)
			 */
			@Override
			public void shellClosed(ShellEvent e) {
				SWTUtil.secureDispose(dialogShell);
			}
		});

		dialogShell.setSize(WIDTH, HEIGHT);
		dialogShell.setTextForLocaleKey("ClassLookupDialog.gui.dialogTitle.chooseClass"); //$NON-NLS-1$
		image = ImageManager.getSystemImage(SystemImage.QUESTION);
		dialogShell.setImage(image);

		SWTUtil.setGridLayout(dialogShell, 2, true);

		if (multi) {
			list = new XList(dialogShell, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
					| SWT.V_SCROLL);
		}
		else {
			list = new XList(dialogShell, SWT.BORDER | SWT.SINGLE
					| SWT.H_SCROLL | SWT.V_SCROLL);
		}
		SWTUtil.setGridData(list, true, true, SWT.FILL, SWT.FILL, 2, 1);
		for (String s : pluginNameLookup( )) {
			Vector<String> itm = new Vector<String>( );
			CollectionUtils.addAll(itm, list.getItems( ));
			if (!itm.contains(s)) {
				list.add(s);
			}
		}
		list.addSelectionListener(new SelectionAdapter( ) {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (list.getSelectionCount( ) > 0) {
					ok.setEnabled(true);
				}
				else {
					ok.setEnabled(false);
				}
			}
		});

		ok = new XButton(dialogShell, XButtonType.OK);
		ok.setEnabled(false);
		SWTUtil.setGridData(ok, true, false, SWT.FILL, SWT.CENTER, 1, 1);
		ok.addSelectionListener(new SelectionAdapter( ) {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				CollectionUtils.addAll(classes, list.getSelection( ));
				dialogShell.close( );
			}
		});

		cancel = new XButton(dialogShell, XButtonType.CANCEL);
		SWTUtil.setGridData(cancel, true, false, SWT.FILL, SWT.CENTER, 1, 1);
		cancel.addSelectionListener(new SelectionAdapter( ) {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialogShell.close( );
			}
		});

		dialogShell.setDefaultButton(ok);

		dialogShell.layout( );
		SWTUtil.centerDialog(dialogShell);
		dialogShell.open( );

		SWTUtil.blockDialogFromReturning(dialogShell);

		return Collections.unmodifiableList(classes);
	}

	/**
	 * Returns the available plugin class names.
	 * 
	 * @return The class names.
	 */
	private Vector<String> pluginNameLookup() {
		java.util.List<IPlugable> plugins = PluginManager.getPluginSamples( );

		Vector<String> cl = new Vector<String>( );

		if (all) {
			for (IPlugable plugin : plugins) {
				cl.add(plugin.getInfo( ).getMainClass( ));
			}
		}
		else {
			for (IPlugable plugin : plugins) {
				if (PluginType.containsType(plugin, type)) {
					cl.add(plugin.getInfo( ).getMainClass( ));
				}
			}
		}
		return cl;
	}
}
