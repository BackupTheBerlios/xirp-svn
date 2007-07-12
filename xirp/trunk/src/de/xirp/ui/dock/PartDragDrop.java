package de.xirp.ui.dock;

/*********************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights
 * reserved. This program and the accompanying materials are made
 * available under the terms of the Common Public License v1.0 which
 * accompanies this distribution, and instanceof available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ********************************************************************/

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tracker;

import de.xirp.ui.util.SWTUtil;

/**
 * Controls the drag and drop of an editor or view layout part.
 * 
 * @see IPartDropListener
 * @see IPartDropTarget
 * @see PartDropEvent
 * @see LayoutPart
 */
class PartDragDrop extends Object {
//	 Define the relative position
	public enum DragDropLocation {
		INVALID,//0
		LEFT,//1
		RIGHT,//2
		TOP,//3
		BOTTOM,//4
		CENTER,//5
		OFFSCREEN;//6
		
		public IPageLayout.Location toPageLayoutLocation(){
			switch(this){
				case RIGHT: return IPageLayout.Location.RIGHT;
				case TOP: return IPageLayout.Location.TOP;
				case BOTTOM: return IPageLayout.Location.BOTTOM;
				default: return IPageLayout.Location.LEFT;
			}
		}
		
		public ImageDescriptor getSource(){
			ImageRegistry registry = JFaceResources.getImageRegistry( );
			switch(this){
				case LEFT: return registry.getDescriptor(IMG_OBJS_DND_LEFT_SOURCE);
				case RIGHT: return registry.getDescriptor(IMG_OBJS_DND_RIGHT_SOURCE);
				case TOP: return registry.getDescriptor(IMG_OBJS_DND_TOP_SOURCE);
				case BOTTOM: return registry.getDescriptor(IMG_OBJS_DND_BOTTOM_SOURCE);
				case CENTER: return registry.getDescriptor(IMG_OBJS_DND_STACK_SOURCE);
				case OFFSCREEN: return registry.getDescriptor(IMG_OBJS_DND_OFFSCREEN_SOURCE);
				case INVALID: return registry.getDescriptor(IMG_OBJS_DND_INVALID_SOURCE);
				default: return null;
			}
		}
		
		public ImageDescriptor getMask(){
			ImageRegistry registry = JFaceResources.getImageRegistry( );
			switch(this){
				case LEFT: return registry.getDescriptor(IMG_OBJS_DND_LEFT_MASK);
				case RIGHT: return registry.getDescriptor(IMG_OBJS_DND_RIGHT_MASK);
				case TOP: return registry.getDescriptor(IMG_OBJS_DND_TOP_MASK);
				case BOTTOM: return registry.getDescriptor(IMG_OBJS_DND_BOTTOM_MASK);
				case CENTER: return registry.getDescriptor(IMG_OBJS_DND_STACK_MASK);
				case OFFSCREEN: return registry.getDescriptor(IMG_OBJS_DND_OFFSCREEN_MASK);
				case INVALID: return registry.getDescriptor(IMG_OBJS_DND_INVALID_MASK);
				default: return null;
			}
		}
	}

	
	// Image static finalants from WorkbenchImageConsts
	// part direct manipulation objects
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_LEFT_SOURCE = "dnd/left_source.bmp"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_LEFT_MASK = "dnd/left_mask.bmp"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_RIGHT_SOURCE = "dnd/right_source.bmp"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_RIGHT_MASK = "dnd/right_mask.bmp"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_TOP_SOURCE = "dnd/top_source.bmp"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_TOP_MASK = "dnd/top_mask.bmp"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_BOTTOM_SOURCE = "dnd/bottom_source.bmp"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_BOTTOM_MASK = "dnd/bottom_mask.bmp"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_INVALID_SOURCE = "dnd/invalid_source.bmp"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_INVALID_MASK = "dnd/invalid_mask.bmp"; //$NON-NLS-1 //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_STACK_SOURCE = "dnd/stack_source.bmp"; //$NON-NLS-1 //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_STACK_MASK = "dnd/stack_mask.bmp"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_OFFSCREEN_SOURCE = "dnd/offscreen_source.bmp"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String IMG_OBJS_DND_OFFSCREEN_MASK = "dnd/offscreen_mask.bmp"; //$NON-NLS-1$

