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
 * AbstractLayoutPart.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * 					 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 11.06.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.novocode.naf.swt.custom.FramedComposite;

import de.xirp.plugin.IPlugable;
import de.xirp.ui.dock.DockingManager;
import de.xirp.ui.dock.ILayoutContainer;
import de.xirp.ui.dock.ILayoutPart;
import de.xirp.ui.dock.LayoutPart;
import de.xirp.ui.dock.PartTabFolder;
import de.xirp.ui.event.AppearanceChangedEvent;
import de.xirp.ui.event.AppearanceChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.util.ressource.ColorManager;
import de.xirp.ui.util.ressource.CursorManager;
import de.xirp.ui.util.ressource.ImageManager;
import de.xirp.ui.util.ressource.ImageManager.SystemImage;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.ui.widgets.custom.XLabel;
import de.xirp.ui.widgets.custom.XToolBar;
import de.xirp.ui.widgets.custom.XToolItem;
import de.xirp.ui.widgets.dialogs.GenericDialog;
import de.xirp.util.II18nHandler;
import de.xirp.util.Variables;

/**
 * This abstract class represents a part shown in the docking
 * workspace. Each panel which will be shown in the workspace must
 * extend this class.
 * 
 * @author Matthias Gernand
 */
public abstract class AbstractLayoutPart extends LayoutPart {

	/**
	 * A cursor.
	 */
	private static final Cursor CURSOR_ARROW = CursorManager.getCursor(SWT.CURSOR_ARROW);
	/**
	 * A cursor.
	 */
	private static final Cursor CURSOR_HAND = CursorManager.getCursor(SWT.CURSOR_HAND);
	/**
	 * A composite.
	 */
	private XComposite control;
	/**
	 * A label.
	 */
	private XLabel label;
	/**
	 * A docking manager.
	 */
	protected DockingManager dockingManager;
	/**
	 * A appearance change listener.
	 * 
	 * @see de.xirp.ui.event.AppearanceChangedListener
	 */
	private AppearanceChangedListener listener;
	/**
	 * A framed composite.
	 * 
	 * @see com.novocode.naf.swt.custom.FramedComposite
	 */
	private FramedComposite headComposite;
	/**
	 * A tool bar.
	 */
	private XToolBar toolbar;
	/**
	 * A robot name.
	 */
	private String robotName;

	/**
	 * Constructs a new layout part.
	 * 
	 * @param id
	 *            The id.
	 * @param dm
	 *            The docking manager.
	 * @param robotName
	 *            The robot name.
	 */
	public AbstractLayoutPart(String id, DockingManager dm, String robotName) {
		super(id);
		dockingManager = dm;
		this.robotName = robotName;
	}

	/**
	 * Returns <code>true</code> if the view pane is visible.
	 * 
	 * @see LayoutPart#isViewPane()
	 */
	@Override
	public boolean isViewPane() {
		return true;
	}

	/**
	 * Returns the name of the part.
	 * 
	 * @see de.xirp.ui.dock.LayoutPart#getName()
	 */
	@Override
	public String getName() {
		II18nHandler handler = getI18nHandler( );
		if (handler != null) {
			return handler.getString(getNameKey( ));
		}
		return getNameKey( );
	}

	/**
	 * Returns the i18n name key.
	 * 
	 * @see LayoutPart#getName()
	 */
	@Override
	public abstract String getNameKey();

