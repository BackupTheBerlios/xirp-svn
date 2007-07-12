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
 * XWidgetLocaleHandler.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   Rabea Gransberger [rgransberger AT web.de]
 *
 * Changes
 * -------
 * 02.11.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

import de.xirp.ui.event.LocaleChangedEvent;
import de.xirp.ui.event.LocaleChangedListener;
import de.xirp.ui.util.ApplicationManager;
import de.xirp.ui.util.SWTUtil;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * Handles the translations for widgets which have methods for setting
 * a text and/or tooltip text. To use this class, create an instance
 * and delegate the methods for setting text/tooltip to this instance.<br>
 * All widgets in the package
 * {@link de.xirp.ui.widgets.custom} do already support
 * translations by methods which mainly end on "ForLocaleKey".
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class XWidgetLocaleHandler {

	/**
	 * The Logger for this class
	 */
	private static final Logger logClass = Logger.getLogger(XWidgetLocaleHandler.class);
	/**
	 * Listener which is informed if the {@link java.util.Locale}
	 * which the application uses has changed.
	 */
	private LocaleChangedListener localeListener;
	/**
	 * The handler used for translation
	 */
	private II18nHandler handler;
	/**
	 * Translation key for the text of the widget
	 */
	private String textKey;
	/**
	 * Translation key for the tooltip of the widget
	 */
	private String tooltipKey;
	/**
	 * The arguments used for replacing parameters for the text
	 * translation
	 */
	private Object[] textArgs;
	/**
	 * The arguments used for replacing parameters for the tooltip
	 * translation
	 */
	private Object[] tooltipArgs;
	/**
	 * The underlying widget
	 */
	private Widget widget;
	/**
	 * The method for setting the text of the widget
	 */
	private Method textMethod;
	/**
	 * The method for setting the tooltip of the widget
	 */
	private Method tooltipMethod;

	/**
	 * Creates a new locale handler which will handle the translations
	 * for the given widget using the default internationalization
	 * handler of the application.
	 * 
	 * @param widget
	 *            the widget which should be translatable
	 */
	public XWidgetLocaleHandler(Widget widget) {
		this.widget = widget;
		this.handler = I18n.getGenericI18n( );
		init( );
	}

	/**
	 * Creates a new locale handler which will handle the translations
	 * for the given widget using the given internationalization
	 * handler for translations.
	 * 
	 * @param widget
	 *            the widget which should be translatable
	 * @param handler
	 *            the handler to use for translations.
	 */
	public XWidgetLocaleHandler(Widget widget, II18nHandler handler) {
		this.widget = widget;
		this.handler = handler;
		init( );
	}

	/**
	 * Tries to find setter methods for text and tooltip and
	 * initializes the locale change listener.
	 */
	private void init() {
		if (handler == null) {
			handler = I18n.getGenericI18n( );
		}
		// look for setText and setToolTipText methods of the given
		// widget
		try {
			BeanInfo info = Introspector.getBeanInfo(widget.getClass( ));
			for (PropertyDescriptor des : info.getPropertyDescriptors( )) {
				String name = des.getName( );
				if (name.equalsIgnoreCase("text")) { //$NON-NLS-1$
					textMethod = des.getWriteMethod( );
				}
				else if (name.equalsIgnoreCase("toolTipText")) { //$NON-NLS-1$
					tooltipMethod = des.getWriteMethod( );
				}
			}
		}
		catch (IntrospectionException e1) {
			logClass.error("Error: " + e1.getMessage( ) + Constants.LINE_SEPARATOR, e1); //$NON-NLS-1$
		}

		// add a listener for the locale changed event
		localeListener = new LocaleChangedListener( ) {

			public void localeChanged(@SuppressWarnings("unused")
			LocaleChangedEvent event) {
				// set the tooltip if method is available
				if (tooltipMethod != null && tooltipKey != null) {
					if (tooltipArgs != null) {
						setToolTipText(tooltipKey, tooltipArgs);
					}
					else {
						setToolTipText(tooltipKey);
					}
				}
				// set the text if method is available
				if (textMethod != null && textKey != null) {
					if (textArgs != null) {
						setText(textKey, textArgs);
					}
					else {
						setText(textKey);
					}
					if (widget instanceof Control) {
						// layout the parent to have all
						// controls with text be able to show the text
						// with
						// its right size
						Control control = (Control) widget;
						control.getParent( ).layout( );
					}
				}
			}
		};
		ApplicationManager.addLocaleChangedListener(localeListener);

		// remove the listener when widget is disposed
		widget.addDisposeListener(new DisposeListener( ) {

			public void widgetDisposed(@SuppressWarnings("unused")
			DisposeEvent e) {
				ApplicationManager.removeLocaleChangedListener(localeListener);
			}

		});
	}

	/**
	 * Call the given method with the translated text and the current
	 * widget as arguments. Does nothing if the widget fails assertion
	 * or the given method is <code>null</code>.
	 * 
	 * @param method
	 *            the method to call
	 * @param key
	 *            the key to use for translation
	 * @param args
	 *            optional translation arguments (may be
	 *            <code>null</code>
	 */
	protected void callMethod(Method method, String key, Object... args) {
		if (SWTUtil.swtAssert(widget) && method != null) {
			try {
				if (args != null) {
					method.invoke(widget, handler.getString(key, args));
				}
				else {
					method.invoke(widget, handler.getString(key));
				}
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
		}
	}

	/**
	 * Sets the text of the underlying widget which is translated from
	 * the given key replacing any parameters of the translation with
	 * the given arguments.
	 * 
	 * @param key
	 *            The key for translation.
	 * @param args
	 *            The arguments to use for replacing parameters of the
	 *            translation.
	 */
	protected void setText(String key, Object... args) {
		this.textKey = key;
		this.textArgs = args;

		if (widget != null && textMethod != null) {
			callMethod(textMethod, textKey, textArgs);
		}
	}

	/**
	 * Sets the tooltip of the underlying widget which is translated
	 * from the given key replacing any parameters of the translation
	 * with the given arguments.
	 * 
	 * @param key
	 *            The key for translation.
	 * @param objects
	 *            The arguments to use for replacing parameters of the
	 *            translation.
	 */
	protected void setToolTipText(String key, Object... objects) {
		this.tooltipKey = key;
		this.tooltipArgs = objects;

		if (widget != null && tooltipMethod != null) {
			callMethod(tooltipMethod, tooltipKey, tooltipArgs);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (widget != null) {
			return "HWidgetLocaleHandler: " + widget.toString( ); //$NON-NLS-1$
		}
		return "HWidgetLocaleHandler"; //$NON-NLS-1$
	}

	/**
	 * Gets the key used for translating the text of this widget.
	 * 
	 * @return the text key or <code>null</code> if none was set.
	 */
	public String getTextKey() {
		return textKey;
	}

	/**
	 * Gets the key used for translating the tooltip of this widget.
	 * 
	 * @return the tooltip key or <code>null</code> if none was set.
	 */
	public String getToolTipTextKey() {
		return tooltipKey;
	}

	/**
	 * Gets the arguments used for replacing parameters in the
	 * translation for the text.
	 * 
	 * @return the arguments or <code>null</code> if none were set.
	 */
	public Object[] getTextArgs() {
		return textArgs;
	}

	/**
	 * Gets the arguments used for replacing parameters in the
	 * translation for the tooltip.
	 * 
	 * @return the arguments or <code>null</code> if none were set.
	 */
	public Object[] getToolTipTextArgs() {
		return tooltipArgs;
	}

}
