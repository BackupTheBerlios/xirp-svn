package de.xirp.ui.dock;

/*********************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights
 * reserved. This program and the accompanying materials! are made
 * available under the terms of the Common Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 * Cagatay Kavukcuoglu <cagatayk@acm.org> - Fix for bug 10025 -
 * Resizing views should not use height ratios
 ********************************************************************/

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * A presentation part is used to build the presentation for the<br>
 * workbench. Common subclasses are pane and folder.<br>
 */
public abstract class LayoutPart implements ILayoutPart {
	/**
	 * 
	 */
	public static final String PROP_VISIBILITY = "PROP_VISIBILITY"; //$NON-NLS-1$
	/**
	 * 
	 */
	protected ILayoutContainer container;
	/**
	 * 
	 */
	protected String id;
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private Boolean visible;
	/**
	 * 
	 */
	private ListenerList propertyListeners = new ListenerList(1);

	/**
	 * 
	 */
	protected boolean isZoomed;

	/**
	 * PresentationPart constructor comment.<br>
	 * 
	 * @param id
	 */
	public LayoutPart(String id) {
		super( );
		this.id = id;
	}

	/**
	 * @see ILayoutPart#getDragHandle()
	 */
	public Control getDragHandle() {
		return null;
	}

	/**
	 * @param value
	 */
	public void setID(String value) {
		id = value;
	}

	/**
	 * @see ILayoutPart#isViewPane()
	 */
	public abstract boolean isViewPane();

	/**
	 * @see ILayoutPart#getName()
	 */
	public String getName() {
//		return name == null ? id : name;
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.xirp.ui.dock.ILayoutPart#getNameKey()
	 */
	public String getNameKey() {
		return null;
	}


	/**
	 * @see ILayoutPart#setName(java.lang.String)
	 */
	public void setName(String s) {
		name = s;
	}

	/**
	 * Adds a property change listener to this action. Has no effect<br>
	 * if an identical listener is already registered.<br>
	 * 
	 * @param listener
	 *            a property change listener
	 */
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		propertyListeners.add(listener);
	}

