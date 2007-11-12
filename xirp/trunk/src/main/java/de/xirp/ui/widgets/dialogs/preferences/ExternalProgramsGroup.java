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
 * ExternalProgramsGroup.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 16.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import de.xirp.profile.Executable;
import de.xirp.profile.ExternalTools;
import de.xirp.profile.Profile;
import de.xirp.profile.Tool;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.FontManager;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XButton;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.ui.widgets.custom.XGroup;
import de.xirp.ui.widgets.custom.XList;
import de.xirp.ui.widgets.custom.XStyledText;
import de.xirp.ui.widgets.custom.XTable;
import de.xirp.ui.widgets.custom.XTableColumn;
import de.xirp.ui.widgets.custom.XTableItem;
import de.xirp.ui.widgets.custom.XTextField;
import de.xirp.ui.widgets.custom.XButton.XButtonType;
import de.xirp.ui.widgets.dialogs.InputDialog;
import de.xirp.ui.widgets.dialogs.XMessageBox;
import de.xirp.ui.widgets.dialogs.XMessageBox.HMessageBoxType;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Util;

/**
 * This group contains a UI for configuring the external
 * {@link de.xirp.profile.Tool tools} of a profile.
 * 
 * @author Matthias Gernand
 * @see de.xirp.profile.Tool
 */
public class ExternalProgramsGroup extends XGroup {

	/**
	 * Constant string.
	 */
	private static final String EXE = "exe"; //$NON-NLS-1$
	/**
	 * A table.
	 */
	private XTable table;
	/**
	 * A button.
	 */
	private XButton toolDetail;
	/**
	 * A button.
	 */
	private XButton removeTool;
	/**
	 * A button.
	 */
	private XButton addTool;
	/**
	 * A button.
	 */
	private XButton removeExecutable;
	/**
	 * The profile name.
	 */
	private String profileName;
	/**
	 * The currently selected tool name.
	 */
	private String currentToolName;
	/**
	 * The parent folder.
	 */
	private ExternalProgramStartupFolder folder;
	/**
	 * The profile.
	 */
	private Profile profile;
	/**
	 * The external tools.
	 */
	private ExternalTools external;

	/**
	 * Constructs a new external program group.
	 * 
	 * @param parent
	 *            The parent.
	 * @param style
	 *            The style.
	 * @param profile
	 *            The profile.
	 * @param folder
	 *            The parent folder.
	 */
	public ExternalProgramsGroup(Composite parent, int style, Profile profile,
			ExternalProgramStartupFolder folder) {
		super(parent, style);
		external = new ExternalTools( );
		for (Tool tool : profile.getExternalTools( ).getTools( )) {
			external.addTool(tool.clone( ));
		}
		this.profileName = profile.getName( );
		this.profile = profile;
		this.folder = folder;
		init( );
	}

