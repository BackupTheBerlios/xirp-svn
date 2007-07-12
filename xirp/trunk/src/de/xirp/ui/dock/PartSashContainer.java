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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.xirp.ui.dock.IPageLayout.Location;

/**
 * Abstract container that groups various layout parts (possibly other<br>
 * containers) together as a unit. Manages the placement and size of<br>
 * these layout part based on the location of sashes within the<br>
 * container.<br>
 */
abstract class PartSashContainer extends LayoutPart implements ILayoutContainer {

	/**
	 * 
	 */
	protected Composite parent;
	/**
	 * 
	 */
	protected ControlListener resizeListener;
	/**
	 * 
	 */
	protected LayoutTree root;
	/**
	 * 
	 */
	protected LayoutTree unzoomRoot;
	/**
	 * 
	 */
	protected Listener mouseDownListener;
	/**
	 * 
	 */
	boolean active = false;
	/**
	 * Array of LayoutPart
	 */
	protected ArrayList<ILayoutPart> children = new ArrayList<ILayoutPart>( );

	// protected class RelationshipInfo { // APORT changed to public
	// protected ILayoutPart part;
	// protected ILayoutPart relative;
	// protected int relationship;
	// protected float ratio;
	// }

	/**
	 * @param id
	 */
	public PartSashContainer(String id) {

		super(id);
		resizeListener = new ControlAdapter( ) {

			@Override
			public void controlResized(ControlEvent e) {
				resizeSashes(parent.getClientArea( ));
			}
		};
		// Mouse down listener to hide fast view when
		// user clicks on empty editor area or sashes.
		mouseDownListener = new Listener( ) {

			public void handleEvent(Event event) {
				if (event.type == SWT.MouseDown) {
					// page.toggleFastView(null);
				}
			}
		};
	}

	/**
	 * @see LayoutPart#isViewPane()
	 */
	@Override
	public boolean isViewPane() {
		return false;
	}

	/**
	 * Find the sashs around the specified part.<br>
	 * 
	 * @param pane
	 * @param sashes
	 */
	public void findSashes(LayoutPart pane, HSashes sashes) {
		LayoutTree part = root.find(pane);
		if (part == null) {
			return;
		}
		part.findSashes(sashes);
	}

	/**
	 * Add a part.<br>
	 * 
	 * @param child
	 */
	public void add(ILayoutPart child) {
		if (isZoomed( )) {
			zoomOut( );
		}

		if (child == null) {
			return;
		}

		RelationshipInfo info = new RelationshipInfo( );
		info.part = child;
		if (root != null) {
			findPosition(info);
		}
		addChild(info);
	}

	/**
	 * Add on part relative to another.<br>
	 * 
	 * @param child
	 * @param relativePosition
	 * @param ratio
	 * @param relative
	 */
	public void add(ILayoutPart child, Location relativePosition, float ratio,
			ILayoutPart relative) {
		if (isZoomed( )) {
			zoomOut( );
		}

		if (child == null) {
			return;
		}
		if (relative != null && !isChild(relative)) {
			return;
		}
//		if (relativePosition < IPageLayout.LEFT
//				|| relativePosition > IPageLayout.BOTTOM) {
//			relativePosition = IPageLayout.LEFT;
//		}

		// store info about relative positions
		RelationshipInfo info = new RelationshipInfo( );
		info.part = child;
		info.relationship = relativePosition;
		info.ratio = ratio;
		info.relative = relative;
		addChild(info);
	}

	/**
	 * @param info
	 */
	private void addChild(RelationshipInfo info) {
		ILayoutPart child = info.part;
		children.add(child);

		if (root == null) {
			root = new LayoutTree(child);
		}
		else {
			// Add the part to the tree.
			int vertical = info.relationship.getSWTConstant( );
			boolean left = info.relationship.isLeft( );
			LayoutPartSash sash = new LayoutPartSash(this, vertical);
			sash.setRatio(info.ratio);
			if ((parent != null) && !(child instanceof PartPlaceholder)){
				sash.createControl(parent);
			}
			root = root.insert(child, left, sash, info.relative);
		}

		childAdded(child);

		if (active) {
			child.createControl(parent);
			child.setVisible(true);
			child.setContainer(this);
			resizeSashes(parent.getClientArea( ));
		}

	}

