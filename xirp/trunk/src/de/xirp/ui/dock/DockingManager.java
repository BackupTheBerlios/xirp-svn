package de.xirp.ui.dock;

import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMemento;

/**
 * 
 */
public class DockingManager implements IDockingManager, IZoomManager {

	/**
	 * 
	 */
	private RootLayoutContainer mainLayout;
	/**
	 * 
	 */
	private Composite parentWidget;
	/**
	 * 
	 */
	private Hashtable<ILayoutPart, PartDragDrop> dragParts = new Hashtable<ILayoutPart, PartDragDrop>( ); 
	/**
	 * 
	 */
	private IPartDropListener partDropListener;
	/**
	 * 
	 */
	private ILayoutPart zoomPart;
	/**
	 * 
	 */
	private int folderStyle;

	/**
	 * @param parentWidget
	 * @param style
	 */
	public DockingManager(Composite parentWidget, int style) {
		this.parentWidget = parentWidget;
		folderStyle = style;
		mainLayout = new RootLayoutContainer( );
		mainLayout.createControl(parentWidget);
		this.partDropListener = new AnonPartDropListener(this);

		enableAllDrag( );
		enableAllDrop( );

	}

	// from PerspectivePresentation
	/**
	 * 
	 */
	private class AnonPartDropListener implements IPartDropListener {

		/**
		 * 
		 */
		private DockingManager dm;

		/**
		 * @param dm
		 */
		public AnonPartDropListener(DockingManager dm) {
			this.dm = dm;
		}

		/**
		 * @see IPartDropListener#dragOver(de.xirp.ui.dock.PartDropEvent)
		 */
		public void dragOver(PartDropEvent e) {
			dm.onPartDragOver(e);
		}

		/**
		 * @see IPartDropListener#drop(de.xirp.ui.dock.PartDropEvent)
		 */
		public void drop(PartDropEvent e) {
			dm.onPartDrop(e);
		}
	}

	/**
	 * Adds a part to the presentation. If a placeholder exists for<br>
	 * the part then swap the part in. Otherwise, add the part in the<br>
	 * bottom right corner of the presentation.<br>
	 * 
	 * @param part
	 */
	// from PerspectivePresentation
	public void addPart(ILayoutPart part) {

		// If part added / removed always zoom out.
		if (isZoomed( )) {
			zoomOut( );
		}

		// Look for a placeholder.
		PartPlaceholder placeholder = null;
		ILayoutPart testPart = findPart(part.getID( ));
		if (testPart != null && testPart instanceof PartPlaceholder) {
			placeholder = (PartPlaceholder) testPart;
		}

		// If there is no placeholder do a simple add. Otherwise,
		// replace the placeholder.
		if (placeholder == null) {

			ILayoutPart relative = mainLayout.findBottomRight( );
			// the condition ! is ContainerPlaceholder fixes bug 1476
			if (relative != null && !(relative instanceof ContainerPlaceholder) 
					/* && !(relative is EditorArea) */) {
				stack(part, relative);
			}
			else {
				mainLayout.add(part);
			}
		}
		else {
			ILayoutContainer container = placeholder.getContainer( );
			if (container != null) {
				// reconsistute parent if necessary
				if (container instanceof ContainerPlaceholder) {
					ContainerPlaceholder containerPlaceholder = (ContainerPlaceholder) container;
					ILayoutContainer parentContainer = containerPlaceholder
							.getContainer( );
					container = (ILayoutContainer) containerPlaceholder
							.getRealContainer( );
					if (container instanceof LayoutPart) {
						parentContainer.replace(containerPlaceholder,
								(LayoutPart) container);
					}
					containerPlaceholder.setRealContainer(null);
				}

				// reparent part.
				if (container instanceof PartTabFolder) {
					PartTabFolder folder = (PartTabFolder) container;
					part.reparent(folder.getControl( ).getParent( ));
				}
				else {
					part.reparent(mainLayout.getParent( ));
				}

				if(container != null){
				// replace placeholder with real part
					container.replace(placeholder, part);
				}
			}
		}

		// enable direct manipulation
		if (part.isViewPane( )) {
			enableDrag(part);
		}
		enableDrop(part);

	}

