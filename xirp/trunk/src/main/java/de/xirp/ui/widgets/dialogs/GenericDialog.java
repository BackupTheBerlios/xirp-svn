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
 * GenericDialog.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 14.03.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.dialogs;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import de.xirp.plugin.IPlugable;
import de.xirp.plugin.PluginManager;
import de.xirp.plugin.VisualizationType;
import de.xirp.ui.event.LocaleChangedEvent;
import de.xirp.ui.event.LocaleChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XDialog;
import de.xirp.ui.widgets.custom.XShell;
import de.xirp.ui.widgets.panels.AbstractLayoutPart;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This dialog is used to open the GUI of a plugin in an own window, or
 * non-plugin panels of the workspace, f.e. the live chart composite.
 * 
 * @author Matthias Gernand TODO: Size issues
 */
public final class GenericDialog extends XDialog {

	/**
	 * 
	 */
	private static final String ROBOT_NAME_SEPARATOR = ": ";
	/**
	 * The Logger for this class.
	 */
	private static Logger logClass = Logger.getLogger(GenericDialog.class);
	/**
	 * The shell of the dialog.
	 */
	private XShell dialogShell = null;
	/**
	 * The parent shell.
	 */
	private Shell parent = null;
	/**
	 * The composite which will be created by the starting plugin.
	 */
	private Composite comp;
	/**
	 * The plugins instance.
	 */
	private IPlugable plugin;
	/**
	 * A abstract layout part.
	 */
	private AbstractLayoutPart origin;
	/**
	 * A shell listener.
	 */
	private ShellAdapter listener;
	/**
	 * A locale change listener
	 */
	private LocaleChangedListener localeListener;

	/**
	 * Was plugin loaded as unreferenced?
	 */
	private boolean unref = false;