	/**
	 * @return boolean
	 * @see ILayoutContainer#allowsBorder
	 */
	public boolean allowsBorder() {
		return true;
	}

	/**
	 * Notification that a child layout part has been added to the<br>
	 * container. Subclasses may override this method to perform any<br>
	 * container specific work.<br>
	 * 
	 * @param child
	 */
	protected abstract void childAdded(ILayoutPart child);

	/**
	 * Notification that a child layout part has been removed from the<br>
	 * container. Subclasses may override this method to perform any<br>
	 * container specific work.<br>
	 * 
	 * @param child
	 */
	protected abstract void childRemoved(ILayoutPart child);

	/**
	 * Returns an array with all the relation ship between the parts.<br>
	 * 
	 * @return RelationshipInfo[]
	 */
	public RelationshipInfo[] computeRelation() {
		LayoutTree treeRoot = root;
		if (isZoomed( )) {
			treeRoot = unzoomRoot;
		}
		ArrayList<RelationshipInfo> list = new ArrayList<RelationshipInfo>( );
		if (treeRoot == null) {
			return new RelationshipInfo[0];
		}
		RelationshipInfo r = new RelationshipInfo( );
		r.part = treeRoot.computeRelation(list);
		list.add(0, r);
		
		RelationshipInfo[] result = new RelationshipInfo[list.size( )];
		list.toArray(result);
		return result;
	}

	/**
	 * @param parentWidget
	 * @see LayoutPart#getControl
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	@Override
	public void createControl(Composite parentWidget) {
		if (active) {
			return;
		}

		parent = createParent(parentWidget);
		parent.addControlListener(resizeListener);

		ArrayList<ILayoutPart> children = (ArrayList<ILayoutPart>) this.children
				.clone( );
		for (ILayoutPart child : children) {
			// for (int i = 0, length = children.size( ); i < length;
			// i++) {
			// LayoutPart child = (LayoutPart) children.get(i);
			child.setContainer(this);
			child.createControl(parent);
		}

		if (root != null) {
			root.updateSashes(parent);
		}
		active = true;
		resizeSashes(parent.getClientArea( ));
	}

	/**
	 * Subclasses this method to specify the composite to use to<br>
	 * parent all children layout parts it contains.<br>
	 * 
	 * @param parentWidget
	 * @return Composite
	 */
	protected abstract Composite createParent(Composite parentWidget);

	/**
	 * @see LayoutPart#dispose
	 */
	@Override
	public void dispose() {
		if (!active) {
			return;
		}

		// remove all Listeners
		if (resizeListener != null && parent != null) {
			parent.removeControlListener(resizeListener);
		}

		resizeSashes(new Rectangle(-200, -200, 0, 0));

		if (children != null) {
			for (Iterator<ILayoutPart> it = children.iterator( ); it.hasNext( );) {
				// for (int i = 0, length = children.size( ); i <
				// length; i++) {
				LayoutPart child = (LayoutPart) it.next( );
				child.setContainer(null);
				// In PartSashContainer dispose really means
				// deactivate, so we
				// only dispose PartTabFolders.
				if (child instanceof PartTabFolder) {
					child.dispose( );
				}
			}
		}

		disposeParent( );
		this.parent = null;

		active = false;
	}

	/**
	 * Subclasses this method to dispose of any swt resources created<br>
	 * during createParent.<br>
	 */
	protected abstract void disposeParent();

	/**
	 * Dispose all sashs used in this perspective.<br>
	 */
	public void disposeSashes() {
		if (root != null) {
			root.disposeSashes( );
		}
	}