	/**
	 * Move this many pixels before dragging starts
	 */
	private static final int HYSTERESIS = 10;
	/**
	 * Define width of part's "hot" border
	 */
	private static final int MARGIN = 10;
	/**
	 * 
	 */
	private static final Cursor[] cursors = new Cursor[7];
	/**
	 * Drag source layout part
	 */
	private ILayoutPart sourcePart;
	/**
	 * Control which acts as the drag object (could be a titlebar,
	 * could be the entire VisualPart)
	 */
	private Control dragControl;
	/**
	 * 
	 */
	private int xAnchor;
	/**
	 * 
	 */
	private int yAnchor;
	/**
	 * 
	 */
	private boolean isDragAllowed = false;
	/**
	 * 
	 */
	private IPartDropListener[] dropListeners;
	/**
	 * 
	 */
	private MouseMoveListener mouseMoveListener;
	/**
	 * 
	 */
	private MouseListener mouseListener;
	/**
	 * 
	 */
	private Listener dragListener;

	/**
	 * Constructs a new drag drop.<br>
	 */
	public PartDragDrop() {
		ImageRegistry r = JFaceResources.getImageRegistry( );
		addImage(IMG_OBJS_DND_LEFT_SOURCE, r);
		addImage(IMG_OBJS_DND_LEFT_MASK, r);
		addImage(IMG_OBJS_DND_RIGHT_SOURCE, r);
		addImage(IMG_OBJS_DND_RIGHT_MASK, r);
		addImage(IMG_OBJS_DND_TOP_SOURCE, r);
		addImage(IMG_OBJS_DND_TOP_MASK, r);
		addImage(IMG_OBJS_DND_BOTTOM_SOURCE, r);
		addImage(IMG_OBJS_DND_BOTTOM_MASK, r);
		addImage(IMG_OBJS_DND_INVALID_SOURCE, r);
		addImage(IMG_OBJS_DND_INVALID_MASK, r);
		addImage(IMG_OBJS_DND_STACK_SOURCE, r);
		addImage(IMG_OBJS_DND_STACK_MASK, r);
		addImage(IMG_OBJS_DND_OFFSCREEN_SOURCE, r);
		addImage(IMG_OBJS_DND_OFFSCREEN_MASK, r);
	}

	/**
	 * @param name
	 * @param r
	 */
	private static void addImage(String name, ImageRegistry r) {
		r.put(name, ImageDescriptor.createFromFile(PartDragDrop.class, name));
	}

	/**
	 * Constructs a new drag drop for the given layout part and
	 * control.<br>
	 * 
	 * @param dragPart
	 *            The layout part.
	 * 
	 * @param dragHandle
	 *            The control
	 * 
	 * @see ILayoutPart
	 */
	public PartDragDrop(ILayoutPart dragPart, Control dragHandle) {
		this( );
		sourcePart = dragPart;
		dragControl = dragHandle;

		// listeners
		dragListener = new Listener( ) {

			public void handleEvent(Event event) {
				Point position = event.display.getCursorLocation( );
				Control control = event.display.getCursorControl( );
				if (control != null) {
					isDragAllowed(control.toControl(position));
				}
			}
		};
		dragControl.addListener(SWT.DragDetect, dragListener);
		mouseMoveListener = new MouseMoveListener( ) {

			public void mouseMove(MouseEvent event) {
				handleMouseMove(event);
			}
		};
		dragControl.addMouseMoveListener(mouseMoveListener);
		mouseListener = new MouseListener( ) {

			public void mouseDoubleClick(MouseEvent e) {
				handleMouseDoubleClick(e);
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
				handleMouseUp(e);
			}
		};
		dragControl.addMouseListener(mouseListener);

	}