	/**
	 * Creates the content holding control.
	 * 
	 * @see LayoutPart#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		if (SWTUtil.swtAssert(control)) {
			return;
		}

		control = new XComposite(parent, SWT.NONE);
		GridLayout gridLayout = SWTUtil.setGridLayout(control, 1, true);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;

		headComposite = new FramedComposite(control, SWT.NONE);
		// HComposite headComposite = new HComposite(control,
		// SWT.SHADOW_ETCHED_OUT | SWT.BORDER);
		headComposite.setBackground(ColorManager.getColor(Variables.BACKGROUND_COLOR));
		GridData gridData = SWTUtil.setGridData(headComposite,
				true,
				false,
				SWT.FILL,
				SWT.CENTER,
				1,
				1);
		gridData.heightHint = 22;
		gridLayout = SWTUtil.setGridLayout(headComposite, 2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;

		String key = getNameKey( );
		if (key != null) {
			II18nHandler handler = getI18nHandler( );
			label = new XLabel(headComposite, SWT.NONE, handler);
			label.setTextForLocaleKey(key);
			label.setToolTipTextForLocaleKey(key);
		}
		else {
			label = new XLabel(headComposite, SWT.NONE);
			label.setText(getName( ));
			label.setToolTipText(getName( ));
		}

		label.addMouseTrackListener(new MouseTrackAdapter( ) {

			@Override
			public void mouseEnter(MouseEvent event) {
				label.setCursor(CURSOR_HAND);

			}

			@Override
			public void mouseExit(MouseEvent event) {
				label.setCursor(CURSOR_ARROW);
			}
		});
		SWTUtil.setGridData(label, true, true, SWT.FILL, SWT.CENTER, 1, 1);

		toolbar = new XToolBar(headComposite, SWT.FLAT);
		final XToolItem zoomButton = new XToolItem(toolbar, SWT.PUSH);
		zoomButton.addListener(SWT.Selection, new Listener( ) {

			public void handleEvent(Event e) {
				if (!dockingManager.isZoomed( )) {
					dockingManager.zoomIn(AbstractLayoutPart.this);
					zoomButton.setImage(ImageManager.getSystemImage(SystemImage.MINIMIZE));
				}
				else {
					dockingManager.zoomOut( );
					zoomButton.setImage(ImageManager.getSystemImage(SystemImage.MAGNIFY));
				}
			}
		});
		zoomButton.setImage(ImageManager.getSystemImage(SystemImage.MAGNIFY));

		if (this.canShowInWindow( )) {
			final XToolItem windowButton = new XToolItem(toolbar, SWT.PUSH);
			windowButton.addListener(SWT.Selection, new Listener( ) {

				public void handleEvent(Event e) {
					if (AbstractLayoutPart.this instanceof GenericLayoutPart) {
						GenericLayoutPart generic = (GenericLayoutPart) AbstractLayoutPart.this;
						IPlugable plugin = generic.getPlugin( );
						String mainClass = plugin.getInfo( ).getMainClass( );

						GenericDialog gd = new GenericDialog(control.getShell( ));
						gd.setOrigin(generic);
						String robotName = plugin.getRobotName( );

						gd.open(mainClass,
								plugin.getIdentifier( ),
								robotName,
								true);

						dockingManager.removePart(generic);
					}
					else {
						GenericDialog gd = new GenericDialog(control.getShell( ));
						gd.setOrigin(AbstractLayoutPart.this);
						gd.open(robotName, true);

						dockingManager.removePart(AbstractLayoutPart.this);
					}
				}
			});
			windowButton.setImage(ImageManager.getSystemImage(SystemImage.WINDOW));
		}
		toolbar.pack( );
		gridData = SWTUtil.setGridData(toolbar,
				false,
				false,
				SWT.END,
				SWT.CENTER,
				1,
				1);
		if (this.canShowInWindow( )) {
			gridData.widthHint = 46;
		}
		else {
			gridData.widthHint = 23;
		}

		final XComposite mainControl = new XComposite(control, SWT.NONE);
		// ScrolledComposite mainControl = new
		// ScrolledComposite(control, SWT.V_SCROLL | SWT.H_SCROLL);
		mainControl.setBackground(ColorManager.getColor(Variables.BACKGROUND_COLOR));
		gridLayout = SWTUtil.setGridLayout(mainControl, 1, true);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		SWTUtil.setGridData(mainControl, true, true, SWT.FILL, SWT.FILL, 1, 1);

		Composite content = createContent(mainControl);
		// Composite content = new Composite(mainControl, SWT.NONE);
		// content.setBackground(new Color(null,255,0,0));
		// content.setSize(content.computeSize(SWT.DEFAULT,
		// SWT.DEFAULT));
		SWTUtil.setGridData(content, true, true, SWT.FILL, SWT.FILL, 1, 1);
		// mainControl.setContent(content);

		mainControl.forceFocus( );

		// headComposite.addMouseTrackListener(new MouseTrackAdapter()
		// {
		//
		// @Override
		// public void mouseEnter(MouseEvent e) {
		// ((GridData)headComposite.getLayoutData()).heightHint = 22;
		// headComposite.setVisible(true);
		// headComposite.getParent().layout();
		//				
		// }
		//
		// @Override
		// public void mouseExit(MouseEvent e) {
		// ((GridData)headComposite.getLayoutData()).heightHint = 0;
		// headComposite.setVisible(false);
		// headComposite.getParent().layout();
		//				
		// }
		//			
		// });

		headComposite.setBackground(ColorManager.getColor(Variables.PANEL_HEADER_COLOR));
		label.setBackground(ColorManager.getColor(Variables.PANEL_HEADER_COLOR));
		toolbar.setBackground(ColorManager.getColor(Variables.PANEL_HEADER_COLOR));

		listener = new AppearanceChangedListener( ) {

			public void appearanceChanged(AppearanceChangedEvent event) {
				headComposite.setBackground(ColorManager.getColor(Variables.PANEL_HEADER_COLOR));
				label.setBackground(ColorManager.getColor(Variables.PANEL_HEADER_COLOR));
				toolbar.setBackground(ColorManager.getColor(Variables.PANEL_HEADER_COLOR));
			}

		};

		ApplicationManager.addAppearanceChangedListener(listener);

		control.addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(DisposeEvent e) {
				ApplicationManager.removeAppearanceChangedListener(listener);

			}

		});
	}

	/**
	 * Creates the content composite of the part.
	 * 
	 * @param parent
	 *            The parent.
	 * @return The content composite.
	 */
	public abstract Composite createContent(Composite parent);

	/**
	 * Returns the control.
	 * 
	 * @return The control.
	 * @see LayoutPart#getControl()
	 */
	@Override
	public Control getControl() {
		return control;
	}

	/**
	 * Returns the drag handle. In this case it returns the label.
	 * 
	 * @return The drag handle.
	 * @see LayoutPart#getDragHandle()
	 */
	@Override
	public Control getDragHandle() {
		return label;
	}

	/**
	 * Returns the target part.
	 * 
	 * @see LayoutPart#targetPartFor(de.xirp.ui.dock.ILayoutPart)
	 */
	@Override
	public ILayoutPart targetPartFor(ILayoutPart dragSource) {
		ILayoutContainer container = getContainer( );
		if (container instanceof PartTabFolder) {
			return (PartTabFolder) container;
		}
		else {
			return this;
		}
	}

	/**
	 * Re-adds the part to the workspace.
	 */
	public void reAdd() {
		this.dockingManager.addPart(this);
	}

	/**
	 * Removes and returns the part.
	 * 
	 * @return The removed part.
	 */
	public AbstractLayoutPart removePart() {
		dockingManager.removePart(this);
		return this;
	}

	/**
	 * Returns the contents class.
	 * 
	 * @return The class.
	 */
	public abstract Class<? extends Composite> getContentClass();

	/**
	 * Indicates if this part can be shown in an own window.
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>: Can be shown in own window.<br>
	 *         <code>false</code>: Can not be shown in own window.<br>
	 */
	public abstract boolean canShowInWindow();
}