	/**
	 * Removes the given listener from this action. Has no effect if<br>
	 * an identical listener is not registered.<br>
	 * 
	 * @param listener
	 *            a property change listener
	 */
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		propertyListeners.remove(listener);
	}

	/**
	 * Creates the SWT control.<br>
	 * 
	 * @param parent
	 */
	abstract public void createControl(Composite parent);

	/**
	 * Disposes the SWT control.<br>
	 */
	public void dispose() {
	}

	/**
	 * Gets the presentation bounds.<br>
	 * 
	 * @return Rectangle
	 */
	public Rectangle getBounds() {
		Control ctrl = this.getControl( );
		if (ctrl == null) {
			return new Rectangle(0, 0, 0, 0);
		}
		else {
			return ctrl.getBounds( );
		}
	}

	/**
	 * Sets the presentation bounds.<br>
	 * 
	 * @param value
	 */
	public void setBounds(Rectangle value) {
		Control ctrl = this.getControl( );
		if (ctrl != null) {
			ctrl.setBounds(value);
		}
	}

	/**
	 * Gets root container for this part.<br>
	 * 
	 * @return RootLayoutContainer
	 */
	public RootLayoutContainer getRootContainer() {
		if (container != null) {
			return container.getRootContainer( );
		}
		return null;
	}

	/**
	 * Gets the parent for this part.<br>
	 * 
	 * @return ILayoutContainer
	 */
	public ILayoutContainer getContainer() {
		return container;
	}

	/**
	 * @see ILayoutPart#setContainer
	 */
	public void setContainer(ILayoutContainer value) {
		container = value;
	}

	/**
	 * Get the part control. This method may return null.<br>
	 * 
	 * @return Control
	 */
	abstract public Control getControl();

	/**
	 * Gets the ID for this part.<br>
	 * 
	 * @return String
	 */
	public String getID() {
		return id;
	}

	/**
	 * @param value
	 */
	public void setString(String value) {
		id = value;
	}

	/**
	 * Return the place the preferences used by layout parts reside.
	 * 
	 * @return IPreferenceStore
	 */
	// /*package*/ IPreferenceStore getPreferenceStore() {
	// return WorkbenchPlugin.getDefault().getPreferenceStore();
	// } APORT
	/**
	 * Return whether the window's shell is activated
	 */
	// protected boolean getShellActivated() {
	// // Window window = this.getWindow();
	// Shell shell = this.getParentShell();
	// if (shell instanceof IShellActivationTracker)
	// return ((IShellActivationTracker) shell).getShellActivated();
	// else
	// return false;
	// }
	/**
	 * Gets the presentation size.<br>
	 * 
	 * @return Point
	 */
	public Point getSize() {
		Rectangle r = getBounds( );
		Point ptSize = new Point(r.width, r.height);
		return ptSize;
	}

	// getMinimumWidth() added by cagatayk@acm.org
	/**
	 * Returns the minimum width a part can have. Subclasses may<br>
	 * override as necessary.<br>
	 * 
	 * @return int
	 */
	public int getMinimumWidth() {
		return 0;
	}

	// getMinimumHeight() added by cagatayk@acm.org
	/**
	 * Returns the minimum height a part can have. Subclasses may<br>
	 * override as necessary.<br>
	 * 
	 * @return int
	 */
	public int getMinimumHeight() {
		return 0;
	}

	/**
	 * Returns the top level window for a part.
	 */
	// public Window getWindow() {
	// Control ctrl = this.getControl();
	// if (ctrl != null) {
	// Object data = ctrl.getShell().getData();
	// if (data instanceof Window){
	// System.out.println("window: "+((Window)data).toString());
	// return (Window) data;
	// }
	// }
	// return null;
	// }
	// /**
	// * Returns the workbench window window for a part.
	// */
	// public abstract AppWindow AppWindow {
	// get {
	// Window parentWindow = Window;
	// if (parentWindow is AppWindow)
	// return (AppWindow)parentWindow;
	// /*
	// * Detached window no longer supported - remove when confirmed
	// *
	// * if (parentWindow instanceof DetachedWindow)
	// * return
	// ((DetachedWindow)parentWindow).getWorkbenchPage().getWorkbenchWindow();
	// */
	// return null;
	// }
	// }
	/**
	 * Allow the layout part to determine if they are in an acceptable<br>
	 * state to start a drag & drop operation.<br>
	 * 
	 * @param p
	 * @return boolean
	 */
	public boolean isDragAllowed(Point p) {
		return true;
	}

	/**
	 * Move the control over another one.<br>
	 * 
	 * @param refControl
	 */
	public void moveAbove(Control refControl) {
		if (getControl( ) != null) {
			getControl( ).moveAbove(refControl);
		}
	}

	/**
	 * Reparent a part.<br>
	 * 
	 * @param newParent
	 */
	public void reparent(Composite newParent) {
		if (!newParent.isReparentable( )) {
			return;
		}

		Control control = this.getControl( );
		if ((control == null) || (control.isDisposed( ))
				|| (control.getParent( ) == newParent)) {
			return;
		}

		// make control small in case it is not resized with other
		// controls
		control.setBounds(0, 0, 0, 0);
		// By setting the control to disabled before moving it,
		// we ensure that the focus goes away from the control and its
		// children
		// and moves somewhere else
		boolean enabled = control.getEnabled( );
		control.setEnabled(false);
		control.setParent(newParent);
		control.setEnabled(enabled);
	}

	/**
	 * Returns true if this part is visible.<br>
	 * 
	 * @return boolean
	 */
	public boolean isVisible() {
		Control ctrl = getControl( );
		if (ctrl != null) {
			return visible == Boolean.TRUE ? true : false;
		}
		return false;
	}

	/**
	 * Shows the receiver if <code>visible</code> is true otherwise<br>
	 * hide it.<br>
	 * 
	 * @param makeVisible
	 */
	public void setVisible(boolean makeVisible) {
		Control ctrl = getControl( );
		if (ctrl != null) {
			if (visible != null && makeVisible == visible.booleanValue( )) {
				return;
			}

			visible = makeVisible ? Boolean.TRUE : Boolean.FALSE;
			ctrl.setVisible(makeVisible);
			final Object[] listeners = propertyListeners.getListeners( );
			if (listeners.length > 0) {
				Boolean oldValue = makeVisible ? Boolean.FALSE : Boolean.TRUE;
				PropertyChangeEvent event = new PropertyChangeEvent(this,
						PROP_VISIBILITY, oldValue, visible);
				for (int i = 0; i < listeners.length; ++i) {
					((IPropertyChangeListener) listeners[i])
							.propertyChange(event);
				}
			}
		}
	}

	/**
	 * Sets focus to this part.<br>
	 */
	public void setFocus() {
	}

	/**
	 * @see IPartDropTarget#targetPartFor
	 */
	// public abstract ILayoutPart targetPartFor(ILayoutPart
	// dragSource);
	public ILayoutPart targetPartFor(ILayoutPart dragSource) {
		// // When zoomed, its like we are not part of the
		// // tab folder so return the view.
		// if (isZoomed())
		// return this;
		//
		// // Make use of the container if a tab folder
		// ILayoutContainer container = getContainer();
		// if (container instanceof PartTabFolder)
		// return (PartTabFolder) container;
		// else
		// return this;
		//
		return null;
	}

	/**
	 * Return whether the pane is zoomed or not.<br>
	 * 
	 * @return boolean
	 */
	public boolean isZoomed() {
		return isZoomed;
	}

	/**
	 * Set whether the pane is zoomed or not.<br>
	 * 
	 * @param value
	 */
	// from PartPane
	public void setZoomed(final boolean value) {
		isZoomed = value;
	}

	/* (non-Javadoc)
	 * @see de.xirp.ui.dock.ILayoutPart#getI18nHandler()
	 */
	public II18nHandler getI18nHandler() {
		return I18n.getGenericI18n();
	}

	/* (non-Javadoc)
	 * @see de.xirp.ui.dock.ILayoutPart#getI18nArgs()
	 */
	public Object[] getI18nArgs() {
		// TODO Auto-generated method stub
		return null;
	}
}