	/**
	 * Return the most bottom right part or null if none.<br>
	 * 
	 * @return ILayoutPart
	 */
	public ILayoutPart findBottomRight() {
		if (root == null) {
			return null;
		}
		return root.findBottomRight( );
	}

	/**
	 * Find a initial position for a new part.<br>
	 * 
	 * @param info
	 */
	private void findPosition(RelationshipInfo info) {
		info.ratio = (float) 0.5;
		info.relationship = IPageLayout.Location.RIGHT;
		info.relative = root.findBottomRight( );

		// If no parent go with default.
		if (parent == null) {
			return;
		}

		// If the relative part is too small, place the part on the
		// left of
		// everything.
		if (((float) this.getBounds( ).width
				/ (float) info.relative.getBounds( ).width > 2)
				|| ((float) this.getBounds( ).height
						/ (float) info.relative.getBounds( ).height > 4)) {
			info.relative = null;
			info.ratio = 0.75f;
		}
	}

	/**
	 * @return Rectangle
	 * @see LayoutPart#getBounds
	 */
	@Override
	public Rectangle getBounds() {
		return this.parent.getBounds( );
	}

	/**
	 * @param value
	 * @see LayoutPart#setBounds
	 */
	@Override
	public void setBounds(Rectangle value) {
		this.parent.setBounds(value);
	}

	// getMinimumHeight() added by cagatayk@acm.org
	/**
	 * @see LayoutPart#getMinimumHeight()
	 */
	@Override
	public int getMinimumHeight() {
		return this.getLayoutTree( ).getMinimumHeight( );
	}

	// getMinimumHeight() added by cagatayk@acm.org
	/**
	 * @see LayoutPart#getMinimumWidth()
	 */
	@Override
	public int getMinimumWidth() {
		return this.getLayoutTree( ).getMinimumWidth( );
	}

	/**
	 * @return ILayoutPart[]
	 * @see ILayoutContainer#getChildren
	 */
	public ILayoutPart[] getChildren() {
		ILayoutPart[] result = new ILayoutPart[children.size( )];
		children.toArray(result);
		return result;
	}

	/**
	 * @return Control
	 * @see LayoutPart#getControl
	 */
	@Override
	public Control getControl() {
		return this.parent;
	}

	/**
	 * @return LayoutTree
	 */
	public LayoutTree getLayoutTree() {
		return root;
	}

	/**
	 * Return the interested listener of mouse down events.<br>
	 * 
	 * @return Listener
	 */
	Listener getMouseDownListener() {
		return mouseDownListener;
	}

	/**
	 * Returns the composite used to parent all the layout parts<br>
	 * contained within.<br>
	 * 
	 * @return Composite
	 */
	public Composite getParent() {
		return parent;
	}

	/**
	 * @param part
	 * @return boolean
	 */
	private boolean isChild(ILayoutPart part) {
		return children.indexOf(part) >= 0;
	}

	/**
	 * @param relativePosition
	 * @param isVertical
	 * @return boolean
	 */
	private boolean isRelationshipCompatible(Location relativePosition,
			boolean isVertical) {
		if (isVertical) {
			return relativePosition.isVertical( );
		}
		else {
			return relativePosition.isHorizontal( );
		}
	}

	/**
	 * @see LayoutPart#isZoomed()
	 */
	@Override
	public boolean isZoomed() {
		return (unzoomRoot != null);
	}