	/**
	 * Bring a part forward so it is visible.<br>
	 * 
	 * @param part
	 * @return boolean<br>
	 *         <code>true</code>,if the part was brought to top.<br>
	 *         <code>false</code> if not.
	 */
	// from PerspectivePresentation
	public boolean bringPartToTop(ILayoutPart part) {
		ILayoutContainer container = part.getContainer( );
		if (container != null && container instanceof PartTabFolder) {
			PartTabFolder folder = (PartTabFolder) container;
			int nIndex = folder.indexOf(part);
			if (folder.getSelection( ) != nIndex) {
				folder.setSelection(nIndex);
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true is not in a tab folder or if it is the top one in<br>
	 * a tab folder.<br>
	 * 
	 * @param partId
	 * @return boolean
	 */
	// from PerspectivePresentation
	public boolean isPartVisible(String partId) {
		ILayoutPart part = findPart(partId);
		if (part == null || part instanceof PartPlaceholder) {
			return false;
		}

		ILayoutContainer container = part.getContainer( );
		if (container != null && container instanceof ContainerPlaceholder) {
			return false;
		}

		if (container != null && container instanceof PartTabFolder) {
			PartTabFolder folder = (PartTabFolder) container;
			ILayoutPart visiblePart = folder.getVisiblePart( );
			if (visiblePart == null) {
				return false;
			}
			return part.getID( ).equals(visiblePart.getID( ));
		}
		return true;
	}

	/**
	 * Returns whether the presentation is zoomed.<br>
	 * 
	 * @return boolean
	 */
	// from PerspectivePresentation
	public boolean isZoomed() {
		return (zoomPart != null);
	}

	/**
	 * Open the tracker to allow the user to move the specified part<br>
	 * using keyboard.<br>
	 * 
	 * @param pane
	 */
	public void openTracker(ILayoutPart pane) {
		PartDragDrop dnd = dragParts.get(pane);
		dnd.openTracker( );
	}

	/**
	 * Answer a list of the view panes.<br>
	 * 
	 * @param result
	 * @param parts
	 */
	// from PerspectivePresentation
	private void collectDragParts(ArrayList<ILayoutPart> result,
			ILayoutPart[] parts) {
		for (ILayoutPart part : parts) {
			// for (int i = 0, length = parts.length; i < length; i++)
			// {
			// ILayoutPart part = parts[i];
			if (part.isViewPane( )) {
				result.add(part);
			}
			else if (part instanceof ILayoutContainer) {
				collectDragParts(result, ((ILayoutContainer) part)
						.getChildren( ));
			}
		}
	}

	/**
	 * Answer a list of the view panes.<br>
	 * 
	 * @param result
	 * @param parts
	 */
	// from PerspectivePresentation
	private void collectDropTargets(ArrayList<ILayoutPart> result,
			ILayoutPart[] parts) {
		for (ILayoutPart part : parts) {
			// for (int i = 0, length = parts.length; i < length; i++)
			// {
			// ILayoutPart part = parts[i];
			if (part.isViewPane( )) {
				result.add(part);
			}
			else if (part instanceof ILayoutContainer) {
				collectDropTargets(result, ((ILayoutContainer) part)
						.getChildren( ));
			}
		}
	}

	/**
	 * Answer a list of the PartPlaceholder objects.<br>
	 * 
	 * @param parts
	 * @return PartPlaceholder[]
	 */
	public PartPlaceholder[] collectPlaceholders(ILayoutPart[] parts) {
		PartPlaceholder[] result = new PartPlaceholder[0];
		for (ILayoutPart part : parts) {
			// for (int i = 0, length = parts.length; i < length; i++)
			// {
			// ILayoutPart part = parts[i];
			if (part instanceof ILayoutContainer) {
				// iterate through sub containers to find sub-parts
				PartPlaceholder[] newParts = collectPlaceholders(((ILayoutContainer) part)
						.getChildren( ));
				PartPlaceholder[] newResult = new PartPlaceholder[result.length
						+ newParts.length];
				System.arraycopy(result, 0, newResult, 0, result.length);
				System.arraycopy(newParts, 0, newResult, result.length,
						newParts.length);
				result = newResult;
			}
			else if (part instanceof PartPlaceholder) {
				PartPlaceholder[] newResult = new PartPlaceholder[result.length + 1];
				System.arraycopy(result, 0, newResult, 0, result.length);
				newResult[result.length] = (PartPlaceholder) part;
				result = newResult;
			}
		}

		return result;
	}

	/**
	 * Answer a list of the view panes.<br>
	 * 
	 * @param result
	 */
	// from PerspectivePresentation
	public void collectViewPanes(ArrayList<ILayoutPart> result) {
		// Scan the main window.
		collectViewPanes(result, mainLayout.getChildren( ));

	}

	/**
	 * Answer a list of the view panes.<br>
	 * 
	 * @param result
	 * @param parts
	 */
	// from PerspectivePresentation
	private void collectViewPanes(ArrayList<ILayoutPart> result,
			ILayoutPart[] parts) {
		for (ILayoutPart part : parts) {
			// for (int i = 0, length = parts.length; i < length; i++)
			// {
			// ILayoutPart part = parts[i];
			if (part.isViewPane( )) {
				result.add(part);
			}
			else if (part instanceof ILayoutContainer) {
				collectViewPanes(result, ((ILayoutContainer) part)
						.getChildren( ));
			}
		}
	}

	/**
	 * Hide the presentation.<br>
	 */
	// from PerspectivePresentation (was Deactivate)
	public void dispose() {
		disableAllDrag( );

		// Reparent all views to the main window
		Composite parent = mainLayout.getParent( );
		ArrayList<ILayoutPart> children = new ArrayList<ILayoutPart>( );
		collectViewPanes(children, mainLayout.getChildren( ));

		/*
		 * Detached window no longer supported - remove when confirmed
		 * for (int i = 0, length = detachedWindowList.size(); i <
		 * length; i++){ DetachedWindow window =
		 * (DetachedWindow)detachedWindowList.get(i);
		 * collectViewPanes(children, window.getChildren()); }
		 */

		// *** Do we even need to do this if detached windows not
		// supported?
		for (ILayoutPart part : children) {
			// for (int i = 0; i < children.size( ); i++) {
			// LayoutPart part = (LayoutPart) children.get(i);
			part.reparent(parent);
		}

		// Dispose main layout.
		mainLayout.dispose( );

		/*
		 * Detached window no longer supported - remove when confirmed //
		 * Dispose the detached windows for (int i = 0, length =
		 * detachedWindowList.size(); i < length; i++){ DetachedWindow
		 * window = (DetachedWindow)detachedWindowList.get(i);
		 * window.close(); }
		 */

		mainLayout.disposeSashes( );
	}

	/**
	 * Deref a given part. Deconstruct its container as required. Do<br>
	 * not remove drag listeners.<br>
	 * 
	 * @param part
	 */
	// from PerspectivePresentation
	@SuppressWarnings("null") //$NON-NLS-1$
	private void derefPart(ILayoutPart part) {
		// Get vital part stats before reparenting.
		// Window oldWindow = part.getWindow();
		ILayoutContainer oldContainer = part.getContainer( );

		/*
		 * Detached window no longer supported - remove when confirmed //
		 * Reparent the part back to the main window
		 * part.reparent((Composite)mainLayout.getParent());
		 */

		// Update container.
		if (oldContainer == null) {
			return;
		}

		oldContainer.remove(part);
		updateContainerVisibleTab(oldContainer);

		ILayoutPart[] children = oldContainer.getChildren( );
		// if (oldWindow instanceof ApplicationWindow) {
		boolean hasChildren = (children != null) && (children.length > 0);
		if (hasChildren) {
			// make sure one is at least visible
			int childVisible = 0;
			for (ILayoutPart child : children) {
				// for (int i = 0; i < children.length; i++)
				if (child.getControl( ) != null) {
					childVisible++;
				}
			}

			// none visible, then reprarent and remove container
			if (oldContainer instanceof PartTabFolder) {
				PartTabFolder folder = (PartTabFolder) oldContainer;
				if (childVisible == 0) {
					ILayoutContainer parentContainer = folder.getContainer( );
					for (ILayoutPart childd : children) {
						// for (int i = 0; i < children.length; i++) {
						folder.remove(childd);
						parentContainer.add(childd);
					}
					hasChildren = false;
				}
				else if (childVisible == 1) {
					LayoutTree layout = mainLayout.getLayoutTree( );
					layout = layout.find(folder);
					layout.setBounds(layout.getBounds( ));
				}
			}
		}
		if (!hasChildren) {
			// There are no more children in this container, so get
			// rid of it
			if (oldContainer instanceof LayoutPart) {
				LayoutPart parent = (LayoutPart) oldContainer;
				ILayoutContainer parentContainer = parent.getContainer( );
				if (parentContainer != null) {
					parentContainer.remove(parent);
					parent.dispose( );
				}
			}
		}
		// }//instanceof applicationwindow
	}

	/**
	 * disable dragging.<br>
	 */
	// from PerspectivePresentation
	private void disableAllDrag() {

		// release Drag parts
		for (PartDragDrop part : dragParts.values( )) {
			// remove d&d to start from the view's tab if in a folder
			ILayoutContainer container = part.getSourcePart( ).getContainer( );
			if (container instanceof PartTabFolder)
				((PartTabFolder) container).disableDrag(part.getSourcePart( ));

			part.dispose( );
		}

		dragParts.clear( );
	}

	/**
	 * disable dragging.<br>
	 * 
	 * @param part
	 */
	// from PerspectivePresentation
	protected void disableDrag(ILayoutPart part) {
		// remove view from the drag sources
		if (dragParts.containsKey(part)) {
			PartDragDrop partDragDrop = dragParts.get(part);
			partDragDrop.dispose( );
			dragParts.remove(part);
		}

		// remove d&d to start from the view's tab if in a folder
		ILayoutContainer container = part.getContainer( );
		if (container instanceof PartTabFolder) {
			((PartTabFolder) container).disableDrag(part);
		}
	}

	/**
	 * enable dragging.<br>
	 */
	// from PerspectivePresentation
	private void enableAllDrag() {

		ArrayList<ILayoutPart> draggableParts = new ArrayList<ILayoutPart>( );
		collectDragParts(draggableParts, mainLayout.getChildren( ));

		/*
		 * Detached window no longer supported - remove when confirmed
		 * for (int i = 0, length = detachedWindowList.size(); i <
		 * length; i++){ DetachedWindow window =
		 * (DetachedWindow)detachedWindowList.get(i);
		 * collectDragParts(draggableParts, window.getChildren()); }
		 */
		for (ILayoutPart part : draggableParts) {
			if (part.isViewPane( ))
				enableDrag(part);
		}

	}

	/**
	 * enable dragging.<br>
	 */
	// from PerspectivePresentation
	private void enableAllDrop() {

		ArrayList<ILayoutPart> dropTargets = new ArrayList<ILayoutPart>( );
		collectDropTargets(dropTargets, mainLayout.getChildren( ));

		/*
		 * Detached window no longer supported - remove when confirmed
		 * for (int i = 0, length = detachedWindowList.size(); i <
		 * length; i++){ DetachedWindow window =
		 * (DetachedWindow)detachedWindowList.get(i);
		 * collectDropTargets(dropTargets, window.getChildren()); }
		 */
		for (ILayoutPart part : dropTargets) {
			enableDrop(part);
		}
	}

	/**
	 * enable drag.<br>
	 * 
	 * @param part
	 */
	// from PerspectivePresentation
	protected void enableDrag(ILayoutPart part) {
		// allow d&d to start from the view's title bar
		Control control = part.getDragHandle( );
		if (control != null) {
			PartDragDrop dragSource = new PartDragDrop(part, control);
			dragSource.addDropListener(partDropListener);
			dragParts.put(part, dragSource);
		}

		// allow d&d to start from the view's tab if in a folder
		ILayoutContainer container = part.getContainer( );
		if (container instanceof PartTabFolder) {
			((PartTabFolder) container).enableDrag(part, partDropListener);
		}
	}

	/**
	 * @param part
	 */
	// from PerspectivePresentation
	private void enableDrop(ILayoutPart part) {
		Control control = part.getControl( );
		if (control != null) {
			control.setData(part);
		}
	}

	/**
	 * Find the first part with a given ID in the presentation.<br>
	 * 
	 * @param id
	 * @return ILayoutPart
	 */
	// from PerspectivePresentation
	public ILayoutPart findPart(String id) {
		// Check main window.
		ILayoutPart part = findPart(id, mainLayout.getChildren( ));
		if (part != null) {
			return part;
		}

		/*
		 * Detached window no longer supported - remove when confirmed //
		 * Check each detached windows for (int i = 0, length =
		 * detachedWindowList.size(); i < length; i++){ DetachedWindow
		 * window = (DetachedWindow)detachedWindowList.get(i); part =
		 * findPart(id, window.getChildren()); if (part != null)
		 * return part; } for (int i = 0; i <
		 * detachedPlaceHolderList.size(); i++){ DetachedPlaceHolder
		 * holder =
		 * (DetachedPlaceHolder)detachedPlaceHolderList.get(i); part =
		 * findPart(id,holder.getChildren()); if(part != null) return
		 * part; }
		 */

		// Not found.
		return null;
	}

	/**
	 * Find the first part with a given ID in the presentation.<br>
	 * 
	 * @param id
	 * @param parts
	 * @return ILayoutPart
	 */
	// from PerspectivePresentation
	private ILayoutPart findPart(String id, ILayoutPart[] parts) {
		for (ILayoutPart part : parts) {
			// for (int i = 0, length = parts.length; i < length; i++)
			// {
			// ILayoutPart part = parts[i];
//			if (part != null) {
				if (part.getID( ).equals(id)) {
					return part;
					// } else if (part is EditorArea) {
					// // Skip.
				}
				else if (part instanceof ILayoutContainer) {
					part = findPart(id, ((ILayoutContainer) part).getChildren( ));
					if (part != null) {
						return part;
					}
				}
//			}
		}
		return null;
	}

	/**
	 * Returns true if a placeholder exists for a given ID.<br>
	 * 
	 * @param id
	 * @return boolean
	 */
	// from PerspectivePresentation
	public boolean hasPlaceholder(String id) {
		ILayoutPart testPart = findPart(id);
		return (testPart != null && testPart instanceof PartPlaceholder);
	}

	/**
	 * Returns the layout container.<br>
	 * 
	 * @return RootLayoutContainer
	 */
	// from PerspectivePresentation
	public RootLayoutContainer getLayout() {
		return mainLayout;
	}

	/**
	 * Move a part from one position to another. Supports moving a<br>
	 * part within the same window and moving a part from a detach<br>
	 * window into the main window.<br>
	 * 
	 * @param part
	 * @param position
	 * @param relativePart
	 */
	// from PerspectivePresentation
	private void movePart(ILayoutPart part, PartDragDrop.DragDropLocation position,
			ILayoutPart relativePart) {
		ILayoutContainer newContainer = relativePart.getContainer( );

		if (newContainer instanceof RootLayoutContainer) {
			// Determine the position
			RootLayoutContainer sashContainer = (RootLayoutContainer) newContainer;
			IPageLayout.Location relativePosition = position.toPageLayoutLocation( );

			ILayoutContainer oldContainer = part.getContainer( );
			if (oldContainer != sashContainer) {
				// just a normal move
				derefPart(part);
				sashContainer.add(part, relativePosition, (float) 0.5,
						relativePart);
			}
			else {
				// Move the part to its new position but keep its
				// bounds if possible.
				sashContainer.move(part, relativePosition, relativePart);
			}
			part.setFocus( );
		}
		else if (newContainer instanceof PartTabFolder) {
			// move this part relative to the folder
			// rather than relative to the part in the folder
			movePart(part, position, (PartTabFolder) newContainer);
		}
	}

	/**
	 * Notification sent during drag and drop operation. Only allow<br>
	 * views, tab folders, and fast view icons to participate in the<br>
	 * drag. Only allow the drop on a view, tab folder, the shortcut<br>
	 * bar, or editor area.<br>
	 * 
	 * @param e
	 */
	// from PerspectivePresentation
	protected void onPartDragOver(PartDropEvent e) {
		PartDragDrop.DragDropLocation offScreenPosition = PartDragDrop.DragDropLocation.INVALID;

		// If source and target are in different windows reject.
		// if (e.dragSource != null && e.dropTarget != null) {
		// if (e.dragSource.getWindow() != e.dropTarget.getWindow()) {
		// e.dropTarget = null;
		// e.relativePosition = offScreenPosition;
		// return;
		// }
		// }

		// If drop target is offscreen ..
		if (e.relativePosition == PartDragDrop.DragDropLocation.OFFSCREEN) {
			e.relativePosition = PartDragDrop.DragDropLocation.INVALID;

			// All seems well
			return;
		}

		// If drop target is not registered object then reject.
		if (e.dropTarget == null
				&& e.relativePosition != PartDragDrop.DragDropLocation.OFFSCREEN) {
			e.dropTarget = null;
			e.relativePosition = offScreenPosition;
			return;
		}

		// If drop target is not over view, or tab folder, reject.
		if (!(e.dropTarget.isViewPane( ) || e.dropTarget instanceof PartTabFolder)) {
			e.dropTarget = null;
			e.relativePosition = offScreenPosition;
			return;
		}

		// If drag source is view ..
		if (e.dragSource.isViewPane( )) {
			if (e.dragSource == e.dropTarget) {
				// Reject stack onto same view
				if (e.relativePosition == PartDragDrop.DragDropLocation.CENTER) {
					e.dropTarget = null;
					e.relativePosition = PartDragDrop.DragDropLocation.INVALID;
					return;
				}
				// Reject attach & detach to ourself
				ILayoutContainer container = e.dragSource.getContainer( );
				if (!(container instanceof PartTabFolder)) {
					e.dropTarget = null;
					e.relativePosition = PartDragDrop.DragDropLocation.INVALID;
					return;
				}
				if (((PartTabFolder) container).getItemCount( ) == 1) {
					e.dropTarget = null;
					e.relativePosition = PartDragDrop.DragDropLocation.INVALID;
					return;
				}
			}

			// If drag source's folder same as target
			if (e.dragSource.getContainer( ) == e.dropTarget) {
				// Reject stack/detach/attach to ourself
				if (((PartTabFolder) e.dropTarget).getItemCount( ) == 1) {
					e.dropTarget = null;
					e.relativePosition = PartDragDrop.DragDropLocation.INVALID;
					return;
				}
			}

			// All seems well
			return;
		}

		// If drag source is tab folder..
		if (e.dragSource instanceof PartTabFolder) {
			// Reject stack in same tab folder
			if (e.dragSource == e.dropTarget) {
				e.dropTarget = null;
				e.relativePosition = PartDragDrop.DragDropLocation.INVALID;
				return;
			}

			// Reject stack on view in same tab folder
			if (e.dropTarget.isViewPane( )) {
				if (e.dropTarget.getContainer( ) == e.dragSource) {
					e.dropTarget = null;
					e.relativePosition = PartDragDrop.DragDropLocation.INVALID;
					return;
				}
			}

			// All seems well
			return;
		}

		// If invalid case reject drop.
		e.dropTarget = null;
		e.relativePosition = offScreenPosition;
	}

	/**
	 * Notification sent when drop happens. Only views and tab folders<br>
	 * were allowed to participate.<br>
	 * 
	 * @param e
	 */
	// from PerspectivePresentation
	@SuppressWarnings("incomplete-switch") //$NON-NLS-1$
	protected void onPartDrop(PartDropEvent e) {
		// If invalid drop position ignore the drop (except for
		// possibly reactivating previous
		// active fast view.
		if (e.relativePosition == PartDragDrop.DragDropLocation.INVALID) {
			return;
		}

		switch (e.relativePosition) {
			case OFFSCREEN:
				break;
			case CENTER:
				if (e.dragSource.isViewPane( )
						&& e.dropTarget instanceof PartTabFolder) {
					if (e.dragSource.getContainer( ) == e.dropTarget) {
						((PartTabFolder) e.dropTarget).reorderTab(e.dragSource,
								e.cursorX, e.cursorY);
						break;
					}
				}
				stack(e.dragSource, e.dropTarget);
				break;
			case LEFT:
			case RIGHT:
			case TOP:
			case BOTTOM:
				movePart(e.dragSource, e.relativePosition, e.dropTarget);
				break;
		}
	}

	/**
	 * Remove all references to a part.<br>
	 * 
	 * @param part
	 */
	// from PerspectivePresentation
	public void removePart(ILayoutPart part) {
		// Disable drag.
		if (part.isViewPane( )) {
			disableDrag(part);
		}

		// Reparent the part back to the main window
		Composite parent = mainLayout.getParent( );
		part.reparent(parent);

		// Replace part with a placeholder
		ILayoutContainer container = part.getContainer( );
		if (container != null) {
			container.replace(part, new PartPlaceholder(part.getID( )));
			updateContainerVisibleTab(container);

			// If the parent is root we're done. Do not try to replace
			// it with placeholder.
			if (container == mainLayout) {
				return;
			}

			// If the parent is empty replace it with a placeholder.
			ILayoutPart[] children = container.getChildren( );
			if (children != null) {
				boolean allInvisible = true;
				for (ILayoutPart child : children) {
					// for (int i = 0, length = children.length; i <
					// length; i++) {
					if (!(child instanceof PartPlaceholder)) {
						allInvisible = false;
						break;
					}
				}
				if (allInvisible && (container instanceof LayoutPart)) {
					// what type of window are we in?
					LayoutPart cPart = (LayoutPart) container;
					// Window oldWindow = cPart.getWindow();
					// if (oldWindow instanceof ApplicationWindow) {
					// PR 1GDFVBY: PartTabFolder not disposed when
					// page closed.
					if (container instanceof PartTabFolder) {
						((PartTabFolder) container).dispose( );
					}

					// replace the real container with a
					// ContainerPlaceholder
					ILayoutContainer parentContainer = cPart.getContainer( );
					ContainerPlaceholder placeholder = new ContainerPlaceholder(
							cPart.getID( ));
					placeholder.setRealContainer(container);
					parentContainer.replace(cPart, placeholder);
					// }
				}
			}
		}
	}

	/**
	 * @see IDockingManager#restoreState(org.eclipse.ui.IMemento)
	 */
	public void restoreState(IMemento memento) {
		IMemento childMem = memento
				.getChild(IDockingManagerConsts.TAG_MAIN_WINDOW);
		mainLayout.restoreState(childMem);

		parentWidget.setRedraw(true);
		parentWidget.layout( );

		// TODO: Floating windows
	}

	/**
	 * @see IDockingManager#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento) {
		IMemento layout = memento
				.createChild(IDockingManagerConsts.TAG_MAIN_WINDOW);
		mainLayout.saveState(layout);
		// TODO: Floating windows
	}

	/**
	 * Stack a layout part on the reference part.<br>
	 * 
	 * @param part
	 * @param refPart
	 */
	// from PerspectivePresentation
	private void stack(ILayoutPart part, ILayoutPart refPart) {
		parentWidget.setRedraw(false);
		if (part instanceof PartTabFolder) {
			ILayoutPart visiblePart = ((PartTabFolder) part).getVisiblePart( );
			ILayoutPart[] children = ((PartTabFolder) part).getChildren( );
			for (ILayoutPart child : children) {
				if (child.isViewPane( ))
					stackView(child, refPart);
			}
			if (visiblePart != null) {
				bringPartToTop(visiblePart);
				visiblePart.setFocus( );
			}
		}
		else {
			stackView(part, refPart);
			bringPartToTop(part);
			part.setFocus( );
		}
		parentWidget.setRedraw(true);
	}

	/**
	 * Stack a view on a reference part.<br>
	 * 
	 * @param newPart
	 * @param refPart
	 */
	// from PerspectivePresentation
	private void stackView(/* ViewPane */ILayoutPart newPart,
			ILayoutPart refPart) {
		// derefence the part from its current container and shell
		derefPart(newPart);

		// determine the new container in which to add the part
		ILayoutContainer newContainer;
		if (refPart instanceof ILayoutContainer) {
			newContainer = (ILayoutContainer) refPart;
		}
		else {
			newContainer = refPart.getContainer( );
		}

		if (newContainer instanceof PartTabFolder) {
			// Reparent part. We may be adding it to a different shell
			// !!!
			PartTabFolder folder = (PartTabFolder) newContainer;
			Composite newParent = folder.getParent( );
			newPart.reparent(newParent);

			// Add part to existing folder
			folder.add(newPart);
			folder.enableDrag(newPart, partDropListener);
		}
		else if (newContainer instanceof RootLayoutContainer) {
			// Create a new folder and add both items
			PartTabFolder folder = new PartTabFolder(folderStyle);
			((RootLayoutContainer) newContainer).replace(refPart, folder);
			folder.add(refPart);
			folder.add(newPart);
			if (!(refPart instanceof ILayoutContainer)) {
				folder.enableDrag(refPart, partDropListener);
			}
			folder.enableDrag(newPart, partDropListener);
		}
	}

	/**
	 * Update the container to show the correct visible tab based on<br>
	 * the activation list.<br>
	 * 
	 * @param container
	 *            ILayoutContainer
	 */
	// from PerspectivePresentation
	private void updateContainerVisibleTab(ILayoutContainer container) {
		if (!(container instanceof PartTabFolder)) {
			return;
		}
		// TODO: Callback??
		// PartTabFolder folder = (PartTabFolder) container;
		// ILayoutPart[] parts = folder.getChildren();
		// if (parts.length < 1)
		// return;

		// PartPane selPart = null;
		// int topIndex = 0;
		// IDnpFramePartReference[] sortedPartsArray =
		// workPane.SortedParts;
		// //((WorkbenchPage)page).getSortedParts();
		// ArrayList sortedParts = new ArrayList(sortedPartsArray);
		// for (int i = 0; i < parts.length; i++) {
		// if (parts[i] instanceof PartPane) {
		// IDnpFramePartReference part = ((PartPane)
		// parts[i]).PartReference;
		// int index = sortedParts.IndexOf(part);
		// if (index >= topIndex) {
		// topIndex = index;
		// selPart = (PartPane)parts[i];
		// }
		// }
		// }

		// if (selPart != null) {
		// //Make sure the new visible part is restored.
		// //If part can't be restored an error part is created.
		// selPart.PartReference.GetPart(true);
		// int selIndex = folder.IndexOf(selPart);
		// if (folder.SelectionIndex() != selIndex)
		// folder.SetSelection(selIndex);
		// }
	}

	/**
	 * Zoom out.<br>
	 */
	// from PerspectivePresentation
	public void zoomOut() {
		// Sanity check.
		if (zoomPart == null) {
			return;
		}

		// PartPane pane =
		// ((WorkbenchPartReference)zoomPart).getPane();

		if (zoomPart.isViewPane( )) {
			parentWidget.setRedraw(false);
			mainLayout.zoomOut( );
			zoomPart.setZoomed(false);
			parentWidget.setRedraw(true);
		}
		else { // if null
			parentWidget.setRedraw(false);
			mainLayout.zoomOut( );
			parentWidget.setRedraw(true);
		}

		// Deref all.
		zoomPart = null;
	}

	/**
	 * Returns whether changes to a part will affect zoom. There are a<br>
	 * few conditions for this .. - we are zoomed. - the part is<br>
	 * contained in the main window. - the part is not the zoom part -<br>
	 * the part is not a fast view - the part and the zoom part are<br>
	 * not in the same editor workbook.<br>
	 * 
	 * @param pane
	 * @return boolean
	 */
	// from PerspectivePresentation
	public boolean partChangeAffectsZoom(ILayoutPart pane) {
		if (zoomPart == null) {
			return false;
		}
		if (pane.isZoomed( )) {
			return false;
		}

		return true;
	}

	/**
	 * Zoom in on a particular layout part.<br>
	 * 
	 * @param pane
	 */
	public void zoomIn(ILayoutPart pane) {
		// Save zoom part.
		zoomPart = pane;

		// If view ..
		if (pane.isViewPane( )) {
			parentWidget.setRedraw(false);
			mainLayout.zoomIn(pane);
			pane.setZoomed(true);
			parentWidget.setRedraw(true);
		}

		// Otherwise.
		else {
			zoomPart = null;
			return;
		}
	}

	/**
	 * @see IDockingManager#updateNames(de.xirp.ui.dock.ILayoutPart)
	 */
	public void updateNames(ILayoutPart part) {
		ILayoutContainer container = part.getContainer( );
		if (container instanceof PartTabFolder) {
			((PartTabFolder) container).updateTabs( );
		}
	}
}
