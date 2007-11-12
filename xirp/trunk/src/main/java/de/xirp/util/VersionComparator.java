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
 * VersionComparator.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [TODO: email];
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.05.2007:		Created by Rabea Gransberger.
 */
package de.xirp.util;

import java.util.Comparator;

/**
 * Comparator which may be used to compare version numbers given as a
 * string.
 * 
 * @author Rabea Gransberger
 */
public class VersionComparator implements Comparator<String> {

	/**
	 * Compares the given version strings. The version strings must
	 * alternate between numbers and points starting with a number.
	 * They might be unequal in the number of subversion numbers.<br>
	 * Example:<br>
	 * <code>VersionComparator comp = new VersionComparator( );</code><br>
	 * <code>List<String> versions = new ArrayList<String>( );</code><br>
	 * <code>versions.add("1");</code><br>
	 * <code>versions.add("2.3.1.2");</code><br>
	 * <code>versions.add("2.a.1.2");</code><br>
	 * <code>versions.add("1.3.1.2");</code><br>
	 * <code>versions.add("1.1.1.2");</code><br>
	 * <code>Collections.sort(versions, comp);</code><br>
	 * <code>System.out.println(versions);</code><br>
	 * <br>
	 * Outputs:<br>
	 * <code>[1, 1.1.1.2, 1.3.1.2, 2.3.1.2, 2.a.1.2]</code>
	 * 
	 * @param value1
	 *            the first version string
	 * @param value2
	 *            the seconds version string
	 * @return 0 if the versions are equal, -1 if the first version is
	 *         lesser then the seconds and 1 otherwise.
	 * @see java.util.Comparator#compare(java.lang.Object,
	 *      java.lang.Object)
	 */
	public int compare(String value1, String value2) {
		if (value1 == null) {
			return 1;
		}
		if (value2 == null) {
			return -1;
		}
		// Split at . separator
		String[] arr1 = value1.split("\\."); //$NON-NLS-1$
		String[] arr2 = value2.split("\\.");//$NON-NLS-1$
		// Compare each version part of the two strings
		for (int i = 0; i < arr1.length && i < arr2.length; i++) {
			String first = arr1[i];
			String second = arr2[i];
			int one = -1;
			int two = -1;
			try {
				one = Integer.parseInt(first);
			}
			catch (NumberFormatException e) {
				return 1;
			}
			try {
				two = Integer.parseInt(second);
			}
			catch (NumberFormatException e) {
				return -1;
			}
			if (one == two) {
				continue;
			}
			else {
				if (one < two) {
					return -1;
				}
				else {
					return 1;
				}
			}
		}
		if (arr1.length < arr2.length) {
			return -1;
		}
		else if (arr1.length > arr2.length) {
			return 1;
		}

		return 0;
	}
}
