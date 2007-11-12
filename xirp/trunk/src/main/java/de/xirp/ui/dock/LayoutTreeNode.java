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

import de.xirp.ui.widgets.custom.XSash;
import de.xirp.util.Constants;

/**
 * Implementation of a tree node. The node represents a sash and it<br>
 * allways has two children.<br>
 */
class LayoutTreeNode extends LayoutTree {

	/**
	 * The node children witch may be another node or a leaf
	 */
	private LayoutTree[] children = new LayoutTree[2];
	/**
	 * The sash's width when vertical and hight on horizontal
	 */
	private static final int SASH_WIDTH = 3;

	/**
	 * Initialize this tree with its sash.
	 * 
	 * @param sash
	 */
	public LayoutTreeNode(LayoutPartSash sash) {
		super(sash);
	}

	/**
	 * Add the relation ship between the children in the list and<br>
	 * returns the left children.<br>
	 * 
	 * @param relations
	 * @return ILayoutPart
	 */
	@Override
	public ILayoutPart computeRelation(ArrayList<RelationshipInfo> relations) {
		RelationshipInfo r = new RelationshipInfo( );
		r.relative = children[0].computeRelation(relations);
		r.part = children[1].computeRelation(relations);
		r.ratio = getSash( ).getRatio( );
		r.relationship = getSash( ).isVertical( ) ? IPageLayout.Location.RIGHT
				: IPageLayout.Location.BOTTOM;
		relations.add(0, r);
		return r.relative;
	}

	/**
	 * Dispose all Sashs in this tree.<br>
	 */
	@Override
	public void disposeSashes() {
		children[0].disposeSashes( );
		children[1].disposeSashes( );
		getSash( ).dispose( );
	}

	/**
	 * Find a LayoutPart in the tree and return its sub-tree. Returns<br>
	 * null if the child is not found.<br>
	 * 
	 * @param child
	 * @return LayoutTree
	 */
	@Override
	public LayoutTree find(ILayoutPart child) {
		LayoutTree node = children[0].find(child);
		if (node != null){
			return node;
		}
		node = children[1].find(child);
		return node;
	}

	/**
	 * Find the part that is in the bottom right position.<br>
	 * 
	 * @return ILayoutPart
	 */
	@Override
	public ILayoutPart findBottomRight() {
		if (children[1].isVisible( )){
			return children[1].findBottomRight( );
		}
		return children[0].findBottomRight( );
	}

	/**
	 * Go up in the tree finding a parent that is common of both<br>
	 * children. Return the subtree.<br>
	 * 
	 * @param child1
	 * @param child2
	 * @return LayoutTreeNode
	 */
	public LayoutTreeNode findCommonParent(ILayoutPart child1,
			ILayoutPart child2) {
		return findCommonParent(child1, child2, false, false);
	}

	/**
	 * Go up in the tree finding a parent that is common of both<br>
	 * children. Return the subtree.<br>
	 * 
	 * @param child1
	 * @param child2
	 * @param foundChild1
	 * @param foundChild2
	 * @return LayoutTreeNode
	 */
	LayoutTreeNode findCommonParent(ILayoutPart child1, ILayoutPart child2,
			boolean foundChild1, boolean foundChild2) {
		if (!foundChild1){
			foundChild1 = find(child1) != null;
		}
		if (!foundChild2){
			foundChild2 = find(child2) != null;
		}
		if (foundChild1 && foundChild2){
			return this;
		}
		if (parent == null){
			return null;
		}
		return parent
				.findCommonParent(child1, child2, foundChild1, foundChild2);
	}

	/**
	 * Find a sash in the tree and return its sub-tree. Returns null<br>
	 * if the sash is not found.<br>
	 * 
	 * @param sash
	 * @return LayoutTreeNode
	 */
	@Override
	public LayoutTreeNode findSash(LayoutPartSash sash) {
		if (this.getSash( ) == sash){
			return this;
		}
		LayoutTreeNode node = children[0].findSash(sash);
		if (node != null){
			return node;
		}
		node = children[1].findSash(sash);
		if (node != null){
			return node;
		}
		return null;
	}

	/**
	 * Sets the elements in the array of sashes with the<br>
	 * Left,Rigth,Top and Botton sashes. The elements may be null<br>
	 * depending whether there is a shash beside the <code>part</code><br>
	 * 
	 * @param child
	 * @param sashes
	 */
	public void findSashes(LayoutTree child, HSashes sashes) {
		XSash sash = (XSash) getSash( ).getControl( );
		boolean leftOrTop = children[0] == child;
		if (sash != null) {
			LayoutPartSash partSash = getSash( );
			// If the child is in the left, the sash
			// is in the rigth and so on.
			if (leftOrTop) {
				if (partSash.isVertical( )) {
					if (sashes.right == null){
						sashes.right = sash;
					}
				}
				else {
					if (sashes.bottom == null){
						sashes.bottom = sash;
					}
				}
			}
			else {
				if (partSash.isVertical( )) {
					if (sashes.left == null){
						sashes.left = sash;
					}
				}
				else {
					if (sashes.top == null){
						sashes.top = sash;
					}
				}
			}
		}
		if (getParent( ) != null){
			getParent( ).findSashes(this, sashes);
		}
	}