	/**
	 * Adds the listener to receive events.
	 * <p>
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addDropListener(IPartDropListener listener) {

		if (listener == null){
			return;
		}

		if (dropListeners == null) {
			dropListeners = new IPartDropListener[1];
			dropListeners[0] = listener;
		}
		else {
			IPartDropListener[] newDropListeners = new IPartDropListener[dropListeners.length + 1];
			System.arraycopy(dropListeners, 0, newDropListeners, 0,
					dropListeners.length);
			newDropListeners[dropListeners.length] = listener;
			dropListeners = newDropListeners;

		}
	}

	/**
	 * Returns a drag event, for the given tracker, representing the
	 * current state of dragging.<br>
	 * 
	 * @param tracker
	 *            The tracker.
	 * 
	 * @return PartDropEvent<br>
	 *         The part drop event.
	 */
	private PartDropEvent createDropEvent(Tracker tracker) {

		Display display = dragControl.getDisplay( );
		Control targetControl = display.getCursorControl( );

		PartDropEvent event = new PartDropEvent( ); // APORT renamed
		// event

		Rectangle rect = tracker.getRectangles( )[0];
		event.x = rect.x;
		event.y = rect.y;
		event.dragSource = sourcePart;

		if (targetControl == null) {
			// cursor instanceof outside of the shell
			event.relativePosition = DragDropLocation.OFFSCREEN;
			return event;
		}

		ILayoutPart targetPart = getTargetPart(targetControl);
		if (targetPart == null) {
			event.relativePosition = DragDropLocation.INVALID;
			return event;
		}

		event.dropTarget = targetPart;
		Control c = targetPart.getControl( );
		if (c == null) {
			event.relativePosition = DragDropLocation.INVALID;
			return event;
		}

		Point p = c.toControl(display.getCursorLocation( ));
		Point e = c.getSize( );

		// first determine whether mouse position instanceof in center
		// of part
		int hmargin = Math.min(e.x / 3, MARGIN);
		int vmargin = Math.min(e.y / 3, MARGIN);

		Rectangle inner = new Rectangle(hmargin, vmargin, e.x - (hmargin * 2),
				e.y - (vmargin * 2));
		if (inner.contains(p)) {
			event.relativePosition = DragDropLocation.CENTER;
		}
		else {
			// normalize to center
			p.x -= e.x / 2;
			p.y -= e.y / 2;

			// now determine quadrant
			double a = Math.atan2(p.y * e.x, p.x * e.y) * (180 / Math.PI);

			if (a >= -135 && a < -45)
				event.relativePosition = DragDropLocation.TOP;
			else if (a > -45 && a < 45)
				event.relativePosition = DragDropLocation.RIGHT;
			else if (a > 45 && a < 135)
				event.relativePosition = DragDropLocation.BOTTOM;
			else
				event.relativePosition = DragDropLocation.LEFT;
		}
		return event;
	}

	/**
	 * Dispose of the drag drop.
	 */
	public void dispose() {
		// Get rid of control.
		if (SWTUtil.swtAssert(dragControl)) {
			dragControl.removeMouseMoveListener(mouseMoveListener);
			dragControl.removeMouseListener(mouseListener);
			dragControl.removeListener(SWT.DragDetect, dragListener);
		}
		dragControl = null;

		// Get rid of cursors.
		for (int i = 0, length = cursors.length; i < length; i++) {
			if (cursors[i] != null && !cursors[i].isDisposed( ))
				cursors[i].dispose( );
			cursors[i] = null;
		}

		// Deref all else.
		dropListeners = null;
		sourcePart = null;
	}

	/**
	 * Return the cursor for a drop scenario, as identified by code.<br>
	 * Code must be one of INVALID, LEFT, RIGHT, TOP, etc. If the code<br>
	 * instanceof not found default to INVALID.<br>
	 * 
	 * @param display
	 *            The display to work on.
	 * 
	 * @param code
	 *            Flags, for the cursor to choose.
	 * 
	 * @return Cursor<br>
	 *         The appropriate cursor.
	 */
	private Cursor getCursor(Display display, DragDropLocation code) {
		Cursor cursor = cursors[code.ordinal( )];
		if (cursor == null) {
			cursor = new Cursor(display, code.getSource( ).getImageData( ),
					code.getMask( ).getImageData( ), 16, 16);
			cursors[code.ordinal( )] = cursor;
		}
		return cursor;
	}

