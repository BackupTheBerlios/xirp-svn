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
 * AbstractContentFolderComposite.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 16.03.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs.preferences;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.xirp.settings.Option;
import de.xirp.settings.Settings;
import de.xirp.settings.SettingsChangedEvent;
import de.xirp.settings.SettingsChangedListener;
import de.xirp.settings.SettingsPage;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.FontManager;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.ui.widgets.custom.XGroup;
import de.xirp.ui.widgets.custom.XLabel;
import de.xirp.ui.widgets.custom.XStyledText;
import de.xirp.ui.widgets.custom.XTabFolder;
import de.xirp.ui.widgets.custom.XTabItem;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * Abstract class represents a settings folder. Some UI methods are
 * already implemented. Just the text field properties must be
 * implemented by any class extending this abstract class.
 * 
 * @author Matthias Gernand
 */
public abstract class AbstractContentFolderComposite extends XComposite {

	/**
	 * A i18n handler.
	 */
	private II18nHandler handler = I18n.getGenericI18n( );
	/**
	 * The tab folder.
	 */
	protected XTabFolder tabFolder;
	/**
	 * A {@link org.eclipse.swt.layout.GridLayout} object.
	 * 
	 * @see org.eclipse.swt.layout.GridLayout
	 */
	protected GridLayout gl;
	/**
	 * The titleKey of the folder.
	 */
	private String titleKey;
	/**
	 * The path to the image to be shown in the list of the
	 * preferences dialog.
	 */
	private String imagePath;
	/**
	 * A {@link de.xirp.settings.Settings} object.
	 * 
	 * @see de.xirp.settings.Settings
	 */
	protected Settings settings;
	/**
	 * The
	 * {@link de.xirp.settings.SettingsChangedListener settings}
	 * change listeners.
	 */
	private Vector<SettingsChangedListener> listeners = new Vector<SettingsChangedListener>( );

	/**
	 * Constructs a new folder composite.
	 * 
	 * @param parent
	 *            The parent composite.
	 * @param style
	 *            The style.
	 */
	public AbstractContentFolderComposite(Composite parent, int style) {
		super(parent, style);
		initFolder( );
	}

	/**
	 * Initializes the tab folder and layout.
	 */
	private void initFolder() {
		tabFolder = new XTabFolder(this, SWT.TOP | SWT.MULTI);
		gl = SWTUtil.setGridLayout(this, 1, true);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginBottom = 0;
		gl.marginTop = 0;
		gl.marginLeft = 0;
		gl.marginRight = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		SWTUtil.setGridData(tabFolder, true, true, SWT.FILL, SWT.FILL, 1, 1);

	}

	/**
	 * Sets the first tab item selected.
	 */
	protected void finishInit() {
		tabFolder.setSelection(0);
	}

	/**
	 * Constructs the UI from the
	 * {@link de.xirp.settings.Settings settings} object.
	 * 
	 * @param settings
	 *            The settings.
	 */
	protected void constructUIFromSettings(Settings settings) {
		for (SettingsPage page : settings.getPages( )) {
			XTabItem item = new XTabItem(tabFolder, SWT.NONE, page.getI18n( ));
			item.setTextForLocaleKey(page.getNameKey( ), page.getNameKeyArgs( ));

			XGroup grp = new XGroup(tabFolder, SWT.NONE, page.getI18n( ));
			setGroupProperties(grp,
					page.getI18n( ),
					page.getShortDescriptionKey( ),
					page.getShortKeyArgs( ));
			SWTUtil.setGridLayout(grp, 2, false);

			if (page.getLongDescriptionKey( ) != null) {
				addLongDescription(grp,
						page.getI18n( ),
						page.getLongDescriptionKey( ),
						page.getLongKeyArgs( ));
			}

			for (Option option : page.getOptions( )) {
				option.getRenderer( ).render(grp, option);
			}

			item.setControl(grp);
		}
	}

	/**
	 * Adds the given long description to the settings page.
	 * 
	 * @param parent
	 *            The parent.
	 * @param handler
	 *            The handler.
	 * @param key
	 *            The key.
	 * @param objects
	 *            The args.
	 */
	protected void addLongDescription(Composite parent, II18nHandler handler,
			String key, Object... objects) {
		XStyledText text = new XStyledText(parent, SWT.WRAP, handler, true);
		text.setTextForLocaleKey(key, objects);
		text.setEditable(false);
		text.setEnabled(false);
		text.setFont(FontManager.getFont("Arial", 8, SWT.BOLD)); //$NON-NLS-1$
		SWTUtil.setGridData(text, true, false, SWT.LEFT, SWT.CENTER, 2, 1);
	}

