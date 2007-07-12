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
 * HotkeysFolder.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   Rabea Gransberger [rgransberger AT web.de]
 *
 * Changes
 * -------
 * 16.03.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import de.xirp.io.command.CommandDefinition;
import de.xirp.io.command.CommandGamepadManager;
import de.xirp.io.command.CommandKeyManager;
import de.xirp.io.command.CommandManager;
import de.xirp.io.gamepad.GamepadEvent;
import de.xirp.io.gamepad.GamepadEventListener;
import de.xirp.io.gamepad.GamepadManager;
import de.xirp.io.gamepad.GamepadControl.AxisType;
import de.xirp.settings.*;
import de.xirp.settings.Option.OptionType;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.*;
import de.xirp.ui.widgets.dialogs.preferences.renderer.IValidator;
import de.xirp.util.I18n;

/**
 * A custom composite which shows the settings for the hotkeys and
 * commands.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class HotkeysFolder extends AbstractContentFolderComposite {

	/**
	 * A constant string.
	 */
	private static final String COMMAND = "command"; //$NON-NLS-1$
	/**
	 * Local representation of the settings, needed to be able to
	 * revert the
	 * {@link de.xirp.settings.Settings settings}.
	 * 
	 * @see de.xirp.settings.Settings
	 */
	private Map<CommandDefinition, KeySequence> keyMappings = new HashMap<CommandDefinition, KeySequence>( );
	/**
	 * Local representation of the settings, needed to be able to
	 * revert the
	 * {@link de.xirp.settings.Settings settings}.
	 * 
	 * @see de.xirp.settings.Settings
	 */
	private Map<CommandDefinition, String> gamepadMappings = new HashMap<CommandDefinition, String>( );
	/**
	 * Formal name of the key which is interpreted as the standard
	 * empty key for the key mappings.
	 */
	private static final String FORMAL_EMPTY_KEY = "DEL"; //$NON-NLS-1$
	/**
	 * {@link de.xirp.settings.Option} for the quit
	 * shortcut.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option quitProgram;
	/**
	 * {@link de.xirp.settings.Option} for the open
	 * preferences shortcut.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option openPreferences;
	/**
	 * {@link de.xirp.settings.Option} for the show help
	 * shortcut.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option showHelp;
	/**
	 * {@link de.xirp.settings.Option} for the search
	 * reports shortcut.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option searchReports;
	/**
	 * {@link de.xirp.settings.Option} for the open mail
	 * shortcut.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option openMail;
	/**
	 * {@link de.xirp.settings.Option} for the manage
	 * contacts shortcut.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option manageContacts;
	/**
	 * {@link de.xirp.settings.Option} for the show
	 * program info shortcut.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option showProgramInfo;
	/**
	 * {@link de.xirp.settings.Option} for the show
	 * plugin info shortcut.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option showPluginInfo;
	/**
	 * {@link de.xirp.settings.Option} for the create
	 * chart shortcut.
	 * 
	 * @see de.xirp.settings.Option
	 */
	private Option createChart;
	/**
	 * A flag indicating whether the table content has changed or not.
	 */
	private boolean tableChanged = false;

	/**
	 * Constructs a new hotkeys folder.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	public HotkeysFolder(XComposite parent) {
		super(parent, SWT.NONE);
		initSettings( );
	}

	/**
	 * Saves the values to the ini file.
	 */
	private void saveToIni() {
		IValue value = quitProgram.getSelectedValue( );
		PropertiesManager.setProgramQuit(value.getDisplayValue( ));

		value = openPreferences.getSelectedValue( );
		PropertiesManager.setProgramPreferences(value.getDisplayValue( ));

		value = showHelp.getSelectedValue( );
		PropertiesManager.setProgramHelp(value.getDisplayValue( ));

		value = searchReports.getSelectedValue( );
		PropertiesManager.setSearchReports(value.getDisplayValue( ));

		value = openMail.getSelectedValue( );
		PropertiesManager.setOpenMail(value.getDisplayValue( ));

		value = manageContacts.getSelectedValue( );
		PropertiesManager.setManageContacts(value.getDisplayValue( ));

		value = showProgramInfo.getSelectedValue( );
		PropertiesManager.setProgramInfo(value.getDisplayValue( ));

		value = showPluginInfo.getSelectedValue( );
		PropertiesManager.setPluginInfo(value.getDisplayValue( ));

		value = createChart.getSelectedValue( );
		PropertiesManager.setCreateChart(value.getDisplayValue( ));
	}

	/**
	 * Initializes the {@link de.xirp.settings.Settings}
	 * object and builds the UI from it.
	 */
	private void initSettings() {
		IValidator toUpper = new IValidator( ) {

			/**
			 * Formats the given string to upper case
			 * 
			 * @return the given string as upper case
			 */
			public String checkString(String strg) {
				return strg.toUpperCase( );
			}

			/**
			 * Does nothing
			 * 
			 * @return <code>true</code>
			 */
			public boolean validate(@SuppressWarnings("unused")
			String strg) {
				return true;
			}
		};

		settings = new Settings("HotkeysFolder"); //$NON-NLS-1$
		SettingsPage ctrlPage = settings.addPage("gui.ctrlShortcut", "editShortcuts", null); //$NON-NLS-1$ //$NON-NLS-2$

		quitProgram = ctrlPage.addOption("quitProgram", OptionType.TEXTFIELD); //$NON-NLS-1$
		quitProgram.addStringValue("quitProgram", "Q", 1).setSavedValue(PropertiesManager.getProgramQuit( )); //$NON-NLS-1$ //$NON-NLS-2$
		quitProgram.getRenderer( ).addValidator(toUpper);

		openPreferences = ctrlPage.addOption("openPreferences", OptionType.TEXTFIELD); //$NON-NLS-1$
		openPreferences.addStringValue("openPreferences", "P", 1).setSavedValue(PropertiesManager.getProgramPreferences( )); //$NON-NLS-1$ //$NON-NLS-2$
		openPreferences.getRenderer( ).addValidator(toUpper);

		showHelp = ctrlPage.addOption("showHelp", OptionType.TEXTFIELD); //$NON-NLS-1$
		showHelp.addStringValue("showHelp", "H", 1).setSavedValue(PropertiesManager.getProgramHelp( )); //$NON-NLS-1$ //$NON-NLS-2$
		showHelp.getRenderer( ).addValidator(toUpper);

		openMail = ctrlPage.addOption("openMail", OptionType.TEXTFIELD); //$NON-NLS-1$
		openMail.addStringValue("openMail", "M", 1).setSavedValue(PropertiesManager.getOpenMail( )); //$NON-NLS-1$ //$NON-NLS-2$
		openMail.getRenderer( ).addValidator(toUpper);

		createChart = ctrlPage.addOption("createChart", OptionType.TEXTFIELD); //$NON-NLS-1$
		createChart.addStringValue("createChart", "D", 1).setSavedValue(PropertiesManager.getCreateChart( )); //$NON-NLS-1$ //$NON-NLS-2$
		createChart.getRenderer( ).addValidator(toUpper);

		// second
		SettingsPage shiftPage = settings.addPage("gui.ctrlShiftShortcut", "editShortcuts", null); //$NON-NLS-1$ //$NON-NLS-2$

		showProgramInfo = shiftPage.addOption("showProgramInfo", OptionType.TEXTFIELD); //$NON-NLS-1$
		showProgramInfo.addStringValue("showProgramInfo", "I", 1).setSavedValue(PropertiesManager.getProgramInfo( )); //$NON-NLS-1$ //$NON-NLS-2$
		showProgramInfo.getRenderer( ).addValidator(toUpper);

		searchReports = ctrlPage.addOption("searchReports", OptionType.TEXTFIELD); //$NON-NLS-1$
		searchReports.addStringValue("searchReports", "S", 1).setSavedValue(PropertiesManager.getSearchReports( )); //$NON-NLS-1$ //$NON-NLS-2$
		searchReports.getRenderer( ).addValidator(toUpper);

		manageContacts = ctrlPage.addOption("manageContacts", OptionType.TEXTFIELD); //$NON-NLS-1$
		manageContacts.addStringValue("manageContacts", "C", 1).setSavedValue(PropertiesManager.getManageContacts( )); //$NON-NLS-1$ //$NON-NLS-2$
		manageContacts.getRenderer( ).addValidator(toUpper);

		// third
		SettingsPage altPage = settings.addPage("gui.ctrlAltShortcut", "editShortcuts", null); //$NON-NLS-1$ //$NON-NLS-2$

		showPluginInfo = altPage.addOption("showPluginInfo", OptionType.TEXTFIELD); //$NON-NLS-1$
		showPluginInfo.addStringValue("showPluginInfo", "I", 1).setSavedValue(PropertiesManager.getPluginInfo( )); //$NON-NLS-1$ //$NON-NLS-2$
		showPluginInfo.getRenderer( ).addValidator(toUpper);

		constructUIFromSettings(settings);

		settings.addSettingsChangedListener(new SettingsChangedListener( ) {

			public void settingsChanged(@SuppressWarnings("unused")
			SettingsChangedEvent event) {
				saveToIni( );
			}

		});

		// Get the commands and set up the table
		Collection<CommandDefinition> commands = CommandManager.getRobotIndependentCommandDefinitions( );
		if (commands != null && !commands.isEmpty( )) {
			XTabItem itemPlugins = new XTabItem(tabFolder, SWT.NONE);
			itemPlugins.setTextForLocaleKey("HotkeysFolder.gui.pluginShortcuts"); //$NON-NLS-1$
			// Fourth: Settings for key for the plugin commands
			XGroup grp = new XGroup(tabFolder, SWT.SHADOW_NONE);
			setGroupProperties(grp,
					I18n.getGenericI18n( ),
					"HotkeysFolder.gui.pluginHotkey.editShortcuts"); //$NON-NLS-1$
			SWTUtil.setGridLayout(grp, 1, true);

			XLabel label = new XLabel(grp, SWT.WRAP);
			setLabelProperties(label,
					I18n.getGenericI18n( ),
					"HotkeysFolder.gui.showPluginHotkeyInfo"); //$NON-NLS-1$
			SWTUtil.setGridData(label, true, false, SWT.FILL, SWT.TOP, 1, 1);

			// Table for setting the keys for commands
			final XTable table = new XTable(grp, SWT.FULL_SELECTION |
					SWT.SINGLE | SWT.BORDER);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);

			setupCommandsTable(table, commands);
			SWTUtil.packTable(table);
			table.setSize(table.computeSize(SWT.DEFAULT, 400));

			SWTUtil.setGridData(table, true, true, SWT.FILL, SWT.FILL, 1, 1);

			itemPlugins.setControl(grp);
		}

		finishInit( );
	}

	/**
	 * Sets up the table for configuring the mapping between commands
	 * and according key sequences and gamepad actions.
	 * 
	 * @param table
	 *            the table for the mappings.
	 * @param commands
	 *            the registered commands of the application
	 */
	private void setupCommandsTable(final Table table,
			Collection<CommandDefinition> commands) {
		String[] titles = {
				"HotkeysFolder.table.column.class", "HotkeysFolder.table.column.command", //$NON-NLS-1$ //$NON-NLS-2$ 
				"HotkeysFolder.table.column.key", "HotkeysFolder.table.column.gamepad"}; //$NON-NLS-1$//$NON-NLS-2$ 
		for (String title : titles) {
			XTableColumn column = new XTableColumn(table, SWT.NONE);
			column.setTextForLocaleKey(title);
		}

		for (CommandDefinition command : commands) {
			if (command.getCommandable( ) != null) {
				XTableItem item = new XTableItem(table,
						SWT.NONE,
						command.getHandler( ));
				item.setText(0, command.getCommandable( )
						.getClass( )
						.getSimpleName( ));
				item.setData(COMMAND, command);
				item.setTextForLocaleKey(1, command.getKey( ));

				String commandKey = CommandKeyManager.getKeyName(command);
				if (commandKey == null) {
					commandKey = ""; //$NON-NLS-1$
				}
				else {
					if (commandKey.length( ) == 1) {
						commandKey = commandKey.toUpperCase( );
					}
				}
				item.setText(2, commandKey);
				String gamepad = CommandGamepadManager.getGamepadConfig(command);
				if (gamepad == null) {
					gamepad = ""; //$NON-NLS-1$
				}
				item.setText(3, gamepad);
				gamepadMappings.put(command, gamepad);

				keyMappings.put(command,
						CommandKeyManager.getKeySequence(command));
			}
		}

		// }
		// add an empty item, for unselecting all other rows
		XTableItem item = new XTableItem(table, SWT.NONE);

		item.setText(0, ""); //$NON-NLS-1$
		item.setText(1, ""); //$NON-NLS-1$
		item.setText(2, ""); //$NON-NLS-1$
		item.setText(3, ""); //$NON-NLS-1$

		final TableEditor editor = new TableEditor(table);
		// The editor must have the same size as the cell and must
		// not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		table.addListener(SWT.MouseDown, new MouseDownListener(table,
				editor,
				2,
				3));
	}

	/**
	 * Tests if the given key is already used for an other command.
	 * 
	 * @param command
	 *            the actual command to check.
	 * @param seq
	 *            the key to check.
	 * @return <code>true</code> if the given key is free to be used
	 *         by the given command.
	 */
	private boolean isFree(CommandDefinition command, KeySequence seq) {
		if (seq.toString( ).equalsIgnoreCase(FORMAL_EMPTY_KEY)) {
			return true;
		}
		KeySequence old = keyMappings.get(command);
		if (old != null && old.equals(seq)) {
			return true;
		}
		else {
			return !keyMappings.values( ).contains(seq);
		}
	}

	/**
	 * Tests if the given gamepad action is already used for an other
	 * command.
	 * 
	 * @param command
	 *            the actual command to check.
	 * @param strg
	 *            the gamepad action to check
	 * @return <code>true</code> if the given action is free to be
	 *         used by the given command.
	 */
	private boolean isFree(CommandDefinition command, String strg) {
		String old = gamepadMappings.get(command);
		if (old != null && old.equals(strg)) {
			return true;
		}
		else {
			return !gamepadMappings.values( ).contains(strg);
		}
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

			public void modifyText(@SuppressWarnings("unused")
			ModifyEvent e) {
				char[] test = textField.getText( ).toCharArray( );
				if (test.length > 0 && Character.isLowerCase(test[0])) {
					textField.setText(textField.getText( ).toUpperCase( ));
				}
			}
		});
	}

	/**
	 * Saves the settings for hotkeys via the
	 * {@link de.xirp.settings.PropertiesManager}.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#save()
	 */
	@Override
	public void save() {
		settings.save( );

		for (Entry<CommandDefinition, KeySequence> entry : keyMappings.entrySet( )) {
			CommandKeyManager.addCommandKeyMapping(entry.getKey( ),
					entry.getValue( ));
		}
		CommandKeyManager.save( );
		for (Entry<CommandDefinition, String> entry : gamepadMappings.entrySet( )) {
			CommandGamepadManager.addCommandGamepadMapping(entry.getKey( ),
					entry.getValue( ));
		}
		CommandGamepadManager.save( );

		tableChanged = false;
	}

	/**
	 * This folder supports defaults.
	 * 
	 * @return <code>true</code>
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#supportsDefaults()
	 */
	@Override
	public boolean supportsDefaults() {
		return true;
	}

	/**
	 * This folder supports resets.
	 * 
	 * @return <code>true</code>
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#supportsReset()
	 */
	@Override
	public boolean supportsReset() {
		return true;
	}

	/**
	 * Returns whether the content hast changed or not. The table must
	 * be checked separately here, that's why the method has been
	 * overridden.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.AbstractContentFolderComposite#hasChanged()
	 */
	@Override
	public boolean hasChanged() {
		return super.hasChanged( ) || tableChanged;
	}

	/**
	 * Class for input of one key. The shown input is converted to
	 * upper case but the key sequence is left untouched.
	 * 
	 * @author Rabea Gransberger
	 */
	static class KeyText extends Text {

		/**
		 * The key sequence of this text field.
		 */
		private KeySequence sequence;
		/**
		 * Listener which is informed if the key has changed
		 */
		private List<IPropertyChangeListener> listeners = new ArrayList<IPropertyChangeListener>( );

		/**
		 * Constructs a new text field which allows to input one key,
		 * saves the corresponding key sequence and formats the input
		 * for the current language.
		 * 
		 * @param parent
		 *            the parent composite
		 * @param style
		 *            the style of this text field
		 */
		public KeyText(Composite parent, int style) {
			super(parent, style);
			init( );
		}

		/**
		 * Adds a listener which is informed if the key sequence has
		 * changed.
		 * 
		 * @param listener
		 *            the listener to add
		 */
		private void addPropertyChangeListener(IPropertyChangeListener listener) {
			listeners.add(listener);
		}

		/**
		 * Adds a key listener to this text field, checks the input
		 * key and informs the property change listeners if the key
		 * was verified.
		 */
		private void init() {
			addKeyListener(new KeyAdapter( ) {

				@Override
				public void keyPressed(KeyEvent evt) {
					if (getCaretPosition( ) < getTextLimit( )) {
						KeyStroke stroke = KeyStroke.getInstance(evt.stateMask,
								evt.keyCode);
						evt.doit = false;
						if (evt.keyCode != SWT.SHIFT && evt.stateMask == 0) {
							KeySequence seq = KeySequence.getInstance(stroke);
							sequence = seq;
							String formated = CommandKeyManager.format(seq);
							if (formated.length( ) == 1) {
								formated = formated.toUpperCase( );
							}
							setText(formated);
							for (IPropertyChangeListener listener : listeners) {
								listener.propertyChange(new PropertyChangeEvent(KeyText.this,
										"name", //$NON-NLS-1$
										null,
										null));
							}
						}
						else {
							sequence = null;
						}
					}
				}
			});
		}

		/**
		 * Gets the key sequence of this text field.
		 * 
		 * @return the sequence
		 */
		public KeySequence getKeySequence() {
			return sequence;
		}

		/**
		 * @see org.eclipse.swt.widgets.Widget#checkSubclass()
		 */
		@Override
		protected void checkSubclass() {
			// do nothing
		}

	}

	/**
	 * Listener which is registered on mouse down events of the
	 * hotkeys table and handles the input.
	 * 
	 * @author Rabea Gransberger
	 */
	private class MouseDownListener implements Listener {

		/**
		 * Constant for a column which was not found
		 */
		private static final int NOT_FOUND = -1;
		/**
		 * The item which is currently selected
		 */
		private TableItem item;
		/**
		 * The underlying table
		 */
		private final Table table;
		/**
		 * The editor for the current item
		 */
		private final TableEditor editor;
		/**
		 * Number of the key sequence column
		 */
		private final int keyColumn;
		/**
		 * Number of the gamepad column
		 */
		private final int gamepadColumn;
		/**
		 * The currently selected column
		 */
		private int selectedColumn;
		/**
		 * Textfield of the editor
		 */
		private Text editorText;

		/**
		 * Creates a new listener which allows key sequence and
		 * gamepad input for specific columns.
		 * 
		 * @param table
		 *            the underlying table
		 * @param editor
		 *            the editor which is used
		 * @param keyColumn
		 *            the column for key sequence input
		 * @param gamepadColumn
		 *            the column for gamepad input
		 */
		public MouseDownListener(final Table table, final TableEditor editor,
				int keyColumn, int gamepadColumn) {
			this.table = table;
			this.editor = editor;
			this.keyColumn = keyColumn;
			this.gamepadColumn = gamepadColumn;
			initListener( );
		}

		/**
		 * Initializes the gamepad listener if a gamepad column is
		 * present.
		 */
		private void initListener() {
			if (gamepadColumn != NOT_FOUND) {
				GamepadManager.addGamepadEventListener(new GamepadEventListener( ) {

					/**
					 * Constructs a string of axis type and value and
					 * checks if this action is already used.
					 * Otherwise it is bind to the command of the
					 * current item.
					 */
					@Override
					public void axisChanged(GamepadEvent e) {
						if (SWTUtil.swtAssert(editorText) &&
								selectedColumn == gamepadColumn) {
							Set<AxisType> axisTypes = e.getAxisTypes( );
							if (!axisTypes.isEmpty( )) {
								final AxisType type = axisTypes.iterator( )
										.next( );
								final String fullString = type.toString( );
								final TableItem item = editor.getItem( );

								SWTUtil.asyncExec(new Runnable( ) {

									@Override
									public void run() {
										CommandDefinition command = (CommandDefinition) item.getData(COMMAND);
										if (isFree(command, fullString)) {
											editorText.setText(fullString);
										}
									}

								});

							}

						}
					}

					/**
					 * Constructs a string of button type and checks
					 * if this action is already used. Otherwise it is
					 * bind to the command of the current item.
					 */
					@Override
					public void buttonPressed(GamepadEvent e) {
						if (SWTUtil.swtAssert(editorText) &&
								selectedColumn == gamepadColumn) {
							List<Integer> pressed = e.getPressed( );
							if (!pressed.isEmpty( )) {
								final String button = Integer.toString(pressed.get(0));
								final TableItem item = editor.getItem( );

								SWTUtil.asyncExec(new Runnable( ) {

									@Override
									public void run() {
										CommandDefinition command = (CommandDefinition) item.getData(COMMAND);
										if (isFree(command, button)) {
											editorText.setText(button);
										}
									}

								});
							}
						}
					}

				});
			}
		}

		/**
		 * Tries to find the column at which the mouse was clicked by
		 * traversing the tables items.
		 * 
		 * @param event
		 *            the event with the coordinates of the mouse
		 *            click
		 * @return the column at which the mouse was clicked or
		 *         {@value #NOT_FOUND} if no column was found
		 */
		private int findColumn(Event event) {
			Point pt = new Point(event.x, event.y);
			int index = table.getTopIndex( );
			while (index < table.getItemCount( )) {
				item = table.getItem(index);
				for (int i = 0; i < table.getColumnCount( ); i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						final int column = i;
						return column;
					}
				}
				index++;
			}

			return NOT_FOUND;

		}

		/**
		 * Executes if the mouse was hold down.<br>
		 * Checks if the mouse was hold down on the key sequence or
		 * gamepad column and sets up an editor in this case which
		 * allows to input a key sequence or gamepad action.
		 * 
		 * @param event
		 *            the events object
		 */
		public void handleEvent(Event event) {
			final int column = findColumn(event);

			if (column != NOT_FOUND &&
					(column == gamepadColumn || column == keyColumn)) {
				this.selectedColumn = column;

				// text field for editing
				final Text text;

				// key sequence column
				if (this.selectedColumn == keyColumn) {

					final KeyText keyText = new KeyText(table, SWT.NONE);
					text = keyText;

					keyText.addPropertyChangeListener(new IPropertyChangeListener( ) {

						@Override
						public void propertyChange(@SuppressWarnings("unused")
						PropertyChangeEvent event) {
							// get the command of the current item
							final TableItem item = editor.getItem( );
							CommandDefinition command = (CommandDefinition) item.getData(COMMAND);
							// Read the key sequence from the text
							// field and
							// check if it is useable.
							// If this is the case format the key
							// sequence and set it to the table and
							// fire a
							// changed event
							KeySequence seq = keyText.getKeySequence( );
							if (isFree(command, seq)) {
								String name = CommandKeyManager.format(seq);
								if (name != null) {
									// Delete mapping if the empty
									// key is hit
									String string = seq.toString( );

									if (string.equalsIgnoreCase(FORMAL_EMPTY_KEY)) {
										keyMappings.put(command, null);
										item.setText(selectedColumn, "");//$NON-NLS-1$
										text.setText("");//$NON-NLS-1$
									}
									else {
										keyMappings.put(command, seq);
										if (name.length( ) == 1) {
											name = name.toUpperCase( );
										}
										item.setText(selectedColumn, name);
									}
									tableChanged = true;
									fireChangeEvent( );
								}
							}
							else {
								text.setText(item.getText(selectedColumn));
							}
						}

					});
				}
				else {
					text = new Text(table, SWT.NONE);
					text.setEditable(false);
				}
				this.editorText = text;

				Listener textListener = new Listener( ) {

					@SuppressWarnings("fallthrough")
					public void handleEvent(final Event e) {
						final TableItem item = editor.getItem( );
						CommandDefinition command = (CommandDefinition) item.getData(COMMAND);
						switch (e.type) {
							case SWT.FocusOut:
								if (selectedColumn == gamepadColumn) {
									item.setText(column, text.getText( ));
									gamepadMappings.put(command, text.getText( ));
								}
								SWTUtil.secureDispose(text);
								break;
							case SWT.Traverse:
								switch (e.detail) {
									case SWT.TRAVERSE_RETURN:
										if (selectedColumn == gamepadColumn) {
											item.setText(column, text.getText( ));
											gamepadMappings.put(command,
													text.getText( ));
										}
										// FALL THROUGH
									case SWT.TRAVERSE_ESCAPE:
										SWTUtil.secureDispose(text);
										e.doit = false;
								}
								break;
						}
						tableChanged = true;
						fireChangeEvent( );
					}
				};
				text.addListener(SWT.FocusOut, textListener);
				text.addListener(SWT.Traverse, textListener);

				editor.setEditor(text, item, column);
				text.setText(item.getText(column));
				text.selectAll( );
				text.setFocus( );
			}
		}
	}

}