	/**
	 * Constructs a new generic dialog which can display generic content.
	 * 
	 * @param parent
	 *            The parent shell.
	 */
	public GenericDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		this.parent = parent;
		init();
	}

	/**
	 * Initializes the listeners. And invokes the plugin.
	 */
	private void init() {
		listener = new ShellAdapter() {

			@Override
			public void shellClosed(ShellEvent e) {
				if (origin != null) {
					origin.reAdd();
				}
				if (plugin != null) {
					if (unref) {
						PluginManager.stopUnreferencedPlugin(plugin.getClass()
								.getName(), plugin.getInstanceID());
					} else {
						PluginManager.stopPlugin(plugin.getRobotName(), plugin
								.getClass().getName(), plugin.getIdentifier());
					}
				}
				SWTUtil.secureDispose(dialogShell);
				ApplicationManager.removeLocaleChangedListener(localeListener);
			}
		};
	}

	/**
	 * Closes the dialog.
	 */
	public void close() {
		if (SWTUtil.swtAssert(dialogShell)) {
			dialogShell.close();
		}
	}

	/**
	 * Opens the dialog for the given plugin class name, robot name and flag
	 * indicating if the window should be resizeable.
	 * 
	 * @param className
	 *            The class name.
	 * @param robotName
	 *            The robot name.
	 * @param resizeable
	 *            A flag indicating if the dialog should be resizeable.
	 */
	public void open(String className, String robotName, boolean resizeable) {
		open(className, null, robotName, resizeable);
	}

	/**
	 * Opens the generic dialog and loads the content for plugin-origin parts.
	 * 
	 * @param className
	 *            The class name of the content.
	 * @param key
	 *            key if the class name is that of a plugin. Might be
	 *            <code>null</code>.
	 * @param robotName
	 *            The name of the robot.
	 * @param resizeable
	 *            Sets the dialog resizeable <code>true</code>: resizeable<br>
	 *            <code>false:</code> static size.
	 */
	public void open(String className, String key, String robotName,
			boolean resizeable) {
		initDialogShell(resizeable);

		if (className != null && robotName != null) {
			try {
				if (PluginManager.isPlugin(className)) {
					IPlugable plugin = PluginManager.runPlugin(robotName,
							className, key);

					if (plugin == null) {
						plugin = PluginManager.runUnreferencedPlugin(className,
								robotName);
						unref = true;
					}
					if (plugin != null
							&& (VisualizationType.containsType(plugin,
									VisualizationType.WINDOW) || VisualizationType
									.containsType(plugin,
											VisualizationType.EMBEDDED))) {
						this.plugin = plugin;
						dialogShell.dispose();
						initDialogShell(plugin.getWindowStyle());
						comp = plugin.getGUI(dialogShell);

					}
				} else {
					comp = getComposite(className, dialogShell);
				}
				if (comp != null) {
					SWTUtil.setGridData(comp, true, true, SWT.FILL, SWT.FILL,
							1, 1);
				}
			} catch (InstantiationException e) {
				logClass
						.error(I18n
								.getString("GenericDialog.log.errorWhileLoadingDialogContent") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
				logClass.error(e.getMessage() + Constants.LINE_SEPARATOR);
				logClass
						.info(I18n
								.getString("GenericDialog.log.infoMessageConstuctors") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
				dialogShell.close();
				return;
			}
		}

		openGenericDialog(plugin, robotName);

	}

	/**
	 * Opens the dialog.
	 * 
	 * @param plugin
	 *            The plugin. may be <code>null</code> if an internal panel
	 *            should be opened.
	 * @param robotName
	 *            the robot name.
	 */
	private void openGenericDialog(final IPlugable plugin,
			final String robotName) {
		if (comp != null) {
			dialogShell.layout();
			dialogShell.pack();
			SWTUtil.centerDialog(dialogShell);

			if (plugin != null) {
				dialogShell.setImage(ImageManager
						.getSystemImage(SystemImage.PLUGIN));
				dialogShell.setText(robotName + ROBOT_NAME_SEPARATOR
						+ plugin.getHandler().getString(plugin.getNameKey()));
				localeListener = new LocaleChangedListener() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see de.xirp.ui.event.LocaleChangedListener#localeChanged(de.xirp.ui.event.LocaleChangedEvent)
					 */
					public void localeChanged(LocaleChangedEvent event) {
						dialogShell.setText(robotName
								+ ROBOT_NAME_SEPARATOR
								+ plugin.getHandler().getString(
										plugin.getNameKey()));
					}

				};
			} else {
				dialogShell.setImage(ImageManager
						.getSystemImage(SystemImage.WINDOW));
				dialogShell
						.setText(robotName + ROBOT_NAME_SEPARATOR + (origin == null ? "unknown"
								: I18n.getString(origin.getNameKey())));
				localeListener = new LocaleChangedListener() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see de.xirp.ui.event.LocaleChangedListener#localeChanged(de.xirp.ui.event.LocaleChangedEvent)
					 */
					public void localeChanged(LocaleChangedEvent event) {
						dialogShell.setText(robotName + ROBOT_NAME_SEPARATOR
								+ (origin == null ? "unknown" : I18n
								.getString(origin.getNameKey())));
					}

				};
			}
			ApplicationManager.addLocaleChangedListener(localeListener);
			dialogShell.open();
		} else {
			logClass
					.info(I18n
							.getString("GenericDialog.log.genericDialogCouldNotBeOpened") //$NON-NLS-1$
							+ Constants.LINE_SEPARATOR);
			dialogShell.close();
		}
	}

	/**
	 * Creates a shell using the given style. If the style contains
	 * {@link SWT#NO_TRIM} the shell is maximized.
	 * 
	 * @param style
	 *            the style to use for the dialog shell
	 */
	private void initDialogShell(int style) {

		dialogShell = new XShell(parent, style);

		dialogShell.addShellListener(listener);

		if (SWTUtil.checkStyle(style, SWT.NO_TRIM)) {
			dialogShell.setMaximized(true);
		}

		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginBottom = 0;
		gl.marginHeight = 0;
		gl.marginLeft = 0;
		gl.marginRight = 0;
		gl.marginTop = 0;
		gl.marginWidth = 0;
		dialogShell.setLayout(gl);

	}

	/**
	 * Initializes the dialog.
	 * 
	 * @param resizeable
	 *            Sets the dialog resizeable <code>true</code>: resizeable<br>
	 *            <code>false:</code> static size.
	 */
	private void initDialogShell(boolean resizeable) {
		if (resizeable) {
			initDialogShell(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
		} else {
			initDialogShell(SWT.DIALOG_TRIM);

		}

	}

	/**
	 * Returns the content composite for the given robot name, content class and
	 * parent composite.
	 * 
	 * @param robotName
	 *            The robot name.
	 * @param contentClass
	 *            The class of the content.
	 * @param parent
	 *            The parent.
	 * @return The content composite.
	 * @throws InstantiationException
	 *             when no suitable constructor could be found.
	 */
	private Composite getComposite(String robotName,
			Class<? extends Composite> contentClass, Composite parent)
			throws InstantiationException {

		try {
			if (robotName == null) {
				Constructor<? extends Composite> constr = contentClass
						.getConstructor(new Class[] { Composite.class,
								int.class });
				return constr.newInstance(new Object[] { parent, SWT.NONE });
			} else {
				Constructor<? extends Composite> constr = contentClass
						.getConstructor(new Class[] { Composite.class,
								String.class });
				return constr.newInstance(new Object[] { parent, robotName });
			}
		} catch (Exception e) {
			String message = e.getMessage();
			if (message == null) {
				message = e.getCause().getMessage();
			}
			throw new InstantiationException(message);
		}
	}

	/**
	 * Loads the generic content from the given class name and returns the
	 * content as composite.
	 * 
	 * @param className
	 *            The class to load.
	 * @param parent
	 *            The parent composite.
	 * @return Composite with the loaded content.
	 * @throws InstantiationException
	 *             when no suitable constructor could be found.
	 */
	@SuppressWarnings("unchecked")
	private Composite getComposite(String className, Composite parent)
			throws InstantiationException {
		Composite comp = null;
		try {
			Class<Composite> clazz = (Class<Composite>) Class
					.forName(className);
			Constructor[] constructors = clazz.getConstructors();
			Constructor useConstructor = null;
			int length = -1;
			for (Constructor constructor : constructors) {
				Class[] types = constructor.getParameterTypes();
				if (types.length == 2 && length != 1) {
					if ((types[0] == Composite.class)
							&& (types[1] == int.class)) {
						useConstructor = constructor;
						length = types.length;
					}
				} else if (types.length == 1) {
					if (types[0] == Composite.class) {
						useConstructor = constructor;
						length = types.length;
					}
				}
			}
			if (useConstructor == null) {
				throw new InstantiationException(
						"No suitable Constructor found"); //$NON-NLS-1$
			} else {
				Object[] arglist = new Object[length];
				if (length == 2) {
					arglist[0] = parent;
					arglist[1] = SWT.NONE;
					comp = (Composite) useConstructor.newInstance(arglist);
				} else if (length == 1) {
					arglist[0] = dialogShell;
					comp = (Composite) useConstructor.newInstance(arglist);
				}
			}
		} catch (Exception e) {
			String message = e.getMessage();
			if (message == null) {
				message = e.getCause().getMessage();
			}
			throw new InstantiationException(message);
		}
		return comp;
	}

	/**
	 * Set the origin part of the dialog.
	 * 
	 * @param origin
	 *            AbstractLayoutPart
	 */
	public void setOrigin(AbstractLayoutPart origin) {
		this.origin = origin;
	}

	/**
	 * Returns the content composite.
	 * 
	 * @return The content composite.
	 */
	public Composite getComposite() {
		return comp;
	}

	/**
	 * Opens a generic dialog for non-plugin origin parts.
	 * 
	 * @param robotName
	 *            The robot name.
	 * @param resizeable
	 *            Sets the dialog resizeable<br>
	 *            <code>true</code> resizeable<br>
	 *            <code>false</code> static size
	 */
	public void open(String robotName, boolean resizeable) {
		initDialogShell(resizeable);

		try {
			comp = getComposite(robotName, origin.getContentClass(),
					dialogShell);
		}

		catch (InstantiationException ex) {
			logClass.error("Error: " + ex.getMessage() //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, ex);
			dialogShell.close();
		}
		if (comp != null) {
			SWTUtil.setGridData(comp, true, true, SWT.FILL, SWT.FILL, 1, 1);
		}

		openGenericDialog(plugin, robotName);
	}
}