	/**
	 * Return the bounds of this tree which is the rectangle that<br>
	 * contains all Controls in this tree.<br>
	 * 
	 * @return Rectangle
	 */
	@Override
	public Rectangle getBounds() {
		if (!children[0].isVisible( )){
			return children[1].getBounds( );
		}

		if (!children[1].isVisible( )){
			return children[0].getBounds( );
		}

		Rectangle leftBounds = children[0].getBounds( );
		Rectangle rightBounds = children[1].getBounds( );
		Rectangle sashBounds = getSash( ).getBounds( );
		Rectangle result = new Rectangle(leftBounds.x, leftBounds.y,
				leftBounds.width, leftBounds.height);
		if (getSash( ).isVertical( )) {
			result.width = rightBounds.width + leftBounds.width
					+ sashBounds.width;
		}
		else {
			result.height = rightBounds.height + leftBounds.height
					+ sashBounds.height;
		}
		return result;
	}

	/**
	 * Resize the parts on this tree to fit in <code>bounds</code>.<br>
	 * 
	 * @param bounds
	 */
	@Override
	public void setBounds(Rectangle bounds) {
		if (!children[0].isVisible( )) {
			children[1].setBounds(bounds);
			return;
		}
		if (!children[1].isVisible( )) {
			children[0].setBounds(bounds);
			return;
		}

		Rectangle leftBounds = new Rectangle(bounds.x, bounds.y, bounds.width,
				bounds.height);
		Rectangle rightBounds = new Rectangle(bounds.x, bounds.y, bounds.width,
				bounds.height);
		Rectangle sashBounds = new Rectangle(bounds.x, bounds.y, bounds.width,
				bounds.height);
		if (getSash( ).isVertical( )) {
			// Work on x and width
			leftBounds.width = (int) (getSash( ).getRatio( ) * bounds.width);
			sashBounds.x = leftBounds.x + leftBounds.width;
			sashBounds.width = SASH_WIDTH;
			rightBounds.x = sashBounds.x + sashBounds.width;
			rightBounds.width = bounds.width - leftBounds.width
					- sashBounds.width;
			adjustWidths(bounds, leftBounds, rightBounds, sashBounds);
		}
		else {
			// Work on y and height
			leftBounds.height = (int) (getSash( ).getRatio( ) * bounds.height);
			sashBounds.y = leftBounds.y + leftBounds.height;
			sashBounds.height = SASH_WIDTH;
			rightBounds.y = sashBounds.y + sashBounds.height;
			rightBounds.height = bounds.height - leftBounds.height
					- sashBounds.height;
			adjustHeights(bounds, leftBounds, rightBounds, sashBounds);
		}
		getSash( ).setBounds(sashBounds);
		children[0].setBounds(leftBounds);
		children[1].setBounds(rightBounds);
	}

	/**
	 * Returns the sash of this node.<br>
	 * 
	 * @return layoutPartSash
	 */
	public LayoutPartSash getSash() {
		return (LayoutPartSash) part;
	}

	/**
	 * Returns true if this tree has visible parts otherwise returns<br>
	 * false.<br>
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isVisible() {
		return children[0].isVisible( ) || children[1].isVisible( );
	}

	/**
	 * Recompute the ratios in this tree. The ratio for a node is the<br>
	 * width (or height if the sash is horizontal) of the left child's<br>
	 * bounds divided by the width or height of node's bounds. Sash<br>
	 * width <em>is</em> considered in ratio computation.<br>
	 */
	@Override
	public void recomputeRatio() {
		children[0].recomputeRatio( );
		children[1].recomputeRatio( );

		if (children[0].isVisible( ) && children[1].isVisible( )) {
			if (getSash( ).isVertical( )) {
				float left = children[0].getBounds( ).width;
				float right = children[1].getBounds( ).width;
				getSash( ).setRatio(left / (right + left + SASH_WIDTH));
			}
			else {
				float left = children[0].getBounds( ).height;
				float right = children[1].getBounds( ).height;
				getSash( ).setRatio(left / (right + left + SASH_WIDTH));
			}
		}
	}

