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
 * ViewerBase.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 14.09.2006:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import de.xirp.ui.util.SWTUtil;
import de.xirp.ui.widgets.custom.XComposite;
import de.xirp.ui.widgets.custom.XStyledSpinner;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Util;
import de.xirp.util.collections.MultiValueHashMap;

/**
 * Base class for UI implementations which are split from the
 * underlying data. This class handles the synchronization between UI
 * and data with different class types almost automatically.
 * 
 * @author Rabea Gransberger
 */
public class ViewerBase extends XComposite implements PropertyChangeListener {

	/**
	 * log4j Logger of this Class
	 */
	private static Logger logClass = Logger.getLogger(ViewerBase.class);

	/**
	 * Key under which the setter method is saved in the properties of
	 * the widget
	 */
	private static final String SETTER_KEY = "setterMethod"; //$NON-NLS-1$

	/**
	 * Prefix for a setter method ({@value})
	 */
	private static final String SET_PREFIX = "set"; //$NON-NLS-1$

	/**
	 * Prefix for a getter method ({@value})
	 */
	private static final String GET_PREFIX = "get"; //$NON-NLS-1$
	/**
	 * Prefix for a getter method ({@value})
	 */
	private static final String IS_PREFIX = "is"; //$NON-NLS-1$
	/**
	 * The data which the GUI should synchronize with
	 */
	private AbstractData data;

	/**
	 * Mapping from setter to controls
	 */
	private MultiValueHashMap<String, Control> setter = new MultiValueHashMap<String, Control>( );

	/**
	 * Extension instance for update, selection and conversion
	 */
	private ViewerBaseExtensor extensor = new ViewerBaseExtensor( );

	/**
	 * Constructs a new instance for synchronizing UI and data
	 * 
	 * @param parent
	 *            the parent composite of this composite
	 * @param style
	 *            the style for this composite
	 * @param data
	 *            data to synchronize with
	 */
	protected ViewerBase(Composite parent, int style, AbstractData data) {
		super(parent, style);
		this.data = data;
	}

