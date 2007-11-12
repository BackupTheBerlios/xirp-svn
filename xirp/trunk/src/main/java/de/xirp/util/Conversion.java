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
 * Conversion.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 14.02.2006:		Created by Rabea Gransberger.
 */
package de.xirp.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

/**
 * Utility class for converting from/to byte arrays.
 * 
 * @author Rabea Gransberger
 */
public class Conversion {

	/**
	 * The logger of this class
	 */
	private static final Logger logClass = Logger.getLogger(Conversion.class);

	/**
	 * The stream used for conversion
	 */
	private final ByteArrayOutputStream stream;

	/**
	 * Initialize the stream with the given capacity.
	 * 
	 * @param capacity
	 *            the initial array size
	 */
	protected Conversion(int capacity) {
		stream = new ByteArrayOutputStream(capacity);
	}

	/**
	 * Initialize the stream with the standard capacity.
	 */
	protected Conversion() {
		stream = new ByteArrayOutputStream( );
	}

	/**
	 * Constructs a new object for converting to/from byte arrays with
	 * an initial array capacity.
	 * 
	 * @param capacity
	 *            the initial array capacity
	 * @return the new object
	 */
	public static Conversion allocate(int capacity) {
		return new Conversion(capacity);
	}

	/**
	 * Constructs a new object for converting to/from byte arrays with
	 * an initial standard array capacity.<br>
	 * This might be much slower then using with an self set initial
	 * capacity.
	 * 
	 * @return the new object
	 */
	public static Conversion allocate() {
		return new Conversion( );
	}

	/**
	 * Appends a <code>byte[]</code> to the data.
	 * 
	 * @param data
	 *            Byte Array to append
	 * @return the conversion object itself after appending the new
	 *         data
	 */
	public Conversion append(byte[] data) {
		try {
			stream.write(data);
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}

		return this;
	}

	/**
	 * Append a single byte to the data.
	 * 
	 * @param data
	 *            Byte to append
	 * @return the conversion object itself after appending the new
	 *         data
	 */
	public Conversion append(byte data) {
		addByte(data);
		return this;
	}

	/**
	 * Get the byte array constructed in this object.
	 * 
	 * @return Byte Array with all the appended/added Data
	 */
	public byte[] getByteArray() {
		return stream.toByteArray( );
	}

	/**
	 * Adds a unspecific value of type {@link java.lang.Number} to
	 * this object.
	 * 
	 * @param <T>
	 *            Only objects which extend {@link java.lang.Number}
	 *            can be added
	 * @param s
	 *            The value to add
	 * @return the conversion object itself after appending the new
	 *         data
	 * @throws InvalidClassException
	 *             If class of the object was not supported
	 */
	public <T extends Number> Conversion addGeneric(T s)
			throws InvalidClassException {
		if (s instanceof Integer) {
			addInt((Integer) s);
		}
		else if (s instanceof Double) {
			addDouble((Double) s);
		}
		else if (s instanceof Byte) {
			addByte((Byte) s);
		}
		else if (s instanceof Float) {
			addFloat((Float) s);
		}
		else if (s instanceof Long) {
			addLong((Long) s);
		}
		else if (s instanceof Short) {
			addShort((Short) s);
		}
		else {
			throw new InvalidClassException("The Method Conversion.addGeneric() is only applicable for instances of the following classes: Byte, Double, Float, Integer, Long, Short"); //$NON-NLS-1$
		}
		return this;
	}

	/**
	 * Adds a <code>short</code> to this byte array.
	 * 
	 * @param s
	 *            the short to append
	 * @return the conversion object itself after appending the new
	 *         data
	 */
	public Conversion addShort(int s) {
		this.append(Conversion.shortToByteArray(s));
		return this;
	}

	/**
	 * Adds an <code>int</code> to this byte array.
	 * 
	 * @param i
	 *            the <code>int</code> to append
	 * @return the conversion object itself after appending the new
	 *         data
	 */
	public Conversion addInt(int i) {
		this.append(Conversion.intToByteArray(i));
		return this;
	}

	/**
	 * Adds a <code>byte</code> to this byte array
	 * 
	 * @param b
	 *            the <code>byte</code> to append
	 * @return the conversion object itself after appending the new
	 *         data
	 */
	public Conversion addByte(byte b) {
		this.append(Conversion.byteToByteArray(b));
		return this;
	}

