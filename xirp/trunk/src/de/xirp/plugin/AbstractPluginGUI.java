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
 * AbstractPluginGUI.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 22.02.2006:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * Any Plugin which has a GUI has to extend this class to return the
 * GUI with {@link IPlugable#getGUI(Composite)}.
 * 
 * @author Rabea Gransberger
 */
public abstract class AbstractPluginGUI extends ViewerBase {

	/**
	 * Parent of this Composite
	 */
	protected Composite parent;
	/**
	 * Basic size of this composite. The composite will not have this
	 * size, but the scale values a computed relative to this size.
	 */
	protected Point baseSize = new Point(50, 50);
	/**
	 * Scaling factor on x-axis relative to the baseSize
	 */
	protected double scaleX = 1;
	/**
	 * Scaling factor on y-axis relative to the baseSize
	 */
	protected double scaleY = 1;
	/**
	 * The plugin which this GUI belongs to
	 */
	@SuppressWarnings("unchecked")
	private AbstractPlugin plugin;
	/**
	 * Default spacing for the edges of the UI
	 */
	protected static final int DEFAULT_SPACING = 10;
	/**
	 * Half of the default spacing
	 */
	protected static final int DEFAULT_SPACING_HALF = DEFAULT_SPACING / 2;

	/**
	 * Flag if the UI is currently disposing
	 */
	private boolean isDisposing = false;

	/**
	 * Constructs a new plugin GUI based on the {@link ViewerBase}
	 * 
	 * @param parent
	 *            the Parent Composite
	 * @param plugin
	 *            the plugin of this GUI
	 */
	@SuppressWarnings("unchecked")
	public AbstractPluginGUI(Composite parent, AbstractPlugin plugin) {
		this(parent, SWT.NONE, plugin);
	}

	/**
	 * Constructs a new plugin GUI based on the {@link ViewerBase}
	 * 
	 * @param parent
	 *            the Parent Composite
	 * @param style
	 *            style for the Composite
	 * @param plugin
	 *            the plugin of this GUI
	 */
	@SuppressWarnings("unchecked")
	public AbstractPluginGUI(Composite parent, int style, AbstractPlugin plugin) {
		super(parent, style, plugin.getPluginData( ));
		this.parent = parent;
		this.plugin = plugin;
		initListeners( );
	}

	/**
	 * Initialize paint and resize listeners
	 */
	private void initListeners() {
		this.addPaintListener(new PaintListener( ) {

			public void paintControl(PaintEvent event) {
				paint(event.gc);
			}

		});
		this.addControlListener(new ControlAdapter( ) {

			@Override
			public void controlResized(@SuppressWarnings("unused")
			ControlEvent event) {
				scaleX = getSize( ).x / (double) baseSize.x;
				scaleY = getSize( ).y / (double) baseSize.y;
				resized( );
			}

		});
		this.addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(@SuppressWarnings("unused")
			DisposeEvent event) {
				isDisposing = true;
				plugin.guiDisposed(AbstractPluginGUI.this);
				disposeEvent( );
			}

		});
	}

	/**
	 * Checks if this GUI is currently disposing.
	 * 
	 * @return <code>true</code> if the GUI is currently disposing
	 */
	public boolean isDisposing() {
		return isDisposing;
	}

	/**
	 * Called if redraw is called on this composite
	 * 
	 * @param gc
	 *            The graphics context to draw on
	 */
	protected void paint(@SuppressWarnings("unused")
	GC gc) {
		// do nothing as default
	}

	/**
	 * Called if control is resized.<br/> Do all size based
	 * calculations within this method and save the results to a local
	 * structure to be able to draw faster with this results.<br/>
	 * NOTE: the paint method is implicitly called on resize, so don't
	 * call it by yourself.
	 */
	protected void resized() {
		// do nothing as default
	}

	/**
	 * Called when composite is disposed.<br>
	 * Free all resources (colors, fonts, images, etc. which are not
	 * from the Managers) in here and remove all listeners from other
	 * classes.
	 */
	protected void disposeEvent() {
		// do nothing as default
	}

	/**
	 * Called if the locale of the application has changed and if the
	 * plugin of this GUI extends {@link AbstractPlugin}
	 * 
	 * @param locale
	 *            the new locale of the application
	 */
	protected void localeChanged(@SuppressWarnings("unused")
	Locale locale) {
		// do nothing as default
	}

	/**
	 * @see de.xirp.plugin.ViewerBase#sync(Control,
	 *      String)
	 */
	@Override
	protected final void sync(Control control, String propertyName) {
		super.sync(control, propertyName);
	}

	/**
	 * Takes a screenshot of the current UI of this plugin.<br>
	 * Remember to dispose the image after use.
	 * 
	 * @return the screenshot as an image
	 */
	public final Image captureScreenshot() {
		/* Take the screen shot */
		GC gc = new GC(this);
		final Display display = this.getDisplay( );
		final Image image = new Image(display, this.getBounds( ));
		gc.copyArea(image, 0, 0);
		gc.dispose( );

		return image;
	}
}