	/**
	 * Checks if a method with the given name exists in the data of
	 * this instance
	 * 
	 * @param name
	 *            the name of the method
	 * @return <code>true</code> if the method exists
	 */
	private boolean methodExists(String name) {
		if (data == null) {
			return false;
		}
		try {
			@SuppressWarnings("unused")
			Method method = data.getClass( ).getMethod(name, new Class[] {});
		}
		catch (SecurityException e) {
			return false;
		}
		catch (NoSuchMethodException e) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the value for a given getter from the data
	 * 
	 * @param getterName
	 *            the name of the getter in the data
	 * @return the value or <code>null</code> if the method was not
	 *         found or another problem occurred.
	 */
	private Object get(String getterName) {
		try {
			Method method = data.getClass( )
					.getMethod(getterName, new Class[0]);
			Object test = method.invoke(data, new Object[0]);

			return test;
		}
		catch (SecurityException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		catch (NoSuchMethodException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		catch (IllegalArgumentException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		catch (IllegalAccessException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		catch (InvocationTargetException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}

		return null;
	}

	/**
	 * Synchronize the control with the named property of the data.<br/>
	 * This is shorthand for<br>
	 * <code>sync(control, {@value #SET_PREFIX}+propertyName, {@value #GET_PREFIX}+propertyName);</code>
	 * or<br>
	 * <code>sync(control, {@value #SET_PREFIX}+propertyName, {@value #IS_PREFIX}+propertyName);</code>
	 * 
	 * @param control
	 *            the control to sync with data
	 * @param propertyName
	 *            the property name of a field of the data
	 */
	protected void sync(Control control, String propertyName) {
		String uPropertyName = StringUtils.capitalize(propertyName);
		String setterName = SET_PREFIX + uPropertyName;
		String getterName = GET_PREFIX + uPropertyName;
		boolean exists = methodExists(getterName);
		if (!exists) {
			if (methodExists(IS_PREFIX + uPropertyName)) {
				getterName = IS_PREFIX + uPropertyName;
			}
		}

		if (data != null) {
			data.addPropertyChangeListener(propertyName, this);
		}

		setter.put(setterName, control);

		// Add the setter to the control
		control.setData(SETTER_KEY, setterName);

		// determine appropriate listener type for the control
		int listenerType = SWT.Selection;
		if (control instanceof Text) {
			listenerType = SWT.Modify;
		}

		// add the listener
		control.addListener(listenerType, new Listener( ) {

			public void handleEvent(Event event) {
				// only handle the event if theres no boolean
				// indicating that this event was already handled
				Object obj = event.data;
				if (obj == null || !(obj instanceof Boolean)) {
					select(event);
				}
			}
		});

		Object value = get(getterName);

		this.propertyChange(new PropertyChangeEvent(data,
				propertyName,
				null,
				value));
	}

	/**
	 * Handles the selection event of a control registered for
	 * synchronization
	 * 
	 * @param event
	 *            the events data
	 */
	private final void select(Event event) {

		Widget widget = event.widget;
		// Get the setter name of the events widget
		String setterName = (String) widget.getData(SETTER_KEY);

		// check if this widget is handled by the extension
		// mechanism for the viewer base
		if (selectExtension(widget, data, setterName)) {
			return;
		}

		// determine the type and value of the new state
		// of the control
		Class<?> clazz = null;
		Object obj = null;

		if (widget instanceof Slider) {
			Slider slider = (Slider) widget;
			obj = slider.getSelection( );
			clazz = int.class;
		}
		else if (widget instanceof Combo) {
			Combo combo = (Combo) widget;
			obj = combo.getText( );
			clazz = String.class;
		}
		else if (widget instanceof Button) {
			Button comp = (Button) widget;
			// not for push, only for radio and toggle
			if (!((comp.getStyle( ) & SWT.PUSH) != 0)) {
				obj = comp.getSelection( );
				clazz = boolean.class;
			}
		}
		else if (widget instanceof Spinner) {
			Spinner comp = (Spinner) widget;
			obj = comp.getSelection( );
			clazz = int.class;
		}
		else if (widget instanceof XStyledSpinner) {
			XStyledSpinner comp = (XStyledSpinner) widget;
			if (comp.isDouble( )) {
				obj = comp.getSelectionDouble( );
				clazz = double.class;
			}
			else {
				obj = comp.getSelection( );
				clazz = int.class;
			}
		}
		else if (widget instanceof Scale) {
			Scale comp = (Scale) widget;
			obj = comp.getSelection( );
			clazz = int.class;
		}
		else if (widget instanceof Text) {
			Text comp = (Text) widget;
			obj = comp.getText( );
			clazz = String.class;
		}

		// if the control is supported we should have a value now
		if (obj != null) {
			try {
				// get the setter and invoke with the value
				Method method = data.getClass( ).getMethod(setterName, clazz);
				method.invoke(data, new Object[] {obj});
			}
			catch (Exception e) {
				logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
						+ Constants.LINE_SEPARATOR, e);
			}
		}

	}

	/**
	 * Converts an object of any type to the given type.</br> The
	 * conversion is first tried with the extension, and if this fails
	 * with the original method of this class.
	 * 
	 * @param obj
	 *            the object to convert
	 * @param clazz
	 *            the type to convert to
	 * @return the converted object or <code>null</code> if
	 *         conversion failed
	 */
	private Object convertObject(Object obj, Class<?> clazz) {
		Object converted = convertExtension(obj, clazz);
		if (converted == null) {
			converted = Util.convertObject(obj, clazz);
		}
		return converted;
	}

	/**
	 * Sets the given value to the control, by converting it to the
	 * type which the control needs
	 * 
	 * @param control
	 *            the control to set the value on
	 * @param obj
	 *            the value to set
	 */
	private void setValue(final Control control, Object obj) {
		if (!SWTUtil.swtAssert(control)) {
			return;
		}

		Method m = null;
		Object converted = null;
		final Event event = new Event( );
		event.data = Boolean.FALSE;
		event.widget = control;
		int eventType = -1;

		try {
			m = control.getClass( )
					.getMethod("setSelection", new Class[] {int.class}); //$NON-NLS-1$
			converted = convertObject(obj, Integer.class);
			eventType = SWT.Selection;
		}
		catch (Exception e) {
			// logClass.debug(e.getMessage( ) +
			// Constants.LINE_SEPARATOR, e);
		}

		if (m == null) {
			try {
				m = control.getClass( )
						.getMethod("setSelection", new Class[] {boolean.class}); //$NON-NLS-1$
				converted = convertObject(obj, Boolean.class);
				eventType = SWT.Selection;
			}
			catch (Exception e) {
				// logClass.debug(e.getMessage()
				// + Constants.LINE_SEPARATOR, e);
			}
		}

		if (m == null) {
			try {
				m = control.getClass( )
						.getMethod("setText", new Class[] {String.class}); //$NON-NLS-1$
				converted = convertObject(obj, String.class);
				eventType = SWT.Modify;
			}
			catch (Exception e) {
				// logClass.warn("Warning: " + e.getMessage()
				// + Constants.LINE_SEPARATOR, e);
			}
		}

		if (m != null && converted != null) {
			final Method setter = m;
			final Object value = converted;

			final int type = eventType;

			Display.getDefault( ).syncExec(new Runnable( ) {

				public void run() {
					try {
						setter.invoke(control, new Object[] {value});
						control.notifyListeners(type, event);
					}
					catch (Exception e) {
						logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
								+ Constants.LINE_SEPARATOR, e);
					}
				}

			});
		}
		else {
			logClass.warn(I18n.getString("ViewerBase.log.noMethodFound") //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR);
		}
	}

	/**
	 * The synchronization may be extended through an extensor which
	 * will handle the selection event, update and conversion.
	 * 
	 * @param extensor
	 *            the extensor to use. <code>null</code> to reset
	 *            the extension to the default (none)
	 */
	public void setExtension(ViewerBaseExtensor extensor) {
		if (extensor == null) {
			this.extensor = new ViewerBaseExtensor( );
		}
		else {
			this.extensor = extensor;
		}

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
	private Object convertExtension(final Object obj, final Class<?> clazz) {
		return extensor.convertExtension(obj, clazz);
	}

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
	private boolean selectExtension(final Widget widget,
			final AbstractData data, final String setterName) {
		return extensor.selectExtension(widget, data, setterName);
	}

	/**
	 * If a property has changed the controls which are referenced by
	 * this property are determined and the value of the property is
	 * set to them. Controls which are already disposed are ignored.
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String propName = event.getPropertyName( );
		String setterName = SET_PREFIX + StringUtils.capitalize(propName);

		List<Control> controls = setter.get(setterName);
		List<Control> delete = new ArrayList<Control>(controls.size( ));
		for (Control control : controls) {
			if (SWTUtil.swtAssert(control)) {
				Object value = event.getNewValue( );
				// if the method returned something
				// update the ui with it
				if (value != null) {
					setValue(control, value);
				}
			}
			else {
				// mark disposed controls for deletion
				delete.add(control);
			}
		}
		// Delete disposed controls
		for (Control control : delete) {
			setter.remove(setterName, control);
		}
	}
}
