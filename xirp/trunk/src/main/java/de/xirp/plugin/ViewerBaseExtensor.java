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
 * ViewerBaseExtensor.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 15.11.2006:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * Class which might be extended to have more control over the
 * synchronization mechanism of the {@link ViewerBase}. All the
 * methods which are implemented by ViewerBase are available as
 * extension in here and are called before the original method, to
 * give the user the possibility to extend it for new widgets.
 * 
 * @author Rabea Gransberger
 */
public class ViewerBaseExtensor {

	/**
	 * Handles the selection event of a widget and updates the data
	 * accordingly.
	 * 
	 * @param widget
	 *            the widget which changed
	 * @param data
	 *            the data to update
	 * @param setterName
	 *            the name of the setter to update the data with
	 * @return <code>true</code> if the extension updated the data,
	 *         <code>false</code> if it failed and would like the
	 *         original method to do the job
	 */
	public boolean selectExtension(@SuppressWarnings("unused")
	final Widget widget, @SuppressWarnings("unused")
	final AbstractData data, @SuppressWarnings("unused")
	final String setterName) {
		return false;
	}

	/**
	 * Updates the given control with the data which is provided
	 * through the given getter name.
	 * 
	 * @param data
	 *            the data which contains the methods
	 * @param setterName
	 *            the name of the setter method (only provided because
	 *            it might be needed)
	 * @param getterName
	 *            the name of the getter method over which the actual
	 *            value for the control can be obtained
	 * @param control
	 *            the control to update
	 * @return <code>true</code> if the extension updated the
	 *         control, <code>false</code> if it failed and would
	 *         like the original method to do the job
	 * @see java.util.Observer#update(java.util.Observable,
	 *      java.lang.Object)
	 */
	public boolean updateExtension(@SuppressWarnings("unused")
	final AbstractData data, @SuppressWarnings("unused")
	final String setterName, @SuppressWarnings("unused")
	final String getterName, @SuppressWarnings("unused")
	final Control control) {
		return false;
	}

	/**
	 * Converts an object of any type to the given type.
	 * 
	 * @param obj
	 *            the object to convert
	 * @param clazz
	 *            the type to convert to
	 * @return the converted object or <code>null</code> if
	 *         conversion failed
	 */
	public Object convertExtension(@SuppressWarnings("unused")
	final Object obj, @SuppressWarnings("unused")
	final Class<?> clazz) {
		return null;
	}

}