	/**
	 * Initializes the UI.
	 */
	private void init() {
		setTextForLocaleKey("ExternalProgramsGroup.gui.title", profileName); //$NON-NLS-1$
		SWTUtil.setGridLayout(this, 2, true);

		XStyledText text = new XStyledText(this, SWT.WRAP, true);
		text.setTextForLocaleKey("ExternalProgramsGroup.gui.description", //$NON-NLS-1$
				profileName);
		text.setEditable(false);
		text.setEnabled(false);
		text.setFont(FontManager.getFont("Arial", 8, SWT.BOLD)); //$NON-NLS-1$
		SWTUtil.setGridData(text, true, false, SWT.LEFT, SWT.CENTER, 2, 1);

		final XList toolsList = new XList(this, SWT.BORDER | SWT.V_SCROLL |
				SWT.SINGLE);
		SWTUtil.setGridData(toolsList, true, false, SWT.FILL, SWT.FILL, 1, 4);
		for (Tool tool : external.getTools( )) {
			toolsList.add(tool.getName( ));
		}
		toolsList.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				XList list = (XList) e.widget;
				if (list.getSelectionCount( ) > 0) {
					int idx = list.getSelectionIndex( );
					currentToolName = list.getItem(idx);
					setTable( );
					toolDetail.setEnabled(true);
					removeTool.setEnabled(true);
				}
				else {
					toolDetail.setEnabled(false);
					removeTool.setEnabled(false);
				}
			}

		});

		addTool = new XButton(this);
		addTool.setTextForLocaleKey("ExternalProgramsGroup.button.add"); //$NON-NLS-1$
		SWTUtil.setGridData(addTool,
				false,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				1,
				1);
		addTool.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog dialog = new InputDialog(getShell( ),
						"ExternalProgramsGroup.inputdialog.nameOfTool"); //$NON-NLS-1$
				List<String> result = dialog.open( );
				if (!result.isEmpty( ) && result != null) {
					String aux = result.get(0);
					if (!Arrays.asList(toolsList.getItems( )).contains(aux)) {
						toolsList.add(aux);
						Tool tool = new Tool( );
						tool.setName(aux);

						external.addTool(tool);
					}
					table.removeAll( );
					check( );
				}
			}

		});

		removeTool = new XButton(this);
		removeTool.setTextForLocaleKey("ExternalProgramsGroup.button.removeTool"); //$NON-NLS-1$
		removeTool.setEnabled(false);
		SWTUtil.setGridData(removeTool,
				false,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				1,
				1);
		removeTool.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (toolsList.getSelectionCount( ) > 0) {
					int idx = toolsList.getSelectionIndex( );
					String toolName = toolsList.getItem(idx);
					int toRemove = -1;
					Vector<Tool> tools = external.getTools( );
					for (Tool t : tools) {
						if (t.getName( ).equals(toolName)) {
							toRemove = tools.indexOf(t);
							break;
						}
					}
					tools.remove(toRemove);
					toolsList.remove(idx);
					table.removeAll( );
					check( );
				}
			}

		});

		toolDetail = new XButton(this);
		toolDetail.setEnabled(false);
		toolDetail.setTextForLocaleKey("ExternalProgramsGroup.button.addExecutable"); //$NON-NLS-1$
		SWTUtil.setGridData(toolDetail,
				false,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				1,
				1);
		toolDetail.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog dialog = new InputDialog(getShell( ),
						"ExternalProgramsGroup.inputdialog.value.one.name", //$NON-NLS-1$
						"ExternalProgramsGroup.inputdialog.value.two.args", //$NON-NLS-1$
						"ExternalProgramsGroup.inputdialog.value.three.waittime"); //$NON-NLS-1$
				List<String> result = dialog.open(1);
				String path = null;
				if (!result.isEmpty( ) && result != null) {
					TableItem[] items = table.getItems( );
					List<String> names = new ArrayList<String>( );
					for (TableItem i : items) {
						names.add(i.getText(0));
					}

					if (names.contains(result.get(0))) {
						XMessageBox box = new XMessageBox(getShell( ),
								HMessageBoxType.ERROR,
								XButtonType.CLOSE);
						box.setTextForLocaleKey("ExternalProgramsGroup.messagebox.title.nameAlreadyChosen"); //$NON-NLS-1$
						box.setMessageForLocaleKey("ExternalProgramsGroup.messagebox.message.nameExists", result.get(0)); //$NON-NLS-1$
						box.open( );
						return;
					}
					else {
						FileDialog fd = new FileDialog(getShell( ));
						fd.setFilterPath(Constants.USER_DIR);
						fd.setFilterNames(new String[] {
								I18n.getString("ExternalProgramsGroup.filedialog.filterNames.windowsExes"), //$NON-NLS-1$
								I18n.getString("ExternalProgramsGroup.filedialog.filterNames.linuxBins"), //$NON-NLS-1$
								I18n.getString("ExternalProgramsGroup.filedialog.filterNames.allFiles")}); //$NON-NLS-1$
						fd.setFilterExtensions(new String[] {
								"*.exe", "*.bin", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						path = fd.open( );
					}
					try {
						String time = result.get(2);
						long wait = Long.parseLong(time);
						if (!Util.isEmpty(path)) {
							Executable exe = new Executable( );
							exe.setName(result.get(0));
							exe.setArguments(result.get(1));
							exe.setWaitTime(wait);
							exe.setPath(path);

							Vector<Tool> tools = external.getTools( );
							for (Tool tool : tools) {
								if (tool.getName( ).equals(currentToolName)) {
									tool.addExecutable(exe);
									break;
								}
							}
							setTable( );
						}
					}
					catch (NumberFormatException ex) {
						XMessageBox box = new XMessageBox(getShell( ),
								HMessageBoxType.ERROR,
								XButtonType.CLOSE);
						box.setTextForLocaleKey("ExternalProgramsGroup.messagebox.title.numberFormat"); //$NON-NLS-1$
						box.setMessageForLocaleKey("ExternalProgramsGroup.messagebox.message.onlyNumericValue", Constants.LINE_SEPARATOR, ex.getMessage( )); //$NON-NLS-1$
						box.open( );
					}
				}
			}

		});

		removeExecutable = new XButton(this);
		removeExecutable.setEnabled(false);
		removeExecutable.setTextForLocaleKey("ExternalProgramsGroup.button.removeExecutable"); //$NON-NLS-1$
		SWTUtil.setGridData(removeExecutable,
				false,
				false,
				SWT.FILL,
				SWT.BEGINNING,
				1,
				1);
		removeExecutable.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int idx = table.getSelectionIndex( );
				TableItem itm = table.getItem(idx);
				Executable exe = (Executable) itm.getData(EXE);

				Vector<Tool> tools = external.getTools( );
				for (Tool tool : tools) {
					if (tool.getName( ).equals(currentToolName)) {
						tool.getExecutables( ).remove(exe);
						break;
					}
				}
				SWTUtil.secureDispose(itm);
				check( );
			}

		});

		XComposite holder = new XComposite(this, SWT.NONE);
		SWTUtil.setGridData(holder, true, true, SWT.FILL, SWT.FILL, 2, 1);
		SWTUtil.setGridLayout(holder, 2, false);

		XButton up = new XButton(holder);
		up.setImage(ImageManager.getSystemImage(SystemImage.ARROW_UP_SMALL));
		up.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				down( );
			}

		});
		SWTUtil.setGridData(up, false, true, SWT.CENTER, SWT.FILL, 1, 1);

		// Table for setting the keys for commands
		table = new XTable(holder, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		String[] keys = {"ExternalProgramsGroup.table.column.header.name", //$NON-NLS-1$
				"ExternalProgramsGroup.table.column.header.workingDirectory", //$NON-NLS-1$
				"ExternalProgramsGroup.table.column.header.command", //$NON-NLS-1$
				"ExternalProgramsGroup.table.column.header.args", //$NON-NLS-1$
				"ExternalProgramsGroup.table.column.header.timeToNext"}; //$NON-NLS-1$
		for (String key : keys) {
			XTableColumn column = new XTableColumn(table, SWT.NONE);
			column.setTextForLocaleKey(key);
			column.pack( );
			column.disableSortable( );
		}
		SWTUtil.setGridData(table, true, true, SWT.FILL, SWT.FILL, 1, 2);
		table.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				Table t = (Table) e.widget;
				if (t.getSelectionCount( ) > 0) {
					removeExecutable.setEnabled(true);
				}
				else {
					removeExecutable.setEnabled(false);
				}
				check( );
			}
		});

		XButton down = new XButton(holder);
		down.setImage(ImageManager.getSystemImage(SystemImage.ARROW_DOWN_SMALL));
		down.addSelectionListener(new SelectionAdapter( ) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				up( );
			}

		});
		SWTUtil.setGridData(down, false, true, SWT.CENTER, SWT.FILL, 1, 1);
	}

	/**
	 * Sets the content of the table.
	 */
	private void setTable() {
		table.removeAll( );
		for (Tool tool : external.getTools( )) {
			if (tool.getName( ).equals(currentToolName)) {
				for (Executable e : tool.getExecutables( )) {
					XTableItem itm = new XTableItem(table, SWT.NONE);
					itm.setText(0, e.getName( ));
					itm.setText(1, e.getWorkingDirectory( ));
					itm.setText(2, e.getExecutableName( ));
					itm.setText(3, e.getArguments( ));
					itm.setText(4, new Long(e.getWaitTime( )).toString( ));
					itm.setData(EXE, e);
					check( );
				}
				break;
			}
		}
	}

	/**
	 * Moves the selected executable one step up.
	 */
	private void up() {
		upDown(-1);
	}

	/**
	 * Moves the selected executable one step down.
	 */
	private void down() {
		upDown(1);
	}

	/**
	 * Moves the selected executable up or down by the given factor. A
	 * negative factor moves down and a positive factor moves up.
	 * 
	 * @param factor
	 *            The steps to move.
	 */
	private void upDown(int factor) {
		int idx = -1;
		Vector<Executable> exes = new Vector<Executable>( );

		for (Tool tool : external.getTools( )) {
			if (tool.getName( ).equals(currentToolName)) {
				exes = tool.getExecutables( );
				break;
			}
		}
		Executable exe = (Executable) table.getItem(table.getSelectionIndex( ))
				.getData(EXE);
		idx = exes.indexOf(exe);

		TableItem[] items = table.getItems( );
		if (items.length <= 1 || idx <= 0) {
			return;
		}

		if (factor == 1) {
			Collections.swap(exes, idx, idx - 1);
		}
		else if (factor == -1) {
			Collections.swap(exes, idx, idx + 1);
		}
		setTable( );
	}

	/**
	 * Sets the text field properties for this folder.
	 * 
	 * @param textField
	 *            The text field to set the properties on.
	 */
	protected void setTextFieldProperties(final XTextField textField) {
		textField.setTextLimit(1);
		textField.addModifyListener(new ModifyListener( ) {

			public void modifyText(ModifyEvent e) {
				char[] test = textField.getText( ).toCharArray( );
				if (test.length > 0 && Character.isLowerCase(test[0])) {
					textField.setText(textField.getText( ).toUpperCase( ));
				}
				check( );
			}
		});
	}

	/**
	 * Checks if a change occurred. If a change occurred the folder is
	 * notified of this change.
	 */
	private void check() {
		folder.change = !external.equals(profile.getExternalTools( ));
		if (folder.change) {
			folder.fireChangeEvent( );
		}
	}

	/**
	 * Returns the {@link de.xirp.profile.Tool tools}.
	 * 
	 * @return The tools.
	 * @see de.xirp.profile.Tool
	 */
	protected Vector<Tool> getTools() {
		return external.getTools( );
	}

	/**
	 * Returns the profile name.
	 * 
	 * @return The profile name.
	 */
	protected String getProfileName() {
		return profileName;
	}
}