	/**
	 * Returns the drag handle.<br>
	 * 
	 * @return Control<br>
	 *         The drag handle.
	 */
	protected Control getDragControl() {
		return dragControl;
	}

	/**
	 * Returns the source's bounds.<br>
	 * 
	 * @return Rectangle<br>
	 *         The source's bounds.
	 */
	protected Rectangle getSourceBounds() {
		return sourcePart.getControl( ).getBounds( );
	}

	/**
	 * Returns the drag source part.<br>
	 * 
	 * @return ILayoutPart<br>
	 *         The drag source part.
	 */
	public ILayoutPart getSourcePart() {
		return sourcePart;
	}

	/**
	 * Returns the target part containing a particular control. If the<br>
	 * target part instanceof not in the same window as the source<br>
	 * part return null.<br>
	 * 
	 * @param target
	 *            The target control.
	 * 
	 * @return ILayoutPart<br>
	 *         The target part.
	 */
	private ILayoutPart getTargetPart(Control target) {
		while (target != null) {
			Object data = target.getData( );
			if (data instanceof IPartDropTarget){
				return ((IPartDropTarget) data).targetPartFor(sourcePart);
			}

			target = target.getParent( );
		}
		return null;
	}

	/**
	 * Returns whether the mouse has moved enough to warrant opening a<br>
	 * tracker.<br>
	 * 
	 * @param e
	 *            The mouse event to work on.
	 * 
	 * @return boolean<br>
	 *         <code>true</code>, mouse has moved enough to warrant
	 *         opening a tracker.<br>
	 *         <code>false</code>,mouse has not moved enough to
	 *         warrant opening a tracker.
	 */
	protected boolean hasMovedEnough(MouseEvent e) {
		int dx = e.x - xAnchor;
		int dy = e.y - yAnchor;
		if (Math.abs(dx) < HYSTERESIS && Math.abs(dy) < HYSTERESIS){
			return false;
		}
		else{
			return true;
		}
	}

	/**
	 * @param e
	 */
	protected void handleMouseDoubleClick(MouseEvent e) {
		isDragAllowed = false;
	}

	/**
	 * @param e
	 */
	protected void handleMouseMove(MouseEvent e) {
		// If the mouse instanceof not down or the mouse has moved
		// only a small
		// amount
		// ignore the move.
		// Bug 9004: If a previous MouseDown event caused a dialog to
		// open,
		// the PartDragDrop will not be notified of the MouseUp event
		// and the
		// mouseDown flag will not be reset. The fix instanceof to
		// check and
		// make sure
		// that the mouse button instanceof still pressed.
		// Can not use a focus listener since the dragControl may not
		// actually
		// receive focus on a MouseDown.
		if (!isDragAllowed){
			return;
		}
		if (!hasMovedEnough(e)){
			return;
		}

		// If the source part instanceof not in a state to allow drag
		// & drop
		// operation to start, ignore the move
		if (!sourcePart.isDragAllowed(new Point(e.x, e.y))){
			return;
		}

		openTracker( );
	}

	/**
	 * Called when a drag operation has been requested.
	 * 
	 * @param position
	 *            mouse cursor location relative to the control
	 *            underneath the cursor
	 */
	public void isDragAllowed(Point position) {
		if (getSourceBounds( ).width == 0 || getSourceBounds( ).height == 0){
			return;
		}
		if (!sourcePart.isDragAllowed(position)){
			return;
		}
		isDragAllowed = true;
		xAnchor = position.x;
		yAnchor = position.y;
	}