	/**
	 * Adds a <code>double</code> to this byte array
	 * 
	 * @param d
	 *            the <code>double</code> to append
	 * @return the conversion object itself after appending the new
	 *         data
	 */
	public Conversion addDouble(double d) {
		this.append(Conversion.doubleToByteArray(d));
		return this;
	}

	/**
	 * Adds a <code>float</code> to this byte array
	 * 
	 * @param f
	 *            the <code>float</code> to append
	 * @return the conversion object itself after appending the new
	 *         data
	 */
	public Conversion addFloat(float f) {
		this.append(Conversion.floatToByteArray(f));
		return this;
	}

	/**
	 * Adds a <code>long</code> to this byte array
	 * 
	 * @param l
	 *            the <code>long</code> to append
	 * @return the conversion object itself after appending the new
	 *         data
	 */
	public Conversion addLong(long l) {
		this.append(Conversion.longToByteArray(l));
		return this;
	}

	/**
	 * Adds a <code>string</code> to this byte array
	 * 
	 * @param s
	 *            the <code>string</code> to append
	 * @return the conversion object itself after appending the new
	 *         data
	 */
	public Conversion addString(String s) {
		this.append(Conversion.stringToByteArray(s));
		return this;
	}

	/**
	 * Converts a byte array to a <code>long</code>.
	 * 
	 * @param b
	 *            The array to convert
	 * @return Byte array as a <code>long</code>
	 */
	public static long byteArrayToLong(byte[] b) {
		return ByteBuffer.wrap(b).getLong( );
	}

	/**
	 * Converts a byte array to an integer.
	 * 
	 * @param b
	 *            The array to convert
	 * @return Byte array as an integer
	 */
	public static int byteArrayToInt(byte[] b) {
		return ByteBuffer.wrap(b).getInt( );
	}

	/**
	 * Converts a byte array to a single byte
	 * 
	 * @param b
	 *            The array to convert
	 * @return Byte array as a byte
	 */
	public static byte byteArrayToByte(byte[] b) {
		return ByteBuffer.wrap(b).get( );
	}

	/**
	 * Converts a byte array to a short
	 * 
	 * @param b
	 *            The array to convert
	 * @return Byte array as a short
	 */
	public static int byteArrayToShort(byte[] b) {
		return ByteBuffer.wrap(b).getShort( );
	}

	/**
	 * Converts a byte array to a double
	 * 
	 * @param b
	 *            The array to convert
	 * @return Byte array as a double
	 */
	public static double byteArrayToDouble(byte[] b) {
		/*
		 * long result = 0; for(int i = 0; i < 7; i++) { result =
		 * result | (int)b[i]; result = result << 8; } result =
		 * result | (int)b[7]; return Double.longBitsToDouble(result);
		 */
		return ByteBuffer.wrap(b).getDouble( );
	}

	/**
	 * Converts a byte array to a float
	 * 
	 * @param b
	 *            The srray to convert
	 * @return Byte array as a float
	 */
	public static float byteArrayToFloat(byte[] b) {
		return ByteBuffer.wrap(b).getFloat( );
	}

	/**
	 * Converts a byte array to a string.
	 * 
	 * @param b
	 *            The array to convert
	 * @return Byte array as a string
	 */
	public static String byteArrayToString(byte[] b) {
		String strg = new String(b);
		return strg;
	}

	/**
	 * Converts a short to a byte array.
	 * 
	 * @param value
	 *            The short to convert
	 * @return The resulting byte array
	 */
	public static byte[] shortToByteArray(int value) {
		ByteBuffer buf = ByteBuffer.allocate(2);
		buf.putShort((short) value);
		return buf.array( );
	}