	/**
	 * Move a part to a new position and keep the bounds when<br>
	 * possible, ie, when the new relative part has the same higth or<br>
	 * width as the part being move.<br>
	 * 
	 * @param child
	 * @param relativePosition
	 * @param relative
	 */
	public void move(ILayoutPart child, Location relativePosition, ILayoutPart relative) {
		LayoutTree childTree = root.find(child);
		LayoutTree relativeTree = root.find(relative);

		LayoutTreeNode commonParent = relativeTree.getParent( )
				.findCommonParent(child, relative);
		boolean isVertical = commonParent.getSash( ).isVertical( );
		boolean recomputeRatio = false;
		recomputeRatio = isRelationshipCompatible(relativePosition, isVertical)
				&& commonParent.sameDirection(isVertical, relativeTree
						.getParent( ))
				&& commonParent
						.sameDirection(isVertical, childTree.getParent( ));

		root = root.remove(child);
		int vertical = relativePosition.getSWTConstant( );
		boolean left = relativePosition.isLeft( );
		LayoutPartSash sash = new LayoutPartSash(this, vertical);
		sash.setRatio(0.5f);
		if ((parent != null) && !(child instanceof PartPlaceholder)) {
			sash.createControl(parent);
		}
		root = root.insert(child, left, sash, relative);
		root.updateSashes(parent);
		if (recomputeRatio) {
			root.recomputeRatio( );
		}

		resizeSashes(parent.getClientArea( ));
	}

	/**
	 * Remove a part.<br>
	 * 
	 * @param child
	 */
	public void remove(ILayoutPart child) {
		if (isZoomed( )) {
			zoomOut( );
		}

		if (!isChild(child)) {
			return;
		}

		children.remove(child);
		root = root.remove(child);
		if (root != null) {
			root.updateSashes(parent);
		}
		childRemoved(child);

		if (active) {
			child.setVisible(false);
			child.setContainer(null);
			resizeSashes(parent.getClientArea( ));
		}
	}

	/**
	 * Replace one part with another.<br>
	 * 
	 * @param oldChild
	 * @param newChild
	 */
	public void replace(ILayoutPart oldChild, ILayoutPart newChild) {
		if (isZoomed( )) {
			zoomOut( );
		}

		if (!isChild(oldChild)) {
			return;
		}

		children.remove(oldChild);
		children.add(newChild);

		childAdded(newChild);
		LayoutTree leaf = root.find(oldChild);
		leaf.setPart(newChild);
		root.updateSashes(parent);

		childRemoved(oldChild);
		if (active) {
			oldChild.setVisible(false);
			oldChild.setContainer(null);
			newChild.createControl(parent);
			newChild.setContainer(this);
			newChild.setVisible(true);
			resizeSashes(parent.getClientArea( ));
		}
	}

	/**
	 * @param parentSize
	 */
	private void resizeSashes(Rectangle parentSize) {
		if (!active) {
			return;
		}
		if (root != null) {
			root.setBounds(parentSize);
		}
	}

	/**
	 * Zoom in on a particular layout part.<br>
	 * The implementation of zoom is quite simple. When zoom occurs we<br>
	 * create a zoom root which only contains the zoom part. We store<br>
	 * the old root in unzoomRoot and then active the zoom root. When<br>
	 * unzoom occurs we restore the unzoomRoot and dispose the zoom<br>
	 * root.<br>
	 * Note: Method assumes we are active.<br>
	 * 
	 * @param part
	 */
	public void zoomIn(ILayoutPart part) {
		// Sanity check.
		if (unzoomRoot != null) {
			return;
		}

		// Hide main root.
		Rectangle oldBounds = root.getBounds( );
		root.setBounds(new Rectangle(0, 0, 0, 0));
		unzoomRoot = root;

		// Show zoom root.
		root = new LayoutTree(part);
		root.setBounds(oldBounds);
	}

	/**
	 * Zoom out.<br>
	 * See zoomIn for implementation details.<br>
	 * Note: Method assumes we are active.<br>
	 */
	public void zoomOut() {
		// Sanity check.
		if (unzoomRoot == null) {
			return;
		}

		// Dispose zoom root.
		Rectangle oldBounds = root.getBounds( );
		root.setBounds(new Rectangle(0, 0, 0, 0));

		// Show main root.
		root = unzoomRoot;
		root.setBounds(oldBounds);
		unzoomRoot = null;
	}

}