	/**
	 * Adds the given settings change
	 * {@link de.xirp.settings.SettingsChangedListener listener}.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public void addSettingsChangeListener(SettingsChangedListener listener) {
		if (settings != null) {
			settings.addImmediateChangeListener(listener);
		}
		// else {TODO: only one listener
		listeners.add(listener);
		// }
	}

	/**
	 * Removes the given
	 * {@link de.xirp.settings.SettingsChangedListener listener}.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeSettingsChangeListener(SettingsChangedListener listener) {
		if (settings != null) {
			settings.removeImmediateChangeListener(listener);
		}
		else {
			listeners.remove(listener);
		}
	}

	/**
	 * Fires a
	 * {@link de.xirp.settings.SettingsChangedEvent} to
	 * all listeners.
	 */
	protected void fireChangeEvent() {
		SettingsChangedEvent event = new SettingsChangedEvent(this);
		for (SettingsChangedListener l : listeners) {
			l.settingsChanged(event);
		}
	}

	/**
	 * Save method. Can be overridden by the specialized folder, f.e.
	 * to save properties to the ini file.
	 */
	public void save() {
		if (settings != null) {
			settings.save( );
		}
	}

	/**
	 * Resets the current changes to the saved settings. May be left
	 * empty if resets are not supported.
	 */
	public void reset() {
		if (settings != null && supportsReset( )) {
			settings.reset( );
		}
	}

	/**
	 * Checks if resets are supported.
	 * 
	 * @return <code>true</code> if this folder supports resets.
	 */
	public abstract boolean supportsReset();

	/**
	 * Resets the current changes to the default settings. May be left
	 * empty if defaults are not supported.
	 */
	public void restoreDefaults() {
		if (settings != null && supportsDefaults( )) {
			settings.restoreDefaults( );
		}
	}

	/**
	 * Checks if the default is selected.
	 * 
	 * @return <code>true</code> if the default is selected.
	 */
	public boolean isDefaultSelected() {
		if (settings != null) {
			return settings.isDefaultSelected( );
		}
		return false;
	}

	/**
	 * Check if defaults are supported.
	 * 
	 * @return <code>true</code> if this folder supports restoring
	 *         defaults.
	 */
	public abstract boolean supportsDefaults();

	/**
	 * Check if the settings in this folder have changed compared to
	 * the saved or default settings.
	 * 
	 * @return <code>true</code> if the settings have changed.
	 */
	public boolean hasChanged() {
		if (settings != null) {
			return settings.hasChanged( );
		}
		return false;
	}

	/**
	 * Layouts the given
	 * {@link de.xirp.ui.widgets.custom.XLabel label}.
	 * 
	 * @param label
	 *            The label to layout.
	 * @param titleKey
	 *            The i18n key of the label.
	 */
	protected void setLabelProperties(XLabel label, II18nHandler handler,
			String titleKey, Object... objects) {
		label.setTextForLocaleKey(titleKey, objects);
	}

	/**
	 * Layouts a group.
	 * 
	 * @param group
	 *            The group to layout.
	 * @param titleKey
	 *            The title key of the group.
	 */
	protected void setGroupProperties(XGroup group, II18nHandler handler,
			String titleKey, Object... objects) {
		group.setTextForLocaleKey(titleKey, objects);
	}

	/**
	 * Sets the title key of the folder.
	 * 
	 * @param titleKey
	 *            The title key to set.
	 */
	public void setTitleKey(String titleKey) {
		this.titleKey = titleKey;

	}

	/**
	 * Returns the title key of the folder.
	 * 
	 * @return The title key.
	 */
	public String getTitleKey() {
		return titleKey;
	}

	/**
	 * Returns the image path to the icon for the list item.
	 * 
	 * @return The image path.
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * Sets the image path to the icon for the list item.
	 * 
	 * @param path
	 *            The path to the icon.
	 */
	public void setImagePath(String path) {
		this.imagePath = path;
	}

	/**
	 * Returns the handler.
	 * 
	 * @return The handler.
	 */
	public II18nHandler getHandler() {
		return handler;
	}

	/**
	 * Sets the handler.
	 * 
	 * @param handler
	 *            The handler to set.
	 */
	public void setHandler(II18nHandler handler) {
		this.handler = handler;
	}
}