	/**
	 * Converts an int to a byte array
	 * 
	 * @param value
	 *            The int to convert
	 * @return The resulting byte array
	 */
	public static byte[] intToByteArray(int value) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.putInt(value);
		return buf.array( );
	}

	/**
	 * Converts a single byte to a byte array
	 * 
	 * @param value
	 *            The byte to convert
	 * @return The resulting byte array
	 */
	public static byte[] byteToByteArray(byte value) {
		ByteBuffer buf = ByteBuffer.allocate(1);
		buf.put(value);
		return buf.array( );
	}

	/**
	 * Converts a long to a byte array
	 * 
	 * @param value
	 *            The long to convert
	 * @return The resulting byte array
	 */
	public static byte[] longToByteArray(long value) {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putLong(value);
		return buf.array( );
	}

	/**
	 * Converts a float to a byte array
	 * 
	 * @param value
	 *            The float to convert
	 * @return The resulting byte array
	 */
	public static byte[] floatToByteArray(float value) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.putFloat(value);
		return buf.array( );
	}

	/**
	 * Converts a double to a byte array
	 * 
	 * @param value
	 *            The double to convert
	 * @return The resulting byte array
	 */
	public static byte[] doubleToByteArray(double value) {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putDouble(value);
		return buf.array( );
	}

	/**
	 * Converts a string to a byte array
	 * 
	 * @param value
	 *            The string to convert
	 * @return The resulting byte array
	 */
	public static byte[] stringToByteArray(String value) {
		return value.getBytes( );
	}

	/**
	 * Converts a string to a null terminated byte array
	 * 
	 * @param value
	 *            The string to convert
	 * @return The resulting byte array
	 */
	public static byte[] stringToByteArrayNull(String value) {
		byte[] b = value.getBytes( );
		byte[] b2 = new byte[b.length + 1];
		b2[b2.length - 1] = 0;
		for (int i = 0; i < b.length; i++) {
			b2[i] = b[i];
		}
		return b2;
	}

	/**
	 * Converts a byte array to the primitive type
	 * 
	 * @param b
	 *            Byte array to convert
	 * @return the array with primitives
	 */
	public static byte[] toPrimitiveArray(Byte[] b) {
		byte[] arr = new byte[b.length];
		for (int i = 0; i < b.length; i++) {
			arr[i] = b[i];
		}
		return arr;
	}

	/**
	 * Makes a debug string of the given byte array.
	 * 
	 * @param b
	 *            Byte array to make debug string from
	 * @return String with byte array, and byte array as integer
	 */
	public static String debugString(byte[] b) {
		if (b == null) {
			return null;
		}
		StringBuffer buf = new StringBuffer( );
		for (byte byt : b) {
			buf.append(byt).append(", "); //$NON-NLS-1$
		}
		String asInt = ""; //$NON-NLS-1$
		try {
			BigInteger big = new BigInteger(b);
			asInt = big.toString(10) + " "; //$NON-NLS-1$
		}
		catch (Exception e) {
			// nothing to do
		}

		return asInt + buf.toString( );
	}

	/**
	 * Converts an int array to an byte array.
	 * 
	 * @param array
	 *            the array of ints to convert
	 * @return the array of bytes after conversion
	 */
	public static byte[] intArrayToByteArray(int[] array) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream( );
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			for (int i = 0; i < array.length; i++) {
				dos.writeInt(array[i]);
			}
			dos.flush( );
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		finally {
			try {
				dos.close( );
			}
			catch (IOException e) {
				logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
			try {
				bos.close( );
			}
			catch (IOException e) {
				logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
		}
		return bos.toByteArray( );
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	public static void test() {
		Conversion con = Conversion.allocate( ).addString("Hi").addString( //$NON-NLS-1$
		"tach"); //$NON-NLS-1$
		byte[] byt0 = con.getByteArray( );
		String t = Conversion.byteArrayToString(byt0);
		System.out.println(t);

		byte[] byt = Conversion.intToByteArray(120);
		int i = Conversion.byteArrayToInt(byt);
		System.out.println(i);

		byte[] byt2 = Conversion.longToByteArray(1200550089);
		long l = Conversion.byteArrayToLong(byt2);
		System.out.println(l);

		byte[] byt3 = Conversion.doubleToByteArray(5.5);
		double d = Conversion.byteArrayToDouble(byt3);
		System.out.println(d);

		byte[] byt4 = Conversion.floatToByteArray(55.56666f);
		float f = Conversion.byteArrayToFloat(byt4);
		System.out.println(f);

		byte[] byt5 = Conversion.stringToByteArray("Hallo"); //$NON-NLS-1$
		ByteParser parser = new ByteParser(byt5);
		String strg = Conversion.byteArrayToString(byt5);
		System.out.println(strg
				+ " " + parser.getNextString(2) + parser.getNextString( )); //$NON-NLS-1$

		Conversion conc = Conversion.allocate( )
				.addString("Hi").addDouble(55.55); //$NON-NLS-1$
		byte[] byt6 = conc.getByteArray( );
		ByteParser parser2 = new ByteParser(byt6);
		System.out.println(parser2.getNextString(2)
				+ " " + parser2.getNextDouble( )); //$NON-NLS-1$
	}

	// public static void main(String[] args) {
	// test( );
	// }
}
