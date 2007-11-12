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

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

/**
 * Implementation of a tree where the node is allways a sash and it<br>
 * allways has two chidren. If a children is removed the sash, ie the<br>
 * node, is removed as well and its other children placed on its<br>
 * parent.<br>
 */
class LayoutTree {

	/**
	 * The parent of this tree or null if it is the root.
	 */
	protected LayoutTreeNode parent;
	/**
	 * Any LayoutPart if this is a leaf or a LayoutSashPart if it is a<br>
	 * node.
	 */
	protected ILayoutPart part;

	/**
	 * Initialize this tree with its part.<br>
	 * 
	 * @param part
	 */
	public LayoutTree(ILayoutPart part) {
		this.part = part;
	}

	/**
	 * Add the relation ship between the children in the list and<br>
	 * returns the left children.<br>
	 * 
	 * @param relations
	 * @return ILayoutPart
	 */
	public ILayoutPart computeRelation(ArrayList<RelationshipInfo> relations) {
		return part;
	}

	/**
	 * Dispose all Sashs in this tree.<br>
	 */
	public void disposeSashes() {

	}

	/**
	 * Find a LayoutPart in the tree and return its sub-tree. Returns<br>
	 * null if the child is not found.<br>
	 * 
	 * @param child
	 * @return LayoutTree
	 */
	public LayoutTree find(ILayoutPart child) {
		if (part != child) {
			return null;
		}
		return this;
	}

	/**
	 * Find the Left,Right,Top and Botton sashes around this tree and<br>
	 * set them in <code>sashes</code><br>
	 * 
	 * @param sashes
	 */
	public void findSashes(HSashes sashes) {
		if (this.getParent( ) == null) {
			return;
		}

		getParent( ).findSashes(this, sashes);
	}

	/**
	 * Find the part that is in the bottom rigth possition.<br>
	 * 
	 * @return ILayoutPart
	 */
	public ILayoutPart findBottomRight() {
		return part;
	}

	/**
	 * Find a sash in the tree and return its sub-tree. Returns null<br>
	 * if the sash is not found.<br>
	 * 
	 * @param sash
	 * @return LayoutTreeNode
	 */
	public LayoutTreeNode findSash(LayoutPartSash sash) {
		return null;
	}

	/**
	 * Return the bounds of this tree which is the rectangle that<br>
	 * contains all Controls in this tree.<br>
	 * 
	 * @return Rectangle
	 */
	public Rectangle getBounds() {
		return part.getBounds( );
	}

	/**
	 * @param value
	 */
	public void setBounds(Rectangle value) {
		part.setBounds(value);
	}

	// getMinimumWidth() added by cagatayk@acm.org
	/**
	 * @return int
	 */
	public int getMinimumWidth() {
		return part.getMinimumWidth( );
	}

	// getMinimumHeight() added by cagatayk@acm.org
	/**
	 * @return int
	 */
	public int getMinimumHeight() {
		return part.getMinimumHeight( );
	}

	/**
	 * Returns the parent of this tree or null if it is the root.<br>
	 * 
	 * @return LayoutTreeNode
	 */
	public LayoutTreeNode getParent() {

		return parent;
	}

	/**
	 * Set the parent of this tree.<br>
	 * 
	 * @param value
	 */
	public void setParent(LayoutTreeNode value) {
		this.parent = value;
	}

	/**
	 * Inserts a new child on the tree. The child will be placed<br>
	 * beside the <code>relative</code> child. Returns the new root<br>
	 * of the tree.<br>
	 * 
	 * @param child
	 * @param left
	 * @param sash
	 * @param relative
	 * @return LayoutTree
	 */
	public LayoutTree insert(ILayoutPart child, boolean left,
			LayoutPartSash sash, ILayoutPart relative) {
		LayoutTree relativeChild = find(relative);
		LayoutTreeNode node = new LayoutTreeNode(sash);
		if (relativeChild == null) {
			// Did not find the relative part. Insert beside the root.
			node.setChild(left, child);
			node.setChild(!left, this);
			return node;
		}
		else {
			LayoutTreeNode oldParent = relativeChild.getParent( );
			node.setChild(left, child);
			node.setChild(!left, relativeChild);
			if (oldParent == null) {
				// It was the root. Return a new root.
				return node;
			}
			oldParent.replaceChild(relativeChild, node);
			return this;
		}
	}

	/**
	 * Returns true if this tree has visible parts otherwise returns<br>
	 * false.<br>
	 * 
	 * @return boolean
	 */
	public boolean isVisible() {
		return !(part instanceof PartPlaceholder);

	}

	/**
	 * Recompute the ratios in this tree.<br>
	 */
	public void recomputeRatio() {
	}

	/**
	 * Find a child in the tree and remove it and its parent. The<br>
	 * other child of its parent is placed on the parent's parent.<br>
	 * Returns the new root of the tree.<br>
	 * 
	 * @param child
	 * @return LayoutTree
	 */
	public LayoutTree remove(ILayoutPart child) {
		LayoutTree tree = find(child);
		if (tree == null) {
			return this;
		}
		LayoutTreeNode oldParent = tree.getParent( );
		if (oldParent == null) {
			// It was the root and the only child of this tree
			return null;
		}
		if (oldParent.getParent( ) == null) {
			return oldParent.remove(tree);
		}

		oldParent.remove(tree);
		return this;
	}

	/**
	 * Set the part of this leaf.<br>
	 * 
	 * @param part
	 */
	public void setPart(ILayoutPart part) {
		this.part = part;
	}

	/**
	 * Returns a string representation of this object.<br>
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		return "(" + part.toString( ) + ")";//$NON-NLS-2$//$NON-NLS-1$
	}

	/**
	 * Create the sashes if the children are visible and dispose it if<br>
	 * they are not.<br>
	 * 
	 * @param parent
	 */
	public void updateSashes(Composite parent) {
	}
}
