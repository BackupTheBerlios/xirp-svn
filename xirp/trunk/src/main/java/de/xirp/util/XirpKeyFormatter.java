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
 * XirpKeyFormatter.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 20.08.2006:		Created by Rabea Gransberger.
 */
package de.xirp.util;

import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.bindings.keys.KeyLookupFactory;
import org.eclipse.jface.bindings.keys.formatting.AbstractKeyFormatter;
import org.eclipse.swt.SWT;

/**
 * Implementation of JFaces key formatter, using the localized
 * resource bundles of the application.
 * 
 * @author Rabea Gransberger
 */
public class XirpKeyFormatter extends AbstractKeyFormatter {

	/**
	 * Formats an individual key into a human readable format. It uses
	 * the Application I18n bundle.
	 * 
	 * @see org.eclipse.jface.bindings.keys.formatting.AbstractKeyFormatter#format(org.eclipse.jface.bindings.keys.KeySequence)
	 */
	@Override
	public String format(final int key) {
		final IKeyLookup lookup = KeyLookupFactory.getDefault( );
		final String name = lookup.formalNameLookup(key);

		// NL issue. Possible problem with 4-byte characters.
		if (name.length( ) == 1) {
			return name;
		}

		return I18n.getString(name);
	}

	/**
	 * An accessor for the delimiter you wish to use between key
	 * strokes.
	 * 
	 * @see org.eclipse.jface.bindings.keys.formatting.NativeKeyFormatter
	 */
	@Override
	public String getKeyStrokeDelimiter() {
		return ","; //$NON-NLS-1$
	}

	/**
	 * An accessor for the delimiter you wish to use between keys.
	 * 
	 * @see org.eclipse.jface.bindings.keys.formatting.NativeKeyFormatter
	 */
	@Override
	public String getKeyDelimiter() {
		return "+"; //$NON-NLS-1$
	}

	/**
	 * Separates the modifier keys from each other, and then places
	 * them in an array in some sorted order. The sort order is
	 * dependent on the type of formatter.
	 * 
	 * @see org.eclipse.jface.bindings.keys.formatting.NativeKeyFormatter
	 */
	@Override
	protected int[] sortModifierKeys(final int modifierKeys) {
		final IKeyLookup lookup = KeyLookupFactory.getDefault( );
		final String platform = SWT.getPlatform( );
		final int[] sortedKeys = new int[4];
		int index = 0;

		if ("win32".equals(platform)) { //$NON-NLS-1$
			if ((modifierKeys & lookup.getCtrl( )) != 0) {
				sortedKeys[index++] = lookup.getCtrl( );
			}
			if ((modifierKeys & lookup.getAlt( )) != 0) {
				sortedKeys[index++] = lookup.getAlt( );
			}
			if ((modifierKeys & lookup.getShift( )) != 0) {
				sortedKeys[index++] = lookup.getShift( );
			}

		}
		else if ("gtk".equals(platform) || "motif".equals(platform)) { //$NON-NLS-1$ //$NON-NLS-2$
			if ((modifierKeys & lookup.getShift( )) != 0) {
				sortedKeys[index++] = lookup.getShift( );
			}
			if ((modifierKeys & lookup.getCtrl( )) != 0) {
				sortedKeys[index++] = lookup.getCtrl( );
			}
			if ((modifierKeys & lookup.getAlt( )) != 0) {
				sortedKeys[index++] = lookup.getAlt( );
			}

		}
		else if ("carbon".equals(platform)) { //$NON-NLS-1$
			if ((modifierKeys & lookup.getShift( )) != 0) {
				sortedKeys[index++] = lookup.getShift( );
			}
			if ((modifierKeys & lookup.getCtrl( )) != 0) {
				sortedKeys[index++] = lookup.getCtrl( );
			}
			if ((modifierKeys & lookup.getAlt( )) != 0) {
				sortedKeys[index++] = lookup.getAlt( );
			}
			if ((modifierKeys & lookup.getCommand( )) != 0) {
				sortedKeys[index++] = lookup.getCommand( );
			}

		}

		return sortedKeys;
	}
}
