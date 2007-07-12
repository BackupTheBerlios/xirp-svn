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
 * IOptionRenderer.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 29.10.2006:		Created by Rabea Gransberger.
 */
package de.xirp.ui.widgets.dialogs.preferences.renderer;

import java.util.Observer;

import org.eclipse.swt.widgets.Composite;

import de.xirp.settings.Option;

/**
 * Interface for renderers for options of settings.<br>
 * Remember to show the options with widgets supporting
 * internationalization and don't forget to implement the
 * <code>update</code> method of the Observer interface and
 * to add the observer to each value with this piece of code:<br>
 * <code>value.addObserverToValue(this);</code>
 * @author Rabea Gransberger
 */
public interface IOptionRenderer extends Observer {
	/**
	 * Renders the given options, this is creating controls
	 * referring and updating the options on the current parent
	 * composite
	 * @param parent the parent composite to render on
	 * @param option the options to render
	 */
	public void render(Composite parent, Option option);
	/**
	 * Adds a validator for validating selections made to the options
	 * values. May not be supported by the renderer.
	 * @param validator the validator to add
	 */
	public void addValidator(IValidator validator);

}