	/**
	 * Remove the child and this node from the tree.<br>
	 * 
	 * @param child
	 * @return LayoutTree
	 */
	LayoutTree remove(LayoutTree child) {
		getSash( ).dispose( );
		if (parent == null) {
			// This is the root. Return the other child to be the new
			// root.
			if (children[0] == child) {
				children[1].setParent(null);
				return children[1];
			}
			children[0].setParent(null);
			return children[0];
		}

		LayoutTreeNode oldParent = parent;
		if (children[0] == child){
			oldParent.replaceChild(this, children[1]);
		}
		else{
			oldParent.replaceChild(this, children[0]);
		}
		return oldParent;
	}

	/**
	 * Replace a child with a new child and sets the new child's<br>
	 * parent.<br>
	 * 
	 * @param oldChild
	 * @param newChild
	 */
	public void replaceChild(LayoutTree oldChild, LayoutTree newChild) {
		if (children[0] == oldChild){
			children[0] = newChild;
		}
		else if (children[1] == oldChild){
			children[1] = newChild;
		}
		newChild.setParent(this);
		if (!children[0].isVisible( ) || !children[0].isVisible( )){
			getSash( ).dispose( );
		}
	}

	/**
	 * Go up from the subtree and return true if all the sash are in<br>
	 * the direction specified by <code>isVertical</code><br>
	 * 
	 * @param isVertical
	 * @param subTree
	 * @return boolean
	 */
	public boolean sameDirection(boolean isVertical, LayoutTreeNode subTree) {
		boolean treeVertical = getSash( ).isVertical( );
		if (treeVertical != isVertical){
			return false;
		}
		while (subTree != null) {
			if (this == subTree){
				return true;
			}
			if (subTree.children[0].isVisible( )
					&& subTree.children[1].isVisible( )){
				if (subTree.getSash( ).isVertical( ) != isVertical){
					return false;
				}
			}
			subTree = subTree.getParent( );
		}
		return true;
	}

	// adjustHeights added by cagatayk@acm.org
	/**
	 * @param node
	 * @param left
	 * @param right
	 * @param sash
	 * @return boolean
	 */
	private boolean adjustHeights(Rectangle node, Rectangle left,
			Rectangle right, Rectangle sash) {
		int leftAdjustment = 0;
		int rightAdjustment = 0;

		leftAdjustment = adjustChildHeight(left, node, true);
		if (leftAdjustment > 0) {
			right.height -= leftAdjustment;
		}

		rightAdjustment = adjustChildHeight(right, node, false);
		if (rightAdjustment > 0) {
			left.height -= rightAdjustment;
		}

		boolean adjusted = leftAdjustment > 0 || rightAdjustment > 0;
		if (adjusted) {
			sash.y = left.y + left.height;
			right.y = sash.y + sash.height;
		}

		return adjusted;
	}

	// adjustChildHeight added by cagatayk@acm.org
	/**
	 * @param childBounds
	 * @param nodeBounds
	 * @param left
	 * @return int
	 */
	private int adjustChildHeight(Rectangle childBounds, Rectangle nodeBounds,
			boolean left) {
		int adjustment = 0;
		int minimum = 0;

		minimum = left ? Math.round(getMinimumRatioFor(nodeBounds)
				* nodeBounds.height)
				: Math.round((1 - getMaximumRatioFor(nodeBounds))
						* nodeBounds.height)
						- SASH_WIDTH;

		if (minimum > childBounds.height) {
			adjustment = minimum - childBounds.height;
			childBounds.height = minimum;
		}

		return adjustment;
	}

	// adjustWidths added by cagatayk@acm.org
	/**
	 * @param node
	 * @param left
	 * @param right
	 * @param sash
	 * @return boolean
	 */
	private boolean adjustWidths(Rectangle node, Rectangle left,
			Rectangle right, Rectangle sash) {
		int leftAdjustment = 0;
		int rightAdjustment = 0;

		leftAdjustment = adjustChildWidth(left, node, true);
		if (leftAdjustment > 0) {
			right.width -= leftAdjustment;
		}

		rightAdjustment = adjustChildWidth(right, node, false);
		if (rightAdjustment > 0) {
			left.width -= rightAdjustment;
		}

		boolean adjusted = leftAdjustment > 0 || rightAdjustment > 0;
		if (adjusted) {
			sash.x = left.x + left.width;
			right.x = sash.x + sash.width;
		}

		return adjusted;
	}

	// adjustChildWidth added by cagatayk@acm.org
	/**
	 * @param childBounds
	 * @param nodeBounds
	 * @param left
	 * @return int
	 */
	private int adjustChildWidth(Rectangle childBounds, Rectangle nodeBounds,
			boolean left) {
		int adjustment = 0;
		int minimum = 0;

		minimum = left ? Math.round(getMinimumRatioFor(nodeBounds)
				* nodeBounds.width) : Math
				.round((1 - getMaximumRatioFor(nodeBounds)) * nodeBounds.width)
				- SASH_WIDTH;

		if (minimum > childBounds.width) {
			adjustment = minimum - childBounds.width;
			childBounds.width = minimum;
		}

		return adjustment;
	}

