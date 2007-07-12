package de.xirp.ui.dock;

/*********************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights
 * reserved. This program and the accompanying materials are made
 * available under the terms of the Common Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ********************************************************************/

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.xirp.util.II18nHandler;

/**
 * ILayoutPart is the interface for ViewPane in eclipse.<br>
 */
public interface ILayoutPart extends IPartDropTarget {

	/**
	 * The local id of the part within a Docker.<br>
	 * 
	 * @return String
	 */
	public String getID();

	/**
	 * The name of the part (for display in TabFolder tabs).<br>
	 * 
	 * @return String
	 */
	public String getName();
	
	public String getNameKey();
	
	public II18nHandler getI18nHandler();

	/**
	 * @param s
	 */
	public void setName(String s);
	
	public Object[] getI18nArgs();

	/**
	 * @return ILayoutContainer
	 */
	public ILayoutContainer getContainer();

	/**
	 * @param lc
	 */
	public void setContainer(ILayoutContainer lc);

	/**
	 * @return Control
	 */
	public Control getControl();

	/**
	 * @param p
	 * @return boolean
	 */
	public boolean isDragAllowed(Point p);

	/**
	 * @param newParent
	 */
	public void reparent(Composite newParent);

	/**
	 * 
	 */
	public void setFocus();

	/**
	 * @return Rectangle
	 */
	public Rectangle getBounds();

	/**
	 * @param r
	 */
	public void setBounds(Rectangle r);

	/**
	 * @return int
	 */
	public int getMinimumWidth();

	/**
	 * @return int
	 */
	public int getMinimumHeight();

	/**
	 * @param parent
	 */
	public void createControl(Composite parent);

	/**
	 * @return boolean
	 */
	public boolean isVisible();

	/**
	 * @param val
	 */
	public void setVisible(boolean val);

	/**
	 * @param refControl
	 */
	public void moveAbove(Control refControl);

	/**
	 * @return Control
	 */
	public Control getDragHandle();

	/**
	 * Real parts should answer yes, containers and placeholders no.<br>
	 * 
	 * @return boolean
	 */
	public boolean isViewPane();

	/**
	 * Zoom related methods and properties.<br>
	 * 
	 * @return boolean
	 */
	public boolean isZoomed();

	/**
	 * @param value
	 */
	public void setZoomed(boolean value);
}
