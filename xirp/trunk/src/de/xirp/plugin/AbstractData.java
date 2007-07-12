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
 * AbstractData.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 14.09.2006:		Created by Rabea Gransberger.
 * 25.04.2007:		Changed to property changes from observer.
 */
package de.xirp.plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * AbstractData which should be implemented by plugins which like to
 * split UI and data.<br/> For every setter used in implemented
 * classes, <br/>
 * <code>pluginMainClass.notifyUI("setterMethodName");</code> <br/>
 * has to be called to inform the UI about a change in the data.
 * 
 * @author Rabea Gransberger
 */
public abstract class AbstractData {

	/**
	 * The change support delegate
	 */
	private PropertyChangeSupport support = new PropertyChangeSupport(this);

	/**
	 * Add a PropertyChangeListener to the listener list. The listener
	 * is registered for all properties. The same listener object may
	 * be added more than once, and will be called as many times as it
	 * is added. If listener is null, no exception is thrown and no
	 * action is taken.
	 * 
	 * @param listener
	 *            The PropertyChangeListener to be added
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	/**
	 * Add a PropertyChangeListener for a specific property. The
	 * listener will be invoked only when a call on firePropertyChange
	 * names that specific property. The same listener object may be
	 * added more than once. For each property, the listener will be
	 * invoked the number of times it was added for that property. If
	 * propertyName or listener is null, no exception is thrown and no
	 * action is taken.
	 * 
	 * @param propertyName
	 *            The name of the property to listen on.
	 * @param listener
	 *            The PropertyChangeListener to be added
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.lang.String,
	 *      java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Remove a PropertyChangeListener from the listener list. This
	 * removes a PropertyChangeListener that was registered for all
	 * properties. If listener was added more than once to the same
	 * event source, it will be notified one less time after being
	 * removed. If listener is null, or was never added, no exception
	 * is thrown and no action is taken.
	 * 
	 * @param listener
	 *            The PropertyChangeListener to be removed
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

	/**
	 * Remove a PropertyChangeListener for a specific property. If
	 * listener was added more than once to the same event source for
	 * the specified property, it will be notified one less time after
	 * being removed. If propertyName is null, no exception is thrown
	 * and no action is taken. If listener is null, or was never added
	 * for the specified property, no exception is thrown and no
	 * action is taken.
	 * 
	 * @param propertyName
	 *            The name of the property that was listened on.
	 * @param listener
	 *            The PropertyChangeListener to be removed
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.lang.String,
	 *      java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		support.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * Report an <code>boolean</code> bound indexed property update
	 * to any registered listeners.<br/><br/> No event is fired if
	 * old and new values are equal.<br/><br/> This is merely a
	 * convenience wrapper around the more general
	 * fireIndexedPropertyChange method which takes Object values.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property that was
	 *            changed.
	 * @param index
	 *            index of the property element that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 * @see java.beans.PropertyChangeSupport#fireIndexedPropertyChange(java.lang.String,
	 *      int, boolean, boolean)
	 */
	public void fireIndexedPropertyChange(String propertyName, int index,
			boolean oldValue, boolean newValue) {
		support.fireIndexedPropertyChange(propertyName,
				index,
				oldValue,
				newValue);
	}

	/**
	 * Report an <code>int</code> bound indexed property update to
	 * any registered listeners.<br/><br/> No event is fired if old
	 * and new values are equal.<br/><br/> This is merely a
	 * convenience wrapper around the more general
	 * fireIndexedPropertyChange method which takes Object values.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property that was
	 *            changed.
	 * @param index
	 *            index of the property element that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 * @see java.beans.PropertyChangeSupport#fireIndexedPropertyChange(java.lang.String,
	 *      int, int, int)
	 */
	public void fireIndexedPropertyChange(String propertyName, int index,
			int oldValue, int newValue) {
		support.fireIndexedPropertyChange(propertyName,
				index,
				oldValue,
				newValue);
	}

	/**
	 * Report a bound indexed property update to any registered
	 * listeners.<br/><br/> No event is fired if old and new values
	 * are equal and non-null.<br/><br/> This is merely a
	 * convenience wrapper around the more general firePropertyChange
	 * method that takes PropertyChangeEvent value.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property that was
	 *            changed.
	 * @param index
	 *            index of the property element that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 * @see java.beans.PropertyChangeSupport#fireIndexedPropertyChange(java.lang.String,
	 *      int, java.lang.Object, java.lang.Object)
	 */
	public void fireIndexedPropertyChange(String propertyName, int index,
			Object oldValue, Object newValue) {
		support.fireIndexedPropertyChange(propertyName,
				index,
				oldValue,
				newValue);
	}

	/**
	 * Fire an existing PropertyChangeEvent to any registered
	 * listeners. No event is fired if the given event's old and new
	 * values are equal and non-null.
	 * 
	 * @param evt
	 *            The PropertyChangeEvent object.
	 * @see java.beans.PropertyChangeSupport#firePropertyChange(java.beans.PropertyChangeEvent)
	 */
	public void firePropertyChange(PropertyChangeEvent evt) {
		support.firePropertyChange(evt);
	}

	/**
	 * Report an <code>boolean</code> bound property update to any
	 * registered listeners. No event is fired if old and new are
	 * equal.<br/><br/> This is merely a convenience wrapper around
	 * the more general firePropertyChange method that takes Object
	 * values.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property that was
	 *            changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String,
	 *      boolean, boolean)
	 */
	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
		support.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Report an <code>int</code> bound property update to any
	 * registered listeners. No event is fired if old and new are
	 * equal.<br/><br/> This is merely a convenience wrapper around
	 * the more general firePropertyChange method that takes Object
	 * values.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property that was
	 *            changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String,
	 *      int, int)
	 */
	public void firePropertyChange(String propertyName, int oldValue,
			int newValue) {
		support.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Report a bound property update to any registered listeners. No
	 * event is fired if old and new are equal and non-null.<br/><br/>
	 * This is merely a convenience wrapper around the more general
	 * firePropertyChange method that takes PropertyChangeEvent value.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property that was
	 *            changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		support.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Check if there are any listeners for a specific property,
	 * including those registered on all properties. If propertyName
	 * is null, only check for listeners registered on all properties.
	 * 
	 * @param propertyName
	 *            the property name.
	 * @return <code>true</code> if there are one or more listeners
	 *         for the given property
	 * @see java.beans.PropertyChangeSupport#hasListeners(java.lang.String)
	 */
	public boolean hasListeners(String propertyName) {
		return support.hasListeners(propertyName);
	}
}
