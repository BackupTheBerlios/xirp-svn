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
 * Util.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   Rabea Gransberger [rgransberger AT web.de]
 *
 * Changes
 * -------
 * 08.05.2006:		Created by Matthias Gernand.
 */
package de.xirp.util;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Utility methods for formatting dates, converting objects etc.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class Util {

	/**
	 * The log4j logger of this class
	 */
	private static final Logger logClass = Logger.getLogger(Util.class);

	/**
	 * Returns the given date as a formatted String. It is formatted
	 * like this:<br>
	 * 2006-05-08_16-12-56<br>
	 * yyyy-MM-dd_HH-mm-ss
	 * 
	 * @param date
	 *            The date to format
	 * @return Date as formatted string
	 */
	public static final String getTimeAsString(final Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss"); //$NON-NLS-1$
		return format.format(date);
	}

	/**
	 * Returns the given Date as a formatted String for the given
	 * formatting rule.
	 * 
	 * @param date
	 *            The Date to format
	 * @param format
	 *            The formatting rule
	 * @return String Date as formatted String
	 */
	public static final String getTimeAsString(final Date date,
			final String format) {
		SimpleDateFormat frmt = new SimpleDateFormat(format);
		return frmt.format(date);
	}

	/**
	 * Returns the given time in milliseconds as a formatted String.
	 * It is formatted like this:<br>
	 * 2006-05-08_16-12-56<br>
	 * yyyy-MM-dd_HH-mm-ss
	 * 
	 * @param millis
	 *            The milliseconds to format
	 * @return String time as formatted String
	 */
	public static final String getTimeAsString(final long millis) {
		return getTimeAsString(new Date(millis));
	}

	/**
	 * Returns the given time in milliseconds as a formatted String
	 * for the given formatting rule.
	 * 
	 * @param millis
	 *            The time in milliseconds to format
	 * @param format
	 *            The formatting rule
	 * @return String Date as formatted String
	 */
	public static final String getTimeAsString(final long millis,
			final String format) {
		SimpleDateFormat frmt = new SimpleDateFormat(format);
		return frmt.format(new Date(millis));
	}

	/**
	 * Encrypts the given string (which might be decrypted with
	 * {@link #decrypt(String)}.
	 * 
	 * @param toEncrypt
	 *            the string to encrypt
	 * @return the encrypted string
	 */
	public static final String encrypt(final String toEncrypt) {

		String enc = null;

		if (toEncrypt != null && !toEncrypt.equals("")) { //$NON-NLS-1$
			Key key = new SecretKeySpec(Constants.KEY.getBytes( ), "DES"); //$NON-NLS-1$
			Cipher cipher;
			try {
				cipher = Cipher.getInstance("DES"); //$NON-NLS-1$
				cipher.init(Cipher.ENCRYPT_MODE, key);
				byte[] encypted = cipher.doFinal(toEncrypt.getBytes("UTF8")); //$NON-NLS-1$
				enc = new BASE64Encoder( ).encode(encypted);
			}
			catch (Exception e) {
				logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
		}
		else {
			enc = ""; //$NON-NLS-1$
		}
		// logClass.debug("Encoded : " + enc +
		// Constants.LINE_SEPARATOR); //$NON-NLS-1$
		// //$NON-NLS-2$
		return enc;
	}

	/**
	 * Decrypts the given string (which was encrypted with
	 * {@link #encrypt(String)}
	 * 
	 * @param toDecrypt
	 *            the string to decrypt
	 * @return the decrypted string, as it was given to
	 *         {@link #encrypt(String)}
	 */
	public static final String decrypt(final String toDecrypt) {

		String deco = null;

		if (toDecrypt != null && !toDecrypt.equals("")) { //$NON-NLS-1$
			Key key = new SecretKeySpec(Constants.KEY.getBytes( ), "DES"); //$NON-NLS-1$
			Cipher cipher;
			byte[] decypted;

			try {
				cipher = Cipher.getInstance("DES"); //$NON-NLS-1$
				cipher.init(Cipher.DECRYPT_MODE, key);
				byte[] dec = new BASE64Decoder( ).decodeBuffer(toDecrypt);
				if (dec.length % 8 == 0) {
					decypted = cipher.doFinal(dec);
				}
				else {
					return null;
				}
				deco = new String(decypted, "UTF8"); //$NON-NLS-1$
			}
			catch (Exception e) {
				logClass.error(I18n.getString("Util.log.error") + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
		}
		else {
			deco = ""; //$NON-NLS-1$
		}
		// logClass.debug("Decoded : " + deco +
		// Constants.LINE_SEPARATOR); //$NON-NLS-1$
		// //$NON-NLS-2$
		return deco;
	}

	/**
	 * Converts an given object to the given class type.
	 * 
	 * @param obj
	 *            the object to convert
	 * @param clazz
	 *            the destination class type
	 * @return the converted object of the given class type or
	 *         <code>null</code> if the conversion failed
	 */
	public static final Object convertObject(final Object obj,
			final Class<?> clazz) {
		// the object has already the correct type
		if (clazz.isAssignableFrom(obj.getClass( ))) {
			return obj;
		}
		else {
			// use to string to convert to an object to a string
			if (String.class.isAssignableFrom(clazz)) {
				return obj.toString( );
			}
			// use the parse methods to convert from String to a
			// number
			// or the according number method to convert from
			// number to an other number
			else if (Integer.class.isAssignableFrom(clazz)) {
				if (obj instanceof String) {
					String strg = (String) obj;
					try {
						Integer i = Integer.parseInt(strg);
						return i;
					}
					catch (NumberFormatException e) {
						// do nothing
					}
				}
				else if (obj instanceof Number) {
					Number d = (Number) obj;
					return d.intValue( );
				}
			}
			else if (Double.class.isAssignableFrom(clazz)) {
				if (obj instanceof String) {
					String strg = (String) obj;
					try {
						Double i = Double.parseDouble(strg);
						return i;
					}
					catch (NumberFormatException e) {
						// do nothing
					}
				}
				else if (obj instanceof Number) {
					Number d = (Number) obj;
					return d.doubleValue( );
				}
			}
			else if (Long.class.isAssignableFrom(clazz)) {
				if (obj instanceof String) {
					String strg = (String) obj;
					try {
						Long i = Long.parseLong(strg);
						return i;
					}
					catch (NumberFormatException e) {
						// do nothing
					}
				}
				else if (obj instanceof Number) {
					Number d = (Number) obj;
					return d.longValue( );
				}
			}
			else if (Float.class.isAssignableFrom(clazz)) {
				if (obj instanceof String) {
					String strg = (String) obj;
					try {
						Float i = Float.parseFloat(strg);
						return i;
					}
					catch (NumberFormatException e) {
						// do nothing
					}
				}
				else if (obj instanceof Number) {
					Number d = (Number) obj;
					return d.floatValue( );
				}
			}
			else if (Short.class.isAssignableFrom(clazz)) {
				if (obj instanceof String) {
					String strg = (String) obj;
					try {
						Short i = Short.parseShort(strg);
						return i;
					}
					catch (NumberFormatException e) {
						// do nothing
					}
				}
				else if (obj instanceof Number) {
					Number d = (Number) obj;
					return d.shortValue( );
				}
			}
			else if (Byte.class.isAssignableFrom(clazz)) {
				if (obj instanceof String) {
					String strg = (String) obj;
					try {
						Byte i = Byte.parseByte(strg);
						return i;
					}
					catch (NumberFormatException e) {
						// do nothing
					}
				}
				else if (obj instanceof Number) {
					Number d = (Number) obj;
					return d.byteValue( );
				}
			}
			// return null if conversion fails
			return null;
		}
	}

	/**
	 * Maps have a load factor which affects the rehashing of the map.
	 * This method calculates the optimal map size for a given
	 * original map size assuming an load factor of 0.75.
	 * 
	 * @param originalSize
	 *            the size the map should have
	 * @return the optimal size for the map to avoid rehashing
	 */
	public static final int getOptimalMapSize(final int originalSize) {
		return (int) Math.ceil(originalSize / 0.75) + 1;
	}

	/**
	 * Checks if a String is empty (null or "").<br>
	 * 
	 * @param value
	 *            The String to check
	 * @return boolean <br>
	 *         <code>true</code> if String is empty<br>
	 *         <code>false</code> if String is not empty
	 */
	public static final boolean isEmpty(final String value) {
		if ((value == null) || (value.trim( ).equals(""))) { //$NON-NLS-1$ 
			return true;
		}
		return false;
	}

	/**
	 * Gets the name of the given enum or an empty string.
	 * 
	 * @param e
	 *            the enum constant to get the name for
	 * @return returns the name of the given enum or an empty string.
	 */
	@SuppressWarnings("unchecked")
	public static String getEnumNameOrNothing(Enum e) {
		if (e != null) {
			return e.name( );
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * Converts the given value from a range from <code>eMin</code>
	 * to <code>eMax</code> to a value at an according position in
	 * the range from <code>eMin</code> to <code>eMax</code> using
	 * the formula
	 * {@literal ((aMax - aMin) * (value - eMin)) / (eMax - eMin) + aMin}.
	 * Example: <br>
	 * <code>System.out.println(Util.scale(50, 100, 100, 200, 75));</code><br>
	 * results in: <code>150</code>
	 * 
	 * @param eMin
	 *            the minimum value of the input range the value is
	 *            currently in
	 * @param eMax
	 *            the maximum value of the input range the value is
	 *            currently in
	 * @param aMin
	 *            the minimum value of the input range the value is in
	 *            after conversion
	 * @param aMax
	 *            the maximum value of the input range the value is in
	 *            after conversion
	 * @param value
	 *            the input value to convert from one range to another
	 * @return the converted value
	 */
	public static double scale(double eMin, double eMax, double aMin,
			double aMax, double value) {
		return ((aMax - aMin) * (value - eMin)) / (eMax - eMin) + aMin;
	}

	/**
	 * Converts the given value from a range from <code>eMin</code>
	 * to <code>eMax</code> to a value at an according position in
	 * the range from <code>eMin</code> to <code>eMax</code> using
	 * the formula
	 * {@literal ((aMax - aMin) * (value - eMin)) / (eMax - eMin) + aMin}.
	 * Example: <br>
	 * <code>System.out.println(Util.scale(50, 100, 100, 200, 75));</code><br>
	 * results in: <code>150</code>
	 * 
	 * @param eMin
	 *            the minimum value of the input range the value is
	 *            currently in
	 * @param eMax
	 *            the maximum value of the input range the value is
	 *            currently in
	 * @param aMin
	 *            the minimum value of the input range the value is in
	 *            after conversion
	 * @param aMax
	 *            the maximum value of the input range the value is in
	 *            after conversion
	 * @param value
	 *            the input value to convert from one range to another
	 * @return the converted value
	 */
	public static long scale(long eMin, long eMax, long aMin, long aMax,
			long value) {
		return ((aMax - aMin) * (value - eMin)) / (eMax - eMin) + aMin;
	}
}