	/**
	 * Open a tracker (a XOR rect on the screen) change the cursor
	 * indicanting where the part will be dropped and notify the drag
	 * listeners.
	 */
	public void openTracker() {
		// Create a tracker. This instanceof just an XOR rect on the
		// screen.
		// As it moves we notify the drag listeners.
		final Display display = dragControl.getDisplay( ); // APORT
		final Tracker tracker = new Tracker(display, SWT.NULL); // APORT
		tracker.addListener(SWT.Move, new Listener( ) {

			public void handleEvent(Event event) {
				PartDropEvent dropEvent = createDropEvent(tracker);
				// 1GBXIEO: SWT:ALL - DCR: Include cursor pos in
				// Tracker move
				// event
				// Until support is provided, just get the current
				// location (which could be different than when the
				// event
				// occured
				// if the user moves the mouse quickly!)
				Point p = dragControl.toControl(display.getCursorLocation( ));
				dropEvent.cursorX = p.x;
				dropEvent.cursorY = p.y;
				if (dropListeners != null) {
					for(IPartDropListener listener : dropListeners){
//					for (int i = 0, length = dropListeners.length; i < length; i++) {
						listener.dragOver(dropEvent);
					}
				}
				Cursor cursor = getCursor(display, dropEvent.relativePosition);
				tracker.setCursor(cursor);
			}
		});

		// Create a drag rect.
		Control sourceControl = sourcePart.getControl( );
		Rectangle sourceBounds = getSourceBounds( );
		Point sourcePos = new Point(sourceBounds.x, sourceBounds.y);
		if (!(sourceControl instanceof Shell)) {
			sourcePos = sourceControl.getParent( ).toDisplay(sourcePos);
		}
		if (isDragAllowed) {
			Point anchorPos = dragControl
					.toDisplay(new Point(xAnchor, yAnchor));
			Point cursorPos = display.getCursorLocation( );
			sourceBounds.x = sourcePos.x - (anchorPos.x - cursorPos.x);
			sourceBounds.y = sourcePos.y - (anchorPos.y - cursorPos.y);
		}
		else {
			sourceBounds.x = sourcePos.x + HYSTERESIS;
			sourceBounds.y = sourcePos.y + HYSTERESIS;
		}

		tracker.setRectangles(new Rectangle[] {sourceBounds});

		// Run tracker until mouse up occurs or escape key pressed.
		boolean trackingOk = tracker.open( );
		isDragAllowed = false;

		// Generate drop event.
		PartDropEvent event = createDropEvent(tracker);
		// 1GBXIEO: SWT:ALL - DCR: Include cursor pos in Tracker move
		// event
		// Until support is provided, just get the current
		// location (which could be different than when the event
		// occured
		// if the user moves the mouse quickly!)
		Point p1 = dragControl.toControl(display.getCursorLocation( ));
		event.cursorX = p1.x;
		event.cursorY = p1.y;
		if (!dragControl.isDisposed( )){
			dragControl.setCursor(null);
		}
		if (dropListeners != null) {
			if (trackingOk) {
				for(IPartDropListener listener : dropListeners){
//				for (int i = 0, length = dropListeners.length; i < length; i++) {
					listener.dragOver(event);
				}
			}
			else {
				event.relativePosition = DragDropLocation.INVALID;
			}
			for(IPartDropListener listener : dropListeners){
//			for (int i = 0, length = dropListeners.length; i < length; i++) {
				listener.drop(event);
			}
		}

		// Cleanup.
		tracker.dispose( );
	}

	/**
	 * @param e
	 *            Teh mouse event.
	 * 
	 * @see MouseListener#mouseUp
	 */
	public void handleMouseUp(MouseEvent e) {
		isDragAllowed = false;
	}

	/**
	 * Removes the listener.
	 * <p>
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeDropListener(IPartDropListener listener) {
		if (listener == null){
			return;
		}

		if (dropListeners.length == 1) {
			dropListeners = null;
		}
		else {
			IPartDropListener[] newListeners = new IPartDropListener[dropListeners.length - 1];
			for (int index = 0, length = dropListeners.length; index < length; index++) {
				if (dropListeners[index].equals(listener)) {
					System.arraycopy(dropListeners, 0, newListeners, 0, index);
					System.arraycopy(dropListeners, index + 1, newListeners, index,
							newListeners.length - index);
					dropListeners = newListeners;
					break;
				}
			}
		}
	}
}