	// getMinimumRatioFor added by cagatayk@acm.org
	/**
	 * Obtain the minimum ratio required to display the control on the<br>
	 * "left" using its minimum dimensions.<br>
	 * 
	 * @param bounds
	 * @return float
	 */
	public float getMinimumRatioFor(Rectangle bounds) {
		float part = 0, whole = 0;

		if (getSash( ).isVertical( )) {
			part = children[0].getMinimumWidth( );
			whole = bounds.width;
		}
		else {
			part = children[0].getMinimumHeight( );
			whole = bounds.height;
		}

		return (part != 0) ? part / whole : IPageLayout.RATIO_MIN;
	}

	// getMaximumRatioFor added by cagatayk@acm.org
	/**
	 * Obtain the maximum ratio required to display the control on the<br>
	 * "right" using its minimum dimensions.<br>
	 * 
	 * @param bounds
	 * @return float
	 */
	public float getMaximumRatioFor(Rectangle bounds) {
		float part = 0, whole = 0;

		if (getSash( ).isVertical( )) {
			whole = bounds.width;
			part = whole - children[1].getMinimumWidth( );
		}
		else {
			whole = bounds.height;
			part = whole - children[1].getMinimumHeight( );
		}

		return (part != whole) ? (part - SASH_WIDTH) / whole
				: IPageLayout.RATIO_MAX;
	}

	// getMinimumHeight added by cagatayk@acm.org
	/**
	 * Obtain the minimum height required to display all controls<br>
	 * under this node.<br>
	 * 
	 * @return int
	 */
	@Override
	public int getMinimumHeight() {
		int left = children[0].getMinimumHeight( );
		int right = children[1].getMinimumHeight( );

		int minimum = 0;
		if (getSash( ).isVertical( )){
			minimum = Math.max(left, right);
		}
		else if (left > 0 || right > 0) {
			minimum = left + right;
			// only consider sash if both children are visible, fix
			// for placeholders
			if (children[0].isVisible( ) && children[1].isVisible( )) {
				minimum += SASH_WIDTH;
			}
		}

		return minimum;
	}

	// getMinimumWidth added by cagatayk@acm.org
	/**
	 * Obtain the minimum width required to display all controls under<br>
	 * this node.<br>
	 * 
	 * @return int
	 */
	@Override
	public int getMinimumWidth() {
		int left = children[0].getMinimumWidth( );
		int right = children[1].getMinimumWidth( );

		int minimum = 0;
		if (!getSash( ).isVertical( )){
			minimum = Math.max(left, right);
		}
		else if (left > 0 || right > 0) {
			minimum = left + right;
			// only consider sash if both children are visible, fix
			// for placeholders
			if (children[0].isVisible( ) && children[1].isVisible( )) {
				minimum += SASH_WIDTH;
			}
		}

		return minimum;
	}

	/**
	 * Sets a child in this node.<br>
	 * 
	 * @param left
	 * @param part
	 */
	public void setChild(boolean left, ILayoutPart part) {
		LayoutTree child = new LayoutTree(part);
		setChild(left, child);
	}

	/**
	 * Sets a child in this node.<br>
	 * 
	 * @param left
	 * @param child
	 */
	public void setChild(boolean left, LayoutTree child) {
		int index = left ? 0 : 1;
		children[index] = child;
		child.setParent(this);
	}

	/**
	 * Returns a String representation of this object.<br>
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		String s = "<null>"+Constants.LINE_SEPARATOR;//$NON-NLS-1$
		if (part.getControl( ) != null){
			s = "<@" + part.getControl( ).hashCode( ) + ">"+Constants.LINE_SEPARATOR;//$NON-NLS-2$//$NON-NLS-1$
		}
		String result = "["; //$NON-NLS-1$
		if (children[0].getParent( ) != this){
			result = result + "{" + children[0] + "}" + s;//$NON-NLS-2$//$NON-NLS-1$
		}
		else{
			result = result + children[0] + s;
		}

		if (children[1].getParent( ) != this){
			result = result + "{" + children[1] + "}]";//$NON-NLS-2$//$NON-NLS-1$
		}
		else{
			result = result + children[1] + "]";//$NON-NLS-1$
		}
		return result;
	}

	/**
	 * Create the sashes if the children are visible and dispose it if<br>
	 * they are not.<br>
	 * 
	 * @param parent
	 */
	@Override
	public void updateSashes(Composite parent) {
		if (parent == null){
			return;
		}
		children[0].updateSashes(parent);
		children[1].updateSashes(parent);
		if (children[0].isVisible( ) && children[1].isVisible( )){
			getSash( ).createControl(parent);
		}
		else{
			getSash( ).dispose( );
		}
	}
}
