package de.xirp.ui.dock;

/*********************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights
 * reserved. This program and the accompanying materials! are made
 * available under the terms of the Common Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 * Cagatay Kavukcuoglu <cagatayk@acm.org>- Fix for bug 10025 -
 * Resizing views should not use height ratios
 ********************************************************************/

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.xirp.ui.widgets.custom.XSash;

/**
 * 
 */
class LayoutPartSash extends LayoutPart { // APORT changed

	// to public
	/**
	 * 
	 */
	private XSash sash;
	/**
	 * 
	 */
	private PartSashContainer rootContainer;
	/**
	 * 
	 */
	private int style;
	/**
	 * 
	 */
	private LayoutPartSash preLimit;
	/**
	 * 
	 */
	private LayoutPartSash postLimit;
	/**
	 * 
	 */
	protected SelectionListener selectionListener;
	/**
	 * 
	 */
	private float ratio = 0.5f;
	/**
	 * Optimize limit checks by calculating minimum and maximum ratios<br>
	 * once per drag.<br>
	 */
	private float minRatio;
	/**
	 * 
	 */
	private float maxRatio;

	/**
	 * @param rootContainer
	 * @param style
	 */
	LayoutPartSash(PartSashContainer rootContainer, int style) {
		super(null);
		this.style = style;
		this.rootContainer = rootContainer;

		selectionListener = new SelectionAdapter( ) {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.DRAG) {
					checkDragLimit(e);
				}
				else {
					LayoutPartSash.this.widgetSelected(e.x, e.y, e.width,
							e.height);
				}
			}
		};

		initDragRatios( );
	}

	/**
	 * @see LayoutPart#isViewPane()
	 */
	@Override
	public boolean isViewPane() {
		return false;
	}

	/**
	 * @param event
	 */
	// checkDragLimit contains changes by cagatayk@acm.org
	private void checkDragLimit(SelectionEvent event) { // APORT
		// renamed
		// event
		// to _event
		LayoutTree root = rootContainer.getLayoutTree( );
		LayoutTreeNode node = root.findSash(this);
		Rectangle nodeBounds = node.getBounds( );

		// optimization: compute ratios only once per drag
		if (minRatio < 0) {
			minRatio = node.getMinimumRatioFor(nodeBounds);
		}
		if (maxRatio < 0) {
			maxRatio = node.getMaximumRatioFor(nodeBounds);
		}

		if (style == SWT.VERTICAL) {
			// limit drag to current node's bounds
			if (event.x < nodeBounds.x) {
				event.x = nodeBounds.x;
			}
			int sumX = nodeBounds.x + nodeBounds.width;
			if ((event.x + event.width) > sumX) {
				event.x = sumX - event.width;
			}
			// limit drag to current node's ratios
			float width = nodeBounds.width;
			float minRatWidth = width * minRatio;
			float maxRatWidth = width * maxRatio;
			int diffX = event.x - nodeBounds.x;
			if (diffX < minRatWidth) {
				event.x = nodeBounds.x + (int) (minRatWidth);
			}
			if (diffX > width * maxRatWidth) {
				event.x = nodeBounds.x + (int) (maxRatWidth);
			}
		}
		else {
			// limit drag to current node's bounds
			if (event.y < nodeBounds.y) {
				event.y = nodeBounds.y;
			}
			int sumY = nodeBounds.y + nodeBounds.height;
			if ((event.y + event.height) > (sumY)) {
				event.y = sumY - event.height;
			}
			// limit drag to current node's ratios
			float height = nodeBounds.height;
			float minRatHeight = height * minRatio;
			float maxRatHeight = height * maxRatio;
			int diffY = event.y - nodeBounds.y;
			if (diffY < minRatHeight) {
				event.y = nodeBounds.y + (int) (minRatHeight);
			}
			if (diffY > maxRatHeight) {
				event.y = nodeBounds.y + (int) (maxRatHeight);
			}
		}
	}

	/**
	 * Creates the control
	 * 
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		if (sash == null) {
			sash = new XSash(parent, style | SWT.SMOOTH);
			sash.addListener(SWT.MouseDown, rootContainer
					.getMouseDownListener( ));
			sash.addSelectionListener(selectionListener);
		}
	}

	/**
	 * See LayoutPart#dispose
	 */
	@Override
	public void dispose() {

		if (sash != null) {
			sash.dispose( );
		}
		sash = null;
	}

	/**
	 * Gets the presentation bounds.
	 * 
	 * @return Rectangle
	 */
	@Override
	public Rectangle getBounds() {
		if (sash == null) {
			return super.getBounds( );
		}
		return sash.getBounds( );
	}

	/**
	 * Returns the part control.
	 * 
	 * @return Control
	 */
	@Override
	public Control getControl() {
		return sash;
	}

	/**
	 * @return String
	 */
	@Override
	public String getID() {
		return null;
	}

	/**
	 * @return LayoutPartSash
	 */
	public LayoutPartSash getPostLimit() {
		return postLimit;
	}

	/**
	 * @param value
	 */
	public void setPostLimit(LayoutPartSash value) {
		postLimit = value;
	}

	/**
	 * @return LayoutPartSash
	 */
	public LayoutPartSash getPreLimit() {
		return preLimit;
	}

	/**
	 * @param value
	 */
	public void setPreLimit(LayoutPartSash value) {
		preLimit = value;
	}

	/**
	 * @return float
	 */
	public float getRatio() {
		return ratio;
	}

	/**
	 * @param value
	 */
	public void setRatio(float value) {
		if (value < 0.0 || value > 1.0) {
			return;
		}
		ratio = value;

	}

	/**
	 * @return boolean
	 */
	public boolean isHorizontal() {
		return ((style & SWT.HORIZONTAL) == SWT.HORIZONTAL);
	}

	/**
	 * @return boolean
	 */
	public boolean isVertical() {
		return ((style & SWT.VERTICAL) == SWT.VERTICAL);
	}

	/**
	 * @param dragSource
	 * @return ILayoutPart
	 * @see IPartDropTarget#targetPartFor
	 */
	@Override
	public ILayoutPart targetPartFor(ILayoutPart dragSource) {
		return null;
	}

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	private void widgetSelected(int x, int y, int width, int height) {
		LayoutTree root = rootContainer.getLayoutTree( );
		LayoutTreeNode node = root.findSash(this);
		Rectangle nodeBounds = node.getBounds( );
		// Recompute ratio
		if (style == SWT.VERTICAL) {
			setRatio((float) (x - nodeBounds.x) / (float) nodeBounds.width);
		}
		else {
			setRatio((float) (y - nodeBounds.y) / (float) nodeBounds.height);
		}

		node.setBounds(nodeBounds);
		initDragRatios( );
	}

	/**
	 * 
	 */
	private void initDragRatios() {
		minRatio = maxRatio = -1f;
	}

}
